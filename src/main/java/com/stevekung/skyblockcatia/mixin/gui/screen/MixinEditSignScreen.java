package com.stevekung.skyblockcatia.mixin.gui.screen;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.gui.SignSelectionList;
import com.stevekung.skyblockcatia.utils.skyblock.SBNumberUtils;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.NumberUtils;

import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.StringUtils;

@Mixin(EditSignScreen.class)
public class MixinEditSignScreen extends Screen
{
    private final EditSignScreen that = (EditSignScreen) (Object) this;
    private SignSelectionList globalSelector;

    @Shadow
    @Final
    private String[] field_238846_r_;

    private MixinEditSignScreen()
    {
        super(null);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    private void init(CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock)
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
                this.globalSelector = new SignSelectionList(this.that, this.width + 200, this.height, 64, this.height - 64, list, title);
            }
        }
    }

    @Inject(method = "closeScreen()V", at = @At("HEAD"))
    private void closeScreen(CallbackInfo info)
    {
        this.that.tileSign.markDirty();

        if (SkyBlockcatiaSettings.INSTANCE.auctionBidConfirm && this.isAuctionStartBidSign())
        {
            info.cancel();
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;IIF)V", at = @At("RETURN"))
    private void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (this.globalSelector != null)
            {
                this.globalSelector.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Overwrite
    private void close()
    {
        String text = this.field_238846_r_[0];

        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (!StringUtils.isNullOrEmpty(text))
            {
                if (SBNumberUtils.isNumericWithKM(text) && (this.isAuctionPrice() || this.isAuctionStartBidSign() || this.isBazaarPrice() || this.isBankWithdraw() || this.isBankDeposit()))
                {
                    this.globalSelector.add(text, this.that);
                }
                else if (NumberUtils.isNumeric(text) && this.isBazaarOrder())
                {
                    this.globalSelector.add(text, this.that);
                }
                else if (this.isAuctionQuery())
                {
                    this.globalSelector.add(text, this.that);
                }
            }
        }

        if (SkyBlockcatiaSettings.INSTANCE.auctionBidConfirm && !StringUtils.isNullOrEmpty(text) && NumberUtils.isNumeric(text) && this.isAuctionStartBidSign())
        {
            int price = Integer.parseInt(text);

            if (price >= SkyBlockcatiaSettings.INSTANCE.auctionBidConfirmValue)
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
                , LangUtils.translate("message.bid_confirm_title"), LangUtils.translate("message.bid_confirm")));
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

    private boolean isAuctionStartBidSign()
    {
        return this.field_238846_r_[2].equals("Your auction") && this.field_238846_r_[3].equals("starting bid");
    }

    private boolean isAuctionPrice()
    {
        return this.field_238846_r_[2].equals("auction bid") && this.field_238846_r_[3].equals("amount");
    }

    private boolean isBazaarPrice()
    {
        return this.field_238846_r_[2].equals("Enter price") && this.field_238846_r_[3].equals("big nerd");
    }

    private boolean isAuctionQuery()
    {
        return this.field_238846_r_[3].equals("Enter query");
    }

    private boolean isBankWithdraw()
    {
        return this.field_238846_r_[2].equals("Enter the amount") && this.field_238846_r_[3].equals("to withdraw");
    }

    private boolean isBankDeposit()
    {
        return this.field_238846_r_[2].equals("Enter the amount") && this.field_238846_r_[3].equals("to deposit");
    }

    private boolean isBazaarOrder()
    {
        return this.field_238846_r_[2].equals("Enter amount") && this.field_238846_r_[3].equals("to order");
    }
}