package dev.taikin.miviewer.common;

import io.papermc.paper.datacomponent.DataComponentTypes;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * Shared wand item factory and PDC accessors for the MI viewer plugins.
 *
 * <p>Both plugins use the same item (STICK) and item models
 * ({@code miviewer:wand_menu}, {@code wand_view}, {@code wand_atom},
 * {@code wand_frame}). The wand records which viewer it currently targets so
 * only one plugin opens its GUI when both are installed.</p>
 */
public final class WandItems {

    public enum WandType {
        MENU,
        VIEW,
        ATOM,
        FRAME
    }

    public enum ViewerTarget {
        CIF,
        MOL
    }

    public static final NamespacedKey TYPE_KEY = new NamespacedKey("miviewer", "wand-type");
    public static final NamespacedKey TARGET_KEY = new NamespacedKey("miviewer", "wand-target");

    private WandItems() {
    }

    public static ItemStack create(WandType type, ViewerTarget target, Component name, List<Component> lore) {
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name.decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(TYPE_KEY, PersistentDataType.STRING, type.name());
        meta.getPersistentDataContainer().set(TARGET_KEY, PersistentDataType.STRING, target.name());
        item.setItemMeta(meta);
        item.setData(DataComponentTypes.ITEM_MODEL, modelKey(type));
        return item;
    }

    public static Key modelKey(WandType type) {
        return Key.key("miviewer", "wand_" + type.name().toLowerCase(Locale.ROOT));
    }

    public static WandType type(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        String value = item.getItemMeta().getPersistentDataContainer()
                .get(TYPE_KEY, PersistentDataType.STRING);
        if (value == null) {
            return null;
        }
        try {
            return WandType.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static ViewerTarget target(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        String value = item.getItemMeta().getPersistentDataContainer()
                .get(TARGET_KEY, PersistentDataType.STRING);
        if (value == null) {
            return null;
        }
        try {
            return ViewerTarget.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static void setTarget(ItemStack item, ViewerTarget target) {
        if (item == null) {
            return;
        }
        item.editMeta(meta -> meta.getPersistentDataContainer()
                .set(TARGET_KEY, PersistentDataType.STRING, target.name()));
    }

    public static void migrate(ItemStack item, WandType type, ViewerTarget target) {
        if (item == null) {
            return;
        }
        item.editMeta(meta -> {
            meta.getPersistentDataContainer().set(TYPE_KEY, PersistentDataType.STRING, type.name());
            meta.getPersistentDataContainer().set(TARGET_KEY, PersistentDataType.STRING, target.name());
        });
        item.setData(DataComponentTypes.ITEM_MODEL, modelKey(type));
    }

    public static void refreshAppearance(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            WandType type = type(item);
            if (type == null) {
                continue;
            }
            ItemStack updated = item.clone();
            updated.setData(DataComponentTypes.ITEM_MODEL, modelKey(type));
            player.getInventory().setItem(slot, updated);
        }
    }

    public static ItemStack held(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public static void setHeldTarget(Player player, ViewerTarget target) {
        ItemStack item = held(player);
        if (type(item) == null) {
            return;
        }
        setTarget(item, target);
        player.getInventory().setItemInMainHand(item);
    }
}
