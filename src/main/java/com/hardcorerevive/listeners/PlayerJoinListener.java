package com.hardcorerevive.listeners;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final HardcoreRevivePlugin plugin;

    public PlayerJoinListener(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 加载玩家数据
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) {
            data = new PlayerData(player.getUniqueId(), player.getName());
            plugin.getPlayerDataManager().savePlayerData(data);
        }
        
        // 更新计分板
        plugin.getScoreboardManager().updateAllScoreboards();
    }
}