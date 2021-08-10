package com.stevekung.skyblockcatia.gui.screen;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.glfw.GLFW;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import com.stevekung.skyblockcatia.core.SkyBlockcatia;
import com.stevekung.skyblockcatia.gui.APIErrorInfo;
import com.stevekung.skyblockcatia.gui.ScrollingListScreen;
import com.stevekung.skyblockcatia.gui.widget.button.ItemButton;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.IViewerLoader;
import com.stevekung.skyblockcatia.utils.TimeUtils;
import com.stevekung.skyblockcatia.utils.skyblock.*;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils.APIUrl;
import com.stevekung.skyblockcatia.utils.skyblock.api.*;
import com.stevekung.stevekungslib.proxy.LibClientProxy;
import com.stevekung.stevekungslib.utils.*;
import com.stevekung.stevekungslib.utils.client.ClientUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.util.datafix.fixes.EntityTheRenameningFix;
import net.minecraft.util.datafix.fixes.ItemStackTheFlatteningFix;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

@SuppressWarnings("deprecation")
public class SkyBlockAPIViewerScreen extends Screen
{
    private static final ResourceLocation INVENTORY_TABS = new ResourceLocation("skyblockcatia:textures/gui/groups.png");
    private static final ResourceLocation XP_BARS = new ResourceLocation("skyblockcatia:textures/gui/skill_xp_bar.png");
    private static final String[] REVENANT_HORROR_HEAD = new String[] {"0862e0b0-a14f-3f93-894f-013502936b59", "dbad99ed3c820b7978190ad08a934a68dfa90d9986825da1c97f6f21f49ad626"};

    // Based stuff
    private boolean firstLoad;
    private boolean loadingApi = true;
    private boolean error;
    private String errorMessage;
    private String statusMessage;
    private Button doneButton;
    private Button backButton;
    private ItemButton showArmorButton;
    private SkyblockProfiles.Profile skyblockProfiles;
    private final List<ProfileDataCallback> profiles;
    private final String sbProfileId;
    private final Component sbProfileName;
    private final String username;
    private final String displayName;
    private final Component gameMode;
    private final String guild;
    private final String uuid;
    private final GameProfile profile;
    private ScrollingListScreen currentSlot;
    private ViewButton viewButton = ViewButton.PLAYER;
    private OthersViewButton othersButton = OthersViewButton.KILLS;
    private BasicInfoViewButton basicInfoButton = BasicInfoViewButton.PLAYER_STATS;
    private boolean updated;
    private final ViewerData data = new ViewerData();
    private int skillCount;
    private ScrollingListScreen errorInfo;
    private final List<Component> errorList = Lists.newArrayList();
    private boolean showArmor = true;

    // API
    private static final Pattern STATS_PATTERN = Pattern.compile("(?<type>Strength|Crit Chance|Crit Damage|Health|Defense|Speed|Intelligence|True Defense|Sea Creature Chance|Magic Find|Pet Luck|Bonus Attack Speed|Ferocity|Ability Damage|Mining Speed|Mining Fortune|Farming Fortune|Foraging Fortune): (?<value>(?:\\+|\\-)[0-9,.]+)?(?:\\%)?(?:(?: HP(?: \\((?:\\+|\\-)[0-9,.]+ HP\\))?(?: \\(\\w+ (?:\\+|\\-)[0-9,.]+ HP\\))?)|(?: \\((?:\\+|\\-)[0-9,.]+\\))|(?: \\(\\w+ (?:\\+|\\-)[0-9,.]+(?:\\%)?\\)))?(?: \\((?:\\+|\\-)[0-9,.]+ HP\\))?");
    public static boolean renderSecondLayer;
    private final List<SkyBlockInfo> infoList = Lists.newArrayList();
    private final List<SBSkills.Info> skillLeftList = Lists.newArrayList();
    private final List<SBSkills.Info> skillRightList = Lists.newArrayList();
    private final List<SkyBlockSlayerInfo> slayerInfo = Lists.newArrayList();
    private final List<SBStats.Display> sbKills = Lists.newArrayList();
    private final List<SBStats.Display> sbDeaths = Lists.newArrayList();
    private final List<SBStats.Display> sbOthers = Lists.newArrayList();
    private final List<BankHistory.Stats> sbBankHistories = Lists.newArrayList();
    private final List<SBMinions.CraftedInfo> sbCraftedMinions = Lists.newArrayList();
    private final List<ItemStack> armorItems = Lists.newArrayList();
    private final List<ItemStack> inventoryToStats = Lists.newArrayList();
    private final List<SBCollections> collections = Lists.newArrayList();
    private List<SkyBlockInfo> jacobInfo = Lists.newArrayList();
    private final Multimap<String, Integer> craftedMinions = HashMultimap.create();
    private int additionalMinionSlot;
    private int craftedMinionCount;
    private int currentMinionSlot;
    private int slayerTotalAmountSpent;
    private int totalSlayerXp;
    private int totalDisabledInv;
    private SBFakePlayerEntity player;
    private String skillAvg;
    private int petScore;
    private int activeSlayerTier;
    private SBSlayers.Type activeSlayerType;
    private int farmingLevelCap;

    // Info & Inventory
    private static final int SIZE = 36;
    private static final SBInventoryGroup.ExtendedInventory TEMP_INVENTORY = new SBInventoryGroup.ExtendedInventory(SkyBlockAPIViewerScreen.SIZE);
    private static final SBInventoryGroup.ExtendedInventory TEMP_ARMOR_INVENTORY = new SBInventoryGroup.ExtendedInventory(4);
    public static final List<SBInventoryGroup.Data> SKYBLOCK_INV = Lists.newArrayList();
    private int selectedTabIndex = SBInventoryGroup.INVENTORY.getIndex();
    private float currentScroll;
    private boolean isScrolling;
    private final SkyBlockContainer skyBlockContainer;
    private ArmorContainer skyBlockArmorContainer;

    // Player Bonus Stats
    private int totalFairySouls;
    private int farmingLevel;
    private int foragingLevel;
    private int miningLevel;
    private int fishingLevel;
    private int combatLevel;
    private int enchantingLevel;
    private int alchemyLevel;
    private int tamingLevel;
    private int catacombsLevel;
    private int zombieSlayerLevel;
    private int spiderSlayerLevel;
    private int wolfSlayerLevel;
    private int endermanSlayerLevel;
    private final BonusStatTemplate allStat = BonusStatTemplate.getDefault();

    // ContainerScreen fields
    private final int xSize;
    private final int ySize;
    private int guiLeft;
    private int guiTop;
    private Slot hoveredSlot;

    public SkyBlockAPIViewerScreen(List<ProfileDataCallback> profiles, ProfileDataCallback callback)
    {
        super(TextComponentUtils.component("SkyBlock Player Data"));
        this.firstLoad = true;
        this.skyBlockContainer = new SkyBlockContainer();
        this.skyBlockArmorContainer = new ArmorContainer(true);
        this.profiles = profiles;
        this.skyblockProfiles = callback.getSkyblockProfile();
        this.sbProfileId = callback.getProfileId();
        this.sbProfileName = callback.getProfileName();
        this.username = callback.getUsername();
        this.displayName = callback.getDisplayName();
        this.gameMode = callback.getGameMode();
        this.guild = callback.getGuild();
        this.uuid = callback.getUUID();
        this.profile = callback.getGameProfile();

        this.xSize = 202;
        this.ySize = 125;
    }

    @Override
    public void init()
    {
        this.addRenderableWidget(this.doneButton = new Button(this.width / 2 - 154, this.height - 25, 150, 20, LangUtils.translate("gui.close"), button -> this.minecraft.setScreen(this.error ? new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.SEARCH, this.username, this.displayName, this.guild, this.profiles) : null)));
        this.addRenderableWidget(this.backButton = new Button(this.width / 2 + 4, this.height - 25, 150, 20, CommonComponents.GUI_BACK, button -> this.minecraft.setScreen(this.profiles.size() == 0 ? new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.EMPTY, this.username, this.displayName, "") : new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.SEARCH, this.username, this.displayName, this.guild, this.profiles))));
        this.addRenderableWidget(this.showArmorButton = new ItemButton(this.width / 2 - 115, this.height / 2 - 65, Items.DIAMOND_CHESTPLATE, TextComponentUtils.component("Show Armor: " + ChatFormatting.GREEN + "ON"), button -> this.setShowArmor()));
        var infoButton = ViewButton.PLAYER.button = new Button(this.width / 2 - 197, 6, 70, 20, LangUtils.translate("gui.sb_view_player"), button -> this.performedInfo(ViewButton.PLAYER));
        infoButton.active = false;
        this.addRenderableWidget(infoButton);
        this.addRenderableWidget(ViewButton.SKILLS.button = new Button(this.width / 2 - 116, 6, 70, 20, LangUtils.translate("gui.sb_view_skills"), button -> this.performedInfo(ViewButton.SKILLS)));
        this.addRenderableWidget(ViewButton.SLAYERS.button = new Button(this.width / 2 - 35, 6, 70, 20, LangUtils.translate("gui.sb_view_slayers"), button -> this.performedInfo(ViewButton.SLAYERS)));
        this.addRenderableWidget(ViewButton.DUNGEONS.button = new Button(this.width / 2 + 45, 6, 70, 20, LangUtils.translate("gui.sb_view_dungeons"), button -> this.performedInfo(ViewButton.DUNGEONS)));
        this.addRenderableWidget(ViewButton.OTHERS.button = new Button(this.width / 2 + 126, 6, 70, 20, LangUtils.translate("gui.sb_view_others"), button -> this.performedInfo(ViewButton.OTHERS)));

        var statKillsButton = OthersViewButton.KILLS.button = new Button(this.width / 2 - 170, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.kills"), button -> this.performedOthers(OthersViewButton.KILLS));
        statKillsButton.active = false;
        this.addRenderableWidget(statKillsButton);
        this.addRenderableWidget(OthersViewButton.DEATHS.button = new Button(this.width / 2 - 84, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.deaths"), button -> this.performedOthers(OthersViewButton.DEATHS)));
        this.addRenderableWidget(OthersViewButton.OTHER_STATS.button = new Button(this.width / 2 + 4, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.others_stats"), button -> this.performedOthers(OthersViewButton.OTHER_STATS)));
        this.addRenderableWidget(OthersViewButton.BANK_HISTORY.button = new Button(this.width / 2 + 90, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.bank_history"), button -> this.performedOthers(OthersViewButton.BANK_HISTORY)));

        var basicInfoButton = BasicInfoViewButton.PLAYER_STATS.button = new Button(this.width / 2 - 170, this.height - 48, 80, 20, LangUtils.translate("gui.sb_player_stats"), button -> this.performedBasicInfo(BasicInfoViewButton.PLAYER_STATS));
        basicInfoButton.active = false;
        this.addRenderableWidget(basicInfoButton);
        this.addRenderableWidget(BasicInfoViewButton.INVENTORY.button = new Button(this.width / 2 - 84, this.height - 48, 80, 20, LangUtils.translate("gui.sb_inventory"), button -> this.performedBasicInfo(BasicInfoViewButton.INVENTORY)));
        this.addRenderableWidget(BasicInfoViewButton.COLLECTIONS.button = new Button(this.width / 2 + 4, this.height - 48, 80, 20, LangUtils.translate("gui.sb_collections"), button -> this.performedBasicInfo(BasicInfoViewButton.COLLECTIONS)));
        this.addRenderableWidget(BasicInfoViewButton.CRAFTED_MINIONS.button = new Button(this.width / 2 + 90, this.height - 48, 80, 20, LangUtils.translate("gui.sb_crafted_minions"), button -> this.performedBasicInfo(BasicInfoViewButton.CRAFTED_MINIONS)));

        if (this.firstLoad)
        {
            this.firstLoad = false;

            CommonUtils.runAsync(() ->
            {
                try
                {
                    var start = Instant.now();
                    this.getPlayerData();
                    var after = Instant.now();
                    var delta = Duration.between(start, after).toMillis();
                    SkyBlockcatia.LOGGER.info("Parsing Skyblock Profile took {} ms", delta);
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

        if (!this.updated)
        {
            this.performedInfo(this.viewButton);

            if (this.errorInfo != null)
            {
                this.errorInfo = new APIErrorInfo(this, this.width - 39, this.height, 40, this.height / 4 + 128, 19, 12, this.errorList);
            }
            this.updated = true;
        }

        var i = this.selectedTabIndex;
        this.selectedTabIndex = -1;
        this.setCurrentGroup(SBInventoryGroup.GROUPS[i]);
        this.guiLeft = (this.width - this.xSize) / 2 + 50;
        this.guiTop = (this.height - this.ySize) / 2 + 10;

        if (this.error)
        {
            this.updateErrorButton();
        }
    }

    @Override
    public void tick()
    {
        if (this.player != null)
        {
            this.player.tick();
        }
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        this.updated = false;
        super.resize(mc, width, height);
    }

    @Override
    public void removed()
    {
        TEMP_INVENTORY.clearContent();
        TEMP_ARMOR_INVENTORY.clearContent();
        SKYBLOCK_INV.clear();
        this.minecraft.getConnection().getOnlinePlayers().removeIf(network -> ((IViewerLoader) network).isLoadedFromViewer());
        SkyBlockAPIViewerScreen.renderSecondLayer = false;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers)
    {
        if (key == KeyBindingHandler.KEY_SB_VIEW_RECIPE.getDefaultKey().getValue())
        {
            if (this.hoveredSlot != null && this.hoveredSlot.hasItem() && this.hoveredSlot.getItem().hasTag())
            {
                var extraAttrib = this.hoveredSlot.getItem().getTag().getCompound("ExtraAttributes");

                if (extraAttrib.contains("id"))
                {
                    var itemId = extraAttrib.getString("id");
                    ClientUtils.printClientMessage(TextComponentUtils.component("Click to view ").append(this.hoveredSlot.getItem().getHoverName().copy().withStyle(ChatFormatting.GOLD).append(TextComponentUtils.formatted(" recipe", ChatFormatting.GREEN))).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewrecipe " + itemId)).applyFormat(ChatFormatting.GREEN)));
                }
            }
        }
        else if (key == GLFW.GLFW_KEY_F5)
        {
            this.skyblockProfiles = null;
            this.minecraft.setScreen(new SkyBlockAPIViewerScreen(this.profiles, new ProfileDataCallback(this.sbProfileId, this.sbProfileName, this.username, this.displayName, this.gameMode, this.guild, this.uuid, this.profile, -1)));
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void onClose()
    {
        this.minecraft.setScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.SEARCH, this.username, this.displayName, this.guild, this.profiles));
    }

    @Override
    public GuiEventListener getFocused()
    {
        if (this.currentSlot != null)
        {
            return this.currentSlot;
        }
        if (this.errorInfo != null)
        {
            return this.errorInfo;
        }
        return super.getFocused();
    }

    @Override
    public Optional<GuiEventListener> getChildAt(double mouseX, double mouseY)
    {
        if (this.currentSlot != null && this.currentSlot.isMouseOver(mouseX, mouseY))
        {
            return Optional.of(this.currentSlot);
        }
        if (this.errorInfo != null && this.errorInfo.isMouseOver(mouseX, mouseY))
        {
            return Optional.of(this.errorInfo);
        }
        return super.getChildAt(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (this.loadingApi)
        {
            return false;
        }
        else
        {
            if (state == 0 && this.currentSlot != null && this.currentSlot instanceof EmptyList && ((EmptyList) this.currentSlot).type == EmptyList.Type.INVENTORY)
            {
                var i = mouseX - this.guiLeft;
                var j = mouseY - this.guiTop;

                for (var group : SBInventoryGroup.GROUPS)
                {
                    if (group != null && this.isMouseOverGroup(group, i, j) && !group.isDisabled())
                    {
                        return true;
                    }
                }

                if (this.isHoveredScroll(mouseX, mouseY))
                {
                    this.isScrolling = this.needsScrollBars();
                    return true;
                }
            }
            if (this.currentSlot != null && this.currentSlot.mouseClicked(mouseX, mouseY, state))
            {
                if (state == 0)
                {
                    this.setDragging(true);
                }
                return true;
            }
            if (this.errorInfo != null && this.errorInfo.mouseClicked(mouseX, mouseY, state))
            {
                if (state == 0)
                {
                    this.setDragging(true);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        if (this.loadingApi)
        {
            return false;
        }
        else
        {
            if (state == 0 && this.currentSlot != null && this.currentSlot instanceof EmptyList && ((EmptyList) this.currentSlot).type == EmptyList.Type.INVENTORY)
            {
                var i = mouseX - this.guiLeft;
                var j = mouseY - this.guiTop;
                this.isScrolling = false;

                for (var group : SBInventoryGroup.GROUPS)
                {
                    if (group != null && this.isMouseOverGroup(group, i, j) && !group.isDisabled())
                    {
                        this.setCurrentGroup(group);
                        return true;
                    }
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
    {
        if (this.loadingApi)
        {
            return false;
        }
        else
        {
            if (this.isScrolling && this.currentSlot != null && this.currentSlot instanceof EmptyList && ((EmptyList) this.currentSlot).type == EmptyList.Type.INVENTORY)
            {
                var i = this.guiTop + 18;
                var j = i + 72;
                this.currentScroll = ((float) mouseY - i - 7.5F) / (j - i - 15.0F);
                this.currentScroll = Mth.clamp(this.currentScroll, 0.0F, 1.0F);
                this.skyBlockContainer.scrollTo(this.currentScroll);
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta)
    {
        if (this.loadingApi)
        {
            return false;
        }
        else
        {
            if (this.currentSlot != null)
            {
                if (this.currentSlot instanceof EmptyList && ((EmptyList) this.currentSlot).type == EmptyList.Type.INVENTORY)
                {
                    if (!this.needsScrollBars())
                    {
                        return false;
                    }
                    else
                    {
                        var i = (this.skyBlockContainer.itemList.size() + 9 - 1) / 9 - 4;
                        this.currentScroll = (float) (this.currentScroll - scrollDelta / i);
                        this.currentScroll = Mth.clamp(this.currentScroll, 0.0F, 1.0F);
                        this.skyBlockContainer.scrollTo(this.currentScroll);
                        return true;
                    }
                }
                this.currentSlot.mouseScrolled(mouseX, mouseY, scrollDelta);
            }
            if (this.errorInfo != null)
            {
                this.errorInfo.mouseScrolled(mouseX, mouseY, scrollDelta);
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollDelta);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(poseStack);

        if (this.loadingApi)
        {
            var text = "Downloading SkyBlock stats";
            var i = this.font.width(text);
            GuiComponent.drawCenteredString(poseStack, this.font, text, this.width / 2, this.height / 2 + this.font.lineHeight * 2 - 35, 16777215);
            GuiComponent.drawString(poseStack, this.font, SkyBlockProfileSelectorScreen.downloadingStates[(int) (Util.getMillis() / 500L % SkyBlockProfileSelectorScreen.downloadingStates.length)], this.width / 2 + i / 2, this.height / 2 + this.font.lineHeight * 2 - 35, 16777215);
            GuiComponent.drawCenteredString(poseStack, this.font, "Status: " + ChatFormatting.GRAY + this.statusMessage, this.width / 2, this.height / 2 + this.font.lineHeight * 2 - 15, 16777215);

            if (this.showArmorButton != null)
            {
                this.showArmorButton.visible = false;
            }
        }
        else
        {
            if (this.error)
            {
                GuiComponent.drawCenteredString(poseStack, this.font, "SkyBlock API Viewer", this.width / 2, 20, 16777215);

                if (this.errorInfo != null)
                {
                    this.errorInfo.render(poseStack, mouseX, mouseY, partialTicks);
                }
                else
                {
                    GuiComponent.drawCenteredString(poseStack, this.font, ChatFormatting.RED + this.errorMessage, this.width / 2, 100, 16777215);
                }
                if (this.showArmorButton != null)
                {
                    this.showArmorButton.visible = false;
                }
                super.render(poseStack, mouseX, mouseY, partialTicks);
            }
            else
            {
                if (this.currentSlot != null)
                {
                    this.currentSlot.render(poseStack, mouseX, mouseY, partialTicks);
                }

                GuiComponent.drawCenteredString(poseStack, this.font, TextComponentUtils.component(this.displayName).append(TextComponentUtils.formatted(" Profile: ", ChatFormatting.YELLOW)).append(this.sbProfileName.copy().withStyle(ChatFormatting.GOLD)).append(TextComponentUtils.formatted(" Game Mode: ", ChatFormatting.YELLOW)).append(this.gameMode).append(this.guild), this.width / 2, 29, 16777215);

                if (this.currentSlot != null && this.currentSlot instanceof EmptyList stat)
                {
                    if (stat.type == EmptyList.Type.INVENTORY)
                    {
                        this.drawGroupsBackgroundLayer(poseStack);
                    }
                }

                super.render(poseStack, mouseX, mouseY, partialTicks);

                if (this.currentSlot != null)
                {
                    if (this.currentSlot instanceof InfosList)
                    {
                        RenderSystem.enableDepthTest();
                        SkyBlockAPIViewerScreen.renderEntity(this.width / 2 - 106, this.height / 2 + 40, this.guiLeft - 55 - (float) mouseX, this.guiTop + 25 - (float) mouseY, this.player);
                        this.drawContainerSlot(poseStack, mouseX, mouseY, true);

                        if (this.hoveredSlot != null && this.hoveredSlot.hasItem())
                        {
                            this.renderTooltip(poseStack, this.hoveredSlot.getItem(), mouseX, mouseY);
                        }
                    }
                    else if (this.currentSlot instanceof EmptyList stat)
                    {
                        if (stat.type == EmptyList.Type.INVENTORY)
                        {
                            this.drawContainerSlot(poseStack, mouseX, mouseY, false);
                            this.drawTabsForegroundLayer(poseStack);

                            for (var group : SBInventoryGroup.GROUPS)
                            {
                                if (this.renderGroupsHoveringText(poseStack, group, mouseX, mouseY))
                                {
                                    break;
                                }
                            }

                            SkyBlockAPIViewerScreen.renderEntity(this.width / 2 - 96, this.height / 2 + 40, this.guiLeft - 46 - (float) mouseX, this.guiTop + 75 - 50 - (float) mouseY, this.player);

                            if (this.hoveredSlot != null && this.hoveredSlot.hasItem())
                            {
                                this.renderTooltip(poseStack, this.hoveredSlot.getItem(), mouseX, mouseY);
                            }
                        }
                        else if (stat.type == EmptyList.Type.DUNGEON)//TODO
                        {
                            var i = 0;

                            for (var dungeon : this.dungeonData)
                            {
                                var x = this.width / 2 - 150;
                                var y = 50;
                                var textY = y + 12 * i;
                                GuiComponent.drawString(poseStack, this.font, dungeon, x, textY, -1);
                                ++i;
                            }
                        }
                        else
                        {
                            var i = 0;
                            var height = this.height / 7;

                            for (var info : this.skillLeftList)
                            {
                                var x = this.width / 2 - 120;
                                var y = height + 12;
                                var barY = y + 20 + height * i;
                                var textY = y + height * i;
                                var extraCap = "";

                                if (info.name().equals("Farming") && this.farmingLevelCap > 0)
                                {
                                    extraCap = ChatFormatting.GOLD + " (+" + this.farmingLevelCap + ")";
                                }

                                this.renderSkillBar(poseStack, info.name(), x, barY, x + 46, textY, info.currentXp(), info.xpRequired(), info.currentLvl(), info.reachLimit(), extraCap);
                                ++i;
                            }

                            i = 0;

                            for (var info : this.skillRightList)
                            {
                                var x = this.width / 2 + 30;
                                var y = height + 12;
                                var barY = y + 20 + height * i;
                                var textY = y + height * i;
                                this.renderSkillBar(poseStack, info.name(), x, barY, x + 46, textY, info.currentXp(), info.xpRequired(), info.currentLvl(), info.reachLimit(), "");
                                ++i;
                            }

                            if (this.skillAvg != null)
                            {
                                var component = TextComponentUtils.component("AVG: " + this.skillAvg);
                                component.setStyle(component.getStyle().withFont(ClientUtils.UNICODE));
                                GuiComponent.drawString(poseStack, this.font, component, this.width - this.font.width(component) - 60, this.height - 38, 16777215);
                            }
                        }
                    }
                    else if (this.currentSlot instanceof SlayersList)
                    {
                        var total1 = ChatFormatting.GRAY + "Total Amount Spent: " + ChatFormatting.YELLOW + NumberUtils.NUMBER_FORMAT.format(this.slayerTotalAmountSpent);
                        var total2 = ChatFormatting.GRAY + "Total Slayer XP: " + ChatFormatting.YELLOW + NumberUtils.NUMBER_FORMAT.format(this.totalSlayerXp);
                        GuiComponent.drawString(poseStack, this.font, total1, this.width - this.font.width(total1) - 60, this.height - 36, 16777215);
                        GuiComponent.drawString(poseStack, this.font, total2, this.width - this.font.width(total2) - 60, this.height - 46, 16777215);

                        if (this.activeSlayerType != null)
                        {
                            GuiComponent.drawString(poseStack, this.font, ChatFormatting.GRAY + "Active Slayer: ", 60, this.height - 46, 16777215);
                            GuiComponent.drawString(poseStack, this.font, ChatFormatting.YELLOW + this.activeSlayerType.getName() + " - Tier " + this.activeSlayerTier, 60, this.height - 36, 16777215);
                        }
                    }
                    else if (this.currentSlot instanceof CraftedMinionsList)
                    {
                        var total1 = ChatFormatting.GRAY + "Unique Minions: " + ChatFormatting.YELLOW + this.craftedMinionCount + "/" + SBMinions.MINIONS.uniqueMinions() + ChatFormatting.GRAY + " (" + this.craftedMinionCount * 100 / SBMinions.MINIONS.uniqueMinions() + "%)";
                        var total2 = ChatFormatting.GRAY + "Current Minion Slot: " + ChatFormatting.YELLOW + this.currentMinionSlot + (this.additionalMinionSlot > 0 ? ChatFormatting.GOLD + " (Bonus +" + this.additionalMinionSlot + ")" : "");
                        GuiComponent.drawString(poseStack, this.font, total1, this.width - this.font.width(total1) - 60, this.height - 68, 16777215);
                        GuiComponent.drawString(poseStack, this.font, total2, this.width - this.font.width(total2) - 60, this.height - 58, 16777215);
                    }
                }

                RenderSystem.blendFuncSeparate(770, 771, 1, 0);
                RenderSystem.enableDepthTest();
            }
        }
    }

    // Input
    private void performedInfo(ViewButton viewButton)
    {
        switch (viewButton)
        {
            default -> {
                this.currentSlot = new InfosList(this.width - 119, this.height, 40, this.height - 50, 59, 12, this.infoList);
                this.refreshBasicInfoViewButton(this.basicInfoButton);
                this.refreshOthersViewButton(this.othersButton, false);
                this.performedBasicInfo(this.basicInfoButton);
            }
            case SKILLS -> {
                this.currentSlot = new EmptyList(this.width - 119, this.height, 40, this.height - 28, 59, 12, EmptyList.Type.SKILL);
                this.hideBasicInfoButton();
                this.hideOthersButton();
            }
            case SLAYERS -> {
                this.currentSlot = new SlayersList(this.width - 119, this.height, 40, this.height - 50, 59, 16, this.slayerInfo);
                this.hideBasicInfoButton();
                this.hideOthersButton();
            }
            case DUNGEONS -> {
                this.currentSlot = new EmptyList(this.width - 119, this.height, 40, this.height - 28, 59, 12, EmptyList.Type.DUNGEON);
                this.hideBasicInfoButton();
                this.hideOthersButton();
            }
            case OTHERS -> {
                this.hideBasicInfoButton();
                this.performedOthers(this.othersButton);
            }
        }
        this.refreshViewButton(viewButton);
    }

    private void performedBasicInfo(BasicInfoViewButton basicInfoButton)
    {
        switch (basicInfoButton)
        {
            default -> {
                this.currentSlot = new InfosList(this.width - 119, this.height, 40, this.height - 50, 59, 12, this.infoList);
                this.showArmorButton.visible = true;
                this.showArmorButton.x = this.width / 2 - 114;
                this.skyBlockArmorContainer = new ArmorContainer(true);
            }
            case INVENTORY -> {
                this.currentSlot = new EmptyList(this.width - 119, this.height, 40, this.height - 50, 59, 12, EmptyList.Type.INVENTORY);
                this.setCurrentGroup(SBInventoryGroup.GROUPS[this.selectedTabIndex]);
                this.showArmorButton.visible = true;
                this.showArmorButton.x = this.width / 2 - 104;
                this.skyBlockArmorContainer = new ArmorContainer(false);
            }
            case COLLECTIONS -> {
                this.currentSlot = new CollectionsList(this, this.width - 119, this.height, 40, this.height - 50, 59, 20, this.collections);
                this.showArmorButton.visible = false;
            }
            case CRAFTED_MINIONS -> {
                this.currentSlot = new CraftedMinionsList(this, this.width - 99, this.height, 40, this.height - 70, 49, 20, this.sbCraftedMinions);
                this.showArmorButton.visible = false;
            }
        }
        this.refreshBasicInfoViewButton(basicInfoButton);
    }

    private void performedOthers(OthersViewButton othersButton)
    {
        var list = switch (othersButton)
                {
                    case KILLS -> this.sbKills;
                    case DEATHS -> this.sbDeaths;
                    case OTHER_STATS -> this.sbOthers;
                    case BANK_HISTORY -> this.sbBankHistories;
                };

        this.currentSlot = new OthersList(this.width - 119, this.height, 40, this.height - 50, 59, 12, list);
        this.showArmorButton.visible = false;
        this.refreshOthersViewButton(othersButton, true);
    }

    private void refreshViewButton(ViewButton viewButton)
    {
        this.viewButton = viewButton;

        for (var view : ViewButton.VALUES)
        {
            if (view.button != null)
            {
                if (view.button == ViewButton.SKILLS.button)
                {
                    if (!this.data.hasSkills())
                    {
                        view.button.active = false;
                        continue;
                    }
                }
                if (view.button == ViewButton.SLAYERS.button)
                {
                    if (!this.data.hasSlayers())
                    {
                        view.button.active = false;
                        continue;
                    }
                }
                if (view.button == ViewButton.DUNGEONS.button)
                {
                    if (!this.data.hasDungeons())
                    {
                        view.button.active = false;
                        continue;
                    }
                }
                if (view.button == ViewButton.OTHERS.button)
                {
                    if (!this.data.hasOthersTab())
                    {
                        view.button.active = false;
                        continue;
                    }
                }
                view.button.active = this.viewButton != view;
            }
        }
    }

    private void refreshBasicInfoViewButton(BasicInfoViewButton basicInfoButton)
    {
        this.basicInfoButton = basicInfoButton;

        for (var view : BasicInfoViewButton.VALUES)
        {
            if (view.button != null)
            {
                view.button.active = this.basicInfoButton != view;

                if (view.button == BasicInfoViewButton.INVENTORY.button)
                {
                    if (!this.data.hasInventories())
                    {
                        view.button.active = false;
                        view.button.visible = true;
                        continue;
                    }
                }
                if (view.button == BasicInfoViewButton.COLLECTIONS.button)
                {
                    if (!this.data.hasCollections())
                    {
                        view.button.active = false;
                        view.button.visible = true;
                        continue;
                    }
                }
                if (view.button == BasicInfoViewButton.CRAFTED_MINIONS.button)
                {
                    if (!this.data.hasMinions())
                    {
                        view.button.active = false;
                        view.button.visible = true;
                        continue;
                    }
                }

                view.button.visible = true;
            }
        }
    }

    private void refreshOthersViewButton(OthersViewButton othersButton, boolean visible)
    {
        this.othersButton = othersButton;

        for (var view : OthersViewButton.VALUES)
        {
            if (view.button != null)
            {
                view.button.active = this.othersButton != view;

                if (view.button == OthersViewButton.KILLS.button)
                {
                    if (!this.data.hasKills())
                    {
                        view.button.active = false;
                        view.button.visible = visible;
                        continue;
                    }
                }
                if (view.button == OthersViewButton.DEATHS.button)
                {
                    if (!this.data.hasDeaths())
                    {
                        view.button.active = false;
                        view.button.visible = visible;
                        continue;
                    }
                }
                if (view.button == OthersViewButton.OTHER_STATS.button)
                {
                    if (!this.data.hasOthers())
                    {
                        view.button.active = false;
                        view.button.visible = visible;
                        continue;
                    }
                }
                if (view.button == OthersViewButton.BANK_HISTORY.button)
                {
                    if (!this.data.hasBankHistory())
                    {
                        view.button.active = false;
                        view.button.visible = visible;
                        continue;
                    }
                }

                view.button.visible = visible;
            }
        }
    }

    private void hideBasicInfoButton()
    {
        for (var view : BasicInfoViewButton.VALUES)
        {
            if (view.button != null)
            {
                view.button.visible = false;
            }
        }
        this.showArmorButton.visible = false;
    }

    private void hideOthersButton()
    {
        for (var view : OthersViewButton.VALUES)
        {
            if (view.button != null)
            {
                view.button.visible = false;
            }
        }
        this.showArmorButton.visible = false;
    }

    private void setErrorMessage(String message, boolean errorList)
    {
        this.error = true;
        this.loadingApi = false;
        this.updateErrorButton();

        if (errorList)
        {
            this.errorInfo = new APIErrorInfo(this, this.width - 39, this.height, 40, this.height / 4 + 128, 19, 12, this.errorList);
        }
        else
        {
            this.errorMessage = message;
        }
    }

    private void updateErrorButton()
    {
        this.backButton.visible = false;
        this.showArmorButton.visible = false;
        this.doneButton.x = this.width / 2 - 75;
        this.doneButton.y = this.height / 4 + 132;
        this.doneButton.setMessage(CommonComponents.GUI_BACK);

        for (var widget : this.renderables)
        {
            if (widget instanceof AbstractWidget abstractWidget && abstractWidget != this.doneButton)
            {
                abstractWidget.visible = false;
            }
        }
    }

    private boolean isMouseOverGroup(SBInventoryGroup group, double mouseX, double mouseY)
    {
        var i = group.getColumn();
        var j = 28 * i;
        var k = 0;

        if (i > 0)
        {
            j += i;
        }
        if (group.isOnTopRow())
        {
            k = k - 26;
        }
        else
        {
            k = k + this.ySize - 32;
        }
        return mouseX >= j + 1 && mouseX <= j + 27 && mouseY >= k && mouseY <= k + 26;
    }

    private boolean isSlotSelected(Slot slot, double mouseX, double mouseY)
    {
        return this.isPointInRegion(slot.x, slot.y, 16, 16, mouseX, mouseY);
    }

    private boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY)
    {
        var i = this.guiLeft;
        var j = this.guiTop;
        mouseX = mouseX - i;
        mouseY = mouseY - j;
        return mouseX >= x - 1 && mouseX < x + width + 1 && mouseY >= y - 1 && mouseY < y + height + 1;
    }

    private boolean isHoveredScroll(double mouseX, double mouseY)
    {
        var i = this.guiLeft;
        var j = this.guiTop;
        var k = i + 182;
        var l = j + 18;
        var i1 = k + 14;
        var j1 = l + 72;
        return mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1;
    }

    private void setCurrentGroup(SBInventoryGroup group)
    {
        if (group == null)
        {
            return;
        }
        this.selectedTabIndex = group.getIndex();
        var container = this.skyBlockContainer;
        container.itemList.clear();
        group.fill(container.itemList);
        this.currentScroll = 0.0F;
        container.scrollTo(0.0F);
    }

    private boolean needsScrollBars()
    {
        if (SBInventoryGroup.GROUPS[this.selectedTabIndex] == null)
        {
            return false;
        }
        return this.skyBlockContainer.canScroll();
    }

    // Render
    private void renderSkillBar(PoseStack poseStack, String name, int xBar, int yBar, int xText, int yText, double playerXp, int xpRequired, int currentLvl, boolean reachLimit, String extra)
    {
        var color = ColorUtils.toFloatArray(128, 255, 0);

        if (reachLimit)
        {
            color = ColorUtils.toFloatArray(255, 185, 0);
        }

        RenderSystem.setShaderColor(color[0], color[1], color[2], 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, XP_BARS);
        GuiComponent.blit(poseStack, xBar, yBar, 0, 0, 91, 5, 91, 10);

        if (xpRequired > 0)
        {
            var filled = reachLimit ? 91 : Math.min((int) Math.floor(playerXp * 92 / xpRequired), 91);

            if (filled > 0)
            {
                GuiComponent.blit(poseStack, xBar, yBar, 0, 5, filled, 5, 91, 10);
            }

            GuiComponent.drawCenteredString(poseStack, this.font, ChatFormatting.GRAY + name + (reachLimit ? ChatFormatting.GOLD : ChatFormatting.YELLOW) + " " + currentLvl + extra, xText, yText, 16777215);

            if (reachLimit)
            {
                GuiComponent.drawCenteredString(poseStack, this.font, SBNumberUtils.formatWithM(playerXp), xText, yText + 10, 16777215);
            }
            else
            {
                GuiComponent.drawCenteredString(poseStack, this.font, NumberUtils.formatCompact((long) playerXp) + "/" + NumberUtils.formatCompact(xpRequired), xText, yText + 10, 16777215);
            }
        }
        else
        {
            GuiComponent.drawCenteredString(poseStack, this.font, name, xText, yText + 8, 16777215);
        }
    }

    private void drawContainerSlot(PoseStack poseStack, int mouseX, int mouseY, boolean info)
    {
        var i = this.guiLeft;
        var j = this.guiTop;
        var poseStack2 = RenderSystem.getModelViewStack();
        poseStack2.pushPose();
        poseStack2.translate(i, j, 0.0D);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.hoveredSlot = null;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (!info)
        {
            for (var slot : this.skyBlockContainer.slots)
            {
                this.drawSlot(poseStack, slot);

                if (this.isSlotSelected(slot, mouseX, mouseY) && slot.isActive())
                {
                    this.hoveredSlot = slot;
                    AbstractContainerScreen.renderSlotHighlight(poseStack, i, j, this.getBlitOffset());
                }
            }
        }

        if (this.showArmor)
        {
            for (var slot : this.skyBlockArmorContainer.slots)
            {
                if (this.isSlotSelected(slot, mouseX, mouseY) && slot.isActive())
                {
                    this.hoveredSlot = slot;
                }
            }
        }
    }

    private void drawSlot(PoseStack poseStack, Slot slot)
    {
        var i = slot.x;
        var j = slot.y;
        var itemStack = slot.getItem();
        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0F;

        if (itemStack.isEmpty())
        {
            var pair = slot.getNoItemIcon();

            if (pair != null)
            {
                var sprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                RenderSystem.setShaderTexture(0, sprite.atlas().location());
                GuiComponent.blit(poseStack, i, j, this.getBlitOffset(), 16, 16, sprite);
            }
        }

        RenderSystem.enableDepthTest();
        this.itemRenderer.renderAndDecorateItem(itemStack, i, j);

        var slotLeft = slot.x;
        var slotTop = slot.y;
        var slotRight = slotLeft + 16;
        var slotBottom = slotTop + 16;
        var green = ColorUtils.to32Bit(85, 255, 85, 150);

        if (!itemStack.isEmpty() && itemStack.hasTag() && itemStack.getTag().getBoolean("active"))
        {
            this.fillGradient(poseStack, slotLeft, slotTop, slotRight, slotBottom, green, green);
        }

        this.renderItemOverlayIntoGUI(itemStack, i, j);
        this.itemRenderer.blitOffset = 0.0F;
        this.setBlitOffset(0);
    }

    private void renderItemOverlayIntoGUI(ItemStack itemStack, int xPosition, int yPosition)
    {
        if (!itemStack.isEmpty() && itemStack.getCount() != 1)
        {
            var poseStack = new PoseStack();
            var fontRenderer = this.font;
            var component = TextComponentUtils.component(NumberUtils.formatCompact(itemStack.getCount()));

            if (itemStack.getCount() >= 100)
            {
                component.setStyle(component.getStyle().withFont(ClientUtils.UNICODE));
            }

            poseStack.translate(0.0D, 0.0D, this.itemRenderer.blitOffset + 200.0F);
            var irendertypebuffer$impl = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            fontRenderer.drawInBatch(component, xPosition + 19 - 2 - fontRenderer.width(component), yPosition + 6 + 3, 16777215, true, poseStack.last().pose(), irendertypebuffer$impl, false, 0, 15728880);
            irendertypebuffer$impl.endBatch();
        }
        if (itemStack.isDamaged())
        {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();
            var tessellator = Tesselator.getInstance();
            var bufferbuilder = tessellator.getBuilder();
            var f = (float) itemStack.getDamageValue();
            var g = (float) itemStack.getMaxDamage();
            var h = Math.max(0.0F, (g - f) / g);
            var i = Math.round(13.0F - f * 13.0F / g);
            var j = Mth.hsvToRgb(h / 3.0F, 1.0F, 1.0F);
            this.itemRenderer.fillRect(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
            this.itemRenderer.fillRect(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }

    private boolean renderGroupsHoveringText(PoseStack poseStack, SBInventoryGroup group, int mouseX, int mouseY)
    {
        var i = group.getColumn();
        var j = 28 * i;
        var k = 0;

        if (i > 0)
        {
            j += i;
        }
        if (group.isOnTopRow())
        {
            k = k - 28;
        }
        else
        {
            k = k + this.ySize - 32;
        }

        if (this.isPointInRegion(j + 2, k + 3, 25, 25, mouseX, mouseY))
        {
            this.renderTooltip(poseStack, group.getTranslationKey(), mouseX, mouseY);
            return true;
        }
        else
        {
            return false;
        }
    }

    private void drawGroupsBackgroundLayer(PoseStack poseStack)
    {
        var group = SBInventoryGroup.GROUPS[this.selectedTabIndex];

        for (var group1 : SBInventoryGroup.GROUPS)
        {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, INVENTORY_TABS);

            if (group1.getIndex() != this.selectedTabIndex)
            {
                this.drawGroup(poseStack, group1);
            }
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, new ResourceLocation("skyblockcatia:textures/gui/group_" + group.getBackgroundTexture()));
        this.blit(poseStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        var i = this.guiLeft + 182;
        var j = this.guiTop + 18;
        var k = j + 72;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, INVENTORY_TABS);
        this.blit(poseStack, i, j + (int) ((k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
        this.drawGroup(poseStack, group);
        RenderSystem.disableDepthTest();
    }

    private void drawTabsForegroundLayer(PoseStack poseStack)
    {
        var group = SBInventoryGroup.GROUPS[this.selectedTabIndex];

        if (group != null)
        {
            RenderSystem.disableBlend();
            this.font.draw(poseStack, group.getTranslationKey(), this.guiLeft + 11, this.guiTop + 6, 4210752);
        }
    }

    private void drawGroup(PoseStack poseStack, SBInventoryGroup group)
    {
        var flag = group.getIndex() == this.selectedTabIndex;
        var flag1 = group.isOnTopRow();
        var i = group.getColumn();
        var j = i * 28;
        var k = 0;
        var l = this.guiLeft + 28 * i;
        var i1 = this.guiTop;
        var j1 = 32;

        if (flag)
        {
            k += 32;
        }
        if (i > 0)
        {
            l += i;
        }

        if (flag1)
        {
            i1 = i1 - 28;
        }
        else
        {
            k += 64;
            i1 = i1 + this.ySize - 33;
        }

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, l, i1, j, k, 28, j1);
        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0F;
        l = l + 6;
        i1 = i1 + 8 + (flag1 ? 1 : -1);
        var itemStack = group.getIcon();
        this.itemRenderer.renderAndDecorateItem(itemStack, l, i1);
        this.itemRenderer.renderGuiItemDecorations(this.font, itemStack, l, i1);

        if (group.isDisabled())
        {
            RenderSystem.disableDepthTest();
            itemStack = new ItemStack(Blocks.BARRIER);
            this.itemRenderer.blitOffset = 200.0F;
            this.itemRenderer.renderAndDecorateItem(itemStack, l, i1);
            this.itemRenderer.renderGuiItemDecorations(this.font, itemStack, l, i1);
            this.itemRenderer.blitOffset = 0.0F;
            RenderSystem.enableDepthTest();
        }

        this.itemRenderer.blitOffset = 0.0F;
        this.setBlitOffset(0);
    }

    // Player Data
    private void getPlayerData() throws IOException
    {
        this.statusMessage = "Getting Player Data";
        Map<String, SkyblockProfiles.Members> profiles;
        SkyblockProfiles.Banking banking;
        CommunityUpgrades communityUpgrade;

        if (this.skyblockProfiles == null)
        {
            var url = new URL(APIUrl.SKYBLOCK_PROFILE.getUrl() + this.sbProfileId);
            var profile = SkyBlockcatia.GSON.fromJson(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8), SkyblockProfiles.DirectProfile.class);

            if (profile.profile() == null)
            {
                this.setErrorMessage("No API data returned, please try again later!", false);
                return;
            }
            profiles = profile.profile().members();
            banking = profile.profile().banking();
            communityUpgrade = profile.profile().communityUpgrades();
        }
        else
        {
            profiles = this.skyblockProfiles.members();
            banking = this.skyblockProfiles.banking();
            communityUpgrade = this.skyblockProfiles.communityUpgrades();
        }

        for (var entry : profiles.entrySet())
        {
            var userUUID = entry.getKey();
            var currentUserProfile = profiles.get(userUUID);
            this.getCraftedMinions(currentUserProfile);

            if (banking != null)
            {
                this.getBankHistories(banking);
            }
            this.data.setHasBankHistory(banking != null && this.sbBankHistories.size() > 0);
        }

        this.processCraftedMinions();
        var checkUUID = this.uuid;

        for (var entry : profiles.entrySet())
        {
            var userUUID = entry.getKey();
            checkUUID = userUUID;

            if (userUUID.equals(this.uuid))
            {
                var currentUserProfile = profiles.get(userUUID);
                var urlStatus = new URL(SBAPIUtils.APIUrl.STATUS.getUrl() + this.uuid);
                var status = SkyBlockcatia.GSON.fromJson(IOUtils.toString(urlStatus.openConnection().getInputStream(), StandardCharsets.UTF_8), GameStatus.class);

                if (currentUserProfile.getJacob() != null)
                {
                    this.jacobInfo = this.getJacobData(currentUserProfile.getJacob());
                }

                this.getSkills(currentUserProfile);
                this.getStats(currentUserProfile);
                this.getSlayerInfo(currentUserProfile);
                this.getInventories(currentUserProfile);
                this.getPets(currentUserProfile);
                this.getCollections(currentUserProfile);
                this.getSacks(currentUserProfile);
                this.getDungeons(currentUserProfile);
                this.createFakePlayer();
                this.calculatePlayerStats(currentUserProfile);
                this.getItemStats(this.inventoryToStats, false);
                this.getItemStats(this.armorItems, true);
                this.applyBonuses();

                for (var group : SBInventoryGroup.GROUPS)
                {
                    if (group.isDisabled())
                    {
                        this.totalDisabledInv++;
                    }
                }

                this.data.setHasInventories(this.totalDisabledInv != 13);
                this.allStat.setEffectiveHealth(this.allStat.getDefense() <= 0 ? this.allStat.getHealth() : (int) (this.allStat.getHealth() * (1 + this.allStat.getDefense() / 100.0D)));
                this.getBasicInfo(currentUserProfile, banking, status, communityUpgrade);
                break;
            }
        }

        if (!checkUUID.equals(this.uuid))
        {
            this.setErrorMessage("Current Player UUID not matched Profile UUID, please try again later!", false);
            return;
        }

        this.refreshViewButton(this.viewButton);
        this.refreshBasicInfoViewButton(this.basicInfoButton);
        this.refreshOthersViewButton(this.othersButton, false);
        this.showArmorButton.visible = true;
        this.loadingApi = false;
    }

    private List<SkyBlockInfo> getJacobData(SkyblockProfiles.Jacob jacob)
    {
        var info = Lists.<SkyBlockInfo>newArrayList();
        var medals = jacob.medals();
        var perks = jacob.perks();

        if (medals != null)
        {
            var gold = ColorUtils.toHex(255, 215, 0);
            var silver = ColorUtils.toHex(192, 192, 192);
            var bronze = ColorUtils.toHex(205, 127, 50);

            info.add(new SkyBlockInfo("Gold Medal", String.valueOf(medals.gold()), gold));
            info.add(new SkyBlockInfo("Silver Medal", String.valueOf(medals.silver()), silver));
            info.add(new SkyBlockInfo("Bronze Medal", String.valueOf(medals.bronze()), bronze));
        }
        if (perks != null)
        {
            info.add(new SkyBlockInfo("Double Drops Perk", perks.doubleDrops() * 2 + "%"));
            this.farmingLevelCap = perks.levelCap();
            info.add(new SkyBlockInfo("Farming Level Cap", String.valueOf(this.farmingLevelCap)));
        }
        return info;
    }

    private List<SkyBlockInfo> getCommunityUpgrades(CommunityUpgrades communityUpgrades)
    {
        var info = Lists.<SkyBlockInfo>newArrayList();
        var upgrading = communityUpgrades.currentUpgrade();
        var states = communityUpgrades.upgradeStates();

        if (upgrading != null)
        {
            info.add(new SkyBlockInfo("Current Upgrade", upgrading.toString()));
        }
        if (states != null)
        {
            var upgradeStateMap = HashMultimap.<String, Integer>create();

            for (var state : states)
            {
                upgradeStateMap.put(state.upgrade(), state.tier());
            }
            for (var type : upgradeStateMap.keySet())
            {
                var data = CommunityUpgrades.Data.getData(type, Collections.max(upgradeStateMap.get(type)));
                var tier = data.getTier();
                var maxed = data.getType().getMaxed();
                var color = tier == maxed ? ChatFormatting.GOLD.toString() : "";

                if (data.getType() == CommunityUpgrades.Type.MINION_SLOTS)
                {
                    this.additionalMinionSlot = tier;
                }

                info.add(new SkyBlockInfo(color + data.getDisplayName(), color + tier + "/" + maxed));
            }
        }
        return info;
    }

    @Deprecated
    private final List<String> dungeonData = Lists.newArrayList();

    //TODO Make it better
    private void getDungeons(SkyblockProfiles.Members currentUserProfile)
    {
        var dungeons = currentUserProfile.getDungeons();

        this.dungeonData.add(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "WORK IN PROGRESS! NOT A FINAL GUI!");
        this.dungeonData.add(ChatFormatting.YELLOW + "Selected Class: " + ChatFormatting.GOLD + WordUtils.capitalize(dungeons.selectedClass()));

        for (var entry : dungeons.playerClasses().entrySet())
        {
            var info2 = this.calculateDungeonSkill(entry.getValue().experience(), SBDungeons.DUNGEONS.leveling(), SBDungeons.Class.valueOf(entry.getKey().toUpperCase(Locale.ROOT)).getName());
            this.dungeonData.add(ChatFormatting.YELLOW + info2.name() + ":" + ChatFormatting.GOLD + " LVL " + info2.currentLvl() + " " + (int) Math.floor(info2.currentXp()) + "/" + info2.xpRequired());
        }

        this.dungeonData.add("");

        for (var entry : dungeons.dungeonTypes().entrySet())
        {
            var typeData = entry.getValue();

            if (SBDungeons.DUNGEONS.validDungeons().contains(entry.getKey()))
            {
                var info = this.calculateDungeonSkill(typeData.experience(), SBDungeons.DUNGEONS.leveling(), WordUtils.capitalize(entry.getKey()));
                this.catacombsLevel = info.currentLvl();
                this.dungeonData.add(ChatFormatting.YELLOW + info.name() + ":" + ChatFormatting.GOLD + " LVL " + info.currentLvl() + " " + (int) Math.floor(info.currentXp()) + "/" + info.xpRequired());
            }

            if (typeData.highestFloorCompleted() > 0)
            {
                this.dungeonData.add(ChatFormatting.YELLOW + WordUtils.capitalize(entry.getKey().replace("_", " ")) + " Highest Floor: " + ChatFormatting.GOLD + typeData.highestFloorCompleted());
            }

            var builder = new StringBuilder();

            if (typeData.floorCompletions() != null)
            {
                for (var entry1 : typeData.floorCompletions().entrySet())
                {
                    builder.append(ChatFormatting.YELLOW).append("F").append(entry1.getKey()).append(": ").append(ChatFormatting.GOLD).append(NumberUtils.NUMBER_FORMAT.format(entry1.getValue())).append(" ");
                }
            }
            this.dungeonData.add(builder.toString());
        }
        this.data.setHasDungeons(dungeons.selectedClass() != null);
    }

    private SBSkills.Info calculateDungeonSkill(double playerXp, int[] leveling, String name)
    {
        var xpRequired = 0;
        var currentLvl = 0;
        var levelToCheck = 0;
        var xpTotal = 0;
        var xpToNextLvl = 0D;
        double currentXp;

        for (var x = 0; x < leveling.length; ++x)
        {
            if (playerXp >= xpTotal)
            {
                xpTotal += leveling[x];
                currentLvl = x;
                levelToCheck = x + 1;

                if (levelToCheck <= leveling.length)
                {
                    xpRequired = leveling[x];
                }
            }
        }

        if (levelToCheck < leveling.length)
        {
            xpToNextLvl = xpTotal - playerXp;
            currentXp = xpRequired - xpToNextLvl;
        }
        else
        {
            currentLvl = leveling.length;
            currentXp = playerXp - xpTotal;
        }

        if (currentXp < 0 && levelToCheck <= leveling.length) // fix for skill level almost reach to limit
        {
            xpToNextLvl = xpTotal - playerXp;
            currentXp = xpRequired - xpToNextLvl;
            currentLvl = leveling.length - 1;
        }
        return new SBSkills.Info(name, currentXp, xpRequired, currentLvl, 0, xpToNextLvl <= 0);
    }

    private void getBankHistories(SkyblockProfiles.Banking banking)
    {
        var bankHistory = banking.transactions();
        Collections.reverse(Arrays.asList(bankHistory));

        if (bankHistory.length > 0)
        {
            for (var bank : bankHistory)
            {
                this.sbBankHistories.add(new BankHistory.Stats(TextComponentUtils.formatted("------------------------------", ChatFormatting.DARK_GRAY)));
                this.sbBankHistories.add(new BankHistory.Stats(TextComponentUtils.component("Initiator: ").append(bank.name().equals("Bank Interest") ? TextComponentUtils.formatted(bank.name(), ColorUtils.toDecimal(255, 215, 0)) : TextComponentUtils.component(bank.name()))));
                this.sbBankHistories.add(new BankHistory.Stats(TextComponentUtils.formatted(bank.action().name, bank.action().color).append(TextComponentUtils.formatted(" " + SBNumberUtils.formatWithM(bank.amount()), ChatFormatting.GOLD)).append(TextComponentUtils.formatted(" about " + TimeUtils.getRelativeTime(bank.timestamp()), ChatFormatting.WHITE))));
            }
            this.sbBankHistories.add(new BankHistory.Stats(TextComponentUtils.formatted("------------------------------", ChatFormatting.DARK_GRAY)));
        }
    }

    private String getLocation(GameStatus status)
    {
        var session = status.session();
        var locationText = "";

        if (session.online())
        {
            var gameType = session.gameType();
            var mode = session.mode();

            if (gameType.equals("SKYBLOCK"))
            {
                return SBStats.STATS.currentLocations().getOrDefault(mode, mode);
            }
        }
        return locationText;
    }

    private void getCraftedMinions(SkyblockProfiles.Members currentProfile)
    {
        var craftedGenerators = currentProfile.getCraftedGenerators();

        if (craftedGenerators != null && craftedGenerators.length > 0)
        {
            for (var craftedMinion : craftedGenerators)
            {
                var split = craftedMinion.split("_");
                var minionType = split.length >= 3 ? split[0] + "_" + split[1] : split[0];
                var unlockedLvl = Integer.parseInt(split[split.length - 1]);
                this.craftedMinions.put(minionType, unlockedLvl);
                this.craftedMinionCount++;
            }
        }
    }

    private void processCraftedMinions()
    {
        for (var minion : SBMinions.MINIONS.craftedMinions().entrySet())
        {
            if (minion.getKey() <= this.craftedMinionCount)
            {
                this.currentMinionSlot = minion.getValue();
            }
        }

        var minionLevels = Lists.<SBMinions.Info>newArrayList();
        var minionDatas = Lists.<SBMinions.Data>newArrayList();
        var level = 1;

        for (var minion : SBMinions.MINIONS.type())
        {
            for (var minionType : this.craftedMinions.keySet())
            {
                if (minion.type().equals(minionType))
                {
                    level = Collections.max(this.craftedMinions.get(minionType));
                    break;
                }
            }
            minionLevels.add(new SBMinions.Info(minion.type(), minion.displayName(), minion.getMinionItem(), level, minion.getCategory()));
        }

        for (var entry : this.craftedMinions.asMap().entrySet())
        {
            var minionType = entry.getKey();
            var dummyTiers = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
            var craftedList = entry.getValue();
            var builder = new StringBuilder();
            var craftedTiers = Ints.toArray(craftedList);
            var minionList = Lists.<String>newArrayList();
            var dummySet = Sets.<Integer>newHashSet();
            var skippedList = Sets.<Integer>newHashSet();
            var type = SBMinions.MINIONS.getTypeByName(minionType);

            if (type != null && type.hasTier12())
            {
                dummyTiers = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
            }
            if (type == null)
            {
                SkyBlockcatia.LOGGER.warning("Found an unknown minion!, type: {}", minionType);
            }

            for (var craftedTier : craftedTiers)
            {
                dummySet.add(craftedTier);
            }
            for (var dummyTier : dummyTiers)
            {
                if (dummySet.add(dummyTier))
                {
                    skippedList.add(dummyTier);
                }
            }

            for (var skipped : skippedList)
            {
                minionList.add(ChatFormatting.RED.toString() + skipped);
            }
            for (var crafted : craftedList)
            {
                minionList.add(ChatFormatting.GREEN.toString() + crafted);
            }

            minionList.sort((text1, text2) -> new CompareToBuilder().append(Integer.parseInt(ChatFormatting.stripFormatting(text1)), Integer.parseInt(ChatFormatting.stripFormatting(text2))).build());
            var i = 0;

            for (var allTiers : minionList)
            {
                builder.append(allTiers, 0, allTiers.length() - (allTiers.length() == 4 ? 2 : 1)).append(NumberUtils.intToRoman(Integer.parseInt(allTiers.substring(2))));

                if (i < minionList.size() - 1)
                {
                    builder.append(" ");
                }
                ++i;
            }
            minionDatas.add(new SBMinions.Data(minionType, builder.toString()));
        }

        var farmingMinion = Lists.<SBMinions.CraftedInfo>newArrayList();
        var miningMinion = Lists.<SBMinions.CraftedInfo>newArrayList();
        var combatMinion = Lists.<SBMinions.CraftedInfo>newArrayList();
        var foragingMinion = Lists.<SBMinions.CraftedInfo>newArrayList();
        var fishingMinion = Lists.<SBMinions.CraftedInfo>newArrayList();
        var unknownMinion = Lists.<SBMinions.CraftedInfo>newArrayList();
        var dummy = new SBMinions.CraftedInfo(null, null, 0, null, ItemStack.EMPTY);
        String displayName = null;
        var itemStack = ItemStack.EMPTY;
        SBSkills.Type category = null;
        Comparator<SBMinions.CraftedInfo> com = (cm1, cm2) -> new CompareToBuilder().append(cm1.minionName().getString(), cm2.minionName().getString()).build();

        for (var minionData : minionDatas)
        {
            for (var minionLevel : minionLevels)
            {
                if (minionLevel.minionType().equals(minionData.minionType()))
                {
                    displayName = minionLevel.displayName();
                    level = minionLevel.minionMaxTier();
                    itemStack = minionLevel.minionItem();
                    category = minionLevel.category();
                    break;
                }
            }

            var min = new SBMinions.CraftedInfo(TextComponentUtils.component(minionData.minionType()), displayName, level, minionData.craftedTiers(), itemStack);

            switch (category)
            {
                case FARMING -> farmingMinion.add(min);
                case MINING -> miningMinion.add(min);
                case COMBAT -> combatMinion.add(min);
                case FORAGING -> foragingMinion.add(min);
                case FISHING -> fishingMinion.add(min);
                default -> unknownMinion.add(min);
            }
        }

        farmingMinion.sort(com);
        miningMinion.sort(com);
        combatMinion.sort(com);
        foragingMinion.sort(com);
        fishingMinion.sort(com);
        unknownMinion.sort(com);

        if (!farmingMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo(TextComponentUtils.formatted("Farming", ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), null, 0, null, ItemStack.EMPTY));
            this.sbCraftedMinions.addAll(farmingMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!miningMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo(TextComponentUtils.formatted("Mining", ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), null, 0, null, ItemStack.EMPTY));
            this.sbCraftedMinions.addAll(miningMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!combatMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo(TextComponentUtils.formatted("Combat", ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), null, 0, null, ItemStack.EMPTY));
            this.sbCraftedMinions.addAll(combatMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!foragingMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo(TextComponentUtils.formatted("Foraging", ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), null, 0, null, ItemStack.EMPTY));
            this.sbCraftedMinions.addAll(foragingMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!fishingMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo(TextComponentUtils.formatted("Fishing", ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), null, 0, null, ItemStack.EMPTY));
            this.sbCraftedMinions.addAll(fishingMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!unknownMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo(TextComponentUtils.formatted("Unknown", ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), null, 0, null, ItemStack.EMPTY));
            this.sbCraftedMinions.addAll(unknownMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (this.sbCraftedMinions.isEmpty())
        {
            this.data.setHasMinions(false);
        }
    }

    private void getCollections(SkyblockProfiles.Members currentProfile)
    {
        var collections = currentProfile.getCollection();
        var unlockedTiers = currentProfile.getUnlockedCollections();
        var skyblockCollectionMap = HashMultimap.<String, Integer>create();

        if (unlockedTiers != null && unlockedTiers.length > 0)
        {
            for (var unlockedTier : unlockedTiers)
            {
                var split = unlockedTier.toLowerCase(Locale.ROOT).split("_");
                var unlockedId = split.length >= 3 ? split[0] + "_" + split[1] : split[0];
                var unlockedLvl = Integer.parseInt(split[split.length - 1]);
                skyblockCollectionMap.put(this.replaceId(unlockedId), unlockedLvl);
            }
        }

        var dummyCollection = new SBCollections(ItemStack.EMPTY, null, -1, -1);

        if (collections != null && collections.entrySet().size() > 0)
        {
            var farming = Lists.<SBCollections>newArrayList();
            var mining = Lists.<SBCollections>newArrayList();
            var combat = Lists.<SBCollections>newArrayList();
            var foraging = Lists.<SBCollections>newArrayList();
            var fishing = Lists.<SBCollections>newArrayList();
            var unknown = Lists.<SBCollections>newArrayList();
            var allItem = Lists.<ItemLike>newArrayList();

            for (var collection : collections.entrySet())
            {
                var collectionId = this.replaceId(collection.getKey().toLowerCase(Locale.ROOT));
                var collectionCount = collection.getValue();
                var split = collectionId.split(":");
                var itemId = split[0];
                var meta = 0;
                var level = 0;

                try
                {
                    meta = Integer.parseInt(split[1]);
                }
                catch (Exception ignored)
                {
                }

                for (var itemIdFromLvl : skyblockCollectionMap.keySet())
                {
                    if (collectionId.equals(itemIdFromLvl))
                    {
                        level = Collections.max(skyblockCollectionMap.get(itemIdFromLvl));

                        if (level == -1)
                        {
                            level = 0;
                        }
                        break;
                    }
                }

                var newItemReg = ItemStackTheFlatteningFix.updateItem("minecraft:" + itemId, meta);

                if (newItemReg != null)
                {
                    itemId = EntityTheRenameningFix.RENAMED_ITEMS.getOrDefault(newItemReg, newItemReg);
                    itemId = SBConstants.SBITEM_ID_TO_MC_REMAP.getOrDefault(itemId, itemId);
                }

                Item item;
                ItemStack itemStack;

                if (itemId.startsWith("enchanted_"))
                {
                    item = Registry.ITEM.get(new ResourceLocation(itemId.replace("enchanted_", "")));
                    var unknownCollection = new ItemStack(item == Items.AIR ? Blocks.BARRIER : item);
                    var compound = new CompoundTag();
                    var list = new ListTag();
                    list.add(new CompoundTag());
                    compound.put("Enchantments", list);
                    unknownCollection.setTag(compound);
                    unknownCollection.setHoverName(TextComponentUtils.component(WordUtils.capitalize(itemId.replace("_", " "))));
                    itemStack = unknownCollection;
                }
                else
                {
                    item = Registry.ITEM.get(new ResourceLocation(itemId));
                    itemStack = new ItemStack(item);
                }

                if (itemStack.isEmpty())
                {
                    item = Registry.ITEM.get(new ResourceLocation(itemId));
                    var unknownCollection = new ItemStack(item == Items.AIR ? Blocks.BARRIER : item);
                    unknownCollection.setHoverName(TextComponentUtils.component(WordUtils.capitalize(itemId.replace("_", " "))));
                    itemStack = unknownCollection;
                }

                for (var entry : SBConstants.COLLECTION_MAP.entrySet())
                {
                    allItem.addAll(entry.getValue());

                    if (!itemId.startsWith("enchanted_") && this.matchesCollection(entry.getValue(), item))
                    {
                        switch (entry.getKey())
                        {
                            case COMBAT -> this.addToCollection(combat, entry.getKey(), itemStack, collectionCount, level);
                            case FARMING -> this.addToCollection(farming, entry.getKey(), itemStack, collectionCount, level);
                            case FISHING -> this.addToCollection(fishing, entry.getKey(), itemStack, collectionCount, level);
                            case FORAGING -> this.addToCollection(foraging, entry.getKey(), itemStack, collectionCount, level);
                            case MINING -> this.addToCollection(mining, entry.getKey(), itemStack, collectionCount, level);
                        }
                    }
                }

                if (itemId.startsWith("enchanted_"))
                {
                    this.addToCollection(unknown, SBCollections.Type.UNKNOWN, itemStack, collectionCount, level);
                }
                else if (itemId.equals("mithril_ore"))
                {
                    itemStack = new ItemStack(Items.PRISMARINE_CRYSTALS);
                    itemStack.setHoverName(TextComponentUtils.component("Mithril Ore"));
                    this.addToCollection(mining, SBCollections.Type.MINING, itemStack, collectionCount, level);
                }
                else if (!this.matchesCollection(allItem, item))
                {
                    this.addToCollection(unknown, SBCollections.Type.UNKNOWN, itemStack, collectionCount, level);
                }
            }

            Comparator<SBCollections> com = (sbColl1, sbColl2) -> new CompareToBuilder().append(sbColl1.type().ordinal(), sbColl2.type().ordinal()).append(sbColl2.value(), sbColl1.value()).build();
            farming.sort(com);
            mining.sort(com);
            combat.sort(com);
            foraging.sort(com);
            fishing.sort(com);
            unknown.sort(com);

            if (!farming.isEmpty())
            {
                this.collections.add(new SBCollections(ItemStack.EMPTY, SBCollections.Type.FARMING, -1, -1));
                this.collections.addAll(farming);
            }
            if (!mining.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SBCollections(ItemStack.EMPTY, SBCollections.Type.MINING, -1, -1));
                this.collections.addAll(mining);
            }
            if (!combat.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SBCollections(ItemStack.EMPTY, SBCollections.Type.COMBAT, -1, -1));
                this.collections.addAll(combat);
            }
            if (!foraging.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SBCollections(ItemStack.EMPTY, SBCollections.Type.FORAGING, -1, -1));
                this.collections.addAll(foraging);
            }
            if (!fishing.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SBCollections(ItemStack.EMPTY, SBCollections.Type.FISHING, -1, -1));
                this.collections.addAll(fishing);
            }
            if (!unknown.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SBCollections(ItemStack.EMPTY, SBCollections.Type.UNKNOWN, -1, -1));
                this.collections.addAll(unknown);
            }
            this.collections.add(dummyCollection);
        }
        else
        {
            this.data.setHasCollections(false);
        }
    }

    private boolean matchesCollection(List<ItemLike> list, Item item)
    {
        return list.stream().anyMatch(miningItem -> item == miningItem.asItem());
    }

    private void addToCollection(List<SBCollections> list, SBCollections.Type type, ItemStack itemStack, int collectionCount, int level)
    {
        list.add(new SBCollections(itemStack, type, collectionCount, level));
    }

    private void getSacks(SkyblockProfiles.Members currentProfile)
    {
        var sacks = Lists.<ItemStack>newArrayList();
        var runes = HashMultimap.<String, org.apache.commons.lang3.tuple.Pair<Integer, Integer>>create();

        if (currentProfile.getSacks() != null && !currentProfile.getSacks().isEmpty())
        {
            for (var sackEntry : currentProfile.getSacks().entrySet())
            {
                var count = sackEntry.getValue();
                var sackId = this.replaceId(sackEntry.getKey().toLowerCase(Locale.ROOT));
                var split = sackId.split(":");
                var itemId = split[0];
                var meta = 0;

                try
                {
                    meta = Integer.parseInt(split[1]);
                }
                catch (Exception ignored)
                {
                }

                var newItemReg = ItemStackTheFlatteningFix.updateItem("minecraft:" + itemId, meta);

                if (newItemReg != null)
                {
                    itemId = EntityTheRenameningFix.RENAMED_ITEMS.getOrDefault(newItemReg, newItemReg);
                }

                if (itemId.equals("minecraft:carved_pumpkin"))
                {
                    itemId = "minecraft:pumpkin";
                }
                else if (itemId.equals("melon"))
                {
                    itemId = "melon_slice";
                }

                if (count > 0)
                {
                    if (this.matchSackId(itemId, SBSlayers.Drops.values()))
                    {
                        this.checkSlayerSack(itemId, count, sacks);
                    }
                    else if (this.matchSackId(itemId, SBDungeons.Drops.values()))
                    {
                        this.checkDungeonSack(itemId, count, sacks);
                    }
                    else if (this.matchSackId(itemId, CandySacks.values()))
                    {
                        this.checkCandySack(itemId, count, sacks);
                    }
                    else if (this.matchSackId(itemId, DwarvenSacks.values()))
                    {
                        this.checkDwarvenSacks(itemId, count, sacks);
                    }
                    else
                    {
                        var item = Registry.ITEM.get(new ResourceLocation(itemId));

                        if (item == Items.AIR)
                        {
                            if (itemId.startsWith("rune_"))
                            {
                                var runeName = itemId.trim().replaceAll("(_[0-9])(?!_[0-9])", "");
                                var runeLevel = 0;
                                var runeLvlPattern = Pattern.compile("_[0-9]((?!_[0-9]))");
                                var runeLvlMatcher = runeLvlPattern.matcher(itemId);

                                if (runeLvlMatcher.find())
                                {
                                    runeLevel = Integer.parseInt(runeLvlMatcher.group().replace("_", ""));
                                }
                                runes.put(runeName, org.apache.commons.lang3.tuple.Pair.of(runeLevel, count));
                            }
                            else if (itemId.startsWith("enchanted_"))
                            {
                                item = Registry.ITEM.get(new ResourceLocation(itemId.replace("enchanted_", "")));
                                var enchantedItem = SBConstants.ENCHANTED_ID_TO_ITEM.getOrDefault(itemId, new ItemStack(item == Items.AIR ? Items.BARRIER : item, count));
                                enchantedItem.setCount(count);

                                if (enchantedItem.hasTag())
                                {
                                    var list = new ListTag();
                                    list.add(new CompoundTag());
                                    enchantedItem.getTag().put("Enchantments", list);
                                }
                                else
                                {
                                    var compound = new CompoundTag();
                                    var list = new ListTag();
                                    list.add(new CompoundTag());
                                    compound.put("Enchantments", list);
                                    enchantedItem.setTag(compound);
                                }
                                var component = TextComponentUtils.formatted(WordUtils.capitalize(itemId.replace("_", " ")), ChatFormatting.WHITE);
                                enchantedItem.setHoverName(component.setStyle(component.getStyle().withItalic(false)));
                                this.addSackItemStackCount(enchantedItem, count, null, true, sacks);
                            }
                            else
                            {
                                this.addSackItemStackCount(Blocks.BARRIER, count, TextComponentUtils.formatted(itemId, ChatFormatting.RED), false, sacks);
                            }
                        }
                        else
                        {
                            this.addSackItemStackCount(item, count, null, false, sacks);
                        }
                    }
                }
            }
            this.parseRunes(runes, sacks);
            sacks.sort((itemStack1, itemStack2) -> new CompareToBuilder().append(itemStack2.getCount(), itemStack1.getCount()).build());
        }
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(sacks, SBInventoryGroup.SACKS));
    }

    private <T extends Enum<?>> boolean matchSackId(String itemId, T[] enums)
    {
        return Arrays.stream(enums).anyMatch(drop -> itemId.contains(drop.name().toLowerCase(Locale.ROOT)));
    }

    private void checkSlayerSack(String itemId, int count, List<ItemStack> sacks)
    {
        try
        {
            var slayerDrops = SBSlayers.Drops.valueOf(itemId.toUpperCase(Locale.ROOT));
            this.addSackItemStackCount(slayerDrops.getBaseItem(), count, slayerDrops.getDisplayName(), true, sacks);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void checkDungeonSack(String itemId, int count, List<ItemStack> sacks)
    {
        try
        {
            var dungeonDrops = SBDungeons.Drops.valueOf(itemId.toUpperCase(Locale.ROOT));
            this.addSackItemStackCount(dungeonDrops.getBaseItem(), count, dungeonDrops.getDisplayName(), dungeonDrops.isEnchanted(), sacks);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void checkCandySack(String itemId, int count, List<ItemStack> sacks)
    {
        try
        {
            var candySacks = CandySacks.valueOf(itemId.toUpperCase(Locale.ROOT));
            var base = candySacks.getBaseItem();
            base.setCount(count);
            this.addSackItemStackCount(base, count, candySacks.getDisplayName(), false, sacks);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void checkDwarvenSacks(String itemId, int count, List<ItemStack> sacks)
    {
        try
        {
            var dwarvenSacks = DwarvenSacks.valueOf(itemId.toUpperCase(Locale.ROOT));
            var base = dwarvenSacks.getBaseItem();
            base.setCount(count);
            this.addSackItemStackCount(base, count, dwarvenSacks.getDisplayName(), dwarvenSacks == DwarvenSacks.TITANIUM_ORE, sacks);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void parseRunes(Multimap<String, org.apache.commons.lang3.tuple.Pair<Integer, Integer>> runes, List<ItemStack> sacks)
    {
        for (var entry : runes.asMap().entrySet())
        {
            var rune = RuneSacks.byName(entry.getKey());
            var base = rune.getBaseItem();
            var sortedLvl = Lists.newArrayList(entry.getValue());
            Collections.sort(sortedLvl);
            var sum = sortedLvl.stream().mapToInt(org.apache.commons.lang3.tuple.Pair::getRight).sum();
            base.setCount(sum);

            if (rune == RuneSacks.UNKNOWN)
            {
                this.addSackItemStackCount(base, sum, rune.getDisplayName().copy().append(" - " + entry.getKey()), false, sacks);
            }
            else
            {
                var loreList = new ListTag();

                for (var level : sortedLvl)
                {
                    loreList.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.WHITE + NumberUtils.intToRoman(level.getLeft()) + ":" + ChatFormatting.GRAY + " x" + level.getRight())));
                }

                base.getOrCreateTagElement("display").put("Lore", loreList);
                this.addSackItemStackCount(base, sum, rune.getDisplayName(), true, sacks);
            }
        }
    }

    private void addSackItemStackCount(ItemLike item, int count, Component altName, boolean ench, List<ItemStack> sacks)
    {
        var itemStack = new ItemStack(item, count);
        this.addSackItemStackCount(itemStack, count, altName, ench, sacks);
    }

    private void addSackItemStackCount(ItemStack itemStack, int count, Component altName, boolean ench, List<ItemStack> sacks)
    {
        if (count >= 1000)
        {
            MutableComponent component;

            if (altName != null)
            {
                component = altName.copy().append(TextComponentUtils.formatted(" x" + NumberUtils.NUMBER_FORMAT.format(count), ChatFormatting.GRAY));
            }
            else
            {
                component = itemStack.getHoverName().copy().append(TextComponentUtils.formatted(" x" + NumberUtils.NUMBER_FORMAT.format(count), ChatFormatting.GRAY));
            }
            itemStack.setHoverName(component.setStyle(component.getStyle().withItalic(false)));
        }
        else
        {
            if (altName != null)
            {
                var component = altName.copy();
                itemStack.setHoverName(component.setStyle(component.getStyle().withItalic(false)));
            }
        }

        if (ench)
        {
            var listEnch = new ListTag();
            listEnch.add(new CompoundTag());
            itemStack.getTag().put("Enchantments", listEnch);
        }
        sacks.add(itemStack);
    }

    private void getPets(SkyblockProfiles.Members currentUserProfile)
    {
        var petData = Lists.<SBPets.Data>newArrayList();
        var petItem = Lists.<ItemStack>newArrayList();
        var pets = currentUserProfile.getPets();

        if (pets == null || pets.length <= 0)
        {
            this.totalDisabledInv++;
            SKYBLOCK_INV.add(new SBInventoryGroup.Data(Collections.singletonList(new ItemStack(Blocks.BARRIER)), SBInventoryGroup.PET));
            return;
        }

        for (var pet : pets)
        {
            var exp = pet.exp();
            var petRarity = pet.tier();
            var candyUsed = pet.candyUsed();
            var heldItemObj = pet.heldItem();
            var skinObj = pet.skin();
            SBPets.HeldItem heldItem = null;
            String heldItemType = null;
            String skin = null;
            var skinName = "";

            if (skinObj != null)
            {
                skin = skinObj;
            }
            if (heldItemObj != null)
            {
                heldItem = SBPets.PETS.getHeldItemByName(heldItemObj);

                if (heldItem == null)
                {
                    heldItemType = heldItemObj;
                    SkyBlockcatia.LOGGER.warning("Found an unknown pet item!, type: {}", heldItemType);
                }
            }

            var tier = SBPets.Tier.valueOf(petRarity);
            var active = pet.active();
            var petType = pet.type();
            var list = new ListTag();

            if (heldItem != null && heldItem.isUpgrade())
            {
                tier = tier.getNextRarity();
            }

            var petInfo = this.checkPetLevel(exp, tier);
            var rarity = tier.getTierColor();
            var type = SBPets.PETS.getTypeByName(petType);
            var level = petInfo.currentPetLevel();

            if (type != null)
            {
                var stats = type.stats();
                var statsLore = type.statsLore();
                var lore = type.lore();
                var descList = type.description();
                var loreMode = type.loreMode();
                var listStats = Lists.<StringTag>newLinkedList();
                var listLore = Lists.<StringTag>newLinkedList();

                if (descList != null)
                {
                    for (var desc : descList)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(desc)));
                    }
                    listStats.add(StringTag.valueOf(TextComponentUtils.toJson("")));
                }
                if (stats != null)
                {
                    var damage = stats.damage();
                    var health = stats.health();
                    var strength = stats.strength();
                    var critDamage = stats.critDamage();
                    var critChance = stats.critChance();
                    var ferocity = stats.ferocity();
                    var attackSpeed = stats.attackSpeed();
                    var defense = stats.defense();
                    var trueDefense = stats.trueDefense();
                    var speed = stats.speed();
                    var intelligence = stats.intelligence();
                    var seaCreatureChance = stats.seaCreatureChance();
                    var magicFind = stats.magicFind();
                    var abilityDamage = stats.abilityDamage();

                    if (damage != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(damage.getString("Damage", level))));
                    }
                    if (health != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(health.getString("Health", level))));

                        if (active)
                        {
                            this.allStat.addHealth(health.getValue(level));
                        }
                    }
                    if (strength != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(strength.getString("Strength", level))));

                        if (active)
                        {
                            this.allStat.addStrength(strength.getValue(level));
                        }
                    }
                    if (critDamage != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(critDamage.getString("Crit Damage", level))));

                        if (active)
                        {
                            this.allStat.addCritDamage(critDamage.getValue(level));
                        }
                    }
                    if (critChance != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(critChance.getString("Crit Chance", level))));

                        if (active)
                        {
                            this.allStat.addCritChance(critChance.getValue(level));
                        }
                    }
                    if (ferocity != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(ferocity.getString("Ferocity", level))));

                        if (active)
                        {
                            this.allStat.addFerocity(ferocity.getValue(level));
                        }
                    }
                    if (attackSpeed != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(attackSpeed.getString("Bonus Attack Speed", level))));

                        if (active)
                        {
                            this.allStat.addAttackSpeed(attackSpeed.getValue(level));
                        }
                    }
                    if (speed != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(speed.getString("Speed", level))));

                        if (active)
                        {
                            this.allStat.addSpeed(speed.getValue(level));
                        }
                    }
                    if (defense != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(defense.getString("Defense", level))));

                        if (active)
                        {
                            this.allStat.addDefense(defense.getValue(level));
                        }
                    }
                    if (trueDefense != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(trueDefense.getString("True Defense", level))));

                        if (active)
                        {
                            this.allStat.addTrueDefense(trueDefense.getValue(level));
                        }
                    }
                    if (intelligence != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(intelligence.getString("Intelligence", level))));

                        if (active)
                        {
                            this.allStat.addIntelligence(intelligence.getValue(level));
                        }
                    }
                    if (seaCreatureChance != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(seaCreatureChance.getString("Sea Creature Chance", level))));

                        if (active)
                        {
                            this.allStat.addSeaCreatureChance(seaCreatureChance.getValue(level));
                        }
                    }
                    if (magicFind != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(magicFind.getString("Magic Find", level))));

                        if (active)
                        {
                            this.allStat.addMagicFind(magicFind.getValue(level));
                        }
                    }
                    if (abilityDamage != null)
                    {
                        listStats.add(StringTag.valueOf(TextComponentUtils.toJson(abilityDamage.getString("Ability Damage", level))));

                        if (active)
                        {
                            this.allStat.addAbilityDamage(abilityDamage.getValue(level));
                        }
                    }
                }
                if (lore != null)
                {
                    for (var entry : lore.entrySet())
                    {
                        var statTierName = entry.getKey();
                        var statTier = SBPets.Tier.valueOf(statTierName);
                        var foundLowTier = tier.ordinal() < statTier.ordinal();

                        if (loreMode != null && loreMode.equals("REPLACE") && !tier.equals(statTier))
                        {
                            continue;
                        }

                        var formattedLore = Lists.<StringTag>newArrayList();

                        for (var lore2 : entry.getValue())
                        {
                            if (foundLowTier)
                            {
                                continue;
                            }

                            if (statsLore != null)
                            {
                                var array = statsLore.get(tier.name());

                                if (statsLore.get("ALL") != null)
                                {
                                    array = statsLore.get("ALL");
                                }
                                else
                                {
                                    for (var tierTmp : SBPets.Tier.values())
                                    {
                                        var foundProp = statsLore.get(tierTmp.name());

                                        if (foundProp != null && array == null)
                                        {
                                            array = foundProp;
                                        }
                                    }
                                }

                                var statsLoreBase = array.base();
                                var statsLoreMult = array.multiply();
                                var statsLoreAddit = array.additional();
                                var roundingMode = array.roundingMode();
                                var displayMode = array.displayMode();

                                if (statsLoreBase != null && statsLoreMult != null)
                                {
                                    for (var i = 0; i < statsLoreMult.length; i++)
                                    {
                                        var value = statsLoreBase[i] + statsLoreMult[i] * level;
                                        var decimal = new BigDecimal(value);

                                        if (roundingMode != null)
                                        {
                                            decimal = decimal.setScale(1, RoundingMode.valueOf(roundingMode));
                                        }
                                        if (displayMode != null)
                                        {
                                            if (displayMode.equals("DISPLAY_AT_LEVEL_1"))
                                            {
                                                lore2 = lore2.replace("{" + i + "}", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(level == 1 ? statsLoreBase[i] : statsLoreMult[i] * level));
                                            }
                                        }
                                        else
                                        {
                                            lore2 = lore2.replace("{" + i + "}", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(level == 1 ? statsLoreBase[i] : decimal));
                                        }
                                    }
                                }
                                if (statsLoreAddit != null)
                                {
                                    for (var i2 = 0; i2 < statsLoreAddit.length; i2++)
                                    {
                                        lore2 = lore2.replace("{" + (char) (i2 + 65) + "}", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(statsLoreAddit[i2]));
                                    }
                                }
                            }
                            formattedLore.add(StringTag.valueOf(TextComponentUtils.toJson(lore2)));
                        }

                        listLore.addAll(formattedLore);

                        if (!foundLowTier)
                        {
                            listLore.add(StringTag.valueOf(TextComponentUtils.toJson("")));
                        }
                    }
                }

                var itemStack = type.getPetItem();
                var skinMark = "";

                if (skin != null)
                {
                    for (var petSkin : SBPets.PETS.skin())
                    {
                        if (skin.equals(petSkin.type()))
                        {
                            itemStack = ItemUtils.setSkullSkin(itemStack.copy(), petSkin.uuid(), petSkin.texture());
                            skinName = petSkin.name();
                            break;
                        }
                    }
                    if (skinName.isEmpty())
                    {
                        SkyBlockcatia.LOGGER.warning("Found an unknown pet skin! type: {}", skin);
                    }
                    skinName = ", " + (skinName.isEmpty() ? ChatFormatting.RED + skin + ChatFormatting.DARK_GRAY : skinName) + " Skin";
                    skinMark = " ✦";
                }

                itemStack.setHoverName(TextComponentUtils.component(ChatFormatting.GRAY + "[Lvl " + level + "] " + rarity + WordUtils.capitalize(petType.toLowerCase(Locale.ROOT).replace("_", " ")) + skinMark));
                list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET.toString() + ChatFormatting.DARK_GRAY + type.getSkill().getName() + " Pet" + skinName)));

                if (listStats.size() > 0)
                {
                    list.add(StringTag.valueOf(TextComponentUtils.toJson("")));
                    list.addAll(listStats);
                }

                if (listLore.size() > 0)
                {
                    list.add(StringTag.valueOf(TextComponentUtils.toJson("")));
                    list.addAll(listLore);
                }
                else
                {
                    list.add(StringTag.valueOf(TextComponentUtils.toJson("")));
                }

                if (heldItem != null)
                {
                    var heldItemName = ChatFormatting.getByName(heldItem.color()) + heldItem.name();
                    list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET.toString() + ChatFormatting.GOLD + "Held Item: " + heldItemName)));

                    for (var heldItemLore : heldItem.lore())
                    {
                        list.add(StringTag.valueOf(TextComponentUtils.toJson(heldItemLore)));
                    }
                    list.add(StringTag.valueOf(TextComponentUtils.toJson("")));
                }
                else
                {
                    if (heldItemType != null)
                    {
                        list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET.toString() + ChatFormatting.GRAY + "Held Item: " + ChatFormatting.RED + heldItemType)));
                        list.add(StringTag.valueOf(TextComponentUtils.toJson("")));
                    }
                }

                if (candyUsed > 0)
                {
                    list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET.toString() + ChatFormatting.GRAY + "Candy Used: " + ChatFormatting.YELLOW + candyUsed + ChatFormatting.GOLD + "/" + ChatFormatting.YELLOW + 10)));
                    list.add(StringTag.valueOf(TextComponentUtils.toJson("")));
                }

                list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET + (level < 100 ? ChatFormatting.GRAY + "Progress to Level " + petInfo.nextPetLevel() + ": " + ChatFormatting.YELLOW + petInfo.getPercent() : petInfo.getPercent()))));

                if (level < 100)
                {
                    list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET + this.getTextPercentage((int) petInfo.currentPetXp(), petInfo.xpRequired()) + " " + ChatFormatting.YELLOW + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(petInfo.currentPetXp()) + ChatFormatting.GOLD + "/" + ChatFormatting.YELLOW + SBNumberUtils.formatWithM(petInfo.xpRequired()))));
                }

                list.add(StringTag.valueOf(TextComponentUtils.toJson("")));
                list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET.toString() + ChatFormatting.GRAY + "Total XP: " + ChatFormatting.YELLOW + SBNumberUtils.formatWithM(petInfo.petXp()) + ChatFormatting.GOLD + "/" + ChatFormatting.YELLOW + SBNumberUtils.formatWithM(petInfo.totalPetTypeXp()))));
                list.add(StringTag.valueOf(TextComponentUtils.toJson(rarity.toString() + ChatFormatting.BOLD + tier + " PET")));
                itemStack.getTag().getCompound("display").put("Lore", list);
                itemStack.getOrCreateTagElement("ExtraAttributes").putString("id", "PET");
                itemStack.getTag().putBoolean("active", active);
                petData.add(new SBPets.Data(tier, level, TextComponentUtils.component(WordUtils.capitalize(petType.toLowerCase(Locale.ROOT).replace("_", " "))), active, Collections.singletonList(itemStack)));

                switch (tier)
                {
                    case COMMON -> this.petScore += 1;
                    case UNCOMMON -> this.petScore += 2;
                    case RARE -> this.petScore += 3;
                    case EPIC -> this.petScore += 4;
                    case LEGENDARY -> this.petScore += 5;
                    case MYTHIC -> this.petScore += 6;
                }
            }
            else
            {
                var itemStack = new ItemStack(Items.BONE);
                itemStack.setHoverName(TextComponentUtils.formatted(WordUtils.capitalize(petType.toLowerCase(Locale.ROOT).replace("_", " ")), ChatFormatting.RED));
                list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "UNKNOWN PET")));
                itemStack.getTag().getCompound("display").put("Lore", list);
                petData.add(new SBPets.Data(SBPets.Tier.COMMON, 0, itemStack.getHoverName(), false, Lists.newArrayList(itemStack)));
                SkyBlockcatia.LOGGER.warning("Found an unknown pet! type: {}", petType);
            }
            petData.sort((o1, o2) -> new CompareToBuilder().append(o2.isActive(), o1.isActive()).append(o2.tier().ordinal(), o1.tier().ordinal()).append(o2.currentLevel(), o1.currentLevel()).append(o1.name().getString(), o2.name().getString()).build());
        }
        for (var data : petData)
        {
            petItem.addAll(data.itemStack());
        }
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(petItem, SBInventoryGroup.PET));
    }

    private SBPets.Info checkPetLevel(double petExp, SBPets.Tier tier)
    {
        var index = SBPets.PETS.index().get(tier.name());
        var totalPetTypeXp = 0;
        var xpRequired = 0;
        var currentLvl = 0;
        var levelToCheck = 0;
        var xpTotal = 0D;
        double xpToNextLvl;
        var currentXp = 0D;

        for (var i = index; i < 99 + index; i++)
        {
            var level = SBPets.PETS.leveling()[i];
            totalPetTypeXp += level;

            if (petExp >= xpTotal)
            {
                xpTotal += level;
                currentLvl = i - index + 1;
                levelToCheck = currentLvl + 1;
                xpRequired = level;
            }
        }

        if (currentLvl < 100)
        {
            xpToNextLvl = Mth.ceil(xpTotal - petExp);
            currentXp = xpRequired - xpToNextLvl;
        }
        else if (currentLvl == 100)
        {
            xpToNextLvl = Mth.ceil(Math.abs(xpTotal - petExp));
            currentXp = xpRequired - xpToNextLvl;
        }

        if (petExp >= xpTotal || currentXp >= xpRequired)
        {
            currentLvl = 100;
            xpRequired = 0;
        }
        return new SBPets.Info(currentLvl, levelToCheck, currentXp, xpRequired, petExp, totalPetTypeXp);
    }

    private void applyBonuses()
    {
        if (this.checkSkyBlockItem(this.inventoryToStats, "NIGHT_CRYSTAL") == 1 || this.checkSkyBlockItem(this.inventoryToStats, "DAY_CRYSTAL") == 1)
        {
            this.allStat.addDefense(5);
            this.allStat.addStrength(5);
        }
        if (this.checkSkyBlockItem(this.inventoryToStats, "MELODY_HAIR") == 1)
        {
            this.allStat.addIntelligence(26);
        }
        if (this.checkSkyBlockItem(this.armorItems, "LAPIS_ARMOR_") == 4)
        {
            this.allStat.addHealth(60);
        }
        if (this.checkSkyBlockItem(this.armorItems, "MASTIFF_") == 4)
        {
            this.allStat.addHealth(50 * this.allStat.getCritDamage());
        }
        if (this.checkSkyBlockItem(this.armorItems, "YOUNG_DRAGON_") == 4)
        {
            this.allStat.addSpeed(70);
        }
        if (this.checkSkyBlockItem(this.armorItems, "SPEEDSTER_") == 4)
        {
            this.allStat.addSpeed(20);
        }
        if (this.checkSkyBlockItem(this.armorItems, "ANGLER_") == 4)
        {
            this.allStat.addSeaCreatureChance(4);
        }
        if (this.checkSkyBlockItem(this.armorItems, "SUPERIOR_DRAGON_") == 4)
        {
            this.allStat.setHealth(Math.round(this.allStat.getHealth() * 1.05D));
            this.allStat.setDefense(Math.round(this.allStat.getDefense() * 1.05D));
            this.allStat.setStrength(Math.round(this.allStat.getStrength() * 1.05D));
            this.allStat.setSpeed(Math.round(this.allStat.getSpeed() * 1.05D));
            this.allStat.setCritChance(Math.round(this.allStat.getCritChance() * 1.05D));
            this.allStat.setCritDamage(Math.round(this.allStat.getCritDamage() * 1.05D));
            this.allStat.setIntelligence(Math.round(this.allStat.getIntelligence() * 1.05D));
            this.allStat.setSeaCreatureChance(Math.round(this.allStat.getSeaCreatureChance() * 1.05D));
            this.allStat.setMagicFind(Math.round(this.allStat.getMagicFind() * 1.05D));
            this.allStat.setPetLuck(Math.round(this.allStat.getPetLuck() * 1.05D));
        }
        if (this.checkSkyBlockItem(this.armorItems, "FAIRY_") == 4)
        {
            this.allStat.addSpeed(10);
        }
        if (this.checkSkyBlockItem(this.armorItems, "CHEAP_TUXEDO_") == 3)
        {
            this.allStat.setHealth(75);
        }
        if (this.checkSkyBlockItem(this.armorItems, "FANCY_TUXEDO_") == 3)
        {
            this.allStat.setHealth(150);
        }
        if (this.checkSkyBlockItem(this.armorItems, "ELEGANT_TUXEDO_") == 3)
        {
            this.allStat.setHealth(250);
        }

        this.armorItems.stream().filter(armor -> !armor.isEmpty() && armor.hasTag() && armor.getTag().getCompound("ExtraAttributes").getString("modifier").equals("renowned")).collect(Collectors.toList()).forEach(itemStack ->
        {
            this.allStat.setHealth(Math.round(this.allStat.getHealth() * 1.01D));
            this.allStat.setDefense(Math.round(this.allStat.getDefense() * 1.01D));
            this.allStat.setStrength(Math.round(this.allStat.getStrength() * 1.01D));
            this.allStat.setSpeed(Math.round(this.allStat.getSpeed() * 1.01D));
            this.allStat.setCritChance(Math.round(this.allStat.getCritChance() * 1.01D));
            this.allStat.setCritDamage(Math.round(this.allStat.getCritDamage() * 1.01D));
            this.allStat.setIntelligence(Math.round(this.allStat.getIntelligence() * 1.01D));
            this.allStat.setSeaCreatureChance(Math.round(this.allStat.getSeaCreatureChance() * 1.01D));
            this.allStat.setMagicFind(Math.round(this.allStat.getMagicFind() * 1.01D));
            this.allStat.setPetLuck(Math.round(this.allStat.getPetLuck() * 1.01D));
        });
    }

    private void calculatePlayerStats(SkyblockProfiles.Members currentProfile)
    {
        this.totalFairySouls = currentProfile.getFairySoulsCollected();
        this.getFairySouls(currentProfile.getFairyExchanges());
        this.getMagicFindFromPets(this.petScore);
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.bonus().farming(), this.farmingLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.bonus().foraging(), this.foragingLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.bonus().mining(), this.miningLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.bonus().fishing(), this.fishingLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.bonus().combat(), this.combatLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.bonus().enchanting(), this.enchantingLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.bonus().alchemy(), this.alchemyLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.bonus().taming(), this.tamingLevel));
        this.allStat.add(this.calculateSkillBonus(SBDungeons.DUNGEONS.bonus().catacombs(), this.catacombsLevel));
        this.allStat.add(this.calculateSkillBonus(SBSlayers.SLAYERS.bonus().zombie(), this.zombieSlayerLevel));
        this.allStat.add(this.calculateSkillBonus(SBSlayers.SLAYERS.bonus().spider(), this.spiderSlayerLevel));
        this.allStat.add(this.calculateSkillBonus(SBSlayers.SLAYERS.bonus().wolf(), this.wolfSlayerLevel));
        this.allStat.add(this.calculateSkillBonus(SBSlayers.SLAYERS.bonus().enderman(), this.endermanSlayerLevel));
    }

    private BonusStatTemplate calculateSkillBonus(IBonusTemplate[] bonus, int skillLevel)
    {
        var healthTemp = 0;
        var defenseTemp = 0;
        var trueDefenseTemp = 0;
        var strengthTemp = 0;
        var speedTemp = 0;
        var critChanceTemp = 0;
        var critDamageTemp = 0;
        var attackSpeedTemp = 0;
        var intelligenceTemp = 0;
        var seaCreatureChanceTemp = 0;
        var magicFindTemp = 0;
        var petLuckTemp = 0;
        var ferocityTemp = 0;
        var abilityDamageTemp = 0;
        var miningSpeedTemp = 0;
        var miningFortuneTemp = 0;
        var farmingFortuneTemp = 0;
        var foragingFortuneTemp = 0;

        for (var i = 0; i < bonus.length; ++i)
        {
            var levelToCheck = bonus[i].getLevel();
            var nextIndex = 0;
            var limit = true;

            if (nextIndex <= i)
            {
                nextIndex = i + 1; // check level at next index of json
            }

            if (nextIndex >= bonus.length)
            {
                nextIndex = bonus.length - 1;
                limit = false;
            }

            var levelToCheck2 = bonus[nextIndex].getLevel();

            if (levelToCheck <= skillLevel)
            {
                var health = bonus[i].getHealth();
                var defense = bonus[i].getDefense();
                var trueDefense = bonus[i].getTrueDefense();
                var strength = bonus[i].getStrength();
                var speed = bonus[i].getSpeed();
                var critChance = bonus[i].getCritChance();
                var critDamage = bonus[i].getCritDamage();
                var attackSpeed = bonus[i].getAttackSpeed();
                var intelligence = bonus[i].getIntelligence();
                var seaCreatureChance = bonus[i].getSeaCreatureChance();
                var magicFind = bonus[i].getMagicFind();
                var petLuck = bonus[i].getPetLuck();
                var ferocity = bonus[i].getFerocity();
                var abilityDamage = bonus[i].getAbilityDamage();
                var miningSpeed = bonus[i].getMiningSpeed();
                var miningFortune = bonus[i].getMiningFortune();
                var farmingFortune = bonus[i].getFarmingFortune();
                var foragingFortune = bonus[i].getForagingFortune();

                for (var level = levelToCheck; level <= skillLevel; level++)
                {
                    if (level >= levelToCheck2 && limit)
                    {
                        break;
                    }
                    healthTemp += health;
                    defenseTemp += defense;
                    trueDefenseTemp += trueDefense;
                    strengthTemp += strength;
                    speedTemp += speed;
                    critChanceTemp += critChance;
                    critDamageTemp += critDamage;
                    attackSpeedTemp += attackSpeed;
                    intelligenceTemp += intelligence;
                    seaCreatureChanceTemp += seaCreatureChance;
                    magicFindTemp += magicFind;
                    petLuckTemp += petLuck;
                    ferocityTemp += ferocity;
                    abilityDamageTemp += abilityDamage;
                    miningSpeedTemp += miningSpeed;
                    miningFortuneTemp += miningFortune;
                    farmingFortuneTemp += farmingFortune;
                    foragingFortuneTemp += foragingFortune;
                }
            }
        }
        return new BonusStatTemplate(healthTemp, defenseTemp, trueDefenseTemp, 0, strengthTemp, speedTemp, critChanceTemp, critDamageTemp, attackSpeedTemp, intelligenceTemp, seaCreatureChanceTemp, magicFindTemp, petLuckTemp, ferocityTemp, abilityDamageTemp, miningSpeedTemp, miningFortuneTemp, farmingFortuneTemp, foragingFortuneTemp);
    }

    private void getHealthFromCake(CompoundTag extraAttrib)
    {
        var itemStack1 = Lists.<ItemStack>newArrayList();
        var cakeData = extraAttrib.getByteArray("new_year_cake_bag_data");

        if (cakeData.length == 0)
        {
            return;
        }

        try
        {
            var compound1 = NbtIo.readCompressed(new ByteArrayInputStream(cakeData));
            var list = compound1.getList("i", 10);
            var cakeYears = Lists.<Integer>newArrayList();

            for (var i = 0; i < list.size(); ++i)
            {
                itemStack1.add(SBItemUtils.flatteningItemStack(list.getCompound(i)));
            }

            for (var cake : itemStack1)
            {
                if (!cake.isEmpty() && cake.hasTag())
                {
                    var year = cake.getTag().getCompound("ExtraAttributes").getInt("new_years_cake");

                    if (!cakeYears.contains(year))
                    {
                        cakeYears.add(year);
                    }
                }
            }
            this.allStat.addHealth(cakeYears.size());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void getItemStats(List<ItemStack> inventory, boolean armor)
    {
        var healthTemp = 0;
        var defenseTemp = 0;
        var trueDefenseTemp = 0;
        var strengthTemp = 0;
        var speedTemp = 0;
        var critChanceTemp = 0;
        var critDamageTemp = 0;
        var attackSpeedTemp = 0;
        var intelligenceTemp = 0;
        var seaCreatureChanceTemp = 0;
        var magicFindTemp = 0;
        var petLuckTemp = 0;
        var ferocityTemp = 0;
        var abilityDamageTemp = 0;
        var miningSpeedTemp = 0;
        var miningFortuneTemp = 0;
        var farmingFortuneTemp = 0;
        var foragingFortuneTemp = 0;

        for (var itemStack : inventory)
        {
            if (!itemStack.isEmpty() && itemStack.hasTag())
            {
                var compound = itemStack.getTag().getCompound("display");
                var extraAttrib = itemStack.getTag().getCompound("ExtraAttributes");
                var itemId = extraAttrib.getString("id");

                switch (itemId)
                {
                    case "FROZEN_CHICKEN", "SPEED_TALISMAN" -> this.allStat.addSpeed(1);
                    case "SPEED_RING" -> this.allStat.addSpeed(3);
                    case "SPEED_ARTIFACT" -> this.allStat.addSpeed(5);
                    case "NEW_YEAR_CAKE_BAG" -> this.getHealthFromCake(extraAttrib);
                }

                if (compound.getTagType("Lore") == 9)
                {
                    var list = compound.getList("Lore", 8);

                    for (var j1 = 0; j1 < list.size(); ++j1)
                    {
                        var lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));
                        var lastLore = TextComponentUtils.fromJsonUnformatted(list.getString(list.size() - 1));
                        var matcher = STATS_PATTERN.matcher(lore);

                        if (!armor && !(lastLore.endsWith(" ACCESSORY") || lastLore.endsWith(" HATCCESSORY") || lastLore.endsWith(" ACCESSORY a") || lastLore.endsWith(" HATCCESSORY a")))
                        {
                            continue;
                        }

                        if (matcher.matches())
                        {
                            var type = matcher.group("type");
                            var value = matcher.group("value").replace(",", "");
                            var valueD = 0;

                            try
                            {
                                valueD = NumberUtils.NUMBER_FORMAT_WITH_OPERATORS.parse(value).intValue();
                            }
                            catch (Exception ignored)
                            {
                            }

                            switch (type)
                            {
                                case "Health" -> healthTemp += valueD;
                                case "Defense" -> defenseTemp += valueD;
                                case "True Defense" -> trueDefenseTemp += valueD;
                                case "Strength" -> strengthTemp += valueD;
                                case "Speed" -> speedTemp += valueD;
                                case "Crit Chance" -> critChanceTemp += valueD;
                                case "Crit Damage" -> critDamageTemp += valueD;
                                case "Intelligence" -> intelligenceTemp += valueD;
                                case "Sea Creature Chance" -> seaCreatureChanceTemp += valueD;
                                case "Magic Find" -> magicFindTemp += valueD;
                                case "Pet Luck" -> petLuckTemp += valueD;
                                case "Bonus Attack Speed" -> attackSpeedTemp += valueD;
                                case "Ferocity" -> ferocityTemp += valueD;
                                case "Ability Damage" -> abilityDamageTemp += valueD;
                                case "Mining Speed" -> miningSpeedTemp += valueD;
                                case "Mining Fortune" -> miningFortuneTemp += valueD;
                                case "Farming Fortune" -> farmingFortuneTemp += valueD;
                                case "Foraging Fortune" -> foragingFortuneTemp += valueD;
                            }
                        }
                    }
                }
            }
        }
        this.allStat.add(new BonusStatTemplate(healthTemp, defenseTemp, trueDefenseTemp, 0, strengthTemp, speedTemp, critChanceTemp, critDamageTemp, attackSpeedTemp, intelligenceTemp, seaCreatureChanceTemp, magicFindTemp, petLuckTemp, ferocityTemp, abilityDamageTemp, miningSpeedTemp, miningFortuneTemp, farmingFortuneTemp, foragingFortuneTemp));
    }

    private void getBasicInfo(SkyblockProfiles.Members currentProfile, SkyblockProfiles.Banking banking, GameStatus status, CommunityUpgrades communityUpgrade)
    {
        var deathCounts = currentProfile.getDeathCount();
        var coins = currentProfile.getPurse();
        var lastSaveMillis = currentProfile.getLastSave();
        var firstJoinMillis = currentProfile.getFirstJoin();

        if (this.uuid.equals("eef3a6031c1b4c988264d2f04b231ef4")) // special case for me :D
        {
            firstJoinMillis = 1565111612000L;
        }

        var heath = ColorUtils.toHex(239, 83, 80);
        var defense = ColorUtils.toHex(156, 204, 101);
        var trueDefense = ColorUtils.toHex(255, 255, 255);
        var strength = ColorUtils.toHex(181, 33, 30);
        var speed = ColorUtils.toHex(255, 255, 255);
        var critChance = ColorUtils.toHex(121, 134, 203);
        var critDamage = ColorUtils.toHex(70, 90, 201);
        var attackSpeed = ColorUtils.toHex(255, 255, 85);
        var miningSpeed = ColorUtils.toHex(255, 170, 0);
        var intelligence = ColorUtils.toHex(129, 212, 250);
        var fairySoulsColor = ColorUtils.toHex(203, 54, 202);
        var seaCreatureChance = ColorUtils.toHex(0, 170, 170);
        var magicFind = ColorUtils.toHex(85, 255, 255);
        var petLuck = ColorUtils.toHex(255, 85, 255);
        var bank = ColorUtils.toHex(255, 215, 0);
        var purseColor = ColorUtils.toHex(255, 165, 0);
        var ferocity = ColorUtils.toHex(224, 120, 0);
        var abilityDamage = ColorUtils.toHex(214, 36, 0);
        var location = this.getLocation(status);

        this.infoList.add(new SkyBlockInfo(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD + ChatFormatting.UNDERLINE + "Base Stats", ""));
        this.infoList.add(new SkyBlockInfo("❤ Health", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getHealth()), heath));
        this.infoList.add(new SkyBlockInfo("♥ Effective Health", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getEffectiveHealth()), heath));
        this.infoList.add(new SkyBlockInfo("❈ Defense", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getDefense()), defense));
        this.infoList.add(new SkyBlockInfo("❂ True Defense", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getTrueDefense()), trueDefense));
        this.infoList.add(new SkyBlockInfo("❁ Strength", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getStrength()), strength));
        this.infoList.add(new SkyBlockInfo("✦ Speed", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getSpeed()), speed));
        this.infoList.add(new SkyBlockInfo("☣ Crit Chance", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getCritChance()) + "%", critChance));
        this.infoList.add(new SkyBlockInfo("☠ Crit Damage", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getCritDamage()) + "%", critDamage));
        this.infoList.add(new SkyBlockInfo("⚔ Attack Speed", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getAttackSpeed()) + "%", attackSpeed));
        this.infoList.add(new SkyBlockInfo("↑ Mining Speed", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getMiningSpeed()), miningSpeed));
        this.infoList.add(new SkyBlockInfo("✎ Intelligence", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getIntelligence()), intelligence));
        this.infoList.add(new SkyBlockInfo("α Sea Creature Chance", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getSeaCreatureChance()) + "%", seaCreatureChance));
        this.infoList.add(new SkyBlockInfo("✯ Magic Find", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getMagicFind()), magicFind));
        this.infoList.add(new SkyBlockInfo("♣ Pet Luck", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getPetLuck()), petLuck));
        this.infoList.add(new SkyBlockInfo("⫽ Ferocity", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getFerocity()), ferocity));
        this.infoList.add(new SkyBlockInfo("๑ Ability Damage", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getAbilityDamage()) + "%", abilityDamage));
        this.infoList.add(new SkyBlockInfo("♆ Mining Fortune", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getMiningFortune()), miningSpeed));
        this.infoList.add(new SkyBlockInfo("♆ Farming Fortune", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getFarmingFortune()), miningSpeed));
        this.infoList.add(new SkyBlockInfo("♆ Foraging Fortune", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getForagingFortune()), miningSpeed));
        this.infoList.add(new SkyBlockInfo("☘ Fairy Souls Collected", this.totalFairySouls + "/" + SBAPIUtils.MAX_FAIRY_SOULS, fairySoulsColor));

        this.infoList.add(new SkyBlockInfo("", ""));
        this.infoList.add(new SkyBlockInfo(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD + ChatFormatting.UNDERLINE + "Account", ""));

        if (!StringUtil.isNullOrEmpty(location))
        {
            this.infoList.add(new SkyBlockInfo(ChatFormatting.GREEN + "Current Location", ChatFormatting.GREEN + location));
        }
        else
        {
            this.infoList.add(new SkyBlockInfo(ChatFormatting.RED + "Status", ChatFormatting.RED + "Offline"));
        }

        if (banking != null)
        {
            var balance = banking.balance();
            this.infoList.add(new SkyBlockInfo("Banking Account", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(balance), bank));
        }
        else
        {
            this.infoList.add(new SkyBlockInfo("Banking Account", ChatFormatting.RED + "API is not enabled!"));
        }

        this.infoList.add(new SkyBlockInfo("Purse", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(coins), purseColor));

        if (communityUpgrade != null)
        {
            var comm = this.getCommunityUpgrades(communityUpgrade);

            if (comm.size() > 0)
            {
                this.infoList.add(new SkyBlockInfo("", ""));
                this.infoList.add(new SkyBlockInfo(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD + ChatFormatting.UNDERLINE + "Community Upgrades", ""));
                this.infoList.addAll(comm);
            }
        }

        if (this.jacobInfo.size() > 0)
        {
            this.infoList.add(new SkyBlockInfo("", ""));
            this.infoList.add(new SkyBlockInfo(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD + ChatFormatting.UNDERLINE + "Farming Contest", ""));
            this.infoList.addAll(this.jacobInfo);
        }

        this.infoList.add(new SkyBlockInfo("", ""));
        this.infoList.add(new SkyBlockInfo(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD + ChatFormatting.UNDERLINE + "Others", ""));

        var firstJoinDate = new Date(firstJoinMillis);
        var lastSaveDate = new Date(lastSaveMillis);
        var logoutDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ROOT);
        var lastLogout = logoutDate.format(lastSaveDate);
        var joinDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ROOT);
        joinDate.setTimeZone(this.uuid.equals("eef3a6031c1b4c988264d2f04b231ef4") ? TimeZone.getTimeZone("GMT") : TimeZone.getDefault());
        var firstJoinDateFormat = joinDate.format(firstJoinDate);

        this.infoList.add(new SkyBlockInfo("Joined", firstJoinMillis != -1 ? TimeUtils.getRelativeTime(firstJoinDate.getTime()) + " (" + TimeUtils.getRelativeDay(firstJoinDate.getTime()) + ")" : ChatFormatting.RED + "No first join data!"));
        this.infoList.add(new SkyBlockInfo("Joined (Date)", firstJoinMillis != -1 ? firstJoinDateFormat : ChatFormatting.RED + "No first join data!"));
        this.infoList.add(new SkyBlockInfo("Last Updated", lastSaveMillis != -1 ? String.valueOf(lastSaveDate.getTime()) : ChatFormatting.RED + "No last save data!"));
        this.infoList.add(new SkyBlockInfo("Last Updated (Date)", lastSaveMillis != -1 ? lastLogout : ChatFormatting.RED + "No last save data!"));

        this.infoList.add(new SkyBlockInfo("Death Count", NumberUtils.NUMBER_FORMAT.format(deathCounts)));
    }

    private void getFairySouls(int fairyExchanges)
    {
        var healthBase = 0;
        var defenseBase = 0;
        var strengthBase = 0;
        var speed = Math.floor(fairyExchanges / 10D);

        for (var i = 0; i < fairyExchanges; i++)
        {
            healthBase += 3 + Math.floor(i / 2D);
            defenseBase += (i + 1) % 5 == 0 ? 2 : 1;
            strengthBase += (i + 1) % 5 == 0 ? 2 : 1;
        }
        this.allStat.addHealth(healthBase);
        this.allStat.addDefense(defenseBase);
        this.allStat.addStrength(strengthBase);
        this.allStat.addSpeed(speed);
    }

    private void getMagicFindFromPets(int petsScore)
    {
        for (var score : SBPets.PETS.score().entrySet())
        {
            if (score.getKey() <= petsScore)
            {
                this.allStat.addMagicFind(score.getValue());
            }
        }
    }

    private String replaceStatsString(String statName, String replace)
    {
        var original = statName.replace(replace + "_", "").replace("_", " ");
        return original.equals(replace) ? "Total " + replace : WordUtils.capitalize(original) + " " + replace;
    }

    private void getSkills(SkyblockProfiles.Members currentProfile)
    {
        this.skillLeftList.add(this.checkSkill(currentProfile.getFarmingExp(), SBSkills.Type.FARMING));
        this.skillLeftList.add(this.checkSkill(currentProfile.getForagingExp(), SBSkills.Type.FORAGING));
        this.skillLeftList.add(this.checkSkill(currentProfile.getMiningExp(), SBSkills.Type.MINING));
        this.skillLeftList.add(this.checkSkill(currentProfile.getFishingExp(), SBSkills.Type.FISHING));
        this.skillLeftList.add(this.checkSkill(currentProfile.getRunecraftingExp(), SBSkills.Type.RUNECRAFTING, SBSkills.SKILLS.leveling().get("runecrafting")));

        this.skillRightList.add(this.checkSkill(currentProfile.getCombatExp(), SBSkills.Type.COMBAT));
        this.skillRightList.add(this.checkSkill(currentProfile.getEnchantingExp(), SBSkills.Type.ENCHANTING));
        this.skillRightList.add(this.checkSkill(currentProfile.getAlchemyExp(), SBSkills.Type.ALCHEMY));
        this.skillRightList.add(this.checkSkill(currentProfile.getTamingExp(), SBSkills.Type.TAMING));
        this.skillRightList.add(this.checkSkill(currentProfile.getCarpentryExp(), SBSkills.Type.CARPENTRY));

        var avg = 0.0D;
        var progress = 0.0D;
        var count = 0;
        var skills = Lists.<SBSkills.Info>newArrayList();
        skills.addAll(this.skillLeftList);
        skills.addAll(this.skillRightList);

        for (var skill : skills)
        {
            if (skill.name().contains("Runecrafting") || skill.name().contains("Carpentry"))
            {
                continue;
            }
            avg += skill.currentLvl();
            progress += skill.skillProgress();
            ++count;
        }

        var allProgress = new BigDecimal(progress / count).setScale(2, RoundingMode.HALF_UP).doubleValue();

        if (avg > 0)
        {
            var realAvg = avg / count + allProgress;
            this.skillAvg = new BigDecimal(realAvg).setScale(2, RoundingMode.HALF_UP).toString();
        }
        if (this.skillCount == 0)
        {
            this.data.setHasSkills(false);
        }
    }

    private SBSkills.Info checkSkill(Double exp, SBSkills.Type type)
    {
        return this.checkSkill(exp, type, SBSkills.SKILLS.leveling().get("default"));
    }

    private SBSkills.Info checkSkill(Double exp, SBSkills.Type type, int[] leveling)
    {
        if (exp != null)
        {
            var xpRequired = 0;
            var currentLvl = 0;
            var levelToCheck = 0;
            var xpTotal = 0;
            var xpToNextLvl = 0D;
            double currentXp;
            var skillProgress = 0D;
            var cap = SBSkills.SKILLS.cap().get(type.name().toLowerCase(Locale.ROOT));

            if (type == SBSkills.Type.FARMING)
            {
                cap += this.farmingLevelCap;
            }

            for (var x = 0; x < cap; ++x)
            {
                if (exp >= xpTotal)
                {
                    xpTotal += leveling[x];
                    currentLvl = x;
                    levelToCheck = currentLvl + 1;

                    if (levelToCheck <= cap)
                    {
                        xpRequired = leveling[x];
                    }
                }
            }

            if (levelToCheck < cap)
            {
                xpToNextLvl = xpTotal - exp;
                currentXp = (int) (xpRequired - xpToNextLvl);
            }
            else
            {
                currentLvl = cap;
                currentXp = exp - xpTotal;
            }

            if (currentXp < 0 && levelToCheck <= cap) // fix for skill level almost reach to limit
            {
                xpToNextLvl = xpTotal - exp;
                currentXp = (int) (xpRequired - xpToNextLvl);
                currentLvl = cap - 1;
            }
            if (!type.isCosmetic())
            {
                skillProgress = currentLvl < cap ? currentXp / xpRequired : 0.0D;
            }
            this.setSkillLevel(type, currentLvl);
            this.skillCount += 1;
            return new SBSkills.Info(type.getName(), currentXp, xpRequired, currentLvl, skillProgress, xpToNextLvl <= 0);
        }
        else
        {
            return new SBSkills.Info(ChatFormatting.RED + type.getName() + " is not available!", 0, 0, 0, 0, false);
        }
    }

    private void setSkillLevel(SBSkills.Type type, int currentLevel)
    {
        switch (type)
        {
            case FARMING -> this.farmingLevel = currentLevel;
            case FORAGING -> this.foragingLevel = currentLevel;
            case MINING -> this.miningLevel = currentLevel;
            case FISHING -> this.fishingLevel = currentLevel;
            case COMBAT -> this.combatLevel = currentLevel;
            case ENCHANTING -> this.enchantingLevel = currentLevel;
            case ALCHEMY -> this.alchemyLevel = currentLevel;
            case TAMING -> this.tamingLevel = currentLevel;
        }
    }

    private void getStats(SkyblockProfiles.Members currentProfile)
    {
        var stats = currentProfile.getStats();
        var auctions = Lists.<SBStats.Display>newArrayList();
        var fished = Lists.<SBStats.Display>newArrayList();
        var winter = Lists.<SBStats.Display>newArrayList();
        var petMilestone = Lists.<SBStats.Display>newArrayList();
        var others = Lists.<SBStats.Display>newArrayList();
        var mobKills = Lists.<SBStats.Display>newArrayList();
        var seaCreatures = Lists.<SBStats.Display>newArrayList();
        var dragons = Lists.<SBStats.Display>newArrayList();
        var race = Lists.<SBStats.Display>newArrayList();
        var mythosBurrowsDug = Lists.<SBStats.Display>newArrayList();

        // special case
        var emperorKills = 0;
        var deepMonsterKills = 0;

        for (var stat : stats.entrySet().stream().filter(entry -> SBStats.STATS.blacklist().stream().noneMatch(stat -> entry.getKey().equals(stat))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).entrySet())
        {
            var statName = stat.getKey().toLowerCase(Locale.ROOT);
            var value = stat.getValue().getAsDouble();

            if (statName.startsWith("kills") || statName.endsWith("kills"))
            {
                if (SBStats.STATS.seaCreatures().stream().anyMatch(statName::contains))
                {
                    if (statName.contains("skeleton_emperor") || statName.contains("guardian_emperor"))
                    {
                        emperorKills += value;
                    }
                    else if (statName.contains("chicken_deep") || statName.contains("zombie_deep"))
                    {
                        deepMonsterKills += value;
                    }
                    else
                    {
                        seaCreatures.add(new SBStats.Display(this.replaceStatsString(statName, "kills"), value));
                    }
                }
                else if (statName.contains("dragon"))
                {
                    dragons.add(new SBStats.Display(this.replaceStatsString(statName, "kills"), value));
                }
                else
                {
                    mobKills.add(new SBStats.Display(this.replaceStatsString(statName, "kills"), value));
                }
            }
            else if (statName.startsWith("deaths"))
            {
                this.sbDeaths.add(new SBStats.Display(this.replaceStatsString(statName, "deaths"), value));
            }
            else
            {
                statName = SBStats.STATS.renamed().getOrDefault(statName, statName);

                if (statName.contains("auctions"))
                {
                    auctions.add(new SBStats.Display(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
                else if (statName.contains("items_fished") || statName.contains("shredder"))
                {
                    fished.add(new SBStats.Display(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
                else if (statName.contains("gifts") || statName.contains("most_winter"))
                {
                    winter.add(new SBStats.Display(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
                else if (statName.contains("pet_milestone"))
                {
                    petMilestone.add(new SBStats.Display(WordUtils.capitalize(statName.replace("pet_milestone_", "").replace("_", " ")), value));
                }
                else if (statName.contains("race") || statName.contains("dungeon_hub"))
                {
                    race.add(new SBStats.Display(WordUtils.capitalize(statName.replaceAll("dungeon_hub_|_best_time", "").replace("_", " ")), String.format("%1$TM:%1$TS.%1$TL", (long) value)));
                }
                else if (statName.startsWith("mythos_burrows_"))
                {
                    mythosBurrowsDug.add(new SBStats.Display(WordUtils.capitalize(statName.toLowerCase(Locale.ROOT).replace("mythos_burrows_", "").replace("_", " ")), value));
                }
                else
                {
                    others.add(new SBStats.Display(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
            }
        }

        // special case
        if (emperorKills > 0)
        {
            seaCreatures.add(new SBStats.Display("Sea Emperor kills", emperorKills));
        }
        if (deepMonsterKills > 0)
        {
            seaCreatures.add(new SBStats.Display("Monster of the Deep kills", deepMonsterKills));
        }

        this.sbDeaths.sort((stat1, stat2) -> new CompareToBuilder().append(stat2.getValue(), stat1.getValue()).build());
        auctions.sort((stat1, stat2) -> new CompareToBuilder().append(stat1.getName().getString(), stat2.getName().getString()).build());
        auctions.add(0, new SBStats.Display(TextComponentUtils.formatted("Auctions", ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), 0.0F));

        this.sortStats(fished, "Fishing");
        this.sortStats(winter, "Winter Event");
        this.sortStats(petMilestone, "Pet Milestones");
        this.sortStats(race, "Races");
        this.sortStats(mythosBurrowsDug, "Mythos Burrows Dug");
        this.sortStats(others, "Others");

        mobKills.sort((stat1, stat2) -> new CompareToBuilder().append(stat2.getValue(), stat1.getValue()).build());
        mobKills.add(0, new SBStats.Display(TextComponentUtils.formatted("Mob Kills", ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), 0.0F));

        this.sortStatsByValue(dragons, "Dragon Kills");
        this.sortStatsByValue(seaCreatures, "Sea Creature Kills");

        this.checkEmptyList(this.sbKills, mobKills);
        this.checkEmptyList(this.sbKills, dragons);
        this.checkEmptyList(this.sbKills, seaCreatures);

        this.checkEmptyList(this.sbOthers, auctions);
        this.checkEmptyList(this.sbOthers, fished);
        this.checkEmptyList(this.sbOthers, winter);
        this.checkEmptyList(this.sbOthers, petMilestone);
        this.checkEmptyList(this.sbOthers, race);
        this.checkEmptyList(this.sbOthers, mythosBurrowsDug);
        this.checkEmptyList(this.sbOthers, others);

        this.data.setHasKills(this.sbKills.size() > 1);
        this.data.setHasDeaths(this.sbDeaths.size() > 1);
        this.data.setHasOthers(this.sbOthers.size() > 1);

        if (!this.data.hasKills() && !this.data.hasDeaths() && !this.data.hasOthers())
        {
            this.data.setHasOthersTab(false);
        }
    }

    private <T> void checkEmptyList(List<T> parent, List<T> toAdd)
    {
        if (toAdd.size() > 2)
        {
            parent.addAll(toAdd);
        }
    }

    private void sortStats(List<SBStats.Display> list, String name)
    {
        list.sort((stat1, stat2) -> new CompareToBuilder().append(stat1.getName().getString(), stat2.getName().getString()).build());
        list.add(0, new SBStats.Display(TextComponent.EMPTY, 0.0F));
        list.add(1, new SBStats.Display(TextComponentUtils.formatted(name, ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), 0.0F));
    }

    private void sortStatsByValue(List<SBStats.Display> list, String name)
    {
        list.sort((stat1, stat2) -> new CompareToBuilder().append(stat2.getValue(), stat1.getValue()).build());
        list.add(0, new SBStats.Display(TextComponent.EMPTY, 0.0F));
        list.add(1, new SBStats.Display(TextComponentUtils.formatted(name, ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), 0.0F));
    }

    private long checkSkyBlockItem(List<ItemStack> list, String type)
    {
        return list.stream().filter(armor -> !armor.isEmpty() && armor.hasTag() && armor.getTag().getCompound("ExtraAttributes").getString("id").startsWith(type)).count();
    }

    private void getInventories(SkyblockProfiles.Members currentProfile)
    {
        this.armorItems.addAll(SBItemUtils.decodeItem(currentProfile.getArmorInventory(), InventoryType.ARMOR).stream().filter(Predicate.not(ItemStack::isEmpty)).collect(Collectors.toList()));

        if (this.armorItems.size() > 0)
        {
            for (var i = 0; i < this.armorItems.size(); ++i)
            {
                SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY.setItem(i, this.armorItems.get(i));
            }
        }

        var mainInventory = SBItemUtils.decodeItem(currentProfile.getMainInventory(), InventoryType.INVENTORY);
        var accessoryInventory = SBItemUtils.decodeItem(currentProfile.getAccessoryInventory(), InventoryType.ACCESSORY_BAG);
        var backpackInventory = Lists.<ItemStack>newArrayList();

        if (currentProfile.getBackpackInventory() != null)
        {
            for (var entry : currentProfile.getBackpackInventory().entrySet())
            {
                backpackInventory.addAll(SBItemUtils.decodeItem(entry.getValue(), InventoryType.BACKPACK));
            }
        }

        SKYBLOCK_INV.add(new SBInventoryGroup.Data(mainInventory, SBInventoryGroup.INVENTORY));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile.getEnderChestInventory(), InventoryType.ENDER_CHEST), SBInventoryGroup.ENDER_CHEST));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(backpackInventory, SBInventoryGroup.BACKPACK));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile.getVaultInventory(), InventoryType.PERSONAL_VAULT), SBInventoryGroup.PERSONAL_VAULT));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(accessoryInventory, SBInventoryGroup.ACCESSORY));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile.getPotionInventory(), InventoryType.POTION_BAG), SBInventoryGroup.POTION));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile.getFishingInventory(), InventoryType.FISHING_BAG), SBInventoryGroup.FISHING));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile.getQuiverInventory(), InventoryType.QUIVER), SBInventoryGroup.QUIVER));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile.getCandyInventory(), InventoryType.CANDY), SBInventoryGroup.CANDY));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile.getWardrobeInventory(), InventoryType.WARDROBE), SBInventoryGroup.WARDROBE));

        this.inventoryToStats.addAll(mainInventory);
        this.inventoryToStats.addAll(accessoryInventory);
    }

    private void getSlayerInfo(SkyblockProfiles.Members currentProfile)
    {
        var slayerBosses = currentProfile.getSlayerBoss();
        var slayerQuest = currentProfile.getSlayerQuest();

        if (slayerQuest != null)
        {
            this.activeSlayerType = slayerQuest.getType();
            this.activeSlayerTier = 1 + slayerQuest.tier();
        }

        if (slayerBosses != null)
        {
            var zombie = this.getSlayer(slayerBosses, SBSlayers.Type.ZOMBIE);
            var spider = this.getSlayer(slayerBosses, SBSlayers.Type.SPIDER);
            var wolf = this.getSlayer(slayerBosses, SBSlayers.Type.WOLF);
            var enderman = this.getSlayer(slayerBosses, SBSlayers.Type.ENDERMAN);

            if (!zombie.isEmpty())
            {
                this.slayerInfo.addAll(zombie);
            }
            if (!spider.isEmpty())
            {
                this.slayerInfo.addAll(spider);
            }
            if (!wolf.isEmpty())
            {
                this.slayerInfo.addAll(wolf);
            }
            if (!enderman.isEmpty())
            {
                this.slayerInfo.addAll(enderman);
            }
        }
        if (this.slayerInfo.isEmpty())
        {
            this.data.setHasSlayers(false);
        }
    }

    private void createFakePlayer()
    {
        if (this.minecraft.getConnection().getPlayerInfo(this.profile.getName()) == null)
        {
            var playerUpdate = new ClientboundPlayerInfoPacket.PlayerUpdate(this.profile, 0, null, null);
            this.minecraft.getConnection().playerInfoMap.put(this.profile.getId(), ((IViewerLoader) new PlayerInfo(playerUpdate)).setLoadedFromViewer(true)); // hack into map to show their skin :D
        }

        this.player = new SBFakePlayerEntity(this.minecraft.level, this.profile);
        SkyBlockAPIViewerScreen.renderSecondLayer = true;

        for (var armor : this.armorItems)
        {
            if (armor.isEmpty())
            {
                continue;
            }

            var index = Mob.getEquipmentSlotForItem(armor).getIndex();

            if (armor.getItem() instanceof BlockItem)
            {
                index = 3;
            }
            this.player.setItemSlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, index), armor);
        }
    }

    private List<SkyBlockSlayerInfo> getSlayer(JsonElement element, SBSlayers.Type type)
    {
        var list = Lists.<SkyBlockSlayerInfo>newArrayList();
        var lowerType = type.name().toLowerCase(Locale.ROOT);
        var slayer = element.getAsJsonObject().get(type.name().toLowerCase(Locale.ROOT));

        if (slayer != null)
        {
            var xp = slayer.getAsJsonObject().get("xp");

            if (xp != null)
            {
                var playerSlayerXp = xp.getAsInt();
                var xpRequired = 0;
                var slayerLvl = 0;
                var levelToCheck = 0;
                var xpToNextLvl = 0;
                var maxLevel = SBSlayers.SLAYERS.leveling().get(lowerType).length;
                var reachLimit = false;

                for (var i = 0; i < maxLevel; i++)
                {
                    var slayerXp = SBSlayers.SLAYERS.leveling().get(lowerType)[i];

                    if (slayerXp <= playerSlayerXp)
                    {
                        levelToCheck = i + 1;

                        if (levelToCheck < maxLevel)
                        {
                            xpRequired = SBSlayers.SLAYERS.leveling().get(lowerType)[levelToCheck];
                        }
                        ++slayerLvl;
                    }
                    if (slayerLvl == 0)
                    {
                        xpRequired = SBSlayers.SLAYERS.leveling().get(lowerType)[0];
                    }
                }

                if (levelToCheck < maxLevel)
                {
                    levelToCheck += 1;
                    xpToNextLvl = xpRequired - playerSlayerXp;
                }
                else
                {
                    levelToCheck = maxLevel;
                    reachLimit = true;
                }

                this.setSlayerSkillLevel(type, slayerLvl);

                list.add(new SkyBlockSlayerInfo(ChatFormatting.GRAY + type.getName() + " Slayer: " + (reachLimit ? ChatFormatting.GOLD : ChatFormatting.YELLOW) + "LVL " + slayerLvl));
                list.add(new SkyBlockSlayerInfo(ChatFormatting.GRAY + "EXP: " + ChatFormatting.LIGHT_PURPLE + (xpToNextLvl == 0 ? NumberUtils.NUMBER_FORMAT.format(playerSlayerXp) : NumberUtils.NUMBER_FORMAT.format(playerSlayerXp) + ChatFormatting.DARK_PURPLE + "/" + ChatFormatting.LIGHT_PURPLE + NumberUtils.NUMBER_FORMAT.format(xpRequired))));

                if (xpToNextLvl != 0)
                {
                    list.add(new SkyBlockSlayerInfo(ChatFormatting.GRAY + "XP to " + ChatFormatting.YELLOW + "LVL " + levelToCheck + ": " + ChatFormatting.LIGHT_PURPLE + NumberUtils.NUMBER_FORMAT.format(xpToNextLvl)));
                }

                list.add(SkyBlockSlayerInfo.createMobAndXp(type.getName(), playerSlayerXp + "," + xpRequired + "," + xpToNextLvl, reachLimit));
                var amount = 0;

                for (var i = 1; i <= 5; i++)
                {
                    var kill = slayer.getAsJsonObject().get("boss_kills_tier_" + (i - 1));
                    var kills = kill != null ? kill.getAsInt() : 0;
                    amount += kills * SBSlayers.SLAYERS.price().get(i - 1);
                    list.add(new SkyBlockSlayerInfo(ChatFormatting.GRAY + "Tier " + i + ": " + ChatFormatting.YELLOW + this.formatSlayerKill(kills)));
                }
                this.slayerTotalAmountSpent += amount;
                this.totalSlayerXp += playerSlayerXp;
                list.add(new SkyBlockSlayerInfo(ChatFormatting.GRAY + "Amount Spent: " + ChatFormatting.YELLOW + NumberUtils.NUMBER_FORMAT.format(amount)));
                list.add(SkyBlockSlayerInfo.empty());
                return list;
            }
        }
        return Collections.emptyList();
    }

    private void setSlayerSkillLevel(SBSlayers.Type type, int currentLevel)
    {
        switch (type)
        {
            case ZOMBIE -> this.zombieSlayerLevel = currentLevel;
            case SPIDER -> this.spiderSlayerLevel = currentLevel;
            case WOLF -> this.wolfSlayerLevel = currentLevel;
            case ENDERMAN -> this.endermanSlayerLevel = currentLevel;
        }
    }

    private String formatSlayerKill(int kills)
    {
        return NumberUtils.NUMBER_FORMAT.format(kills) + " kill" + (kills <= 1 ? "" : "s");
    }

    private String replaceId(String id)
    {
        for (var sbItem : SBConstants.SKYBLOCK_ITEM_ID_REMAP.entrySet())
        {
            var sbItemId = sbItem.getKey();

            if (id.contains(sbItemId))
            {
                id = id.replace(sbItemId, sbItem.getValue());
            }
        }
        return id;
    }

    private void setShowArmor()
    {
        if (this.showArmor)
        {
            for (var i = 0; i < this.player.getInventory().armor.size(); i++)
            {
                this.player.setItemSlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, i), ItemStack.EMPTY);
            }
            this.showArmorButton.setName(TextComponentUtils.component("Show Armor: " + ChatFormatting.RED + "OFF"));
            this.showArmor = false;
        }
        else
        {
            this.setPlayerArmors();
            this.showArmorButton.setName(TextComponentUtils.component("Show Armor: " + ChatFormatting.GREEN + "ON"));
            this.showArmor = true;
        }
    }

    private void setPlayerArmors()
    {
        for (var armor : this.armorItems.stream().filter(Predicate.not(ItemStack::isEmpty)).collect(Collectors.toList()))
        {
            try
            {
                var type = Mob.getEquipmentSlotForItem(armor);
                this.player.setItemSlot(type, armor);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void renderEntity(int posX, int posY, LivingEntity entity)
    {
        renderEntity(posX, posY, entity, 40.0F);
    }

    private static void renderEntity(int posX, int posY, LivingEntity entity, float scale)
    {
        var poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.translate(posX, posY, 1050.0D);
        poseStack.scale(1.0F, 1.0F, -1.0F);
        var poseStack1 = new PoseStack();
        poseStack1.translate(0.0D, 0.0D, 1000.0D);
        poseStack1.scale(scale, scale, scale);
        var quaternion = Vector3f.ZP.rotationDegrees(-180.0F);
        var quaternion1 = Vector3f.XP.rotationDegrees(-10.0F);
        var quaternion2 = Vector3f.YP.rotationDegrees(-190.0F);
        quaternion.mul(quaternion1);
        poseStack1.mulPose(quaternion);
        poseStack1.mulPose(quaternion2);
        entity.setYRot((float) (Math.atan(0) * 40.0F));
        entity.yHeadRot = entity.getYRot();
        var entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderermanager.overrideCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        var irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> entityrenderermanager.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, poseStack1, irendertypebuffer$impl, 15728880));
        irendertypebuffer$impl.endBatch();
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
        entityrenderermanager.setRenderShadow(true);
    }

    private static void renderEntity(int posX, int posY, float mouseX, float mouseY, LivingEntity livingEntity)
    {
        var f = (float) Math.atan(mouseX / 40.0F);
        var f1 = (float) Math.atan(mouseY / 40.0F);
        var poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.translate(posX, posY, 1050.0D);
        poseStack.scale(1.0F, 1.0F, -1.0F);
        var poseStack1 = new PoseStack();
        poseStack1.translate(0.0D, 0.0D, 1000.0D);
        var scale = 40F;
        poseStack1.scale(scale, scale, scale);
        var quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        var quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.mul(quaternion1);
        poseStack1.mulPose(quaternion);
        var f2 = livingEntity.yBodyRot;
        var f3 = livingEntity.getYRot();
        var f4 = livingEntity.getXRot();
        var f5 = livingEntity.yHeadRotO;
        var f6 = livingEntity.yHeadRot;
        livingEntity.yBodyRot = 180.0F + f * 20.0F;
        livingEntity.setYRot(180.0F + f * 40.0F);
        livingEntity.setXRot(-f1 * 20.0F);
        livingEntity.yHeadRot = livingEntity.getYRot();
        livingEntity.yHeadRotO = livingEntity.getYRot();
        var entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderermanager.overrideCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        var irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> entityrenderermanager.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, poseStack1, irendertypebuffer$impl, 15728880));
        irendertypebuffer$impl.endBatch();
        entityrenderermanager.setRenderShadow(true);
        livingEntity.yBodyRot = f2;
        livingEntity.setYRot(f3);
        livingEntity.setXRot(f4);
        livingEntity.yHeadRotO = f5;
        livingEntity.yHeadRot = f6;
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    private void drawItemStackSlot(PoseStack poseStack, int x, int y, ItemStack itemStack)
    {
        this.drawSprite(poseStack, x + 1, y + 1);
        this.itemRenderer.renderGuiItem(itemStack, x + 2, y + 2);
    }

    private void drawSprite(PoseStack poseStack, int left, int top)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GuiComponent.STATS_ICON_LOCATION);
        GuiComponent.blit(poseStack, left, top, this.getBlitOffset(), 0, 0, 18, 18, 128, 128);
    }

    private String getTextPercentage(int current, int total)
    {
        var size = 16;

        if (current > total)
        {
            throw new IllegalArgumentException();
        }

        var donePercents = 100 * current / total;
        var doneLength = size * donePercents / 100;
        var builder = new StringBuilder();

        for (var i = 0; i < size; i++)
        {
            builder.append(i < doneLength ? ChatFormatting.DARK_GREEN + "-" + ChatFormatting.WHITE : ChatFormatting.WHITE + "-");
        }
        return builder.toString();
    }

    static class ArmorContainer extends AbstractContainerMenu
    {
        ArmorContainer(boolean info)
        {
            super(null, 0);
            var x = info ? -62 : -52;
            this.addSlot(new Slot(SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY, 0, x, 75)); // boots
            this.addSlot(new Slot(SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY, 1, x, 56));
            this.addSlot(new Slot(SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY, 2, x, 36));
            this.addSlot(new Slot(SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY, 3, x, 12)); // helmet
        }

        @Override
        public boolean stillValid(Player player)
        {
            return false;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index)
        {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot)
        {
            return false;
        }

        @Override
        public boolean canDragTo(Slot slot)
        {
            return false;
        }
    }

    static class SkyBlockContainer extends AbstractContainerMenu
    {
        public final NonNullList<ItemStack> itemList = NonNullList.create();

        SkyBlockContainer()
        {
            super(null, 0);

            for (var columns = 0; columns < 4; ++columns)
            {
                for (var rows = 0; rows < 9; ++rows)
                {
                    this.addSlot(new Slot(SkyBlockAPIViewerScreen.TEMP_INVENTORY, columns * 9 + rows, 12 + rows * 18, 18 + columns * 18));
                }
            }
            this.scrollTo(0.0F);
        }

        @Override
        public boolean stillValid(Player player)
        {
            return false;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index)
        {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot)
        {
            return false;
        }

        @Override
        public boolean canDragTo(Slot slot)
        {
            return false;
        }

        boolean canScroll()
        {
            return this.itemList.size() > SkyBlockAPIViewerScreen.SIZE;
        }

        void scrollTo(float scroll)
        {
            var i = (this.itemList.size() + 9 - 1) / 9 - 4;
            var j = (int) (scroll * i + 0.5D);

            if (j < 0)
            {
                j = 0;
            }

            for (var k = 0; k < 4; ++k)
            {
                for (var l = 0; l < 9; ++l)
                {
                    var i1 = l + (k + j) * 9;

                    if (i1 >= 0 && i1 < this.itemList.size())
                    {
                        SkyBlockAPIViewerScreen.TEMP_INVENTORY.setItem(l + k * 9, this.itemList.get(i1));
                    }
                    else
                    {
                        SkyBlockAPIViewerScreen.TEMP_INVENTORY.setItem(l + k * 9, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    static class SkyBlockInfo
    {
        final String title;
        final String value;
        final String hex;

        SkyBlockInfo(String title, String value)
        {
            this(title, value, null);
        }

        SkyBlockInfo(String title, String value, String hex)
        {
            this.title = title;
            this.value = value;
            this.hex = hex;
        }

        public String getValue()
        {
            if (this.title.equals("Last Updated"))
            {
                try
                {
                    return TimeUtils.getRelativeTime(Long.parseLong(this.value));
                }
                catch (Exception e)
                {
                    return this.value;
                }
            }
            return this.value;
        }
    }

    static class SkyBlockSlayerInfo
    {
        final String text;
        String xp;
        boolean reachLimit;
        Type type = Type.TEXT;

        SkyBlockSlayerInfo(String text)
        {
            this.text = text;
        }

        SkyBlockSlayerInfo(String text, String xp, Type type, boolean reachLimit)
        {
            this(text);
            this.xp = xp;
            this.type = type;
            this.reachLimit = reachLimit;
        }

        static SkyBlockSlayerInfo createMobAndXp(String slayerType, String xp, boolean reachLimit)
        {
            return new SkyBlockSlayerInfo(slayerType, xp, Type.XP_AND_MOB, reachLimit);
        }

        static SkyBlockSlayerInfo empty()
        {
            return new SkyBlockSlayerInfo("");
        }

        enum Type
        {
            TEXT,
            XP_AND_MOB
        }
    }

    static class EmptyList extends ScrollingListScreen
    {
        final Type type;

        EmptyList(int width, int height, int top, int bottom, int left, int slotHeight, Type type)
        {
            super(width, height, top, bottom, left, slotHeight);
            this.type = type;
        }

        @Override
        protected void drawPanel(PoseStack poseStack, int index, int left, int right, int top) {}

        enum Type
        {
            INVENTORY,
            SKILL,
            DUNGEON
        }
    }

    class InfosList extends ScrollingListScreen
    {
        private final List<SkyBlockInfo> stats;

        InfosList(int width, int height, int top, int bottom, int left, int slotHeight, List<SkyBlockInfo> stats)
        {
            super(width, height, top, bottom, left, slotHeight);
            this.stats = stats;
        }

        @Override
        protected int getSize()
        {
            return this.stats.size();
        }

        @Override
        protected void drawPanel(PoseStack poseStack, int index, int left, int right, int top)
        {
            var stat = this.stats.get(index);
            var isCurrentUpgrade = stat.title.equals("Current Upgrade");
            this.font.draw(poseStack, stat.title + (isCurrentUpgrade ? SkyBlockProfileSelectorScreen.downloadingStates[(int) (Util.getMillis() / 250L % SkyBlockProfileSelectorScreen.downloadingStates.length)] : ""), SkyBlockAPIViewerScreen.this.guiLeft - 20, top, stat.hex != null ? ColorUtils.hexToDecimal(stat.hex) : index % 2 == 0 ? 16777215 : 9474192);
            this.font.draw(poseStack, stat.getValue(), SkyBlockAPIViewerScreen.this.guiLeft - this.font.width(stat.getValue()) + 195, top, stat.hex != null ? ColorUtils.hexToDecimal(stat.hex) : index % 2 == 0 ? 16777215 : 9474192);
        }
    }

    class SlayersList extends ScrollingListScreen
    {
        private final List<SkyBlockSlayerInfo> stats;

        SlayersList(int width, int height, int top, int bottom, int left, int slotHeight, List<SkyBlockSlayerInfo> stats)
        {
            super(width, height, top, bottom, left, slotHeight);
            this.stats = stats;
            this.headerHeight = 16;
        }

        @Override
        protected int getSize()
        {
            return this.stats.size();
        }

        @Override
        protected void drawPanel(PoseStack poseStack, int index, int left, int right, int top)
        {
            var stat = this.stats.get(index);

            if (stat.type == SkyBlockSlayerInfo.Type.XP_AND_MOB)
            {
                switch (stat.text)
                {
                    case "Zombie" -> {
                        Zombie zombie = new Zombie(this.world);
                        ItemStack heldItem = new ItemStack(Items.DIAMOND_HOE);
                        heldItem.enchant(Enchantments.UNBREAKING, 1);
                        ItemStack helmet = ItemUtils.getSkullItemStack(SkyBlockAPIViewerScreen.REVENANT_HORROR_HEAD[0], SkyBlockAPIViewerScreen.REVENANT_HORROR_HEAD[1]);
                        ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
                        chestplate.enchant(Enchantments.UNBREAKING, 1);
                        ItemStack leggings = new ItemStack(Items.CHAINMAIL_LEGGINGS);
                        leggings.enchant(Enchantments.UNBREAKING, 1);
                        ItemStack boots = new ItemStack(Items.DIAMOND_BOOTS);
                        zombie.setItemSlot(EquipmentSlot.HEAD, helmet);
                        zombie.setItemSlot(EquipmentSlot.CHEST, chestplate);
                        zombie.setItemSlot(EquipmentSlot.LEGS, leggings);
                        zombie.setItemSlot(EquipmentSlot.FEET, boots);
                        zombie.setItemSlot(EquipmentSlot.MAINHAND, heldItem);
                        zombie.tickCount = LibClientProxy.ticks;
                        SkyBlockAPIViewerScreen.renderEntity(SkyBlockAPIViewerScreen.this.guiLeft - 30, top + 60, zombie);
                    }
                    case "Spider" -> {
                        Spider spider = new Spider(EntityType.SPIDER, this.world);
                        CaveSpider cave = new CaveSpider(EntityType.CAVE_SPIDER, this.world);
                        SkyBlockAPIViewerScreen.renderEntity(SkyBlockAPIViewerScreen.this.guiLeft - 30, top + 40, cave);
                        SkyBlockAPIViewerScreen.renderEntity(SkyBlockAPIViewerScreen.this.guiLeft - 30, top + 60, spider);
                        RenderSystem.blendFunc(770, 771);
                    }
                    case "Wolf" -> {
                        Wolf wolf = new Wolf(EntityType.WOLF, this.world);
                        wolf.setRemainingPersistentAngerTime(Integer.MAX_VALUE);
                        SkyBlockAPIViewerScreen.renderEntity(SkyBlockAPIViewerScreen.this.guiLeft - 30, top + 60, wolf);
                    }
                    case "Enderman" -> {
                        EnderMan enderman = new EnderMan(EntityType.ENDERMAN, this.world);
                        enderman.setRemainingPersistentAngerTime(Integer.MAX_VALUE);
                        SkyBlockAPIViewerScreen.renderEntity(SkyBlockAPIViewerScreen.this.guiLeft - 30, top + 60, enderman, 30.0F);
                    }
                }

                var color = ColorUtils.toFloatArray(0, 255, 255);
                var reachLimit = stat.reachLimit;

                if (reachLimit)
                {
                    color = ColorUtils.toFloatArray(255, 185, 0);
                }

                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderColor(color[0], color[1], color[2], 1.0F);
                RenderSystem.setShaderTexture(0, XP_BARS);
                RenderSystem.disableBlend();

                var xpSplit = stat.xp.split(",");
                var playerSlayerXp = Integer.parseInt(xpSplit[0]);
                var xpRequired = Integer.parseInt(xpSplit[1]);
                var filled = stat.reachLimit ? 91 : Math.min((int) Math.floor(playerSlayerXp * 92F / xpRequired), 91);
                GuiComponent.blit(poseStack, SkyBlockAPIViewerScreen.this.guiLeft + 90, top, 0, 0, 91, 5, 91, 10);

                if (filled > 0)
                {
                    GuiComponent.blit(poseStack, SkyBlockAPIViewerScreen.this.guiLeft + 90, top, 0, 5, filled, 5, 91, 10);
                }

                RenderSystem.enableBlend();
            }
            else
            {
                if (this.getSize() == 1)
                {
                    this.font.draw(poseStack, stat.text, SkyBlockAPIViewerScreen.this.guiLeft + 200, top, 16777215);
                }
                else
                {
                    this.font.draw(poseStack, stat.text, SkyBlockAPIViewerScreen.this.guiLeft - this.font.width(stat.text) + 180, top, 16777215);
                }
            }
        }
    }

    class OthersList extends ScrollingListScreen
    {
        private final List<?> stats;

        OthersList(int width, int height, int top, int bottom, int left, int slotHeight, List<?> stats)
        {
            super(width, height, top, bottom, left, slotHeight);
            this.stats = stats;
        }

        @Override
        protected int getSize()
        {
            return this.stats.size();
        }

        @Override
        protected void drawPanel(PoseStack poseStack, int index, int left, int right, int top)
        {
            if (!this.stats.isEmpty())
            {
                var obj = this.stats.get(index);

                if (obj instanceof SBStats.Display stat)
                {
                    var component = stat.getName().copy();

                    if (this.font.width(component) > 200)
                    {
                        component.setStyle(component.getStyle().withFont(ClientUtils.UNICODE));
                    }

                    this.font.draw(poseStack, component, SkyBlockAPIViewerScreen.this.guiLeft - 85, top, index % 2 == 0 ? 16777215 : 9474192);
                    this.font.draw(poseStack, stat.getValueByString(), SkyBlockAPIViewerScreen.this.guiLeft - this.font.width(stat.getValueByString()) + 180, top, index % 2 == 0 ? 16777215 : 9474192);
                }
                else if (obj instanceof BankHistory.Stats stat)
                {
                    this.font.draw(poseStack, stat.stats(), SkyBlockAPIViewerScreen.this.guiLeft - 55, top, 16777215);
                }
            }
        }
    }

    static class CollectionsList extends ScrollingListScreen
    {
        private final List<SBCollections> collection;
        private final SkyBlockAPIViewerScreen parent;

        CollectionsList(SkyBlockAPIViewerScreen parent, int width, int height, int top, int bottom, int left, int slotHeight, List<SBCollections> collection)
        {
            super(width, height, top, bottom, left, slotHeight);
            this.collection = collection;
            this.parent = parent;
        }

        @Override
        protected int getSize()
        {
            return this.collection.size();
        }

        @Override
        protected void drawPanel(PoseStack poseStack, int index, int left, int right, int top)
        {
            var collection = this.collection.get(index);

            if (collection.type() != null)
            {
                if (!collection.itemStack().isEmpty())
                {
                    var collectionLvl = collection.type() == SBCollections.Type.UNKNOWN ? "" : " " + ChatFormatting.GOLD + collection.level();
                    this.parent.drawItemStackSlot(poseStack, this.parent.guiLeft - 65, top, collection.itemStack());
                    this.font.draw(poseStack, (collection.type() == SBCollections.Type.UNKNOWN ? ChatFormatting.RED : "") + collection.itemStack().getHoverName().getString() + collectionLvl, this.parent.guiLeft - 41, top + 6, 16777215);
                    this.font.draw(poseStack, collection.getCollectionAmount(), this.parent.guiLeft - this.font.width(collection.getCollectionAmount()) + 170, top + 6, index % 2 == 0 ? 16777215 : 9474192);
                }
                else
                {
                    this.font.draw(poseStack, collection.type().getName(), this.parent.guiLeft - 65, top + 5, 16777215);
                }
            }
        }
    }

    static class CraftedMinionsList extends ScrollingListScreen
    {
        private final List<SBMinions.CraftedInfo> craftMinions;
        private final SkyBlockAPIViewerScreen parent;

        CraftedMinionsList(SkyBlockAPIViewerScreen parent, int width, int height, int top, int bottom, int left, int slotHeight, List<SBMinions.CraftedInfo> craftMinions)
        {
            super(width, height, top, bottom, left, slotHeight);
            this.craftMinions = craftMinions;
            this.parent = parent;
        }

        @Override
        protected int getSize()
        {
            return this.craftMinions.size();
        }

        @Override
        protected void drawPanel(PoseStack poseStack, int index, int left, int right, int top)
        {
            var craftedMinion = this.craftMinions.get(index);

            if (!craftedMinion.minionItem().isEmpty())
            {
                this.parent.drawItemStackSlot(poseStack, this.parent.guiLeft - 102, top, craftedMinion.minionItem());
                this.font.draw(poseStack, craftedMinion.displayName() + " " + ChatFormatting.GOLD + craftedMinion.minionMaxTier(), this.parent.guiLeft - 79, top + 6, 16777215);
                this.font.draw(poseStack, craftedMinion.craftedTiers(), this.parent.guiLeft - this.font.width(craftedMinion.craftedTiers()) + 202, top + 6, index % 2 == 0 ? 16777215 : 9474192);
            }
            else
            {
                if (craftedMinion.minionName() != null)
                {
                    this.font.draw(poseStack, craftedMinion.minionName(), this.parent.guiLeft - 100, top + 5, 16777215);
                }
            }
        }
    }

    enum ViewButton
    {
        PLAYER,
        SKILLS,
        SLAYERS,
        DUNGEONS,
        OTHERS;

        protected static final ViewButton[] VALUES = ViewButton.values();
        Button button;
    }

    enum OthersViewButton
    {
        KILLS,
        DEATHS,
        OTHER_STATS,
        BANK_HISTORY;

        protected static final OthersViewButton[] VALUES = OthersViewButton.values();
        Button button;
    }

    enum BasicInfoViewButton
    {
        PLAYER_STATS,
        INVENTORY,
        COLLECTIONS,
        CRAFTED_MINIONS;

        protected static final BasicInfoViewButton[] VALUES = BasicInfoViewButton.values();
        Button button;
    }
}