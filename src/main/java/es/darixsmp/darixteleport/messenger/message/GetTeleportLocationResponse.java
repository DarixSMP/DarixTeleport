package es.darixsmp.darixteleport.messenger.message;

import es.darixsmp.darixteleportapi.teleport.TeleportLocation;

import java.util.UUID;

public class GetTeleportLocationResponse extends DefaultMessage {

    private final UUID playerUUID;
    private final TeleportLocation currentLocation;

    public GetTeleportLocationResponse(UUID playerUUID, TeleportLocation currentLocation) {
        super(DefaultMessageType.GET_TELEPORT_LOCATION_RESPONSE);
        this.playerUUID = playerUUID;
        this.currentLocation = currentLocation;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public TeleportLocation getCurrentLocation() {
        return currentLocation;
    }
}