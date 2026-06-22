package com.hardcorerevive.models;

import org.bukkit.Location;

import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private final String playerName;
    private int deathCount;
    private int deathLevel;
    private Location deathLocation;
    private String deathWorld;

    public PlayerData(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.deathCount = 0;
        this.deathLevel = 0;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public int getDeathLevel() {
        return deathLevel;
    }

    public void setDeathLevel(int deathLevel) {
        this.deathLevel = deathLevel;
    }

    public Location getDeathLocation() {
        return deathLocation;
    }

    public void setDeathLocation(Location deathLocation) {
        this.deathLocation = deathLocation;
        if (deathLocation != null && deathLocation.getWorld() != null) {
            this.deathWorld = deathLocation.getWorld().getName();
        }
    }

    public String getDeathWorld() {
        return deathWorld;
    }

    public void setDeathWorld(String deathWorld) {
        this.deathWorld = deathWorld;
    }
}