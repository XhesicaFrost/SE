package com.blm.takeout.entity;

public enum ReviewStatus {
    PENDING("待审核"),
    APPROVED("已通过"),
    REJECTED("已拒绝");

    private final String displayName;

    ReviewStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}