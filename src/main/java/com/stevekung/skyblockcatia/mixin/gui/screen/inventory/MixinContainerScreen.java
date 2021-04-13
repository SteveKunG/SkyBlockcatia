package com.stevekung.skyblockcatia.mixin.gui.screen.inventory;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.commons.text.WordUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.gui.screen.SkyBlockProfileSelectorScreen;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.GuiScreenUtils;
import com.stevekung.skyblockcatia.utils.ITradeScreen;
import com.stevekung.skyblockcatia.utils.SearchMode;
import com.stevekung.stevekungslib.client.gui.NumberWidget;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.CommonUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import com.stevekung.stevekungslib.utils.client.ClientUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

@Mixin(ContainerScreen.class)
public class MixinContainerScreen<T extends Container> extends Screen implements ITradeScreen
{
    private final ContainerScreen that = (ContainerScreen) (Object) this;
    private SearchMode mode = SearchMode.SIMPLE;
    private String fandomUrl;

    // ChatScreen fields
    private String historyBuffer = "";
    private int sentHistoryCursor = -1;
    protected TextFieldWidget inputField;
    private CommandSuggestionHelper commandSuggestionHelper;

    // Auction
    private NumberWidget priceSearch;

    MixinContainerScreen()
    {
        super(null);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    private void init(CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
        {
            if (GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()))
            {
                this.minecraft.keyboardListener.enableRepeatEvents(true);
                this.priceSearch = new NumberWidget(this.font, this.that.getGuiLeft() + 180, this.that.getGuiTop() + 40, 100, 20);
                this.priceSearch.setText(MainEventHandler.auctionPrice);
                this.priceSearch.setCanLoseFocus(true);
            }
            if (GuiScreenUtils.isChatable(this.getTitle()))
            {
                this.minecraft.keyboardListener.enableRepeatEvents(true);
                this.sentHistoryCursor = this.minecraft.ingameGUI.getChatGUI().getSentMessages().size();
                this.inputField = new TextFieldWidget(this.font, 4, this.height - 12, this.width - 4, 12, TextComponentUtils.component("chat.editBox"));
                this.inputField.setMaxStringLength(256);
                this.inputField.setEnableBackgroundDrawing(false);
                this.inputField.setText("");
                this.inputField.setResponder(text -> this.setCommandResponder());
                this.children.add(this.inputField);
                this.commandSuggestionHelper = new CommandSuggestionHelper(this.minecraft, this, this.inputField, this.font, false, false, 1, 10, true, -805306368);
                this.commandSuggestionHelper.init();
            }
            if (GuiScreenUtils.isOtherAuction(this.getTitle().getString()))
            {
                this.addButton(new Button(this.that.getGuiLeft() + 180, this.that.getGuiTop() + 70, 70, 20, TextComponentUtils.component("Copy Seller"), button ->
                {
                    String title = this.title.getString();
                    ClientUtils.printClientMessage(TextComponentUtils.formatted("Copied seller auction command!", TextFormatting.GREEN));
                    this.minecraft.keyboardListener.setClipboardString("/ah " + title.replace(title.substring(title.indexOf('\'')), ""));
                }));
                this.addButton(new Button(this.that.getGuiLeft() + 180, this.that.getGuiTop() + 92, 70, 20, TextComponentUtils.component("View API"), button ->
                {
                    String title = this.title.getString();
                    this.minecraft.displayGuiScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.PLAYER, title.replace(title.substring(title.indexOf('\'')), ""), "", ""));
                }));
            }
            if (GuiScreenUtils.isOtherProfile(this.getTitle().getString()))
            {
                this.addButton(new Button(this.that.getGuiLeft() + 180, this.that.getGuiTop() + 40, 70, 20, TextComponentUtils.component("View API"), button ->
                {
                    String title = this.title.getString();
                    this.minecraft.displayGuiScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.PLAYER, title.replace(title.substring(title.indexOf('\'')), ""), "", ""));
                }));
            }
        }
    }

    @Inject(method = "mouseClicked(DDI)Z", cancellable = true, at = @At("HEAD"))
    private void mouseClicked(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable<Boolean> info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
        {
            if (GuiScreenUtils.isChatable(this.getTitle()))
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
            else if (GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()) && this.priceSearch.mouseClicked(mouseX, mouseY, mouseButton))
            {
                info.setReturnValue(true);
            }
        }
    }

    @Inject(method = "closeScreen()V", at = @At("RETURN"))
    private void closeScreen(CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
        {
            if (GuiScreenUtils.isChatable(this.getTitle()) || GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()))
            {
                this.minecraft.keyboardListener.enableRepeatEvents(false);
            }
            if (GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()))
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
            if (GuiScreenUtils.isChatable(this.getTitle()))
            {
                this.inputField.tick();
            }
            if (GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()))
            {
                MainEventHandler.auctionPrice = this.priceSearch.getText();
                this.priceSearch.tick();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;IIF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screen/Screen.render(Lcom/mojang/blaze3d/matrix/MatrixStack;IIF)V", shift = Shift.BEFORE))
    private void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
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
                AbstractGui.fill(matrixStack, 2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
                this.commandSuggestionHelper.drawSuggestionList(matrixStack, mouseX, mouseY);
                this.inputField.render(matrixStack, mouseX, mouseY, partialTicks);
            }
            if (this.priceSearch != null && GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()))
            {
                AbstractGui.drawString(matrixStack, this.font, "Search for price:", this.that.getGuiLeft() + 180, this.that.getGuiTop() + 26, 10526880);
                this.priceSearch.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Inject(method = "keyPressed(III)Z", cancellable = true, at = @At("HEAD"))
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info)
    {
        if (!GuiScreenUtils.isChatable(this.getTitle()) && !GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()) && this.that.getSlotUnderMouse() != null && this.that.getSlotUnderMouse().getHasStack())
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
                    String itemId = itemStack.getTag().getCompound("ExtraAttributes").getString("id").toLowerCase(Locale.ROOT).replace("_", " ");
                    itemId = WordUtils.capitalize(itemId);
                    this.fandomUrl = "https://hypixel-skyblock.fandom.com/wiki/" + itemId.replace(" ", "_");
                    this.minecraft.displayGuiScreen(new ConfirmOpenLinkScreen(this::openFandom, this.fandomUrl, true));
                    info.setReturnValue(true);
                }
            }
        }

        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
        {
            if (GuiScreenUtils.isChatable(this.getTitle()))
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
                                String itemId = itemStack.getTag().getCompound("ExtraAttributes").getString("id").toLowerCase(Locale.ROOT).replace("_", " ");
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
                                String itemId = itemStack.getTag().getCompound("ExtraAttributes").getString("id").toLowerCase(Locale.ROOT).replace("_", " ");
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
            if (this.that instanceof ChestScreen)
            {
                if (mouseButton == 2 && type == ClickType.CLONE && GuiScreenUtils.canViewSeller(this.getTitle().getString()))
                {
                    if (!slot.getStack().isEmpty() && slot.getStack().hasTag())
                    {
                        CompoundNBT compound = slot.getStack().getTag().getCompound("display");

                        if (compound.getTagId("Lore") == Constants.NBT.TAG_LIST)
                        {
                            ListNBT list = compound.getList("Lore", Constants.NBT.TAG_STRING);

                            for (int j1 = 0; j1 < list.size(); ++j1)
                            {
                                String lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));

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

    @SuppressWarnings("deprecation")
    @Inject(method = "moveItems(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/inventory/container/Slot;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/ItemRenderer.renderItemAndEffectIntoGUI(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;II)V", shift = Shift.AFTER))
    private void renderAnvilLevel(MatrixStack matrixStack, Slot slot, CallbackInfo info)
    {
        if (this.that instanceof ChestScreen)
        {
            int i = 0;
            int j = 0;
            String levelString = "";

            if (this.getTitle().getString().equals("Anvil") || this.getTitle().getString().equals("Reforge Item"))
            {
                Slot anvilSlot = this.that.getContainer().inventorySlots.get(31);
                ItemStack itemStack = this.that.getContainer().inventorySlots.get(22).getStack();
                i = anvilSlot.xPos;
                j = anvilSlot.yPos;

                if (!itemStack.isEmpty() && itemStack.hasTag())
                {
                    CompoundNBT compound = itemStack.getTag().getCompound("display");

                    if (compound.getTagId("Lore") == Constants.NBT.TAG_LIST)
                    {
                        ListNBT list = compound.getList("Lore", Constants.NBT.TAG_STRING);

                        for (int j1 = 0; j1 < list.size(); ++j1)
                        {
                            String lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));

                            if (lore.endsWith("Exp Levels") || lore.endsWith("Exp Level"))
                            {
                                int level = 0;

                                try
                                {
                                    level = NumberFormat.getNumberInstance(Locale.ROOT).parse(lore.replaceAll(" Exp Level(?:s){0,1}", "")).intValue();
                                }
                                catch (ParseException e) {}

                                if (level > 0)
                                {
                                    if (this.minecraft.player.experienceLevel < level)
                                    {
                                        levelString = TextFormatting.RED + String.valueOf(level);
                                    }
                                    else
                                    {
                                        levelString = TextFormatting.GREEN + String.valueOf(level);
                                    }
                                }
                            }
                            else if (lore.endsWith("Coins") || lore.endsWith("Coin"))
                            {
                                int coin = 0;

                                try
                                {
                                    coin = NumberFormat.getNumberInstance(Locale.ROOT).parse(lore.replaceAll(" Coin(?:s){0,1}", "")).intValue();
                                }
                                catch (ParseException e) {}

                                if (coin > 0)
                                {
                                    levelString = TextFormatting.GOLD + String.valueOf(coin);
                                }
                                break;
                            }
                        }
                    }
                }
                RenderSystem.pushMatrix();
                RenderSystem.disableDepthTest();
                RenderSystem.translatef(0.0F, 0.0F, 300.0F);
                AbstractGui.drawCenteredString(matrixStack, this.font, levelString, i + 8, j + 4, 0);
                RenderSystem.enableDepthTest();
                RenderSystem.popMatrix();
            }
        }
    }

    @Inject(method = "moveItems(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/inventory/container/Slot;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/ItemRenderer.renderItemOverlayIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"))
    private void renderOverlays(MatrixStack matrixStack, Slot slot, CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
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

    @Redirect(method = "moveItems(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/inventory/container/Slot;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/ItemRenderer.renderItemOverlayIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"))
    private void renderPlayerCount(ItemRenderer renderer, FontRenderer font, ItemStack stack, int xPosition, int yPosition, @Nullable String text)
    {
        boolean found = false;

        if (SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer && this.that instanceof ChestScreen)
        {
            if (this.title.getString().contains("Hub Selector") && !stack.isEmpty() && stack.hasTag())
            {
                CompoundNBT compound = stack.getTag().getCompound("display");

                if (compound.getTagId("Lore") == Constants.NBT.TAG_LIST)
                {
                    ListNBT list = compound.getList("Lore", Constants.NBT.TAG_STRING);

                    for (int j1 = 0; j1 < list.size(); ++j1)
                    {
                        String lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));

                        if (lore.startsWith("Players: "))
                        {
                            lore = lore.substring(lore.indexOf(" ") + 1);
                            String[] loreCount = lore.split("/");
                            renderer.renderItemOverlayIntoGUI(font, stack, xPosition, yPosition, loreCount[0]);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        if (!found)
        {
            renderer.renderItemOverlayIntoGUI(font, stack, xPosition, yPosition, text);
        }
    }

    @Redirect(method = "hotkeySwapItems(I)V", slice = @Slice(
            from = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isEmpty()Z"),
            to = @At(value = "CONSTANT", args = "intValue=40")),
            at = @At(value = "INVOKE", target = "net/minecraft/client/settings/KeyBinding.matchesMouseKey(I)Z"))
    private boolean disableHotbarSwap(KeyBinding key, int keyCode)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            return false;
        }
        return key.matchesMouseKey(keyCode);
    }

    @Redirect(method = "itemStackMoved(II)Z", slice = @Slice(
            from = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isEmpty()Z"),
            to = @At(value = "CONSTANT", args = "intValue=40")),
            at = @At(value = "INVOKE", remap = false, target = "net/minecraft/client/settings/KeyBinding.isActiveAndMatches(Lnet/minecraft/client/util/InputMappings$Input;)Z"))
    private boolean disableItemStackSwap(KeyBinding key, InputMappings.Input keyCodeInput)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            return false;
        }
        return key.isActiveAndMatches(keyCodeInput);
    }

    @Override
    @Nullable
    public IGuiEventListener getListener()
    {
        if (this.priceSearch != null)
        {
            return this.priceSearch;
        }
        else if (this.inputField != null)
        {
            return this.inputField;
        }
        return super.getListener();
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen)
        {
            if (GuiScreenUtils.isChatable(this.getTitle()))
            {
                String text = this.inputField.getText();
                boolean focus = this.inputField.isFocused();
                super.resize(mc, width, height);
                this.inputField.setText(text);
                this.inputField.setFocused2(focus);
                this.commandSuggestionHelper.init();
            }
            else if (GuiScreenUtils.isAuctionBrowser(this.getTitle().getString()))
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
        if (SkyBlockEventHandler.isSkyBlock && this.that instanceof ChestScreen && GuiScreenUtils.isChatable(this.getTitle()))
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

    private void setCommandResponder()
    {
        this.commandSuggestionHelper.shouldAutoSuggest(false);
        this.commandSuggestionHelper.init();
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
                this.commandSuggestionHelper.shouldAutoSuggest(false);
                this.sentHistoryCursor = i;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void renderChat(MatrixStack matrixStack)
    {
        NewChatGui chat = this.minecraft.ingameGUI.getChatGUI();

        if (!chat.func_238496_i_())
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
                    ChatLine<IReorderingProcessor> chatline = chat.drawnChatLines.get(i1 + chat.scrollPos);

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
                                RenderSystem.enableBlend();
                                this.font.drawTextWithShadow(matrixStack, chatline.getLineString(), 0.0F, k2 - 8, 16777215 + (l1 << 24));
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

    private void renderBids(MatrixStack matrixStack, Slot slot)
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
                    String lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));
                    Matcher matcher = Pattern.compile("(?:(?:Top|Starting) bid|Buy it now): (?<coin>[0-9,]+) coins").matcher(lore);
                    int red = ColorUtils.to32Bit(255, 85, 85, 128);
                    int green = ColorUtils.to32Bit(85, 255, 85, 128);
                    int yellow = ColorUtils.to32Bit(255, 255, 85, 128);

                    if (lore.startsWith("Status: Sold!"))
                    {
                        this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, yellow, yellow);
                    }

                    if (((ITradeScreen)(ChestScreen)this.that).getNumberField() == null || ((ITradeScreen)(ChestScreen)this.that).getNumberField().getText().isEmpty())
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
                        catch (Exception e) {}
                    }
                }
            }
        }
    }

    private void renderCurrentSelectedPet(MatrixStack matrixStack, Slot slot)
    {
        if (slot.getStack() != null && slot.getStack().hasTag())
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

    private void renderHubOverlay(MatrixStack matrixStack, Slot slot)
    {
        if (slot.getStack() != null && slot.getStack().hasTag())
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
                    String lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));

                    if (lore.startsWith("Players: "))
                    {
                        lore = lore.substring(lore.indexOf(" ") + 1);
                        String[] loreCount = lore.split("/");
                        int min = Integer.valueOf(loreCount[0]);
                        int max = Integer.valueOf(loreCount[1]);
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
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - this.getPlayerCount(playerCount, maxedPlayerCount))) / 3.0F, 1.0F, 1.0F);
    }

    private double getPlayerCount(int playerCount, int maxedPlayerCount)
    {
        return (double) playerCount / maxedPlayerCount;
    }

    private void checkCondition(MatrixStack matrixStack, int moneyFromText, int moneyFromAh, int priceMin, int priceMax, int slotLeft, int slotTop, int slotRight, int slotBottom, int color1, int color2)
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