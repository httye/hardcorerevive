package com.hardcorerevive.managers;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.data.DatabaseExtensions;
import com.hardcorerevive.models.Bounty;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class BountyManager {
    private final HardcoreRevivePlugin plugin;
    private DatabaseExtensions dbExt;

    public BountyManager(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
        try {
            Field connField = plugin.getDatabaseManager().getClass().getDeclaredField("connection");
            connField.setAccessible(true);
            this.dbExt = new DatabaseExtensions(plugin, (java.sql.Connection) connField.get(plugin.getDatabaseManager()));
        } catch (Exception e) {
            plugin.getLogger().severe("初始化 BountyManager 失败: " + e.getMessage());
        }
    }

    public int createBounty(UUID creatorUuid, String description, String rewardCode) {
        return dbExt.createBounty(creatorUuid, description, rewardCode);
    }

    public Bounty getBounty(int id) {
        return dbExt.getBounty(id);
    }

    public List<Bounty> getAllBounties() {
        return dbExt.getAllBounties();
    }

    public boolean claimBounty(int id, UUID claimerUuid) {
        Bounty bounty = getBounty(id);
        if (bounty == null || bounty.isClaimed()) {
            return false;
        }
        
        bounty.setClaimed(true);
        bounty.setClaimerUuid(claimerUuid);
        bounty.setClaimedAt(System.currentTimeMillis());
        dbExt.updateBounty(bounty);
        return true;
    }
}
