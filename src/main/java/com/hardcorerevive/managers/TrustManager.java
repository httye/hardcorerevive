package com.hardcorerevive.managers;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.data.DatabaseExtensions;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class TrustManager {
    private final HardcoreRevivePlugin plugin;
    private DatabaseExtensions dbExt;

    public TrustManager(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
        try {
            Field connField = plugin.getDatabaseManager().getClass().getDeclaredField("connection");
            connField.setAccessible(true);
            this.dbExt = new DatabaseExtensions(plugin, (java.sql.Connection) connField.get(plugin.getDatabaseManager()));
        } catch (Exception e) {
            plugin.getLogger().severe("初始化 TrustManager 失败: " + e.getMessage());
        }
    }

    public boolean addTrust(UUID playerUuid, UUID trustedUuid) {
        List<UUID> trustList = getTrustList(playerUuid);
        int maxTrust = plugin.getConfig().getInt("social.max_trust_list", 10);
        
        if (trustList.size() >= maxTrust) {
            return false;
        }
        
        if (trustList.contains(trustedUuid)) {
            return false;
        }
        
        dbExt.addTrust(playerUuid, trustedUuid);
        return true;
    }

    public void removeTrust(UUID playerUuid, UUID trustedUuid) {
        dbExt.removeTrust(playerUuid, trustedUuid);
    }

    public List<UUID> getTrustList(UUID playerUuid) {
        return dbExt.getTrustList(playerUuid);
    }

    public boolean isTrusted(UUID playerUuid, UUID trustedUuid) {
        return getTrustList(playerUuid).contains(trustedUuid);
    }
}
