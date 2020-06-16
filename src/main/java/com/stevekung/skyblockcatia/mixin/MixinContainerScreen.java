package com.stevekung.skyblockcatia.mixin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.WordUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.gui.screen.SkyBlockProfileViewerScreen;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.ITradeScreen;
import com.stevekung.skyblockcatia.utils.SearchMode;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;
import com.stevekung.stevekungslib.client.gui.NumberWidget;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.CommonUtils;
import com.stevekung.stevekungslib.utils.JsonUtils;
import com.stevekung.stevekungslib.utils.client.ClientUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.CommandSuggestionHelper;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

@Mixin(ContainerScreen.class)
public abstract class MixinContainerScreen<T extends Container> extends Screen implements ITradeScreen
{
    private final ContainerScreen that = (ContainerScreen) (Object) this;
    private static final ImmutableList<String> IGNORE_ITEMS = ImmutableList.of(" ", "Recipe Required", "Item To Upgrade", "Rune to Sacrifice", "Runic Pedestal", "Final confirmation", "Quick Crafting Slot", "Enchant Item", "Item to Sacrifice", "Anvil");
    private static final ImmutableList<String> INVENTORY_LIST = ImmutableList.of("SkyBlock Menu", "Skill", "Collection", "Crafted Minions", "Recipe", "Quest Log", "Fairy Souls Guide", "Calendar and Events", "Settings", "Profiles Management", "Fast Travel", "SkyBlock Profile", "'s Profile", "' Profile", "Bank", "Harp");
    private static final ImmutableList<String> ITEM_LIST = ImmutableList.of(TextFormatting.GREEN + "Go Back", TextFormatting.RED + "Close");
    private SearchMode mode = SearchMode.SIMPLE;
    private String fandomUrl;

    // ChatScreen fields
    private String historyBuffer = "";
    private int sentHistoryCursor = -1;
    protected TextFieldWidget inputField;
    private CommandSuggestionHelper commandSuggestionHelper;

    // Auction
    private NumberWidget priceSearch;

    public MixinContainerScreen(T screenContainer, PlayerInventory inv, ITextComponent title)
    {
        super(title);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    private void init(CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
        {
            if (this.isAuctionBrowser())
            {
                this.minecraft.keyboardListener.enableRepeatEvents(true);
                this.priceSearch = new NumberWidget(this.font, this.that.getGuiLeft() + 180, this.that.getGuiTop() + 40, 100, 20);
                this.priceSearch.setText(MainEventHandler.auctionPrice);
                this.priceSearch.setCanLoseFocus(true);
                this.children.add(this.priceSearch);
                this.setFocusedDefault(this.priceSearch);
            }
            if (this.isChatableGui())
            {
                this.minecraft.keyboardListener.enableRepeatEvents(true);
                this.sentHistoryCursor = this.minecraft.ingameGUI.getChatGUI().getSentMessages().size();
                this.inputField = new TextFieldWidget(this.font, 4, this.height - 12, this.width - 4, 12, I18n.format("chat.editBox"));
                this.inputField.setMaxStringLength(256);
                this.inputField.setEnableBackgroundDrawing(false);
                this.inputField.setText("");
                this.inputField.setResponder(text -> this.setCommandResponder());
                this.children.add(this.inputField);
                this.commandSuggestionHelper = new CommandSuggestionHelper(this.minecraft, this, this.inputField, this.font, false, false, 1, 10, true, -805306368);
                this.commandSuggestionHelper.init();
                this.setFocusedDefault(this.inputField);
            }
            if (this.isPeopleAuction())
            {
                this.addButton(new Button(this.that.getGuiLeft() + 180, this.that.getGuiTop() + 70, 70, 20, "Copy Seller", button ->
                {
                    String title = this.title.getUnformattedComponentText();
                    ClientUtils.printClientMessage(JsonUtils.create("Copied seller auction command!").applyTextStyle(TextFormatting.GREEN));
                    this.minecraft.keyboardListener.setClipboardString("/ah " + title.replace(title.substring(title.indexOf('\'')), ""));
                }));
                this.addButton(new Button(this.that.getGuiLeft() + 180, this.that.getGuiTop() + 92, 70, 20, "View API", button ->
                {
                    String title = this.title.getUnformattedComponentText();
                    this.minecraft.displayGuiScreen(new SkyBlockProfileViewerScreen(SkyBlockProfileViewerScreen.GuiState.PLAYER, title.replace(title.substring(title.indexOf('\'')), ""), "", ""));
                }));
            }
            if (this.isPeopleProfile())
            {
                this.addButton(new Button(this.that.getGuiLeft() + 180, this.that.getGuiTop() + 40, 70, 20, "View API", button ->
                {
                    String title = this.title.getUnformattedComponentText();
                    this.minecraft.displayGuiScreen(new SkyBlockProfileViewerScreen(SkyBlockProfileViewerScreen.GuiState.PLAYER, title.replace(title.substring(title.indexOf('\'')), ""), "", ""));
                }));
            }
        }
    }

    @Inject(method = "mouseClicked(DDI)Z", cancellable = true, at = @At("HEAD"))
    private void mouseClicked(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable<Boolean> info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
        {
            if (this.isChatableGui())
            {
                if (this.commandSuggestionHelper.onClick((int)mouseX, (int)mouseY, mouseButton))
                {
                    this.inputField.setFocused2(true);
                    info.setReturnValue(true);
                }
                else
                {
                    if (this.inputField.mouseClicked(mouseX, mouseY, mouseButton))
                    {
                        info.setReturnValue(true);
                    }
                }
            }
            else if (this.isAuctionBrowser() && this.priceSearch.mouseClicked(mouseX, mouseY, mouseButton))
            {
                info.setReturnValue(true);
            }
        }
    }

    @Inject(method = "removed()V", at = @At("RETURN"))
    private void removed(CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
        {
            if (this.isChatableGui() || this.isAuctionBrowser())
            {
                this.minecraft.keyboardListener.enableRepeatEvents(false);
            }
            if (this.isAuctionBrowser())
            {
                MainEventHandler.auctionPrice = this.priceSearch.getText();
            }
        }
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    private void tick(CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
        {
            if (this.isChatableGui())
            {
                this.inputField.tick();
            }
            if (this.isAuctionBrowser())
            {
                MainEventHandler.auctionPrice = this.priceSearch.getText();
                this.priceSearch.tick();
            }
        }
    }

    @Inject(method = "render(IIF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screen/Screen.render(IIF)V", shift = Shift.BEFORE))
    private void render(int mouseX, int mouseY, float partialTicks, CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
        {
            if (this.isChatableGui())
            {
                if (MainEventHandler.showChat)
                {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.disableAlphaTest();
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef(0.0F, this.height - 48, 0.0F);
                    this.renderChat();
                    RenderSystem.popMatrix();
                }
                AbstractGui.fill(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
                this.commandSuggestionHelper.render(mouseX, mouseY);
                this.inputField.render(mouseX, mouseY, partialTicks);
            }
            if (this.priceSearch != null && this.isAuctionBrowser())
            {
                this.drawString(this.font, "Search for price:", this.that.getGuiLeft() + 180, this.that.getGuiTop() + 26, 10526880);
                this.priceSearch.render(mouseX, mouseY, partialTicks);
            }
        }
    }

    @Inject(method = "keyPressed(III)Z", cancellable = true, at = @At("HEAD"))
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info)
    {
        if (!this.isChatableGui() && !this.isAuctionBrowser() && this.that.getSlotUnderMouse() != null && this.that.getSlotUnderMouse().getHasStack())
        {
            if (keyCode == KeyBindingHandler.KEY_SB_VIEW_RECIPE.getKey().getKeyCode())
            {
                this.viewRecipe(this.minecraft.player, this.that.getSlotUnderMouse());
                info.setReturnValue(true);
            }
            else if (keyCode == KeyBindingHandler.KEY_SB_OPEN_WIKI.getKey().getKeyCode())
            {
                ItemStack itemStack = this.that.getSlotUnderMouse().getStack();

                if (!itemStack.isEmpty() && itemStack.hasTag() && itemStack.getTag().contains("ExtraAttributes"))
                {
                    String itemId = itemStack.getTag().getCompound("ExtraAttributes").getString("id").toLowerCase().replace("_", " ");
                    itemId = WordUtils.capitalize(itemId);
                    this.fandomUrl = "https://hypixel-skyblock.fandom.com/wiki/" + itemId.replace(" ", "_");
                    this.minecraft.displayGuiScreen(new ConfirmOpenLinkScreen(this::openFandom, this.fandomUrl, true));
                    info.setReturnValue(true);
                }
            }
        }

        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
        {
            if (this.isChatableGui())
            {
                if (this.commandSuggestionHelper.onKeyPressed(keyCode, scanCode, modifiers))
                {
                    info.setReturnValue(true);
                }
                else if (super.keyPressed(keyCode, scanCode, modifiers))
                {
                    info.setReturnValue(true);
                }
                else if (this.inputField.isFocused())
                {
                    if (keyCode == 256)
                    {
                        this.inputField.setFocused2(false);
                    }
                    if (keyCode != 257 && keyCode != 335)
                    {
                        if (keyCode == 265)
                        {
                            this.getSentHistory(-1);
                            info.setReturnValue(true);
                        }
                        else if (keyCode == 264)
                        {
                            this.getSentHistory(1);
                            info.setReturnValue(true);
                        }
                        else
                        {
                            info.setReturnValue(false);
                        }
                    }
                    else
                    {
                        String text = this.inputField.getText().trim();

                        if (!text.isEmpty())
                        {
                            this.sendMessage(text);
                            this.sentHistoryCursor = this.minecraft.ingameGUI.getChatGUI().getSentMessages().size();
                            this.inputField.setText("");
                        }
                        info.setReturnValue(true);
                    }
                }
                else
                {
                    if (this.that.getSlotUnderMouse() != null && this.that.getSlotUnderMouse().getHasStack())
                    {
                        if (keyCode == KeyBindingHandler.KEY_SB_VIEW_RECIPE.getKey().getKeyCode())
                        {
                            this.viewRecipe(this.minecraft.player, this.that.getSlotUnderMouse());
                            info.setReturnValue(true);
                        }
                        else if (keyCode == KeyBindingHandler.KEY_SB_OPEN_WIKI.getKey().getKeyCode())
                        {
                            ItemStack itemStack = this.that.getSlotUnderMouse().getStack();

                            if (!itemStack.isEmpty() && itemStack.hasTag() && itemStack.getTag().contains("ExtraAttributes"))
                            {
                                String itemId = itemStack.getTag().getCompound("ExtraAttributes").getString("id").toLowerCase().replace("_", " ");
                                itemId = WordUtils.capitalize(itemId);
                                this.fandomUrl = "https://hypixel-skyblock.fandom.com/wiki/" + itemId.replace(" ", "_");
                                this.minecraft.displayGuiScreen(new ConfirmOpenLinkScreen(this::openFandom, this.fandomUrl, true));
                                info.setReturnValue(true);
                            }
                            info.setReturnValue(false);
                        }
                    }
                }
            }
            else if (this.isAuctionBrowser())
            {
                if (super.keyPressed(keyCode, scanCode, modifiers))
                {
                    info.setReturnValue(true);
                }
                else if (this.priceSearch.isFocused())
                {
                    if (keyCode == 256)
                    {
                        this.priceSearch.setFocused2(false);
                    }
                    info.setReturnValue(false);
                }
                else
                {
                    if (this.that.getSlotUnderMouse() != null && this.that.getSlotUnderMouse().getHasStack())
                    {
                        if (keyCode == KeyBindingHandler.KEY_SB_VIEW_RECIPE.getKey().getKeyCode())
                        {
                            this.viewRecipe(this.minecraft.player, this.that.getSlotUnderMouse());
                            info.setReturnValue(true);
                        }
                        else if (keyCode == KeyBindingHandler.KEY_SB_OPEN_WIKI.getKey().getKeyCode())
                        {
                            ItemStack itemStack = this.that.getSlotUnderMouse().getStack();

                            if (!itemStack.isEmpty() && itemStack.hasTag() && itemStack.getTag().contains("ExtraAttributes"))
                            {
                                String itemId = itemStack.getTag().getCompound("ExtraAttributes").getString("id").toLowerCase().replace("_", " ");
                                itemId = WordUtils.capitalize(itemId);
                                this.fandomUrl = "https://hypixel-skyblock.fandom.com/wiki/" + itemId.replace(" ", "_");
                                this.minecraft.displayGuiScreen(new ConfirmOpenLinkScreen(this::openFandom, this.fandomUrl, true));
                                info.setReturnValue(true);
                            }
                            info.setReturnValue(false);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "handleMouseClick(Lnet/minecraft/inventory/container/Slot;IILnet/minecraft/inventory/container/ClickType;)V", cancellable = true, at = @At("HEAD"))
    private void handleMouseClick(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo info)
    {
        if (slot != null && SkyBlockEventHandler.isSkyBlock)
        {
            ItemStack itemStack = slot.getStack();

            if (!itemStack.isEmpty())
            {
                if (this.ignoreNullItem(itemStack, IGNORE_ITEMS))
                {
                    info.cancel();
                }
                if (itemStack.hasTag() && itemStack.getTag().contains("ExtraAttributes"))
                {
                    String id = itemStack.getTag().getCompound("ExtraAttributes").getString("id");

                    if (id.equals("SKYBLOCK_MENU"))
                    {
                        this.minecraft.playerController.windowClick(that.getContainer().windowId, slotId, 2, ClickType.CLONE, this.minecraft.player);
                        info.cancel();
                    }
                }
            }

            if (this.that instanceof ChestScreen)
            {
                String name = itemStack.getDisplayName().getFormattedText();

                if (mouseButton == 0 && type == ClickType.PICKUP && (MainEventHandler.isSuitableForGUI(INVENTORY_LIST, this.getTitle()) || ITEM_LIST.stream().anyMatch(itemName -> name.equals(itemName))))
                {
                    this.minecraft.playerController.windowClick(that.getContainer().windowId, slotId, 2, ClickType.CLONE, this.minecraft.player);
                    info.cancel();
                }
                if (mouseButton == 2 && type == ClickType.CLONE && this.canViewSeller())
                {
                    if (!slot.getStack().isEmpty() && slot.getStack().hasTag())
                    {
                        CompoundNBT compound = slot.getStack().getTag().getCompound("display");

                        if (compound.getTagId("Lore") == Constants.NBT.TAG_LIST)
                        {
                            ListNBT list = compound.getList("Lore", Constants.NBT.TAG_STRING);

                            for (int j1 = 0; j1 < list.size(); ++j1)
                            {
                                String lore = TextFormatting.getTextWithoutFormattingCodes(ITextComponent.Serializer.fromJson(list.getString(j1)).getString());

                                if (lore.startsWith("Seller: "))
                                {
                                    this.minecraft.player.sendChatMessage("/ah " + lore.replaceAll("Seller: ?(?:\\[VIP?\\u002B{0,1}\\]|\\[MVP?\\u002B{0,2}\\]|\\[YOUTUBE\\]){0,1} ", ""));
                                }
                            }
                        }
                    }
                    info.cancel();
                }
            }
        }
    }

    @Inject(method = "drawSlot(Lnet/minecraft/inventory/container/Slot;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/ItemRenderer.renderItemAndEffectIntoGUI(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;II)V"))
    private void renderRarity(Slot slot, CallbackInfo info)
    {
        if (SBExtendedConfig.INSTANCE.showItemRarity)
        {
            SBRenderUtils.renderRarity(slot.getStack(), slot.xPos, slot.yPos);
        }
    }

    @Inject(method = "drawSlot(Lnet/minecraft/inventory/container/Slot;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/ItemRenderer.renderItemOverlayIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"))
    private void renderBids(Slot slot, CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
        {
            if (this.isRenderBids())
            {
                this.renderBids(slot);
            }
        }
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
        {
            if (this.isChatableGui())
            {
                String text = this.inputField.getText();
                boolean focus = this.inputField.isFocused();
                super.resize(mc, width, height);
                this.inputField.setText(text);
                this.inputField.setFocused2(focus);
                this.commandSuggestionHelper.init();
            }
            else if (this.isAuctionBrowser())
            {
                boolean focus = this.priceSearch.isFocused();
                super.resize(mc, width, height);
                this.priceSearch.setFocused2(focus);
            }
            else
            {
                super.resize(mc, width, height);
            }
        }
        else
        {
            super.resize(mc, width, height);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen && this.isChatableGui())
        {
            if (scrollDelta > 1.0D)
            {
                scrollDelta = 1.0D;
            }
            if (scrollDelta < -1.0D)
            {
                scrollDelta = -1.0D;
            }

            if (this.commandSuggestionHelper.onScroll(scrollDelta))
            {
                return true;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return super.mouseScrolled(mouseX, mouseY, scrollDelta);
        }
    }

    @Override
    public NumberWidget getNumberField()
    {
        return this.priceSearch;
    }

    private boolean isAuctionBrowser()
    {
        String title = this.title.getUnformattedComponentText();
        return title.equals("Auctions Browser") || title.endsWith("'s Auctions");
    }

    private boolean isRenderBids()
    {
        String title = this.title.getUnformattedComponentText();
        return title.equals("Auctions Browser") || title.equals("Manage Auctions") || title.equals("Your Bids") || title.endsWith("'s Auctions");
    }

    private boolean isPeopleAuction()
    {
        String title = this.title.getUnformattedComponentText();
        return title.endsWith("'s Auctions");
    }

    private boolean isPeopleProfile()
    {
        String title = this.title.getUnformattedComponentText();
        return title.endsWith("'s Profile") || title.endsWith("' Profile");
    }

    private boolean isChatableGui()
    {
        String title = this.title.getUnformattedComponentText();
        return MainEventHandler.CHATABLE_LIST.stream().anyMatch(invName -> title.contains(invName));
    }

    private boolean canViewSeller()
    {
        String title = this.title.getUnformattedComponentText();
        return title.equals("Auctions Browser") || title.equals("Your Bids") || title.equals("Auction View");
    }

    private void setCommandResponder()
    {
        this.commandSuggestionHelper.func_228124_a_(false);
        this.commandSuggestionHelper.init();
    }

    private boolean ignoreNullItem(ItemStack itemStack, List<String> ignores)
    {
        return ignores.stream().anyMatch(name -> itemStack.getDisplayName().getString().equals(name));
    }

    private void getSentHistory(int msgPos)
    {
        int i = this.sentHistoryCursor + msgPos;
        int j = this.minecraft.ingameGUI.getChatGUI().getSentMessages().size();
        i = MathHelper.clamp(i, 0, j);

        if (i != this.sentHistoryCursor)
        {
            if (i == j)
            {
                this.sentHistoryCursor = j;
                this.inputField.setText(this.historyBuffer);
            }
            else
            {
                if (this.sentHistoryCursor == j)
                {
                    this.historyBuffer = this.inputField.getText();
                }
                this.inputField.setText(this.minecraft.ingameGUI.getChatGUI().getSentMessages().get(i));
                this.commandSuggestionHelper.func_228124_a_(false);
                this.sentHistoryCursor = i;
            }
        }
    }

    private void renderChat()
    {
        NewChatGui chat = this.minecraft.ingameGUI.getChatGUI();

        if (chat.isChatVisible())
        {
            int i = chat.getLineCount();
            int j = chat.drawnChatLines.size();

            if (j > 0)
            {
                double d0 = chat.getScale();
                RenderSystem.pushMatrix();
                RenderSystem.translatef(2.0F, 8.0F, 0.0F);
                RenderSystem.scaled(d0, d0, 1.0D);
                double d1 = this.minecraft.gameSettings.chatOpacity * 0.9F + 0.1F;

                for (int i1 = 0; i1 + chat.scrollPos < chat.drawnChatLines.size() && i1 < i; ++i1)
                {
                    ChatLine chatline = chat.drawnChatLines.get(i1 + chat.scrollPos);

                    if (chatline != null)
                    {
                        int j1 = this.minecraft.ingameGUI.getTicks() - chatline.getUpdatedCounter();

                        if (j1 < 200)
                        {
                            double d3 = NewChatGui.getLineBrightness(j1);
                            int l1 = (int)(255.0D * d3 * d1);
                            if (l1 > 3)
                            {
                                int k2 = -i1 * 9;
                                String s = chatline.getChatComponent().getFormattedText();
                                RenderSystem.enableBlend();
                                this.font.drawStringWithShadow(s, 0.0F, k2 - 8, 16777215 + (l1 << 24));
                                RenderSystem.disableAlphaTest();
                                RenderSystem.disableBlend();
                            }
                        }
                    }
                }
                RenderSystem.popMatrix();
            }
        }
    }

    private void renderBids(Slot slot)
    {
        if (!slot.getStack().isEmpty() && slot.getStack().hasTag())
        {
            CompoundNBT compound = slot.getStack().getTag().getCompound("display");

            if (compound.getTagId("Lore") == Constants.NBT.TAG_LIST)
            {
                ListNBT list = compound.getList("Lore", Constants.NBT.TAG_STRING);

                for (int j1 = 0; j1 < list.size(); ++j1)
                {
                    int slotLeft = slot.xPos;
                    int slotTop = slot.yPos;
                    int slotRight = slotLeft + 16;
                    int slotBottom = slotTop + 16;
                    String lore = TextFormatting.getTextWithoutFormattingCodes(ITextComponent.Serializer.fromJson(list.getString(j1)).getString());
                    Matcher matcher = Pattern.compile("(?:(?:Top|Starting) bid|Buy it now): (?<coin>[0-9,]+) coins").matcher(lore);
                    int red = ColorUtils.to32BitColor(128, 255, 85, 85);
                    int green = ColorUtils.to32BitColor(128, 85, 255, 85);
                    int yellow = ColorUtils.to32BitColor(128, 255, 255, 85);

                    if (((ITradeScreen)(ChestScreen)this.that).getNumberField() == null || ((ITradeScreen)(ChestScreen)this.that).getNumberField().getText().isEmpty())
                    {
                        if (lore.startsWith("Starting bid:"))
                        {
                            this.fillGradient(slotLeft, slotTop, slotRight, slotBottom, green, green);
                        }
                        else if (lore.startsWith("Bidder:"))
                        {
                            this.fillGradient(slotLeft, slotTop, slotRight, slotBottom, red, red);
                        }
                    }
                    else
                    {
                        try
                        {
                            int priceMin = 0;
                            int priceMax = 0;
                            int moneyFromText = 0;

                            if (matcher.matches())
                            {
                                String[] priceSplit = MainEventHandler.auctionPrice.split("\\.\\.");
                                int moneyFromAh = Integer.parseInt(matcher.group("coin").replaceAll("[^\\d.]+", ""));

                                if (MainEventHandler.auctionPrice.matches("[\\d]+\\.\\.[\\d]+"))
                                {
                                    priceMin = Integer.parseInt(priceSplit[0]);
                                    priceMax = Integer.parseInt(priceSplit[1]);
                                    this.mode = SearchMode.RANGED;
                                }
                                else if (MainEventHandler.auctionPrice.matches("[\\d]+\\.\\."))
                                {
                                    priceMin = Integer.parseInt(MainEventHandler.auctionPrice.replaceAll("\\.\\.", ""));
                                    this.mode = SearchMode.MIN;
                                }
                                else if (MainEventHandler.auctionPrice.matches("\\.\\.[\\d]+"))
                                {
                                    priceMax = Integer.parseInt(MainEventHandler.auctionPrice.replaceAll("\\.\\.", ""));
                                    this.mode = SearchMode.MAX;
                                }
                                else
                                {
                                    moneyFromText = Integer.parseInt(MainEventHandler.auctionPrice);
                                    this.mode = SearchMode.SIMPLE;
                                }

                                if (lore.startsWith("Top bid:"))
                                {
                                    this.checkCondition(moneyFromText, moneyFromAh, priceMin, priceMax, slotLeft, slotTop, slotRight, slotBottom, yellow, red);
                                }
                                else if (lore.startsWith("Starting bid:"))
                                {
                                    this.checkCondition(moneyFromText, moneyFromAh, priceMin, priceMax, slotLeft, slotTop, slotRight, slotBottom, green, red);
                                }
                                else if (lore.startsWith("Buy it now:"))
                                {
                                    this.checkCondition(moneyFromText, moneyFromAh, priceMin, priceMax, slotLeft, slotTop, slotRight, slotBottom, green, red);
                                }
                            }
                        }
                        catch (Exception e) {}
                    }
                }
            }
        }
    }

    private void checkCondition(int moneyFromText, int moneyFromAh, int priceMin, int priceMax, int slotLeft, int slotTop, int slotRight, int slotBottom, int color1, int color2)
    {
        switch (this.mode)
        {
        default:
        case SIMPLE:
            if (moneyFromText == moneyFromAh)
            {
                this.fillGradient(slotLeft, slotTop, slotRight, slotBottom, color1, color1);
            }
            else
            {
                this.fillGradient(slotLeft, slotTop, slotRight, slotBottom, color2, color2);
            }
            break;
        case MIN:
            if (moneyFromAh >= priceMin)
            {
                this.fillGradient(slotLeft, slotTop, slotRight, slotBottom, color1, color1);
            }
            else
            {
                this.fillGradient(slotLeft, slotTop, slotRight, slotBottom, color2, color2);
            }
            break;
        case MAX:
            if (moneyFromAh <= priceMax)
            {
                this.fillGradient(slotLeft, slotTop, slotRight, slotBottom, color1, color1);
            }
            else
            {
                this.fillGradient(slotLeft, slotTop, slotRight, slotBottom, color2, color2);
            }
            break;
        case RANGED:
            if (moneyFromAh >= priceMin && moneyFromAh <= priceMax)
            {
                this.fillGradient(slotLeft, slotTop, slotRight, slotBottom, color1, color1);
            }
            else
            {
                this.fillGradient(slotLeft, slotTop, slotRight, slotBottom, color2, color2);
            }
            break;
        }
    }

    private void viewRecipe(ClientPlayerEntity player, Slot slot)
    {
        if (!slot.getStack().isEmpty() && slot.getStack().hasTag())
        {
            CompoundNBT extraAttrib = slot.getStack().getTag().getCompound("ExtraAttributes");

            if (extraAttrib.contains("id"))
            {
                String itemId = extraAttrib.getString("id");
                player.sendChatMessage("/viewrecipe " + itemId);
            }
        }
    }

    private void openFandom(boolean confirm)
    {
        if (confirm)
        {
            CommonUtils.openLink(this.fandomUrl);
        }
        this.minecraft.displayGuiScreen(this);
    }
}