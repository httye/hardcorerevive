package com.hardcorerevive.managers;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.ReviveCode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

public class ReviveCodeManager {
    private final HardcoreRevivePlugin plugin;
    private final SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public ReviveCodeManager(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    public ReviveCode generateCode(UUID ownerUuid) {
        int length = plugin.getConfig().getInt("revive_code.code_length", 8);
        String code;
        
        do {
            code = generateRandomCode(length);
        } while (plugin.getDatabaseManager().getReviveCode(code) != null);
        
        ReviveCode reviveCode = new ReviveCode(code, ownerUuid);
        long expiryHours = plugin.getConfig().getLong("revive_code.expiry_hours", 168);
        if (expiryHours > 0) {
            long expiryTime = System.currentTimeMillis() + (expiryHours * 3600000L);
            reviveCode.setExpiryTime(expiryTime);
        }
        plugin.getDatabaseManager().saveReviveCode(reviveCode);
        return reviveCode;
    }

    public List<ReviveCode> generateCodes(UUID ownerUuid, int count) {
        List<ReviveCode> codes = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            codes.add(generateCode(ownerUuid));
        }
        return codes;
    }

    private String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));
        }
        return code.toString();
    }

    public ReviveCode getCode(String code) {
        return plugin.getDatabaseManager().getReviveCode(code);
    }

    public List<ReviveCode> getPlayerCodes(UUID uuid) {
        return plugin.getDatabaseManager().getPlayerReviveCodes(uuid);
    }

    public boolean useCode(String code, UUID userUuid) {
        ReviveCode reviveCode = getCode(code);
        if (reviveCode == null) {
            return false;
        }
        
        if (!reviveCode.isValid()) {
            return false;
        }
        
        if (!reviveCode.getBoundPlayerUuid().equals(userUuid)) {
            return false;
        }
        
        reviveCode.setUsed(true);
        plugin.getDatabaseManager().saveReviveCode(reviveCode);
        return true;
    }

    public boolean transferCode(String code, UUID fromUuid, UUID toUuid) {
        ReviveCode reviveCode = getCode(code);
        if (reviveCode == null || !reviveCode.isValid()) {
            return false;
        }
        
        if (!reviveCode.getBoundPlayerUuid().equals(fromUuid)) {
            return false;
        }
        
        reviveCode.setBoundPlayerUuid(toUuid);
        plugin.getDatabaseManager().saveReviveCode(reviveCode);
        return true;
    }

    public void autoGrantCodes() {
        boolean aliveOnly = plugin.getConfig().getBoolean("revive_code.auto_grant_alive_only", true);
        int amount = plugin.getConfig().getInt("revive_code.auto_grant_amount", 1);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (aliveOnly) {
                var playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
                if (playerData != null && playerData.isJailed()) {
                    continue;
                }
            }
            List<ReviveCode> codes = generateCodes(player.getUniqueId(), amount);
            player.sendMessage("§a你获得了 " + amount + " 个复活码！");
            for (ReviveCode code : codes) {
                player.sendMessage("§e复活码: §f" + code.getCode());
            }
        }
    }
}
