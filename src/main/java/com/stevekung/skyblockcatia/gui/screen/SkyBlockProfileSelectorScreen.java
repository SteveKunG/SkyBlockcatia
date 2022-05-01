package com.stevekung.skyblockcatia.gui.screen;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.gui.widget.GuiErrorInfoScrollingList;
import com.stevekung.skyblockcatia.gui.widget.GuiRightClickTextField;
import com.stevekung.skyblockcatia.gui.widget.button.GuiButtonItem;
import com.stevekung.skyblockcatia.gui.widget.button.GuiButtonSearch;
import com.stevekung.skyblockcatia.gui.widget.button.GuiSkyBlockProfileButton;
import com.stevekung.skyblockcatia.utils.*;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.*;

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

public class SkyBlockProfileSelectorScreen extends GuiScreen implements ITabComplete
{
    public static final String[] downloadingStates = new String[] { "", ".", "..", "..." };
    private static boolean firstLoad;
    private static ItemStack selfItemCache;
    private static final Gson GSON = new Gson();
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
    private final List<GuiSkyBlockProfileButton> profileButtonList = new ArrayList<>();
    private final boolean fromError;
    private boolean playerNamesFound;
    private boolean waitingOnAutocomplete;
    private int autocompleteIndex;
    private final List<String> foundPlayerNames = new ArrayList<>();
    private String guild = "";
    private GuiScrollingList errorInfo;
    private final List<String> errorList = new ArrayList<>();
    private static final Map<String, String> USERNAME_CACHE = Maps.newHashMap();
    public static final Map<String, Pair<Long, HypixelProfiles>> INIT_PROFILE_CACHE = Maps.newConcurrentMap();
    public static final Map<String, Pair<Long, SkyblockProfiles>> PROFILE_CACHE = Maps.newConcurrentMap();

    public SkyBlockProfileSelectorScreen(GuiState state)
    {
        this(state, "", "", "");
    }

    public SkyBlockProfileSelectorScreen(GuiState state, String username, String displayName, String guild)
    {
        this(state, username, displayName, guild, null);
    }

    public SkyBlockProfileSelectorScreen(GuiState state, String username, String displayName, String guild, List<ProfileDataCallback> profiles)
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
        if (selfItemCache == null)
        {
            selfItemCache = new ItemStack(Items.skull);
        }

        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(this.checkButton = new GuiButtonSearch(0, this.width / 2 + 78, 46));
        this.buttonList.add(this.closeButton = new GuiButton(1, this.width / 2 - 75, this.height / 4 + 152, 150, 20, LangUtils.translate("gui.close")));
        this.buttonList.add(this.selfButton = new GuiButtonItem(2, this.width / 2 - 96, 46, selfItemCache, "Check Self"));
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
                    Instant start = Instant.now();
                    this.checkAPI();
                    Instant after = Instant.now();
                    long delta = Duration.between(start, after).toMillis();
                    LoggerIN.info("Profile Selector took {} ms", delta);
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
            List<GuiSkyBlockProfileButton> buttons = new ArrayList<>();

            for (ProfileDataCallback data : this.profiles)
            {
                GuiSkyBlockProfileButton button = new GuiSkyBlockProfileButton(i + 1000, this.width / 2 - 75, 75, 150, 20, data);
                buttons.add(button);
                ++i;
            }

            buttons.sort((button1, button2) -> new CompareToBuilder().append(button2.getLastSave(), button1.getLastSave()).build());

            int i2 = 0;

            for (GuiSkyBlockProfileButton button : buttons)
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
            this.errorInfo = new GuiErrorInfoScrollingList(this, this.width - 39, this.height, 40, this.height / 4 + 128, 19, 12, this.width, this.height, this.errorList);
        }
    }

    @Override
    public void updateScreen()
    {
        this.usernameTextField.updateCursorCounter();
        this.checkButton.enabled = this.usernameTextField.getText().trim().length() > 0;

        if (!firstLoad && selfItemCache != null && selfItemCache.getItem() == Items.skull && selfItemCache.getItemDamage() == 0)
        {
            CommonUtils.runAsync(this::setItemCache);
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
            switch (button.id)
            {
                case 0:
                    this.input = this.usernameTextField.getText();
                    this.profiles.clear();
                    this.profileButtonList.clear();
                    this.guild = "";
                    this.loadingApi = true;
                    CommonUtils.runAsync(() ->
                    {
                        try
                        {
                            Instant start = Instant.now();
                            this.checkAPI();
                            Instant after = Instant.now();
                            long delta = Duration.between(start, after).toMillis();
                            LoggerIN.info("Profile Selector took {} ms", delta);
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
                    break;
                case 1:
                    this.mc.displayGuiScreen(this.error ? new SkyBlockProfileSelectorScreen(GuiState.ERROR, this.input, this.displayName, this.guild) : null);
                    break;
                case 2:
                    this.mc.displayGuiScreen(new SkyBlockProfileSelectorScreen(GuiState.PLAYER, GameProfileUtils.getUsername(), this.displayName, ""));
                    break;
                default:
                    break;
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
                this.mc.displayGuiScreen(new SkyBlockProfileSelectorScreen(GuiState.PLAYER, this.input, this.displayName, this.guild));
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
                for (GuiSkyBlockProfileButton button : this.profileButtonList)
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

                for (GuiSkyBlockProfileButton button : this.profileButtonList)
                {
                    button.drawButton(this.mc, mouseX, mouseY);
                }

                super.drawScreen(mouseX, mouseY, partialTicks);
                List<String> displayStrings = Lists.newArrayList();

                for (GuiSkyBlockProfileButton button : this.profileButtonList)
                {
                    boolean isHover = mouseX >= button.xPosition && mouseY >= button.yPosition && mouseX < button.xPosition + button.width && mouseY < button.yPosition + button.height;

                    if (isHover)
                    {
                        if (button.getIslandMembers().size() > 0)
                        {
                            displayStrings.add(EnumChatFormatting.YELLOW + "Members:");
                            displayStrings.addAll(button.getIslandMembers());
                            displayStrings.add("");
                        }

                        displayStrings.addAll(Lists.newArrayList(button.getLastActive(), button.getGameMode()));
                        GuiUtils.drawHoveringText(displayStrings, mouseX, mouseY, this.mc.displayWidth, this.mc.displayHeight, -1, this.fontRendererObj);
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

    private void setItemCache()
    {
        selfItemCache = RenderUtils.getPlayerHead(GameProfileUtils.getUsername());
        this.selfButton.setItemStack(selfItemCache);
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

        HypixelProfiles profiles = null;
        String lowerInput = this.input.toLowerCase(Locale.ROOT);

        if (INIT_PROFILE_CACHE.containsKey(lowerInput))
        {
            profiles = INIT_PROFILE_CACHE.get(lowerInput).getRight();
        }
        else
        {
            profiles = GSON.fromJson(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8), HypixelProfiles.class);
            INIT_PROFILE_CACHE.put(lowerInput, Pair.of(System.currentTimeMillis(), profiles));
        }

        if (!profiles.isSuccess())
        {
            this.setErrorMessage(profiles.getCause(), false);
            return;
        }

        HypixelProfiles.HypixelPlayerProfile player = profiles.getPlayer();

        if (player == null)
        {
            this.setErrorMessage("Player not found!", false);
            return;
        }

        String newPackageRank = player.getNewPackageRank(); // base rank
        String rank = player.getRank(); // rank priority NORMAL/YOUTUBER
        String rankPlusColor = player.getRankPlusColor();

        String monthlyPackageRank = player.getMonthlyPackageRank();
        String monthlyRankColor = player.getMonthlyRankColor();
        String prefix = player.getPrefix();

        String baseRankText = "";
        String rankPlus = "";
        String color = "";

        try
        {
            if (newPackageRank != null)
            {
                if (rank != null)
                {
                    HypixelRank.Type rankType = HypixelRank.Type.valueOf(rank);

                    if (rankType == HypixelRank.Type.NORMAL)
                    {
                        HypixelRank.Base baseRank = HypixelRank.Base.valueOf(newPackageRank);
                        baseRankText = baseRank.getName();
                        color = baseRank.getColor().toString();
                        rankPlus = EnumChatFormatting.valueOf(rankPlusColor) + "+";
                    }
                    else
                    {
                        baseRankText = rankType == HypixelRank.Type.YOUTUBER ? EnumChatFormatting.WHITE + rankType.getName() : rankType.getName();
                        color = rankType.getColor().toString();
                    }
                }
                else
                {
                    HypixelRank.Base baseRank = HypixelRank.Base.valueOf(newPackageRank);

                    if (monthlyPackageRank != null && !monthlyPackageRank.equals("NONE"))
                    {
                        if (rankPlusColor != null)
                        {
                            baseRankText = "MVP" + EnumChatFormatting.valueOf(rankPlusColor) + "++";
                        }
                        else
                        {
                            baseRankText = "MVP" + EnumChatFormatting.RED + "++";
                        }

                        if (monthlyRankColor != null)
                        {
                            color = EnumChatFormatting.valueOf(monthlyRankColor).toString();
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
                                rankPlus = EnumChatFormatting.valueOf(rankPlusColor) + "+";
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
                    HypixelRank.Type rankType = HypixelRank.Type.valueOf(rank);
                    baseRankText = rankType == HypixelRank.Type.YOUTUBER ? EnumChatFormatting.WHITE + rankType.getName() : rankType.getName();
                    color = rankType.getColor().toString();
                }
                if (monthlyPackageRank != null && !monthlyPackageRank.equals("NONE"))
                {
                    baseRankText = "MVP" + EnumChatFormatting.valueOf(rankPlusColor) + "++";

                    if (monthlyRankColor != null)
                    {
                        color = EnumChatFormatting.valueOf(monthlyRankColor).toString();
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

        this.input = player.getDisplayName();

        if (prefix != null)
        {
            this.displayName = prefix + " " + this.input;
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

        String uuid = player.getUUID();
        URL urlGuild = new URL(SBAPIUtils.GUILD + uuid);
        HypixelGuild.Guild guild = GSON.fromJson(IOUtils.toString(urlGuild.openConnection().getInputStream(), StandardCharsets.UTF_8), HypixelGuild.class).getGuild();

        if (guild != null)
        {
            String guildName = guild.getName();
            this.guild = EnumChatFormatting.YELLOW + " Guild: " + EnumChatFormatting.GOLD + guildName;
        }

        URL urlSB = new URL(SBAPIUtils.SKYBLOCK_PROFILES + uuid);
        SkyblockProfiles sbProfiles = null;

        if (PROFILE_CACHE.containsKey(uuid))
        {
            sbProfiles = PROFILE_CACHE.get(uuid).getRight();
        }
        else
        {
            sbProfiles = GSON.fromJson(IOUtils.toString(urlSB.openConnection().getInputStream(), StandardCharsets.UTF_8), SkyblockProfiles.class);
            PROFILE_CACHE.put(uuid, Pair.of(System.currentTimeMillis(), sbProfiles));
        }

        SkyblockProfiles.Profile[] sbProfile = sbProfiles.getProfiles();
        GameProfile gameProfile = TileEntitySkull.updateGameprofile(new GameProfile(UUID.fromString(uuid.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5")), this.input));

        if (sbProfile == null || sbProfile.length <= 0)
        {
            this.statusMessage = "Found default profile";
            ProfileDataCallback callback = new ProfileDataCallback(uuid, "Avocado", this.input, this.displayName, EnumChatFormatting.GOLD + "Normal", this.guild, uuid, gameProfile, -1);
            this.mc.displayGuiScreen(new SkyBlockAPIViewerScreen(this.profiles, callback));
            return;
        }

        int i = 0;
        List<GuiSkyBlockProfileButton> buttons = new ArrayList<>();

        for (SkyblockProfiles.Profile profile : sbProfile)
        {
            boolean hasOneProfile = sbProfile.length == 1;
            long lastSave = -1;
            SkyblockProfiles.Profile availableProfile;
            String gameModeType = profile.getGameMode();
            String gameMode = EnumChatFormatting.GOLD + "Normal";

            if (gameModeType != null)
            {
                gameMode = gameModeType.equals("ironman") ? EnumChatFormatting.GRAY + "♲ Iron Man" : EnumChatFormatting.RED + gameModeType;
            }

            List<String> islandMembers = Lists.newLinkedList();
            Set<Entry<String, SkyblockProfiles.Members>> membersEntry = profile.getMembers().entrySet();
            int memberSize = 1;

            for (Map.Entry<String, SkyblockProfiles.Members> entry : membersEntry.stream().filter(en -> en.getKey().equals(uuid)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).entrySet())
            {
                Long lastSaveEle = entry.getValue().getLastSave();
                lastSave = lastSaveEle == null ? -1 : lastSaveEle;
            }

            for (Map.Entry<String, SkyblockProfiles.Members> entry : membersEntry.stream().filter(en -> !en.getKey().equals(uuid)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).entrySet())
            {
                String memberUuid = entry.getKey();
                memberSize++;

                if (!hasOneProfile)
                {
                    if (!USERNAME_CACHE.containsKey(memberUuid))
                    {
                        USERNAME_CACHE.put(memberUuid, this.getName(memberUuid));
                    }

                    islandMembers.add(USERNAME_CACHE.get(memberUuid));
                    int allMembers = membersEntry.size() - memberSize;

                    if (memberSize > 5 && allMembers > 0)
                    {
                        islandMembers.add(EnumChatFormatting.ITALIC + "and " + allMembers + " more...");
                        break;
                    }
                }
            }

            availableProfile = profile;
            ProfileDataCallback callback = new ProfileDataCallback(availableProfile, this.input, this.displayName, gameMode, this.guild, uuid, gameProfile, hasOneProfile ? -1 : lastSave, islandMembers);
            GuiSkyBlockProfileButton button = new GuiSkyBlockProfileButton(i + 1000, this.width / 2 - 75, 75, 150, 20, callback);

            if (hasOneProfile)
            {
                this.mc.displayGuiScreen(new SkyBlockAPIViewerScreen(this.profiles, callback));
                break;
            }

            buttons.add(button);
            this.profiles.add(callback);
            ++i;
        }

        buttons.sort((button1, button2) -> new CompareToBuilder().append(button2.getLastSave(), button1.getLastSave()).build());

        int i2 = 0;

        for (GuiSkyBlockProfileButton button : buttons)
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
            this.errorInfo = new GuiErrorInfoScrollingList(this, this.width - 39, this.height, 40, this.height / 4 + 128, 19, 12, this.width, this.height, this.errorList);
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
            String s = this.usernameTextField.getText().substring(i).toLowerCase(Locale.ROOT);
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

    private String getName(String uuid)
    {
        try
        {
            JsonArray array = new JsonParser().parse(IOUtils.toString(new URL("https://api.mojang.com/user/profiles/" + uuid + "/names"))).getAsJsonArray();
            return array.get(array.size() - 1).getAsJsonObject().get("name").getAsString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return EnumChatFormatting.RED + uuid;
        }
    }

    public enum GuiState
    {
        EMPTY, ERROR, PLAYER, SEARCH;
    }
}