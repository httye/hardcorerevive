package com.hardcorerevive.models;

import org.bukkit.Location;
import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private String playerName;
    private boolean isJailed;
    private int deathCount;
    private int reviveCount;
    private long lastReviveTime;
    private long lastSosTime;
    private Location deathLocation;
    private String deathWorld;
    private int deathLevel;
    private long survivalStartTime;
    public PlayerData(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.isJailed = false;
        this.deathCount = 0;
        this.reviveCount = 0;
        this.lastReviveTime = 0;
        this.lastSosTime = 0;
        this.survivalStartTime = System.currentTimeMillis();
    }
    
    // Geters and Setters
    public UUID getUuid() {
        return uuid;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public boolean isJailed() {
        return isJailed;
    }
    
    public void setJailed(boolean jailed) {
        isJailed = jailed;
    }
    
    public int getDeathCount() {
        return deathCount;
    }
    
    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }
    
    public void incrementDeathCount() {
        this.deathCount++;
    }
    
    public int getReviveCount() {
        return reviveCount;
    }
    
    public void setReviveCount(int reviveCount) {
        this.reviveCount = reviveCount;
    }
    
    public void incrementReviveCount() {
        this.reviveCount++;
    }
    
    public long getLastReviveTime() {
        return lastReviveTime;
    }
    
    public void setLastReviveTime(long lastReviveTime) {
        this.lastReviveTime = lastReviveTime;
    }
    public long getLastSosTime() {
        return lastSosTime;
    }
    public void setLastSosTime(long lastSosTime) {
        this.lastSosTime = lastSosTime;
    }
    
    public Location getDeathLocation() {
        return deathLocation;
    }
    
    public void setDeathLocation(Location deathLocation) {
        this.deathLocation = deathLocation;
    }
    
    public String getDeathWorld() {
        return deathWorld;
    }
    public void setDeathWorld(String deathWorld) {
        this.deathWorld = deathWorld;
    }
    public int getDeathLevel() {
        return deathLevel;
    }
    
    public void setDeathLevel(int deathLevel) {
        this.deathLevel = deathLevel;
    }
    
    public long getSurvivalStartTime() {
        return survivalStartTime;
    }
    
    public void setSurvivalStartTime(long survivalStartTime) {
        this.survivalStartTime = survivalStartTime;
    }
    
    public long getSurvivalTime() {
        if (isJailed) {
            return 0;
        }
        return System.currentTimeMillis() - survivalStartTime;
    }
}
