package es.darixsmp.darixteleport.command.home;

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
import net.smoothplugins.smoothbase.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeCommand extends DefaultCommand {

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
        return "home";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.home";
    }

    @Override
    public int getArgsLength() {
        return -1;
    }

    @Override
    public String getUsage() {
        return "/home (nombre)";
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
            String homeName = args.length == 0 ? "casa" : args[0];
            Player player = (Player) sender;

            User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
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
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            Player player = (Player) sender;
            User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
            return user.getHomes().keySet().stream().toList();
        }

        return null;
    }
}
