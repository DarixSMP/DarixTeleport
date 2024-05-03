package es.darixsmp.darixteleport.command.warp;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.DefaultCommand;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.warp.Warp;
import es.darixsmp.darixteleportapi.warp.WarpService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DelWarpCommand extends DefaultCommand {

    @Inject
    @Named("messages")
    private Configuration messages;
    @Inject
    private WarpService warpService;
    @Inject
    private DarixTeleport plugin;

    @Override
    public String getName() {
        return "delwarp";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.delwarp";
    }

    @Override
    public int getArgsLength() {
        return 2;
    }

    @Override
    public String getUsage() {
        return "/delwarp <nombre> <server/all>";
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
            String name = args[0];
            String server = args[1];

            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%warp%", name);
            placeholders.put("%server%", server);

            Warp warp = warpService.get(name).orElse(null);
            if (warp == null) {
                sender.sendMessage(messages.getComponent("commands.delwarp.not-found", placeholders));
                return;
            }

            if (server.equalsIgnoreCase("all")) {
                warpService.delete(warp.getName());
                sender.sendMessage(messages.getComponent("commands.delwarp.success", placeholders));
                return;
            }

            warp.removeLocation(server);
            warpService.update(warp, Destination.DATABASE, Destination.CACHE_IF_PRESENT);
            sender.sendMessage(messages.getComponent("commands.delwarp.success", placeholders));
        });
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            return warpService.getAll().stream().map(Warp::getName).toList();
        }

        if (args.length == 2) {
            List<String> completions = new ArrayList<>();
            completions.add("all");

            warpService.get(args[0]).ifPresent(warp -> completions.addAll(warp.getLocations().keySet()));

            return completions;
        }

        return null;
    }
}
