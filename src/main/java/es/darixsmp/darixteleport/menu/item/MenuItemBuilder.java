package es.darixsmp.darixteleport.menu.item;

import es.virtualhit.virtualmenu.menu.item.Clickable;
import es.virtualhit.virtualmenu.menu.item.MenuItem;
import es.virtualhit.virtualmenu.utility.SkullCreator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.smoothplugins.smoothbase.configuration.Configuration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class MenuItemBuilder {

    public static MenuItem build(Configuration config, String path, HashMap<String, String> placeholders, Clickable clickable) {
        Material material = config.getMaterial(path + ".material");
        Component displayName = config.getComponent(path + ".name", placeholders);
        List<Component> lore = config.getComponentList(path + ".lore", placeholders);
        int customModelData = config.getInt(path + ".custom-model-data");

        displayName = displayName.decoration(TextDecoration.ITALIC, false);
        lore = lore.stream().map(component -> component.decoration(TextDecoration.ITALIC, false)).toList();

        ItemStack itemStack = new ItemStack(material);
        if (material == Material.PLAYER_HEAD) {
            itemStack = SkullCreator.itemFromBase64(config.getString(path + ".head"));
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(displayName);
        itemMeta.lore(lore);
        itemMeta.setCustomModelData(customModelData);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);

        MenuItem menuItem = new MenuItem(itemStack, config.getInt(path + ".slot"));
        menuItem.setClickable(clickable);

        return menuItem;
    }
}
