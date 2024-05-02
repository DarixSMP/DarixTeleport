package es.darixsmp.darixteleport.loader;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.home.SetHomeCommand;

public class CommandLoader {

    @Inject
    private DarixTeleport plugin;

    @Inject
    private SetHomeCommand setHomeCommand;

    public void load() {
        setHomeCommand.registerCommand(plugin);
    }
}
