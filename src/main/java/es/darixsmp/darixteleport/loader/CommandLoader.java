package es.darixsmp.darixteleport.loader;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.command.back.BackCommand;
import es.darixsmp.darixteleport.command.home.*;
import es.darixsmp.darixteleport.command.spawn.SpawnCommand;
import es.darixsmp.darixteleport.command.tp.TPCommand;
import es.darixsmp.darixteleport.command.tp.TPHereCommand;
import es.darixsmp.darixteleport.command.tp.TPOCommand;
import es.darixsmp.darixteleport.command.tpa.TPACancelCommand;
import es.darixsmp.darixteleport.command.tpa.TPACommand;
import es.darixsmp.darixteleport.command.tpa.TPAHereCommand;
import es.darixsmp.darixteleport.command.tpa.TPAcceptCommand;
import es.darixsmp.darixteleport.command.warp.DelWarpCommand;
import es.darixsmp.darixteleport.command.warp.SetWarpCommand;
import es.darixsmp.darixteleport.command.warp.WarpCommand;

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
    @Inject
    private WarpCommand warpCommand;
    @Inject
    private SpawnCommand spawnCommand;
    @Inject
    private BackCommand backCommand;
    @Inject
    private TPOCommand tpoOfflineCommand;
    @Inject
    private TPHereCommand tpHereCommand;
    @Inject
    private TPCommand tpCommand;
    @Inject
    private TPACommand tpaCommand;
    @Inject
    private TPAHereCommand tpaHereCommand;
    @Inject
    private TPAcceptCommand tpaAcceptCommand;
    @Inject
    private TPACancelCommand tpaCancelCommand;
    @Inject
    private HomesCommand homesCommand;
    @Inject
    private HomeLimitCommand homeLimitCommand;

    public void load() {
        setHomeCommand.registerCommand(plugin);
        homeCommand.registerCommand(plugin);
        delHomeCommand.registerCommand(plugin);
        pHomeCommand.registerCommand(plugin);
        setHomeCommand.registerCommand(plugin);
        setWarpCommand.registerCommand(plugin);
        delWarpCommand.registerCommand(plugin);
        warpCommand.registerCommand(plugin);
        spawnCommand.registerCommand(plugin);
        backCommand.registerCommand(plugin);
        tpoOfflineCommand.registerCommand(plugin);
        tpHereCommand.registerCommand(plugin);
        tpCommand.registerCommand(plugin);
        tpaCommand.registerCommand(plugin);
        tpaHereCommand.registerCommand(plugin);
        tpaAcceptCommand.registerCommand(plugin);
        tpaCancelCommand.registerCommand(plugin);
        homesCommand.registerCommand(plugin);
        homeLimitCommand.registerCommand(plugin);
    }
}
