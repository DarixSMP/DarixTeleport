package es.darixsmp.darixteleport.loader;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import es.darixsmp.darixteleportapi.warp.WarpService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import net.smoothplugins.smoothbase.messenger.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MainLoader {

    @Inject
    private CommandLoader commandLoader;
    @Inject
    private ListenerLoader listenerLoader;

    @Inject
    private WarpService warpService;
    @Inject
    private UserService userService;
    @Inject @Named("config")
    private Configuration config;
    @Inject
    private DarixTeleport plugin;
    @Inject
    private Messenger messenger;

    public void load() {
        commandLoader.load();
        listenerLoader.load();

        warpService.loadWarpsToCache();

        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        messenger.register();
    }

    public void unload() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();

            TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
            user.setLastLocation(currentLocation);

            userService.update(user, Destination.DATABASE, Destination.CACHE_IF_PRESENT);
            userService.setTTLOfCacheByUUID(user.getUuid(), config.getInt("timeouts.user-quit") / 1000);
        });

        messenger.unregister();
    }
}
