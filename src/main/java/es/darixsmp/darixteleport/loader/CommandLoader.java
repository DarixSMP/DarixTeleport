package es.darixsmp.darixteleport.loader;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.home.DelHomeCommand;
import es.darixsmp.darixteleport.command.home.HomeCommand;
import es.darixsmp.darixteleport.command.home.PHomeCommand;
import es.darixsmp.darixteleport.command.home.SetHomeCommand;
import es.darixsmp.darixteleport.command.warp.DelWarpCommand;
import es.darixsmp.darixteleport.command.warp.SetWarpCommand;

public class CommandLoader {

    @Inject
    private DarixTeleport plugin;

    @Inject
    private SetHomeCommand setHomeCommand;
    @Inject
    private HomeCommand homeCommand;
    @Inject
    private DelHomeCommand delHomeCommand;
    @Inject
    private PHomeCommand pHomeCommand;
    @Inject
    private SetWarpCommand setWarpCommand;
    @Inject
    private DelWarpCommand delWarpCommand;

    public void load() {
        setHomeCommand.registerCommand(plugin);
        homeCommand.registerCommand(plugin);
        delHomeCommand.registerCommand(plugin);
        pHomeCommand.registerCommand(plugin);
        setHomeCommand.registerCommand(plugin);
        setWarpCommand.registerCommand(plugin);
    }
}
