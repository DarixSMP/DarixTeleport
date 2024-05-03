package es.darixsmp.darixteleport.command.tp;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.DefaultCommand;
import es.darixsmp.darixteleport.messenger.message.PlayerMessage;
import es.darixsmp.darixteleportapi.countdown.CountdownCallback;
import es.darixsmp.darixteleportapi.countdown.CountdownService;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import net.smoothplugins.smoothbase.messenger.Messenger;
import net.smoothplugins.smoothbase.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class TPCommand extends DefaultCommand {

    @Inject
    private UserService userService;
    @Inject
    private TeleportService teleportService;
    @Inject
    private CountdownService countdownService;
    @Inject @Named("messages")
    private Configuration messages;
    @Inject @Named("config")
    private Configuration config;
    @Inject
    private DarixTeleport plugin;


    @Override
    public String getName() {
        return "tp";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.tp";
    }

    @Override
    public int getArgsLength() {
        return -1;
    }

    @Override
    public String getUsage() {
        return "/tp <player> | /tp <x> <y> <z>";
    }

    @Override
    public boolean mustBePlayer() {
        return true;
    }

    @Override
    public void registerSubcommands() {

    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = (Player) sender;

            if (args.length != 1 && args.length != 3) {
                player.sendMessage(getUsageComponent());
                return;
            }

            if (args.length == 3) {
                double x;
                double y;
                double z;

                try {
                    x = Double.parseDouble(args[0]);
                    y = Double.parseDouble(args[1]);
                    z = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(messages.getComponent("commands.tp.coords.invalid-coords"));
                    return;
                }

                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%coords%", x + ", " + y + ", " + z);

                TeleportLocation location = new TeleportLocation(DarixTeleport.CURRENT_SERVER, player.getWorld().getUID(), x, y, z, 0, 0);
                int countdownDuration = config.getInt("countdown.duration");
                double maxMovement = config.getDouble("countdown.max-movement");
                countdownService.startCountdown(player, countdownDuration, maxMovement, new CountdownCallback() {
                    @Override
                    public void onSuccess() {
                        TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
                        User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
                        user.setLastLocation(currentLocation);
                        userService.update(user, Destination.CACHE_IF_PRESENT);

                        player.sendMessage(messages.getComponent("commands.tp.coords.success", placeholders));
                        teleportService.teleport(player.getUniqueId(), location);
                    }

                    @Override
                    public void onFail() {
                        player.sendMessage(messages.getComponent("global.countdown-cancelled"));
                    }
                });

                return;
            }

            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", args[0]);

            User target = userService.getUserByUsername(args[0]).orElse(null);
            if (target == null) {
                player.sendMessage(messages.getComponent("global.user-not-found", placeholders));
                return;
            }

            TeleportLocation targetLocation = null;
            try {
                targetLocation = teleportService.getTeleportLocation(target.getUuid()).join();
            } catch (Exception e) {
                player.sendMessage(messages.getComponent("global.user-not-found", placeholders));
                return;
            }

            int countdownDuration = config.getInt("countdown.duration");
            double maxMovement = config.getDouble("countdown.max-movement");
            TeleportLocation finalTargetLocation = targetLocation;
            countdownService.startCountdown(player, countdownDuration, maxMovement, new CountdownCallback() {
                @Override
                public void onSuccess() {
                    TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
                    User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
                    user.setLastLocation(currentLocation);
                    userService.update(target, Destination.CACHE_IF_PRESENT);

                    player.sendMessage(messages.getComponent("commands.tp.player.success", placeholders));
                    teleportService.teleport(player.getUniqueId(), finalTargetLocation);
                }

                @Override
                public void onFail() {
                    player.sendMessage(messages.getComponent("global.countdown-cancelled"));
                }
            });
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return userService.getAllConnectedUsernames();
        }

        if (args.length == 2) {
            return List.of("Y");
        }

        if (args.length == 3) {
            return List.of("Z");
        }

        return null;
    }
}
