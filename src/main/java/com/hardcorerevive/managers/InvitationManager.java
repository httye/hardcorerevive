package com.hardcorerevive.managers;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.data.DatabaseExtensions;
import com.hardcorerevive.models.ReviveCode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InvitationManager {
    private final HardcoreRevivePlugin plugin;
    private DatabaseExtensions dbExt;

    public InvitationManager(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
        try {
            Field connField = plugin.getDatabaseManager().getClass().getDeclaredField("connection");
            connField.setAccessible(true);
            this.dbExt = new DatabaseExtensions(plugin, (java.sql.Connection) connField.get(plugin.getDatabaseManager()));
        } catch (Exception e) {
            plugin.getLogger().severe("初始化 InvitationManager 失败: " + e.getMessage());
        }
    }

    public void recordInvitation(UUID inviterUuid, UUID invitedUuid, String invitedName) {
        dbExt.saveInvitation(inviterUuid, invitedUuid, invitedName, System.currentTimeMillis());
    }

    public void checkInvitations() {
        long requiredPlaytime = plugin.getConfig().getLong("invitation.required_playtime", 30) * 60 * 1000;
        int rewardAmount = plugin.getConfig().getInt("invitation.reward_amount", 2);
        
        Map<UUID, Long> pending = dbExt.getPendingInvitations();
        
        for (Map.Entry<UUID, Long> entry : pending.entrySet()) {
            UUID invitedUuid = entry.getKey();
            long joinTime = entry.getValue();
            Player invited = Bukkit.getPlayer(invitedUuid);
            if (invited != null && invited.isOnline()) {
                long playTime = System.currentTimeMillis() - joinTime;
                if (playTime >= requiredPlaytime) {
                    UUID inviterUuid = dbExt.getInviter(invitedUuid);
                    if (inviterUuid != null) {
                        List<ReviveCode> codes = plugin.getReviveCodeManager().generateCodes(inviterUuid, rewardAmount);
                        
                Player inviter = Bukkit.getPlayer(inviterUuid);
                        if (inviter != null && inviter.isOnline()) {
                            inviter.sendMessage("§a你邀请的玩家 " + invited.getName() + " 已达到游玩时间，奖励 " + rewardAmount + " 个复活码！");
                for (ReviveCode code : codes) {
                                inviter.sendMessage("§e复活码: §f" + code.getCode());
                            }
                        }
                        dbExt.markInvitationRewarded(invitedUuid);
                    }
                }
            }
        }
    }
}
