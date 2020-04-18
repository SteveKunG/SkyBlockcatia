package stevekung.mods.indicatia.mixin;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S3APacketTabComplete;
import stevekung.mods.indicatia.utils.ITabComplete;

@Mixin(NetHandlerPlayClient.class)
public abstract class NetHandlerPlayClientMixin
{
    private final NetHandlerPlayClient that = (NetHandlerPlayClient) (Object) this;

    @Shadow
    private Minecraft gameController;

    @Shadow
    @Final
    @Mutable
    private NetworkManager netManager;

    @Shadow
    private boolean doneLoadingTerrain;

    @Inject(method = "handleTabComplete(Lnet/minecraft/network/play/server/S3APacketTabComplete;)V", at = @At("RETURN"))
    private void handleTabComplete(S3APacketTabComplete packet, CallbackInfo info)
    {
        if (this.gameController.currentScreen instanceof ITabComplete)
        {
            ITabComplete chest = (ITabComplete)this.gameController.currentScreen;
            chest.onAutocompleteResponse(packet.func_149630_c());
        }
    }

    @Overwrite
    public void handlePlayerPosLook(S08PacketPlayerPosLook packet)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this.that, this.gameController);
        EntityPlayer player = this.gameController.thePlayer;
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        float yaw = packet.getYaw();
        float pitch = packet.getPitch();

        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X))
        {
            player.lastTickPosX += x;
            player.chasingPosX += x;
            player.prevChasingPosX += x;
            x += player.posX;
        }
        else
        {
            player.lastTickPosX = x;
            player.chasingPosX = x;
            player.prevChasingPosX = x;
            player.motionX = 0.0D;
        }

        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y))
        {
            player.lastTickPosY += y;
            player.chasingPosY += y;
            player.prevChasingPosY += y;
            y += player.posY;
        }
        else
        {
            player.lastTickPosY = y;
            player.chasingPosY = y;
            player.prevChasingPosY = y;
            player.motionY = 0.0D;
        }

        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z))
        {
            player.lastTickPosZ += z;
            player.chasingPosZ += z;
            player.prevChasingPosZ += z;
            z += player.posZ;
        }
        else
        {
            player.lastTickPosZ = z;
            player.chasingPosZ = z;
            player.prevChasingPosZ = z;
            player.motionZ = 0.0D;
        }

        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT))
        {
            pitch += player.rotationPitch;
        }

        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT))
        {
            yaw += player.rotationYaw;
        }

        player.setPositionAndRotation(x, y, z, yaw, pitch);
        this.netManager.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(player.posX, player.getEntityBoundingBox().minY, player.posZ, player.rotationYaw, player.rotationPitch, false));

        if (!this.doneLoadingTerrain)
        {
            this.gameController.thePlayer.prevPosX = this.gameController.thePlayer.posX;
            this.gameController.thePlayer.prevPosY = this.gameController.thePlayer.posY;
            this.gameController.thePlayer.prevPosZ = this.gameController.thePlayer.posZ;
            this.doneLoadingTerrain = true;
            this.gameController.displayGuiScreen(null);
        }
    }
}