package com.hardcorerevive.listeners;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final HardcoreRevivePlugin plugin;

    public PlayerDeathListener(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // 获取或创建玩家数据
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) {
            data = new PlayerData(player.getUniqueId(), player.getName());
        }
        
        // 更新死亡信息
        data.setDeathCount(data.getDeathCount() + 1);
        data.setDeathLocation(player.getLocation());
        data.setDeathLevel(player.getLevel());
        
        // 保存数据
        plugin.getPlayerDataManager().savePlayerData(data);
        
        // 生成墓碑
        if (plugin.getConfig().getBoolean("tombstone.enabled", true)) {
            plugin.getTombstoneManager().createTombstone(player, player.getLocation());
        }
        
        // 更新计分板
        plugin.getScoreboardManager().updateAllScoreboards();
    }
}