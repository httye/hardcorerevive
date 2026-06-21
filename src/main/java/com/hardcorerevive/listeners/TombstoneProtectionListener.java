package com.hardcorerevive.listeners;

import com.hardcorerevive.HardcoreRevivePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class TombstoneProtectionListener implements Listener {
    private final HardcoreRevivePlugin plugin;

    public TombstoneProtectionListener(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getTombstoneManager().isTombstone(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§c你不能破坏墓碑！");
        }
    }
}
