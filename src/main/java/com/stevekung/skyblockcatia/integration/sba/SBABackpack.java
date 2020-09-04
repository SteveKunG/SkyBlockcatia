package com.stevekung.skyblockcatia.integration.sba;

import java.lang.reflect.Method;

import com.stevekung.skyblockcatia.gui.api.GuiSkyBlockData;

import codes.biscuit.skyblockaddons.SkyblockAddons;
import codes.biscuit.skyblockaddons.asm.hooks.GuiContainerHook;
import codes.biscuit.skyblockaddons.asm.hooks.GuiScreenHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SBABackpack
{
    public static final SBABackpack INSTANCE = new SBABackpack();
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private static int mouseXFreeze;
    private static int mouseYFreeze;

    public void drawBackpacks(GuiSkyBlockData gui, int mouseX, int mouseY, float partialTicks)
    {
        try
        {
            Minecraft mc = Minecraft.getMinecraft();
            Class<?> skyblockAddons = Class.forName("codes.biscuit.skyblockaddons.SkyblockAddons");
            Class<?> feature = Class.forName("codes.biscuit.skyblockaddons.core.Feature");
            Object getInstance = skyblockAddons.getDeclaredMethod("getInstance").invoke(skyblockAddons);
            Object getUtils = getInstance.getClass().getDeclaredMethod("getUtils").invoke(getInstance);
            Object getConfigValues = getInstance.getClass().getDeclaredMethod("getConfigValues").invoke(getInstance);
            Object getBackpackToPreview = getUtils.getClass().getDeclaredMethod("getBackpackToPreview").invoke(getUtils);
            Method featureValues = feature.getDeclaredMethod("values");
            Object colorBackpack = null;

            for (Object obj : (Object[])featureValues.invoke(null))
            {
                try
                {
                    if (obj.toString().equals("MAKE_BACKPACK_INVENTORIES_COLORED"))
                    {
                        colorBackpack = obj;
                        break;
                    }
                }
                catch (Exception e) {}
            }

            if (getBackpackToPreview != null)
            {
                Class<?> backpackClass = getBackpackToPreview.getClass();
                int x = (int)backpackClass.getDeclaredMethod("getX").invoke(getBackpackToPreview);
                int y = (int)backpackClass.getDeclaredMethod("getY").invoke(getBackpackToPreview);
                ItemStack[] items = (ItemStack[])backpackClass.getDeclaredMethod("getItems").invoke(getBackpackToPreview);
                int length = items.length;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                if (!this.isFreezeBackpack())
                {
                    SBABackpack.mouseXFreeze = mouseX;
                    SBABackpack.mouseYFreeze = mouseY;
                }

                if (getConfigValues.getClass().getDeclaredMethod("getBackpackStyle").invoke(getConfigValues).toString().equals("GUI"))
                {
                    mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
                    int rows = length/9;
                    GlStateManager.disableLighting();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0,0,300);
                    int textColor = 4210752;

                    if (colorBackpack != null && (boolean)getConfigValues.getClass().getDeclaredMethod("isEnabled", feature).invoke(getConfigValues, colorBackpack))
                    {
                        Object backpackColor = backpackClass.getDeclaredMethod("getBackpackColor").invoke(getBackpackToPreview);
                        Class<?> backpackColorClass = backpackColor.getClass();
                        GlStateManager.color((float)backpackColorClass.getDeclaredMethod("getR").invoke(backpackColor), (float)backpackColorClass.getDeclaredMethod("getG").invoke(backpackColor), (float)backpackColorClass.getDeclaredMethod("getB").invoke(backpackColor), 1);
                        textColor = (int)backpackColorClass.getDeclaredMethod("getInventoryTextColor").invoke(backpackColor);
                    }

                    int screenHeight = gui.height;
                    int tooltipX = SBABackpack.mouseXFreeze;
                    int tooltipTextWidth = 176;

                    if (tooltipX + tooltipTextWidth > gui.width)
                    {
                        tooltipX = SBABackpack.mouseXFreeze - 16 - tooltipTextWidth;
                    }

                    int tooltipHeight = length / 9 * 18;
                    int tooltipY = SBABackpack.mouseYFreeze;

                    if (tooltipY + tooltipHeight + 24 > screenHeight)
                    {
                        tooltipY = screenHeight - tooltipHeight - 24;
                    }

                    x = tooltipX;
                    y = tooltipY;

                    gui.drawTexturedModalRect(x, y, 0, 0, 176, rows * 18 + 17);
                    gui.drawTexturedModalRect(x, y + rows * 18 + 17, 0, 215, 176, 7);
                    mc.fontRendererObj.drawString((String)backpackClass.getDeclaredMethod("getBackpackName").invoke(getBackpackToPreview), x+8, y+6, textColor);
                    GlStateManager.popMatrix();
                    GlStateManager.enableLighting();
                    RenderHelper.enableGUIStandardItemLighting();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableRescaleNormal();

                    ItemStack toRenderOverlay = null;

                    for (int i = 0; i < length; i++)
                    {
                        ItemStack item = items[i];

                        if (item != null)
                        {
                            int itemX = x+8 + i % 9 * 18;
                            int itemY = y+18 + i / 9 * 18;
                            RenderItem renderItem = mc.getRenderItem();
                            gui.zLevel = 200;
                            renderItem.zLevel = 200;
                            renderItem.renderItemAndEffectIntoGUI(item, itemX, itemY);
                            renderItem.renderItemOverlayIntoGUI(mc.fontRendererObj, item, itemX, itemY, null);

                            if (this.isFreezeBackpack() && mouseX > itemX && mouseX < itemX + 16 && mouseY > itemY && mouseY < itemY + 16)
                            {
                                toRenderOverlay = item;
                            }

                            gui.zLevel = 0;
                            renderItem.zLevel = 0;
                        }
                    }
                    if (toRenderOverlay != null)
                    {
                        gui.drawHoveringText(toRenderOverlay.getTooltip(null, mc.gameSettings.advancedItemTooltips), mouseX, mouseY);
                    }
                }
                else
                {
                    GlStateManager.disableLighting();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0,0, 300);
                    Gui.drawRect(x, y, x + 16 * 9 + 3, y + 16 * (length / 9) + 3, -95070891);
                    GlStateManager.popMatrix();
                    GlStateManager.enableLighting();
                    RenderHelper.enableGUIStandardItemLighting();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableRescaleNormal();

                    for (int i = 0; i < length; i++)
                    {
                        ItemStack item = items[i];

                        if (item != null)
                        {
                            int itemX = x + i % 9 * 16;
                            int itemY = y + i / 9 * 16;
                            RenderItem renderItem = mc.getRenderItem();
                            gui.zLevel = 200;
                            renderItem.zLevel = 200;
                            renderItem.renderItemAndEffectIntoGUI(item, itemX, itemY);
                            renderItem.renderItemOverlayIntoGUI(mc.fontRendererObj, item, itemX, itemY, null);
                            gui.zLevel = 0;
                            renderItem.zLevel = 0;
                        }
                    }
                }
                if (!this.isFreezeBackpack())
                {
                    Method setBackpack = getUtils.getClass().getDeclaredMethod("setBackpackToPreview", backpackClass);
                    setBackpack.invoke(getUtils, new Object[] { null });
                }
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                RenderHelper.enableStandardItemLighting();
            }
        }
        catch (Exception e) {}
    }

    public void keyTyped(int keyCode)
    {
        try
        {
            long lastBackpackFreezeKey = 0L;
            SkyblockAddons main = SkyblockAddons.getInstance();
            Method lastBackpackFreezeKeyMethod = GuiScreenHook.class.getDeclaredMethod("getLastBackpackFreezeKey");
            Method setLastBackpackFreezeKeyMethod = GuiScreenHook.class.getDeclaredMethod("setLastBackpackFreezeKey", long.class);
            lastBackpackFreezeKeyMethod.setAccessible(true);
            setLastBackpackFreezeKeyMethod.setAccessible(true);
            lastBackpackFreezeKey = (long)lastBackpackFreezeKeyMethod.invoke(null);

            if (keyCode == 1 || keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode())
            {
                GuiContainerHook.setFreezeBackpack(false);
                Class<?> skyblockAddons = Class.forName("codes.biscuit.skyblockaddons.SkyblockAddons");
                Object getInstance = skyblockAddons.getDeclaredMethod("getInstance").invoke(skyblockAddons);
                Object getUtils = getInstance.getClass().getDeclaredMethod("getUtils").invoke(getInstance);
                Object getBackpackToPreview = getUtils.getClass().getDeclaredMethod("getBackpackToPreview").invoke(getUtils);

                if (getBackpackToPreview != null)
                {
                    Method setBackpack = getUtils.getClass().getDeclaredMethod("setBackpackToPreview", getBackpackToPreview.getClass());
                    setBackpack.invoke(getUtils, new Object[] { null });
                }
                this.clear();
            }
            if (keyCode == main.getFreezeBackpackKey().getKeyCode() && this.isFreezeBackpack() && System.currentTimeMillis() - lastBackpackFreezeKey > 500)
            {
                setLastBackpackFreezeKeyMethod.invoke(null, System.currentTimeMillis());
                GuiContainerHook.setFreezeBackpack(false);
                this.clear();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void clearRenderBackpack()
    {
        try
        {
            Method setLastBackpackFreezeKeyMethod = GuiScreenHook.class.getDeclaredMethod("setLastBackpackFreezeKey", long.class);
            setLastBackpackFreezeKeyMethod.setAccessible(true);
            setLastBackpackFreezeKeyMethod.invoke(null, System.currentTimeMillis());
            GuiContainerHook.setFreezeBackpack(false);
            Class<?> skyblockAddons = Class.forName("codes.biscuit.skyblockaddons.SkyblockAddons");
            Object getInstance = skyblockAddons.getDeclaredMethod("getInstance").invoke(skyblockAddons);
            Object getUtils = getInstance.getClass().getDeclaredMethod("getUtils").invoke(getInstance);
            Object getBackpackToPreview = getUtils.getClass().getDeclaredMethod("getBackpackToPreview").invoke(getUtils);

            if (getBackpackToPreview != null)
            {
                Method setBackpack = getUtils.getClass().getDeclaredMethod("setBackpackToPreview", getBackpackToPreview.getClass());
                setBackpack.invoke(getUtils, new Object[] { null });
            }
            this.clear();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isFreezeBackpack()
    {
        return GuiContainerHook.isFreezeBackpack();
    }

    private void clear()
    {
        SBABackpack.mouseXFreeze = 0;
        SBABackpack.mouseYFreeze = 0;
    }
}