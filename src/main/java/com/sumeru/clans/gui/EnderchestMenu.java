package com.sumeru.clans.gui;

import com.sumeru.clans.QDClans;
import com.sumeru.clans.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnderchestMenu implements InventoryHolder {
    Inventory inventory = Bukkit.createInventory(this, 45, Component.text("Меню клана:"));
    public EnderchestMenu(String clanName) {
        if(QDClans.instance.getConfig().contains("storageData." + clanName)) {
            ItemStack[] items = Utils.loadInventoryFromConfig("storageData." + clanName);
            inventory.setContents(items);
        }
        ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta paneMeta = redPane.getItemMeta();
        paneMeta.setDisplayName("");
        redPane.setItemMeta(paneMeta);
        ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans."+clanName);
        if (clanSection != null) {
            int slots = 2 * clanSection.getInt("level")+2;
            List<Integer> freeSlotPositions = new ArrayList<>();
            for (int i = 0; i < slots; i++) {
                freeSlotPositions.add(i);
            }
            for (int i = 0; i <= 44; i++) {
                if (!freeSlotPositions.contains(i)) {
                    inventory.setItem(i, redPane);
                } else if (inventory.getItem(i) != null && inventory.getItem(i).getType()==Material.RED_STAINED_GLASS_PANE) {
                    inventory.setItem(i, new ItemStack(Material.AIR));
                }
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
