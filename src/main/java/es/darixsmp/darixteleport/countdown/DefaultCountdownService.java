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
    @Inject @Named("config")
    private Configuration config;

    @Override
    public void startCountdown(Player player, CountdownCallback countdownCallback) {
        if (player.hasPermission("darixteleport.bypass.countdown")) {
            countdownCallback.onSuccess();
            return;
        }

        int countdownDuration = config.getInt("countdown.duration");
        double maxMovement = config.getDouble("countdown.max-movement");
        Location originalLocation = player.getLocation();

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%time%", String.valueOf(countdownDuration));
        Notification notification = Notification.of(messages, "notifications.countdown", placeholders);
        notification.send(player);

        int elapsedSeconds = 0;
        while (elapsedSeconds < countdownDuration) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Location currentLocation = player.getLocation();
            if (currentLocation.distance(originalLocation) > maxMovement) {
                player.sendMessage(messages.getComponent("global.countdown-cancelled"));
                return;
            }

            elapsedSeconds++;

            if (countdownDuration - elapsedSeconds > 0) {
                placeholders.put("%time%", String.valueOf(countdownDuration - elapsedSeconds));
                notification = Notification.of(messages, "notifications.countdown", placeholders);
                notification.send(player);
            }
        }

        countdownCallback.onSuccess();
    }
}
