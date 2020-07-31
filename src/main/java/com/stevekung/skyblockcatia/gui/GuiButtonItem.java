package com.stevekung.skyblockcatia.gui;

import java.lang.reflect.Field;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class GuiButtonItem extends GuiButton
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/blank.png");
    private final int originalX;
    private final int potionX;
    private final Item item;
    private final Minecraft mc;
    private String customName;

    // Patcher Compatibility
    private static Class<?> patcherConfig;
    private boolean patcherInventoryPosition;

    // Vanilla Enhancements Compatibility
    private static Class<?> vanillaEnConfig;
    private boolean vanillaEnFixInventory;

    public GuiButtonItem(int buttonID, int xPos, int yPos, Item item)
    {
        this(buttonID, xPos, yPos, xPos, item, true, item.getItemStackDisplayName(new ItemStack(item)));
    }

    public GuiButtonItem(int buttonID, int xPos, int yPos, int potionX, Item item)
    {
        this(buttonID, xPos, yPos, potionX, item, true, item.getItemStackDisplayName(new ItemStack(item)));
    }

    public GuiButtonItem(int buttonID, int xPos, int yPos, Item item, boolean condition)
    {
        this(buttonID, xPos, yPos, xPos, item, condition, item.getItemStackDisplayName(new ItemStack(item)));
    }

    public GuiButtonItem(int buttonID, int xPos, int yPos, Item item, String customName)
    {
        this(buttonID, xPos, yPos, xPos, item, true, customName);
    }

    public GuiButtonItem(int buttonID, int xPos, int yPos, int potionX, Item item, boolean condition, String customName)
    {
        super(buttonID, xPos, yPos, 18, 18, "");
        this.originalX = xPos;
        this.potionX = potionX;
        this.item = item;
        this.mc = Minecraft.getMinecraft();
        this.visible = condition;
        this.customName = customName;

        if (SkyBlockcatiaMod.isPatcherLoaded)
        {
            try
            {
                patcherConfig = Class.forName("club.sk1er.patcher.config.PatcherConfig");
                Field inventoryPosition = patcherConfig.getDeclaredField("inventoryPosition");
                this.patcherInventoryPosition = inventoryPosition.getBoolean(patcherConfig);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (SkyBlockcatiaMod.isVanillaEnhancementsLoaded)
        {
            try
            {
                vanillaEnConfig = Class.forName("com.orangemarshall.enhancements.config.Config");
                Object instance = vanillaEnConfig.getDeclaredMethod("instance").invoke(vanillaEnConfig);
                Field fixInventory = instance.getClass().getDeclaredField("fixInventory");
                this.vanillaEnFixInventory = fixInventory.getBoolean(instance);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (!(this.vanillaEnFixInventory || this.patcherInventoryPosition))
        {
            boolean hasVisibleEffect = false;

            for (PotionEffect potioneffect : this.mc.thePlayer.getActivePotionEffects())
            {
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];

                if (potion.shouldRender(potioneffect))
                {
                    hasVisibleEffect = true;
                    break;
                }
            }

            if (!this.mc.thePlayer.getActivePotionEffects().isEmpty() && hasVisibleEffect)
            {
                this.xPosition = this.potionX;
            }
            else
            {
                this.xPosition = this.originalX;
            }
        }

        boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        if (this.visible)
        {
            ItemStack itemStack = new ItemStack(this.item);

            if (this.item == Items.nether_star)
            {
                ItemStack skyBlockMenu = itemStack.copy();
                NBTTagList list = new NBTTagList();
                skyBlockMenu.setStackDisplayName("SkyBlock Menu");
                list.appendTag(new NBTTagString(EnumChatFormatting.GRAY + "View all of your SkyBlock"));
                skyBlockMenu.getTagCompound().getCompoundTag("display").setTag("Lore", list);
                itemStack = skyBlockMenu;
            }

            mc.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Gui.drawModalRectWithCustomSizedTexture(this.xPosition, this.yPosition, flag ? 18 : 0, 0, this.width, this.height, 36, 18);

            GlStateManager.enableDepth();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, this.xPosition + 1, this.yPosition + 1);
            RenderHelper.enableGUIStandardItemLighting();
        }
    }

    public String getName()
    {
        return this.customName;
    }

    public void setName(String name)
    {
        this.customName = name;
    }
}