package com.stevekung.skyblockcatia.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.*;

import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.renderer.TileEntityEnchantedSkullRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StringUtils;

@Mixin(LayerCustomHead.class)
public abstract class LayerCustomHeadMixin
{
    @Shadow
    @Final
    @Mutable
    private ModelRenderer field_177209_a;

    @Overwrite
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ItemStack itemstack = entity.getCurrentArmor(3);

        if (itemstack != null && itemstack.getItem() != null)
        {
            Item item = itemstack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();
            GlStateManager.pushMatrix();

            if (entity.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            boolean flag = entity instanceof EntityVillager || entity instanceof EntityZombie && ((EntityZombie)entity).isVillager();

            if (!flag && entity.isChild())
            {
                float f = 2.0F;
                float f1 = 1.4F;
                GlStateManager.scale(f1 / f, f1 / f, f1 / f);
                GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            }

            this.field_177209_a.postRender(0.0625F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (item instanceof ItemBlock)
            {
                float f2 = 0.625F;
                GlStateManager.translate(0.0F, -0.25F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(f2, -f2, -f2);

                if (flag)
                {
                    GlStateManager.translate(0.0F, 0.1875F, 0.0F);
                }
                minecraft.getItemRenderer().renderItem(entity, itemstack, ItemCameraTransforms.TransformType.HEAD);
            }
            else if (item == Items.skull)
            {
                float f3 = 1.1875F;
                GlStateManager.scale(f3, -f3, -f3);

                if (flag)
                {
                    GlStateManager.translate(0.0F, 0.0625F, 0.0F);
                }

                GameProfile gameprofile = null;

                if (itemstack.hasTagCompound())
                {
                    NBTTagCompound nbttagcompound = itemstack.getTagCompound();

                    if (nbttagcompound.hasKey("SkullOwner", 10))
                    {
                        gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                    }
                    else if (nbttagcompound.hasKey("SkullOwner", 8))
                    {
                        String s = nbttagcompound.getString("SkullOwner");

                        if (!StringUtils.isNullOrEmpty(s))
                        {
                            gameprofile = TileEntitySkull.updateGameprofile(new GameProfile((UUID)null, s));
                            nbttagcompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
                        }
                    }
                }
                TileEntityEnchantedSkullRenderer.INSTANCE.renderSkull(-0.5F, 0.0F, -0.5F, EnumFacing.UP, 180.0F, itemstack.getMetadata(), gameprofile, partialTicks, itemstack.hasEffect(), entity);
            }
            GlStateManager.popMatrix();
        }
    }
}