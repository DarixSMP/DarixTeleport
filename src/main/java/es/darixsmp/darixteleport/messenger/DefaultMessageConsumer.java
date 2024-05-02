package es.darixsmp.darixteleport.messenger;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.messenger.message.DefaultMessage;
import es.darixsmp.darixteleport.messenger.message.GetTeleportLocationRequest;
import es.darixsmp.darixteleport.messenger.message.GetTeleportLocationResponse;
import es.darixsmp.darixteleport.messenger.message.TeleportPlayerMessage;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import net.smoothplugins.smoothbase.messenger.MessageConsumer;
import net.smoothplugins.smoothbase.messenger.Messenger;
import net.smoothplugins.smoothbase.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DefaultMessageConsumer implements MessageConsumer {

    @Inject
    private Serializer serializer;
    @Inject
    private TeleportService teleportService;
    @Inject
    private Messenger messenger;

    @Override
    public void consume(String JSON, @Nullable UUID identifier) {
        DefaultMessage tempMessage = serializer.deserialize(JSON, DefaultMessage.class);
        switch (tempMessage.getType()) {
            case TELEPORT_PLAYER -> {
                TeleportPlayerMessage message = serializer.deserialize(JSON, TeleportPlayerMessage.class);
                consumeTeleportPlayerMessage(message);
            }

            case GET_TELEPORT_LOCATION_REQUEST -> {
                GetTeleportLocationRequest message = serializer.deserialize(JSON, GetTeleportLocationRequest.class);
                consumeGetTeleportLocationRequest(message, identifier);
            }
        }
    }

    private void consumeTeleportPlayerMessage(TeleportPlayerMessage message) {
        Player player = Bukkit.getPlayer(message.getPlayerUUID());
        if (player == null) return;

        teleportService.teleport(message.getPlayerUUID(), message.getTarget(), null);
    }

    private void consumeGetTeleportLocationRequest(GetTeleportLocationRequest message, UUID identifier) {
        Player player = Bukkit.getPlayer(message.getPlayerUUID());
        if (player == null) return;

        TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
        GetTeleportLocationResponse response = new GetTeleportLocationResponse(message.getPlayerUUID(), currentLocation);
        messenger.sendResponse(serializer.serialize(response), identifier);
    }
}
