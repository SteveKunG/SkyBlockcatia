package com.stevekung.skyblockcatia.mixin.gui.screens.inventory;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.gui.screen.SkyBlockProfileSelectorScreen;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.mixin.InvokerChatComponent;
import com.stevekung.skyblockcatia.utils.GuiScreenUtils;
import com.stevekung.skyblockcatia.utils.ITradeScreen;
import com.stevekung.skyblockcatia.utils.SearchMode;
import com.stevekung.stevekungslib.client.gui.components.NumberEditBox;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.CommonUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import com.stevekung.stevekungslib.utils.client.ClientUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen<T extends AbstractContainerMenu> extends Screen implements ITradeScreen
{
    private final AbstractContainerScreen<?> that = (AbstractContainerScreen<?>)(Object)this;
    private SearchMode mode = SearchMode.SIMPLE;
    private String fandomUrl;

    // ChatScreen fields
    private String historyBuffer = "";
    private int sentHistoryCursor = -1;
    protected EditBox inputField;
    private CommandSuggestions commandSuggestionHelper;

    // Auction
    private NumberEditBox priceSearch;

    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Shadow
    protected Slot hoveredSlot;

    MixinAbstractContainerScreen()
    {
        super(null);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    private void init(CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ContainerScreen)
        {
            if (GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()))
            {
                this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
                this.priceSearch = new NumberEditBox(this.font, this.leftPos + 180, this.topPos + 40, 100, 20);
                this.priceSearch.setValue(MainEventHandler.auctionPrice);
                this.priceSearch.setCanLoseFocus(true);
            }
            if (GuiScreenUtils.isChatable(this.getTitle()))
            {
                this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
                this.sentHistoryCursor = this.minecraft.gui.getChat().getRecentChat().size();
                this.inputField = new EditBox(this.font, 4, this.height - 12, this.width - 4, 12, TextComponentUtils.component("chat.editBox"));
                this.inputField.setMaxLength(256);
                this.inputField.setBordered(false);
                this.inputField.setValue("");
                this.inputField.setResponder(text -> this.setCommandResponder());
                this.children.add(this.inputField);
                this.commandSuggestionHelper = new CommandSuggestions(this.minecraft, this, this.inputField, this.font, false, false, 1, 10, true, -805306368);
                this.commandSuggestionHelper.updateCommandInfo();
            }
            if (GuiScreenUtils.isOtherAuction(this.getTitle().getString()))
            {
                this.addButton(new Button(this.leftPos + 180, this.topPos + 70, 70, 20, TextComponentUtils.component("Copy Seller"), button ->
                {
                    String title = this.title.getString();
                    ClientUtils.printClientMessage(TextComponentUtils.formatted("Copied seller auction command!", ChatFormatting.GREEN));
                    this.minecraft.keyboardHandler.setClipboard("/ah " + title.replace(title.substring(title.indexOf('\'')), ""));
                }));
                this.addButton(new Button(this.leftPos + 180, this.topPos + 92, 70, 20, TextComponentUtils.component("View API"), button ->
                {
                    String title = this.title.getString();
                    this.minecraft.setScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.PLAYER, title.replace(title.substring(title.indexOf('\'')), ""), "", ""));
                }));
            }
            if (GuiScreenUtils.isOtherProfile(this.getTitle().getString()))
            {
                this.addButton(new Button(this.leftPos + 180, this.topPos + 40, 70, 20, TextComponentUtils.component("View API"), button ->
                {
                    String title = this.title.getString();
                    this.minecraft.setScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.PLAYER, title.replace(title.substring(title.indexOf('\'')), ""), "", ""));
                }));
            }
        }
    }

    @Inject(method = "mouseClicked(DDI)Z", cancellable = true, at = @At("HEAD"))
    private void mouseClicked(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable<Boolean> info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ContainerScreen)
        {
            if (GuiScreenUtils.isChatable(this.getTitle()))
            {
                if (this.commandSuggestionHelper.mouseClicked((int)mouseX, (int)mouseY, mouseButton))
                {
                    this.inputField.setFocus(true);
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
            else if (GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()) && this.priceSearch.mouseClicked(mouseX, mouseY, mouseButton))
            {
                info.setReturnValue(true);
            }
        }
    }

    @Inject(method = "onClose()V", at = @At("RETURN"))
    private void onClose(CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ContainerScreen)
        {
            if (GuiScreenUtils.isChatable(this.getTitle()) || GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()))
            {
                this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
            }
            if (GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()))
            {
                MainEventHandler.auctionPrice = this.priceSearch.getValue();
            }
        }
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    private void tick(CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ContainerScreen)
        {
            if (GuiScreenUtils.isChatable(this.getTitle()))
            {
                this.inputField.tick();
            }
            if (GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()))
            {
                MainEventHandler.auctionPrice = this.priceSearch.getValue();
                this.priceSearch.tick();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screens/Screen.render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", shift = Shift.BEFORE))
    private void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ContainerScreen)
        {
            if (GuiScreenUtils.isChatable(this.getTitle()))
            {
                if (MainEventHandler.showChat)
                {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.disableAlphaTest();
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef(0.0F, this.height - 48, 0.0F);
                    this.renderChat(matrixStack);
                    RenderSystem.popMatrix();
                }
                GuiComponent.fill(matrixStack, 2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
                this.commandSuggestionHelper.render(matrixStack, mouseX, mouseY);
                this.inputField.render(matrixStack, mouseX, mouseY, partialTicks);
            }
            if (this.priceSearch != null && GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()))
            {
                GuiComponent.drawString(matrixStack, this.font, "Search for price:", this.leftPos + 180, this.topPos + 26, 10526880);
                this.priceSearch.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Inject(method = "keyPressed(III)Z", cancellable = true, at = @At("HEAD"))
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info)
    {
        if (!GuiScreenUtils.isChatable(this.getTitle()) && !GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()) && this.hoveredSlot != null && this.hoveredSlot.hasItem())
        {
            if (keyCode == KeyBindingHandler.KEY_SB_VIEW_RECIPE.getDefaultKey().getValue())
            {
                this.viewRecipe(this.minecraft.player, this.hoveredSlot);
                info.setReturnValue(true);
            }
            else if (keyCode == KeyBindingHandler.KEY_SB_OPEN_WIKI.getDefaultKey().getValue())
            {
                ItemStack itemStack = this.hoveredSlot.getItem();

                if (!itemStack.isEmpty() && itemStack.hasTag() && itemStack.getTag().contains("ExtraAttributes"))
                {
                    String itemId = itemStack.getTag().getCompound("ExtraAttributes").getString("id").toLowerCase(Locale.ROOT).replace("_", " ");
                    itemId = WordUtils.capitalize(itemId);
                    this.fandomUrl = "https://hypixel-skyblock.fandom.com/wiki/" + itemId.replace(" ", "_");
                    this.minecraft.setScreen(new ConfirmLinkScreen(this::openFandom, this.fandomUrl, true));
                    info.setReturnValue(true);
                }
            }
        }

        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ContainerScreen)
        {
            if (GuiScreenUtils.isChatable(this.getTitle()))
            {
                if (this.commandSuggestionHelper.keyPressed(keyCode, scanCode, modifiers))
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
                        this.inputField.setFocus(false);
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
                        String text = this.inputField.getValue().trim();

                        if (!text.isEmpty())
                        {
                            this.sendMessage(text);
                            this.sentHistoryCursor = this.minecraft.gui.getChat().getRecentChat().size();
                            this.inputField.setValue("");
                        }
                        info.setReturnValue(true);
                    }
                }
                else
                {
                    if (this.hoveredSlot != null && this.hoveredSlot.hasItem())
                    {
                        if (keyCode == KeyBindingHandler.KEY_SB_VIEW_RECIPE.getDefaultKey().getValue())
                        {
                            this.viewRecipe(this.minecraft.player, this.hoveredSlot);
                            info.setReturnValue(true);
                        }
                        else if (keyCode == KeyBindingHandler.KEY_SB_OPEN_WIKI.getDefaultKey().getValue())
                        {
                            ItemStack itemStack = this.hoveredSlot.getItem();

                            if (!itemStack.isEmpty() && itemStack.hasTag() && itemStack.getTag().contains("ExtraAttributes"))
                            {
                                String itemId = itemStack.getTag().getCompound("ExtraAttributes").getString("id").toLowerCase(Locale.ROOT).replace("_", " ");
                                itemId = WordUtils.capitalize(itemId);
                                this.fandomUrl = "https://hypixel-skyblock.fandom.com/wiki/" + itemId.replace(" ", "_");
                                this.minecraft.setScreen(new ConfirmLinkScreen(this::openFandom, this.fandomUrl, true));
                                info.setReturnValue(true);
                            }
                            info.setReturnValue(false);
                        }
                    }
                }
            }
            else if (GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()))
            {
                if (super.keyPressed(keyCode, scanCode, modifiers))
                {
                    info.setReturnValue(true);
                }
                else if (this.priceSearch.isFocused())
                {
                    if (keyCode == 256)
                    {
                        this.priceSearch.setFocus(false);
                    }
                    info.setReturnValue(false);
                }
                else
                {
                    if (this.hoveredSlot != null && this.hoveredSlot.hasItem())
                    {
                        if (keyCode == KeyBindingHandler.KEY_SB_VIEW_RECIPE.getDefaultKey().getValue())
                        {
                            this.viewRecipe(this.minecraft.player, this.hoveredSlot);
                            info.setReturnValue(true);
                        }
                        else if (keyCode == KeyBindingHandler.KEY_SB_OPEN_WIKI.getDefaultKey().getValue())
                        {
                            ItemStack itemStack = this.hoveredSlot.getItem();

                            if (!itemStack.isEmpty() && itemStack.hasTag() && itemStack.getTag().contains("ExtraAttributes"))
                            {
                                String itemId = itemStack.getTag().getCompound("ExtraAttributes").getString("id").toLowerCase(Locale.ROOT).replace("_", " ");
                                itemId = WordUtils.capitalize(itemId);
                                this.fandomUrl = "https://hypixel-skyblock.fandom.com/wiki/" + itemId.replace(" ", "_");
                                this.minecraft.setScreen(new ConfirmLinkScreen(this::openFandom, this.fandomUrl, true));
                                info.setReturnValue(true);
                            }
                            info.setReturnValue(false);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V", cancellable = true, at = @At("HEAD"))
    private void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo info)
    {
        if (slot != null && SkyBlockEventHandler.isSkyBlock)
        {
            if (this.that instanceof ContainerScreen)
            {
                if (mouseButton == 2 && type == ClickType.CLONE && GuiScreenUtils.canViewSeller(this.getTitle().getString()))
                {
                    if (!slot.getItem().isEmpty() && slot.getItem().hasTag())
                    {
                        CompoundTag compound = slot.getItem().getTag().getCompound("display");

                        if (compound.getTagType("Lore") == 9)
                        {
                            ListTag list = compound.getList("Lore", 8);

                            for (int j1 = 0; j1 < list.size(); ++j1)
                            {
                                String lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));

                                if (lore.startsWith("Seller: "))
                                {
                                    this.minecraft.player.chat("/ah " + lore.replaceAll("Seller: ?(?:\\[VIP?\\u002B?]|\\[MVP?\\u002B{0,2}]|\\[YOUTUBE])? ", ""));
                                }
                            }
                        }
                    }
                    info.cancel();
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "renderSlot(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/inventory/Slot;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/entity/ItemRenderer.renderAndDecorateItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;II)V", shift = Shift.AFTER))
    private void renderAnvilLevel(PoseStack matrixStack, Slot slot, CallbackInfo info)
    {
        if (this.that instanceof ContainerScreen)
        {
            String levelString = "";

            if (this.getTitle().getString().equals("Anvil") || this.getTitle().getString().equals("Reforge Item"))
            {
                Slot anvilSlot = this.that.getMenu().slots.get(31);
                ItemStack itemStack = this.that.getMenu().slots.get(22).getItem();
                int i = anvilSlot.x;
                int j = anvilSlot.y;

                if (!itemStack.isEmpty() && itemStack.hasTag())
                {
                    CompoundTag compound = itemStack.getTag().getCompound("display");

                    if (compound.getTagType("Lore") == 9)
                    {
                        ListTag list = compound.getList("Lore", 8);

                        for (int j1 = 0; j1 < list.size(); ++j1)
                        {
                            String lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));

                            if (lore.endsWith("Exp Levels") || lore.endsWith("Exp Level"))
                            {
                                int level = 0;

                                try
                                {
                                    level = NumberFormat.getNumberInstance(Locale.ROOT).parse(lore.replaceAll(" Exp Levels?", "")).intValue();
                                }
                                catch (ParseException ignored) {}

                                if (level > 0)
                                {
                                    if (this.minecraft.player.experienceLevel < level)
                                    {
                                        levelString = ChatFormatting.RED + String.valueOf(level);
                                    }
                                    else
                                    {
                                        levelString = ChatFormatting.GREEN + String.valueOf(level);
                                    }
                                }
                            }
                            else if (lore.endsWith("Coins") || lore.endsWith("Coin"))
                            {
                                int coin = 0;

                                try
                                {
                                    coin = NumberFormat.getNumberInstance(Locale.ROOT).parse(lore.replaceAll(" Coins?", "")).intValue();
                                }
                                catch (ParseException ignored) {}

                                if (coin > 0)
                                {
                                    levelString = ChatFormatting.GOLD + String.valueOf(coin);
                                }
                                break;
                            }
                        }
                    }
                }
                RenderSystem.pushMatrix();
                RenderSystem.disableDepthTest();
                RenderSystem.translatef(0.0F, 0.0F, 300.0F);
                GuiComponent.drawCenteredString(matrixStack, this.font, levelString, i + 8, j + 4, 0);
                RenderSystem.enableDepthTest();
                RenderSystem.popMatrix();
            }
        }
    }

    @Inject(method = "renderSlot(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/inventory/Slot;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/entity/ItemRenderer.renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"))
    private void renderOverlays(PoseStack matrixStack, Slot slot, CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ContainerScreen)
        {
            if (MainEventHandler.bidHighlight && GuiScreenUtils.canRenderBids(this.getTitle().getString()))
            {
                this.renderBids(matrixStack, slot);
            }

            this.renderCurrentSelectedPet(matrixStack, slot);

            if (SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer && this.title.getString().contains("Hub Selector"))
            {
                this.renderHubOverlay(matrixStack, slot);
            }
        }
    }

    @Redirect(method = "renderSlot(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/inventory/Slot;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/entity/ItemRenderer.renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"))
    private void renderPlayerCount(ItemRenderer renderer, Font font, ItemStack stack, int xPosition, int yPosition, String text)
    {
        boolean found = false;

        if (SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer && this.that instanceof ContainerScreen)
        {
            if (this.title.getString().contains("Hub Selector") && !stack.isEmpty() && stack.hasTag())
            {
                CompoundTag compound = stack.getTag().getCompound("display");

                if (compound.getTagType("Lore") == 9)
                {
                    ListTag list = compound.getList("Lore", 8);

                    for (int j1 = 0; j1 < list.size(); ++j1)
                    {
                        String lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));

                        if (lore.startsWith("Players: "))
                        {
                            lore = lore.substring(lore.indexOf(" ") + 1);
                            String[] loreCount = lore.split("/");
                            renderer.renderGuiItemDecorations(font, stack, xPosition, yPosition, loreCount[0]);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        if (!found)
        {
            renderer.renderGuiItemDecorations(font, stack, xPosition, yPosition, text);
        }
    }

    @Redirect(method = "checkHotbarMouseClicked(I)V", slice = @Slice(from = @At(value = "INVOKE", target = "net/minecraft/world/item/ItemStack.isEmpty()Z"), to = @At(value = "CONSTANT", args = "intValue=40")), at = @At(value = "INVOKE", target = "net/minecraft/client/KeyMapping.matchesMouse(I)Z"))
    private boolean disableHotbarSwap(KeyMapping key, int keyCode)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            return false;
        }
        return key.matchesMouse(keyCode);
    }

    @Override
    public GuiEventListener getFocused()
    {
        if (this.priceSearch != null)
        {
            return this.priceSearch;
        }
        else if (this.inputField != null)
        {
            return this.inputField;
        }
        return super.getFocused();
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ContainerScreen)
        {
            if (GuiScreenUtils.isChatable(this.getTitle()))
            {
                String text = this.inputField.getValue();
                boolean focus = this.inputField.isFocused();
                super.resize(mc, width, height);
                this.inputField.setValue(text);
                this.inputField.setFocus(focus);
                this.commandSuggestionHelper.updateCommandInfo();
            }
            else if (GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()))
            {
                boolean focus = this.priceSearch.isFocused();
                super.resize(mc, width, height);
                this.priceSearch.setFocus(focus);
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
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ContainerScreen && GuiScreenUtils.isChatable(this.getTitle()))
        {
            return true;
        }
        else
        {
            return super.mouseScrolled(mouseX, mouseY, scrollDelta);
        }
    }

    @Override
    public NumberEditBox getNumberEditBox()
    {
        return this.priceSearch;
    }

    private void setCommandResponder()
    {
        this.commandSuggestionHelper.setAllowSuggestions(false);
        this.commandSuggestionHelper.updateCommandInfo();
    }

    private void getSentHistory(int msgPos)
    {
        int i = this.sentHistoryCursor + msgPos;
        int j = this.minecraft.gui.getChat().getRecentChat().size();
        i = Mth.clamp(i, 0, j);

        if (i != this.sentHistoryCursor)
        {
            if (i == j)
            {
                this.sentHistoryCursor = j;
                this.inputField.setValue(this.historyBuffer);
            }
            else
            {
                if (this.sentHistoryCursor == j)
                {
                    this.historyBuffer = this.inputField.getValue();
                }
                this.inputField.setValue(this.minecraft.gui.getChat().getRecentChat().get(i));
                this.commandSuggestionHelper.setAllowSuggestions(false);
                this.sentHistoryCursor = i;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void renderChat(PoseStack matrixStack)
    {
        ChatComponent chat = this.minecraft.gui.getChat();

        if (!((InvokerChatComponent)chat).invokeIsChatHidden())
        {
            int i = chat.getLinesPerPage();
            int j = ((InvokerChatComponent)chat).getTrimmedMessages().size();

            if (j > 0)
            {
                double d0 = chat.getScale();
                RenderSystem.pushMatrix();
                RenderSystem.translatef(2.0F, 8.0F, 0.0F);
                RenderSystem.scaled(d0, d0, 1.0D);
                double d1 = this.minecraft.options.chatOpacity * 0.9F + 0.1F;

                for (int i1 = 0; i1 + ((InvokerChatComponent)chat).getChatScrollbarPos() < ((InvokerChatComponent)chat).getTrimmedMessages().size() && i1 < i; ++i1)
                {
                    GuiMessage<FormattedCharSequence> chatline = ((InvokerChatComponent)chat).getTrimmedMessages().get(i1 + ((InvokerChatComponent)chat).getChatScrollbarPos());

                    if (chatline != null)
                    {
                        int j1 = this.minecraft.gui.getGuiTicks() - chatline.getAddedTime();

                        if (j1 < 200)
                        {
                            double d3 = InvokerChatComponent.invokeGetTimeFactor(j1);
                            int l1 = (int)(255.0D * d3 * d1);
                            if (l1 > 3)
                            {
                                int k2 = -i1 * 9;
                                RenderSystem.enableBlend();
                                this.font.drawShadow(matrixStack, chatline.getMessage(), 0.0F, k2 - 8, 16777215 + (l1 << 24));
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

    private void renderBids(PoseStack matrixStack, Slot slot)
    {
        if (!slot.getItem().isEmpty() && slot.getItem().hasTag())
        {
            CompoundTag compound = slot.getItem().getTag().getCompound("display");

            if (compound.getTagType("Lore") == 9)
            {
                ListTag list = compound.getList("Lore", 8);

                for (int j1 = 0; j1 < list.size(); ++j1)
                {
                    int slotLeft = slot.x;
                    int slotTop = slot.y;
                    int slotRight = slotLeft + 16;
                    int slotBottom = slotTop + 16;
                    String lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));
                    Matcher matcher = Pattern.compile("(?:(?:Top|Starting) bid|Buy it now): (?<coin>[0-9,]+) coins").matcher(lore);
                    int red = ColorUtils.to32Bit(255, 85, 85, 128);
                    int green = ColorUtils.to32Bit(85, 255, 85, 128);
                    int yellow = ColorUtils.to32Bit(255, 255, 85, 128);

                    if (lore.startsWith("Status: Sold!"))
                    {
                        this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, yellow, yellow);
                    }

                    if (((ITradeScreen)this.that).getNumberEditBox() == null || ((ITradeScreen)this.that).getNumberEditBox().getValue().isEmpty())
                    {
                        if (lore.startsWith("Starting bid:"))
                        {
                            this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, green, green);
                        }
                        else if (lore.startsWith("Bidder:"))
                        {
                            this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, red, red);
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
                                    this.checkCondition(matrixStack, moneyFromText, moneyFromAh, priceMin, priceMax, slotLeft, slotTop, slotRight, slotBottom, yellow, red);
                                }
                                else if (lore.startsWith("Starting bid:"))
                                {
                                    this.checkCondition(matrixStack, moneyFromText, moneyFromAh, priceMin, priceMax, slotLeft, slotTop, slotRight, slotBottom, green, red);
                                }
                                else if (lore.startsWith("Buy it now:"))
                                {
                                    this.checkCondition(matrixStack, moneyFromText, moneyFromAh, priceMin, priceMax, slotLeft, slotTop, slotRight, slotBottom, green, red);
                                }
                            }
                        }
                        catch (Exception ignored) {}
                    }
                }
            }
        }
    }

    private void renderCurrentSelectedPet(PoseStack matrixStack, Slot slot)
    {
        if (slot.getItem() != null && slot.getItem().hasTag())
        {
            CompoundTag compound = slot.getItem().getTag().getCompound("display");

            if (compound.getTagType("Lore") == 9)
            {
                ListTag list = compound.getList("Lore", 8);

                for (int j1 = 0; j1 < list.size(); ++j1)
                {
                    int slotLeft = slot.x;
                    int slotTop = slot.y;
                    int slotRight = slotLeft + 16;
                    int slotBottom = slotTop + 16;
                    String lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));
                    int green = ColorUtils.to32Bit(85, 255, 85, 150);

                    if (lore.startsWith("Click to despawn"))
                    {
                        this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, green, green);
                        break;
                    }
                }
            }
        }
    }

    private void renderHubOverlay(PoseStack matrixStack, Slot slot)
    {
        if (slot.getItem() != null && slot.getItem().hasTag())
        {
            CompoundTag compound = slot.getItem().getTag().getCompound("display");

            if (compound.getTagType("Lore") == 9)
            {
                ListTag list = compound.getList("Lore", 8);

                for (int j1 = 0; j1 < list.size(); ++j1)
                {
                    int slotLeft = slot.x;
                    int slotTop = slot.y;
                    int slotRight = slotLeft + 16;
                    int slotBottom = slotTop + 16;
                    String lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));

                    if (lore.startsWith("Players: "))
                    {
                        lore = lore.substring(lore.indexOf(" ") + 1);
                        String[] loreCount = lore.split("/");
                        int min = Integer.parseInt(loreCount[0]);
                        int max = Integer.parseInt(loreCount[1]);
                        int playerCountColor = this.getRGBPlayerCount(min, max);
                        int color = ColorUtils.to32Bit(playerCountColor >> 16 & 255, playerCountColor >> 8 & 255, playerCountColor & 255, 128);
                        this.setBlitOffset(300);
                        this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, color, color);
                        this.setBlitOffset(0);
                        break;
                    }
                }
            }
        }
    }

    private int getRGBPlayerCount(int playerCount, int maxedPlayerCount)
    {
        return Mth.hsvToRgb(Math.max(0.0F, (float)(1.0F - this.getPlayerCount(playerCount, maxedPlayerCount))) / 3.0F, 1.0F, 1.0F);
    }

    private double getPlayerCount(int playerCount, int maxedPlayerCount)
    {
        return (double)playerCount / maxedPlayerCount;
    }

    private void checkCondition(PoseStack matrixStack, int moneyFromText, int moneyFromAh, int priceMin, int priceMax, int slotLeft, int slotTop, int slotRight, int slotBottom, int color1, int color2)
    {
        switch (this.mode)
        {
            default:
            case SIMPLE:
                if (moneyFromText == moneyFromAh)
                {
                    this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, color1, color1);
                }
                else
                {
                    this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, color2, color2);
                }
                break;
            case MIN:
                if (moneyFromAh >= priceMin)
                {
                    this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, color1, color1);
                }
                else
                {
                    this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, color2, color2);
                }
                break;
            case MAX:
                if (moneyFromAh <= priceMax)
                {
                    this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, color1, color1);
                }
                else
                {
                    this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, color2, color2);
                }
                break;
            case RANGED:
                if (moneyFromAh >= priceMin && moneyFromAh <= priceMax)
                {
                    this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, color1, color1);
                }
                else
                {
                    this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, color2, color2);
                }
                break;
        }
    }

    private void viewRecipe(LocalPlayer player, Slot slot)
    {
        if (!slot.getItem().isEmpty() && slot.getItem().hasTag())
        {
            CompoundTag extraAttrib = slot.getItem().getTag().getCompound("ExtraAttributes");

            if (extraAttrib.contains("id"))
            {
                String itemId = extraAttrib.getString("id");
                player.chat("/viewrecipe " + itemId);
            }
        }
    }

    private void openFandom(boolean confirm)
    {
        if (confirm)
        {
            CommonUtils.openLink(this.fandomUrl);
        }
        this.minecraft.setScreen(this);
    }
}