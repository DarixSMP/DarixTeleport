package es.darixsmp.darixteleport.pending;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleportapi.pending.PendingTeleport;
import es.darixsmp.darixteleportapi.pending.PendingTeleportService;
import net.smoothplugins.smoothbase.configuration.Configuration;
import net.smoothplugins.smoothbase.serializer.Serializer;
import net.smoothplugins.smoothbase.storage.RedisStorage;

import java.util.Optional;
import java.util.UUID;

public class DefaultPendingTeleportService implements PendingTeleportService {

    @Inject @Named("pending")
    private RedisStorage redisStorage;
    @Inject
    private Serializer serializer;
    @Inject @Named("config")
    private Configuration config;

    @Override
    public Optional<PendingTeleport> get(UUID uuid) {
        return Optional.ofNullable(serializer.deserialize(redisStorage.get(uuid.toString()), PendingTeleport.class));
    }

    @Override
    public void create(PendingTeleport pendingTeleport) {
        redisStorage.createWithTTL(pendingTeleport.getUserUUID().toString(),
                serializer.serialize(pendingTeleport), config.getInt("timeouts.pending-teleport-ttl") / 1000);
    }

    @Override
    public void delete(UUID uuid) {
        redisStorage.delete(uuid.toString());
    }
}
