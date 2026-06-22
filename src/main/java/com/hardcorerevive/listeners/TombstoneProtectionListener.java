package com.hardcorerevive.listeners;

import com.hardcorerevive.HardcoreRevivePlugin;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class TombstoneProtectionListener implements Listener {
    private final HardcoreRevivePlugin plugin;

    public TombstoneProtectionListener(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // 如果墓碑保护未启用，直接返回
        if (!plugin.getConfig().getBoolean("tombstone.enabled", true)) {
            return;
        }

        // 检查是否是玩家头颅或告示牌
        Material type = event.getBlock().getType();
        if (type == Material.PLAYER_HEAD || type.name().contains("SIGN")) {
            // 如果没有权限，取消破坏
            if (!event.getPlayer().hasPermission("hardcorerevive.admin")) {
                event.getPlayer().sendMessage("§c墓碑受保护，无法破坏！");
                event.setCancelled(true);
            }
        }
    }
}