package es.darixsmp.darixteleport.messenger.message;

import es.darixsmp.darixteleportapi.teleport.TeleportLocation;

import java.util.UUID;

public class AcceptedTPAMessage extends DefaultMessage {

    private final UUID target;
    private final TeleportLocation location;
    private final String senderUsername;

    public AcceptedTPAMessage(UUID target, TeleportLocation location, String senderUsername) {
        super(DefaultMessageType.ACCEPTED_TPA);
        this.target = target;
        this.location = location;
        this.senderUsername = senderUsername;
    }

    public UUID getTarget() {
        return target;
    }

    public TeleportLocation getLocation() {
        return location;
    }

    public String getSenderUsername() {
        return senderUsername;
    }
}
