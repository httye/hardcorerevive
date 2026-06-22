package com.hardcorerevive.managers;

import com.hardcorerevive.HardcoreRevivePlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TombstoneManager {
    private final HardcoreRevivePlugin plugin;

    public TombstoneManager(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    public void createTombstone(Player player, Location location) {
        World world = location.getWorld();
        if (world == null) return;

        // 确保位置在地面上
        Location tombLoc = location.clone();
        tombLoc.setY(world.getHighestBlockYAt(location) + 1);

        // 创建玩家头颅
        Block headBlock = tombLoc.getBlock();
        headBlock.setType(Material.PLAYER_HEAD);
        
        // 创建告示牌
        Block signBlock = tombLoc.clone().add(0, 1, 0).getBlock();
        signBlock.setType(Material.OAK_SIGN);
        
        if (signBlock.getState() instanceof Sign) {
            Sign sign = (Sign) signBlock.getState();
            sign.setLine(0, "§cR.I.P");
            sign.setLine(1, "§f" + player.getName());
            sign.setLine(2, "§7" + new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
            sign.update();
        }
    }
}