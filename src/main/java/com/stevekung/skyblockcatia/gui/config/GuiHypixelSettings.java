package com.stevekung.skyblockcatia.gui.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiHypixelSettings extends GuiScreen
{
    private final GuiScreen parent;
    private GuiConfigButtonRowList optionsRowList;
    private static final List<ExtendedConfig.Options> OPTIONS = new ArrayList<>();

    static
    {
        OPTIONS.add(ExtendedConfig.Options.RIGHT_CLICK_ADD_PARTY);
        OPTIONS.add(ExtendedConfig.Options.ADD_PARTY_VISIT_ISLAND);
        OPTIONS.add(ExtendedConfig.Options.JUNGLE_AXE_COOLDOWN);
        OPTIONS.add(ExtendedConfig.Options.GRAPPLING_HOOK_COOLDOWN);
        OPTIONS.add(ExtendedConfig.Options.ZEALOT_RESPAWN_COOLDOWN);
        OPTIONS.add(ExtendedConfig.Options.VISIT_ISLAND_TOAST_MODE);
        OPTIONS.add(ExtendedConfig.Options.ITEM_DROP_TOAST_MODE);
        OPTIONS.add(ExtendedConfig.Options.FISH_CATCH_TOAST_MODE);
        OPTIONS.add(ExtendedConfig.Options.GIFT_TOAST_MODE);
        OPTIONS.add(ExtendedConfig.Options.PET_TOAST_MODE);
        OPTIONS.add(ExtendedConfig.Options.GLOWING_DRAGON_ARMOR);
        OPTIONS.add(ExtendedConfig.Options.PLACED_SUMMONING_EYE_TRACKER);
        OPTIONS.add(ExtendedConfig.Options.SHOW_ITEM_RARITY);
        OPTIONS.add(ExtendedConfig.Options.ITEM_RARITY_OPACITY);
        OPTIONS.add(ExtendedConfig.Options.SHOW_HITBOX_WHEN_DRAGON_SPAWNED);
        OPTIONS.add(ExtendedConfig.Options.SHOW_DRAGON_HITBOX_ONLY);
        OPTIONS.add(ExtendedConfig.Options.LEAVE_PARTY_WHEN_LAST_EYE_PLACED);
        OPTIONS.add(ExtendedConfig.Options.SNEAK_TO_OPEN_INVENTORY_WHILE_FIGHT_DRAGON);
        OPTIONS.add(ExtendedConfig.Options.CURRENT_SERVER_DAY);
        OPTIONS.add(ExtendedConfig.Options.LOBBY_PLAYER_VIEWER);
        OPTIONS.add(ExtendedConfig.Options.AUCTION_BID_CONFIRM);
        OPTIONS.add(ExtendedConfig.Options.AUCTION_BID_CONFIRM_VALUE);
        OPTIONS.add(ExtendedConfig.Options.DISABLE_BLOCK_PARTICLES);
        OPTIONS.add(ExtendedConfig.Options.SUPPORTERS_FANCY_COLOR);
        OPTIONS.add(ExtendedConfig.Options.GOLEM_STAGE_TRACKER);
        OPTIONS.add(ExtendedConfig.Options.BAZAAR_ON_TOOLTIPS);
    }

    public GuiHypixelSettings(GuiScreen parent)
    {
        this.parent = parent;
    }

    @Override
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 27, LangUtils.translate("gui.done")));

        ExtendedConfig.Options[] options = new ExtendedConfig.Options[OPTIONS.size()];
        options = OPTIONS.toArray(options);
        this.optionsRowList = new GuiConfigButtonRowList(this.width, this.height, 32, this.height - 32, 25, options);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.optionsRowList.handleMouseInput();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            ExtendedConfig.instance.save();
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.optionsRowList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        this.optionsRowList.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            ExtendedConfig.instance.save();

            if (button.id == 200)
            {
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.optionsRowList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, LangUtils.translate("extended_config.hypixel.title"), this.width / 2, 5, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}