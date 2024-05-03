package es.darixsmp.darixteleport.command.warp;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.DefaultCommand;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.warp.Warp;
import es.darixsmp.darixteleportapi.warp.WarpService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class SetWarpCommand extends DefaultCommand {

    @Inject @Named("messages")
    private Configuration messages;
    @Inject
    private WarpService warpService;

    @Override
    public String getName() {
        return "setwarp";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.setwarp";
    }

    @Override
    public int getArgsLength() {
        return 1;
    }

    @Override
    public String getUsage() {
        return "/setwarp <nombre>";
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
        String name = args[0];
        Player player = (Player) sender;

        TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
        Warp warp = warpService.get(name).orElseGet(() -> new Warp(name));
        warp.addLocation(currentLocation);
        warpService.create(warp);

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%warp%", name);
        sender.sendMessage(messages.getComponent("commands.setwarp.success", placeholders));
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] strings) {
        return null;
    }
}
