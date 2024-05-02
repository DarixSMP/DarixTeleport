package es.darixsmp.darixteleport.listener;

import com.google.inject.Inject;
import es.darixsmp.darixteleportapi.pending.PendingTeleport;
import es.darixsmp.darixteleportapi.pending.PendingTeleportService;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class PlayerSpawnLocationListener implements Listener {

    @Inject
    private PendingTeleportService pendingTeleportService;
    @Inject
    private TeleportService teleportService;

    @EventHandler
    public void onSpawn(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        PendingTeleport pendingTeleport = pendingTeleportService.get(player.getUniqueId()).orElse(null);
        if (pendingTeleport == null) return;

        pendingTeleportService.delete(player.getUniqueId());
        teleportService.teleport(player.getUniqueId(), pendingTeleport.getTarget(), event);
    }
}
