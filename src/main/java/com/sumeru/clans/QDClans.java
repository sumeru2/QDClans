package com.sumeru.clans;

import com.sumeru.clans.command.ClanCommand;
import com.sumeru.clans.economy.Vault;
import com.sumeru.clans.event.EventListener;
import com.sumeru.clans.glowing.Glower;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class QDClans extends JavaPlugin {
    public static QDClans instance;
    public static Logger logger;
    public static Glower glower = new Glower();
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
            saveDefaultConfig();
            logger.info("Plugin enabled!");
            getCommand("clan").setExecutor(new ClanCommand());
            Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        }
    }

    @Override
    public void onDisable() {
        logger.info("Plugin disabled!");
    }
}
