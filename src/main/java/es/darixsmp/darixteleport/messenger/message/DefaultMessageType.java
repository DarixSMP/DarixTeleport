package es.darixsmp.darixteleport.messenger.message;

public enum DefaultMessageType {
    PLAYER_NOTIFICATION, // This is a notification message (e.g. "You have been teleported")
    ACCEPTED_TPA, // This is a message that indicates that a TPA request has been accepted and the player can start the countdown
    GET_TELEPORT_LOCATION_REQUEST, // This is a message that requests the location of a player
    GET_TELEPORT_LOCATION_RESPONSE, // This is a message that contains the location of a player
    TELEPORT_PLAYER, // This is a message that requests a teleport to the server that has the player
}
