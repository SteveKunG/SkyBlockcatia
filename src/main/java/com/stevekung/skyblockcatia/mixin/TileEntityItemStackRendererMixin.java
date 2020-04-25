package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.renderer.TileEntityEnchantedSkullRenderer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;

@Mixin(TileEntityItemStackRenderer.class)
public abstract class TileEntityItemStackRendererMixin
{
    @Shadow
    private TileEntityChest field_147717_b;

    @Shadow
    private TileEntityChest field_147718_c;

    @Shadow
    private TileEntityEnderChest enderChest;

    @Shadow
    private TileEntityBanner banner;

    @Shadow
    private TileEntitySkull skull;

    @Overwrite
    public void renderByItem(ItemStack itemStack)
    {
        if (itemStack.getItem() == Items.banner)
        {
            this.banner.setItemValues(itemStack);
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.banner, 0.0D, 0.0D, 0.0D, 0.0F);
        }
        else if (itemStack.getItem() == Items.skull)
        {
            GameProfile gameprofile = null;

            if (itemStack.hasTagCompound())
            {
                NBTTagCompound nbttagcompound = itemStack.getTagCompound();

                if (nbttagcompound.hasKey("SkullOwner", 10))
                {
                    gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                }
                else if (nbttagcompound.hasKey("SkullOwner", 8) && nbttagcompound.getString("SkullOwner").length() > 0)
                {
                    GameProfile lvt_2_2_ = new GameProfile(null, nbttagcompound.getString("SkullOwner"));
                    gameprofile = TileEntitySkull.updateGameprofile(lvt_2_2_);
                    nbttagcompound.removeTag("SkullOwner");
                    nbttagcompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
                }
            }

            if (TileEntitySkullRenderer.instance != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(-0.5F, 0.0F, -0.5F);
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                GlStateManager.disableCull();
                TileEntityEnchantedSkullRenderer.INSTANCE.renderSkull(0.0F, 0.0F, 0.0F, EnumFacing.UP, 0.0F, itemStack.getMetadata(), gameprofile, Minecraft.getMinecraft().timer.renderPartialTicks, itemStack.hasEffect(), null);
                GlStateManager.enableCull();
                GlStateManager.popMatrix();
            }
        }
        else
        {
            Block block = Block.getBlockFromItem(itemStack.getItem());

            if (block == Blocks.ender_chest)
            {
                TileEntityRendererDispatcher.instance.renderTileEntityAt(this.enderChest, 0.0D, 0.0D, 0.0D, 0.0F);
            }
            else if (block == Blocks.trapped_chest)
            {
                TileEntityRendererDispatcher.instance.renderTileEntityAt(this.field_147718_c, 0.0D, 0.0D, 0.0D, 0.0F);
            }
            else if (block != Blocks.chest)
            {
                ForgeHooksClient.renderTileItem(itemStack.getItem(), itemStack.getMetadata());
            }
            else
            {
                TileEntityRendererDispatcher.instance.renderTileEntityAt(this.field_147717_b, 0.0D, 0.0D, 0.0D, 0.0F);
            }
        }
    }
}