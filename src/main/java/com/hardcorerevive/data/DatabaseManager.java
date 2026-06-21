package com.hardcorerevive.data;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.Bounty;
import com.hardcorerevive.models.PlayerData;
import com.hardcorerevive.models.ReviveCode;
import org.bukkit.Location;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private final HardcoreRevivePlugin plugin;
    private Connection connection;

    public DatabaseManager(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;}

    public boolean initialize() {
        try {
            String dbType = plugin.getConfig().getString("database.type", "sqlite");
            if (dbType.equalsIgnoreCase("sqlite")) {
                File dataFolder = plugin.getDataFolder();
                if (!dataFolder.exists()) {
                    dataFolder.mkdirs();
                }
                String path = plugin.getConfig().getString("database.sqlite_path", "plugins/HardcoreRevive/data.db");
                File dbFile = new File(path);
                dbFile.getParentFile().mkdirs();
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            } else if (dbType.equalsIgnoreCase("mysql")) {
                String host = plugin.getConfig().getString("database.mysql.host");
                int port = plugin.getConfig().getInt("database.mysql.port");
                String database = plugin.getConfig().getString("database.mysql.database");
                String username = plugin.getConfig().getString("database.mysql.username");
                String password = plugin.getConfig().getString("database.mysql.password");
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database,
                    username, password
                );
            }
            
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
            "is_jailed BOOLEAN DEFAULT 0," +
            "death_count INT DEFAULT 0," +
            "revive_count INT DEFAULT 0," +
            "last_revive_time BIGINT DEFAULT 0," +
            "last_sos_time BIGINT DEFAULT 0," +
            "death_world VARCHAR(255)," +
            "death_x DOUBLE," +
            "death_y DOUBLE," +
            "death_z DOUBLE," +
            "death_yaw FLOAT," +
            "death_pitch FLOAT," +
            "death_level INT DEFAULT 0," +
            "survival_start_time BIGINT DEFAULT 0" +
            ")");
        stmt.execute("CREATE TABLE IF NOT EXISTS revive_codes (" +
            "code VARCHAR(8) PRIMARY KEY," +
            "owner_uuid VARCHAR(36) NOT NULL," +
            "bound_player_uuid VARCHAR(36) NOT NULL," +
            "used BOOLEAN DEFAULT 0," +
            "created_at BIGINT NOT NULL," +
            "expiry_time BIGINT DEFAULT 0" +
            ")");
        
        stmt.execute("CREATE TABLE IF NOT EXISTS trust_list (" +
            "player_uuid VARCHAR(36) NOT NULL," +
            "trusted_uuid VARCHAR(36) NOT NULL," +
            "PRIMARY KEY (player_uuid, trusted_uuid)" +
            ")");
        
        stmt.execute("CREATE TABLE IF NOT EXISTS jail_locations (" +
            "world_name VARCHAR(255) PRIMARY KEY," +
            "x DOUBLE NOT NULL," +
            "y DOUBLE NOT NULL," +
            "z DOUBLE NOT NULL," +
            "yaw FLOAT DEFAULT 0," +
            "pitch FLOAT DEFAULT 0" +
            ")");
        
        stmt.execute("CREATE TABLE IF NOT EXISTS bounties (" +
            "id INTEGER PRIMARY KEY " + (isMySQL() ? "AUTO_INCREMENT" : "AUTOINCREMENT") + "," +
            "creator_uuid VARCHAR(36) NOT NULL," +
            "description TEXT NOT NULL," +
            "reward_code VARCHAR(8) NOT NULL," +
            "claimed BOOLEAN DEFAULT 0," +
            "claimer_uuid VARCHAR(36)," +
            "created_at BIGINT NOT NULL," +
            "claimed_at BIGINT DEFAULT 0" +
            ");
        
        stmt.execute("CREATE TABLE IF NOT EXISTS invitations (" +
            "inviter_uuid VARCHAR(36) NOT NULL," +
            "invited_uuid VARCHAR(36) PRIMARY KEY," +
            "invited_name VARCHAR(16) NOT NULL," +
            "join_time BIGINT NOT NULL," +
            "rewarded BOOLEAN DEFAULT 0" +
            ")");
        stmt.close();
    }

    private boolean isMySQL() {
        try {
            return connection.getMetaData().getURL().startsWith("jdbc:mysql");
        } catch (SQLException e) {
            return false;
        }
    }

    public PlayerData getPlayerData(UUID uuid) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM player_data WHERE uuid = ?");
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                PlayerData data = new PlayerData(uuid, rs.getString("player_name"));
                data.setJailed(rs.getBoolean("is_jailed"));
                data.setDeathCount(rs.getInt("death_count"));
                data.setReviveCount(rs.getInt("revive_count"));
                data.setLastReviveTime(rs.getLong("last_revive_time"));
                data.setLastSosTime(rs.getLong("last_sos_time"));
                data.setDeathLevel(rs.getInt("death_level"));
                data.setSurvivalStartTime(rs.getLong("survival_start_time"));
                
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
                "(uuid, player_name, is_jailed, death_count, revive_count, last_revive_time, last_sos_time, " +
                "death_world, death_x, death_y, death_z, death_yaw, death_pitch, death_level, survival_start_time) " +
                "VALUES (?, ?, ?, ?, )"
            );
            
            stmt.setString(1, data.getUuid().toString());
            stmt.setString(2, data.getPlayerName());
            stmt.setBoolean(3, data.isJailed());
            stmt.setInt(4, data.getDeathCount());
            stmt.setInt(5, data.getReviveCount());
            stmt.setLong(6, data.getLastReviveTime());
            stmt.setLong(7, data.getLastSosTime());
            Location deathLoc = data.getDeathLocation();
            if (deathLoc != null && deathLoc.getWorld() != null) {
                stmt.setString(8, deathLoc.getWorld().getName());
                stmt.setDouble(9, deathLoc.getX());
                stmt.setDouble(10, deathLoc.getY());
                stmt.setDouble(11, deathLoc.getZ());
                stmt.setFloat(12, deathLoc.getYaw());
                stmt.setFloat(13, deathLoc.getPitch());
            } else {
                stmt.setNull(8, Types.VARCHAR);
                stmt.setNull(9, Types.DOUBLE);
                stmt.setNull(10, Types.DOUBLE);
                stmt.setNull(11, Types.DOUBLE);
                stmt.setNull(12, Types.FLOAT);
                stmt.setNull(13, Types.FLOAT);
            }
            
            stmt.setInt(14, data.getDeathLevel());
            stmt.setLong(15, data.getSurvivalStartTime());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("保存玩家数据失败: " + e.getMessage());
        }
    }

    public List<PlayerData> getAllJailedPlayers() {
        List<PlayerData> players = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT uuid FROM player_data WHERE is_jailed = 1");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                PlayerData data = getPlayerData(uuid);
                if (data != null) {
                    players.add(data);
                }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("获取所有被关押玩家失败: " + e.getMessage());
        }
        return players;
    }

    public void saveReviveCode(ReviveCode code) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "REPLACE INTO revive_codes " +
                "(code, owner_uuid, bound_player_uuid, used, created_at, expiry_time) " +
                "VALUES (?, ?, ?, ?, ?)"
            );
            
            stmt.setString(1, code.getCode());
            stmt.setString(2, code.getOwnerUuid().toString());
            stmt.setString(3, code.getBoundPlayerUuid().toString());
            stmt.setBoolean(4, code.isUsed());
            stmt.setLong(5, code.getCreatedAt());
            stmt.setLong(6, code.getExpiryTime());
            
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("保存复活码失败: " + e.getMessage());
        }
    }

    public ReviveCode getReviveCode(String code) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM revive_codes WHERE code = ?");
            stmt.setString(1, code.toUpperCase());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ReviveCode reviveCode = new ReviveCode(
                    rs.getString("code"),
                    UUID.fromString(rs.getString("owner_uuid")),
                    UUID.fromString(rs.getString("bound_player_uuid")),
                    rs.getBoolean("used"),
                    rs.getLong("created_at"),
                    rs.getLong("expiry_time")
                );
                rs.close();
                stmt.close();
                return reviveCode;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("获取复活码失败: " + e.getMessage());
        }
        return null;
    }

    public List<ReviveCode> getPlayerReviveCodes(UUID uuid) {
        List<ReviveCode> codes = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM revive_codes WHERE bound_player_uuid = ? AND used = 0");
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ReviveCode code = new ReviveCode(
                    rs.getString("code"),
                    UUID.fromString(rs.getString("owner_uuid")),
                    UUID.fromString(rs.getString("bound_player_uuid")),
                    rs.getBoolean("used"),
                    rs.getLong("created_at"),
                    rs.getLong("expiry_time")
                );
                codes.add(code);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("获取玩家复活码失败: " + e.getMessage());
        }
        return codes;
    }

    public void deleteReviveCode(String code) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM revive_codes WHERE code = ?");
            stmt.setString(1, code.toUpperCase());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("删除复活码失败: " + e.getMessage());
        }
    }

    public void purgeUsedCodes() {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM revive_codes WHERE used = 1");
            int count = stmt.executeUpdate();
            stmt.close();
            plugin.getLogger().info("已清理 " + count + " 个已使用的复活码");
        } catch (SQLException e) {
            plugin.getLogger().severe("清理已使用复活码失败: " + e.getMessage());
        }
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
