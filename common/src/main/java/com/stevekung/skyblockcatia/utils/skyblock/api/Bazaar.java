package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Bazaar
{
    private final long lastUpdated;
    private final Map<String, Product> products;

    public Bazaar(long lastUpdated, Map<String, Product> products)
    {
        this.lastUpdated = lastUpdated;
        this.products = products;
    }

    public long getLastUpdated()
    {
        return this.lastUpdated;
    }

    public Map<String, Product> getProducts()
    {
        return this.products;
    }

    public static class Product
    {
        @SerializedName("quick_status")
        private final Status quickStatus;
        @SerializedName("buy_summary")
        private final Summary[] buySummary;
        @SerializedName("sell_summary")
        private final Summary[] sellSummary;

        public Product(Status quickStatus, Summary[] buySummary, Summary[] sellSummary)
        {
            this.quickStatus = quickStatus;
            this.buySummary = buySummary;
            this.sellSummary = sellSummary;
        }

        public Status getQuickStatus()
        {
            return this.quickStatus;
        }

        public Summary[] getBuySummary()
        {
            return this.buySummary;
        }

        public Summary[] getSellSummary()
        {
            return this.sellSummary;
        }
    }

    public static class Status
    {
        private final double buyPrice;
        private final double sellPrice;

        public Status(double buyPrice, double sellPrice)
        {
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
        }

        public double getBuyPrice()
        {
            return this.buyPrice;
        }

        public double getSellPrice()
        {
            return this.sellPrice;
        }
    }

    public static class Summary
    {
        private final double pricePerUnit;

        public Summary(double pricePerUnit)
        {
            this.pricePerUnit = pricePerUnit;
        }

        public double getPricePerUnit()
        {
            return this.pricePerUnit;
        }
    }

    public static class Data
    {
        private final long lastUpdated;
        private final Status status;

        public Data(long lastUpdated, Status status)
        {
            this.lastUpdated = lastUpdated;
            this.status = status;
        }

        public long getLastUpdated()
        {
            return this.lastUpdated;
        }

        public Status getStatus()
        {
            return this.status;
        }
    }
}