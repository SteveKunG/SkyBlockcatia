package com.stevekung.skyblockcatia.gui;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.NumberUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class SignSelectionList extends ObjectSelectionList<SignSelectionList.Entry>
{
    public static final List<SignSelectionList.Entry> AUCTION_STARTING_BID_PRICES = Lists.newArrayList();
    public static final List<SignSelectionList.Entry> AUCTION_BID_PRICES = Lists.newArrayList();
    public static final List<SignSelectionList.Entry> AUCTION_QUERIES = Lists.newArrayList();
    public static final List<SignSelectionList.Entry> BANK_WITHDRAW = Lists.newArrayList();
    public static final List<SignSelectionList.Entry> BANK_DEPOSIT = Lists.newArrayList();
    public static final List<SignSelectionList.Entry> BAZAAR_ORDER = Lists.newArrayList();
    public static final List<SignSelectionList.Entry> BAZAAR_PRICE = Lists.newArrayList();
    private final String title;
    private final List<SignSelectionList.Entry> list;
    private final SignEditScreen parent;

    public SignSelectionList(SignEditScreen parent, int width, int height, int top, int bottom, List<SignSelectionList.Entry> list, String title)
    {
        super(Minecraft.getInstance(), width, height, top, bottom, 16);
        this.title = title;
        this.parent = parent;
        list = list.stream().distinct().collect(Collectors.toList());
        this.list = list;

        /*List<Entry> test = this.list.stream().distinct().collect(Collectors.toList());TODO

        if (this.getItemCount() > 5)
        {
            test.remove(test.size() - 1);
            this.list.remove(test.size() - 1);
        }*/
        for (SignSelectionList.Entry element : list)
        {
            this.addEntry(new SignSelectionList.Entry(element.getValue(), parent));
        }
        Collections.reverse(this.children());
    }

    @Override
    protected boolean isFocused()
    {
        return this.parent.getFocused() == this;
    }

    @Override
    public int getRowWidth()
    {
        return 100;
    }

    @Override
    protected int getMaxPosition()
    {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        int k = this.getRowLeft();
        int l = this.y0 + 4 - (int) this.getScrollAmount();
        this.renderList(poseStack, k, l, mouseX, mouseY, partialTicks);
        this.minecraft.font.draw(poseStack, this.title + ":", k, this.y0 - 12, 16777215);
    }

    public void add(String value, SignEditScreen parent)
    {
        this.list.add(new SignSelectionList.Entry(value, parent));
    }

    public static void clearAll()
    {
        SignSelectionList.AUCTION_STARTING_BID_PRICES.clear();
        SignSelectionList.AUCTION_BID_PRICES.clear();
        SignSelectionList.AUCTION_QUERIES.clear();
        SignSelectionList.BANK_WITHDRAW.clear();
        SignSelectionList.BANK_DEPOSIT.clear();
        SignSelectionList.BAZAAR_ORDER.clear();
        SignSelectionList.BAZAAR_PRICE.clear();
    }

    public class Entry extends ObjectSelectionList.Entry<SignSelectionList.Entry>
    {
        private final Minecraft mc;
        private final SignEditScreen parent;
        private final String value;
        private long lastClicked;

        public Entry(String value, SignEditScreen parent)
        {
            this.mc = Minecraft.getInstance();
            this.value = value;
            this.parent = parent;
        }

        @Override
        public void render(PoseStack poseStack, int index, int rowTop, int rowLeft, int rowWidth, int itemHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks)
        {
            this.mc.font.draw(poseStack, this.value, rowLeft + 2, rowTop + 2, 16777215);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseEvent)
        {
            SignBlockEntity sign = ((SignEditScreen) this.mc.screen).sign;

            if (mouseEvent == 0)
            {
                sign.setChanged();

                if (Util.getMillis() - this.lastClicked < 250L)
                {
                    if (SkyBlockcatiaSettings.INSTANCE.auctionBidConfirm && NumberUtils.isNumeric(this.value))
                    {
                        int price = Integer.parseInt(this.value);

                        if (price >= SkyBlockcatiaSettings.INSTANCE.auctionBidConfirmValue)
                        {
                            this.mc.setScreen(new ConfirmScreen(confirm ->
                            {
                                if (confirm)
                                {
                                    this.confirmSign(sign);
                                }
                                else
                                {
                                    this.mc.setScreen(this.parent);
                                }
                            }, LangUtils.translate("message.bid_confirm_title"), LangUtils.translate("message.bid_confirm")));
                        }
                        else
                        {
                            this.confirmSign(sign);
                        }
                    }
                    else
                    {
                        this.confirmSign(sign);
                    }
                    return true;
                }
                SignSelectionList.this.setSelected(this);
                sign.setMessage(0, TextComponentUtils.component(this.value));
                ((SignEditScreen) this.mc.screen).signField.setCursorToEnd();
                this.lastClicked = Util.getMillis();
                return true;
            }
            else
            {
                return false;
            }
        }

        private void confirmSign(SignBlockEntity sign)
        {
            SignSelectionList.processSignData(sign);
            this.mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.mc.setScreen(null);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof SignSelectionList.Entry))
            {
                return false;
            }
            if (obj == this)
            {
                return true;
            }
            SignSelectionList.Entry other = (SignSelectionList.Entry) obj;
            return new EqualsBuilder().append(this.value, other.value).isEquals();
        }

        @Override
        public int hashCode()
        {
            return new HashCodeBuilder().append(this.value).toHashCode();
        }

        public String getValue()
        {
            return this.value;
        }
    }

    public static void processSignData(SignBlockEntity sign)
    {
        ClientPacketListener clientplaynethandler = Minecraft.getInstance().getConnection();

        if (clientplaynethandler != null)
        {
            clientplaynethandler.send(new ServerboundSignUpdatePacket(sign.getBlockPos(), sign.getMessage(0).getString(), sign.getMessage(1).getString(), sign.getMessage(2).getString(), sign.getMessage(3).getString()));
        }
        sign.setEditable(true);
    }
}