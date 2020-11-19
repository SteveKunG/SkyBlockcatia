package com.stevekung.skyblockcatia.gui.config;

import java.io.IOException;

import com.stevekung.skyblockcatia.config.ConfigGuiFactory;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiSkyblockSettings extends GuiScreen
{
    private static final ExtendedConfig.Options[] QOL_OPTIONS = { ExtendedConfig.Options.JUNGLE_AXE_COOLDOWN, ExtendedConfig.Options.GRAPPLING_HOOK_COOLDOWN, ExtendedConfig.Options.ZEALOT_RESPAWN_COOLDOWN, ExtendedConfig.Options.PLACED_SUMMONING_EYE_TRACKER, ExtendedConfig.Options.SHOW_ITEM_RARITY, ExtendedConfig.Options.LEAVE_PARTY_WHEN_LAST_EYE_PLACED, ExtendedConfig.Options.LOBBY_PLAYER_VIEWER, ExtendedConfig.Options.AUCTION_BID_CONFIRM, ExtendedConfig.Options.AUCTION_BID_CONFIRM_VALUE, ExtendedConfig.Options.BAZAAR_ON_TOOLTIPS, ExtendedConfig.Options.IGNORE_INTERACT_INVISIBLE_ARMOR_STAND, ExtendedConfig.Options.AUTOMATIC_OPEN_MADDOX, ExtendedConfig.Options.SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND, ExtendedConfig.Options.DISPLAY_ITEM_ABILITY_MAX_USED, ExtendedConfig.Options.PREVENT_SCROLL_HOTBAR_WHILE_FIGHT_DRAGON, ExtendedConfig.Options.PREVENT_CLICKING_ON_DUMMY_ITEM, ExtendedConfig.Options.SHORTCUT_BUTTON_IN_INVENTORY };
    private static final ExtendedConfig.Options[] RENDERING_OPTIONS = { ExtendedConfig.Options.SUPPORTERS_FANCY_COLOR, ExtendedConfig.Options.MAKE_SPECIAL_ZEALOT_HELD_GOLD, ExtendedConfig.Options.ITEM_RARITY_OPACITY, ExtendedConfig.Options.SHOW_HITBOX_WHEN_DRAGON_SPAWNED, ExtendedConfig.Options.SHOW_DRAGON_HITBOX_ONLY, ExtendedConfig.Options.GLOWING_DRAGON_ARMOR, ExtendedConfig.Options.DISABLE_BLOCK_PARTICLES, ExtendedConfig.Options.FIX_SKYBLOCK_ENCHANT_TAG };
    private static final ExtendedConfig.Options[] MISC_OPTIONS = { ExtendedConfig.Options.RIGHT_CLICK_ADD_PARTY, ExtendedConfig.Options.VISIT_ISLAND_TOAST_MODE, ExtendedConfig.Options.VISIT_ISLAND_TOAST_TIME, ExtendedConfig.Options.RARE_DROP_TOAST_MODE, ExtendedConfig.Options.RARE_DROP_TOAST_TIME, ExtendedConfig.Options.SPECIAL_DROP_TOAST_TIME, ExtendedConfig.Options.FISH_CATCH_TOAST_MODE, ExtendedConfig.Options.FISH_CATCH_TOAST_TIME, ExtendedConfig.Options.GIFT_TOAST_MODE, ExtendedConfig.Options.GIFT_TOAST_TIME, ExtendedConfig.Options.PET_TOAST_MODE, ExtendedConfig.Options.PET_TOAST_TIME, ExtendedConfig.Options.SNEAK_TO_OPEN_INVENTORY_WHILE_FIGHT_DRAGON, ExtendedConfig.Options.IGNORE_BUSH_HITBOX, ExtendedConfig.Options.ONLY_MINEABLE_HITBOX, ExtendedConfig.Options.LOBBY_PLAYER_COUNT, ExtendedConfig.Options.SHOW_OBTAINED_DATE };
    private final GuiScreen parent;

    public GuiSkyblockSettings(GuiScreen parent)
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
            ExtendedConfig.instance.save();
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
                this.mc.displayGuiScreen(new GuiSkyblockOptionSettings(this, "extended_config.skyblock.qol.title", QOL_OPTIONS));
            }
            else if (button.id == 101)
            {
                this.mc.displayGuiScreen(new GuiSkyblockOptionSettings(this, "extended_config.skyblock.rendering.title", RENDERING_OPTIONS));
            }
            else if (button.id == 102)
            {
                this.mc.displayGuiScreen(new GuiSkyblockOptionSettings(this, "extended_config.skyblock.misc.title", MISC_OPTIONS));
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
        ExtendedConfig.instance.save();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, LangUtils.translate("extended_config.skyblock.title"), this.width / 2, 10, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}