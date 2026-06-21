package com.hardcorerevive.commands;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SosCommand implements CommandExecutor {
    private final HardcoreRevivePlugin plugin;

    public SosCommand(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c该命令只能由玩家执行！");
            return true;
        }

        Player player = (Player) sender;
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        
        if (data == null || !data.isJailed()) {
            player.sendMessage("§c你必须处于死亡状态才能发送求救信号！");
            return true;
        }

        long cooldown = plugin.getConfig().getLong("social.sos_cooldown", 10) * 60000;
        if (System.currentTimeMillis() - data.getLastSosTime() < cooldown) {
            long remaining = (cooldown - (System.currentTimeMillis() - data.getLastSosTime())) / 6000;
            player.sendMessage("§c求救信号冷却中，剩余 " + remaining + " 分钟！");
            return true;
        }

        data.setLastSosTime(System.currentTimeMillis());
        plugin.getPlayerDataManager().savePlayerData(data);
        
        Bukkit.broadcastMessage("§c§l[SOS] " + player.getName() + " 发出了求救信号！");
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.equals(player)) {
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
                p.sendTitle("§c§lSOS", "§e" + player.getName() + " 需要帮助！", 10, 40, 10);
            }
        }
        
        player.sendMessage("§a求救信号已发送！");
        return true;
    }
}
