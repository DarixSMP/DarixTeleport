package es.darixsmp.darixteleport.command.tp;

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

public class TPOCommand extends DefaultCommand {

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
        return "tpo";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.tpo";
    }

    @Override
    public int getArgsLength() {
        return 1;
    }

    @Override
    public String getUsage() {
        return "/phome <jugador>";
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

            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", args[0]);

            User target = userService.getUserByUsername(args[0]).orElse(null);
            if (target == null) {
                player.sendMessage(messages.getComponent("global.user-not-found", placeholders));
                return;
            }

            TeleportLocation lastLocation = target.getLastLocation();

            countdownService.startCountdown(player, () -> {
                TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
                User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
                user.setLastLocation(currentLocation);
                userService.update(target, Destination.CACHE_IF_PRESENT);

                player.sendMessage(messages.getComponent("commands.tpo.success", placeholders));
                teleportService.teleport(player.getUniqueId(), lastLocation);
            });
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("<player>");
        }

        return null;
    }
}
