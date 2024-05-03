package es.darixsmp.darixteleport.loader;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.home.DelHomeCommand;
import es.darixsmp.darixteleport.command.home.HomeCommand;
import es.darixsmp.darixteleport.command.home.SetHomeCommand;

public class CommandLoader {

    @Inject
    private DarixTeleport plugin;

    @Inject
    private SetHomeCommand setHomeCommand;
    @Inject
    private HomeCommand homeCommand;
    @Inject
    private DelHomeCommand delHomeCommand;

    public void load() {
        setHomeCommand.registerCommand(plugin);
        homeCommand.registerCommand(plugin);
        delHomeCommand.registerCommand(plugin);
    }
}
