package com.stevekung.skyblockcatia.gui.screen;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.lwjgl.glfw.GLFW;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.gui.APIErrorInfo;
import com.stevekung.skyblockcatia.gui.ScrollingListScreen;
import com.stevekung.skyblockcatia.gui.widget.button.ItemButton;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.mixin.InvokerClientPacketListener;
import com.stevekung.skyblockcatia.mixin.InvokerItemRenderer;
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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

@SuppressWarnings("deprecation")
public class SkyBlockAPIViewerScreen extends Screen
{
    private static final ResourceLocation INVENTORY_TABS = new ResourceLocation("skyblockcatia:textures/gui/groups.png");
    private static final ResourceLocation XP_BARS = new ResourceLocation("skyblockcatia:textures/gui/skill_xp_bar.png");
    private static final String[] REVENANT_HORROR_HEAD = new String[]{"0862e0b0-a14f-3f93-894f-013502936b59", "dbad99ed3c820b7978190ad08a934a68dfa90d9986825da1c97f6f21f49ad626"};

    // Based stuff
    private boolean firstLoad;
    private boolean loadingApi = true;
    private boolean error;
    private String errorMessage;
    private String statusMessage;
    private Button doneButton;
    private Button backButton;
    private ItemButton showArmorButton;
    private JsonObject skyblockProfiles;
    private final List<ProfileDataCallback> profiles;
    private final String sbProfileId;
    private final Component sbProfileName;
    private final String username;
    private final String displayName;
    private final Component gameMode;
    private final String guild;
    private final String uuid;
    private final GameProfile profile;
    private final StopWatch watch = new StopWatch();
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
    private static final Pattern STATS_PATTERN = Pattern.compile("(?<type>Strength|Crit Chance|Crit Damage|Health|Defense|Speed|Intelligence|True Defense|Sea Creature Chance|Magic Find|Pet Luck|Bonus Attack Speed|Ferocity|Ability Damage|Mining Speed|Mining Fortune|Farming Fortune|Foraging Fortune): (?<value>(?:\\+|\\-)[0-9,.]+)?(?:\\%){0,1}(?:(?: HP(?: \\((?:\\+|\\-)[0-9,.]+ HP\\)){0,1}(?: \\(\\w+ (?:\\+|\\-)[0-9,.]+ HP\\)){0,1})|(?: \\((?:\\+|\\-)[0-9,.]+\\))|(?: \\(\\w+ (?:\\+|\\-)[0-9,.]+(?:\\%){0,1}\\))){0,1}(?: \\((?:\\+|\\-)[0-9,.]+ HP\\)){0,1}");

    public static boolean renderSecondLayer;
    private final List<SkyBlockInfo> infoList = Lists.newArrayList();
    private final List<SBSkills.Info> skillLeftList = Lists.newArrayList();
    private final List<SBSkills.Info> skillRightList = Lists.newArrayList();
    private final List<SkyBlockSlayerInfo> slayerInfo = Lists.newArrayList();
    private final List<SBStats> sbKills = Lists.newArrayList();
    private final List<SBStats> sbDeaths = Lists.newArrayList();
    private final List<SBStats> sbOthers = Lists.newArrayList();
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
        this.addButton(this.doneButton = new Button(this.width / 2 - 154, this.height - 25, 150, 20, LangUtils.translate("gui.close"), button -> this.minecraft.setScreen(this.error ? new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.SEARCH, this.username, this.displayName, this.guild, this.profiles) : null)));
        this.addButton(this.backButton = new Button(this.width / 2 + 4, this.height - 25, 150, 20, CommonComponents.GUI_BACK, button -> this.minecraft.setScreen(this.profiles.size() == 0 ? new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.EMPTY, this.username, this.displayName, "") : new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.SEARCH, this.username, this.displayName, this.guild, this.profiles))));
        this.addButton(this.showArmorButton = new ItemButton(this.width / 2 - 115, this.height / 2 - 65, Items.DIAMOND_CHESTPLATE, TextComponentUtils.component("Show Armor: " + ChatFormatting.GREEN + "ON"), button -> this.setShowArmor()));
        Button infoButton = ViewButton.PLAYER.button = new Button(this.width / 2 - 197, 6, 70, 20, LangUtils.translate("gui.sb_view_player"), button -> this.performedInfo(ViewButton.PLAYER));
        infoButton.active = false;
        this.addButton(infoButton);
        this.addButton(ViewButton.SKILLS.button = new Button(this.width / 2 - 116, 6, 70, 20, LangUtils.translate("gui.sb_view_skills"), button -> this.performedInfo(ViewButton.SKILLS)));
        this.addButton(ViewButton.SLAYERS.button = new Button(this.width / 2 - 35, 6, 70, 20, LangUtils.translate("gui.sb_view_slayers"), button -> this.performedInfo(ViewButton.SLAYERS)));
        this.addButton(ViewButton.DUNGEONS.button = new Button(this.width / 2 + 45, 6, 70, 20, LangUtils.translate("gui.sb_view_dungeons"), button -> this.performedInfo(ViewButton.DUNGEONS)));
        this.addButton(ViewButton.OTHERS.button = new Button(this.width / 2 + 126, 6, 70, 20, LangUtils.translate("gui.sb_view_others"), button -> this.performedInfo(ViewButton.OTHERS)));

        Button statKillsButton = OthersViewButton.KILLS.button = new Button(this.width / 2 - 170, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.kills"), button -> this.performedOthers(OthersViewButton.KILLS));
        statKillsButton.active = false;
        this.addButton(statKillsButton);
        this.addButton(OthersViewButton.DEATHS.button = new Button(this.width / 2 - 84, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.deaths"), button -> this.performedOthers(OthersViewButton.DEATHS)));
        this.addButton(OthersViewButton.OTHER_STATS.button = new Button(this.width / 2 + 4, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.others_stats"), button -> this.performedOthers(OthersViewButton.OTHER_STATS)));
        this.addButton(OthersViewButton.BANK_HISTORY.button = new Button(this.width / 2 + 90, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.bank_history"), button -> this.performedOthers(OthersViewButton.BANK_HISTORY)));

        Button basicInfoButton = BasicInfoViewButton.PLAYER_STATS.button = new Button(this.width / 2 - 170, this.height - 48, 80, 20, LangUtils.translate("gui.sb_player_stats"), button -> this.performedBasicInfo(BasicInfoViewButton.PLAYER_STATS));
        basicInfoButton.active = false;
        this.addButton(basicInfoButton);
        this.addButton(BasicInfoViewButton.INVENTORY.button = new Button(this.width / 2 - 84, this.height - 48, 80, 20, LangUtils.translate("gui.sb_inventory"), button -> this.performedBasicInfo(BasicInfoViewButton.INVENTORY)));
        this.addButton(BasicInfoViewButton.COLLECTIONS.button = new Button(this.width / 2 + 4, this.height - 48, 80, 20, LangUtils.translate("gui.sb_collections"), button -> this.performedBasicInfo(BasicInfoViewButton.COLLECTIONS)));
        this.addButton(BasicInfoViewButton.CRAFTED_MINIONS.button = new Button(this.width / 2 + 90, this.height - 48, 80, 20, LangUtils.translate("gui.sb_crafted_minions"), button -> this.performedBasicInfo(BasicInfoViewButton.CRAFTED_MINIONS)));

        if (this.firstLoad)
        {
            this.firstLoad = false;

            CommonUtils.runAsync(() ->
            {
                try
                {
                    this.watch.start();
                    this.getPlayerData();
                    this.watch.stop();

                    if (this.skyblockProfiles == null)
                    {
                        SkyBlockcatiaMod.LOGGER.info("API Download finished in: {}ms", this.watch.getTime());
                    }

                    this.watch.reset();
                }
                catch (Throwable e)
                {
                    this.errorList.add(TextComponentUtils.formatted(e.getClass().getName() + ": " + e.getMessage(), ChatFormatting.RED, ChatFormatting.UNDERLINE, ChatFormatting.BOLD));

                    for (StackTraceElement stack : e.getStackTrace())
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

        int i = this.selectedTabIndex;
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
        this.minecraft.getConnection().getOnlinePlayers().removeIf(network -> ((IViewerLoader)network).isLoadedFromViewer());
        SkyBlockAPIViewerScreen.renderSecondLayer = false;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers)
    {
        if (key == KeyBindingHandler.KEY_SB_VIEW_RECIPE.getDefaultKey().getValue())
        {
            if (this.hoveredSlot != null && this.hoveredSlot.hasItem() && this.hoveredSlot.getItem().hasTag())
            {
                CompoundTag extraAttrib = this.hoveredSlot.getItem().getTag().getCompound("ExtraAttributes");

                if (extraAttrib.contains("id"))
                {
                    String itemId = extraAttrib.getString("id");

                    /*if (SkyBlockcatiaMod.isSkyblockAddonsLoaded && SkyBlockAddonsBackpack.INSTANCE.isFreezeBackpack()) TODO
                    {
                        return false;
                    }*/
                    ClientUtils.printClientMessage(TextComponentUtils.component("Click to view ").append(this.hoveredSlot.getItem().getHoverName().copy().withStyle(ChatFormatting.GOLD).append(TextComponentUtils.formatted(" recipe", ChatFormatting.GREEN))).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewrecipe " + itemId)).applyFormat(ChatFormatting.GREEN)));
                }
            }
        }
        else if (key == GLFW.GLFW_KEY_F5)
        {
            this.skyblockProfiles = null;
            this.minecraft.setScreen(new SkyBlockAPIViewerScreen(this.profiles, new ProfileDataCallback(this.sbProfileId, this.sbProfileName, this.username, this.displayName, this.gameMode, this.guild, this.uuid, this.profile, -1)));
        }
        /*if (SkyBlockcatiaMod.isSkyblockAddonsLoaded) TODO
        {
            SkyBlockAddonsBackpack.INSTANCE.keyTyped(keyCode);
        }*/
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
            if (state == 0 && this.currentSlot != null && this.currentSlot instanceof EmptyList && ((EmptyList)this.currentSlot).type == EmptyList.Type.INVENTORY)
            {
                double i = mouseX - this.guiLeft;
                double j = mouseY - this.guiTop;

                for (SBInventoryGroup group : SBInventoryGroup.GROUPS)
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
            if (state == 0 && this.currentSlot != null && this.currentSlot instanceof EmptyList && ((EmptyList)this.currentSlot).type == EmptyList.Type.INVENTORY)
            {
                double i = mouseX - this.guiLeft;
                double j = mouseY - this.guiTop;
                this.isScrolling = false;

                for (SBInventoryGroup group : SBInventoryGroup.GROUPS)
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
            if (this.isScrolling && this.currentSlot != null && this.currentSlot instanceof EmptyList && ((EmptyList)this.currentSlot).type == EmptyList.Type.INVENTORY)
            {
                int i = this.guiTop + 18;
                int j = i + 72;
                this.currentScroll = ((float)mouseY - i - 7.5F) / (j - i - 15.0F);
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
                if (this.currentSlot instanceof EmptyList && ((EmptyList)this.currentSlot).type == EmptyList.Type.INVENTORY)
                {
                    if (!this.needsScrollBars())
                    {
                        return false;
                    }
                    else
                    {
                        int i = (this.skyBlockContainer.itemList.size() + 9 - 1) / 9 - 4;
                        this.currentScroll = (float)(this.currentScroll - scrollDelta / i);
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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);

        if (this.loadingApi)
        {
            String text = "Downloading SkyBlock stats";
            int i = this.font.width(text);
            GuiComponent.drawCenteredString(matrixStack, this.font, text, this.width / 2, this.height / 2 + this.font.lineHeight * 2 - 35, 16777215);
            GuiComponent.drawString(matrixStack, this.font, SkyBlockProfileSelectorScreen.downloadingStates[(int)(Util.getMillis() / 500L % SkyBlockProfileSelectorScreen.downloadingStates.length)], this.width / 2 + i / 2, this.height / 2 + this.font.lineHeight * 2 - 35, 16777215);
            GuiComponent.drawCenteredString(matrixStack, this.font, "Status: " + ChatFormatting.GRAY + this.statusMessage, this.width / 2, this.height / 2 + this.font.lineHeight * 2 - 15, 16777215);

            if (this.showArmorButton != null)
            {
                this.showArmorButton.visible = false;
            }
        }
        else
        {
            if (this.error)
            {
                GuiComponent.drawCenteredString(matrixStack, this.font, "SkyBlock API Viewer", this.width / 2, 20, 16777215);

                if (this.errorInfo != null)
                {
                    this.errorInfo.render(matrixStack, mouseX, mouseY, partialTicks);
                }
                else
                {
                    GuiComponent.drawCenteredString(matrixStack, this.font, ChatFormatting.RED + this.errorMessage, this.width / 2, 100, 16777215);
                }
                if (this.showArmorButton != null)
                {
                    this.showArmorButton.visible = false;
                }
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }
            else
            {
                if (this.currentSlot != null)
                {
                    this.currentSlot.render(matrixStack, mouseX, mouseY, partialTicks);
                }

                GuiComponent.drawCenteredString(matrixStack, this.font, TextComponentUtils.component(this.displayName).append(TextComponentUtils.formatted(" Profile: ", ChatFormatting.YELLOW)).append(this.sbProfileName.copy().withStyle(ChatFormatting.GOLD)).append(TextComponentUtils.formatted(" Game Mode: ", ChatFormatting.YELLOW)).append(this.gameMode).append(this.guild), this.width / 2, 29, 16777215);

                if (this.currentSlot != null && this.currentSlot instanceof EmptyList)
                {
                    EmptyList stat = (EmptyList)this.currentSlot;

                    if (stat.type == EmptyList.Type.INVENTORY)
                    {
                        this.drawGroupsBackgroundLayer(matrixStack);
                    }
                }

                super.render(matrixStack, mouseX, mouseY, partialTicks);

                if (this.currentSlot != null)
                {
                    if (this.currentSlot instanceof InfosList)
                    {
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                        RenderSystem.enableDepthTest();
                        SkyBlockAPIViewerScreen.renderEntity(this.width / 2 - 106, this.height / 2 + 40, this.guiLeft - 55 - (float)mouseX, this.guiTop + 25 - (float)mouseY, this.player);
                        this.drawContainerSlot(matrixStack, mouseX, mouseY, true);

                        if (this.hoveredSlot != null && this.hoveredSlot.hasItem())
                        {
                            this.renderTooltip(matrixStack, this.hoveredSlot.getItem(), mouseX, mouseY);
                        }
                    }
                    else if (this.currentSlot instanceof EmptyList)
                    {
                        EmptyList stat = (EmptyList)this.currentSlot;

                        if (stat.type == EmptyList.Type.INVENTORY)
                        {
                            this.drawContainerSlot(matrixStack, mouseX, mouseY, false);

                            Lighting.turnOff();
                            this.drawTabsForegroundLayer(matrixStack);

                            for (SBInventoryGroup group : SBInventoryGroup.GROUPS)
                            {
                                if (this.renderGroupsHoveringText(matrixStack, group, mouseX, mouseY))
                                {
                                    break;
                                }
                            }

                            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                            RenderSystem.disableLighting();

                            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                            SkyBlockAPIViewerScreen.renderEntity(this.width / 2 - 96, this.height / 2 + 40, this.guiLeft - 46 - (float)mouseX, this.guiTop + 75 - 50 - (float)mouseY, this.player);

                            if (this.hoveredSlot != null && this.hoveredSlot.hasItem())
                            {
                                this.renderTooltip(matrixStack, this.hoveredSlot.getItem(), mouseX, mouseY);
                            }
                            /*if (SkyBlockcatiaMod.isSkyblockAddonsLoaded) TODO
                            {
                                SkyBlockAddonsBackpack.INSTANCE.drawBackpacks(this, mouseX, mouseY, partialTicks);
                            }*/
                        }
                        else if (stat.type == EmptyList.Type.DUNGEON)//TODO
                        {
                            int i = 0;

                            for (String dungeon : this.dungeonData)
                            {
                                int x = this.width / 2 - 150;
                                int y = 50;
                                int textY = y + 12 * i;
                                GuiComponent.drawString(matrixStack, this.font, dungeon, x, textY, -1);
                                ++i;
                            }
                        }
                        else
                        {
                            int i = 0;
                            int height = this.height / 7;

                            for (SBSkills.Info info : this.skillLeftList)
                            {
                                int x = this.width / 2 - 120;
                                int y = height + 12;
                                int barY = y + 20 + height * i;
                                int textY = y + height * i;
                                String extraCap = "";

                                if (info.getName().equals("Farming") && this.farmingLevelCap > 0)
                                {
                                    extraCap = ChatFormatting.GOLD + " (+" + this.farmingLevelCap + ")";
                                }

                                this.renderSkillBar(matrixStack, info.getName(), x, barY, x + 46, textY, info.getCurrentXp(), info.getXpRequired(), info.getCurrentLvl(), info.isReachLimit(), extraCap);
                                ++i;
                            }

                            i = 0;

                            for (SBSkills.Info info : this.skillRightList)
                            {
                                int x = this.width / 2 + 30;
                                int y = height + 12;
                                int barY = y + 20 + height * i;
                                int textY = y + height * i;
                                this.renderSkillBar(matrixStack, info.getName(), x, barY, x + 46, textY, info.getCurrentXp(), info.getXpRequired(), info.getCurrentLvl(), info.isReachLimit(), "");
                                ++i;
                            }

                            if (this.skillAvg != null)
                            {
                                MutableComponent component = TextComponentUtils.component("AVG: " + this.skillAvg);
                                component.setStyle(component.getStyle().withFont(ClientUtils.UNICODE));
                                GuiComponent.drawString(matrixStack, this.font, component, this.width - this.font.width(component) - 60, this.height - 38, 16777215);
                            }
                        }
                    }
                    else if (this.currentSlot instanceof SlayersList)
                    {
                        String total1 = ChatFormatting.GRAY + "Total Amount Spent: " + ChatFormatting.YELLOW + NumberUtils.NUMBER_FORMAT.format(this.slayerTotalAmountSpent);
                        String total2 = ChatFormatting.GRAY + "Total Slayer XP: " + ChatFormatting.YELLOW + NumberUtils.NUMBER_FORMAT.format(this.totalSlayerXp);
                        GuiComponent.drawString(matrixStack, this.font, total1, this.width - this.font.width(total1) - 60, this.height - 36, 16777215);
                        GuiComponent.drawString(matrixStack, this.font, total2, this.width - this.font.width(total2) - 60, this.height - 46, 16777215);

                        if (this.activeSlayerType != null)
                        {
                            GuiComponent.drawString(matrixStack, this.font, ChatFormatting.GRAY + "Active Slayer: ", 60, this.height - 46, 16777215);
                            GuiComponent.drawString(matrixStack, this.font, ChatFormatting.YELLOW + this.activeSlayerType.getName() + " - Tier " + this.activeSlayerTier, 60, this.height - 36, 16777215);
                        }
                    }
                    else if (this.currentSlot instanceof CraftedMinionsList)
                    {
                        String total1 = ChatFormatting.GRAY + "Unique Minions: " + ChatFormatting.YELLOW + this.craftedMinionCount + "/" + SBMinions.MINIONS.getUniqueMinions() + ChatFormatting.GRAY + " (" + this.craftedMinionCount * 100 / SBMinions.MINIONS.getUniqueMinions() + "%)";
                        String total2 = ChatFormatting.GRAY + "Current Minion Slot: " + ChatFormatting.YELLOW + this.currentMinionSlot + (this.additionalMinionSlot > 0 ? ChatFormatting.GOLD + " (Bonus +" + this.additionalMinionSlot + ")" : "");
                        GuiComponent.drawString(matrixStack, this.font, total1, this.width - this.font.width(total1) - 60, this.height - 68, 16777215);
                        GuiComponent.drawString(matrixStack, this.font, total2, this.width - this.font.width(total2) - 60, this.height - 58, 16777215);
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
            case PLAYER:
            default:
                this.currentSlot = new InfosList(this.width - 119, this.height, 40, this.height - 50, 59, 12, this.infoList);
                this.refreshBasicInfoViewButton(this.basicInfoButton);
                this.refreshOthersViewButton(this.othersButton, false);
                this.performedBasicInfo(this.basicInfoButton);
                break;
            case SKILLS:
                this.currentSlot = new EmptyList(this.width - 119, this.height, 40, this.height - 28, 59, 12, EmptyList.Type.SKILL);
                this.hideBasicInfoButton();
                this.hideOthersButton();
                break;
            case SLAYERS:
                this.currentSlot = new SlayersList(this.width - 119, this.height, 40, this.height - 50, 59, 16, this.slayerInfo);
                this.hideBasicInfoButton();
                this.hideOthersButton();
                break;
            case DUNGEONS:
                this.currentSlot = new EmptyList(this.width - 119, this.height, 40, this.height - 28, 59, 12, EmptyList.Type.DUNGEON);
                this.hideBasicInfoButton();
                this.hideOthersButton();
                break;
            case OTHERS:
                this.hideBasicInfoButton();
                this.performedOthers(this.othersButton);
                break;
        }
        this.refreshViewButton(viewButton);
    }

    private void performedBasicInfo(BasicInfoViewButton basicInfoButton)
    {
        switch (basicInfoButton)
        {
            case PLAYER_STATS:
            default:
                this.currentSlot = new InfosList(this.width - 119, this.height, 40, this.height - 50, 59, 12, this.infoList);
                this.showArmorButton.visible = true;
                this.showArmorButton.x = this.width / 2 - 114;
                this.skyBlockArmorContainer = new ArmorContainer(true);
                break;
            case INVENTORY:
                this.currentSlot = new EmptyList(this.width - 119, this.height, 40, this.height - 50, 59, 12, EmptyList.Type.INVENTORY);
                this.setCurrentGroup(SBInventoryGroup.GROUPS[this.selectedTabIndex]);
                this.showArmorButton.visible = true;
                this.showArmorButton.x = this.width / 2 - 104;
                this.skyBlockArmorContainer = new ArmorContainer(false);
                break;
            case COLLECTIONS:
                this.currentSlot = new CollectionsList(this, this.width - 119, this.height, 40, this.height - 50, 59, 20, this.collections);
                this.showArmorButton.visible = false;
                break;
            case CRAFTED_MINIONS:
                this.currentSlot = new CraftedMinionsList(this, this.width - 99, this.height, 40, this.height - 70, 49, 20, this.sbCraftedMinions);
                this.showArmorButton.visible = false;
                break;
        }
        this.refreshBasicInfoViewButton(basicInfoButton);
    }

    private void performedOthers(OthersViewButton othersButton)
    {
        List<?> list;

        switch (othersButton)
        {
            default:
            case KILLS:
                list = this.sbKills;
                break;
            case DEATHS:
                list = this.sbDeaths;
                break;
            case OTHER_STATS:
                list = this.sbOthers;
                break;
            case BANK_HISTORY:
                list = this.sbBankHistories;
                break;
        }
        this.currentSlot = new OthersList(this.width - 119, this.height, 40, this.height - 50, 59, 12, list);
        this.showArmorButton.visible = false;
        this.refreshOthersViewButton(othersButton, true);
    }

    private void refreshViewButton(ViewButton viewButton)
    {
        this.viewButton = viewButton;

        for (ViewButton view : ViewButton.VALUES)
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

        for (BasicInfoViewButton view : BasicInfoViewButton.VALUES)
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

        for (OthersViewButton view : OthersViewButton.VALUES)
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
        for (BasicInfoViewButton view : BasicInfoViewButton.VALUES)
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
        for (OthersViewButton view : OthersViewButton.VALUES)
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

        for (AbstractWidget button : this.buttons)
        {
            if (button != this.doneButton)
            {
                button.visible = false;
            }
        }
    }

    private boolean isMouseOverGroup(SBInventoryGroup group, double mouseX, double mouseY)
    {
        int i = group.getColumn();
        int j = 28 * i;
        int k = 0;

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
        int i = this.guiLeft;
        int j = this.guiTop;
        mouseX = mouseX - i;
        mouseY = mouseY - j;
        return mouseX >= x - 1 && mouseX < x + width + 1 && mouseY >= y - 1 && mouseY < y + height + 1;
    }

    private boolean isHoveredScroll(double mouseX, double mouseY)
    {
        int i = this.guiLeft;
        int j = this.guiTop;
        int k = i + 182;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 72;
        return mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1;
    }

    private void setCurrentGroup(SBInventoryGroup group)
    {
        if (group == null)
        {
            return;
        }
        /*if (SkyBlockcatiaMod.isSkyblockAddonsLoaded) TODO
        {
            SkyBlockAddonsBackpack.INSTANCE.clearRenderBackpack();
        }*/
        this.selectedTabIndex = group.getIndex();
        SkyBlockContainer container = this.skyBlockContainer;
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
    private void renderSkillBar(PoseStack matrixStack, String name, int xBar, int yBar, int xText, int yText, double playerXp, int xpRequired, int currentLvl, boolean reachLimit, String extra)
    {
        float[] color = ColorUtils.toFloatArray(128, 255, 0);

        if (reachLimit)
        {
            color = ColorUtils.toFloatArray(255, 185, 0);
        }

        this.minecraft.getTextureManager().bind(XP_BARS);
        RenderSystem.color4f(color[0], color[1], color[2], 1.0F);
        GuiComponent.blit(matrixStack, xBar, yBar, 0, 0, 91, 5, 91, 10);

        if (xpRequired > 0)
        {
            int filled = reachLimit ? 91 : Math.min((int)Math.floor(playerXp * 92 / xpRequired), 91);

            if (filled > 0)
            {
                GuiComponent.blit(matrixStack, xBar, yBar, 0, 5, filled, 5, 91, 10);
            }

            GuiComponent.drawCenteredString(matrixStack, this.font, ChatFormatting.GRAY + name + (reachLimit ? ChatFormatting.GOLD : ChatFormatting.YELLOW) + " " + currentLvl + extra, xText, yText, 16777215);

            if (reachLimit)
            {
                GuiComponent.drawCenteredString(matrixStack, this.font, SBNumberUtils.formatWithM(playerXp), xText, yText + 10, 16777215);
            }
            else
            {
                GuiComponent.drawCenteredString(matrixStack, this.font, NumberUtils.formatCompact((long)playerXp) + "/" + NumberUtils.formatCompact(xpRequired), xText, yText + 10, 16777215);
            }
        }
        else
        {
            GuiComponent.drawCenteredString(matrixStack, this.font, name, xText, yText + 8, 16777215);
        }
    }

    private void drawContainerSlot(PoseStack matrixStack, int mouseX, int mouseY, boolean info)
    {
        int i = this.guiLeft;
        int j = this.guiTop;
        RenderSystem.pushMatrix();
        RenderSystem.translatef(i, j, 0.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableRescaleNormal();
        this.hoveredSlot = null;
        RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (!info)
        {
            for (Slot slot : this.skyBlockContainer.slots)
            {
                this.drawSlot(matrixStack, slot);

                if (this.isSlotSelected(slot, mouseX, mouseY) && slot.isActive())
                {
                    this.hoveredSlot = slot;

                    /*if (SkyBlockcatiaMod.isSkyblockAddonsLoaded && SkyBlockAddonsBackpack.INSTANCE.isFreezeBackpack()) TODO
                    {
                        continue;
                    }*/
                    RenderSystem.disableLighting();
                    RenderSystem.disableDepthTest();
                    int j1 = slot.x;
                    int k1 = slot.y;
                    RenderSystem.colorMask(true, true, true, false);
                    this.fillGradient(matrixStack, j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
                    RenderSystem.colorMask(true, true, true, true);
                    RenderSystem.enableLighting();
                    RenderSystem.enableDepthTest();
                }
            }
        }

        if (this.showArmor)
        {
            for (Slot slot : this.skyBlockArmorContainer.slots)
            {
                if (this.isSlotSelected(slot, mouseX, mouseY) && slot.isActive())
                {
                    this.hoveredSlot = slot;
                }
            }
        }
        RenderSystem.popMatrix();
    }

    private void drawSlot(PoseStack matrixStack, Slot slot)
    {
        int i = slot.x;
        int j = slot.y;
        ItemStack itemStack = slot.getItem();
        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0F;

        if (itemStack.isEmpty())
        {
            Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();

            if (pair != null)
            {
                TextureAtlasSprite sprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                this.minecraft.getTextureManager().bind(sprite.atlas().location());
                GuiComponent.blit(matrixStack, i, j, this.getBlitOffset(), 16, 16, sprite);
            }
        }

        RenderSystem.enableDepthTest();
        this.itemRenderer.renderAndDecorateItem(itemStack, i, j);

        int slotLeft = slot.x;
        int slotTop = slot.y;
        int slotRight = slotLeft + 16;
        int slotBottom = slotTop + 16;
        int green = ColorUtils.to32Bit(85, 255, 85, 150);

        if (!itemStack.isEmpty() && itemStack.hasTag() && itemStack.getTag().getBoolean("active"))
        {
            this.fillGradient(matrixStack, slotLeft, slotTop, slotRight, slotBottom, green, green);
        }

        this.renderItemOverlayIntoGUI(itemStack, i, j);
        this.itemRenderer.blitOffset = 0.0F;
        this.setBlitOffset(0);
    }

    private void renderItemOverlayIntoGUI(ItemStack itemStack, int xPosition, int yPosition)
    {
        if (!itemStack.isEmpty() && itemStack.getCount() != 1)
        {
            PoseStack matrixstack = new PoseStack();
            Font fontRenderer = this.font;
            MutableComponent component = TextComponentUtils.component(NumberUtils.formatCompact(itemStack.getCount()));

            if (itemStack.getCount() >= 100)
            {
                component.setStyle(component.getStyle().withFont(ClientUtils.UNICODE));
            }

            matrixstack.translate(0.0D, 0.0D, this.itemRenderer.blitOffset + 200.0F);
            MultiBufferSource.BufferSource irendertypebuffer$impl = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            fontRenderer.drawInBatch(component, xPosition + 19 - 2 - fontRenderer.width(component), yPosition + 6 + 3, 16777215, true, matrixstack.last().pose(), irendertypebuffer$impl, false, 0, 15728880);
            irendertypebuffer$impl.endBatch();
        }
        if (itemStack.isDamaged())
        {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();
            float f = (float)itemStack.getDamageValue();
            float g = (float)itemStack.getMaxDamage();
            float h = Math.max(0.0F, (g - f) / g);
            int i = Math.round(13.0F - f * 13.0F / g);
            int j = Mth.hsvToRgb(h / 3.0F, 1.0F, 1.0F);
            ((InvokerItemRenderer)this.itemRenderer).invokeFillRect(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
            ((InvokerItemRenderer)this.itemRenderer).invokeFillRect(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }

    private boolean renderGroupsHoveringText(PoseStack matrixStack, SBInventoryGroup group, int mouseX, int mouseY)
    {
        int i = group.getColumn();
        int j = 28 * i;
        int k = 0;

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
            this.renderTooltip(matrixStack, group.getTranslationKey(), mouseX, mouseY);
            return true;
        }
        else
        {
            return false;
        }
    }

    private void drawGroupsBackgroundLayer(PoseStack matrixStack)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        SBInventoryGroup group = SBInventoryGroup.GROUPS[this.selectedTabIndex];

        for (SBInventoryGroup group1 : SBInventoryGroup.GROUPS)
        {
            this.minecraft.getTextureManager().bind(INVENTORY_TABS);

            if (group1.getIndex() != this.selectedTabIndex)
            {
                this.drawGroup(matrixStack, group1);
            }
        }

        this.minecraft.getTextureManager().bind(new ResourceLocation("skyblockcatia:textures/gui/group_" + group.getBackgroundTexture()));
        this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.guiLeft + 182;
        int j = this.guiTop + 18;
        int k = j + 72;
        this.minecraft.getTextureManager().bind(INVENTORY_TABS);
        this.blit(matrixStack, i, j + (int)((k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
        this.drawGroup(matrixStack, group);
        RenderSystem.disableRescaleNormal();
        Lighting.turnOff();
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
    }

    private void drawTabsForegroundLayer(PoseStack matrixStack)
    {
        SBInventoryGroup group = SBInventoryGroup.GROUPS[this.selectedTabIndex];

        if (group != null)
        {
            RenderSystem.disableBlend();
            this.font.draw(matrixStack, group.getTranslationKey(), this.guiLeft + 11, this.guiTop + 6, 4210752);
        }
    }

    private void drawGroup(PoseStack matrixStack, SBInventoryGroup group)
    {
        boolean flag = group.getIndex() == this.selectedTabIndex;
        boolean flag1 = group.isOnTopRow();
        int i = group.getColumn();
        int j = i * 28;
        int k = 0;
        int l = this.guiLeft + 28 * i;
        int i1 = this.guiTop;
        int j1 = 32;

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

        RenderSystem.disableLighting();
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        this.blit(matrixStack, l, i1, j, k, 28, j1);
        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0F;
        l = l + 6;
        i1 = i1 + 8 + (flag1 ? 1 : -1);
        RenderSystem.enableLighting();
        RenderSystem.enableRescaleNormal();
        ItemStack itemStack = group.getIcon();
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

        RenderSystem.disableLighting();
        this.itemRenderer.blitOffset = 0.0F;
        this.setBlitOffset(0);
    }

    // Player Data
    private void getPlayerData() throws IOException
    {
        this.statusMessage = "Getting Player Data";
        JsonObject profiles;
        JsonElement banking;
        CommunityUpgrades communityUpgrade;

        if (this.skyblockProfiles == null)
        {
            URL url = new URL(APIUrl.SKYBLOCK_PROFILE.getUrl() + this.sbProfileId);
            JsonObject obj = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
            JsonElement profile = obj.get("profile");

            if (profile == null || profile.isJsonNull())
            {
                this.setErrorMessage("No API data returned, please try again later!", false);
                return;
            }
            profiles = profile.getAsJsonObject().get("members").getAsJsonObject();
            banking = profile.getAsJsonObject().get("banking");
            communityUpgrade = TextComponentUtils.GSON.fromJson(profile.getAsJsonObject().get("community_upgrades"), CommunityUpgrades.class);
        }
        else
        {
            profiles = this.skyblockProfiles.get("members").getAsJsonObject();
            banking = this.skyblockProfiles.get("banking");
            communityUpgrade = TextComponentUtils.GSON.fromJson(this.skyblockProfiles.getAsJsonObject().get("community_upgrades"), CommunityUpgrades.class);
        }

        for (Map.Entry<String, JsonElement> entry : profiles.entrySet())
        {
            String userUUID = entry.getKey();
            JsonObject currentUserProfile = profiles.get(userUUID).getAsJsonObject();
            this.getCraftedMinions(currentUserProfile);

            if (banking != null)
            {
                this.getBankHistories(banking.getAsJsonObject());
            }
            this.data.setHasBankHistory(banking != null && this.sbBankHistories.size() > 0);
        }

        this.processCraftedMinions();
        String checkUUID = this.uuid;

        for (Map.Entry<String, JsonElement> entry : profiles.entrySet())
        {
            String userUUID = entry.getKey();
            checkUUID = userUUID;

            if (userUUID.equals(this.uuid))
            {
                JsonObject currentUserProfile = profiles.get(userUUID).getAsJsonObject();
                URL urlStatus = new URL(SBAPIUtils.APIUrl.STATUS.getUrl() + this.uuid);
                JsonObject objStatus = new JsonParser().parse(IOUtils.toString(urlStatus.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();

                if (currentUserProfile.get("jacob2") != null)
                {
                    this.jacobInfo = this.getJacobData(currentUserProfile.get("jacob2"));
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

                for (SBInventoryGroup group : SBInventoryGroup.GROUPS)
                {
                    if (group.isDisabled())
                    {
                        this.totalDisabledInv++;
                    }
                }

                this.data.setHasInventories(this.totalDisabledInv != 11);
                this.allStat.setEffectiveHealth(this.allStat.getDefense() <= 0 ? this.allStat.getHealth() : (int)(this.allStat.getHealth() * (1 + this.allStat.getDefense() / 100.0D)));
                this.getBasicInfo(currentUserProfile, banking, objStatus, communityUpgrade);
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

    private List<SkyBlockInfo> getJacobData(JsonElement jacob)
    {
        List<SkyBlockInfo> info = Lists.newArrayList();
        JsonElement medals = jacob.getAsJsonObject().get("medals_inv");
        JsonElement perks = jacob.getAsJsonObject().get("perks");

        if (medals.getAsJsonObject().entrySet().size() > 0)
        {
            String gold = ColorUtils.toHex(255, 215, 0);
            String silver = ColorUtils.toHex(192, 192, 192);
            String bronze = ColorUtils.toHex(205, 127, 50);

            if (medals.getAsJsonObject().get("gold") != null)
            {
                info.add(new SkyBlockInfo("Gold Medal", medals.getAsJsonObject().get("gold").getAsString(), gold));
            }
            if (medals.getAsJsonObject().get("silver") != null)
            {
                info.add(new SkyBlockInfo("Silver Medal", medals.getAsJsonObject().get("silver").getAsString(), silver));
            }
            if (medals.getAsJsonObject().get("bronze") != null)
            {
                info.add(new SkyBlockInfo("Bronze Medal", medals.getAsJsonObject().get("bronze").getAsString(), bronze));
            }
        }
        if (perks.getAsJsonObject().entrySet().size() > 0)
        {
            if (perks.getAsJsonObject().get("double_drops") != null)
            {
                info.add(new SkyBlockInfo("Double Drops Perk", perks.getAsJsonObject().get("double_drops").getAsInt() * 2 + "%"));
            }
            if (perks.getAsJsonObject().get("farming_level_cap") != null)
            {
                this.farmingLevelCap = perks.getAsJsonObject().get("farming_level_cap").getAsInt();
                info.add(new SkyBlockInfo("Farming Level Cap", perks.getAsJsonObject().get("farming_level_cap").getAsString()));
            }
        }
        return info;
    }

    private List<SkyBlockInfo> getCommunityUpgrades(CommunityUpgrades communityUpgrades)
    {
        List<SkyBlockInfo> info = Lists.newArrayList();
        CommunityUpgrades.Upgrading upgrading = communityUpgrades.getCurrentUpgrade();
        List<CommunityUpgrades.States> states = communityUpgrades.getUpgradeStates();

        if (upgrading != null)
        {
            info.add(new SkyBlockInfo("Current Upgrade", upgrading.toString()));
        }
        if (states != null)
        {
            Multimap<String, Integer> upgradeStateMap = HashMultimap.create();

            for (CommunityUpgrades.States state : states)
            {
                upgradeStateMap.put(state.getUpgrade(), state.getTier());
            }
            for (String type : upgradeStateMap.keySet())
            {
                CommunityUpgrades.Data data = CommunityUpgrades.Data.getData(type, Collections.max(upgradeStateMap.get(type)));
                int tier = data.getTier();
                int maxed = data.getType().getMaxed();
                String color = tier == maxed ? ChatFormatting.GOLD.toString() : "";

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

    private void getDungeons(JsonObject currentUserProfile)//TODO
    {
        JsonElement dungeon = currentUserProfile.get("dungeons");
        int i = 0;

        if (dungeon != null)
        {
            //Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //System.out.println(gson.toJson(dungeon));
            this.dungeonData.add(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "WORK IN PROGRESS! NOT A FINAL GUI!");
            this.dungeonData.add("");

            JsonElement dungeonType = dungeon.getAsJsonObject().get("dungeon_types");
            JsonElement selectedClass = dungeon.getAsJsonObject().get("selected_dungeon_class");
            JsonElement playerClassExp = dungeon.getAsJsonObject().get("player_classes");
            JsonObject catacombsDungeon = dungeonType.getAsJsonObject().get("catacombs").getAsJsonObject();
            JsonElement catacombsExp = catacombsDungeon.get("experience");
            JsonElement highestFloor = catacombsDungeon.get("highest_tier_completed");
            JsonElement tierCompletion = catacombsDungeon.get("tier_completions");

            if (catacombsExp != null)
            {
                SBSkills.Info info = this.calculateDungeonSkill(catacombsExp.getAsDouble(), SBDungeons.Type.THE_CATACOMBS);
                this.catacombsLevel = info.getCurrentLvl();
                this.dungeonData.add(ChatFormatting.RED + info.getName() + ChatFormatting.RESET + ", Level: " + info.getCurrentLvl() + " " + (int)Math.floor(info.getCurrentXp()) + "/" + info.getXpRequired());
                i++;
            }

            if (selectedClass != null)
            {
                this.dungeonData.add("Selected Class: " + WordUtils.capitalize(selectedClass.getAsString()));
                i++;
            }
            if (highestFloor != null)
            {
                this.dungeonData.add("Highest Floor: " + highestFloor.getAsInt());
                i++;
            }
            this.dungeonData.add("");

            for (Map.Entry<String, JsonElement> entry : playerClassExp.getAsJsonObject().entrySet())
            {
                JsonElement classExp = entry.getValue().getAsJsonObject().get("experience");

                if (classExp != null)
                {
                    SBSkills.Info info2 = this.calculateDungeonSkill(classExp.getAsDouble(), SBDungeons.Type.valueOf(entry.getKey().toUpperCase(Locale.ROOT)));
                    this.dungeonData.add(ChatFormatting.RED + info2.getName() + ChatFormatting.RESET + ", Level: " + info2.getCurrentLvl() + " " + (int)Math.floor(info2.getCurrentXp()) + "/" + info2.getXpRequired());
                    i++;
                }
            }

            this.dungeonData.add("");
            StringBuilder builder = new StringBuilder();

            if (tierCompletion != null)
            {
                for (Map.Entry<String, JsonElement> entry : tierCompletion.getAsJsonObject().entrySet().stream().filter(entry -> !entry.getKey().equals("0")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).entrySet())
                {
                    builder.append("Floor: ").append(entry.getKey()).append("/").append(NumberUtils.NUMBER_FORMAT.format(entry.getValue().getAsInt())).append(", ");
                }
                i++;
            }

            this.dungeonData.add(builder.toString());
        }
        this.data.setHasDungeons(dungeon != null && i > 0);
    }

    private SBSkills.Info calculateDungeonSkill(double playerXp, SBDungeons.Type type)
    {
        ExpProgress[] progress = ExpProgress.DUNGEON;
        int xpRequired = 0;
        int currentLvl = 0;
        int levelToCheck = 0;
        double xpTotal = 0;
        double xpToNextLvl = 0;
        double currentXp;

        for (int x = 0; x < progress.length; ++x)
        {
            if (playerXp >= xpTotal)
            {
                xpTotal += progress[x].getXp();
                currentLvl = x;
                levelToCheck = progress[x].getLevel();

                if (levelToCheck <= progress.length)
                {
                    xpRequired = (int)progress[x].getXp();
                }
            }
        }

        if (levelToCheck < progress.length)
        {
            xpToNextLvl = xpTotal - playerXp;
            currentXp = xpRequired - xpToNextLvl;
        }
        else
        {
            currentLvl = progress.length;
            currentXp = playerXp - xpTotal;
        }

        if (currentXp < 0 && levelToCheck <= progress.length) // fix for skill level almost reach to limit
        {
            xpToNextLvl = xpTotal - playerXp;
            currentXp = xpRequired - xpToNextLvl;
            currentLvl = progress.length - 1;
        }
        return new SBSkills.Info(type.getName(), currentXp, xpRequired, currentLvl, 0, xpToNextLvl <= 0);
    }

    private void getBankHistories(JsonObject banking)
    {
        BankHistory[] bankHistory = TextComponentUtils.GSON.fromJson(banking.get("transactions"), BankHistory[].class);
        Collections.reverse(Arrays.asList(bankHistory));

        if (bankHistory.length > 0)
        {
            for (BankHistory bank : bankHistory)
            {
                this.sbBankHistories.add(new BankHistory.Stats(TextComponentUtils.formatted("------------------------------", ChatFormatting.DARK_GRAY)));
                this.sbBankHistories.add(new BankHistory.Stats(TextComponentUtils.component("Initiator: ").append(bank.getName().equals("Bank Interest") ? TextComponentUtils.formatted(bank.getName(), ColorUtils.toDecimal(255, 215, 0)) : TextComponentUtils.component(bank.getName()))));
                this.sbBankHistories.add(new BankHistory.Stats(TextComponentUtils.formatted(bank.getAction().name, bank.getAction().color).append(TextComponentUtils.formatted(" " + SBNumberUtils.formatWithM(bank.getAmount()), ChatFormatting.GOLD)).append(TextComponentUtils.formatted(" about " + TimeUtils.getRelativeTime(bank.getTimestamp()), ChatFormatting.WHITE))));
            }
            this.sbBankHistories.add(new BankHistory.Stats(TextComponentUtils.formatted("------------------------------", ChatFormatting.DARK_GRAY)));
        }
    }

    private String getLocation(JsonObject objStatus)
    {
        JsonObject session = objStatus.get("session").getAsJsonObject();
        String locationText = "";

        if (session.get("online").getAsBoolean())
        {
            JsonElement gameType = session.get("gameType");
            JsonElement mode = session.get("mode");

            if (gameType.getAsString().equals("SKYBLOCK"))
            {
                locationText = SBConstants.CURRENT_LOCATION_MAP.getOrDefault(mode.getAsString(), mode.getAsString());
            }
        }
        return locationText;
    }

    private void getCraftedMinions(JsonObject currentProfile)
    {
        JsonElement craftedGenerators = currentProfile.get("crafted_generators");

        if (craftedGenerators != null)
        {
            for (JsonElement craftedMinion : craftedGenerators.getAsJsonArray())
            {
                String[] split = craftedMinion.getAsString().split("_");
                String minionType = split.length >= 3 ? split[0] + "_" + split[1] : split[0];
                int unlockedLvl = Integer.parseInt(split[split.length - 1]);
                this.craftedMinions.put(minionType, unlockedLvl);
                this.craftedMinionCount++;
            }
        }
    }

    private void processCraftedMinions()
    {
        for (SBMinions.CraftedMinions minion : SBMinions.MINIONS.getCraftedMinions())
        {
            if (minion.getCount() <= this.craftedMinionCount)
            {
                this.currentMinionSlot = minion.getSlot();
            }
        }

        List<SBMinions.Info> minionLevels = Lists.newArrayList();
        List<SBMinions.Data> minionDatas = Lists.newArrayList();
        int level = 1;

        for (SBMinions.Type minion : SBMinions.MINIONS.getType())
        {
            for (String minionType : this.craftedMinions.keySet())
            {
                if (minion.getType().equals(minionType))
                {
                    level = Collections.max(this.craftedMinions.get(minionType));
                    break;
                }
            }
            minionLevels.add(new SBMinions.Info(minion.getType(), minion.getDisplayName(), minion.getMinionItem(), level, minion.getCategory()));
        }

        for (Map.Entry<String, Collection<Integer>> entry : this.craftedMinions.asMap().entrySet())
        {
            String minionType = entry.getKey();
            int[] dummyTiers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
            Collection<Integer> craftedList = entry.getValue();
            StringBuilder builder = new StringBuilder();
            int[] craftedTiers = Ints.toArray(craftedList);
            List<String> minionList = Lists.newArrayList();
            Set<Integer> dummySet = Sets.newHashSet();
            Set<Integer> skippedList = Sets.newHashSet();

            if (SBMinions.MINIONS.getTypeByName(minionType).hasTier12())
            {
                dummyTiers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
            }

            for (int craftedTier : craftedTiers)
            {
                dummySet.add(craftedTier);
            }
            for (int dummyTier : dummyTiers)
            {
                if (dummySet.add(dummyTier))
                {
                    skippedList.add(dummyTier);
                }
            }

            for (int skipped : skippedList)
            {
                minionList.add(ChatFormatting.RED.toString() + skipped);
            }
            for (int crafted : craftedList)
            {
                minionList.add(ChatFormatting.GREEN.toString() + crafted);
            }

            minionList.sort((text1, text2) -> new CompareToBuilder().append(Integer.parseInt(ChatFormatting.stripFormatting(text1)), Integer.parseInt(ChatFormatting.stripFormatting(text2))).build());
            int i = 0;

            for (String allTiers : minionList)
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

        List<SBMinions.CraftedInfo> farmingMinion = Lists.newArrayList();
        List<SBMinions.CraftedInfo> miningMinion = Lists.newArrayList();
        List<SBMinions.CraftedInfo> combatMinion = Lists.newArrayList();
        List<SBMinions.CraftedInfo> foragingMinion = Lists.newArrayList();
        List<SBMinions.CraftedInfo> fishingMinion = Lists.newArrayList();
        SBMinions.CraftedInfo dummy = new SBMinions.CraftedInfo(null, null, 0, null, ItemStack.EMPTY);
        String displayName = null;
        ItemStack itemStack = ItemStack.EMPTY;
        SBSkills.Type category = null;
        Comparator<SBMinions.CraftedInfo> com = (cm1, cm2) -> new CompareToBuilder().append(cm1.getMinionName().getString(), cm2.getMinionName().getString()).build();

        for (SBMinions.Data minionData : minionDatas)
        {
            for (SBMinions.Info minionLevel : minionLevels)
            {
                if (minionLevel.getMinionType().equals(minionData.getMinionType()))
                {
                    displayName = minionLevel.getDisplayName();
                    level = minionLevel.getMinionMaxTier();
                    itemStack = minionLevel.getMinionItem();
                    category = minionLevel.getMinionCategory();
                    break;
                }
            }

            SBMinions.CraftedInfo min = new SBMinions.CraftedInfo(TextComponentUtils.component(minionData.getMinionType()), displayName, level, minionData.getCraftedTiers(), itemStack);

            switch (category)
            {
                case FARMING:
                default:
                    farmingMinion.add(min);
                    break;
                case MINING:
                    miningMinion.add(min);
                    break;
                case COMBAT:
                    combatMinion.add(min);
                    break;
                case FORAGING:
                    foragingMinion.add(min);
                    break;
                case FISHING:
                    fishingMinion.add(min);
                    break;
            }
        }

        farmingMinion.sort(com);
        miningMinion.sort(com);
        combatMinion.sort(com);
        foragingMinion.sort(com);
        fishingMinion.sort(com);

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

        if (this.sbCraftedMinions.isEmpty())
        {
            this.data.setHasMinions(false);
        }
    }

    private void getCollections(JsonObject currentProfile)
    {
        JsonElement collections = currentProfile.get("collection");
        JsonElement unlockedTiersElement = currentProfile.get("unlocked_coll_tiers");
        Multimap<String, Integer> skyblockCollectionMap = HashMultimap.create();

        if (unlockedTiersElement != null)
        {
            JsonArray unlockedTiers = unlockedTiersElement.getAsJsonArray();

            for (JsonElement unlockedTier : unlockedTiers)
            {
                String[] split = unlockedTier.getAsString().toLowerCase(Locale.ROOT).split("_");
                String unlockedId = split.length >= 3 ? split[0] + "_" + split[1] : split[0];
                int unlockedLvl = Integer.parseInt(split[split.length - 1]);
                skyblockCollectionMap.put(this.replaceId(unlockedId), unlockedLvl);
            }
        }

        SBCollections dummyCollection = new SBCollections(ItemStack.EMPTY, null, -1, -1);

        if (collections != null && collections.getAsJsonObject().entrySet().size() > 0)
        {
            List<SBCollections> farming = Lists.newArrayList();
            List<SBCollections> mining = Lists.newArrayList();
            List<SBCollections> combat = Lists.newArrayList();
            List<SBCollections> foraging = Lists.newArrayList();
            List<SBCollections> fishing = Lists.newArrayList();
            List<SBCollections> unknown = Lists.newArrayList();
            List<ItemLike> allItem = Lists.newArrayList();

            for (Map.Entry<String, JsonElement> collection : collections.getAsJsonObject().entrySet())
            {
                String collectionId = this.replaceId(collection.getKey().toLowerCase(Locale.ROOT));
                int collectionCount = collection.getValue().getAsInt();
                String[] split = collectionId.split(":");
                String itemId = split[0];
                int meta = 0;
                int level = 0;

                try
                {
                    meta = Integer.parseInt(split[1]);
                }
                catch (Exception ignored) {}

                for (String itemIdFromLvl : skyblockCollectionMap.keySet())
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

                String newItemReg = ItemStackTheFlatteningFix.updateItem("minecraft:" + itemId, meta);

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
                    ItemStack unknownCollection = new ItemStack(item == Items.AIR ? Blocks.BARRIER : item);
                    CompoundTag compound = new CompoundTag();
                    ListTag list = new ListTag();
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
                    ItemStack unknownCollection = new ItemStack(item == Items.AIR ? Blocks.BARRIER : item);
                    unknownCollection.setHoverName(TextComponentUtils.component(WordUtils.capitalize(itemId.replace("_", " "))));
                    itemStack = unknownCollection;
                }

                for (Map.Entry<SBCollections.Type, ImmutableList<ItemLike>> entry : SBConstants.COLLECTION_MAP.entrySet())
                {
                    allItem.addAll(entry.getValue());

                    if (!itemId.startsWith("enchanted_") && this.matchesCollection(entry.getValue(), item))
                    {
                        switch (entry.getKey())
                        {
                            case COMBAT:
                                this.addToCollection(combat, entry.getKey(), itemStack, collectionCount, level);
                                break;
                            case FARMING:
                                this.addToCollection(farming, entry.getKey(), itemStack, collectionCount, level);
                                break;
                            case FISHING:
                                this.addToCollection(fishing, entry.getKey(), itemStack, collectionCount, level);
                                break;
                            case FORAGING:
                                this.addToCollection(foraging, entry.getKey(), itemStack, collectionCount, level);
                                break;
                            case MINING:
                                this.addToCollection(mining, entry.getKey(), itemStack, collectionCount, level);
                                break;
                            default:
                                break;
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

            Comparator<SBCollections> com = (sbColl1, sbColl2) -> new CompareToBuilder().append(sbColl1.getCollectionType().ordinal(), sbColl2.getCollectionType().ordinal()).append(sbColl2.getValue(), sbColl1.getValue()).build();
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

    private void getSacks(JsonObject currentProfile)
    {
        List<ItemStack> sacks = Lists.newArrayList();
        JsonElement sacksCounts = currentProfile.get("sacks_counts");
        Multimap<String, org.apache.commons.lang3.tuple.Pair<Integer, Integer>> runes = HashMultimap.create();

        if (sacksCounts != null)
        {
            for (Map.Entry<String, JsonElement> sackEntry : sacksCounts.getAsJsonObject().entrySet())
            {
                int count = sackEntry.getValue().getAsInt();
                String sackId = this.replaceId(sackEntry.getKey().toLowerCase(Locale.ROOT));
                String[] split = sackId.split(":");
                String itemId = split[0];
                int meta = 0;

                try
                {
                    meta = Integer.parseInt(split[1]);
                }
                catch (Exception ignored) {}

                String newItemReg = ItemStackTheFlatteningFix.updateItem("minecraft:" + itemId, meta);

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
                        Item item = Registry.ITEM.get(new ResourceLocation(itemId));

                        if (item == Items.AIR)
                        {
                            if (itemId.startsWith("rune_"))
                            {
                                String runeName = itemId.trim().replaceAll("(_[0-9])(?!_[0-9])", "");
                                int runeLevel = 0;
                                Pattern runeLvlPattern = Pattern.compile("_[0-9]((?!_[0-9]))");
                                Matcher runeLvlMatcher = runeLvlPattern.matcher(itemId);

                                if (runeLvlMatcher.find())
                                {
                                    runeLevel = Integer.parseInt(runeLvlMatcher.group().replace("_", ""));
                                }
                                runes.put(runeName, org.apache.commons.lang3.tuple.Pair.of(runeLevel, count));
                            }
                            else if (itemId.startsWith("enchanted_"))
                            {
                                item = Registry.ITEM.get(new ResourceLocation(itemId.replace("enchanted_", "")));
                                ItemStack enchantedItem = SBConstants.ENCHANTED_ID_TO_ITEM.getOrDefault(itemId, new ItemStack(item == Items.AIR ? Items.BARRIER : item, count));
                                enchantedItem.setCount(count);

                                if (enchantedItem.hasTag())
                                {
                                    ListTag list = new ListTag();
                                    list.add(new CompoundTag());
                                    enchantedItem.getTag().put("Enchantments", list);
                                }
                                else
                                {
                                    CompoundTag compound = new CompoundTag();
                                    ListTag list = new ListTag();
                                    list.add(new CompoundTag());
                                    compound.put("Enchantments", list);
                                    enchantedItem.setTag(compound);
                                }
                                MutableComponent component = TextComponentUtils.formatted(WordUtils.capitalize(itemId.replace("_", " ")), ChatFormatting.WHITE);
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
            SBSlayers.Drops slayerDrops = SBSlayers.Drops.valueOf(itemId.toUpperCase(Locale.ROOT));
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
            SBDungeons.Drops dungeonDrops = SBDungeons.Drops.valueOf(itemId.toUpperCase(Locale.ROOT));
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
            CandySacks candySacks = CandySacks.valueOf(itemId.toUpperCase(Locale.ROOT));
            ItemStack base = candySacks.getBaseItem();
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
            DwarvenSacks dwarvenSacks = DwarvenSacks.valueOf(itemId.toUpperCase(Locale.ROOT));
            ItemStack base = dwarvenSacks.getBaseItem();
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
        for (Map.Entry<String, Collection<org.apache.commons.lang3.tuple.Pair<Integer, Integer>>> entry : runes.asMap().entrySet())
        {
            RuneSacks rune = RuneSacks.byName(entry.getKey());
            ItemStack base = rune.getBaseItem();
            List<org.apache.commons.lang3.tuple.Pair<Integer, Integer>> sortedLvl = Lists.newArrayList(entry.getValue());
            Collections.sort(sortedLvl);
            int sum = sortedLvl.stream().mapToInt(org.apache.commons.lang3.tuple.Pair::getRight).sum();
            base.setCount(sum);

            if (rune == RuneSacks.UNKNOWN)
            {
                this.addSackItemStackCount(base, sum, rune.getDisplayName().copy().append(" - " + entry.getKey()), false, sacks);
            }
            else
            {
                ListTag loreList = new ListTag();

                for (org.apache.commons.lang3.tuple.Pair<Integer, Integer> level : sortedLvl)
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
        ItemStack itemStack = new ItemStack(item, count);
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
                MutableComponent component = altName.copy();
                itemStack.setHoverName(component.setStyle(component.getStyle().withItalic(false)));
            }
        }

        if (ench)
        {
            ListTag listEnch = new ListTag();
            listEnch.add(new CompoundTag());
            itemStack.getTag().put("Enchantments", listEnch);
        }
        sacks.add(itemStack);
    }

    private void getPets(JsonObject currentUserProfile)
    {
        List<SBPets.Data> petData = Lists.newArrayList();
        List<ItemStack> petItem = Lists.newArrayList();
        JsonElement petsObj = currentUserProfile.get("pets");

        if (petsObj == null)
        {
            this.totalDisabledInv++;
            return;
        }

        JsonArray pets = petsObj.getAsJsonArray();

        if (pets.size() > 0)
        {
            for (JsonElement element : pets)
            {
                double exp = 0.0D;
                String petRarity = SBPets.Tier.COMMON.name();
                int candyUsed = 0;
                JsonElement heldItemObj = element.getAsJsonObject().get("heldItem");
                JsonElement skinObj = element.getAsJsonObject().get("skin");
                SBPets.HeldItem heldItem = null;
                String heldItemType = null;
                String skin = null;
                String skinName = null;
                boolean addEmpty = false;

                if (element.getAsJsonObject().get("exp") != null)
                {
                    exp = element.getAsJsonObject().get("exp").getAsDouble();
                }
                if (element.getAsJsonObject().get("tier") != null)
                {
                    petRarity = element.getAsJsonObject().get("tier").getAsString();
                }
                if (element.getAsJsonObject().get("candyUsed") != null)
                {
                    candyUsed = element.getAsJsonObject().get("candyUsed").getAsInt();
                    addEmpty = candyUsed > 0;
                }
                if (skinObj != null && !skinObj.isJsonNull())
                {
                    skin = skinObj.getAsString();
                    addEmpty = skin != null;
                }
                if (heldItemObj != null && !heldItemObj.isJsonNull())
                {
                    try
                    {
                        heldItem = SBPets.PETS.getHeldItemByName(heldItemObj.getAsString());
                        addEmpty = heldItem != null;
                    }
                    catch (Exception e)
                    {
                        heldItemType = heldItemObj.getAsString();
                        addEmpty = heldItemType != null;
                    }
                }

                SBPets.Tier tier = SBPets.Tier.valueOf(petRarity);
                boolean active = element.getAsJsonObject().get("active").getAsBoolean();
                String petType = element.getAsJsonObject().get("type").getAsString();
                ListTag list = new ListTag();

                if (heldItem != null && heldItem.isUpgrade())
                {
                    tier = tier.getNextRarity();
                }

                SBPets.Info level = this.checkPetLevel(exp, tier);

                try
                {
                    ChatFormatting rarity = tier.getTierColor();
                    SBPets.Type type = SBPets.PETS.getTypeByName(petType);
                    ItemStack itemStack = type.getPetItem();

                    itemStack.setHoverName(TextComponentUtils.component(ChatFormatting.GRAY + "[Lvl " + level.getCurrentPetLevel() + "] " + rarity + WordUtils.capitalize(petType.toLowerCase(Locale.ROOT).replace("_", " "))));
                    list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET.toString() + ChatFormatting.DARK_GRAY + type.getSkill().getName() + " Pet")));
                    list.add(StringTag.valueOf(TextComponentUtils.toJson("")));
                    list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET + (level.getCurrentPetLevel() < 100 ? ChatFormatting.GRAY + "Progress to Level " + level.getNextPetLevel() + ": " + ChatFormatting.YELLOW + level.getPercent() : level.getPercent()))));

                    if (level.getCurrentPetLevel() < 100)
                    {
                        list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET + this.getTextPercentage((int)level.getCurrentPetXp(), level.getXpRequired()) + " " + ChatFormatting.YELLOW + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(level.getCurrentPetXp()) + ChatFormatting.GOLD + "/" + ChatFormatting.YELLOW + SBNumberUtils.formatWithM(level.getXpRequired()))));
                    }
                    if (addEmpty)
                    {
                        list.add(StringTag.valueOf(TextComponentUtils.toJson("")));
                    }
                    if (candyUsed > 0)
                    {
                        list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET.toString() + ChatFormatting.GRAY + "Candy Used: " + ChatFormatting.YELLOW + candyUsed + ChatFormatting.GOLD + "/" + ChatFormatting.YELLOW + 10)));
                    }
                    if (skin != null)
                    {
                        for (SBPets.Skin petSkin : SBPets.PETS.getSkin())
                        {
                            if (skin.equals(petSkin.getType()))
                            {
                                ItemStack tmp = itemStack.copy();
                                itemStack = ItemUtils.setSkullSkin(itemStack, petSkin.getUUID(), petSkin.getTexture());
                                itemStack.setHoverName(tmp.getHoverName());
                                skinName = petSkin.getColor() + petSkin.getName();
                                break;
                            }
                        }
                        list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET.toString() + ChatFormatting.GRAY + "Skin: " + (skinName == null ? skin : skinName))));
                    }
                    if (heldItem != null)
                    {
                        String heldItemName = ChatFormatting.getByName(heldItem.getColor()) + heldItem.getName();
                        list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET.toString() + ChatFormatting.GRAY + "Held Item: " + heldItemName)));
                    }
                    else
                    {
                        if (heldItemType != null)
                        {
                            list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET.toString() + ChatFormatting.GRAY + "Held Item: " + ChatFormatting.RED + heldItemType)));
                        }
                    }

                    list.add(StringTag.valueOf(TextComponentUtils.toJson("")));
                    list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RESET.toString() + ChatFormatting.GRAY + "Total XP: " + ChatFormatting.YELLOW + SBNumberUtils.formatWithM(level.getPetXp()) + ChatFormatting.GOLD + "/" + ChatFormatting.YELLOW + SBNumberUtils.formatWithM(level.getTotalPetTypeXp()))));
                    list.add(StringTag.valueOf(TextComponentUtils.toJson(rarity.toString() + ChatFormatting.BOLD + tier + " PET")));
                    itemStack.getTag().getCompound("display").put("Lore", list);
                    itemStack.getOrCreateTagElement("ExtraAttributes").putString("id", "PET");
                    itemStack.getTag().putBoolean("active", active);
                    petData.add(new SBPets.Data(tier, level.getCurrentPetLevel(), TextComponentUtils.component(WordUtils.capitalize(petType.toLowerCase(Locale.ROOT).replace("_", " "))), active, Collections.singletonList(itemStack)));

                    switch (tier)
                    {
                        case COMMON:
                            this.petScore += 1;
                            break;
                        case UNCOMMON:
                            this.petScore += 2;
                            break;
                        case RARE:
                            this.petScore += 3;
                            break;
                        case EPIC:
                            this.petScore += 4;
                            break;
                        case LEGENDARY:
                            this.petScore += 5;
                            break;
                        case MYTHIC:
                            this.petScore += 6;
                            break;
                        default:
                            break;
                    }
                }
                catch (Exception e)
                {
                    ItemStack itemStack = new ItemStack(Items.BONE);
                    itemStack.setHoverName(TextComponentUtils.formatted(WordUtils.capitalize(petType.toLowerCase(Locale.ROOT).replace("_", " ")), ChatFormatting.RED));
                    list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "UNKNOWN PET")));
                    itemStack.getTag().getCompound("display").put("Lore", list);
                    petData.add(new SBPets.Data(SBPets.Tier.COMMON, 0, itemStack.getHoverName(), false, Lists.newArrayList(itemStack)));
                    SkyBlockcatiaMod.LOGGER.warning("Found an unknown pet! type: {}", petType);
                }
                petData.sort((o1, o2) -> new CompareToBuilder().append(o2.isActive(), o1.isActive()).append(o2.getTier().ordinal(), o1.getTier().ordinal()).append(o2.getCurrentLevel(), o1.getCurrentLevel()).append(o2.getName().getString(), o1.getName().getString()).build());
            }
            for (SBPets.Data data : petData)
            {
                petItem.addAll(data.getItemStack());
            }
        }
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(petItem, SBInventoryGroup.PET));
    }

    private SBPets.Info checkPetLevel(double petExp, SBPets.Tier tier)
    {
        int index = SBPets.PETS.getIndex().get(tier.name());
        int totalPetTypeXp = 0;
        int xpRequired = 0;
        int currentLvl = 0;
        int levelToCheck = 0;
        double xpTotal = 0;
        double xpToNextLvl;
        double currentXp = 0;

        for (int i = index; i < 99 + index; i++)
        {
            int level = SBPets.PETS.getLeveling()[i];
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

    private void calculatePlayerStats(JsonObject currentProfile)
    {
        JsonElement fairySouls = currentProfile.get("fairy_souls_collected");
        JsonElement fairyExchangesEle = currentProfile.get("fairy_exchanges");
        int fairyExchanges = 0;

        if (fairySouls != null)
        {
            this.totalFairySouls = fairySouls.getAsInt();
        }
        if (fairyExchangesEle != null)
        {
            fairyExchanges = fairyExchangesEle.getAsInt();
        }

        this.getFairySouls(fairyExchanges);
        this.getMagicFindFromPets(this.petScore);
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.getBonus().getFarming(), this.farmingLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.getBonus().getForaging(), this.foragingLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.getBonus().getMining(), this.miningLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.getBonus().getFishing(), this.fishingLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.getBonus().getCombat(), this.combatLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.getBonus().getEnchanting(), this.enchantingLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.getBonus().getAlchemy(), this.alchemyLevel));
        this.allStat.add(this.calculateSkillBonus(SBSkills.SKILLS.getBonus().getTaming(), this.tamingLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.CATACOMBS_DUNGEON, this.catacombsLevel));
        this.allStat.add(this.calculateSkillBonus(SBSlayers.SLAYERS.getBonus().getZombie(), this.zombieSlayerLevel));
        this.allStat.add(this.calculateSkillBonus(SBSlayers.SLAYERS.getBonus().getSpider(), this.spiderSlayerLevel));
        this.allStat.add(this.calculateSkillBonus(SBSlayers.SLAYERS.getBonus().getWolf(), this.wolfSlayerLevel));
    }

    private BonusStatTemplate calculateSkillBonus(IBonusTemplate[] bonus, int skillLevel)
    {
        double healthTemp = 0;
        double defenseTemp = 0;
        double trueDefenseTemp = 0;
        double strengthTemp = 0;
        double speedTemp = 0;
        double critChanceTemp = 0;
        double critDamageTemp = 0;
        double attackSpeedTemp = 0;
        double intelligenceTemp = 0;
        double seaCreatureChanceTemp = 0;
        double magicFindTemp = 0;
        double petLuckTemp = 0;
        double ferocityTemp = 0;
        double abilityDamageTemp = 0;
        double miningSpeedTemp = 0;
        double miningFortuneTemp = 0;
        double farmingFortuneTemp = 0;
        double foragingFortuneTemp = 0;

        for (int i = 0; i < bonus.length; ++i)
        {
            int levelToCheck = bonus[i].getLevel();
            int nextIndex = 0;
            boolean limit = true;

            if (nextIndex <= i)
            {
                nextIndex = i + 1; // check level at next index of json
            }

            if (nextIndex >= bonus.length)
            {
                nextIndex = bonus.length - 1;
                limit = false;
            }

            int levelToCheck2 = bonus[nextIndex].getLevel();

            if (levelToCheck <= skillLevel)
            {
                double health = bonus[i].getHealth();
                double defense = bonus[i].getDefense();
                double trueDefense = bonus[i].getTrueDefense();
                double strength = bonus[i].getStrength();
                double speed = bonus[i].getSpeed();
                double critChance = bonus[i].getCritChance();
                double critDamage = bonus[i].getCritDamage();
                double attackSpeed = bonus[i].getAttackSpeed();
                double intelligence = bonus[i].getIntelligence();
                double seaCreatureChance = bonus[i].getSeaCreatureChance();
                double magicFind = bonus[i].getMagicFind();
                double petLuck = bonus[i].getPetLuck();
                double ferocity = bonus[i].getFerocity();
                double abilityDamage = bonus[i].getAbilityDamage();
                double miningSpeed = bonus[i].getMiningSpeed();
                double miningFortune = bonus[i].getMiningFortune();
                double farmingFortune = bonus[i].getFarmingFortune();
                double foragingFortune = bonus[i].getForagingFortune();

                for (int level = levelToCheck; level <= skillLevel; level++)
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
        List<ItemStack> itemStack1 = Lists.newArrayList();
        byte[] cakeData = extraAttrib.getByteArray("new_year_cake_bag_data");

        if (cakeData.length == 0)
        {
            return;
        }

        try
        {
            CompoundTag compound1 = NbtIo.readCompressed(new ByteArrayInputStream(cakeData));
            ListTag list = compound1.getList("i", 10);
            List<Integer> cakeYears = Lists.newArrayList();

            for (int i = 0; i < list.size(); ++i)
            {
                itemStack1.add(SBItemUtils.flatteningItemStack(list.getCompound(i)));
            }

            for (ItemStack cake : itemStack1)
            {
                if (!cake.isEmpty() && cake.hasTag())
                {
                    int year = cake.getTag().getCompound("ExtraAttributes").getInt("new_years_cake");

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
        double healthTemp = 0;
        double defenseTemp = 0;
        double trueDefenseTemp = 0;
        double strengthTemp = 0;
        double speedTemp = 0;
        double critChanceTemp = 0;
        double critDamageTemp = 0;
        double attackSpeedTemp = 0;
        double intelligenceTemp = 0;
        double seaCreatureChanceTemp = 0;
        double magicFindTemp = 0;
        double petLuckTemp = 0;
        double ferocityTemp = 0;
        double abilityDamageTemp = 0;
        double miningSpeedTemp = 0;
        double miningFortuneTemp = 0;
        double farmingFortuneTemp = 0;
        double foragingFortuneTemp = 0;

        for (ItemStack itemStack : inventory)
        {
            if (!itemStack.isEmpty() && itemStack.hasTag())
            {
                CompoundTag compound = itemStack.getTag().getCompound("display");
                CompoundTag extraAttrib = itemStack.getTag().getCompound("ExtraAttributes");
                String itemId = extraAttrib.getString("id");

                switch (itemId)
                {
                    case "FROZEN_CHICKEN":
                    case "SPEED_TALISMAN":
                        this.allStat.addSpeed(1);
                        break;
                    case "SPEED_RING":
                        this.allStat.addSpeed(3);
                        break;
                    case "SPEED_ARTIFACT":
                        this.allStat.addSpeed(5);
                        break;
                    case "NEW_YEAR_CAKE_BAG":
                        this.getHealthFromCake(extraAttrib);
                        break;
                }

                if (compound.getTagType("Lore") == 9)
                {
                    ListTag list = compound.getList("Lore", 8);

                    for (int j1 = 0; j1 < list.size(); ++j1)
                    {
                        String lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));
                        String lastLore = TextComponentUtils.fromJsonUnformatted(list.getString(list.size() - 1));
                        Matcher matcher = STATS_PATTERN.matcher(lore);

                        if (!armor && !(lastLore.endsWith(" ACCESSORY") || lastLore.endsWith(" HATCCESSORY") || lastLore.endsWith(" ACCESSORY a") || lastLore.endsWith(" HATCCESSORY a")))
                        {
                            continue;
                        }

                        if (matcher.matches())
                        {
                            String type = matcher.group("type");
                            String value = matcher.group("value").replace(",", "");
                            int valueD = 0;

                            try
                            {
                                valueD = NumberUtils.NUMBER_FORMAT_WITH_OPERATORS.parse(value).intValue();
                            }
                            catch (Exception ignored) {}

                            switch (type)
                            {
                                case "Health":
                                    healthTemp += valueD;
                                    break;
                                case "Defense":
                                    defenseTemp += valueD;
                                    break;
                                case "True Defense":
                                    trueDefenseTemp += valueD;
                                    break;
                                case "Strength":
                                    strengthTemp += valueD;
                                    break;
                                case "Speed":
                                    speedTemp += valueD;
                                    break;
                                case "Crit Chance":
                                    critChanceTemp += valueD;
                                    break;
                                case "Crit Damage":
                                    critDamageTemp += valueD;
                                    break;
                                case "Intelligence":
                                    intelligenceTemp += valueD;
                                    break;
                                case "Sea Creature Chance":
                                    seaCreatureChanceTemp += valueD;
                                    break;
                                case "Magic Find":
                                    magicFindTemp += valueD;
                                    break;
                                case "Pet Luck":
                                    petLuckTemp += valueD;
                                    break;
                                case "Bonus Attack Speed":
                                    attackSpeedTemp += valueD;
                                    break;
                                case "Ferocity":
                                    ferocityTemp += valueD;
                                    break;
                                case "Ability Damage":
                                    abilityDamageTemp += valueD;
                                    break;
                                case "Mining Speed":
                                    miningSpeedTemp += valueD;
                                    break;
                                case "Mining Fortune":
                                    miningFortuneTemp += valueD;
                                    break;
                                case "Farming Fortune":
                                    farmingFortuneTemp += valueD;
                                    break;
                                case "Foraging Fortune":
                                    foragingFortuneTemp += valueD;
                                    break;
                            }
                        }
                    }
                }
            }
        }
        this.allStat.add(new BonusStatTemplate(healthTemp, defenseTemp, trueDefenseTemp, 0, strengthTemp, speedTemp, critChanceTemp, critDamageTemp, attackSpeedTemp, intelligenceTemp, seaCreatureChanceTemp, magicFindTemp, petLuckTemp, ferocityTemp, abilityDamageTemp, miningSpeedTemp, miningFortuneTemp, farmingFortuneTemp, foragingFortuneTemp));
    }

    private void getBasicInfo(JsonObject currentProfile, JsonElement banking, JsonObject objStatus, CommunityUpgrades communityUpgrade)
    {
        JsonElement deathCount = currentProfile.get("death_count");
        JsonElement purse = currentProfile.get("coin_purse");
        JsonElement lastSave = currentProfile.get("last_save");
        JsonElement firstJoin = currentProfile.get("first_join");
        int deathCounts = 0;
        double coins = 0.0D;
        long lastSaveMillis = -1;
        long firstJoinMillis = -1;

        if (deathCount != null)
        {
            deathCounts = deathCount.getAsInt();
        }
        if (purse != null)
        {
            coins = purse.getAsDouble();
        }
        if (lastSave != null)
        {
            lastSaveMillis = lastSave.getAsLong();
        }
        if (firstJoin != null)
        {
            firstJoinMillis = firstJoin.getAsLong();
        }
        if (this.uuid.equals("eef3a6031c1b4c988264d2f04b231ef4")) // special case for me :D
        {
            firstJoinMillis = 1565111612000L;
        }

        String heath = ColorUtils.toHex(239, 83, 80);
        String defense = ColorUtils.toHex(156, 204, 101);
        String trueDefense = ColorUtils.toHex(255, 255, 255);
        String strength = ColorUtils.toHex(181, 33, 30);
        String speed = ColorUtils.toHex(255, 255, 255);
        String critChance = ColorUtils.toHex(121, 134, 203);
        String critDamage = ColorUtils.toHex(70, 90, 201);
        String attackSpeed = ColorUtils.toHex(255, 255, 85);
        String miningSpeed = ColorUtils.toHex(255, 170, 0);
        String intelligence = ColorUtils.toHex(129, 212, 250);
        String fairySoulsColor = ColorUtils.toHex(203, 54, 202);
        String seaCreatureChance = ColorUtils.toHex(0, 170, 170);
        String magicFind = ColorUtils.toHex(85, 255, 255);
        String petLuck = ColorUtils.toHex(255, 85, 255);
        String bank = ColorUtils.toHex(255, 215, 0);
        String purseColor = ColorUtils.toHex(255, 165, 0);
        String ferocity = ColorUtils.toHex(224, 120, 0);
        String abilityDamage = ColorUtils.toHex(214, 36, 0);
        String location = this.getLocation(objStatus);

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
        this.infoList.add(new SkyBlockInfo("✹ Ability Damage", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getAbilityDamage()) + "%", abilityDamage));
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
            double balance = banking.getAsJsonObject().get("balance").getAsDouble();
            this.infoList.add(new SkyBlockInfo("Banking Account", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(balance), bank));
        }
        else
        {
            this.infoList.add(new SkyBlockInfo("Banking Account", ChatFormatting.RED + "API is not enabled!"));
        }

        this.infoList.add(new SkyBlockInfo("Purse", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(coins), purseColor));

        if (communityUpgrade != null)
        {
            List<SkyBlockInfo> comm = this.getCommunityUpgrades(communityUpgrade);

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

        Date firstJoinDate = new Date(firstJoinMillis);
        Date lastSaveDate = new Date(lastSaveMillis);
        SimpleDateFormat logoutDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ROOT);
        String lastLogout = logoutDate.format(lastSaveDate);
        SimpleDateFormat joinDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ROOT);
        joinDate.setTimeZone(this.uuid.equals("eef3a6031c1b4c988264d2f04b231ef4") ? TimeZone.getTimeZone("GMT") : TimeZone.getDefault());
        String firstJoinDateFormat = joinDate.format(firstJoinDate);

        this.infoList.add(new SkyBlockInfo("Joined", firstJoinMillis != -1 ? TimeUtils.getRelativeTime(firstJoinDate.getTime()) + " (" + TimeUtils.getRelativeDay(firstJoinDate.getTime()) + ")" : ChatFormatting.RED + "No first join data!"));
        this.infoList.add(new SkyBlockInfo("Joined (Date)", firstJoinMillis != -1 ? firstJoinDateFormat : ChatFormatting.RED + "No first join data!"));
        this.infoList.add(new SkyBlockInfo("Last Updated", lastSaveMillis != -1 ? String.valueOf(lastSaveDate.getTime()) : ChatFormatting.RED + "No last save data!"));
        this.infoList.add(new SkyBlockInfo("Last Updated (Date)", lastSaveMillis != -1 ? lastLogout : ChatFormatting.RED + "No last save data!"));

        this.infoList.add(new SkyBlockInfo("Death Count", NumberUtils.NUMBER_FORMAT.format(deathCounts)));
    }

    private void getFairySouls(int fairyExchanges)
    {
        double healthBase = 0;
        double defenseBase = 0;
        double strengthBase = 0;
        double speed = Math.floor(fairyExchanges / 10D);

        for (int i = 0; i < fairyExchanges; i++)
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
        double magicFindBase = 0;

        for (SBPets.Score score : SBPets.PETS.getScore())
        {
            int scoreToCheck = score.getScore();
            double magicFind = score.getMagicFind();

            if (scoreToCheck <= petsScore)
            {
                magicFindBase = magicFind;
            }
        }
        this.allStat.addMagicFind(magicFindBase);
    }

    private String replaceStatsString(String statName, String replace)
    {
        String original = statName.replace(replace + "_", "").replace("_", " ");
        return original.equals(replace) ? "Total " + replace : WordUtils.capitalize(original) + " " + replace;
    }

    private void getSkills(JsonObject currentProfile)
    {
        this.skillLeftList.add(this.checkSkill(currentProfile.get("experience_skill_farming"), SBSkills.Type.FARMING));
        this.skillLeftList.add(this.checkSkill(currentProfile.get("experience_skill_foraging"), SBSkills.Type.FORAGING));
        this.skillLeftList.add(this.checkSkill(currentProfile.get("experience_skill_mining"), SBSkills.Type.MINING));
        this.skillLeftList.add(this.checkSkill(currentProfile.get("experience_skill_fishing"), SBSkills.Type.FISHING));
        this.skillLeftList.add(this.checkSkill(currentProfile.get("experience_skill_runecrafting"), SBSkills.Type.RUNECRAFTING, SBSkills.SKILLS.getLeveling().get("runecrafting")));

        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_combat"), SBSkills.Type.COMBAT));
        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_enchanting"), SBSkills.Type.ENCHANTING));
        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_alchemy"), SBSkills.Type.ALCHEMY));
        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_taming"), SBSkills.Type.TAMING));
        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_carpentry"), SBSkills.Type.CARPENTRY));

        double avg = 0.0D;
        double progress = 0.0D;
        int count = 0;
        List<SBSkills.Info> skills = Lists.newArrayList();
        skills.addAll(this.skillLeftList);
        skills.addAll(this.skillRightList);

        for (SBSkills.Info skill : skills)
        {
            if (skill.getName().contains("Runecrafting") || skill.getName().contains("Carpentry"))
            {
                continue;
            }
            avg += skill.getCurrentLvl();
            progress += skill.getSkillProgress();
            ++count;
        }

        double allProgress = new BigDecimal(progress / count).setScale(2, RoundingMode.HALF_UP).doubleValue();

        if (avg > 0)
        {
            double realAvg = avg / count + allProgress;
            this.skillAvg = new BigDecimal(realAvg).setScale(2, RoundingMode.HALF_UP).toString();
        }
        if (this.skillCount == 0)
        {
            this.data.setHasSkills(false);
        }
    }

    private SBSkills.Info checkSkill(JsonElement element, SBSkills.Type type)
    {
        return this.checkSkill(element, type, SBSkills.SKILLS.getLeveling().get("default"));
    }

    private SBSkills.Info checkSkill(JsonElement element, SBSkills.Type type, int[] leveling)
    {
        if (element != null)
        {
            double playerXp = element.getAsDouble();
            int xpRequired = 0;
            int currentLvl = 0;
            int levelToCheck = 0;
            double xpTotal = 0;
            double xpToNextLvl = 0;
            double currentXp;
            double skillProgress = 0;
            int cap = SBSkills.SKILLS.getCap().get(type.name().toLowerCase(Locale.ROOT));

            if (type == SBSkills.Type.FARMING)
            {
                cap += this.farmingLevelCap;
            }

            for (int x = 0; x < cap; ++x)
            {
                if (playerXp >= xpTotal)
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
                xpToNextLvl = xpTotal - playerXp;
                currentXp = (int)(xpRequired - xpToNextLvl);
            }
            else
            {
                currentLvl = cap;
                currentXp = playerXp - xpTotal;
            }

            if (currentXp < 0 && levelToCheck <= cap) // fix for skill level almost reach to limit
            {
                xpToNextLvl = xpTotal - playerXp;
                currentXp = (int)(xpRequired - xpToNextLvl);
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
            case FARMING:
                this.farmingLevel = currentLevel;
                break;
            case FORAGING:
                this.foragingLevel = currentLevel;
                break;
            case MINING:
                this.miningLevel = currentLevel;
                break;
            case FISHING:
                this.fishingLevel = currentLevel;
                break;
            case COMBAT:
                this.combatLevel = currentLevel;
                break;
            case ENCHANTING:
                this.enchantingLevel = currentLevel;
                break;
            case ALCHEMY:
                this.alchemyLevel = currentLevel;
                break;
            case TAMING:
                this.tamingLevel = currentLevel;
                break;
            default:
                break;
        }
    }

    private void getStats(JsonObject currentProfile)
    {
        JsonObject stats = currentProfile.get("stats").getAsJsonObject();
        List<SBStats> auctions = Lists.newArrayList();
        List<SBStats> fished = Lists.newArrayList();
        List<SBStats> winter = Lists.newArrayList();
        List<SBStats> petMilestone = Lists.newArrayList();
        List<SBStats> others = Lists.newArrayList();
        List<SBStats> mobKills = Lists.newArrayList();
        List<SBStats> seaCreatures = Lists.newArrayList();
        List<SBStats> dragons = Lists.newArrayList();
        List<SBStats> race = Lists.newArrayList();
        List<SBStats> mythosBurrowsDug = Lists.newArrayList();

        // special case
        int emperorKills = 0;
        int deepMonsterKills = 0;

        for (Map.Entry<String, JsonElement> stat : stats.entrySet().stream().filter(entry -> SBConstants.BLACKLIST_STATS.stream().noneMatch(stat -> entry.getKey().equals(stat))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).entrySet())
        {
            String statName = stat.getKey().toLowerCase(Locale.ROOT);
            double value = stat.getValue().getAsDouble();

            if (statName.startsWith("kills") || statName.endsWith("kills"))
            {
                if (SBConstants.SEA_CREATURES.stream().anyMatch(statName::contains))
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
                        seaCreatures.add(new SBStats(this.replaceStatsString(statName, "kills"), value));
                    }
                }
                else if (statName.contains("dragon"))
                {
                    dragons.add(new SBStats(this.replaceStatsString(statName, "kills"), value));
                }
                else
                {
                    mobKills.add(new SBStats(this.replaceStatsString(statName, "kills"), value));
                }
            }
            else if (statName.startsWith("deaths"))
            {
                this.sbDeaths.add(new SBStats(this.replaceStatsString(statName, "deaths"), value));
            }
            else
            {
                statName = SBConstants.RENAMED_STATS_MAP.getOrDefault(statName, statName);

                if (statName.contains("auctions"))
                {
                    auctions.add(new SBStats(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
                else if (statName.contains("items_fished") || statName.contains("shredder"))
                {
                    fished.add(new SBStats(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
                else if (statName.contains("gifts") || statName.contains("most_winter"))
                {
                    winter.add(new SBStats(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
                else if (statName.contains("pet_milestone"))
                {
                    petMilestone.add(new SBStats(WordUtils.capitalize(statName.replace("pet_milestone_", "").replace("_", " ")), value));
                }
                else if (statName.contains("race") || statName.contains("dungeon_hub"))
                {
                    race.add(new SBStats(WordUtils.capitalize(statName.replaceAll("dungeon_hub_|_best_time", "").replace("_", " ")), String.format("%1$TM:%1$TS.%1$TL", (long)value)));
                }
                else if (statName.startsWith("mythos_burrows_"))
                {
                    mythosBurrowsDug.add(new SBStats(WordUtils.capitalize(statName.toLowerCase(Locale.ROOT).replace("mythos_burrows_", "").replace("_", " ")), value));
                }
                else
                {
                    others.add(new SBStats(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
            }
        }

        // special case
        if (emperorKills > 0)
        {
            seaCreatures.add(new SBStats("Sea Emperor kills", emperorKills));
        }
        if (deepMonsterKills > 0)
        {
            seaCreatures.add(new SBStats("Monster of the Deep kills", deepMonsterKills));
        }

        this.sbDeaths.sort((stat1, stat2) -> new CompareToBuilder().append(stat2.getValue(), stat1.getValue()).build());
        auctions.sort((stat1, stat2) -> new CompareToBuilder().append(stat1.getName().getString(), stat2.getName().getString()).build());
        auctions.add(0, new SBStats(TextComponentUtils.formatted("Auctions", ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), 0.0F));

        this.sortStats(fished, "Fishing");
        this.sortStats(winter, "Winter Event");
        this.sortStats(petMilestone, "Pet Milestones");
        this.sortStats(race, "Races");
        this.sortStats(mythosBurrowsDug, "Mythos Burrows Dug");
        this.sortStats(others, "Others");

        mobKills.sort((stat1, stat2) -> new CompareToBuilder().append(stat2.getValue(), stat1.getValue()).build());
        mobKills.add(0, new SBStats(TextComponentUtils.formatted("Mob Kills", ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), 0.0F));

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

    private void sortStats(List<SBStats> list, String name)
    {
        list.sort((stat1, stat2) -> new CompareToBuilder().append(stat1.getName().getString(), stat2.getName().getString()).build());
        list.add(0, new SBStats(TextComponent.EMPTY, 0.0F));
        list.add(1, new SBStats(TextComponentUtils.formatted(name, ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), 0.0F));
    }

    private void sortStatsByValue(List<SBStats> list, String name)
    {
        list.sort((stat1, stat2) -> new CompareToBuilder().append(stat2.getValue(), stat1.getValue()).build());
        list.add(0, new SBStats(TextComponent.EMPTY, 0.0F));
        list.add(1, new SBStats(TextComponentUtils.formatted(name, ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE), 0.0F));
    }

    private long checkSkyBlockItem(List<ItemStack> list, String type)
    {
        return list.stream().filter(armor -> !armor.isEmpty() && armor.hasTag() && armor.getTag().getCompound("ExtraAttributes").getString("id").startsWith(type)).count();
    }

    @Deprecated // Removed in Java 11
    private <T> Predicate<T> not(Predicate<T> predicate)
    {
        return predicate.negate();
    }

    private void getInventories(JsonObject currentProfile)
    {
        this.armorItems.addAll(SBItemUtils.decodeItem(currentProfile, InventoryType.ARMOR).stream().filter(this.not(ItemStack::isEmpty)).collect(Collectors.toList()));

        if (this.armorItems.size() > 0)
        {
            for (int i = 0; i < this.armorItems.size(); ++i)
            {
                SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY.setItem(i, this.armorItems.get(i));
            }
        }

        List<ItemStack> mainInventory = SBItemUtils.decodeItem(currentProfile, InventoryType.INVENTORY);
        List<ItemStack> accessoryInventory = SBItemUtils.decodeItem(currentProfile, InventoryType.ACCESSORY_BAG);

        SKYBLOCK_INV.add(new SBInventoryGroup.Data(mainInventory, SBInventoryGroup.INVENTORY));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile, InventoryType.ENDER_CHEST), SBInventoryGroup.ENDER_CHEST));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile, InventoryType.PERSONAL_VAULT), SBInventoryGroup.PERSONAL_VAULT));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(accessoryInventory, SBInventoryGroup.ACCESSORY));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile, InventoryType.POTION_BAG), SBInventoryGroup.POTION));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile, InventoryType.FISHING_BAG), SBInventoryGroup.FISHING));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile, InventoryType.QUIVER), SBInventoryGroup.QUIVER));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile, InventoryType.CANDY), SBInventoryGroup.CANDY));
        SKYBLOCK_INV.add(new SBInventoryGroup.Data(SBItemUtils.decodeItem(currentProfile, InventoryType.WARDROBE), SBInventoryGroup.WARDROBE));

        this.inventoryToStats.addAll(mainInventory);
        this.inventoryToStats.addAll(accessoryInventory);
    }

    private void getSlayerInfo(JsonObject currentProfile)
    {
        JsonElement slayerBosses = currentProfile.get("slayer_bosses");
        JsonElement slayerQuest = currentProfile.get("slayer_quest");

        if (slayerQuest != null)
        {
            try
            {
                this.activeSlayerType = SBSlayers.Type.valueOf(slayerQuest.getAsJsonObject().get("type").getAsString().toUpperCase(Locale.ROOT));
                this.activeSlayerTier = 1 + slayerQuest.getAsJsonObject().get("tier").getAsInt();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        if (slayerBosses != null)
        {
            List<SkyBlockSlayerInfo> zombie = this.getSlayer(slayerBosses, SBSlayers.Type.ZOMBIE);
            List<SkyBlockSlayerInfo> spider = this.getSlayer(slayerBosses, SBSlayers.Type.SPIDER);
            List<SkyBlockSlayerInfo> wolf = this.getSlayer(slayerBosses, SBSlayers.Type.WOLF);

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
            try
            {
                Class<?> innerClass = ClientboundPlayerInfoPacket.PlayerUpdate.class;
                Constructor<?> ctor = innerClass.getDeclaredConstructor(ClientboundPlayerInfoPacket.class, GameProfile.class, int.class, GameType.class, Component.class);
                Object innerInstance = ctor.newInstance(new ClientboundPlayerInfoPacket(), this.profile, 0, null, null);
                ((InvokerClientPacketListener)this.minecraft.getConnection()).getPlayerInfoMap().put(this.profile.getId(), ((IViewerLoader)new PlayerInfo((ClientboundPlayerInfoPacket.PlayerUpdate)innerInstance)).setLoadedFromViewer(true)); // hack into map to show their skin :D
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        this.player = new SBFakePlayerEntity(this.minecraft.level, this.profile);
        SkyBlockAPIViewerScreen.renderSecondLayer = true;

        for (ItemStack armor : this.armorItems)
        {
            if (armor.isEmpty())
            {
                continue;
            }

            int index = Mob.getEquipmentSlotForItem(armor).getIndex();

            if (armor.getItem() instanceof BlockItem)
            {
                index = 3;
            }
            this.player.setItemSlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, index), armor);
        }
    }

    private List<SkyBlockSlayerInfo> getSlayer(JsonElement element, SBSlayers.Type type)
    {
        List<SkyBlockSlayerInfo> list = Lists.newArrayList();
        String lowerType = type.name().toLowerCase(Locale.ROOT);
        JsonElement slayer = element.getAsJsonObject().get(type.name().toLowerCase(Locale.ROOT));

        if (slayer != null)
        {
            JsonElement xp = slayer.getAsJsonObject().get("xp");

            if (xp != null)
            {
                int playerSlayerXp = xp.getAsInt();
                int xpRequired = 0;
                int slayerLvl = 0;
                int levelToCheck = 0;
                int xpToNextLvl = 0;
                int maxLevel = SBSlayers.SLAYERS.getLeveling().get(lowerType).length;
                boolean reachLimit = false;

                for (int i = 0; i < maxLevel; i++)
                {
                    int slayerXp = SBSlayers.SLAYERS.getLeveling().get(lowerType)[i];

                    if (slayerXp <= playerSlayerXp)
                    {
                        levelToCheck = i + 1;

                        if (levelToCheck < maxLevel)
                        {
                            xpRequired = SBSlayers.SLAYERS.getLeveling().get(lowerType)[levelToCheck];
                        }
                        ++slayerLvl;
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
                int amount = 0;

                for (int i = 1; i <= 4; i++)
                {
                    JsonElement kill = slayer.getAsJsonObject().get("boss_kills_tier_" + (i - 1));
                    int kills = this.getSlayerKill(kill);
                    amount += this.getSlayerPrice(kills, i - 1);
                    list.add(new SkyBlockSlayerInfo(ChatFormatting.GRAY + "Tier " + i + ": " + ChatFormatting.YELLOW + this.formatSlayerKill(this.getSlayerKill(kill))));
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
            case ZOMBIE:
                this.zombieSlayerLevel = currentLevel;
                break;
            case SPIDER:
                this.spiderSlayerLevel = currentLevel;
                break;
            case WOLF:
                this.wolfSlayerLevel = currentLevel;
                break;
            default:
                break;
        }
    }

    private int getSlayerKill(JsonElement element)
    {
        if (element != null)
        {
            return element.getAsInt();
        }
        return 0;
    }

    private int getSlayerPrice(int kills, int index)
    {
        int price;

        switch (index)
        {
            default:
            case 0:
                price = 100;
                break;
            case 1:
                price = 2000;
                break;
            case 2:
                price = 10000;
                break;
            case 3:
                price = 50000;
                break;
        }
        return kills * price;
    }

    private String formatSlayerKill(int kills)
    {
        return NumberUtils.NUMBER_FORMAT.format(kills) + " kill" + (kills <= 1 ? "" : "s");
    }

    private String replaceId(String id)
    {
        for (Map.Entry<String, String> sbItem : SBConstants.SKYBLOCK_ITEM_ID_REMAP.entrySet())
        {
            String sbItemId = sbItem.getKey();

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
            for (int i = 0; i < this.player.inventory.armor.size(); i++)
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
        for (ItemStack armor : this.armorItems.stream().filter(this.not(ItemStack::isEmpty)).collect(Collectors.toList()))
        {
            try
            {
                EquipmentSlot type = Mob.getEquipmentSlotForItem(armor);
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
        RenderSystem.pushMatrix();
        RenderSystem.translatef(posX, posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        PoseStack matrixstack = new PoseStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        float scale = 40F;
        matrixstack.scale(scale, scale, scale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(-180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(-10.0F);
        Quaternion quaternion2 = Vector3f.YP.rotationDegrees(-190.0F);
        quaternion.mul(quaternion1);
        matrixstack.mulPose(quaternion);
        matrixstack.mulPose(quaternion2);
        entity.yRot = (float)(Math.atan(0) * 40.0F);
        entity.yHeadRot = entity.yRot;
        EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderermanager.overrideCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> entityrenderermanager.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880));
        irendertypebuffer$impl.endBatch();
        entityrenderermanager.setRenderShadow(true);
        RenderSystem.popMatrix();
    }

    private static void renderEntity(int posX, int posY, float mouseX, float mouseY, LivingEntity livingEntity)
    {
        float f = (float)Math.atan(mouseX / 40.0F);
        float f1 = (float)Math.atan(mouseY / 40.0F);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(posX, posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        PoseStack matrixstack = new PoseStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        float scale = 40F;
        matrixstack.scale(scale, scale, scale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.mul(quaternion1);
        matrixstack.mulPose(quaternion);
        float f2 = livingEntity.yBodyRot;
        float f3 = livingEntity.yRot;
        float f4 = livingEntity.xRot;
        float f5 = livingEntity.yHeadRotO;
        float f6 = livingEntity.yHeadRot;
        livingEntity.yBodyRot = 180.0F + f * 20.0F;
        livingEntity.yRot = 180.0F + f * 40.0F;
        livingEntity.xRot = -f1 * 20.0F;
        livingEntity.yHeadRot = livingEntity.yRot;
        livingEntity.yHeadRotO = livingEntity.yRot;
        EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderermanager.overrideCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> entityrenderermanager.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880));
        irendertypebuffer$impl.endBatch();
        entityrenderermanager.setRenderShadow(true);
        livingEntity.yBodyRot = f2;
        livingEntity.yRot = f3;
        livingEntity.xRot = f4;
        livingEntity.yHeadRotO = f5;
        livingEntity.yHeadRot = f6;
        RenderSystem.popMatrix();
    }

    private void drawItemStackSlot(PoseStack matrixStack, int x, int y, ItemStack itemStack)
    {
        this.drawSprite(matrixStack, x + 1, y + 1);
        RenderSystem.enableRescaleNormal();
        this.itemRenderer.renderGuiItem(itemStack, x + 2, y + 2);
        RenderSystem.disableRescaleNormal();
    }

    private void drawSprite(PoseStack matrixStack, int left, int top)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GuiComponent.STATS_ICON_LOCATION);
        GuiComponent.blit(matrixStack, left, top, this.getBlitOffset(), 0, 0, 18, 18, 128, 128);
    }

    private String getTextPercentage(int current, int total)
    {
        int size = 16;

        if (current > total)
        {
            throw new IllegalArgumentException();
        }

        int donePercents = 100 * current / total;
        int doneLength = size * donePercents / 100;

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < size; i++)
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
            int x = info ? -62 : -52;
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

            for (int columns = 0; columns < 4; ++columns)
            {
                for (int rows = 0; rows < 9; ++rows)
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
            int i = (this.itemList.size() + 9 - 1) / 9 - 4;
            int j = (int)(scroll * i + 0.5D);

            if (j < 0)
            {
                j = 0;
            }

            for (int k = 0; k < 4; ++k)
            {
                for (int l = 0; l < 9; ++l)
                {
                    int i1 = l + (k + j) * 9;

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
        protected void drawPanel(PoseStack matrixStack, int index, int left, int right, int top) {}

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
        protected void drawPanel(PoseStack matrixStack, int index, int left, int right, int top)
        {
            SkyBlockInfo stat = this.stats.get(index);
            boolean isCurrentUpgrade = stat.title.equals("Current Upgrade");
            this.font.draw(matrixStack, stat.title + (isCurrentUpgrade ? SkyBlockProfileSelectorScreen.downloadingStates[(int)(Util.getMillis() / 250L % SkyBlockProfileSelectorScreen.downloadingStates.length)] : ""), SkyBlockAPIViewerScreen.this.guiLeft - 20, top, stat.hex != null ? ColorUtils.hexToDecimal(stat.hex) : index % 2 == 0 ? 16777215 : 9474192);
            this.font.draw(matrixStack, stat.getValue(), SkyBlockAPIViewerScreen.this.guiLeft - this.font.width(stat.getValue()) + 195, top, stat.hex != null ? ColorUtils.hexToDecimal(stat.hex) : index % 2 == 0 ? 16777215 : 9474192);
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
        protected void drawPanel(PoseStack matrixStack, int index, int left, int right, int top)
        {
            SkyBlockSlayerInfo stat = this.stats.get(index);

            if (stat.type == SkyBlockSlayerInfo.Type.XP_AND_MOB)
            {
                if (stat.text.equals("Zombie"))
                {
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
                else if (stat.text.equals("Spider"))
                {
                    Spider spider = new Spider(EntityType.SPIDER, this.world);
                    CaveSpider cave = new CaveSpider(EntityType.CAVE_SPIDER, this.world);
                    SkyBlockAPIViewerScreen.renderEntity(SkyBlockAPIViewerScreen.this.guiLeft - 30, top + 40, cave);
                    SkyBlockAPIViewerScreen.renderEntity(SkyBlockAPIViewerScreen.this.guiLeft - 30, top + 60, spider);
                    RenderSystem.blendFunc(770, 771);
                }
                else
                {
                    Wolf wolf = new Wolf(EntityType.WOLF, this.world);
                    wolf.setRemainingPersistentAngerTime(Integer.MAX_VALUE);
                    SkyBlockAPIViewerScreen.renderEntity(SkyBlockAPIViewerScreen.this.guiLeft - 30, top + 60, wolf);
                }

                float[] color = ColorUtils.toFloatArray(0, 255, 255);
                boolean reachLimit = stat.reachLimit;

                if (reachLimit)
                {
                    color = ColorUtils.toFloatArray(255, 185, 0);
                }

                this.mc.getTextureManager().bind(XP_BARS);
                RenderSystem.color4f(color[0], color[1], color[2], 1.0F);
                RenderSystem.disableBlend();

                String[] xpSplit = stat.xp.split(",");
                int playerSlayerXp = Integer.parseInt(xpSplit[0]);
                int xpRequired = Integer.parseInt(xpSplit[1]);
                int filled = stat.reachLimit ? 91 : Math.min((int)Math.floor(playerSlayerXp * 92F / xpRequired), 91);
                GuiComponent.blit(matrixStack, SkyBlockAPIViewerScreen.this.guiLeft + 90, top, 0, 0, 91, 5, 91, 10);

                if (filled > 0)
                {
                    GuiComponent.blit(matrixStack, SkyBlockAPIViewerScreen.this.guiLeft + 90, top, 0, 5, filled, 5, 91, 10);
                }

                RenderSystem.enableBlend();
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
            else
            {
                if (this.getSize() == 1)
                {
                    this.font.draw(matrixStack, stat.text, SkyBlockAPIViewerScreen.this.guiLeft + 200, top, 16777215);
                }
                else
                {
                    this.font.draw(matrixStack, stat.text, SkyBlockAPIViewerScreen.this.guiLeft - this.font.width(stat.text) + 180, top, 16777215);
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
        protected void drawPanel(PoseStack matrixStack, int index, int left, int right, int top)
        {
            if (!this.stats.isEmpty())
            {
                Object obj = this.stats.get(index);

                if (obj instanceof SBStats)
                {
                    SBStats stat = (SBStats)obj;
                    MutableComponent component = stat.getName().copy();

                    if (this.font.width(component) > 200)
                    {
                        component.setStyle(component.getStyle().withFont(ClientUtils.UNICODE));
                    }

                    this.font.draw(matrixStack, component, SkyBlockAPIViewerScreen.this.guiLeft - 85, top, index % 2 == 0 ? 16777215 : 9474192);
                    this.font.draw(matrixStack, stat.getValueByString(), SkyBlockAPIViewerScreen.this.guiLeft - this.font.width(stat.getValueByString()) + 180, top, index % 2 == 0 ? 16777215 : 9474192);
                }
                else if (obj instanceof BankHistory.Stats)
                {
                    BankHistory.Stats stat = (BankHistory.Stats)obj;
                    this.font.draw(matrixStack, stat.getStats(), SkyBlockAPIViewerScreen.this.guiLeft - 55, top, 16777215);
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
        protected void drawPanel(PoseStack matrixStack, int index, int left, int right, int top)
        {
            SBCollections collection = this.collection.get(index);

            if (collection.getCollectionType() != null)
            {
                if (!collection.getItemStack().isEmpty())
                {
                    String collectionLvl = collection.getCollectionType() == SBCollections.Type.UNKNOWN ? "" : " " + ChatFormatting.GOLD + collection.getLevel();
                    this.parent.drawItemStackSlot(matrixStack, this.parent.guiLeft - 65, top, collection.getItemStack());
                    this.font.draw(matrixStack, (collection.getCollectionType() == SBCollections.Type.UNKNOWN ? ChatFormatting.RED : "") + collection.getItemStack().getHoverName().getString() + collectionLvl, this.parent.guiLeft - 41, top + 6, 16777215);
                    this.font.draw(matrixStack, collection.getCollectionAmount(), this.parent.guiLeft - this.font.width(collection.getCollectionAmount()) + 170, top + 6, index % 2 == 0 ? 16777215 : 9474192);
                }
                else
                {
                    this.font.draw(matrixStack, collection.getCollectionType().getName(), this.parent.guiLeft - 65, top + 5, 16777215);
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
        protected void drawPanel(PoseStack matrixStack, int index, int left, int right, int top)
        {
            SBMinions.CraftedInfo craftedMinion = this.craftMinions.get(index);

            if (!craftedMinion.getMinionItem().isEmpty())
            {
                this.parent.drawItemStackSlot(matrixStack, this.parent.guiLeft - 102, top, craftedMinion.getMinionItem());
                this.font.draw(matrixStack, craftedMinion.getDisplayName() + " " + ChatFormatting.GOLD + craftedMinion.getMinionMaxTier(), this.parent.guiLeft - 79, top + 6, 16777215);
                this.font.draw(matrixStack, craftedMinion.getCraftedTiers(), this.parent.guiLeft - this.font.width(craftedMinion.getCraftedTiers()) + 202, top + 6, index % 2 == 0 ? 16777215 : 9474192);
            }
            else
            {
                if (craftedMinion.getMinionName() != null)
                {
                    this.font.draw(matrixStack, craftedMinion.getMinionName(), this.parent.guiLeft - 100, top + 5, 16777215);
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