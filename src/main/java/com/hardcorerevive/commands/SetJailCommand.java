package com.hardcorerevive.commands;

import com.hardcorerevive.HardcoreRevivePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetJailCommand implements CommandExecutor {
    private final HardcoreRevivePlugin plugin;

    public SetJailCommand(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c该命令只能由玩家执行！");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("hardcorevive.admin")) {
            player.sendMessage("§c你没有权限执行此命令！");
            return true;
        }

        plugin.getJailManager().setJailLocation(player.getLocation());
        player.sendMessage("§a已将当前位置设置为 " + player.getWorld().getName() + " 的小黑屋坐标！");

        return true;
    }
}
