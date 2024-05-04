package es.darixsmp.darixteleport.messenger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.messenger.message.*;
import es.darixsmp.darixteleportapi.countdown.CountdownService;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import net.smoothplugins.smoothbase.messenger.MessageConsumer;
import net.smoothplugins.smoothbase.messenger.Messenger;
import net.smoothplugins.smoothbase.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class DefaultMessageConsumer implements MessageConsumer {

    @Inject
    private Serializer serializer;
    @Inject
    private TeleportService teleportService;
    @Inject
    private Messenger messenger;
    @Inject
    private CountdownService countdownService;
    @Inject
    private UserService userService;
    @Inject @Named("messages")
    private Configuration messages;

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

            case ACCEPTED_TPA -> {
                AcceptedTPAMessage message = serializer.deserialize(JSON, AcceptedTPAMessage.class);
                consumeAcceptedTPAMessage(message);
            }

            case PLAYER_MESSAGE -> {
                PlayerMessage message = serializer.deserialize(JSON, PlayerMessage.class);
                consumePlayerMessage(message);
            }
        }
    }

    private void consumeTeleportPlayerMessage(TeleportPlayerMessage message) {
        Player player = Bukkit.getPlayer(message.getPlayerUUID());
        if (player == null) return;

        teleportService.teleport(message.getPlayerUUID(), message.getTarget());
    }

    private void consumeGetTeleportLocationRequest(GetTeleportLocationRequest message, UUID identifier) {
        Player player = Bukkit.getPlayer(message.getPlayerUUID());
        if (player == null) return;

        TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
        GetTeleportLocationResponse response = new GetTeleportLocationResponse(message.getPlayerUUID(), currentLocation);
        messenger.sendResponse(serializer.serialize(response), identifier);
    }

    private void consumeAcceptedTPAMessage(AcceptedTPAMessage message) {
        Player player = Bukkit.getPlayer(message.getTarget());
        if (player == null) return;

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%target%", message.getSenderUsername());

        player.sendMessage(messages.getComponent("commands.tpa.accepted", placeholders));

        countdownService.startCountdown(player, () -> {
            TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
            User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
            user.setLastLocation(currentLocation);
            userService.update(user, Destination.CACHE_IF_PRESENT);

            player.sendMessage(messages.getComponent("commands.tpa.teleporting"));
            teleportService.teleport(player.getUniqueId(), message.getLocation());
        });
    }

    private void consumePlayerMessage(PlayerMessage message) {
        Player player = Bukkit.getPlayer(message.getPlayerUUID());
        if (player == null) return;

        message.getMessage().forEach(player::sendMessage);
    }
}
