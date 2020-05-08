package com.stevekung.skyblockcatia.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.gui.ItemButton;
import com.stevekung.skyblockcatia.gui.api.GuiSkyBlockAPIViewer;
import com.stevekung.skyblockcatia.utils.SkyBlockFakePlayerEntity;
import com.stevekung.stevekungslib.utils.CalendarUtils;
import com.stevekung.stevekungslib.utils.ColorUtils;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class MainEventHandler
{
    private final Minecraft mc;
    private static final List<String> INVENTORY_LIST = new ArrayList<>(Arrays.asList("Trades", "Shop Trading Options", "Runic Pedestal"));
    public static String auctionPrice = "";
    public static final List<String> CHATABLE_LIST = new ArrayList<>(Arrays.asList("You                  ", "Ender Chest", "Craft Item", "Trades", "Shop Trading Options", "Runic Pedestal", "Your Bids", "Bank", "Bank Deposit", "Bank Withdrawal"));
    public static boolean showChat;
    public static String playerToView;
    public static final List<String> SKYBLOCK_PACK_16 = new ArrayList<>(Arrays.asList("v8F1.8 Hypixel Skyblock Pack (16x)", "v8O1.8 Hypixel Skyblock Pack(16x)", "v9F1.8 Hypixel Skyblock Pack (16x)", "v9O1.8 Hypixel Skyblock Pack (16x)", "vXF16x_Skyblock_Pack_1.8.9", "vXO16x_Skyblock_Pack_1.8.9"));
    public static final List<String> SKYBLOCK_PACK_32 = new ArrayList<>(Arrays.asList("v8F1.8 Hypixel Skyblock Pack (x32)", "v8O1.8 Hypixel Skyblock Pack (32x)", "v9F1.8 Hypixel Skyblock Pack (32x)", "v9.1O1.8 Hypixel Skyblock Pack (32x)", "vXF32x_Skyblock_Pack_1.8.9", "vXO32x_Skyblock_Pack_1.8.9"));

    public MainEventHandler()
    {
        this.mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void onRenderNameplate(RenderNameplateEvent event)
    {
        if (event.getEntity() instanceof SkyBlockFakePlayerEntity)
        {
            event.setResult(Result.DENY);
        }
    }

    @SubscribeEvent
    public void onRenderTooltipEvent(RenderTooltipEvent.Pre event)
    {
        if (HypixelEventHandler.isSkyBlock && TextFormatting.getTextWithoutFormattingCodes(event.getLines().get(0)).equals(" "))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            if (MainEventHandler.playerToView != null)
            {
                this.mc.displayGuiScreen(new GuiSkyBlockAPIViewer(GuiSkyBlockAPIViewer.GuiState.PLAYER, MainEventHandler.playerToView, ""));
                MainEventHandler.playerToView = null;
            }
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event)
    {
        Screen gui = event.getGui();
        int width = gui.width / 2;
        int height = gui.height / 2 - 106;

        if (HypixelEventHandler.isSkyBlock)
        {
            if (gui instanceof InventoryScreen)
            {
                ItemButton craftingButton = new ItemButton(width + 28, height + 86, Blocks.CRAFTING_TABLE.asItem(), button -> this.mc.player.sendChatMessage("/viewcraftingtable"));
                craftingButton.visible = HypixelEventHandler.SKY_BLOCK_LOCATION.isHub();
                event.removeWidget(event.getWidgetList().get(0));
                event.addWidget(craftingButton);
                event.addWidget(new ItemButton(width + 9, height + 86, Blocks.ENDER_CHEST.asItem(), button -> this.mc.player.sendChatMessage("/enderchest")));
            }
            else if (gui instanceof ChestScreen)
            {
                ChestScreen chest = (ChestScreen)gui;
                ITextComponent title = chest.getTitle();
                ItemButton craftingButton = new ItemButton(width + 88, height + 65, Blocks.CRAFTING_TABLE.asItem(), HypixelEventHandler.SKY_BLOCK_LOCATION.isHub(), button -> this.mc.player.sendChatMessage("/viewcraftingtable"));

                if (MainEventHandler.isSuitableForGUI(MainEventHandler.CHATABLE_LIST, title))
                {
                    event.addWidget(new Button(width - 108, height + 190, 20, 20, "C", button -> MainEventHandler.showChat = !MainEventHandler.showChat));
                }

                if (MainEventHandler.isSuitableForGUI(MainEventHandler.INVENTORY_LIST, title))
                {
                    event.addWidget(new ItemButton(width + 88, height + 47, Blocks.ENDER_CHEST.asItem(), button -> this.mc.player.sendChatMessage("/enderchest")));
                    event.addWidget(craftingButton);
                    event.addWidget(new ItemButton(width + 88, height + 65, Items.NETHER_STAR, HypixelEventHandler.SKY_BLOCK_LOCATION.isShopOutsideHub(), "SkyBlock Menu", button -> this.mc.player.sendChatMessage("/sbmenu")));
                }
                else if (title.getUnformattedComponentText().equals("Craft Item"))
                {
                    event.addWidget(new ItemButton(width + 88, height + 47, Blocks.ENDER_CHEST.asItem(), button -> this.mc.player.sendChatMessage("/enderchest")));
                    event.addWidget(new ItemButton(width + 88, height + 65, Items.NETHER_STAR, "SkyBlock Menu", button -> this.mc.player.sendChatMessage("/sbmenu")));
                }
                else if (title.getUnformattedComponentText().equals("Ender Chest"))
                {
                    craftingButton = new ItemButton(width + 88, height + 47, Blocks.CRAFTING_TABLE.asItem(), HypixelEventHandler.SKY_BLOCK_LOCATION.isHub(), button -> this.mc.player.sendChatMessage("/viewcraftingtable"));
                    event.addWidget(craftingButton);
                    event.addWidget(new ItemButton(width + 88, height + (HypixelEventHandler.SKY_BLOCK_LOCATION.isHub() ? 65 : 47), Items.NETHER_STAR, "SkyBlock Menu", button -> this.mc.player.sendChatMessage("/sbmenu")));
                }
            }
        }
    }

    @SubscribeEvent
    public void onPostGuiDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        Screen gui = event.getGui();

        for (Widget button : gui.buttons.stream().filter(button -> button instanceof ItemButton).collect(Collectors.toList()))
        {
            boolean hover = event.getMouseX() >= button.x && event.getMouseY() >= button.y && event.getMouseX() < button.x + button.getWidth() && event.getMouseY() < button.y + button.getHeight();

            if (hover && button.visible)
            {
                GuiUtils.drawHoveringText(Collections.singletonList(((ItemButton)button).getName()), event.getMouseX(), event.getMouseY(), gui.width, gui.height, -1, this.mc.fontRenderer);
                RenderSystem.disableLighting();
            }
        }

        if (gui instanceof ChestScreen)
        {
            ChestScreen chest = (ChestScreen)gui;

            if (ExtendedConfig.INSTANCE.lobbyPlayerViewer && chest.getTitle().getUnformattedComponentText().equals("SkyBlock Hub Selector"))
            {
                List<String> lobby1 = new ArrayList<>();
                List<String> lobby2 = new ArrayList<>();

                for (int i = 0; i < chest.getContainer().getLowerChestInventory().getSizeInventory(); i++)
                {
                    ItemStack itemStack = chest.getContainer().getLowerChestInventory().getStackInSlot(i);

                    if (itemStack.isEmpty())
                    {
                        continue;
                    }

                    if (itemStack.getDisplayName().getUnformattedComponentText().contains("SkyBlock Hub"))
                    {
                        String name = itemStack.getDisplayName().getUnformattedComponentText().substring(itemStack.getDisplayName().getUnformattedComponentText().indexOf("#"));
                        int lobbyNum = Integer.valueOf(name.substring(name.indexOf("#") + 1));
                        String lobbyCount = "";
                        int min = 0;
                        int max = 0;

                        if (itemStack.hasTag())
                        {
                            CompoundNBT compound = itemStack.getTag().getCompound("display");

                            if (compound.getTagId("Lore") == Constants.NBT.TAG_LIST)
                            {
                                ListNBT list = compound.getList("Lore", Constants.NBT.TAG_STRING);

                                int countIndex = -1;

                                for (int j1 = 0; j1 < list.size(); ++j1)
                                {
                                    String playerLore = TextFormatting.getTextWithoutFormattingCodes(ITextComponent.Serializer.fromJson(list.getString(j1)).getString());

                                    if (playerLore.contains("Players: "))
                                    {
                                        countIndex = j1;
                                        break;
                                    }
                                }

                                String lore = TextFormatting.getTextWithoutFormattingCodes(ITextComponent.Serializer.fromJson(list.getString(countIndex)).getString());
                                lore = lore.substring(lore.indexOf(" ") + 1);
                                String[] loreCount = lore.split("/");
                                min = Integer.valueOf(loreCount[0]);
                                max = Integer.valueOf(loreCount[1]);

                                if (min >= max)
                                {
                                    lobbyCount = TextFormatting.RED + "Full!";
                                }
                                else if (min >= max - 15) // 70
                                {
                                    lobbyCount = TextFormatting.YELLOW + "" + min + TextFormatting.GRAY + "/" + TextFormatting.RED + max;
                                }
                                else if (min > max - 40 && min < max - 15) // 40 > 70
                                {
                                    lobbyCount = TextFormatting.GOLD + "" + min + TextFormatting.GRAY + "/" + TextFormatting.RED + max;
                                }
                                else if (min <= max - 40) // < 40
                                {
                                    lobbyCount = TextFormatting.GREEN + "" + min + TextFormatting.GRAY + "/" + TextFormatting.RED + max;
                                }
                            }
                        }

                        if (lobbyNum > 14)
                        {
                            lobby2.add(ColorUtils.stringToRGB("36,224,186").toColoredFont() + name + " " + lobbyCount);
                        }
                        else
                        {
                            lobby1.add(ColorUtils.stringToRGB("36,224,186").toColoredFont() + name + " " + lobbyCount);
                        }
                    }
                }

                int i = 0;
                RenderHelper.disableStandardItemLighting();

                for (String lobbyCount : lobby1)
                {
                    int fontHeight = this.mc.fontRenderer.FONT_HEIGHT + 1;
                    int yOffset = chest.height / 2 - 75 + fontHeight * i;
                    chest.drawString(this.mc.fontRenderer, lobbyCount, chest.width / 2 - 200, yOffset, 0);
                    i++;
                }

                int i2 = 0;

                for (String lobbyCount : lobby2)
                {
                    int fontHeight = this.mc.fontRenderer.FONT_HEIGHT + 1;
                    int yOffset = chest.height / 2 - 75 + fontHeight * i2;
                    chest.drawString(this.mc.fontRenderer, lobbyCount, chest.width / 2 - 143, yOffset, 0);
                    i2++;
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
        if (event.getGui() instanceof MainMenuScreen)
        {
            MainMenuScreen menu = (MainMenuScreen)event.getGui();

            if (CalendarUtils.isSteveKunGBirthDay())
            {
                menu.splashText = "Happy birthday, SteveKunG!";
            }
        }
    }

    private static <T extends Container> boolean isSuitableForGUI(List<String> invList, ITextComponent title)
    {
        return invList.stream().anyMatch(invName -> title.getUnformattedComponentText().contains(invName));
    }
}