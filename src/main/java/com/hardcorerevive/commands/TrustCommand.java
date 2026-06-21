package com.hardcorerevive.commands;

import com.hardcorerevive.HardcoreRevivePlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TrustCommand implements CommandExecutor {
    private final HardcoreRevivePlugin plugin;

    public TrustCommand(HardcoreRevivePlugin plugin) {
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
            sender.sendMessage("§c用法: /trust <add|remove|list> [玩家名]");
            return true;
        }

        String subCmd = args[0].toLowerCase();

        switch (subCmd) {
            case "add":
                return handleAdd(player, args);
            case "remove":
                return handleRemove(player, args);
            case "list":
                return handleList(player);
            default:
                player.sendMessage("§c未知子命令！用法: /trust <add|remove|list>");
                return true;
        }
    }

    private boolean handleAdd(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c用法: /trust add <玩家名>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§c玩家不在线！");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§c你不能信任自己！");
            return true;
        }

        if (plugin.getTrustManager().addTrust(player.getUniqueId(), target.getUniqueId())) {
            player.sendMessage("§a已将 " + target.getName() + " 添加到信任列表！");
            target.sendMessage("§a" + player.getName() + " 将你添加到了信任列表！");
        } else {
            player.sendMessage("§c添加失败！可能已达到信任列表上限或该玩家已在列表中。");
        }

        return true;
    }

    private boolean handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c用法: /trust remove <玩家名>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        UUID targetUuid;
        String targetName;
        
        if (target != null) {
            targetUuid = target.getUniqueId();
            targetName = target.getName();
        } else {
            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[1]);
            targetUuid = offlineTarget.getUniqueId();
            targetName = args[1];
        }

        plugin.getTrustManager().removeTrust(player.getUniqueId(), targetUuid);
        player.sendMessage("§a已将 " + targetName + " 从信任列表移除！");

        return true;
    }

    private boolean handleList(Player player) {
        List<UUID> trustList = plugin.getTrustManager().getTrustList(player.getUniqueId());
        if (trustList.isEmpty()) {
            player.sendMessage("§e你的信任列表为空。");
        } else {
            player.sendMessage("§e你的信任列表:");
            for (UUID uuid : trustList) {
                OfflinePlayer trusted = Bukkit.getOfflinePlayer(uuid);
                String status = trusted.isOnline() ? "§a在线" : "§7离线";
                player.sendMessage("§f- " + trusted.getName() + " " + status);
            }
        }

        return true;
    }
}
