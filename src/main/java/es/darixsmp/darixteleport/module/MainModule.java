package es.darixsmp.darixteleport.module;

import com.google.inject.AbstractModule;
import es.darixsmp.darixteleport.DarixTeleport;
import net.smoothplugins.smoothusersapi.SmoothUsersAPI;

public class MainModule extends AbstractModule {

    private final DarixTeleport plugin;
    private final SmoothUsersAPI smoothUsersAPI;

    public MainModule(DarixTeleport plugin, SmoothUsersAPI smoothUsersAPI) {
        this.plugin = plugin;
        this.smoothUsersAPI = smoothUsersAPI;
    }

    @Override
    protected void configure() {
        bind(DarixTeleport.class).toInstance(plugin);
        bind(SmoothUsersAPI.class).toInstance(smoothUsersAPI);
    }
}
