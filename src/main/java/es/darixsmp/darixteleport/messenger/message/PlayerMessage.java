package es.darixsmp.darixteleport.messenger.message;

import java.util.UUID;

public class PlayerMessage extends DefaultMessage {

    private final UUID playerUUID;
    private final String message;

    public PlayerMessage(UUID playerUUID, String message) {
        super(DefaultMessageType.PLAYER_MESSAGE);
        this.playerUUID = playerUUID;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
