package com.stevekung.skyblockcatia.gui.screen;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.time.StopWatch;
import org.lwjgl.glfw.GLFW;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.gui.APIErrorInfo;
import com.stevekung.skyblockcatia.gui.ScrollingListScreen;
import com.stevekung.skyblockcatia.gui.widget.RightClickTextFieldWidget;
import com.stevekung.skyblockcatia.gui.widget.button.APISearchButton;
import com.stevekung.skyblockcatia.gui.widget.button.SkyBlockProfileButton;
import com.stevekung.skyblockcatia.utils.PlayerNameSuggestionHelper;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.HypixelRank;
import com.stevekung.skyblockcatia.utils.skyblock.api.ProfileDataCallback;
import com.stevekung.stevekungslib.utils.CommonUtils;
import com.stevekung.stevekungslib.utils.JsonUtils;
import com.stevekung.stevekungslib.utils.LangUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class SkyBlockProfileViewerScreen extends Screen
{
    public static final String[] downloadingStates = new String[] {"", ".", "..", "..."};
    private RightClickTextFieldWidget usernameTextField;
    private APISearchButton checkButton;
    private Button closeButton;
    private String input = "";
    private String displayName = "";
    private boolean openFromPlayer;
    private boolean loadingApi;
    private boolean error;
    private String errorMessage;
    private String statusMessage;
    private List<ProfileDataCallback> profiles = new ArrayList<>();
    private final StopWatch watch = new StopWatch();
    private boolean fromError;
    private PlayerNameSuggestionHelper suggestionHelper;
    private String guild = "";
    private ScrollingListScreen errorInfo;
    private List<String> errorList = new ArrayList<>();

    public SkyBlockProfileViewerScreen(GuiState state)
    {
        this(state, "", "", "");
    }

    public SkyBlockProfileViewerScreen(GuiState state, String username, String displayName, String guild)
    {
        this(state, username, displayName, guild, null);
    }

    public SkyBlockProfileViewerScreen(GuiState state, String username, String displayName, String guild, List<ProfileDataCallback> profiles)
    {
        super(JsonUtils.create("API Viewer"));

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
    public void init()
    {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.addButton(this.checkButton = new APISearchButton(this.width / 2 + 78, 46, button ->
        {
            this.input = this.usernameTextField.getText();
            this.profiles.clear();
            this.guild = "";
            this.loadingApi = true;

            CommonUtils.runAsync(() ->
            {
                try
                {
                    this.watch.reset();
                    this.watch.start();
                    this.checkAPI();

                    if (this.watch.getTime() > 0)
                    {
                        SkyBlockcatiaMod.LOGGER.info("API Download finished in: {}ms", this.watch.getTime());
                    }
                }
                catch (Throwable e)
                {
                    this.errorList.add(TextFormatting.UNDERLINE.toString() + TextFormatting.BOLD + e.getClass().getName() + ": " + e.getMessage());

                    for (StackTraceElement stack : e.getStackTrace())
                    {
                        this.errorList.add("at " + stack.toString());
                    }
                    this.setErrorMessage("", true);
                    e.printStackTrace();
                }
            });
        } ));
        this.addButton(this.closeButton = new Button(this.width / 2 - 75, this.height / 4 + 152, 150, 20, LangUtils.translate("gui.close"), button -> this.minecraft.displayGuiScreen(this.error ? new SkyBlockProfileViewerScreen(GuiState.ERROR, this.input, this.displayName, this.guild) : null)));
        this.usernameTextField = new RightClickTextFieldWidget(this.width / 2 - 75, 45, 150, 20);
        this.usernameTextField.setMaxStringLength(32767);
        this.usernameTextField.setFocused2(true);
        this.usernameTextField.setText(this.input);
        this.usernameTextField.setResponder(text -> this.setCommandResponder());
        this.checkButton.active = this.usernameTextField.getText().trim().length() > 0;
        this.checkButton.visible = !this.error;
        this.children.add(this.usernameTextField);
        this.suggestionHelper = new PlayerNameSuggestionHelper(this.minecraft, this, this.usernameTextField, this.font, true, true, 0, 7, false, Integer.MIN_VALUE);
        this.suggestionHelper.func_228124_a_(true);
        this.suggestionHelper.init();

        if (this.error)
        {
            this.closeButton.setMessage(LangUtils.translate("gui.back"));
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
                        SkyBlockcatiaMod.LOGGER.info("API Download finished in: {}ms", this.watch.getTime());
                    }
                    this.watch.reset();
                }
                catch (Throwable e)
                {
                    this.errorList.add(TextFormatting.UNDERLINE.toString() + TextFormatting.BOLD + e.getClass().getName() + ": " + e.getMessage());

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
            List<SkyBlockProfileButton> buttons = new ArrayList<>();

            for (ProfileDataCallback data : this.profiles)
            {
                SkyBlockProfileButton button = new SkyBlockProfileButton(this.width / 2 - 75, 75, 150, 20, data);
                buttons.add(button);
            }

            buttons.sort((button1, button2) -> new CompareToBuilder().append(button2.getLastSave(), button1.getLastSave()).build());

            int i2 = 0;

            for (SkyBlockProfileButton button : buttons)
            {
                if (i2 == 0)
                {
                    button.setMessage(TextFormatting.YELLOW + "" + TextFormatting.BOLD + button.getMessage());
                }
                button.y += i2 * 22;
                button.setProfileList(this.profiles);
                this.addButton(button);
                ++i2;
            }
        }
        if (this.errorInfo != null)
        {
            this.errorInfo = new APIErrorInfo(this, this.width - 39, this.height, 40, this.height / 4 + 128, 19, 12, this.errorList);
        }
    }

    @Override
    @Nullable
    public IGuiEventListener getFocused()
    {
        return this.usernameTextField;
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        String s = this.usernameTextField.getText();
        this.init(mc, width, height);
        this.usernameTextField.setText(s);
        this.suggestionHelper.init();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        if (delta > 1.0D)
        {
            delta = 1.0D;
        }
        if (delta < -1.0D)
        {
            delta = -1.0D;
        }

        if (this.suggestionHelper.onScroll(delta))
        {
            return true;
        }
        else
        {
            return super.mouseScrolled(mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers)
    {
        if (!this.loadingApi && !this.error)
        {
            if ((key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) && !StringUtils.isNullOrEmpty(this.usernameTextField.getText()))
            {
                this.checkButton.onPress();
                return true;
            }
            else if (key == GLFW.GLFW_KEY_F5 && !this.profiles.isEmpty())
            {
                this.minecraft.displayGuiScreen(new SkyBlockProfileViewerScreen(GuiState.PLAYER, this.input, this.displayName, this.guild));
            }
            else if (this.suggestionHelper.onKeyPressed(key, scanCode, modifiers))
            {
                return true;
            }
            else
            {
                return super.keyPressed(key, scanCode, modifiers);
            }
        }
        return false;
    }

    @Override
    public void tick()
    {
        this.usernameTextField.tick();
        this.checkButton.active = this.usernameTextField.getText().trim().length() > 0;
    }

    @Override
    public void removed()
    {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (!this.loadingApi)
        {
            if (this.suggestionHelper.onClick((int)mouseX, (int)mouseY, mouseButton))
            {
                return true;
            }
            else if (this.usernameTextField.mouseClicked(mouseX, mouseY, mouseButton))
            {
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();

        if (this.loadingApi)
        {
            String text = "Downloading SkyBlock stats";
            int i = this.font.getStringWidth(text);
            this.drawCenteredString(this.font, text, this.width / 2, this.height / 2 + this.font.FONT_HEIGHT * 2 - 35, 16777215);
            this.drawString(this.font, downloadingStates[(int)(Util.milliTime() / 500L % downloadingStates.length)], this.width / 2 + i / 2, this.height / 2 + this.font.FONT_HEIGHT * 2 - 35, 16777215);
            this.drawCenteredString(this.font, "Status: " + TextFormatting.GRAY + this.statusMessage, this.width / 2, this.height / 2 + this.font.FONT_HEIGHT * 2 - 15, 16777215);
        }
        else
        {
            this.drawCenteredString(this.font, "SkyBlock API Viewer", this.width / 2, 20, 16777215);

            if (this.error)
            {
                if (this.errorInfo != null)
                {
                    this.errorInfo.render(mouseX, mouseY, partialTicks);
                }
                else
                {
                    this.drawCenteredString(this.font, TextFormatting.RED + this.errorMessage, this.width / 2, 100, 16777215);
                }
                super.render(mouseX, mouseY, partialTicks);
            }
            else
            {
                if (!this.profiles.isEmpty())
                {
                    this.drawCenteredString(this.font, this.displayName + TextFormatting.GOLD + " Profiles" + this.guild, this.width / 2, 30, 16777215);
                }

                this.suggestionHelper.render(mouseX, mouseY);
                this.usernameTextField.render(mouseX, mouseY, partialTicks);

                if (this.suggestionHelper.suggestions == null && StringUtils.isNullOrEmpty(this.usernameTextField.getText()) && !this.usernameTextField.isFocused())
                {
                    this.drawString(this.font, "Enter username", this.width / 2 - 71, 51, 10526880);
                }

                super.render(mouseX, mouseY, partialTicks);

                for (Widget button : this.buttons.stream().filter(button -> button instanceof SkyBlockProfileButton).collect(Collectors.toList()))
                {
                    boolean hover = this.suggestionHelper.suggestions == null && mouseX >= button.x && mouseY >= button.y && mouseX < button.x + button.getWidth() && mouseY < button.y + button.getHeight();
                    button.visible = button.active = this.suggestionHelper.suggestions == null;

                    if (hover)
                    {
                        GuiUtils.drawHoveringText(Collections.singletonList(((SkyBlockProfileButton)button).getLastActive()), mouseX, mouseY, this.width, this.height, -1, this.font);
                        RenderSystem.disableLighting();
                        break;
                    }
                }
            }
        }
    }

    private void checkAPI() throws IOException
    {
        URL url = null;

        if (this.input.length() == 32)
        {
            url = new URL(SBAPIUtils.PLAYER_UUID + this.input);
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
                url = new URL(SBAPIUtils.PLAYER_NAME + this.input);
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
                        rankPlus = TextFormatting.valueOf(rankPlusColor.getAsString()) + "+";
                    }
                    else
                    {
                        baseRankText = rankType == HypixelRank.Type.YOUTUBER ? TextFormatting.WHITE + rankType.getName() : rankType.getName();
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
                            baseRankText = "MVP" + TextFormatting.valueOf(rankPlusColor.getAsString()) + "++";
                        }
                        else
                        {
                            baseRankText = "MVP" + TextFormatting.RED + "++";
                        }

                        if (monthlyRankColor != null)
                        {
                            color = TextFormatting.valueOf(monthlyRankColor.getAsString()).toString();
                        }
                        else
                        {
                            color = TextFormatting.GOLD.toString();
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
                                rankPlus = TextFormatting.valueOf(rankPlusColor.getAsString()) + "+";
                            }
                            else
                            {
                                rankPlus = TextFormatting.RED + "+";
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
                    baseRankText = rankType == HypixelRank.Type.YOUTUBER ? TextFormatting.WHITE + rankType.getName() : rankType.getName();
                    color = rankType.getColor().toString();
                }
                if (monthlyPackageRank != null && !monthlyPackageRank.getAsString().equals("NONE"))
                {
                    baseRankText = "MVP" + TextFormatting.valueOf(rankPlusColor.getAsString()) + "++";

                    if (monthlyRankColor != null)
                    {
                        color = TextFormatting.valueOf(monthlyRankColor.getAsString()).toString();
                    }
                    else
                    {
                        color = TextFormatting.GOLD.toString();
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
        URL urlGuild = new URL(SBAPIUtils.GUILD + uuid);
        JsonObject objGuild = new JsonParser().parse(IOUtils.toString(urlGuild.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        JsonElement guild = objGuild.get("guild");

        if (!guild.isJsonNull())
        {
            String guildName = guild.getAsJsonObject().get("name").getAsString();
            this.guild = TextFormatting.YELLOW + " Guild: " + TextFormatting.GOLD + guildName;
        }

        URL urlSB = new URL(SBAPIUtils.SKYBLOCK_PROFILES + uuid);
        JsonObject objSB = new JsonParser().parse(IOUtils.toString(urlSB.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        JsonElement sbProfile = objSB.get("profiles");
        GameProfile gameProfile = SkullTileEntity.updateGameProfile(new GameProfile(UUID.fromString(uuid.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5")), this.input));

        if (sbProfile.isJsonNull())
        {
            this.statusMessage = "Found default profile";
            ProfileDataCallback callback = new ProfileDataCallback(uuid, "Avocado", this.input, this.displayName, this.guild, uuid, gameProfile, -1);
            this.minecraft.displayGuiScreen(new SkyBlockAPIViewerScreen(this.profiles, callback));
            return;
        }

        JsonArray profilesList = sbProfile.getAsJsonArray();
        List<SkyBlockProfileButton> buttons = new ArrayList<>();

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
            SkyBlockProfileButton button = new SkyBlockProfileButton(this.width / 2 - 75, 75, 150, 20, callback);

            if (hasOneProfile)
            {
                this.minecraft.displayGuiScreen(new SkyBlockAPIViewerScreen(this.profiles, callback));
                break;
            }

            buttons.add(button);
            this.profiles.add(callback);
        }

        buttons.sort((button1, button2) -> new CompareToBuilder().append(button2.getLastSave(), button1.getLastSave()).build());

        int i2 = 0;

        for (SkyBlockProfileButton button : buttons)
        {
            if (i2 == 0)
            {
                button.setMessage(TextFormatting.YELLOW + "" + TextFormatting.BOLD + button.getMessage());
            }
            button.y += i2 * 22;
            button.setProfileList(this.profiles);
            this.addButton(button);
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
        this.checkButton.active = !this.error;
        this.usernameTextField.active = !this.error;
        this.closeButton.setMessage(LangUtils.translate("gui.back"));

        if (errorList)
        {
            this.errorInfo = new APIErrorInfo(this, this.width - 39, this.height, 40, this.height / 4 + 128, 19, 12, this.errorList);
        }
        else
        {
            this.errorMessage = message;
        }
    }

    private void setCommandResponder()
    {
        this.suggestionHelper.func_228124_a_(true);
        this.suggestionHelper.init();
    }

    public enum GuiState
    {
        EMPTY, ERROR, PLAYER, SEARCH;
    }
}