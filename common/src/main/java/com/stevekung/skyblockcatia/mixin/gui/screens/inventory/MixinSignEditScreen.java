package com.stevekung.skyblockcatia.mixin.gui.screens.inventory;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.gui.SignSelectionList;
import com.stevekung.skyblockcatia.utils.skyblock.SBNumberUtils;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.NumberUtils;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.entity.SignBlockEntity;

@Mixin(SignEditScreen.class)
public class MixinSignEditScreen extends Screen
{
    private SignSelectionList globalSelector;

    @Shadow
    @Final
    private String[] messages;

    @Shadow
    @Final
    private SignBlockEntity sign;

    MixinSignEditScreen()
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
                this.globalSelector = new SignSelectionList((SignEditScreen) (Object) this, this.width + 200, this.height, 64, this.height - 64, list, title);
            }
        }
    }

    @Inject(method = "onClose()V", at = @At("HEAD"))
    private void onClose(CallbackInfo info)
    {
        this.sign.setChanged();

        if (SkyBlockcatiaSettings.INSTANCE.auctionBidConfirm && this.isAuctionStartBidSign())
        {
            info.cancel();
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", at = @At("RETURN"))
    private void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo info)
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
    private void onDone()
    {
        SignEditScreen sign = (SignEditScreen) (Object) this;
        String text = this.messages[0];

        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (!StringUtil.isNullOrEmpty(text))
            {
                if (SBNumberUtils.isNumericWithKM(text) && (this.isAuctionPrice() || this.isAuctionStartBidSign() || this.isBazaarPrice() || this.isBankWithdraw() || this.isBankDeposit()))
                {
                    this.globalSelector.add(text, sign);
                }
                else if (NumberUtils.isNumeric(text) && this.isBazaarOrder())
                {
                    this.globalSelector.add(text, sign);
                }
                else if (this.isAuctionQuery())
                {
                    this.globalSelector.add(text, sign);
                }
            }
        }

        if (SkyBlockcatiaSettings.INSTANCE.auctionBidConfirm && !StringUtil.isNullOrEmpty(text) && NumberUtils.isNumeric(text) && this.isAuctionStartBidSign())
        {
            int price = Integer.parseInt(text);

            if (price >= SkyBlockcatiaSettings.INSTANCE.auctionBidConfirmValue)
            {
                this.minecraft.setScreen(new ConfirmScreen(confirm ->
                {
                    if (confirm)
                    {
                        this.confirmSign();
                    }
                    else
                    {
                        this.minecraft.setScreen(this);
                    }
                }, LangUtils.translate("message.bid_confirm_title"), LangUtils.translate("message.bid_confirm")));
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
        this.sign.setChanged();
        SignSelectionList.processSignData(this.sign);
        this.minecraft.setScreen(null);
    }

    private boolean isAuctionStartBidSign()
    {
        return this.messages[2].equals("Your auction") && this.messages[3].equals("starting bid");
    }

    private boolean isAuctionPrice()
    {
        return this.messages[2].equals("auction bid") && this.messages[3].equals("amount");
    }

    private boolean isBazaarPrice()
    {
        return this.messages[2].equals("Enter price") && this.messages[3].equals("big nerd");
    }

    private boolean isAuctionQuery()
    {
        return this.messages[3].equals("Enter query");
    }

    private boolean isBankWithdraw()
    {
        return this.messages[2].equals("Enter the amount") && this.messages[3].equals("to withdraw");
    }

    private boolean isBankDeposit()
    {
        return this.messages[2].equals("Enter the amount") && this.messages[3].equals("to deposit");
    }

    private boolean isBazaarOrder()
    {
        return this.messages[2].equals("Enter amount") && this.messages[3].equals("to order");
    }
}