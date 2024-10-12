package es.darixsmp.darixteleport.command.home;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.DefaultCommand;
import es.darixsmp.darixteleport.menu.HomesMenu;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomesCommand extends DefaultCommand {

    @Inject
    private UserService userService;
    @Inject
    private DarixTeleport plugin;

    @Override
    public String getName() {
        return "homes";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.homes";
    }

    @Override
    public int getArgsLength() {
        return 0;
    }

    @Override
    public String getUsage() {
        return "/homes";
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

            Bukkit.getScheduler().runTask(plugin, () -> {
                HomesMenu homesMenu = new HomesMenu(player, user);
                homesMenu.open();
            });
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
