package com.sumeru.clans.gui;

import com.sumeru.clans.QDClans;
import com.sumeru.clans.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClanMenu implements InventoryHolder {
    Inventory inventory = Bukkit.createInventory(this, 45, Component.text("Меню клана:"));
    public ClanMenu(Player player) {
        ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta paneMeta = redPane.getItemMeta();
        paneMeta.setDisplayName("");
        redPane.setItemMeta(paneMeta);

        ItemStack infoBook = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta bookMeta = infoBook.getItemMeta();
        bookMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6[ &bИнформания &6]"));
        bookMeta.setLore(createLoreForBook(Utils.getPlayerClan(player.getName())));
        infoBook.setItemMeta(bookMeta);

        ItemStack redChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) redChestplate.getItemMeta();
        chestplateMeta.setColor(org.bukkit.Color.GREEN);
        chestplateMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aПодсветка Клана"));
        chestplateMeta.setLore(createLoreForChestplate());
        redChestplate.setItemMeta(chestplateMeta);

        ItemStack yourLevelItem = new ItemStack(Material.DIAMOND);
        ItemMeta yourLevelMeta = yourLevelItem.getItemMeta();
        yourLevelMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bУровень Клана"));
        yourLevelMeta.setLore(createLoreForLevelItem(Utils.getPlayerClan(player.getName())));
        yourLevelItem.setItemMeta(yourLevelMeta);

        ItemStack storageItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta storageMeta = storageItem.getItemMeta();
        storageMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dКлановое Хранилище"));
        storageMeta.setLore(createLoreForStorageItem());
        storageItem.setItemMeta(storageMeta);

        inventory.setItem(33, redChestplate);
        inventory.setItem(34, storageItem);
        inventory.setItem(15, infoBook);
        inventory.setItem(16, yourLevelItem);

        int[] redPanePositions = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        for (int position : redPanePositions) {
            inventory.setItem(position, redPane);
        }
    }

    private static List<String> createLoreForBook(String clanName) {
        List<String> lore = new ArrayList<>();
        ConfigurationSection clansSection = QDClans.instance.getConfig().getConfigurationSection("clans." + clanName);

        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Название клана: " + ChatColor.RESET + clanName);
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Баланс: " + ChatColor.RESET + clansSection.getInt("money"));

        List<String> players = clansSection.getStringList("players");
        String playersString = String.join(", ", players);
        lore.add(ChatColor.AQUA + "" + ChatColor.BOLD + "Участники: " + ChatColor.RESET + playersString);

        lore.add(ChatColor.RED + "" + ChatColor.BOLD + "PvP Статус: " + ChatColor.RESET + (clansSection.getBoolean("pvp-enabled") ? ChatColor.GREEN + "Включен" : ChatColor.RED + "Выключен"));

        return lore;
    }

    private static List<String> createLoreForChestplate() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Игроки вашего клана будут");
        lore.add(ChatColor.YELLOW + "" + ChatColor.ITALIC + "отображены цветной подсветкой.");
        return lore;
    }

    private static List<String> createLoreForLevelItem(String clanName) {
        List<String> lore = new ArrayList<>();
        ConfigurationSection clansSection = QDClans.instance.getConfig().getConfigurationSection("clans." + clanName);

        int level = clansSection.getInt("level");

        lore.add(ChatColor.BLUE + "" + ChatColor.BOLD + "Уровень клана: " + ChatColor.RESET + level);
        lore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "Прокачивайте клан, чтобы");
        lore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "получать новые возможности.");
        lore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "Каждый уровень клана открывает");
        lore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "2 слота в хранилище.");
        lore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "На 3 уровне доступен");
        lore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "/clan sethome.");
        lore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "Уровни повышаются, когда");
        lore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "вы вносите деньги и");
        lore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "в клан присоединяются игроки!");

        return lore;
    }


    private static List<String> createLoreForStorageItem() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Создайте общий клановый");
        lore.add(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "сундук для хранения вещей.");
        return lore;
    }
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
