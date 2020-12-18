package com.stevekung.skyblockcatia.event.handler;

import java.text.DecimalFormat;
import java.util.Random;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.utils.JsonUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ToastTestEventHandler
{
    private final Minecraft mc;
    private final DecimalFormat format = new DecimalFormat("#,###");

    public ToastTestEventHandler()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onItemPickedUpTest(PlayerEvent.ItemPickupEvent event)
    {
        boolean dev = SkyBlockcatiaMod.isDevelopment;

        if (!dev)
        {
            return;
        }

        Random rand = event.player.getRNG();
        EntityPlayer player = event.player;
        ItemStack itemStack = event.pickedUp.getEntityItem();
        String magic = rand.nextBoolean() ? " §r§b(+" + rand.nextInt(100) + "% Magic Find!)§r" : "";
        char formatter = EnumChatFormatting.values()[new Random().nextInt(EnumChatFormatting.values().length)].formattingCode;

        // ****************** Toast Update or Add Section ******************
        // Pet Level Up
        //String[] pet = new String[] {"Enderman","Bat","Parrot","Blue Whale","Bee","Skeleton Horse","Flying Fish","Magma Cube"};
        //player.addChatComponentMessage(JsonUtils.create("§r§aYour §r§" + formatter + pet[rand.nextInt(pet.length)] + " §r§alevelled up to level §r§9" + rand.nextInt(100) + "§r§a!§r"));

        // Skill XP Gift
        /*String[] skill = new String[] {"Farming","Mining","Combat","Foraging","Fishing","Enchanting","Alchemy"};
        String exp = "+" + format.format(rand.nextInt(10000)) + " " + skill[rand.nextInt(skill.length)] + " XP gift with TEST!";

        switch (rand.nextInt(3))
        {
        case 0:
            player.addChatComponentMessage(JsonUtils.create("COMMON! " + exp));
            break;
        case 1:
            player.addChatComponentMessage(JsonUtils.create("SWEET! " + exp));
            break;
        case 2:
            player.addChatComponentMessage(JsonUtils.create("RARE! " + exp));
            break;
        }*/

        // Gift Coins
        /*String coins = "+" + format.format(rand.nextInt(10000)) + " coins gift with TEST!";

        switch (rand.nextInt(3))
        {
        case 0:
            player.addChatComponentMessage(JsonUtils.create("COMMON! " + coins));
            break;
        case 1:
            player.addChatComponentMessage(JsonUtils.create("SWEET! " + coins));
            break;
        case 2:
            player.addChatComponentMessage(JsonUtils.create("RARE! " + coins));
            break;
        }*/

        // Fishing Coins
        /*int coin = rand.nextInt(20000);

        if (rand.nextBoolean())
        {
            player.addChatComponentMessage(JsonUtils.create("GOOD CATCH! You found " + coin + " Coins."));
        }
        else
        {
            player.addChatComponentMessage(JsonUtils.create("GREAT CATCH! You found " + coin + " Coins."));
        }*/

        // Bank Interest
        //String coins = "Since you've been away you earned " + format.format(rand.nextInt(10000)) + " coins as interest in your personal bank account!";
        //player.addChatComponentMessage(JsonUtils.create(coins));

        // Allowance
        //String coins = "ALLOWANCE! You earned " + format.format(rand.nextInt(10000)) + " coins!";
        //player.addChatComponentMessage(JsonUtils.create(coins));

        // Mythos Drop
        //String coins = "Wow! You dug out " + format.format(rand.nextInt(10000)) + " coins!";
        //player.addChatComponentMessage(JsonUtils.create(coins));

        // ****************** Item Drop Section ******************

        // Pet Drop
        //player.addChatComponentMessage(JsonUtils.create("PET DROP! " + EnumChatFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName().replace("[Lvl 1] ", "")) + EnumChatFormatting.getTextWithoutFormattingCodes(magic)));

        // Rare Drop
        String test = "§r§6§lRARE DROP! " + "§r" + itemStack.getDisplayName() + magic; // vanilla/skyblock items
        //String test = "§r§6§lRARE DROP! " + "§r§f" + EnumChatFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName()) + magic; // skyblock common items
        player.addChatComponentMessage(JsonUtils.create(test));

        // Slayer Drop
        /*switch (rand.nextInt(4))
        {
        case 0:
        default:
            player.addChatComponentMessage(JsonUtils.create("§r§b§lRARE DROP! §r§7(").appendSibling(JsonUtils.create(itemStack.getDisplayName())).appendSibling(JsonUtils.create("§r§7" + ")" + magic)));
            break;
        case 1:
            player.addChatComponentMessage(JsonUtils.create("§r§9§lVERY RARE DROP!  §r§7(").appendSibling(JsonUtils.create(itemStack.getDisplayName())).appendSibling(JsonUtils.create("§r§7" + ")" + magic)));
            break;
        case 2:
            player.addChatComponentMessage(JsonUtils.create("§r§5§lVERY RARE DROP!  §r§7(").appendSibling(JsonUtils.create(itemStack.getDisplayName())).appendSibling(JsonUtils.create("§r§7" + ")" + magic)));
            break;
        case 3:
            player.addChatComponentMessage(JsonUtils.create("§r§d§lCRAZY RARE DROP!  §r§7(").appendSibling(JsonUtils.create(itemStack.getDisplayName())).appendSibling(JsonUtils.create("§r§7" + ")" + magic)));
            break;
        }*/

        // Dragon Drop
        //player.addChatComponentMessage(JsonUtils.create(com.stevekung.skyblockcatia.utils.GameProfileUtils.getUsername() + " has obtained " + itemStack.getDisplayName() + "!"));

        // Gift Item Drop
        /*switch (rand.nextInt(4))
        {
        case 0:
            player.addChatComponentMessage(JsonUtils.create("COMMON! ").appendSibling(JsonUtils.create(itemStack.getDisplayName())).appendSibling(JsonUtils.create(" gift with TEST!")));
            break;
        case 1:
            player.addChatComponentMessage(JsonUtils.create("SWEET! ").appendSibling(JsonUtils.create(itemStack.getDisplayName())).appendSibling(JsonUtils.create(" gift with TEST!")));
            break;
        case 2:
            player.addChatComponentMessage(JsonUtils.create("RARE! ").appendSibling(JsonUtils.create(itemStack.getDisplayName())).appendSibling(JsonUtils.create(" gift with TEST!")));
            break;
        case 3:
            player.addChatComponentMessage(JsonUtils.create("SANTA TIER! ").appendSibling(JsonUtils.create(itemStack.getDisplayName())).appendSibling(JsonUtils.create(" gift with TEST!")));
            break;
        }*/

        // Fishing Drop
        /*switch (rand.nextInt(2))
        {
        case 0:
        default:
            player.addChatComponentMessage(JsonUtils.create("GOOD CATCH! You found a ").appendSibling(JsonUtils.create(itemStack.getDisplayName())).appendSibling(JsonUtils.create("!")));
            break;
        case 1:
            player.addChatComponentMessage(JsonUtils.create("GREAT CATCH! You found a ").appendSibling(JsonUtils.create(itemStack.getDisplayName())).appendSibling(JsonUtils.create("!")));
            break;
        }*/

        // Dungeon Drop
        //String test = "You found a Top Quality Item! " + itemStack.getDisplayName();
        //String test = "You found a Top Quality Item! Skeleton Soldier Boots";
        //String test = "     RARE REWARD! Adaptive Boots";
        //String test = "     RARE REWARD! " + itemStack.getDisplayName();
        //player.addChatComponentMessage(JsonUtils.create(test));

        // Mythos Drop
        //String test = "RARE DROP! You dug out a " + itemStack.getDisplayName() + "!";
        //player.addChatComponentMessage(JsonUtils.create(test));
    }
}