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
        plugin.getPlayerDataManager().unloadPlayerData(event.getPlayer().getUniqueId());
    }
}
