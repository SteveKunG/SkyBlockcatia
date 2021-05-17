package com.stevekung.skyblockcatia.mixin.renderer.entity.layers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.renderer.DragonArmorRenderType;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidHeadModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;

@Mixin(CustomHeadLayer.class)
public abstract class MixinCustomHeadLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M>
{
    private final SkullModel head = new HumanoidHeadModel();

    MixinCustomHeadLayer()
    {
        super(null);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info)
    {
        if (!SkyBlockcatiaSettings.INSTANCE.glowingDragonArmor)
        {
            return;
        }

        ItemStack itemStack = entity.getItemBySlot(EquipmentSlot.HEAD);

        if (!itemStack.isEmpty())
        {
            Item item = itemStack.getItem();
            matrixStack.pushPose();
            boolean flag = entity instanceof Villager || entity instanceof ZombieVillager;

            if (entity.isBaby() && !(entity instanceof Villager))
            {
                matrixStack.translate(0.0D, 0.03125D, 0.0D);
                matrixStack.scale(0.7F, 0.7F, 0.7F);
                matrixStack.translate(0.0D, 1.0D, 0.0D);
            }

            this.getParentModel().getHead().translateAndRotate(matrixStack);

            if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractSkullBlock && itemStack.hasTag())
            {
                matrixStack.scale(1.1875F, -1.1875F, -1.1875F);

                if (flag)
                {
                    matrixStack.translate(0.0D, 0.0625D, 0.0D);
                }

                matrixStack.translate(-0.5D, 0.0D, -0.5D);

                CompoundTag compound = itemStack.getTag().getCompound("ExtraAttributes");
                String id = compound.getString("id");
                ResourceLocation location = SBRenderUtils.getDragonEyeTexture(id);

                if (compound.contains("skin"))
                {
                    location = SBRenderUtils.getDragonSkinTexture(id, compound.getString("skin"));
                }

                if (location != null)
                {
                    matrixStack.pushPose();
                    matrixStack.translate(0.5D, 0.0D, 0.5D);
                    matrixStack.scale(-1.0F, -1.0F, 1.0F);

                    VertexConsumer ivertexbuilder = buffer.getBuffer(DragonArmorRenderType.getGlowingDragonOverlay(location));
                    this.head.setupAnim(0.0F, 180.0F, 0.0F);
                    this.head.renderToBuffer(matrixStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                    matrixStack.popPose();
                }
            }
            matrixStack.popPose();
        }
    }
}