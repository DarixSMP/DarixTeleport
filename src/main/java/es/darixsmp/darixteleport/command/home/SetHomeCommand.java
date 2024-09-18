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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SetHomeCommand extends DefaultCommand {

    @Inject
    private UserService userService;
    @Inject @Named("messages")
    private Configuration messages;
    @Inject @Named("config")
    private Configuration config;
    @Inject
    private DarixTeleport plugin;

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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String homeName = args.length == 0 ? "casa" : args[0];
            if (!homeName.matches("[a-zA-Z0-9]+")) {
                sender.sendMessage(messages.getComponent("commands.sethome.invalid-name"));
                return;
            }

            Player player = (Player) sender;

            User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();

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

            if (user.getHomes().size() >= maxLimit) {
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%limit%", String.valueOf(maxLimit));
                player.sendMessage(messages.getComponent("commands.sethome.limit", placeholders));
                return;
            }

            TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
            user.setHome(homeName, currentLocation);

            userService.update(user, Destination.CACHE_IF_PRESENT);

            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%home%", homeName.toLowerCase(Locale.ROOT));
            player.sendMessage(messages.getComponent("commands.sethome.success", placeholders));
        });
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            return List.of("<nombre>");
        }

        return null;
    }
}
