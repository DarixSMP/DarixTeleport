package es.darixsmp.darixteleport.module;

import com.google.inject.AbstractModule;
import es.darixsmp.darixteleport.user.DefaultUserService;
import es.darixsmp.darixteleportapi.user.UserService;

public class UserModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserService.class).to(DefaultUserService.class);
    }
}
