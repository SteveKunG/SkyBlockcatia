package com.stevekung.skyblockcatia.integration.sba;

import java.lang.reflect.Method;

import com.stevekung.skyblockcatia.gui.screen.SkyBlockAPIViewerScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SBABackpackV1 implements IBackpackRenderer
{
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    @Override
    public void drawBackpacks(SkyBlockAPIViewerScreen gui, int mouseX, int mouseY, float partialTicks)
    {
        try
        {
            Minecraft mc = Minecraft.getMinecraft();
            Class<?> skyblockAddons = Class.forName("codes.biscuit.skyblockaddons.SkyblockAddons");
            Class<?> feature = Class.forName("codes.biscuit.skyblockaddons.core.Feature");
            Object getInstance = skyblockAddons.getDeclaredMethod("getInstance").invoke(skyblockAddons);
            Object getUtils = getInstance.getClass().getDeclaredMethod("getUtils").invoke(getInstance);
            Object getConfigValues = getInstance.getClass().getDeclaredMethod("getConfigValues").invoke(getInstance);
            Object getBackpackToPreview = null;

            try
            {
                getBackpackToPreview = getUtils.getClass().getDeclaredMethod("getBackpackToPreview").invoke(getUtils);
            }
            catch (Exception e)
            {
                getBackpackToPreview = getUtils.getClass().getDeclaredMethod("getContainerPreviewToRender").invoke(getUtils);
            }

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
                int screenHeight = gui.height;
                ItemStack tooltipItem = null;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

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

                        if (backpackColor != null)
                        {
                            Class<?> backpackColorClass = backpackColor.getClass();
                            GlStateManager.color((float)backpackColorClass.getDeclaredMethod("getR").invoke(backpackColor), (float)backpackColorClass.getDeclaredMethod("getG").invoke(backpackColor), (float)backpackColorClass.getDeclaredMethod("getB").invoke(backpackColor), 1);
                            textColor = (int)backpackColorClass.getDeclaredMethod("getInventoryTextColor").invoke(backpackColor);
                        }
                    }

                    int totalWidth  = 176;

                    if (x + totalWidth > gui.width)
                    {
                        x -= totalWidth;
                    }

                    int totalHeight = rows * 18 + 24;

                    if (y + totalHeight > screenHeight)
                    {
                        y = screenHeight - totalHeight;
                    }

                    gui.drawTexturedModalRect(x, y, 0, 0, 176, rows * 18 + 17);
                    gui.drawTexturedModalRect(x, y + rows * 18 + 17, 0, 215, 176, 7);

                    Method containerName = null;

                    try
                    {
                        containerName = backpackClass.getDeclaredMethod("getBackpackName");
                    }
                    catch (Exception e)
                    {
                        containerName = backpackClass.getDeclaredMethod("getName");
                    }

                    mc.fontRendererObj.drawString((String)containerName.invoke(getBackpackToPreview), x+8, y+6, textColor);
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
                            int itemX = x+8 + i % 9 * 18;
                            int itemY = y+18 + i / 9 * 18;
                            RenderItem renderItem = mc.getRenderItem();
                            gui.zLevel = 200;
                            renderItem.zLevel = 200;
                            renderItem.renderItemAndEffectIntoGUI(item, itemX, itemY);
                            renderItem.renderItemOverlayIntoGUI(mc.fontRendererObj, item, itemX, itemY, null);
                            gui.zLevel = 0;
                            renderItem.zLevel = 0;

                            if (this.isFreezeBackpack() && mouseX > itemX && mouseX < itemX + 16 && mouseY > itemY && mouseY < itemY + 16)
                            {
                                tooltipItem = item;
                            }
                        }
                    }
                }
                else
                {
                    int totalWidth = 16 * 9 + 3;

                    if (x + totalWidth > gui.width)
                    {
                        x -= totalWidth;
                    }

                    int totalHeight = 16 * (length / 9) + 3;

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
                            int itemX = x + i % 9 * 16;
                            int itemY = y + i / 9 * 16;
                            RenderItem renderItem = mc.getRenderItem();
                            gui.zLevel = 200;
                            renderItem.zLevel = 200;
                            renderItem.renderItemAndEffectIntoGUI(item, itemX, itemY);
                            renderItem.renderItemOverlayIntoGUI(mc.fontRendererObj, item, itemX, itemY, null);
                            gui.zLevel = 0;
                            renderItem.zLevel = 0;

                            if (this.isFreezeBackpack() && mouseX > itemX && mouseX < itemX + 16 && mouseY > itemY && mouseY < itemY + 16)
                            {
                                tooltipItem = item;
                            }
                        }
                    }
                }

                if (tooltipItem != null)
                {
                    gui.drawHoveringText(tooltipItem.getTooltip(null, mc.gameSettings.advancedItemTooltips), mouseX, mouseY);
                }
                if (!this.isFreezeBackpack())
                {
                    Method setBackpack = null;

                    try
                    {
                        setBackpack = getUtils.getClass().getDeclaredMethod("setBackpackToPreview", backpackClass);
                    }
                    catch (Exception e)
                    {
                        setBackpack = getUtils.getClass().getDeclaredMethod("setContainerPreviewToRender", backpackClass);
                    }
                    setBackpack.invoke(getUtils, new Object[] { null });
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
            long lastBackpackFreezeKey = 0L;
            Class<?> skyblockAddons = Class.forName("codes.biscuit.skyblockaddons.SkyblockAddons");
            Object getInstance = skyblockAddons.getDeclaredMethod("getInstance").invoke(skyblockAddons);
            Object getFreezeBackpackKeyObj = getInstance.getClass().getDeclaredMethod("getFreezeBackpackKey").invoke(getInstance);
            Method lastBackpackFreezeKeyMethod = Class.forName("codes.biscuit.skyblockaddons.asm.hooks.GuiScreenHook").getDeclaredMethod("getLastBackpackFreezeKey");
            Method setLastBackpackFreezeKeyMethod = Class.forName("codes.biscuit.skyblockaddons.asm.hooks.GuiScreenHook").getDeclaredMethod("setLastBackpackFreezeKey", long.class);
            lastBackpackFreezeKeyMethod.setAccessible(true);
            setLastBackpackFreezeKeyMethod.setAccessible(true);
            lastBackpackFreezeKey = (long)lastBackpackFreezeKeyMethod.invoke(null);

            if (keyCode == 1 || keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode())
            {
                this.setFreezeBackpack();
                Object getUtils = getInstance.getClass().getDeclaredMethod("getUtils").invoke(getInstance);
                this.clear(getUtils);
            }

            int freezeBackpackKey = (int)getFreezeBackpackKeyObj.getClass().getDeclaredMethod("getKeyCode").invoke(getFreezeBackpackKeyObj);

            if (keyCode == freezeBackpackKey && this.isFreezeBackpack() && System.currentTimeMillis() - lastBackpackFreezeKey > 500)
            {
                setLastBackpackFreezeKeyMethod.invoke(null, System.currentTimeMillis());
                this.setFreezeBackpack();
            }
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
            Method setLastBackpackFreezeKeyMethod = Class.forName("codes.biscuit.skyblockaddons.asm.hooks.GuiScreenHook").getDeclaredMethod("setLastBackpackFreezeKey", long.class);
            setLastBackpackFreezeKeyMethod.setAccessible(true);
            setLastBackpackFreezeKeyMethod.invoke(null, System.currentTimeMillis());
            this.setFreezeBackpack();
            Class<?> skyblockAddons = Class.forName("codes.biscuit.skyblockaddons.SkyblockAddons");
            Object getInstance = skyblockAddons.getDeclaredMethod("getInstance").invoke(skyblockAddons);
            Object getUtils = getInstance.getClass().getDeclaredMethod("getUtils").invoke(getInstance);
            this.clear(getUtils);
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
            Object obj = Class.forName("codes.biscuit.skyblockaddons.asm.hooks.GuiContainerHook").getDeclaredMethod("isFreezeBackpack").invoke(null);
            return (boolean)obj;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private void setFreezeBackpack() throws Exception
    {
        Method setFreezeBackpackM = Class.forName("codes.biscuit.skyblockaddons.asm.hooks.GuiContainerHook").getDeclaredMethod("setFreezeBackpack", boolean.class);
        setFreezeBackpackM.invoke(null, false);
    }

    private void clear(Object getUtils) throws Exception
    {
        Object getBackpackToPreview = null;

        try
        {
            getBackpackToPreview = getUtils.getClass().getDeclaredMethod("getBackpackToPreview").invoke(getUtils);
        }
        catch (Exception e)
        {
            getBackpackToPreview = getUtils.getClass().getDeclaredMethod("getContainerPreviewToRender").invoke(getUtils);
        }

        if (getBackpackToPreview != null)
        {
            Method setBackpack = null;

            try
            {
                setBackpack = getUtils.getClass().getDeclaredMethod("setBackpackToPreview", getBackpackToPreview.getClass());
            }
            catch (Exception e)
            {
                setBackpack = getUtils.getClass().getDeclaredMethod("setContainerPreviewToRender", getBackpackToPreview.getClass());
            }
            setBackpack.invoke(getUtils, new Object[] { null });
        }
    }
}