package es.darixsmp.darixteleport.countdown;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleportapi.countdown.CountdownCallback;
import es.darixsmp.darixteleportapi.countdown.CountdownService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import net.smoothplugins.smoothbase.notification.Notification;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class DefaultCountdownService implements CountdownService {

    @Inject
    private DarixTeleport plugin;
    @Inject @Named("messages")
    private Configuration messages;

    @Override
    public void startCountdown(Player player, int seconds, double maxDistance, CountdownCallback countdownCallback) {
        if (player.hasPermission("darixteleport.bypass.countdown")) {
            countdownCallback.onSuccess();
            return;
        }

        Location originalLocation = player.getLocation();

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%time%", String.valueOf(seconds));
        Notification notification = Notification.of(messages, "notifications.countdown", placeholders);
        notification.send(player);

        int elapsedSeconds = 0;
        while (elapsedSeconds < seconds) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Location currentLocation = player.getLocation();
            if (currentLocation.distance(originalLocation) > maxDistance) {
                countdownCallback.onFail();
                return;
            }

            elapsedSeconds++;

            if (seconds - elapsedSeconds > 0) {
                placeholders.put("%time%", String.valueOf(seconds - elapsedSeconds));
                notification = Notification.of(messages, "notifications.countdown", placeholders);
                notification.send(player);
            }
        }

        countdownCallback.onSuccess();
    }
}
