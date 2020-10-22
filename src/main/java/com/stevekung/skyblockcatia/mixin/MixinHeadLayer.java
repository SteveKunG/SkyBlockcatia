package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.renderer.DragonArmorRenderType;
import com.stevekung.skyblockcatia.utils.skyblock.api.DragonType;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.entity.model.HumanoidHeadModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

@Mixin(HeadLayer.class)
public abstract class MixinHeadLayer<T extends LivingEntity, M extends EntityModel<T> & IHasHead> extends LayerRenderer<T, M>
{
    private static final ResourceLocation DIVER = new ResourceLocation("skyblockcatia:textures/entity/diver_head.png");
    private static final ResourceLocation SUPERIOR_BABY = new ResourceLocation("skyblockcatia:textures/entity/superior_baby.png");
    private final GenericHeadModel head = new HumanoidHeadModel();

    private MixinHeadLayer()
    {
        super(null);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("RETURN"))
    private void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info)
    {
        if (!SkyBlockcatiaSettings.INSTANCE.glowingDragonArmor)
        {
            return;
        }

        ItemStack itemStack = entity.getItemStackFromSlot(EquipmentSlotType.HEAD);

        if (!itemStack.isEmpty())
        {
            Item item = itemStack.getItem();
            matrixStack.push();
            boolean flag = entity instanceof VillagerEntity || entity instanceof ZombieVillagerEntity;

            if (entity.isChild() && !(entity instanceof VillagerEntity))
            {
                matrixStack.translate(0.0D, 0.03125D, 0.0D);
                matrixStack.scale(0.7F, 0.7F, 0.7F);
                matrixStack.translate(0.0D, 1.0D, 0.0D);
            }

            ((IHasHead)this.getEntityModel()).getModelHead().translateRotate(matrixStack);

            if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock && itemStack.hasTag())
            {
                matrixStack.scale(1.1875F, -1.1875F, -1.1875F);

                if (flag)
                {
                    matrixStack.translate(0.0D, 0.0625D, 0.0D);
                }

                matrixStack.translate(-0.5D, 0.0D, -0.5D);

                CompoundNBT compound = itemStack.getTag().getCompound("ExtraAttributes");
                String id = compound.getString("id");
                ResourceLocation location = this.getDragonEyeTexture(id);

                if (compound.contains("skin"))
                {
                    if (id.equals("SUPERIOR_DRAGON_HELMET") && compound.getString("skin").equals("SUPERIOR_BABY"))
                    {
                        location = SUPERIOR_BABY;
                    }
                }

                if (location != null)
                {
                    matrixStack.push();
                    matrixStack.translate(0.5D, 0.0D, 0.5D);
                    matrixStack.scale(-1.0F, -1.0F, 1.0F);

                    IVertexBuilder ivertexbuilder = buffer.getBuffer(DragonArmorRenderType.getGlowingDragonOverlay(location));
                    this.head.func_225603_a_(0.0F, 180.0F, 0.0F);
                    this.head.render(matrixStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                    matrixStack.pop();
                }
            }
            matrixStack.pop();
        }
    }

    private ResourceLocation getDragonEyeTexture(String id)
    {
        DragonType dragonType = DragonType.getDragonTypeById(id);
        return dragonType != null ? new ResourceLocation("skyblockcatia:textures/entity/" + (dragonType.isWhiteEye() ? "white_eye" : dragonType.getShortName()) + ".png") : id.equals("DIVER_HELMET") ? DIVER : null;
    }
}