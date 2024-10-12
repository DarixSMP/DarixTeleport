package es.darixsmp.darixteleport.command.home;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.DefaultCommand;
import es.darixsmp.darixteleport.utils.HomeUtils;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DelHomeCommand extends DefaultCommand {

    @Inject
    private UserService userService;
    @Inject
    private DarixTeleport plugin;
    @Inject
    private HomeUtils homeUtils;

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
        return "/delhome <nombre>";
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
            homeUtils.handleRemoveHome((Player) sender, args[0]);
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