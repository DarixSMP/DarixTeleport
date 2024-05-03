package es.darixsmp.darixteleport.command.home;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.DefaultCommand;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DelHomeCommand extends DefaultCommand {

    @Inject
    private UserService userService;
    @Inject @Named("messages")
    private Configuration messages;
    @Inject
    private DarixTeleport plugin;

    @Override
    public String getName() {
        return "delhome";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.delhome";
    }

    @Override
    public int getArgsLength() {
        return 1;
    }

    @Override
    public String getUsage() {
        return "/sethome <nombre>";
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
            user.removeHome(args[0]);
            userService.update(user, Destination.CACHE_IF_PRESENT);

            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%home%", args[0].toLowerCase(Locale.ROOT));
            player.sendMessage(messages.getComponent("commands.delhome.success", placeholders));
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