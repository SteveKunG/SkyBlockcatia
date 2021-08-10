package com.stevekung.skyblockcatia.gui.screen;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.core.SkyBlockcatia;
import com.stevekung.skyblockcatia.gui.APIErrorInfo;
import com.stevekung.skyblockcatia.gui.ScrollingListScreen;
import com.stevekung.skyblockcatia.gui.widget.RightClickTextFieldWidget;
import com.stevekung.skyblockcatia.gui.widget.button.APISearchButton;
import com.stevekung.skyblockcatia.gui.widget.button.ItemButton;
import com.stevekung.skyblockcatia.gui.widget.button.SkyBlockProfileButton;
import com.stevekung.skyblockcatia.utils.PlayerNameSuggestionHelper;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils.APIUrl;
import com.stevekung.skyblockcatia.utils.skyblock.api.*;
import com.stevekung.stevekungslib.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class SkyBlockProfileSelectorScreen extends Screen
{
    public static final String[] downloadingStates = new String[] {"", ".", "..", "..."};
    private static boolean firstLoad;
    private static ItemStack selfItemCache;
    private RightClickTextFieldWidget usernameTextField;
    private APISearchButton checkButton;
    private Button closeButton;
    private ItemButton selfButton;
    private String input;
    private String displayName;
    private boolean openFromPlayer;
    private boolean loadingApi;
    private boolean error;
    private String errorMessage;
    private String statusMessage;
    private List<ProfileDataCallback> profiles = Lists.newArrayList();
    private final boolean fromError;
    private PlayerNameSuggestionHelper suggestionHelper;
    private String guild;
    private ScrollingListScreen errorInfo;
    private final List<Component> errorList = Lists.newArrayList();
    private static final Map<String, Component> USERNAME_CACHE = Maps.newHashMap();
    public static final Map<String, Pair<Long, HypixelProfiles>> INIT_PROFILE_CACHE = Maps.newConcurrentMap();
    public static final Map<String, Pair<Long, SkyblockProfiles>> PROFILE_CACHE = Maps.newConcurrentMap();

    public SkyBlockProfileSelectorScreen(Mode mode)
    {
        this(mode, "", "", "");
    }

    public SkyBlockProfileSelectorScreen(Mode mode, String username, String displayName, String guild)
    {
        this(mode, username, displayName, guild, null);
    }

    public SkyBlockProfileSelectorScreen(Mode mode, String username, String displayName, String guild, List<ProfileDataCallback> profiles)
    {
        super(TextComponentUtils.component("API Viewer"));

        if (mode == Mode.SEARCH)
        {
            this.profiles = profiles;
        }
        this.loadingApi = mode == Mode.PLAYER;
        this.openFromPlayer = mode == Mode.PLAYER;
        this.fromError = mode == Mode.ERROR;
        this.displayName = displayName;
        this.input = username;
        this.guild = guild;
    }

    @Override
    public void init()
    {
        if (selfItemCache == null)
        {
            selfItemCache = new ItemStack(Items.SKELETON_SKULL);
        }

        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.addRenderableWidget(this.checkButton = new APISearchButton(this.width / 2 + 78, 46, button ->
        {
            this.input = this.usernameTextField.getValue();
            this.profiles.clear();
            this.guild = "";
            this.loadingApi = true;

            CommonUtils.runAsync(() ->
            {
                try
                {
                    var start = Instant.now();
                    this.checkAPI();
                    var after = Instant.now();
                    var delta = Duration.between(start, after).toMillis();
                    SkyBlockcatia.LOGGER.info("Profile Selector took {} ms", delta);
                }
                catch (Throwable e)
                {
                    this.errorList.add(TextComponentUtils.formatted(e.getClass().getName() + ": " + e.getMessage(), ChatFormatting.RED, ChatFormatting.UNDERLINE, ChatFormatting.BOLD));

                    for (var stack : e.getStackTrace())
                    {
                        this.errorList.add(TextComponentUtils.formatted("at " + stack.toString(), ChatFormatting.RED));
                    }
                    this.setErrorMessage("", true);
                    e.printStackTrace();
                }
            });
        }));
        this.addRenderableWidget(this.closeButton = new Button(this.width / 2 - 75, this.height / 4 + 152, 150, 20, LangUtils.translate("gui.close"), button -> this.minecraft.setScreen(this.error ? new SkyBlockProfileSelectorScreen(Mode.ERROR, this.input, this.displayName, this.guild) : null)));
        this.addRenderableWidget(this.selfButton = new ItemButton(this.width / 2 - 96, 46, selfItemCache, TextComponentUtils.component("Check Self"), button -> this.minecraft.setScreen(new SkyBlockProfileSelectorScreen(Mode.PLAYER, GameProfileUtils.getUsername(), this.displayName, ""))));
        this.usernameTextField = new RightClickTextFieldWidget(this.width / 2 - 75, 45, 150, 20);
        this.usernameTextField.setMaxLength(32767);
        this.usernameTextField.setFocus(true);
        this.usernameTextField.setValue(this.input);
        this.usernameTextField.setResponder(text -> this.setCommandResponder());
        this.checkButton.active = this.usernameTextField.getValue().trim().length() > 0;
        this.checkButton.visible = !this.error;
        this.addWidget(this.usernameTextField);
        this.suggestionHelper = new PlayerNameSuggestionHelper(this.minecraft, this, this.usernameTextField, this.font, 10);
        this.suggestionHelper.setAllowSuggestions(true);
        this.suggestionHelper.updateCommandInfo();

        if (this.error)
        {
            this.closeButton.setMessage(CommonComponents.GUI_BACK);
        }
        if (this.fromError)
        {
            this.usernameTextField.setValue(this.input);
        }

        if (this.openFromPlayer)
        {
            this.openFromPlayer = false;

            CommonUtils.runAsync(() ->
            {
                try
                {
                    var start = Instant.now();
                    this.checkAPI();
                    var after = Instant.now();
                    var delta = Duration.between(start, after).toMillis();
                    SkyBlockcatia.LOGGER.info("Profile Selector took {} ms", delta);
                }
                catch (Throwable e)
                {
                    this.errorList.add(TextComponentUtils.formatted(e.getClass().getName() + ": " + e.getMessage(), ChatFormatting.RED, ChatFormatting.UNDERLINE, ChatFormatting.BOLD));

                    for (var stack : e.getStackTrace())
                    {
                        this.errorList.add(TextComponentUtils.formatted("at " + stack.toString(), ChatFormatting.RED));
                    }
                    this.setErrorMessage("", true);
                    e.printStackTrace();
                }
            });
        }
        if (!this.profiles.isEmpty())
        {
            var buttons = Lists.<SkyBlockProfileButton>newArrayList();

            for (var data : this.profiles)
            {
                buttons.add(new SkyBlockProfileButton(this.width / 2 - 75, 75, 150, 20, data));
            }

            buttons.sort((button1, button2) -> new CompareToBuilder().append(button2.getLastSave(), button1.getLastSave()).build());

            var i2 = 0;

            for (var button : buttons)
            {
                if (i2 == 0)
                {
                    button.setMessage(button.getMessage().copy().withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
                }
                button.y += i2 * 22;
                button.setProfileList(this.profiles);
                this.addRenderableWidget(button);
                ++i2;
            }
        }
        if (this.errorInfo != null)
        {
            this.errorInfo = new APIErrorInfo(this, this.width - 39, this.height, 40, this.height / 4 + 128, 19, 12, this.errorList);
        }
    }

    @Override
    public GuiEventListener getFocused()
    {
        return this.usernameTextField;
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        var text = this.usernameTextField.getValue();
        this.init(mc, width, height);
        this.usernameTextField.setValue(text);
        this.suggestionHelper.updateCommandInfo();
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

        if (this.suggestionHelper.mouseScrolled(delta))
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
            if ((key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) && !StringUtil.isNullOrEmpty(this.usernameTextField.getValue()))
            {
                this.checkButton.onPress();
                return true;
            }
            else if (key == GLFW.GLFW_KEY_F5 && !this.profiles.isEmpty())
            {
                this.minecraft.setScreen(new SkyBlockProfileSelectorScreen(Mode.PLAYER, this.input, this.displayName, this.guild));
            }
            else if (this.suggestionHelper.keyPressed(key, scanCode, modifiers))
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
        this.checkButton.active = this.usernameTextField.getValue().trim().length() > 0;

        if (!firstLoad && !selfItemCache.isEmpty() && selfItemCache.getItem() == Items.SKELETON_SKULL)
        {
            CommonUtils.runAsync(this::setItemCache);
            firstLoad = true;
        }
    }

    @Override
    public void removed()
    {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (!this.loadingApi)
        {
            if (this.suggestionHelper.mouseClicked((int) mouseX, (int) mouseY, mouseButton))
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(poseStack);
        this.selfButton.visible = !this.loadingApi && !this.error;

        if (this.loadingApi)
        {
            var text = "Downloading SkyBlock stats";
            var i = this.font.width(text);
            GuiComponent.drawCenteredString(poseStack, this.font, text, this.width / 2, this.height / 2 + this.font.lineHeight * 2 - 35, 16777215);
            GuiComponent.drawString(poseStack, this.font, downloadingStates[(int) (Util.getMillis() / 500L % downloadingStates.length)], this.width / 2 + i / 2, this.height / 2 + this.font.lineHeight * 2 - 35, 16777215);
            GuiComponent.drawCenteredString(poseStack, this.font, "Status: " + ChatFormatting.GRAY + this.statusMessage, this.width / 2, this.height / 2 + this.font.lineHeight * 2 - 15, 16777215);
        }
        else
        {
            GuiComponent.drawCenteredString(poseStack, this.font, "SkyBlock API Viewer", this.width / 2, 20, 16777215);

            if (this.error)
            {
                if (this.errorInfo != null)
                {
                    this.errorInfo.render(poseStack, mouseX, mouseY, partialTicks);
                }
                else
                {
                    GuiComponent.drawCenteredString(poseStack, this.font, ChatFormatting.RED + this.errorMessage, this.width / 2, 100, 16777215);
                }
                super.render(poseStack, mouseX, mouseY, partialTicks);
            }
            else
            {
                if (!this.profiles.isEmpty())
                {
                    GuiComponent.drawCenteredString(poseStack, this.font, this.displayName + ChatFormatting.GOLD + " Profiles" + this.guild, this.width / 2, 30, 16777215);
                }

                this.suggestionHelper.render(poseStack, mouseX, mouseY);
                this.usernameTextField.render(poseStack, mouseX, mouseY, partialTicks);

                if (this.suggestionHelper.suggestions == null && StringUtil.isNullOrEmpty(this.usernameTextField.getValue()) && !this.usernameTextField.isFocused())
                {
                    GuiComponent.drawString(poseStack, this.font, "Enter Username or UUID", this.width / 2 - 71, 51, 10526880);
                }

                super.render(poseStack, mouseX, mouseY, partialTicks);
                var displayStrings = Lists.<Component>newArrayList();

                for (var button : this.renderables.stream().filter(SkyBlockProfileButton.class::isInstance).map(SkyBlockProfileButton.class::cast).collect(Collectors.toList()))
                {
                    var hover = this.suggestionHelper.suggestions == null && mouseX >= button.x && mouseY >= button.y && mouseX < button.x + button.getWidth() && mouseY < button.y + button.getHeight();
                    button.visible = button.active = this.suggestionHelper.suggestions == null;

                    if (hover)
                    {
                        if (button.getIslandMembers().size() > 0)
                        {
                            displayStrings.add(TextComponentUtils.formatted("Members:", ChatFormatting.YELLOW));
                            displayStrings.addAll(button.getIslandMembers());
                            displayStrings.add(TextComponentUtils.component(""));
                        }

                        displayStrings.addAll(Lists.newArrayList(TextComponentUtils.component(button.getLastActive()), button.getGameMode()));
                        this.renderComponentTooltip(poseStack, displayStrings, mouseX, mouseY);
                        break;
                    }
                }
            }
        }
    }

    private void checkAPI() throws IOException
    {
        this.renderables.removeIf(SkyBlockProfileButton.class::isInstance);
        URL url;

        if (this.input.length() == 32)
        {
            url = new URL(APIUrl.PLAYER_UUID.getUrl() + this.input);
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
                url = new URL(APIUrl.PLAYER_NAME.getUrl() + this.input);
            }
        }

        this.statusMessage = "Getting Hypixel API";

        HypixelProfiles profiles;
        var lowerInput = this.input.toLowerCase(Locale.ROOT);

        if (INIT_PROFILE_CACHE.containsKey(lowerInput))
        {
            profiles = INIT_PROFILE_CACHE.get(lowerInput).getRight();
        }
        else
        {
            profiles = SkyBlockcatia.GSON.fromJson(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8), HypixelProfiles.class);
            INIT_PROFILE_CACHE.put(lowerInput, Pair.of(System.currentTimeMillis(), profiles));
        }

        if (!profiles.success())
        {
            this.setErrorMessage(profiles.cause(), false);
            return;
        }

        var player = profiles.player();

        if (player == null)
        {
            this.setErrorMessage("Player not found!", false);
            return;
        }

        var newPackageRank = player.getNewPackageRank(); // base rank
        var rank = player.getRank(); // rank priority NORMAL/YOUTUBER
        var rankPlusColor = player.getRankPlusColor();

        var monthlyPackageRank = player.getMonthlyPackageRank();
        var monthlyRankColor = player.getMonthlyRankColor();
        var prefix = player.getPrefix();

        var baseRankText = "";
        var rankPlus = "";
        var color = "";

        try
        {
            if (newPackageRank != null)
            {
                if (rank != null)
                {
                    var rankType = HypixelRank.Type.valueOf(rank);

                    if (rankType == HypixelRank.Type.NORMAL)
                    {
                        var baseRank = HypixelRank.Base.valueOf(newPackageRank);
                        baseRankText = baseRank.getName();
                        color = baseRank.getColor().toString();
                        rankPlus = ChatFormatting.valueOf(rankPlusColor) + "+";
                    }
                    else
                    {
                        baseRankText = rankType == HypixelRank.Type.YOUTUBER ? ChatFormatting.WHITE + rankType.getName() : rankType.getName();
                        color = rankType.getColor().toString();
                    }
                }
                else
                {
                    var baseRank = HypixelRank.Base.valueOf(newPackageRank);

                    if (monthlyPackageRank != null && !monthlyPackageRank.equals("NONE"))
                    {
                        if (rankPlusColor != null)
                        {
                            baseRankText = "MVP" + ChatFormatting.valueOf(rankPlusColor) + "++";
                        }
                        else
                        {
                            baseRankText = "MVP" + ChatFormatting.RED + "++";
                        }

                        if (monthlyRankColor != null)
                        {
                            color = ChatFormatting.valueOf(monthlyRankColor).toString();
                        }
                        else
                        {
                            color = ChatFormatting.GOLD.toString();
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
                                rankPlus = ChatFormatting.valueOf(rankPlusColor) + "+";
                            }
                            else
                            {
                                rankPlus = ChatFormatting.RED + "+";
                            }
                        }
                    }
                }
            }
            else
            {
                if (rank != null)
                {
                    var rankType = HypixelRank.Type.valueOf(rank);
                    baseRankText = rankType == HypixelRank.Type.YOUTUBER ? ChatFormatting.WHITE + rankType.getName() : rankType.getName();
                    color = rankType.getColor().toString();
                }
                if (monthlyPackageRank != null && !monthlyPackageRank.equals("NONE"))
                {
                    baseRankText = "MVP" + ChatFormatting.valueOf(rankPlusColor) + "++";

                    if (monthlyRankColor != null)
                    {
                        color = ChatFormatting.valueOf(monthlyRankColor).toString();
                    }
                    else
                    {
                        color = ChatFormatting.GOLD.toString();
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

        var uuid = player.getUUID();
        var urlGuild = new URL(APIUrl.GUILD.getUrl() + uuid);
        var guild = SkyBlockcatia.GSON.fromJson(IOUtils.toString(urlGuild.openConnection().getInputStream(), StandardCharsets.UTF_8), HypixelGuild.class).guild();

        if (guild != null)
        {
            var guildName = guild.name();
            this.guild = ChatFormatting.YELLOW + " Guild: " + ChatFormatting.GOLD + guildName;
        }

        var urlSB = new URL(APIUrl.SKYBLOCK_PROFILES.getUrl() + uuid);
        SkyblockProfiles sbProfiles;

        if (PROFILE_CACHE.containsKey(uuid))
        {
            sbProfiles = PROFILE_CACHE.get(uuid).getRight();
        }
        else
        {
            sbProfiles = SkyBlockcatia.GSON.fromJson(IOUtils.toString(urlSB.openConnection().getInputStream(), StandardCharsets.UTF_8), SkyblockProfiles.class);
            PROFILE_CACHE.put(uuid, Pair.of(System.currentTimeMillis(), sbProfiles));
        }

        var sbProfile = sbProfiles.profiles();
        SkullBlockEntity.updateGameprofile(new GameProfile(UUID.fromString(uuid.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5")), this.input), gameProfile ->
        {
            if (sbProfile == null || sbProfile.length <= 0)
            {
                this.statusMessage = "Found default profile";
                ProfileDataCallback callback = new ProfileDataCallback(uuid, TextComponentUtils.component("Avocado"), this.input, this.displayName, TextComponentUtils.formatted("Normal", ChatFormatting.GOLD), this.guild, uuid, gameProfile, -1);
                this.minecraft.setScreen(new SkyBlockAPIViewerScreen(this.profiles, callback));
                return;
            }

            var buttons = Lists.<SkyBlockProfileButton>newArrayList();

            for (var profile : sbProfile)
            {
                var hasOneProfile = sbProfile.length == 1;
                var lastSave = -1L;
                SkyblockProfiles.Profile availableProfile;
                var gameModeType = profile.gameMode();
                var gameMode = TextComponentUtils.formatted("Normal", ChatFormatting.GOLD);

                if (gameModeType != null)
                {
                    gameMode = gameModeType.equals("ironman") ? TextComponentUtils.formatted("♲ Iron Man", ChatFormatting.GRAY) : TextComponentUtils.formatted(gameModeType, ChatFormatting.RED);
                }

                var islandMembers = Lists.<Component>newLinkedList();
                var membersEntry = profile.members().entrySet();
                var memberSize = 1;

                for (var entry : membersEntry.stream().filter(en -> en.getKey().equals(uuid)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).entrySet())
                {
                    lastSave = entry.getValue().getLastSave();
                }

                for (var entry : membersEntry.stream().filter(en -> !en.getKey().equals(uuid)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).entrySet())
                {
                    var memberUuid = entry.getKey();
                    memberSize++;

                    if (!hasOneProfile)
                    {
                        if (!USERNAME_CACHE.containsKey(memberUuid))
                        {
                            USERNAME_CACHE.put(memberUuid, TextComponentUtils.component(this.getName(memberUuid)));
                        }

                        islandMembers.add(USERNAME_CACHE.get(memberUuid));
                        var allMembers = membersEntry.size() - memberSize;

                        if (memberSize > 5 && allMembers > 0)
                        {
                            islandMembers.add(TextComponentUtils.formatted("and " + allMembers + " more...", ChatFormatting.ITALIC));
                            break;
                        }
                    }
                }

                availableProfile = profile;
                var callback = new ProfileDataCallback(availableProfile, this.input, this.displayName, gameMode, this.guild, uuid, gameProfile, hasOneProfile ? -1 : lastSave, islandMembers);
                var button = new SkyBlockProfileButton(this.width / 2 - 75, 75, 150, 20, callback);

                if (hasOneProfile)
                {
                    this.minecraft.setScreen(new SkyBlockAPIViewerScreen(this.profiles, callback));
                    break;
                }

                buttons.add(button);
                this.profiles.add(callback);
            }

            buttons.sort((button1, button2) -> new CompareToBuilder().append(button2.getLastSave(), button1.getLastSave()).build());

            var i2 = 0;

            for (var button : buttons)
            {
                if (i2 == 0)
                {
                    button.setMessage(button.getMessage().copy().withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
                }
                button.y += i2 * 22;
                button.setProfileList(this.profiles);
                this.addRenderableWidget(button);
                ++i2;
            }
        });
        this.usernameTextField.setValue(this.input);
        this.loadingApi = false;
    }

    private void setErrorMessage(String message, boolean errorList)
    {
        this.error = true;
        this.loadingApi = false;
        this.checkButton.visible = !this.error;
        this.checkButton.active = !this.error;
        this.selfButton.visible = !this.error;
        this.usernameTextField.active = !this.error;
        this.closeButton.setMessage(CommonComponents.GUI_BACK);

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
        this.suggestionHelper.setAllowSuggestions(true);
        this.suggestionHelper.updateCommandInfo();
    }

    private void setItemCache()
    {
        selfItemCache = ItemUtils.getPlayerHead(GameProfileUtils.getUsername());
        this.selfButton.setItemStack(selfItemCache);
    }

    private String getName(String uuid)
    {
        try
        {
            var array = new JsonParser().parse(IOUtils.toString(new URL("https://api.mojang.com/user/profiles/" + uuid + "/names"), StandardCharsets.UTF_8)).getAsJsonArray();
            return array.get(array.size() - 1).getAsJsonObject().get("name").getAsString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return ChatFormatting.RED + uuid;
        }
    }

    public enum Mode
    {
        EMPTY,
        ERROR,
        PLAYER,
        SEARCH
    }
}