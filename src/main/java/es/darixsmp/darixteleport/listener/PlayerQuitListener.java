package es.darixsmp.darixteleport.listener;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @Inject
    private UserService userService;
    @Inject
    private DarixTeleport plugin;
    @Inject @Named("config")
    private Configuration config;

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            User user = userService.getUserByUUID(event.getPlayer().getUniqueId()).orElseThrow();

            TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, event.getPlayer().getLocation());
            user.setLastLocation(currentLocation);

            userService.update(user, Destination.DATABASE, Destination.CACHE_IF_PRESENT);
            userService.setTTLOfCacheByUUID(user.getUuid(), config.getInt("timeouts.user-quit") / 1000);
        });
    }
}
