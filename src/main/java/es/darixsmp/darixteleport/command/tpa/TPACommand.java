package es.darixsmp.darixteleport.command.tpa;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.DefaultCommand;
import es.darixsmp.darixteleportapi.countdown.CountdownService;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import es.darixsmp.darixteleportapi.user.UserService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TPACommand extends DefaultCommand {

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
        return null;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public int getArgsLength() {
        return 0;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public boolean mustBePlayer() {
        return false;
    }

    @Override
    public void registerSubcommands() {

    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] strings) {
        return null;
    }
}
