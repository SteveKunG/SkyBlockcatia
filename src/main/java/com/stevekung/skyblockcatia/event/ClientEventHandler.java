package com.stevekung.skyblockcatia.event;

import java.util.Random;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.utils.GameProfileUtils;
import com.stevekung.skyblockcatia.utils.GuiChatRegistry;
import com.stevekung.skyblockcatia.utils.JsonUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatStyle;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ClientEventHandler
{
    private final Minecraft mc;
    public static int ticks;
    public static int ticksPaused;
    public static float renderPartialTicks;

    public ClientEventHandler()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (this.mc.currentScreen instanceof GuiMainMenu)
        {
            ClientEventHandler.ticks = 0;
            ClientEventHandler.ticksPaused = 0;
        }
        if (event.phase == Phase.START)
        {
            ClientEventHandler.ticks++;
            ClientEventHandler.renderPartialTicks = ClientEventHandler.ticks + this.mc.timer.renderPartialTicks;

            if (!this.mc.isGamePaused())
            {
                ClientEventHandler.ticksPaused++;
            }
        }
        if (SkyBlockcatiaMod.CURRENT_UUID != null && !SkyBlockcatiaMod.CURRENT_UUID.toString().equals(GameProfileUtils.getUUID().toString()) && !(GameProfileUtils.getUsername().equals("MCCommanderz") || GameProfileUtils.getUsername().equals("TheWatcherz") || GameProfileUtils.getUsername().equals("BesutoKunG") || GameProfileUtils.getUsername().equals("iamcameraman")))
        {
            this.mc.shutdown();
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event)
    {
        GuiChatRegistry.getGuiChatList().forEach(gui -> gui.actionPerformed(event.button));
    }

    @SubscribeEvent
    public void onItemPickedUpTest(PlayerEvent.ItemPickupEvent event)
    {
        boolean enable = SkyBlockcatiaMod.isDevelopment;

        if (!enable)
        {
            return;
        }

        Random rand = event.player.worldObj.rand;
        EntityPlayer player = event.player;
        ItemStack itemStack = event.pickedUp.getEntityItem();
        ChatStyle hoverStyle = JsonUtils.style().setChatHoverEvent(JsonUtils.hover(HoverEvent.Action.SHOW_ITEM, JsonUtils.create(itemStack.writeToNBT(new NBTTagCompound()).toString())));
//        String magic = rand.nextBoolean() ? " (+" + rand.nextInt(100) + "% Magic Find!)" : "";
//        String magic = " (+" + 100 + "% Magic Find!)";
        String magic = "";

        // Pet Level Up
        /*String[] pet = new String[] {"Enderman","Bat","Parrot","Blue Whale","Bee","Skeleton Horse"};
        player.addChatComponentMessage(JsonUtils.create("Your " + pet[rand.nextInt(pet.length)] + " levelled up to level " + rand.nextInt(50) + "!"));*/

        // Pet Drop
        //player.addChatComponentMessage(JsonUtils.create("PET DROP! ").appendSibling(JsonUtils.create(itemStack.getDisplayName()).setChatStyle(JsonUtils.style().setChatHoverEvent(JsonUtils.hover(HoverEvent.Action.SHOW_ITEM, JsonUtils.create(itemStack.writeToNBT(new NBTTagCompound()).toString()))))).appendSibling(JsonUtils.create(magic)));

        // Rare Drop with or without bracket
        switch (rand.nextInt(2))
        {
        case 0:
        default:
//            player.addChatComponentMessage(JsonUtils.create("RARE DROP! (").appendSibling(JsonUtils.create(itemStack.getDisplayName()).setChatStyle(hoverStyle)).appendSibling(JsonUtils.create(")" + magic)));
            player.addChatComponentMessage(JsonUtils.create("RARE DROP! ").appendSibling(JsonUtils.create(itemStack.getDisplayName()).setChatStyle(hoverStyle)).appendSibling(JsonUtils.create(magic)));
            break;
        case 1:
            player.addChatComponentMessage(JsonUtils.create("RARE DROP! ").appendSibling(JsonUtils.create(itemStack.getDisplayName()).setChatStyle(hoverStyle)).appendSibling(JsonUtils.create(magic)));
            break;
        }

        // Slayer Drop
        /*switch (rand.nextInt(3))
        {
        case 0:
        default:
            player.addChatComponentMessage(JsonUtils.create("RARE DROP! (").appendSibling(JsonUtils.create(itemStack.getDisplayName()).setChatStyle(hoverStyle)).appendSibling(JsonUtils.create(")" + magic)));
            break;
        case 1:
            player.addChatComponentMessage(JsonUtils.create("VERY RARE DROP!  (").appendSibling(JsonUtils.create(itemStack.getDisplayName()).setChatStyle(hoverStyle)).appendSibling(JsonUtils.create(")" + magic)));
            break;
        case 2:
            player.addChatComponentMessage(JsonUtils.create("CRAZY RARE DROP!  (").appendSibling(JsonUtils.create(itemStack.getDisplayName()).setChatStyle(hoverStyle)).appendSibling(JsonUtils.create(")" + magic)));
            break;
        }*/

        // Dragon Drop
        //player.addChatComponentMessage(JsonUtils.create(com.stevekung.skyblockcatia.utils.GameProfileUtils.getUsername() + " has obtained " + itemStack.getDisplayName() + "!"));

        // Gift Item Drop
        /*switch (rand.nextInt(4))
        {
        case 0:
            player.addChatComponentMessage(JsonUtils.create("COMMON! ").appendSibling(JsonUtils.create(itemStack.getDisplayName()).setChatStyle(hoverStyle)).appendSibling(JsonUtils.create(" gift with TEST!")));
            break;
        case 1:
            player.addChatComponentMessage(JsonUtils.create("SWEET! ").appendSibling(JsonUtils.create(itemStack.getDisplayName()).setChatStyle(hoverStyle)).appendSibling(JsonUtils.create(" gift with TEST!")));
            break;
        case 2:
            player.addChatComponentMessage(JsonUtils.create("RARE! ").appendSibling(JsonUtils.create(itemStack.getDisplayName()).setChatStyle(hoverStyle)).appendSibling(JsonUtils.create(" gift with TEST!")));
            break;
        case 3:
            player.addChatComponentMessage(JsonUtils.create("SANTA TIER! ").appendSibling(JsonUtils.create(itemStack.getDisplayName()).setChatStyle(hoverStyle)).appendSibling(JsonUtils.create(" gift with TEST!")));
            break;
        }*/

        /*String[] skill = new String[] {"Farming","Mining","Combat","Foraging","Fishing","Enchanting","Alchemy"};
        String exp = "+" + new java.text.DecimalFormat("#,###,###").format(rand.nextInt(10000)) + " " + skill[rand.nextInt(skill.length)] + " XP gift with TEST!";

        // Skill XP Gift
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
        /*String coins = "+" + new java.text.DecimalFormat("#,###,###").format(rand.nextInt(10000)) + " coins gift with TEST!";

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

        // Fishing Drop
        /*switch (rand.nextInt(2))
        {
        case 0:
        default:
            player.addChatComponentMessage(JsonUtils.create("GOOD CATCH! You found a ").appendSibling(JsonUtils.create(itemStack.getDisplayName()).setChatStyle(hoverStyle)).appendSibling(JsonUtils.create("!")));
            break;
        case 1:
            player.addChatComponentMessage(JsonUtils.create("GREAT CATCH! You found a ").appendSibling(JsonUtils.create(itemStack.getDisplayName()).setChatStyle(hoverStyle)).appendSibling(JsonUtils.create("!")));
            break;
        }*/
    }
}