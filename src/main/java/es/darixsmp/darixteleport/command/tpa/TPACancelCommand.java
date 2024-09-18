package es.darixsmp.darixteleport.command.tpa;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.DefaultCommand;
import es.darixsmp.darixteleportapi.request.Request;
import es.darixsmp.darixteleportapi.request.RequestService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import net.smoothplugins.smoothbase.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TPACancelCommand extends DefaultCommand {

    @Inject @Named("messages")
    private Configuration messages;
    @Inject
    private DarixTeleport plugin;
    @Inject
    private RequestService requestService;

    @Override
    public String getName() {
        return "tpacancel";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.tpacancel";
    }

    @Override
    public int getArgsLength() {
        return 0;
    }

    @Override
    public String getUsage() {
        return "/tpacancel";
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
            List<Request> requests = requestService.getRequestsOfSender(player.getUniqueId());
            if (requests.isEmpty()) {
                sender.sendMessage(messages.getComponent("commands.tpacancel.no-requests"));
                return;
            }

            requests.forEach(request -> {
                requestService.delete(request.getSender(), request.getReceiver());
            });

            sender.sendMessage(messages.getComponent("commands.tpacancel.success"));
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
