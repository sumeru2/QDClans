package com.sumeru.clans.placeholder;

import com.sumeru.clans.QDClans;
import com.sumeru.clans.utils.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClanPlaceholder extends PlaceholderExpansion implements Relational {

    @Override
    @NotNull
    public String getAuthor() {
        return "sumeru";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "clan";
    }

    @Override
    @NotNull
    public String getVersion() {
        return QDClans.instance.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        String clanName = Utils.getPlayerClan(player.getName());
        if (params.equalsIgnoreCase("name") && clanName!=null) {
            return clanName;
        }

        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player, Player player1, String s) {
        return "";
    }
}
