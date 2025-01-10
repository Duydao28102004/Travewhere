package com.example.travewhere.models;

public class MemberStatus {
    private final String title;
    private final int iconResId;
    private final String exclusiveDiscountDescription;
    private final String exclusiveBirthdayGiftDescription;
    private final String exclusiveSpecialOfferDescription;

    public MemberStatus(String title, int iconResId, String exclusiveDiscountDescription, String exclusiveBirthdayGiftDescription, String exclusiveSpecialOfferDescription) {
        this.title = title;
        this.iconResId = iconResId;
        this.exclusiveDiscountDescription = exclusiveDiscountDescription;
        this.exclusiveBirthdayGiftDescription = exclusiveBirthdayGiftDescription;
        this.exclusiveSpecialOfferDescription = exclusiveSpecialOfferDescription;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getExclusiveDiscountDescription() {
        return exclusiveDiscountDescription;
    }

    public String getExclusiveBirthdayGiftDescription() {
        return exclusiveBirthdayGiftDescription;
    }

    public String getExclusiveSpecialOfferDescription() {
        return exclusiveSpecialOfferDescription;
    }
}
