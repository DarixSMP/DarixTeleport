package es.darixsmp.darixteleport.command;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.kyori.adventure.text.Component;
import net.smoothplugins.smoothbase.command.Command;
import net.smoothplugins.smoothbase.configuration.Configuration;

import java.util.HashMap;

public abstract class DefaultCommand extends Command {

    @Inject
    @Named("messages")
    private Configuration messages;

    @Override
    public Component getPlayerComponent() {
        return messages.getComponent("global.not-a-player");
    }

    @Override
    public Component getPermissionComponent() {
        return messages.getComponent("global.no-permission");
    }

    @Override
    public Component getUsageComponent() {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%usage%", getUsage());
        return messages.getComponent("global.wrong-usage", placeholders);
    }
}
