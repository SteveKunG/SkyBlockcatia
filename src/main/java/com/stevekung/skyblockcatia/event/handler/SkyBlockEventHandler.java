package com.stevekung.skyblockcatia.event.handler;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.config.ToastMode;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.gui.SignSelectionList;
import com.stevekung.skyblockcatia.gui.screen.SkyBlockProfileSelectorScreen;
import com.stevekung.skyblockcatia.gui.toasts.GiftToast;
import com.stevekung.skyblockcatia.gui.toasts.ItemDropsToast;
import com.stevekung.skyblockcatia.gui.toasts.NumericToast;
import com.stevekung.skyblockcatia.gui.toasts.VisitIslandToast;
import com.stevekung.skyblockcatia.hud.InfoUtils;
import com.stevekung.skyblockcatia.keybinding.KeyBindingsSB;
import com.stevekung.skyblockcatia.utils.*;
import com.stevekung.skyblockcatia.utils.ToastUtils.ToastType;
import com.stevekung.skyblockcatia.utils.skyblock.*;
import com.stevekung.skyblockcatia.utils.skyblock.api.Bazaar;
import com.stevekung.skyblockcatia.utils.skyblock.api.HypixelProfiles;
import com.stevekung.skyblockcatia.utils.skyblock.api.PetStats;
import com.stevekung.skyblockcatia.utils.skyblock.api.SkyblockProfiles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.event.ClickEvent;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class SkyBlockEventHandler
{
    private static final Pattern LETTERS_NUMBERS = Pattern.compile("[^a-z A-Z:0-9/'()]");
    private static final Pattern VISIT_ISLAND_PATTERN = Pattern.compile("(?:\\[SkyBlock\\]|\\[SkyBlock\\] (?:\\[VIP?\\+{0,1}\\]|\\[MVP?\\+{0,2}\\]|\\[YOUTUBE\\])) (?<name>\\w+) is visiting Your Island!");
    public static final String UUID_PATTERN_STRING = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    private static final Pattern UUID_PATTERN = Pattern.compile("Your new API key is (?<uuid>" + SkyBlockEventHandler.UUID_PATTERN_STRING + ")");
    private static final String RANKED_PATTERN = "(?:(?:\\w)|(?:\\[VIP?\\+{0,1}\\]|\\[MVP?\\+{0,2}\\]|\\[YOUTUBE\\]) \\w)+";
    private static final Pattern PET_CARE_PATTERN = Pattern.compile("§r§aI'm currently taking care of your §r(?<pet>§[0-9a-fk-or][\\w ]+)§r§a! You can pick it up in (?:(?<day>[\\d]+) day(?:s){0,1} ){0,1}(?:(?<hour>[\\d]+) hour(?:s){0,1} ){0,1}(?:(?<minute>[\\d]+) minute(?:s){0,1} ){0,1}(?:(?<second>[\\d]+) second(?:s){0,1}).§r");
    private static final Pattern DRAGON_DOWN_PATTERN = Pattern.compile("§r +§r§6§l(?<dragon>SUPERIOR|STRONG|YOUNG|OLD|PROTECTOR|UNSTABLE|WISE) DRAGON DOWN!§r");
    private static final Pattern DRAGON_SPAWNED_PATTERN = Pattern.compile("§5☬ §r§d§lThe §r§5§c§l(?<dragon>Superior|Strong|Young|Unstable|Wise|Old|Protector) Dragon§r§d§l has spawned!§r");
    private static final Pattern ESTIMATED_TIME_PATTERN = Pattern.compile("\\((?<time>(?:\\d+d ){0,1}(?:\\d+h ){0,1}(?:\\d+m ){0,1}(?:\\d+s)|(?:\\d+h))\\)");

    // Item Drop Stuff
    private static final String ITEM_PATTERN = "[\\w\\'◆\\[\\] -]+";
    private static final String DROP_PATTERN = "(?<item>(?:§r§[0-9a-fk-or]){0,1}(?:§[0-9a-fk-or]){0,1}" + ITEM_PATTERN + "(?:[\\(][^\\)]" + ITEM_PATTERN + "[\\)]){0,1})";
    private static final Pattern RARE_DROP_PATTERN = Pattern.compile("§r§6§lRARE DROP! " + DROP_PATTERN + "(?:\\b§r\\b){0,1} ?(?:§r§b\\(\\+(?<mf>[0-9]+)% Magic Find!\\)§r){0,1}");
    private static final Pattern RARE_DROP_2_SPACE_PATTERN = Pattern.compile("§r§b§lRARE DROP! §r§7\\(" + DROP_PATTERN + "§r§7\\)(?:\\b§r\\b){0,1} ?(?:§r§b\\(\\+(?<mf>[0-9]+)% Magic Find!\\)§r){0,1}");
    private static final Pattern RARE_DROP_WITH_BRACKET_PATTERN = Pattern.compile("(?<type>§r§9§lVERY RARE|§r§5§lVERY RARE|§r§d§lCRAZY RARE) DROP!  §r§7\\(" + DROP_PATTERN + "§r§7\\)(?:\\b§r\\b){0,1} ?(?:§r§b\\(\\+(?<mf>[0-9]+)% Magic Find!\\)§r){0,1}");
    private static final Pattern BOSS_DROP_PATTERN = Pattern.compile("(?:(?:" + GameProfileUtils.getUsername() + ")|(?:\\[VIP?\\+{0,1}\\]|\\[MVP?\\+{0,2}\\]|\\[YOUTUBE\\]) " + GameProfileUtils.getUsername() + ") has obtained " + DROP_PATTERN + "!");

    // Mythos Events
    private static final Pattern RARE_DROP_MYTHOS_PATTERN = Pattern.compile("RARE DROP! You dug out a " + DROP_PATTERN + "!");
    private static final Pattern COINS_MYTHOS_PATTERN = Pattern.compile("Wow! You dug out (?<coin>[0-9,]+) coins!");

    // Bank Interest/Allowance
    private static final Pattern BANK_INTEREST_PATTERN = Pattern.compile("Since you've been away you earned (?<coin>[0-9,]+) coins");
    private static final Pattern ALLOWANCE_PATTERN = Pattern.compile("ALLOWANCE! You earned (?<coin>[0-9,]+) coins!");

    // Dungeons
    private static final Pattern DUNGEON_QUALITY_DROP_PATTERN = Pattern.compile("You found a Top Quality Item! " + DROP_PATTERN);
    private static final Pattern DUNGEON_REWARD_PATTERN = Pattern.compile(" +RARE REWARD! " + DROP_PATTERN);

    // Fish catch stuff
    private static final Pattern FISH_CATCH_PATTERN = Pattern.compile("(?<type>GOOD|GREAT) CATCH! You found a " + DROP_PATTERN + ".");
    private static final Pattern COINS_CATCH_PATTERN = Pattern.compile("(?<type>GOOD|GREAT) CATCH! You found (?<coin>[0-9,]+) Coins.");

    // Winter island stuff
    private static final Pattern COINS_GIFT_PATTERN = Pattern.compile("(?<type>COMMON|SWEET|RARE)! \\+(?<coin>[0-9,]+) coins gift with " + RANKED_PATTERN + "!");
    private static final Pattern SKILL_EXP_GIFT_PATTERN = Pattern.compile("(?<type>COMMON|SWEET|RARE)! \\+(?<exp>[0-9,]+) (?<skill>Farming|Mining|Combat|Foraging|Fishing|Enchanting|Alchemy)+ XP gift with " + RANKED_PATTERN + "!");
    private static final Pattern ITEM_DROP_GIFT_PATTERN = Pattern.compile("(?<type>COMMON|SWEET|RARE)! " + DROP_PATTERN + " gift with " + RANKED_PATTERN + "!");
    private static final Pattern SANTA_TIER_PATTERN = Pattern.compile("SANTA TIER! " + DROP_PATTERN + " gift with " + RANKED_PATTERN + "!");

    // Pet
    private static final Pattern PET_LEVEL_UP_PATTERN = Pattern.compile("§r§aYour (?<name>§r§[0-9a-fk-or][\\w ]+) §r§alevelled up to level §r§9(?<level>\\d+)§r§a!§r");
    private static final Pattern PET_DROP_PATTERN = Pattern.compile("PET DROP! " + DROP_PATTERN + " ?(?:\\(\\+(?<mf>[0-9]+)% Magic Find!\\)){0,1}");

    private static final Map<String, String> RENAMED_DROP = ImmutableMap.<String, String>builder().put("◆ Ice Rune", "◆ Ice Rune I").build();
    public static boolean isSkyBlock = false;
    public static SBLocation SKY_BLOCK_LOCATION = SBLocation.YOUR_ISLAND;
    public static float dragonHealth;
    private static final List<ToastUtils.ItemDropCheck> ITEM_DROP_CHECK_LIST = new ArrayList<>();
    private List<ItemStack> previousInventory;
    private SBBossBar.DragonType dragonType;
    private final Minecraft mc;
    private boolean initVersionCheck;
    private boolean initApiData;

    public SkyBlockEventHandler()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (this.mc.thePlayer != null && event.phase == TickEvent.Phase.START)
        {
            if (GameProfileUtils.isSteveKunG() && Keyboard.isKeyDown(Keyboard.KEY_F7) && this.mc.currentScreen instanceof GuiContainer)
            {
                GuiContainer container = (GuiContainer)this.mc.currentScreen;
                Slot slot = container.getSlotUnderMouse();

                if (slot != null && slot.getHasStack())
                {
                    ItemStack itemStack = slot.getStack();
                    GuiScreen.setClipboardString("/give @p " + itemStack.getItem().getRegistryName() + " " + 1 + " " + itemStack.getItemDamage() + " " + itemStack.getTagCompound());
                    ClientUtils.printClientMessage(EnumChatFormatting.GREEN + "Copied item data!");
                }
            }
            if (GameProfileUtils.isSteveKunG() && Keyboard.isKeyDown(Keyboard.KEY_F8) && this.mc.currentScreen instanceof GuiContainer)
            {
                GuiContainer container = (GuiContainer)this.mc.currentScreen;
                Slot slot = container.getSlotUnderMouse();

                if (slot != null && slot.getHasStack())
                {
                    ItemStack itemStack = slot.getStack();
                    StringBuilder builder = new StringBuilder();

                    for (int i = 0; i < itemStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8).tagCount(); i++)
                    {
                        String test = itemStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8).getStringTagAt(i);
                        builder.append("list.add(\"");
                        builder.append(test);
                        builder.append("\");");
                        builder.append("\n");
                    }
                    GuiScreen.setClipboardString(builder.toString());
                    ClientUtils.printClientMessage(EnumChatFormatting.GREEN + "Copied lore data!");
                }
            }

            for (Map.Entry<String, Pair<Long, SkyblockProfiles>> entry : SkyBlockProfileSelectorScreen.PROFILE_CACHE.entrySet())
            {
                long now = System.currentTimeMillis();
                long checkedTime = entry.getValue().getLeft();

                if (now - checkedTime > 180000D)
                {
                    SkyBlockProfileSelectorScreen.PROFILE_CACHE.remove(entry.getKey());
                }
            }
            for (Map.Entry<String, Pair<Long, HypixelProfiles>> entry : SkyBlockProfileSelectorScreen.INIT_PROFILE_CACHE.entrySet())
            {
                long now = System.currentTimeMillis();
                long checkedTime = entry.getValue().getLeft();

                if (now - checkedTime > 180000D)
                {
                    SkyBlockProfileSelectorScreen.INIT_PROFILE_CACHE.remove(entry.getKey());
                }
            }

            if (this.mc.thePlayer.ticksExisted % 5 == 0)
            {
                this.getInventoryDifference(this.mc.thePlayer.inventory.mainInventory);
            }
            if (this.mc.theWorld != null)
            {
                boolean found = false;
                boolean foundDrag = false;
                ScoreObjective scoreObj = this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
                Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
                Collection<Score> collection = scoreboard.getSortedScores(scoreObj);
                List<Score> list = Lists.newArrayList(collection.stream().filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")).collect(Collectors.toList()));

                if (list.size() > 15)
                {
                    collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
                }
                else
                {
                    collection = list;
                }

                for (Score score1 : collection)
                {
                    ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score1.getPlayerName());
                    String scoreText = this.keepLettersAndNumbersOnly(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score1.getPlayerName()));

                    if (scoreText.startsWith("Dragon HP: "))
                    {
                        try
                        {
                            SkyBlockEventHandler.dragonHealth = Float.parseFloat(scoreText.replaceAll("[^\\d]", ""));

                            if (this.dragonType != null)
                            {
                                SBBossBar.healthScale = SkyBlockEventHandler.dragonHealth / this.dragonType.getMaxHealth();
                            }
                            foundDrag = true;
                            break;
                        }
                        catch (Exception e) {}
                    }
                }

                for (Score score1 : collection)
                {
                    ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score1.getPlayerName());
                    String scoreText = EnumChatFormatting.getTextWithoutFormattingCodes(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score1.getPlayerName()));

                    for (SBLocation location : SBLocation.values())
                    {
                        if (scoreText.contains("⏣") && this.keepLettersAndNumbersOnly(scoreText).substring(2).equals(location.getLocation()))
                        {
                            SkyBlockEventHandler.SKY_BLOCK_LOCATION = location;
                            found = true;
                            break;
                        }
                    }
                }

                if (scoreObj != null)
                {
                    SkyBlockEventHandler.isSkyBlock = EnumChatFormatting.getTextWithoutFormattingCodes(scoreObj.getDisplayName()).contains("SKYBLOCK");
                    HUDRenderEventHandler.otherPlayerIsland = EnumChatFormatting.getTextWithoutFormattingCodes(scoreObj.getDisplayName()).contains("SKYBLOCK GUEST");
                }
                else
                {
                    SkyBlockEventHandler.isSkyBlock = false;
                }

                if (!found)
                {
                    SkyBlockEventHandler.SKY_BLOCK_LOCATION = SBLocation.NONE;
                }
                SBBossBar.renderBossBar = foundDrag;
            }
        }
    }

    @SubscribeEvent
    public void onMouseClick(MouseEvent event)
    {
        if (InfoUtils.INSTANCE.isHypixel() && event.button == 1 && event.buttonstate && this.mc.pointedEntity instanceof EntityOtherPlayerMP)
        {
            EntityOtherPlayerMP player = (EntityOtherPlayerMP)this.mc.pointedEntity;

            if (!this.mc.thePlayer.isSneaking() && this.mc.thePlayer.getHeldItem() == null && SkyBlockcatiaSettings.INSTANCE.rightClickToAddParty && CommonUtils.getPlayerInfoMap(this.mc.thePlayer.sendQueue).stream().anyMatch(info -> info.getGameProfile().getName().equals(player.getName())))
            {
                this.mc.thePlayer.sendChatMessage("/p " + player.getName());
                event.setCanceled(true);
            }
            if (this.mc.thePlayer.isSneaking() && SkyBlockcatiaSettings.INSTANCE.sneakToTradeOtherPlayerIsland && HUDRenderEventHandler.otherPlayerIsland)
            {
                this.mc.thePlayer.sendChatMessage("/trade " + player.getName());
            }
        }
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event)
    {
        if (event.message == null)
        {
            return;
        }

        String formattedMessage = event.message.getFormattedText();
        String message = event.message.getUnformattedText();
        boolean cancelMessage = false;

        if (this.mc.isSingleplayer() && SkyBlockcatiaMod.isDevelopment)
        {
            formattedMessage = message;
        }

        if (InfoUtils.INSTANCE.isHypixel() || SkyBlockcatiaMod.isDevelopment)
        {
            // Common matcher
            Matcher visitIslandMatcher = SkyBlockEventHandler.VISIT_ISLAND_PATTERN.matcher(message);
            Matcher uuidMatcher = SkyBlockEventHandler.UUID_PATTERN.matcher(message);
            Matcher petCareMatcher = SkyBlockEventHandler.PET_CARE_PATTERN.matcher(formattedMessage);
            Matcher dragonDownMatcher = SkyBlockEventHandler.DRAGON_DOWN_PATTERN.matcher(formattedMessage);
            Matcher dragonSpawnedMatcher = SkyBlockEventHandler.DRAGON_SPAWNED_PATTERN.matcher(formattedMessage);

            // Item Drop matcher
            Matcher rareDropPattern = SkyBlockEventHandler.RARE_DROP_PATTERN.matcher(formattedMessage);
            Matcher bossDropPattern = SkyBlockEventHandler.BOSS_DROP_PATTERN.matcher(message);

            // Mythos Events matcher
            Matcher rareDropMythosPattern = SkyBlockEventHandler.RARE_DROP_MYTHOS_PATTERN.matcher(message);
            Matcher coinsMythosPattern = SkyBlockEventHandler.COINS_MYTHOS_PATTERN.matcher(message);

            // Bank Interest/Allowance
            Matcher bankInterestPattern = SkyBlockEventHandler.BANK_INTEREST_PATTERN.matcher(message);
            Matcher allowancePattern = SkyBlockEventHandler.ALLOWANCE_PATTERN.matcher(message);

            // Dungeons matcher
            Matcher dungeonQualityDropPattern = SkyBlockEventHandler.DUNGEON_QUALITY_DROP_PATTERN.matcher(message);
            Matcher dungeonRewardPattern = SkyBlockEventHandler.DUNGEON_REWARD_PATTERN.matcher(message);

            // Fish catch matcher
            Matcher fishCatchPattern = SkyBlockEventHandler.FISH_CATCH_PATTERN.matcher(message);
            Matcher coinsCatchPattern = SkyBlockEventHandler.COINS_CATCH_PATTERN.matcher(message);

            // Slayer Drop matcher
            Matcher rareDropBracketPattern = SkyBlockEventHandler.RARE_DROP_WITH_BRACKET_PATTERN.matcher(formattedMessage);
            Matcher rareDrop2SpaceBracketPattern = SkyBlockEventHandler.RARE_DROP_2_SPACE_PATTERN.matcher(formattedMessage);

            // Gift matcher
            Matcher coinsGiftPattern = SkyBlockEventHandler.COINS_GIFT_PATTERN.matcher(message);
            Matcher skillExpGiftPattern = SkyBlockEventHandler.SKILL_EXP_GIFT_PATTERN.matcher(message);
            Matcher itemDropGiftPattern = SkyBlockEventHandler.ITEM_DROP_GIFT_PATTERN.matcher(message);
            Matcher santaTierPattern = SkyBlockEventHandler.SANTA_TIER_PATTERN.matcher(message);

            // Pet
            Matcher petLevelUpPattern = SkyBlockEventHandler.PET_LEVEL_UP_PATTERN.matcher(formattedMessage);
            Matcher petDropPattern = SkyBlockEventHandler.PET_DROP_PATTERN.matcher(message);

            if (event.type == 0 || this.mc.isSingleplayer() && SkyBlockcatiaMod.isDevelopment)
            {
                if (visitIslandMatcher.matches())
                {
                    String name = visitIslandMatcher.group("name");

                    if (ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.visitIslandToastMode) == ToastMode.TOAST || ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.visitIslandToastMode) == ToastMode.CHAT_AND_TOAST)
                    {
                        SkyBlockEventHandler.addVisitingToast(name);
                        LoggerIN.logToast(formattedMessage);
                    }
                    cancelMessage = ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.visitIslandToastMode) == ToastMode.TOAST || ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.visitIslandToastMode) == ToastMode.DISABLED;
                }
                else if (uuidMatcher.matches())
                {
                    SBAPIUtils.setApiKeyFromServer(uuidMatcher.group("uuid"));
                    ClientUtils.printClientMessage("Setting a new API Key!", JsonUtils.green());
                }
                else if (petCareMatcher.matches())
                {
                    int day = 0;
                    int hour = 0;
                    int minute = 0;
                    int second = 0;

                    if (petCareMatcher.group("day") != null)
                    {
                        day = Integer.parseInt(petCareMatcher.group("day"));
                    }
                    if (petCareMatcher.group("hour") != null)
                    {
                        hour = Integer.parseInt(petCareMatcher.group("hour"));
                    }
                    if (petCareMatcher.group("minute") != null)
                    {
                        minute = Integer.parseInt(petCareMatcher.group("minute"));
                    }
                    if (petCareMatcher.group("second") != null)
                    {
                        second = Integer.parseInt(petCareMatcher.group("second"));
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, day);
                    calendar.add(Calendar.HOUR, hour);
                    calendar.add(Calendar.MINUTE, minute);
                    calendar.add(Calendar.SECOND, second);
                    String date1 = new SimpleDateFormat("d MMMMM yyyy", Locale.ROOT).format(calendar.getTime());
                    String date2 = new SimpleDateFormat("h:mm:ss a", Locale.ROOT).format(calendar.getTime());
                    ClientUtils.printClientMessage(JsonUtils.create(petCareMatcher.group("pet") + EnumChatFormatting.GREEN + " will be finished on " + date1 + " " + date2).setChatStyle(JsonUtils.green()));
                    cancelMessage = true;
                }

                if (SkyBlockcatiaSettings.INSTANCE.leavePartyWhenLastEyePlaced && message.contains(" Brace yourselves! (8/8)"))
                {
                    this.mc.thePlayer.sendChatMessage("/p leave");
                }

                if (SkyBlockcatiaSettings.INSTANCE.disableSuperboomNotification && message.contains("Your Superboom TNT is ineffective"))
                {
                     cancelMessage = true;
                }

                if (SkyBlockEventHandler.isSkyBlock || SkyBlockcatiaMod.isDevelopment)
                {
                    if (dragonDownMatcher.matches())
                    {
                        this.clearBossData();

                        if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.showHitboxWhenDragonSpawned)
                        {
                            this.mc.getRenderManager().setDebugBoundingBox(false);
                        }
                    }
                    if (dragonSpawnedMatcher.matches())
                    {
                        String dragon = dragonSpawnedMatcher.group("dragon");
                        SBBossBar.DragonType type = SBBossBar.DragonType.valueOf(dragon.toUpperCase(Locale.ROOT));
                        SBBossBar.renderBossBar = true;
                        SBBossBar.bossName = EnumChatFormatting.RED + type.getName();
                        HUDRenderEventHandler.foundDragon = true;
                        this.dragonType = type;

                        if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.showHitboxWhenDragonSpawned)
                        {
                            this.mc.getRenderManager().setDebugBoundingBox(true);
                        }
                    }

                    if (bankInterestPattern.find())
                    {
                        String coin = bankInterestPattern.group("coin");
                        ItemStack coinSkull = RenderUtils.getSkullItemStack(CoinType.TYPE_3.getId(), CoinType.TYPE_3.getValue());
                        NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), ToastUtils.DropType.BANK_INTEREST, Integer.parseInt(coin.replace(",", "")), coinSkull, ToastUtils.DropType.BANK_INTEREST);
                        LoggerIN.logToast(formattedMessage);
                        cancelMessage = true;
                    }
                    else if (allowancePattern.matches())
                    {
                        String coin = allowancePattern.group("coin");
                        ItemStack coinSkull = RenderUtils.getSkullItemStack(CoinType.TYPE_3.getId(), CoinType.TYPE_3.getValue());
                        NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), ToastUtils.DropType.ALLOWANCE, Integer.parseInt(coin.replace(",", "")), coinSkull, ToastUtils.DropType.ALLOWANCE);
                        LoggerIN.logToast(formattedMessage);
                        cancelMessage = true;
                    }

                    if (ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.fishCatchToastMode) == ToastMode.TOAST || ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.fishCatchToastMode) == ToastMode.CHAT_AND_TOAST)
                    {
                        if (fishCatchPattern.matches())
                        {
                            String dropType = fishCatchPattern.group("type");
                            String name = fishCatchPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, dropType.equals("GOOD") ? ToastUtils.DropType.GOOD_CATCH : ToastUtils.DropType.GREAT_CATCH, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.fishCatchToastMode) == ToastMode.TOAST;
                        }
                        else if (coinsCatchPattern.matches())
                        {
                            String type = coinsCatchPattern.group("type");
                            String coin = coinsCatchPattern.group("coin");
                            CoinType coinType = type.equals("GOOD") ? CoinType.TYPE_1 : CoinType.TYPE_2;
                            ItemStack coinSkull = RenderUtils.getSkullItemStack(coinType.getId(), coinType.getValue());
                            NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), type.equals("GOOD") ? ToastUtils.DropType.GOOD_CATCH_COINS : ToastUtils.DropType.GREAT_CATCH_COINS, Integer.parseInt(coin.replace(",", "")), coinSkull, type + "$" + coinType);
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.fishCatchToastMode) == ToastMode.TOAST;
                        }
                    }

                    if (ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.giftToastMode) == ToastMode.TOAST || ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.giftToastMode) == ToastMode.CHAT_AND_TOAST)
                    {
                        if (coinsGiftPattern.matches())
                        {
                            String type = coinsGiftPattern.group("type");
                            String coin = coinsGiftPattern.group("coin");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), rarity, Integer.parseInt(coin.replace(",", "")), RenderUtils.getSkullItemStack(CoinType.TYPE_1.getId(), CoinType.TYPE_1.getValue()), rarity);
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.giftToastMode) == ToastMode.TOAST;
                        }
                        else if (skillExpGiftPattern.matches())
                        {
                            String type = skillExpGiftPattern.group("type");
                            String exp = skillExpGiftPattern.group("exp");
                            String skill = skillExpGiftPattern.group("skill");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            SBSkills.Type skillType = SBSkills.Type.byName(skill);
                            NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), rarity, Integer.parseInt(exp.replace(",", "")), skillType.getItemStack().setStackDisplayName(skillType.getName()), type + "$" + skillType);
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.giftToastMode) == ToastMode.TOAST;
                        }
                        else if (itemDropGiftPattern.matches())
                        {
                            String type = itemDropGiftPattern.group("type");
                            String name = itemDropGiftPattern.group("item");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, rarity, ToastUtils.ToastType.GIFT));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.giftToastMode) == ToastMode.TOAST;
                        }
                        else if (santaTierPattern.matches())
                        {
                            String name = santaTierPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.SANTA_TIER, ToastUtils.ToastType.GIFT));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.giftToastMode) == ToastMode.TOAST;
                        }
                    }

                    if (ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.rareDropToastMode) == ToastMode.TOAST || ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.rareDropToastMode) == ToastMode.CHAT_AND_TOAST)
                    {
                        boolean isToast = ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.rareDropToastMode) == ToastMode.TOAST;

                        if (message.contains("You destroyed an Ender Crystal!"))
                        {
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck("Crystal Fragment", ToastUtils.DropType.DRAGON_CRYSTAL_FRAGMENT, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }

                        if (rareDropPattern.matches())
                        {
                            String name = rareDropPattern.group("item");
                            String magicFind = rareDropPattern.group("mf");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, ToastUtils.DropType.RARE_DROP, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (rareDropMythosPattern.matches())
                        {
                            String name = rareDropMythosPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.RARE_DROP, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (coinsMythosPattern.matches())
                        {
                            String coin = coinsMythosPattern.group("coin");
                            ItemStack coinSkull = RenderUtils.getSkullItemStack(CoinType.TYPE_1.getId(), CoinType.TYPE_1.getValue());
                            NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), ToastUtils.DropType.MYTHOS_COINS, Integer.parseInt(coin.replace(",", "")), coinSkull, ToastUtils.DropType.MYTHOS_COINS);
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (bossDropPattern.matches())
                        {
                            String name = bossDropPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.BOSS_DROP, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (dungeonQualityDropPattern.matches())
                        {
                            String name = dungeonQualityDropPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.DUNGEON_QUALITY_DROP, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (dungeonRewardPattern.matches())
                        {
                            String name = dungeonRewardPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.DUNGEON_REWARD_DROP, ToastType.DROP));
                        }
                        else if (rareDropBracketPattern.matches())
                        {
                            String type = rareDropBracketPattern.group("type");
                            String name = rareDropBracketPattern.group("item");
                            String magicFind = rareDropBracketPattern.group(3);
                            ToastUtils.DropType dropType = type.startsWith("\u00a7r\u00a79\u00a7lVERY RARE") ? ToastUtils.DropType.SLAYER_VERY_RARE_DROP_BLUE : type.startsWith("\u00a7r\u00a75\u00a7lVERY RARE") ? ToastUtils.DropType.SLAYER_VERY_RARE_DROP_PURPLE : ToastUtils.DropType.SLAYER_CRAZY_RARE_DROP;
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, dropType, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (rareDrop2SpaceBracketPattern.matches())
                        {
                            String name = rareDrop2SpaceBracketPattern.group("item");
                            String magicFind = rareDrop2SpaceBracketPattern.group(2);
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, ToastUtils.DropType.SLAYER_RARE_DROP, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                    }

                    if (ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.petToastMode) == ToastMode.TOAST || ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.petToastMode) == ToastMode.CHAT_AND_TOAST)
                    {
                        boolean isToast = ToastMode.byId(SkyBlockcatiaSettings.INSTANCE.petToastMode) == ToastMode.TOAST;

                        if (petLevelUpPattern.matches())
                        {
                            String name = petLevelUpPattern.group("name");
                            String level = petLevelUpPattern.group("level");
                            SBPets.Type type = SBPets.PETS.getTypeByName(EnumChatFormatting.getTextWithoutFormattingCodes(name).replace(" ", "_").toUpperCase(Locale.ROOT));
                            ItemStack itemStack = type.getPetItem();
                            itemStack.setStackDisplayName(name);
                            NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), ToastUtils.DropType.PET_LEVEL_UP, Integer.parseInt(level), itemStack, true, type);
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (petDropPattern.matches())
                        {
                            String name = petDropPattern.group("item");
                            String magicFind = petDropPattern.group("mf");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, ToastUtils.DropType.PET_DROP, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                    }
                }
                event.setCanceled(cancelMessage);
            }
        }
    }

    @SubscribeEvent
    public void onPressKey(InputEvent.KeyInputEvent event)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (KeyBindingsSB.KEY_SB_ENDER_CHEST.isKeyDown())
            {
                this.mc.thePlayer.sendChatMessage("/enderchest");
            }
            else if (KeyBindingsSB.KEY_SB_CRAFTED_MINIONS.isKeyDown())
            {
                this.mc.thePlayer.sendChatMessage("/craftedgenerators");
            }
            else if (KeyBindingsSB.KEY_SB_CRAFTING_TABLE.isKeyDown())
            {
                this.mc.thePlayer.sendChatMessage("/craft");
            }
            else if (KeyBindingsSB.KEY_SB_MENU.isKeyDown())
            {
                this.mc.thePlayer.sendChatMessage("/sbmenu");
            }
            else if (KeyBindingsSB.KEY_SB_VIEW_RECIPE.isKeyDown() && this.mc.currentScreen == null && !Keyboard.isKeyDown(Keyboard.KEY_F3))
            {
                this.mc.thePlayer.sendChatMessage("/recipes");
            }
            else if (KeyBindingsSB.KEY_SB_PETS.isKeyDown())
            {
                this.mc.thePlayer.sendChatMessage("/pets");
            }
            else if (KeyBindingsSB.KEY_SB_WARDROBE.isKeyDown())
            {
                this.mc.thePlayer.sendChatMessage("/wardrobe");
            }
            else if (KeyBindingsSB.KEY_SB_HOTM.isKeyDown())
            {
                this.mc.thePlayer.sendChatMessage("/hotm");
            }
        }

        if (KeyBindingsSB.KEY_SB_API_VIEWER.isKeyDown())
        {
            if (StringUtils.isNullOrEmpty(SkyBlockcatiaConfig.hypixelApiKey))
            {
                ClientUtils.printClientMessage("Couldn't open API Viewer, Empty API Key in the Config!", JsonUtils.red());
                ClientUtils.printClientMessage(JsonUtils.create("Make sure you're in the Hypixel!").setChatStyle(JsonUtils.yellow()).appendSibling(JsonUtils.create(" Click Here to create an API key").setChatStyle(JsonUtils.gold().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/api new")))));
                return;
            }
            if (!SkyBlockcatiaConfig.hypixelApiKey.matches(SkyBlockEventHandler.UUID_PATTERN_STRING))
            {
                ClientUtils.printClientMessage("Invalid UUID for Hypixel API Key!", JsonUtils.red());
                ClientUtils.printClientMessage("Example UUID pattern: " + UUID.randomUUID(), JsonUtils.yellow());
                return;
            }
            if (this.mc.pointedEntity instanceof EntityOtherPlayerMP)
            {
                EntityOtherPlayerMP player = (EntityOtherPlayerMP)this.mc.pointedEntity;

                if (CommonUtils.getPlayerInfoMap(this.mc.thePlayer.sendQueue).stream().anyMatch(info -> info.getGameProfile().getName().equals(player.getName())))
                {
                    this.mc.displayGuiScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.GuiState.PLAYER, player.getDisplayNameString(), "", ""));
                }
                else
                {
                    this.mc.displayGuiScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.GuiState.EMPTY));
                }
            }
            else
            {
                this.mc.displayGuiScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.GuiState.EMPTY));
            }
        }
    }

    @SubscribeEvent
    public void onWorldJoin(EntityJoinWorldEvent event)
    {
        if (event.entity == this.mc.thePlayer)
        {
            this.previousInventory = null;
            this.clearBossData();
            ITEM_DROP_CHECK_LIST.clear();

            if (!this.initVersionCheck)
            {
                SkyBlockcatiaMod.CHECKER.startCheckIfFailed();
                SkyBlockcatiaMod.CHECKER.printInfo(this.mc.thePlayer);
                this.initVersionCheck = true;
            }
            if (!this.initApiData && InfoUtils.INSTANCE.isHypixel())
            {
                ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
                exec.scheduleAtFixedRate(MainEventHandler::getBazaarData, 0, 10, TimeUnit.SECONDS);
                ScheduledExecutorService exec1 = Executors.newSingleThreadScheduledExecutor();
                exec1.scheduleAtFixedRate(PetStats::scheduleDownloadPetStats, 0, 2, TimeUnit.MINUTES);
                this.initApiData = true;
            }
        }
    }

    @SubscribeEvent
    public void onDisconnectedFromServerEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        SignSelectionList.clearAll();
        this.clearBossData();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemTooltip(ItemTooltipEvent event)
    {
        try
        {
            Calendar calendar = Calendar.getInstance();
            ItemStack itemStack = event.itemStack;

            if (itemStack.hasTagCompound())
            {
                NBTTagCompound extraAttrib = itemStack.getTagCompound().getCompoundTag("ExtraAttributes");
                ModDecimalFormat format = new ModDecimalFormat("#,###.#");
                int insertAt = event.toolTip.size();
                insertAt--; // rarity

                if (this.mc.gameSettings.advancedItemTooltips)
                {
                    insertAt -= 2; // item name + nbt

                    if (itemStack.isItemDamaged())
                    {
                        insertAt--; // 1 damage
                    }
                }

                if (SkyBlockcatiaSettings.INSTANCE.showObtainedDate && extraAttrib.hasKey("timestamp"))
                {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[MM/dd/yy h:mm a][M/dd/yy h:mm a][M/d/yy h:mm a]", Locale.ENGLISH);
                    LocalDateTime datetime = LocalDateTime.parse(extraAttrib.getString("timestamp"), formatter);
                    Date convertedDatetime = Date.from(datetime.atZone(ZoneId.systemDefault()).toInstant());
                    String formatted = new SimpleDateFormat("d MMMM yyyy h:mm aa", Locale.ROOT).format(convertedDatetime);
                    event.toolTip.add(insertAt++, EnumChatFormatting.GRAY + "Obtained: " + EnumChatFormatting.RESET + formatted);
                }
                if (SkyBlockcatiaSettings.INSTANCE.bazaarOnTooltips)
                {
                    for (Map.Entry<String, Bazaar.Data> entry : MainEventHandler.BAZAAR_DATA.entrySet())
                    {
                        Bazaar.Status status = entry.getValue().getStatus();

                        if (extraAttrib.getString("id").equals(entry.getKey()))
                        {
                            if (ClientUtils.isShiftKeyDown())
                            {
                                if (StringUtils.isNullOrEmpty(SkyBlockcatiaConfig.hypixelApiKey))
                                {
                                    event.toolTip.add(insertAt++, EnumChatFormatting.RED + "Couldn't get bazaar data, Empty API Key in the Config!");
                                }
                                else if (!SkyBlockcatiaConfig.hypixelApiKey.matches(SkyBlockEventHandler.UUID_PATTERN_STRING))
                                {
                                    event.toolTip.add(insertAt++, EnumChatFormatting.RED + "Invalid UUID for Hypixel API Key!");
                                }
                                else
                                {
                                    double buyStack = 64 * status.getBuyPrice();
                                    double sellStack = 64 * status.getSellPrice();
                                    double buyCurrent = itemStack.stackSize * status.getBuyPrice();
                                    double sellCurrent = itemStack.stackSize * status.getSellPrice();
                                    event.toolTip.add(insertAt++, "Buy/Sell (Stack): " + EnumChatFormatting.GOLD + format.format(buyStack) + EnumChatFormatting.YELLOW + "/" + EnumChatFormatting.GOLD + format.format(sellStack) + " coins");

                                    if (itemStack.stackSize > 1 && itemStack.stackSize < 64)
                                    {
                                        event.toolTip.add(insertAt++, "Buy/Sell (Current): " + EnumChatFormatting.GOLD + format.format(buyCurrent) + EnumChatFormatting.YELLOW + "/" + EnumChatFormatting.GOLD + format.format(sellCurrent) + " coins");
                                    }

                                    event.toolTip.add(insertAt++, "Buy/Sell (One): " + EnumChatFormatting.GOLD + format.format(status.getBuyPrice()) + EnumChatFormatting.YELLOW + "/" + EnumChatFormatting.GOLD + format.format(status.getSellPrice()) + " coins");
                                    event.toolTip.add(insertAt++, "Last Updated: " + EnumChatFormatting.WHITE + CommonUtils.getRelativeTime(entry.getValue().getLastUpdated()));
                                }
                            }
                            else
                            {
                                event.toolTip.add(insertAt++, "Press <SHIFT> to view Bazaar Buy/Sell");
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < event.toolTip.size(); i++)
            {
                String lore = EnumChatFormatting.getTextWithoutFormattingCodes(event.toolTip.get(i));

                if (this.mc.currentScreen instanceof GuiChest)
                {
                    GuiChest chest = (GuiChest)this.mc.currentScreen;
                    String name = chest.lowerChestInventory.getDisplayName().getUnformattedText();

                    if (name.equals("SkyBlock Menu"))
                    {
                        SkyBlockEventHandler.replaceText(lore, calendar, event.toolTip, i, "Ends in:", "Ends at:");
                        SkyBlockEventHandler.replaceText(lore, calendar, event.toolTip, i, "Starting in:", "Starts at:");
                    }
                    else if (name.equals("Community Shop"))
                    {
                        SkyBlockEventHandler.replaceText(lore, calendar, event.toolTip, i, "Starts in:", "Starts at:");
                    }
                    else
                    {
                        SkyBlockEventHandler.replaceText(lore, calendar, event.toolTip, i, "Starts in:", "Event starts at:");
                    }

                    if (!name.equals("SkyBlock Menu"))
                    {
                        SkyBlockEventHandler.replaceEstimatedTime(lore, event.toolTip, i);
                        SkyBlockEventHandler.replaceAuctionTime(lore, calendar, event.toolTip, i, "Ends in: ");
                    }
                }

                SkyBlockEventHandler.replaceText(lore, calendar, event.toolTip, i, "Time Remaining:", "Finished on:");
                SkyBlockEventHandler.replaceText(lore, calendar, event.toolTip, i, "Time left:", "Finished on:");
                SkyBlockEventHandler.replaceText(lore, calendar, event.toolTip, i, "Available:", "Available on:");
                SkyBlockEventHandler.replaceBankInterestTime(lore, calendar, event.toolTip, i, "Interest in: ");
                SkyBlockEventHandler.replaceBankInterestTime(lore, calendar, event.toolTip, i, "Until interest: ");
            }
        }
        catch (Exception e) {}
    }

    private String keepLettersAndNumbersOnly(String text)
    {
        return LETTERS_NUMBERS.matcher(EnumChatFormatting.getTextWithoutFormattingCodes(text)).replaceAll("");
    }

    /**
     * Credit to codes.biscuit.skyblockaddons.utils.InventoryUtils
     */
    private void getInventoryDifference(ItemStack[] currentInventory)
    {
        List<ItemStack> newInventory = this.copyInventory(currentInventory);
        Map<String, ItemDropDiff> previousInventoryMap = Maps.newConcurrentMap();
        Map<String, ItemDropDiff> newInventoryMap = Maps.newConcurrentMap();
        SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.removeIf(this::removeUndisplayedToast);

        if (this.previousInventory != null)
        {
            for (int i = 0; i < newInventory.size(); i++)
            {
                ItemStack previousItem = this.previousInventory.get(i);
                ItemStack newItem = newInventory.get(i);

                if (previousItem != null)
                {
                    int amount;

                    if (previousInventoryMap.containsKey(previousItem.getDisplayName()))
                    {
                        amount = previousInventoryMap.get(previousItem.getDisplayName()).count + previousItem.stackSize;
                    }
                    else
                    {
                        amount = previousItem.stackSize;
                    }

                    NBTTagCompound extraAttributes = previousItem.getSubCompound("ExtraAttributes", false);

                    if (extraAttributes != null)
                    {
                        extraAttributes = (NBTTagCompound) extraAttributes.copy();
                    }
                    previousInventoryMap.put(previousItem.getDisplayName(), new ItemDropDiff(previousItem, amount, extraAttributes));
                }

                if (newItem != null)
                {
                    int amount;

                    if (newInventoryMap.containsKey(newItem.getDisplayName()))
                    {
                        amount = newInventoryMap.get(newItem.getDisplayName()).count + newItem.stackSize;
                    }
                    else
                    {
                        amount = newItem.stackSize;
                    }

                    NBTTagCompound extraAttributes = newItem.getSubCompound("ExtraAttributes", false);

                    if (extraAttributes != null)
                    {
                        extraAttributes = (NBTTagCompound) extraAttributes.copy();
                    }
                    newInventoryMap.put(newItem.getDisplayName(), new ItemDropDiff(newItem, amount, extraAttributes));
                }
            }

            Set<String> keySet = new HashSet<>(previousInventoryMap.keySet());
            keySet.addAll(newInventoryMap.keySet());

            keySet.forEach(key ->
            {
                int previousAmount = 0;

                if (previousInventoryMap.containsKey(key))
                {
                    previousAmount = previousInventoryMap.get(key).count;
                }

                int newAmount = 0;

                if (newInventoryMap.containsKey(key))
                {
                    newAmount = newInventoryMap.get(key).count;
                }

                ItemDropDiff newDiff = newInventoryMap.getOrDefault(key, previousInventoryMap.get(key));
                int diff = newAmount - previousAmount;

                if (diff != 0)
                {
                    ItemStack newItem = newDiff.itemStack;

                    if (newItem != null)
                    {
                        for (Iterator<ToastUtils.ItemDropCheck> iterator = SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.iterator(); iterator.hasNext();)
                        {
                            ToastUtils.ItemDropCheck drop = iterator.next();
                            String dropName = drop.getName();

                            if (drop.getType() == ToastUtils.DropType.PET_DROP)
                            {
                                if (("[Lvl 1] " + dropName).equals(EnumChatFormatting.getTextWithoutFormattingCodes(key)))
                                {
                                    newItem.stackSize = diff;

                                    if (HUDRenderEventHandler.INSTANCE.getToastGui().add(new ItemDropsToast(newItem, drop.getType(), drop.getMagicFind())))
                                    {
                                        iterator.remove();
                                    }
                                }
                            }
                            else
                            {
                                dropName = RENAMED_DROP.getOrDefault(dropName, dropName);

                                if (dropName.equals("\u00a7fEnchanted Book"))
                                {
                                    dropName = "\u00a79Enchanted Book";
                                }

                                if (dropName.equals(key) || !drop.getType().matches(ToastUtils.DropCondition.FORMAT) && dropName.equals(EnumChatFormatting.getTextWithoutFormattingCodes(key)))
                                {
                                    newItem.stackSize = diff;

                                    if (drop.getToastType() == ToastType.DROP)
                                    {
                                        if (HUDRenderEventHandler.INSTANCE.getToastGui().add(new ItemDropsToast(newItem, drop.getType(), drop.getMagicFind())))
                                        {
                                            iterator.remove();
                                        }
                                    }
                                    else
                                    {
                                        if (HUDRenderEventHandler.INSTANCE.getToastGui().add(new GiftToast(newItem, drop.getType(), drop.getType() == ToastUtils.DropType.SANTA_TIER)))
                                        {
                                            iterator.remove();
                                        }
                                    }
                                }
                                else if (drop.getType().matches(ToastUtils.DropCondition.CONTAINS) && key.contains(dropName) && HUDRenderEventHandler.INSTANCE.getToastGui().add(new ItemDropsToast(newItem, drop.getType(), drop.getMagicFind())))
                                {
                                    iterator.remove();
                                }
                            }
                        }
                    }
                }
            });
        }
        this.previousInventory = newInventory;
    }

    private boolean removeUndisplayedToast(ToastUtils.ItemDropCheck drop)
    {
        if (System.currentTimeMillis() > drop.getTimestamp() + 10000L)
        {
            LoggerIN.logToast("You got " + drop.getName() + " but it doesn't show on toast!");
            return true;
        }
        return false;
    }

    private void clearBossData()
    {
        SBBossBar.renderBossBar = false;
        SBBossBar.bossName = null;
        SBBossBar.healthScale = 0;
        SkyBlockEventHandler.dragonHealth = 0;
        this.dragonType = null;
        HUDRenderEventHandler.foundDragon = false;
    }

    private List<ItemStack> copyInventory(ItemStack[] inventory)
    {
        List<ItemStack> copy = new ArrayList<>(inventory.length);

        for (ItemStack item : inventory)
        {
            copy.add(item != null ? ItemStack.copyItemStack(item) : null);
        }
        return copy;
    }

    private static void addVisitingToast(String name)
    {
        CommonUtils.runAsync(() -> HUDRenderEventHandler.INSTANCE.getToastGui().add(new VisitIslandToast(name)));
    }

    private static void replaceText(String lore, Calendar calendar, List<String> tooltip, int i, String startWith, String newText)
    {
        if (lore.startsWith(startWith))
        {
            lore = lore.substring(lore.indexOf(":") + 2).replaceAll("[^a-zA-Z0-9 ]|^[a-zA-Z ]+", "");
            String[] timeEstimate = Arrays.stream(lore.split(" ")).map(time -> time.replaceAll("[^0-9]+", "")).toArray(size -> new String[size]);

            int dayF = 0;
            int hourF = 0;
            int minuteF = 0;
            int secondF = 0;

            if (timeEstimate.length == 2)
            {
                minuteF = Integer.parseInt(timeEstimate[0]);
                secondF = Integer.parseInt(timeEstimate[1]);
            }
            else if (timeEstimate.length == 3)
            {
                hourF = Integer.parseInt(timeEstimate[0]);
                minuteF = Integer.parseInt(timeEstimate[1]);
                secondF = Integer.parseInt(timeEstimate[2]);
            }
            else
            {
                dayF = Integer.parseInt(timeEstimate[0]);
                hourF = Integer.parseInt(timeEstimate[1]);
                minuteF = Integer.parseInt(timeEstimate[2]);
                secondF = Integer.parseInt(timeEstimate[3]);
            }

            calendar.add(Calendar.DATE, dayF);
            calendar.add(Calendar.HOUR, hourF);
            calendar.add(Calendar.MINUTE, minuteF);
            calendar.add(Calendar.SECOND, secondF);
            String date1 = new SimpleDateFormat("EEEE h:mm:ss a", Locale.ROOT).format(calendar.getTime());
            String date2 = new SimpleDateFormat("d MMMMM yyyy", Locale.ROOT).format(calendar.getTime());
            List<String> newStrings = Lists.newArrayList();
            newStrings.add(newText);
            newStrings.add(EnumChatFormatting.YELLOW + date1);
            newStrings.add(EnumChatFormatting.YELLOW + date2);

            if (!GuiScreen.isShiftKeyDown())
            {
                tooltip.add(i + 1, "Press <SHIFT> to view exact time");
            }
            else
            {
                tooltip.remove(i);

                for (String text : newStrings)
                {
                    tooltip.add(i++, text);
                }
            }
        }
    }

    private static void replaceEstimatedTime(String lore, List<String> tooltip, int i)
    {
        Matcher mat = ESTIMATED_TIME_PATTERN.matcher(lore);

        if (mat.find())
        {
            lore = mat.group("time");
            Calendar calendar = Calendar.getInstance();
            String[] timeEstimate = Arrays.stream(lore.split(" ")).map(time -> time.replaceAll("[^0-9]+", "")).toArray(size -> new String[size]);
            boolean isHour = lore.endsWith("h");

            int dayF = 0;
            int hourF = 0;
            int minuteF = 0;
            int secondF = 0;

            if (timeEstimate.length == 2)
            {
                minuteF = Integer.parseInt(timeEstimate[0]);
                secondF = Integer.parseInt(timeEstimate[1]);
            }
            else if (timeEstimate.length == 3)
            {
                hourF = Integer.parseInt(timeEstimate[0]);
                minuteF = Integer.parseInt(timeEstimate[1]);
                secondF = Integer.parseInt(timeEstimate[2]);
            }
            else
            {
                if (timeEstimate.length == 1)
                {
                    if (isHour)
                    {
                        hourF = Integer.parseInt(timeEstimate[0]);
                    }
                }
                else
                {
                    dayF = Integer.parseInt(timeEstimate[0]);
                    hourF = Integer.parseInt(timeEstimate[1]);
                    minuteF = Integer.parseInt(timeEstimate[2]);
                    secondF = Integer.parseInt(timeEstimate[3]);
                }
            }

            calendar.add(Calendar.DATE, dayF);
            calendar.add(Calendar.HOUR, hourF);
            calendar.add(Calendar.MINUTE, minuteF);
            calendar.add(Calendar.SECOND, secondF);
            String date1 = new SimpleDateFormat("EEEE h:mm:ss a", Locale.ROOT).format(calendar.getTime());

            if (timeEstimate.length == 1)
            {
                date1 = new SimpleDateFormat("EEEE h:00 a", Locale.ROOT).format(calendar.getTime());
            }

            String date2 = new SimpleDateFormat("d MMMMM yyyy", Locale.ROOT).format(calendar.getTime());

            if (!GuiScreen.isShiftKeyDown())
            {
                tooltip.add(i + 1, "Press <SHIFT> to view exact time");
            }
            else
            {
                tooltip.add(i + 1, date2 + " " + date1);
            }
        }
    }

    private static void replaceBankInterestTime(String lore, Calendar calendar, List<String> tooltip, int i, String startWith)
    {
        if (lore.startsWith(startWith))
        {
            lore = lore.substring(lore.indexOf(":") + 2).replaceAll("[^0-9]+", " ");
            String[] timeEstimate = Arrays.stream(lore.split(" ")).map(time -> time.replaceAll("[^0-9]+", "")).toArray(size -> new String[size]);
            int hourF = 0;
            int minuteF = 0;
            int secondF = 0;

            if (timeEstimate.length == 1)
            {
                hourF = Integer.parseInt(timeEstimate[0]);
            }
            else if (timeEstimate.length == 2)
            {
                minuteF = Integer.parseInt(timeEstimate[0]);
                secondF = Integer.parseInt(timeEstimate[1]);
            }
            else
            {
                hourF = Integer.parseInt(timeEstimate[0]);
                minuteF = Integer.parseInt(timeEstimate[1]);
                secondF = Integer.parseInt(timeEstimate[2]);
            }

            calendar.add(Calendar.HOUR, hourF);
            calendar.add(Calendar.MINUTE, minuteF);
            calendar.add(Calendar.SECOND, secondF);
            String date1 = new SimpleDateFormat("EEEE h:mm:ss a", Locale.ROOT).format(calendar.getTime());

            if (timeEstimate.length == 1)
            {
                date1 = new SimpleDateFormat("EEEE h:00 a", Locale.ROOT).format(calendar.getTime());
            }

            String date2 = new SimpleDateFormat("d MMMMM yyyy", Locale.ROOT).format(calendar.getTime());
            List<String> newStrings = Lists.newArrayList();
            newStrings.add("Interest receive at: ");
            newStrings.add(EnumChatFormatting.YELLOW + date1);
            newStrings.add(EnumChatFormatting.YELLOW + date2);

            if (!GuiScreen.isShiftKeyDown())
            {
                tooltip.add(i + 1, "Press <SHIFT> to view exact time");
            }
            else
            {
                tooltip.remove(i);

                for (String text : newStrings)
                {
                    tooltip.add(i++, text);
                }
            }
        }
    }

    private static void replaceAuctionTime(String lore, Calendar calendar, List<String> tooltip, int i, String startWith)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (lore.startsWith(startWith))
        {
            boolean isDay = lore.endsWith("d");
            lore = lore.substring(lore.indexOf(":") + 2).replaceAll("[^0-9]+", " ");
            String[] timeEstimate = Arrays.stream(lore.split(" ")).map(time -> time.replaceAll("[^0-9]+", "")).toArray(size -> new String[size]);
            int dayF = 0;
            int hourF = 0;
            int minuteF = 0;
            int secondF = 0;
            List<String> newStrings = Lists.newArrayList();

            if (timeEstimate.length == 1)
            {
                if (isDay)
                {
                    dayF = Integer.parseInt(timeEstimate[0]);
                }
                else
                {
                    hourF = Integer.parseInt(timeEstimate[0]);
                }
            }
            else if (timeEstimate.length == 2)
            {
                minuteF = Integer.parseInt(timeEstimate[0]);
                secondF = Integer.parseInt(timeEstimate[1]);
            }
            else
            {
                hourF = Integer.parseInt(timeEstimate[0]);
                minuteF = Integer.parseInt(timeEstimate[1]);
                secondF = Integer.parseInt(timeEstimate[2]);
            }

            calendar.add(Calendar.DATE, dayF);
            calendar.add(Calendar.HOUR, hourF);
            calendar.add(Calendar.MINUTE, minuteF);
            calendar.add(Calendar.SECOND, secondF);
            String date1 = new SimpleDateFormat("EEEE h:mm:ss a", Locale.ROOT).format(calendar.getTime());

            if (timeEstimate.length == 1)
            {
                date1 = new SimpleDateFormat("EEEE h:00 a", Locale.ROOT).format(calendar.getTime());
            }

            String date2 = new SimpleDateFormat("d MMMMM yyyy", Locale.ROOT).format(calendar.getTime());

            if (mc.currentScreen instanceof GuiChest)
            {
                GuiChest chest = (GuiChest)mc.currentScreen;
                String name = chest.lowerChestInventory.getDisplayName().getUnformattedText();

                if (name.equals("Auction View"))
                {
                    newStrings.add("Ends at: " + EnumChatFormatting.YELLOW + date1 + ", " + date2);
                }
                else
                {
                    newStrings.add("Ends at: ");
                    newStrings.add(EnumChatFormatting.YELLOW + date1);
                    newStrings.add(EnumChatFormatting.YELLOW + date2);
                }
            }

            if (!GuiScreen.isShiftKeyDown())
            {
                tooltip.add(i + 1, "Press <SHIFT> to view exact time");
            }
            else
            {
                tooltip.remove(i);

                for (String text : newStrings)
                {
                    tooltip.add(i++, text);
                }
            }
        }
    }

    static class ItemDropDiff
    {
        final ItemStack itemStack;
        final int count;
        final NBTTagCompound extraAttributes;

        public ItemDropDiff(ItemStack itemStack, int count, NBTTagCompound extraAttributes)
        {
            this.itemStack = itemStack;
            this.count = count;
            this.extraAttributes = extraAttributes;
        }
    }
}