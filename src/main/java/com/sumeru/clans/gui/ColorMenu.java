package com.sumeru.clans.gui;

import com.sumeru.clans.QDClans;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ColorMenu implements InventoryHolder {
    Inventory inventory = Bukkit.createInventory(this, 45, Component.text("Подсветка клана:"));

    public ColorMenu() {
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierMeta = barrier.getItemMeta();
        barrierMeta.setDisplayName("");
        barrier.setItemMeta(barrierMeta);

        ItemStack lever = new ItemStack(Material.LEVER);
        ItemMeta leverMeta = lever.getItemMeta();
        leverMeta.setDisplayName(ChatColor.GREEN+"Вкл/выкл подсветку");
        lever.setItemMeta(leverMeta);

        int[] redPanePositions = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        for (int position : redPanePositions) {
            inventory.setItem(position, barrier);
        }
        ChatColor[] colors = {
                ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA,
                ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY,
                ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA,
                ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW
        };

        int[] colorSlots1 = {10, 11, 12, 13, 14, 15, 16};
        int[] colorSlots2 = {19, 20, 21, 22, 23, 24, 25};

        int colorIndex = 0;
        for (int slot : colorSlots1) {
            if (colorIndex < colors.length) {
                inventory.setItem(slot, createColorItem(colors[colorIndex]));
                colorIndex++;
            }
        }

        for (int slot : colorSlots2) {
            if (colorIndex < colors.length) {
                inventory.setItem(slot, createColorItem(colors[colorIndex]));
                colorIndex++;
            }
        }
        inventory.setItem(28, lever);
    }
    private ItemStack createColorItem(ChatColor color) {
        Material material = switch (color) {
            case DARK_BLUE -> Material.BLUE_STAINED_GLASS_PANE;
            case DARK_GREEN -> Material.GREEN_STAINED_GLASS_PANE;
            case DARK_AQUA, AQUA -> Material.CYAN_STAINED_GLASS_PANE;
            case DARK_RED, RED -> Material.RED_STAINED_GLASS_PANE;
            case DARK_PURPLE -> Material.PURPLE_STAINED_GLASS_PANE;
            case GOLD -> Material.ORANGE_STAINED_GLASS_PANE;
            case GRAY -> Material.LIGHT_GRAY_STAINED_GLASS_PANE;
            case DARK_GRAY -> Material.GRAY_STAINED_GLASS_PANE;
            case BLUE -> Material.LIGHT_BLUE_STAINED_GLASS_PANE;
            case GREEN -> Material.LIME_STAINED_GLASS_PANE;
            case LIGHT_PURPLE -> Material.MAGENTA_STAINED_GLASS_PANE;
            case YELLOW -> Material.YELLOW_STAINED_GLASS_PANE;
            default -> Material.BLACK_STAINED_GLASS_PANE;
        };

        ItemStack colorItem = new ItemStack(material);
        ItemMeta meta = colorItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color + color.name());
            meta.getPersistentDataContainer().set(new NamespacedKey(QDClans.instance, "color"), PersistentDataType.STRING, color.name());
            colorItem.setItemMeta(meta);
        }
        return colorItem;
    }
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
