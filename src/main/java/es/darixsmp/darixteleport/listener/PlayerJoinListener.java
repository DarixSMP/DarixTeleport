package es.darixsmp.darixteleport.listener;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @Inject
    private UserService userService;
    @Inject
    private DarixTeleport plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            User user = userService.getUserByUUID(event.getPlayer().getUniqueId()).orElseGet(() -> {
                TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, event.getPlayer().getLocation());
                User newUser = new User(event.getPlayer().getUniqueId(), currentLocation);
                userService.create(newUser);
                return newUser;
            });

            if (!userService.cacheContainsByUUID(user.getUuid())) {
                userService.loadToCache(user);
                return;
            }

            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                if (userService.removeTTLFromCacheByUUID(user.getUuid())) return;

                User updatedUser = userService.getUserByUUID(user.getUuid()).orElseThrow();
                userService.loadToCache(updatedUser);
            }, 20L * 3);
        });
    }
}
