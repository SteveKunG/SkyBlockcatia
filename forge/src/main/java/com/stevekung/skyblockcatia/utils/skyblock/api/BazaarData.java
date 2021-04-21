package com.stevekung.skyblockcatia.utils.skyblock.api;

public class BazaarData
{
    private final long lastUpdated;
    private final Product product;

    public BazaarData(long lastUpdated, Product product)
    {
        this.lastUpdated = lastUpdated;
        this.product = product;
    }

    public long getLastUpdated()
    {
        return this.lastUpdated;
    }

    public Product getProduct()
    {
        return this.product;
    }

    public static class Product
    {
        private final double buyPrice;
        private final double sellPrice;

        public Product(double buyPrice, double sellPrice)
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
}