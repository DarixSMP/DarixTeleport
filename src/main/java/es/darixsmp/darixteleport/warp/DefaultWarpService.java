package es.darixsmp.darixteleport.warp;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.warp.Warp;
import es.darixsmp.darixteleportapi.warp.WarpService;
import net.smoothplugins.smoothbase.serializer.Serializer;
import net.smoothplugins.smoothbase.storage.MongoStorage;
import net.smoothplugins.smoothbase.storage.RedisStorage;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class DefaultWarpService implements WarpService {

    @Inject @Named("warp")
    private MongoStorage mongoStorage;
    @Inject @Named("warp")
    private RedisStorage redisStorage;
    @Inject
    private Serializer serializer;

    @Override
    public Optional<Warp> get(String warp) {
        return Optional.ofNullable(serializer.deserialize(redisStorage.get(warp), Warp.class));
    }

    @Override
    public void create(Warp warp) {
        mongoStorage.create(serializer.serialize(warp));
        redisStorage.create(warp.getName(), serializer.serialize(warp));
    }

    @Override
    public void delete(String name, Destination... destinations) {
        name = name.toLowerCase(Locale.ROOT);

        for (Destination destination : destinations) {
            switch (destination) {
                case DATABASE -> {
                    mongoStorage.delete("_id", name);
                }

                case CACHE_IF_PRESENT -> {
                    if (redisStorage.contains(name)) {
                        redisStorage.delete(name);
                    }
                }
            }
        }
    }

    @Override
    public void update(Warp warp, Destination... destinations) {
        for (Destination destination : destinations) {
            switch (destination) {
                case DATABASE -> {
                    mongoStorage.update("_id", warp.getName(), serializer.serialize(warp));
                }

                case CACHE_IF_PRESENT -> {
                    if (redisStorage.contains(warp.getName())) {
                        redisStorage.update(warp.getName(), serializer.serialize(warp));
                    }
                }
            }
        }
    }

    @Override
    public List<Warp> getAll() {
        return redisStorage.getAllValues().stream().map(warpJSON -> serializer.deserialize(warpJSON, Warp.class)).toList();
    }

    @Override
    public void loadWarpsToCache() {
        mongoStorage.getAllValues().forEach(warpJSON -> {
            Warp warp = serializer.deserialize(warpJSON, Warp.class);
            if (redisStorage.contains(warp.getName())) return;

            redisStorage.create(warp.getName(), warpJSON);
        });
    }
}
