package com.sumeru.clans.glowing;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Glower {
    public HashMap<Player, Team> teams = new HashMap<>();
    public void glowColor(Player player, ChatColor color) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team glowColorTeam = scoreboard.getTeam(player.getName());
        if (glowColorTeam == null) {
            glowColorTeam = scoreboard.registerNewTeam(player.getName());
        }

        glowColorTeam.setPrefix(color.toString());
        glowColorTeam.setColor(color);
        glowColorTeam.addEntry(player.getName());
        teams.put(player, glowColorTeam);
    }
    public void stopGlowing(Player player) {
        if (teams.containsKey(player)) {
            teams.get(player).unregister();
            teams.remove(player);
        }
        player.setGlowing(false);
    }
    public void glowPlayer(Player p, String color) {
        p.setGlowing(true);
        glowColor(p, ChatColor.valueOf(color));
    }
}
