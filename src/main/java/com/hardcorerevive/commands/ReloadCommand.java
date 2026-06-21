package com.hardcorerevive.commands;

import com.hardcorerevive.HardcoreRevivePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final HardcoreRevivePlugin plugin;

    public ReloadCommand(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("hardcorerevive.admin")) {
            sender.sendMessage("§c你没有权限执行此命令！");
            return true;
        }

        plugin.reloadPlugin();
        sender.sendMessage("§a配置文件已重载！");

        return true;
    }
}
