package es.darixsmp.darixteleport.api;

import com.google.inject.Inject;
import es.darixsmp.darixteleportapi.DarixTeleportAPI;
import es.darixsmp.darixteleportapi.countdown.CountdownService;
import es.darixsmp.darixteleportapi.pending.PendingTeleportService;
import es.darixsmp.darixteleportapi.request.RequestService;
import es.darixsmp.darixteleportapi.teleport.TeleportService;
import es.darixsmp.darixteleportapi.user.UserService;
import es.darixsmp.darixteleportapi.warp.WarpService;

public class DefaultDarixTeleportAPI implements DarixTeleportAPI {

    @Inject
    private UserService userService;
    @Inject
    private TeleportService teleportService;
    @Inject
    private WarpService warpService;
    @Inject
    private CountdownService countdownService;
    @Inject
    private PendingTeleportService pendingTeleportService;
    @Inject
    private RequestService requestService;

    @Override
    public TeleportService getTeleportService() {
        return teleportService;
    }

    @Override
    public WarpService getWarpService() {
        return warpService;
    }

    @Override
    public CountdownService getCountdownService() {
        return countdownService;
    }

    @Override
    public PendingTeleportService getPendingTeleportService() {
        return pendingTeleportService;
    }

    @Override
    public RequestService getRequestService() {
        return requestService;
    }

    @Override
    public UserService getUserService() {
        return userService;
    }
}
