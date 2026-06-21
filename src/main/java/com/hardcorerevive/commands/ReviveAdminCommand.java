package com.hardcorerevive.commands;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.PlayerData;
import com.hardcorerevive.models.ReviveCode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ReviveAdminCommand implements CommandExecutor {
    private final HardcoreRevivePlugin plugin;

    public ReviveAdminCommand(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("hardcorerevive.admin")) {
            sender.sendMessage("§c你没有权限执行此命令！");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§c用法: /reviveadmin <generate|list|guiveall|reviveall|purgecodes|forceRevive>");
            return true;
        }

        String subCmd = args[0].toLowerCase();

        switch (subCmd) {
            case "generate":
                return handleGenerate(sender, args);
            case "list":
                return handleList(sender, args);
            case "gui":
                return handleGui(sender);
            case "giveall":
                return handleGiveAll(sender, args);
            case "reviveall":
                return handleReviveAll(sender);
            case "purgecodes":
                return handlePurgeCodes(sender);
            case "forcerevive":
                return handleForceRevive(sender, args);
            default:
                sender.sendMessage("§c未知子命令！");
                return true;
        }
    }

    private boolean handleGenerate(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c用法: /reviveadmin generate <玩家> [数量]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c玩家不在线！");
            return true;
        }

        int amount = 1;
        if (args.length > 2) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§c无效的数量！");
                return true;
            }
        }

        List<ReviveCode> codes = plugin.getReviveCodeManager().generateCodes(target.getUniqueId(), amount);
        sender.sendMessage("§a已为 " + target.getName() + " 生成 " + amount + " 个复活码:");
        for (ReviveCode code : codes) {
            sender.sendMessage("§e- " + code.getCode());
        }
        target.sendMessage("§a管理员为你生成了 " + amount + " 个复活码！");

        return true;
    }

    private boolean handleList(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c用法: /reviveadmin list <玩家>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        List<ReviveCode> codes = plugin.getReviveCodeManager().getPlayerCodes(target.getUniqueId());

        sender.sendMessage("§e" + target.getName() + " 的复活码:");
        if (codes.isEmpty()) {
            sender.sendMessage("§7无");
        } else {
            for (ReviveCode code : codes) {
                String status = code.isValid() ? "§a有效" : (code.isUsed() ? "§c已使用" : "§e已过期");
                sender.sendMessage("§f- " + code.getCode() + " " + status);
            }
        }

        return true;
    }

    private boolean handleGui(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c该命令只能由玩家执行！");
            return true;
        }

        Player player = (Player) sender;
        List<PlayerData> jailedPlayers = plugin.getDatabaseManager().getAllJailedPlayers();

        Inventory gui = Bukkit.createInventory(null, 54, "§6§l死亡玩家管理");

        for (int i = 0; i < Math.min(jailedPlayers.size(), 54); i++) {
            PlayerData data = jailedPlayers.get(i);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(data.getUuid());
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(offlinePlayer);
            meta.setDisplayName("§e" + data.getPlayerName());
            List<String> lore = new ArrayList<>();
            lore.add("§7死亡次数: §f" + data.getDeathCount());
            lore.add("§7复活次数: §f" + data.getReviveCount());
            lore.add("§a左键复活");
            lore.add("§e右键发放复活码");
            meta.setLore(lore);
            skull.setItemMeta(meta);
            gui.setItem(i, skull);
        }

        player.openInventory(gui);
        return true;
    }

    private boolean handleGiveAll(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c用法: /reviveadmin giveall <数量>");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§c无效的数量！");
            return true;
        }

        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getReviveCodeManager().generateCodes(player.getUniqueId(), amount);
            player.sendMessage("§a管理员给所有玩家发放了 " + amount + " 个复活码！");
            count++;
        }

        sender.sendMessage("§a已为 " + count + " 名在线玩家各发放 " + amount + " 个复活码！");
        return true;
    }

    private boolean handleReviveAll(CommandSender sender) {
        List<PlayerData> jailedPlayers = plugin.getDatabaseManager().getAllJailedPlayers();
        
        int count = 0;
        for (PlayerData data : jailedPlayers) {
            Player player = Bukkit.getPlayer(data.getUuid());
            if (player != null && player.isOnline()) {
                plugin.getJailManager().releasePlayer(player);
                plugin.getTombstoneManager().removeTombstone(player.getUniqueId());
                count++;
            }
        }

        sender.sendMessage("§a已复活 " + count + " 名玩家！");
        Bukkit.broadcastMessage("§a管理员复活了所有死亡玩家！");

        return true;
    }

    private boolean handlePurgeCodes(CommandSender sender) {
        plugin.getDatabaseManager().purgeUsedCodes();
        sender.sendMessage("§a已清理所有已使用的复活码！");
        return true;
    }

    private boolean handleForceRevive(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c用法: /reviveadmin forceRevive <玩家名>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c玩家不在线！");
            return true;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target.getUniqueId());
        if (data == null || !data.isJailed()) {
            sender.sendMessage("§c该玩家未死亡！");
            return true;
        }

        plugin.getJailManager().releasePlayer(target);
        plugin.getTombstoneManager().removeTombstone(target.getUniqueId());
        sender.sendMessage("§a已强制复活 " + target.getName() + "！");
        target.sendMessage("§a管理员强制复活了你！");

        return true;
    }
}
