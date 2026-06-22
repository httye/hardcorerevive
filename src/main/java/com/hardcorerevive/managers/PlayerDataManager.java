package com.hardcorerevive.managers;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.PlayerData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {
    private final HardcoreRevivePlugin plugin;
    private final Map<UUID, PlayerData> playerDataCache = new ConcurrentHashMap<>();

    public PlayerDataManager(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    public PlayerData getPlayerData(UUID uuid) {
        PlayerData data = playerDataCache.get(uuid);
        if (data == null) {
            data = plugin.getDatabaseManager().getPlayerData(uuid);
            if (data != null) {
                playerDataCache.put(uuid, data);
            }
        }
        return data;
    }

    public void savePlayerData(PlayerData data) {
        plugin.getDatabaseManager().savePlayerData(data);
        playerDataCache.put(data.getUuid(), data);
    }

    public void unloadPlayer(UUID uuid) {
        playerDataCache.remove(uuid);
    }

    public void clearCache() {
        playerDataCache.clear();
    }
}