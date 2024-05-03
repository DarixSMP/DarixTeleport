package es.darixsmp.darixteleport.request;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleportapi.pending.PendingTeleport;
import es.darixsmp.darixteleportapi.request.Request;
import es.darixsmp.darixteleportapi.request.RequestService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import net.smoothplugins.smoothbase.serializer.Serializer;
import net.smoothplugins.smoothbase.storage.RedisStorage;

import java.util.Optional;
import java.util.UUID;

public class DefaultRequestService implements RequestService {

    @Inject @Named("request")
    private RedisStorage redisStorage;
    @Inject
    private Serializer serializer;
    @Inject @Named("config")
    private Configuration config;

    @Override
    public Optional<Request> get(UUID sender, UUID receiver) {
        return Optional.ofNullable(serializer.deserialize(redisStorage.get(sender.toString() + receiver.toString()), Request.class));
    }

    @Override
    public void create(Request request) {
        redisStorage.createWithTTL(request.getSender().toString() + request.getReceiver().toString(),
                serializer.serialize(request), config.getInt("timeouts.pending-teleport-ttl") / 1000);
    }

    @Override
    public void delete(UUID sender, UUID receiver) {
        redisStorage.delete(sender.toString() + receiver.toString());
    }
}
