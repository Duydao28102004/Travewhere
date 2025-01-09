package com.example.travewhere.models;

public class MemberStatus {
    private final String title;
    private final int iconResId;

    public MemberStatus(String title, int iconResId) {
        this.title = title;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }
}
