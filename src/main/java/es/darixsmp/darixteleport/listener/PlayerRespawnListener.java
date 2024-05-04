package es.darixsmp.darixteleport.listener;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.warp.Warp;
import es.darixsmp.darixteleportapi.warp.WarpService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    @Inject
    private WarpService warpService;

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Warp warp = warpService.get("spawn").orElse(null);
        if (warp == null) return;

        TeleportLocation teleportLocation = warp.getLocation(DarixTeleport.CURRENT_SERVER);
        if (teleportLocation == null) return;

        event.setRespawnLocation(teleportLocation.toLocation());
    }
}
