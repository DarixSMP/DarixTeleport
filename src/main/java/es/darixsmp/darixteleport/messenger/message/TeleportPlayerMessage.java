package es.darixsmp.darixteleport.messenger.message;

import es.darixsmp.darixteleportapi.teleport.TeleportLocation;

import java.util.UUID;

public class TeleportPlayerMessage extends DefaultMessage {

    private final UUID playerUUID;
    private final TeleportLocation target;

    public TeleportPlayerMessage(UUID playerUUID, TeleportLocation target) {
        super(DefaultMessageType.TELEPORT_PLAYER);
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
