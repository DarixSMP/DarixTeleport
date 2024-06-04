package es.darixsmp.darixteleport.listener;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import es.darixsmp.darixteleportapi.warp.Warp;
import es.darixsmp.darixteleportapi.warp.WarpService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    @Inject
    private WarpService warpService;
    @Inject
    private DarixTeleport plugin;
    @Inject
    private TeleportService teleportService;
    @Inject
    private UserService userService;

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Location deathLocation = event.getPlayer().getLocation();

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            User user = userService.getUserByUUID(event.getPlayer().getUniqueId()).orElseThrow();

            TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, deathLocation);
            user.setLastLocation(currentLocation);
            userService.update(user, Destination.CACHE_IF_PRESENT);

            Warp warp = warpService.get("spawn").orElse(null);
            if (warp == null) return;

            TeleportLocation teleportLocation = warp.getLocation(DarixTeleport.CURRENT_SERVER);
            if (teleportLocation == null) return;

            PlayerQuitListener.ignoreLastLocation.add(user.getUuid());
            teleportService.teleport(event.getPlayer().getUniqueId(), teleportLocation);
        }, 1L);
    }
}
