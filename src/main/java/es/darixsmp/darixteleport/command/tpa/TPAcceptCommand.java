package es.darixsmp.darixteleport.command.tpa;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.DefaultCommand;
import es.darixsmp.darixteleport.messenger.message.AcceptedTPAMessage;
import es.darixsmp.darixteleportapi.countdown.CountdownService;
import es.darixsmp.darixteleportapi.request.Request;
import es.darixsmp.darixteleportapi.request.RequestService;
import es.darixsmp.darixteleportapi.request.RequestType;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.teleport.TeleportLocation;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import net.smoothplugins.smoothbase.messenger.Messenger;
import net.smoothplugins.smoothbase.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TPAcceptCommand extends DefaultCommand {

    @Inject
    private UserService userService;
    @Inject
    private TeleportService teleportService;
    @Inject
    private CountdownService countdownService;
    @Inject @Named("messages")
    private Configuration messages;
    @Inject @Named("config")
    private Configuration config;
    @Inject
    private DarixTeleport plugin;
    @Inject
    private RequestService requestService;
    @Inject
    private Serializer serializer;
    @Inject
    private Messenger messenger;

    @Override
    public String getName() {
        return "tpaccept";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.tpaccept";
    }

    @Override
    public int getArgsLength() {
        return -1;
    }

    @Override
    public String getUsage() {
        return "/tpaccept";
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
            if (user.getLastTPARequestPlayer() == null) {
                player.sendMessage(messages.getComponent("commands.tpaccept.no-request"));
                return;
            }

            UUID senderUUID = null;
            if (args.length == 0) {
                senderUUID = user.getLastTPARequestPlayer();
            } else {
                try {
                    senderUUID = UUID.fromString(args[0]);
                } catch (IllegalArgumentException exception) {
                    sender.sendMessage(getUsageComponent());
                    return;
                }
            }

            Request request = requestService.get(senderUUID, player.getUniqueId()).orElse(null);
            if (request == null) {
                player.sendMessage(messages.getComponent("commands.tpaccept.no-request"));
                return;
            }

            requestService.delete(senderUUID, player.getUniqueId());

            if (request.getType() == RequestType.TPA) {
                player.sendMessage(messages.getComponent("commands.tpaccept.success"));

                TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
                AcceptedTPAMessage message = new AcceptedTPAMessage(senderUUID, currentLocation, player.getName());
                messenger.send(serializer.serialize(message));
                return;
            }

            TeleportLocation targetLocation = null;
            try {
                targetLocation = teleportService.getTeleportLocation(senderUUID).join();
            } catch (Exception e) {
                player.sendMessage(messages.getComponent("commands.tpaccept.here.get-location-error"));
                return;
            }

            TeleportLocation finalTargetLocation = targetLocation;
            countdownService.startCountdown(player, () -> {
                TeleportLocation currentLocation = TeleportLocation.fromLocation(DarixTeleport.CURRENT_SERVER, player.getLocation());
                User updatedUser = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
                updatedUser.setLastLocation(currentLocation);
                userService.update(updatedUser, Destination.CACHE_IF_PRESENT);

                teleportService.teleport(player.getUniqueId(), finalTargetLocation);
            });
        });
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] strings) {
        return null;
    }
}
