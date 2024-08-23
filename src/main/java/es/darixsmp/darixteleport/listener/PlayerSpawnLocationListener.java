package es.darixsmp.darixteleport.listener;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleportapi.pending.PendingTeleport;
import es.darixsmp.darixteleportapi.pending.PendingTeleportService;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class PlayerSpawnLocationListener implements Listener {

    @Inject
    private PendingTeleportService pendingTeleportService;
    @Inject
    private TeleportService teleportService;
    @Inject
    private DarixTeleport plugin;

    @EventHandler
    public void onSpawn(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        PendingTeleport pendingTeleport = pendingTeleportService.get(player.getUniqueId()).orElse(null);
        if (pendingTeleport == null) return;

        TeleportLocation teleportLocation = pendingTeleport.getTarget();
        if (teleportLocation.getServer().equals(DarixTeleport.CURRENT_SERVER)) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
               pendingTeleportService.delete(player.getUniqueId());
            });

            event.setSpawnLocation(teleportLocation.toLocation());

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    if (player.getLocation().distance(teleportLocation.toLocation()) < 5.0) return;

                    player.teleport(teleportLocation.toLocation());
                } catch (IllegalArgumentException ignored) {
                    player.teleport(teleportLocation.toLocation());
                }
            }, 5L);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    teleportService.teleport(player.getUniqueId(), teleportLocation)
            );
        }
    }
}
