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

public class TPHereCommand extends DefaultCommand {

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
    @Inject
    private Serializer serializer;
    @Inject
    private Messenger messenger;


    @Override
    public String getName() {
        return "tphere";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.tphere";
    }

    @Override
    public int getArgsLength() {
        return 1;
    }

    @Override
    public String getUsage() {
        return "/tphere <jugador>";
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
            placeholders.put("%target%", args[0]);
            placeholders.put("%player%", player.getName());

            User target = userService.getUserByUsername(args[0]).orElse(null);
            if (target == null) {
                placeholders.put("%player%", args[0]);
                player.sendMessage(messages.getComponent("global.user-not-found", placeholders));
                placeholders.put("%player%", player.getName());
                return;
            }

            TeleportLocation lastLocation = null;
            try {
                lastLocation = teleportService.getTeleportLocation(target.getUuid()).join();
                target.setLastLocation(lastLocation);
                userService.update(target, Destination.CACHE_IF_PRESENT);
            } catch (Exception e) {
                player.sendMessage(messages.getComponent("global.user-not-found", placeholders));
                return;
            }

            player.sendMessage(messages.getComponent("commands.tphere.success", placeholders));

            PlayerMessage targetMessage = new PlayerMessage(target.getUuid(), messages.getString("commands.tphere.success-target", placeholders));
            messenger.send(serializer.serialize(targetMessage));

            TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
            teleportService.teleport(target.getUuid(), currentLocation);
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return userService.getAllConnectedUsernames();
        }

        return null;
    }
}