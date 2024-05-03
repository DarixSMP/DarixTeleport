package es.darixsmp.darixteleport.command.tpa;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.DefaultCommand;
import es.darixsmp.darixteleport.messenger.message.PlayerMessage;
import es.darixsmp.darixteleportapi.request.Request;
import es.darixsmp.darixteleportapi.request.RequestService;
import es.darixsmp.darixteleportapi.request.RequestType;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import net.smoothplugins.smoothbase.messenger.Messenger;
import net.smoothplugins.smoothbase.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class TPAHereCommand extends DefaultCommand {

    @Inject
    private UserService userService;
    @Inject @Named("messages")
    private Configuration messages;
    @Inject @Named("config")
    private Configuration config;
    @Inject
    private DarixTeleport plugin;
    @Inject
    private Messenger messenger;
    @Inject
    private Serializer serializer;
    @Inject
    private RequestService requestService;

    @Override
    public String getName() {
        return "tpahere";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.tpahere";
    }

    @Override
    public int getArgsLength() {
        return 1;
    }

    @Override
    public String getUsage() {
        return "/tpahere <player>";
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
            String targetName = args[0];

            User target = userService.getUserByUsername(targetName).orElse(null);
            if (target == null || !userService.cacheContainsByUUID(target.getUuid())) {
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%player%", targetName);
                player.sendMessage(messages.getComponent("global.user-not-found", placeholders));
                return;
            }

            target.setLastTPARequestPlayer(player.getUniqueId());
            userService.update(target, Destination.CACHE_IF_PRESENT);

            Request request = new Request(player.getUniqueId(), target.getUuid(), RequestType.TPA_HERE);
            requestService.create(request);

            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%target%", targetName);
            placeholders.put("%player%", player.getName());
            placeholders.put("%sender-uuid%", player.getUniqueId().toString());

            player.sendMessage(messages.getComponent("commands.tpahere.success", placeholders));

            List<String> targetMessages = messages.getStringList("commands.tpahere.success-target", placeholders);
            PlayerMessage targetMessage = new PlayerMessage(target.getUuid(), targetMessages);
            messenger.send(serializer.serialize(targetMessage));
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return userService.getAllConnectedUsernames();
        }

        return null;
    }
}
