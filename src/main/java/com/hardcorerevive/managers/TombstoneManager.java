package com.hardcorerevive.managers;

import com.hardcorerevive.HardcoreRevivePlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TombstoneManager {
    private final HardcoreRevivePlugin plugin;
    private final Map<UUID, Location> tombstones = new HashMap<>();

    public TombstoneManager(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;}

    public void createTombstone(UUID playerUuid, String playerName, Location location) {
        if (!plugin.getConfig().getBoolean("tombstone.enabled", true)) {
            return;
        }
        
        Location safeLoc = location.clone();
        if (safeLoc.getBlock().getType() != Material.AIR) {
            safeLoc.add(0, 1, 0);
        }
        Block skullBlock = safeLoc.getBlock();
        skullBlock.setType(Material.PLAYER_HEAD);
        if (skullBlock.getState() instanceof Skull) {
            Skull skull = (Skull) skullBlock.getState();
            skull.setOwningPlayer(plugin.getServer().getOfflinePlayer(playerUuid));
            skull.update();
        }
        
        Block signBlock = safeLoc.getBlock().getRelative(BlockFace.DOWN);
        if (signBlock.getType() == Material.AIR) {
            signBlock.setType(Material.OAK_SIGN);
            if (signBlock.getState() instanceof Sign) {
                Sign sign = (Sign) signBlock.getState();
                sign.setLine(0, "§l§4RIP");
                sign.setLine(1, playerName);
                sign.setLine(2, getCurrentDate());
                sign.setLine(3, "");
                sign.update();
            }
        }
        
        tombstones.put(playerUuid, safeLoc);
    }

    public void removeTombstone(UUID playerUuid) {
        Location loc = tombstones.remove(playerUuid);
        if (loc != null) {
            loc.getBlock().setType(Material.AIR);
            loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.AIR);
        }
    }

    public boolean isTombstone(Location location) {
        return tombstones.containsValue(location);
    }

    private String getCurrentDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new java.util.Date());
    }
}
