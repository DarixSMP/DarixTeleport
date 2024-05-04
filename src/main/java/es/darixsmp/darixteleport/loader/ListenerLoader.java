package es.darixsmp.darixteleport.loader;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.listener.PlayerJoinListener;
import es.darixsmp.darixteleport.listener.PlayerQuitListener;
import es.darixsmp.darixteleport.listener.PlayerRespawnListener;
import es.darixsmp.darixteleport.listener.PlayerSpawnLocationListener;
import org.bukkit.Bukkit;

public class ListenerLoader {

    @Inject
    private DarixTeleport plugin;
    @Inject
    private PlayerSpawnLocationListener playerSpawnLocationListener;
    @Inject
    private PlayerJoinListener playerJoinListener;
    @Inject
    private PlayerQuitListener playerQuitListener;
    @Inject
    private PlayerRespawnListener playerRespawnListener;

    public void load() {
        Bukkit.getPluginManager().registerEvents(playerSpawnLocationListener, plugin);
        Bukkit.getPluginManager().registerEvents(playerJoinListener, plugin);
        Bukkit.getPluginManager().registerEvents(playerQuitListener, plugin);
        Bukkit.getPluginManager().registerEvents(playerRespawnListener, plugin);
    }
}
