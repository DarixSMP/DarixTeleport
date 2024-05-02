package es.darixsmp.darixteleport.loader;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.listener.PlayerJoinListener;
import es.darixsmp.darixteleport.listener.PlayerSpawnLocationListener;
import org.bukkit.Bukkit;

public class ListenerLoader {

    @Inject
    private DarixTeleport plugin;
    @Inject
    private PlayerSpawnLocationListener playerSpawnLocationListener;
    @Inject
    private PlayerJoinListener playerJoinListener;

    public void load() {
        Bukkit.getPluginManager().registerEvents(playerSpawnLocationListener, plugin);
        Bukkit.getPluginManager().registerEvents(playerJoinListener, plugin);
    }
}
