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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerLoadEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SetHomeCommand extends DefaultCommand {

    @Inject
    private UserService userService;
    @Inject @Named("messages")
    private Configuration messages;

    @Override
    public String getName() {
        return "sethome";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.sethome";
    }

    @Override
    public int getArgsLength() {
        return -1;
    }

    @Override
    public String getUsage() {
        return "/sethome (nombre)";
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
        String homeName = args.length == 0 ? "casa" : args[0];
        Player player = (Player) sender;

        User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();

        // TODO: Check if user can have more homes

        TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
        user.setHome(homeName, currentLocation);

        userService.update(user, Destination.DATABASE, Destination.CACHE_IF_PRESENT);

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%home%", homeName.toLowerCase(Locale.ROOT));
        player.sendMessage(messages.getComponent("commands.sethome.success", placeholders));
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            return List.of("<nombre>");
        }

        return null;
    }
}
