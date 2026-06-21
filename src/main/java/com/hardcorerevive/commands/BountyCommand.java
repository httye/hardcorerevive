package com.hardcorerevive.commands;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.Bounty;
import com.hardcorerevive.models.ReviveCode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BountyCommand implements CommandExecutor {
    private final HardcoreRevivePlugin plugin;

    public BountyCommand(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c该命令只能由玩家执行！");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage("§c用法: /bounty <create|claim|list> [参数]");
            return true;
        }

        String subCmd = args[0].toLowerCase();

        switch (subCmd) {
            case "create":
                return handleCreate(player, args);
            case "claim":
                return handleClaim(player, args);
            case "list":
                return handleList(player);
            default:
                player.sendMessage("§c未知子命令！用法: /bounty <create|claim|list>");
                return true;
        }
    }

    private boolean handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c用法: /bounty create <描述>");
            return true;
        }

        StringBuilder description = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            description.append(args[i]).append(" ");
        }

        List<ReviveCode> codes = plugin.getReviveCodeManager().getPlayerCodes(player.getUniqueId());
        codes = codes.stream().filter(ReviveCode::isValid).limit(1).collect(java.util.stream.Collectors.toList());
        
        if (codes.isEmpty()) {
            player.sendMessage("§c你没有可用的复活码来创建悬赏！");
            return true;
        }

        ReviveCode code = codes.get(0);
        int bountyId = plugin.getBountyManager().createBounty(player.getUniqueId(), description.toString().trim(), code.getCode());
        
        if (bountyId > 0) {
            code.setUsed(true);
            plugin.getDatabaseManager().saveReviveCode(code);
            player.sendMessage("§a悬赏已创建！悬赏ID: " + bountyId);
            Bukkit.broadcastMessage("§e" + player.getName() + " 发布了新悬赏！使用 /bounty list 查看");
        } else {
            player.sendMessage("§c创建悬赏失败！");
        }

        return true;
    }

    private boolean handleClaim(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c用法: /bounty claim <悬赏ID>");
            return true;
        }

        int bountyId;
        try {
            bountyId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§c无效的悬赏ID！");
            return true;
        }

        Bounty bounty = plugin.getBountyManager().getBounty(bountyId);
        if (bounty == null) {
            player.sendMessage("§c悬赏不存在！");
            return true;
        }

        if (bounty.isClaimed()) {
            player.sendMessage("§c该悬赏已被领取！");
            return true;
        }

        if (bounty.getCreatorUuid().equals(player.getUniqueId())) {
            player.sendMessage("§c你不能领取自己的悬赏！");
            return true;
        }

        if (plugin.getBountyManager().claimBounty(bountyId, player.getUniqueId())) {
            ReviveCode code = plugin.getReviveCodeManager().getCode(bounty.getRewardCode());
            if (code != null) {
                code.setBoundPlayerUuid(player.getUniqueId());
                plugin.getDatabaseManager().saveReviveCode(code);
            }
            player.sendMessage("§a你领取了悬赏并获得复活码: " + bounty.getRewardCode());
            Player creator = Bukkit.getPlayer(bounty.getCreatorUuid());
            if (creator != null && creator.isOnline()) {
                creator.sendMessage("§a" + player.getName() + " 领取了你的悬赏！");
            }
        } else {
            player.sendMessage("§c领取悬赏失败！");
        }

        return true;
    }

    private boolean handleList(Player player) {
        List<Bounty> bounties = plugin.getBountyManager().getAllBounties();
        
        if (bounties.isEmpty()) {
            player.sendMessage("§e当前没有可用的悬赏。");
        } else {
            player.sendMessage("§e可用悬赏列表:");
            for (Bounty bounty : bounties) {
                OfflinePlayer creator = Bukkit.getOfflinePlayer(bounty.getCreatorUuid());
                player.sendMessage("§f[" + bounty.getId() + "] " + bounty.getDescription() + " §7(发布者: " + creator.getName() + ")");
            }
            player.sendMessage("§e使用 /bounty claim <ID> 来领取悬赏");
        }

        return true;
    }
}
