package com.stevekung.skyblockcatia.event.handler;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler
{
    private final Minecraft mc;

    public ClientEventHandler()
    {
        this.mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void onItemPickedUpTest(PlayerEvent.ItemPickupEvent event)
    {
        boolean enable = true;

        if (!enable)
        {
            return;
        }

        PlayerEntity player = event.getPlayer();
        Random rand = player.world.rand;
        ItemStack itemStack = event.getStack();
        String magic = rand.nextBoolean() ? " §r§b(+" + rand.nextInt(100) + "% Magic Find!)§r" : "";
        char formatter = TextFormatting.values()[new Random().nextInt(TextFormatting.values().length)].formattingCode;

        // Pet Level Up
      //String[] pet = new String[] {"Enderman","Bat","Parrot","Blue Whale","Bee","Skeleton Horse","Flying Fish","Magma Cube"};
        //player.sendMessage(JsonUtils.create("§r§aYour §r§" + formatter + pet[rand.nextInt(pet.length)] + " §r§alevelled up to level §r§9" + rand.nextInt(100) + "§r§a!§r"));

        // Pet Drop
        //player.addChatComponentMessage(JsonUtils.create("PET DROP! " + EnumChatFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName().replace("[Lvl 1] ", "")) + EnumChatFormatting.getTextWithoutFormattingCodes(magic)));

        // Rare Drop
        //String test = "§r§6§lRARE DROP! " + "§r§" + formatter + itemStack.getDisplayName() + magic;
        //String test = "§r§6§lRARE DROP! " + "§r" + itemStack.getDisplayName().getFormattedText() + magic;
      //String test = "§r§6§lRARE DROP! " + "§r§f" + EnumChatFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName()) + magic;
        //player.sendMessage(JsonUtils.create(test));

        // Slayer Drop
        /*switch (rand.nextInt(4))
        {
        case 0:
        default:
            player.sendMessage(JsonUtils.create("§r§b§lRARE DROP! §r§7(").appendSibling(JsonUtils.create(itemStack.getDisplayName().getFormattedText())).appendSibling(JsonUtils.create("§r§7" + ")" + magic)));
            player.sendMessage(JsonUtils.create("§r§b§lRARE DROP! §r§7(").appendSibling(JsonUtils.create("§r§" + formatter + itemStack.getDisplayName().getFormattedText())).appendSibling(JsonUtils.create("§r§7" + ")" + magic)));
            break;
        case 1:
            player.sendMessage(JsonUtils.create("§r§9§lVERY RARE DROP!  §r§7(").appendSibling(JsonUtils.create(itemStack.getDisplayName().getFormattedText())).appendSibling(JsonUtils.create("§r§7" + ")" + magic)));
            player.sendMessage(JsonUtils.create("§r§9§lVERY RARE DROP!  §r§7(").appendSibling(JsonUtils.create("§r§" + formatter + itemStack.getDisplayName().getFormattedText())).appendSibling(JsonUtils.create("§r§7" + ")" + magic)));
            break;
        case 2:
            player.sendMessage(JsonUtils.create("§r§5§lVERY RARE DROP!  §r§7(").appendSibling(JsonUtils.create(itemStack.getDisplayName().getFormattedText())).appendSibling(JsonUtils.create("§r§7" + ")" + magic)));
            player.sendMessage(JsonUtils.create("§r§5§lVERY RARE DROP!  §r§7(").appendSibling(JsonUtils.create("§r§" + formatter + itemStack.getDisplayName().getFormattedText())).appendSibling(JsonUtils.create("§r§7" + ")" + magic)));
            break;
        case 3:
            player.sendMessage(JsonUtils.create("§r§d§lCRAZY RARE DROP!  §r§7(").appendSibling(JsonUtils.create(itemStack.getDisplayName().getFormattedText())).appendSibling(JsonUtils.create("§r§7" + ")" + magic)));
            player.sendMessage(JsonUtils.create("§r§d§lCRAZY RARE DROP!  §r§7(").appendSibling(JsonUtils.create("§r§" + formatter + itemStack.getDisplayName().getFormattedText())).appendSibling(JsonUtils.create("§r§7" + ")" + magic)));
            break;
        }*/

        // Dragon Drop
        //player.sendMessage(JsonUtils.create(com.stevekung.stevekungslib.utils.GameProfileUtils.getUsername() + " has obtained " + itemStack.getDisplayName().getFormattedText() + "!"));

        // Gift Item Drop
        /*switch (rand.nextInt(4))
        {
        case 0:
            player.sendMessage(JsonUtils.create("COMMON! ").appendSibling(itemStack.getDisplayName()).appendSibling(JsonUtils.create(" gift with TEST!")));
            break;
        case 1:
            player.sendMessage(JsonUtils.create("SWEET! ").appendSibling(itemStack.getDisplayName()).appendSibling(JsonUtils.create(" gift with TEST!")));
            break;
        case 2:
            player.sendMessage(JsonUtils.create("RARE! ").appendSibling(itemStack.getDisplayName()).appendSibling(JsonUtils.create(" gift with TEST!")));
            break;
        case 3:
            player.sendMessage(JsonUtils.create("SANTA TIER! ").appendSibling(itemStack.getDisplayName()).appendSibling(JsonUtils.create(" gift with TEST!")));
            break;
        }*/

        /*String[] skill = new String[] {"Farming","Mining","Combat","Foraging","Fishing","Enchanting","Alchemy"};
        String exp = "+" + new java.text.DecimalFormat("#,###,###").format(rand.nextInt(10000)) + " " + skill[rand.nextInt(skill.length)] + " XP gift with TEST!";

        // Skill XP Gift
        switch (rand.nextInt(3))
        {
        case 0:
            player.sendMessage(JsonUtils.create("COMMON! " + exp));
            break;
        case 1:
            player.sendMessage(JsonUtils.create("SWEET! " + exp));
            break;
        case 2:
            player.sendMessage(JsonUtils.create("RARE! " + exp));
            break;
        }*/

        // Gift Coins
        /*String coins = "+" + new java.text.DecimalFormat("#,###,###").format(rand.nextInt(10000)) + " coins gift with TEST!";

        switch (rand.nextInt(3))
        {
        case 0:
            player.sendMessage(JsonUtils.create("COMMON! " + coins));
            break;
        case 1:
            player.sendMessage(JsonUtils.create("SWEET! " + coins));
            break;
        case 2:
            player.sendMessage(JsonUtils.create("RARE! " + coins));
            break;
        }*/

        // Fishing Coins
        /*int coin = rand.nextInt(20000);

        if (rand.nextBoolean())
        {
            player.sendMessage(JsonUtils.create("GOOD CATCH! You found " + coin + " Coins."));
        }
        else
        {
            player.sendMessage(JsonUtils.create("GREAT CATCH! You found " + coin + " Coins."));
        }*/

        // Fishing Drop
        /*switch (rand.nextInt(2))
        {
        case 0:
        default:
            player.sendMessage(JsonUtils.create("GOOD CATCH! You found a ").appendSibling(itemStack.getDisplayName()).appendSibling(JsonUtils.create("!")));
            break;
        case 1:
            player.sendMessage(JsonUtils.create("GREAT CATCH! You found a ").appendSibling(itemStack.getDisplayName()).appendSibling(JsonUtils.create("!")));
            break;
        }*/

        // Dungeon Drop
        //String test = "You found a Top Quality Item! " + itemStack.getDisplayName().getFormattedText();
        //String test = "You found a Top Quality Item! Skeleton Soldier Boots";
        //String test = "     RARE REWARD! Adaptive Boots";
      //String test = "     RARE REWARD! " + itemStack.getDisplayName();
        //player.sendMessage(JsonUtils.create(test));
        
        // Mythos Drop
        //String test = "RARE DROP! You dug out a " + itemStack.getDisplayName() + "!";
        //player.addChatComponentMessage(JsonUtils.create(test));
        //String coins = "Wow! You dug out " + new java.text.DecimalFormat("#,###,###").format(rand.nextInt(10000)) + " coins!";
        //player.addChatComponentMessage(JsonUtils.create(coins));
        
        // Bank Interest/Allowance
        //String coins = "Since you've been away you earned " + new java.text.DecimalFormat("#,###,###").format(rand.nextInt(10000)) + " coins as interest in your personal bank account!";
        //String coins = "ALLOWANCE! You earned " + new java.text.DecimalFormat("#,###,###").format(rand.nextInt(10000)) + " coins!";
        //player.addChatComponentMessage(JsonUtils.create(coins));
    }
}