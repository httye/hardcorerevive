package com.hardcorerevive.managers;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.data.DatabaseExtensions;
import com.hardcorerevive.models.PlayerData;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class JailManager {
    private final HardcoreRevivePlugin plugin;
    private DatabaseExtensions dbExt;

    public JailManager(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;try {
            Field connField = plugin.getDatabaseManager().getClass().getDeclaredField("connection");
            connField.setAccessible(true);
            this.dbExt = new DatabaseExtensions(plugin, (java.sql.Connection) connField.get(plugin.getDatabaseManager()));
        } catch (Exception e) {
            plugin.getLogger().severe("初始化 JailManager 失败: " + e.getMessage());
        }
    }

    public void jailPlayer(Player player, Location deathLocation) {
        PlayerData data = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        data.setJailed(true);
        data.setDeathLocation(deathLocation);
        data.setDeathWorld(deathLocation.getWorld().getName());
        data.setDeathLevel(player.getLevel());
        plugin.getPlayerDataManager().savePlayerData(data);
        Location jailLoc = getJailLocation(player.getWorld());
        player.teleport(jailLoc);
        player.setGameMode(GameMode.ADVENTURE);
        if (plugin.getConfig().getBoolean("jail.auto_generate_cage", true)) {
            generateCage(jailLoc);
        }
        
        player.sendTitle("§4§l你已死亡", "§c使用复活码来复活", 10, 70, 20);
    }

    public Location getJailLocation(World world) {
        String worldName = world.getName();
        Location saved = dbExt.getJailLocation(worldName);
        if (saved != null) {
            return saved;
        }
        
        return world.getSpawnLocation();
    }

    public void setJailLocation(Location location) {
        dbExt.saveJailLocation(location.getWorld().getName(), location);
    }

    private void generateCage(Location center) {
        int radius = plugin.getConfig().getInt("jail.cage_radius", 3);
        World world = center.getWorld();
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= radius + 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = new Location(world, cx + x, cy + y, cz + z);
                    boolean isEdge = (Math.abs(x) == radius || Math.abs(z) == radius || y == -1 || y == radius + 1);
                    
                    if (isEdge) {
                        if (y == -1 || y == radius + 1) {
                            loc.getBlock().setType(Material.BEDROCK);
                        } else {
                            loc.getBlock().setType(Material.IRON_BARS);
                        }
                    }
                }
            }
        }
    }

    public void releasePlayer(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null || !data.isJailed()) {
            return;
        }
        
        Location deathLoc = data.getDeathLocation();
        if (deathLoc != null && deathLoc.getWorld() != null) {
            Location safeLoc = findSafeLocation(deathLoc);
            player.teleport(safeLoc);
        } else {
            player.teleport(player.getWorld().getSpawnLocation());
        }
        
        player.setGameMode(GameMode.SURVIVAL);
        data.setJailed(false);
        data.setSurvivalStartTime(System.currentTimeMillis());
        plugin.getPlayerDataManager().savePlayerData(data);
        player.sendTitle("§a§l复活成功", "§2欢迎回来！", 10, 50, 20);
    }

    private Location findSafeLocation(Location loc) {
        if (loc.getY() < -64) {
            loc = loc.getWorld().getSpawnLocation();
        }
        while (loc.getY() < 256 && !loc.getBlock().getType().isAir()) {
            loc.add(0, 1, 0);
        }
        return loc;
    }

    public void checkEscapedPlayers() {
        double escapeDistance = plugin.getConfig().getDouble("jail.escape_distance", 15);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
            if (data != null && data.isJailed()) {
                Location jailLoc = getJailLocation(player.getWorld());
                if (player.getLocation().distance(jailLoc) > escapeDistance) {
                    player.teleport(jailLoc);
                    player.sendMessage("§c检测到越狱行为，已传送回小黑屋！");
                }
            }
        }
    }
}
