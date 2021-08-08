package com.stevekung.skyblockcatia.gui.screen.config;

import static com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings.*;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.config.AbstractSettings;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

public class SkyBlockSettingsScreen extends Screen
{
    private static final ImmutableList<AbstractSettings<SkyBlockcatiaSettings>> QOL_OPTIONS = ImmutableList.of(AXE_COOLDOWN, GRAPPLING_HOOK_COOLDOWN, ZEALOT_RESPAWN_COOLDOWN, LEAVE_PARTY_WHEN_LAST_EYE_PLACED, LOBBY_PLAYER_VIEWER, AUCTION_BID_CONFIRM, AUCTION_BID_CONFIRM_VALUE, BAZAAR_ON_ITEM_TOOLTIP, SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND, DISPLAY_ITEM_ABILITY_MAX_USED, SHORTCUT_BUTTON_IN_INVENTORY);
    private static final ImmutableList<AbstractSettings<SkyBlockcatiaSettings>> RENDERING_OPTIONS = ImmutableList.of(SUPPORTERS_FANCY_COLOR, ITEM_RARITY, ITEM_RARITY_OPACITY, MAKE_SPECIAL_ZEALOT_HELD_GOLD, SHOW_HITBOX_WHEN_DRAGON_SPAWNED, HITBOX_RENDER_MODE, GLOWING_DRAGON_ARMOR, DISABLE_BLOCK_PARTICLES, FIX_SKYBLOCK_ENCHANT_TAG, DISABLE_NIGHT_VISION);
    private static final ImmutableList<AbstractSettings<SkyBlockcatiaSettings>> MISC_OPTIONS = ImmutableList.of(SHOW_OBTAINED_DATE, SNEAK_TO_OPEN_INVENTORY);
    private static final ImmutableList<AbstractSettings<SkyBlockcatiaSettings>> TOAST_OPTIONS = ImmutableList.of(VISIT_ISLAND_DISPLAY_MODE, VISIT_ISLAND_TOAST_TIME, ITEM_LOG_DISPLAY_MODE, RARE_DROP_TOAST_TIME, SPECIAL_DROP_TOAST_TIME, FISH_CATCH_DISPLAY_MODE, FISH_CATCH_TOAST_TIME, GIFT_DISPLAY_MODE, GIFT_TOAST_TIME, PET_DISPLAY_MODE, PET_TOAST_TIME);

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
            this.minecraft.setScreen(new SBOptionsScreen(this, LangUtils.translate("menu.skyblock_qol.title"), QOL_OPTIONS));
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height / 6 - 12, 150, 20, LangUtils.translate("menu.skyblock_rendering.title"), button ->
        {
            SkyBlockcatiaSettings.INSTANCE.save();
            this.minecraft.setScreen(new SBOptionsScreen(this, LangUtils.translate("menu.skyblock_rendering.title"), RENDERING_OPTIONS));
        }));
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 13, 150, 20, LangUtils.translate("menu.skyblock_misc.title"), button ->
        {
            SkyBlockcatiaSettings.INSTANCE.save();
            this.minecraft.setScreen(new SBOptionsScreen(this, LangUtils.translate("menu.skyblock_misc.title"), MISC_OPTIONS));
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 13, 150, 20, LangUtils.translate("menu.skyblock_global.title"), button -> openConfig(this)));
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 38, 150, 20, LangUtils.translate("menu.skyblock_toast.title"), button ->
        {
            SkyBlockcatiaSettings.INSTANCE.save();
            this.minecraft.setScreen(new SBOptionsScreen(this, LangUtils.translate("menu.skyblock_toast.title"), TOAST_OPTIONS));
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, CommonComponents.GUI_DONE, button ->
        {
            SkyBlockcatiaSettings.INSTANCE.save();
            this.minecraft.setScreen(null);
        }));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        SkyBlockcatiaSettings.INSTANCE.save();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(poseStack);
        GuiComponent.drawCenteredString(poseStack, this.font, LangUtils.translate("menu.skyblock.title"), this.width / 2, 10, 16777215);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @ExpectPlatform
    private static void openConfig(Screen parent) {}
}