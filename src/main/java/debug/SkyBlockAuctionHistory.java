package debug;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.stevekung.skyblockcatia.utils.LoggerIN;

import debug.SkyBlockAuctionData.Bidder;

public class SkyBlockAuctionHistory
{
    public static SkyBlockAuctionData data;
    public static String SKYBLOCK_PROFILE;

    public static void main(String[] args) throws IOException
    {
        SKYBLOCK_PROFILE = "https://api.hypixel.net/skyblock/auction?key=e32c5f73-4c21-4129-ba2d-683da9eb786e&profile=eef3a6031c1b4c988264d2f04b231ef4";
        SkyBlockAuctionHistory.checkAPI();
    }

    static String getName(String uuid) throws JsonSyntaxException, IOException
    {
        URL url = new URL("https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names");
        JsonArray array = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonArray();
        String name = array.get(array.size() - 1).getAsJsonObject().get("name").getAsString();
        return name;
    }

    private static void checkAPI() throws IOException
    {
        Gson gson = new Gson();
        URL url = new URL(SKYBLOCK_PROFILE);
        data = gson.fromJson(new InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8), SkyBlockAuctionData.class);
        
        data.getAuctionProducts().sort((product1, product2) -> new CompareToBuilder().append(product2.getEnd(), product1.getEnd()).build());
        
        for (SkyBlockAuctionData.Product product : data.getAuctionProducts())
        {
            Date startDate = new Date(product.getStart());
            Date endDate = new Date(product.getEnd());
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            LoggerIN.info("ItemName: {}", product.getItem_name());
            LoggerIN.info("Start: {}", format.format(startDate));
            LoggerIN.info("End: {}", format.format(endDate));
            LoggerIN.info("HighBid: {}", product.getHighest_bid_amount());
            LoggerIN.info("IsClaimed: {}", product.isClaimed());
            
            LoggerIN.info("");
            
            for (String claimedBidder : product.getClaimed_bidders())
            {
                LoggerIN.info("ClaimedBidderUUID: {}", (claimedBidder));
                LoggerIN.info("ClaimedBidderName: {}", getName(claimedBidder));
                LoggerIN.info("");
            }
            
            product.getBids().sort((bidder1, bidder2) -> new CompareToBuilder().append(bidder2.getAmount(), bidder1.getAmount()).build());
            int i = 0;
            
            for (Bidder bidder : product.getBids())
            {
                i++;
                Date bidTime = new Date(bidder.getTimestamp());
                LoggerIN.info("Amount: {}", bidder.getAmount());
                LoggerIN.info("BidderUUID: {}", bidder.getBidder());
                LoggerIN.info("BidderName: {}", getName(bidder.getBidder()));
                LoggerIN.info("BidTime: {}", format.format(bidTime));
                LoggerIN.info("");
                
                if (i == 5)
                {
                    break;
                }
            }
            LoggerIN.info("----------------------------------");
        }
    }
}