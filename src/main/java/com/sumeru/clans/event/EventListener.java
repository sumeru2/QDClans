package com.sumeru.clans.event;

import com.sumeru.clans.QDClans;
import com.sumeru.clans.gui.ClanMenu;
import com.sumeru.clans.gui.EnderchestMenu;
import com.sumeru.clans.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        String clanName = Utils.getPlayerClan(playerName);

        if (clanName != null) {
            ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans."+clanName);

            if (clanSection != null && clanSection.getBoolean("glowing-enabled")) {
                String glowingColor = clanSection.getString("glowing-color");
                if (glowingColor != null) {
                    QDClans.glower.glowPlayer(event.getPlayer(), glowingColor);
                }
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof EnderchestMenu) {
            String clanName = Utils.getPlayerClan(event.getPlayer().getName());
            Utils.saveInventoryToConfig("storageData."+clanName, event.getInventory().getContents());
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        QDClans.glower.stopGlowing(event.getPlayer());
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory().getHolder() instanceof ClanMenu) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType()!=Material.AIR) {
                if (event.getCurrentItem().getType()==Material.ENDER_CHEST) {
                    EnderchestMenu menu = new EnderchestMenu(Utils.getPlayerClan(player.getName()));
                    if (menu.getInventory().getViewers().isEmpty()) {
                        player.openInventory(menu.getInventory());
                    } else {
                        player.sendMessage(ChatColor.RED+"Кто то уже находится в хранилище!");
                    }
                }
                event.setCancelled(true);
            }
        } else if (event.getClickedInventory().getHolder() instanceof EnderchestMenu) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType()==Material.RED_STAINED_GLASS_PANE) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            String damagerClanName = Utils.getPlayerClan(event.getDamager().getName());
            String entityClanName = Utils.getPlayerClan(event.getEntity().getName());

            if (damagerClanName != null && damagerClanName.equals(entityClanName)) {
                ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans").getConfigurationSection(damagerClanName);

                if (clanSection != null && !clanSection.getBoolean("pvp-enabled")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
