package com.hardcorerevive.managers;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final HardcoreRevivePlugin plugin;
    private final Map<UUID, PlayerData> playerDataCache = new HashMap<>();

    public PlayerDataManager(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    public PlayerData getPlayerData(UUID uuid) {
        if (playerDataCache.containsKey(uuid)) {
            return playerDataCache.get(uuid);
        }
        
        PlayerData data = plugin.getDatabaseManager().getPlayerData(uuid);
        if (data != null) {
            playerDataCache.put(uuid, data);
        }
        return data;
    }

    public PlayerData getOrCreatePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = getPlayerData(uuid);
        
        if (data == null) {
            data = new PlayerData(uuid, player.getName());
            playerDataCache.put(uuid, data);
            plugin.getDatabaseManager().savePlayerData(data);
        } else if (!data.getPlayerName().equals(player.getName())) {
            data.setPlayerName(player.getName());
            plugin.getDatabaseManager().savePlayerData(data);
        }
        
        return data;
    }

    public void savePlayerData(PlayerData data) {
        playerDataCache.put(data.getUuid(), data);
        plugin.getDatabaseManager().savePlayerData(data);
    }

    public void unloadPlayerData(UUID uuid) {
        PlayerData data = playerDataCache.remove(uuid);
        if (data != null) {
            plugin.getDatabaseManager().savePlayerData(data);
        }
    }

    public void saveAll() {
        for (PlayerData data : playerDataCache.values()) {
            plugin.getDatabaseManager().savePlayerData(data);
        }
    }
}
