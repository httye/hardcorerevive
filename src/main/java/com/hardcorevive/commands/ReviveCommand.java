package com.hardcorerevive.commands;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.PlayerData;
import com.hardcorerevive.models.ReviveCode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ReviveCommand implements CommandExecutor {
    private final HardcoreRevivePlugin plugin;

    public ReviveCommand(HardcoreRevivePlugin plugin) {
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
            sender.sendMessage("§c用法: /revive <复活码> [玩家名]");
            sender.sendMessage("§c或: /revive send<玩家名> <复活码>");
            sender.sendMessage("§c或: /revive list");
            return true;
        }

        if (args[0].equalsIgnoreCase("send")) {
            return handleSend(player, args);
        } else if (args[0].equalsIgnoreCase("list")) {
            return handleList(player);
        } else {
            return handleRevive(player, args);
        }
    }

    private boolean handleRevive(Player player, String[] args) {
        String code = args[0].toUpperCase();
        Player target = player;
        
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage("§c玩家不在线！");
                return true;
            }
            if (!plugin.getTrustManager().isTrusted(target.getUniqueId(), player.getUniqueId())) {
                player.sendMessage("§c你不在该玩家的信任列表中！");
                return true;
            }
        }

        PlayerData targetData = plugin.getPlayerDataManager().getPlayerData(target.getUniqueId());
        if (targetData == null || !targetData.isJailed()) {
            player.sendMessage("§c该玩家未死亡！");
            return true;
        }

        long cooldown = plugin.getConfig().getLong("revive_limits.cooldown_minutes", 30) * 60000;
        if (System.currentTimeMillis() - targetData.getLastReviveTime() < cooldown) {
            long remaining = (cooldown - (System.currentTimeMillis() - targetData.getLastReviveTime())) / 1000;
            player.sendMessage("§c复活冷却中，剩余 " + remaining + " 秒！");
            return true;
        }

        int maxRevives = plugin.getConfig().getInt("revive_limits.max_revives", 0);
        if (maxRevives > 0 && targetData.getReviveCount() >= maxRevives) {
            player.sendMessage("§c该玩家已达到复活次数上限！");
            return true;
        }

        boolean progressive = plugin.getConfig().getBoolean("revive_limits.progressive_cost", true);
        int requiredCodes = progressive ? (targetData.getReviveCount() + 1) : 1;
        
        List<ReviveCode> codes = plugin.getReviveCodeManager().getPlayerCodes(player.getUniqueId());
        codes = codes.stream().filter(ReviveCode::isValid).limit(requiredCodes).collect(java.util.stream.Collectors.toList());
        
        if (codes.size() < requiredCodes) {
            player.sendMessage("§c复活码不足！需要 " + requiredCodes + " 个，你只有 " + codes.size() + " 个。");
            return true;
        }

        for (int i = 0; i < requiredCodes; i++) {
            plugin.getReviveCodeManager().useCode(codes.get(i).getCode(), player.getUniqueId());
        }

        targetData.incrementReviveCount();
        targetData.setLastReviveTime(System.currentTimeMillis());
        int expPenalty = plugin.getConfig().getInt("revive_limits.exp_penalty_percent", 20);
        int levelLoss = (int) (targetData.getDeathLevel() * (expPenalty / 100.0));
        target.setLevel(Math.max(0, targetData.getDeathLevel() - levelLoss));
        
        plugin.getJailManager().releasePlayer(target);
        plugin.getTombstoneManager().removeTombstone(target.getUniqueId());
        Bukkit.broadcastMessage("§a" + target.getName() + " 已复活！");
        
        if (plugin.getConfig().getBoolean("announcements.revive_title_enabled", true)) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle("§a✦", "§2" + target.getName() + " 复活了", 10, 40, 10);
            }
        }
        
        return true;
    }

    private boolean handleSend(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c用法: /revive send <玩家名> <复活码>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§c玩家不在线！");
            return true;
        }

        String code = args[2].toUpperCase();
        if (plugin.getReviveCodeManager().transferCode(code, player.getUniqueId(), target.getUniqueId())) {
            player.sendMessage("§a已将复活码 " + code + " 转赠给 " + target.getName());
            target.sendMessage("§a" + player.getName() + " 转赠了复活码 " + code + " 给你！");
        } else {
            player.sendMessage("§c转赠失败！请检查复活码是否有效且属于你。");
        }

        return true;
    }

    private boolean handleList(Player player) {
        List<ReviveCode> codes = plugin.getReviveCodeManager().getPlayerCodes(player.getUniqueId());
        codes = codes.stream().filter(ReviveCode::isValid).collect(java.util.stream.Collectors.toList());
        
        if (codes.isEmpty()) {
            player.sendMessage("§e你没有可用的复活码。");
        } else {
            player.sendMessage("§e你的复活码列表:");
            for (ReviveCode code : codes) {
                player.sendMessage("§f- " + code.getCode());
            }
        }
        return true;
    }
}
