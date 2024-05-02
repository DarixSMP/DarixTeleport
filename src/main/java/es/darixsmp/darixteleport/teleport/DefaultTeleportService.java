package es.darixsmp.darixteleport.teleport;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.messenger.message.TeleportPlayerMessage;
import es.darixsmp.darixteleportapi.pending.PendingTeleport;
import es.darixsmp.darixteleportapi.pending.PendingTeleportService;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import net.smoothplugins.smoothbase.messenger.Messenger;
import net.smoothplugins.smoothbase.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

    @Override
    public void teleport(UUID playerUUID, TeleportLocation teleportLocation) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) {
            TeleportPlayerMessage message = new TeleportPlayerMessage(playerUUID, teleportLocation);
            messenger.send(serializer.serialize(message));
            return;
        }

        if (teleportLocation.getServer().equals(DarixTeleport.CURRENT_SERVER)) {
            player.teleportAsync(teleportLocation.toLocation());
            return;
        }

        pendingTeleportService.create(new PendingTeleport(playerUUID, teleportLocation));

        ByteArrayDataOutput pluginMessage = ByteStreams.newDataOutput();
        pluginMessage.writeUTF("Connect");
        pluginMessage.writeUTF(teleportLocation.getServer());
        player.sendPluginMessage(plugin, "BungeeCord", pluginMessage.toByteArray());
    }

    @Override
    public CompletableFuture<TeleportLocation> getTeleportLocation(UUID uuid) {
        return null;
    }
}
