package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public record Bazaar(long lastUpdated, Map<String, Product> products)
{
    public record Product(@SerializedName("quick_status") Status quickStatus, @SerializedName("buy_summary") Summary[] buySummary, @SerializedName("sell_summary") Summary[] sellSummary) {}

    public record Status(double buyPrice, double sellPrice) {}

    public record Summary(double pricePerUnit) {}

    public record Data(long lastUpdated, Status status) {}
}