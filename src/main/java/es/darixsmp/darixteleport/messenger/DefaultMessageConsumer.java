package es.darixsmp.darixteleport.messenger;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.messenger.message.DefaultMessage;
import es.darixsmp.darixteleport.messenger.message.TeleportPlayerMessage;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import net.smoothplugins.smoothbase.messenger.MessageConsumer;
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

    @Override
    public void consume(String JSON, @Nullable UUID identifier) {
        DefaultMessage tempMessage = serializer.deserialize(JSON, DefaultMessage.class);
        switch (tempMessage.getType()) {
            case TELEPORT_PLAYER -> {
                TeleportPlayerMessage message = serializer.deserialize(JSON, TeleportPlayerMessage.class);
            }
        }
    }

    private void consumeTeleportPlayerMessage(TeleportPlayerMessage message) {
        Player player = Bukkit.getPlayer(message.getPlayerUUID());
        if (player != null) {
            teleportService.teleport(message.getPlayerUUID(), message.getTarget());
        }
    }
}
