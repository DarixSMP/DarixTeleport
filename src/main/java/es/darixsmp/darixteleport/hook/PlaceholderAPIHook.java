package es.darixsmp.darixteleport.hook;

import com.google.inject.Inject;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    @Inject
    private UserService userService;

    @Override
    public @NotNull String getIdentifier() {
        return "DarixCrates";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Alex_2k";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equals("homes")) {
            User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
            return String.valueOf(user.getHomes().size());
        }

        return null;
    }
}
