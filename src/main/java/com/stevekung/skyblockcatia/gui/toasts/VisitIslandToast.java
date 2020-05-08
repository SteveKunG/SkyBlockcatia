//package com.stevekung.skyblockcatia.gui.toasts;
//
//import java.util.UUID;
//
//import com.mojang.authlib.GameProfile;
//import com.stevekung.skyblockcatia.renderer.EquipmentOverlay;
//import com.stevekung.skyblockcatia.utils.ColorUtils;
//import com.stevekung.skyblockcatia.utils.JsonUtils;
//
//import net.minecraft.client.gui.Gui;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.init.Items;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.nbt.NBTUtil;
//import net.minecraft.tileentity.TileEntitySkull;
//import net.minecraft.util.ResourceLocation;
//
//public class VisitIslandToast implements IToast TODO
//{
//    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/visit_island_toasts.png");
//    private final ItemStack itemStack;
//    private final String name;
//
//    public VisitIslandToast(String name, UUID uuid)
//    {
//        this.itemStack = VisitIslandToast.getPlayerHead(uuid, name);
//        this.name = name;
//    }
//
//    @Override
//    public IToast.Visibility draw(GuiToast toastGui, long delta)
//    {
//        toastGui.mc.getTextureManager().bindTexture(TEXTURE);
//        GlStateManager.color(1.0F, 1.0F, 1.0F);
//        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 160, 32, 160, 32);
//        toastGui.mc.fontRendererObj.drawString(ColorUtils.stringToRGB("255,255,85").toColoredFont() + JsonUtils.create(this.name).setChatStyle(JsonUtils.style().setBold(true)).getFormattedText(), 30, 7, 16777215);
//        toastGui.mc.fontRendererObj.drawString("is visiting Your Island!", 30, 18, ColorUtils.rgbToDecimal(255, 255, 255));
//        EquipmentOverlay.renderItem(this.itemStack, 8, 8);
//        return delta >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
//    }
//
//    private static ItemStack getPlayerHead(UUID uuid, String name)
//    {
//        ItemStack itemStack = new ItemStack(Items.skull, 1, 3);
//        NBTTagCompound compound = new NBTTagCompound();
//        GameProfile profile = TileEntitySkull.updateGameprofile(new GameProfile(uuid, name));
//        compound.removeTag("SkullOwner");
//        compound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), profile));
//        itemStack.setTagCompound(compound);
//        return itemStack;
//    }
//}