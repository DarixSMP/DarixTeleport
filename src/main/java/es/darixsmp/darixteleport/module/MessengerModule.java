package es.darixsmp.darixteleport.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.messenger.DefaultMessageConsumer;
import es.darixsmp.darixteleport.messenger.DefaultRedisMessenger;
import net.smoothplugins.smoothbase.connection.RedisConnection;
import net.smoothplugins.smoothbase.messenger.MessageConsumer;
import net.smoothplugins.smoothbase.messenger.Messenger;
import net.smoothplugins.smoothbase.serializer.Serializer;

public class MessengerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MessageConsumer.class).to(DefaultMessageConsumer.class);
    }

    @Provides @Singleton
    public Messenger provideMessenger(DarixTeleport plugin, RedisConnection connection, MessageConsumer consumer, Serializer serializer) {
        return new DefaultRedisMessenger(plugin, connection, consumer, serializer);
    }
}
