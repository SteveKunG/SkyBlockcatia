package debug;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SkyBlockAuctionData
{
    @SerializedName("auctions")
    private final List<Product> auctionProducts;
    
    public SkyBlockAuctionData(List<Product> auctionProducts)
    {
        super();
        this.auctionProducts = auctionProducts;
    }

    public List<Product> getAuctionProducts()
    {
        return auctionProducts;
    }

    public static class Product
    {
        private final long start;
        private final long end;
        
        @SerializedName("item_bytes")
        private final AhItem item;
        private final boolean claimed;
        private final List<String> claimed_bidders;
        private final int highest_bid_amount;
        private final List<Bidder> bids;
        
        public Product(long start, long end, AhItem item, boolean claimed, List<String> claimed_bidders, int highest_bid_amount, List<Bidder> bids,
                String item_name)
        {
            super();
            this.start = start;
            this.end = end;
            this.item = item;
            this.claimed = claimed;
            this.claimed_bidders = claimed_bidders;
            this.highest_bid_amount = highest_bid_amount;
            this.bids = bids;
            this.item_name = item_name;
        }

        private final String item_name;

        public long getStart()
        {
            return start;
        }

        public long getEnd()
        {
            return end;
        }

        public AhItem getItem()
        {
            return item;
        }

        public boolean isClaimed()
        {
            return claimed;
        }

        public List<String> getClaimed_bidders()
        {
            return claimed_bidders;
        }

        public int getHighest_bid_amount()
        {
            return highest_bid_amount;
        }

        public List<Bidder> getBids()
        {
            return bids;
        }

        public String getItem_name()
        {
            return item_name;
        }
    }
    
    public static class AhItem
    {
        private final String data;

        public AhItem(String data)
        {
            super();
            this.data = data;
        }

        public String getData()
        {
            return data;
        }
    }
    
    public static class Bidder
    {
        private final String bidder;
        private final int amount;
        private final long timestamp;
        public Bidder(String bidder, int amount, long timestamp)
        {
            super();
            this.bidder = bidder;
            this.amount = amount;
            this.timestamp = timestamp;
        }
        public String getBidder()
        {
            return bidder;
        }
        public int getAmount()
        {
            return amount;
        }
        public long getTimestamp()
        {
            return timestamp;
        }
    }
    
}
