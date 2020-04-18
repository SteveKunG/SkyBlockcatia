package stevekung.mods.indicatia.gui.api;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.time.StopWatch;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.ObjectArrays;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.*;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.config.GuiUtils;
import stevekung.mods.indicatia.gui.GuiButtonSearch;
import stevekung.mods.indicatia.gui.GuiRightClickTextField;
import stevekung.mods.indicatia.gui.GuiSBProfileButton;
import stevekung.mods.indicatia.utils.*;

public class GuiSkyBlockAPIViewer extends GuiScreen implements GuiYesNoCallback, ITabComplete
{
    public static final String[] downloadingStates = new String[] {"", ".", "..", "..."};
    private GuiRightClickTextField usernameTextField;
    private GuiButtonSearch checkButton;
    private GuiButton closeButton;
    private String username = "";
    private String displayName = "";
    private boolean openFromPlayer;
    private boolean loadingApi;
    private boolean error;
    private boolean showWeb;
    private String errorMessage;
    private String statusMessage;
    private List<ProfileDataCallback> profiles = new ArrayList<>();
    private final StopWatch watch = new StopWatch();
    private List<GuiSBProfileButton> profileButtonList = new ArrayList<>();
    private final String skyblockStats = "https://sky.lea.moe/";
    private boolean fromError;
    private boolean playerNamesFound;
    private boolean waitingOnAutocomplete;
    private int autocompleteIndex;
    private List<String> foundPlayerNames = new ArrayList<>();

    public GuiSkyBlockAPIViewer(GuiState state)
    {
        this(state, "", "");
    }

    public GuiSkyBlockAPIViewer(GuiState state, String username, String displayName)
    {
        this(state, username, displayName, null);
    }

    public GuiSkyBlockAPIViewer(GuiState state, String username, String displayName, List<ProfileDataCallback> profiles)
    {
        if (state == GuiState.SEARCH)
        {
            this.profiles = profiles;
        }
        this.loadingApi = state == GuiState.PLAYER;
        this.openFromPlayer = state == GuiState.PLAYER;
        this.fromError = state == GuiState.ERROR;
        this.displayName = displayName;
        this.username = username;
    }

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(this.checkButton = new GuiButtonSearch(0, this.width / 2 + 78, 46));
        this.buttonList.add(this.closeButton = new GuiButton(1, this.width / 2 - 75, this.height / 4 + 152, 150, 20, LangUtils.translate("gui.close")));
        this.usernameTextField = new GuiRightClickTextField(2, this.fontRendererObj, this.width / 2 - 75, 45, 150, 20);
        this.usernameTextField.setMaxStringLength(32767);
        this.usernameTextField.setFocused(true);
        this.usernameTextField.setText(this.username);
        this.checkButton.enabled = this.usernameTextField.getText().trim().length() > 0;
        this.checkButton.visible = !this.error;

        if (this.error)
        {
            this.closeButton.displayString = LangUtils.translate("gui.back");
        }
        if (this.fromError)
        {
            this.usernameTextField.setText(this.username);
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
                    this.setErrorMessage(e.getStackTrace()[0].toString());
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
    }

    @Override
    public void updateScreen()
    {
        this.usernameTextField.updateCursorCounter();
        this.checkButton.enabled = this.usernameTextField.getText().trim().length() > 0;
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
                this.username = this.usernameTextField.getText();
                this.profiles.clear();
                this.profileButtonList.clear();
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
                        this.setErrorMessage(e.getStackTrace()[0].toString());
                        e.printStackTrace();
                    }
                });
            }
            else if (button.id == 1)
            {
                this.mc.displayGuiScreen(this.error ? new GuiSkyBlockAPIViewer(GuiState.ERROR, this.username, this.displayName) : null);
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
        }
        else
        {
            this.actionPerformed(this.checkButton);
            this.usernameTextField.setFocused(false);
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

                if (this.showWeb)
                {
                    String url = "Click here to open SkyBlock Stats: " + this.skyblockStats;
                    int minX = this.width / 2 - this.fontRendererObj.getStringWidth(url) / 2 - 2;
                    int minY = 119;
                    int maxX = minX + this.fontRendererObj.getStringWidth(url) + 2;
                    int maxY = minY + this.fontRendererObj.FONT_HEIGHT + 1;

                    if (mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY)
                    {
                        this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, this.skyblockStats, 500, false));
                    }
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

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
                this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.RED + this.errorMessage, this.width / 2, 100, 16777215);

                if (this.showWeb)
                {
                    String url = "Click here to open SkyBlock Stats: " + this.skyblockStats;
                    boolean hover = false;
                    int minX = this.width / 2 - this.fontRendererObj.getStringWidth(url) / 2 - 2;
                    int minY = 119;
                    int maxX = minX + this.fontRendererObj.getStringWidth(url) + 2;
                    int maxY = minY + this.fontRendererObj.FONT_HEIGHT + 1;

                    if (mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY)
                    {
                        hover = true;
                    }
                    Gui.drawRect(minX, minY, maxX, maxY, ColorUtils.to32BitColor(hover ? 128 : 60, 255, 255, 255));
                    this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.YELLOW + url, this.width / 2, 120, 16777215);
                }
                super.drawScreen(mouseX, mouseY, partialTicks);
            }
            else
            {
                if (!this.profiles.isEmpty())
                {
                    this.drawCenteredString(this.fontRendererObj, this.displayName + EnumChatFormatting.GOLD + " Profile(s)", this.width / 2, 30, 16777215);
                }

                this.usernameTextField.drawTextBox();

                if (StringUtils.isNullOrEmpty(this.usernameTextField.getText()))
                {
                    this.drawString(this.fontRendererObj, "Enter username", this.width / 2 - 71, 51, 10526880);
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
    public void confirmClicked(boolean result, int id)
    {
        if (id == 500)
        {
            if (result)
            {
                CommonUtils.openLink(this.skyblockStats);
            }
            this.mc.displayGuiScreen(this);
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
        if (!this.username.matches("\\w+"))
        {
            this.setErrorMessage("Invalid Username Pattern!");
            return;
        }

        this.statusMessage = "Getting Hypixel API";

        URL url = new URL(SkyBlockAPIUtils.PLAYER_NAME + this.username);
        JsonObject obj = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();

        if (!obj.get("success").getAsBoolean())
        {
            this.setErrorMessage(obj.get("cause").getAsString());
            return;
        }

        JsonElement jsonPlayer = obj.get("player");

        if (jsonPlayer.isJsonNull())
        {
            this.setErrorMessage("Player not found!");
            return;
        }

        //        System.out.print(jsonPlayer);
        //        System.out.println();

        JsonElement newPackageRank = jsonPlayer.getAsJsonObject().get("newPackageRank"); // base rank
        System.out.println("newPackageRank: " + newPackageRank);
        JsonElement rank = jsonPlayer.getAsJsonObject().get("rank"); // rank priority NORMAL/YOUTUBER
        System.out.println("rank: " + rank);
        JsonElement rankPlusColor = jsonPlayer.getAsJsonObject().get("rankPlusColor");
        System.out.println("rankPlusColor: " + rankPlusColor);

        JsonElement monthlyPackageRank = jsonPlayer.getAsJsonObject().get("monthlyPackageRank");
        System.out.println("monthlyPackageRank: " + monthlyPackageRank);
        JsonElement monthlyRankColor = jsonPlayer.getAsJsonObject().get("monthlyRankColor");
        System.out.println("monthlyRankColor: " + monthlyRankColor);

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
                        baseRankText = "MVP" + EnumChatFormatting.valueOf(rankPlusColor.getAsString()) + "++";
                        color = EnumChatFormatting.GOLD.toString();
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.username = jsonPlayer.getAsJsonObject().get("displayname").getAsString();

        if (newPackageRank != null)
        {
            this.displayName = color + "[" + baseRankText + rankPlus + color + "] " + this.username;
        }
        else
        {
            this.displayName = HypixelRank.Base.NONE.getColor() + this.username;
        }

        //ClientUtils.printClientMessage("****************** " + this.displayName + " ******************");
        JsonElement stats = jsonPlayer.getAsJsonObject().get("stats");

        if (stats == null)
        {
            this.setErrorMessage("Couldn't get stats from API, Please try again later!");
            return;
        }

        JsonElement jsonSkyBlock = stats.getAsJsonObject().get("SkyBlock");

        if (jsonSkyBlock == null)
        {
            this.setErrorMessage("Player has not played SkyBlock yet!");
            return;
        }

        JsonObject profiles = jsonSkyBlock.getAsJsonObject().get("profiles").getAsJsonObject();
        int i = 0;

        if (profiles.entrySet().isEmpty())
        {
            this.setErrorMessage("Empty profile data! Please check on website instead", true);
            return;
        }

        this.statusMessage = "Getting SkyBlock profiles";
        List<GuiSBProfileButton> buttons = new ArrayList<>();

        for (Map.Entry<String, JsonElement> entry : profiles.entrySet())
        {
            String sbProfileId = profiles.get(entry.getKey()).getAsJsonObject().get("profile_id").getAsString();
            String profileName = profiles.get(entry.getKey()).getAsJsonObject().get("cute_name").getAsString();
            String uuid = jsonPlayer.getAsJsonObject().get("uuid").getAsString();
            this.statusMessage = "Found " + EnumChatFormatting.GOLD + "\"" + profileName + "\"" + EnumChatFormatting.GRAY + " profile";
            GameProfile profile = TileEntitySkull.updateGameprofile(new GameProfile(UUID.fromString(uuid.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5")), this.username));
            ProfileDataCallback callback = new ProfileDataCallback(sbProfileId, profileName, this.username, this.displayName, uuid, profile, this.getLastSaveProfile(sbProfileId, uuid));
            GuiSBProfileButton button = new GuiSBProfileButton(i + 1000, this.width / 2 - 75, 75, 150, 20, callback);

            if (profiles.entrySet().size() == 1)
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
        this.usernameTextField.setText(this.username);
        this.loadingApi = false;
    }

    private long getLastSaveProfile(String currentProfileId, String uuid) throws IOException
    {
        long lastSave = -1;
        URL url = new URL(SkyBlockAPIUtils.SKYBLOCK_PROFILE + currentProfileId);
        JsonObject obj = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        JsonElement profile = obj.get("profile");

        if (profile == null)
        {
            return lastSave;
        }

        JsonObject profiles = profile.getAsJsonObject().get("members").getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : profiles.entrySet().stream().filter(entry -> entry.getKey().equals(uuid)).collect(Collectors.toList()))
        {
            JsonObject currentUserProfile = profiles.get(entry.getKey()).getAsJsonObject();
            JsonElement lastSaveJson = currentUserProfile.get("last_save");

            if (lastSaveJson != null)
            {
                lastSave = lastSaveJson.getAsLong();
            }
        }
        return lastSave;
    }

    private void setErrorMessage(String message)
    {
        this.setErrorMessage(message, false);
    }

    private void setErrorMessage(String message, boolean showWeb)
    {
        this.error = true;
        this.loadingApi = false;
        this.showWeb = showWeb;
        this.errorMessage = message;
        this.checkButton.visible = !this.error;
        this.closeButton.displayString = LangUtils.translate("gui.back");
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