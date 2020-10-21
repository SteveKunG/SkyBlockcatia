package com.stevekung.skyblockcatia.event.handler;

import java.util.Random;

import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
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
        //player.sendMessage(TextComponentUtils.component("§r§aYour §r§" + formatter + pet[rand.nextInt(pet.length)] + " §r§alevelled up to level §r§9" + rand.nextInt(100) + "§r§a!§r"), Util.DUMMY_UUID);

        // Pet Drop
        //player.sendMessage(TextComponentUtils.component("PET DROP! " + TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName().getString().replaceAll("\\[Lvl \\d+\\] ", "")) + TextFormatting.getTextWithoutFormattingCodes(magic)), Util.DUMMY_UUID);

        // Rare Drop
        //String test = "§r§6§lRARE DROP! " + "§r§" + formatter + itemStack.getDisplayName().getString() + magic;
        //String test = "§r§6§lRARE DROP! " + "§r" + itemStack.getDisplayName().getString() + magic;
        //String test = "§r§6§lRARE DROP! " + "§r§f" + TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName().getString()) + magic;
        //player.sendMessage(TextComponentUtils.component(test), Util.DUMMY_UUID);

        // Slayer Drop
        /*switch (rand.nextInt(4))
        {
        case 0:
        default:
            player.sendMessage(TextComponentUtils.component("§r§b§lRARE DROP! §r§7(").append(TextComponentUtils.component(itemStack.getDisplayName().getString())).append(TextComponentUtils.component("§r§7" + ")" + magic)), Util.DUMMY_UUID);
            player.sendMessage(TextComponentUtils.component("§r§b§lRARE DROP! §r§7(").append(TextComponentUtils.component("§r§" + formatter + itemStack.getDisplayName().getString())).append(TextComponentUtils.component("§r§7" + ")" + magic)), Util.DUMMY_UUID);
            break;
        case 1:
            player.sendMessage(TextComponentUtils.component("§r§9§lVERY RARE DROP!  §r§7(").append(TextComponentUtils.component(itemStack.getDisplayName().getString())).append(TextComponentUtils.component("§r§7" + ")" + magic)), Util.DUMMY_UUID);
            player.sendMessage(TextComponentUtils.component("§r§9§lVERY RARE DROP!  §r§7(").append(TextComponentUtils.component("§r§" + formatter + itemStack.getDisplayName().getString())).append(TextComponentUtils.component("§r§7" + ")" + magic)), Util.DUMMY_UUID);
            break;
        case 2:
            player.sendMessage(TextComponentUtils.component("§r§5§lVERY RARE DROP!  §r§7(").append(TextComponentUtils.component(itemStack.getDisplayName().getString())).append(TextComponentUtils.component("§r§7" + ")" + magic)), Util.DUMMY_UUID);
            player.sendMessage(TextComponentUtils.component("§r§5§lVERY RARE DROP!  §r§7(").append(TextComponentUtils.component("§r§" + formatter + itemStack.getDisplayName().getString())).append(TextComponentUtils.component("§r§7" + ")" + magic)), Util.DUMMY_UUID);
            break;
        case 3:
            player.sendMessage(TextComponentUtils.component("§r§d§lCRAZY RARE DROP!  §r§7(").append(TextComponentUtils.component(itemStack.getDisplayName().getString())).append(TextComponentUtils.component("§r§7" + ")" + magic)), Util.DUMMY_UUID);
            player.sendMessage(TextComponentUtils.component("§r§d§lCRAZY RARE DROP!  §r§7(").append(TextComponentUtils.component("§r§" + formatter + itemStack.getDisplayName().getString())).append(TextComponentUtils.component("§r§7" + ")" + magic)), Util.DUMMY_UUID);
            break;
        }*/

        // Dragon Drop
        //player.sendMessage(TextComponentUtils.component(com.stevekung.stevekungslib.utils.GameProfileUtils.getUsername() + " has obtained " + itemStack.getDisplayName().getString() + "!"), Util.DUMMY_UUID);

        // Gift Item Drop
        /*switch (rand.nextInt(4))
        {
        case 0:
            player.sendMessage(TextComponentUtils.component("COMMON! ").append(itemStack.getDisplayName()).append(TextComponentUtils.component(" gift with TEST!")), Util.DUMMY_UUID);
            break;
        case 1:
            player.sendMessage(TextComponentUtils.component("SWEET! ").append(itemStack.getDisplayName()).append(TextComponentUtils.component(" gift with TEST!")), Util.DUMMY_UUID);
            break;
        case 2:
            player.sendMessage(TextComponentUtils.component("RARE! ").append(itemStack.getDisplayName()).append(TextComponentUtils.component(" gift with TEST!")), Util.DUMMY_UUID);
            break;
        case 3:
            player.sendMessage(TextComponentUtils.component("SANTA TIER! ").append(itemStack.getDisplayName()).append(TextComponentUtils.component(" gift with TEST!")), Util.DUMMY_UUID);
            break;
        }*/

        /*String[] skill = new String[] {"Farming","Mining","Combat","Foraging","Fishing","Enchanting","Alchemy"};
        String exp = "+" + new java.text.DecimalFormat("#,###,###").format(rand.nextInt(10000)) + " " + skill[rand.nextInt(skill.length)] + " XP gift with TEST!";

        // Skill XP Gift
        switch (rand.nextInt(3))
        {
        case 0:
            player.sendMessage(TextComponentUtils.component("COMMON! " + exp), Util.DUMMY_UUID);
            break;
        case 1:
            player.sendMessage(TextComponentUtils.component("SWEET! " + exp), Util.DUMMY_UUID);
            break;
        case 2:
            player.sendMessage(TextComponentUtils.component("RARE! " + exp), Util.DUMMY_UUID);
            break;
        }*/

        // Gift Coins
        /*String coins = "+" + new java.text.DecimalFormat("#,###,###").format(rand.nextInt(10000)) + " coins gift with TEST!";

        switch (rand.nextInt(3))
        {
        case 0:
            player.sendMessage(TextComponentUtils.component("COMMON! " + coins), Util.DUMMY_UUID);
            break;
        case 1:
            player.sendMessage(TextComponentUtils.component("SWEET! " + coins), Util.DUMMY_UUID);
            break;
        case 2:
            player.sendMessage(TextComponentUtils.component("RARE! " + coins), Util.DUMMY_UUID);
            break;
        }*/

        // Fishing Coins
        /*int coin = rand.nextInt(20000);

        if (rand.nextBoolean())
        {
            player.sendMessage(TextComponentUtils.component("GOOD CATCH! You found " + coin + " Coins."), Util.DUMMY_UUID);
        }
        else
        {
            player.sendMessage(TextComponentUtils.component("GREAT CATCH! You found " + coin + " Coins."), Util.DUMMY_UUID);
        }*/

        // Fishing Drop
        /*switch (rand.nextInt(2))
        {
        case 0:
        default:
            player.sendMessage(TextComponentUtils.component("GOOD CATCH! You found a ").append(itemStack.getDisplayName()).append(TextComponentUtils.component("!")), Util.DUMMY_UUID);
            break;
        case 1:
            player.sendMessage(TextComponentUtils.component("GREAT CATCH! You found a ").append(itemStack.getDisplayName()).append(TextComponentUtils.component("!")), Util.DUMMY_UUID);
            break;
        }*/

        // Dungeon Drop
        //String test = "You found a Top Quality Item! " + itemStack.getDisplayName().getString();
        //String test = "You found a Top Quality Item! Skeleton Soldier Boots";
        //String test = "     RARE REWARD! Adaptive Boots";
        //String test = "     RARE REWARD! " + itemStack.getDisplayName().getString();
        //player.sendMessage(TextComponentUtils.component(test), Util.DUMMY_UUID);

        // Mythos Drop
        //String test = "RARE DROP! You dug out a " + itemStack.getDisplayName().getString() + "!";
        //player.sendMessage(TextComponentUtils.component(test), Util.DUMMY_UUID);
        //String coins = "Wow! You dug out " + new java.text.DecimalFormat("#,###,###").format(rand.nextInt(10000)) + " coins!";
        //player.sendMessage(TextComponentUtils.component(coins), Util.DUMMY_UUID);

        // Bank Interest/Allowance
        //String coins = "Since you've been away you earned " + new java.text.DecimalFormat("#,###,###").format(rand.nextInt(10000)) + " coins as interest in your personal bank account!";
        //String coins = "ALLOWANCE! You earned " + new java.text.DecimalFormat("#,###,###").format(rand.nextInt(10000)) + " coins!";
        //player.sendMessage(TextComponentUtils.component(coins), Util.DUMMY_UUID);
    }
}