package com.stevekung.skyblockcatia.gui.api;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.time.StopWatch;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.ObjectArrays;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.gui.*;
import com.stevekung.skyblockcatia.utils.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.*;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiSkyBlockAPIViewer extends GuiScreen implements ITabComplete
{
    public static final String[] downloadingStates = new String[] {"", ".", "..", "..."};
    private static boolean firstLoad;
    private GuiRightClickTextField usernameTextField;
    private GuiButtonSearch checkButton;
    private GuiButton closeButton;
    private GuiButtonItem selfButton;
    private String input = "";
    private String displayName = "";
    private boolean openFromPlayer;
    private boolean loadingApi;
    private boolean error;
    private String errorMessage;
    private String statusMessage;
    private List<ProfileDataCallback> profiles = new ArrayList<>();
    private final StopWatch watch = new StopWatch();
    private List<GuiSBProfileButton> profileButtonList = new ArrayList<>();
    private boolean fromError;
    private boolean playerNamesFound;
    private boolean waitingOnAutocomplete;
    private int autocompleteIndex;
    private List<String> foundPlayerNames = new ArrayList<>();
    private String guild = "";
    private GuiScrollingList errorInfo;
    private List<String> errorList = new ArrayList<>();

    public GuiSkyBlockAPIViewer(GuiState state)
    {
        this(state, "", "", "");
    }

    public GuiSkyBlockAPIViewer(GuiState state, String username, String displayName, String guild)
    {
        this(state, username, displayName, guild, null);
    }

    public GuiSkyBlockAPIViewer(GuiState state, String username, String displayName, String guild, List<ProfileDataCallback> profiles)
    {
        if (state == GuiState.SEARCH)
        {
            this.profiles = profiles;
        }
        this.loadingApi = state == GuiState.PLAYER;
        this.openFromPlayer = state == GuiState.PLAYER;
        this.fromError = state == GuiState.ERROR;
        this.displayName = displayName;
        this.input = username;
        this.guild = guild;
    }

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(this.checkButton = new GuiButtonSearch(0, this.width / 2 + 78, 46));
        this.buttonList.add(this.closeButton = new GuiButton(1, this.width / 2 - 75, this.height / 4 + 152, 150, 20, LangUtils.translate("gui.close")));
        this.buttonList.add(this.selfButton = new GuiButtonItem(2, this.width / 2 - 96, 46, !firstLoad ? new ItemStack(Items.skull) : RenderUtils.getPlayerHead(GameProfileUtils.getUsername()), "Check Self"));
        this.usernameTextField = new GuiRightClickTextField(2, this.fontRendererObj, this.width / 2 - 75, 45, 150, 20);
        this.usernameTextField.setMaxStringLength(32767);
        this.usernameTextField.setFocused(true);
        this.usernameTextField.setText(this.input);
        this.checkButton.enabled = this.usernameTextField.getText().trim().length() > 0;
        this.checkButton.visible = !this.error;

        if (this.error)
        {
            this.closeButton.displayString = LangUtils.translate("gui.back");
        }
        if (this.fromError)
        {
            this.usernameTextField.setText(this.input);
        }

        if (this.openFromPlayer)
        {
            this.openFromPlayer = false;

            CommonUtils.runAsync(() ->
            {
                try
                {
                    this.watch.start();
                    this.checkAPI();
                    this.watch.stop();

                    if (this.watch.getTime() > 0)
                    {
                        LoggerIN.info("API Download finished in: {}ms", this.watch.getTime());
                    }
                    this.watch.reset();
                }
                catch (Throwable e)
                {
                    this.errorList.add(EnumChatFormatting.UNDERLINE.toString() + EnumChatFormatting.BOLD + e.getClass().getName() + ": " + e.getMessage());

                    for (StackTraceElement stack : e.getStackTrace())
                    {
                        this.errorList.add("at " + stack.toString());
                    }
                    this.setErrorMessage("", true);
                    e.printStackTrace();
                }
            });
        }
        if (!this.profiles.isEmpty())
        {
            int i = 0;
            List<GuiSBProfileButton> buttons = new ArrayList<>();

            for (ProfileDataCallback data : this.profiles)
            {
                GuiSBProfileButton button = new GuiSBProfileButton(i + 1000, this.width / 2 - 75, 75, 150, 20, data);
                buttons.add(button);
                ++i;
            }

            buttons.sort((button1, button2) -> new CompareToBuilder().append(button2.getLastSave(), button1.getLastSave()).build());

            int i2 = 0;

            for (GuiSBProfileButton button : buttons)
            {
                if (i2 == 0)
                {
                    button.displayString = EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + button.displayString;
                }
                button.yPosition += i2 * 22;
                button.setProfileList(this.profiles);
                this.profileButtonList.add(button);
                ++i2;
            }
        }
        if (this.errorInfo != null)
        {
            this.errorInfo = new APIErrorInfo(this, this.width - 39, this.height, 40, this.height / 4 + 128, 19, 12, this.width, this.height, this.errorList);
        }
    }

    @Override
    public void updateScreen()
    {
        this.usernameTextField.updateCursorCounter();
        this.checkButton.enabled = this.usernameTextField.getText().trim().length() > 0;

        if (!firstLoad && this.selfButton.getItemStack().getItem() == Items.skull && this.selfButton.getItemStack().getItemDamage() == 0)
        {
            CommonUtils.runAsync(() -> this.selfButton.setItemStack(RenderUtils.getPlayerHead(GameProfileUtils.getUsername())));
            firstLoad = true;
        }
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                this.input = this.usernameTextField.getText();
                this.profiles.clear();
                this.profileButtonList.clear();
                this.guild = "";
                this.loadingApi = true;

                CommonUtils.runAsync(() ->
                {
                    try
                    {
                        this.watch.start();
                        this.checkAPI();
                        this.watch.stop();

                        if (this.watch.getTime() > 0)
                        {
                            LoggerIN.info("API Download finished in: {}ms", this.watch.getTime());
                        }
                        this.watch.reset();
                    }
                    catch (Throwable e)
                    {
                        this.errorList.add(EnumChatFormatting.UNDERLINE.toString() + EnumChatFormatting.BOLD + e.getClass().getName() + ": " + e.getMessage());

                        for (StackTraceElement stack : e.getStackTrace())
                        {
                            this.errorList.add("at " + stack.toString());
                        }
                        this.setErrorMessage("", true);
                        e.printStackTrace();
                    }
                });
            }
            else if (button.id == 1)
            {
                this.mc.displayGuiScreen(this.error ? new GuiSkyBlockAPIViewer(GuiState.ERROR, this.input, this.displayName, this.guild) : null);
            }
            else if (button.id == 2)
            {
                this.mc.displayGuiScreen(new GuiSkyBlockAPIViewer(GuiState.PLAYER, GameProfileUtils.getUsername(), this.displayName, ""));
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        this.usernameTextField.textboxKeyTyped(typedChar, keyCode);
        this.waitingOnAutocomplete = false;

        if (keyCode == 15)
        {
            this.autocompletePlayerNames();
        }
        else
        {
            this.playerNamesFound = false;
        }

        if (keyCode != 28 && keyCode != 156)
        {
            if (keyCode == 1)
            {
                this.actionPerformed(this.closeButton);
            }
            else if (keyCode == 63 && !this.profiles.isEmpty())
            {
                this.mc.displayGuiScreen(new GuiSkyBlockAPIViewer(GuiState.PLAYER, this.input, this.displayName, this.guild));
            }
        }
        else
        {
            if (!this.loadingApi && !this.error)
            {
                this.actionPerformed(this.checkButton);
                this.usernameTextField.setFocused(false);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (!this.loadingApi)
        {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            this.usernameTextField.mouseClicked(mouseX, mouseY, mouseButton);

            if (mouseButton == 0)
            {
                for (GuiSBProfileButton button : this.profileButtonList)
                {
                    if (button.mousePressed(this.mc, mouseX, mouseY))
                    {
                        this.selectedButton = button;
                        button.playPressSound(this.mc.getSoundHandler());
                    }
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.selfButton.visible = !this.loadingApi && !this.error;

        if (this.loadingApi)
        {
            String text = "Downloading SkyBlock stats";
            int i = this.fontRendererObj.getStringWidth(text);
            this.drawCenteredString(this.fontRendererObj, text, this.width / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2 - 35, 16777215);
            this.drawString(this.fontRendererObj, downloadingStates[(int)(Minecraft.getSystemTime() / 500L % downloadingStates.length)], this.width / 2 + i / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2 - 35, 16777215);
            this.drawCenteredString(this.fontRendererObj, "Status: " + EnumChatFormatting.GRAY + this.statusMessage, this.width / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2 - 15, 16777215);
        }
        else
        {
            this.drawCenteredString(this.fontRendererObj, "SkyBlock API Viewer", this.width / 2, 20, 16777215);

            if (this.error)
            {
                if (this.errorInfo != null)
                {
                    this.errorInfo.drawScreen(mouseX, mouseY, partialTicks);
                }
                else
                {
                    this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.RED + this.errorMessage, this.width / 2, 100, 16777215);
                }
                super.drawScreen(mouseX, mouseY, partialTicks);
            }
            else
            {
                if (!this.profiles.isEmpty())
                {
                    this.drawCenteredString(this.fontRendererObj, this.displayName + EnumChatFormatting.GOLD + " Profiles" + this.guild, this.width / 2, 30, 16777215);
                }

                this.usernameTextField.drawTextBox();

                if (StringUtils.isNullOrEmpty(this.usernameTextField.getText()) && !this.usernameTextField.isFocused())
                {
                    this.drawString(this.fontRendererObj, "Enter Username or UUID", this.width / 2 - 71, 51, 10526880);
                }

                for (GuiSBProfileButton button : this.profileButtonList)
                {
                    button.drawButton(this.mc, mouseX, mouseY);
                }

                super.drawScreen(mouseX, mouseY, partialTicks);

                for (GuiSBProfileButton button : this.profileButtonList)
                {
                    boolean isHover = mouseX >= button.xPosition && mouseY >= button.yPosition && mouseX < button.xPosition + button.width && mouseY < button.yPosition + button.height;

                    if (isHover)
                    {
                        GuiUtils.drawHoveringText(Collections.singletonList(button.getLastActive()), mouseX, mouseY, this.mc.displayWidth, this.mc.displayHeight, -1, this.fontRendererObj);
                        GlStateManager.disableLighting();
                    }
                }
            }
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        this.profileButtonList.clear();
        super.setWorldAndResolution(mc, width, height);
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

            String s1 = this.usernameTextField.getText().substring(this.usernameTextField.func_146197_a(-1, this.usernameTextField.getCursorPosition(), false));
            String s2 = org.apache.commons.lang3.StringUtils.getCommonPrefix(list);
            s2 = EnumChatFormatting.getTextWithoutFormattingCodes(s2);

            if (s2.length() > 0 && !s1.equalsIgnoreCase(s2))
            {
                this.usernameTextField.deleteFromCursor(this.usernameTextField.func_146197_a(-1, this.usernameTextField.getCursorPosition(), false) - this.usernameTextField.getCursorPosition());
                this.usernameTextField.writeText(s2);
            }
            else if (this.foundPlayerNames.size() > 0)
            {
                this.playerNamesFound = true;
                this.autocompletePlayerNames();
            }
        }
    }

    private void checkAPI() throws IOException
    {
        URL url = null;

        if (this.input.length() == 32)
        {
            url = new URL(SkyBlockAPIUtils.PLAYER_UUID + this.input);
        }
        else
        {
            if (!this.input.matches("\\w+"))
            {
                this.setErrorMessage("Invalid Username Pattern!", false);
                return;
            }
            else
            {
                url = new URL(SkyBlockAPIUtils.PLAYER_NAME + this.input);
            }
        }

        this.statusMessage = "Getting Hypixel API";

        JsonObject obj = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();

        if (!obj.get("success").getAsBoolean())
        {
            this.setErrorMessage(obj.get("cause").getAsString(), false);
            return;
        }

        JsonElement jsonPlayer = obj.get("player");

        if (jsonPlayer.isJsonNull())
        {
            this.setErrorMessage("Player not found!", false);
            return;
        }

        JsonElement newPackageRank = jsonPlayer.getAsJsonObject().get("newPackageRank"); // base rank
        JsonElement rank = jsonPlayer.getAsJsonObject().get("rank"); // rank priority NORMAL/YOUTUBER
        JsonElement rankPlusColor = jsonPlayer.getAsJsonObject().get("rankPlusColor");

        JsonElement monthlyPackageRank = jsonPlayer.getAsJsonObject().get("monthlyPackageRank");
        JsonElement monthlyRankColor = jsonPlayer.getAsJsonObject().get("monthlyRankColor");
        JsonElement prefix = jsonPlayer.getAsJsonObject().get("prefix");

        String baseRankText = "";
        String rankPlus = "";
        String color = "";

        try
        {
            if (newPackageRank != null)
            {
                if (rank != null)
                {
                    HypixelRank.Type rankType = HypixelRank.Type.valueOf(rank.getAsString());

                    if (rankType == HypixelRank.Type.NORMAL)
                    {
                        HypixelRank.Base baseRank = HypixelRank.Base.valueOf(newPackageRank.getAsString());
                        baseRankText = baseRank.getName();
                        color = baseRank.getColor().toString();
                        rankPlus = EnumChatFormatting.valueOf(rankPlusColor.getAsString()) + "+";
                    }
                    else
                    {
                        baseRankText = rankType == HypixelRank.Type.YOUTUBER ? EnumChatFormatting.WHITE + rankType.getName() : rankType.getName();
                        color = rankType.getColor().toString();
                    }
                }
                else
                {
                    HypixelRank.Base baseRank = HypixelRank.Base.valueOf(newPackageRank.getAsString());

                    if (monthlyPackageRank != null && !monthlyPackageRank.getAsString().equals("NONE"))
                    {
                        if (rankPlusColor != null)
                        {
                            baseRankText = "MVP" + EnumChatFormatting.valueOf(rankPlusColor.getAsString()) + "++";
                        }
                        else
                        {
                            baseRankText = "MVP" + EnumChatFormatting.RED + "++";
                        }

                        if (monthlyRankColor != null)
                        {
                            color = EnumChatFormatting.valueOf(monthlyRankColor.getAsString()).toString();
                        }
                        else
                        {
                            color = EnumChatFormatting.GOLD.toString();
                        }
                    }
                    else
                    {
                        baseRankText = baseRank.getName();
                        color = baseRank.getColor().toString();

                        if (baseRank == HypixelRank.Base.VIP_PLUS || baseRank == HypixelRank.Base.MVP_PLUS)
                        {
                            if (rankPlusColor != null)
                            {
                                rankPlus = EnumChatFormatting.valueOf(rankPlusColor.getAsString()) + "+";
                            }
                            else
                            {
                                rankPlus = EnumChatFormatting.RED + "+";
                            }
                        }
                    }
                }
            }
            else
            {
                if (rank != null)
                {
                    HypixelRank.Type rankType = HypixelRank.Type.valueOf(rank.getAsString());
                    baseRankText = rankType == HypixelRank.Type.YOUTUBER ? EnumChatFormatting.WHITE + rankType.getName() : rankType.getName();
                    color = rankType.getColor().toString();
                }
                if (monthlyPackageRank != null && !monthlyPackageRank.getAsString().equals("NONE"))
                {
                    baseRankText = "MVP" + EnumChatFormatting.valueOf(rankPlusColor.getAsString()) + "++";

                    if (monthlyRankColor != null)
                    {
                        color = EnumChatFormatting.valueOf(monthlyRankColor.getAsString()).toString();
                    }
                    else
                    {
                        color = EnumChatFormatting.GOLD.toString();
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.input = jsonPlayer.getAsJsonObject().get("displayname").getAsString();

        if (prefix != null)
        {
            this.displayName = prefix.getAsString() + " " + this.input;
        }
        else
        {
            if (!baseRankText.isEmpty())
            {
                this.displayName = color + "[" + baseRankText + rankPlus + color + "] " + this.input;
            }
            else
            {
                this.displayName = HypixelRank.Base.NONE.getColor() + this.input;
            }
        }

        String uuid = jsonPlayer.getAsJsonObject().get("uuid").getAsString();
        URL urlGuild = new URL(SkyBlockAPIUtils.GUILD + uuid);
        JsonObject objGuild = new JsonParser().parse(IOUtils.toString(urlGuild.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        JsonElement guild = objGuild.get("guild");

        if (!guild.isJsonNull())
        {
            String guildName = guild.getAsJsonObject().get("name").getAsString();
            this.guild = EnumChatFormatting.YELLOW + " Guild: " + EnumChatFormatting.GOLD + guildName;
        }

        URL urlSB = new URL(SkyBlockAPIUtils.SKYBLOCK_PROFILES + uuid);
        JsonObject objSB = new JsonParser().parse(IOUtils.toString(urlSB.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        JsonElement sbProfile = objSB.get("profiles");
        GameProfile gameProfile = TileEntitySkull.updateGameprofile(new GameProfile(UUID.fromString(uuid.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5")), this.input));

        if (sbProfile.isJsonNull())
        {
            this.statusMessage = "Found default profile";
            ProfileDataCallback callback = new ProfileDataCallback(uuid, "Avocado", this.input, this.displayName, this.guild, uuid, gameProfile, -1);
            this.mc.displayGuiScreen(new GuiSkyBlockData(this.profiles, callback));
            return;
        }

        JsonArray profilesList = sbProfile.getAsJsonArray();
        int i = 0;
        List<GuiSBProfileButton> buttons = new ArrayList<>();

        for (JsonElement profile : profilesList)
        {
            boolean hasOneProfile = profilesList.size() == 1;
            long lastSave = -1;
            JsonObject availableProfile = null;

            for (Map.Entry<String, JsonElement> entry : profile.getAsJsonObject().get("members").getAsJsonObject().entrySet())
            {
                if (!entry.getKey().equals(uuid))
                {
                    continue;
                }
                JsonElement lastSaveEle = entry.getValue().getAsJsonObject().get("last_save");
                lastSave = lastSaveEle == null ? -1 : lastSaveEle.getAsLong();
            }

            availableProfile = profile.getAsJsonObject();
            ProfileDataCallback callback = new ProfileDataCallback(availableProfile, this.input, this.displayName, this.guild, uuid, gameProfile, hasOneProfile ? -1 : lastSave);
            GuiSBProfileButton button = new GuiSBProfileButton(i + 1000, this.width / 2 - 75, 75, 150, 20, callback);

            if (hasOneProfile)
            {
                this.mc.displayGuiScreen(new GuiSkyBlockData(this.profiles, callback));
                break;
            }

            buttons.add(button);
            this.profiles.add(callback);
            ++i;
        }

        buttons.sort((button1, button2) -> new CompareToBuilder().append(button2.getLastSave(), button1.getLastSave()).build());

        int i2 = 0;

        for (GuiSBProfileButton button : buttons)
        {
            if (i2 == 0)
            {
                button.displayString = EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + button.displayString;
            }
            button.yPosition += i2 * 22;
            button.setProfileList(this.profiles);
            this.profileButtonList.add(button);
            ++i2;
        }
        this.usernameTextField.setText(this.input);
        this.loadingApi = false;
    }

    private void setErrorMessage(String message, boolean errorList)
    {
        this.error = true;
        this.loadingApi = false;
        this.checkButton.visible = !this.error;
        this.selfButton.visible = !this.error;
        this.closeButton.displayString = LangUtils.translate("gui.back");

        if (errorList)
        {
            this.errorInfo = new APIErrorInfo(this, this.width - 39, this.height, 40, this.height / 4 + 128, 19, 12, this.width, this.height, this.errorList);
        }
        else
        {
            this.errorMessage = message;
        }
    }

    private void autocompletePlayerNames()
    {
        if (this.playerNamesFound)
        {
            this.usernameTextField.deleteFromCursor(this.usernameTextField.func_146197_a(-1, this.usernameTextField.getCursorPosition(), false) - this.usernameTextField.getCursorPosition());

            if (this.autocompleteIndex >= this.foundPlayerNames.size())
            {
                this.autocompleteIndex = 0;
            }
        }
        else
        {
            int i = this.usernameTextField.func_146197_a(-1, this.usernameTextField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.autocompleteIndex = 0;
            String s = this.usernameTextField.getText().substring(i).toLowerCase();
            String s1 = this.usernameTextField.getText().substring(0, this.usernameTextField.getCursorPosition());
            this.sendAutocompleteRequest(s1, s);

            if (this.foundPlayerNames.isEmpty())
            {
                return;
            }
            this.playerNamesFound = true;
            this.usernameTextField.deleteFromCursor(i - this.usernameTextField.getCursorPosition());
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
        this.usernameTextField.writeText(EnumChatFormatting.getTextWithoutFormattingCodes(this.foundPlayerNames.get(this.autocompleteIndex++)));
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

    public enum GuiState
    {
        EMPTY, ERROR, PLAYER, SEARCH;
    }
}