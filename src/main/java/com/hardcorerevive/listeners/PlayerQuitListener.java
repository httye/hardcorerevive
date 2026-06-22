package com.hardcorerevive.listeners;

import com.hardcorerevive.HardcoreRevivePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final HardcoreRevivePlugin plugin;

    public PlayerQuitListener(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 卸载玩家数据缓存
        plugin.getPlayerDataManager().unloadPlayer(event.getPlayer().getUniqueId());
        
        // 更新计分板
        plugin.getScoreboardManager().updateAllScoreboards();
    }
}