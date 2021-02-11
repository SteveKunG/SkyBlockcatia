package com.stevekung.skyblockcatia.integration.sba;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.stevekung.skyblockcatia.gui.screen.GuiSkyBlockAPIViewer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SBABackpackV2 implements IBackpackRenderer
{
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    @Override
    public void drawBackpacks(GuiSkyBlockAPIViewer gui, int mouseX, int mouseY, float partialTicks)
    {
        try
        {
            Class<?> cpmClass = Class.forName("codes.biscuit.skyblockaddons.features.backpacks.ContainerPreviewManager");
            Field containerPreview = cpmClass.getDeclaredField("currentContainerPreview");
            Field drawingFrozenItemTooltip = cpmClass.getDeclaredField("drawingFrozenItemTooltip");
            containerPreview.setAccessible(true);
            drawingFrozenItemTooltip.setAccessible(true);
            Object containerPreviewObj = containerPreview.get(null);
            Minecraft mc = Minecraft.getMinecraft();
            Class<?> skyblockAddons = Class.forName("codes.biscuit.skyblockaddons.SkyblockAddons");
            Class<?> feature = Class.forName("codes.biscuit.skyblockaddons.core.Feature");
            Object getInstance = skyblockAddons.getDeclaredMethod("getInstance").invoke(skyblockAddons);
            Object getConfigValues = getInstance.getClass().getDeclaredMethod("getConfigValues").invoke(getInstance);
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

            if (containerPreviewObj != null)
            {
                Class<?> backpackClass = containerPreviewObj.getClass();
                int x = (int)backpackClass.getDeclaredMethod("getX").invoke(containerPreviewObj);
                int y = (int)backpackClass.getDeclaredMethod("getY").invoke(containerPreviewObj);
                ItemStack[] items = (ItemStack[])backpackClass.getDeclaredMethod("getItems").invoke(containerPreviewObj);
                int length = items.length;
                int screenHeight = gui.height;
                int rows = (int)backpackClass.getDeclaredMethod("getNumRows").invoke(containerPreviewObj);
                int cols = (int)backpackClass.getDeclaredMethod("getNumCols").invoke(containerPreviewObj);
                String containerName = (String)backpackClass.getDeclaredMethod("getName").invoke(containerPreviewObj);
                ItemStack tooltipItem = null;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                if (getConfigValues.getClass().getDeclaredMethod("getBackpackStyle").invoke(getConfigValues).toString().equals("GUI"))
                {
                    mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
                    GlStateManager.disableLighting();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0,0,300);
                    int textColor = 4210752;

                    if (colorBackpack != null && (boolean)getConfigValues.getClass().getDeclaredMethod("isEnabled", feature).invoke(getConfigValues, colorBackpack))
                    {
                        Object backpackColor = backpackClass.getDeclaredMethod("getBackpackColor").invoke(containerPreviewObj);

                        if (backpackColor != null)
                        {
                            Class<?> backpackColorClass = backpackColor.getClass();
                            GlStateManager.color((float)backpackColorClass.getDeclaredMethod("getR").invoke(backpackColor), (float)backpackColorClass.getDeclaredMethod("getG").invoke(backpackColor), (float)backpackColorClass.getDeclaredMethod("getB").invoke(backpackColor), 1);
                            textColor = (int)backpackColorClass.getDeclaredMethod("getInventoryTextColor").invoke(backpackColor);
                        }
                    }

                    int topBorder = containerName == null ? 7 : 17;
                    int totalWidth = cols * 18 + 14;
                    int totalHeight = rows * 18 + topBorder + 7;
                    int squaresEndWidth = totalWidth - 7;
                    int squaresEndHeight = totalHeight - 7;

                    if (x + totalWidth > gui.width)
                    {
                        x -= totalWidth;
                    }

                    if (y + totalHeight > screenHeight)
                    {
                        y = screenHeight - totalHeight;
                    }

                    if (containerName == null)
                    {
                        gui.drawTexturedModalRect(x, y, 0, 0, squaresEndWidth, topBorder);
                        gui.drawTexturedModalRect(x, y + topBorder, 0, 17, squaresEndWidth, squaresEndHeight - topBorder);
                    }
                    else
                    {
                        gui.drawTexturedModalRect(x, y, 0, 0, squaresEndWidth, squaresEndHeight);
                    }

                    gui.drawTexturedModalRect(x, y + squaresEndHeight, 0, 215, squaresEndWidth, 7);
                    gui.drawTexturedModalRect(x + squaresEndWidth, y, 169, 0, 7, squaresEndHeight);
                    gui.drawTexturedModalRect(x + squaresEndWidth, y + squaresEndHeight, 169, 215, 7, 7);

                    if (containerName != null)
                    {
                        mc.fontRendererObj.drawString(containerName, x+8, y+6, textColor);
                    }

                    GlStateManager.popMatrix();
                    GlStateManager.enableLighting();
                    RenderHelper.enableGUIStandardItemLighting();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableRescaleNormal();

                    int itemStartX = x + 8;
                    int itemStartY = y + topBorder + 1;

                    for (int i = 0; i < length; i++)
                    {
                        ItemStack item = items[i];

                        if (item != null)
                        {
                            int itemX = itemStartX + i % cols * 18;
                            int itemY = itemStartY + i / cols * 18;
                            RenderItem renderItem = mc.getRenderItem();
                            gui.zLevel = 200.0F;
                            renderItem.zLevel = 200.0F;
                            renderItem.renderItemAndEffectIntoGUI(item, itemX, itemY);
                            renderItem.renderItemOverlayIntoGUI(mc.fontRendererObj, item, itemX, itemY, null);
                            gui.zLevel = 0.0F;
                            renderItem.zLevel = 0.0F;

                            if (this.isFreezeBackpack() && mouseX > itemX && mouseX < itemX + 16 && mouseY > itemY && mouseY < itemY + 16)
                            {
                                tooltipItem = item;
                            }
                        }
                    }
                }
                else
                {
                    int totalWidth = 16 * cols + 3;

                    if (x + totalWidth > gui.width)
                    {
                        x -= totalWidth;
                    }

                    int totalHeight = 16 * rows + 3;

                    if (y + totalHeight > screenHeight)
                    {
                        y = screenHeight - totalHeight;
                    }

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
                            int itemX = x + i % cols * 16;
                            int itemY = y + i / cols * 16;
                            RenderItem renderItem = mc.getRenderItem();
                            gui.zLevel = 200.0F;
                            renderItem.zLevel = 200.0F;
                            renderItem.renderItemAndEffectIntoGUI(item, itemX, itemY);
                            renderItem.renderItemOverlayIntoGUI(mc.fontRendererObj, item, itemX, itemY, null);
                            gui.zLevel = 0.0F;
                            renderItem.zLevel = 0.0F;

                            if (this.isFreezeBackpack() && mouseX > itemX && mouseX < itemX + 16 && mouseY > itemY && mouseY < itemY + 16)
                            {
                                tooltipItem = item;
                            }
                        }
                    }
                }

                if (tooltipItem != null)
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0.0F, 0.0F, 302.0F);
                    drawingFrozenItemTooltip.set(null, true);
                    gui.drawHoveringText(tooltipItem.getTooltip(null, mc.gameSettings.advancedItemTooltips), mouseX, mouseY);
                    drawingFrozenItemTooltip.set(null, false);
                    GlStateManager.popMatrix();
                }
                if (!this.isFreezeBackpack())
                {
                    containerPreview.set(null, null);
                }
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                RenderHelper.enableStandardItemLighting();
            }
        }
        catch (Exception e) {}
    }

    @Override
    public void keyTyped(int keyCode)
    {
        try
        {
            Class<?> cpmClass = Class.forName("codes.biscuit.skyblockaddons.features.backpacks.ContainerPreviewManager");
            Method met = cpmClass.getDeclaredMethod("onContainerKeyTyped", int.class);
            met.invoke(null, keyCode);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void clearRenderBackpack()
    {
        try
        {
            Class<?> cpmClass = Class.forName("codes.biscuit.skyblockaddons.features.backpacks.ContainerPreviewManager");
            Field lastToggleFreezeTime = cpmClass.getDeclaredField("lastToggleFreezeTime");
            Field frozen = cpmClass.getDeclaredField("frozen");
            lastToggleFreezeTime.setAccessible(true);
            frozen.setAccessible(true);

            lastToggleFreezeTime.set(null, System.currentTimeMillis());
            frozen.set(null, false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isFreezeBackpack()
    {
        try
        {
            Object obj = Class.forName("codes.biscuit.skyblockaddons.features.backpacks.ContainerPreviewManager").getDeclaredMethod("isFrozen").invoke(null);
            return (boolean)obj;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}