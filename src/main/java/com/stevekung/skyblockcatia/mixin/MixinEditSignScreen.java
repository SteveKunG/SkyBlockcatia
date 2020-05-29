package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.gui.SignSelectionList;
import com.stevekung.skyblockcatia.utils.skyblock.SBNumberUtils;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.NumberUtils;

import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;

@Mixin(EditSignScreen.class)
public abstract class MixinEditSignScreen extends Screen
{
    private final EditSignScreen that = (EditSignScreen) (Object) this;
    private SignSelectionList auctionPriceSelector;
    private SignSelectionList auctionQuerySelector;
    private SignSelectionList withdrawSelector;
    private SignSelectionList depositSelector;
    private SignSelectionList bazaarOrderSelector;

    public MixinEditSignScreen(ITextComponent title)
    {
        super(title);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    private void init(CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (this.isAuctionSign())
            {
                this.auctionPriceSelector = new SignSelectionList(this.that, this.width + 200, this.height, 64, this.height - 64, SignSelectionList.AUCTION_PRICES, "Select price");
                this.children.add(this.auctionPriceSelector);
            }
            if (this.isAuctionQuery())
            {
                this.auctionQuerySelector = new SignSelectionList(this.that, this.width + 200, this.height, 64, this.height - 64, SignSelectionList.AUCTION_QUERIES, "Select query");
                this.children.add(this.auctionQuerySelector);
            }
            if (this.isBankWithdraw())
            {
                this.withdrawSelector = new SignSelectionList(this.that, this.width + 200, this.height, 64, this.height - 64, SignSelectionList.BANK_WITHDRAW, "Select withdraw");
                this.children.add(this.withdrawSelector);
            }
            if (this.isBankDeposit())
            {
                this.depositSelector = new SignSelectionList(this.that, this.width + 200, this.height, 64, this.height - 64, SignSelectionList.BANK_DEPOSIT, "Select deposit");
                this.children.add(this.depositSelector);
            }
            if (this.isBarzaarOrder())
            {
                this.bazaarOrderSelector = new SignSelectionList(this.that, this.width + 200, this.height, 64, this.height - 64, SignSelectionList.BAZAAR_ORDER, "Bazaar Order");
                this.children.add(this.bazaarOrderSelector);
            }
        }
    }

    @Inject(method = "removed()V", at = @At("HEAD"))
    private void removed(CallbackInfo info)
    {
        this.that.tileSign.markDirty();

        if (SBExtendedConfig.INSTANCE.auctionBidConfirm && this.isAuctionSign())
        {
            info.cancel();
        }
    }

    @Inject(method = "render(IIF)V", at = @At("RETURN"))
    private void render(int mouseX, int mouseY, float partialTicks, CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (this.isAuctionSign())
            {
                this.auctionPriceSelector.render(mouseX, mouseY, partialTicks);
            }
            if (this.isAuctionQuery())
            {
                this.auctionQuerySelector.render(mouseX, mouseY, partialTicks);
            }
            if (this.isBankWithdraw())
            {
                this.withdrawSelector.render(mouseX, mouseY, partialTicks);
            }
            if (this.isBankDeposit())
            {
                this.depositSelector.render(mouseX, mouseY, partialTicks);
            }
            if (this.isBarzaarOrder())
            {
                this.bazaarOrderSelector.render(mouseX, mouseY, partialTicks);
            }
        }
    }

    @Overwrite
    private void close()
    {
        String text = this.that.tileSign.signText[0].getString();

        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (!StringUtils.isNullOrEmpty(text))
            {
                if (SBNumberUtils.isNumericWithKM(text))
                {
                    if (this.isBankWithdraw())
                    {
                        this.withdrawSelector.add(SignSelectionList.BANK_WITHDRAW, text, this.that);
                    }
                    if (this.isBankDeposit())
                    {
                        this.depositSelector.add(SignSelectionList.BANK_DEPOSIT, text, this.that);
                    }
                }
                if (NumberUtils.isNumeric(text))
                {
                    if (this.isAuctionSign())
                    {
                        this.auctionPriceSelector.add(SignSelectionList.AUCTION_PRICES, text, this.that);
                    }
                    if (this.isBarzaarOrder())
                    {
                        this.bazaarOrderSelector.add(SignSelectionList.BAZAAR_ORDER, text, this.that);
                    }
                }
                if (this.isAuctionQuery())
                {
                    this.auctionQuerySelector.add(SignSelectionList.AUCTION_QUERIES, text, this.that);
                }
            }
        }

        if (SBExtendedConfig.INSTANCE.auctionBidConfirm && NumberUtils.isNumeric(text) && this.isAuctionSign())
        {
            int price = Integer.parseInt(text);

            if (price >= SBExtendedConfig.INSTANCE.auctionBidConfirmValue)
            {
                this.minecraft.displayGuiScreen(new ConfirmScreen(confirm ->
                {
                    if (confirm)
                    {
                        this.confirmSign();
                    }
                    else
                    {
                        this.minecraft.displayGuiScreen(this);
                    }
                }
                , LangUtils.translateComponent("message.bid_confirm_title"), LangUtils.translateComponent("message.bid_confirm")));
            }
            else
            {
                this.confirmSign();
            }
        }
        else
        {
            this.confirmSign();
        }
    }

    private void confirmSign()
    {
        this.that.tileSign.markDirty();
        SignSelectionList.processSignData(this.that.tileSign);
        this.minecraft.displayGuiScreen(null);
    }

    private boolean isAuctionSign()
    {
        return this.that.tileSign.signText[2].getString().equals("Your auction") && this.that.tileSign.signText[3].getString().equals("starting bid");
    }

    private boolean isAuctionQuery()
    {
        return this.that.tileSign.signText[3].getString().equals("Enter query");
    }

    private boolean isBankWithdraw()
    {
        return this.that.tileSign.signText[2].getString().equals("Enter the amount") && this.that.tileSign.signText[3].getString().equals("to withdraw");
    }

    private boolean isBankDeposit()
    {
        return this.that.tileSign.signText[2].getString().equals("Enter the amount") && this.that.tileSign.signText[3].getString().equals("to deposit");
    }

    private boolean isBarzaarOrder()
    {
        return this.that.tileSign.signText[2].getString().equals("Enter amount") && this.that.tileSign.signText[3].getString().equals("to order");
    }
}