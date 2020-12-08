package com.stevekung.skyblockcatia.gui.config;

import java.io.IOException;

import com.stevekung.skyblockcatia.config.ConfigGuiFactory;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiSkyBlockSettings extends GuiScreen
{
    private static final SkyBlockcatiaSettings.Options[] QOL_OPTIONS = { SkyBlockcatiaSettings.Options.JUNGLE_AXE_COOLDOWN, SkyBlockcatiaSettings.Options.GRAPPLING_HOOK_COOLDOWN, SkyBlockcatiaSettings.Options.ZEALOT_RESPAWN_COOLDOWN, SkyBlockcatiaSettings.Options.PLACED_SUMMONING_EYE_TRACKER, SkyBlockcatiaSettings.Options.SHOW_ITEM_RARITY, SkyBlockcatiaSettings.Options.LEAVE_PARTY_WHEN_LAST_EYE_PLACED, SkyBlockcatiaSettings.Options.LOBBY_PLAYER_VIEWER, SkyBlockcatiaSettings.Options.AUCTION_BID_CONFIRM, SkyBlockcatiaSettings.Options.AUCTION_BID_CONFIRM_VALUE, SkyBlockcatiaSettings.Options.BAZAAR_ON_TOOLTIPS, SkyBlockcatiaSettings.Options.IGNORE_INTERACT_INVISIBLE_ARMOR_STAND, SkyBlockcatiaSettings.Options.AUTOMATIC_OPEN_MADDOX, SkyBlockcatiaSettings.Options.SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND, SkyBlockcatiaSettings.Options.DISPLAY_ITEM_ABILITY_MAX_USED, SkyBlockcatiaSettings.Options.PREVENT_SCROLL_HOTBAR_WHILE_FIGHT_DRAGON, SkyBlockcatiaSettings.Options.PREVENT_CLICKING_ON_DUMMY_ITEM, SkyBlockcatiaSettings.Options.SHORTCUT_BUTTON_IN_INVENTORY };
    private static final SkyBlockcatiaSettings.Options[] RENDERING_OPTIONS = { SkyBlockcatiaSettings.Options.SUPPORTERS_FANCY_COLOR, SkyBlockcatiaSettings.Options.MAKE_SPECIAL_ZEALOT_HELD_GOLD, SkyBlockcatiaSettings.Options.ITEM_RARITY_OPACITY, SkyBlockcatiaSettings.Options.SHOW_HITBOX_WHEN_DRAGON_SPAWNED, SkyBlockcatiaSettings.Options.SHOW_DRAGON_HITBOX_ONLY, SkyBlockcatiaSettings.Options.GLOWING_DRAGON_ARMOR, SkyBlockcatiaSettings.Options.DISABLE_BLOCK_PARTICLES, SkyBlockcatiaSettings.Options.FIX_SKYBLOCK_ENCHANT_TAG, SkyBlockcatiaSettings.Options.DISABLE_NIGHT_VISION };
    private static final SkyBlockcatiaSettings.Options[] MISC_OPTIONS = { SkyBlockcatiaSettings.Options.RIGHT_CLICK_ADD_PARTY, SkyBlockcatiaSettings.Options.VISIT_ISLAND_TOAST_MODE, SkyBlockcatiaSettings.Options.VISIT_ISLAND_TOAST_TIME, SkyBlockcatiaSettings.Options.RARE_DROP_TOAST_MODE, SkyBlockcatiaSettings.Options.RARE_DROP_TOAST_TIME, SkyBlockcatiaSettings.Options.SPECIAL_DROP_TOAST_TIME, SkyBlockcatiaSettings.Options.FISH_CATCH_TOAST_MODE, SkyBlockcatiaSettings.Options.FISH_CATCH_TOAST_TIME, SkyBlockcatiaSettings.Options.GIFT_TOAST_MODE, SkyBlockcatiaSettings.Options.GIFT_TOAST_TIME, SkyBlockcatiaSettings.Options.PET_TOAST_MODE, SkyBlockcatiaSettings.Options.PET_TOAST_TIME, SkyBlockcatiaSettings.Options.SNEAK_TO_OPEN_INVENTORY_WHILE_FIGHT_DRAGON, SkyBlockcatiaSettings.Options.IGNORE_BUSH_HITBOX, SkyBlockcatiaSettings.Options.ONLY_MINEABLE_HITBOX, SkyBlockcatiaSettings.Options.LOBBY_PLAYER_COUNT, SkyBlockcatiaSettings.Options.SHOW_OBTAINED_DATE };
    private final GuiScreen parent;

    public GuiSkyBlockSettings(GuiScreen parent)
    {
        this.parent = parent;
    }

    @Override
    public void initGui()
    {
        this.buttonList.add(new GuiButton(100, this.width / 2 - 155, this.height / 6 - 12, 150, 20, LangUtils.translate("extended_config.skyblock.qol.title")));
        this.buttonList.add(new GuiButton(101, this.width / 2 + 5, this.height / 6 - 12, 150, 20, LangUtils.translate("extended_config.skyblock.rendering.title")));
        this.buttonList.add(new GuiButton(102, this.width / 2 - 155, this.height / 6 + 13, 150, 20, LangUtils.translate("extended_config.skyblock.misc.title")));
        this.buttonList.add(new GuiButton(103, this.width / 2 + 5, this.height / 6 + 13, 150, 20, LangUtils.translate("extended_config.skyblock.global.title")));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, LangUtils.translate("gui.done")));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            SkyBlockcatiaSettings.INSTANCE.save();
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 100)
            {
                this.mc.displayGuiScreen(new GuiSkyBlockOptionSettings(this, "extended_config.skyblock.qol.title", QOL_OPTIONS));
            }
            else if (button.id == 101)
            {
                this.mc.displayGuiScreen(new GuiSkyBlockOptionSettings(this, "extended_config.skyblock.rendering.title", RENDERING_OPTIONS));
            }
            else if (button.id == 102)
            {
                this.mc.displayGuiScreen(new GuiSkyBlockOptionSettings(this, "extended_config.skyblock.misc.title", MISC_OPTIONS));
            }
            else if (button.id == 103)
            {
                this.mc.displayGuiScreen(new ConfigGuiFactory.GuiMainConfig(this));
            }
            else if (button.id == 200)
            {
                this.mc.displayGuiScreen(this.parent);
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