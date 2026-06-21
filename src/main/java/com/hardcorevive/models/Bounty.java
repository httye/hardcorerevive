package com.hardcorerevive.models;

import java.util.UUID;

public class Bounty {
    private int id;
    private UUID creatorUuid;
    private String description;
    private String rewardCode;
    private boolean claimed;
    private UUID claimerUuid;
    private long createdAt;
    private long claimedAt;
    public Bounty(int id, UUID creatorUuid, String description, String rewardCode) {
        this.id = id;
        this.creatorUuid = creatorUuid;
        this.description = description;
        this.rewardCode = rewardCode;
        this.claimed = false;
        this.createdAt = System.currentTimeMillis();
    }
    
    public Bounty(int id, UUID creatorUuid, String description, String rewardCode, 
                  boolean claimed, UUID claimerUuid, long createdAt, long claimedAt) {
        this.id = id;
        this.creatorUuid = creatorUuid;
        this.description = description;
        this.rewardCode = rewardCode;
        this.claimed = claimed;
        this.claimerUuid = claimerUuid;
        this.createdAt = createdAt;
        this.claimedAt = claimedAt;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    public UUID getCreatorUuid() {
        return creatorUuid;
    }
    public String getDescription() {
        return description;
    }
    
    public String getRewardCode() {
        return rewardCode;
    }
    public boolean isClaimed() {
        return claimed;
    }
    
    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }
    
    public UUID getClaimerUuid() {
        return claimerUuid;
    }
    
    public void setClaimerUuid(UUID claimerUuid) {
        this.claimerUuid = claimerUuid;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public long getClaimedAt() {
        return claimedAt;
    }
    
    public void setClaimedAt(long claimedAt) {
        this.claimedAt = claimedAt;
    }
}
