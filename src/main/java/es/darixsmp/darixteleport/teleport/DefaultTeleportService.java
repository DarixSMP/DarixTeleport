package es.darixsmp.darixteleport.teleport;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.messenger.message.GetTeleportLocationRequest;
import es.darixsmp.darixteleport.messenger.message.GetTeleportLocationResponse;
import es.darixsmp.darixteleport.messenger.message.TeleportPlayerMessage;
import es.darixsmp.darixteleportapi.pending.PendingTeleport;
import es.darixsmp.darixteleportapi.pending.PendingTeleportService;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import net.smoothplugins.smoothbase.messenger.Messenger;
import net.smoothplugins.smoothbase.messenger.Response;
import net.smoothplugins.smoothbase.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DefaultTeleportService implements TeleportService {

    @Inject
    private Messenger messenger;
    @Inject
    private Serializer serializer;
    @Inject
    private PendingTeleportService pendingTeleportService;
    @Inject
    private DarixTeleport plugin;
    @Inject @Named("config")
    private Configuration config;

    @Override
    public void teleport(UUID playerUUID, TeleportLocation teleportLocation, PlayerSpawnLocationEvent event) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) {
            TeleportPlayerMessage message = new TeleportPlayerMessage(playerUUID, teleportLocation);
            messenger.send(serializer.serialize(message));
            return;
        }

        if (teleportLocation.getServer().equals(DarixTeleport.CURRENT_SERVER)) {
            if (event == null) {
                player.teleportAsync(teleportLocation.toLocation());
            } else {
                event.setSpawnLocation(teleportLocation.toLocation());
            }

            return;
        }

        pendingTeleportService.create(new PendingTeleport(playerUUID, teleportLocation));

        ByteArrayDataOutput pluginMessage = ByteStreams.newDataOutput();
        pluginMessage.writeUTF("Connect");
        pluginMessage.writeUTF(teleportLocation.getServer());
        player.sendPluginMessage(plugin, "BungeeCord", pluginMessage.toByteArray());
    }

    @Override
    public CompletableFuture<TeleportLocation> getTeleportLocation(UUID playerUUID) {
        CompletableFuture<TeleportLocation> future = new CompletableFuture<>();

        GetTeleportLocationRequest request = new GetTeleportLocationRequest(playerUUID);
        messenger.sendRequest(serializer.serialize(request), new Response() {
            @Override
            public void onSuccess(String channel, String json) {
                GetTeleportLocationResponse response = serializer.deserialize(json, GetTeleportLocationResponse.class);
                future.complete(response.getCurrentLocation());
            }

            @Override
            public void onFail(String s) {
                future.completeExceptionally(new Exception("No se ha podido obtener la ubicación del jugador"));
            }
        }, config.getLong("timeouts.get-teleport-location-request"));

        return future;
    }
}
