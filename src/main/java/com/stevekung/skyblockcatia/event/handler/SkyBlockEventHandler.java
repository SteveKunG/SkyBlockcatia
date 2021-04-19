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
import org.lwjgl.glfw.GLFW;

import com.google.common.collect.*;
import com.google.gson.JsonObject;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.gui.SignSelectionList;
import com.stevekung.skyblockcatia.gui.screen.SkyBlockProfileSelectorScreen;
import com.stevekung.skyblockcatia.gui.screen.config.SkyBlockSettingsScreen;
import com.stevekung.skyblockcatia.gui.toasts.*;
import com.stevekung.skyblockcatia.gui.toasts.ToastUtils.ToastType;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.*;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;
import com.stevekung.skyblockcatia.utils.skyblock.SBPets;
import com.stevekung.skyblockcatia.utils.skyblock.SBSkills;
import com.stevekung.skyblockcatia.utils.skyblock.api.BazaarData;
import com.stevekung.skyblockcatia.utils.skyblock.api.DragonType;
import com.stevekung.skyblockcatia.utils.skyblock.api.PetStats;
import com.stevekung.stevekungslib.utils.CommonUtils;
import com.stevekung.stevekungslib.utils.GameProfileUtils;
import com.stevekung.stevekungslib.utils.ItemUtils;
import com.stevekung.stevekungslib.utils.NumberUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import com.stevekung.stevekungslib.utils.client.ClientUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.NonNullList;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

@SuppressWarnings("deprecation")
public class SkyBlockEventHandler
{
    private static final Pattern CUSTOM_FORMATTING_CODE_PATTERN = Pattern.compile("(?i)§[0-9A-Z]");
    private static final Pattern VISIT_ISLAND_PATTERN = Pattern.compile("(?:\\[SkyBlock\\]|\\[SkyBlock\\] (?:\\[VIP?\\+{0,1}\\]|\\[MVP?\\+{0,2}\\]|\\[YOUTUBE\\])) (?<name>\\w+) is visiting Your Island!");
    public static final String UUID_PATTERN_STRING = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    private static final Pattern UUID_PATTERN = Pattern.compile("Your new API key is (?<uuid>" + SkyBlockEventHandler.UUID_PATTERN_STRING + ")");
    private static final String RANKED_PATTERN = "(?:(?:\\w)|(?:\\[VIP?\\+{0,1}\\]|\\[MVP?\\+{0,2}\\]|\\[YOUTUBE\\]) \\w)+";
    private static final Pattern PET_CARE_PATTERN = Pattern.compile("§r§aI'm currently taking care of your §r(?<pet>§[0-9a-fk-or][\\w ]+)§r§a! You can pick it up in (?:(?<day>[\\d]+) day(?:s){0,1} ){0,1}(?:(?<hour>[\\d]+) hour(?:s){0,1} ){0,1}(?:(?<minute>[\\d]+) minute(?:s){0,1} ){0,1}(?:(?<second>[\\d]+) second(?:s){0,1}).§r");
    private static final Pattern DRAGON_DOWN_PATTERN = Pattern.compile("(?:§r){0,1} +§r§6§l(?<dragon>SUPERIOR|STRONG|YOUNG|OLD|PROTECTOR|UNSTABLE|WISE) DRAGON DOWN!§r");
    private static final Pattern DRAGON_SPAWNED_PATTERN = Pattern.compile("§5☬ §r§d§lThe §r§5§c§l(?<dragon>Superior|Strong|Young|Unstable|Wise|Old|Protector) Dragon§r§d§l has spawned!§r");
    private static final Pattern ESTIMATED_TIME_PATTERN = Pattern.compile("\\((?<time>(?:\\d+d ){0,1}(?:\\d+h ){0,1}(?:\\d+m ){0,1}(?:\\d+s)|(?:\\d+h))\\)");

    // Item Drop Stuff
    private static final String ITEM_PATTERN = "[\\w\\'◆\\[\\] -]+";
    private static final String DROP_PATTERN = "(?<item>(?:§r§[0-9a-fk-or]){0,1}(?:§[0-9a-fk-or]){0,1}" + ITEM_PATTERN + "(?:[\\(][^\\)]" + ITEM_PATTERN + "[\\)]){0,1})";
    private static final Pattern RARE_DROP_PATTERN = Pattern.compile("(?:§r){0,1}§6§lRARE DROP! " + DROP_PATTERN + "(?:\\b§r\\b){0,1} ?(?:§r§b\\(\\+(?<mf>[0-9]+)% Magic Find!\\)§r){0,1}");
    private static final Pattern RARE_DROP_2_SPACE_PATTERN = Pattern.compile("(?:§r){0,1}§b§lRARE DROP! §r§7\\(" + DROP_PATTERN + "§r§7\\)(?:\\b§r\\b){0,1} ?(?:§r§b\\(\\+(?<mf>[0-9]+)% Magic Find!\\)§r){0,1}");
    private static final Pattern RARE_DROP_WITH_BRACKET_PATTERN = Pattern.compile("(?<type>(?:§r){0,1}§9§lVERY RARE|§r§5§lVERY RARE|§r§d§lCRAZY RARE) DROP!  §r§7\\(" + DROP_PATTERN + "§r§7\\)(?:\\b§r\\b){0,1} ?(?:§r§b\\(\\+(?<mf>[0-9]+)% Magic Find!\\)§r){0,1}");
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
    private static final Pattern PET_LEVEL_UP_PATTERN = Pattern.compile("(?:§r){0,1}§aYour (?<name>§r§[0-9a-fk-or][\\w ]+) §r§alevelled up to level §r§9(?<level>\\d+)§r§a!§r");
    private static final Pattern PET_DROP_PATTERN = Pattern.compile("PET DROP! " + DROP_PATTERN + " ?(?:\\(\\+(?<mf>[0-9]+)% Magic Find!\\)){0,1}");

    private static final Map<String, String> RENAMED_DROP = ImmutableMap.<String, String>builder().put("◆ Ice Rune", "◆ Ice Rune I").build();
    public static boolean isSkyBlock;
    public static boolean foundSkyBlockPack;
    public static String skyBlockPackResolution = "16";
    public static SBLocation SKY_BLOCK_LOCATION = SBLocation.YOUR_ISLAND;
    public static float dragonHealth;
    public static boolean otherPlayerIsland;
    private static final List<ToastUtils.ItemDropCheck> ITEM_DROP_CHECK_LIST = Lists.newArrayList();
    private List<ItemStack> previousInventory;
    private DragonType dragonType;
    private final Minecraft mc;
    private boolean initApiData;

    public SkyBlockEventHandler()
    {
        this.mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (this.mc.player != null)
        {
            if (!SkyBlockcatiaMod.CHECKER.hasChecked())
            {
                SkyBlockcatiaMod.CHECKER.checkFail();
                SkyBlockcatiaMod.CHECKER.printInfo();
                SkyBlockcatiaMod.CHECKER.setChecked(true);
            }
            if (event.phase == TickEvent.Phase.START)
            {
                if (this.mc.player.ticksExisted % 5 == 0)
                {
                    this.getInventoryDifference(this.mc.player.inventory.mainInventory);
                }

                for (Map.Entry<String, Pair<Long, JsonObject>> entry : SkyBlockProfileSelectorScreen.PROFILE_CACHE.entrySet())
                {
                    long now = System.currentTimeMillis();
                    long checkedTime = entry.getValue().getLeft();

                    if (now - checkedTime > 120000D)
                    {
                        SkyBlockProfileSelectorScreen.PROFILE_CACHE.remove(entry.getKey());
                    }
                }
                for (Map.Entry<String, Pair<Long, JsonObject>> entry : SkyBlockProfileSelectorScreen.INIT_PROFILE_CACHE.entrySet())
                {
                    long now = System.currentTimeMillis();
                    long checkedTime = entry.getValue().getLeft();

                    if (now - checkedTime > 120000D)
                    {
                        SkyBlockProfileSelectorScreen.INIT_PROFILE_CACHE.remove(entry.getKey());
                    }
                }

                if (this.mc.world != null)
                {
                    boolean found = false;
                    ScoreObjective scoreObj = this.mc.world.getScoreboard().getObjectiveInDisplaySlot(1);
                    Scoreboard scoreboard = this.mc.world.getScoreboard();
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
                        String scoreText = CUSTOM_FORMATTING_CODE_PATTERN.matcher(ScorePlayerTeam.func_237500_a_(scorePlayerTeam, new StringTextComponent(score1.getPlayerName())).getString()).replaceAll("");

                        if (scoreText.startsWith("Dragon HP: "))
                        {
                            try
                            {
                                SkyBlockEventHandler.dragonHealth = Float.parseFloat(scoreText.replaceAll("[^\\d]", ""));

                                if (this.dragonType != null)
                                {
                                    //SkyBlockBossBar.healthScale = HypixelEventHandler.dragonHealth / this.dragonType.getMaxHealth();TODO
                                }
                                break;
                            }
                            catch (Exception e) {}
                        }
                    }

                    for (Score score1 : collection)
                    {
                        ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score1.getPlayerName());
                        String scoreText = CUSTOM_FORMATTING_CODE_PATTERN.matcher(ScorePlayerTeam.func_237500_a_(scorePlayerTeam, new StringTextComponent(score1.getPlayerName())).getString()).replaceAll("");
                        String textNoSpecial = scoreText.replaceAll("[^a-z A-Z:0-9/'()]", "");

                        for (SBLocation location : SBLocation.VALUES)
                        {
                            if (scoreText.contains("⏣") && textNoSpecial.substring(2).equals(location.getLocation()))
                            {
                                SkyBlockEventHandler.SKY_BLOCK_LOCATION = location;
                                found = true;
                                break;
                            }
                        }
                    }

                    if (scoreObj != null)
                    {
                        SkyBlockEventHandler.isSkyBlock = CUSTOM_FORMATTING_CODE_PATTERN.matcher(scoreObj.getDisplayName().getString()).replaceAll("").contains("SKYBLOCK");
                        otherPlayerIsland = CUSTOM_FORMATTING_CODE_PATTERN.matcher(scoreObj.getDisplayName().getString()).replaceAll("").contains("SKYBLOCK GUEST");
                    }
                    else
                    {
                        SkyBlockEventHandler.isSkyBlock = false;
                    }

                    if (!found)
                    {
                        SkyBlockEventHandler.SKY_BLOCK_LOCATION = SBLocation.NONE;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouseClick(InputEvent.MouseInputEvent event)
    {
        if (event.getButton() == GLFW.GLFW_PRESS && event.getAction() == GLFW.GLFW_MOUSE_BUTTON_2 && this.mc.pointedEntity != null && this.mc.pointedEntity instanceof RemoteClientPlayerEntity && this.mc.player.isSneaking() && SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.sneakToTradeOtherPlayerIsland && otherPlayerIsland)
        {
            RemoteClientPlayerEntity player = (RemoteClientPlayerEntity)this.mc.pointedEntity;
            this.mc.player.sendChatMessage("/trade " + TextFormatting.getTextWithoutFormattingCodes(player.getName().getString()));
        }
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event)
    {
        if (event.getMessage() == null)
        {
            return;
        }

        String formattedMessage = event.getMessage().getString();
        String message = TextFormatting.getTextWithoutFormattingCodes(formattedMessage);
        boolean cancelMessage = false;
        boolean dev = this.mc.isSingleplayer() && !FMLEnvironment.production;

        if (Utils.isHypixel() || dev)
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

            if (event.getType() == ChatType.CHAT || dev)
            {
                if (visitIslandMatcher.matches())
                {
                    String name = visitIslandMatcher.group("name");

                    if (SkyBlockcatiaSettings.INSTANCE.visitIslandDisplayMode == ToastMode.TOAST || SkyBlockcatiaSettings.INSTANCE.visitIslandDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        SkyBlockEventHandler.addVisitingToast(this.mc, name);
                        ToastLog.logToast(message);
                    }
                    cancelMessage = SkyBlockcatiaSettings.INSTANCE.visitIslandDisplayMode == ToastMode.TOAST || SkyBlockcatiaSettings.INSTANCE.visitIslandDisplayMode == ToastMode.DISABLED;
                }
                else if (uuidMatcher.matches())
                {
                    SBAPIUtils.setApiKeyFromServer(uuidMatcher.group("uuid"));
                    ClientUtils.printClientMessage("Setting a new API Key!", TextFormatting.GREEN);
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
                    ClientUtils.printClientMessage(TextComponentUtils.formatted(petCareMatcher.group("pet") + TextFormatting.GREEN + " will be finished on " + date1 + " " + date2, TextFormatting.GREEN));
                    cancelMessage = true;
                }

                if (SkyBlockcatiaSettings.INSTANCE.leavePartyWhenLastEyePlaced && message.contains(" Brace yourselves! (8/8)"))
                {
                    this.mc.player.sendChatMessage("/p leave");
                }

                if (SkyBlockEventHandler.isSkyBlock || dev)
                {
                    if (dragonDownMatcher.matches())
                    {
                        SkyBlockEventHandler.dragonHealth = 0;
                        HUDRenderEventHandler.foundDragon = false;

                        if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.showHitboxWhenDragonSpawned)
                        {
                            this.mc.getRenderManager().setDebugBoundingBox(false);
                        }
                    }
                    if (dragonSpawnedMatcher.matches())
                    {
                        String dragon = dragonSpawnedMatcher.group("dragon");
                        DragonType type = DragonType.valueOf(dragon.toUpperCase(Locale.ROOT));
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
                        ItemStack coinSkull = ItemUtils.getSkullItemStack(CoinType.TYPE_3.getId(), CoinType.TYPE_3.getValue());
                        NumericToast.addValueOrUpdate(this.mc.getToastGui(), ToastUtils.DropType.BANK_INTEREST, Integer.parseInt(coin.replace(",", "")), coinSkull, ToastUtils.DropType.BANK_INTEREST);
                        ToastLog.logToast(formattedMessage);
                        cancelMessage = true;
                    }
                    else if (allowancePattern.matches())
                    {
                        String coin = allowancePattern.group("coin");
                        ItemStack coinSkull = ItemUtils.getSkullItemStack(CoinType.TYPE_3.getId(), CoinType.TYPE_3.getValue());
                        NumericToast.addValueOrUpdate(this.mc.getToastGui(), ToastUtils.DropType.ALLOWANCE, Integer.parseInt(coin.replace(",", "")), coinSkull, ToastUtils.DropType.ALLOWANCE);
                        ToastLog.logToast(formattedMessage);
                        cancelMessage = true;
                    }

                    if (SkyBlockcatiaSettings.INSTANCE.fishCatchDisplayMode == ToastMode.TOAST || SkyBlockcatiaSettings.INSTANCE.fishCatchDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        if (fishCatchPattern.matches())
                        {
                            String dropType = fishCatchPattern.group("type");
                            String name = fishCatchPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, dropType.equals("GOOD") ? ToastUtils.DropType.GOOD_CATCH : ToastUtils.DropType.GREAT_CATCH, ToastType.DROP));
                            ToastLog.logToast(formattedMessage);
                            cancelMessage = SkyBlockcatiaSettings.INSTANCE.fishCatchDisplayMode == ToastMode.TOAST;
                        }
                        else if (coinsCatchPattern.matches())
                        {
                            String type = coinsCatchPattern.group("type");
                            String coin = coinsCatchPattern.group("coin");
                            CoinType coinType = type.equals("GOOD") ? CoinType.TYPE_1 : CoinType.TYPE_2;
                            ItemStack coinSkull = ItemUtils.getSkullItemStack(coinType.getId(), coinType.getValue());
                            NumericToast.addValueOrUpdate(this.mc.getToastGui(), type.equals("GOOD") ? ToastUtils.DropType.GOOD_CATCH_COINS : ToastUtils.DropType.GREAT_CATCH_COINS, Integer.parseInt(coin.replace(",", "")), coinSkull, type + "$" + coinType);
                            ToastLog.logToast(formattedMessage);
                            cancelMessage = SkyBlockcatiaSettings.INSTANCE.fishCatchDisplayMode == ToastMode.TOAST;
                        }
                    }

                    if (SkyBlockcatiaSettings.INSTANCE.giftDisplayMode == ToastMode.TOAST || SkyBlockcatiaSettings.INSTANCE.giftDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        if (coinsGiftPattern.matches())
                        {
                            String type = coinsGiftPattern.group("type");
                            String coin = coinsGiftPattern.group("coin");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            ItemStack coinSkull = ItemUtils.getSkullItemStack(CoinType.TYPE_1.getId(), CoinType.TYPE_1.getValue());
                            NumericToast.addValueOrUpdate(this.mc.getToastGui(), rarity, Integer.parseInt(coin.replace(",", "")), coinSkull, "Coins");
                            ToastLog.logToast(message);
                            cancelMessage = SkyBlockcatiaSettings.INSTANCE.giftDisplayMode == ToastMode.TOAST;
                        }
                        else if (skillExpGiftPattern.matches())
                        {
                            String type = skillExpGiftPattern.group("type");
                            String exp = skillExpGiftPattern.group("exp");
                            String skill = skillExpGiftPattern.group("skill");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            SBSkills.Type skillType = SBSkills.Type.byName(skill);
                            NumericToast.addValueOrUpdate(this.mc.getToastGui(), rarity, Integer.parseInt(exp.replace(",", "")), skillType.getItemStack().setDisplayName(TextComponentUtils.component(skillType.getName())), type + "$" + skillType);
                            ToastLog.logToast(message);
                            cancelMessage = SkyBlockcatiaSettings.INSTANCE.giftDisplayMode == ToastMode.TOAST;
                        }
                        else if (itemDropGiftPattern.matches())
                        {
                            String type = itemDropGiftPattern.group("type");
                            String name = itemDropGiftPattern.group("item");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, rarity, ToastUtils.ToastType.GIFT));
                            ToastLog.logToast(message);
                            cancelMessage = SkyBlockcatiaSettings.INSTANCE.giftDisplayMode == ToastMode.TOAST;
                        }
                        else if (santaTierPattern.matches())
                        {
                            String name = santaTierPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.SANTA_TIER, ToastUtils.ToastType.GIFT));
                            ToastLog.logToast(message);
                            cancelMessage = SkyBlockcatiaSettings.INSTANCE.giftDisplayMode == ToastMode.TOAST;
                        }
                    }

                    if (SkyBlockcatiaSettings.INSTANCE.itemLogDisplayMode == ToastMode.TOAST || SkyBlockcatiaSettings.INSTANCE.itemLogDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        boolean isToast = SkyBlockcatiaSettings.INSTANCE.itemLogDisplayMode == ToastMode.TOAST;

                        if (message.contains("You destroyed an Ender Crystal!"))
                        {
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck("Crystal Fragment", ToastUtils.DropType.DRAGON_CRYSTAL_FRAGMENT, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }

                        if (rareDropPattern.matches())
                        {
                            String name = rareDropPattern.group("item");
                            String magicFind = rareDropPattern.group("mf");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, ToastUtils.DropType.RARE_DROP, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (rareDropMythosPattern.matches())
                        {
                            String name = rareDropMythosPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.RARE_DROP, ToastType.DROP));
                            ToastLog.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (coinsMythosPattern.matches())
                        {
                            String coin = coinsMythosPattern.group("coin");
                            ItemStack coinSkull = ItemUtils.getSkullItemStack(CoinType.TYPE_1.getId(), CoinType.TYPE_1.getValue());
                            NumericToast.addValueOrUpdate(this.mc.getToastGui(), ToastUtils.DropType.MYTHOS_COINS, Integer.parseInt(coin.replace(",", "")), coinSkull, ToastUtils.DropType.MYTHOS_COINS);
                            ToastLog.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (bossDropPattern.matches())
                        {
                            String name = bossDropPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.BOSS_DROP, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (dungeonQualityDropPattern.matches())
                        {
                            String name = dungeonQualityDropPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.DUNGEON_QUALITY_DROP, ToastType.DROP));
                            ToastLog.logToast(formattedMessage);
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
                            ToastUtils.DropType dropType = type.startsWith("§r§9§lVERY RARE") ? ToastUtils.DropType.SLAYER_VERY_RARE_DROP_BLUE : type.startsWith("§r§5§lVERY RARE") ? ToastUtils.DropType.SLAYER_VERY_RARE_DROP_PURPLE : ToastUtils.DropType.SLAYER_CRAZY_RARE_DROP;
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, dropType, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (rareDrop2SpaceBracketPattern.matches())
                        {
                            String name = rareDrop2SpaceBracketPattern.group("item");
                            String magicFind = rareDrop2SpaceBracketPattern.group(2);
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, ToastUtils.DropType.SLAYER_RARE_DROP, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                    }

                    if (SkyBlockcatiaSettings.INSTANCE.petDisplayMode == ToastMode.TOAST || SkyBlockcatiaSettings.INSTANCE.petDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        boolean isToast = SkyBlockcatiaSettings.INSTANCE.petDisplayMode == ToastMode.TOAST;

                        if (petLevelUpPattern.matches())
                        {
                            String name = petLevelUpPattern.group("name");
                            String level = petLevelUpPattern.group("level");
                            SBPets.Type type = SBPets.PETS.getTypeByName(TextFormatting.getTextWithoutFormattingCodes(name).replace(" ", "_").toUpperCase(Locale.ROOT));
                            ItemStack itemStack = type.getPetItem();
                            itemStack.setDisplayName(TextComponentUtils.component(name));
                            NumericToast.addValueOrUpdate(this.mc.getToastGui(), ToastUtils.DropType.PET_LEVEL_UP, Integer.parseInt(level), itemStack, true, type);
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (petDropPattern.matches())
                        {
                            String name = petDropPattern.group("item");
                            String magicFind = petDropPattern.group("mf");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, ToastUtils.DropType.PET_DROP, ToastType.DROP));
                            ToastLog.logToast(message);
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
            if (KeyBindingHandler.KEY_SB_ENDER_CHEST.isKeyDown())
            {
                this.mc.player.sendChatMessage("/enderchest");
            }
            else if (KeyBindingHandler.KEY_SB_CRAFTED_MINIONS.isKeyDown())
            {
                this.mc.player.sendChatMessage("/craftedgenerators");
            }
            else if (KeyBindingHandler.KEY_SB_CRAFTING_TABLE.isKeyDown())
            {
                this.mc.player.sendChatMessage("/craft");
            }
            else if (KeyBindingHandler.KEY_SB_MENU.isKeyDown())
            {
                this.mc.player.sendChatMessage("/sbmenu");
            }
            else if (KeyBindingHandler.KEY_SB_VIEW_RECIPE.isKeyDown() && this.mc.currentScreen == null)
            {
                this.mc.player.sendChatMessage("/recipes");
            }
            else if (KeyBindingHandler.KEY_SB_PETS.isKeyDown())
            {
                this.mc.player.sendChatMessage("/pets");
            }
            else if (KeyBindingHandler.KEY_SB_WARDROBE.isKeyDown())
            {
                this.mc.player.sendChatMessage("/wardrobe");
            }
            else if (KeyBindingHandler.KEY_SB_HOTM.isKeyDown())
            {
                this.mc.player.sendChatMessage("/hotm");
            }
        }

        if (KeyBindingHandler.KEY_SB_SETTINGS.isKeyDown())
        {
            this.mc.displayGuiScreen(new SkyBlockSettingsScreen());
        }
        else if (KeyBindingHandler.KEY_SB_API_VIEWER.isKeyDown())
        {
            if (StringUtils.isNullOrEmpty(SkyBlockcatiaConfig.GENERAL.hypixelApiKey.get()))
            {
                ClientUtils.printClientMessage("Couldn't open API Viewer, Empty API Key in the Config!", TextFormatting.RED);
                ClientUtils.printClientMessage(TextComponentUtils.formatted("Make sure you're in the Hypixel!", TextFormatting.YELLOW).appendSibling(TextComponentUtils.formatted(" Click Here to create an API key", TextFormatting.GOLD).setStyle(Style.EMPTY.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/api new")))));
                return;
            }
            if (!SkyBlockcatiaConfig.GENERAL.hypixelApiKey.get().matches(SkyBlockEventHandler.UUID_PATTERN_STRING))
            {
                ClientUtils.printClientMessage("Invalid UUID for Hypixel API Key!", TextFormatting.RED);
                ClientUtils.printClientMessage("Example UUID pattern: " + UUID.randomUUID(), TextFormatting.YELLOW);
                return;
            }
            if (this.mc.pointedEntity != null && this.mc.pointedEntity instanceof RemoteClientPlayerEntity)
            {
                RemoteClientPlayerEntity player = (RemoteClientPlayerEntity)this.mc.pointedEntity;

                if (this.mc.player.connection.getPlayerInfoMap().stream().anyMatch(info -> info.getGameProfile().getName().equals(player.getName().getString())))
                {
                    this.mc.displayGuiScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.PLAYER, player.getDisplayName().getString(), "", ""));
                }
                else
                {
                    this.mc.displayGuiScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.EMPTY));
                }
            }
            else
            {
                this.mc.displayGuiScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.EMPTY));
            }
        }
    }

    @SubscribeEvent
    public void onWorldJoin(EntityJoinWorldEvent event)
    {
        if (event.getEntity() == this.mc.player)
        {
            this.previousInventory = null;
            SkyBlockEventHandler.dragonHealth = 0;
            ITEM_DROP_CHECK_LIST.clear();

            if (!this.initApiData && Utils.isHypixel())
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
    public void onDisconnectedFromServerEvent(ClientPlayerNetworkEvent.LoggedOutEvent event)
    {
        SignSelectionList.clearAll();
        SkyBlockEventHandler.dragonHealth = 0;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemTooltip(ItemTooltipEvent event)
    {
        try
        {
            Calendar calendar = Calendar.getInstance();
            ItemStack itemStack = event.getItemStack();
            List<ITextComponent> tooltip = event.getToolTip();

            if (event.getItemStack().hasTag())
            {
                CompoundNBT extraAttrib = event.getItemStack().getTag().getCompound("ExtraAttributes");
                int insertAt = tooltip.size();
                insertAt--; // rarity

                if (this.mc.gameSettings.advancedItemTooltips)
                {
                    insertAt -= 2; // item name + nbt

                    if (itemStack.isDamaged())
                    {
                        insertAt--; // 1 damage
                    }
                }

                if (SkyBlockcatiaSettings.INSTANCE.showObtainedDate && extraAttrib.contains("timestamp"))
                {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[MM/dd/yy h:mm a][M/dd/yy h:mm a][M/d/yy h:mm a]", Locale.ENGLISH);
                    LocalDateTime datetime = LocalDateTime.parse(extraAttrib.getString("timestamp"), formatter);
                    Date convertedDatetime = Date.from(datetime.atZone(ZoneId.systemDefault()).toInstant());
                    String formatted = new SimpleDateFormat("d MMMM yyyy h:mm aa", Locale.ROOT).format(convertedDatetime);
                    tooltip.add(insertAt++, TextComponentUtils.formatted("Obtained: " + formatted, TextFormatting.GRAY));
                }
                if (SkyBlockcatiaSettings.INSTANCE.bazaarOnItemTooltip)
                {
                    for (Map.Entry<String, BazaarData> entry : MainEventHandler.BAZAAR_DATA.entrySet())
                    {
                        BazaarData.Product product = entry.getValue().getProduct();

                        if (extraAttrib.getString("id").equals(entry.getKey()))
                        {
                            if (ClientUtils.isShiftKeyDown())
                            {
                                if (StringUtils.isNullOrEmpty(SkyBlockcatiaConfig.GENERAL.hypixelApiKey.get()))
                                {
                                    tooltip.add(insertAt++, TextComponentUtils.formatted("Couldn't get bazaar data, Empty API Key in the Config!", TextFormatting.RED));
                                }
                                else if (!SkyBlockcatiaConfig.GENERAL.hypixelApiKey.get().matches(SkyBlockEventHandler.UUID_PATTERN_STRING))
                                {
                                    tooltip.add(insertAt++, TextComponentUtils.formatted("Invalid UUID for Hypixel API Key!", TextFormatting.RED));
                                }
                                else
                                {
                                    double buyStack = 64 * product.getBuyPrice();
                                    double sellStack = 64 * product.getSellPrice();
                                    double buyCurrent = event.getItemStack().getCount() * product.getBuyPrice();
                                    double sellCurrent = event.getItemStack().getCount() * product.getSellPrice();
                                    tooltip.add(insertAt++, TextComponentUtils.formatted("Buy/Sell (Stack): ", TextFormatting.GRAY).appendString(TextFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(buyStack) + TextFormatting.YELLOW + "/" + TextFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(sellStack) + " coins"));

                                    if (event.getItemStack().getCount() > 1 && event.getItemStack().getCount() < 64)
                                    {
                                        tooltip.add(insertAt++, TextComponentUtils.formatted("Buy/Sell (Current): ", TextFormatting.GRAY).appendString(TextFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(buyCurrent) + TextFormatting.YELLOW + "/" + TextFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(sellCurrent) + " coins"));
                                    }

                                    tooltip.add(insertAt++, TextComponentUtils.formatted("Buy/Sell (One): ", TextFormatting.GRAY).appendString(TextFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(product.getBuyPrice()) + TextFormatting.YELLOW + "/" + TextFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(product.getSellPrice()) + " coins"));
                                    tooltip.add(insertAt++, TextComponentUtils.formatted("Last Updated: ", TextFormatting.GRAY).appendSibling(TextComponentUtils.formatted(TimeUtils.getRelativeTime(entry.getValue().getLastUpdated()), TextFormatting.WHITE)));
                                }
                            }
                            else
                            {
                                tooltip.add(insertAt++, TextComponentUtils.formatted("Press <SHIFT> to view Bazaar Buy/Sell", TextFormatting.GRAY));
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < tooltip.size(); i++)
            {
                String lore = TextComponentUtils.fromJson(tooltip.get(i));

                if (this.mc.currentScreen != null && this.mc.currentScreen instanceof ChestScreen)
                {
                    String name = this.mc.currentScreen.getTitle().getString();

                    if (name.equals("SkyBlock Menu"))
                    {
                        SkyBlockEventHandler.replaceText(lore, calendar, tooltip, i, "Ends in:", "Ends at:");
                        SkyBlockEventHandler.replaceText(lore, calendar, tooltip, i, "Starting in:", "Starts at:");
                    }
                    else if (name.equals("Community Shop"))
                    {
                        SkyBlockEventHandler.replaceText(lore, calendar, tooltip, i, "Starts in:", "Starts at:");
                    }
                    else
                    {
                        SkyBlockEventHandler.replaceText(lore, calendar, tooltip, i, "Starts in:", "Event starts at:");
                    }

                    if (!name.equals("SkyBlock Menu"))
                    {
                        SkyBlockEventHandler.replaceEstimatedTime(lore, tooltip, i);
                        SkyBlockEventHandler.replaceAuctionTime(lore, calendar, tooltip, i, "Ends in: ");
                    }
                }

                SkyBlockEventHandler.replaceText(lore, calendar, tooltip, i, "Time Remaining:", "Finished on:");
                SkyBlockEventHandler.replaceText(lore, calendar, tooltip, i, "Time left:", "Finished on:");
                SkyBlockEventHandler.replaceText(lore, calendar, tooltip, i, "Available:", "Available on:");
                SkyBlockEventHandler.replaceBankInterestTime(lore, calendar, tooltip, i, "Interest in: ");
                SkyBlockEventHandler.replaceBankInterestTime(lore, calendar, tooltip, i, "Until interest: ");
            }
        }
        catch (Exception e) {}
    }

    /**
     * Credit to codes.biscuit.skyblockaddons.utils.InventoryUtils
     */
    private void getInventoryDifference(NonNullList<ItemStack> currentInventory)
    {
        List<ItemStack> newInventory = this.copyInventory(currentInventory);
        Map<String, ItemDropDiff> previousInventoryMap = Maps.newHashMap();
        Map<String, ItemDropDiff> newInventoryMap = Maps.newHashMap();
        SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.removeIf(this::removeUndisplayedToast);

        if (this.previousInventory != null)
        {
            for (int i = 0; i < newInventory.size(); i++)
            {
                ItemStack previousItem = this.previousInventory.get(i);
                ItemStack newItem = newInventory.get(i);

                if (!previousItem.isEmpty())
                {
                    int amount;

                    if (previousInventoryMap.containsKey(previousItem.getDisplayName().getString()))
                    {
                        amount = previousInventoryMap.get(previousItem.getDisplayName().getString()).count + previousItem.getCount();
                    }
                    else
                    {
                        amount = previousItem.getCount();
                    }

                    CompoundNBT extraAttributes = previousItem.getOrCreateChildTag("ExtraAttributes");

                    if (extraAttributes != null)
                    {
                        extraAttributes = extraAttributes.copy();
                    }
                    previousInventoryMap.put(previousItem.getDisplayName().getString(), new ItemDropDiff(previousItem, amount, extraAttributes));
                }
                if (!newItem.isEmpty())
                {
                    int amount;

                    if (newInventoryMap.containsKey(newItem.getDisplayName().getString()))
                    {
                        amount = newInventoryMap.get(newItem.getDisplayName().getString()).count + newItem.getCount();
                    }
                    else
                    {
                        amount = newItem.getCount();
                    }

                    CompoundNBT extraAttributes = newItem.getOrCreateChildTag("ExtraAttributes");

                    if (extraAttributes != null)
                    {
                        extraAttributes = extraAttributes.copy();
                    }
                    newInventoryMap.put(newItem.getDisplayName().getString(), new ItemDropDiff(newItem, amount, extraAttributes));
                }
            }

            Set<String> keySet = Sets.newHashSet(previousInventoryMap.keySet());
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

                    if (!newItem.isEmpty())
                    {
                        for (Iterator<ToastUtils.ItemDropCheck> iterator = SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.iterator(); iterator.hasNext();)
                        {
                            ToastUtils.ItemDropCheck drop = iterator.next();
                            String dropName = drop.getName();

                            if (drop.getType() == ToastUtils.DropType.PET_DROP)
                            {
                                if (("[Lvl 1] " + dropName).equals(TextFormatting.getTextWithoutFormattingCodes(key)))
                                {
                                    newItem.setCount(diff);

                                    if (this.mc.getToastGui().toastsQueue.add(new ItemDropsToast(newItem, drop.getType(), drop.getMagicFind())))
                                    {
                                        iterator.remove();
                                    }
                                }
                            }
                            else
                            {
                                dropName = RENAMED_DROP.getOrDefault(dropName, dropName);

                                if (dropName.equals("§fEnchanted Book"))
                                {
                                    dropName = "§9Enchanted Book";
                                }

                                if (dropName.equals(key.replaceAll("§r$", "")) || !drop.getType().matches(ToastUtils.DropCondition.FORMAT) && dropName.equals(TextFormatting.getTextWithoutFormattingCodes(key)))
                                {
                                    newItem.setCount(diff);

                                    if (drop.getToastType() == ToastType.DROP)
                                    {
                                        if (this.mc.getToastGui().toastsQueue.add(new ItemDropsToast(newItem, drop.getType(), drop.getMagicFind())))
                                        {
                                            iterator.remove();
                                        }
                                    }
                                    else
                                    {
                                        if (this.mc.getToastGui().toastsQueue.add(new GiftToast(newItem, drop.getType(), drop.getType() == ToastUtils.DropType.SANTA_TIER)))
                                        {
                                            iterator.remove();
                                        }
                                    }
                                }
                                else if (drop.getType().matches(ToastUtils.DropCondition.CONTAINS) && key.contains(dropName))
                                {
                                    if (this.mc.getToastGui().toastsQueue.add(new ItemDropsToast(newItem, drop.getType(), drop.getMagicFind())))
                                    {
                                        iterator.remove();
                                    }
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
            ToastLog.logToast("You got " + drop.getName() + " but it doesn't show on toast!");
            return true;
        }
        return false;
    }

    private List<ItemStack> copyInventory(NonNullList<ItemStack> inventory)
    {
        List<ItemStack> copy = Lists.newArrayListWithCapacity(inventory.size());

        for (ItemStack item : inventory)
        {
            copy.add(item.copy());
        }
        return copy;
    }

    private static void addVisitingToast(Minecraft mc, String name)
    {
        CommonUtils.runAsync(() -> mc.getToastGui().add(new VisitIslandToast(name)));
    }

    private static void replaceText(String lore, Calendar calendar, List<ITextComponent> tooltip, int i, String startWith, String newText)
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
            List<ITextComponent> newStrings = Lists.newArrayList();
            newStrings.add(TextComponentUtils.formatted(newText, TextFormatting.GRAY));
            newStrings.add(TextComponentUtils.formatted(date1, TextFormatting.YELLOW));
            newStrings.add(TextComponentUtils.formatted(date2, TextFormatting.YELLOW));

            if (!ClientUtils.isShiftKeyDown())
            {
                tooltip.add(i + 1, TextComponentUtils.formatted("Press <SHIFT> to view exact time", TextFormatting.GRAY));
            }
            else
            {
                tooltip.remove(i);

                for (ITextComponent text : newStrings)
                {
                    tooltip.add(i++, text);
                }
            }
        }
    }

    private static void replaceEstimatedTime(String lore, List<ITextComponent> tooltip, int i)
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

            if (!ClientUtils.isShiftKeyDown())
            {
                tooltip.add(i + 1, TextComponentUtils.formatted("Press <SHIFT> to view exact time", TextFormatting.GRAY));
            }
            else
            {
                tooltip.add(i + 1, TextComponentUtils.formatted(date2 + " " + date1, TextFormatting.YELLOW));
            }
        }
    }

    private static void replaceBankInterestTime(String lore, Calendar calendar, List<ITextComponent> tooltip, int i, String startWith)
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
            List<ITextComponent> newStrings = Lists.newArrayList();
            newStrings.add(TextComponentUtils.formatted("Interest receive at: ", TextFormatting.GRAY));
            newStrings.add(TextComponentUtils.formatted(date1, TextFormatting.YELLOW));
            newStrings.add(TextComponentUtils.formatted(date2, TextFormatting.YELLOW));

            if (!ClientUtils.isShiftKeyDown())
            {
                tooltip.add(i + 1, TextComponentUtils.formatted("Press <SHIFT> to view exact time", TextFormatting.GRAY));
            }
            else
            {
                tooltip.remove(i);

                for (ITextComponent text : newStrings)
                {
                    tooltip.add(i++, text);
                }
            }
        }
    }

    private static void replaceAuctionTime(String lore, Calendar calendar, List<ITextComponent> tooltip, int i, String startWith)
    {
        Minecraft mc = Minecraft.getInstance();

        if (lore.startsWith(startWith))
        {
            boolean isDay = lore.endsWith("d");
            lore = lore.substring(lore.indexOf(":") + 2).replaceAll("[^0-9]+", " ");
            String[] timeEstimate = Arrays.stream(lore.split(" ")).map(time -> time.replaceAll("[^0-9]+", "")).toArray(size -> new String[size]);
            int dayF = 0;
            int hourF = 0;
            int minuteF = 0;
            int secondF = 0;
            List<ITextComponent> newStrings = Lists.newArrayList();

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

            if (mc.currentScreen != null && mc.currentScreen instanceof ChestScreen)
            {
                String name = mc.currentScreen.getTitle().getString();

                if (name.equals("Auction View"))
                {
                    newStrings.add(TextComponentUtils.formatted("Ends at: " + TextFormatting.YELLOW + date1 + ", " + date2, TextFormatting.GRAY));
                }
                else
                {
                    newStrings.add(TextComponentUtils.formatted("Ends at: ", TextFormatting.GRAY));
                    newStrings.add(TextComponentUtils.formatted(date1, TextFormatting.YELLOW));
                    newStrings.add(TextComponentUtils.formatted(date2, TextFormatting.YELLOW));
                }
            }

            if (!ClientUtils.isShiftKeyDown())
            {
                tooltip.add(i + 1, TextComponentUtils.formatted("Press <SHIFT> to view exact time", TextFormatting.GRAY));
            }
            else
            {
                tooltip.remove(i);

                for (ITextComponent text : newStrings)
                {
                    tooltip.add(i++, text);
                }
            }
        }
    }

    class ItemDropDiff
    {
        final ItemStack itemStack;
        final int count;
        final CompoundNBT extraAttributes;

        public ItemDropDiff(ItemStack itemStack, int count, CompoundNBT extraAttributes)
        {
            this.itemStack = itemStack;
            this.count = count;
            this.extraAttributes = extraAttributes;
        }
    }
}