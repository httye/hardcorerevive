package com.hardcorerevive.models;

import java.util.UUID;

public class ReviveCode {
    private String code;
    private UUID ownerUuid;
    private UUID boundPlayerUuid;
    private boolean used;
    private long createdAt;
    private long expiryTime;
    public ReviveCode(String code, UUID ownerUuid) {
        this.code = code.toUpperCase();
        this.ownerUuid = ownerUuid;
        this.boundPlayerUuid = ownerUuid;
        this.used = false;
        this.createdAt = System.currentTimeMillis();
        this.expiryTime = 0;
    }
    
    public ReviveCode(String code, UUID ownerUuid, UUID boundPlayerUuid, boolean used, long createdAt, long expiryTime) {
        this.code = code.toUpperCase();
        this.ownerUuid = ownerUuid;
        this.boundPlayerUuid = boundPlayerUuid;
        this.used = used;
        this.createdAt = createdAt;
        this.expiryTime = expiryTime;
    }
    
    public boolean isExpired() {
        if (expiryTime == 0) {
            return false;
        }
        return System.currentTimeMillis() > expiryTime;
    }
    
    public boolean isValid() {
        return !used && !isExpired();
    }
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public UUID getOwnerUuid() {
        return ownerUuid;
    }
    
    public UUID getBoundPlayerUuid() {
        return boundPlayerUuid;
    }
    
    public void setBoundPlayerUuid(UUID boundPlayerUuid) {
        this.boundPlayerUuid = boundPlayerUuid;
    }
    public boolean isUsed() {
        return used;
    }
    public void setUsed(boolean used) {
        this.used = used;
    }
    public long getCreatedAt() {
        return createdAt;
    }
    
    public long getExpiryTime() {
        return expiryTime;
    }
    
    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }
}
