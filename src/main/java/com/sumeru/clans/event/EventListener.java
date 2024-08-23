package com.sumeru.clans.event;

import com.sumeru.clans.QDClans;
import com.sumeru.clans.gui.ClanMenu;
import com.sumeru.clans.gui.ColorMenu;
import com.sumeru.clans.gui.EnderchestMenu;
import com.sumeru.clans.utils.Utils;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(QDClans.instance, () -> Utils.switchGlowing(event.getPlayer()), 20L);
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
        Utils.switchGlowing(event.getPlayer());
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
                } else if (event.getCurrentItem().getType()==Material.LEATHER_CHESTPLATE) {
                    ColorMenu menu = new ColorMenu();
                    player.openInventory(menu.getInventory());
                }
                event.setCancelled(true);
            }
        } else if (event.getClickedInventory().getHolder() instanceof EnderchestMenu) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType()==Material.RED_STAINED_GLASS_PANE) {
                event.setCancelled(true);
            }
        } else if (event.getClickedInventory().getHolder() instanceof ColorMenu) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType()!=Material.AIR) {
                String clanName = Utils.getPlayerClan(player.getName());
                if (event.getCurrentItem().getType()!=Material.BARRIER && event.getCurrentItem().getType()!=Material.LEVER && clanName!=null) {
                    ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans."+clanName);
                    if (clanSection != null) {
                        if (Objects.equals(clanSection.getString("leader"), player.getName())) {
                            String glowingColor = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(QDClans.instance, "color"), PersistentDataType.STRING);
                            if (glowingColor != null) {
                                clanSection.set("glowing-color", glowingColor);
                                QDClans.instance.saveConfig();
                                TabPlayer plr = TabAPI.getInstance().getPlayer(player.getUniqueId());
                                String tagPrefix = TabAPI.getInstance().getNameTagManager().getOriginalPrefix(plr);

                                Utils.setGlowing(plr, glowingColor, tagPrefix);
                                player.sendMessage(ChatColor.GREEN + "Вы успешно сменили цвет подсветки!");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED+"Вы не являетесь лидером клана!");
                        }
                        player.getOpenInventory().close();
                    }
                } else if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.LEVER) {
                    if (clanName != null) {
                        ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans." + clanName);
                        if (clanSection != null) {
                            if (Objects.equals(clanSection.getString("leader"), player.getName())) {
                                boolean currentGlowState = clanSection.getBoolean("glowing-enabled");
                                clanSection.set("glowing-enabled", !currentGlowState);
                                QDClans.instance.saveConfig();
                                player.sendMessage(ChatColor.GREEN + "Подсветка " + (currentGlowState ? "выключена" : "включена"));
                                List<String> players = clanSection.getStringList("players");
                                List<Player> onlinePlayers = new ArrayList<>();
                                for (String p : players) {
                                    if (Bukkit.getPlayerExact(p)!=null) {
                                        onlinePlayers.add(Bukkit.getPlayerExact(p));
                                    }
                                }
                                if (!onlinePlayers.isEmpty()) {
                                    onlinePlayers.forEach(Utils::switchGlowing);
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Вы не являетесь лидером клана!");
                            }
                            player.getOpenInventory().close();
                        }
                    }
                }
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
