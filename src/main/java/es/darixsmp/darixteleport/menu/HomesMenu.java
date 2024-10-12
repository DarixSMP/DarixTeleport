package es.darixsmp.darixteleport.menu;

import com.google.inject.Key;
import com.google.inject.name.Names;
import es.darixsmp.darixteleport.DarixTeleport;
import es.darixsmp.darixteleport.menu.item.MenuItemBuilder;
import es.darixsmp.darixteleport.utils.HomeUtils;
import es.darixsmp.darixteleportapi.user.User;
import es.darixsmp.darixteleportapi.user.UserService;
import es.virtualhit.virtualmenu.event.PlayerClickMenuItemEvent;
import es.virtualhit.virtualmenu.menu.PaginatedMenu;
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
import java.util.List;
import java.util.Locale;

public class HomesMenu extends PaginatedMenu {

    private User user;
    private final Configuration menuConfig;
    private final Configuration messages;
    private final HomeUtils homeUtils;
    private final DarixTeleport plugin;
    private final UserService userService;
    private int itemsPerPage;
    private int maxHomes;

    public HomesMenu(Player player, User user) {
        super(player);
        this.user = user;
        this.menuConfig = DarixTeleport.getInjector().getInstance(Key.get(Configuration.class, Names.named("homes-menu")));
        this.messages = DarixTeleport.getInjector().getInstance(Key.get(Configuration.class, Names.named("messages")));
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

        itemsPerPage = menuConfig.getIntegerList("dynamic-items.bed.slots").size();
        maxHomes = homeUtils.getMaxHomes(getPlayer(), user);

        loadStaticItems();
        loadDynamicItems();
        super.open();
    }

    private void loadStaticItems() {
        for (String key : menuConfig.getConfigurationSection("static-items").getKeys(false)) {
            Clickable clickable = getClickable(ActionType.valueOf(menuConfig.getString("static-items." + key + ".action").toUpperCase(Locale.ROOT)), null);
            MenuItem item = MenuItemBuilder.build(menuConfig, "static-items." + key, null, clickable);
            super.addItem(item);
        }
    }

    private void loadDynamicItems() {
        int start = getCurrentPage() * itemsPerPage;
        int end = getCurrentPage() * itemsPerPage + itemsPerPage;

        List<String> homeNames = new ArrayList<>(user.getHomes().keySet());

        int slotCount = 0;
        while (start < end) {
            removeItem(menuConfig.getIntegerList("dynamic-items.bed.slots").get(slotCount));
            removeItem(menuConfig.getIntegerList("dynamic-items.remove.slots").get(slotCount));
            getInventory().setItem(menuConfig.getIntegerList("dynamic-items.remove.slots").get(slotCount), null);

            if (homeNames.size() <= start) {
                if (start < maxHomes) {
                    Clickable setClickable = getClickable(ActionType.SET_HOME, null);
                    String bedPath = "dynamic-items.bed.unset";
                    MenuItem bedItem = MenuItemBuilder.build(menuConfig, bedPath, null, setClickable);
                    bedItem.setSlot(menuConfig.getIntegerList("dynamic-items.bed.slots").get(slotCount));
                    super.addItem(bedItem);

                    start++;
                    slotCount++;
                    continue;
                }

                String bedPath = "dynamic-items.bed.not-available";
                MenuItem bedItem = MenuItemBuilder.build(menuConfig, bedPath, null, getClickable(ActionType.NONE, null));
                bedItem.setSlot(menuConfig.getIntegerList("dynamic-items.bed.slots").get(slotCount));
                super.addItem(bedItem);

                start++;
                slotCount++;
                continue;
            }

            String home = homeNames.get(start);

            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%home%", home);

            Clickable teleportClickable = getClickable(ActionType.TELEPORT_HOME, home);
            String bedPath = "dynamic-items.bed.set";
            MenuItem bedItem = MenuItemBuilder.build(menuConfig, bedPath, placeholders, teleportClickable);
            bedItem.setSlot(menuConfig.getIntegerList("dynamic-items.bed.slots").get(slotCount));

            Clickable removeClickable = getClickable(ActionType.REMOVE_HOME, home);
            String removePath = "dynamic-items.remove";
            MenuItem removeItem = MenuItemBuilder.build(menuConfig, removePath, placeholders, removeClickable);
            removeItem.setSlot(menuConfig.getIntegerList("dynamic-items.remove.slots").get(slotCount));

            super.addItem(bedItem);
            super.addItem(removeItem);
            start++;
            slotCount++;
        }

        super.updateItems();
        super.updateBackground();
    }

    public enum ActionType {
        NONE,
        PREVIOUS_PAGE,
        CLOSE,
        NEXT_PAGE,
        SET_HOME,
        TELEPORT_HOME,
        REMOVE_HOME
    }

    private Clickable getClickable(ActionType type, @Nullable String homeName) {
        return new Clickable() {
            @Override
            public void onClick(PlayerClickMenuItemEvent event) {
                event.setCancelled(true);
                Player player = event.getMenu().getPlayer();

                switch (type) {
                    case CLOSE -> {
                        player.closeInventory();
                    }

                    case PREVIOUS_PAGE -> {
                        if (getCurrentPage() <= 0) {
                            player.sendMessage(messages.getComponent("global.min-previous-page"));
                            return;
                        }

                        setCurrentPage(getCurrentPage() - 1);
                        loadDynamicItems();
                    }

                    case NEXT_PAGE -> {
                        if (getCurrentPage() * itemsPerPage + itemsPerPage >= (maxHomes == Integer.MAX_VALUE ? maxHomes : maxHomes + 1)) {
                            player.sendMessage(messages.getComponent("global.max-next-page"));
                            return;
                        }

                        setCurrentPage(getCurrentPage() + 1);
                        loadDynamicItems();
                    }

                    case SET_HOME -> {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            boolean success = homeUtils.handleSetHome(player, null);

                            if (success) {
                                user = userService.getUserByUUID(player.getUniqueId()).orElseThrow();

                                Bukkit.getScheduler().runTask(plugin, () -> {
                                    loadDynamicItems();
                                });
                            }
                        });
                    }

                    case TELEPORT_HOME -> {
                        player.closeInventory();
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            homeUtils.handleTeleportHome(player, homeName);
                        });
                    }

                    case REMOVE_HOME -> {
                        HomeRemoveConfirmationMenu menu = new HomeRemoveConfirmationMenu(player, homeName);
                        menu.open();
                    }
                }
            }
        };
    }
}
