package com.stevekung.skyblockcatia.gui.config;

import static com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings.Options.*;

import java.io.IOException;

import com.stevekung.skyblockcatia.config.ConfigGuiFactory;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.utils.CommonUtils;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class GuiSkyBlockSettings extends GuiScreen
{
    private static final SkyBlockcatiaSettings.Options[] QOL_OPTIONS = { JUNGLE_AXE_COOLDOWN, GRAPPLING_HOOK_COOLDOWN, ZEALOT_RESPAWN_COOLDOWN, PLACED_SUMMONING_EYE_TRACKER, LEAVE_PARTY_WHEN_LAST_EYE_PLACED, LOBBY_PLAYER_VIEWER, AUCTION_BID_CONFIRM, AUCTION_BID_CONFIRM_VALUE, BAZAAR_ON_TOOLTIPS, SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND, DISPLAY_ITEM_ABILITY_MAX_USED, SHORTCUT_BUTTON_IN_INVENTORY };
    private static final SkyBlockcatiaSettings.Options[] RENDERING_OPTIONS = { SUPPORTERS_FANCY_COLOR, SHOW_ITEM_RARITY, ITEM_RARITY_OPACITY, MAKE_SPECIAL_ZEALOT_HELD_GOLD, SHOW_HITBOX_WHEN_DRAGON_SPAWNED, HITBOX_RENDER_MODE, GLOWING_DRAGON_ARMOR, DISABLE_BLOCK_PARTICLES, FIX_SKYBLOCK_ENCHANT_TAG, DISABLE_NIGHT_VISION, DISPLAY_REALTIME_PING, DISABLE_SUPERBOOM_NOTIFICATION };
    private static final SkyBlockcatiaSettings.Options[] MISC_OPTIONS = { RIGHT_CLICK_ADD_PARTY, SNEAK_TO_OPEN_INVENTORY_WHILE_FIGHT_DRAGON, LOBBY_PLAYER_COUNT, SHOW_OBTAINED_DATE };
    private static final SkyBlockcatiaSettings.Options[] TOAST_OPTIONS = { VISIT_ISLAND_TOAST_MODE, VISIT_ISLAND_TOAST_TIME, RARE_DROP_TOAST_MODE, RARE_DROP_TOAST_TIME, SPECIAL_DROP_TOAST_TIME, FISH_CATCH_TOAST_MODE, FISH_CATCH_TOAST_TIME, GIFT_TOAST_MODE, GIFT_TOAST_TIME, PET_TOAST_MODE, PET_TOAST_TIME };

    public void display()
    {
        CommonUtils.registerEventHandler(this);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        CommonUtils.unregisterEventHandler(this);
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    @Override
    public void initGui()
    {
        this.buttonList.add(new GuiButton(100, this.width / 2 - 155, this.height / 6 - 12, 150, 20, LangUtils.translate("extended_config.skyblock.qol.title")));
        this.buttonList.add(new GuiButton(101, this.width / 2 + 5, this.height / 6 - 12, 150, 20, LangUtils.translate("extended_config.skyblock.rendering.title")));
        this.buttonList.add(new GuiButton(102, this.width / 2 - 155, this.height / 6 + 13, 150, 20, LangUtils.translate("extended_config.skyblock.misc.title")));
        this.buttonList.add(new GuiButton(103, this.width / 2 + 5, this.height / 6 + 13, 150, 20, LangUtils.translate("extended_config.skyblock.global.title")));
        this.buttonList.add(new GuiButton(104, this.width / 2 - 155, this.height / 6 + 38, 150, 20, LangUtils.translate("extended_config.skyblock.toast.title")));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, LangUtils.translate("gui.done")));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            SkyBlockcatiaSettings.INSTANCE.save();
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            switch (button.id)
            {
                case 100:
                    this.mc.displayGuiScreen(new GuiSkyBlockOptionSettings(this, "extended_config.skyblock.qol.title", QOL_OPTIONS));
                    break;
                case 101:
                    this.mc.displayGuiScreen(new GuiSkyBlockOptionSettings(this, "extended_config.skyblock.rendering.title", RENDERING_OPTIONS));
                    break;
                case 102:
                    this.mc.displayGuiScreen(new GuiSkyBlockOptionSettings(this, "extended_config.skyblock.misc.title", MISC_OPTIONS));
                    break;
                case 103:
                    this.mc.displayGuiScreen(new ConfigGuiFactory.GuiMainConfig(this));
                    break;
                case 104:
                    this.mc.displayGuiScreen(new GuiSkyBlockOptionSettings(this, "extended_config.skyblock.toast.title", TOAST_OPTIONS));
                    break;
                case 200:
                    SkyBlockcatiaSettings.INSTANCE.save();
                    this.mc.displayGuiScreen(null);
                    break;
                default:
                    break;
            }
        }
        SkyBlockcatiaSettings.INSTANCE.save();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, LangUtils.translate("extended_config.skyblock.title"), this.width / 2, 10, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}