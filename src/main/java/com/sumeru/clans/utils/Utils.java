package com.sumeru.clans.utils;

import com.sumeru.clans.QDClans;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.ItemSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    public static void setGlowing(TabPlayer plr, String glowingColor) {
        String tagPrefix = TabAPI.getInstance().getNameTagManager().getOriginalPrefix(plr);
        switch (glowingColor.toLowerCase()) {
            case "red" -> glowingColor = color("&c");
            case "dark_red" -> glowingColor = color("&4");
            case "gold" -> glowingColor = color("&6");
            case "yellow" -> glowingColor = color("&e");
            case "aqua" -> glowingColor = color("&b");
            case "dark_aqua" -> glowingColor = color("&3");
            case "blue" -> glowingColor = color("&9");
            case "dark_blue" -> glowingColor = color("&1");
            case "green" -> glowingColor = color("&a");
            case "dark_green" -> glowingColor = color("&2");
            case "light_purple" -> glowingColor = color("&d");
            case "dark_purple" -> glowingColor = color("&5");
            case "gray" -> glowingColor = color("&7");
            case "dark_gray" -> glowingColor = color("&8");
            case "black" -> glowingColor = color("&0");
        }
        TabAPI.getInstance().getNameTagManager().setPrefix(plr, ((!tagPrefix.isEmpty()) ? tagPrefix : "")+glowingColor);
        TabAPI.getInstance().getTabListFormatManager().setPrefix(plr, tagPrefix);
    }
    public static void switchGlowing(Player player) {
        String playerName = player.getName();
        String clanName = getPlayerClan(playerName);
        TabPlayer plr = TabAPI.getInstance().getPlayer(player.getUniqueId());

        if (clanName != null) {
            ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans."+clanName);

            if (clanSection != null) {
                Boolean isGlowing = clanSection.getBoolean("glowing-enabled");
                String glowingColor = clanSection.getString("glowing-color");
                if (glowingColor != null) {
                    if (!isGlowing || !player.isGlowing()) {
                        player.setGlowing(true);
                        setGlowing(plr, glowingColor);
                        return;
                    }
                }
            }
        }
        player.setGlowing(false);
        String tagPrefix = TabAPI.getInstance().getNameTagManager().getOriginalPrefix(plr);
        TabAPI.getInstance().getNameTagManager().setPrefix(plr, tagPrefix);
        TabAPI.getInstance().getTabListFormatManager().setPrefix(plr, tagPrefix);
    }
    public static byte[] serializeItems(Map<Integer, ItemStack> itemMap) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BukkitObjectOutputStream oos = new BukkitObjectOutputStream(baos)) {
            oos.writeObject(itemMap);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    public static Map<Integer, ItemStack> deserializeItems(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             BukkitObjectInputStream ois = new BukkitObjectInputStream(bais)) {
            return (Map<Integer, ItemStack>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }


    public static void saveInventoryToConfig(String path, ItemStack[] items) {
        Map<Integer, ItemStack> itemMap = new HashMap<>();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() != Material.AIR) {
                itemMap.put(i, items[i]);
            }
        }
        byte[] data = serializeItems(itemMap);
        QDClans.instance.getConfig().set(path, data);
    }


    public static ItemStack[] loadInventoryFromConfig(String path) {
        byte[] data = (byte[]) QDClans.instance.getConfig().get(path);
        Map<Integer, ItemStack> itemMap = deserializeItems(data);

        int size = Collections.max(itemMap.keySet()) + 1;
        ItemStack[] items = new ItemStack[size];
        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            items[entry.getKey()] = entry.getValue();
        }
        return items;
    }

    public static Boolean clanAlreadyExist(String clanName) {
        ConfigurationSection clansSection = QDClans.instance.getConfig().getConfigurationSection("clans");

        if (clansSection != null && !clansSection.getKeys(false).isEmpty()) {
            for (String clan : clansSection.getKeys(false)) {
                if (clan.equals(clanName)) {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean updateClan(String clanName) {
        ConfigurationSection clansSection = QDClans.instance.getConfig().getConfigurationSection("clans." + clanName);

        if (clansSection == null) {
            return false;
        }

        double clanMoney = clansSection.getDouble("money");
        int playerCount = clansSection.getStringList("players").size();
        int currentLevel = clansSection.getInt("level", 1);
        int newLevel = currentLevel;

        switch (currentLevel) {
            case 1:
                if (playerCount >= 1 && clanMoney >= 1000) newLevel = 2;
                break;
            case 2:
                if (playerCount >= 2 && clanMoney >= 2000) newLevel = 3;
                break;
            case 3:
                if (playerCount >= 3 && clanMoney >= 3000) newLevel = 4;
                break;
            case 4:
                if (playerCount >= 4 && clanMoney >= 4000) newLevel = 5;
                break;
            case 5:
                if (playerCount >= 5 && clanMoney >= 5000) newLevel = 6;
                break;
            case 6:
                if (playerCount >= 6 && clanMoney >= 6000) newLevel = 7;
                break;
            case 7:
                if (playerCount >= 7 && clanMoney >= 7000) newLevel = 8;
                break;
            case 8:
                if (playerCount >= 8 && clanMoney >= 8000) newLevel = 9;
                break;
            case 9:
                if (playerCount >= 9 && clanMoney >= 9000) newLevel = 10;
                break;
            case 10:
                if (playerCount >= 10 && clanMoney >= 10000) newLevel = 11;
                break;
            case 11:
                if (playerCount >= 11 && clanMoney >= 11000) newLevel = 12;
                break;
            case 12:
                if (playerCount >= 12 && clanMoney >= 12000) newLevel = 13;
                break;
            case 13:
                if (playerCount >= 13 && clanMoney >= 13000) newLevel = 14;
                break;
            case 14:
                if (playerCount >= 14 && clanMoney >= 14000) newLevel = 15;
                break;
            case 15:
                if (playerCount >= 15 && clanMoney >= 15000) newLevel = 16;
                break;
            case 16:
                if (playerCount >= 16 && clanMoney >= 16000) newLevel = 17;
                break;
            case 17:
                if (playerCount >= 17 && clanMoney >= 17000) newLevel = 18;
                break;
            case 18:
                if (playerCount >= 18 && clanMoney >= 18000) newLevel = 19;
                break;
            case 19:
                if (playerCount >= 19 && clanMoney >= 19000) newLevel = 20;
                break;
            default:
                return false;
        }

        if (newLevel != currentLevel) {
            clansSection.set("level", newLevel);
            QDClans.instance.saveConfig();

            String levelUpMessage = ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.clan_level_up")
                    .replace("%new_level%", String.valueOf(newLevel)));

            for (String playerName : clansSection.getStringList("players")) {
                Player player = Bukkit.getPlayerExact(playerName);
                if (player != null) {
                    player.sendMessage(levelUpMessage);
                }
            }

            return true;
        }

        return false;
    }
    public static String getPlayerClan(String playerName) {
        ConfigurationSection clansSection = QDClans.instance.getConfig().getConfigurationSection("clans");

        if (clansSection != null && !clansSection.getKeys(false).isEmpty()) {
            for (String clanName : clansSection.getKeys(false)) {
                ConfigurationSection clanSection = clansSection.getConfigurationSection(clanName);

                if (clanSection != null && clanSection.contains("players")) {
                    List<String> players = clanSection.getStringList("players");

                    if (players.contains(playerName)) {
                        return clanName;
                    }
                }
            }
        }
        return null;
    }
    public static String centerText(String text, int totalWidth) {
        if (text != null && !text.isEmpty()) {
            int spaces = totalWidth - text.length();
            if (spaces <= 0) {
                return text;
            } else {
                int leftSpaces = spaces / 2;
                int rightSpaces = spaces - leftSpaces;
                return " ".repeat(leftSpaces) + text + " ".repeat(rightSpaces);
            }
        } else {
            return "";
        }
    }
    public static TextComponent getInviteNotification(String sender, String clanName) {
        String invitation = ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.invite_notification"));
        invitation = invitation.replace("%sender%", sender);
        invitation = invitation.replace("%clan_name%", clanName);
        TextComponent inviteMessage = new TextComponent(centerText(invitation, MinecraftFontWidthCalculator.getStringWidth(invitation)));
        TextComponent confirmLink = new TextComponent("[ВСТУПИТЬ(кликабельно)]");
        confirmLink.setColor(net.md_5.bungee.api.ChatColor.RED);
        confirmLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan join " + clanName));
        inviteMessage.addExtra(confirmLink);

        return inviteMessage;
    }
    public static String getExitNotification(String playerName) {
        String invitation = ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.exit_notification"));
        invitation = invitation.replace("%player%", playerName);
        return centerText(invitation, MinecraftFontWidthCalculator.getStringWidth(invitation));
    }
    public static String getKickNotification(String playerName) {
        String invitation = ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.kick_notification"));
        invitation = invitation.replace("%player%", playerName);
        return centerText(invitation, MinecraftFontWidthCalculator.getStringWidth(invitation));
    }
}
