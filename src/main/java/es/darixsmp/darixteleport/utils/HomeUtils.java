package es.darixsmp.darixteleport.utils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleportapi.countdown.CountdownService;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;

public class HomeUtils {

    @Inject @Named("config")
    private Configuration config;
    @Inject @Named("messages")
    private Configuration messages;
    @Inject
    private DarixTeleport plugin;
    @Inject
    private UserService userService;
    @Inject
    private CountdownService countdownService;
    @Inject
    private TeleportService teleportService;

    public int getMaxHomes(Player player, User user) {
        int maxLimit = 0;
        if (player.hasPermission("darixteleport.homes.unlimited")) {
            maxLimit = Integer.MAX_VALUE;
        } else {
            ConfigurationSection section = config.getConfigurationSection("home-limits");
            for (String key : section.getKeys(false)) {
                int limit = section.getInt(key);
                if (limit > maxLimit && player.hasPermission("darixteleport.homes." + key)) {
                    maxLimit = limit;
                }
            }
        }

        if (maxLimit != Integer.MAX_VALUE) {
            maxLimit += user.getExtraHomes();
        }

        return maxLimit;
    }

    public boolean handleSetHome(Player player, @Nullable String homeName) {
        User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
        String newHomeName = homeName;

        if (newHomeName == null) {
            int number = 1;
            while (true) {
                TeleportLocation homeLocation = user.getHome(String.valueOf(number));
                if (homeLocation != null) {
                    number++;
                    continue;
                }

                newHomeName = String.valueOf(number);
                break;
            }
        }

        if (!newHomeName.matches("[a-zA-Z0-9]+")) {
            player.sendMessage(messages.getComponent("commands.sethome.invalid-name"));
            return false;
        }

        int maxLimit = getMaxHomes(player, user);
        if (user.getHomes().size() >= maxLimit) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%limit%", String.valueOf(maxLimit));
            player.sendMessage(messages.getComponent("commands.sethome.limit", placeholders));
            return false;
        }

        TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
        user.setHome(newHomeName, currentLocation);

        userService.update(user, Destination.CACHE_IF_PRESENT);

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%home%", newHomeName.toLowerCase(Locale.ROOT));
        player.sendMessage(messages.getComponent("commands.sethome.success", placeholders));

        return true;
    }

    public void handleTeleportHome(Player player, @Nullable String homeName) {
        User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
        if (user.getHomes().isEmpty()) {
            player.sendMessage(messages.getComponent("commands.home.no-homes"));
            return;
        }

        if (homeName == null) {
            homeName = user.getHomes().keySet().stream().findFirst().orElseThrow();
        }

        TeleportLocation home = user.getHome(homeName);

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%home%", homeName.toLowerCase(Locale.ROOT));
        if (home == null) {
            player.sendMessage(messages.getComponent("commands.home.not-found", placeholders));
            return;
        }

        countdownService.startCountdown(player, () -> {
            TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
            User updatedUser = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
            updatedUser.setLastLocation(currentLocation);
            userService.update(updatedUser, Destination.CACHE_IF_PRESENT);

            player.sendMessage(messages.getComponent("commands.home.success", placeholders));
            teleportService.teleport(player.getUniqueId(), home);
        });
    }

    public void handleRemoveHome(Player player, @NotNull String homeName) {
        User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
        user.removeHome(homeName);
        userService.update(user, Destination.CACHE_IF_PRESENT);

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%home%", homeName.toLowerCase(Locale.ROOT));
        player.sendMessage(messages.getComponent("commands.delhome.success", placeholders));
    }
}
