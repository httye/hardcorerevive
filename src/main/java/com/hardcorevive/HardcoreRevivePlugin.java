package com.hardcorerevive;

import com.hardcorerevive.commands.*;
import com.hardcorerevive.data.DatabaseManager;
import com.hardcorerevive.listeners.*;
import com.hardcorerevive.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class HardcoreRevivePlugin extends JavaPlugin {

    private static HardcoreRevivePlugin instance;
    private DatabaseManager databaseManager;
    private PlayerDataManager playerDataManager;
    private ReviveCodeManager reviveCodeManager;
    private JailManager jailManager;
    private TrustManager trustManager;
    private BountyManager bountyManager;
    private TombstoneManager tombstoneManager;
    private ScoreboardManager scoreboardManager;
    private InvitationManager invitationManager;

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
        reviveCodeManager = new ReviveCodeManager(this);
        jailManager = new JailManager(this);
        trustManager = new TrustManager(this);
        bountyManager = new BountyManager(this);
        tombstoneManager = new TombstoneManager(this);
        scoreboardManager = new ScoreboardManager(this);
        invitationManager = new InvitationManager(this);
        
        // 注册事件监听器
        registerListeners();
        
        // 注册命令
        registerCommands();
        // 启动定时任务
        startScheduledTasks();
        
        getLogger().info("HardcoreRevive 插件已启用！");
    }

    @Override
    public void onDisable() {
        // 停止所有任务
        getServer().getScheduler().cancelTasks(this);
        
        // 关闭数据库
        if (databaseManager != null) {
            databaseManager.close();
        }
        
        getLogger().info("HardcoreRevive 插件已禁用！");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new JailProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new TombstoneProtectionListener(this), this);
    }

    private void registerCommands() {
        getCommand("revive").setExecutor(new ReviveCommand(this));
        getCommand("sos").setExecutor(new SosCommand(this));
        getCommand("trust").setExecutor(new TrustCommand(this));
        getCommand("setjail").setExecutor(new SetJailCommand(this));
        getCommand("reviveadmin").setExecutor(new ReviveAdminCommand(this));
        getCommand("bounty").setExecutor(new BountyCommand(this));
        getCommand("hcreload").setExecutor(new ReloadCommand(this));
    }

    private void startScheduledTasks() {
        // 越狱检测任务
        long escapeCheckInterval = getConfig().getLong("jail.escape_check_interval", 10) * 20L;
        getServer().getScheduler().runTaskTimer(this, 
            () -> jailManager.checkEscapedPlayers(), 
            escapeCheckInterval, 
            escapeCheckInterval);
        
        // 定时发放复活码任务
        long autoGrantInterval = getConfig().getLong("revive_code.auto_grant_interval", 60) * 60* 20L;
        getServer().getScheduler().runTaskTimer(this, 
            () -> reviveCodeManager.autoGrantCodes(), 
            autoGrantInterval);
        
        // 计分板更新任务
        if (getConfig().getBoolean("scoreboard.enabled", true)) {
            long scoreboardUpdateInterval = getConfig().getLong("scoreboard.update_interval", 5) * 20L;
            getServer().getScheduler().runTaskTimer(this, 
                () -> scoreboardManager.updateAllScoreboards(), 
                scoreboardUpdateInterval, 
                scoreboardUpdateInterval);
        }
        // 邀请追踪任务（每分钟检查一次）
        getServer().getScheduler().runTaskTimer(this, 
            () -> invitationManager.checkInvitations(), 
            1200L, 
            1200L);
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

    public ReviveCodeManager getReviveCodeManager() {
        return reviveCodeManager;
    }

    public JailManager getJailManager() {
        return jailManager;
    }

    public TrustManager getTrustManager() {
        return trustManager;
    }

    public BountyManager getBountyManager() {
        return bountyManager;
    }

    public TombstoneManager getTombstoneManager() {
        return tombstoneManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public InvitationManager getInvitationManager() {
        return invitationManager;
    }
}
