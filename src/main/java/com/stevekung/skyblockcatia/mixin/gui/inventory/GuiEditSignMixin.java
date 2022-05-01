package com.stevekung.skyblockcatia.mixin.gui.inventory;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.gui.SignSelectionList;
import com.stevekung.skyblockcatia.utils.*;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StringUtils;

@Mixin(GuiEditSign.class)
public class GuiEditSignMixin extends GuiScreen implements IEditSign
{
    private final GuiEditSign that = (GuiEditSign)(Object)this;
    private TextInputUtil textInputUtil;
    private SignSelectionList globalSelector;

    @Shadow
    int editLine;

    @Shadow
    int updateCounter;

    @Inject(method = "initGui()V", at = @At("TAIL"))
    private void initGui(CallbackInfo info)
    {
        this.textInputUtil = new TextInputUtil(this.fontRendererObj, () -> ((IModifiedSign)this.that.tileSign).getText(this.editLine).getUnformattedText(), text -> ((IModifiedSign)this.that.tileSign).setText(this.editLine, new ChatComponentText(text)), 90);

        if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaConfig.enableSignSelectionList)
        {
            List<SignSelectionList.Entry> list = null;
            String title = null;

            if (this.isAuctionStartBidSign())
            {
                list = SignSelectionList.AUCTION_STARTING_BID_PRICES;
                title = "Select price";
            }
            if (this.isAuctionPrice())
            {
                list = SignSelectionList.AUCTION_BID_PRICES;
                title = "Select bid price";
            }
            if (this.isAuctionQuery())
            {
                list = SignSelectionList.AUCTION_QUERIES;
                title = "Select query";
            }
            if (this.isBankWithdraw())
            {
                list = SignSelectionList.BANK_WITHDRAW;
                title = "Select withdraw";
            }
            if (this.isBankDeposit())
            {
                list = SignSelectionList.BANK_DEPOSIT;
                title = "Select deposit";
            }
            if (this.isBazaarOrder())
            {
                list = SignSelectionList.BAZAAR_ORDER;
                title = "Select bazaar order";
            }
            if (this.isBazaarPrice())
            {
                list = SignSelectionList.BAZAAR_PRICE;
                title = "Select bazaar price";
            }
            if (list != null && title != null)
            {
                this.globalSelector = new SignSelectionList(this.mc, this.width + 200, this.height, 64, this.height - 64, list, title);
            }
        }
    }

    @Inject(method = "onGuiClosed()V", cancellable = true, at = @At("HEAD"))
    private void onGuiClosed(CallbackInfo info)
    {
        if (SkyBlockcatiaConfig.enableSignSelectionList)
        {
            Keyboard.enableRepeatEvents(false);

            if (SkyBlockEventHandler.isSkyBlock)
            {
                String text = this.that.tileSign.signText[0].getUnformattedText();

                if (!StringUtils.isNullOrEmpty(text))
                {
                    if (NumberUtils.isNumericWithKM(text) && (!SkyBlockcatiaSettings.INSTANCE.auctionBidConfirm && this.isAuctionPrice() || this.isAuctionStartBidSign() || this.isBazaarPrice() || this.isBankWithdraw() || this.isBankDeposit()))
                    {
                        this.globalSelector.add(text);
                    }
                    else if (NumberUtils.isNumeric(text) && this.isBazaarOrder())
                    {
                        this.globalSelector.add(text);
                    }
                    else if (this.isAuctionQuery())
                    {
                        this.globalSelector.add(text);
                    }
                }
            }
            if (!(SkyBlockcatiaSettings.INSTANCE.auctionBidConfirm && this.isAuctionPrice()))
            {
                SignSelectionList.processSignData(this.that.tileSign);
            }
            info.cancel();
        }
    }

    @Inject(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", cancellable = true, at = @At(value = "INVOKE", target = "net/minecraft/tileentity/TileEntitySign.markDirty()V", shift = Shift.AFTER))
    private void actionPerformed(GuiButton button, CallbackInfo info) throws IOException
    {
        if (SkyBlockcatiaSettings.INSTANCE.auctionBidConfirm)
        {
            String text = this.that.tileSign.signText[0].getUnformattedText();

            if (!StringUtils.isNullOrEmpty(text) && NumberUtils.isNumeric(text) && this.isAuctionPrice())
            {
                int price = Integer.parseInt(text);

                if (price >= SkyBlockcatiaSettings.INSTANCE.auctionBidConfirmValue)
                {
                    this.mc.displayGuiScreen(new GuiYesNo(this, LangUtils.translate("message.bid_confirm_title"), LangUtils.translate("message.bid_confirm"), 201));
                    info.cancel();
                }
                else
                {
                    this.that.tileSign.markDirty();
                    SignSelectionList.processSignData(this.that.tileSign);
                    this.globalSelector.add(text);
                }
            }
        }
    }

    @Inject(method = "keyTyped(CI)V", cancellable = true, at = @At("HEAD"))
    private void keyTyped(char typedChar, int keyCode, CallbackInfo info) throws IOException
    {
        if (SkyBlockcatiaConfig.enableOverwriteSignEditing)
        {
            this.textInputUtil.insert(typedChar);
            this.keyPressed(keyCode);
            info.cancel();
        }
    }

    @Inject(method = "drawScreen(IIF)V", cancellable = true, at = @At("HEAD"))
    private void drawScreenPre(int mouseX, int mouseY, float partialTicks, CallbackInfo info)
    {
        if (SkyBlockcatiaConfig.enableOverwriteSignEditing)
        {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, LangUtils.translate("sign.edit"), this.width / 2, 40, 16777215);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.width / 2, 0.0F, 50.0F);
            float f = 93.75F;
            GlStateManager.scale(-f, -f, -f);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            Block block = this.that.tileSign.getBlockType();

            if (block == Blocks.standing_sign)
            {
                float f1 = this.that.tileSign.getBlockMetadata() * 360 / 16.0F;
                GlStateManager.rotate(f1, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, -1.0625F, 0.0F);
            }
            else
            {
                int i = this.that.tileSign.getBlockMetadata();
                float f2 = 0.0F;

                if (i == 2)
                {
                    f2 = 180.0F;
                }

                if (i == 4)
                {
                    f2 = 90.0F;
                }

                if (i == 5)
                {
                    f2 = -90.0F;
                }
                GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, -1.0625F, 0.0F);
            }
            ((IModifiedSign)this.that.tileSign).setSelectionState(this.editLine, this.textInputUtil.getSelectionStart(), this.textInputUtil.getSelectionEnd(), this.updateCounter / 6 % 2 == 0);
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.that.tileSign, -0.5D, -0.75D, -0.5D, 0.0F);
            ((IModifiedSign)this.that.tileSign).resetSelectionState();
            GlStateManager.popMatrix();
            super.drawScreen(mouseX, mouseY, partialTicks);

            if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaConfig.enableSignSelectionList && this.globalSelector != null)
            {
                this.globalSelector.drawScreen(mouseX, mouseY, partialTicks);
            }
            info.cancel();
        }
    }

    @Inject(method = "drawScreen(IIF)V", cancellable = true, at = @At("TAIL"))
    private void drawScreenPost(int mouseX, int mouseY, float partialTicks, CallbackInfo info)
    {
        if (!SkyBlockcatiaConfig.enableOverwriteSignEditing && SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaConfig.enableSignSelectionList && this.globalSelector != null)
        {
            this.globalSelector.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public TextInputUtil getTextInputUtil()
    {
        return this.textInputUtil;
    }

    @Override
    public SignSelectionList getSignSelectionList()
    {
        return this.globalSelector;
    }

    private boolean keyPressed(int keyCode)
    {
        if (keyCode == Keyboard.KEY_UP)
        {
            this.editLine = this.editLine - 1 & 3;
            this.textInputUtil.moveCaretToEnd();
            return true;
        }
        else if (keyCode != Keyboard.KEY_DOWN && keyCode != Keyboard.KEY_RETURN && keyCode != Keyboard.KEY_NUMPADENTER)
        {
            return this.textInputUtil.handleSpecialKey(keyCode);
        }
        else
        {
            this.editLine = this.editLine + 1 & 3;
            this.textInputUtil.moveCaretToEnd();
            return true;
        }
    }

    private boolean isAuctionStartBidSign()
    {
        return this.that.tileSign.signText[2].getUnformattedText().equals("Your auction") && this.that.tileSign.signText[3].getUnformattedText().equals("starting bid");
    }

    private boolean isAuctionPrice()
    {
        return this.that.tileSign.signText[2].getUnformattedText().equals("auction bid") && this.that.tileSign.signText[3].getUnformattedText().equals("amount");
    }

    private boolean isBazaarPrice()
    {
        return this.that.tileSign.signText[2].getUnformattedText().equals("Enter price") && this.that.tileSign.signText[3].getUnformattedText().equals("big nerd");
    }

    private boolean isAuctionQuery()
    {
        return this.that.tileSign.signText[3].getUnformattedText().equals("Enter query");
    }

    private boolean isBankWithdraw()
    {
        return this.that.tileSign.signText[2].getUnformattedText().equals("Enter the amount") && this.that.tileSign.signText[3].getUnformattedText().equals("to withdraw");
    }

    private boolean isBankDeposit()
    {
        return this.that.tileSign.signText[2].getUnformattedText().equals("Enter the amount") && this.that.tileSign.signText[3].getUnformattedText().equals("to deposit");
    }

    private boolean isBazaarOrder()
    {
        return this.that.tileSign.signText[2].getUnformattedText().equals("Enter amount") && this.that.tileSign.signText[3].getUnformattedText().equals("to order");
    }
}