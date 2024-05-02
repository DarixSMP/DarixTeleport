package es.darixsmp.darixteleport.messenger.message;

import es.darixsmp.darixteleportapi.teleport.TeleportLocation;

import java.util.UUID;

public class TeleportRequestMessage extends DefaultMessage {

    private final UUID playerUUID;
    private final TeleportLocation target;

    public TeleportRequestMessage(UUID playerUUID, TeleportLocation target) {
        super(DefaultMessageType.TELEPORT_REQUEST);
        this.playerUUID = playerUUID;
        this.target = target;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public TeleportLocation getTarget() {
        return target;
    }
}
