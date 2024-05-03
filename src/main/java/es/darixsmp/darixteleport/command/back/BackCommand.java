package es.darixsmp.darixteleport.command.back;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.DefaultCommand;
import es.darixsmp.darixteleportapi.countdown.CountdownCallback;
import es.darixsmp.darixteleportapi.countdown.CountdownService;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import es.darixsmp.darixteleportapi.warp.Warp;
import net.smoothplugins.smoothbase.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BackCommand extends DefaultCommand {

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
        return "back";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.back";
    }

    @Override
    public int getArgsLength() {
        return 0;
    }

    @Override
    public String getUsage() {
        return "/back";
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

            User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
            TeleportLocation lastLocation = user.getLastLocation();

            int countdownDuration = config.getInt("countdown.duration");
            double maxMovement = config.getDouble("countdown.max-movement");
            countdownService.startCountdown(player, countdownDuration, maxMovement, new CountdownCallback() {
                @Override
                public void onSuccess() {
                    TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
                    User updatedUser = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
                    updatedUser.setLastLocation(currentLocation);
                    userService.update(updatedUser, Destination.CACHE_IF_PRESENT);

                    player.sendMessage(messages.getComponent("commands.back.success"));
                    teleportService.teleport(player.getUniqueId(), lastLocation);
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
        return null;
    }
}
