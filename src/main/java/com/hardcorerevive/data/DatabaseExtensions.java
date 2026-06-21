package com.hardcorerevive.data;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.Bounty;
import org.bukkit.Location;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.*;

public class DatabaseExtensions {
    private final HardcoreRevivePlugin plugin;
    private final Connection connection;

    public DatabaseExtensions(HardcoreRevivePlugin plugin, Connection connection) {
        this.plugin = plugin;
        this.connection = connection;
    }

    // Trust list operations
    public void addTrust(UUID playerUuid, UUID trustedUuid) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR IGNORE INTO trust_list (player_uuid, trusted_uuid) VALUES (?, ?)");
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, trustedUuid.toString());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("添加信任失败: " + e.getMessage());
        }
    }

    public void removeTrust(UUID playerUuid, UUID trustedUuid) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM trust_list WHERE player_uuid = ? AND trusted_uuid = ?");
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, trustedUuid.toString());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("移除信任失败: " + e.getMessage());
        }
    }

    public List<UUID> getTrustList(UUID playerUuid) {
        List<UUID> trustList = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT trusted_uuid FROM trust_list WHERE player_uuid = ?");
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                trustList.add(UUID.fromString(rs.getString("trusted_uuid")));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("获取信任列表失败: " + e.getMessage());
        }
        return trustList;
    }

    // Jail location operations
    public void saveJailLocation(String worldName, Location location) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "REPLACE INTO jail_locations (world_name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setString(1, worldName);
            stmt.setDouble(2, location.getX());
            stmt.setDouble(3, location.getY());
            stmt.setDouble(4, location.getZ());
            stmt.setFloat(5, location.getYaw());
            stmt.setFloat(6, location.getPitch());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("保存小黑屋坐标失败: " + e.getMessage());
        }
    }

    public Location getJailLocation(String worldName) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM jail_locations WHERE world_name = ?");
            stmt.setString(1, worldName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && Bukkit.getWorld(worldName) != null) {
                Location loc = new Location(
                    Bukkit.getWorld(worldName),
                    rs.getDouble("x"),
                    rs.getDouble("y"),
                    rs.getDouble("z"),
                    rs.getFloat("yaw"),
                    rs.getFloat("pitch")
                );
                rs.close();
                stmt.close();
                return loc;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("获取小黑屋坐标失败: " + e.getMessage());
        }
        return null;
    }

    // Bounty operations
    public int createBounty(UUID creatorUuid, String description, String rewardCode) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO bounties (creator_uuid, description, reward_code, created_at) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, creatorUuid.toString());
            stmt.setString(2, description);
            stmt.setString(3, rewardCode);
            stmt.setLong(4, System.currentTimeMillis());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                rs.close();
                stmt.close();
                return id;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("创建悬赏失败: " + e.getMessage());
        }
        return -1;
    }

    public Bounty getBounty(int id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM bounties WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                UUID claimerUuid = null;
                String claimerStr = rs.getString("claimer_uuid");
                if (claimerStr != null) {
                    claimerUuid = UUID.fromString(claimerStr);
                }
                Bounty bounty = new Bounty(
                    rs.getInt("id"),
                    UUID.fromString(rs.getString("creator_uuid")),
                    rs.getString("description"),
                rs.getString("reward_code"),
                    rs.getBoolean("claimed"),
                    claimerUuid,
                    rs.getLong("created_at"),
                    rs.getLong("claimed_at")
                );
                rs.close();
                stmt.close();
                return bounty;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("获取悬赏失败: " + e.getMessage());
        }
        return null;
    }

    public List<Bounty> getAllBounties() {
        List<Bounty> bounties = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM bounties WHERE claimed = 0");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Bounty bounty = new Bounty(
                    rs.getInt("id"),
                    UUID.fromString(rs.getString("creator_uuid")),
                    rs.getString("description"),
                    rs.getString("reward_code")
                );
                bounties.add(bounty);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("获取所有悬赏失败: " + e.getMessage());
        }
        return bounties;
    }

    public void updateBounty(Bounty bounty) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE bounties SET claimed = ?, claimer_uuid = ?, claimed_at = ? WHERE id = ?");
            stmt.setBoolean(1, bounty.isClaimed());
            stmt.setString(2, bounty.getClaimerUuid() != null ? bounty.getClaimerUuid().toString() : null);
            stmt.setLong(3, bounty.getClaimedAt());
            stmt.setInt(4, bounty.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("更新悬赏失败: " + e.getMessage());
        }
    }

    // Invitation operations
    public void saveInvitation(UUID inviterUuid, UUID invitedUuid, String invitedName, long joinTime) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "REPLACE INTO invitations (inviter_uuid, invited_uuid, invited_name, join_time, rewarded) VALUES (?, ?, ?, 0)");
            stmt.setString(1, inviterUuid.toString());
            stmt.setString(2, invitedUuid.toString());
            stmt.setString(3, invitedName);
            stmt.setLong(4, joinTime);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("保存邀请记录失败: " + e.getMessage());
        }
    }

    public UUID getInviter(UUID invitedUuid) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT inviter_uuid FROM invitations WHERE invited_uuid = ?");
            stmt.setString(1, invitedUuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                UUID inviterUuid = UUID.fromString(rs.getString("inviter_uuid"));
                rs.close();
                stmt.close();
                return inviterUuid;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("获取邀请者失败: " + e.getMessage());
        }
        return null;
    }

    public Map<UUID, Long> getPendingInvitations() {
        Map<UUID, Long> invitations = new HashMap<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT invited_uuid, join_time FROM invitations WHERE rewarded = 0");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                invitations.put(
                    UUID.fromString(rs.getString("invited_uuid")),
                    rs.getLong("join_time")
                );
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("获取待处理邀请失败: " + e.getMessage());
        }
        return invitations;
    }

    public void markInvitationRewarded(UUID invitedUuid) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE invitations SET rewarded = 1 WHERE invited_uuid = ?");
            stmt.setString(1, invitedUuid.toString());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("标记邀请已奖励失败: " + e.getMessage());
        }
    }
}
