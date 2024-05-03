package es.darixsmp.darixteleport.messenger.message;

import java.util.List;
import java.util.UUID;

public class PlayerMessage extends DefaultMessage {

    private final UUID playerUUID;
    private final List<String> message;

    public PlayerMessage(UUID playerUUID, String message) {
        super(DefaultMessageType.PLAYER_MESSAGE);
        this.playerUUID = playerUUID;
        this.message = List.of(message);
    }

    public PlayerMessage(UUID playerUUID, List<String> message) {
        super(DefaultMessageType.PLAYER_MESSAGE);
        this.playerUUID = playerUUID;
        this.message = message;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public List<String> getMessage() {
        return message;
    }
}
