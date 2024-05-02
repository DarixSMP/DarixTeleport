package es.darixsmp.darixteleport.loader;

import com.google.inject.Inject;
import es.darixsmp.darixteleportapi.warp.WarpService;

public class MainLoader {

    @Inject
    private CommandLoader commandLoader;
    @Inject
    private ListenerLoader listenerLoader;

    @Inject
    private WarpService warpService;

    public void load() {
        commandLoader.load();
        listenerLoader.load();

        warpService.loadWarpsToCache();
    }

    public void unload() {

    }
}
