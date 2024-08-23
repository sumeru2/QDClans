package com.sumeru.clans;

import com.sumeru.clans.command.ClanCommand;
import com.sumeru.clans.economy.Vault;
import com.sumeru.clans.event.EventListener;
import com.sumeru.clans.utils.Utils;
import me.neznamy.tab.api.TabAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class QDClans extends JavaPlugin {
    public static QDClans instance;
    public static Logger logger;
    public boolean usingTAB;
    @Override
    public void onEnable() {
        instance = this;
        logger = this.getLogger();
        if (!Vault.setupEconomy(instance)) {
            getLogger().severe(String.format("[%s] - Плагин отключается, так как отсутствует одна из зависимостей - Vault!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        } else {
            Vault.setupPermissions(instance);
            Vault.setupChat(instance);
            if (Bukkit.getPluginManager().getPlugin("TAB")!=null) {
                usingTAB = true;
                TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%clan_name%", 100, player -> {
                    String clanName = Utils.getPlayerClan(player.getName());
                    if (clanName != null) {
                        String message = QDClans.instance.getConfig().getString("clan-name-placeholder");
                        message = ChatColor.translateAlternateColorCodes('&', message.replace("%clan_name%", clanName));
                        return message;
                    } else {
                        return "";
                    }
                });
                saveDefaultConfig();
                logger.info("Plugin enabled!");
                getCommand("clan").setExecutor(new ClanCommand());
                Bukkit.getPluginManager().registerEvents(new EventListener(), this);
            } else {
                getLogger().severe(String.format("[%s] - Плагин отключается, так как отсутствует одна из зависимостей - TAB!", getDescription().getName()));
                getServer().getPluginManager().disablePlugin(this);
            }
        }
    }

    @Override
    public void onDisable() {
        logger.info("Plugin disabled!");
    }
}
