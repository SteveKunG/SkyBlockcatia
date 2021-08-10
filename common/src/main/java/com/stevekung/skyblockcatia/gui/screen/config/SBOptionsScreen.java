package com.stevekung.skyblockcatia.gui.screen.config;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.gui.screen.config.widget.ConfigButtonListWidget;
import com.stevekung.stevekungslib.utils.config.AbstractSettings;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SBOptionsScreen extends Screen
{
    private final Screen parent;
    private final ImmutableList<AbstractSettings<SkyBlockcatiaSettings>> configs;
    private ConfigButtonListWidget optionsRowList;

    public SBOptionsScreen(Screen parent, Component title, ImmutableList<AbstractSettings<SkyBlockcatiaSettings>> configs)
    {
        super(title);
        this.parent = parent;
        this.configs = configs;
    }

    @Override
    public void init()
    {
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_DONE, button ->
        {
            SkyBlockcatiaSettings.INSTANCE.save();
            this.minecraft.setScreen(this.parent);
        }));

        this.optionsRowList = new ConfigButtonListWidget(this.width, this.height, 16, this.height - 30, 25);
        this.optionsRowList.addAll(this.configs);
        this.addWidget(this.optionsRowList);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        SkyBlockcatiaSettings.INSTANCE.save();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose()
    {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(poseStack);
        this.optionsRowList.render(poseStack, mouseX, mouseY, partialTicks);
        GuiComponent.drawCenteredString(poseStack, this.font, this.getTitle(), this.width / 2, 5, 16777215);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}