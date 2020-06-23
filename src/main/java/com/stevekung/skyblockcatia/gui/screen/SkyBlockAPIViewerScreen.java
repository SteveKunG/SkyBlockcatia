package com.stevekung.skyblockcatia.gui.screen;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.text.WordUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.gui.ScrollingListScreen;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.IViewerLoader;
import com.stevekung.skyblockcatia.utils.TimeUtils;
import com.stevekung.skyblockcatia.utils.skyblock.*;
import com.stevekung.skyblockcatia.utils.skyblock.api.*;
import com.stevekung.stevekungslib.client.event.ClientEventHandler;
import com.stevekung.stevekungslib.utils.*;
import com.stevekung.stevekungslib.utils.client.ClientUtils;
import com.stevekung.stevekungslib.utils.client.RenderUtils;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.fixes.EntityRenaming1510;
import net.minecraft.util.datafix.fixes.ItemStackDataFlattening;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.GameType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

public class SkyBlockAPIViewerScreen extends Screen
{
    private static final ResourceLocation INVENTORY_TABS = new ResourceLocation("skyblockcatia:textures/gui/groups.png");
    private static final ResourceLocation XP_BARS = new ResourceLocation("skyblockcatia:textures/gui/skill_xp_bar.png");
    private static final String[] REVENANT_HORROR_HEAD = new String[] {"0862e0b0-a14f-3f93-894f-013502936b59", "eyJ0aW1lc3RhbXAiOjE1Njg0NTc0MjAxMzcsInByb2ZpbGVJZCI6IjQxZDNhYmMyZDc0OTQwMGM5MDkwZDU0MzRkMDM4MzFiIiwicHJvZmlsZU5hbWUiOiJNZWdha2xvb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RiYWQ5OWVkM2M4MjBiNzk3ODE5MGFkMDhhOTM0YTY4ZGZhOTBkOTk4NjgyNWRhMWM5N2Y2ZjIxZjQ5YWQ2MjYifX19"};

    // Based stuff
    private boolean firstLoad;
    private boolean loadingApi = true;
    private boolean error = false;
    private String errorMessage;
    private String statusMessage;
    private Button doneButton;
    private Button backButton;
    private final List<ProfileDataCallback> profiles;
    private final String sbProfileId;
    private final String sbProfileName;
    private final String username;
    private final String displayName;
    private final String guild;
    private final String uuid;
    private final GameProfile profile;
    private final StopWatch watch = new StopWatch();
    private ScrollingListScreen currentSlot;
    private ViewButton viewButton = ViewButton.INFO;
    private OthersViewButton othersButton = OthersViewButton.KILLS;
    private BasicInfoViewButton basicInfoButton = BasicInfoViewButton.INFO;
    private boolean updated;
    private final ViewerData data = new ViewerData();
    private int skillCount;

    // API
    private static final int MAXED_UNIQUE_MINIONS = 572;
    private static final Pattern STATS_PATTERN = Pattern.compile("(?<type>Strength|Crit Chance|Crit Damage|Health|Defense|Speed|Intelligence|True Defense|Sea Creature Chance|Magic Find|Pet Luck): (?<value>(?:\\+|\\-)[0-9,.]+)?(?:\\%){0,1}(?:(?: HP(?: \\(\\+[0-9,.]+ HP\\)){0,1}(?: \\(\\w+ \\+[0-9,.]+ HP\\)){0,1})|(?: \\(\\+[0-9,.]+\\))|(?: \\(\\w+ \\+[0-9,.]+(?:\\%){0,1}\\))){0,1}");
    public static boolean renderSecondLayer;
    private final List<SkyBlockInfo> infoList = new ArrayList<>();
    private final List<SBSkills.Info> skillLeftList = new ArrayList<>();
    private final List<SBSkills.Info> skillRightList = new ArrayList<>();
    private final List<SkyBlockSlayerInfo> slayerInfo = new ArrayList<>();
    private final List<SBStats> sbKills = new ArrayList<>();
    private final List<SBStats> sbDeaths = new ArrayList<>();
    private final List<SBStats> sbOthers = new ArrayList<>();
    private final List<SBMinions.CraftedInfo> sbCraftedMinions = new ArrayList<>();
    private final List<ItemStack> armorItems = new ArrayList<>();
    private final List<ItemStack> inventoryToStats = new ArrayList<>();
    private final List<SBCollections> collections = new ArrayList<>();
    private final Multimap<String, Integer> craftedMinions = HashMultimap.create();
    private int craftedMinionCount;
    private int currentMinionSlot;
    private int slayerTotalAmountSpent;
    private int totalSlayerXp;
    private SBFakePlayerEntity player;
    private String skillAvg;
    private int petScore;

    // Info & Inventory
    private static final int SIZE = 36;
    private static final ExtendedInventory TEMP_INVENTORY = new ExtendedInventory(SkyBlockAPIViewerScreen.SIZE);
    private static final ExtendedInventory TEMP_ARMOR_INVENTORY = new ExtendedInventory(4);
    public static final List<SkyBlockInventory> SKYBLOCK_INV = new ArrayList<>();
    private int selectedTabIndex = SBInventoryGroup.INVENTORY.getIndex();
    private float currentScroll;
    private boolean isScrolling;
    private final SkyBlockContainer skyBlockContainer;
    private final ArmorContainer skyBlockArmorContainer;

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
    private BonusStatTemplate allStat = new BonusStatTemplate(100, 0, 0, 0, 0, 100, 20, 50, 100, 20, 10, 0);

    // GuiContainer fields
    private int xSize;
    private int ySize;
    private int guiLeft;
    private int guiTop;
    private Slot hoveredSlot;

    public SkyBlockAPIViewerScreen(List<ProfileDataCallback> profiles, ProfileDataCallback callback)
    {
        super(JsonUtils.create("SkyBlock Player Data"));
        this.firstLoad = true;
        this.skyBlockContainer = new SkyBlockContainer();
        this.skyBlockArmorContainer = new ArmorContainer();
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
    }

    @Override
    public void init()
    {
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
                    SkyBlockcatiaMod.LOGGER.info("API Download finished in: {}ms", this.watch.getTime());
                    this.watch.reset();
                }
                catch (Throwable e)
                {
                    this.setErrorMessage(e.getStackTrace()[0].toString());
                    e.printStackTrace();
                }
            });
        }

        this.addButton(this.doneButton = new Button(this.width / 2 - 154, this.height - 25, 150, 20, LangUtils.translate("gui.close"), button -> this.minecraft.displayGuiScreen(this.error ? new SkyBlockProfileViewerScreen(SkyBlockProfileViewerScreen.GuiState.SEARCH, this.username, this.displayName, this.guild, this.profiles) : null)));
        this.addButton(this.backButton = new Button(this.width / 2 + 4, this.height - 25, 150, 20, LangUtils.translate("gui.back"), button -> this.minecraft.displayGuiScreen(this.profiles.size() == 0 ? new SkyBlockProfileViewerScreen(SkyBlockProfileViewerScreen.GuiState.EMPTY, this.username, this.displayName, this.guild) : new SkyBlockProfileViewerScreen(SkyBlockProfileViewerScreen.GuiState.SEARCH, this.username, this.displayName, this.guild, this.profiles))));
        Button infoButton = ViewButton.INFO.button = new Button(this.width / 2 - 185, 6, 80, 20, LangUtils.translate("gui.sb_view_info"), button -> this.performedInfo(ViewButton.INFO));
        infoButton.active = false;
        this.addButton(infoButton);
        this.addButton(ViewButton.SKILLS.button = new Button(this.width / 2 - 88, 6, 80, 20, LangUtils.translate("gui.sb_view_skills"), button -> this.performedInfo(ViewButton.SKILLS)));
        this.addButton(ViewButton.SLAYERS.button = new Button(this.width / 2 + 8, 6, 80, 20, LangUtils.translate("gui.sb_view_slayers"), button -> this.performedInfo(ViewButton.SLAYERS)));
        this.addButton(ViewButton.OTHERS.button = new Button(this.width / 2 + 104, 6, 80, 20, LangUtils.translate("gui.sb_view_others"), button -> this.performedInfo(ViewButton.OTHERS)));

        Button statKillsButton = OthersViewButton.KILLS.button = new Button(this.width / 2 - 124, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.kills"), button -> this.performedOthers(OthersViewButton.KILLS));
        statKillsButton.active = false;
        this.addButton(statKillsButton);
        this.addButton(OthersViewButton.DEATHS.button = new Button(this.width / 2 - 40, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.deaths"), button -> this.performedOthers(OthersViewButton.DEATHS)));
        this.addButton(OthersViewButton.OTHER_STATS.button = new Button(this.width / 2 + 44, this.height - 48, 80, 20, LangUtils.translate("gui.sb_others.others_stats"), button -> this.performedOthers(OthersViewButton.OTHER_STATS)));

        Button basicInfoButton = BasicInfoViewButton.INFO.button = new Button(this.width / 2 - 170, this.height - 48, 80, 20, LangUtils.translate("gui.sb_basic_info"), button -> this.performedBasicInfo(BasicInfoViewButton.INFO));
        basicInfoButton.active = false;
        this.addButton(basicInfoButton);
        this.addButton(BasicInfoViewButton.INVENTORY.button = new Button(this.width / 2 - 84, this.height - 48, 80, 20, LangUtils.translate("gui.sb_inventory"), button -> this.performedBasicInfo(BasicInfoViewButton.INVENTORY)));
        this.addButton(BasicInfoViewButton.COLLECTIONS.button = new Button(this.width / 2 + 4, this.height - 48, 80, 20, LangUtils.translate("gui.sb_collections"), button -> this.performedBasicInfo(BasicInfoViewButton.COLLECTIONS)));
        this.addButton(BasicInfoViewButton.CRAFTED_MINIONS.button = new Button(this.width / 2 + 90, this.height - 48, 80, 20, LangUtils.translate("gui.sb_crafted_minions"), button -> this.performedBasicInfo(BasicInfoViewButton.CRAFTED_MINIONS)));

        if (!this.updated)
        {
            this.performedInfo(this.viewButton);
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
        TEMP_INVENTORY.clear();
        TEMP_ARMOR_INVENTORY.clear();
        SKYBLOCK_INV.clear();
        this.minecraft.getConnection().getPlayerInfoMap().removeIf(network -> ((IViewerLoader)network).isLoadedFromViewer());
        SkyBlockAPIViewerScreen.renderSecondLayer = false;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers)
    {
        if (key == KeyBindingHandler.KEY_SB_VIEW_RECIPE.getKey().getKeyCode())
        {
            if (this.hoveredSlot != null && this.hoveredSlot.getHasStack() && this.hoveredSlot.getStack().hasTag())
            {
                CompoundNBT extraAttrib = this.hoveredSlot.getStack().getTag().getCompound("ExtraAttributes");

                if (extraAttrib.contains("id"))
                {
                    String itemId = extraAttrib.getString("id");

                    /*if (SkyBlockcatiaMod.isSkyblockAddonsLoaded && SkyBlockAddonsBackpack.INSTANCE.isFreezeBackpack()) TODO
                    {
                        return false;
                    }*/
                    ClientUtils.printClientMessage(JsonUtils.create("Click to view ").appendSibling(this.hoveredSlot.getStack().getDisplayName().applyTextStyle(TextFormatting.GOLD).appendSibling(JsonUtils.create(" recipe").applyTextStyle(TextFormatting.GREEN))).setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewrecipe " + itemId)).setColor(TextFormatting.GREEN)));
                }
            }
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
        this.minecraft.displayGuiScreen(new SkyBlockProfileViewerScreen(SkyBlockProfileViewerScreen.GuiState.SEARCH, this.username, this.displayName, this.guild, this.profiles));
    }

    @Override
    public IGuiEventListener getFocused()
    {
        if (this.currentSlot != null)
        {
            return this.currentSlot;
        }
        return super.getFocused();
    }

    @Override
    public Optional<IGuiEventListener> getEventListenerForPos(double mouseX, double mouseY)
    {
        if (this.currentSlot != null && this.currentSlot.isMouseOver(mouseX, mouseY))
        {
            return Optional.of(this.currentSlot);
        }
        return super.getEventListenerForPos(mouseX, mouseY);
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
            if (state == 0 && this.currentSlot != null && this.currentSlot instanceof EmptyStats && ((EmptyStats)this.currentSlot).getType() == EmptyStats.Type.INVENTORY)
            {
                double i = mouseX - this.guiLeft;
                double j = mouseY - this.guiTop;

                for (SBInventoryGroup group : SBInventoryGroup.GROUPS)
                {
                    if (group != null && this.isMouseOverGroup(group, i, j))
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
            if (state == 0 && this.currentSlot != null && this.currentSlot instanceof EmptyStats && ((EmptyStats)this.currentSlot).getType() == EmptyStats.Type.INVENTORY)
            {
                double i = mouseX - this.guiLeft;
                double j = mouseY - this.guiTop;
                this.isScrolling = false;

                for (SBInventoryGroup group : SBInventoryGroup.GROUPS)
                {
                    if (group != null && this.isMouseOverGroup(group, i, j))
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
            if (this.isScrolling && this.currentSlot != null && this.currentSlot instanceof EmptyStats && ((EmptyStats)this.currentSlot).getType() == EmptyStats.Type.INVENTORY)
            {
                int i = this.guiTop + 18;
                int j = i + 72;
                this.currentScroll = ((float)mouseY - i - 7.5F) / (j - i - 15.0F);
                this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
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
                if (this.currentSlot instanceof EmptyStats && ((EmptyStats)this.currentSlot).getType() == EmptyStats.Type.INVENTORY)
                {
                    if (!this.needsScrollBars())
                    {
                        return false;
                    }
                    else
                    {
                        int i = (this.skyBlockContainer.itemList.size() + 9 - 1) / 9 - 4;
                        this.currentScroll = (float)(this.currentScroll - scrollDelta / i);
                        this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
                        this.skyBlockContainer.scrollTo(this.currentScroll);
                        return true;
                    }
                }
                this.currentSlot.mouseScrolled(mouseX, mouseY, scrollDelta);
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollDelta);
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
            this.drawString(this.font, SkyBlockProfileViewerScreen.downloadingStates[(int)(Util.milliTime() / 500L % SkyBlockProfileViewerScreen.downloadingStates.length)], this.width / 2 + i / 2, this.height / 2 + this.font.FONT_HEIGHT * 2 - 35, 16777215);
            this.drawCenteredString(this.font, "Status: " + TextFormatting.GRAY + this.statusMessage, this.width / 2, this.height / 2 + this.font.FONT_HEIGHT * 2 - 15, 16777215);
        }
        else
        {
            if (this.error)
            {
                this.drawCenteredString(this.font, "SkyBlock API Viewer", this.width / 2, 20, 16777215);
                this.drawCenteredString(this.font, TextFormatting.RED + this.errorMessage, this.width / 2, 100, 16777215);
                super.render(mouseX, mouseY, partialTicks);
            }
            else
            {
                if (this.currentSlot != null)
                {
                    this.currentSlot.render(mouseX, mouseY, partialTicks);
                }

                this.drawCenteredString(this.font, this.displayName + TextFormatting.GOLD + " Profile: " + this.sbProfileName + this.guild, this.width / 2, 29, 16777215);

                if (this.currentSlot != null && this.currentSlot instanceof EmptyStats)
                {
                    EmptyStats stat = (EmptyStats)this.currentSlot;

                    if (stat.getType() == EmptyStats.Type.INVENTORY)
                    {
                        this.drawGroupsBackgroundLayer(partialTicks, mouseX, mouseY);
                    }
                }

                super.render(mouseX, mouseY, partialTicks);

                if (this.currentSlot != null)
                {
                    if (this.currentSlot instanceof InfoStats)
                    {
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                        RenderSystem.enableDepthTest();
                        SkyBlockAPIViewerScreen.renderEntity(this.width / 2 - 106, this.height / 2 + 40, 40, this.player);
                    }
                    else if (this.currentSlot instanceof EmptyStats)
                    {
                        EmptyStats stat = (EmptyStats)this.currentSlot;

                        if (stat.getType() == EmptyStats.Type.INVENTORY)
                        {
                            this.drawContainerSlot(mouseX, mouseY);

                            RenderHelper.disableStandardItemLighting();
                            this.drawTabsForegroundLayer();

                            for (SBInventoryGroup group : SBInventoryGroup.GROUPS)
                            {
                                if (this.renderGroupsHoveringText(group, mouseX, mouseY))
                                {
                                    break;
                                }
                            }

                            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                            RenderSystem.disableLighting();

                            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                            SkyBlockAPIViewerScreen.renderEntity(this.width / 2 - 96, this.height / 2 + 40, 40, this.player);

                            if (this.hoveredSlot != null && this.hoveredSlot.getHasStack())
                            {
                                this.renderTooltip(this.hoveredSlot.getStack(), mouseX, mouseY);
                            }
                            /*if (SkyBlockcatiaMod.isSkyblockAddonsLoaded)TODO
                            {
                                SkyBlockAddonsBackpack.INSTANCE.drawBackpacks(this, mouseX, mouseY, partialTicks);
                            }*/
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
                                this.renderSkillBar(info.getName(), x, barY, x + 46, textY, info.getCurrentXp(), info.getXpRequired(), info.getCurrentLvl(), info.isReachLimit());
                                ++i;
                            }

                            i = 0;

                            for (SBSkills.Info info : this.skillRightList)
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
                                this.drawString(ClientUtils.unicodeFontRenderer, avg, this.width - ClientUtils.unicodeFontRenderer.getStringWidth(avg) - 60, this.height - 38, 16777215);
                            }
                        }
                    }
                    else if (this.currentSlot instanceof SlayerStats)
                    {
                        String total1 = TextFormatting.GRAY + "Total Amount Spent: " + TextFormatting.YELLOW + NumberUtils.NUMBER_FORMAT.format(this.slayerTotalAmountSpent);
                        String total2 = TextFormatting.GRAY + "Total Slayer XP: " + TextFormatting.YELLOW + NumberUtils.NUMBER_FORMAT.format(this.totalSlayerXp);
                        this.drawString(this.font, total1, this.width - this.font.getStringWidth(total1) - 60, this.height - 36, 16777215);
                        this.drawString(this.font, total2, this.width - this.font.getStringWidth(total2) - 60, this.height - 46, 16777215);
                    }
                    else if (this.currentSlot instanceof SkyBlockCraftedMinions)
                    {
                        String total1 = TextFormatting.GRAY + "Unique Minions: " + TextFormatting.YELLOW + this.craftedMinionCount + "/" + SkyBlockAPIViewerScreen.MAXED_UNIQUE_MINIONS + TextFormatting.GRAY + " (" + this.craftedMinionCount * 100 / SkyBlockAPIViewerScreen.MAXED_UNIQUE_MINIONS + "%)";
                        String total2 = TextFormatting.GRAY + "Current Minion Slot: " + TextFormatting.YELLOW + this.currentMinionSlot;
                        this.drawString(this.font, total1, this.width - this.font.getStringWidth(total1) - 60, this.height - 68, 16777215);
                        this.drawString(this.font, total2, this.width - this.font.getStringWidth(total2) - 60, this.height - 58, 16777215);
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
        case INFO:
        default:
            this.currentSlot = new InfoStats(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.infoList);
            this.refreshBasicInfoViewButton(this.basicInfoButton, true);
            this.refreshOthersViewButton(this.othersButton, false);
            this.performedBasicInfo(this.basicInfoButton);
            break;
        case SKILLS:
            this.currentSlot = new EmptyStats(this, this.width - 119, this.height, 40, this.height - 28, 59, 12, EmptyStats.Type.SKILL);
            this.hideBasicInfoButton();
            this.hideOthersButton();
            break;
        case SLAYERS:
            this.currentSlot = new SlayerStats(this, this.width - 119, this.height, 40, this.height - 49, 59, 16, this.slayerInfo);
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
        case INFO:
        default:
            this.currentSlot = new InfoStats(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, this.infoList);
            break;
        case INVENTORY:
            this.currentSlot = new EmptyStats(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, EmptyStats.Type.INVENTORY);
            this.setCurrentGroup(SBInventoryGroup.GROUPS[this.selectedTabIndex]);
            break;
        case COLLECTIONS:
            this.currentSlot = new SkyBlockCollections(this, this.width - 119, this.height, 40, this.height - 50, 59, 20, this.collections);
            break;
        case CRAFTED_MINIONS:
            this.currentSlot = new SkyBlockCraftedMinions(this, this.width - 119, this.height, 40, this.height - 70, 59, 20, this.sbCraftedMinions);
            break;
        }
        this.refreshBasicInfoViewButton(basicInfoButton, true);
    }

    private void performedOthers(OthersViewButton othersButton)
    {
        SBStats.Type statType = SBStats.Type.KILLS;
        List<SBStats> list = null;

        switch (othersButton)
        {
        default:
        case KILLS:
            statType = SBStats.Type.KILLS;
            list = this.sbKills;
            break;
        case DEATHS:
            statType = SBStats.Type.DEATHS;
            list = this.sbDeaths;
            break;
        case OTHER_STATS:
            statType = SBStats.Type.OTHERS;
            list = this.sbOthers;
            break;
        }
        if (list != null)
        {
            this.currentSlot = new Others(this, this.width - 119, this.height, 40, this.height - 50, 59, 12, list, statType);
        }
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

    private void refreshBasicInfoViewButton(BasicInfoViewButton basicInfoButton, boolean visible)
    {
        this.basicInfoButton = basicInfoButton;

        for (BasicInfoViewButton view : BasicInfoViewButton.VALUES)
        {
            if (view.button != null)
            {
                view.button.active = this.basicInfoButton != view;

                if (view.button == BasicInfoViewButton.COLLECTIONS.button)
                {
                    if (!this.data.hasCollections())
                    {
                        view.button.active = false;
                        view.button.visible = visible;
                        continue;
                    }
                }
                if (view.button == BasicInfoViewButton.CRAFTED_MINIONS.button)
                {
                    if (!this.data.hasMinions())
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
        this.doneButton.x = this.width / 2 - 75;
        this.doneButton.y = this.height / 4 + 132;
        this.doneButton.setMessage(LangUtils.translate("gui.back"));

        for (Widget button : this.buttons)
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
        return this.isPointInRegion(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY);
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
        /*if (SkyBlockcatiaMod.isSkyblockAddonsLoaded)TODO
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
        return SBInventoryGroup.GROUPS[this.selectedTabIndex].hasScrollbar() && this.skyBlockContainer.canScroll();
    }

    // Render
    private void renderSkillBar(String name, int xBar, int yBar, int xText, int yText, double playerXp, int xpRequired, int currentLvl, boolean reachLimit)
    {
        this.minecraft.getTextureManager().bindTexture(XP_BARS);
        RenderSystem.color4f(0.5F, 1.0F, 0.0F, 1.0F);
        AbstractGui.blit(xBar, yBar, 0, 0, 91, 5, 91, 10);

        if (xpRequired > 0)
        {
            int filled = Math.min((int)Math.floor(playerXp * 92 / xpRequired), 91);

            if (filled > 0)
            {
                AbstractGui.blit(xBar, yBar, 0, 5, filled, 5, 91, 10);
            }

            this.drawCenteredString(this.font, TextFormatting.GRAY + name + TextFormatting.YELLOW + " " + currentLvl, xText, yText, 16777215);

            if (reachLimit)
            {
                this.drawCenteredString(this.font, NumberUtils.format((long)playerXp), xText, yText + 10, 16777215);
            }
            else
            {
                this.drawCenteredString(this.font, NumberUtils.format((long)playerXp) + "/" + NumberUtils.format(xpRequired), xText, yText + 10, 16777215);
            }
        }
        else
        {
            this.drawCenteredString(this.font, name, xText, yText + 8, 16777215);
        }
    }

    private void drawContainerSlot(int mouseX, int mouseY)
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

        for (Slot slot : this.skyBlockContainer.inventorySlots)
        {
            this.drawSlot(slot);

            if (this.isSlotSelected(slot, mouseX, mouseY) && slot.isEnabled())
            {
                this.hoveredSlot = slot;

                /*if (SkyBlockcatiaMod.isSkyblockAddonsLoaded && SkyBlockAddonsBackpack.INSTANCE.isFreezeBackpack())
                {
                    continue;TODO
                }*/
                RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                int j1 = slot.xPos;
                int k1 = slot.yPos;
                RenderSystem.colorMask(true, true, true, false);
                this.fillGradient(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableLighting();
                RenderSystem.enableDepthTest();
            }
        }

        for (Slot slot : this.skyBlockArmorContainer.inventorySlots)
        {
            if (this.isSlotSelected(slot, mouseX, mouseY) && slot.isEnabled())
            {
                this.hoveredSlot = slot;
            }
        }
        RenderSystem.popMatrix();
    }

    private void drawSlot(Slot slot)
    {
        int i = slot.xPos;
        int j = slot.yPos;
        ItemStack itemStack = slot.getStack();
        this.setBlitOffset(100);
        this.itemRenderer.zLevel = 100.0F;

        if (itemStack.isEmpty())
        {
            Pair<ResourceLocation, ResourceLocation> pair = slot.getBackground();

            if (pair != null)
            {
                TextureAtlasSprite sprite = this.minecraft.getAtlasSpriteGetter(pair.getFirst()).apply(pair.getSecond());
                this.minecraft.getTextureManager().bindTexture(sprite.getAtlasTexture().getTextureLocation());
                blit(i, j, this.getBlitOffset(), 16, 16, sprite);
            }
        }

        RenderSystem.enableDepthTest();

        if (SBExtendedConfig.INSTANCE.showItemRarity)
        {
            SBRenderUtils.renderRarity(slot.getStack(), slot.xPos, slot.yPos);
        }

        this.itemRenderer.renderItemAndEffectIntoGUI(itemStack, i, j);
        this.renderItemOverlayIntoGUI(itemStack, i, j);
        this.itemRenderer.zLevel = 0.0F;
        this.setBlitOffset(0);
    }

    private void renderItemOverlayIntoGUI(ItemStack itemStack, int xPosition, int yPosition)
    {
        if (!itemStack.isEmpty())
        {
            MatrixStack matrixstack = new MatrixStack();

            if (itemStack.getCount() != 1)
            {
                FontRenderer fontRenderer = this.font;
                String stackSize = String.valueOf(NumberUtils.format(itemStack.getCount()));

                if (itemStack.getCount() >= 100)
                {
                    fontRenderer = ClientUtils.unicodeFontRenderer;
                }

                matrixstack.translate(0.0D, 0.0D, this.itemRenderer.zLevel + 200.0F);
                IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
                fontRenderer.renderString(stackSize, xPosition + 19 - 2 - fontRenderer.getStringWidth(stackSize), yPosition + 6 + 3, 16777215, true, matrixstack.getLast().getMatrix(), irendertypebuffer$impl, false, 0, 15728880);
                irendertypebuffer$impl.finish();
            }
        }
    }

    private boolean renderGroupsHoveringText(SBInventoryGroup group, int mouseX, int mouseY)
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
            this.renderTooltip(Collections.singletonList(group.getTranslationKey()), mouseX, mouseY);
            return true;
        }
        else
        {
            return false;
        }
    }

    private void drawGroupsBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        SBInventoryGroup group = SBInventoryGroup.GROUPS[this.selectedTabIndex];

        for (SBInventoryGroup group1 : SBInventoryGroup.GROUPS)
        {
            this.minecraft.getTextureManager().bindTexture(INVENTORY_TABS);

            if (group1.getIndex() != this.selectedTabIndex)
            {
                this.drawGroup(group1);
            }
        }

        this.minecraft.getTextureManager().bindTexture(new ResourceLocation("skyblockcatia:textures/gui/group_" + group.getBackgroundTexture()));
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.guiLeft + 182;
        int j = this.guiTop + 18;
        int k = j + 72;
        this.minecraft.getTextureManager().bindTexture(INVENTORY_TABS);

        if (group.hasScrollbar())
        {
            this.blit(i, j + (int)((k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
        }

        this.drawGroup(group);
        RenderSystem.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
    }

    private void drawTabsForegroundLayer()
    {
        SBInventoryGroup group = SBInventoryGroup.GROUPS[this.selectedTabIndex];

        if (group != null)
        {
            RenderSystem.disableBlend();
            this.font.drawString(group.getTranslationKey(), this.guiLeft + 11, this.guiTop + 6, 4210752);
        }
    }

    private void drawGroup(SBInventoryGroup group)
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
        this.blit(l, i1, j, k, 28, j1);
        this.setBlitOffset(100);
        this.itemRenderer.zLevel = 100.0F;
        l = l + 6;
        i1 = i1 + 8 + (flag1 ? 1 : -1);
        RenderSystem.enableLighting();
        RenderSystem.enableRescaleNormal();
        ItemStack itemStack = group.getIcon();
        this.itemRenderer.renderItemAndEffectIntoGUI(itemStack, l, i1);
        this.itemRenderer.renderItemOverlays(this.font, itemStack, l, i1);
        RenderSystem.disableLighting();
        this.itemRenderer.zLevel = 0.0F;
        this.setBlitOffset(0);
    }

    // Player Data
    private void getPlayerData() throws IOException
    {
        this.statusMessage = "Getting Player Data";

        URL url = new URL(SBAPIUtils.SKYBLOCK_PROFILE + this.sbProfileId);
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
                URL urlStatus = new URL("https://api.hypixel.net/status?key=" + SkyBlockcatiaConfig.GENERAL.hypixelApiKey.get() + "&uuid=" + this.uuid);
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
                this.allStat.add(new BonusStatTemplate(0, 0, 0, this.allStat.getDefense() <= 0 ? this.allStat.getHealth() : (int)(this.allStat.getHealth() * (1 + this.allStat.getDefense() / 100.0D)), 0, 0, 0, 0, 0, 0, 0, 0));
                this.getBasicInfo(currentUserProfile, banking, objStatus, userUUID);
                break;
            }
        }

        if (!checkUUID.equals(this.uuid))
        {
            this.setErrorMessage("Current Player UUID not matched Profile UUID, please try again later!");
            return;
        }

        this.refreshViewButton(this.viewButton);
        this.refreshBasicInfoViewButton(this.basicInfoButton, true);
        this.refreshOthersViewButton(this.othersButton, false);
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
        for (SBMinions.Slot minion : SBMinions.MINION_SLOTS)
        {
            if (minion.getCurrentSlot() <= this.craftedMinionCount)
            {
                this.currentMinionSlot = minion.getMinionSlot();
            }
        }

        List<SBMinions.Info> minionLevels = new ArrayList<>();
        List<SBMinions.Data> minionDatas = new ArrayList<>();
        int level = 1;

        for (SBMinions.Type minion : SBMinions.Type.VALUES)
        {
            for (String minionType : this.craftedMinions.keySet())
            {
                if (minion.name().equals(minionType))
                {
                    level = Collections.max(this.craftedMinions.get(minionType));
                    break;
                }
            }
            minionLevels.add(new SBMinions.Info(minion.name(), minion.getAltName(), minion.getPetItem(), level, minion.getMinionCategory()));
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
                minionList.add(TextFormatting.RED + "" + skipped);
            }
            for (int crafted : craftedList)
            {
                minionList.add(TextFormatting.GREEN + "" + crafted);
            }

            minionList.sort((text1, text2) -> new CompareToBuilder().append(Integer.parseInt(TextFormatting.getTextWithoutFormattingCodes(text1)), Integer.parseInt(TextFormatting.getTextWithoutFormattingCodes(text2))).build());
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
        SBMinions.CraftedInfo dummy = new SBMinions.CraftedInfo(null, null, 0, null, ItemStack.EMPTY, null);
        String displayName = null;
        ItemStack itemStack = ItemStack.EMPTY;
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
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo("Farming", null, 0, null, ItemStack.EMPTY, null));
            this.sbCraftedMinions.addAll(farmingMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!miningMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo("Mining", null, 0, null, ItemStack.EMPTY, null));
            this.sbCraftedMinions.addAll(miningMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!combatMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo("Combat", null, 0, null, ItemStack.EMPTY, null));
            this.sbCraftedMinions.addAll(combatMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!foragingMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo("Foraging", null, 0, null, ItemStack.EMPTY, null));
            this.sbCraftedMinions.addAll(foragingMinion);
            this.sbCraftedMinions.add(dummy);
        }

        if (!fishingMinion.isEmpty())
        {
            this.sbCraftedMinions.add(new SBMinions.CraftedInfo("Fishing", null, 0, null, ItemStack.EMPTY, null));
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

                for (SBCollections.ItemId sbItem : SBCollections.ItemId.VALUES)
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

        SBCollections dummyCollection = new SBCollections(ItemStack.EMPTY, null, -1, -1);

        if (collections != null)
        {
            List<SBCollections> farming = new ArrayList<>();
            List<SBCollections> mining = new ArrayList<>();
            List<SBCollections> combat = new ArrayList<>();
            List<SBCollections> foraging = new ArrayList<>();
            List<SBCollections> fishing = new ArrayList<>();

            for (Map.Entry<String, JsonElement> collection : collections.getAsJsonObject().entrySet())
            {
                String collectionId = collection.getKey().toLowerCase();
                int collectionCount = collection.getValue().getAsInt();

                for (SBCollections.ItemId sbItem : SBCollections.ItemId.VALUES)
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

                String newItemReg = ItemStackDataFlattening.updateItem("minecraft:" + itemId, meta);

                if (newItemReg != null)
                {
                    itemId = EntityRenaming1510.ITEM_RENAME_MAP.getOrDefault(newItemReg, newItemReg);
                }

                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
                SBCollections.Type type = SBCollections.Type.FARMING;
                SBCollections itemCollection = new SBCollections(new ItemStack(item), type, collectionCount, level);

                if (item == Blocks.COBBLESTONE.asItem() || item == Items.COAL || item == Items.IRON_INGOT || item == Items.GOLD_INGOT || item == Items.DIAMOND || item == Items.EMERALD || item == Items.REDSTONE
                        || item == Items.QUARTZ || item == Blocks.OBSIDIAN.asItem() || item == Items.GLOWSTONE_DUST || item == Blocks.GRAVEL.asItem() || item == Blocks.ICE.asItem() || item == Blocks.NETHERRACK.asItem()
                        || item == Blocks.SAND.asItem() || item == Blocks.END_STONE.asItem() || item == Items.LAPIS_LAZULI)
                {
                    mining.add(itemCollection);
                    type = SBCollections.Type.MINING;
                }
                else if (item == Items.ROTTEN_FLESH || item == Items.BONE || item == Items.STRING || item == Items.SPIDER_EYE || item == Items.GUNPOWDER || item == Items.ENDER_PEARL || item == Items.GHAST_TEAR || item == Items.SLIME_BALL || item == Items.BLAZE_ROD || item == Items.MAGMA_CREAM)
                {
                    combat.add(itemCollection);
                    type = SBCollections.Type.COMBAT;
                }
                else if (item == Blocks.OAK_LOG.asItem() || item == Blocks.BIRCH_LOG.asItem() || item == Blocks.SPRUCE_LOG.asItem() || item == Blocks.ACACIA_LOG.asItem() || item == Blocks.JUNGLE_LOG.asItem() || item == Blocks.DARK_OAK_LOG.asItem())
                {
                    foraging.add(itemCollection);
                    type = SBCollections.Type.FORAGING;
                }
                else if (item == Items.COD || item == Items.SALMON || item == Items.PUFFERFISH || item == Items.TROPICAL_FISH || item == Items.PRISMARINE_SHARD || item == Items.PRISMARINE_CRYSTALS || item == Items.CLAY_BALL || item == Blocks.LILY_PAD.asItem() || item == Blocks.SPONGE.asItem() || item == Items.INK_SAC)
                {
                    fishing.add(itemCollection);
                    type = SBCollections.Type.FISHING;
                }
                else
                {
                    farming.add(itemCollection);
                }
            }

            Comparator<SBCollections> com = (sbColl1, sbColl2) -> new CompareToBuilder().append(sbColl1.getCollectionType().ordinal(), sbColl2.getCollectionType().ordinal()).append(sbColl2.getValue(), sbColl1.getValue()).build();
            farming.sort(com);
            mining.sort(com);
            combat.sort(com);
            foraging.sort(com);
            fishing.sort(com);

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

                    for (SBCollections.ItemId sbItem : SBCollections.ItemId.VALUES)
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

                    String newItemReg = ItemStackDataFlattening.updateItem("minecraft:" + itemId, meta);

                    if (newItemReg != null)
                    {
                        itemId = EntityRenaming1510.ITEM_RENAME_MAP.getOrDefault(newItemReg, newItemReg);
                    }

                    if (itemId.equals("revenant_flesh") || itemId.equals("tarantula_web") || itemId.equals("wolf_tooth"))
                    {
                        SlayerDrops slayerDrops = SlayerDrops.valueOf(itemId.toUpperCase());
                        ItemStack itemStack = new ItemStack(slayerDrops.getBaseItem(), count);
                        itemStack.setDisplayName(JsonUtils.create(slayerDrops.getDisplayName()));
                        ListNBT listEnch = new ListNBT();
                        listEnch.add(new CompoundNBT());
                        itemStack.getTag().put("Enchantments", listEnch);
                        sacks.add(itemStack);
                    }
                    else
                    {
                        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));

                        if (count > 1)
                        {
                            if (item != null)
                            {
                                sacks.add(new ItemStack(item, count));
                            }
                        }
                    }
                }
            }
            else
            {
                ItemStack barrier = new ItemStack(Blocks.BARRIER);
                barrier.setDisplayName(JsonUtils.create(TextFormatting.RESET + "" + TextFormatting.RED + "Sacks is not available!"));

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
        SKYBLOCK_INV.add(new SkyBlockInventory(sacks, SBInventoryGroup.SACKS));
    }

    private void getPets(JsonObject currentUserProfile)
    {
        List<SBPets.Data> petData = new ArrayList<>();
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
                String petRarity = SBPets.Tier.COMMON.name();
                int candyUsed = 0;
                SBPets.HeldItem heldItem = null;

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
                    heldItem = SBPets.HeldItem.valueOf(element.getAsJsonObject().get("heldItem").getAsString());
                }

                SBPets.Tier tier = SBPets.Tier.valueOf(petRarity);
                boolean active = element.getAsJsonObject().get("active").getAsBoolean();
                String petType = element.getAsJsonObject().get("type").getAsString();
                ListNBT list = new ListNBT();

                if (heldItem != null && heldItem == SBPets.HeldItem.PET_ITEM_TIER_BOOST)
                {
                    tier = SBPets.Tier.values()[Math.min(SBPets.Tier.values().length - 1, tier.ordinal() + 1)];
                }

                SBPets.Info level = this.checkPetLevel(exp, tier);

                try
                {
                    TextFormatting rarity = tier.getTierColor();
                    SBPets.Type type = SBPets.Type.valueOf(petType);
                    ItemStack itemStack = type.getPetItem();

                    itemStack.setDisplayName(JsonUtils.create(TextFormatting.RESET + "" + TextFormatting.GRAY + "[Lvl " + level.getCurrentPetLevel() + "] " + rarity + WordUtils.capitalize(petType.toLowerCase().replace("_", " "))));
                    list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.RESET + "" + TextFormatting.GRAY + type.getType().getName() + " Pet"))));
                    list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(""))));
                    list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.RESET + "" + (active ? TextFormatting.GREEN + "Active Pet" : TextFormatting.RED + "Inactive Pet")))));
                    list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.RESET + "" + (level.getCurrentPetLevel() < 100 ? TextFormatting.GRAY + "Next level is " + level.getNextPetLevel() + ": " + TextFormatting.YELLOW + level.getPercent() : level.getPercent())))));

                    if (level.getCurrentPetLevel() < 100)
                    {
                        list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.RESET + "" + TextFormatting.GRAY + "Current EXP: " + TextFormatting.YELLOW + NumberUtils.NUMBER_FORMAT.format(level.getCurrentPetXp()) + TextFormatting.GOLD + "/" + TextFormatting.YELLOW + SBNumberUtils.formatWithM(level.getXpRequired())))));
                    }
                    if (candyUsed > 0)
                    {
                        list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.RESET + "" + TextFormatting.GRAY + "Candy Used: " + TextFormatting.YELLOW + candyUsed + TextFormatting.GOLD + "/" + TextFormatting.YELLOW + 10))));
                    }
                    if (heldItem != null)
                    {
                        String heldItemName = heldItem.getColor() + WordUtils.capitalize(heldItem.toString().toLowerCase().replace("pet_item_", "").replace("_", " "));

                        if (heldItem.getAltName() != null)
                        {
                            heldItemName = heldItem.getColor() + WordUtils.capitalize(heldItem.getAltName().toLowerCase().replace("pet_item_", "").replace("_", " "));
                        }
                        list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.RESET + "" + TextFormatting.GRAY + "Held Item: " + heldItemName))));
                    }

                    list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(""))));
                    list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.RESET + "" + TextFormatting.GRAY + "Total XP: " + TextFormatting.YELLOW + NumberUtils.format(level.getPetXp()) + TextFormatting.GOLD + "/" + TextFormatting.YELLOW + SBNumberUtils.formatWithM(level.getTotalPetTypeXp())))));
                    list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(rarity + "" + TextFormatting.BOLD + tier + " PET"))));
                    itemStack.getTag().getCompound("display").put("Lore", list);
                    petData.add(new SBPets.Data(tier, level.getCurrentPetLevel(), active, Arrays.asList(itemStack)));

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
                    ItemStack itemStack = new ItemStack(Items.BONE);
                    itemStack.setDisplayName(JsonUtils.create(TextFormatting.RESET + "" + TextFormatting.RED + WordUtils.capitalize(petType.toLowerCase().replace("_", " "))));
                    list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.RED + "" + TextFormatting.BOLD + "UNKNOWN PET"))));
                    itemStack.getTag().getCompound("display").put("Lore", list);
                    petData.add(new SBPets.Data(SBPets.Tier.COMMON, 0, false, Arrays.asList(itemStack)));
                    SkyBlockcatiaMod.LOGGER.warning("Found an unknown pet! type: {}", petType);
                }
                petData.sort((o1, o2) -> new CompareToBuilder().append(o2.isActive(), o1.isActive()).append(o2.getTier().ordinal(), o1.getTier().ordinal()).append(o2.getCurrentLevel(), o1.getCurrentLevel()).build());
            }
            for (SBPets.Data data : petData)
            {
                SKYBLOCK_INV.add(new SkyBlockInventory(data.getItemStack(), SBInventoryGroup.PET));
            }
        }
        this.petScore = commonScore + uncommonScore + rareScore + epicScore + legendaryScore;
    }

    private SBPets.Info checkPetLevel(double petExp, SBPets.Tier tier)
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
            xpToNextLvl = MathHelper.ceil(xpTotal - petExp);
            currentXp = xpRequired - xpToNextLvl;
        }
        else
        {
            currentLvl = progress.length + 1;
            xpRequired = 0;
        }
        return new SBPets.Info(currentLvl, levelToCheck, (int)currentXp, xpRequired, (int)petExp, totalPetTypeXp);
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
            this.allStat.setHealth((int)Math.round(this.allStat.getHealth() * 1.05D));
            this.allStat.setDefense((int)Math.round(this.allStat.getDefense() * 1.05D));
            this.allStat.setStrength((int)Math.round(this.allStat.getStrength() * 1.05D));
            this.allStat.setSpeed((int)Math.round(this.allStat.getSpeed() * 1.05D));
            this.allStat.setCritChance((int)Math.round(this.allStat.getCritChance() * 1.05D));
            this.allStat.setCritDamage((int)Math.round(this.allStat.getCritDamage() * 1.05D));
            this.allStat.setIntelligence((int)Math.round(this.allStat.getIntelligence() * 1.05D));
            this.allStat.setSeaCreatureChance((int)Math.round(this.allStat.getSeaCreatureChance() * 1.05D));
            this.allStat.setMagicFind((int)Math.round(this.allStat.getMagicFind() * 1.05D));
            this.allStat.setPetLuck((int)Math.round(this.allStat.getPetLuck() * 1.05D));
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
                    intelligenceTemp += intelligence;
                    seaCreatureChanceTemp += seaCreatureChance;
                    magicFindTemp += magicFind;
                    petLuckTemp += petLuck;
                }
            }
        }
        return new BonusStatTemplate(healthTemp, defenseTemp, trueDefenseTemp, 0, strengthTemp, speedTemp, critChanceTemp, critDamageTemp, intelligenceTemp, seaCreatureChanceTemp, magicFindTemp, petLuckTemp);
    }

    private void getHealthFromCake(CompoundNBT extraAttrib)
    {
        List<ItemStack> itemStack1 = new ArrayList<>();

        try
        {
            CompoundNBT compound1 = CompressedStreamTools.readCompressed(new ByteArrayInputStream(extraAttrib.getByteArray("new_year_cake_bag_data")));
            ListNBT list = compound1.getList("i", Constants.NBT.TAG_COMPOUND);
            List<Integer> cakeYears = new ArrayList<>();

            for (int i = 0; i < list.size(); ++i)
            {
                itemStack1.add(SBAPIUtils.flatteningItemStack(list.getCompound(i)));
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
        double intelligenceTemp = 0;
        double seaCreatureChanceTemp = 0;
        double magicFindTemp = 0;
        double petLuckTemp = 0;

        for (ItemStack itemStack : inventory)
        {
            if (!itemStack.isEmpty() && itemStack.hasTag())
            {
                CompoundNBT compound = itemStack.getTag().getCompound("display");
                CompoundNBT extraAttrib = itemStack.getTag().getCompound("ExtraAttributes");
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

                if (compound.getTagId("Lore") == Constants.NBT.TAG_LIST)
                {
                    ListNBT list = compound.getList("Lore", Constants.NBT.TAG_STRING);

                    for (int j1 = 0; j1 < list.size(); ++j1)
                    {
                        String lore = TextFormatting.getTextWithoutFormattingCodes(ITextComponent.Serializer.fromJson(list.getString(j1)).getString());
                        String lastLore = TextFormatting.getTextWithoutFormattingCodes(ITextComponent.Serializer.fromJson(list.getString(list.size() - 1)).getString());
                        Matcher matcher = STATS_PATTERN.matcher(lore);

                        if (!armor && (lastLore.endsWith(" BOOTS") || lastLore.endsWith(" LEGGINGS") || lastLore.endsWith(" CHESTPLATE") || lastLore.endsWith(" HELMET")))
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
                            }
                        }
                    }
                }
            }
        }
        this.allStat.add(new BonusStatTemplate(healthTemp, defenseTemp, trueDefenseTemp, 0, strengthTemp, speedTemp, critChanceTemp, critDamageTemp, intelligenceTemp, seaCreatureChanceTemp, magicFindTemp, petLuckTemp));
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
        String intelligence = ColorUtils.stringToRGB("129,212,250").toColoredFont();
        String fairySoulsColor = ColorUtils.stringToRGB("203,54,202").toColoredFont();
        String seaCreatureChance = ColorUtils.stringToRGB("0,170,170").toColoredFont();
        String magicFind = ColorUtils.stringToRGB("85,255,255").toColoredFont();
        String petLuck = ColorUtils.stringToRGB("255,85,255").toColoredFont();
        String location = this.getLocation(objStatus, uuid);

        if (!StringUtils.isNullOrEmpty(location))
        {
            this.infoList.add(new SkyBlockInfo("\u23E3 Current Location", location));
        }

        this.infoList.add(new SkyBlockInfo(fairySoulsColor + "Fairy Souls Collected", fairySoulsColor + this.totalFairySouls + "/" + SBAPIUtils.MAX_FAIRY_SOULS));
        this.infoList.add(new SkyBlockInfo("", ""));
        this.infoList.add(new SkyBlockInfo(heath + "\u2764 Health", heath + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getHealth())));
        this.infoList.add(new SkyBlockInfo(heath + "\u2665 Effective Health", heath + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getEffectiveHealth())));
        this.infoList.add(new SkyBlockInfo(defense + "\u2748 Defense", defense + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getDefense())));
        this.infoList.add(new SkyBlockInfo(trueDefense + "\u2742 True Defense", trueDefense + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getTrueDefense())));
        this.infoList.add(new SkyBlockInfo(strength + "\u2741 Strength", strength + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getStrength())));
        this.infoList.add(new SkyBlockInfo(speed + "\u2726 Speed", speed + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getSpeed())));
        this.infoList.add(new SkyBlockInfo(critChance + "\u2623 Crit Chance", critChance + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getCritChance())));
        this.infoList.add(new SkyBlockInfo(critDamage + "\u2620 Crit Damage", critDamage + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getCritDamage())));
        this.infoList.add(new SkyBlockInfo(intelligence + "\u270E Intelligence", intelligence + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getIntelligence())));
        this.infoList.add(new SkyBlockInfo(seaCreatureChance + "\u03B1 Sea Creature Chance", seaCreatureChance + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getSeaCreatureChance())));
        this.infoList.add(new SkyBlockInfo(magicFind + "\u272F Magic Find", magicFind + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getMagicFind())));
        this.infoList.add(new SkyBlockInfo(petLuck + "\u2663 Pet Luck", petLuck + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.allStat.getPetLuck())));

        this.infoList.add(new SkyBlockInfo("", ""));

        Date firstJoinDate = new Date(firstJoinMillis);
        Date lastSaveDate = new Date(lastSaveMillis);
        SimpleDateFormat logoutDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        String lastLogout = logoutDate.format(lastSaveDate);
        SimpleDateFormat joinDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        joinDate.setTimeZone(this.uuid.equals("eef3a6031c1b4c988264d2f04b231ef4") ? TimeZone.getTimeZone("GMT") : TimeZone.getDefault());
        String firstJoinDateFormat = joinDate.format(firstJoinDate);

        this.infoList.add(new SkyBlockInfo("Joined", firstJoinMillis != -1 ? TimeUtils.getRelativeTime(firstJoinDate.getTime()) : TextFormatting.RED + "No first join data!"));
        this.infoList.add(new SkyBlockInfo("Joined (Date)", firstJoinMillis != -1 ? firstJoinDateFormat : TextFormatting.RED + "No first join data!"));
        this.infoList.add(new SkyBlockInfo("Last Updated", lastSaveMillis != -1 ? String.valueOf(lastSaveDate.getTime()) : TextFormatting.RED + "No last save data!"));
        this.infoList.add(new SkyBlockInfo("Last Updated (Date)", lastSaveMillis != -1 ? lastLogout : TextFormatting.RED + "No last save data!"));

        this.infoList.add(new SkyBlockInfo("Death Count", String.valueOf(deathCounts)));

        if (banking != null)
        {
            double balance = banking.getAsJsonObject().get("balance").getAsDouble();
            this.infoList.add(new SkyBlockInfo("Banking Account", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(balance)));
        }
        else
        {
            this.infoList.add(new SkyBlockInfo("Banking Account", TextFormatting.RED + "API is not enabled!"));
        }
        this.infoList.add(new SkyBlockInfo("Purse", NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(coins)));
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
        return new BonusStatTemplate(healthBase, defenseBase, 0, 0, strengthBase, speedBase, 0, 0, 0, 0, 0, 0);
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
        return new BonusStatTemplate(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, magicFindBase, 0);
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
        this.skillLeftList.add(this.checkSkill(currentProfile.get("experience_skill_runecrafting"), SBSkills.Type.RUNECRAFTING, ExpProgress.RUNECRAFTING));

        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_combat"), SBSkills.Type.COMBAT));
        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_enchanting"), SBSkills.Type.ENCHANTING));
        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_alchemy"), SBSkills.Type.ALCHEMY));
        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_taming"), SBSkills.Type.TAMING));
        this.skillRightList.add(this.checkSkill(currentProfile.get("experience_skill_carpentry"), SBSkills.Type.CARPENTRY));

        double avg = 0.0D;
        int count = 0;
        List<SBSkills.Info> skills = new ArrayList<>();
        skills.addAll(this.skillLeftList);
        skills.addAll(this.skillRightList);

        for (SBSkills.Info skill : skills)
        {
            avg += skill.getCurrentLvl() + skill.getSkillProgress();
            ++count;
        }
        if (avg > 0)
        {
            this.skillAvg = NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(avg / count);
        }
        if (this.skillCount == 0)
        {
            this.data.setHasSkills(false);
        }
    }

    private SBSkills.Info checkSkill(JsonElement element, SBSkills.Type type)
    {
        return this.checkSkill(element, type, ExpProgress.SKILL);
    }

    private SBSkills.Info checkSkill(JsonElement element, SBSkills.Type type, ExpProgress[] progress)
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
            if (type != SBSkills.Type.RUNECRAFTING && type != SBSkills.Type.CARPENTRY)
            {
                skillProgress = Math.max(0, Math.min(currentXp / xpToNextLvl, 1));
            }
            this.setSkillLevel(type, currentLvl);
            this.skillCount += 1;
            return new SBSkills.Info(type.getName(), currentXp, xpRequired, currentLvl, skillProgress, xpToNextLvl <= 0);
        }
        else
        {
            return new SBSkills.Info(TextFormatting.RED + type.getName() + " is not available!", 0, 0, 0, 0, false);
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
        List<SBStats> auctions = new ArrayList<>();
        List<SBStats> fished = new ArrayList<>();
        List<SBStats> winter = new ArrayList<>();
        List<SBStats> petMilestone = new ArrayList<>();
        List<SBStats> others = new ArrayList<>();
        List<SBStats> mobKills = new ArrayList<>();
        List<SBStats> seaCreatures = new ArrayList<>();
        List<SBStats> dragons = new ArrayList<>();
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
                else
                {
                    others.add(new SBStats(WordUtils.capitalize(statName.replace("_", " ")), value));
                }
            }
        }

        if (emperorKills > 0)
        {
            seaCreatures.add(new SBStats("Sea Emperor kills", emperorKills)); // special case
        }

        this.sbDeaths.sort((stat1, stat2) -> new CompareToBuilder().append(stat2.getValue(), stat1.getValue()).build());
        auctions.sort((stat1, stat2) -> new CompareToBuilder().append(stat1.getName(), stat2.getName()).build());
        auctions.add(0, new SBStats(new StringTextComponent("Auctions").applyTextStyles(TextFormatting.YELLOW, TextFormatting.BOLD, TextFormatting.UNDERLINE).getFormattedText(), 0.0F));

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

    private void sortStats(List<SBStats> list, String name)
    {
        list.sort((stat1, stat2) -> new CompareToBuilder().append(stat1.getName(), stat2.getName()).build());
        list.add(0, new SBStats(null, 0.0F));
        list.add(1, new SBStats(new StringTextComponent(name).applyTextStyles(TextFormatting.YELLOW, TextFormatting.BOLD, TextFormatting.UNDERLINE).getFormattedText(), 0.0F));
    }

    private void sortStatsByValue(List<SBStats> list, String name)
    {
        list.sort((stat1, stat2) -> new CompareToBuilder().append(stat2.getValue(), stat1.getValue()).build());
        list.add(0, new SBStats(null, 0.0F));
        list.add(1, new SBStats(new StringTextComponent(name).applyTextStyles(TextFormatting.YELLOW, TextFormatting.BOLD, TextFormatting.UNDERLINE).getFormattedText(), 0.0F));
    }

    private long checkSkyBlockItem(List<ItemStack> list, String type)
    {
        return list.stream().filter(armor -> !armor.isEmpty() && armor.hasTag() && armor.getTag().getCompound("ExtraAttributes").getString("id").startsWith(type)).count();
    }

    private void getInventories(JsonObject currentProfile)
    {
        this.armorItems.addAll(SBAPIUtils.decodeItem(currentProfile, SBInventoryType.ARMOR));

        for (int i = 0; i < 4; ++i)
        {
            SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY.setInventorySlotContents(i, this.armorItems.get(i));
        }

        List<ItemStack> mainInventory = SBAPIUtils.decodeItem(currentProfile, SBInventoryType.INVENTORY);
        List<ItemStack> accessoryInventory = SBAPIUtils.decodeItem(currentProfile, SBInventoryType.ACCESSORY_BAG);

        SKYBLOCK_INV.add(new SkyBlockInventory(mainInventory, SBInventoryGroup.INVENTORY));
        SKYBLOCK_INV.add(new SkyBlockInventory(SBAPIUtils.decodeItem(currentProfile, SBInventoryType.ENDER_CHEST), SBInventoryGroup.ENDER_CHEST));
        SKYBLOCK_INV.add(new SkyBlockInventory(accessoryInventory, SBInventoryGroup.ACCESSORY));
        SKYBLOCK_INV.add(new SkyBlockInventory(SBAPIUtils.decodeItem(currentProfile, SBInventoryType.POTION_BAG), SBInventoryGroup.POTION));
        SKYBLOCK_INV.add(new SkyBlockInventory(SBAPIUtils.decodeItem(currentProfile, SBInventoryType.FISHING_BAG), SBInventoryGroup.FISHING));
        SKYBLOCK_INV.add(new SkyBlockInventory(SBAPIUtils.decodeItem(currentProfile, SBInventoryType.QUIVER), SBInventoryGroup.QUIVER));
        SKYBLOCK_INV.add(new SkyBlockInventory(SBAPIUtils.decodeItem(currentProfile, SBInventoryType.CANDY), SBInventoryGroup.CANDY));
        SKYBLOCK_INV.add(new SkyBlockInventory(SBAPIUtils.decodeItem(currentProfile, SBInventoryType.WARDROBE), SBInventoryGroup.WARDROBE));

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
        if (this.minecraft.getConnection().getPlayerInfo(this.profile.getName()) == null)
        {
            try
            {
                Class<?> innerClass = SPlayerListItemPacket.AddPlayerData.class;
                Constructor<?> ctor = innerClass.getDeclaredConstructor(SPlayerListItemPacket.class, GameProfile.class, int.class, GameType.class, ITextComponent.class);
                Object innerInstance = ctor.newInstance(new SPlayerListItemPacket(), this.profile, 0, null, null);
                this.minecraft.getConnection().playerInfoMap.put(this.profile.getId(), ((IViewerLoader)new NetworkPlayerInfo((SPlayerListItemPacket.AddPlayerData)innerInstance)).setLoadedFromViewer(true)); // hack into map to show their skin :D
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        this.player = new SBFakePlayerEntity(this.minecraft.world, this.profile);
        SkyBlockAPIViewerScreen.renderSecondLayer = true;

        for (ItemStack armor : this.armorItems)
        {
            if (armor.isEmpty())
            {
                continue;
            }

            int index = MobEntity.getSlotForItemStack(armor).getIndex();

            if (armor.getItem() instanceof BlockItem)
            {
                index = 3;
            }
            this.player.setItemStackToSlot(EquipmentSlotType.fromSlotTypeAndIndex(EquipmentSlotType.Group.ARMOR, index), armor);
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

                list.add(new SkyBlockSlayerInfo(TextFormatting.GRAY + type.getName() + " Slayer: " + TextFormatting.YELLOW + "LVL " + slayerLvl));
                list.add(new SkyBlockSlayerInfo(TextFormatting.GRAY + "EXP: " + TextFormatting.LIGHT_PURPLE + (xpToNextLvl == 0 ? NumberUtils.NUMBER_FORMAT.format(playerSlayerXp) : NumberUtils.NUMBER_FORMAT.format(playerSlayerXp) + TextFormatting.DARK_PURPLE + "/" + TextFormatting.LIGHT_PURPLE + NumberUtils.NUMBER_FORMAT.format(xpRequired))));

                if (xpToNextLvl != 0)
                {
                    list.add(new SkyBlockSlayerInfo(TextFormatting.GRAY + "XP to " + TextFormatting.YELLOW + "LVL " + levelToCheck + ": " + TextFormatting.LIGHT_PURPLE + NumberUtils.NUMBER_FORMAT.format(xpToNextLvl)));
                }

                list.add(SkyBlockSlayerInfo.createMobAndXp(type.getName(), playerSlayerXp + "," + xpRequired + "," + xpToNextLvl));
                int amount = 0;

                for (int i = 1; i <= 4; i++)
                {
                    JsonElement kill = slayer.getAsJsonObject().get("boss_kills_tier_" + (i - 1));
                    int kills = this.getSlayerKill(kill);
                    amount += this.getSlayerPrice(kills, i - 1);
                    list.add(new SkyBlockSlayerInfo(TextFormatting.GRAY + "Tier " + i + ": " + TextFormatting.YELLOW + this.formatSlayerKill(this.getSlayerKill(kill))));
                }
                this.slayerTotalAmountSpent += amount;
                this.totalSlayerXp += playerSlayerXp;
                list.add(new SkyBlockSlayerInfo(TextFormatting.GRAY + "Amount Spent: " + TextFormatting.YELLOW + NumberUtils.NUMBER_FORMAT.format(amount)));
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
        return NumberUtils.NUMBER_FORMAT.format(kills) + " kill" + (kills <= 1 ? "" : "s");
    }

    private static void renderEntity(int posX, int posY, int scale, LivingEntity entity)
    {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(posX, posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale(scale, scale, scale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.YP.rotationDegrees(ClientEventHandler.renderPartialTicks);
        quaternion.multiply(quaternion1);
        matrixstack.rotate(quaternion);
        entity.rotationYaw = (float)(Math.atan(0) * 40.0F);
        entity.rotationYawHead = entity.rotationYaw;
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        quaternion1.conjugate();
        entityrenderermanager.setCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        entityrenderermanager.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
        irendertypebuffer$impl.finish();
        entityrenderermanager.setRenderShadow(true);
        RenderSystem.popMatrix();
    }

    private void drawItemStackSlot(int x, int y, ItemStack itemStack)
    {
        this.drawSprite(x + 1, y + 1);
        RenderSystem.enableRescaleNormal();
        this.minecraft.getItemRenderer().renderItemIntoGUI(itemStack, x + 2, y + 2);
        RenderSystem.disableRescaleNormal();
    }

    private void drawSprite(int left, int top)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtils.bindTexture(AbstractGui.STATS_ICON_LOCATION);
        AbstractGui.blit(left, top, this.getBlitOffset(), 0, 0, 18, 18, 128, 128);
    }

    public class SkyBlockInventory
    {
        private final List<ItemStack> items;
        private final SBInventoryGroup group;

        public SkyBlockInventory(List<ItemStack> items, SBInventoryGroup group)
        {
            this.items = items;
            this.group = group;
        }

        public List<ItemStack> getItems()
        {
            return this.items;
        }

        public SBInventoryGroup getGroup()
        {
            return this.group;
        }
    }

    static class ArmorContainer extends Container
    {
        public ArmorContainer()
        {
            super(null, 0);
            this.addSlot(new Slot(SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY, 0, -52, 75)); // boots
            this.addSlot(new Slot(SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY, 1, -52, 56));
            this.addSlot(new Slot(SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY, 2, -52, 36));
            this.addSlot(new Slot(SkyBlockAPIViewerScreen.TEMP_ARMOR_INVENTORY, 3, -52, 12)); // helmet
        }

        @Override
        public boolean canInteractWith(PlayerEntity player)
        {
            return false;
        }

        @Override
        public ItemStack transferStackInSlot(PlayerEntity player, int index)
        {
            return ItemStack.EMPTY;
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

    static class SkyBlockContainer extends Container
    {
        public final NonNullList<ItemStack> itemList = NonNullList.create();

        public SkyBlockContainer()
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
        public boolean canInteractWith(PlayerEntity player)
        {
            return false;
        }

        @Override
        public ItemStack transferStackInSlot(PlayerEntity player, int index)
        {
            return ItemStack.EMPTY;
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
                        SkyBlockAPIViewerScreen.TEMP_INVENTORY.setInventorySlotContents(l + k * 9, ItemStack.EMPTY);
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
                    return TimeUtils.getRelativeTime(Long.valueOf(this.value));
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

    static class EmptyStats extends ScrollingListScreen
    {
        private final Type type;

        public EmptyStats(SkyBlockAPIViewerScreen parent, int width, int height, int top, int bottom, int left, int slotHeight, Type type)
        {
            super(parent, width, height, top, bottom, left, slotHeight);
            this.type = type;
        }

        @Override
        protected void drawPanel(int index, int left, int right, int top) {}

        public Type getType()
        {
            return this.type;
        }

        enum Type
        {
            INVENTORY, SKILL;
        }
    }

    class InfoStats extends ScrollingListScreen
    {
        private final List<SkyBlockInfo> stats;

        public InfoStats(SkyBlockAPIViewerScreen parent, int width, int height, int top, int bottom, int left, int slotHeight, List<SkyBlockInfo> stats)
        {
            super(parent, width, height, top, bottom, left, slotHeight);
            this.stats = stats;
        }

        @Override
        protected int getSize()
        {
            return this.stats.size();
        }

        @Override
        protected void drawPanel(int index, int left, int right, int top)
        {
            SkyBlockInfo stat = InfoStats.this.stats.get(index);
            this.fontRenderer.drawString(stat.getTitle(), SkyBlockAPIViewerScreen.this.guiLeft - 20, top, index % 2 == 0 ? 16777215 : 9474192);
            this.fontRenderer.drawString(stat.getValue(), SkyBlockAPIViewerScreen.this.guiLeft - this.fontRenderer.getStringWidth(stat.getValue()) + 195, top, index % 2 == 0 ? 16777215 : 9474192);
        }
    }

    class SlayerStats extends ScrollingListScreen
    {
        private final List<SkyBlockSlayerInfo> stats;

        public SlayerStats(SkyBlockAPIViewerScreen parent, int width, int height, int top, int bottom, int left, int slotHeight, List<SkyBlockSlayerInfo> stats)
        {
            super(parent, width, height, top, bottom, left, slotHeight);
            this.stats = stats;
            this.headerHeight = 16;
        }

        @Override
        protected int getSize()
        {
            return this.stats.size();
        }

        @Override
        protected void drawPanel(int index, int left, int right, int top)
        {
            SkyBlockSlayerInfo stat = this.stats.get(index);

            switch (stat.getType())
            {
            case XP_AND_MOB:
                if (stat.getText().equals("Zombie"))
                {
                    ZombieEntity zombie = new ZombieEntity(this.world);
                    ItemStack heldItem = new ItemStack(Items.DIAMOND_HOE);
                    ItemStack helmet = SBRenderUtils.getSkullItemStack(SkyBlockAPIViewerScreen.REVENANT_HORROR_HEAD[0], SkyBlockAPIViewerScreen.REVENANT_HORROR_HEAD[1]);
                    ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
                    ItemStack leggings = new ItemStack(Items.CHAINMAIL_LEGGINGS);
                    ItemStack boots = new ItemStack(Items.DIAMOND_BOOTS);
                    zombie.setItemStackToSlot(EquipmentSlotType.HEAD, helmet);
                    zombie.setItemStackToSlot(EquipmentSlotType.CHEST, chestplate);
                    zombie.setItemStackToSlot(EquipmentSlotType.LEGS, leggings);
                    zombie.setItemStackToSlot(EquipmentSlotType.FEET, boots);
                    zombie.setItemStackToSlot(EquipmentSlotType.MAINHAND, heldItem);
                    SkyBlockAPIViewerScreen.renderEntity(SkyBlockAPIViewerScreen.this.guiLeft - 30, top + 60, 40, zombie);
                }
                else if (stat.getText().equals("Spider"))
                {
                    SpiderEntity spider = new SpiderEntity(EntityType.SPIDER, this.world);
                    CaveSpiderEntity cave = new CaveSpiderEntity(EntityType.CAVE_SPIDER, this.world);
                    SkyBlockAPIViewerScreen.renderEntity(SkyBlockAPIViewerScreen.this.guiLeft - 30, top + 40, 40, cave);
                    SkyBlockAPIViewerScreen.renderEntity(SkyBlockAPIViewerScreen.this.guiLeft - 30, top + 60, 40, spider);
                    RenderSystem.blendFunc(770, 771);
                }
                else
                {
                    WolfEntity wolf = new WolfEntity(EntityType.WOLF, this.world);
                    wolf.setAngry(true);
                    SkyBlockAPIViewerScreen.renderEntity(SkyBlockAPIViewerScreen.this.guiLeft - 30, top + 60, 40, wolf);
                }

                this.mc.getTextureManager().bindTexture(XP_BARS);
                RenderSystem.color4f(0.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.disableBlend();

                String[] xpSplit = stat.getXp().split(",");
                int playerSlayerXp = Integer.valueOf(xpSplit[0]);
                int xpRequired = Integer.valueOf(xpSplit[1]);
                int filled = Math.min((int)Math.floor(playerSlayerXp * 92 / xpRequired), 91);
                AbstractGui.blit(SkyBlockAPIViewerScreen.this.guiLeft + 90, top, 0, 0, 91, 5, 91, 10);

                if (filled > 0)
                {
                    AbstractGui.blit(SkyBlockAPIViewerScreen.this.guiLeft + 90, top, 0, 5, filled, 5, 91, 10);
                }

                RenderSystem.enableBlend();
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                break;
            default:
                if (this.getSize() == 1)
                {
                    this.fontRenderer.drawString(stat.getText(), SkyBlockAPIViewerScreen.this.guiLeft + 200, top, 16777215);
                }
                else
                {
                    this.fontRenderer.drawString(stat.getText(), SkyBlockAPIViewerScreen.this.guiLeft - this.fontRenderer.getStringWidth(stat.getText()) + 180, top, 16777215);
                }
                break;
            }
        }
    }

    class Others extends ScrollingListScreen
    {
        private final List<SBStats> stats;

        public Others(SkyBlockAPIViewerScreen parent, int width, int height, int top, int bottom, int left, int slotHeight, List<SBStats> stats, SBStats.Type type)
        {
            super(parent, width, height, top, bottom, left, slotHeight);
            this.stats = stats;
        }

        @Override
        protected int getSize()
        {
            return this.stats.size();
        }

        @Override
        protected void drawPanel(int index, int left, int right, int top)
        {
            if (!this.stats.isEmpty())
            {
                SBStats stat = this.stats.get(index);
                this.fontRenderer.drawString(StringUtils.isNullOrEmpty(stat.getName()) ? "" : stat.getName(), SkyBlockAPIViewerScreen.this.guiLeft - 85, top, index % 2 == 0 ? 16777215 : 9474192);
                this.fontRenderer.drawString(stat.getValueByString(), SkyBlockAPIViewerScreen.this.guiLeft - this.fontRenderer.getStringWidth(stat.getValueByString()) + 180, top, index % 2 == 0 ? 16777215 : 9474192);
            }
        }
    }

    class SkyBlockCollections extends ScrollingListScreen
    {
        private final List<SBCollections> collection;
        private final SkyBlockAPIViewerScreen parent;

        public SkyBlockCollections(SkyBlockAPIViewerScreen parent, int width, int height, int top, int bottom, int left, int slotHeight, List<SBCollections> collection)
        {
            super(parent, width, height, top, bottom, left, slotHeight);
            this.collection = collection;
            this.parent = parent;
        }

        @Override
        protected int getSize()
        {
            return this.collection.size();
        }

        @Override
        protected void drawPanel(int index, int left, int right, int top)
        {
            SBCollections collection = this.collection.get(index);

            if (!collection.getItemStack().isEmpty() && collection.getCollectionType() != null)
            {
                this.parent.drawItemStackSlot(this.parent.guiLeft - 65, top, collection.getItemStack());
                this.fontRenderer.drawString(collection.getItemStack().getDisplayName().getFormattedText() + " " + TextFormatting.GOLD + collection.getLevel(), this.parent.guiLeft - 41, top + 6, 16777215);
                this.fontRenderer.drawString(collection.getCollectionAmount(), this.parent.guiLeft - this.fontRenderer.getStringWidth(collection.getCollectionAmount()) + 170, top + 6, index % 2 == 0 ? 16777215 : 9474192);
            }
            else
            {
                if (collection.getCollectionType() != null)
                {
                    this.fontRenderer.drawString(new StringTextComponent(collection.getCollectionType().getName()).applyTextStyles(TextFormatting.YELLOW, TextFormatting.BOLD, TextFormatting.UNDERLINE).getFormattedText(), this.parent.guiLeft - 65, top + 5, 16777215);
                }
            }
        }
    }

    class SkyBlockCraftedMinions extends ScrollingListScreen
    {
        private final List<SBMinions.CraftedInfo> craftMinions;
        private final SkyBlockAPIViewerScreen parent;

        public SkyBlockCraftedMinions(SkyBlockAPIViewerScreen parent, int width, int height, int top, int bottom, int left, int slotHeight, List<SBMinions.CraftedInfo> craftMinions)
        {
            super(parent, width, height, top, bottom, left, slotHeight);
            this.craftMinions = craftMinions;
            this.parent = parent;
        }

        @Override
        protected int getSize()
        {
            return this.craftMinions.size();
        }

        @Override
        protected void drawPanel(int index, int left, int right, int top)
        {
            SBMinions.CraftedInfo craftedMinion = this.craftMinions.get(index);

            if (!craftedMinion.getMinionItem().isEmpty())
            {
                String name = craftedMinion.getDisplayName() != null ? WordUtils.capitalize(craftedMinion.getDisplayName().toLowerCase().replace("_", " ")) : WordUtils.capitalize(craftedMinion.getMinionName().toLowerCase().replace("_", " "));
                this.parent.drawItemStackSlot(this.parent.guiLeft - 102, top, craftedMinion.getMinionItem());
                this.fontRenderer.drawString(name + " Minion " + TextFormatting.GOLD + craftedMinion.getMinionMaxTier(), this.parent.guiLeft - 79, top + 6, 16777215);
                this.fontRenderer.drawString(craftedMinion.getCraftedTiers(), this.parent.guiLeft - this.fontRenderer.getStringWidth(craftedMinion.getCraftedTiers()) + 192, top + 6, index % 2 == 0 ? 16777215 : 9474192);
            }
            else
            {
                if (craftedMinion.getMinionName() != null)
                {
                    this.fontRenderer.drawString(new StringTextComponent(craftedMinion.getMinionName()).applyTextStyles(TextFormatting.YELLOW, TextFormatting.BOLD, TextFormatting.UNDERLINE).getFormattedText(), this.parent.guiLeft - 100, top + 5, 16777215);
                }
            }
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

    private enum SlayerDrops
    {
        TARANTULA_WEB(TextFormatting.RESET + "" + TextFormatting.GREEN + "Tarantula Web", Items.STRING),
        REVENANT_FLESH(TextFormatting.RESET + "" + TextFormatting.GREEN + "Revenant Flesh", Items.ROTTEN_FLESH),
        WOLF_TOOTH(TextFormatting.RESET + "" + TextFormatting.GREEN + "Wolf Tooth", Items.GHAST_TEAR);

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

    private static class ExtendedInventory extends Inventory
    {
        public ExtendedInventory(int slotCount)
        {
            super(slotCount);
        }

        @Override
        public int getInventoryStackLimit()
        {
            return 20160;
        }
    }

    private enum ViewButton
    {
        INFO,
        SKILLS,
        SLAYERS,
        OTHERS;

        protected static final ViewButton[] VALUES = ViewButton.values();
        Button button;
    }

    private enum OthersViewButton
    {
        KILLS,
        DEATHS,
        OTHER_STATS;

        protected static final OthersViewButton[] VALUES = OthersViewButton.values();
        Button button;
    }

    private enum BasicInfoViewButton
    {
        INFO,
        INVENTORY,
        COLLECTIONS,
        CRAFTED_MINIONS;

        protected static final BasicInfoViewButton[] VALUES = BasicInfoViewButton.values();
        Button button;
    }
}