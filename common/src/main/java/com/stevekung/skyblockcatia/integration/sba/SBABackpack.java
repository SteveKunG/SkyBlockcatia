package com.stevekung.skyblockcatia.integration.sba;
//TODO Update from latest changes
//package com.stevekung.skyblockcatia.integration;
//
//import java.lang.reflect.Method;
//
//import org.apache.commons.lang3.EnumUtils;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.realmsclient.gui.ChatFormatting;
//import com.stevekung.indicatia.config.ExtendedConfig;
//import com.stevekung.skyblockcatia.gui.api.GuiSkyBlockData;
//import com.stevekung.stevekungslib.utils.client.RenderUtils;
//
//import codes.biscuit.skyblockaddons.SkyblockAddons;
//import codes.biscuit.skyblockaddons.asm.hooks.GuiContainerHook;
//import codes.biscuit.skyblockaddons.asm.hooks.GuiScreenHook;
//import codes.biscuit.skyblockaddons.utils.Backpack;
//import codes.biscuit.skyblockaddons.utils.BackpackColor;
//import codes.biscuit.skyblockaddons.utils.Feature;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.Gui;
//import net.minecraft.client.renderer.RenderHelper;
//import net.minecraft.client.renderer.entity.RenderItem;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.ResourceLocation;
//
//public class SkyBlockAddonsBackpack
//{
//    public static final SkyBlockAddonsBackpack INSTANCE = new SkyBlockAddonsBackpack();
//    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
//    public static int mouseXFreeze;
//    public static int mouseYFreeze;
//
//    public void drawBackpacks(GuiSkyBlockData gui, int mouseX, int mouseY, float partialTicks)
//    {
//        SkyblockAddons main = SkyblockAddons.getInstance();
//        Backpack backpack = main.getUtils().getBackpackToRender();
//        Minecraft mc = Minecraft.getMinecraft();
//
//        if (backpack != null)
//        {
//            int x = backpack.getX();
//            int y = backpack.getY();
//            ItemStack[] items = backpack.getItems();
//            int length = items.length;
//            RenderSystem.color(1.0F, 1.0F, 1.0F, 1.0F);
//
//            if (!GuiContainerHook.isFreezeBackpack())
//            {
//                SkyBlockAddonsBackpack.mouseXFreeze = mouseX;
//                SkyBlockAddonsBackpack.mouseYFreeze = mouseY;
//            }
//
//            if (main.getConfigValues().getBackpackStyle() == EnumUtils.BackpackStyle.GUI)
//            {
//                mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
//                int rows = length/9;
//                RenderSystem.disableLighting();
//                RenderSystem.pushMatrix();
//                RenderSystem.translate(0,0,300);
//                int textColor = 4210752;
//
//                if (main.getConfigValues().isEnabled(Feature.MAKE_BACKPACK_INVENTORIES_COLORED))
//                {
//                    BackpackColor color = backpack.getBackpackColor();
//                    RenderSystem.color(color.getR(), color.getG(), color.getB(), 1);
//                    textColor = color.getInventoryTextColor();
//                }
//
//                int tooltipX = SkyBlockAddonsBackpack.mouseXFreeze;
//                int tooltipTextWidth = 176;
//
//                if (tooltipX + tooltipTextWidth > gui.width)
//                {
//                    tooltipX = SkyBlockAddonsBackpack.mouseXFreeze + 150 - x;
//
//                    if (tooltipX > 340) // if the backpack doesn't fit on the screen
//                    {
//                        if (SkyBlockAddonsBackpack.mouseXFreeze > gui.width / 2)
//                        {
//                            tooltipX = SkyBlockAddonsBackpack.mouseXFreeze - tooltipTextWidth;
//                        }
//                    }
//                }
//
//                int tooltipHeight = length / 9 * 18;
//                int tooltipY = SkyBlockAddonsBackpack.mouseYFreeze;
//
//                if (tooltipY + tooltipHeight + 24 > gui.height)
//                {
//                    tooltipY = gui.height - tooltipHeight - 24;
//                }
//
//                x = tooltipX;
//                y = tooltipY;
//
//                gui.drawTexturedModalRect(x, y, 0, 0, 176, rows * 18 + 17);
//                gui.drawTexturedModalRect(x, y + rows * 18 + 17, 0, 215, 176, 7);
//                mc.fontRendererObj.drawString(backpack.getBackpackName(), x+8, y+6, textColor);
//                RenderSystem.popMatrix();
//                RenderSystem.enableLighting();
//                RenderHelper.enableGUIStandardItemLighting();
//                RenderSystem.color(1.0F, 1.0F, 1.0F, 1.0F);
//                RenderSystem.enableRescaleNormal();
//
//                ItemStack toRenderOverlay = null;
//
//                for (int i = 0; i < length; i++)
//                {
//                    ItemStack item = items[i];
//
//                    if (item != null)
//                    {
//                        int itemX = x+8 + i % 9 * 18;
//                        int itemY = y+18 + i / 9 * 18;
//                        RenderItem renderItem = mc.getRenderItem();
//                        gui.zLevel = 200;
//                        renderItem.zLevel = 200;
//
//                        if (ExtendedConfig.instance.showItemRarity)
//                        {
//                            RenderUtils.drawRarity(item, itemX, itemY);
//                        }
//
//                        renderItem.renderItemAndEffectIntoGUI(item, itemX, itemY);
//                        renderItem.renderItemOverlayIntoGUI(mc.fontRendererObj, item, itemX, itemY, null);
//
//                        if (GuiContainerHook.isFreezeBackpack() && mouseX > itemX && mouseX < itemX + 16 && mouseY > itemY && mouseY < itemY + 16)
//                        {
//                            toRenderOverlay = item;
//                        }
//
//                        gui.zLevel = 0;
//                        renderItem.zLevel = 0;
//                    }
//                }
//                if (toRenderOverlay != null)
//                {
//                    gui.drawHoveringText(toRenderOverlay.getTooltip(null, mc.gameSettings.advancedItemTooltips), mouseX, mouseY);
//                }
//            }
//            else
//            {
//                RenderSystem.disableLighting();
//                RenderSystem.pushMatrix();
//                RenderSystem.translate(0,0, 300);
//                Gui.drawRect(x, y, x + 16 * 9 + 3, y + 16 * (length / 9) + 3, ChatFormatting.DARK_GRAY.getColor(250).getRGB());
//                RenderSystem.popMatrix();
//                RenderSystem.enableLighting();
//                RenderHelper.enableGUIStandardItemLighting();
//                RenderSystem.color(1.0F, 1.0F, 1.0F, 1.0F);
//                RenderSystem.enableRescaleNormal();
//
//                for (int i = 0; i < length; i++)
//                {
//                    ItemStack item = items[i];
//
//                    if (item != null)
//                    {
//                        int itemX = x + i % 9 * 16;
//                        int itemY = y + i / 9 * 16;
//                        RenderItem renderItem = mc.getRenderItem();
//                        gui.zLevel = 200;
//                        renderItem.zLevel = 200;
//                        renderItem.renderItemAndEffectIntoGUI(item, itemX, itemY);
//                        renderItem.renderItemOverlayIntoGUI(mc.fontRendererObj, item, itemX, itemY, null);
//                        gui.zLevel = 0;
//                        renderItem.zLevel = 0;
//                    }
//                }
//            }
//            if (!GuiContainerHook.isFreezeBackpack())
//            {
//                main.getUtils().setBackpackToRender(null);
//            }
//            RenderSystem.enableLighting();
//            RenderSystem.enableDepth();
//            RenderHelper.enableStandardItemLighting();
//        }
//    }
//
//    public void keyTyped(int keyCode)
//    {
//        try
//        {
//            long lastBackpackFreezeKey = 0L;
//            SkyblockAddons main = SkyblockAddons.getInstance();
//            Method lastBackpackFreezeKeyMethod = GuiScreenHook.class.getDeclaredMethod("getLastBackpackFreezeKey");
//            Method setLastBackpackFreezeKeyMethod = GuiScreenHook.class.getDeclaredMethod("setLastBackpackFreezeKey", long.class);
//            lastBackpackFreezeKeyMethod.setAccessible(true);
//            setLastBackpackFreezeKeyMethod.setAccessible(true);
//            lastBackpackFreezeKey = (long)lastBackpackFreezeKeyMethod.invoke(null);
//
//            if (keyCode == 1 || keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode())
//            {
//                GuiContainerHook.setFreezeBackpack(false);
//                main.getUtils().setBackpackToRender(null);
//                SkyBlockAddonsBackpack.mouseXFreeze = 0;
//                SkyBlockAddonsBackpack.mouseYFreeze = 0;
//            }
//            if (keyCode == main.getFreezeBackpackKey().getKeyCode() && GuiContainerHook.isFreezeBackpack() && System.currentTimeMillis() - lastBackpackFreezeKey > 500)
//            {
//                setLastBackpackFreezeKeyMethod.invoke(null, System.currentTimeMillis());
//                GuiContainerHook.setFreezeBackpack(false);
//                SkyBlockAddonsBackpack.mouseXFreeze = 0;
//                SkyBlockAddonsBackpack.mouseYFreeze = 0;
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    public void clearRenderBackpack()
//    {
//        try
//        {
//            SkyblockAddons main = SkyblockAddons.getInstance();
//            Method setLastBackpackFreezeKeyMethod = GuiScreenHook.class.getDeclaredMethod("setLastBackpackFreezeKey", long.class);
//            setLastBackpackFreezeKeyMethod.setAccessible(true);
//            setLastBackpackFreezeKeyMethod.invoke(null, System.currentTimeMillis());
//            GuiContainerHook.setFreezeBackpack(false);
//            main.getUtils().setBackpackToRender(null);
//            SkyBlockAddonsBackpack.mouseXFreeze = 0;
//            SkyBlockAddonsBackpack.mouseYFreeze = 0;
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    public boolean isFreezeBackpack()
//    {
//        return GuiContainerHook.isFreezeBackpack();
//    }
//}