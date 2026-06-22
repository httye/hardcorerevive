package com.hardcorerevive.data;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.PlayerData;
import org.bukkit.Location;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private final HardcoreRevivePlugin plugin;
    private Connection connection;

    public DatabaseManager(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean initialize() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            String path = plugin.getConfig().getString("database.path", "plugins/HardcoreRevive/data.db");
            File dbFile = new File(path);
            dbFile.getParentFile().mkdirs();
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            
            createTables();
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS player_data (" +
            "uuid VARCHAR(36) PRIMARY KEY," +
            "player_name VARCHAR(16) NOT NULL," +
            "death_count INT DEFAULT 0," +
            "last_death_time BIGINT DEFAULT 0," +
            "death_world VARCHAR(255)," +
            "death_x DOUBLE," +
            "death_y DOUBLE," +
            "death_z DOUBLE," +
            "death_yaw FLOAT," +
            "death_pitch FLOAT," +
            "death_level INT DEFAULT 0" +
            ")");
        stmt.close();
    }

    public PlayerData getPlayerData(UUID uuid) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM player_data WHERE uuid = ?");
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                PlayerData data = new PlayerData(uuid, rs.getString("player_name"));
                data.setDeathCount(rs.getInt("death_count"));
                data.setDeathLevel(rs.getInt("death_level"));
                
                String deathWorld = rs.getString("death_world");
                if (deathWorld != null && Bukkit.getWorld(deathWorld) != null) {
                    data.setDeathWorld(deathWorld);
                    Location loc = new Location(
                        Bukkit.getWorld(deathWorld),
                        rs.getDouble("death_x"),
                        rs.getDouble("death_y"),
                        rs.getDouble("death_z"),
                        rs.getFloat("death_yaw"),
                        rs.getFloat("death_pitch")
                    );
                    data.setDeathLocation(loc);
                }
                
                rs.close();
                stmt.close();
                return data;
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("获取玩家数据失败: " + e.getMessage());
        }
        return null;
    }

    public void savePlayerData(PlayerData data) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "REPLACE INTO player_data " +
                "(uuid, player_name, death_count, last_death_time, " +
                "death_world, death_x, death_y, death_z, death_yaw, death_pitch, death_level) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            
            stmt.setString(1, data.getUuid().toString());
            stmt.setString(2, data.getPlayerName());
            stmt.setInt(3, data.getDeathCount());
            stmt.setLong(4, System.currentTimeMillis());
            
            Location deathLoc = data.getDeathLocation();
            if (deathLoc != null && deathLoc.getWorld() != null) {
                stmt.setString(5, deathLoc.getWorld().getName());
                stmt.setDouble(6, deathLoc.getX());
                stmt.setDouble(7, deathLoc.getY());
                stmt.setDouble(8, deathLoc.getZ());
                stmt.setFloat(9, deathLoc.getYaw());
                stmt.setFloat(10, deathLoc.getPitch());
            } else {
                stmt.setNull(5, Types.VARCHAR);
                stmt.setNull(6, Types.DOUBLE);
                stmt.setNull(7, Types.DOUBLE);
                stmt.setNull(8, Types.DOUBLE);
                stmt.setNull(9, Types.FLOAT);
                stmt.setNull(10, Types.FLOAT);
            }
            
            stmt.setInt(11, data.getDeathLevel());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("保存玩家数据失败: " + e.getMessage());
        }
    }

    public int getTotalDeathCount() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SUM(death_count) as total FROM player_data");
            if (rs.next()) {
                int total = rs.getInt("total");
                rs.close();
                stmt.close();
                return total;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("获取总死亡数失败: " + e.getMessage());
        }
        return 0;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("关闭数据库连接失败: " + e.getMessage());
        }
    }
}