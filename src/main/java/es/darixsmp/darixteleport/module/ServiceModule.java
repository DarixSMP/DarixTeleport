package es.darixsmp.darixteleport.module;

import com.google.inject.AbstractModule;
import es.darixsmp.darixteleport.countdown.DefaultCountdownService;
import es.darixsmp.darixteleport.pending.DefaultPendingTeleportService;
import es.darixsmp.darixteleport.request.DefaultRequestService;
import es.darixsmp.darixteleport.teleport.DefaultTeleportService;
import es.darixsmp.darixteleport.user.DefaultUserService;
import es.darixsmp.darixteleport.warp.DefaultWarpService;
import es.darixsmp.darixteleportapi.countdown.CountdownService;
import es.darixsmp.darixteleportapi.pending.PendingTeleportService;
import es.darixsmp.darixteleportapi.request.RequestService;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import es.darixsmp.darixteleportapi.user.UserService;
import es.darixsmp.darixteleportapi.warp.WarpService;

public class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserService.class).to(DefaultUserService.class);
        bind(WarpService.class).to(DefaultWarpService.class);
        bind(TeleportService.class).to(DefaultTeleportService.class);
        bind(PendingTeleportService.class).to(DefaultPendingTeleportService.class);
        bind(CountdownService.class).to(DefaultCountdownService.class);
        bind(RequestService.class).to(DefaultRequestService.class);
    }
}
