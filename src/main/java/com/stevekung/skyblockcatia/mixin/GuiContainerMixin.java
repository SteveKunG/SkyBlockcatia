package com.stevekung.skyblockcatia.mixin;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.event.MainEventHandler;
import com.stevekung.skyblockcatia.gui.GuiNumberField;
import com.stevekung.skyblockcatia.gui.api.GuiSkyBlockAPIViewer;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.*;
import com.stevekung.skyblockcatia.utils.ColorUtils.RGB;
import com.stevekung.skyblockcatia.utils.JsonUtils;

import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.*;
import net.minecraftforge.client.ClientCommandHandler;

@Mixin(GuiContainer.class)
public abstract class GuiContainerMixin extends GuiScreen implements ITradeGUI
{
    private final GuiContainer that = (GuiContainer) (Object) this;
    private static final ImmutableList<String> IGNORE_ITEMS = ImmutableList.of(" ", "Recipe Required", "Item To Upgrade", "Rune to Sacrifice", "Runic Pedestal", "Final confirmation", "Quick Crafting Slot", "Enchant Item", "Item to Sacrifice");
    private static final ImmutableList<String> INVENTORY_LIST = ImmutableList.of("SkyBlock Menu", "Skill", "Collection", "Crafted Minions", "Recipe", "Quest Log", "Fairy Souls Guide", "Calendar and Events", "Settings", "Profiles Management", "Fast Travel", "SkyBlock Profile", "'s Profile", "' Profile", "Bank", "Harp");
    private static final ImmutableList<String> ITEM_LIST = ImmutableList.of(EnumChatFormatting.GREEN + "Go Back", EnumChatFormatting.RED + "Close");
    private static final Pattern PET_MENU_PATTERN = Pattern.compile("\\u00a77\\[Lvl \\d+\\] (?<color>\\u00a7[0-9a-fk-or])[\\w ]+");
    private SearchMode mode = SearchMode.SIMPLE;
    private String fandomUrl;

    @Shadow
    protected int guiLeft;

    @Shadow
    protected int guiTop;

    @Shadow
    private Slot theSlot;

    @Shadow
    protected abstract boolean checkHotbarKeys(int keyCode);

    @Shadow
    protected abstract void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType);

    // GuiChat fields
    private GuiTextField inputField;
    private int sentHistoryCursor = -1;
    private boolean playerNamesFound;
    private boolean waitingOnAutocomplete;
    private int autocompleteIndex;
    private List<String> foundPlayerNames = new ArrayList<>();
    private String historyBuffer = "";

    // Auction
    private GuiNumberField priceSearch;

    @Inject(method = "initGui()V", at = @At("RETURN"))
    private void initGui(CallbackInfo info)
    {
        if (this.that instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)this.that;

            if (this.isAuctionBrowser(chest.lowerChestInventory))
            {
                Keyboard.enableRepeatEvents(true);
                this.priceSearch = new GuiNumberField(2, this.fontRendererObj, this.guiLeft + 180, this.guiTop + 40, 100, 20);
                this.priceSearch.setText(MainEventHandler.auctionPrice);
                this.priceSearch.setCanLoseFocus(true);
            }
            if (this.isChatableGui(chest.lowerChestInventory))
            {
                Keyboard.enableRepeatEvents(true);
                this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
                this.inputField = new GuiTextField(0, this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
                this.inputField.setEnableBackgroundDrawing(false);
                this.inputField.setMaxStringLength(256);
                this.inputField.setFocused(false);
                this.inputField.setCanLoseFocus(true);
            }
            if (this.isPeopleAuction(chest.lowerChestInventory))
            {
                this.buttonList.add(new GuiButton(155, this.guiLeft + 180, this.guiTop + 70, 70, 20, "Copy Seller"));
                this.buttonList.add(new GuiButton(156, this.guiLeft + 180, this.guiTop + 92, 70, 20, "View API"));
            }
            if (this.isPeopleProfile(chest.lowerChestInventory))
            {
                this.buttonList.add(new GuiButton(156, this.guiLeft + 180, this.guiTop + 40, 70, 20, "View API"));
            }
        }
    }

    @Inject(method = "mouseClicked(III)V", at = @At("RETURN"))
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo info)
    {
        if (this.that instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)this.that;

            if (this.isChatableGui(chest.lowerChestInventory))
            {
                this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
            }
            if (this.isAuctionBrowser(chest.lowerChestInventory))
            {
                this.priceSearch.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Inject(method = "onGuiClosed()V", at = @At("RETURN"))
    private void onGuiClosed(CallbackInfo info)
    {
        if (this.that instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)this.that;

            if (this.isChatableGui(chest.lowerChestInventory) || this.isAuctionBrowser(chest.lowerChestInventory))
            {
                Keyboard.enableRepeatEvents(false);
            }
            if (this.isAuctionBrowser(chest.lowerChestInventory))
            {
                MainEventHandler.auctionPrice = this.priceSearch.getText();
            }
        }
    }

    @Inject(method = "updateScreen()V", at = @At("RETURN"))
    private void updateScreen(CallbackInfo info)
    {
        if (this.that instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)this.that;

            if (this.isChatableGui(chest.lowerChestInventory))
            {
                this.inputField.updateCursorCounter();
            }
            if (this.priceSearch != null && this.isAuctionBrowser(chest.lowerChestInventory))
            {
                MainEventHandler.auctionPrice = this.priceSearch.getText();
                this.priceSearch.updateCursorCounter();
            }
        }
    }

    @Inject(method = "drawScreen(IIF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiScreen.drawScreen(IIF)V", shift = Shift.BEFORE))
    private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo info)
    {
        if (this.that instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)this.that;

            if (this.isChatableGui(chest.lowerChestInventory))
            {
                if (MainEventHandler.showChat)
                {
                    ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                    int height = scaledresolution.getScaledHeight();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    GlStateManager.disableAlpha();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0.0F, height - 48, 0.0F);
                    this.drawChat();
                    GlStateManager.popMatrix();
                }
                Gui.drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
                this.inputField.drawTextBox();
            }
            if (this.priceSearch != null && this.isAuctionBrowser(chest.lowerChestInventory))
            {
                this.drawString(this.fontRendererObj, "Search for price:", this.guiLeft + 180, this.guiTop + 26, 10526880);
                this.priceSearch.drawTextBox();
            }
        }
    }

    @Inject(method = "keyTyped(CI)V", cancellable = true, at = @At("HEAD"))
    private void keyTyped(char typedChar, int keyCode, CallbackInfo info) throws IOException
    {
        if (this.theSlot != null)
        {
            if (keyCode == KeyBindingHandler.KEY_SB_VIEW_RECIPE.getKeyCode())
            {
                SkyBlockRecipeViewer.viewRecipe(this.that.mc.thePlayer, this.theSlot, keyCode);
                info.cancel();
            }
            else if (keyCode == KeyBindingHandler.KEY_SB_OPEN_WIKI.getKeyCode())
            {
                ItemStack itemStack = this.theSlot.getStack();

                if (itemStack != null && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("ExtraAttributes"))
                {
                    String itemId = itemStack.getTagCompound().getCompoundTag("ExtraAttributes").getString("id").toLowerCase().replace("_", " ");
                    itemId = WordUtils.capitalize(itemId);
                    this.fandomUrl = "https://hypixel-skyblock.fandom.com/wiki/" + itemId.replace(" ", "_");
                    this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, this.fandomUrl, 10000, true));
                }
            }
        }

        if (this.that instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)this.that;

            if (this.isChatableGui(chest.lowerChestInventory))
            {
                if (this.inputField.isFocused())
                {
                    if (keyCode == 1)
                    {
                        this.inputField.setFocused(false);
                    }
                    info.cancel();
                }
                else
                {
                    super.keyTyped(typedChar, keyCode);
                }

                if (keyCode != 28 && keyCode != 156)
                {
                    if (keyCode == 200)
                    {
                        this.getSentHistory(-1);
                    }
                    else if (keyCode == 208)
                    {
                        this.getSentHistory(1);
                    }
                    else
                    {
                        this.inputField.textboxKeyTyped(typedChar, keyCode);
                    }
                }
                else
                {
                    String text = this.inputField.getText().trim();

                    if (!text.isEmpty())
                    {
                        this.sendChatMessage(text);
                    }
                    this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
                    this.inputField.setText("");
                    this.mc.ingameGUI.getChatGUI().resetScroll();
                }

                this.waitingOnAutocomplete = false;

                if (keyCode == 15)
                {
                    this.autocompletePlayerNames();
                }
                else
                {
                    this.playerNamesFound = false;
                }
            }
            else if (this.isAuctionBrowser(chest.lowerChestInventory))
            {
                if (this.priceSearch.isFocused())
                {
                    if (keyCode == 1)
                    {
                        this.priceSearch.setFocused(false);
                    }
                    info.cancel();
                }
                else
                {
                    super.keyTyped(typedChar, keyCode);
                }
                this.priceSearch.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Inject(method = "handleMouseClick(Lnet/minecraft/inventory/Slot;III)V", cancellable = true, at = @At("HEAD"))
    private void handleMouseClick(Slot slot, int slotId, int clickedButton, int clickType, CallbackInfo info)
    {
        if (slot != null)
        {
            ItemStack itemStack = slot.getStack();

            if (itemStack != null)
            {
                if (ExtendedConfig.instance.preventClickingOnDummyItem && this.ignoreNullItem(itemStack, IGNORE_ITEMS))
                {
                    info.cancel();
                }
                if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("ExtraAttributes"))
                {
                    String id = itemStack.getTagCompound().getCompoundTag("ExtraAttributes").getString("id");

                    if (id.equals("SKYBLOCK_MENU"))
                    {
                        this.mc.playerController.windowClick(this.that.inventorySlots.windowId, slotId, 2, 3, this.mc.thePlayer);
                        info.cancel();
                    }
                }
            }

            if (this.that instanceof GuiChest)
            {
                GuiChest chest = (GuiChest)this.that;

                if (itemStack != null)
                {
                    String name = itemStack.getDisplayName();

                    if (clickedButton == 0 && clickType == 0 && (MainEventHandler.isSuitableForGUI(INVENTORY_LIST, chest.lowerChestInventory) || ITEM_LIST.stream().anyMatch(itemName -> name.equals(itemName))))
                    {
                        this.mc.playerController.windowClick(this.that.inventorySlots.windowId, slotId, 2, 3, this.mc.thePlayer);
                        info.cancel();
                    }
                    if (clickedButton == 2 && clickType == 3 && this.canViewSeller(chest.lowerChestInventory))
                    {
                        if (itemStack.hasTagCompound())
                        {
                            NBTTagCompound compound = itemStack.getTagCompound().getCompoundTag("display");

                            if (compound.getTagId("Lore") == 9)
                            {
                                NBTTagList list = compound.getTagList("Lore", 8);

                                if (list.tagCount() > 0)
                                {
                                    for (int j1 = 0; j1 < list.tagCount(); ++j1)
                                    {
                                        String lore = EnumChatFormatting.getTextWithoutFormattingCodes(list.getStringTagAt(j1));

                                        if (lore.startsWith("Seller: "))
                                        {
                                            this.mc.thePlayer.sendChatMessage("/ah " + lore.replaceAll("Seller: ?(?:\\[VIP?\\u002B{0,1}\\]|\\[MVP?\\u002B{0,2}\\]|\\[YOUTUBE\\]){0,1} ", ""));
                                        }
                                    }
                                }
                            }
                        }
                        info.cancel();
                    }
                }
            }
        }
    }

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/entity/RenderItem.renderItemAndEffectIntoGUI(Lnet/minecraft/item/ItemStack;II)V"))
    private void renderRarity(Slot slot, CallbackInfo info)
    {
        if (ExtendedConfig.instance.showItemRarity)
        {
            if (this.that instanceof GuiChest)
            {
                GuiChest chest = (GuiChest)this.that;

                if (chest.lowerChestInventory.getDisplayName().getUnformattedText().matches("\\(\\d+\\/\\d+\\) Pets") || chest.lowerChestInventory.getDisplayName().getUnformattedText().equals("Pets"))
                {
                    if (slot.getHasStack() && slot.slotNumber >= 0 && slot.slotNumber <= 53)
                    {
                        Matcher matcher = PET_MENU_PATTERN.matcher(slot.getStack().getDisplayName());

                        if (matcher.matches())
                        {
                            String color = matcher.group("color");
                            RGB common = ColorUtils.stringToRGB("255,255,255");
                            RGB uncommon = ColorUtils.stringToRGB("85,255,85");
                            RGB rare = ColorUtils.stringToRGB("85,85,255");
                            RGB epic = ColorUtils.stringToRGB("170,0,170");
                            RGB legendary = ColorUtils.stringToRGB("255,170,0");
                            RGB mythic = ColorUtils.stringToRGB("255,85,255");
                            RGB special = ColorUtils.stringToRGB("255,85,85");

                            if (color.equals(EnumChatFormatting.WHITE.toString()))
                            {
                                RenderUtils.renderRarity(slot.xDisplayPosition, slot.yDisplayPosition, common);
                            }
                            else if (color.equals(EnumChatFormatting.GREEN.toString()))
                            {
                                RenderUtils.renderRarity(slot.xDisplayPosition, slot.yDisplayPosition, uncommon);
                            }
                            else if (color.equals(EnumChatFormatting.BLUE.toString()))
                            {
                                RenderUtils.renderRarity(slot.xDisplayPosition, slot.yDisplayPosition, rare);
                            }
                            else if (color.equals(EnumChatFormatting.DARK_PURPLE.toString()))
                            {
                                RenderUtils.renderRarity(slot.xDisplayPosition, slot.yDisplayPosition, epic);
                            }
                            else if (color.equals(EnumChatFormatting.GOLD.toString()))
                            {
                                RenderUtils.renderRarity(slot.xDisplayPosition, slot.yDisplayPosition, legendary);
                            }
                            else if (color.equals(EnumChatFormatting.LIGHT_PURPLE.toString()))
                            {
                                RenderUtils.renderRarity(slot.xDisplayPosition, slot.yDisplayPosition, mythic);
                            }
                            else if (color.equals(EnumChatFormatting.RED.toString()))
                            {
                                RenderUtils.renderRarity(slot.xDisplayPosition, slot.yDisplayPosition, special);
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/entity/RenderItem.renderItemAndEffectIntoGUI(Lnet/minecraft/item/ItemStack;II)V", shift = Shift.AFTER))
    private void renderAnvilLevel(Slot slot, CallbackInfo info)
    {
        if (this.that instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)this.that;
            int i = 0;
            int j = 0;
            String levelString = "";

            if (chest.lowerChestInventory.getDisplayName().getUnformattedText().equals("Anvil") || chest.lowerChestInventory.getDisplayName().getUnformattedText().equals("Reforge Item"))
            {
                Slot anvilSlot = this.that.inventorySlots.inventorySlots.get(31);
                ItemStack itemStack = this.that.inventorySlots.inventorySlots.get(22).getStack();
                i = anvilSlot.xDisplayPosition;
                j = anvilSlot.yDisplayPosition;

                if (itemStack != null && itemStack.hasTagCompound())
                {
                    NBTTagCompound compound = itemStack.getTagCompound().getCompoundTag("display");

                    if (compound.getTagId("Lore") == 9)
                    {
                        NBTTagList list = compound.getTagList("Lore", 8);

                        if (list.tagCount() > 0)
                        {
                            for (int j1 = 0; j1 < list.tagCount(); ++j1)
                            {
                                String lore = EnumChatFormatting.getTextWithoutFormattingCodes(list.getStringTagAt(j1));

                                if (lore.endsWith("Exp Levels") || lore.endsWith("Exp Level"))
                                {
                                    int level = 0;

                                    try
                                    {
                                        level = NumberFormat.getNumberInstance(Locale.US).parse(lore.replaceAll(" Exp Level(?:s){0,1}", "")).intValue();
                                    }
                                    catch (ParseException e) {}

                                    if (this.mc.thePlayer.experienceLevel < level)
                                    {
                                        levelString = EnumChatFormatting.RED + String.valueOf(level);
                                    }
                                    else
                                    {
                                        levelString = EnumChatFormatting.GREEN + String.valueOf(level);
                                    }
                                    break;
                                }
                                else if (lore.endsWith("Coins") || lore.endsWith("Coin"))
                                {
                                    int coin = 0;

                                    try
                                    {
                                        coin = NumberFormat.getNumberInstance(Locale.US).parse(lore.replaceAll(" Coin(?:s){0,1}", "")).intValue();
                                    }
                                    catch (ParseException e) {}

                                    levelString = EnumChatFormatting.GOLD + String.valueOf(coin);
                                    break;
                                }
                            }
                        }
                    }
                }
                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                this.drawCenteredString(this.mc.fontRendererObj, levelString, i + 8, j + 4, 0);
                GlStateManager.enableDepth();
                GlStateManager.popMatrix();
            }
        }
    }

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderItem;renderItemOverlayIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"))
    private void renderBids(Slot slot, CallbackInfo info)
    {
        if (this.that instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)this.that;

            if (MainEventHandler.bidHighlight && this.isRenderBids(chest.lowerChestInventory))
            {
                this.drawBids(slot);
            }
            this.drawCurrentSelectedPet(slot);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
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
                this.mc.displayGuiScreen(new GuiSkyBlockAPIViewer(GuiSkyBlockAPIViewer.GuiState.PLAYER, text.replace(text.substring(text.indexOf('\'')), ""), "", ""));
            }
        }
    }

    @Override
    protected void setText(String newChatText, boolean shouldOverwrite)
    {
        if (this.that instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)this.that;

            if (this.isChatableGui(chest.lowerChestInventory))
            {
                if (shouldOverwrite)
                {
                    this.inputField.setText(newChatText);
                }
                else
                {
                    this.inputField.writeText(newChatText);
                }
            }
        }
    }

    @Override
    public void onAutocompleteResponse(String[] list)
    {
        if (this.waitingOnAutocomplete)
        {
            this.playerNamesFound = false;
            this.foundPlayerNames.clear();

            String[] complete = ClientCommandHandler.instance.latestAutoComplete;

            if (complete != null)
            {
                list = ObjectArrays.concat(complete, list, String.class);
            }

            for (String s : list)
            {
                if (s.length() > 0)
                {
                    this.foundPlayerNames.add(s);
                }
            }

            String s1 = this.inputField.getText().substring(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false));
            String s2 = StringUtils.getCommonPrefix(list);
            s2 = EnumChatFormatting.getTextWithoutFormattingCodes(s2);

            if (s2.length() > 0 && !s1.equalsIgnoreCase(s2))
            {
                this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
                this.inputField.writeText(s2);
            }
            else if (this.foundPlayerNames.size() > 0)
            {
                this.playerNamesFound = true;
                this.autocompletePlayerNames();
            }
        }
    }

    @Override
    public GuiNumberField getNumberField()
    {
        return this.priceSearch;
    }

    @Override
    public void confirmClicked(boolean result, int id)
    {
        super.confirmClicked(result, id);

        if (id == 10000)
        {
            if (result)
            {
                CommonUtils.openLink(this.fandomUrl);
            }
            this.fandomUrl = null;
            this.mc.displayGuiScreen(this);
        }
    }

    private boolean ignoreNullItem(ItemStack itemStack, List<String> ignores)
    {
        String displayName = EnumChatFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName());
        return ignores.stream().anyMatch(name -> displayName.equals(name));
    }

    private void drawBids(Slot slot)
    {
        if (slot.getStack() != null && slot.getStack().hasTagCompound())
        {
            NBTTagCompound compound = slot.getStack().getTagCompound().getCompoundTag("display");

            if (compound.getTagId("Lore") == 9)
            {
                NBTTagList list = compound.getTagList("Lore", 8);

                if (list.tagCount() > 0)
                {
                    for (int j1 = 0; j1 < list.tagCount(); ++j1)
                    {
                        int slotLeft = slot.xDisplayPosition;
                        int slotTop = slot.yDisplayPosition;
                        int slotRight = slotLeft + 16;
                        int slotBottom = slotTop + 16;
                        String lore = EnumChatFormatting.getTextWithoutFormattingCodes(list.getStringTagAt(j1));
                        Matcher matcher = Pattern.compile("(?:(?:Top|Starting) bid|Buy it now): (?<coin>[0-9,]+) coins").matcher(lore);
                        int red = ColorUtils.to32BitColor(128, 255, 85, 85);
                        int green = ColorUtils.to32BitColor(128, 85, 255, 85);
                        int yellow = ColorUtils.to32BitColor(128, 255, 255, 85);

                        if (((ITradeGUI)(GuiChest)this.that).getNumberField() == null || ((ITradeGUI)(GuiChest)this.that).getNumberField().getText().isEmpty())
                        {
                            if (lore.startsWith("Starting bid:"))
                            {
                                this.drawGradientRect(slotLeft, slotTop, slotRight, slotBottom, green, green);
                            }
                            else if (lore.startsWith("Bidder:"))
                            {
                                this.drawGradientRect(slotLeft, slotTop, slotRight, slotBottom, red, red);
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
    }

    private void drawCurrentSelectedPet(Slot slot)
    {
        if (slot.getStack() != null && slot.getStack().hasTagCompound())
        {
            NBTTagCompound compound = slot.getStack().getTagCompound().getCompoundTag("display");

            if (compound.getTagId("Lore") == 9)
            {
                NBTTagList list = compound.getTagList("Lore", 8);

                if (list.tagCount() > 0)
                {
                    for (int j1 = 0; j1 < list.tagCount(); ++j1)
                    {
                        int slotLeft = slot.xDisplayPosition;
                        int slotTop = slot.yDisplayPosition;
                        int slotRight = slotLeft + 16;
                        int slotBottom = slotTop + 16;
                        String lore = EnumChatFormatting.getTextWithoutFormattingCodes(list.getStringTagAt(j1));
                        int green = ColorUtils.to32BitColor(150, 85, 255, 85);

                        if (lore.startsWith("Click to despawn"))
                        {
                            this.drawGradientRect(slotLeft, slotTop, slotRight, slotBottom, green, green);
                            break;
                        }
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
                this.drawGradientRect(slotLeft, slotTop, slotRight, slotBottom, color1, color1);
            }
            else
            {
                this.drawGradientRect(slotLeft, slotTop, slotRight, slotBottom, color2, color2);
            }
            break;
        case MIN:
            if (moneyFromAh >= priceMin)
            {
                this.drawGradientRect(slotLeft, slotTop, slotRight, slotBottom, color1, color1);
            }
            else
            {
                this.drawGradientRect(slotLeft, slotTop, slotRight, slotBottom, color2, color2);
            }
            break;
        case MAX:
            if (moneyFromAh <= priceMax)
            {
                this.drawGradientRect(slotLeft, slotTop, slotRight, slotBottom, color1, color1);
            }
            else
            {
                this.drawGradientRect(slotLeft, slotTop, slotRight, slotBottom, color2, color2);
            }
            break;
        case RANGED:
            if (moneyFromAh >= priceMin && moneyFromAh <= priceMax)
            {
                this.drawGradientRect(slotLeft, slotTop, slotRight, slotBottom, color1, color1);
            }
            else
            {
                this.drawGradientRect(slotLeft, slotTop, slotRight, slotBottom, color2, color2);
            }
            break;
        }
    }

    private boolean canViewSeller(IInventory lowerChestInventory)
    {
        String name = lowerChestInventory.getDisplayName().getUnformattedText();
        return name.equals("Auctions Browser") || name.equals("Your Bids") || name.equals("Auction View");
    }

    private boolean isAuctionBrowser(IInventory lowerChestInventory)
    {
        String name = lowerChestInventory.getDisplayName().getUnformattedText();
        return name.equals("Auctions Browser") || name.endsWith("'s Auctions");
    }

    private boolean isRenderBids(IInventory lowerChestInventory)
    {
        String name = lowerChestInventory.getDisplayName().getUnformattedText();
        return name.equals("Auctions Browser") || name.equals("Manage Auctions") || name.equals("Your Bids") || name.endsWith("'s Auctions");
    }

    private boolean isPeopleAuction(IInventory lowerChestInventory)
    {
        String name = lowerChestInventory.getDisplayName().getUnformattedText();
        return name.endsWith("'s Auctions");
    }

    private boolean isPeopleProfile(IInventory lowerChestInventory)
    {
        String name = lowerChestInventory.getDisplayName().getUnformattedText();
        return name.endsWith("'s Profile") || name.endsWith("' Profile");
    }

    // GuiChat stuff
    private void drawChat()
    {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN)
        {
            int i = this.mc.ingameGUI.getChatGUI().getLineCount();
            int k = this.mc.ingameGUI.getChatGUI().field_146253_i.size();
            float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            if (k > 0)
            {
                float f1 = this.mc.ingameGUI.getChatGUI().getChatScale();
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 8.0F, 0.0F);
                GlStateManager.scale(f1, f1, 1.0F);

                for (int i1 = 0; i1 + this.mc.ingameGUI.getChatGUI().scrollPos < this.mc.ingameGUI.getChatGUI().field_146253_i.size() && i1 < i; ++i1)
                {
                    ChatLine chatline = this.mc.ingameGUI.getChatGUI().field_146253_i.get(i1 + this.mc.ingameGUI.getChatGUI().scrollPos);

                    if (chatline != null)
                    {
                        int j1 = this.mc.ingameGUI.getUpdateCounter() - chatline.getUpdatedCounter();

                        if (j1 < 200)
                        {
                            double d0 = j1 / 200.0D;
                            d0 = 1.0D - d0;
                            d0 = d0 * 10.0D;
                            d0 = MathHelper.clamp_double(d0, 0.0D, 1.0D);
                            d0 = d0 * d0;
                            int l1 = (int)(255.0D * d0);
                            l1 = (int)(l1 * f);

                            if (l1 > 3)
                            {
                                int i2 = 0;
                                int j2 = -i1 * 9;
                                String s = chatline.getChatComponent().getFormattedText();
                                GlStateManager.enableBlend();
                                this.mc.fontRendererObj.drawStringWithShadow(s, i2, j2 - 8, 16777215 + (l1 << 24));
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }

    private void getSentHistory(int msgPos)
    {
        int i = this.sentHistoryCursor + msgPos;
        int j = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        i = MathHelper.clamp_int(i, 0, j);

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
                this.inputField.setText(this.mc.ingameGUI.getChatGUI().getSentMessages().get(i));
                this.sentHistoryCursor = i;
            }
        }
    }

    private void sendAutocompleteRequest(String leftOfCursor, String full)
    {
        if (leftOfCursor.length() >= 1)
        {
            ClientCommandHandler.instance.autoComplete(leftOfCursor, full);
            BlockPos blockpos = null;

            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                blockpos = this.mc.objectMouseOver.getBlockPos();
            }
            this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(leftOfCursor, blockpos));
            this.waitingOnAutocomplete = true;
        }
    }

    private void autocompletePlayerNames()
    {
        if (this.playerNamesFound)
        {
            this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());

            if (this.autocompleteIndex >= this.foundPlayerNames.size())
            {
                this.autocompleteIndex = 0;
            }
        }
        else
        {
            int i = this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.autocompleteIndex = 0;
            String s = this.inputField.getText().substring(i).toLowerCase();
            String s1 = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
            this.sendAutocompleteRequest(s1, s);

            if (this.foundPlayerNames.isEmpty())
            {
                return;
            }
            this.playerNamesFound = true;
            this.inputField.deleteFromCursor(i - this.inputField.getCursorPosition());
        }

        if (this.foundPlayerNames.size() > 1)
        {
            StringBuilder stringbuilder = new StringBuilder();

            for (String s2 : this.foundPlayerNames)
            {
                if (stringbuilder.length() > 0)
                {
                    stringbuilder.append(", ");
                }
                stringbuilder.append(s2);
            }
            this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(stringbuilder.toString()), 1);
        }
        this.inputField.writeText(EnumChatFormatting.getTextWithoutFormattingCodes(this.foundPlayerNames.get(this.autocompleteIndex++)));
    }

    private boolean isChatableGui(IInventory lowerChestInventory)
    {
        return MainEventHandler.CHATABLE_LIST.stream().anyMatch(invName -> lowerChestInventory.getDisplayName().getUnformattedText().contains(invName));
    }
}