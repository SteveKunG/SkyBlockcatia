package com.stevekung.skyblockcatia.core;

import com.stevekung.skyblockcatia.command.BazaarViewerCommand;
import com.stevekung.skyblockcatia.command.SkyBlockAPIViewerCommand;
import com.stevekung.skyblockcatia.command.SkyBlockcatiaCommand;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.event.handler.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.minecraft.world.InteractionResult;

public class SkyBlockcatiaFabric implements ClientModInitializer
{
    public static SkyBlockcatiaConfig CONFIG;

    @Override
    public void onInitializeClient()
    {
        SkyBlockcatia.init();

        var holder = AutoConfig.register(SkyBlockcatiaConfig.class, GsonConfigSerializer::new);

        holder.registerSaveListener((holder2, configuration) ->
        {
            SBAPIUtils.setApiKey();
            return InteractionResult.SUCCESS;
        });

        SkyBlockcatiaFabric.CONFIG = AutoConfig.getConfigHolder(SkyBlockcatiaConfig.class).getConfig();

        new BazaarViewerCommand(ClientCommandManager.DISPATCHER);
        new SkyBlockAPIViewerCommand(ClientCommandManager.DISPATCHER);
        new SkyBlockcatiaCommand(ClientCommandManager.DISPATCHER);

        ClientChunkEvents.CHUNK_LOAD.register((level, chunk) -> HUDRenderEventHandler.INSTANCE.onChunkLoad(chunk.getPos().x, chunk.getPos().z));
        ClientLoginConnectionEvents.DISCONNECT.register((handler, mc) -> SkyBlockEventHandler.INSTANCE.onDisconnectedFromServerEvent());
        ItemTooltipCallback.EVENT.register((itemStack, context, lines) -> SkyBlockEventHandler.INSTANCE.onItemTooltip(itemStack, lines));
        ClientTickEvents.END_CLIENT_TICK.register(mc ->
        {
            SkyBlockEventHandler.INSTANCE.onPressKey();

            //TODO
            /*if (ClientUtils.isKeyDown(GLFW.GLFW_MOUSE_BUTTON_2) && mc.crosshairPickEntity instanceof RemotePlayer && mc.player.isShiftKeyDown() && SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.sneakToTradeOtherPlayerIsland && SkyBlockEventHandler.otherPlayerIsland)
            {
                RemotePlayer player = (RemotePlayer)mc.crosshairPickEntity;
                mc.player.chat("/trade " + ChatFormatting.stripFormatting(player.getName().getString()));
            }*/
        });
        SBAPIUtils.setApiKey();
    }
}