package com.stevekung.skyblockcatia.gui.api;

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
import org.lwjgl.input.Mouse;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.config.ConfigManagerIN;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.event.ClientEventHandler;
import com.stevekung.skyblockcatia.gui.APIErrorInfo;
import com.stevekung.skyblockcatia.gui.GuiButtonItem;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.integration.sba.SBABackpack;
import com.stevekung.skyblockcatia.integration.textoverflow.TooltipOverflow;
import com.stevekung.skyblockcatia.utils.*;
import com.stevekung.skyblockcatia.utils.SkyBlockPets.PetSkin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
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
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.client.GuiScrollingList;

public class GuiSkyBlockData extends GuiScreen
{
    private static final ResourceLocation INVENTORY_TABS = new ResourceLocation("skyblockcatia:textures/gui/tabs.png");
    private static final ResourceLocation XP_BARS = new ResourceLocation("skyblockcatia:textures/gui/skill_xp_bar.png");
    private static final String[] REVENANT_HORROR_HEAD = new String[] {"0862e0b0-a14f-3f93-894f-013502936b59", "dbad99ed3c820b7978190ad08a934a68dfa90d9986825da1c97f6f21f49ad626"};

    // Based stuff
    private boolean firstLoad;
    private boolean loadingApi = true;
    private boolean error = false;
    private String errorMessage;
    private String statusMessage;
    private GuiButton doneButton;
    private GuiButton backButton;
    private GuiButtonItem showArmorButton;
    private JsonObject skyblockProfiles;
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
    private static final WorldClient FAKE_WORLD = new WorldClient(Minecraft.getMinecraft().getNetHandler(), new WorldSettings(0L, WorldSettings.GameType.SURVIVAL, false, false, WorldType.DEFAULT), 0, EnumDifficulty.NORMAL, Minecraft.getMinecraft().mcProfiler);

    // API
    private static final int MAXED_UNIQUE_MINIONS = 572;
    private static final Pattern STATS_PATTERN = Pattern.compile("(?<type>Strength|Crit Chance|Crit Damage|Health|Defense|Speed|Intelligence|True Defense|Sea Creature Chance|Magic Find|Pet Luck|Bonus Attack Speed|Ferocity): (?<value>(?:\\+|\\-)[0-9,.]+)?(?:\\%){0,1}(?:(?: HP(?: \\((?:\\+|\\-)[0-9,.]+ HP\\)){0,1}(?: \\(\\w+ (?:\\+|\\-)[0-9,.]+ HP\\)){0,1})|(?: \\((?:\\+|\\-)[0-9,.]+\\))|(?: \\(\\w+ (?:\\+|\\-)[0-9,.]+(?:\\%){0,1}\\))){0,1}(?: \\((?:\\+|\\-)[0-9,.]+ HP\\)){0,1}");
    private static final ModDecimalFormat FORMAT = new ModDecimalFormat("#,###");
    private static final ModDecimalFormat FORMAT_2 = new ModDecimalFormat("#,###.#");
    private static final ModDecimalFormat NUMBER_FORMAT_WITH_SYMBOL = new ModDecimalFormat("+#;-#");
    private static final ModDecimalFormat SKILL_AVG = new ModDecimalFormat("##.#");
    private static final List<String> SEA_CREATURES = ImmutableList.of("sea_walker", "pond_squid", "night_squid", "frozen_steve", "grinch", "yeti", "frosty_the_snowman", "sea_guardian", "sea_archer", "sea_witch", "chicken_deep", "zombie_deep", "catfish", "sea_leech", "deep_sea_protector", "water_hydra", "skeleton_emperor", "guardian_defender", "guardian_emperor", "carrot_king", "nurse_shark", "blue_shark", "tiger_shark", "great_white_shark", "nightmare", "scarecrow", "werewolf", "phantom_fisherman", "grim_reaper");
    private static final Map<String, String> CURRENT_LOCATION_MAP = ImmutableMap.<String, String>builder().put("dynamic", "Private Island").put("hub", "Hub").put("mining_1", "Gold Mine").put("mining_2", "Deep Caverns").put("combat_1", "Spider's Den").put("combat_2", "Blazing Fortress").put("combat_3", "The End").put("farming_1", "The Barn").put("farming_2", "Mushroom Desert").put("foraging_1", "The Park").put("winter", "Jerry's Workshop").put("dungeon_hub", "Dungeon Hub").put("dungeon", "Dungeon").put("dark_auction", "Dark Auction").build();
    private static final Map<String, String> RENAMED_STATS_MAP = ImmutableMap.<String, String>builder().put("auctions_bought_common", "common_auctions_bought").put("auctions_bought_epic", "epic_auctions_bought").put("auctions_bought_legendary", "legendary_auctions_bought").put("auctions_bought_rare", "rare_auctions_bought").put("auctions_bought_special", "special_auctions_bought").put("auctions_bought_uncommon", "uncommon_auctions_bought").put("auctions_sold_common", "common_auctions_sold").put("auctions_sold_epic", "epic_auctions_sold").put("auctions_sold_legendary", "legendary_auctions_sold")
            .put("auctions_sold_rare", "rare_auctions_sold").put("auctions_sold_special", "special_auctions_sold").put("auctions_sold_uncommon", "uncommon_auctions_sold").put("items_fished_large_treasure", "large_treasure_items_fished").put("items_fished_normal", "normal_items_fished").put("items_fished_treasure", "treasure_items_fished").put("shredder_bait", "bait_used_with_shredder")
            .put("mythos_burrows_chains_complete_common", "mythos_burrows_common_chains_complete").put("mythos_burrows_chains_complete_epic", "mythos_burrows_epic_chains_complete").put("mythos_burrows_chains_complete_legendary", "mythos_burrows_legendary_chains_complete").put("mythos_burrows_chains_complete_rare", "mythos_burrows_rare_chains_complete").put("mythos_burrows_dug_combat_common", "mythos_burrows_dug_common_monsters").put("mythos_burrows_dug_combat_epic", "mythos_burrows_dug_epic_monsters").put("mythos_burrows_dug_combat_legendary", "mythos_burrows_dug_legendary_monsters").put("mythos_burrows_dug_combat_rare", "mythos_burrows_dug_rare_monsters").put("mythos_burrows_dug_next_common", "mythos_burrows_dug_common_arrows").put("mythos_burrows_dug_next_epic", "mythos_burrows_dug_epic_arrows").put("mythos_burrows_dug_next_legendary", "mythos_burrows_dug_legendary_arrows").put("mythos_burrows_dug_next_rare", "mythos_burrows_dug_rare_arrows").put("mythos_burrows_dug_treasure_common", "mythos_burrows_dug_common_treasure").put("mythos_burrows_dug_treasure_epic", "mythos_burrows_dug_epic_treasure").put("mythos_burrows_dug_treasure_legendary", "mythos_burrows_dug_legendary_treasure").put("mythos_burrows_dug_treasure_rare", "mythos_burrows_dug_rare_treasure").build();
    private static final Map<String, String> SKYBLOCK_ITEM_ID_REMAP = ImmutableMap.<String, String>builder().put("seeds", "wheat_seeds").put("raw_chicken", "chicken").put("carrot_item", "carrot").put("potato_item", "potato").put("sulphur", "gunpowder").put("mushroom_collection", "red_mushroom").put("sugar_cane", "reeds").put("pork", "porkchop").put("nether_stalk", "nether_wart").put("raw_fish", "fish").put("ink_sack", "dye").put("water_lily", "waterlily").put("ender_stone", "end_stone").put("log_2", "log2").put("snow_ball", "snowball").build();
    private static final ImmutableList<String> BLACKLIST_STATS = ImmutableList.of("highest_crit_damage", "mythos_burrows_dug_combat", "mythos_burrows_dug_combat_null", "mythos_burrows_dug_treasure", "mythos_burrows_dug_next", "mythos_burrows_dug_treasure_null", "mythos_burrows_chains_complete", "mythos_burrows_chains_complete_null", "mythos_burrows_dug_next_null");
    public static boolean renderSecondLayer;
    private final List<SkyBlockInfo> infoList = new ArrayList<>();
    private final List<SkyBlockSkillInfo> skillLeftList = new ArrayList<>();
    private final List<SkyBlockSkillInfo> skillRightList = new ArrayList<>();
    private final List<SkyBlockSlayerInfo> slayerInfo = new ArrayList<>();
    private final List<SkyBlockStats> sbKills = new ArrayList<>();
    private final List<SkyBlockStats> sbDeaths = new ArrayList<>();
    private final List<SkyBlockStats> sbOthers = new ArrayList<>();
    private final List<BankHistory.Stats> sbBankHistories = new ArrayList<>();
    private final List<CraftedMinion> sbCraftedMinions = new ArrayList<>();
    private final List<ItemStack> armorItems = new ArrayList<>();
    private final List<ItemStack> inventoryToStats = new ArrayList<>();
    private final List<SkyBlockCollection> collections = new ArrayList<>();
    private final Multimap<String, Integer> craftedMinions = HashMultimap.create();
    private int additionalMinionSlot;
    private int craftedMinionCount;
    private int currentMinionSlot;
    private int slayerTotalAmountSpent;
    private int totalSlayerXp;
    private int totalDisabledInv;
    private EntityOtherFakePlayer player;
    private String skillAvg;
    private int petScore;
    private int activeSlayerTier;
    private SlayerType activeSlayerType;

    // Info & Inventory
    private static final int SIZE = 36;
    private static final InventoryExtended TEMP_INVENTORY = new InventoryExtended(GuiSkyBlockData.SIZE);
    private static final InventoryExtended TEMP_ARMOR_INVENTORY = new InventoryExtended(4);
    static final List<SkyBlockInventory> SKYBLOCK_INV = new ArrayList<>();
    private int selectedTabIndex = SkyBlockInventoryTabs.INVENTORY.getTabIndex();
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
    private BonusStatTemplate allStat = new BonusStatTemplate(100, 0, 0, 0, 0, 100, 30, 50, 0, 100, 20, 10, 0, 0);

    // GuiContainer fields
    private int xSize;
    private int ySize;
    private int guiLeft;
    private int guiTop;
    private Slot theSlot;

    public GuiSkyBlockData(List<ProfileDataCallback> profiles, ProfileDataCallback callback)
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
                this.errorInfo = new APIErrorInfo(this, this.width - 39, this.height, 40, this.height / 4 + 128, 19, 12, this.width, this.height, this.errorList);
            }
            this.updated = true;
        }

        int i = this.selectedTabIndex;
        this.selectedTabIndex = -1;
        this.setCurrentTab(SkyBlockInventoryTabs.tabArray[i]);

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
                this.setCurrentTab(SkyBlockInventoryTabs.tabArray[this.selectedTabIndex]);
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
                this.currentSlot = new SkyBlockCraftedMinions(this, this.width - 119, this.height, 40, this.height - 70, 59, 20, this.width, this.height, this.sbCraftedMinions);
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
        GuiSkyBlockData.renderSecondLayer = false;
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
                this.mc.displayGuiScreen(this.error ? new GuiSkyBlockAPIViewer(GuiSkyBlockAPIViewer.GuiState.SEARCH, this.username, this.displayName, this.guild, this.profiles) : null);
            }
            else if (button.id == 1)
            {
                this.mc.displayGuiScreen(this.profiles.size() == 0 ? new GuiSkyBlockAPIViewer(GuiSkyBlockAPIViewer.GuiState.EMPTY, this.username, this.displayName, "") : new GuiSkyBlockAPIViewer(GuiSkyBlockAPIViewer.GuiState.SEARCH, this.username, this.displayName, this.guild, this.profiles));
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
        if (keyCode == KeyBindingHandler.KEY_SB_VIEW_RECIPE.getKeyCode())
        {
            if (this.theSlot != null && this.theSlot.getHasStack() && this.theSlot.getStack().hasTagCompound())
            {
                NBTTagCompound extraAttrib = this.theSlot.getStack().getTagCompound().getCompoundTag("ExtraAttributes");

                if (extraAttrib.hasKey("id"))
                {
                    String itemId = extraAttrib.getString("id");

                    if (SkyBlockcatiaMod.isSkyblockAddonsLoaded && SBABackpack.INSTANCE.isFreezeBackpack())
                    {
                        return;
                    }
                    ClientUtils.printClientMessage(JsonUtils.create("Click to view ").appendSibling(JsonUtils.create(this.theSlot.getStack().getDisplayName()).setChatStyle(JsonUtils.gold()).appendSibling(JsonUtils.create(" recipe").setChatStyle(JsonUtils.green()))).setChatStyle(JsonUtils.green().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewrecipe " + itemId))));
                }
            }
        }
        if (SkyBlockcatiaMod.isSkyblockAddonsLoaded)
        {
            SBABackpack.INSTANCE.keyTyped(keyCode);
        }
        if (keyCode == 1)
        {
            this.actionPerformed(this.backButton);
        }
        else if (keyCode == 63)
        {
            this.skyblockProfiles = null;
            this.mc.displayGuiScreen(new GuiSkyBlockData(this.profiles, new ProfileDataCallback(this.sbProfileId, this.sbProfileName, this.username, this.displayName, this.gameMode, this.guild, this.uuid, this.profile, -1)));
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

                for (SkyBlockInventoryTabs tab : SkyBlockInventoryTabs.tabArray)
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
            this.drawString(this.fontRendererObj, GuiSkyBlockAPIViewer.downloadingStates[(int)(Minecraft.getSystemTime() / 500L % GuiSkyBlockAPIViewer.downloadingStates.length)], this.width / 2 + i / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2 - 35, 16777215);
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
                    GuiSkyBlockData.drawEntityOnScreen(this.width / 2 - 106, this.height / 2 + 40, 40, this.guiLeft - 55 - this.oldMouseX, this.guiTop + 25 - this.oldMouseY, this.player);

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

                        GuiSkyBlockData.drawEntityOnScreen(this.width / 2 - 96, this.height / 2 + 40, 40, this.guiLeft - 46 - this.oldMouseX, this.guiTop + 75 - 50 - this.oldMouseY, this.player);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                        for (SkyBlockInventoryTabs tab : SkyBlockInventoryTabs.tabArray)
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
                        if (SkyBlockcatiaMod.isSkyblockAddonsLoaded)
                        {
                            SBABackpack.INSTANCE.drawBackpacks(this, mouseX, mouseY, partialTicks);
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

                        for (SkyBlockSkillInfo info : this.skillLeftList)
                        {
                            int x = this.width / 2 - 120;
                            int y = height + 12;
                            int barY = y + 20 + height * i;
                            int textY = y + height * i;
                            this.renderSkillBar(info.getName(), x, barY, x + 46, textY, info.getCurrentXp(), info.getXpRequired(), info.getCurrentLvl(), info.isReachLimit());
                            ++i;
                        }

                        i = 0;

                        for (SkyBlockSkillInfo info : this.skillRightList)
                        {
                            int x = this.width / 2 + 30;
                            int y = height + 12;
                            int barY = y + 20 + height * i;
                            int textY = y + height * i;
                            this.renderSkillBar(info.getName(), x, barY, x + 46, textY, info.getCurrentXp(), info.getXpRequired(), info.getCurrentLvl(), info.isReachLimit());
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
                    String total1 = EnumChatFormatting.GRAY + "Unique Minions: " + EnumChatFormatting.YELLOW + this.craftedMinionCount + "/" + GuiSkyBlockData.MAXED_UNIQUE_MINIONS + EnumChatFormatting.GRAY + " (" + this.craftedMinionCount * 100 / GuiSkyBlockData.MAXED_UNIQUE_MINIONS + "%)";
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

        if (SkyBlockcatiaMod.isTextOverflowScrollLoaded)
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
                    this.setCurrentTab(SkyBlockInventoryTabs.tabArray[this.selectedTabIndex]);
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
                    this.currentSlot = new SkyBlockCraftedMinions(this, this.width - 119, this.height, 40, this.height - 70, 59, 20, this.width, this.height, this.sbCraftedMinions);
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
                this.setCurrentTab(SkyBlockInventoryTabs.tabArray[this.selectedTabIndex]);
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
                this.currentSlot = new SkyBlockCraftedMinions(this, this.width - 119, this.height, 40, this.height - 70, 59, 20, this.width, this.height, this.sbCraftedMinions);
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
            this.errorInfo = new APIErrorInfo(this, this.width - 39, this.height, 40, this.height / 4 + 128, 19, 12, this.width, this.height, this.errorList);
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

    private boolean isMouseOverTab(SkyBlockInventoryTabs tab, int mouseX, int mouseY)
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

    private void setCurrentTab(SkyBlockInventoryTabs tab)
    {
        if (tab == null)
        {
            return;
        }
        if (SkyBlockcatiaMod.isSkyblockAddonsLoaded)
        {
            SBABackpack.INSTANCE.clearRenderBackpack();
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
        if (SkyBlockInventoryTabs.tabArray[this.selectedTabIndex] == null)
        {
            return false;
        }
        return SkyBlockInventoryTabs.tabArray[this.selectedTabIndex].hasScrollBar() && this.skyBlockContainer.canScroll();
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
    private void renderSkillBar(String name, int xBar, int yBar, int xText, int yText, double playerXp, int xpRequired, int currentLvl, boolean reachLimit)
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

            this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GRAY + name + (reachLimit ? EnumChatFormatting.GOLD : EnumChatFormatting.YELLOW) + " " + currentLvl, xText, yText, 16777215);

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

                    if (SkyBlockcatiaMod.isSkyblockAddonsLoaded && SBABackpack.INSTANCE.isFreezeBackpack())
                    {
                        continue;
                    }
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
        if (itemStack != null && itemStack.stackSize != 1)
        {
            FontRenderer fontRenderer = this.fontRendererObj;
            String stackSize = String.valueOf(NumberUtils.formatCompact(itemStack.stackSize));
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
    }

    private boolean renderTabsHoveringText(SkyBlockInventoryTabs tab, int mouseX, int mouseY)
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
        SkyBlockInventoryTabs tab = SkyBlockInventoryTabs.tabArray[this.selectedTabIndex];

        for (SkyBlockInventoryTabs tab1 : SkyBlockInventoryTabs.tabArray)
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
        SkyBlockInventoryTabs tab = SkyBlockInventoryTabs.tabArray[this.selectedTabIndex];

        if (tab != null)
        {
            GlStateManager.disableBlend();
            this.fontRendererObj.drawString(tab.getTranslatedTabLabel(), this.guiLeft + 11, this.guiTop + 6, 4210752);
        }
    }

    private void drawTab(SkyBlockInventoryTabs tab)
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
        Gson gson = new Gson();
        JsonObject profiles = null;
        JsonElement banking = null;
        CommunityUpgrades communityUpgrade = null;

        if (this.skyblockProfiles == null)
        {
            URL url = new URL(SkyBlockAPIUtils.SKYBLOCK_PROFILE + this.sbProfileId);
            JsonObject obj = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
            JsonElement profile = obj.get("profile");

            if (profile == null || profile.isJsonNull())
            {
                this.setErrorMessage("No API data returned, please try again later!", false);
                return;
            }
            profiles = profile.getAsJsonObject().get("members").getAsJsonObject();
            banking = profile.getAsJsonObject().get("banking");
            communityUpgrade = gson.fromJson(profile.getAsJsonObject().get("community_upgrades"), CommunityUpgrades.class);
        }
        else
        {
            profiles = this.skyblockProfiles.get("members").getAsJsonObject();
            banking = this.skyblockProfiles.get("banking");
            communityUpgrade = gson.fromJson(this.skyblockProfiles.getAsJsonObject().get("community_upgrades"), CommunityUpgrades.class);
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
                URL urlStatus = new URL("https://api.hypixel.net/status?key=" + ConfigManagerIN.hypixelApiKey + "&uuid=" + this.uuid);
                JsonObject objStatus = new JsonParser().parse(IOUtils.toString(urlStatus.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();

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

                for (SkyBlockInventoryTabs tab : SkyBlockInventoryTabs.tabArray)
                {
                    if (tab.isDisabled())
                    {
                        this.totalDisabledInv++;
                    }
                }

                this.data.setHasInventories(this.totalDisabledInv != 11);
                this.allStat.add(new BonusStatTemplate(0, 0, 0, this.allStat.getDefense() <= 0 ? this.allStat.getHealth() : (int)(this.allStat.getHealth() * (1 + this.allStat.getDefense() / 100.0D)), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                this.getBasicInfo(currentUserProfile, banking, objStatus, userUUID, communityUpgrade);
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

                if (data.getType() == CommunityUpgrades.Type.MINION_SLOTS)
                {
                    this.additionalMinionSlot = tier;
                }

                info.add(new SkyBlockInfo(data.getDisplayName(), "Tier " + NumberUtils.intToRoman(tier)));
            }
        }
        return info;
    }

    @Deprecated
    private final List<String> dungeonData = new ArrayList<>();
    private void getDungeons(JsonObject currentUserProfile)//TODO
    {
        JsonElement dungeon = currentUserProfile.get("dungeons");
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
                SkyBlockSkillInfo info = this.calculateDungeonSkill(catacombsExp.getAsDouble(), DungeonSkillType.THE_CATACOMBS);
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
                    SkyBlockSkillInfo info2 = this.calculateDungeonSkill(classExp.getAsDouble(), DungeonSkillType.valueOf(entry.getKey().toUpperCase(Locale.ROOT)));
                    this.dungeonData.add(EnumChatFormatting.RED + info2.getName() + EnumChatFormatting.RESET + ", Level: " + info2.getCurrentLvl() + " " + (int)Math.floor(info2.getCurrentXp()) + "/" + info2.getXpRequired());
                    i++;
                }
            }

            this.dungeonData.add("");
            StringBuilder builder = new StringBuilder();

            if (tierCompletion != null)
            {
                for (Map.Entry<String, JsonElement> entry : tierCompletion.getAsJsonObject().entrySet().stream().filter(entry -> !entry.getKey().equals("0")).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())).entrySet())
                {
                    builder.append("Floor: " + entry.getKey() + "/" + FORMAT.format(entry.getValue().getAsInt()) + ", ");
                }
                i++;
            }

            this.dungeonData.add(builder.toString());
        }
        this.data.setHasDungeons(dungeon != null && i > 0);
    }

    private SkyBlockSkillInfo calculateDungeonSkill(double playerXp, DungeonSkillType type)
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
        return new SkyBlockSkillInfo(type.getName(), currentXp, xpRequired, currentLvl, 0, xpToNextLvl <= 0);
    }

    private void getBankHistories(JsonObject banking)
    {
        BankHistory[] bankHistory = new Gson().fromJson(banking.get("transactions"), BankHistory[].class);
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

    private String getLocation(JsonObject objStatus, String uuid)
    {
        JsonObject session = objStatus.get("session").getAsJsonObject();
        String locationText = "";

        if (session.get("online").getAsBoolean())
        {
            JsonElement gameType = session.get("gameType");
            JsonElement mode = session.get("mode");

            if (gameType.getAsString().equals("SKYBLOCK"))
            {
                locationText = CURRENT_LOCATION_MAP.getOrDefault(mode.getAsString(), mode.getAsString());
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
        for (SkyBlockMinion.MinionSlot minion : SkyBlockMinion.MINION_SLOTS)
        {
            if (minion.getCurrentSlot() <= this.craftedMinionCount)
            {
                this.currentMinionSlot = minion.getMinionSlot();
            }
        }

        List<MinionLevel> minionLevels = new ArrayList<>();
        List<MinionData> minionDatas = new ArrayList<>();
        int level = 1;

        for (SkyBlockMinion.Type minion : SkyBlockMinion.Type.VALUES)
        {
            for (String minionType : this.craftedMinions.keySet())
            {
                if (minion.name().equals(minionType))
                {
                    level = Collections.max(this.craftedMinions.get(minionType));
                    break;
                }
            }
            minionLevels.add(new MinionLevel(minion.name(), minion.getAltName(), minion.getMinionItem(), level, minion.getMinionCategory()));
        }

        int[] dummyTiers = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

        for (Map.Entry<String, Collection<Integer>> entry : this.craftedMinions.asMap().entrySet())
        {
            String minionType = entry.getKey();
            Collection<Integer> craftedList = entry.getValue();
            StringBuilder builder = new StringBuilder();
            int[] craftedTiers = Ints.toArray(craftedList);
            List<String> minionList = new ArrayList<>();
            Set<Integer> dummySet = new HashSet<>();
            Set<Integer> skippedList = new HashSet<>();

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
            minionDatas.add(new MinionData(minionType, builder.toString()));
        }

        List<CraftedMinion> farmingMinion = new ArrayList<>();
        List<CraftedMinion> miningMinion = new ArrayList<>();
        List<CraftedMinion> combatMinion = new ArrayList<>();
        List<CraftedMinion> foragingMinion = new ArrayList<>();
        List<CraftedMinion> fishingMinion = new ArrayList<>();
        CraftedMinion dummy = new CraftedMinion(null, null, 0, null, null, null);
        String displayName = null;
        ItemStack itemStack = null;
        SkillType category = null;
        Comparator<CraftedMinion> com = (cm1, cm2) -> new CompareToBuilder().append(cm1.getMinionName(), cm2.getMinionName()).build();

        for (MinionData minionData : minionDatas)
        {
            for (MinionLevel minionLevel : minionLevels)
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

            CraftedMinion min = new CraftedMinion(minionData.getMinionType(), displayName, level, minionData.getCraftedTiers(), itemStack, category);

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
            this.sbCraftedMinions.add(new CraftedMinion("Farming", null, 0, null, null, null));
            this.sbCraftedMinions.addAll(farmingMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!miningMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new CraftedMinion("Mining", null, 0, null, null, null));
            this.sbCraftedMinions.addAll(miningMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!combatMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new CraftedMinion("Combat", null, 0, null, null, null));
            this.sbCraftedMinions.addAll(combatMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!foragingMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new CraftedMinion("Foraging", null, 0, null, null, null));
            this.sbCraftedMinions.addAll(foragingMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!fishingMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new CraftedMinion("Fishing", null, 0, null, null, null));
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

        SkyBlockCollection dummyCollection = new SkyBlockCollection(null, null, -1, -1);

        if (collections != null)
        {
            List<SkyBlockCollection> farming = new ArrayList<>();
            List<SkyBlockCollection> mining = new ArrayList<>();
            List<SkyBlockCollection> combat = new ArrayList<>();
            List<SkyBlockCollection> foraging = new ArrayList<>();
            List<SkyBlockCollection> fishing = new ArrayList<>();
            List<SkyBlockCollection> unknown = new ArrayList<>();

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
                        || item == Item.getItemFromBlock(Blocks.sand) || item == Item.getItemFromBlock(Blocks.end_stone) || item == Items.dye && meta == 4)
                {
                    mining.add(new SkyBlockCollection(itemStack, SkyBlockCollection.Type.MINING, collectionCount, level));
                }
                else if (item == Items.rotten_flesh || item == Items.bone || item == Items.string || item == Items.spider_eye || item == Items.gunpowder || item == Items.ender_pearl || item == Items.ghast_tear || item == Items.slime_ball || item == Items.blaze_rod || item == Items.magma_cream)
                {
                    combat.add(new SkyBlockCollection(itemStack, SkyBlockCollection.Type.COMBAT, collectionCount, level));
                }
                else if (item == Item.getItemFromBlock(Blocks.log) || item == Item.getItemFromBlock(Blocks.log2))
                {
                    foraging.add(new SkyBlockCollection(itemStack, SkyBlockCollection.Type.FORAGING, collectionCount, level));
                }
                else if (item == Items.fish || item == Items.prismarine_shard || item == Items.prismarine_crystals || item == Items.clay_ball || item == Item.getItemFromBlock(Blocks.waterlily) || item == Item.getItemFromBlock(Blocks.sponge) || item == Items.dye && meta == 0)
                {
                    fishing.add(new SkyBlockCollection(itemStack, SkyBlockCollection.Type.FISHING, collectionCount, level));
                }
                else if (item == Items.reeds || item == Item.getItemFromBlock(Blocks.pumpkin) || item == Items.carrot || item == Items.wheat || item == Items.potato || item == Items.melon || item == Items.dye && meta == 3 || item == Items.feather || item == Items.chicken
                        || item == Items.porkchop || item == Items.mutton || item == Items.leather || item == Item.getItemFromBlock(Blocks.red_mushroom) || item == Items.nether_wart || item == Items.rabbit || item == Items.wheat_seeds || item == Item.getItemFromBlock(Blocks.cactus))
                {
                    farming.add(new SkyBlockCollection(itemStack, SkyBlockCollection.Type.FARMING, collectionCount, level));
                }
                else
                {
                    unknown.add(new SkyBlockCollection(itemStack, SkyBlockCollection.Type.UNKNOWN, collectionCount, level));
                }
            }

            Comparator<SkyBlockCollection> com = (sbColl1, sbColl2) -> new CompareToBuilder().append(sbColl1.getCollectionType().ordinal(), sbColl2.getCollectionType().ordinal()).append(sbColl2.getValue(), sbColl1.getValue()).build();
            farming.sort(com);
            mining.sort(com);
            combat.sort(com);
            foraging.sort(com);
            fishing.sort(com);
            unknown.sort(com);

            if (!farming.isEmpty())
            {
                this.collections.add(new SkyBlockCollection(null, SkyBlockCollection.Type.FARMING, -1, -1));
                this.collections.addAll(farming);
            }
            if (!mining.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SkyBlockCollection(null, SkyBlockCollection.Type.MINING, -1, -1));
                this.collections.addAll(mining);
            }
            if (!combat.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SkyBlockCollection(null, SkyBlockCollection.Type.COMBAT, -1, -1));
                this.collections.addAll(combat);
            }
            if (!foraging.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SkyBlockCollection(null, SkyBlockCollection.Type.FORAGING, -1, -1));
                this.collections.addAll(foraging);
            }
            if (!fishing.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SkyBlockCollection(null, SkyBlockCollection.Type.FISHING, -1, -1));
                this.collections.addAll(fishing);
            }
            if (!unknown.isEmpty())
            {
                this.collections.add(dummyCollection);
                this.collections.add(new SkyBlockCollection(null, SkyBlockCollection.Type.UNKNOWN, -1, -1));
                this.collections.addAll(unknown);
            }
            this.collections.add(dummyCollection);
        }
        else
        {
            this.data.setHasCollections(false);
        }
    }

    private void getSacks(JsonObject currentProfile)
    {
        List<ItemStack> sacks = new ArrayList<>();

        try
        {
            JsonElement sacksCounts = currentProfile.get("sacks_counts");

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
                    catch (Exception e) {}

                    Item item = Item.getByNameOrId(itemId);

                    if (count > 1)
                    {
                        if (item != null)
                        {
                            ItemStack itemStack = new ItemStack(item, count, meta);
                            this.addSackItemStackCount(itemStack, count, null, false);
                            sacks.add(itemStack);
                        }
                        else
                        {
                            try
                            {
                                SlayerDrops slayerDrops = SlayerDrops.valueOf(itemId.toUpperCase(Locale.ROOT));
                                ItemStack itemStack = new ItemStack(slayerDrops.getBaseItem(), count);
                                this.addSackItemStackCount(itemStack, count, slayerDrops.getDisplayName(), true);
                                sacks.add(itemStack);
                            }
                            catch (Exception e)
                            {
                                DungeonDrops dungeonDrops = DungeonDrops.valueOf(itemId.toUpperCase(Locale.ROOT));
                                ItemStack itemStack = dungeonDrops.getBaseItem();
                                itemStack.stackSize = count;
                                this.addSackItemStackCount(itemStack, count, dungeonDrops.getDisplayName(), false);
                                sacks.add(itemStack);
                            }
                        }
                    }
                }
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
        SKYBLOCK_INV.add(new SkyBlockInventory(sacks, SkyBlockInventoryTabs.SACKS));
    }

    private void addSackItemStackCount(ItemStack itemStack, int count, @Nullable String altName, boolean ench)
    {
        if (count >= 1000)
        {
            if (!StringUtils.isNullOrEmpty(altName))
            {
                itemStack.setStackDisplayName(altName + EnumChatFormatting.GRAY + " x" + FORMAT.format(count));
            }
            else
            {
                itemStack.setStackDisplayName(EnumChatFormatting.RESET + itemStack.getDisplayName() + EnumChatFormatting.GRAY + " x" + FORMAT.format(count));
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
    }

    private void getPets(JsonObject currentUserProfile)
    {
        List<PetData> petData = new ArrayList<>();
        List<ItemStack> petItem = new ArrayList<>();
        JsonElement petsObj = currentUserProfile.get("pets");

        if (petsObj == null)
        {
            this.totalDisabledInv++;
            return;
        }

        JsonArray pets = petsObj.getAsJsonArray();
        int commonScore = 0;
        int uncommonScore = 0;
        int rareScore = 0;
        int epicScore = 0;
        int legendaryScore = 0;

        if (pets.size() > 0)
        {
            for (JsonElement element : pets)
            {
                double exp = 0.0D;
                String petRarity = SkyBlockPets.Tier.COMMON.name();
                int candyUsed = 0;
                JsonElement heldItemObj = element.getAsJsonObject().get("heldItem");
                JsonElement skinObj = element.getAsJsonObject().get("skin");
                SkyBlockPets.HeldItem heldItem = null;
                String heldItemType = null;
                String skin = null;
                String skinName = null;

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
                }
                if (skinObj != null && !skinObj.isJsonNull())
                {
                    skin = skinObj.getAsString();
                }
                if (heldItemObj != null && !heldItemObj.isJsonNull())
                {
                    try
                    {
                        heldItem = SkyBlockPets.HeldItem.valueOf(heldItemObj.getAsString());
                    }
                    catch (Exception e)
                    {
                        heldItemType = heldItemObj.getAsString();
                    }
                }

                SkyBlockPets.Tier tier = SkyBlockPets.Tier.valueOf(petRarity);
                boolean active = element.getAsJsonObject().get("active").getAsBoolean();
                String petType = element.getAsJsonObject().get("type").getAsString();
                NBTTagList list = new NBTTagList();

                if (heldItem != null && heldItem == SkyBlockPets.HeldItem.PET_ITEM_TIER_BOOST)
                {
                    tier = tier.getNextRarity();
                }

                PetLevel level = this.checkPetLevel(exp, tier);

                try
                {
                    EnumChatFormatting rarity = tier.getTierColor();
                    SkyBlockPets.Type type = SkyBlockPets.Type.valueOf(petType);
                    ItemStack itemStack = type.getPetItem();

                    itemStack.setStackDisplayName(EnumChatFormatting.GRAY + "[Lvl " + level.getCurrentPetLevel() + "] " + rarity + WordUtils.capitalize(petType.toLowerCase(Locale.ROOT).replace("_", " ")));
                    list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.DARK_GRAY + type.getSkillType().getName() + " Pet"));
                    list.appendTag(new NBTTagString(""));
                    list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + (level.getCurrentPetLevel() < 100 ? EnumChatFormatting.GRAY + "Progress to Level " + level.getNextPetLevel() + ": " + EnumChatFormatting.YELLOW + level.getPercent() : level.getPercent())));

                    if (level.getCurrentPetLevel() < 100)
                    {
                        list.appendTag(new NBTTagString(EnumChatFormatting.RESET + this.getTextPercentage((int)level.getCurrentPetXp(), level.getXpRequired()) + " " + EnumChatFormatting.YELLOW + FORMAT_2.format(level.getCurrentPetXp()) + EnumChatFormatting.GOLD + "/" + EnumChatFormatting.YELLOW + NumberUtils.formatWithM(level.getXpRequired())));
                    }
                    if (candyUsed > 0 || heldItem != null || heldItemType != null || skin != null)
                    {
                        list.appendTag(new NBTTagString(""));
                    }
                    if (candyUsed > 0)
                    {
                        list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "Candy Used: " + EnumChatFormatting.YELLOW + candyUsed + EnumChatFormatting.GOLD + "/" + EnumChatFormatting.YELLOW + 10));
                    }
                    if (skin != null)
                    {
                        for (PetSkin petSkin : SkyBlockPets.PET_SKIN)
                        {
                            if (skin.equals(petSkin.getSkin()))
                            {
                                itemStack = RenderUtils.setSkullSkin(itemStack.copy(), petSkin.getUUID(), petSkin.getTexture());
                                skinName = petSkin.getName();
                                break;
                            }
                        }
                        list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "Skin: " + (skinName == null ? skin : skinName)));
                    }
                    if (heldItem != null)
                    {
                        String heldItemName = heldItem.getColor() + WordUtils.capitalize(heldItem.toString().toLowerCase(Locale.ROOT).replace("pet_item_", "").replace("_", " "));

                        if (heldItem.getAltName() != null)
                        {
                            heldItemName = heldItem.getColor() + WordUtils.capitalize(heldItem.getAltName().toLowerCase(Locale.ROOT).replace("pet_item_", "").replace("_", " "));
                        }
                        list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "Held Item: " + heldItemName));
                    }
                    else
                    {
                        if (heldItemType != null)
                        {
                            list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "Held Item: " + EnumChatFormatting.RED + heldItemType));
                        }
                    }

                    list.appendTag(new NBTTagString(""));
                    list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "Total XP: " + EnumChatFormatting.YELLOW + NumberUtils.formatWithM(level.getPetXp()) + EnumChatFormatting.GOLD + "/" + EnumChatFormatting.YELLOW + NumberUtils.formatWithM(level.getTotalPetTypeXp())));
                    list.appendTag(new NBTTagString(rarity + "" + EnumChatFormatting.BOLD + tier + " PET"));
                    itemStack.getTagCompound().getCompoundTag("display").setTag("Lore", list);
                    itemStack.getSubCompound("ExtraAttributes", true).setString("id", "PET");
                    itemStack.getTagCompound().setBoolean("active", active);
                    petData.add(new PetData(tier, level.getCurrentPetLevel(), level.getCurrentPetXp(), active, Arrays.asList(itemStack)));

                    switch (tier)
                    {
                    case COMMON:
                        commonScore += 1;
                        break;
                    case UNCOMMON:
                        uncommonScore += 2;
                        break;
                    case RARE:
                        rareScore += 3;
                        break;
                    case EPIC:
                        epicScore += 4;
                        break;
                    case LEGENDARY:
                        legendaryScore += 5;
                        break;
                    default:
                        break;
                    }
                }
                catch (Exception e)
                {
                    ItemStack itemStack = new ItemStack(Items.bone);
                    itemStack.setStackDisplayName(EnumChatFormatting.RESET + "" + EnumChatFormatting.RED + WordUtils.capitalize(petType.toLowerCase(Locale.ROOT).replace("_", " ")));
                    list.appendTag(new NBTTagString(EnumChatFormatting.RED + "" + EnumChatFormatting.BOLD + "UNKNOWN PET"));
                    itemStack.getTagCompound().getCompoundTag("display").setTag("Lore", list);
                    petData.add(new PetData(SkyBlockPets.Tier.COMMON, 0, 0, false, Arrays.asList(itemStack)));
                    LoggerIN.warning("Found an unknown pet! type: {}", petType);
                }
                petData.sort((o1, o2) -> new CompareToBuilder().append(o2.isActive(), o1.isActive()).append(o2.getTier().ordinal(), o1.getTier().ordinal()).append(o2.getCurrentLevel(), o1.getCurrentLevel()).append(o2.getCurrentXp(), o1.getCurrentXp()).build());
            }
            for (PetData data : petData)
            {
                petItem.addAll(data.getItemStack());
            }
        }
        SKYBLOCK_INV.add(new SkyBlockInventory(petItem, SkyBlockInventoryTabs.PET));
        this.petScore = commonScore + uncommonScore + rareScore + epicScore + legendaryScore;
    }

    private PetLevel checkPetLevel(double petExp, SkyBlockPets.Tier tier)
    {
        ExpProgress[] progress = tier.getProgression();
        int totalPetTypeXp = 0;
        int xpRequired = 0;
        int currentLvl = 0;
        int levelToCheck = 0;
        double xpTotal = 0;
        double xpToNextLvl = 0;
        double currentXp = 0;

        for (int x = 0; x < progress.length; ++x)
        {
            totalPetTypeXp += progress[x].getXp();

            if (petExp >= xpTotal)
            {
                xpTotal += progress[x].getXp();
                currentLvl = x + 1;
                levelToCheck = progress[x].getLevel() + 1;
                xpRequired = (int)progress[x].getXp();
            }
        }

        if (currentLvl < progress.length)
        {
            xpToNextLvl = MathHelper.ceiling_double_int(xpTotal - petExp);
            currentXp = xpRequired - xpToNextLvl;
        }
        else if (currentLvl == progress.length)
        {
            xpToNextLvl = MathHelper.ceiling_double_int(Math.abs(xpTotal - petExp));
            currentXp = xpRequired - xpToNextLvl;
        }

        if (petExp >= xpTotal || currentXp >= xpRequired)
        {
            currentLvl = progress.length + 1;
            xpRequired = 0;
        }
        return new PetLevel(currentLvl, levelToCheck, currentXp, xpRequired, petExp, totalPetTypeXp);
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

        this.allStat.add(this.getFairySouls(fairyExchanges));
        this.allStat.add(this.getMagicFindFromPets(this.petScore));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.FARMING, this.farmingLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.FORAGING, this.foragingLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.MINING, this.miningLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.FISHING, this.fishingLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.COMBAT, this.combatLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.ENCHANTING, this.enchantingLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.ALCHEMY, this.alchemyLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.TAMING, this.tamingLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.CATACOMBS_DUNGEON, this.catacombsLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.ZOMBIE_SLAYER, this.zombieSlayerLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.SPIDER_SLAYER, this.spiderSlayerLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.WOLF_SLAYER, this.wolfSlayerLevel));
    }

    private BonusStatTemplate calculateSkillBonus(PlayerStatsBonus.IBonusTemplate[] bonus, int skillLevel)
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
                }
            }
        }
        return new BonusStatTemplate(healthTemp, defenseTemp, trueDefenseTemp, 0, strengthTemp, speedTemp, critChanceTemp, critDamageTemp, attackSpeedTemp, intelligenceTemp, seaCreatureChanceTemp, magicFindTemp, petLuckTemp, ferocityTemp);
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
                                }
                            }
                        }
                    }
                }
            }
        }
        this.allStat.add(new BonusStatTemplate(healthTemp, defenseTemp, trueDefenseTemp, 0, strengthTemp, speedTemp, critChanceTemp, critDamageTemp, attackSpeedTemp, intelligenceTemp, seaCreatureChanceTemp, magicFindTemp, petLuckTemp, ferocityTemp));
    }

    private void getBasicInfo(JsonObject currentProfile, JsonElement banking, JsonObject objStatus, String uuid, CommunityUpgrades communityUpgrade)
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

        String heath = ColorUtils.stringToRGB("239,83,80").toColoredFont();
        String defense = ColorUtils.stringToRGB("156,204,101").toColoredFont();
        String trueDefense = ColorUtils.stringToRGB("255,255,255").toColoredFont();
        String strength = ColorUtils.stringToRGB("181,33,30").toColoredFont();
        String speed = ColorUtils.stringToRGB("255,255,255").toColoredFont();
        String critChance = ColorUtils.stringToRGB("121,134,203").toColoredFont();
        String critDamage = ColorUtils.stringToRGB("70,90,201").toColoredFont();
        String attackSpeed = ColorUtils.stringToRGB("255,255,85").toColoredFont();
        String intelligence = ColorUtils.stringToRGB("129,212,250").toColoredFont();
        String seaCreatureChance = ColorUtils.stringToRGB("0,170,170").toColoredFont();
        String magicFind = ColorUtils.stringToRGB("85,255,255").toColoredFont();
        String petLuck = ColorUtils.stringToRGB("255,85,255").toColoredFont();
        String fairySoulsColor = ColorUtils.stringToRGB("203,54,202").toColoredFont();
        String bank = ColorUtils.stringToRGB("255,215,0").toColoredFont();
        String purseColor = ColorUtils.stringToRGB("255,165,0").toColoredFont();
        String ferocity = ColorUtils.stringToRGB("224,120,0").toColoredFont();
        String location = this.getLocation(objStatus, uuid);

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
        this.infoList.add(new SkyBlockInfo(intelligence + "\u270E Intelligence", intelligence + SKILL_AVG.format(this.allStat.getIntelligence())));
        this.infoList.add(new SkyBlockInfo(seaCreatureChance + "\u03B1 Sea Creature Chance", seaCreatureChance + SKILL_AVG.format(this.allStat.getSeaCreatureChance()) + "%"));
        this.infoList.add(new SkyBlockInfo(magicFind + "\u272F Magic Find", magicFind + SKILL_AVG.format(this.allStat.getMagicFind())));
        this.infoList.add(new SkyBlockInfo(petLuck + "\u2663 Pet Luck", petLuck + SKILL_AVG.format(this.allStat.getPetLuck())));
        this.infoList.add(new SkyBlockInfo(ferocity + "\u2AFD Ferocity", ferocity + SKILL_AVG.format(this.allStat.getFerocity())));
        this.infoList.add(new SkyBlockInfo(fairySoulsColor + "\u2618 Fairy Souls Collected", fairySoulsColor + this.totalFairySouls + "/" + SkyBlockAPIUtils.MAX_FAIRY_SOULS));

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
            double balance = banking.getAsJsonObject().get("balance").getAsDouble();
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

    private BonusStatTemplate getFairySouls(int fairyExchanges)
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
        return new BonusStatTemplate(healthBase, defenseBase, 0, 0, strengthBase, speed, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    private BonusStatTemplate getMagicFindFromPets(int petsScore)
    {
        double magicFindBase = 0;

        for (PlayerStatsBonus.PetsScore score : PlayerStatsBonus.PETS_SCORE)
        {
            int scoreToCheck = score.getScore();
            double magicFind = score.getMagicFind();

            if (scoreToCheck <= petsScore)
            {
                magicFindBase = magicFind;
            }
        }
        return new BonusStatTemplate(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, magicFindBase, 0, 0);
    }

    private String replaceStatsString(String statName, String replace)
    {
        String original = statName.replace(replace + "_", "").replace("_", " ");
        return original.equals(replace) ? "Total " + replace : WordUtils.capitalize(original) + " " + replace;
    }

    private void getSkills(JsonObject currentProfile)
    {
        this.skillLeftList.add(this.checkSkill(currentProfile.get("experience_skill_farming"), SkillType.FARMING));
        this.skillLeftList.add(this.checkSkill(currentProfile.get("experience_skill_foraging"), SkillType.FORAGING));
        this.skillLeftList.add(this.checkSkill(currentProfile.get("experience_skill_mining"), SkillType.MINING));
        this.skillLeftList.add(this.checkSkill(currentProfile.get("experience_skill_fishing"), SkillType.FISHING));
        this.skillLeftList.add(this.checkSkill(currentProfile.get("experience_skill_runecrafting"), SkillType.RUNECRAFTING, ExpProgress.RUNECRAFTING));

        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_combat"), SkillType.COMBAT));
        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_enchanting"), SkillType.ENCHANTING));
        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_alchemy"), SkillType.ALCHEMY));
        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_taming"), SkillType.TAMING));
        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_carpentry"), SkillType.CARPENTRY));

        double avg = 0.0D;
        int count = 0;
        List<SkyBlockSkillInfo> skills = new ArrayList<>();
        skills.addAll(this.skillLeftList);
        skills.addAll(this.skillRightList);

        for (SkyBlockSkillInfo skill : skills)
        {
            if (skill.getName().contains("Runecrafting") || skill.getName().contains("Carpentry"))
            {
                continue;
            }
            avg += skill.getCurrentLvl();
            ++count;
        }
        if (avg > 0)
        {
            double realAvg = avg / count;
            this.skillAvg = realAvg > 50 ? String.valueOf(50) : new BigDecimal(realAvg).setScale(2, RoundingMode.HALF_UP).toString();
        }
        if (this.skillCount == 0)
        {
            this.data.setHasSkills(false);
        }
    }

    private SkyBlockSkillInfo checkSkill(JsonElement element, SkillType type)
    {
        return this.checkSkill(element, type, ExpProgress.SKILL);
    }

    private SkyBlockSkillInfo checkSkill(JsonElement element, SkillType type, ExpProgress[] progress)
    {
        if (element != null)
        {
            double playerXp = element.getAsDouble();
            int xpRequired = 0;
            int currentLvl = 0;
            int levelToCheck = 0;
            double xpTotal = 0;
            double xpToNextLvl = 0;
            double currentXp = 0;
            double skillProgress = 0;

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
                currentXp = (int)(xpRequired - xpToNextLvl);
            }
            else
            {
                currentLvl = progress.length;
                currentXp = playerXp - xpTotal;
            }

            if (currentXp < 0 && levelToCheck <= progress.length) // fix for skill level almost reach to limit
            {
                xpToNextLvl = xpTotal - playerXp;
                currentXp = (int)(xpRequired - xpToNextLvl);
                currentLvl = progress.length - 1;
            }
            if (type != SkillType.RUNECRAFTING && type != SkillType.CARPENTRY)
            {
                skillProgress = Math.max(0, Math.min(currentXp / xpToNextLvl, 1));
            }
            this.setSkillLevel(type, currentLvl);
            this.skillCount += 1;
            return new SkyBlockSkillInfo(type.getName(), currentXp, xpRequired, currentLvl, skillProgress, xpToNextLvl <= 0);
        }
        else
        {
            return new SkyBlockSkillInfo(EnumChatFormatting.RED + type.getName() + " is not available!", 0, 0, 0, 0, false);
        }
    }

    private void setSkillLevel(SkillType type, int currentLevel)
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
        List<SkyBlockStats> auctions = new ArrayList<>();
        List<SkyBlockStats> fished = new ArrayList<>();
        List<SkyBlockStats> winter = new ArrayList<>();
        List<SkyBlockStats> petMilestone = new ArrayList<>();
        List<SkyBlockStats> others = new ArrayList<>();
        List<SkyBlockStats> mobKills = new ArrayList<>();
        List<SkyBlockStats> seaCreatures = new ArrayList<>();
        List<SkyBlockStats> dragons = new ArrayList<>();
        List<SkyBlockStats> race = new ArrayList<>();
        List<SkyBlockStats> mythosBurrowsDug = new ArrayList<>();

        // special case
        int emperorKills = 0;
        int deepMonsterKills = 0;

        for (Map.Entry<String, JsonElement> stat : stats.entrySet().stream().filter(entry -> !BLACKLIST_STATS.stream().anyMatch(stat -> entry.getKey().equals(stat))).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())).entrySet())
        {
            String statName = stat.getKey().toLowerCase(Locale.ROOT);
            double value = stat.getValue().getAsDouble();

            if (statName.startsWith("kills") || statName.endsWith("kills"))
            {
                if (SEA_CREATURES.stream().anyMatch(statName::contains))
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
                        seaCreatures.add(new SkyBlockStats(this.replaceStatsString(statName, "kills"), value));
                    }
                }
                else if (statName.contains("dragon"))
                {
                    dragons.add(new SkyBlockStats(this.replaceStatsString(statName, "kills"), value));
                }
                else
                {
                    mobKills.add(new SkyBlockStats(this.replaceStatsString(statName, "kills"), value));
                }
            }
            else if (statName.startsWith("deaths"))
            {
                this.sbDeaths.add(new SkyBlockStats(this.replaceStatsString(statName, "deaths"), value));
            }
            else
            {
                statName = RENAMED_STATS_MAP.getOrDefault(statName, statName);

                if (statName.contains("auctions"))
                {
                    auctions.add(new SkyBlockStats(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
                else if (statName.contains("items_fished") || statName.contains("shredder"))
                {
                    fished.add(new SkyBlockStats(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
                else if (statName.contains("gifts") || statName.contains("most_winter"))
                {
                    winter.add(new SkyBlockStats(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
                else if (statName.contains("pet_milestone"))
                {
                    petMilestone.add(new SkyBlockStats(WordUtils.capitalize(statName.replace("pet_milestone_", "").replace("_", " ")), value));
                }
                else if (statName.contains("race") || statName.contains("dungeon_hub"))
                {
                    race.add(new SkyBlockStats(WordUtils.capitalize(statName.replaceAll("dungeon_hub_|_best_time", "").replace("_", " ")), value));
                }
                else if (statName.startsWith("mythos_burrows_"))
                {
                    mythosBurrowsDug.add(new SkyBlockStats(WordUtils.capitalize(statName.toLowerCase(Locale.ROOT).replace("mythos_burrows_", "").replace("_", " ")), value));
                }
                else
                {
                    others.add(new SkyBlockStats(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
            }
        }

        // special case
        if (emperorKills > 0)
        {
            seaCreatures.add(new SkyBlockStats("Sea Emperor kills", emperorKills));
        }
        if (deepMonsterKills > 0)
        {
            seaCreatures.add(new SkyBlockStats("Monster of the Deep kills", deepMonsterKills));
        }

        this.sbDeaths.sort((stat1, stat2) -> new CompareToBuilder().append(stat2.getValue(), stat1.getValue()).build());
        auctions.sort((stat1, stat2) -> new CompareToBuilder().append(stat1.getName(), stat2.getName()).build());
        auctions.add(0, new SkyBlockStats(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.UNDERLINE + EnumChatFormatting.BOLD + "Auctions", 0.0F));

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

    private void sortStats(List<SkyBlockStats> list, String name)
    {
        list.sort((stat1, stat2) -> new CompareToBuilder().append(stat1.getName(), stat2.getName()).build());
        list.add(0, new SkyBlockStats(null, 0.0F));
        list.add(1, new SkyBlockStats(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.UNDERLINE + EnumChatFormatting.BOLD + name, 0.0F));
    }

    private void sortStatsByValue(List<SkyBlockStats> list, String name)
    {
        list.sort((stat1, stat2) -> new CompareToBuilder().append(stat2.getValue(), stat1.getValue()).build());
        list.add(0, new SkyBlockStats(null, 0.0F));
        list.add(1, new SkyBlockStats(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.UNDERLINE + EnumChatFormatting.BOLD + name, 0.0F));
    }

    private long checkSkyBlockItem(List<ItemStack> list, String type)
    {
        return list.stream().filter(armor -> armor != null && armor.hasTagCompound() && armor.getTagCompound().getCompoundTag("ExtraAttributes").getString("id").startsWith(type)).count();
    }

    private void getInventories(JsonObject currentProfile)
    {
        this.armorItems.addAll(SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.ARMOR).stream().filter(itemStack -> itemStack == null || itemStack.getItem() != Item.getItemFromBlock(Blocks.barrier)).collect(Collectors.toList()));

        if (this.armorItems.size() > 0)
        {
            for (int i = 0; i < 4; ++i)
            {
                GuiSkyBlockData.TEMP_ARMOR_INVENTORY.setInventorySlotContents(i, this.armorItems.get(i));
            }
        }

        List<ItemStack> mainInventory = SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.INVENTORY);
        List<ItemStack> accessoryInventory = SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.ACCESSORY_BAG);

        SKYBLOCK_INV.add(new SkyBlockInventory(mainInventory, SkyBlockInventoryTabs.INVENTORY));
        SKYBLOCK_INV.add(new SkyBlockInventory(SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.ENDER_CHEST), SkyBlockInventoryTabs.ENDER_CHEST));
        SKYBLOCK_INV.add(new SkyBlockInventory(SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.PERSONAL_VAULT), SkyBlockInventoryTabs.PERSONAL_VAULT));
        SKYBLOCK_INV.add(new SkyBlockInventory(accessoryInventory, SkyBlockInventoryTabs.ACCESSORY));
        SKYBLOCK_INV.add(new SkyBlockInventory(SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.POTION_BAG), SkyBlockInventoryTabs.POTION));
        SKYBLOCK_INV.add(new SkyBlockInventory(SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.FISHING_BAG), SkyBlockInventoryTabs.FISHING));
        SKYBLOCK_INV.add(new SkyBlockInventory(SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.QUIVER), SkyBlockInventoryTabs.QUIVER));
        SKYBLOCK_INV.add(new SkyBlockInventory(SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.CANDY), SkyBlockInventoryTabs.CANDY));
        SKYBLOCK_INV.add(new SkyBlockInventory(SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.WARDROBE), SkyBlockInventoryTabs.WARDROBE));

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
                this.activeSlayerType = SlayerType.valueOf(slayerQuest.getAsJsonObject().get("type").getAsString().toUpperCase(Locale.ROOT));
                this.activeSlayerTier = 1 + slayerQuest.getAsJsonObject().get("tier").getAsInt();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        if (slayerBosses != null)
        {
            List<SkyBlockSlayerInfo> zombie = this.getSlayer(slayerBosses, SlayerType.ZOMBIE);
            List<SkyBlockSlayerInfo> spider = this.getSlayer(slayerBosses, SlayerType.SPIDER);
            List<SkyBlockSlayerInfo> wolf = this.getSlayer(slayerBosses, SlayerType.WOLF);

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
        if (this.mc.getNetHandler().getPlayerInfo(this.profile.getName()) == null)
        {
            this.mc.getNetHandler().playerInfoMap.put(this.profile.getId(), ((IViewerLoader)new NetworkPlayerInfo(this.profile)).setLoadedFromViewer(true)); // hack into map to show their skin :D
        }

        this.player = new EntityOtherFakePlayer(FAKE_WORLD, this.profile);
        GuiSkyBlockData.renderSecondLayer = true;
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

    private List<SkyBlockSlayerInfo> getSlayer(JsonElement element, SlayerType type)
    {
        List<SkyBlockSlayerInfo> list = new ArrayList<>();
        ExpProgress[] progress = type.getProgress();
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
                boolean reachLimit = false;

                for (ExpProgress skill : progress)
                {
                    int slayerXp = (int)skill.getXp();

                    if (slayerXp <= playerSlayerXp)
                    {
                        levelToCheck = skill.getLevel();

                        if (levelToCheck < progress.length)
                        {
                            xpRequired = (int)progress[levelToCheck].getXp();
                        }
                        ++slayerLvl;
                    }
                }

                if (levelToCheck < progress.length)
                {
                    levelToCheck += 1;
                    xpToNextLvl = xpRequired - playerSlayerXp;
                }
                else
                {
                    levelToCheck = progress.length;
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

                for (int i = 1; i <= 4; i++)
                {
                    JsonElement kill = slayer.getAsJsonObject().get("boss_kills_tier_" + (i - 1));
                    int kills = this.getSlayerKill(kill);
                    amount += this.getSlayerPrice(kills, i - 1);
                    list.add(new SkyBlockSlayerInfo(EnumChatFormatting.GRAY + "Tier " + i + ": " + EnumChatFormatting.YELLOW + this.formatSlayerKill(this.getSlayerKill(kill))));
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

    private void setSlayerSkillLevel(SlayerType type, int currentLevel)
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
            int kills = element.getAsInt();
            return kills;
        }
        return 0;
    }

    private int getSlayerPrice(int kills, int index)
    {
        int price = 0;

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

    class MinionLevel
    {
        private final String minionType;
        private final String displayName;
        private final ItemStack minionItem;
        private final int minionMaxTier;
        private final SkillType category;

        public MinionLevel(String minionType, String displayName, ItemStack minionItem, int minionMaxTier, SkillType category)
        {
            this.minionType = minionType;
            this.displayName = displayName;
            this.minionItem = minionItem;
            this.minionMaxTier = minionMaxTier;
            this.category = category;
        }

        public String getMinionType()
        {
            return this.minionType;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public ItemStack getMinionItem()
        {
            return this.minionItem;
        }

        public int getMinionMaxTier()
        {
            return this.minionMaxTier;
        }

        public SkillType getMinionCategory()
        {
            return this.category;
        }
    }

    class MinionData
    {
        private final String minionType;
        private final String craftedTiers;

        public MinionData(String minionType, String craftedTiers)
        {
            this.minionType = minionType;
            this.craftedTiers = craftedTiers;
        }

        public String getMinionType()
        {
            return this.minionType;
        }

        public String getCraftedTiers()
        {
            return this.craftedTiers;
        }
    }

    class CraftedMinion
    {
        private final String minionName;
        private final String displayName;
        private final int minionMaxTier;
        private final String craftedTiers;
        private final ItemStack minionItem;
        private final SkillType category;

        public CraftedMinion(String minionName, String displayName, int minionMaxTier, String craftedTiers, ItemStack minionItem, SkillType category)
        {
            this.minionName = minionName;
            this.displayName = displayName;
            this.minionMaxTier = minionMaxTier;
            this.craftedTiers = craftedTiers;
            this.minionItem = minionItem;
            this.category = category;
        }

        public String getMinionName()
        {
            return this.minionName;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public int getMinionMaxTier()
        {
            return this.minionMaxTier;
        }

        public String getCraftedTiers()
        {
            return this.craftedTiers;
        }

        public ItemStack getMinionItem()
        {
            return this.minionItem;
        }

        public SkillType getMinionCategory()
        {
            return this.category;
        }
    }

    class SkyBlockInventory
    {
        private final List<ItemStack> items;
        private final SkyBlockInventoryTabs tab;

        public SkyBlockInventory(List<ItemStack> items, SkyBlockInventoryTabs tab)
        {
            this.items = items;
            this.tab = tab;
        }

        public List<ItemStack> getItems()
        {
            return this.items;
        }

        public SkyBlockInventoryTabs getTab()
        {
            return this.tab;
        }
    }

    private class PetData
    {
        private final SkyBlockPets.Tier tier;
        private final int currentLevel;
        private final double currentXp;
        private final boolean isActive;
        private final List<ItemStack> itemStack;

        public PetData(SkyBlockPets.Tier tier, int currentLevel, double currentXp, boolean isActive, List<ItemStack> itemStack)
        {
            this.tier = tier;
            this.currentLevel = currentLevel;
            this.currentXp = currentXp;
            this.isActive = isActive;
            this.itemStack = itemStack;
        }

        public SkyBlockPets.Tier getTier()
        {
            return this.tier;
        }

        public List<ItemStack> getItemStack()
        {
            return this.itemStack;
        }

        public int getCurrentLevel()
        {
            return this.currentLevel;
        }

        public double getCurrentXp()
        {
            return this.currentXp;
        }

        public boolean isActive()
        {
            return this.isActive;
        }
    }

    private class PetLevel
    {
        private final int currentPetLevel;
        private final int nextPetLevel;
        private final double currentPetXp;
        private final int xpRequired;
        private final double petXp;
        private final int totalPetTypeXp;

        public PetLevel(int currentPetLevel, int nextPetLevel, double currentPetXp, int xpRequired, double petXp, int totalPetTypeXp)
        {
            this.currentPetLevel = currentPetLevel;
            this.nextPetLevel = nextPetLevel;
            this.currentPetXp = currentPetXp;
            this.xpRequired = xpRequired;
            this.petXp = petXp;
            this.totalPetTypeXp = totalPetTypeXp;
        }

        public int getCurrentPetLevel()
        {
            return this.currentPetLevel;
        }

        public int getNextPetLevel()
        {
            return this.nextPetLevel;
        }

        public double getCurrentPetXp()
        {
            return this.currentPetXp;
        }

        public int getXpRequired()
        {
            return this.xpRequired;
        }

        public double getPetXp()
        {
            return this.petXp;
        }

        public int getTotalPetTypeXp()
        {
            return this.totalPetTypeXp;
        }

        public String getPercent()
        {
            if (this.xpRequired > 0)
            {
                double percent = this.currentPetXp * 100.0D / this.xpRequired;
                return new ModDecimalFormat("##.#").format(percent) + "%";
            }
            else
            {
                return EnumChatFormatting.AQUA.toString() + EnumChatFormatting.BOLD + "MAX LEVEL";
            }
        }
    }

    static class ContainerArmor extends Container
    {
        public ContainerArmor(boolean info)
        {
            int x = info ? -62 : -52;
            this.addSlotToContainer(new Slot(GuiSkyBlockData.TEMP_ARMOR_INVENTORY, 0, x, 75)); // boots
            this.addSlotToContainer(new Slot(GuiSkyBlockData.TEMP_ARMOR_INVENTORY, 1, x, 56));
            this.addSlotToContainer(new Slot(GuiSkyBlockData.TEMP_ARMOR_INVENTORY, 2, x, 36));
            this.addSlotToContainer(new Slot(GuiSkyBlockData.TEMP_ARMOR_INVENTORY, 3, x, 12)); // helmet
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
                    this.addSlotToContainer(new Slot(GuiSkyBlockData.TEMP_INVENTORY, columns * 9 + rows, 12 + rows * 18, 18 + columns * 18));
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
            return this.itemList.size() > GuiSkyBlockData.SIZE;
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
                        GuiSkyBlockData.TEMP_INVENTORY.setInventorySlotContents(l + k * 9, this.itemList.get(i1));
                    }
                    else
                    {
                        GuiSkyBlockData.TEMP_INVENTORY.setInventorySlotContents(l + k * 9, null);
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

    class SkyBlockSkillInfo
    {
        private final String name;
        private final double currentXp;
        private final int xpRequired;
        private final int currentLvl;
        private final double skillProgress;
        private final boolean reachLimit;

        public SkyBlockSkillInfo(String name, double currentXp, int xpRequired, int currentLvl, double skillProgress, boolean reachLimit)
        {
            this.name = name;
            this.currentXp = currentXp;
            this.xpRequired = xpRequired;
            this.currentLvl = currentLvl;
            this.skillProgress = skillProgress;
            this.reachLimit = reachLimit;
        }

        public String getName()
        {
            return this.name;
        }

        public double getCurrentXp()
        {
            return this.currentXp;
        }

        public int getXpRequired()
        {
            return this.xpRequired;
        }

        public int getCurrentLvl()
        {
            return this.currentLvl;
        }

        public double getSkillProgress()
        {
            return this.skillProgress;
        }

        public boolean isReachLimit()
        {
            return this.reachLimit;
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
        private final GuiSkyBlockData parent;

        public InfoStats(GuiSkyBlockData parent, int width, int height, int top, int bottom, int left, int entryHeight, int parentWidth, int parentHeight, List<SkyBlockInfo> stats)
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
            this.parent.drawString(this.parent.mc.fontRendererObj, stat.getTitle() + (isCurrentUpgrade ? GuiSkyBlockAPIViewer.downloadingStates[(int)(Minecraft.getSystemTime() / 250L % GuiSkyBlockAPIViewer.downloadingStates.length)] : ""), this.parent.guiLeft - 20, top, index % 2 == 0 ? 16777215 : 9474192);
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
        private final GuiSkyBlockData parent;

        public SlayerStats(GuiSkyBlockData parent, int width, int height, int top, int bottom, int left, int entryHeight, int parentWidth, int parentHeight, List<SkyBlockSlayerInfo> stats)
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
                    EntityZombie zombie = new EntityZombie(FAKE_WORLD);
                    ItemStack heldItem = new ItemStack(Items.diamond_hoe);
                    heldItem.addEnchantment(Enchantment.unbreaking, 1);
                    ItemStack helmet = RenderUtils.getSkullItemStack(GuiSkyBlockData.REVENANT_HORROR_HEAD[0], GuiSkyBlockData.REVENANT_HORROR_HEAD[1]);
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
                    GuiSkyBlockData.drawEntityOnScreen(this.parent.guiLeft - 30, top + 60, 40, zombie);
                }
                else if (stat.getText().equals("Spider"))
                {
                    EntitySpider spider = new EntitySpider(FAKE_WORLD);
                    EntityCaveSpider cave = new EntityCaveSpider(this.parent.mc.theWorld);
                    GuiSkyBlockData.drawEntityOnScreen(this.parent.guiLeft - 30, top + 40, 40, cave);
                    GuiSkyBlockData.drawEntityOnScreen(this.parent.guiLeft - 30, top + 60, 40, spider);
                    GlStateManager.blendFunc(770, 771);
                }
                else
                {
                    EntityWolf wolf = new EntityWolf(FAKE_WORLD);
                    wolf.setAngry(true);
                    GuiSkyBlockData.drawEntityOnScreen(this.parent.guiLeft - 30, top + 60, 40, wolf);
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
        private final GuiSkyBlockData parent;

        public Others(GuiSkyBlockData parent, int width, int height, int top, int bottom, int left, int entryHeight, int parentWidth, int parentHeight, List<?> stats)
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

                if (obj instanceof SkyBlockStats)
                {
                    SkyBlockStats stat = (SkyBlockStats)obj;

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
        private final List<SkyBlockCollection> collection;
        private final GuiSkyBlockData parent;

        public SkyBlockCollections(GuiSkyBlockData parent, int width, int height, int top, int bottom, int left, int entryHeight, int parentWidth, int parentHeight, List<SkyBlockCollection> collection)
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
            SkyBlockCollection collection = this.collection.get(index);

            if (collection.getCollectionType() != null)
            {
                if (collection.getItemStack() != null)
                {
                    String collectionLvl = collection.getCollectionType() == SkyBlockCollection.Type.UNKNOWN ? "" : " " + EnumChatFormatting.GOLD + collection.getLevel();
                    this.parent.drawItemStackSlot(this.parent.guiLeft - 65, top, collection.getItemStack());
                    this.parent.drawString(this.parent.mc.fontRendererObj, (collection.getCollectionType() == SkyBlockCollection.Type.UNKNOWN ? EnumChatFormatting.RED : "") + collection.getItemStack().getDisplayName() + collectionLvl, this.parent.guiLeft - 41, top + 6, 16777215);
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
        private final List<CraftedMinion> craftMinions;
        private final GuiSkyBlockData parent;

        public SkyBlockCraftedMinions(GuiSkyBlockData parent, int width, int height, int top, int bottom, int left, int entryHeight, int parentWidth, int parentHeight, List<CraftedMinion> craftMinions)
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
            CraftedMinion craftedMinion = this.craftMinions.get(index);

            if (craftedMinion.getMinionItem() != null)
            {
                String name = craftedMinion.getDisplayName() != null ? WordUtils.capitalize(craftedMinion.getDisplayName().toLowerCase(Locale.ROOT).replace("_", " ")) : WordUtils.capitalize(craftedMinion.getMinionName().toLowerCase(Locale.ROOT).replace("_", " "));
                this.parent.drawItemStackSlot(this.parent.guiLeft - 102, top, craftedMinion.getMinionItem());
                this.parent.drawString(this.parent.mc.fontRendererObj, name + " Minion " + EnumChatFormatting.GOLD + craftedMinion.getMinionMaxTier(), this.parent.guiLeft - 79, top + 6, 16777215);
                this.parent.drawString(this.parent.mc.fontRendererObj, craftedMinion.getCraftedTiers(), this.parent.guiLeft - this.parent.mc.fontRendererObj.getStringWidth(craftedMinion.getCraftedTiers()) + 192, top + 6, index % 2 == 0 ? 16777215 : 9474192);
            }
            else
            {
                if (craftedMinion.getMinionName() != null)
                {
                    this.parent.drawString(this.parent.mc.fontRendererObj, EnumChatFormatting.YELLOW + "" + EnumChatFormatting.BOLD + EnumChatFormatting.UNDERLINE + craftedMinion.getMinionName(), this.parent.guiLeft - 100, top + 5, 16777215);
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

    public class BonusStatTemplate
    {
        private double health;
        private double defense;
        private double trueDefense;
        private double effectiveHealth;
        private double strength;
        private double speed;
        private double critChance;
        private double critDamage;
        private double attackSpeed;
        private double intelligence;
        private double seaCreatureChance;
        private double magicFind;
        private double petLuck;
        private double ferocity;

        public BonusStatTemplate(double health, double defense, double trueDefense, double effectiveHealth, double strength, double speed, double critChance, double critDamage, double attackSpeed, double intelligence, double seaCreatureChance, double magicFind, double petLuck, double ferocity)
        {
            this.health = health;
            this.defense = defense;
            this.trueDefense = trueDefense;
            this.effectiveHealth = effectiveHealth;
            this.strength = strength;
            this.speed = speed;
            this.critChance = critChance;
            this.critDamage = critDamage;
            this.attackSpeed = attackSpeed;
            this.intelligence = intelligence;
            this.seaCreatureChance = seaCreatureChance;
            this.magicFind = magicFind;
            this.petLuck = petLuck;
            this.ferocity = ferocity;
        }

        public BonusStatTemplate add(BonusStatTemplate toAdd)
        {
            this.health += toAdd.health;
            this.defense += toAdd.defense;
            this.trueDefense += toAdd.trueDefense;
            this.effectiveHealth += toAdd.effectiveHealth;
            this.strength += toAdd.strength;
            this.speed += toAdd.speed;
            this.critChance += toAdd.critChance;
            this.critDamage += toAdd.critDamage;
            this.attackSpeed += toAdd.attackSpeed;
            this.intelligence += toAdd.intelligence;
            this.seaCreatureChance += toAdd.seaCreatureChance;
            this.magicFind += toAdd.magicFind;
            this.petLuck += toAdd.petLuck;
            this.ferocity += toAdd.ferocity;
            return new BonusStatTemplate(this.health, this.defense, this.trueDefense, this.effectiveHealth, this.strength, this.speed, this.critChance, this.critDamage, this.attackSpeed, this.intelligence, this.seaCreatureChance, this.magicFind, this.petLuck, this.ferocity);
        }

        public double getHealth()
        {
            return this.health;
        }

        public double getDefense()
        {
            if (this.defense <= 0)
            {
                return 0;
            }
            return this.defense;
        }

        public double getTrueDefense()
        {
            return this.trueDefense;
        }

        public double getEffectiveHealth()
        {
            return this.effectiveHealth;
        }

        public double getStrength()
        {
            return this.strength;
        }

        public double getSpeed()
        {
            return this.speed;
        }

        public double getCritChance()
        {
            if (this.critChance > 100)
            {
                return 100;
            }
            return this.critChance;
        }

        public double getCritDamage()
        {
            return this.critDamage;
        }

        public double getAttackSpeed()
        {
            return this.attackSpeed;
        }

        public double getIntelligence()
        {
            return this.intelligence;
        }

        public double getSeaCreatureChance()
        {
            return this.seaCreatureChance;
        }

        public double getMagicFind()
        {
            return this.magicFind;
        }

        public double getPetLuck()
        {
            return this.petLuck;
        }

        public double getFerocity()
        {
            return this.ferocity;
        }

        public void setHealth(double health)
        {
            this.health = health;
        }

        public void setDefense(double defense)
        {
            this.defense = defense;
        }

        public void setTrueDefense(double trueDefense)
        {
            this.trueDefense = trueDefense;
        }

        public void setEffectiveHealth(double effectiveHealth)
        {
            this.effectiveHealth = effectiveHealth;
        }

        public void setStrength(double strength)
        {
            this.strength = strength;
        }

        public void setSpeed(double speed)
        {
            this.speed = speed;
        }

        public void setCritChance(double critChance)
        {
            this.critChance = critChance;
        }

        public void setCritDamage(double critDamage)
        {
            this.critDamage = critDamage;
        }

        public void setAttackSpeed(double attackSpeed)
        {
            this.attackSpeed = attackSpeed;
        }

        public void setIntelligence(double intelligence)
        {
            this.intelligence = intelligence;
        }

        public void setSeaCreatureChance(double seaCreatureChance)
        {
            this.seaCreatureChance = seaCreatureChance;
        }

        public void setMagicFind(double magicFind)
        {
            this.magicFind = magicFind;
        }

        public void setPetLuck(double petLuck)
        {
            this.petLuck = petLuck;
        }

        public void setFerocity(double ferocity)
        {
            this.ferocity = ferocity;
        }

        public BonusStatTemplate addHealth(double health)
        {
            this.health += health;
            return this;
        }

        public BonusStatTemplate addDefense(double defense)
        {
            this.defense += defense;
            return this;
        }

        public BonusStatTemplate addTrueDefense(double trueDefense)
        {
            this.trueDefense += trueDefense;
            return this;
        }

        public BonusStatTemplate addEffectiveHealth(double effectiveHealth)
        {
            this.effectiveHealth += effectiveHealth;
            return this;
        }

        public BonusStatTemplate addStrength(double strength)
        {
            this.strength += strength;
            return this;
        }

        public BonusStatTemplate addSpeed(double speed)
        {
            this.speed += speed;
            return this;
        }

        public BonusStatTemplate addCritChance(double critChance)
        {
            this.critChance += critChance;
            return this;
        }

        public BonusStatTemplate addCritDamage(double critDamage)
        {
            this.critDamage += critDamage;
            return this;
        }

        public BonusStatTemplate addAttackSpeed(double attackSpeed)
        {
            this.attackSpeed += attackSpeed;
            return this;
        }

        public BonusStatTemplate addIntelligence(double intelligence)
        {
            this.intelligence += intelligence;
            return this;
        }

        public BonusStatTemplate addSeaCreatureChance(double seaCreatureChance)
        {
            this.seaCreatureChance += seaCreatureChance;
            return this;
        }

        public BonusStatTemplate addMagicFind(double magicFind)
        {
            this.magicFind += magicFind;
            return this;
        }

        public BonusStatTemplate addPetLuck(double petLuck)
        {
            this.petLuck += petLuck;
            return this;
        }

        public BonusStatTemplate addFerocity(double ferocity)
        {
            this.ferocity += ferocity;
            return this;
        }
    }

    public enum SkillType
    {
        FARMING("Farming"),
        FORAGING("Foraging"),
        MINING("Mining"),
        FISHING("Fishing"),
        COMBAT("Combat"),
        ENCHANTING("Enchanting"),
        ALCHEMY("Alchemy"),
        RUNECRAFTING("Runecrafting"),
        CARPENTRY("Carpentry"),
        TAMING("Taming");

        private final String name;

        private SkillType(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }
    }

    public enum DungeonSkillType
    {
        HEALER("Healer"),
        MAGE("Mage"),
        BERSERK("Berserk"),
        ARCHER("Archer"),
        TANK("Tank"),
        THE_CATACOMBS("The Catacombs");

        private final String name;

        private DungeonSkillType(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }
    }

    private enum SlayerType
    {
        ZOMBIE("Zombie", ExpProgress.ZOMBIE_SLAYER),
        SPIDER("Spider", ExpProgress.SPIDER_SLAYER),
        WOLF("Wolf", ExpProgress.WOLF_SLAYER);

        private final String name;
        private final ExpProgress[] progress;

        private SlayerType(String name, ExpProgress[] progress)
        {
            this.name = name;
            this.progress = progress;
        }

        public String getName()
        {
            return this.name;
        }

        public ExpProgress[] getProgress()
        {
            return this.progress;
        }
    }

    private static class InventoryExtended extends InventoryBasic
    {
        public InventoryExtended(int slotCount)
        {
            super("tmp", false, slotCount);
        }

        @Override
        public int getInventoryStackLimit()
        {
            return 20160;
        }
    }

    private enum SlayerDrops
    {
        TARANTULA_WEB(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Tarantula Web", Items.string),
        REVENANT_FLESH(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Revenant Flesh", Items.rotten_flesh),
        WOLF_TOOTH(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Wolf Tooth", Items.ghast_tear);

        private final String displayName;
        private final Item baseItem;

        private SlayerDrops(String displayName, Item baseItem)
        {
            this.displayName = displayName;
            this.baseItem = baseItem;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public Item getBaseItem()
        {
            return this.baseItem;
        }
    }

    private enum DungeonDrops
    {
        SPIRIT_LEAP(EnumChatFormatting.RESET.toString() + EnumChatFormatting.BLUE + "Spirit Leap", new ItemStack(Items.ender_pearl)),
        DUNGEON_DECOY(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Decoy", new ItemStack(Items.spawn_egg)),
        INFLATABLE_JERRY(EnumChatFormatting.RESET.toString() + EnumChatFormatting.WHITE + "Inflatable Jerry", new ItemStack(Items.spawn_egg, 0, EntityList.getIDFromString("Villager"))),
        DUNGEON_TRAP(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Dungeon Trap", new ItemStack(Blocks.heavy_weighted_pressure_plate));

        private final String displayName;
        private final ItemStack baseItem;

        private DungeonDrops(String displayName, ItemStack baseItem)
        {
            this.displayName = displayName;
            this.baseItem = baseItem;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public ItemStack getBaseItem()
        {
            return this.baseItem;
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
        protected static final ViewButton[] VALUES = ViewButton.values();

        private ViewButton(int id)
        {
            this.id = id;
        }

        public static ViewButton getTypeForButton(GuiButton button)
        {
            for (ViewButton viewButton : ViewButton.VALUES)
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
        protected static final OthersViewButton[] VALUES = OthersViewButton.values();

        private OthersViewButton(int id)
        {
            this.id = id;
        }

        public static OthersViewButton getTypeForButton(GuiButton button)
        {
            for (OthersViewButton viewButton : OthersViewButton.VALUES)
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
        protected static final BasicInfoViewButton[] VALUES = BasicInfoViewButton.values();

        private BasicInfoViewButton(int id)
        {
            this.id = id;
        }

        public static BasicInfoViewButton getTypeForButton(GuiButton button)
        {
            for (BasicInfoViewButton viewButton : BasicInfoViewButton.VALUES)
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