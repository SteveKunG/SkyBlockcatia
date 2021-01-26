package com.stevekung.skyblockcatia.gui.screen.config;

import java.io.File;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.config.AbstractSettings;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.Util;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

public class SkyBlockSettingsScreen extends Screen
{
    private static final ImmutableList<AbstractSettings<SkyBlockcatiaSettings>> QOL_OPTIONS = ImmutableList.of(SkyBlockcatiaSettings.AXE_COOLDOWN, SkyBlockcatiaSettings.GRAPPLING_HOOK_COOLDOWN, SkyBlockcatiaSettings.ZEALOT_RESPAWN_COOLDOWN, SkyBlockcatiaSettings.PLACED_SUMMONING_EYE_TRACKER, SkyBlockcatiaSettings.LEAVE_PARTY_WHEN_LAST_EYE_PLACED, SkyBlockcatiaSettings.PLAYER_COUNT_LOBBY_VIEWER, SkyBlockcatiaSettings.AUCTION_BID_CONFIRM, SkyBlockcatiaSettings.AUCTION_BID_CONFIRM_VALUE, SkyBlockcatiaSettings.BAZAAR_ON_ITEM_TOOLTIP, SkyBlockcatiaSettings.IGNORE_INTERACT_INVISIBLE_ARMOR_STAND, SkyBlockcatiaSettings.AUTOMATIC_OPEN_MADDOX, SkyBlockcatiaSettings.SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND, SkyBlockcatiaSettings.DISPLAY_ITEM_ABILITY_MAX_USED, SkyBlockcatiaSettings.PREVENT_SCROLL_HOTBAR_WHILE_FIGHT_DRAGON, SkyBlockcatiaSettings.SHORTCUT_BUTTON_IN_INVENTORY);
    private static final ImmutableList<AbstractSettings<SkyBlockcatiaSettings>> RENDERING_OPTIONS = ImmutableList.of(SkyBlockcatiaSettings.SUPPORTERS_FANCY_COLOR, SkyBlockcatiaSettings.ITEM_RARITY, SkyBlockcatiaSettings.ITEM_RARITY_OPACITY, SkyBlockcatiaSettings.MAKE_SPECIAL_ZEALOT_HELD_GOLD, SkyBlockcatiaSettings.SHOW_HITBOX_WHEN_DRAGON_SPAWNED, SkyBlockcatiaSettings.HITBOX_RENDER_MODE, SkyBlockcatiaSettings.GLOWING_DRAGON_ARMOR, SkyBlockcatiaSettings.DISABLE_BLOCK_PARTICLES, SkyBlockcatiaSettings.FIX_SKYBLOCK_ENCHANT_TAG, SkyBlockcatiaSettings.DISABLE_NIGHT_VISION);
    private static final ImmutableList<AbstractSettings<SkyBlockcatiaSettings>> MISC_OPTIONS = ImmutableList.of(SkyBlockcatiaSettings.PREVENT_SCROLL_HOTBAR_WHILE_FIGHT_DRAGON, SkyBlockcatiaSettings.IGNORE_BUSH_HITBOX, SkyBlockcatiaSettings.ONLY_MINEABLE_HITBOX, SkyBlockcatiaSettings.LOBBY_PLAYER_COUNT, SkyBlockcatiaSettings.SHOW_OBTAINED_DATE);
    private static final ImmutableList<AbstractSettings<SkyBlockcatiaSettings>> TOAST_OPTIONS = ImmutableList.of(SkyBlockcatiaSettings.VISIT_ISLAND_DISPLAY_MODE, SkyBlockcatiaSettings.VISIT_ISLAND_TOAST_TIME, SkyBlockcatiaSettings.ITEM_LOG_DISPLAY_MODE, SkyBlockcatiaSettings.RARE_DROP_TOAST_TIME, SkyBlockcatiaSettings.SPECIAL_DROP_TOAST_TIME, SkyBlockcatiaSettings.FISH_CATCH_DISPLAY_MODE, SkyBlockcatiaSettings.FISH_CATCH_TOAST_TIME, SkyBlockcatiaSettings.GIFT_DISPLAY_MODE, SkyBlockcatiaSettings.GIFT_TOAST_TIME, SkyBlockcatiaSettings.PET_DISPLAY_MODE, SkyBlockcatiaSettings.PET_TOAST_TIME);

    public SkyBlockSettingsScreen()
    {
        super(LangUtils.translate("menu.skyblock.title"));
    }

    @Override
    public void init()
    {
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 - 12, 150, 20, LangUtils.translate("menu.skyblock_qol.title"), button ->
        {
            SkyBlockcatiaSettings.INSTANCE.save();
            this.minecraft.displayGuiScreen(new SBOptionsScreen(this, LangUtils.translate("menu.skyblock_qol.title"), QOL_OPTIONS));
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height / 6 - 12, 150, 20, LangUtils.translate("menu.skyblock_rendering.title"), button ->
        {
            SkyBlockcatiaSettings.INSTANCE.save();
            this.minecraft.displayGuiScreen(new SBOptionsScreen(this, LangUtils.translate("menu.skyblock_rendering.title"), RENDERING_OPTIONS));
        }));
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 13, 150, 20, LangUtils.translate("menu.skyblock_misc.title"), button ->
        {
            SkyBlockcatiaSettings.INSTANCE.save();
            this.minecraft.displayGuiScreen(new SBOptionsScreen(this, LangUtils.translate("menu.skyblock_misc.title"), MISC_OPTIONS));
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 13, 150, 20, LangUtils.translate("menu.skyblock_global.title"), button ->
        {
            String configPath = ConfigTracker.INSTANCE.getConfigFileName(SkyBlockcatiaMod.MOD_ID, ModConfig.Type.CLIENT);

            if (configPath == null)
            {
                return;
            }
            File config = new File(configPath);
            Util.getOSType().openURI(config.toURI());
        }));
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 38, 150, 20, LangUtils.translate("menu.skyblock_toast.title"), button ->
        {
            SkyBlockcatiaSettings.INSTANCE.save();
            this.minecraft.displayGuiScreen(new SBOptionsScreen(this, LangUtils.translate("menu.skyblock_toast.title"), TOAST_OPTIONS));
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 160, 20, DialogTexts.GUI_DONE, button ->
        {
            SkyBlockcatiaSettings.INSTANCE.save();
            this.minecraft.displayGuiScreen(null);
        }));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        SkyBlockcatiaSettings.INSTANCE.save();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        AbstractGui.drawCenteredString(matrixStack, this.font, LangUtils.translate("menu.skyblock.title"), this.width / 2, 10, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}