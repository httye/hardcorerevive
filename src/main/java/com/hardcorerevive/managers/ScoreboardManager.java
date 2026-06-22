package com.hardcorerevive.managers;

import com.hardcorerevive.HardcoreRevivePlugin;
import com.hardcorerevive.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardManager {
    private final HardcoreRevivePlugin plugin;

    public ScoreboardManager(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    public void updateAllScoreboards() {
        int aliveCount = 0;
        int deadCount = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
            if (data != null && data.isJailed()) {
                deadCount++;
            } else {
                aliveCount++;
            }
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerScoreboard(player, aliveCount, deadCount);
        }
    }

    private void updatePlayerScoreboard(Player player, int aliveCount, int deadCount) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        
        Objective objective = scoreboard.getObjective("hcrevive");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("hcrevive", "dummy", "§c§lDeath-banned");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        int revivesLeft = 0;
        if (data != null) {
            int maxRevives = plugin.getConfig().getInt("revive_limits.max_revives", 0);
            if (maxRevives > 0) {
                revivesLeft = maxRevives - data.getReviveCount();
            } else {
                revivesLeft = -1; // 无限
            }
        }
        clearScores(objective);
        objective.getScore("§e存活: §a" + aliveCount).setScore(3);
        objective.getScore("§e死亡: §c" + deadCount).setScore(2);
        
        if (data != null && !data.isJailed()) {
            long survivalTime = data.getSurvivalTime() / 1000;
            String timeStr = formatTime(survivalTime);
            objective.getScore("§e存活时间: §f" + timeStr).setScore(1);
        }
    }

    private void clearScores(Objective objective) {
        for (String entry : objective.getScoreboard().getEntries()) {
            objective.getScoreboard().resetScores(entry);
        }
    }

    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}
