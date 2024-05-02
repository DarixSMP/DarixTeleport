package es.darixsmp.darixteleport.module;

import com.google.inject.AbstractModule;
import es.darixsmp.darixteleport.teleport.DefaultTeleportService;
import es.darixsmp.darixteleport.user.DefaultUserService;
import es.darixsmp.darixteleport.warp.DefaultWarpService;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import es.darixsmp.darixteleportapi.user.UserService;
import es.darixsmp.darixteleportapi.warp.WarpService;

public class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserService.class).to(DefaultUserService.class);
        bind(WarpService.class).to(DefaultWarpService.class);
        bind(TeleportService.class).to(DefaultTeleportService.class);
    }
}
