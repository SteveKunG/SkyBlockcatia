package com.stevekung.skyblockcatia.gui.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.lwjgl.input.Mouse;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.config.ConfigManagerIN;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.event.ClientEventHandler;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.integration.SkyBlockAddonsBackpack;
import com.stevekung.skyblockcatia.utils.*;

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
import net.minecraftforge.fml.client.GuiScrollingList;

public class GuiSkyBlockData extends GuiScreen
{
    private static final ResourceLocation INVENTORY_TABS = new ResourceLocation("skyblockcatia:textures/gui/tabs.png");
    private static final ResourceLocation XP_BARS = new ResourceLocation("skyblockcatia:textures/gui/skill_xp_bar.png");
    private static final String[] REVENANT_HORROR_HEAD = new String[] {"0862e0b0-a14f-3f93-894f-013502936b59", "eyJ0aW1lc3RhbXAiOjE1Njg0NTc0MjAxMzcsInByb2ZpbGVJZCI6IjQxZDNhYmMyZDc0OTQwMGM5MDkwZDU0MzRkMDM4MzFiIiwicHJvZmlsZU5hbWUiOiJNZWdha2xvb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RiYWQ5OWVkM2M4MjBiNzk3ODE5MGFkMDhhOTM0YTY4ZGZhOTBkOTk4NjgyNWRhMWM5N2Y2ZjIxZjQ5YWQ2MjYifX19"};

    // Based stuff
    private boolean firstLoad;
    private boolean loadingApi = true;
    private boolean error = false;
    private String errorMessage;
    private String statusMessage;
    private GuiButton doneButton;
    private GuiButton backButton;
    private final List<ProfileDataCallback> profiles;
    private final String sbProfileId;
    private final String sbProfileName;
    private final String username;
    private final String displayName;
    private final String guild;
    private final String uuid;
    private final GameProfile profile;
    private final StopWatch watch = new StopWatch();
    private GuiScrollingList currentSlot;
    private int currentSlotId = -1;
    private int currentBasicSlotId = -1;
    private int currentOthersSlotId = -1;
    private ViewButton viewButton = ViewButton.INFO;
    private OthersViewButton othersButton = OthersViewButton.KILLS;
    private BasicInfoViewButton basicInfoButton = BasicInfoViewButton.INFO;
    private boolean updated;
    private final ViewerData data = new ViewerData();
    private int skillCount;

    // API
    private static final int MAXED_UNIQUE_MINIONS = 572;
    private static final Pattern STATS_PATTERN = Pattern.compile("(?<type>Strength|Crit Chance|Crit Damage|Health|Defense|Speed|Intelligence|True Defense|Sea Creature Chance|Magic Find|Pet Luck|Bonus Attack Speed): (?<value>(?:\\+|\\-)[0-9,.]+)?(?:\\%){0,1}(?:(?: HP(?: \\((?:\\+|\\-)[0-9,.]+ HP\\)){0,1}(?: \\(\\w+ (?:\\+|\\-)[0-9,.]+ HP\\)){0,1})|(?: \\((?:\\+|\\-)[0-9,.]+\\))|(?: \\(\\w+ (?:\\+|\\-)[0-9,.]+(?:\\%){0,1}\\))){0,1}");
    private static final DecimalFormat FORMAT = new DecimalFormat("#,###");
    private static final DecimalFormat FORMAT_2 = new DecimalFormat("#,###.#");
    private static final DecimalFormat NUMBER_FORMAT_WITH_SYMBOL = new DecimalFormat("+#;-#");
    private static final DecimalFormat SKILL_AVG = new DecimalFormat("##.#");
    private static final DecimalFormat SKILL_AVG1 = new DecimalFormat("##.##");
    public static boolean renderSecondLayer;
    private final List<SkyBlockInfo> infoList = new ArrayList<>();
    private final List<SkyBlockSkillInfo> skillLeftList = new ArrayList<>();
    private final List<SkyBlockSkillInfo> skillRightList = new ArrayList<>();
    private final List<SkyBlockSlayerInfo> slayerInfo = new ArrayList<>();
    private final List<SkyBlockStats> sbKills = new ArrayList<>();
    private final List<SkyBlockStats> sbDeaths = new ArrayList<>();
    private final List<SkyBlockStats> sbOthers = new ArrayList<>();
    private final List<CraftedMinion> sbCraftedMinions = new ArrayList<>();
    private final List<ItemStack> armorItems = new ArrayList<>();
    private final List<ItemStack> inventoryToStats = new ArrayList<>();
    private final List<SkyBlockCollection> collections = new ArrayList<>();
    private final Multimap<String, Integer> craftedMinions = HashMultimap.create();
    private int craftedMinionCount;
    private int currentMinionSlot;
    private int slayerTotalAmountSpent;
    private int totalSlayerXp;
    private EntityOtherFakePlayer player;
    private String skillAvg;
    private int petScore;

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
    private final ContainerArmor skyBlockArmorContainer;

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
    private int zombieSlayerLevel;
    private int spiderSlayerLevel;
    private int wolfSlayerLevel;
    private BonusStatTemplate allStat = new BonusStatTemplate(100, 0, 0, 0, 0, 100, 30, 50, 0, 100, 20, 10, 0);

    // GuiContainer fields
    private int xSize;
    private int ySize;
    private int guiLeft;
    private int guiTop;
    private Slot theSlot;

    // Patcher Compatibility
    private static Class<?> patcherConfig;
    private boolean patcherEntityCulling;

    public GuiSkyBlockData(List<ProfileDataCallback> profiles, ProfileDataCallback callback)
    {
        this.firstLoad = true;
        this.allowUserInput = true;
        this.skyBlockContainer = new ContainerSkyBlock();
        this.skyBlockArmorContainer = new ContainerArmor();
        this.profiles = profiles;
        this.sbProfileId = callback.getProfileId();
        this.sbProfileName = callback.getProfileName();
        this.username = callback.getUsername();
        this.displayName = callback.getDisplayName();
        this.guild = callback.getGuild();
        this.uuid = callback.getUUID();
        this.profile = callback.getGameProfile();

        this.xSize = 202;
        this.ySize = 125;

        if (SkyBlockcatiaMod.isPatcherLoaded)
        {
            try
            {
                patcherConfig = Class.forName("club.sk1er.patcher.config.PatcherConfig");
                Field entityCulling = patcherConfig.getDeclaredField("entityCulling");
                this.patcherEntityCulling = entityCulling.getBoolean(patcherConfig);
                entityCulling.set(patcherConfig, false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initGui()
    {
        this.buttonList.clear();

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
                    LoggerIN.info("API Download finished in: {}ms", this.watch.getTime());
                    this.watch.reset();
                }
                catch (Throwable e)
                {
                    this.setErrorMessage(e.getStackTrace()[0].toString());
                    e.printStackTrace();
                }
            });
        }

        this.buttonList.add(this.doneButton = new GuiButton(0, this.width / 2 - 154, this.height - 25, 150, 20, LangUtils.translate("gui.close")));
        this.buttonList.add(this.backButton = new GuiButton(1, this.width / 2 + 4, this.height - 25, 150, 20, LangUtils.translate("gui.back")));
        GuiButton infoButton = new GuiButton(ViewButton.INFO.id, this.width / 2 - 185, 6, 80, 20, LangUtils.translate("gui.sb_view_info"));
        infoButton.enabled = false;
        this.buttonList.add(infoButton);
        this.buttonList.add(new GuiButton(ViewButton.SKILLS.id, this.width / 2 - 88, 6, 80, 20, LangUtils.translate("gui.sb_view_skills")));
        this.buttonList.add(new GuiButton(ViewButton.SLAYERS.id, this.width / 2 + 8, 6, 80, 20, LangUtils.translate("gui.sb_view_slayers")));
        this.buttonList.add(new GuiButton(ViewButton.OTHERS.id, this.width / 2 + 104, 6, 80, 20, LangUtils.translate("gui.sb_view_others")));

        GuiButton statKillsButton = new GuiButton(OthersViewButton.KILLS.id, this.width / 2 - 124, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.kills"));
        statKillsButton.enabled = false;
        this.buttonList.add(statKillsButton);
        this.buttonList.add(new GuiButton(OthersViewButton.DEATHS.id, this.width / 2 - 40, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.deaths")));
        this.buttonList.add(new GuiButton(OthersViewButton.OTHER_STATS.id, this.width / 2 + 44, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.others_stats")));

        for (GuiButton viewButton : this.buttonList)
        {
            if (OthersViewButton.getTypeForButton(viewButton) != null)
            {
                viewButton.visible = false;
            }
        }

        GuiButton basicInfoButton = new GuiButton(BasicInfoViewButton.INFO.id, this.width / 2 - 170, this.height - 48, 80, 20, LangUtils.translate("gui.sb_basic_info"));
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
                    button.enabled = this.othersButton != othersType;
                }
            }
            this.updated = true;
        }

        int i = this.selectedTabIndex;
        this.selectedTabIndex = -1;
        this.setCurrentTab(SkyBlockInventoryTabs.tabArray[i]);

        this.guiLeft = (this.width - this.xSize) / 2 + 50;
        this.guiTop = (this.height - this.ySize) / 2 + 10;

        if (this.currentSlotId == -1 || this.currentSlotId == ViewButton.INFO.id)
        {
            if (this.currentBasicSlotId == -1 || this.currentBasicSlotId == BasicInfoViewButton.INFO.id)
            {
                this.currentSlot = new InfoStats(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, this.infoList);
            }
            else if (this.currentBasicSlotId == BasicInfoViewButton.INVENTORY.id)
            {
                this.currentSlot = new EmptyStats(this.mc, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, EmptyStats.Type.INVENTORY);
                this.setCurrentTab(SkyBlockInventoryTabs.tabArray[this.selectedTabIndex]);
            }
            else if (this.currentBasicSlotId == BasicInfoViewButton.COLLECTIONS.id)
            {
                this.currentSlot = new SkyBlockCollections(this, this.width - 119, this.height, 40, this.height - 50, 59, 20, this.width, this.height, this.collections);
            }
            else if (this.currentBasicSlotId == BasicInfoViewButton.CRAFTED_MINIONS.id)
            {
                this.currentSlot = new SkyBlockCraftedMinions(this, this.width - 119, this.height, 40, this.height - 70, 59, 20, this.width, this.height, this.sbCraftedMinions);
            }
        }
        else if (this.currentSlotId == ViewButton.SKILLS.id)
        {
            this.currentSlot = new EmptyStats(this.mc, this.width - 119, this.height, 40, this.height - 28, 59, 12, this.width, this.height, EmptyStats.Type.SKILL);
            this.hideOthersButton();
            this.hideBasicInfoButton();
        }
        else if (this.currentSlotId == ViewButton.SLAYERS.id)
        {
            this.currentSlot = new SlayerStats(this, this.width - 119, this.height, 40, this.height - 49, 59, 16, this.width, this.height, this.slayerInfo);
            this.hideOthersButton();
            this.hideBasicInfoButton();
        }
        else if (this.currentSlotId == ViewButton.OTHERS.id)
        {
            SkyBlockStats.Type statType = SkyBlockStats.Type.KILLS;
            List<SkyBlockStats> list = null;

            if (this.currentOthersSlotId == -1 || this.currentOthersSlotId == OthersViewButton.KILLS.id)
            {
                statType = SkyBlockStats.Type.KILLS;
                list = this.sbKills;
            }
            else if (this.currentOthersSlotId == OthersViewButton.DEATHS.id)
            {
                statType = SkyBlockStats.Type.DEATHS;
                list = this.sbDeaths;
            }
            else if (this.currentOthersSlotId == OthersViewButton.OTHER_STATS.id)
            {
                statType = SkyBlockStats.Type.OTHERS;
                list = this.sbOthers;
            }

            if (list != null)
            {
                this.currentSlot = new Others(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, list, statType);
            }
            this.hideBasicInfoButton();

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
        this.mc.getNetHandler().playerInfoMap.values().removeIf(network -> ((IViewerLoader)network).isLoadedFromViewer());
        GuiSkyBlockData.renderSecondLayer = false;

        if (SkyBlockcatiaMod.isPatcherLoaded)
        {
            try
            {
                Field entityCulling = patcherConfig.getDeclaredField("entityCulling");
                entityCulling.set(patcherConfig, this.patcherEntityCulling);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
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
                this.mc.displayGuiScreen(this.profiles.size() == 0 ? new GuiSkyBlockAPIViewer(GuiSkyBlockAPIViewer.GuiState.EMPTY, this.username, this.displayName, this.guild) : new GuiSkyBlockAPIViewer(GuiSkyBlockAPIViewer.GuiState.SEARCH, this.username, this.displayName, this.guild, this.profiles));
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

                    if (SkyBlockcatiaMod.isSkyblockAddonsLoaded && SkyBlockAddonsBackpack.INSTANCE.isFreezeBackpack())
                    {
                        return;
                    }
                    ClientUtils.printClientMessage(JsonUtils.create("Click to view ").appendSibling(JsonUtils.create(this.theSlot.getStack().getDisplayName()).setChatStyle(JsonUtils.gold()).appendSibling(JsonUtils.create(" recipe").setChatStyle(JsonUtils.green()))).setChatStyle(JsonUtils.green().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewrecipe " + itemId))));
                }
            }
        }
        if (SkyBlockcatiaMod.isSkyblockAddonsLoaded)
        {
            SkyBlockAddonsBackpack.INSTANCE.keyTyped(keyCode);
        }
        if (keyCode == 1)
        {
            this.actionPerformed(this.backButton);
        }
        else if (keyCode == 63)
        {
            this.mc.displayGuiScreen(new GuiSkyBlockData(this.profiles, new ProfileDataCallback(this.sbProfileId, this.sbProfileName, this.username, this.displayName, this.guild, this.uuid, this.profile, -1)));
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
                    if (this.isMouseOverTab(tab, i, j))
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

        if (this.loadingApi)
        {
            String text = "Downloading SkyBlock stats";
            int i = this.fontRendererObj.getStringWidth(text);
            this.drawCenteredString(this.fontRendererObj, text, this.width / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2 - 35, 16777215);
            this.drawString(this.fontRendererObj, GuiSkyBlockAPIViewer.downloadingStates[(int)(Minecraft.getSystemTime() / 500L % GuiSkyBlockAPIViewer.downloadingStates.length)], this.width / 2 + i / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2 - 35, 16777215);
            this.drawCenteredString(this.fontRendererObj, "Status: " + EnumChatFormatting.GRAY + this.statusMessage, this.width / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2 - 15, 16777215);
        }
        else
        {
            if (this.error)
            {
                this.drawCenteredString(this.fontRendererObj, "SkyBlock API Viewer", this.width / 2, 20, 16777215);
                this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.RED + this.errorMessage, this.width / 2, 100, 16777215);
                super.drawScreen(mouseX, mouseY, partialTicks);
            }
            else
            {
                if (this.currentSlot != null)
                {
                    this.currentSlot.drawScreen(mouseX, mouseY, partialTicks);
                }

                this.drawCenteredString(this.fontRendererObj, this.displayName + EnumChatFormatting.YELLOW + " Profile: " + EnumChatFormatting.GOLD + this.sbProfileName + this.guild, this.width / 2, 29, 16777215);

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
                    GuiSkyBlockData.drawEntityOnScreen(this.width / 2 - 106, this.height / 2 + 40, 40, this.player);
                }
                else if (this.currentSlot instanceof EmptyStats)
                {
                    EmptyStats stat = (EmptyStats)this.currentSlot;

                    if (stat.getType() == EmptyStats.Type.INVENTORY)
                    {
                        this.drawContainerSlot(mouseX, mouseY);

                        RenderHelper.disableStandardItemLighting();
                        this.drawTabsForegroundLayer();
                        RenderHelper.enableGUIStandardItemLighting();

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

                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.disableLighting();

                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GuiSkyBlockData.drawEntityOnScreen(this.width / 2 - 96, this.height / 2 + 40, 40, this.player);

                        if (this.theSlot != null && this.theSlot.getHasStack())
                        {
                            this.renderToolTip(this.theSlot.getStack(), mouseX, mouseY);
                        }
                        if (SkyBlockcatiaMod.isSkyblockAddonsLoaded)
                        {
                            SkyBlockAddonsBackpack.INSTANCE.drawBackpacks(this, mouseX, mouseY, partialTicks);
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
                }
                else if (this.currentSlot instanceof SkyBlockCraftedMinions)
                {
                    String total1 = EnumChatFormatting.GRAY + "Unique Minions: " + EnumChatFormatting.YELLOW + this.craftedMinionCount + "/" + GuiSkyBlockData.MAXED_UNIQUE_MINIONS + EnumChatFormatting.GRAY + " (" + this.craftedMinionCount * 100 / GuiSkyBlockData.MAXED_UNIQUE_MINIONS + "%)";
                    String total2 = EnumChatFormatting.GRAY + "Current Minion Slot: " + EnumChatFormatting.YELLOW + this.currentMinionSlot;
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

        if (i != 0 && this.needsScrollBars())
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

            if (type.id == ViewButton.INFO.id)
            {
                if (this.currentBasicSlotId == -1 || this.currentBasicSlotId == BasicInfoViewButton.INFO.id)
                {
                    this.currentSlot = new InfoStats(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, this.infoList);
                }
                else if (this.currentBasicSlotId == BasicInfoViewButton.INVENTORY.id)
                {
                    this.currentSlot = new EmptyStats(this.mc, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, EmptyStats.Type.INVENTORY);
                    this.setCurrentTab(SkyBlockInventoryTabs.tabArray[this.selectedTabIndex]);
                }
                else if (this.currentBasicSlotId == BasicInfoViewButton.COLLECTIONS.id)
                {
                    this.currentSlot = new SkyBlockCollections(this, this.width - 119, this.height, 40, this.height - 50, 59, 20, this.width, this.height, this.collections);
                }
                else if (this.currentBasicSlotId == BasicInfoViewButton.CRAFTED_MINIONS.id)
                {
                    this.currentSlot = new SkyBlockCraftedMinions(this, this.width - 119, this.height, 40, this.height - 70, 59, 20, this.width, this.height, this.sbCraftedMinions);
                }

                this.currentSlotId = ViewButton.INFO.id;
                this.hideOthersButton();

                for (GuiButton viewButton : this.buttonList)
                {
                    BasicInfoViewButton type2 = BasicInfoViewButton.getTypeForButton(viewButton);

                    if (type2 != null)
                    {
                        viewButton.visible = true;

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
            }
            else if (type.id == ViewButton.SLAYERS.id)
            {
                this.currentSlot = new SlayerStats(this, this.width - 119, this.height, 40, this.height - 49, 59, 16, this.width, this.height, this.slayerInfo);
                this.currentSlotId = ViewButton.SLAYERS.id;
                this.hideOthersButton();
                this.hideBasicInfoButton();
            }
            else if (type.id == ViewButton.OTHERS.id)
            {
                SkyBlockStats.Type statType = SkyBlockStats.Type.KILLS;
                List<SkyBlockStats> list = this.sbKills;

                if (this.currentOthersSlotId == -1 || this.currentOthersSlotId == OthersViewButton.KILLS.id)
                {
                    statType = SkyBlockStats.Type.KILLS;
                    list = this.sbKills;
                }
                else if (this.currentOthersSlotId == OthersViewButton.DEATHS.id)
                {
                    statType = SkyBlockStats.Type.DEATHS;
                    list = this.sbDeaths;
                }
                else
                {
                    statType = SkyBlockStats.Type.OTHERS;
                    list = this.sbOthers;
                }

                this.currentSlot = new Others(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, list, statType);
                this.currentSlotId = ViewButton.OTHERS.id;
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
                    viewButton.enabled = this.othersButton != type2;
                }
            }

            SkyBlockStats.Type statType = SkyBlockStats.Type.KILLS;
            List<SkyBlockStats> list = null;

            if (type.id == OthersViewButton.KILLS.id)
            {
                statType = SkyBlockStats.Type.KILLS;
                this.currentOthersSlotId = OthersViewButton.KILLS.id;
                list = this.sbKills;
            }
            else if (type.id == OthersViewButton.DEATHS.id)
            {
                statType = SkyBlockStats.Type.DEATHS;
                this.currentOthersSlotId = OthersViewButton.DEATHS.id;
                list = this.sbDeaths;
            }
            else if (type.id == OthersViewButton.OTHER_STATS.id)
            {
                statType = SkyBlockStats.Type.OTHERS;
                this.currentOthersSlotId = OthersViewButton.OTHER_STATS.id;
                list = this.sbOthers;
            }

            if (list != null)
            {
                this.currentSlot = new Others(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, list, statType);
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

            if (type.id == BasicInfoViewButton.INFO.id)
            {
                this.currentSlot = new InfoStats(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, this.infoList);
                this.currentBasicSlotId = BasicInfoViewButton.INFO.id;
            }
            else if (type.id == BasicInfoViewButton.INVENTORY.id)
            {
                this.currentSlot = new EmptyStats(this.mc, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.width, this.height, EmptyStats.Type.INVENTORY);
                this.currentBasicSlotId = BasicInfoViewButton.INVENTORY.id;
                this.setCurrentTab(SkyBlockInventoryTabs.tabArray[this.selectedTabIndex]);
            }
            else if (type.id == BasicInfoViewButton.COLLECTIONS.id)
            {
                this.currentSlot = new SkyBlockCollections(this, this.width - 119, this.height, 40, this.height - 50, 59, 20, this.width, this.height, this.collections);
                this.currentBasicSlotId = BasicInfoViewButton.COLLECTIONS.id;
            }
            else if (type.id == BasicInfoViewButton.CRAFTED_MINIONS.id)
            {
                this.currentSlot = new SkyBlockCraftedMinions(this, this.width - 119, this.height, 40, this.height - 70, 59, 20, this.width, this.height, this.sbCraftedMinions);
                this.currentBasicSlotId = BasicInfoViewButton.CRAFTED_MINIONS.id;
            }
        }
    }

    private void setErrorMessage(String message)
    {
        this.error = true;
        this.loadingApi = false;
        this.errorMessage = message;
        this.updateErrorButton();
    }

    private void updateErrorButton()
    {
        this.backButton.visible = false;
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
            SkyBlockAddonsBackpack.INSTANCE.clearRenderBackpack();
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
        this.mc.getTextureManager().bindTexture(XP_BARS);
        GlStateManager.color(0.5F, 1.0F, 0.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(xBar, yBar, 0, 0, 91, 5, 91, 10);

        if (xpRequired > 0)
        {
            int filled = Math.min((int)Math.floor(playerXp * 92 / xpRequired), 91);

            if (filled > 0)
            {
                Gui.drawModalRectWithCustomSizedTexture(xBar, yBar, 0, 5, filled, 5, 91, 10);
            }

            this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GRAY + name + EnumChatFormatting.YELLOW + " " + currentLvl, xText, yText, 16777215);

            if (reachLimit)
            {
                this.drawCenteredString(this.fontRendererObj, NumberUtils.formatCompact((long)playerXp), xText, yText + 10, 16777215);
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

    private void drawContainerSlot(int mouseX, int mouseY)
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

        for (Slot slot : this.skyBlockContainer.inventorySlots)
        {
            this.drawSlot(slot);

            if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.canBeHovered())
            {
                this.theSlot = slot;

                if (SkyBlockcatiaMod.isSkyblockAddonsLoaded && SkyBlockAddonsBackpack.INSTANCE.isFreezeBackpack())
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

        for (Slot slot : this.skyBlockArmorContainer.inventorySlots)
        {
            if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.canBeHovered())
            {
                this.theSlot = slot;
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

        if (ExtendedConfig.instance.showItemRarity)
        {
            RenderUtils.drawRarity(slot.getStack(), slot.xDisplayPosition, slot.yDisplayPosition);
        }

        this.itemRender.renderItemAndEffectIntoGUI(itemStack, i, j);
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
        GlStateManager.disableLighting();
        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    // Player Data
    private void getPlayerData() throws IOException
    {
        this.statusMessage = "Getting Player Data";

        URL url = new URL(SkyBlockAPIUtils.SKYBLOCK_PROFILE + this.sbProfileId);
        JsonObject obj = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        JsonElement profile = obj.get("profile");

        if (profile == null || profile.isJsonNull())
        {
            this.setErrorMessage("No API data returned, please try again later!");
            return;
        }

        JsonObject profiles = profile.getAsJsonObject().get("members").getAsJsonObject();
        JsonElement banking = profile.getAsJsonObject().get("banking");

        for (Map.Entry<String, JsonElement> entry : profiles.entrySet())
        {
            String userUUID = entry.getKey();
            this.getCraftedMinions(profiles.get(userUUID).getAsJsonObject());
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
                this.createFakePlayer();
                this.calculatePlayerStats(currentUserProfile);
                this.getItemStats(this.inventoryToStats, false);
                this.getItemStats(this.armorItems, true);
                this.applyBonuses();

                this.allStat.add(new BonusStatTemplate(0, 0, 0, this.allStat.getDefense() <= 0 ? this.allStat.getHealth() : (int)(this.allStat.getHealth() * (1 + this.allStat.getDefense() / 100.0D)), 0, 0, 0, 0, 0, 0, 0, 0, 0));
                this.getBasicInfo(currentUserProfile, banking, objStatus, userUUID);
                break;
            }
        }

        if (!checkUUID.equals(this.uuid))
        {
            this.setErrorMessage("Current Player UUID not matched Profile UUID, please try again later!");
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
                for (IslandLocation location : IslandLocation.VALUES)
                {
                    if (mode.getAsString().equals(location.name().toLowerCase()))
                    {
                        locationText = location.getName();
                    }
                }
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
            minionLevels.add(new MinionLevel(minion.name(), minion.getAltName(), minion.getPetItem(), level, minion.getMinionCategory()));
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
                String[] split = unlockedTier.getAsString().toLowerCase().split("_");
                String unlockedId = split.length >= 3 ? split[0] + "_" + split[1] : split[0];
                int unlockedLvl = Integer.parseInt(split[split.length - 1]);

                for (SkyBlockCollection.ItemId sbItem : SkyBlockCollection.ItemId.VALUES)
                {
                    String sbItemId = sbItem.name().toLowerCase();

                    if (unlockedId.contains(sbItemId))
                    {
                        unlockedId = unlockedId.replace(sbItemId, sbItem.getMinecraftId());
                    }
                }
                skyblockCollectionMap.put(unlockedId, unlockedLvl);
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

            for (Map.Entry<String, JsonElement> collection : collections.getAsJsonObject().entrySet())
            {
                String collectionId = collection.getKey().toLowerCase();
                int collectionCount = collection.getValue().getAsInt();

                for (SkyBlockCollection.ItemId sbItem : SkyBlockCollection.ItemId.VALUES)
                {
                    String sbItemId = sbItem.name().toLowerCase();

                    if (collectionId.contains(sbItemId))
                    {
                        collectionId = collectionId.replace(sbItemId, sbItem.getMinecraftId());
                    }
                }

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
                SkyBlockCollection.Type type = SkyBlockCollection.Type.FARMING;
                SkyBlockCollection itemCollection = new SkyBlockCollection(new ItemStack(item, 0, meta), type, collectionCount, level);

                if (item == Item.getItemFromBlock(Blocks.cobblestone) || item == Items.coal || item == Items.iron_ingot || item == Items.gold_ingot || item == Items.diamond || item == Items.emerald || item == Items.redstone
                        || item == Items.quartz || item == Item.getItemFromBlock(Blocks.obsidian) || item == Items.glowstone_dust || item == Item.getItemFromBlock(Blocks.gravel) || item == Item.getItemFromBlock(Blocks.ice) || item == Item.getItemFromBlock(Blocks.netherrack)
                        || item == Item.getItemFromBlock(Blocks.sand) || item == Item.getItemFromBlock(Blocks.end_stone) || item == Items.dye && meta == 4)
                {
                    mining.add(itemCollection);
                    type = SkyBlockCollection.Type.MINING;
                }
                else if (item == Items.rotten_flesh || item == Items.bone || item == Items.string || item == Items.spider_eye || item == Items.gunpowder || item == Items.ender_pearl || item == Items.ghast_tear || item == Items.slime_ball || item == Items.blaze_rod || item == Items.magma_cream)
                {
                    combat.add(itemCollection);
                    type = SkyBlockCollection.Type.COMBAT;
                }
                else if (item == Item.getItemFromBlock(Blocks.log) || item == Item.getItemFromBlock(Blocks.log2))
                {
                    foraging.add(itemCollection);
                    type = SkyBlockCollection.Type.FORAGING;
                }
                else if (item == Items.fish || item == Items.prismarine_shard || item == Items.prismarine_crystals || item == Items.clay_ball || item == Item.getItemFromBlock(Blocks.waterlily) || item == Item.getItemFromBlock(Blocks.sponge) || item == Items.dye && meta == 0)
                {
                    fishing.add(itemCollection);
                    type = SkyBlockCollection.Type.FISHING;
                }
                else
                {
                    farming.add(itemCollection);
                }
            }

            Comparator<SkyBlockCollection> com = (sbColl1, sbColl2) -> new CompareToBuilder().append(sbColl1.getCollectionType().ordinal(), sbColl2.getCollectionType().ordinal()).append(sbColl2.getValue(), sbColl1.getValue()).build();
            farming.sort(com);
            mining.sort(com);
            combat.sort(com);
            foraging.sort(com);
            fishing.sort(com);

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
                    String sackId = sackEntry.getKey().toLowerCase();

                    for (SkyBlockCollection.ItemId sbItem : SkyBlockCollection.ItemId.VALUES)
                    {
                        String sbItemId = sbItem.name().toLowerCase();

                        if (sackId.contains(sbItemId))
                        {
                            sackId = sackId.replace(sbItemId, sbItem.getMinecraftId());
                        }
                    }

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
                            this.addSackItemStackCount(itemStack, count);
                            sacks.add(itemStack);
                        }
                        else
                        {
                            SlayerDrops slayerDrops = SlayerDrops.valueOf(itemId.toUpperCase());
                            ItemStack itemStack = new ItemStack(slayerDrops.getBaseItem(), count);
                            itemStack.setStackDisplayName(slayerDrops.getDisplayName());
                            itemStack.getTagCompound().setTag("ench", new NBTTagList());
                            this.addSackItemStackCount(itemStack, count);
                            sacks.add(itemStack);
                        }
                    }
                }
            }
            else
            {
                ItemStack barrier = new ItemStack(Blocks.barrier);
                barrier.setStackDisplayName(EnumChatFormatting.RESET + "" + EnumChatFormatting.RED + "Sacks is not available!");

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

    private void addSackItemStackCount(ItemStack itemStack, int count)
    {
        if (count >= 1000)
        {
            itemStack.setTagCompound(new NBTTagCompound());

            if (!itemStack.getTagCompound().hasKey("display", 10))
            {
                itemStack.getTagCompound().setTag("display", new NBTTagCompound());
            }
            NBTTagList itemCount = new NBTTagList();
            itemCount.appendTag(new NBTTagString(EnumChatFormatting.RESET + "Item Count: " + EnumChatFormatting.GRAY + FORMAT.format(count)));
            itemStack.getTagCompound().getCompoundTag("display").setTag("Lore", itemCount);
        }
    }

    private void getPets(JsonObject currentUserProfile)
    {
        List<PetData> petData = new ArrayList<>();
        JsonElement petsObj = currentUserProfile.get("pets");

        if (petsObj == null)
        {
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
                SkyBlockPets.HeldItem heldItem = null;

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
                if (element.getAsJsonObject().get("heldItem") != null && !element.getAsJsonObject().get("heldItem").isJsonNull())
                {
                    heldItem = SkyBlockPets.HeldItem.valueOf(element.getAsJsonObject().get("heldItem").getAsString());
                }

                SkyBlockPets.Tier tier = SkyBlockPets.Tier.valueOf(petRarity);
                boolean active = element.getAsJsonObject().get("active").getAsBoolean();
                String petType = element.getAsJsonObject().get("type").getAsString();
                NBTTagList list = new NBTTagList();

                if (heldItem != null && heldItem == SkyBlockPets.HeldItem.PET_ITEM_TIER_BOOST)
                {
                    tier = SkyBlockPets.Tier.values()[Math.min(SkyBlockPets.Tier.values().length - 1, tier.ordinal() + 1)];
                }

                PetLevel level = this.checkPetLevel(exp, tier);

                try
                {
                    EnumChatFormatting rarity = tier.getTierColor();
                    SkyBlockPets.Type type = SkyBlockPets.Type.valueOf(petType);
                    ItemStack itemStack = type.getPetItem();

                    itemStack.setStackDisplayName(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "[Lvl " + level.getCurrentPetLevel() + "] " + rarity + WordUtils.capitalize(petType.toLowerCase().replace("_", " ")));
                    list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + type.getSkillType().getName() + " Pet"));
                    list.appendTag(new NBTTagString(""));
                    list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + (active ? EnumChatFormatting.GREEN + "Active Pet" : EnumChatFormatting.RED + "Inactive Pet")));
                    list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + (level.getCurrentPetLevel() < 100 ? EnumChatFormatting.GRAY + "Next level is " + level.getNextPetLevel() + ": " + EnumChatFormatting.YELLOW + level.getPercent() : level.getPercent())));

                    if (level.getCurrentPetLevel() < 100)
                    {
                        list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "Current EXP: " + EnumChatFormatting.YELLOW + FORMAT.format(level.getCurrentPetXp()) + EnumChatFormatting.GOLD + "/" + EnumChatFormatting.YELLOW + NumberUtils.formatWithM(level.getXpRequired())));
                    }
                    if (candyUsed > 0)
                    {
                        list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "Candy Used: " + EnumChatFormatting.YELLOW + candyUsed + EnumChatFormatting.GOLD + "/" + EnumChatFormatting.YELLOW + 10));
                    }
                    if (heldItem != null)
                    {
                        String heldItemName = heldItem.getColor() + WordUtils.capitalize(heldItem.toString().toLowerCase().replace("pet_item_", "").replace("_", " "));

                        if (heldItem.getAltName() != null)
                        {
                            heldItemName = heldItem.getColor() + WordUtils.capitalize(heldItem.getAltName().toLowerCase().replace("pet_item_", "").replace("_", " "));
                        }
                        list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "Held Item: " + heldItemName));
                    }

                    list.appendTag(new NBTTagString(""));
                    list.appendTag(new NBTTagString(EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "Total XP: " + EnumChatFormatting.YELLOW + NumberUtils.formatCompact(level.getPetXp()) + EnumChatFormatting.GOLD + "/" + EnumChatFormatting.YELLOW + NumberUtils.formatWithM(level.getTotalPetTypeXp())));
                    list.appendTag(new NBTTagString(rarity + "" + EnumChatFormatting.BOLD + tier + " PET"));
                    itemStack.getTagCompound().getCompoundTag("display").setTag("Lore", list);
                    petData.add(new PetData(tier, level.getCurrentPetLevel(), active, Arrays.asList(itemStack)));

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
                    itemStack.setStackDisplayName(EnumChatFormatting.RESET + "" + EnumChatFormatting.RED + WordUtils.capitalize(petType.toLowerCase().replace("_", " ")));
                    list.appendTag(new NBTTagString(EnumChatFormatting.RED + "" + EnumChatFormatting.BOLD + "UNKNOWN PET"));
                    itemStack.getTagCompound().getCompoundTag("display").setTag("Lore", list);
                    petData.add(new PetData(SkyBlockPets.Tier.COMMON, 0, false, Arrays.asList(itemStack)));
                    LoggerIN.warning("Found an unknown pet! type: {}", petType);
                }
                petData.sort((o1, o2) -> new CompareToBuilder().append(o2.isActive(), o1.isActive()).append(o2.getTier().ordinal(), o1.getTier().ordinal()).append(o2.getCurrentLevel(), o1.getCurrentLevel()).build());
            }
            for (PetData data : petData)
            {
                SKYBLOCK_INV.add(new SkyBlockInventory(data.getItemStack(), SkyBlockInventoryTabs.PET));
            }
        }
        this.petScore = commonScore + uncommonScore + rareScore + epicScore + legendaryScore;
    }

    private PetLevel checkPetLevel(double petExp, SkyBlockPets.Tier tier)
    {
        ExpProgress[] progress = tier.getProgression();
        int totalPetTypeXp = 0;
        int xpRequired = 0;
        int currentLvl = 0;
        int levelToCheck = 0;
        int xpTotal = 0;
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

                if (levelToCheck <= progress.length)
                {
                    xpRequired = (int)progress[x].getXp();
                }
            }
        }

        if (levelToCheck < progress.length)
        {
            xpToNextLvl = MathHelper.ceiling_double_int(xpTotal - petExp);
            currentXp = xpRequired - xpToNextLvl;
        }
        else
        {
            currentLvl = progress.length + 1;
            xpRequired = 0;
        }
        return new PetLevel(currentLvl, levelToCheck, (int)currentXp, xpRequired, (int)petExp, totalPetTypeXp);
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
    }

    private void calculatePlayerStats(JsonObject currentProfile)
    {
        JsonElement fairySouls = currentProfile.get("fairy_souls_collected");

        if (fairySouls != null)
        {
            this.totalFairySouls = fairySouls.getAsInt();
        }

        this.allStat.add(this.getFairySouls(this.totalFairySouls));
        this.allStat.add(this.getMagicFindFromPets(this.petScore));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.FARMING, this.farmingLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.FORAGING, this.foragingLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.MINING, this.miningLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.FISHING, this.fishingLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.COMBAT, this.combatLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.ENCHANTING, this.enchantingLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.ALCHEMY, this.alchemyLevel));
        this.allStat.add(this.calculateSkillBonus(PlayerStatsBonus.TAMING, this.tamingLevel));
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
                }
            }
        }
        return new BonusStatTemplate(healthTemp, defenseTemp, trueDefenseTemp, 0, strengthTemp, speedTemp, critChanceTemp, critDamageTemp, attackSpeedTemp, intelligenceTemp, seaCreatureChanceTemp, magicFindTemp, petLuckTemp);
    }

    private void getHealthFromCake(NBTTagCompound extraAttrib)
    {
        List<ItemStack> itemStack1 = new ArrayList<>();

        try
        {
            NBTTagCompound compound1 = CompressedStreamTools.readCompressed(new ByteArrayInputStream(extraAttrib.getByteArray("new_year_cake_bag_data")));
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

                            if (!armor && (lastLore.endsWith(" BOOTS") || lastLore.endsWith(" LEGGINGS") || lastLore.endsWith(" CHESTPLATE") || lastLore.endsWith(" HELMET")))
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
                                    System.out.println(valueD);
                                    attackSpeedTemp += valueD;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        this.allStat.add(new BonusStatTemplate(healthTemp, defenseTemp, trueDefenseTemp, 0, strengthTemp, speedTemp, critChanceTemp, critDamageTemp, attackSpeedTemp, intelligenceTemp, seaCreatureChanceTemp, magicFindTemp, petLuckTemp));
    }

    private void getBasicInfo(JsonObject currentProfile, JsonElement banking, JsonObject objStatus, String uuid)
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
        String location = this.getLocation(objStatus, uuid);

        if (!StringUtils.isNullOrEmpty(location))
        {
            this.infoList.add(new SkyBlockInfo("\u23E3 Current Location", location));
        }

        this.infoList.add(new SkyBlockInfo(fairySoulsColor + "Fairy Souls Collected", fairySoulsColor + this.totalFairySouls + "/" + SkyBlockAPIUtils.MAX_FAIRY_SOULS));
        this.infoList.add(new SkyBlockInfo("", ""));
        this.infoList.add(new SkyBlockInfo(heath + "\u2764 Health", heath + SKILL_AVG.format(this.allStat.getHealth())));
        this.infoList.add(new SkyBlockInfo(heath + "\u2665 Effective Health", heath + SKILL_AVG.format(this.allStat.getEffectiveHealth())));
        this.infoList.add(new SkyBlockInfo(defense + "\u2748 Defense", defense + SKILL_AVG.format(this.allStat.getDefense())));
        this.infoList.add(new SkyBlockInfo(trueDefense + "\u2742 True Defense", trueDefense + SKILL_AVG.format(this.allStat.getTrueDefense())));
        this.infoList.add(new SkyBlockInfo(strength + "\u2741 Strength", strength + SKILL_AVG.format(this.allStat.getStrength())));
        this.infoList.add(new SkyBlockInfo(speed + "\u2726 Speed", speed + SKILL_AVG.format(this.allStat.getSpeed())));
        this.infoList.add(new SkyBlockInfo(critChance + "\u2623 Crit Chance", critChance + SKILL_AVG.format(this.allStat.getCritChance())));
        this.infoList.add(new SkyBlockInfo(critDamage + "\u2620 Crit Damage", critDamage + SKILL_AVG.format(this.allStat.getCritDamage())));
        this.infoList.add(new SkyBlockInfo(attackSpeed + "\u2694 Attack Speed", attackSpeed + SKILL_AVG.format(this.allStat.getAttackSpeed())));
        this.infoList.add(new SkyBlockInfo(intelligence + "\u270E Intelligence", intelligence + SKILL_AVG.format(this.allStat.getIntelligence())));
        this.infoList.add(new SkyBlockInfo(seaCreatureChance + "\u03B1 Sea Creature Chance", seaCreatureChance + SKILL_AVG.format(this.allStat.getSeaCreatureChance())));
        this.infoList.add(new SkyBlockInfo(magicFind + "\u272F Magic Find", magicFind + SKILL_AVG.format(this.allStat.getMagicFind())));
        this.infoList.add(new SkyBlockInfo(petLuck + "\u2663 Pet Luck", petLuck + SKILL_AVG.format(this.allStat.getPetLuck())));

        this.infoList.add(new SkyBlockInfo("", ""));

        Date firstJoinDate = new Date(firstJoinMillis);
        Date lastSaveDate = new Date(lastSaveMillis);
        SimpleDateFormat logoutDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        String lastLogout = logoutDate.format(lastSaveDate);
        SimpleDateFormat joinDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        joinDate.setTimeZone(this.uuid.equals("eef3a6031c1b4c988264d2f04b231ef4") ? TimeZone.getTimeZone("GMT") : TimeZone.getDefault());
        String firstJoinDateFormat = joinDate.format(firstJoinDate);

        this.infoList.add(new SkyBlockInfo("Joined", firstJoinMillis != -1 ? CommonUtils.getRelativeTime(firstJoinDate.getTime()) : EnumChatFormatting.RED + "No first join data!"));
        this.infoList.add(new SkyBlockInfo("Joined (Date)", firstJoinMillis != -1 ? firstJoinDateFormat : EnumChatFormatting.RED + "No first join data!"));
        this.infoList.add(new SkyBlockInfo("Last Updated", lastSaveMillis != -1 ? String.valueOf(lastSaveDate.getTime()) : EnumChatFormatting.RED + "No last save data!"));
        this.infoList.add(new SkyBlockInfo("Last Updated (Date)", lastSaveMillis != -1 ? lastLogout : EnumChatFormatting.RED + "No last save data!"));

        this.infoList.add(new SkyBlockInfo("Death Count", String.valueOf(deathCounts)));

        if (banking != null)
        {
            double balance = banking.getAsJsonObject().get("balance").getAsDouble();
            this.infoList.add(new SkyBlockInfo("Banking Account", FORMAT_2.format(balance)));
        }
        else
        {
            this.infoList.add(new SkyBlockInfo("Banking Account", EnumChatFormatting.RED + "API is not enabled!"));
        }
        this.infoList.add(new SkyBlockInfo("Purse", FORMAT_2.format(coins)));
    }

    private BonusStatTemplate getFairySouls(int fairySouls)
    {
        double healthBase = 0;
        double defenseBase = 0;
        double strengthBase = 0;
        double speedBase = 0;

        for (PlayerStatsBonus.FairySouls progress : PlayerStatsBonus.FAIRY_SOULS)
        {
            int soulToCheck = progress.getCount();
            double health = progress.getHealth();
            double defense = progress.getDefense();
            double strength = progress.getStrength();
            double speed = progress.getSpeed();

            if (soulToCheck <= fairySouls)
            {
                healthBase += health;
                defenseBase += defense;
                strengthBase += strength;
                speedBase += speed;
            }
        }
        return new BonusStatTemplate(healthBase, defenseBase, 0, 0, strengthBase, speedBase, 0, 0, 0, 0, 0, 0, 0);
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
        return new BonusStatTemplate(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, magicFindBase, 0);
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
            if (skill.getName().equals("Runecrafting") || skill.getName().equals("Carpentry"))
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
        int emperorKills = 0; // special case

        for (Map.Entry<String, JsonElement> stat : stats.entrySet())
        {
            String statName = stat.getKey();
            double value = stat.getValue().getAsDouble();

            if (statName.equals("highest_crit_damage"))
            {
                continue;
            }

            if (statName.startsWith("kills"))
            {
                if (statName.contains("sea_walker") || statName.contains("pond_squid") || statName.contains("night_squid") || statName.contains("frozen_steve") || statName.contains("grinch") || statName.contains("yeti") || statName.contains("frosty_the_snowman") || statName.contains("sea_guardian") || statName.contains("sea_archer") || statName.contains("sea_witch") || statName.contains("chicken_deep") || statName.contains("catfish")
                        || statName.contains("sea_leech") || statName.contains("deep_sea_protector") || statName.contains("water_hydra") || statName.contains("skeleton_emperor") || statName.contains("guardian_defender") || statName.contains("guardian_emperor") || statName.contains("carrot_king"))
                {
                    if (statName.contains("skeleton_emperor") || statName.contains("guardian_emperor"))
                    {
                        emperorKills += value;
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
                try
                {
                    SkyBlockOtherStats statsNew = SkyBlockOtherStats.valueOf(statName.toUpperCase());

                    if (statName.equals(statsNew.name().toLowerCase()))
                    {
                        statName = statsNew.getNewName();
                    }
                }
                catch (Exception e) {}

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
                else
                {
                    others.add(new SkyBlockStats(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
            }
        }

        if (emperorKills > 0)
        {
            seaCreatures.add(new SkyBlockStats("Sea Emperor kills", emperorKills)); // special case
        }

        this.sbDeaths.sort((stat1, stat2) -> new CompareToBuilder().append(stat2.getValue(), stat1.getValue()).build());
        auctions.sort((stat1, stat2) -> new CompareToBuilder().append(stat1.getName(), stat2.getName()).build());
        auctions.add(0, new SkyBlockStats(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.UNDERLINE + EnumChatFormatting.BOLD + "Auctions", 0.0F));

        this.sortStats(fished, "Fishing");
        this.sortStats(winter, "Winter Event");
        this.sortStats(petMilestone, "Pet Milestones");
        this.sortStats(others, "Others");

        this.sortStatsByValue(mobKills, "Mob Kills");
        this.sortStatsByValue(dragons, "Dragon Kills");
        this.sortStatsByValue(seaCreatures, "Sea Creature Kills");

        if (mobKills.size() > 2)
        {
            this.sbKills.addAll(mobKills);
        }
        if (dragons.size() > 2)
        {
            this.sbKills.addAll(dragons);
        }
        if (seaCreatures.size() > 2)
        {
            this.sbKills.addAll(seaCreatures);
        }

        if (auctions.size() > 2)
        {
            this.sbOthers.addAll(auctions);
        }
        if (fished.size() > 2)
        {
            this.sbOthers.addAll(fished);
        }
        if (winter.size() > 2)
        {
            this.sbOthers.addAll(winter);
        }
        if (petMilestone.size() > 2)
        {
            this.sbOthers.addAll(petMilestone);
        }
        if (others.size() > 2)
        {
            this.sbOthers.addAll(others);
        }

        this.data.setHasKills(this.sbKills.size() > 1);
        this.data.setHasDeaths(this.sbDeaths.size() > 1);
        this.data.setHasOthers(this.sbOthers.size() > 1);

        if (!this.data.hasKills() && !this.data.hasDeaths() && !this.data.hasOthers())
        {
            this.data.setHasOthersTab(false);
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
        this.armorItems.addAll(SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.ARMOR));

        for (int i = 0; i < 4; ++i)
        {
            GuiSkyBlockData.TEMP_ARMOR_INVENTORY.setInventorySlotContents(i, this.armorItems.get(i));
        }

        List<ItemStack> mainInventory = SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.INVENTORY);
        List<ItemStack> accessoryInventory = SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.ACCESSORY_BAG);

        SKYBLOCK_INV.add(new SkyBlockInventory(mainInventory, SkyBlockInventoryTabs.INVENTORY));
        SKYBLOCK_INV.add(new SkyBlockInventory(SkyBlockAPIUtils.decodeItem(currentProfile, SkyBlockInventoryType.ENDER_CHEST), SkyBlockInventoryTabs.ENDER_CHEST));
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

        this.player = new EntityOtherFakePlayer(this.mc.theWorld, this.profile);
        GuiSkyBlockData.renderSecondLayer = true;

        for (ItemStack armor : this.armorItems)
        {
            if (armor == null)
            {
                continue;
            }

            int index = EntityLiving.getArmorPosition(armor);

            if (armor.getItem() instanceof ItemBlock)
            {
                index = 4;
            }
            this.player.setCurrentItemOrArmor(index, armor);
        }
    }

    private List<SkyBlockSlayerInfo> getSlayer(JsonElement element, SlayerType type)
    {
        List<SkyBlockSlayerInfo> list = new ArrayList<>();
        ExpProgress[] progress = type.getProgress();
        JsonElement slayer = element.getAsJsonObject().get(type.name().toLowerCase());

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
                }

                this.setSlayerSkillLevel(type, slayerLvl);

                list.add(new SkyBlockSlayerInfo(EnumChatFormatting.GRAY + type.getName() + " Slayer: " + EnumChatFormatting.YELLOW + "LVL " + slayerLvl));
                list.add(new SkyBlockSlayerInfo(EnumChatFormatting.GRAY + "EXP: " + EnumChatFormatting.LIGHT_PURPLE + (xpToNextLvl == 0 ? FORMAT.format(playerSlayerXp) : FORMAT.format(playerSlayerXp) + EnumChatFormatting.DARK_PURPLE + "/" + EnumChatFormatting.LIGHT_PURPLE + FORMAT.format(xpRequired))));

                if (xpToNextLvl != 0)
                {
                    list.add(new SkyBlockSlayerInfo(EnumChatFormatting.GRAY + "XP to " + EnumChatFormatting.YELLOW + "LVL " + levelToCheck + ": " + EnumChatFormatting.LIGHT_PURPLE + FORMAT.format(xpToNextLvl)));
                }

                list.add(SkyBlockSlayerInfo.createMobAndXp(type.getName(), playerSlayerXp + "," + xpRequired + "," + xpToNextLvl));
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

    private static void drawEntityOnScreen(int posX, int posY, int scale, EntityLivingBase entity)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 50.0F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(ClientEventHandler.renderPartialTicks, 0.0F, 1.0F, 0.0F);
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
        this.mc.getRenderItem().renderItemIntoGUI(itemStack, x + 2, y + 2);
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
        private final boolean isActive;
        private final List<ItemStack> itemStack;

        public PetData(SkyBlockPets.Tier tier, int currentLevel, boolean isActive, List<ItemStack> itemStack)
        {
            this.tier = tier;
            this.currentLevel = currentLevel;
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

        public boolean isActive()
        {
            return this.isActive;
        }
    }

    private class PetLevel
    {
        private final int currentPetLevel;
        private final int nextPetLevel;
        private final int currentPetXp;
        private final int xpRequired;
        private final int petXp;
        private final int totalPetTypeXp;

        public PetLevel(int currentPetLevel, int nextPetLevel, int currentPetXp, int xpRequired, int petXp, int totalPetTypeXp)
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

        public int getCurrentPetXp()
        {
            return this.currentPetXp;
        }

        public int getXpRequired()
        {
            return this.xpRequired;
        }

        public int getPetXp()
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
                return new DecimalFormat("##.#").format(percent) + "%";
            }
            else
            {
                return EnumChatFormatting.AQUA + "MAX LEVEL";
            }
        }
    }

    static class ContainerArmor extends Container
    {
        public ContainerArmor()
        {
            this.addSlotToContainer(new Slot(GuiSkyBlockData.TEMP_ARMOR_INVENTORY, 0, -52, 75)); // boots
            this.addSlotToContainer(new Slot(GuiSkyBlockData.TEMP_ARMOR_INVENTORY, 1, -52, 56));
            this.addSlotToContainer(new Slot(GuiSkyBlockData.TEMP_ARMOR_INVENTORY, 2, -52, 36));
            this.addSlotToContainer(new Slot(GuiSkyBlockData.TEMP_ARMOR_INVENTORY, 3, -52, 12)); // helmet
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
        private Type type = Type.TEXT;

        public SkyBlockSlayerInfo(String text)
        {
            this.text = text;
        }

        public SkyBlockSlayerInfo(String text, String xp, Type type)
        {
            this(text);
            this.xp = xp;
            this.type = type;
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

        public static SkyBlockSlayerInfo createMobAndXp(String slayerType, String xp)
        {
            return new SkyBlockSlayerInfo(slayerType, xp, Type.XP_AND_MOB);
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

    private enum SkyBlockOtherStats
    {
        AUCTIONS_BOUGHT_COMMON("common_auctions_bought"),
        AUCTIONS_BOUGHT_EPIC("epic_auctions_bought"),
        AUCTIONS_BOUGHT_LEGENDARY("legendary_auctions_bought"),
        AUCTIONS_BOUGHT_RARE("rare_auctions_bought"),
        AUCTIONS_BOUGHT_SPECIAL("special_auctions_bought"),
        AUCTIONS_BOUGHT_UNCOMMON("uncommon_auctions_bought"),
        AUCTIONS_SOLD_COMMON("common_auctions_sold"),
        AUCTIONS_SOLD_EPIC("epic_auctions_sold"),
        AUCTIONS_SOLD_LEGENDARY("legendary_auctions_sold"),
        AUCTIONS_SOLD_RARE("rare_auctions_sold"),
        AUCTIONS_SOLD_SPECIAL("special_auctions_sold"),
        AUCTIONS_SOLD_UNCOMMON("uncommon_auctions_sold"),
        ITEMS_FISHED_LARGE_TREASURE("large_treasure_items_fished"),
        ITEMS_FISHED_NORMAL("normal_items_fished"),
        ITEMS_FISHED_TREASURE("treasure_items_fished"),
        SHREDDER_BAIT("bait_used_with_shredder");

        private final String newName;

        private SkyBlockOtherStats(String newName)
        {
            this.newName = newName;
        }

        public String getNewName()
        {
            return this.newName;
        }
    }

    private enum IslandLocation
    {
        DYNAMIC("Private Island"),
        HUB("Hub"),
        MINING_1("Gold Mine"),
        MINING_2("Deep Caverns"),
        COMBAT_1("Spider's Den"),
        COMBAT_2("Blazing Fortress"),
        COMBAT_3("The End"),
        FARMING_1("The Barn"),
        FARMING_2("Mushroom Desert"),
        FORAGING_1("The Park"),
        WINTER("Jerry's Workshop");

        private final String name;
        protected static final IslandLocation[] VALUES = IslandLocation.values();

        private IslandLocation(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
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
            INVENTORY, SKILL;
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
            this.parent.drawString(this.parent.mc.fontRendererObj, stat.getTitle(), this.parent.guiLeft - 20, top, index % 2 == 0 ? 16777215 : 9474192);
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
                    EntityZombie zombie = new EntityZombie(this.parent.mc.theWorld);
                    ItemStack heldItem = new ItemStack(Items.diamond_hoe);
                    ItemStack helmet = RenderUtils.getSkullItemStack(GuiSkyBlockData.REVENANT_HORROR_HEAD[0], GuiSkyBlockData.REVENANT_HORROR_HEAD[1]);
                    ItemStack chestplate = new ItemStack(Items.diamond_chestplate);
                    ItemStack leggings = new ItemStack(Items.chainmail_leggings);
                    ItemStack boots = new ItemStack(Items.diamond_boots);
                    zombie.setCurrentItemOrArmor(0, heldItem);
                    zombie.setCurrentItemOrArmor(1, boots);
                    zombie.setCurrentItemOrArmor(2, leggings);
                    zombie.setCurrentItemOrArmor(3, chestplate);
                    zombie.setCurrentItemOrArmor(4, helmet);
                    GuiSkyBlockData.drawEntityOnScreen(this.parent.guiLeft - 30, top + 60, 40, zombie);
                }
                else if (stat.getText().equals("Spider"))
                {
                    EntitySpider spider = new EntitySpider(this.parent.mc.theWorld);
                    EntityCaveSpider cave = new EntityCaveSpider(this.parent.mc.theWorld);
                    GuiSkyBlockData.drawEntityOnScreen(this.parent.guiLeft - 30, top + 40, 40, cave);
                    GuiSkyBlockData.drawEntityOnScreen(this.parent.guiLeft - 30, top + 60, 40, spider);
                    GlStateManager.blendFunc(770, 771);
                }
                else
                {
                    EntityWolf wolf = new EntityWolf(this.parent.mc.theWorld);
                    wolf.setAngry(true);
                    GuiSkyBlockData.drawEntityOnScreen(this.parent.guiLeft - 30, top + 60, 40, wolf);
                }

                this.parent.mc.getTextureManager().bindTexture(XP_BARS);
                GlStateManager.color(0.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableBlend();

                String[] xpSplit = stat.getXp().split(",");
                int playerSlayerXp = Integer.valueOf(xpSplit[0]);
                int xpRequired = Integer.valueOf(xpSplit[1]);

                int filled = Math.min((int)Math.floor(playerSlayerXp * 92 / xpRequired), 91);
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
        private final List<SkyBlockStats> stats;
        private final GuiSkyBlockData parent;

        public Others(GuiSkyBlockData parent, int width, int height, int top, int bottom, int left, int entryHeight, int parentWidth, int parentHeight, List<SkyBlockStats> stats, SkyBlockStats.Type type)
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
                SkyBlockStats stat = this.stats.get(index);
                this.parent.drawString(this.parent.mc.fontRendererObj, StringUtils.isNullOrEmpty(stat.getName()) ? "" : stat.getName(), this.parent.guiLeft - 85, top, index % 2 == 0 ? 16777215 : 9474192);
                this.parent.drawString(this.parent.mc.fontRendererObj, stat.getValueByString(), this.parent.guiLeft - this.parent.mc.fontRendererObj.getStringWidth(stat.getValueByString()) + 180, top, index % 2 == 0 ? 16777215 : 9474192);
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

            if (collection.getItemStack() != null && collection.getCollectionType() != null)
            {
                this.parent.drawItemStackSlot(this.parent.guiLeft - 65, top, collection.getItemStack());
                this.parent.drawString(this.parent.mc.fontRendererObj, collection.getItemStack().getDisplayName() + " " + EnumChatFormatting.GOLD + collection.getLevel(), this.parent.guiLeft - 41, top + 6, 16777215);
                this.parent.drawString(this.parent.mc.fontRendererObj, collection.getCollectionAmount(), this.parent.guiLeft - this.parent.mc.fontRendererObj.getStringWidth(collection.getCollectionAmount()) + 170, top + 6, index % 2 == 0 ? 16777215 : 9474192);
            }
            else
            {
                if (collection.getCollectionType() != null)
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
                String name = craftedMinion.getDisplayName() != null ? WordUtils.capitalize(craftedMinion.getDisplayName().toLowerCase().replace("_", " ")) : WordUtils.capitalize(craftedMinion.getMinionName().toLowerCase().replace("_", " "));
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

        public BonusStatTemplate(double health, double defense, double trueDefense, double effectiveHealth, double strength, double speed, double critChance, double critDamage, double attackSpeed, double intelligence, double seaCreatureChance, double magicFind, double petLuck)
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
            return new BonusStatTemplate(this.health, this.defense, this.trueDefense, this.effectiveHealth, this.strength, this.speed, this.critChance, this.critDamage, this.attackSpeed, this.intelligence, this.seaCreatureChance, this.magicFind, this.petLuck);
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
        TARANTULA_WEB(EnumChatFormatting.RESET + "" + EnumChatFormatting.GREEN + "Tarantula Web", Items.string),
        REVENANT_FLESH(EnumChatFormatting.RESET + "" + EnumChatFormatting.GREEN + "Revenant Flesh", Items.rotten_flesh),
        WOLF_TOOTH(EnumChatFormatting.RESET + "" + EnumChatFormatting.GREEN + "Wolf Tooth", Items.ghast_tear);

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

    private enum ViewButton
    {
        INFO(10),
        SKILLS(11),
        SLAYERS(12),
        OTHERS(13);

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
        OTHER_STATS(22);

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
        INFO(30),
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