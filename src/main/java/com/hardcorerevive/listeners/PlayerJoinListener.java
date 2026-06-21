package com.hardcorerevive.listeners;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.PlayerData;
import org.bukkit.GameMode;
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
        PlayerData data = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        
        if (data.isJailed()) {
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(plugin.getJailManager().getJailLocation(player.getWorld()));
            player.sendMessage("§c你已死亡，使用 /revive <复活码> 来复活！");
        }
    }
}
