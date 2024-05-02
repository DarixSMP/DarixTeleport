package es.darixsmp.darixteleport.messenger.message;

import es.darixsmp.darixteleportapi.teleport.TeleportLocation;

import java.util.UUID;

public class GetTeleportLocationRequest extends DefaultMessage {

    private final UUID playerUUID;

    public GetTeleportLocationRequest(UUID playerUUID) {
        super(DefaultMessageType.GET_TELEPORT_LOCATION_REQUEST);
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
