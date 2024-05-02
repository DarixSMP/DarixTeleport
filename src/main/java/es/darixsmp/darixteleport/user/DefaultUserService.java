package es.darixsmp.darixteleport.user;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import es.darixsmp.darixteleportapi.service.Destination;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import net.smoothplugins.smoothbase.serializer.Serializer;
import net.smoothplugins.smoothbase.storage.MongoStorage;
import net.smoothplugins.smoothbase.storage.RedisStorage;
import net.smoothplugins.smoothusersapi.SmoothUsersAPI;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class DefaultUserService implements UserService {

    @Inject @Named("user")
    private MongoStorage mongoStorage;
    @Inject @Named("user")
    private RedisStorage redisStorage;
    @Inject
    private Serializer serializer;
    @Inject
    private SmoothUsersAPI smoothUsersAPI;

    @Override
    public void create(User user) {
        mongoStorage.create(serializer.serialize(user));
    }

    @Nullable
    private UUID getUUIDByUsername(String username) {
        net.smoothplugins.smoothusersapi.user.User user = smoothUsersAPI.getUserService().getUserByUsername(username).orElse(null);
        if (user == null) return null;

        return user.getUuid();
    }

    @Override
    public void update(User user, Destination... destinations) {
        for (Destination destination : destinations) {
            switch (destination) {
                case DATABASE -> {
                    mongoStorage.update( "_id", user.getUuid().toString(), serializer.serialize(user));
                }

                case CACHE_IF_PRESENT -> {
                    if (redisStorage.contains(user.getUuid().toString())) {
                        redisStorage.update(user.getUuid().toString(), serializer.serialize(user));
                    }
                }
            }
        }
    }

    @Override
    public boolean containsByUUID(UUID uuid) {
        return redisStorage.contains(uuid.toString()) || mongoStorage.contains("_id", uuid.toString());
    }

    @Override
    public boolean containsByUsername(String username) {
        UUID uuid = getUUIDByUsername(username);
        if (uuid == null) return false;

        return containsByUUID(uuid);
    }

    @Override
    public Optional<User> getUserByUUID(UUID uuid) {
        User user = serializer.deserialize(redisStorage.get(uuid.toString()), User.class);
        if (user != null) return Optional.of(user);

        user = serializer.deserialize(mongoStorage.get("_id", uuid.toString()), User.class);

        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        UUID uuid = getUUIDByUsername(username);
        if (uuid == null) return Optional.empty();

        return getUserByUUID(uuid);
    }

    @Override
    public void deleteByUUID(UUID uuid, Destination... destinations) {
        for (Destination destination : destinations) {
            switch (destination) {
                case DATABASE -> {
                    mongoStorage.delete("_id", uuid.toString());
                }

                case CACHE_IF_PRESENT -> {
                    if (redisStorage.contains(uuid.toString())) {
                        redisStorage.delete(uuid.toString());
                    }
                }
            }
        }
    }

    @Override
    public void deleteByUsername(String username, Destination... destinations) {
        UUID uuid = getUUIDByUsername(username);
        if (uuid == null) return;

        deleteByUUID(uuid, destinations);
    }

    @Override
    public boolean cacheContainsByUUID(UUID uuid) {
        return redisStorage.contains(uuid.toString());
    }

    @Override
    public boolean cacheContainsByUsername(String username) {
        UUID uuid = getUUIDByUsername(username);
        if (uuid == null) return false;

        return cacheContainsByUUID(uuid);
    }

    @Override
    public void loadToCache(User user) {
        redisStorage.update(user.getUuid().toString(), serializer.serialize(user));
    }

    @Override
    public boolean removeTTLFromCacheByUUID(UUID uuid) {
        return redisStorage.removeTTL(uuid.toString());
    }

    @Override
    public boolean setTTLOfCacheByUUID(UUID uuid, int seconds) {
        return redisStorage.setTTL(uuid.toString(), seconds);
    }
}
