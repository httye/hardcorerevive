package com.hardcorerevive.managers;

import com.hardcorerevive.HardcoreRevivePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardManager {
    private final HardcoreRevivePlugin plugin;

    public ScoreboardManager(HardcoreRevivePlugin plugin) {
        this.plugin = plugin;
    }

    public void updateAllScoreboards() {
        int totalDeaths = plugin.getDatabaseManager().getTotalDeathCount();
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerScoreboard(player, totalDeaths, onlinePlayers);
        }
    }

    private void updatePlayerScoreboard(Player player, int totalDeaths, int onlinePlayers) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        
        Objective objective = scoreboard.getObjective("deathbanned");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("deathbanned", "dummy", "§c§lDeath-banned");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        
        clearScores(objective);
        objective.getScore("§e在线玩家: §a" + onlinePlayers).setScore(3);
        objective.getScore("§e总死亡数: §c" + totalDeaths).setScore(2);
        objective.getScore("§r").setScore(1);
    }

    private void clearScores(Objective objective) {
        for (String entry : objective.getScoreboard().getEntries()) {
            objective.getScoreboard().resetScores(entry);
        }
    }
}