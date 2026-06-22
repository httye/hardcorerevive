package com.hardcorerevive;

import com.hardcorerevive.data.DatabaseManager;
import com.hardcorerevive.listeners.PlayerDeathListener;
import com.hardcorerevive.listeners.PlayerJoinListener;
import com.hardcorerevive.listeners.PlayerQuitListener;
import com.hardcorerevive.listeners.TombstoneProtectionListener;
import com.hardcorerevive.managers.PlayerDataManager;
import com.hardcorerevive.managers.ScoreboardManager;
import com.hardcorerevive.managers.TombstoneManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HardcoreRevivePlugin extends JavaPlugin {

    private static HardcoreRevivePlugin instance;
    private DatabaseManager databaseManager;
    private PlayerDataManager playerDataManager;
    private TombstoneManager tombstoneManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // 保存默认配置
        saveDefaultConfig();
        
        // 初始化数据库
        databaseManager = new DatabaseManager(this);
        if (!databaseManager.initialize()) {
            getLogger().severe("数据库初始化失败，插件禁用！");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // 初始化管理器
        playerDataManager = new PlayerDataManager(this);
        tombstoneManager = new TombstoneManager(this);
        scoreboardManager = new ScoreboardManager(this);
        
        // 注册事件监听器
        registerListeners();
        
        // 启动计分板更新任务
        startScheduledTasks();
        
        getLogger().info("Death-banned 插件已启用！");
    }

    @Override
    public void onDisable() {
        // 停止所有任务
        getServer().getScheduler().cancelTasks(this);
        
        // 关闭数据库
        if (databaseManager != null) {
            databaseManager.close();
        }
        
        getLogger().info("Death-banned 插件已禁用！");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new TombstoneProtectionListener(this), this);
    }

    private void startScheduledTasks() {
        // 计分板更新任务
        if (getConfig().getBoolean("scoreboard.enabled", true)) {
            long updateInterval = getConfig().getLong("scoreboard.update_interval", 5) * 20L;
            getServer().getScheduler().runTaskTimer(this, 
                () -> scoreboardManager.updateAllScoreboards(), 
                updateInterval, 
                updateInterval);
        }
    }

    public void reloadPlugin() {
        reloadConfig();
        getLogger().info("配置文件已重载！");
    }

    // Getters
    public static HardcoreRevivePlugin getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public TombstoneManager getTombstoneManager() {
        return tombstoneManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
}
