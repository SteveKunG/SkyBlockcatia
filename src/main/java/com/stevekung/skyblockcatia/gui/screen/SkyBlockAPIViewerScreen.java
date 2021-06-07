package com.stevekung.skyblockcatia.gui.screen;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.event.handler.ClientEventHandler;
import com.stevekung.skyblockcatia.gui.widget.GuiErrorInfoScrollingList;
import com.stevekung.skyblockcatia.gui.widget.button.GuiButtonItem;
import com.stevekung.skyblockcatia.integration.textoverflow.TooltipOverflow;
import com.stevekung.skyblockcatia.keybinding.KeyBindingsSB;
import com.stevekung.skyblockcatia.utils.*;
import com.stevekung.skyblockcatia.utils.skyblock.*;
import com.stevekung.skyblockcatia.utils.skyblock.api.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.client.GuiScrollingList;

public class SkyBlockAPIViewerScreen extends GuiScreen
{
    private static final ResourceLocation INVENTORY_TABS = new ResourceLocation("skyblockcatia:textures/gui/tabs.png");
    private static final ResourceLocation XP_BARS = new ResourceLocation("skyblockcatia:textures/gui/skill_xp_bar.png");
    private static final String[] REVENANT_HORROR_HEAD = new String[] {"0862e0b0-a14f-3f93-894f-013502936b59", "dbad99ed3c820b7978190ad08a934a68dfa90d9986825da1c97f6f21f49ad626"};

    // Based stuff
    private boolean firstLoad;
    private boolean loadingApi = true;
    private boolean error;
    private String errorMessage;
    private String statusMessage;
    private GuiButton doneButton;
    private GuiButton backButton;
    private GuiButtonItem showArmorButton;
    private SkyblockProfiles.Profile skyblockProfiles;
    private List<ProfileDataCallback> profiles;
    private final String sbProfileId;
    private final String sbProfileName;
    private final String username;
    private final String displayName;
    private final String gameMode;
    private final String guild;
    private final String uuid;
    private final GameProfile profile;
    private final StopWatch watch = new StopWatch();
    private GuiScrollingList currentSlot;
    private int currentSlotId = -1;
    private int currentBasicSlotId = -1;
    private int currentOthersSlotId = -1;
    private ViewButton viewButton = ViewButton.PLAYER;
    private OthersViewButton othersButton = OthersViewButton.KILLS;
    private BasicInfoViewButton basicInfoButton = BasicInfoViewButton.PLAYER_STATS;
    private boolean updated;
    private final ViewerData data = new ViewerData();
    private int skillCount;
    private GuiScrollingList errorInfo;
    private List<String> errorList = new ArrayList<>();
    private boolean showArmor = true;
    private float oldMouseX;
    private float oldMouseY;

    // API
    private static final Pattern STATS_PATTERN = Pattern.compile("(?<type>Strength|Crit Chance|Crit Damage|Health|Defense|Speed|Intelligence|True Defense|Sea Creature Chance|Magic Find|Pet Luck|Bonus Attack Speed|Ferocity|Ability Damage|Mining Speed|Mining Fortune|Farming Fortune|Foraging Fortune): (?<value>(?:\\+|\\-)[0-9,.]+)?(?:\\%){0,1}(?:(?: HP(?: \\((?:\\+|\\-)[0-9,.]+ HP\\)){0,1}(?: \\(\\w+ (?:\\+|\\-)[0-9,.]+ HP\\)){0,1})|(?: \\((?:\\+|\\-)[0-9,.]+\\))|(?: \\(\\w+ (?:\\+|\\-)[0-9,.]+(?:\\%){0,1}\\))){0,1}(?: \\((?:\\+|\\-)[0-9,.]+ HP\\)){0,1}");
    private static final ModDecimalFormat FORMAT = new ModDecimalFormat("#,###");
    private static final ModDecimalFormat FORMAT_2 = new ModDecimalFormat("#,###.#");
    private static final ModDecimalFormat NUMBER_FORMAT_WITH_SYMBOL = new ModDecimalFormat("+#;-#");
    private static final ModDecimalFormat SKILL_AVG = new ModDecimalFormat("##.#");
    private static final Map<String, String> SKYBLOCK_ITEM_ID_REMAP = ImmutableMap.<String, String>builder().put("seeds", "wheat_seeds").put("raw_chicken", "chicken").put("carrot_item", "carrot").put("potato_item", "potato").put("sulphur", "gunpowder").put("mushroom_collection", "red_mushroom").put("sugar_cane", "reeds").put("pork", "porkchop").put("nether_stalk", "nether_wart").put("raw_fish", "fish").put("ink_sack", "dye").put("water_lily", "waterlily").put("ender_stone", "end_stone").put("log_2", "log2").put("snow_ball", "snowball").build();
    private static final Map<String, ItemStack> ENCHANTED_ID_TO_ITEM = ImmutableMap.<String, ItemStack>builder().put("enchanted_mithril", new ItemStack(Items.prismarine_crystals)).put("enchanted_iron", new ItemStack(Items.iron_ingot)).put("enchanted_endstone", new ItemStack(Blocks.end_stone)).put("enchanted_gold", new ItemStack(Items.gold_ingot)).put("enchanted_lapis_lazuli", new ItemStack(Items.dye, 1, 4)).put("enchanted_titanium", RenderUtils.getSkullItemStack("deb23698-94ea-3571-bb89-cd37ba5d15d8", "3dcc0ec9873f4f8d407ba0a0f983e257787772eaf8784e226a61c7f727ac9e26")).put("enchanted_dark_oak_log", new ItemStack(Blocks.log2, 1, 1)).build();
    public static boolean renderSecondLayer;
    private static final Gson GSON = new Gson();
    private final List<SkyBlockInfo> infoList = new ArrayList<>();
    private final List<SBSkills.Info> skillLeftList = new ArrayList<>();
    private final List<SBSkills.Info> skillRightList = new ArrayList<>();
    private final List<SkyBlockSlayerInfo> slayerInfo = new ArrayList<>();
    private final List<SBStats.Display> sbKills = new ArrayList<>();
    private final List<SBStats.Display> sbDeaths = new ArrayList<>();
    private final List<SBStats.Display> sbOthers = new ArrayList<>();
    private final List<BankHistory.Stats> sbBankHistories = new ArrayList<>();
    private final List<SBMinions.CraftedInfo> sbCraftedMinions = new ArrayList<>();
    private final List<ItemStack> armorItems = new ArrayList<>();
    private final List<ItemStack> inventoryToStats = new ArrayList<>();
    private final List<SBCollections> collections = new ArrayList<>();
    private List<SkyBlockInfo> jacobInfo = new ArrayList<>();
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
    private static final SBInventoryTabs.InventoryExtended TEMP_INVENTORY = new SBInventoryTabs.InventoryExtended(SkyBlockAPIViewerScreen.SIZE);
    private static final SBInventoryTabs.InventoryExtended TEMP_ARMOR_INVENTORY = new SBInventoryTabs.InventoryExtended(4);
    public static final List<SBInventoryTabs.Data> SKYBLOCK_INV = new ArrayList<>();
    private int selectedTabIndex = SBInventoryTabs.INVENTORY.getTabIndex();
    private float currentScroll;
    private boolean isScrolling;
    private boolean wasClicking;
    private final ContainerSkyBlock skyBlockContainer;
    private ContainerArmor skyBlockArmorContainer;

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
    private BonusStatTemplate allStat = BonusStatTemplate.getDefault();

    // GuiContainer fields
    private int xSize;
    private int ySize;
    private int guiLeft;
    private int guiTop;
    private Slot theSlot;

    public SkyBlockAPIViewerScreen(List<ProfileDataCallback> profiles, ProfileDataCallback callback)
    {
        this.firstLoad = true;
        this.allowUserInput = true;
        this.skyBlockContainer = new ContainerSkyBlock();
        this.skyBlockArmorContainer = new ContainerArmor(true);
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
    public void initGui()
    {
        this.buttonList.add(this.doneButton = new GuiButton(0, this.width / 2 - 154, this.height - 25, 150, 20, LangUtils.translate("gui.close")));
        this.buttonList.add(this.backButton = new GuiButton(1, this.width / 2 + 4, this.height - 25, 150, 20, LangUtils.translate("gui.back")));
        this.buttonList.add(this.showArmorButton = new GuiButtonItem(2, this.width / 2 - 115, this.height / 2 - 65, new ItemStack(Items.diamond_chestplate), "Show Armor: " + EnumChatFormatting.GREEN + "ON"));
        GuiButton infoButton = new GuiButton(ViewButton.PLAYER.id, this.width / 2 - 197, 6, 70, 20, LangUtils.translate("gui.sb_view_player"));
        infoButton.enabled = false;
        this.buttonList.add(infoButton);
        this.buttonList.add(new GuiButton(ViewButton.SKILLS.id, this.width / 2 - 116, 6, 70, 20, LangUtils.translate("gui.sb_view_skills")));
        this.buttonList.add(new GuiButton(ViewButton.SLAYERS.id, this.width / 2 - 35, 6, 70, 20, LangUtils.translate("gui.sb_view_slayers")));
        this.buttonList.add(new GuiButton(ViewButton.DUNGEONS.id, this.width / 2 + 45, 6, 70, 20, LangUtils.translate("gui.sb_view_dungeons")));
        this.buttonList.add(new GuiButton(ViewButton.OTHERS.id, this.width / 2 + 126, 6, 70, 20, LangUtils.translate("gui.sb_view_others")));

        GuiButton statKillsButton = new GuiButton(OthersViewButton.KILLS.id, this.width / 2 - 170, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.kills"));
        statKillsButton.enabled = false;
        this.buttonList.add(statKillsButton);
        this.buttonList.add(new GuiButton(OthersViewButton.DEATHS.id, this.width / 2 - 84, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.deaths")));
        this.buttonList.add(new GuiButton(OthersViewButton.OTHER_STATS.id, this.width / 2 + 4, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.others_stats")));
        this.buttonList.add(new GuiButton(OthersViewButton.BANK_HISTORY.id, this.width / 2 + 90, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.bank_history")));

        for (GuiButton viewButton : this.buttonList)
        {
            if (OthersViewButton.getTypeForButton(viewButton) != null)
            {
                viewButton.visible = false;
            }
        }

        GuiButton basicInfoButton = new GuiButton(BasicInfoViewButton.PLAYER_STATS.id, this.width / 2 - 170, this.height - 48, 80, 20, LangUtils.translate("gui.sb_player_stats"));
        basicInfoButton.enabled = false;
        this.buttonList.add(basicInfoButton);
        this.buttonList.add(new GuiButton(BasicInfoViewButton.INVENTORY.id, this.width / 2 - 84, this.height - 48, 80, 20, LangUtils.translate("gui.sb_inventory")));
        this.buttonList.add(new GuiButton(BasicInfoViewButton.COLLECTIONS.id, this.width / 2 + 4, this.height - 48, 80, 20, LangUtils.translate("gui.sb_collections")));
        this.buttonList.add(new GuiButton(BasicInfoViewButton.CRAFTED_MINIONS.id, this.width / 2 + 90, this.height - 48, 80, 20, LangUtils.translate("gui.sb_crafted_minions")));

        for (GuiButton viewButton : this.buttonList)
        {
            if (BasicInfoViewButton.getTypeForButton(viewButton) != null)
            {
                viewButton.visible = true;
            }
        }

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

        if (!this.updated)
        {
            for (GuiButton button : this.buttonList)
            {
                ViewButton viewType = ViewButton.getTypeForButton(button);
                BasicInfoViewButton basicInfoType = BasicInfoViewButton.getTypeForButton(button);
                OthersViewButton othersType = OthersViewButton.getTypeForButton(button);

                if (viewType != null)
                {
                    if (button.id == ViewButton.SKILLS.id)
                    {
                        if (!this.data.hasSkills())
                        {
                            button.enabled = false;
                            continue;
                        }
                    }
                    if (button.id == ViewButton.SLAYERS.id)
                    {
                        if (!this.data.hasSlayers())
                        {
                            button.enabled = false;
                            continue;
                        }
                    }
                    if (button.id == ViewButton.DUNGEONS.id)
                    {
                        if (!this.data.hasDungeons())
                        {
                            button.enabled = false;
                            continue;
                        }
                    }
                    if (button.id == ViewButton.OTHERS.id)
                    {
                        if (!this.data.hasOthersTab())
                        {
                            button.enabled = false;
                            continue;
                        }
                    }
                    button.enabled = this.viewButton != viewType;
                }
                if (basicInfoType != null)
                {
                    if (button.id == BasicInfoViewButton.INVENTORY.id)
                    {
                        if (!this.data.hasInventories())
                        {
                            button.enabled = false;
                            continue;
                        }
                    }
                    if (button.id == BasicInfoViewButton.COLLECTIONS.id)
                    {
                        if (!this.data.hasCollections())
                        {
                            button.enabled = false;
                            continue;
                        }
                    }
                    if (button.id == BasicInfoViewButton.CRAFTED_MINIONS.id)
                    {
                        if (!this.data.hasMinions())
                        {
                            button.enabled = false;
                            continue;
                        }
                    }
                    button.enabled = this.basicInfoButton != basicInfoType;
                }
                if (othersType != null)
                {
                    if (button.id == OthersViewButton.KILLS.id)
                    {
                        if (!this.data.hasKills())
                        {
                            button.enabled = false;
                            continue;
                        }
                    }
                    if (button.id == OthersViewButton.DEATHS.id)
                    {
                        if (!this.data.hasDeaths())
                        {
                            button.enabled = false;
                            continue;
                        }
                    }
                    if (button.id == OthersViewButton.OTHER_STATS.id)
                    {
                        if (!this.data.hasOthers())
                        {
                            button.enabled = false;
                            continue;
                        }
                    }
                    if (button.id == OthersViewButton.BANK_HISTORY.id)
                    {
                        if (!this.data.hasBankHistory())
                        {
                            button.enabled = false;
                            continue;
                        }
                    }
                    button.enabled = this.othersButton != othersType;
                }
            }
            if (this.errorInfo != null)
            {
                this.errorInfo = new GuiErrorInfoScrollingList(this, this.width - 39, this.height, 40, this.height / 4 + 128, 19, 12, this.width, this.height, this.errorList);
            }
            this.updated = true;
        }

        int i = this.selectedTabIndex;
        this.selectedTabIndex = -1;
        this.setCurrentTab(SBInventoryTabs.tabArray[i]);

        this.guiLeft = (this.width - this.xSize) / 2 + 50;
        this.guiTop = (this.height - this.ySize) / 2 + 10;

        if (this.currentSlotId == -1 || this.currentSlotId == ViewButton.PLAYER.id)
        {
            if (this.currentBasicSlotId == -1 || this.currentBasicSlotId == BasicInfoViewButton.PLAYER_STATS.id)
            {
                this.currentSlot = new InfoStats(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, this.infoList);
                this.showArmorButton.visible = true;
                this.showArmorButton.xPosition = this.width / 2 - 114;
            }
            else if (this.currentBasicSlotId == BasicInfoViewButton.INVENTORY.id)
            {
                this.currentSlot = new EmptyStats(this.mc, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, EmptyStats.Type.INVENTORY);
                this.setCurrentTab(SBInventoryTabs.tabArray[this.selectedTabIndex]);
                this.showArmorButton.visible = true;
                this.showArmorButton.xPosition = this.width / 2 - 104;
            }
            else if (this.currentBasicSlotId == BasicInfoViewButton.COLLECTIONS.id)
            {
                this.currentSlot = new SkyBlockCollections(this, this.width - 119, this.height, 40, this.height - 50, 59, 20, this.width, this.height, this.collections);
                this.showArmorButton.visible = false;
            }
            else if (this.currentBasicSlotId == BasicInfoViewButton.CRAFTED_MINIONS.id)
            {
                this.currentSlot = new SkyBlockCraftedMinions(this, this.width - 99, this.height, 40, this.height - 70, 49, 20, this.width, this.height, this.sbCraftedMinions);
                this.showArmorButton.visible = false;
            }
        }
        else if (this.currentSlotId == ViewButton.SKILLS.id)
        {
            this.currentSlot = new EmptyStats(this.mc, this.width - 119, this.height, 40, this.height - 28, 59, 12, this.width, this.height, EmptyStats.Type.SKILL);
            this.hideOthersButton();
            this.hideBasicInfoButton();
            this.showArmorButton.visible = false;
        }
        else if (this.currentSlotId == ViewButton.SLAYERS.id)
        {
            this.currentSlot = new SlayerStats(this, this.width - 119, this.height, 40, this.height - 50, 59, 16, this.width, this.height, this.slayerInfo);
            this.hideOthersButton();
            this.hideBasicInfoButton();
            this.showArmorButton.visible = false;
        }
        else if (this.currentSlotId == ViewButton.DUNGEONS.id)
        {
            this.currentSlot = new EmptyStats(this.mc, this.width - 119, this.height, 40, this.height - 28, 59, 12, this.width, this.height, EmptyStats.Type.DUNGEON);
            this.hideOthersButton();
            this.hideBasicInfoButton();
            this.showArmorButton.visible = false;
        }
        else if (this.currentSlotId == ViewButton.OTHERS.id)
        {
            List<?> list = null;

            if (this.currentOthersSlotId == -1 || this.currentOthersSlotId == OthersViewButton.KILLS.id)
            {
                list = this.sbKills;
            }
            else if (this.currentOthersSlotId == OthersViewButton.DEATHS.id)
            {
                list = this.sbDeaths;
            }
            else if (this.currentOthersSlotId == OthersViewButton.OTHER_STATS.id)
            {
                list = this.sbOthers;
            }
            else if (this.currentOthersSlotId == OthersViewButton.BANK_HISTORY.id)
            {
                list = this.sbBankHistories;
            }

            if (list != null)
            {
                this.currentSlot = new Others(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, list);
            }
            this.hideBasicInfoButton();
            this.showArmorButton.visible = false;

            for (GuiButton viewButton : this.buttonList)
            {
                OthersViewButton type2 = OthersViewButton.getTypeForButton(viewButton);

                if (type2 != null)
                {
                    viewButton.visible = true;

                    if (viewButton.id == OthersViewButton.KILLS.id)
                    {
                        if (!this.data.hasKills())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    if (viewButton.id == OthersViewButton.DEATHS.id)
                    {
                        if (!this.data.hasDeaths())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    if (viewButton.id == OthersViewButton.OTHER_STATS.id)
                    {
                        if (!this.data.hasOthers())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    if (viewButton.id == OthersViewButton.BANK_HISTORY.id)
                    {
                        if (!this.data.hasBankHistory())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }

                    viewButton.enabled = this.othersButton != type2;
                }
            }
        }

        if (this.error)
        {
            this.updateErrorButton();
        }
    }

    @Override
    public void updateScreen()
    {
        if (this.player != null)
        {
            this.player.onUpdate();
        }
    }

    @Override
    public void onResize(Minecraft mc, int width, int height)
    {
        this.updated = false;
        super.onResize(mc, width, height);
    }

    @Override
    public void onGuiClosed()
    {
        TEMP_INVENTORY.clear();
        TEMP_ARMOR_INVENTORY.clear();
        SKYBLOCK_INV.clear();
        this.mc.getNetHandler().getPlayerInfoMap().removeIf(network -> ((IViewerLoader)network).isLoadedFromViewer());
        SkyBlockAPIViewerScreen.renderSecondLayer = false;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            this.actionPerformedViewInfo(button);
            this.actionPerformedOthers(button);
            this.actionPerformedBasicInfo(button);

            if (button.id == 0)
            {
                this.mc.displayGuiScreen(this.error ? new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.GuiState.SEARCH, this.username, this.displayName, this.guild, this.profiles) : null);
            }
            else if (button.id == 1)
            {
                this.mc.displayGuiScreen(this.profiles.size() == 0 ? new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.GuiState.EMPTY, this.username, this.displayName, "") : new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.GuiState.SEARCH, this.username, this.displayName, this.guild, this.profiles));
            }
            else if (button.id == 2)
            {
                if (this.showArmor)
                {
                    for (int i = 0; i < this.player.inventory.armorInventory.length; i++)
                    {
                        this.player.setCurrentItemOrArmor(i, null);
                    }
                    this.showArmorButton.setName("Show Armor: " + EnumChatFormatting.RED + "OFF");
                    this.showArmor = false;
                }
                else
                {
                    this.setPlayerArmors();
                    this.showArmorButton.setName("Show Armor: " + EnumChatFormatting.GREEN + "ON");
                    this.showArmor = true;
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == KeyBindingsSB.KEY_SB_VIEW_RECIPE.getKeyCode())
        {
            if (this.theSlot != null && this.theSlot.getHasStack() && this.theSlot.getStack().hasTagCompound())
            {
                NBTTagCompound extraAttrib = this.theSlot.getStack().getTagCompound().getCompoundTag("ExtraAttributes");

                if (extraAttrib.hasKey("id"))
                {
                    String itemId = extraAttrib.getString("id");
                    ClientUtils.printClientMessage(JsonUtils.create("Click to view ").appendSibling(JsonUtils.create(this.theSlot.getStack().getDisplayName()).setChatStyle(JsonUtils.gold()).appendSibling(JsonUtils.create(" recipe").setChatStyle(JsonUtils.green()))).setChatStyle(JsonUtils.green().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewrecipe " + itemId))));
                }
            }
        }
        if (keyCode == 1)
        {
            this.actionPerformed(this.backButton);
        }
        else if (keyCode == 63)
        {
            this.skyblockProfiles = null;
            this.mc.displayGuiScreen(new SkyBlockAPIViewerScreen(this.profiles, new ProfileDataCallback(this.sbProfileId, this.sbProfileName, this.username, this.displayName, this.gameMode, this.guild, this.uuid, this.profile, -1)));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int state) throws IOException
    {
        if (this.loadingApi)
        {
            return;
        }
        else
        {
            if (state == 0 && this.currentSlot instanceof EmptyStats && ((EmptyStats)this.currentSlot).getType() == EmptyStats.Type.INVENTORY)
            {
                int i = mouseX - this.guiLeft;
                int j = mouseY - this.guiTop;

                for (SBInventoryTabs tab : SBInventoryTabs.tabArray)
                {
                    if (this.isMouseOverTab(tab, i, j) && !tab.isDisabled())
                    {
                        this.setCurrentTab(tab);
                        return;
                    }
                }
            }
            super.mouseClicked(mouseX, mouseY, state);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.loadingApi)
        {
            return;
        }
        else
        {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.oldMouseX = mouseX;
        this.oldMouseY = mouseY;

        if (this.loadingApi)
        {
            String text = "Downloading SkyBlock stats";
            int i = this.fontRendererObj.getStringWidth(text);
            this.drawCenteredString(this.fontRendererObj, text, this.width / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2 - 35, 16777215);
            this.drawString(this.fontRendererObj, SkyBlockProfileSelectorScreen.downloadingStates[(int)(Minecraft.getSystemTime() / 500L % SkyBlockProfileSelectorScreen.downloadingStates.length)], this.width / 2 + i / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2 - 35, 16777215);
            this.drawCenteredString(this.fontRendererObj, "Status: " + EnumChatFormatting.GRAY + this.statusMessage, this.width / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2 - 15, 16777215);
            this.showArmorButton.visible = false;
        }
        else
        {
            if (this.error)
            {
                this.showArmorButton.visible = false;
                this.drawCenteredString(this.fontRendererObj, "SkyBlock API Viewer", this.width / 2, 20, 16777215);

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
                if (this.currentSlot != null)
                {
                    this.currentSlot.drawScreen(mouseX, mouseY, partialTicks);
                }

                this.drawCenteredString(this.fontRendererObj, this.displayName + EnumChatFormatting.YELLOW + " Profile: " + EnumChatFormatting.GOLD + this.sbProfileName + EnumChatFormatting.YELLOW + " Game Mode: " + this.gameMode + this.guild, this.width / 2, 29, 16777215);

                if (this.currentSlot instanceof EmptyStats)
                {
                    EmptyStats stat = (EmptyStats)this.currentSlot;

                    if (stat.getType() == EmptyStats.Type.INVENTORY)
                    {
                        boolean flag = Mouse.isButtonDown(0);
                        int i = this.guiLeft;
                        int j = this.guiTop;
                        int k = i + 182;
                        int l = j + 18;
                        int i1 = k + 14;
                        int j1 = l + 72;

                        if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1)
                        {
                            this.isScrolling = this.needsScrollBars();
                        }

                        if (!flag)
                        {
                            this.isScrolling = false;
                        }

                        this.wasClicking = flag;

                        if (this.isScrolling)
                        {
                            this.currentScroll = (mouseY - l - 7.5F) / (j1 - l - 15.0F);
                            this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
                            this.skyBlockContainer.scrollTo(this.currentScroll);
                        }

                        this.drawTabsBackgroundLayer(partialTicks, mouseX, mouseY);
                        GlStateManager.disableRescaleNormal();
                        RenderHelper.disableStandardItemLighting();
                        GlStateManager.disableLighting();
                        GlStateManager.disableDepth();
                    }
                }

                super.drawScreen(mouseX, mouseY, partialTicks);

                if (this.currentSlot instanceof InfoStats)
                {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableDepth();
                    SkyBlockAPIViewerScreen.drawEntityOnScreen(this.width / 2 - 106, this.height / 2 + 40, 40, this.guiLeft - 55 - this.oldMouseX, this.guiTop + 25 - this.oldMouseY, this.player);

                    this.drawContainerSlot(mouseX, mouseY, true);

                    if (this.theSlot != null && this.theSlot.getHasStack())
                    {
                        this.renderToolTip(this.theSlot.getStack(), mouseX, mouseY);
                    }
                }
                else if (this.currentSlot instanceof EmptyStats)
                {
                    EmptyStats stat = (EmptyStats)this.currentSlot;

                    if (stat.getType() == EmptyStats.Type.INVENTORY)
                    {
                        this.drawContainerSlot(mouseX, mouseY, false);

                        RenderHelper.disableStandardItemLighting();
                        this.drawTabsForegroundLayer();
                        RenderHelper.enableGUIStandardItemLighting();

                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.disableLighting();

                        SkyBlockAPIViewerScreen.drawEntityOnScreen(this.width / 2 - 96, this.height / 2 + 40, 40, this.guiLeft - 46 - this.oldMouseX, this.guiTop + 75 - 50 - this.oldMouseY, this.player);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                        for (SBInventoryTabs tab : SBInventoryTabs.tabArray)
                        {
                            if (tab == null)
                            {
                                continue;
                            }
                            if (this.renderTabsHoveringText(tab, mouseX, mouseY))
                            {
                                break;
                            }
                        }
                        if (this.theSlot != null && this.theSlot.getHasStack())
                        {
                            this.renderToolTip(this.theSlot.getStack(), mouseX, mouseY);
                        }
                    }
                    else if (stat.getType() == EmptyStats.Type.DUNGEON)//TODO
                    {
                        int i = 0;

                        for (String dungeon : this.dungeonData)
                        {
                            int x = this.width / 2 - 150;
                            int y = 50;
                            int textY = y + 12 * i;
                            this.drawString(this.fontRendererObj, dungeon, x, textY, -1);
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
                                extraCap = EnumChatFormatting.GOLD + " (+" + this.farmingLevelCap + ")";
                            }

                            this.renderSkillBar(info.getName(), x, barY, x + 46, textY, info.getCurrentXp(), info.getXpRequired(), info.getCurrentLvl(), info.isReachLimit(), extraCap);
                            ++i;
                        }

                        i = 0;

                        for (SBSkills.Info info : this.skillRightList)
                        {
                            int x = this.width / 2 + 30;
                            int y = height + 12;
                            int barY = y + 20 + height * i;
                            int textY = y + height * i;
                            this.renderSkillBar(info.getName(), x, barY, x + 46, textY, info.getCurrentXp(), info.getXpRequired(), info.getCurrentLvl(), info.isReachLimit(), "");
                            ++i;
                        }

                        if (this.skillAvg != null)
                        {
                            String avg = "AVG: " + this.skillAvg;
                            this.drawString(ColorUtils.unicodeFontRenderer, avg, this.width - ColorUtils.unicodeFontRenderer.getStringWidth(avg) - 60, this.height - 38, 16777215);
                        }
                    }
                }
                else if (this.currentSlot instanceof SlayerStats)
                {
                    String total1 = EnumChatFormatting.GRAY + "Total Amount Spent: " + EnumChatFormatting.YELLOW + FORMAT.format(this.slayerTotalAmountSpent);
                    String total2 = EnumChatFormatting.GRAY + "Total Slayer XP: " + EnumChatFormatting.YELLOW + FORMAT.format(this.totalSlayerXp);
                    this.drawString(this.fontRendererObj, total1, this.width - this.fontRendererObj.getStringWidth(total1) - 60, this.height - 36, 16777215);
                    this.drawString(this.fontRendererObj, total2, this.width - this.fontRendererObj.getStringWidth(total2) - 60, this.height - 46, 16777215);

                    if (this.activeSlayerType != null)
                    {
                        this.drawString(this.fontRendererObj, EnumChatFormatting.GRAY + "Active Slayer: ", 60, this.height - 46, 16777215);
                        this.drawString(this.fontRendererObj, EnumChatFormatting.YELLOW + this.activeSlayerType.getName() + " - Tier " + this.activeSlayerTier, 60, this.height - 36, 16777215);
                    }
                }
                else if (this.currentSlot instanceof SkyBlockCraftedMinions)
                {
                    String total1 = EnumChatFormatting.GRAY + "Unique Minions: " + EnumChatFormatting.YELLOW + this.craftedMinionCount + "/" + SBMinions.MINIONS.getUniqueMinions() + EnumChatFormatting.GRAY + " (" + this.craftedMinionCount * 100 / SBMinions.MINIONS.getUniqueMinions() + "%)";
                    String total2 = EnumChatFormatting.GRAY + "Current Minion Slot: " + EnumChatFormatting.YELLOW + this.currentMinionSlot + (this.additionalMinionSlot > 0 ? EnumChatFormatting.GOLD + " (Bonus +" + this.additionalMinionSlot + ")" : "");
                    this.drawString(this.fontRendererObj, total1, this.width - this.fontRendererObj.getStringWidth(total1) - 60, this.height - 68, 16777215);
                    this.drawString(this.fontRendererObj, total2, this.width - this.fontRendererObj.getStringWidth(total2) - 60, this.height - 58, 16777215);
                }
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.enableDepth();
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        boolean scroll = true;

        if (CompatibilityUtils.isTextOverflowScrollLoaded)
        {
            scroll = !TooltipOverflow.INSTANCE.checkCanScroll();
        }

        if (i != 0 && this.needsScrollBars() && scroll)
        {
            int j = this.skyBlockContainer.itemList.size() / 9 - 4;

            if (i > 0)
            {
                i = 1;
            }
            if (i < 0)
            {
                i = -1;
            }
            this.currentScroll = (float)(this.currentScroll - (double)i / (double)j);
            this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
            this.skyBlockContainer.scrollTo(this.currentScroll);
        }
    }

    @Override
    public void drawHoveringText(List<String> textLines, int x, int y)
    {
        super.drawHoveringText(textLines, x, y);
    }

    // Input
    private void actionPerformedViewInfo(GuiButton button)
    {
        ViewButton type = ViewButton.getTypeForButton(button);

        if (type != null)
        {
            this.viewButton = type;

            for (GuiButton viewButton : this.buttonList)
            {
                ViewButton type2 = ViewButton.getTypeForButton(viewButton);

                if (type2 != null)
                {
                    if (type2.id == ViewButton.SKILLS.id)
                    {
                        if (!this.data.hasSkills())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    if (type2.id == ViewButton.SLAYERS.id)
                    {
                        if (!this.data.hasSlayers())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    if (type2.id == ViewButton.DUNGEONS.id)
                    {
                        if (!this.data.hasDungeons())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    if (type2.id == ViewButton.OTHERS.id)
                    {
                        if (!this.data.hasOthersTab())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    viewButton.enabled = this.viewButton != type2;
                }
            }

            if (type.id == ViewButton.PLAYER.id)
            {
                if (this.currentBasicSlotId == -1 || this.currentBasicSlotId == BasicInfoViewButton.PLAYER_STATS.id)
                {
                    this.currentSlot = new InfoStats(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, this.infoList);
                    this.showArmorButton.visible = true;
                    this.showArmorButton.xPosition = this.width / 2 - 115;
                }
                else if (this.currentBasicSlotId == BasicInfoViewButton.INVENTORY.id)
                {
                    this.currentSlot = new EmptyStats(this.mc, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, EmptyStats.Type.INVENTORY);
                    this.setCurrentTab(SBInventoryTabs.tabArray[this.selectedTabIndex]);
                    this.showArmorButton.visible = true;
                    this.showArmorButton.xPosition = this.width / 2 - 105;
                }
                else if (this.currentBasicSlotId == BasicInfoViewButton.COLLECTIONS.id)
                {
                    this.currentSlot = new SkyBlockCollections(this, this.width - 119, this.height, 40, this.height - 50, 59, 20, this.width, this.height, this.collections);
                    this.showArmorButton.visible = false;
                }
                else if (this.currentBasicSlotId == BasicInfoViewButton.CRAFTED_MINIONS.id)
                {
                    this.currentSlot = new SkyBlockCraftedMinions(this, this.width - 99, this.height, 40, this.height - 70, 49, 20, this.width, this.height, this.sbCraftedMinions);
                    this.showArmorButton.visible = false;
                }

                this.currentSlotId = ViewButton.PLAYER.id;
                this.hideOthersButton();

                for (GuiButton viewButton : this.buttonList)
                {
                    BasicInfoViewButton type2 = BasicInfoViewButton.getTypeForButton(viewButton);

                    if (type2 != null)
                    {
                        viewButton.visible = true;

                        if (type2.id == BasicInfoViewButton.INVENTORY.id)
                        {
                            if (!this.data.hasInventories())
                            {
                                viewButton.enabled = false;
                                continue;
                            }
                        }
                        if (type2.id == BasicInfoViewButton.COLLECTIONS.id)
                        {
                            if (!this.data.hasCollections())
                            {
                                viewButton.enabled = false;
                                continue;
                            }
                        }
                        if (type2.id == BasicInfoViewButton.CRAFTED_MINIONS.id)
                        {
                            if (!this.data.hasMinions())
                            {
                                viewButton.enabled = false;
                                continue;
                            }
                        }
                        viewButton.enabled = this.basicInfoButton != type2;
                    }
                }
            }
            else if (type.id == ViewButton.SKILLS.id)
            {
                this.currentSlot = new EmptyStats(this.mc, this.width - 119, this.height, 40, this.height - 28, 59, 12, this.width, this.height, EmptyStats.Type.SKILL);
                this.currentSlotId = ViewButton.SKILLS.id;
                this.hideOthersButton();
                this.hideBasicInfoButton();
                this.showArmorButton.visible = false;
            }
            else if (type.id == ViewButton.SLAYERS.id)
            {
                this.currentSlot = new SlayerStats(this, this.width - 119, this.height, 40, this.height - 50, 59, 16, this.width, this.height, this.slayerInfo);
                this.currentSlotId = ViewButton.SLAYERS.id;
                this.hideOthersButton();
                this.hideBasicInfoButton();
                this.showArmorButton.visible = false;
            }
            else if (type.id == ViewButton.DUNGEONS.id)
            {
                this.currentSlot = new EmptyStats(this.mc, this.width - 119, this.height, 40, this.height - 28, 59, 12, this.width, this.height, EmptyStats.Type.DUNGEON);
                this.currentSlotId = ViewButton.DUNGEONS.id;
                this.hideOthersButton();
                this.hideBasicInfoButton();
                this.showArmorButton.visible = false;
            }
            else if (type.id == ViewButton.OTHERS.id)
            {
                List<?> list = this.sbKills;

                if (this.currentOthersSlotId == -1 || this.currentOthersSlotId == OthersViewButton.KILLS.id)
                {
                    list = this.sbKills;
                }
                else if (this.currentOthersSlotId == OthersViewButton.DEATHS.id)
                {
                    list = this.sbDeaths;
                }
                else if (this.currentOthersSlotId == OthersViewButton.OTHER_STATS.id)
                {
                    list = this.sbOthers;
                }
                else
                {
                    list = this.sbBankHistories;
                }

                this.currentSlot = new Others(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, list);
                this.currentSlotId = ViewButton.OTHERS.id;
                this.showArmorButton.visible = false;
                this.hideBasicInfoButton();

                for (GuiButton viewButton : this.buttonList)
                {
                    OthersViewButton type2 = OthersViewButton.getTypeForButton(viewButton);

                    if (type2 != null)
                    {
                        viewButton.visible = true;

                        if (type2.id == OthersViewButton.KILLS.id)
                        {
                            if (!this.data.hasKills())
                            {
                                viewButton.enabled = false;
                                continue;
                            }
                        }
                        if (type2.id == OthersViewButton.DEATHS.id)
                        {
                            if (!this.data.hasDeaths())
                            {
                                viewButton.enabled = false;
                                continue;
                            }
                        }
                        if (type2.id == OthersViewButton.OTHER_STATS.id)
                        {
                            if (!this.data.hasOthers())
                            {
                                viewButton.enabled = false;
                                continue;
                            }
                        }
                        if (type2.id == OthersViewButton.BANK_HISTORY.id)
                        {
                            if (!this.data.hasBankHistory())
                            {
                                viewButton.enabled = false;
                                continue;
                            }
                        }
                        viewButton.enabled = this.othersButton != type2;
                    }
                }
            }
            else
            {
                this.currentSlot = null;
                this.hideOthersButton();
                this.hideBasicInfoButton();
            }
        }
    }

    private void actionPerformedOthers(GuiButton button)
    {
        OthersViewButton type = OthersViewButton.getTypeForButton(button);

        if (type != null)
        {
            this.othersButton = type;

            for (GuiButton viewButton : this.buttonList)
            {
                OthersViewButton type2 = OthersViewButton.getTypeForButton(viewButton);

                if (type2 != null)
                {
                    if (type2.id == OthersViewButton.KILLS.id)
                    {
                        if (!this.data.hasKills())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    if (type2.id == OthersViewButton.DEATHS.id)
                    {
                        if (!this.data.hasDeaths())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    if (type2.id == OthersViewButton.OTHER_STATS.id)
                    {
                        if (!this.data.hasOthers())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    if (type2.id == OthersViewButton.BANK_HISTORY.id)
                    {
                        if (!this.data.hasBankHistory())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    viewButton.enabled = this.othersButton != type2;
                }
            }

            List<?> list = null;

            if (type.id == OthersViewButton.KILLS.id)
            {
                this.currentOthersSlotId = OthersViewButton.KILLS.id;
                list = this.sbKills;
            }
            else if (type.id == OthersViewButton.DEATHS.id)
            {
                this.currentOthersSlotId = OthersViewButton.DEATHS.id;
                list = this.sbDeaths;
            }
            else if (type.id == OthersViewButton.OTHER_STATS.id)
            {
                this.currentOthersSlotId = OthersViewButton.OTHER_STATS.id;
                list = this.sbOthers;
            }
            else if (type.id == OthersViewButton.BANK_HISTORY.id)
            {
                this.currentOthersSlotId = OthersViewButton.BANK_HISTORY.id;
                list = this.sbBankHistories;
            }

            if (list != null)
            {
                this.currentSlot = new Others(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, list);
            }
        }
    }

    private void actionPerformedBasicInfo(GuiButton button)
    {
        BasicInfoViewButton type = BasicInfoViewButton.getTypeForButton(button);

        if (type != null)
        {
            this.basicInfoButton = type;

            for (GuiButton viewButton : this.buttonList)
            {
                BasicInfoViewButton type2 = BasicInfoViewButton.getTypeForButton(viewButton);

                if (type2 != null)
                {
                    if (type2.id == BasicInfoViewButton.INVENTORY.id)
                    {
                        if (!this.data.hasInventories())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    if (type2.id == BasicInfoViewButton.COLLECTIONS.id)
                    {
                        if (!this.data.hasCollections())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    if (type2.id == BasicInfoViewButton.CRAFTED_MINIONS.id)
                    {
                        if (!this.data.hasMinions())
                        {
                            viewButton.enabled = false;
                            continue;
                        }
                    }
                    viewButton.enabled = this.basicInfoButton != type2;
                }
            }

            if (type.id == BasicInfoViewButton.PLAYER_STATS.id)
            {
                this.currentSlot = new InfoStats(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, this.infoList);
                this.currentBasicSlotId = BasicInfoViewButton.PLAYER_STATS.id;
                this.showArmorButton.visible = true;
                this.showArmorButton.xPosition = this.width / 2 - 115;
                this.skyBlockArmorContainer = new ContainerArmor(true);
            }
            else if (type.id == BasicInfoViewButton.INVENTORY.id)
            {
                this.currentSlot = new EmptyStats(this.mc, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, EmptyStats.Type.INVENTORY);
                this.currentBasicSlotId = BasicInfoViewButton.INVENTORY.id;
                this.setCurrentTab(SBInventoryTabs.tabArray[this.selectedTabIndex]);
                this.showArmorButton.visible = true;
                this.showArmorButton.xPosition = this.width / 2 - 105;
                this.skyBlockArmorContainer = new ContainerArmor(false);
            }
            else if (type.id == BasicInfoViewButton.COLLECTIONS.id)
            {
                this.currentSlot = new SkyBlockCollections(this, this.width - 119, this.height, 40, this.height - 50, 59, 20, this.width, this.height, this.collections);
                this.currentBasicSlotId = BasicInfoViewButton.COLLECTIONS.id;
                this.showArmorButton.visible = false;
            }
            else if (type.id == BasicInfoViewButton.CRAFTED_MINIONS.id)
            {
                this.currentSlot = new SkyBlockCraftedMinions(this, this.width - 99, this.height, 40, this.height - 70, 49, 20, this.width, this.height, this.sbCraftedMinions);
                this.currentBasicSlotId = BasicInfoViewButton.CRAFTED_MINIONS.id;
                this.showArmorButton.visible = false;
            }
        }
    }

    private void setErrorMessage(String message, boolean errorList)
    {
        this.error = true;
        this.loadingApi = false;
        this.updateErrorButton();

        if (errorList)
        {
            this.errorInfo = new GuiErrorInfoScrollingList(this, this.width - 39, this.height, 40, this.height / 4 + 128, 19, 12, this.width, this.height, this.errorList);
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
        this.doneButton.xPosition = this.width / 2 - 75;
        this.doneButton.yPosition = this.height / 4 + 132;
        this.doneButton.displayString = LangUtils.translate("gui.back");

        for (GuiButton button : this.buttonList)
        {
            if (button != this.doneButton)
            {
                button.visible = false;
            }
        }
    }

    private boolean isMouseOverTab(SBInventoryTabs tab, int mouseX, int mouseY)
    {
        int i = tab.getTabColumn();
        int j = 28 * i;
        int k = 0;

        if (i > 0)
        {
            j += i;
        }
        if (tab.isTabInFirstRow())
        {
            k = k - 26;
        }
        else
        {
            k = k + this.ySize - 32;
        }
        boolean test = mouseX >= j + 1 && mouseX <= j + 27 && mouseY >= k && mouseY <= k + 26;
        return test;
    }

    private boolean isMouseOverSlot(Slot slot, int mouseX, int mouseY)
    {
        return this.isPointInRegion(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY);
    }

    private boolean isPointInRegion(int left, int top, int right, int bottom, int mouseX, int mouseY)
    {
        int i = this.guiLeft;
        int j = this.guiTop;
        mouseX = mouseX - i;
        mouseY = mouseY - j;
        return mouseX >= left - 1 && mouseX < left + right + 1 && mouseY >= top - 1 && mouseY < top + bottom + 1;
    }

    private void setCurrentTab(SBInventoryTabs tab)
    {
        if (tab == null)
        {
            return;
        }
        this.selectedTabIndex = tab.getTabIndex();
        ContainerSkyBlock container = this.skyBlockContainer;
        container.itemList.clear();
        tab.displayAllItems(container.itemList);
        this.currentScroll = 0.0F;
        container.scrollTo(0.0F);
    }

    private boolean needsScrollBars()
    {
        if (SBInventoryTabs.tabArray[this.selectedTabIndex] == null)
        {
            return false;
        }
        return SBInventoryTabs.tabArray[this.selectedTabIndex].hasScrollBar() && this.skyBlockContainer.canScroll();
    }

    private void hideOthersButton()
    {
        for (GuiButton viewButton : this.buttonList)
        {
            if (OthersViewButton.getTypeForButton(viewButton) != null)
            {
                viewButton.visible = false;
            }
        }
    }

    private void hideBasicInfoButton()
    {
        for (GuiButton viewButton : this.buttonList)
        {
            if (BasicInfoViewButton.getTypeForButton(viewButton) != null)
            {
                viewButton.visible = false;
            }
        }
    }

    // Render
    private void renderSkillBar(String name, int xBar, int yBar, int xText, int yText, double playerXp, int xpRequired, int currentLvl, boolean reachLimit, String extra)
    {
        ColorUtils.RGB color = ColorUtils.stringToRGB("128,255,0");

        if (reachLimit)
        {
            color = ColorUtils.stringToRGB("255,185,0");
        }

        this.mc.getTextureManager().bindTexture(XP_BARS);
        GlStateManager.color(color.floatRed(), color.floatGreen(), color.floatBlue(), 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(xBar, yBar, 0, 0, 91, 5, 91, 10);

        if (xpRequired > 0)
        {
            int filled = reachLimit ? 91 : Math.min((int)Math.floor(playerXp * 92 / xpRequired), 91);

            if (filled > 0)
            {
                Gui.drawModalRectWithCustomSizedTexture(xBar, yBar, 0, 5, filled, 5, 91, 10);
            }

            this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GRAY + name + (reachLimit ? EnumChatFormatting.GOLD : EnumChatFormatting.YELLOW) + " " + currentLvl + extra, xText, yText, 16777215);

            if (reachLimit)
            {
                this.drawCenteredString(this.fontRendererObj, NumberUtils.formatWithM(playerXp), xText, yText + 10, 16777215);
            }
            else
            {
                this.drawCenteredString(this.fontRendererObj, NumberUtils.formatCompact((long)playerXp) + "/" + NumberUtils.formatCompact(xpRequired), xText, yText + 10, 16777215);
            }
        }
        else
        {
            this.drawCenteredString(this.fontRendererObj, name, xText, yText + 8, 16777215);
        }
    }

    private void drawContainerSlot(int mouseX, int mouseY, boolean info)
    {
        int i = this.guiLeft;
        int j = this.guiTop;
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(i, j, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        this.theSlot = null;
        int k = 240;
        int l = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k / 1.0F, l / 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (!info)
        {
            for (Slot slot : this.skyBlockContainer.inventorySlots)
            {
                this.drawSlot(slot);

                if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.canBeHovered())
                {
                    this.theSlot = slot;
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    int j1 = slot.xDisplayPosition;
                    int k1 = slot.yDisplayPosition;
                    GlStateManager.colorMask(true, true, true, false);
                    this.drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                }
            }
        }

        if (this.showArmor)
        {
            for (Slot slot : this.skyBlockArmorContainer.inventorySlots)
            {
                if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.canBeHovered())
                {
                    this.theSlot = slot;
                }
            }
        }
        GlStateManager.popMatrix();
    }

    private void drawSlot(Slot slot)
    {
        int i = slot.xDisplayPosition;
        int j = slot.yDisplayPosition;
        ItemStack itemStack = slot.getStack();
        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;

        if (itemStack == null)
        {
            TextureAtlasSprite sprite = slot.getBackgroundSprite();

            if (sprite != null)
            {
                GlStateManager.disableLighting();
                this.mc.getTextureManager().bindTexture(slot.getBackgroundLocation());
                this.drawTexturedModalRect(i, j, sprite, 16, 16);
                GlStateManager.enableLighting();
            }
        }

        GlStateManager.enableDepth();
        this.itemRender.renderItemAndEffectIntoGUI(itemStack, i, j);

        int slotLeft = slot.xDisplayPosition;
        int slotTop = slot.yDisplayPosition;
        int slotRight = slotLeft + 16;
        int slotBottom = slotTop + 16;
        int green = ColorUtils.to32BitColor(150, 85, 255, 85);

        if (itemStack != null && itemStack.hasTagCompound() && itemStack.getTagCompound().getBoolean("active"))
        {
            this.drawGradientRect(slotLeft, slotTop, slotRight, slotBottom, green, green);
        }

        this.renderItemOverlayIntoGUI(itemStack, i, j);
        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    private void renderItemOverlayIntoGUI(ItemStack itemStack, int xPosition, int yPosition)
    {
        if (itemStack != null)
        {
            if (itemStack.stackSize != 1)
            {
                FontRenderer fontRenderer = this.fontRendererObj;
                String stackSize = NumberUtils.formatCompact(itemStack.stackSize);
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();

                if (itemStack.stackSize >= 100)
                {
                    fontRenderer = ColorUtils.unicodeFontRenderer;
                }

                fontRenderer.drawStringWithShadow(stackSize, xPosition + 19 - 2 - fontRenderer.getStringWidth(stackSize), yPosition + 6 + 3, 16777215);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
            if (itemStack.getItem().showDurabilityBar(itemStack))
            {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                double health = itemStack.getItem().getDurabilityForDisplay(itemStack);
                int i = Math.round(13.0F - (float)health * 13.0F);
                int j = MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - health)) / 3.0F, 1.0F, 1.0F);
                this.itemRender.func_181565_a(worldrenderer, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                this.itemRender.func_181565_a(worldrenderer, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
    }

    private boolean renderTabsHoveringText(SBInventoryTabs tab, int mouseX, int mouseY)
    {
        int i = tab.getTabColumn();
        int j = 28 * i;
        int k = 0;

        if (i > 0)
        {
            j += i;
        }
        if (tab.isTabInFirstRow())
        {
            k = k - 28;
        }
        else
        {
            k = k + this.ySize - 32;
        }

        if (this.isPointInRegion(j + 2, k + 3, 25, 25, mouseX, mouseY))
        {
            this.drawHoveringText(Collections.singletonList(tab.getTranslatedTabLabel()), mouseX, mouseY);
            return true;
        }
        else
        {
            return false;
        }
    }

    private void drawTabsBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        SBInventoryTabs tab = SBInventoryTabs.tabArray[this.selectedTabIndex];

        for (SBInventoryTabs tab1 : SBInventoryTabs.tabArray)
        {
            this.mc.getTextureManager().bindTexture(INVENTORY_TABS);

            if (tab1 == null)
            {
                continue;
            }
            if (tab1.getTabIndex() != this.selectedTabIndex)
            {
                this.drawTab(tab1);
            }
        }

        this.mc.getTextureManager().bindTexture(new ResourceLocation("skyblockcatia:textures/gui/tab_" + tab.getBackgroundTexture()));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.guiLeft + 182;
        int j = this.guiTop + 18;
        int k = j + 72;
        this.mc.getTextureManager().bindTexture(INVENTORY_TABS);

        if (tab.hasScrollBar())
        {
            this.drawTexturedModalRect(i, j + (int)((k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
        }
        this.drawTab(tab);
    }

    private void drawTabsForegroundLayer()
    {
        SBInventoryTabs tab = SBInventoryTabs.tabArray[this.selectedTabIndex];

        if (tab != null)
        {
            GlStateManager.disableBlend();
            this.fontRendererObj.drawString(tab.getTranslatedTabLabel(), this.guiLeft + 11, this.guiTop + 6, 4210752);
        }
    }

    private void drawTab(SBInventoryTabs tab)
    {
        boolean flag = tab.getTabIndex() == this.selectedTabIndex;
        boolean flag1 = tab.isTabInFirstRow();
        int i = tab.getTabColumn();
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

        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        this.drawTexturedModalRect(l, i1, j, k, 28, j1);
        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;
        l = l + 6;
        i1 = i1 + 8 + (flag1 ? 1 : -1);
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        ItemStack itemStack = tab.getIcon();
        this.itemRender.renderItemAndEffectIntoGUI(itemStack, l, i1);
        this.itemRender.renderItemOverlays(this.fontRendererObj, itemStack, l, i1);

        if (tab.isDisabled())
        {
            GlStateManager.disableDepth();
            itemStack = new ItemStack(Blocks.barrier);
            this.itemRender.renderItemAndEffectIntoGUI(itemStack, l, i1);
            this.itemRender.renderItemOverlays(this.fontRendererObj, itemStack, l, i1);
            GlStateManager.enableDepth();
        }

        GlStateManager.disableLighting();
        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    // Player Data
    private void getPlayerData() throws IOException
    {
        this.statusMessage = "Getting Player Data";
        Map<String, SkyblockProfiles.Members> profiles = null;
        SkyblockProfiles.Banking banking = null;
        CommunityUpgrades communityUpgrade = null;

        if (this.skyblockProfiles == null)
        {
            URL url = new URL(SBAPIUtils.SKYBLOCK_PROFILE + this.sbProfileId);
            SkyblockProfiles.DirectProfile profile = GSON.fromJson(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8), SkyblockProfiles.DirectProfile.class);

            if (profile.getProfile() == null)
            {
                this.setErrorMessage("No API data returned, please try again later!", false);
                return;
            }
            profiles = profile.getProfile().getMembers();
            banking = profile.getProfile().getBanking();
            communityUpgrade = profile.getProfile().getCommunityUpgrades();
        }
        else
        {
            profiles = this.skyblockProfiles.getMembers();
            banking = this.skyblockProfiles.getBanking();
            communityUpgrade = this.skyblockProfiles.getCommunityUpgrades();
        }

        for (Map.Entry<String, SkyblockProfiles.Members> entry : profiles.entrySet())
        {
            String userUUID = entry.getKey();
            SkyblockProfiles.Members currentUserProfile = profiles.get(userUUID);
            this.getCraftedMinions(currentUserProfile);

            if (banking != null)
            {
                this.getBankHistories(banking);
            }
            this.data.setHasBankHistory(banking != null && this.sbBankHistories.size() > 0);
        }

        this.processCraftedMinions();
        String checkUUID = this.uuid;

        for (Map.Entry<String, SkyblockProfiles.Members> entry : profiles.entrySet())
        {
            String userUUID = entry.getKey();
            checkUUID = userUUID;

            if (userUUID.equals(this.uuid))
            {
                SkyblockProfiles.Members currentUserProfile = profiles.get(userUUID);
                URL urlStatus = new URL("https://api.hypixel.net/status?key=" + SkyBlockcatiaConfig.hypixelApiKey + "&uuid=" + this.uuid);
                GameStatus status = GSON.fromJson(IOUtils.toString(urlStatus.openConnection().getInputStream(), StandardCharsets.UTF_8), GameStatus.class);

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

                for (SBInventoryTabs tab : SBInventoryTabs.tabArray)
                {
                    if (tab.isDisabled())
                    {
                        this.totalDisabledInv++;
                    }
                }

                this.data.setHasInventories(this.totalDisabledInv != 12);
                this.allStat.setEffectiveHealth(this.allStat.getDefense() <= 0 ? this.allStat.getHealth() : (int)(this.allStat.getHealth() * (1 + this.allStat.getDefense() / 100.0D)));
                this.getBasicInfo(currentUserProfile, banking, status, userUUID, communityUpgrade);
                break;
            }
        }

        if (!checkUUID.equals(this.uuid))
        {
            this.setErrorMessage("Current Player UUID not matched Profile UUID, please try again later!", false);
            return;
        }

        for (GuiButton viewButton : this.buttonList)
        {
            ViewButton type2 = ViewButton.getTypeForButton(viewButton);

            if (type2 != null)
            {
                if (type2.id == ViewButton.SKILLS.id)
                {
                    if (!this.data.hasSkills())
                    {
                        viewButton.enabled = false;
                        continue;
                    }
                }
                if (type2.id == ViewButton.SLAYERS.id)
                {
                    if (!this.data.hasSlayers())
                    {
                        viewButton.enabled = false;
                        continue;
                    }
                }
                if (type2.id == ViewButton.DUNGEONS.id)
                {
                    if (!this.data.hasDungeons())
                    {
                        viewButton.enabled = false;
                        continue;
                    }
                }
                if (type2.id == ViewButton.OTHERS.id)
                {
                    if (!this.data.hasOthersTab())
                    {
                        viewButton.enabled = false;
                        continue;
                    }
                }
            }
        }
        for (GuiButton viewButton : this.buttonList)
        {
            BasicInfoViewButton type2 = BasicInfoViewButton.getTypeForButton(viewButton);

            if (type2 != null)
            {
                if (type2.id == BasicInfoViewButton.INVENTORY.id)
                {
                    if (!this.data.hasInventories())
                    {
                        viewButton.enabled = false;
                        continue;
                    }
                }
                if (type2.id == BasicInfoViewButton.COLLECTIONS.id)
                {
                    if (!this.data.hasCollections())
                    {
                        viewButton.enabled = false;
                        continue;
                    }
                }
                if (type2.id == BasicInfoViewButton.CRAFTED_MINIONS.id)
                {
                    if (!this.data.hasMinions())
                    {
                        viewButton.enabled = false;
                        continue;
                    }
                }
            }
        }
        this.loadingApi = false;
        this.showArmorButton.visible = true;
    }

    private List<SkyBlockInfo> getJacobData(SkyblockProfiles.Jacob jacob)
    {
        List<SkyBlockInfo> info = Lists.newArrayList();
        SkyblockProfiles.MedalInventory medals = jacob.getMedals();
        SkyblockProfiles.FarmingPerks perks = jacob.getPerks();

        if (medals != null)
        {
            String gold = ColorUtils.stringToRGB("255,215,0").toColoredFont();
            String silver = ColorUtils.stringToRGB("192,192,192").toColoredFont();
            String bronze = ColorUtils.stringToRGB("205,127,50").toColoredFont();
            info.add(new SkyBlockInfo(gold + "Gold Medal", gold + medals.getGold()));
            info.add(new SkyBlockInfo(silver + "Silver Medal", silver + medals.getSilver()));
            info.add(new SkyBlockInfo(bronze + "Bronze Medal", bronze + medals.getBronze()));
        }
        if (perks != null)
        {
            info.add(new SkyBlockInfo("Double Drops Perk", perks.getDoubleDrops() * 2 + "%"));
            this.farmingLevelCap = perks.getLevelCap();
            info.add(new SkyBlockInfo("Farming Level Cap", String.valueOf(this.farmingLevelCap)));
        }
        return info;
    }

    private List<SkyBlockInfo> getCommunityUpgrades(CommunityUpgrades communityUpgrades)
    {
        List<SkyBlockInfo> info = new ArrayList<>();
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
                String color = tier == maxed ? EnumChatFormatting.GOLD.toString() : "";

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
    private final List<String> dungeonData = new ArrayList<>();
    private void getDungeons(SkyblockProfiles.Members currentUserProfile)//TODO
    {
        JsonElement dungeon = currentUserProfile.getDungeons();
        int i = 0;

        if (dungeon != null)
        {
            //Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //System.out.println(gson.toJson(dungeon));
            this.dungeonData.add(EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD + "WORK IN PROGRESS! NOT A FINAL GUI!");
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
                this.dungeonData.add(EnumChatFormatting.RED + info.getName() + EnumChatFormatting.RESET + ", Level: " + info.getCurrentLvl() + " " + (int)Math.floor(info.getCurrentXp()) + "/" + info.getXpRequired());
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
                    this.dungeonData.add(EnumChatFormatting.RED + info2.getName() + EnumChatFormatting.RESET + ", Level: " + info2.getCurrentLvl() + " " + (int)Math.floor(info2.getCurrentXp()) + "/" + info2.getXpRequired());
                    i++;
                }
            }

            this.dungeonData.add("");
            StringBuilder builder = new StringBuilder();

            if (tierCompletion != null)
            {
                for (Map.Entry<String, JsonElement> entry : tierCompletion.getAsJsonObject().entrySet().stream().filter(entry -> !entry.getKey().equals("0")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).entrySet())
                {
                    builder.append("Floor: " + entry.getKey() + "/" + FORMAT.format(entry.getValue().getAsInt()) + ", ");
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
        double currentXp = 0;

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

    private void getBankHistories(SkyblockProfiles.Banking banking)
    {
        BankHistory[] bankHistory = banking.getTransactions();
        Collections.reverse(Arrays.asList(bankHistory));

        if (bankHistory.length > 0)
        {
            for (BankHistory bank : bankHistory)
            {
                this.sbBankHistories.add(new BankHistory.Stats(EnumChatFormatting.DARK_GRAY + "------------------------------"));
                this.sbBankHistories.add(new BankHistory.Stats("Initiator: " + (bank.getName().equals("Bank Interest") ? ColorUtils.stringToRGB("255,215,0").toColoredFont() + bank.getName() : bank.getName())));
                this.sbBankHistories.add(new BankHistory.Stats(bank.getAction().name + " " + EnumChatFormatting.GOLD + NumberUtils.formatWithM(bank.getAmount()) + EnumChatFormatting.RESET + " about " + CommonUtils.getRelativeTime(bank.getTimestamp())));
            }
            this.sbBankHistories.add(new BankHistory.Stats(EnumChatFormatting.DARK_GRAY + "------------------------------"));
        }
    }

    private String getLocation(GameStatus status)
    {
        GameStatus.Session session = status.getSession();
        String locationText = "";

        if (session.isOnline())
        {
            String gameType = session.getGameType();
            String mode = session.getMode();

            if (gameType.equals("SKYBLOCK"))
            {
                return SBStats.STATS.getCurrentLocations().getOrDefault(mode, mode);
            }
        }
        return locationText;
    }

    private void getCraftedMinions(SkyblockProfiles.Members currentProfile)
    {
        String[] craftedGenerators = currentProfile.getCraftedGenerators();

        if (craftedGenerators != null && craftedGenerators.length > 0)
        {
            for (String craftedMinion : craftedGenerators)
            {
                String[] split = craftedMinion.split("_");
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

        List<SBMinions.Info> minionLevels = new ArrayList<>();
        List<SBMinions.Data> minionDatas = new ArrayList<>();
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
            int[] dummyTiers = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
            Collection<Integer> craftedList = entry.getValue();
            StringBuilder builder = new StringBuilder();
            int[] craftedTiers = Ints.toArray(craftedList);
            List<String> minionList = new ArrayList<>();
            Set<Integer> dummySet = new HashSet<>();
            Set<Integer> skippedList = new HashSet<>();
            SBMinions.Type type = SBMinions.MINIONS.getTypeByName(minionType);

            if (type != null && type.hasTier12())
            {
                dummyTiers = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
            }
            if (type == null)
            {
                LoggerIN.warning("Found an unknown minion!, type: {}", minionType);
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
                minionList.add(EnumChatFormatting.RED + "" + skipped);
            }
            for (int crafted : craftedList)
            {
                minionList.add(EnumChatFormatting.GREEN + "" + crafted);
            }

            minionList.sort((text1, text2) -> new CompareToBuilder().append(Integer.parseInt(EnumChatFormatting.getTextWithoutFormattingCodes(text1)), Integer.parseInt(EnumChatFormatting.getTextWithoutFormattingCodes(text2))).build());
            int i = 0;

            for (String allTiers : minionList)
            {
                builder.append(allTiers.substring(0, allTiers.length() - (allTiers.length() == 4 ? 2 : 1)) + NumberUtils.intToRoman(Integer.parseInt(allTiers.substring(2))));

                if (i < minionList.size() - 1)
                {
                    builder.append(" ");
                }
                ++i;
            }
            minionDatas.add(new SBMinions.Data(minionType, builder.toString()));
        }

        List<SBMinions.CraftedInfo> farmingMinion = new ArrayList<>();
        List<SBMinions.CraftedInfo> miningMinion = new ArrayList<>();
        List<SBMinions.CraftedInfo> combatMinion = new ArrayList<>();
        List<SBMinions.CraftedInfo> foragingMinion = new ArrayList<>();
        List<SBMinions.CraftedInfo> fishingMinion = new ArrayList<>();
        List<SBMinions.CraftedInfo> unknownMinion = new ArrayList<>();
        SBMinions.CraftedInfo dummy = new SBMinions.CraftedInfo(null, null, 0, null, null, null);
        String displayName = null;
        ItemStack itemStack = null;
        SBSkills.Type category = null;
        Comparator<SBMinions.CraftedInfo> com = (cm1, cm2) -> new CompareToBuilder().append(cm1.getMinionName(), cm2.getMinionName()).build();

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

            SBMinions.CraftedInfo min = new SBMinions.CraftedInfo(minionData.getMinionType(), displayName, level, minionData.getCraftedTiers(), itemStack, category);

            switch (category)
            {
            case FARMING:
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
            default:
                unknownMinion.add(min);
                break;
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
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo("Farming", null, 0, null, null, null));
            this.sbCraftedMinions.addAll(farmingMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!miningMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo("Mining", null, 0, null, null, null));
            this.sbCraftedMinions.addAll(miningMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!combatMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo("Combat", null, 0, null, null, null));
            this.sbCraftedMinions.addAll(combatMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!foragingMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo("Foraging", null, 0, null, null, null));
            this.sbCraftedMinions.addAll(foragingMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!fishingMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo("Fishing", null, 0, null, null, null));
            this.sbCraftedMinions.addAll(fishingMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!unknownMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo("Unknown", null, 0, null, null, null));
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
        Map<String, Integer> collections = currentProfile.getCollection();
        String[] unlockedTiers = currentProfile.getUnlockedCollections();
        Multimap<String, Integer> skyblockCollectionMap = HashMultimap.create();

        if (unlockedTiers != null && unlockedTiers.length > 0)
        {
            for (String unlockedTier : unlockedTiers)
            {
                String[] split = unlockedTier.toLowerCase(Locale.ROOT).split("_");
                String unlockedId = split.length >= 3 ? split[0] + "_" + split[1] : split[0];
                int unlockedLvl = Integer.parseInt(split[split.length - 1]);
                skyblockCollectionMap.put(this.replaceId(unlockedId), unlockedLvl);
            }
        }

        SBCollections dummyCollection = new SBCollections(null, null, -1, -1);

        if (collections != null && collections.entrySet().size() > 0)
        {
            List<SBCollections> farming = new ArrayList<>();
            List<SBCollections> mining = new ArrayList<>();
            List<SBCollections> combat = new ArrayList<>();
            List<SBCollections> foraging = new ArrayList<>();
            List<SBCollections> fishing = new ArrayList<>();
            List<SBCollections> unknown = new ArrayList<>();

            for (Map.Entry<String, Integer> collection : collections.entrySet())
            {
                String collectionId = this.replaceId(collection.getKey().toLowerCase(Locale.ROOT));
                int collectionCount = collection.getValue();
                String[] split = collectionId.split(":");
                String itemId = split[0];
                int meta = 0;
                int level = 0;

                try
                {
                    meta = Integer.parseInt(split[1]);
                }
                catch (Exception e) {}

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

                Item item = Item.getByNameOrId(itemId);
                ItemStack itemStack = null;

                if (item == null)
                {
                    ItemStack unknownCollection = new ItemStack(Blocks.barrier, 0, meta);

                    if (itemId.startsWith("enchanted_"))
                    {
                        item = Item.getByNameOrId(itemId.replace("enchanted_", ""));

                        if (item == null)
                        {
                            item = Item.getItemFromBlock(Blocks.barrier);
                        }

                        unknownCollection = new ItemStack(item, 0, meta);
                        NBTTagCompound compound = new NBTTagCompound();
                        compound.setTag("ench", new NBTTagList());
                        unknownCollection.setTagCompound(compound);
                        unknownCollection.setStackDisplayName(WordUtils.capitalize(itemId.replace("_", " ")));
                    }

                    item = Item.getItemFromBlock(Blocks.barrier);
                    itemStack = unknownCollection;
                }
                else
                {
                    itemStack = new ItemStack(item, 0, meta);
                }

                if (item == Item.getItemFromBlock(Blocks.cobblestone) || item == Items.coal || item == Items.iron_ingot || item == Items.gold_ingot || item == Items.diamond || item == Items.emerald || item == Items.redstone
                        || item == Items.quartz || item == Item.getItemFromBlock(Blocks.obsidian) || item == Items.glowstone_dust || item == Item.getItemFromBlock(Blocks.gravel) || item == Item.getItemFromBlock(Blocks.ice) || item == Item.getItemFromBlock(Blocks.netherrack)
                        || item == Item.getItemFromBlock(Blocks.sand) || item == Item.getItemFromBlock(Blocks.end_stone) || item == Items.dye && meta == 4 || itemId.equals("mithril_ore"))
                {
                    if (itemId.equals("mithril_ore"))
                    {
                        itemStack = new ItemStack(Items.prismarine_crystals);
                        itemStack.setStackDisplayName(EnumChatFormatting.RESET + "Mithril Ore");
                    }
                    mining.add(new SBCollections(itemStack, SBCollections.Type.MINING, collectionCount, level));
                }
                else if (item == Items.rotten_flesh || item == Items.bone || item == Items.string || item == Items.spider_eye || item == Items.gunpowder || item == Items.ender_pearl || item == Items.ghast_tear || item == Items.slime_ball || item == Items.blaze_rod || item == Items.magma_cream)
                {
                    combat.add(new SBCollections(itemStack, SBCollections.Type.COMBAT, collectionCount, level));
                }
                else if (item == Item.getItemFromBlock(Blocks.log) || item == Item.getItemFromBlock(Blocks.log2))
                {
                    foraging.add(new SBCollections(itemStack, SBCollections.Type.FORAGING, collectionCount, level));
                }
                else if (item == Items.fish || item == Items.prismarine_shard || item == Items.prismarine_crystals || item == Items.clay_ball || item == Item.getItemFromBlock(Blocks.waterlily) || item == Item.getItemFromBlock(Blocks.sponge) || item == Items.dye && meta == 0)
                {
                    fishing.add(new SBCollections(itemStack, SBCollections.Type.FISHING, collectionCount, level));
                }
                else if (item == Items.reeds || item == Item.getItemFromBlock(Blocks.pumpkin) || item == Items.carrot || item == Items.wheat || item == Items.potato || item == Items.melon || item == Items.dye && meta == 3 || item == Items.feather || item == Items.chicken
                        || item == Items.porkchop || item == Items.mutton || item == Items.leather || item == Item.getItemFromBlock(Blocks.red_mushroom) || item == Items.nether_wart || item == Items.rabbit || item == Items.wheat_seeds || item == Item.getItemFromBlock(Blocks.cactus))
                {
                    farming.add(new SBCollections(itemStack, SBCollections.Type.FARMING, collectionCount, level));
                }
                else
                {
                    unknown.add(new SBCollections(itemStack, SBCollections.Type.UNKNOWN, collectionCount, level));
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
                this.collections.add(new SBCollections(null, SBCollections.Type.FARMING, -1, -1));
                this.collections.addAll(farming);
            }
            if (!mining.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SBCollections(null, SBCollections.Type.MINING, -1, -1));
                this.collections.addAll(mining);
            }
            if (!combat.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SBCollections(null, SBCollections.Type.COMBAT, -1, -1));
                this.collections.addAll(combat);
            }
            if (!foraging.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SBCollections(null, SBCollections.Type.FORAGING, -1, -1));
                this.collections.addAll(foraging);
            }
            if (!fishing.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SBCollections(null, SBCollections.Type.FISHING, -1, -1));
                this.collections.addAll(fishing);
            }
            if (!unknown.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SBCollections(null, SBCollections.Type.UNKNOWN, -1, -1));
                this.collections.addAll(unknown);
            }
            this.collections.add(dummyCollection);
        }
        else
        {
            this.data.setHasCollections(false);
        }
    }

    private void getSacks(SkyblockProfiles.Members currentProfile)
    {
        List<ItemStack> sacks = new ArrayList<>();

        try
        {
            Multimap<String, Pair<Integer, Integer>> runes = HashMultimap.create();

            if (currentProfile.getSacks() != null && !currentProfile.getSacks().isEmpty())
            {
                for (Map.Entry<String, Integer> sackEntry : currentProfile.getSacks().entrySet())
                {
                    int count = sackEntry.getValue();
                    String sackId = this.replaceId(sackEntry.getKey().toLowerCase(Locale.ROOT));
                    String[] split = sackId.split(":");
                    String itemId = split[0];
                    int meta = 0;

                    try
                    {
                        meta = Integer.parseInt(split[1]);
                    }
                    catch (Exception e) {}

                    Item item = Item.getByNameOrId(itemId);

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
                            if (item == null)
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
                                    runes.put(runeName, Pair.of(runeLevel, count));
                                }
                                else if (itemId.startsWith("enchanted_"))
                                {
                                    item = Item.getByNameOrId(itemId.replace("enchanted_", ""));

                                    if (item == null)
                                    {
                                        item = Item.getItemFromBlock(Blocks.barrier);
                                    }

                                    ItemStack enchantedItem = ENCHANTED_ID_TO_ITEM.getOrDefault(itemId, new ItemStack(item, count, meta));
                                    enchantedItem.stackSize = count;

                                    if (enchantedItem.hasTagCompound())
                                    {
                                        enchantedItem.getTagCompound().setTag("ench", new NBTTagList());
                                    }
                                    else
                                    {
                                        NBTTagCompound compound = new NBTTagCompound();
                                        compound.setTag("ench", new NBTTagList());
                                        enchantedItem.setTagCompound(compound);
                                    }
                                    enchantedItem.setStackDisplayName(EnumChatFormatting.RESET.toString() + WordUtils.capitalize(itemId.replace("_", " ")));
                                    this.addSackItemStackCount(enchantedItem, null, true, sacks);
                                }
                                else
                                {
                                    this.addSackItemStackCount(new ItemStack(Blocks.barrier, count), EnumChatFormatting.RED + itemId, false, sacks);
                                }
                            }
                            else
                            {
                                ItemStack itemStack = new ItemStack(item, count, meta);
                                this.addSackItemStackCount(itemStack, null, false, sacks);
                            }
                        }
                    }
                }
                this.parseRunes(runes, sacks);
                sacks.sort((itemStack1, itemStack2) -> new CompareToBuilder().append(itemStack2.stackSize, itemStack1.stackSize).build());
            }
            else
            {
                ItemStack barrier = new ItemStack(Blocks.barrier);
                barrier.setStackDisplayName(EnumChatFormatting.RESET.toString() + EnumChatFormatting.RED + "Sacks is not available!");

                for (int i = 0; i < 36; ++i)
                {
                    sacks.add(barrier);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        SKYBLOCK_INV.add(new SBInventoryTabs.Data(sacks, SBInventoryTabs.SACKS));
    }

    private <T extends Enum> boolean matchSackId(String itemId, T[] enums)
    {
        return Arrays.stream(enums).anyMatch(drop -> itemId.contains(drop.name().toLowerCase(Locale.ROOT)));
    }

    private void checkSlayerSack(String itemId, int count, List<ItemStack> sacks)
    {
        try
        {
            SBSlayers.Drops slayerDrops = SBSlayers.Drops.valueOf(itemId.toUpperCase(Locale.ROOT));
            this.addSackItemStackCount(new ItemStack(slayerDrops.getBaseItem(), count), slayerDrops.getDisplayName(), true, sacks);
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
            ItemStack itemStack = dungeonDrops.getBaseItem();
            itemStack.stackSize = count;
            this.addSackItemStackCount(itemStack, dungeonDrops.getDisplayName(), dungeonDrops.isEnchanted(), sacks);
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
            ItemStack itemStack = candySacks.getBaseItem();
            itemStack.stackSize = count;
            this.addSackItemStackCount(itemStack, candySacks.getDisplayName(), false, sacks);
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
            ItemStack itemStack = dwarvenSacks.getBaseItem();
            itemStack.stackSize = count;
            this.addSackItemStackCount(itemStack, dwarvenSacks.getDisplayName(), dwarvenSacks == DwarvenSacks.TITANIUM_ORE, sacks);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void parseRunes(Multimap<String, Pair<Integer, Integer>> runes, List<ItemStack> sacks)
    {
        for (Map.Entry<String, Collection<Pair<Integer, Integer>>> entry : runes.asMap().entrySet())
        {
            RuneSacks rune = RuneSacks.byName(entry.getKey());
            ItemStack base = rune.getBaseItem();
            List<Pair<Integer, Integer>> sortedLvl = entry.getValue().stream().collect(Collectors.toCollection(ArrayList::new));
            Collections.sort(sortedLvl);
            int sum = sortedLvl.stream().collect(Collectors.summingInt(Pair::getRight));
            base.stackSize = sum;

            if (rune == RuneSacks.UNKNOWN)
            {
                this.addSackItemStackCount(base, rune.getDisplayName() + " - " + entry.getKey(), false, sacks);
            }
            else
            {
                NBTTagList loreList = new NBTTagList();

                for (Pair<Integer, Integer> level : sortedLvl)
                {
                    loreList.appendTag(new NBTTagString(EnumChatFormatting.WHITE + NumberUtils.intToRoman(level.getLeft()) + ":" + EnumChatFormatting.GRAY + " x" + level.getRight()));
                }

                base.getSubCompound("display", true).setTag("Lore", loreList);
                this.addSackItemStackCount(base, rune.getDisplayName(), true, sacks);
            }
        }
    }

    private void addSackItemStackCount(ItemStack itemStack, @Nullable String altName, boolean ench, List<ItemStack> sacks)
    {
        if (itemStack.stackSize >= 1000)
        {
            if (!StringUtils.isNullOrEmpty(altName))
            {
                itemStack.setStackDisplayName(altName + EnumChatFormatting.GRAY + " x" + FORMAT.format(itemStack.stackSize));
            }
            else
            {
                itemStack.setStackDisplayName(EnumChatFormatting.RESET + itemStack.getDisplayName() + EnumChatFormatting.GRAY + " x" + FORMAT.format(itemStack.stackSize));
            }
        }
        else
        {
            if (!StringUtils.isNullOrEmpty(altName))
            {
                itemStack.setStackDisplayName(altName);
            }
        }

        if (ench)
        {
            itemStack.getTagCompound().setTag("ench", new NBTTagList());
        }
        sacks.add(itemStack);
    }

    private void getPets(SkyblockProfiles.Members currentUserProfile)
    {
        List<SBPets.Data> petData = new ArrayList<>();
        List<ItemStack> petItem = new ArrayList<>();
        SkyblockProfiles.Pets[] pets = currentUserProfile.getPets();

        if (pets == null || pets.length <= 0)
        {
            this.totalDisabledInv++;
            SKYBLOCK_INV.add(new SBInventoryTabs.Data(Collections.singletonList(new ItemStack(Blocks.barrier)), SBInventoryTabs.PET));
            return;
        }

        for (SkyblockProfiles.Pets pet : pets)
        {
            double exp = pet.getExp();
            String petRarity = pet.getTier();
            int candyUsed = pet.getCandyUsed();
            String heldItemObj = pet.getHeldItem();
            String skinObj = pet.getSkin();
            SBPets.HeldItem heldItem = null;
            String heldItemType = null;
            String skin = null;
            String skinName = "";

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
                    LoggerIN.warning("Found an unknown pet item!, type: {}", heldItemType);
                }
            }

            SBPets.Tier tier = SBPets.Tier.valueOf(petRarity);
            boolean active = pet.isActive();
            String petType = pet.getType();
            NBTTagList list = new NBTTagList();

            if (heldItem != null && heldItem.isUpgrade())
            {
                tier = tier.getNextRarity();
            }

            SBPets.Info petInfo = this.checkPetLevel(exp, tier);
            EnumChatFormatting rarity = tier.getTierColor();
            SBPets.Type type = SBPets.PETS.getTypeByName(petType);
            int level = petInfo.getCurrentPetLevel();

            if (type != null)
            {
                SBPets.Stats stats = type.getStats();
                Map<String, SBPets.StatsPropertyArray> statsLore = type.getStatsLore();
                Map<String, List<String>> lore = type.getLore();
                List<String> descList = type.getDescription();
                String loreMode = type.getLoreMode();
                List<NBTTagString> listStats = Lists.newLinkedList();
                List<NBTTagString> listLore = Lists.newLinkedList();

                if (descList != null)
                {
                    for (String desc : descList)
                    {
                        listStats.add(new NBTTagString(desc));
                    }
                    listStats.add(new NBTTagString(""));
                }
                if (stats != null)
                {
                    SBPets.StatsProperty damage = stats.getDamage();
                    SBPets.StatsProperty health = stats.getHealth();
                    SBPets.StatsProperty strength = stats.getStrength();
                    SBPets.StatsProperty critDamage = stats.getCritDamage();
                    SBPets.StatsProperty critChance = stats.getCritChance();
                    SBPets.StatsProperty ferocity = stats.getFerocity();
                    SBPets.StatsProperty attackSpeed = stats.getAttackSpeed();
                    SBPets.StatsProperty defense = stats.getDefense();
                    SBPets.StatsProperty trueDefense = stats.getTrueDefense();
                    SBPets.StatsProperty speed = stats.getSpeed();
                    SBPets.StatsProperty intelligence = stats.getIntelligence();
                    SBPets.StatsProperty seaCreatureChance = stats.getSeaCreatureChance();
                    SBPets.StatsProperty magicFind = stats.getMagicFind();
                    SBPets.StatsProperty abilityDamage = stats.getAbilityDamage();

                    if (damage != null)
                    {
                        listStats.add(new NBTTagString(damage.getString("Damage", level)));
                    }
                    if (health != null)
                    {
                        listStats.add(new NBTTagString(health.getString("Health", level)));

                        if (active)
                        {
                            this.allStat.addHealth(health.getValue(level));
                        }
                    }
                    if (strength != null)
                    {
                        listStats.add(new NBTTagString(strength.getString("Strength", level)));

                        if (active)
                        {
                            this.allStat.addStrength(strength.getValue(level));
                        }
                    }
                    if (critDamage != null)
                    {
                        listStats.add(new NBTTagString(critDamage.getString("Crit Damage", level)));

                        if (active)
                        {
                            this.allStat.addCritDamage(critDamage.getValue(level));
                        }
                    }
                    if (critChance != null)
                    {
                        listStats.add(new NBTTagString(critChance.getString("Crit Chance", level)));

                        if (active)
                        {
                            this.allStat.addCritChance(critChance.getValue(level));
                        }
                    }
                    if (ferocity != null)
                    {
                        listStats.add(new NBTTagString(ferocity.getString("Ferocity", level)));

                        if (active)
                        {
                            this.allStat.addFerocity(ferocity.getValue(level));
                        }
                    }
                    if (attackSpeed != null)
                    {
                        listStats.add(new NBTTagString(attackSpeed.getString("Bonus Attack Speed", level)));

                        if (active)
                        {
                            this.allStat.addAttackSpeed(attackSpeed.getValue(level));
                        }
                    }
                    if (speed != null)
                    {
                        listStats.add(new NBTTagString(speed.getString("Speed", level)));

                        if (active)
                        {
                            this.allStat.addSpeed(speed.getValue(level));
                        }
                    }
                    if (defense != null)
                    {
                        listStats.add(new NBTTagString(defense.getString("Defense", level)));

                        if (active)
                        {
                            this.allStat.addDefense(defense.getValue(level));
                        }
                    }
                    if (trueDefense != null)
                    {
                        listStats.add(new NBTTagString(trueDefense.getString("True Defense", level)));

                        if (active)
                        {
                            this.allStat.addTrueDefense(trueDefense.getValue(level));
                        }
                    }
                    if (intelligence != null)
                    {
                        listStats.add(new NBTTagString(intelligence.getString("Intelligence", level)));

                        if (active)
                        {
                            this.allStat.addIntelligence(intelligence.getValue(level));
                        }
                    }
                    if (seaCreatureChance != null)
                    {
                        listStats.add(new NBTTagString(seaCreatureChance.getString("Sea Creature Chance", level)));

                        if (active)
                        {
                            this.allStat.addSeaCreatureChance(seaCreatureChance.getValue(level));
                        }
                    }
                    if (magicFind != null)
                    {
                        listStats.add(new NBTTagString(magicFind.getString("Magic Find", level)));

                        if (active)
                        {
                            this.allStat.addMagicFind(magicFind.getValue(level));
                        }
                    }
                    if (abilityDamage != null)
                    {
                        listStats.add(new NBTTagString(abilityDamage.getString("Ability Damage", level)));

                        if (active)
                        {
                            this.allStat.addAbilityDamage(abilityDamage.getValue(level));
                        }
                    }
                }
                if (lore != null)
                {
                    for (Map.Entry<String, List<String>> entry : lore.entrySet())
                    {
                        String statTierName = entry.getKey();
                        SBPets.Tier statTier = SBPets.Tier.valueOf(statTierName);
                        boolean foundLowTier = tier.ordinal() < statTier.ordinal();

                        if (loreMode != null && loreMode.equals("REPLACE") && !tier.equals(statTier))
                        {
                            continue;
                        }

                        List<NBTTagString> formattedLore = Lists.newArrayList();

                        for (String lore2 : entry.getValue())
                        {
                            if (foundLowTier)
                            {
                                continue;
                            }

                            if (statsLore != null)
                            {
                                SBPets.StatsPropertyArray array = statsLore.get(tier.name());

                                if (statsLore.get("ALL") != null)
                                {
                                    array = statsLore.get("ALL");
                                }
                                else
                                {
                                    for (SBPets.Tier tierTmp : SBPets.Tier.values())
                                    {
                                        SBPets.StatsPropertyArray foundProp = statsLore.get(tierTmp.name());

                                        if (foundProp != null && array == null)
                                        {
                                            array = foundProp;
                                        }
                                    }
                                }

                                double[] statsLoreBase = array.getBase();
                                double[] statsLoreMult = array.getMultiply();
                                String roundingMode = array.getRoundingMode();
                                String displayMode = array.getDisplayMode();

                                if (statsLoreBase != null && statsLoreMult != null)
                                {
                                    for (int i = 0; i < statsLoreMult.length; i++)
                                    {
                                        double value = statsLoreBase[i] + statsLoreMult[i] * level;
                                        BigDecimal decimal = new BigDecimal(value);

                                        if (roundingMode != null)
                                        {
                                            decimal = decimal.setScale(1, RoundingMode.valueOf(roundingMode));
                                        }
                                        if (displayMode != null)
                                        {
                                            if (displayMode.equals("DISPLAY_AT_LEVEL_1"))
                                            {
                                                lore2 = lore2.replace("{" + i + "}", SKILL_AVG.format(level == 1 ? statsLoreBase[i] : statsLoreMult[i] * level));
                                            }
                                        }
                                        else
                                        {
                                            lore2 = lore2.replace("{" + i + "}", SKILL_AVG.format(level == 1 ? statsLoreBase[i] : decimal));
                                        }
                                    }
                                }

                                double[] statsLoreAddit = array.getAdditional();

                                if (statsLoreAddit != null)
                                {
                                    for (int i2 = 0; i2 < statsLoreAddit.length; i2++)
                                    {
                                        lore2 = lore2.replace("{" + (char)(i2 + 65) + "}", SKILL_AVG.format(statsLoreAddit[i2]));
                                    }
                                }
                            }
                            formattedLore.add(new NBTTagString(lore2));
                        }

                        listLore.addAll(formattedLore);

                        if (!foundLowTier)
                        {
                            listLore.add(new NBTTagString(""));
                        }
                    }
                }

                ItemStack itemStack = type.getPetItem();
                String skinMark = "";

                if (skin != null)
                {
                    for (SBPets.Skin petSkin : SBPets.PETS.getSkin())
                    {
                        if (skin.equals(petSkin.getType()))
                        {
                            itemStack = RenderUtils.setSkullSkin(itemStack.copy(), petSkin.getUUID(), petSkin.getTexture());
                            skinName = petSkin.getName();
                            break;
                        }
                    }
                    skinName = ", " + (skinName.isEmpty() ? skin : skinName) + " Skin";
                    skinMark = " ✦";
                }

                itemStack.setStackDisplayName(EnumChatFormatting.GRAY + "[Lvl " + level + "] " + rarity + WordUtils.capitalize(petType.toLowerCase(Locale.ROOT).replace("_", " ")) + skinMark);
                list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.DARK_GRAY + type.getSkill().getName() + " Pet" + skinName));

                if (listStats.size() > 0)
                {
                    list.appendTag(new NBTTagString(""));

                    for (NBTTagString statsLoreDis : listStats)
                    {
                        list.appendTag(statsLoreDis);
                    }
                }

                if (listLore.size() > 0)
                {
                    list.appendTag(new NBTTagString(""));

                    for (NBTTagString statsLoreDis : listLore)
                    {
                        list.appendTag(statsLoreDis);
                    }
                }
                else
                {
                    list.appendTag(new NBTTagString(""));
                }

                if (heldItem != null)
                {
                    String heldItemName = EnumChatFormatting.getValueByName(heldItem.getColor()) + heldItem.getName();
                    list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.GOLD + "Held Item: " + heldItemName));

                    for (String heldItemLore : heldItem.getLore())
                    {
                        list.appendTag(new NBTTagString(heldItemLore));
                    }
                    list.appendTag(new NBTTagString(""));
                }
                else
                {
                    if (heldItemType != null)
                    {
                        list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "Held Item: " + EnumChatFormatting.RED + heldItemType));
                        list.appendTag(new NBTTagString(""));
                    }
                }

                if (candyUsed > 0)
                {
                    list.appendTag(new NBTTagString(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "(" + candyUsed + "/" + 10 + EnumChatFormatting.GREEN + ") Pet Candy Used"));
                    list.appendTag(new NBTTagString(""));
                }

                list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + (level < 100 ? EnumChatFormatting.GRAY + "Progress to Level " + petInfo.getNextPetLevel() + ": " + EnumChatFormatting.YELLOW + petInfo.getPercent() : petInfo.getPercent())));

                if (level < 100)
                {
                    list.appendTag(new NBTTagString(EnumChatFormatting.RESET + this.getTextPercentage((int)petInfo.getCurrentPetXp(), petInfo.getXpRequired()) + " " + EnumChatFormatting.YELLOW + FORMAT_2.format(petInfo.getCurrentPetXp()) + EnumChatFormatting.GOLD + "/" + EnumChatFormatting.YELLOW + NumberUtils.formatWithM(petInfo.getXpRequired())));
                }

                list.appendTag(new NBTTagString(""));
                list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "Total XP: " + EnumChatFormatting.YELLOW + NumberUtils.formatWithM(petInfo.getPetXp()) + EnumChatFormatting.GOLD + "/" + EnumChatFormatting.YELLOW + NumberUtils.formatWithM(petInfo.getTotalPetTypeXp())));
                list.appendTag(new NBTTagString(rarity + "" + EnumChatFormatting.BOLD + tier + " PET"));
                itemStack.getTagCompound().getCompoundTag("display").setTag("Lore", list);
                itemStack.getSubCompound("ExtraAttributes", true).setString("id", "PET");
                itemStack.getTagCompound().setBoolean("active", active);
                petData.add(new SBPets.Data(tier, level, WordUtils.capitalize(petType.toLowerCase(Locale.ROOT).replace("_", " ")), active, Arrays.asList(itemStack)));

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
            else
            {
                ItemStack itemStack = new ItemStack(Items.bone);
                itemStack.setStackDisplayName(EnumChatFormatting.RESET + "" + EnumChatFormatting.RED + WordUtils.capitalize(petType.toLowerCase(Locale.ROOT).replace("_", " ")));
                list.appendTag(new NBTTagString(EnumChatFormatting.RED + "" + EnumChatFormatting.BOLD + "UNKNOWN PET"));
                itemStack.getTagCompound().getCompoundTag("display").setTag("Lore", list);
                petData.add(new SBPets.Data(SBPets.Tier.COMMON, 0, itemStack.getDisplayName(), false, Arrays.asList(itemStack)));
                LoggerIN.warning("Found an unknown pet! type: {}", petType);
            }
            petData.sort((o1, o2) -> new CompareToBuilder().append(o2.isActive(), o1.isActive()).append(o2.getTier().ordinal(), o1.getTier().ordinal()).append(o2.getCurrentLevel(), o1.getCurrentLevel()).append(o1.getName(), o2.getName()).build());
        }
        for (SBPets.Data data : petData)
        {
            petItem.addAll(data.getItemStack());
        }
        SKYBLOCK_INV.add(new SBInventoryTabs.Data(petItem, SBInventoryTabs.PET));
    }

    private SBPets.Info checkPetLevel(double petExp, SBPets.Tier tier)
    {
        int index = SBPets.PETS.getIndex().get(tier.name());
        int totalPetTypeXp = 0;
        int xpRequired = 0;
        int currentLvl = 0;
        int levelToCheck = 0;
        double xpTotal = 0;
        double xpToNextLvl = 0;
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
            xpToNextLvl = MathHelper.ceiling_double_int(xpTotal - petExp);
            currentXp = xpRequired - xpToNextLvl;
        }
        else if (currentLvl == 100)
        {
            xpToNextLvl = MathHelper.ceiling_double_int(Math.abs(xpTotal - petExp));
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

        this.armorItems.stream().filter(armor -> armor != null && armor.hasTagCompound() && armor.getTagCompound().getCompoundTag("ExtraAttributes").getString("modifier").equals("renowned")).forEach(itemStack ->
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
        this.allStat.add(this.calculateSkillBonus(SBSlayers.SLAYERS.getBonus().getEnderman(), this.endermanSlayerLevel));
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

    private void getHealthFromCake(NBTTagCompound extraAttrib)
    {
        List<ItemStack> itemStack1 = new ArrayList<>();
        byte[] cakeData = extraAttrib.getByteArray("new_year_cake_bag_data");

        if (cakeData.length == 0)
        {
            return;
        }

        try
        {
            NBTTagCompound compound1 = CompressedStreamTools.readCompressed(new ByteArrayInputStream(cakeData));
            NBTTagList list = compound1.getTagList("i", 10);
            List<Integer> cakeYears = new ArrayList<>();

            for (int i = 0; i < list.tagCount(); ++i)
            {
                itemStack1.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
            }

            for (ItemStack cake : itemStack1)
            {
                if (cake != null && cake.hasTagCompound())
                {
                    int year = cake.getTagCompound().getCompoundTag("ExtraAttributes").getInteger("new_years_cake");

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
            if (itemStack != null && itemStack.hasTagCompound())
            {
                NBTTagCompound compound = itemStack.getTagCompound().getCompoundTag("display");
                NBTTagCompound extraAttrib = itemStack.getTagCompound().getCompoundTag("ExtraAttributes");
                String itemId = extraAttrib.getString("id");

                if (itemId.equals("FROZEN_CHICKEN"))
                {
                    this.allStat.addSpeed(1);
                }
                else if (itemId.equals("SPEED_TALISMAN"))
                {
                    this.allStat.addSpeed(1);
                }
                else if (itemId.equals("SPEED_RING"))
                {
                    this.allStat.addSpeed(3);
                }
                else if (itemId.equals("SPEED_ARTIFACT"))
                {
                    this.allStat.addSpeed(5);
                }
                else if (itemId.equals("NEW_YEAR_CAKE_BAG"))
                {
                    this.getHealthFromCake(extraAttrib);
                }

                if (compound.getTagId("Lore") == 9)
                {
                    NBTTagList list = compound.getTagList("Lore", 8);

                    if (list.tagCount() > 0)
                    {
                        for (int j1 = 0; j1 < list.tagCount(); ++j1)
                        {
                            String lore = EnumChatFormatting.getTextWithoutFormattingCodes(list.getStringTagAt(j1));
                            String lastLore = EnumChatFormatting.getTextWithoutFormattingCodes(list.getStringTagAt(list.tagCount() - 1));
                            Matcher matcher = STATS_PATTERN.matcher(lore);

                            if (!armor && !(lastLore.endsWith(" ACCESSORY") || lastLore.endsWith(" HATCCESSORY") || lastLore.endsWith(" ACCESSORY a") || lastLore.endsWith(" HATCCESSORY a")))
                            {
                                continue;
                            }

                            if (matcher.matches())
                            {
                                String type = matcher.group("type");
                                String value = matcher.group("value").replace(",", "");
                                double valueD = 0;

                                try
                                {
                                    valueD = NUMBER_FORMAT_WITH_SYMBOL.parse(value).doubleValue();
                                }
                                catch (Exception e) {}

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
        }
        this.allStat.add(new BonusStatTemplate(healthTemp, defenseTemp, trueDefenseTemp, 0, strengthTemp, speedTemp, critChanceTemp, critDamageTemp, attackSpeedTemp, intelligenceTemp, seaCreatureChanceTemp, magicFindTemp, petLuckTemp, ferocityTemp, abilityDamageTemp, miningSpeedTemp, miningFortuneTemp, farmingFortuneTemp, foragingFortuneTemp));
    }

    private void getBasicInfo(SkyblockProfiles.Members currentProfile, SkyblockProfiles.Banking banking, GameStatus status, String uuid, CommunityUpgrades communityUpgrade)
    {
        int deathCounts = currentProfile.getDeathCount();
        double coins = currentProfile.getPurse();
        long lastSaveMillis = currentProfile.getLastSave();
        long firstJoinMillis = currentProfile.getFirstJoin();

        if (this.uuid.equals("eef3a6031c1b4c988264d2f04b231ef4")) // special case for me :D
        {
            firstJoinMillis = 1565111612000L;
        }

        String heath = ColorUtils.stringToRGB("239,83,80").toColoredFont();
        String defense = ColorUtils.stringToRGB("156,204,101").toColoredFont();
        String trueDefense = ColorUtils.stringToRGB("255,255,255").toColoredFont();
        String strength = ColorUtils.stringToRGB("181,33,30").toColoredFont();
        String speed = ColorUtils.stringToRGB("255,255,255").toColoredFont();
        String critChance = ColorUtils.stringToRGB("121,134,203").toColoredFont();
        String critDamage = ColorUtils.stringToRGB("70,90,201").toColoredFont();
        String attackSpeed = ColorUtils.stringToRGB("255,255,85").toColoredFont();
        String miningSpeed = ColorUtils.stringToRGB("255,170,0").toColoredFont();
        String intelligence = ColorUtils.stringToRGB("129,212,250").toColoredFont();
        String seaCreatureChance = ColorUtils.stringToRGB("0,170,170").toColoredFont();
        String magicFind = ColorUtils.stringToRGB("85,255,255").toColoredFont();
        String petLuck = ColorUtils.stringToRGB("255,85,255").toColoredFont();
        String fairySoulsColor = ColorUtils.stringToRGB("203,54,202").toColoredFont();
        String bank = ColorUtils.stringToRGB("255,215,0").toColoredFont();
        String purseColor = ColorUtils.stringToRGB("255,165,0").toColoredFont();
        String ferocity = ColorUtils.stringToRGB("224,120,0").toColoredFont();
        String abilityDamage = ColorUtils.stringToRGB("214,36,0").toColoredFont();
        String location = this.getLocation(status);

        this.infoList.add(new SkyBlockInfo(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD + EnumChatFormatting.UNDERLINE + "Base Stats", ""));
        this.infoList.add(new SkyBlockInfo(heath + "\u2764 Health", heath + SKILL_AVG.format(this.allStat.getHealth())));
        this.infoList.add(new SkyBlockInfo(heath + "\u2665 Effective Health", heath + SKILL_AVG.format(this.allStat.getEffectiveHealth())));
        this.infoList.add(new SkyBlockInfo(defense + "\u2748 Defense", defense + SKILL_AVG.format(this.allStat.getDefense())));
        this.infoList.add(new SkyBlockInfo(trueDefense + "\u2742 True Defense", trueDefense + SKILL_AVG.format(this.allStat.getTrueDefense())));
        this.infoList.add(new SkyBlockInfo(strength + "\u2741 Strength", strength + SKILL_AVG.format(this.allStat.getStrength())));
        this.infoList.add(new SkyBlockInfo(speed + "\u2726 Speed", speed + SKILL_AVG.format(this.allStat.getSpeed())));
        this.infoList.add(new SkyBlockInfo(critChance + "\u2623 Crit Chance", critChance + SKILL_AVG.format(this.allStat.getCritChance()) + "%"));
        this.infoList.add(new SkyBlockInfo(critDamage + "\u2620 Crit Damage", critDamage + SKILL_AVG.format(this.allStat.getCritDamage()) + "%"));
        this.infoList.add(new SkyBlockInfo(attackSpeed + "\u2694 Attack Speed", attackSpeed + SKILL_AVG.format(this.allStat.getAttackSpeed()) + "%"));
        this.infoList.add(new SkyBlockInfo(miningSpeed + "\u2E15 Mining Speed", miningSpeed + SKILL_AVG.format(this.allStat.getMiningSpeed())));
        this.infoList.add(new SkyBlockInfo(intelligence + "\u270E Intelligence", intelligence + SKILL_AVG.format(this.allStat.getIntelligence())));
        this.infoList.add(new SkyBlockInfo(seaCreatureChance + "\u03B1 Sea Creature Chance", seaCreatureChance + SKILL_AVG.format(this.allStat.getSeaCreatureChance()) + "%"));
        this.infoList.add(new SkyBlockInfo(magicFind + "\u272F Magic Find", magicFind + SKILL_AVG.format(this.allStat.getMagicFind())));
        this.infoList.add(new SkyBlockInfo(petLuck + "\u2663 Pet Luck", petLuck + SKILL_AVG.format(this.allStat.getPetLuck())));
        this.infoList.add(new SkyBlockInfo(ferocity + "\u2AFD Ferocity", ferocity + SKILL_AVG.format(this.allStat.getFerocity())));
        this.infoList.add(new SkyBlockInfo(abilityDamage + "๑ Ability Damage", abilityDamage + SKILL_AVG.format(this.allStat.getAbilityDamage()) + "%"));
        this.infoList.add(new SkyBlockInfo(miningSpeed + "\u2646 Mining Fortune", miningSpeed + SKILL_AVG.format(this.allStat.getMiningFortune())));
        this.infoList.add(new SkyBlockInfo(miningSpeed + "\u2646 Farming Fortune", miningSpeed + SKILL_AVG.format(this.allStat.getFarmingFortune())));
        this.infoList.add(new SkyBlockInfo(miningSpeed + "\u2646 Foraging Fortune", miningSpeed + SKILL_AVG.format(this.allStat.getForagingFortune())));
        this.infoList.add(new SkyBlockInfo(fairySoulsColor + "\u2618 Fairy Souls Collected", fairySoulsColor + this.totalFairySouls + "/" + SBAPIUtils.MAX_FAIRY_SOULS));

        this.infoList.add(new SkyBlockInfo("", ""));
        this.infoList.add(new SkyBlockInfo(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD + EnumChatFormatting.UNDERLINE + "Account", ""));

        if (!StringUtils.isNullOrEmpty(location))
        {
            this.infoList.add(new SkyBlockInfo(EnumChatFormatting.GREEN + "Current Location", EnumChatFormatting.GREEN + location));
        }
        else
        {
            this.infoList.add(new SkyBlockInfo(EnumChatFormatting.RED + "Status", EnumChatFormatting.RED + "Offline"));
        }

        if (banking != null)
        {
            double balance = banking.getBalance();
            this.infoList.add(new SkyBlockInfo(bank + "Banking Account", bank + FORMAT_2.format(balance)));
        }
        else
        {
            this.infoList.add(new SkyBlockInfo(bank + "Banking Account", EnumChatFormatting.RED + "API is not enabled!"));
        }

        this.infoList.add(new SkyBlockInfo(purseColor + "Purse", purseColor + FORMAT_2.format(coins)));

        if (communityUpgrade != null)
        {
            List<SkyBlockInfo> comm = this.getCommunityUpgrades(communityUpgrade);

            if (comm.size() > 0)
            {
                this.infoList.add(new SkyBlockInfo("", ""));
                this.infoList.add(new SkyBlockInfo(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD + EnumChatFormatting.UNDERLINE + "Community Upgrades", ""));
                this.infoList.addAll(comm);
            }
        }

        if (this.jacobInfo.size() > 0)
        {
            this.infoList.add(new SkyBlockInfo("", ""));
            this.infoList.add(new SkyBlockInfo(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD + EnumChatFormatting.UNDERLINE + "Farming Contest", ""));
            this.infoList.addAll(this.jacobInfo);
        }

        this.infoList.add(new SkyBlockInfo("", ""));
        this.infoList.add(new SkyBlockInfo(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD + EnumChatFormatting.UNDERLINE + "Others", ""));

        Date firstJoinDate = new Date(firstJoinMillis);
        Date lastSaveDate = new Date(lastSaveMillis);
        SimpleDateFormat logoutDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ROOT);
        String lastLogout = logoutDate.format(lastSaveDate);
        SimpleDateFormat joinDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ROOT);
        joinDate.setTimeZone(this.uuid.equals("eef3a6031c1b4c988264d2f04b231ef4") ? TimeZone.getTimeZone("GMT") : TimeZone.getDefault());
        String firstJoinDateFormat = joinDate.format(firstJoinDate);

        this.infoList.add(new SkyBlockInfo("Joined", firstJoinMillis != -1 ? CommonUtils.getRelativeTime(firstJoinDate.getTime()) + " (" + CommonUtils.getRelativeDay(firstJoinDate.getTime()) + ")" : EnumChatFormatting.RED + "No first join data!"));
        this.infoList.add(new SkyBlockInfo("Joined (Date)", firstJoinMillis != -1 ? firstJoinDateFormat : EnumChatFormatting.RED + "No first join data!"));
        this.infoList.add(new SkyBlockInfo("Last Updated", lastSaveMillis != -1 ? String.valueOf(lastSaveDate.getTime()) : EnumChatFormatting.RED + "No last save data!"));
        this.infoList.add(new SkyBlockInfo("Last Updated (Date)", lastSaveMillis != -1 ? lastLogout : EnumChatFormatting.RED + "No last save data!"));

        this.infoList.add(new SkyBlockInfo("Death Count", FORMAT.format(deathCounts)));
    }

    private void getFairySouls(int fairyExchanges)
    {
        double healthBase = 0;
        double defenseBase = 0;
        double strengthBase = 0;
        double speed = Math.floor(fairyExchanges / 10);

        for (int i = 0; i < fairyExchanges; i++)
        {
            healthBase += 3 + Math.floor(i / 2);
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

    private void getSkills(SkyblockProfiles.Members currentProfile)
    {
        this.skillLeftList.add(this.checkSkill(currentProfile.getFarmingExp(), SBSkills.Type.FARMING));
        this.skillLeftList.add(this.checkSkill(currentProfile.getForagingExp(), SBSkills.Type.FORAGING));
        this.skillLeftList.add(this.checkSkill(currentProfile.getMiningExp(), SBSkills.Type.MINING));
        this.skillLeftList.add(this.checkSkill(currentProfile.getFishingExp(), SBSkills.Type.FISHING));
        this.skillLeftList.add(this.checkSkill(currentProfile.getRunecraftingExp(), SBSkills.Type.RUNECRAFTING, SBSkills.SKILLS.getLeveling().get("runecrafting")));

        this.skillRightList.add(this.checkSkill(currentProfile.getCombatExp(), SBSkills.Type.COMBAT));
        this.skillRightList.add(this.checkSkill(currentProfile.getEnchantingExp(), SBSkills.Type.ENCHANTING));
        this.skillRightList.add(this.checkSkill(currentProfile.getAlchemyExp(), SBSkills.Type.ALCHEMY));
        this.skillRightList.add(this.checkSkill(currentProfile.getTamingExp(), SBSkills.Type.TAMING));
        this.skillRightList.add(this.checkSkill(currentProfile.getCarpentryExp(), SBSkills.Type.CARPENTRY));

        double avg = 0.0D;
        double progress = 0.0D;
        int count = 0;
        List<SBSkills.Info> skills = new ArrayList<>();
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

    private SBSkills.Info checkSkill(Double exp, SBSkills.Type type)
    {
        return this.checkSkill(exp, type, SBSkills.SKILLS.getLeveling().get("default"));
    }

    private SBSkills.Info checkSkill(Double exp, SBSkills.Type type, int[] leveling)
    {
        if (exp != null)
        {
            double playerXp = exp;
            int xpRequired = 0;
            int currentLvl = 0;
            int levelToCheck = 0;
            double xpTotal = 0;
            double xpToNextLvl = 0;
            double currentXp = 0;
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
            if (type != SBSkills.Type.RUNECRAFTING && type != SBSkills.Type.CARPENTRY)
            {
                skillProgress = currentLvl < cap ? currentXp / xpRequired : 0.0D;
            }

            this.setSkillLevel(type, currentLvl);
            this.skillCount += 1;
            return new SBSkills.Info(type.getName(), currentXp, xpRequired, currentLvl, skillProgress, xpToNextLvl <= 0);
        }
        else
        {
            return new SBSkills.Info(EnumChatFormatting.RED + type.getName() + " is not available!", 0, 0, 0, 0, false);
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

    private void getStats(SkyblockProfiles.Members currentProfile)
    {
        JsonObject stats = currentProfile.getStats();
        List<SBStats.Display> auctions = new ArrayList<>();
        List<SBStats.Display> fished = new ArrayList<>();
        List<SBStats.Display> winter = new ArrayList<>();
        List<SBStats.Display> petMilestone = new ArrayList<>();
        List<SBStats.Display> others = new ArrayList<>();
        List<SBStats.Display> mobKills = new ArrayList<>();
        List<SBStats.Display> seaCreatures = new ArrayList<>();
        List<SBStats.Display> dragons = new ArrayList<>();
        List<SBStats.Display> race = new ArrayList<>();
        List<SBStats.Display> mythosBurrowsDug = new ArrayList<>();

        // special case
        int emperorKills = 0;
        int deepMonsterKills = 0;

        for (Map.Entry<String, JsonElement> stat : stats.entrySet().stream().filter(entry -> !SBStats.STATS.getBlacklist().stream().anyMatch(stat -> entry.getKey().equals(stat))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).entrySet())
        {
            String statName = stat.getKey().toLowerCase(Locale.ROOT);
            double value = stat.getValue().getAsDouble();

            if (statName.startsWith("kills") || statName.endsWith("kills"))
            {
                if (SBStats.STATS.getSeaCreatures().stream().anyMatch(statName::contains))
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
                statName = SBStats.STATS.getRenamed().getOrDefault(statName, statName);

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
                    race.add(new SBStats.Display(WordUtils.capitalize(statName.replaceAll("dungeon_hub_|_best_time", "").replace("_", " ")), String.format("%1$TM:%1$TS.%1$TL", (long)value)));
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
        auctions.sort((stat1, stat2) -> new CompareToBuilder().append(stat1.getName(), stat2.getName()).build());
        auctions.add(0, new SBStats.Display(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.UNDERLINE + EnumChatFormatting.BOLD + "Auctions", 0.0F));

        this.sortStats(fished, "Fishing");
        this.sortStats(winter, "Winter Event");
        this.sortStats(petMilestone, "Pet Milestones");
        this.sortStats(race, "Races");
        this.sortStats(mythosBurrowsDug, "Mythos Burrows Dug");
        this.sortStats(others, "Others");

        this.sortStatsByValue(mobKills, "Mob Kills");
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
        list.sort((stat1, stat2) -> new CompareToBuilder().append(stat1.getName(), stat2.getName()).build());
        list.add(0, new SBStats.Display(null, 0.0F));
        list.add(1, new SBStats.Display(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.UNDERLINE + EnumChatFormatting.BOLD + name, 0.0F));
    }

    private void sortStatsByValue(List<SBStats.Display> list, String name)
    {
        list.sort((stat1, stat2) -> new CompareToBuilder().append(stat2.getValue(), stat1.getValue()).build());
        list.add(0, new SBStats.Display(null, 0.0F));
        list.add(1, new SBStats.Display(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.UNDERLINE + EnumChatFormatting.BOLD + name, 0.0F));
    }

    private long checkSkyBlockItem(List<ItemStack> list, String type)
    {
        return list.stream().filter(armor -> armor != null && armor.hasTagCompound() && armor.getTagCompound().getCompoundTag("ExtraAttributes").getString("id").startsWith(type)).count();
    }

    private void getInventories(SkyblockProfiles.Members currentProfile)
    {
        this.armorItems.addAll(SBAPIUtils.decodeItem(currentProfile.getArmorInventory(), InventoryType.ARMOR).stream().filter(itemStack -> itemStack == null || itemStack.getItem() != Item.getItemFromBlock(Blocks.barrier)).collect(Collectors.toList()));

        if (this.armorItems.size() > 0)
        {
            for (int i = 0; i < 4; ++i)
            {
                SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY.setInventorySlotContents(i, this.armorItems.get(i));
            }
        }

        List<ItemStack> mainInventory = SBAPIUtils.decodeItem(currentProfile.getMainInventory(), InventoryType.INVENTORY);
        List<ItemStack> accessoryInventory = SBAPIUtils.decodeItem(currentProfile.getAccessoryInventory(), InventoryType.ACCESSORY_BAG);

        SKYBLOCK_INV.add(new SBInventoryTabs.Data(mainInventory, SBInventoryTabs.INVENTORY));
        SKYBLOCK_INV.add(new SBInventoryTabs.Data(SBAPIUtils.decodeItem(currentProfile.getEnderChestInventory(), InventoryType.ENDER_CHEST), SBInventoryTabs.ENDER_CHEST));
        SKYBLOCK_INV.add(new SBInventoryTabs.Data(SBAPIUtils.decodeItem(currentProfile.getVaultInventory(), InventoryType.PERSONAL_VAULT), SBInventoryTabs.PERSONAL_VAULT));
        SKYBLOCK_INV.add(new SBInventoryTabs.Data(accessoryInventory, SBInventoryTabs.ACCESSORY));
        SKYBLOCK_INV.add(new SBInventoryTabs.Data(SBAPIUtils.decodeItem(currentProfile.getPotionInventory(), InventoryType.POTION_BAG), SBInventoryTabs.POTION));
        SKYBLOCK_INV.add(new SBInventoryTabs.Data(SBAPIUtils.decodeItem(currentProfile.getFishingInventory(), InventoryType.FISHING_BAG), SBInventoryTabs.FISHING));
        SKYBLOCK_INV.add(new SBInventoryTabs.Data(SBAPIUtils.decodeItem(currentProfile.getQuiverInventory(), InventoryType.QUIVER), SBInventoryTabs.QUIVER));
        SKYBLOCK_INV.add(new SBInventoryTabs.Data(SBAPIUtils.decodeItem(currentProfile.getCandyInventory(), InventoryType.CANDY), SBInventoryTabs.CANDY));
        SKYBLOCK_INV.add(new SBInventoryTabs.Data(SBAPIUtils.decodeItem(currentProfile.getWardrobeInventory(), InventoryType.WARDROBE), SBInventoryTabs.WARDROBE));

        this.inventoryToStats.addAll(mainInventory);
        this.inventoryToStats.addAll(accessoryInventory);
    }

    private void getSlayerInfo(SkyblockProfiles.Members currentProfile)
    {
        JsonObject slayerBosses = currentProfile.getSlayerBoss();
        SkyblockProfiles.SlayerQuest slayerQuest = currentProfile.getSlayerQuest();

        if (slayerQuest != null)
        {
            this.activeSlayerType = slayerQuest.getType();
            this.activeSlayerTier = 1 + slayerQuest.getTier();
        }

        if (slayerBosses != null)
        {
            List<SkyBlockSlayerInfo> zombie = this.getSlayer(slayerBosses, SBSlayers.Type.ZOMBIE);
            List<SkyBlockSlayerInfo> spider = this.getSlayer(slayerBosses, SBSlayers.Type.SPIDER);
            List<SkyBlockSlayerInfo> wolf = this.getSlayer(slayerBosses, SBSlayers.Type.WOLF);
            List<SkyBlockSlayerInfo> enderman = this.getSlayer(slayerBosses, SBSlayers.Type.ENDERMAN);

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
        if (this.mc.getNetHandler().getPlayerInfo(this.profile.getName()) == null)
        {
            this.mc.getNetHandler().playerInfoMap.put(this.profile.getId(), ((IViewerLoader)new NetworkPlayerInfo(this.profile)).setLoadedFromViewer(true)); // hack into map to show their skin :D
        }

        this.player = new SBFakePlayerEntity(this.mc.theWorld, this.profile);
        SkyBlockAPIViewerScreen.renderSecondLayer = true;
        this.setPlayerArmors();
    }

    private void setPlayerArmors()
    {
        for (ItemStack armor : this.armorItems.stream().filter(itemStack -> itemStack != null && itemStack.getItem() != null && itemStack.getItem() != Item.getItemFromBlock(Blocks.air)).collect(Collectors.toList()))
        {
            try
            {
                int index = EntityLiving.getArmorPosition(armor);

                if (index == 0 && armor.getItem() instanceof ItemBlock || index < 1)
                {
                    index = 4;
                }

                this.player.inventory.armorInventory[index - 1] = armor;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private List<SkyBlockSlayerInfo> getSlayer(JsonElement element, SBSlayers.Type type)
    {
        List<SkyBlockSlayerInfo> list = new ArrayList<>();
        String lowerType = type.name().toLowerCase(Locale.ROOT);
        JsonElement slayer = element.getAsJsonObject().get(lowerType);

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
                    if (slayerLvl == 0)
                    {
                        xpRequired = SBSlayers.SLAYERS.getLeveling().get(lowerType)[0];
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

                list.add(new SkyBlockSlayerInfo(EnumChatFormatting.GRAY + type.getName() + " Slayer: " + (reachLimit ? EnumChatFormatting.GOLD : EnumChatFormatting.YELLOW) + "LVL " + slayerLvl));
                list.add(new SkyBlockSlayerInfo(EnumChatFormatting.GRAY + "EXP: " + EnumChatFormatting.LIGHT_PURPLE + (xpToNextLvl == 0 ? FORMAT.format(playerSlayerXp) : FORMAT.format(playerSlayerXp) + EnumChatFormatting.DARK_PURPLE + "/" + EnumChatFormatting.LIGHT_PURPLE + FORMAT.format(xpRequired))));

                if (xpToNextLvl != 0)
                {
                    list.add(new SkyBlockSlayerInfo(EnumChatFormatting.GRAY + "XP to " + EnumChatFormatting.YELLOW + "LVL " + levelToCheck + ": " + EnumChatFormatting.LIGHT_PURPLE + FORMAT.format(xpToNextLvl)));
                }

                list.add(SkyBlockSlayerInfo.createMobAndXp(type.getName(), playerSlayerXp + "," + xpRequired + "," + xpToNextLvl, reachLimit));
                int amount = 0;

                for (int i = 1; i <= 5; i++)
                {
                    JsonElement kill = slayer.getAsJsonObject().get("boss_kills_tier_" + (i - 1));
                    int kills = kill != null ? kill.getAsInt() : 0;
                    amount += kills * SBSlayers.SLAYERS.getPrice().get(i - 1);
                    list.add(new SkyBlockSlayerInfo(EnumChatFormatting.GRAY + "Tier " + i + ": " + EnumChatFormatting.YELLOW + this.formatSlayerKill(kills)));
                }
                this.slayerTotalAmountSpent += amount;
                this.totalSlayerXp += playerSlayerXp;
                list.add(new SkyBlockSlayerInfo(EnumChatFormatting.GRAY + "Amount Spent: " + EnumChatFormatting.YELLOW + FORMAT.format(amount)));
                list.add(SkyBlockSlayerInfo.empty());
                return list;
            }
        }
        return new ArrayList<>();
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
        case ENDERMAN:
            this.endermanSlayerLevel = currentLevel;
            break;
        default:
            break;
        }
    }

    private String formatSlayerKill(int kills)
    {
        return FORMAT.format(kills) + " kill" + (kills <= 1 ? "" : "s");
    }

    private String replaceId(String id)
    {
        for (Map.Entry<String, String> sbItem : SKYBLOCK_ITEM_ID_REMAP.entrySet())
        {
            String sbItemId = sbItem.getKey();

            if (id.contains(sbItemId))
            {
                id = id.replace(sbItemId, sbItem.getValue());
            }
        }
        return id;
    }

    private static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase entity)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 50.0F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = entity.renderYawOffset;
        float f1 = entity.rotationYaw;
        float f2 = entity.rotationPitch;
        float f3 = entity.prevRotationYawHead;
        float f4 = entity.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
        entity.renderYawOffset = (float)Math.atan(mouseX / 40.0F) * 20.0F;
        entity.rotationYaw = (float)Math.atan(mouseX / 40.0F) * 40.0F;
        entity.rotationPitch = -((float)Math.atan(mouseY / 40.0F)) * 20.0F;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        rendermanager.setRenderShadow(true);
        entity.renderYawOffset = f;
        entity.rotationYaw = f1;
        entity.rotationPitch = f2;
        entity.prevRotationYawHead = f3;
        entity.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private static void drawEntityOnScreen(int posX, int posY, int scale, EntityLivingBase entity)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 50.0F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-10.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        entity.rotationYaw = (float)(Math.atan(0) * 40.0F);
        entity.rotationYawHead = entity.rotationYaw;
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private void drawItemStackSlot(int x, int y, ItemStack itemStack)
    {
        this.drawSprite(x + 1, y + 1);
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        this.itemRender.renderItemAndEffectIntoGUI(itemStack, x + 2, y + 2);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
    }

    private void drawSprite(int left, int top)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Gui.statIcons);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(left, top + 18, this.zLevel).tex(0, 18 * 0.0078125F).endVertex();
        worldrenderer.pos(left + 18, top + 18, this.zLevel).tex(18 * 0.0078125F, 18 * 0.0078125F).endVertex();
        worldrenderer.pos(left + 18, top, this.zLevel).tex(18 * 0.0078125F, 0).endVertex();
        worldrenderer.pos(left, top, this.zLevel).tex(0, 0).endVertex();
        tessellator.draw();
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
            builder.append(i < doneLength ? EnumChatFormatting.DARK_GREEN + "-" + EnumChatFormatting.WHITE : "-");
        }
        return builder.toString();
    }

    static class ContainerArmor extends Container
    {
        public ContainerArmor(boolean info)
        {
            int x = info ? -62 : -52;
            this.addSlotToContainer(new Slot(SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY, 0, x, 75)); // boots
            this.addSlotToContainer(new Slot(SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY, 1, x, 56));
            this.addSlotToContainer(new Slot(SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY, 2, x, 36));
            this.addSlotToContainer(new Slot(SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY, 3, x, 12)); // helmet
        }

        @Override
        public boolean canInteractWith(EntityPlayer player)
        {
            return false;
        }

        @Override
        protected void retrySlotClick(int index, int clickedButton, boolean mode, EntityPlayer player) {}

        @Override
        public ItemStack transferStackInSlot(EntityPlayer player, int index)
        {
            return null;
        }

        @Override
        public boolean canMergeSlot(ItemStack itemStack, Slot slot)
        {
            return false;
        }

        @Override
        public boolean canDragIntoSlot(Slot slot)
        {
            return false;
        }
    }

    static class ContainerSkyBlock extends Container
    {
        public final List<ItemStack> itemList = new ArrayList<>();

        public ContainerSkyBlock()
        {
            for (int columns = 0; columns < 4; ++columns)
            {
                for (int rows = 0; rows < 9; ++rows)
                {
                    this.addSlotToContainer(new Slot(SkyBlockAPIViewerScreen.TEMP_INVENTORY, columns * 9 + rows, 12 + rows * 18, 18 + columns * 18));
                }
            }
            this.scrollTo(0.0F);
        }

        @Override
        public boolean canInteractWith(EntityPlayer player)
        {
            return false;
        }

        @Override
        protected void retrySlotClick(int index, int clickedButton, boolean mode, EntityPlayer player) {}

        @Override
        public ItemStack transferStackInSlot(EntityPlayer player, int index)
        {
            return null;
        }

        @Override
        public boolean canMergeSlot(ItemStack itemStack, Slot slot)
        {
            return false;
        }

        @Override
        public boolean canDragIntoSlot(Slot slot)
        {
            return false;
        }

        public boolean canScroll()
        {
            return this.itemList.size() > SkyBlockAPIViewerScreen.SIZE;
        }

        public void scrollTo(float scroll)
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
                        SkyBlockAPIViewerScreen.TEMP_INVENTORY.setInventorySlotContents(l + k * 9, this.itemList.get(i1));
                    }
                    else
                    {
                        SkyBlockAPIViewerScreen.TEMP_INVENTORY.setInventorySlotContents(l + k * 9, null);
                    }
                }
            }
        }
    }

    class SkyBlockInfo
    {
        private final String title;
        private final String value;

        public SkyBlockInfo(String title, String value)
        {
            this.title = title;
            this.value = value;
        }

        public String getTitle()
        {
            return this.title;
        }

        public String getValue()
        {
            if (this.title.equals("Last Updated"))
            {
                try
                {
                    return CommonUtils.getRelativeTime(Long.valueOf(this.value));
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
        private final String text;
        private String xp;
        private boolean reachLimit;
        private Type type = Type.TEXT;

        public SkyBlockSlayerInfo(String text)
        {
            this.text = text;
        }

        public SkyBlockSlayerInfo(String text, String xp, Type type, boolean reachLimit)
        {
            this(text);
            this.xp = xp;
            this.type = type;
            this.reachLimit = reachLimit;
        }

        public String getText()
        {
            return this.text;
        }

        public String getXp()
        {
            return this.xp;
        }

        public Type getType()
        {
            return this.type;
        }

        public boolean isReachLimit()
        {
            return this.reachLimit;
        }

        public static SkyBlockSlayerInfo createMobAndXp(String slayerType, String xp, boolean reachLimit)
        {
            return new SkyBlockSlayerInfo(slayerType, xp, Type.XP_AND_MOB, reachLimit);
        }

        public static SkyBlockSlayerInfo empty()
        {
            return new SkyBlockSlayerInfo("");
        }

        public enum Type
        {
            TEXT, XP_AND_MOB;
        }
    }

    static class EmptyStats extends GuiScrollingList
    {
        private final Type type;

        public EmptyStats(Minecraft mc, int width, int height, int top, int bottom, int left, int entryHeight, int parentWidth, int parentHeight, Type type)
        {
            super(mc, width, height, top, bottom, left, entryHeight, parentWidth, parentHeight);
            this.type = type;
        }

        @Override
        protected int getSize()
        {
            return 1;
        }

        @Override
        protected void drawSlot(int index, int right, int top, int height, Tessellator tess) {}

        @Override
        protected void drawBackground() {}

        @Override
        protected void elementClicked(int index, boolean doubleClick) {}

        @Override
        protected boolean isSelected(int index)
        {
            return false;
        }

        public Type getType()
        {
            return this.type;
        }

        enum Type
        {
            INVENTORY, SKILL, DUNGEON;
        }
    }

    class InfoStats extends GuiScrollingList
    {
        private final List<SkyBlockInfo> stats;
        private final SkyBlockAPIViewerScreen parent;

        public InfoStats(SkyBlockAPIViewerScreen parent, int width, int height, int top, int bottom, int left, int entryHeight, int parentWidth, int parentHeight, List<SkyBlockInfo> stats)
        {
            super(parent.mc, width, height, top, bottom, left, entryHeight, parentWidth, parentHeight);
            this.stats = stats;
            this.parent = parent;
        }

        @Override
        protected int getSize()
        {
            return this.stats.size();
        }

        @Override
        protected void drawSlot(int index, int right, int top, int height, Tessellator tess)
        {
            SkyBlockInfo stat = this.stats.get(index);
            boolean isCurrentUpgrade = stat.getTitle().equals("Current Upgrade");
            this.parent.drawString(this.parent.mc.fontRendererObj, stat.getTitle() + (isCurrentUpgrade ? SkyBlockProfileSelectorScreen.downloadingStates[(int)(Minecraft.getSystemTime() / 250L % SkyBlockProfileSelectorScreen.downloadingStates.length)] : ""), this.parent.guiLeft - 20, top, index % 2 == 0 ? 16777215 : 9474192);
            this.parent.drawString(this.parent.mc.fontRendererObj, stat.getValue(), this.parent.guiLeft - this.parent.mc.fontRendererObj.getStringWidth(stat.getValue()) + 195, top, index % 2 == 0 ? 16777215 : 9474192);
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick) {}

        @Override
        protected void drawBackground() {}

        @Override
        protected boolean isSelected(int index)
        {
            return false;
        }
    }

    class SlayerStats extends GuiScrollingList
    {
        private final List<SkyBlockSlayerInfo> stats;
        private final SkyBlockAPIViewerScreen parent;

        public SlayerStats(SkyBlockAPIViewerScreen parent, int width, int height, int top, int bottom, int left, int entryHeight, int parentWidth, int parentHeight, List<SkyBlockSlayerInfo> stats)
        {
            super(parent.mc, width, height, top, bottom, left, entryHeight, parentWidth, parentHeight);
            this.stats = stats;
            this.parent = parent;
            this.setHeaderInfo(true, 16);
        }

        @Override
        protected int getSize()
        {
            return this.stats.size();
        }

        @Override
        protected void drawSlot(int index, int right, int top, int height, Tessellator tess)
        {
            SkyBlockSlayerInfo stat = this.stats.get(index);

            switch (stat.getType())
            {
            case XP_AND_MOB:
                if (stat.getText().equals("Zombie"))
                {
                    EntityZombie zombie = new EntityZombie(this.parent.mc.theWorld);
                    ItemStack heldItem = new ItemStack(Items.diamond_hoe);
                    heldItem.addEnchantment(Enchantment.unbreaking, 1);
                    ItemStack helmet = RenderUtils.getSkullItemStack(SkyBlockAPIViewerScreen.REVENANT_HORROR_HEAD[0], SkyBlockAPIViewerScreen.REVENANT_HORROR_HEAD[1]);
                    ItemStack chestplate = new ItemStack(Items.diamond_chestplate);
                    chestplate.addEnchantment(Enchantment.unbreaking, 1);
                    ItemStack leggings = new ItemStack(Items.chainmail_leggings);
                    leggings.addEnchantment(Enchantment.unbreaking, 1);
                    ItemStack boots = new ItemStack(Items.diamond_boots);
                    zombie.setCurrentItemOrArmor(0, heldItem);
                    zombie.setCurrentItemOrArmor(1, boots);
                    zombie.setCurrentItemOrArmor(2, leggings);
                    zombie.setCurrentItemOrArmor(3, chestplate);
                    zombie.setCurrentItemOrArmor(4, helmet);
                    zombie.ticksExisted = ClientEventHandler.ticks;
                    SkyBlockAPIViewerScreen.drawEntityOnScreen(this.parent.guiLeft - 30, top + 60, 40, zombie);
                }
                else if (stat.getText().equals("Spider"))
                {
                    EntitySpider spider = new EntitySpider(this.parent.mc.theWorld);
                    EntityCaveSpider cave = new EntityCaveSpider(this.parent.mc.theWorld);
                    SkyBlockAPIViewerScreen.drawEntityOnScreen(this.parent.guiLeft - 30, top + 40, 40, cave);
                    SkyBlockAPIViewerScreen.drawEntityOnScreen(this.parent.guiLeft - 30, top + 60, 40, spider);
                    GlStateManager.blendFunc(770, 771);
                }
                else if (stat.getText().equals("Wolf"))
                {
                    EntityWolf wolf = new EntityWolf(this.parent.mc.theWorld);
                    wolf.setAngry(true);
                    SkyBlockAPIViewerScreen.drawEntityOnScreen(this.parent.guiLeft - 30, top + 60, 40, wolf);
                }
                else
                {
                    EntityEnderman enderman = new EntityEnderman(this.parent.mc.theWorld);
                    enderman.setScreaming(true);
                    SkyBlockAPIViewerScreen.drawEntityOnScreen(this.parent.guiLeft - 30, top + 60, 30, enderman);
                }

                ColorUtils.RGB color = ColorUtils.stringToRGB("0,255,255");
                boolean reachLimit = stat.isReachLimit();

                if (reachLimit)
                {
                    color = ColorUtils.stringToRGB("255,185,0");
                }

                this.parent.mc.getTextureManager().bindTexture(XP_BARS);
                GlStateManager.color(color.floatRed(), color.floatGreen(), color.floatBlue(), 1.0F);
                GlStateManager.disableBlend();

                String[] xpSplit = stat.getXp().split(",");
                int playerSlayerXp = Integer.valueOf(xpSplit[0]);
                int xpRequired = Integer.valueOf(xpSplit[1]);

                int filled = stat.isReachLimit() ? 91 : Math.min((int)Math.floor(playerSlayerXp * 92 / xpRequired), 91);
                Gui.drawModalRectWithCustomSizedTexture(this.parent.guiLeft + 90, top, 0, 0, 91, 5, 91, 10);

                if (filled > 0)
                {
                    Gui.drawModalRectWithCustomSizedTexture(this.parent.guiLeft + 90, top, 0, 5, filled, 5, 91, 10);
                }

                GlStateManager.enableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                break;
            default:
                if (this.getSize() == 1)
                {
                    this.parent.drawString(this.parent.mc.fontRendererObj, stat.getText(), this.parent.guiLeft + 200, top, 16777215);
                }
                else
                {
                    this.parent.drawString(this.parent.mc.fontRendererObj, stat.getText(), this.parent.guiLeft - this.parent.mc.fontRendererObj.getStringWidth(stat.getText()) + 180, top, 16777215);
                }
                break;
            }
        }

        @Override
        protected void drawScreen(int mouseX, int mouseY) {}

        @Override
        protected void elementClicked(int index, boolean doubleClick) {}

        @Override
        protected void drawBackground() {}

        @Override
        protected boolean isSelected(int index)
        {
            return false;
        }
    }

    class Others extends GuiScrollingList
    {
        private final List<?> stats;
        private final SkyBlockAPIViewerScreen parent;

        public Others(SkyBlockAPIViewerScreen parent, int width, int height, int top, int bottom, int left, int entryHeight, int parentWidth, int parentHeight, List<?> stats)
        {
            super(parent.mc, width, height, top, bottom, left, entryHeight, parentWidth, parentHeight);
            this.stats = stats;
            this.parent = parent;
        }

        @Override
        protected int getSize()
        {
            return this.stats.size();
        }

        @Override
        protected void drawSlot(int index, int right, int top, int height, Tessellator tess)
        {
            if (!this.stats.isEmpty())
            {
                FontRenderer font = this.parent.mc.fontRendererObj;
                Object obj = this.stats.get(index);

                if (obj instanceof SBStats.Display)
                {
                    SBStats.Display stat = (SBStats.Display)obj;

                    if (!StringUtils.isNullOrEmpty(stat.getName()) && this.parent.mc.fontRendererObj.getStringWidth(stat.getName()) > 200)
                    {
                        font = ColorUtils.unicodeFontRenderer;
                    }

                    this.parent.drawString(font, StringUtils.isNullOrEmpty(stat.getName()) ? "" : stat.getName(), this.parent.guiLeft - 85, top, index % 2 == 0 ? 16777215 : 9474192);
                    this.parent.drawString(this.parent.mc.fontRendererObj, stat.getValueByString(), this.parent.guiLeft - this.parent.mc.fontRendererObj.getStringWidth(stat.getValueByString()) + 180, top, index % 2 == 0 ? 16777215 : 9474192);
                }
                else if (obj instanceof BankHistory.Stats)
                {
                    BankHistory.Stats stat = (BankHistory.Stats)obj;
                    this.parent.drawString(font, stat.getStats(), this.parent.guiLeft - 55, top, 16777215);
                }
            }
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick) {}

        @Override
        protected void drawBackground() {}

        @Override
        protected boolean isSelected(int index)
        {
            return false;
        }
    }

    class SkyBlockCollections extends GuiScrollingList
    {
        private final List<SBCollections> collection;
        private final SkyBlockAPIViewerScreen parent;

        public SkyBlockCollections(SkyBlockAPIViewerScreen parent, int width, int height, int top, int bottom, int left, int entryHeight, int parentWidth, int parentHeight, List<SBCollections> collection)
        {
            super(parent.mc, width, height, top, bottom, left, entryHeight, parentWidth, parentHeight);
            this.collection = collection;
            this.parent = parent;
        }

        @Override
        protected int getSize()
        {
            return this.collection.size();
        }

        @Override
        protected void drawSlot(int index, int right, int top, int height, Tessellator tess)
        {
            SBCollections collection = this.collection.get(index);

            if (collection.getCollectionType() != null)
            {
                if (collection.getItemStack() != null)
                {
                    String collectionLvl = collection.getCollectionType() == SBCollections.Type.UNKNOWN ? "" : " " + EnumChatFormatting.GOLD + collection.getLevel();
                    this.parent.drawItemStackSlot(this.parent.guiLeft - 65, top, collection.getItemStack());
                    this.parent.drawString(this.parent.mc.fontRendererObj, (collection.getCollectionType() == SBCollections.Type.UNKNOWN ? EnumChatFormatting.RED : "") + collection.getItemStack().getDisplayName() + collectionLvl, this.parent.guiLeft - 41, top + 6, 16777215);
                    this.parent.drawString(this.parent.mc.fontRendererObj, collection.getCollectionAmount(), this.parent.guiLeft - this.parent.mc.fontRendererObj.getStringWidth(collection.getCollectionAmount()) + 170, top + 6, index % 2 == 0 ? 16777215 : 9474192);
                }
                else
                {
                    this.parent.drawString(this.parent.mc.fontRendererObj, EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + EnumChatFormatting.UNDERLINE + collection.getCollectionType().getName(), this.parent.guiLeft - 65, top + 5, 16777215);
                }
            }
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick) {}

        @Override
        protected void drawBackground() {}

        @Override
        protected boolean isSelected(int index)
        {
            return false;
        }
    }

    class SkyBlockCraftedMinions extends GuiScrollingList
    {
        private final List<SBMinions.CraftedInfo> craftMinions;
        private final SkyBlockAPIViewerScreen parent;

        public SkyBlockCraftedMinions(SkyBlockAPIViewerScreen parent, int width, int height, int top, int bottom, int left, int entryHeight, int parentWidth, int parentHeight, List<SBMinions.CraftedInfo> craftMinions)
        {
            super(parent.mc, width, height, top, bottom, left, entryHeight, parentWidth, parentHeight);
            this.craftMinions = craftMinions;
            this.parent = parent;
        }

        @Override
        protected int getSize()
        {
            return this.craftMinions.size();
        }

        @Override
        protected void drawSlot(int index, int right, int top, int height, Tessellator tess)
        {
            SBMinions.CraftedInfo craftedMinion = this.craftMinions.get(index);

            if (craftedMinion.getMinionItem() != null)
            {
                this.parent.drawItemStackSlot(this.parent.guiLeft - 102, top, craftedMinion.getMinionItem());
                this.parent.drawString(this.parent.mc.fontRendererObj, craftedMinion.getDisplayName() + " " + EnumChatFormatting.GOLD + craftedMinion.getMinionMaxTier(), this.parent.guiLeft - 79, top + 6, 16777215);
                this.parent.drawString(this.parent.mc.fontRendererObj, craftedMinion.getCraftedTiers(), this.parent.guiLeft - this.parent.mc.fontRendererObj.getStringWidth(craftedMinion.getCraftedTiers()) + 202, top + 6, index % 2 == 0 ? 16777215 : 9474192);
            }
            else
            {
                if (craftedMinion.getMinionName() != null)
                {
                    this.parent.drawString(this.parent.mc.fontRendererObj, EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD + EnumChatFormatting.UNDERLINE + craftedMinion.getMinionName(), this.parent.guiLeft - 100, top + 5, 16777215);
                }
            }
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick) {}

        @Override
        protected void drawBackground() {}

        @Override
        protected boolean isSelected(int index)
        {
            return false;
        }
    }

    private enum ViewButton
    {
        PLAYER(10),
        SKILLS(11),
        SLAYERS(12),
        DUNGEONS(13),
        OTHERS(14);

        private final int id;

        ViewButton(int id)
        {
            this.id = id;
        }

        public static ViewButton getTypeForButton(GuiButton button)
        {
            for (ViewButton viewButton : ViewButton.values())
            {
                if (viewButton.id == button.id)
                {
                    return viewButton;
                }
            }
            return null;
        }
    }

    private enum OthersViewButton
    {
        KILLS(20),
        DEATHS(21),
        OTHER_STATS(22),
        BANK_HISTORY(23);

        private final int id;

        OthersViewButton(int id)
        {
            this.id = id;
        }

        public static OthersViewButton getTypeForButton(GuiButton button)
        {
            for (OthersViewButton viewButton : OthersViewButton.values())
            {
                if (viewButton.id == button.id)
                {
                    return viewButton;
                }
            }
            return null;
        }
    }

    private enum BasicInfoViewButton
    {
        PLAYER_STATS(30),
        INVENTORY(31),
        COLLECTIONS(32),
        CRAFTED_MINIONS(33);

        private final int id;

        BasicInfoViewButton(int id)
        {
            this.id = id;
        }

        public static BasicInfoViewButton getTypeForButton(GuiButton button)
        {
            for (BasicInfoViewButton viewButton : BasicInfoViewButton.values())
            {
                if (viewButton.id == button.id)
                {
                    return viewButton;
                }
            }
            return null;
        }
    }
}