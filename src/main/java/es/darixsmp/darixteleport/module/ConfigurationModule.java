package es.darixsmp.darixteleport.module;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import net.smoothplugins.smoothbase.configuration.Configuration;

public class ConfigurationModule extends AbstractModule {

    private final Configuration config;
    private final Configuration messages;
    private final Configuration homesMenu;
    private final Configuration homeRemoveConfirmationMenu;

    public ConfigurationModule(Configuration config, Configuration messages, Configuration homesMenu, Configuration homeRemoveConfirmationMenu) {
        this.config = config;
        this.messages = messages;
        this.homesMenu = homesMenu;
        this.homeRemoveConfirmationMenu = homeRemoveConfirmationMenu;
    }

    @Override
    protected void configure() {
        bind(Configuration.class).annotatedWith(Names.named("config")).toInstance(config);
        bind(Configuration.class).annotatedWith(Names.named("messages")).toInstance(messages);
        bind(Configuration.class).annotatedWith(Names.named("homes-menu")).toInstance(homesMenu);
        bind(Configuration.class).annotatedWith(Names.named("home-remove-confirmation-menu")).toInstance(homeRemoveConfirmationMenu);
    }
}
