package com.hardcorerevive.listeners;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerData data = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        
        data.incrementDeathCount();
        
        Location deathLoc = player.getLocation();
        boolean hideCoords = plugin.getConfig().getBoolean("announcements.hide_death_coords", false);
        String deathMessage;
        if (hideCoords) {
            deathMessage = "§c" + player.getName() + " 已死亡！";
        } else {
            deathMessage = "§c" + player.getName() + " 在 " + 
                String.format("%.0f, %.0f, %.0f", deathLoc.getX(), deathLoc.getY(), deathLoc.getZ()) + 
                " 死亡！";
        }
        
        Bukkit.broadcastMessage(deathMessage);
        
        if (plugin.getConfig().getBoolean("announcements.death_titlenabled", true)) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle("§4☠", "§c" + player.getName() + " 已死亡", 10, 40, 10);
            }
        }
        plugin.getTombstoneManager().createTombstone(player.getUniqueId(), player.getName(), deathLoc);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                plugin.getJailManager().jailPlayer(player, deathLoc);
            }
        }, 1L);
    }
}
