package es.darixsmp.darixteleport.command.home;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.DefaultCommand;
import es.darixsmp.darixteleport.utils.HomeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetHomeCommand extends DefaultCommand {

    @Inject
    private DarixTeleport plugin;
    @Inject
    private HomeUtils homeUtils;

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
            homeUtils.handleSetHome((Player) sender, args.length == 0 ? null : args[0]);
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
