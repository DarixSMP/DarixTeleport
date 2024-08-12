package es.darixsmp.darixteleport.command.home;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.DefaultCommand;
import es.darixsmp.darixteleport.messenger.message.PlayerMessage;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import net.smoothplugins.smoothbase.messenger.Messenger;
import net.smoothplugins.smoothbase.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeLimitCommand extends DefaultCommand {

    @Inject
    private UserService userService;
    @Inject @Named("messages")
    private Configuration messages;
    @Inject
    private DarixTeleport plugin;
    @Inject
    private Messenger messenger;
    @Inject
    private Serializer serializer;

    @Override
    public String getName() {
        return "home-limit";
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public String getPermission() {
        return "darixteleport.command.home-limit";
    }

    @Override
    public int getArgsLength() {
        return 3;
    }

    @Override
    public String getUsage() {
        return "/home-limit <add/remove/set> <jugador> <number>";
    }

    @Override
    public boolean mustBePlayer() {
        return false;
    }

    @Override
    public void registerSubcommands() {

    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String operation = args[0];
            String targetName = args[1];
            int number = 0;

            try {
                number = Integer.parseInt(args[2]);
                if (number < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(messages.getComponent("commands.home-limit.invalid-number"));
                return;
            }

            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", targetName);

            User target = userService.getUserByUsername(targetName).orElse(null);
            if (target == null) {
                sender.sendMessage(messages.getComponent("global.user-not-found", placeholders));
                return;
            }

            placeholders.put("%old-amount%", String.valueOf(target.getExtraHomes()));

            switch (operation.toLowerCase(Locale.ROOT)) {
                case "add" -> {
                    target.setExtraHomes(target.getExtraHomes() + number);
                    userService.update(target, Destination.DATABASE, Destination.CACHE_IF_PRESENT);

                    placeholders.put("%amount%", String.valueOf(number));
                    sender.sendMessage(messages.getComponent("commands.home-limit.add", placeholders));
                }

                case "remove" -> {
                    target.setExtraHomes(target.getExtraHomes() - number);
                    userService.update(target, Destination.DATABASE, Destination.CACHE_IF_PRESENT);

                    placeholders.put("%amount%", String.valueOf(number));
                    sender.sendMessage(messages.getComponent("commands.home-limit.remove", placeholders));
                }

                case "set" -> {
                    target.setExtraHomes(number);
                    userService.update(target, Destination.DATABASE, Destination.CACHE_IF_PRESENT);

                    placeholders.put("%amount%", String.valueOf(number));
                    sender.sendMessage(messages.getComponent("commands.home-limit.set", placeholders));
                }
            }

            placeholders.put("%new-amount%", String.valueOf(target.getExtraHomes()));

            PlayerMessage targetMessage = new PlayerMessage(target.getUuid(), messages.getString("commands.home-limit.success-target", placeholders));
            messenger.send(serializer.serialize(targetMessage));
        });


    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("add", "remove", "set");
        }

        if (args.length == 2) {
            return userService.getAllConnectedUsernames();
        }

        if (args.length == 3) {
            return List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        }

        return null;
    }
}
