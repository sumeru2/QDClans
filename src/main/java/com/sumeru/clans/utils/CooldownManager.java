package com.sumeru.clans.utils;

import com.sumeru.clans.QDClans;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    public static boolean cooldownGet(UUID playerId) {
        if (cooldowns.containsKey(playerId)) {
            long lastTime = cooldowns.get(playerId);
            long currentTime = System.currentTimeMillis();
            long cooldownTime = QDClans.instance.getConfig().getInt("command-delay") * 1000L;
            return currentTime - lastTime < cooldownTime;
        }
        return false;
    }
    public static void cooldownSet(UUID playerId) {
        cooldowns.put(playerId, System.currentTimeMillis());
    }
}
