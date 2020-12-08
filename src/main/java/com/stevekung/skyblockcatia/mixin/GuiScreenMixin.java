package com.stevekung.skyblockcatia.mixin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.gui.SignSelectionList;
import com.stevekung.skyblockcatia.gui.screen.GuiSkyBlockProfileSelector;
import com.stevekung.skyblockcatia.utils.*;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

@Mixin(GuiScreen.class)
public class GuiScreenMixin
{
    private final GuiScreen that = (GuiScreen) (Object) this;
    private static final List<String> IGNORE_TOOLTIPS = new ArrayList<>(Arrays.asList(" "));

    @Inject(method = "renderToolTip(Lnet/minecraft/item/ItemStack;II)V", cancellable = true, at = @At("HEAD"))
    private void renderToolTip(ItemStack itemStack, int x, int y, CallbackInfo info)
    {
        if (this.ignoreNullItem(itemStack, IGNORE_TOOLTIPS))
        {
            info.cancel();
        }
    }

    @Inject(method = "confirmClicked(ZI)V", at = @At("HEAD"))
    private void confirmClicked(boolean result, int id, CallbackInfo info)
    {
        if (this.that instanceof GuiEditSign)
        {
            GuiEditSign sign = (GuiEditSign)this.that;

            if (result)
            {
                String text = sign.tileSign.signText[0].getUnformattedText();
                sign.tileSign.markDirty();
                SignSelectionList.processSignData(sign.tileSign);
                ((IEditSign)sign).getSignSelectionList().add(text);
                this.that.mc.displayGuiScreen(null);
            }
            else
            {
                this.that.mc.displayGuiScreen(this.that);
            }
        }
    }

    @Inject(method = "mouseClicked(III)V", at = @At("HEAD"))
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo info) throws IOException
    {
        if (this.that instanceof GuiEditSign)
        {
            GuiEditSign sign = (GuiEditSign)this.that;

            if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaConfig.enableSignSelectionList)
            {
                if (((IEditSign)sign).getSignSelectionList() != null)
                {
                    ((IEditSign)sign).getSignSelectionList().mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Inject(method = "mouseReleased(III)V", at = @At("HEAD"))
    private void mouseReleased(int mouseX, int mouseY, int state, CallbackInfo info) throws IOException
    {
        if (this.that instanceof GuiEditSign)
        {
            GuiEditSign sign = (GuiEditSign)this.that;

            if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaConfig.enableSignSelectionList)
            {
                if (((IEditSign)sign).getSignSelectionList() != null)
                {
                    ((IEditSign)sign).getSignSelectionList().mouseReleased(mouseX, mouseY, state);
                }
            }
        }
    }

    @Inject(method = "handleMouseInput()V", at = @At("HEAD"))
    private void handleMouseInput(CallbackInfo info) throws IOException
    {
        if (this.that instanceof GuiEditSign)
        {
            GuiEditSign sign = (GuiEditSign)this.that;

            if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaConfig.enableSignSelectionList)
            {
                if (((IEditSign)sign).getSignSelectionList() != null)
                {
                    ((IEditSign)sign).getSignSelectionList().handleMouseInput();
                }
            }
        }
    }

    @Inject(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo info) throws IOException
    {
        if (this.that instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)this.that;

            if (button.id == 155)
            {
                String text = chest.lowerChestInventory.getDisplayName().getUnformattedText();
                ClientUtils.printClientMessage(JsonUtils.create("Copied seller auction command!").setChatStyle(JsonUtils.green()));
                GuiScreen.setClipboardString("/ah " + text.replace(text.substring(text.indexOf('\'')), ""));
            }
            else if (button.id == 156)
            {
                String text = chest.lowerChestInventory.getDisplayName().getUnformattedText();
                this.that.mc.displayGuiScreen(new GuiSkyBlockProfileSelector(GuiSkyBlockProfileSelector.GuiState.PLAYER, text.replace(text.substring(text.indexOf('\'')), ""), "", ""));
            }
        }
    }

    @Inject(method = "setText(Ljava/lang/String;Z)V", at = @At("HEAD"))
    private void setText(String newChatText, boolean shouldOverwrite, CallbackInfo info)
    {
        if (this.that instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)this.that;

            if (GuiScreenUtils.isChatable(chest.lowerChestInventory))
            {
                if (shouldOverwrite)
                {
                    ((IExtendedChatGui)chest).getChatTextField().setText(newChatText);
                }
                else
                {
                    ((IExtendedChatGui)chest).getChatTextField().writeText(newChatText);
                }
            }
        }
    }

    private boolean ignoreNullItem(ItemStack itemStack, List<String> ignores)
    {
        String displayName = EnumChatFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName());
        return ignores.stream().anyMatch(name -> displayName.equals(name));
    }
}