package es.darixsmp.darixteleport.menu;

import com.google.inject.Key;
import com.google.inject.name.Names;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.menu.item.MenuItemBuilder;
import es.darixsmp.darixteleport.utils.HomeUtils;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import es.virtualhit.virtualmenu.event.PlayerClickMenuItemEvent;
import es.virtualhit.virtualmenu.menu.Menu;
import es.virtualhit.virtualmenu.menu.item.Clickable;
import es.virtualhit.virtualmenu.menu.item.MenuItem;
import es.virtualhit.virtualmenu.menu.type.MenuType;
import net.kyori.adventure.text.Component;
import net.smoothplugins.smoothbase.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class HomeRemoveConfirmationMenu extends Menu {

    private final String home;
    private final Configuration menuConfig;
    private final HomeUtils homeUtils;
    private final DarixTeleport plugin;
    private final UserService userService;

    public HomeRemoveConfirmationMenu(Player player, String home) {
        super(player);
        this.home = home;
        this.menuConfig = DarixTeleport.getInjector().getInstance(Key.get(Configuration.class, Names.named("home-remove-confirmation-menu")));
        this.homeUtils = DarixTeleport.getInjector().getInstance(HomeUtils.class);
        this.plugin = DarixTeleport.getInjector().getInstance(DarixTeleport.class);
        this.userService = DarixTeleport.getInjector().getInstance(UserService.class);
    }

    public void open() {
        Component title = menuConfig.getComponent("title");
        int size = menuConfig.getInt("size");
        MenuType type = MenuType.valueOf(menuConfig.getString("type"));
        super.createInventory(type, size, title, new ArrayList<>());

        ItemStack background = new ItemStack(menuConfig.getMaterial("background"));
        background.editMeta(meta -> {
            meta.displayName(Component.empty());
        });
        super.setBackground(background);

        loadStaticItems();
        updateItems();
        updateBackground();
        super.open();
    }

    private void loadStaticItems() {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%home%", home);

        for (String key : menuConfig.getConfigurationSection("static-items").getKeys(false)) {
            Clickable clickable = getClickable(ActionType.valueOf(menuConfig.getString("static-items." + key + ".action").toUpperCase(Locale.ROOT)), home);
            MenuItem item = MenuItemBuilder.build(menuConfig, "static-items." + key, placeholders, clickable);
            super.addItem(item);
        }
    }

    public enum ActionType {
        NONE,
        GO_BACK,
        REMOVE_HOME
    }

    private Clickable getClickable(ActionType type, @Nullable String homeName) {
        return new Clickable() {
            @Override
            public void onClick(PlayerClickMenuItemEvent event) {
                event.setCancelled(true);
                Player player = event.getMenu().getPlayer();

                switch (type) {
                    case REMOVE_HOME -> {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            homeUtils.handleRemoveHome(player, homeName);

                            User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                HomesMenu homesMenu = new HomesMenu(player, user);
                                homesMenu.open();
                            });
                        });
                    }

                    case GO_BACK -> {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            User user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                HomesMenu homesMenu = new HomesMenu(player, user);
                                homesMenu.open();
                            });
                        });
                    }
                }
            }
        };
    }
}
