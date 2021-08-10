package com.stevekung.skyblockcatia.event.handler;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.*;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
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
import com.stevekung.skyblockcatia.utils.skyblock.api.DragonType;
import com.stevekung.skyblockcatia.utils.skyblock.api.PetStats;
import com.stevekung.stevekungslib.utils.*;
import com.stevekung.stevekungslib.utils.client.ClientUtils;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.platform.Platform;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;

@SuppressWarnings("deprecation")
public class SkyBlockEventHandler
{
    private static final Pattern CUSTOM_FORMATTING_CODE_PATTERN = Pattern.compile("(?i)§[0-9A-Z]");
    private static final Pattern VISIT_ISLAND_PATTERN = Pattern.compile("(?:\\[SkyBlock]|\\[SkyBlock] (?:\\[VIP?\\+?]|\\[MVP?\\+{0,2}]|\\[YOUTUBE])) (?<name>\\w+) is visiting Your Island!");
    public static final String UUID_PATTERN_STRING = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    private static final Pattern UUID_PATTERN = Pattern.compile("Your new API key is (?<uuid>" + SkyBlockEventHandler.UUID_PATTERN_STRING + ")");
    private static final String RANKED_PATTERN = "(?:(?:\\w)|(?:\\[VIP?\\+?]|\\[MVP?\\+{0,2}]|\\[YOUTUBE]) \\w)+";
    private static final Pattern PET_CARE_PATTERN = Pattern.compile("§r§aI'm currently taking care of your §r(?<pet>§[0-9a-fk-or][\\w ]+)§r§a! You can pick it up in (?:(?<day>[\\d]+) days? )?(?:(?<hour>[\\d]+) hours? )?(?:(?<minute>[\\d]+) minutes? )?(?:(?<second>[\\d]+) seconds?).§r");
    private static final Pattern DRAGON_DOWN_PATTERN = Pattern.compile("(?:§r)? +§r§6§l(?<dragon>SUPERIOR|STRONG|YOUNG|OLD|PROTECTOR|UNSTABLE|WISE) DRAGON DOWN!§r");
    private static final Pattern DRAGON_SPAWNED_PATTERN = Pattern.compile("§5☬ §r§d§lThe §r§5§c§l(?<dragon>Superior|Strong|Young|Unstable|Wise|Old|Protector) Dragon§r§d§l has spawned!§r");
    private static final Pattern ESTIMATED_TIME_PATTERN = Pattern.compile("\\((?<time>(?:\\d+d )?(?:\\d+h )?(?:\\d+m )?(?:\\d+s)|(?:\\d+h))\\)");

    // Item Drop Stuff
    private static final String ITEM_PATTERN = "[\\w'◆\\[] -]+";
    private static final String DROP_PATTERN = "(?<item>(?:§r§[0-9a-fk-or])?(?:§[0-9a-fk-or])?" + ITEM_PATTERN + "(?:[\\(][^\\)]" + ITEM_PATTERN + "[\\)])?)";
    private static final Pattern RARE_DROP_PATTERN = Pattern.compile("(?:§r)?§6§lRARE DROP! " + DROP_PATTERN + "(?:\\b§r\\b)? ?(?:§r§b\\(\\+(?<mf>[0-9]+)% Magic Find!\\)§r)?");
    private static final Pattern RARE_DROP_2_SPACE_PATTERN = Pattern.compile("(?:§r)?§b§lRARE DROP! §r§7\\(" + DROP_PATTERN + "§r§7\\)(?:\\b§r\\b)? ?(?:§r§b\\(\\+(?<mf>[0-9]+)% Magic Find!\\)§r)?");
    private static final Pattern RARE_DROP_WITH_BRACKET_PATTERN = Pattern.compile("(?<type>(?:§r)?§9§lVERY RARE|§r§5§lVERY RARE|§r§d§lCRAZY RARE) DROP!  §r§7\\(" + DROP_PATTERN + "§r§7\\)(?:\\b§r\\b)? ?(?:§r§b\\(\\+(?<mf>[0-9]+)% Magic Find!\\)§r)?");
    private static final Pattern BOSS_DROP_PATTERN = Pattern.compile("(?:(?:" + GameProfileUtils.getUsername() + ")|(?:\\[VIP?\\+?]|\\[MVP?\\+{0,2}]|\\[YOUTUBE]) " + GameProfileUtils.getUsername() + ") has obtained " + DROP_PATTERN + "!");

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
    private static final Pattern PET_LEVEL_UP_PATTERN = Pattern.compile("(?:§r)?§aYour (?<name>§r§[0-9a-fk-or][\\w ]+) §r§alevelled up to level §r§9(?<level>\\d+)§r§a!§r");
    private static final Pattern PET_DROP_PATTERN = Pattern.compile("PET DROP! " + DROP_PATTERN + " ?(?:\\(\\+(?<mf>[0-9]+)% Magic Find!\\))?");

    private static final Map<String, String> RENAMED_DROP = ImmutableMap.<String, String>builder().put("◆ Ice Rune", "◆ Ice Rune I").build();
    public static boolean isSkyBlock;
    public static SBLocation SKY_BLOCK_LOCATION = SBLocation.YOUR_ISLAND;
    public static float dragonHealth;
    public static boolean otherPlayerIsland;
    private static final List<ToastUtils.ItemDropCheck> ITEM_DROP_CHECK_LIST = Lists.newArrayList();
    private List<ItemStack> previousInventory;
    private DragonType dragonType;
    private boolean initApiData;
    public static SkyBlockEventHandler INSTANCE = new SkyBlockEventHandler(false);

    public SkyBlockEventHandler(boolean register)
    {
        if (register)
        {
            ClientTickEvent.CLIENT_PRE.register(this::onClientTick);
            ClientChatEvent.RECEIVED.register((chatType, component, sender) -> this.onClientChatReceived(chatType, component));
            EntityEvent.ADD.register(this::onWorldJoin);
        }
    }

    private void onClientTick(Minecraft mc)
    {
        if (mc.player != null)
        {
            if (mc.player.tickCount % 5 == 0)
            {
                this.getInventoryDifference(mc, mc.player.getInventory().items);
            }

            for (var entry : SkyBlockProfileSelectorScreen.PROFILE_CACHE.entrySet())
            {
                var now = System.currentTimeMillis();
                var checkedTime = entry.getValue().getLeft();

                if (now - checkedTime > 180000D)
                {
                    SkyBlockProfileSelectorScreen.PROFILE_CACHE.remove(entry.getKey());
                }
            }
            for (var entry : SkyBlockProfileSelectorScreen.INIT_PROFILE_CACHE.entrySet())
            {
                var now = System.currentTimeMillis();
                var checkedTime = entry.getValue().getLeft();

                if (now - checkedTime > 180000D)
                {
                    SkyBlockProfileSelectorScreen.INIT_PROFILE_CACHE.remove(entry.getKey());
                }
            }

            if (mc.level != null)
            {
                var found = false;
                var scoreObj = mc.level.getScoreboard().getDisplayObjective(1);
                var scoreboard = mc.level.getScoreboard();
                var collection = scoreboard.getPlayerScores(scoreObj);
                var list = collection.stream().filter(score -> score.getOwner() != null && !score.getOwner().startsWith("#")).collect(Collectors.toList());

                if (list.size() > 15)
                {
                    collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
                }
                else
                {
                    collection = list;
                }

                for (var score1 : collection)
                {
                    var scorePlayerTeam = scoreboard.getPlayersTeam(score1.getOwner());
                    var scoreText = CUSTOM_FORMATTING_CODE_PATTERN.matcher(PlayerTeam.formatNameForTeam(scorePlayerTeam, new TextComponent(score1.getOwner())).getString()).replaceAll("");

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
                        catch (Exception ignored)
                        {
                        }
                    }
                }

                for (var score1 : collection)
                {
                    var scorePlayerTeam = scoreboard.getPlayersTeam(score1.getOwner());
                    var scoreText = CUSTOM_FORMATTING_CODE_PATTERN.matcher(PlayerTeam.formatNameForTeam(scorePlayerTeam, new TextComponent(score1.getOwner())).getString()).replaceAll("");
                    var textNoSpecial = scoreText.replaceAll("[^a-z A-Z:0-9/'()]", "");

                    for (var location : SBLocation.VALUES)
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

    private CompoundEventResult<Component> onClientChatReceived(ChatType chatType, Component smessage)
    {
        var mc = Minecraft.getInstance();
        var formattedMessage = smessage.getString();
        var message = ChatFormatting.stripFormatting(formattedMessage);
        var cancelMessage = false;
        var dev = mc.hasSingleplayerServer() && !Platform.isDevelopmentEnvironment();

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

            if (chatType == ChatType.CHAT || dev)
            {
                if (visitIslandMatcher.matches())
                {
                    var name = visitIslandMatcher.group("name");

                    if (SkyBlockcatiaSettings.INSTANCE.visitIslandDisplayMode == ToastMode.TOAST || SkyBlockcatiaSettings.INSTANCE.visitIslandDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        SkyBlockEventHandler.addVisitingToast(mc, name);
                        ToastLog.logToast(message);
                    }
                    cancelMessage = SkyBlockcatiaSettings.INSTANCE.visitIslandDisplayMode == ToastMode.TOAST || SkyBlockcatiaSettings.INSTANCE.visitIslandDisplayMode == ToastMode.DISABLED;
                }
                else if (uuidMatcher.matches())
                {
                    SBAPIUtils.setApiKeyFromServer(uuidMatcher.group("uuid"));
                    ClientUtils.printClientMessage("Setting a new API Key!", ChatFormatting.GREEN);
                }
                else if (petCareMatcher.matches())
                {
                    var day = 0;
                    var hour = 0;
                    var minute = 0;
                    var second = 0;

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

                    var calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, day);
                    calendar.add(Calendar.HOUR, hour);
                    calendar.add(Calendar.MINUTE, minute);
                    calendar.add(Calendar.SECOND, second);
                    var date1 = new SimpleDateFormat("d MMMMM yyyy", Locale.ROOT).format(calendar.getTime());
                    var date2 = new SimpleDateFormat("h:mm:ss a", Locale.ROOT).format(calendar.getTime());
                    ClientUtils.printClientMessage(TextComponentUtils.formatted(petCareMatcher.group("pet") + ChatFormatting.GREEN + " will be finished on " + date1 + " " + date2, ChatFormatting.GREEN));
                    cancelMessage = true;
                }

                if (SkyBlockcatiaSettings.INSTANCE.leavePartyWhenLastEyePlaced && message.contains(" Brace yourselves! (8/8)"))
                {
                    mc.player.chat("/p leave");
                }

                if (SkyBlockEventHandler.isSkyBlock || dev)
                {
                    if (dragonDownMatcher.matches())
                    {
                        SkyBlockEventHandler.dragonHealth = 0;
                        HUDRenderEventHandler.foundDragon = false;

                        if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.showHitboxWhenDragonSpawned)
                        {
                            mc.getEntityRenderDispatcher().setRenderHitBoxes(false);
                        }
                    }
                    if (dragonSpawnedMatcher.matches())
                    {
                        var dragon = dragonSpawnedMatcher.group("dragon");
                        var type = DragonType.valueOf(dragon.toUpperCase(Locale.ROOT));
                        HUDRenderEventHandler.foundDragon = true;
                        this.dragonType = type;

                        if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.showHitboxWhenDragonSpawned)
                        {
                            mc.getEntityRenderDispatcher().setRenderHitBoxes(true);
                        }
                    }

                    if (bankInterestPattern.find())
                    {
                        var coin = bankInterestPattern.group("coin");
                        var coinSkull = ItemUtils.getSkullItemStack(CoinType.TYPE_3.getId(), CoinType.TYPE_3.getValue());
                        NumericToast.addValueOrUpdate(mc.getToasts(), ToastUtils.DropType.BANK_INTEREST, Integer.parseInt(coin.replace(",", "")), coinSkull, ToastUtils.DropType.BANK_INTEREST);
                        ToastLog.logToast(formattedMessage);
                        cancelMessage = true;
                    }
                    else if (allowancePattern.matches())
                    {
                        var coin = allowancePattern.group("coin");
                        var coinSkull = ItemUtils.getSkullItemStack(CoinType.TYPE_3.getId(), CoinType.TYPE_3.getValue());
                        NumericToast.addValueOrUpdate(mc.getToasts(), ToastUtils.DropType.ALLOWANCE, Integer.parseInt(coin.replace(",", "")), coinSkull, ToastUtils.DropType.ALLOWANCE);
                        ToastLog.logToast(formattedMessage);
                        cancelMessage = true;
                    }

                    if (SkyBlockcatiaSettings.INSTANCE.fishCatchDisplayMode == ToastMode.TOAST || SkyBlockcatiaSettings.INSTANCE.fishCatchDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        if (fishCatchPattern.matches())
                        {
                            var dropType = fishCatchPattern.group("type");
                            var name = fishCatchPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, dropType.equals("GOOD") ? ToastUtils.DropType.GOOD_CATCH : ToastUtils.DropType.GREAT_CATCH, ToastType.DROP));
                            ToastLog.logToast(formattedMessage);
                            cancelMessage = SkyBlockcatiaSettings.INSTANCE.fishCatchDisplayMode == ToastMode.TOAST;
                        }
                        else if (coinsCatchPattern.matches())
                        {
                            var type = coinsCatchPattern.group("type");
                            var coin = coinsCatchPattern.group("coin");
                            var coinType = type.equals("GOOD") ? CoinType.TYPE_1 : CoinType.TYPE_2;
                            var coinSkull = ItemUtils.getSkullItemStack(coinType.getId(), coinType.getValue());
                            NumericToast.addValueOrUpdate(mc.getToasts(), type.equals("GOOD") ? ToastUtils.DropType.GOOD_CATCH_COINS : ToastUtils.DropType.GREAT_CATCH_COINS, Integer.parseInt(coin.replace(",", "")), coinSkull, type + "$" + coinType);
                            ToastLog.logToast(formattedMessage);
                            cancelMessage = SkyBlockcatiaSettings.INSTANCE.fishCatchDisplayMode == ToastMode.TOAST;
                        }
                    }

                    if (SkyBlockcatiaSettings.INSTANCE.giftDisplayMode == ToastMode.TOAST || SkyBlockcatiaSettings.INSTANCE.giftDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        if (coinsGiftPattern.matches())
                        {
                            var type = coinsGiftPattern.group("type");
                            var coin = coinsGiftPattern.group("coin");
                            var rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            var coinSkull = ItemUtils.getSkullItemStack(CoinType.TYPE_1.getId(), CoinType.TYPE_1.getValue());
                            NumericToast.addValueOrUpdate(mc.getToasts(), rarity, Integer.parseInt(coin.replace(",", "")), coinSkull, "Coins");
                            ToastLog.logToast(message);
                            cancelMessage = SkyBlockcatiaSettings.INSTANCE.giftDisplayMode == ToastMode.TOAST;
                        }
                        else if (skillExpGiftPattern.matches())
                        {
                            var type = skillExpGiftPattern.group("type");
                            var exp = skillExpGiftPattern.group("exp");
                            var skill = skillExpGiftPattern.group("skill");
                            var rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            var skillType = SBSkills.Type.byName(skill);
                            NumericToast.addValueOrUpdate(mc.getToasts(), rarity, Integer.parseInt(exp.replace(",", "")), skillType.getItemStack().setHoverName(TextComponentUtils.component(skillType.getName())), type + "$" + skillType);
                            ToastLog.logToast(message);
                            cancelMessage = SkyBlockcatiaSettings.INSTANCE.giftDisplayMode == ToastMode.TOAST;
                        }
                        else if (itemDropGiftPattern.matches())
                        {
                            var type = itemDropGiftPattern.group("type");
                            var name = itemDropGiftPattern.group("item");
                            var rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, rarity, ToastUtils.ToastType.GIFT));
                            ToastLog.logToast(message);
                            cancelMessage = SkyBlockcatiaSettings.INSTANCE.giftDisplayMode == ToastMode.TOAST;
                        }
                        else if (santaTierPattern.matches())
                        {
                            var name = santaTierPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.SANTA_TIER, ToastUtils.ToastType.GIFT));
                            ToastLog.logToast(message);
                            cancelMessage = SkyBlockcatiaSettings.INSTANCE.giftDisplayMode == ToastMode.TOAST;
                        }
                    }

                    if (SkyBlockcatiaSettings.INSTANCE.itemLogDisplayMode == ToastMode.TOAST || SkyBlockcatiaSettings.INSTANCE.itemLogDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        var isToast = SkyBlockcatiaSettings.INSTANCE.itemLogDisplayMode == ToastMode.TOAST;

                        if (message.contains("You destroyed an Ender Crystal!"))
                        {
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck("Crystal Fragment", ToastUtils.DropType.DRAGON_CRYSTAL_FRAGMENT, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }

                        if (rareDropPattern.matches())
                        {
                            var name = rareDropPattern.group("item");
                            var magicFind = rareDropPattern.group("mf");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, ToastUtils.DropType.RARE_DROP, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (rareDropMythosPattern.matches())
                        {
                            var name = rareDropMythosPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.RARE_DROP, ToastType.DROP));
                            ToastLog.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (coinsMythosPattern.matches())
                        {
                            var coin = coinsMythosPattern.group("coin");
                            var coinSkull = ItemUtils.getSkullItemStack(CoinType.TYPE_1.getId(), CoinType.TYPE_1.getValue());
                            NumericToast.addValueOrUpdate(mc.getToasts(), ToastUtils.DropType.MYTHOS_COINS, Integer.parseInt(coin.replace(",", "")), coinSkull, ToastUtils.DropType.MYTHOS_COINS);
                            ToastLog.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (bossDropPattern.matches())
                        {
                            var name = bossDropPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.BOSS_DROP, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (dungeonQualityDropPattern.matches())
                        {
                            var name = dungeonQualityDropPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.DUNGEON_QUALITY_DROP, ToastType.DROP));
                            ToastLog.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (dungeonRewardPattern.matches())
                        {
                            var name = dungeonRewardPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.DUNGEON_REWARD_DROP, ToastType.DROP));
                        }
                        else if (rareDropBracketPattern.matches())
                        {
                            var type = rareDropBracketPattern.group("type");
                            var name = rareDropBracketPattern.group("item");
                            var magicFind = rareDropBracketPattern.group(3);
                            var dropType = type.startsWith("§r§9§lVERY RARE") ? ToastUtils.DropType.SLAYER_VERY_RARE_DROP_BLUE : type.startsWith("§r§5§lVERY RARE") ? ToastUtils.DropType.SLAYER_VERY_RARE_DROP_PURPLE : ToastUtils.DropType.SLAYER_CRAZY_RARE_DROP;
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, dropType, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (rareDrop2SpaceBracketPattern.matches())
                        {
                            var name = rareDrop2SpaceBracketPattern.group("item");
                            var magicFind = rareDrop2SpaceBracketPattern.group(2);
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, ToastUtils.DropType.SLAYER_RARE_DROP, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                    }

                    if (SkyBlockcatiaSettings.INSTANCE.petDisplayMode == ToastMode.TOAST || SkyBlockcatiaSettings.INSTANCE.petDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        var isToast = SkyBlockcatiaSettings.INSTANCE.petDisplayMode == ToastMode.TOAST;

                        if (petLevelUpPattern.matches())
                        {
                            var name = petLevelUpPattern.group("name");
                            var level = petLevelUpPattern.group("level");
                            var type = SBPets.PETS.getTypeByName(ChatFormatting.stripFormatting(name).replace(" ", "_").toUpperCase(Locale.ROOT));
                            var itemStack = type.getPetItem();
                            itemStack.setHoverName(TextComponentUtils.component(name));
                            NumericToast.addValueOrUpdate(mc.getToasts(), ToastUtils.DropType.PET_LEVEL_UP, Integer.parseInt(level), itemStack, true, type);
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (petDropPattern.matches())
                        {
                            var name = petDropPattern.group("item");
                            var magicFind = petDropPattern.group("mf");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, ToastUtils.DropType.PET_DROP, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                    }
                }
                if (cancelMessage)
                {
                    return CompoundEventResult.interruptTrue(TextComponent.EMPTY);
                }
            }
        }
        return CompoundEventResult.interruptDefault(smessage);
    }

    public void onPressKey()
    {
        var mc = Minecraft.getInstance();

        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (KeyBindingHandler.KEY_SB_ENDER_CHEST.isDown())
            {
                mc.player.chat("/enderchest");
            }
            else if (KeyBindingHandler.KEY_SB_CRAFTED_MINIONS.isDown())
            {
                mc.player.chat("/craftedgenerators");
            }
            else if (KeyBindingHandler.KEY_SB_CRAFTING_TABLE.isDown())
            {
                mc.player.chat("/craft");
            }
            else if (KeyBindingHandler.KEY_SB_MENU.isDown())
            {
                mc.player.chat("/sbmenu");
            }
            else if (KeyBindingHandler.KEY_SB_VIEW_RECIPE.isDown() && mc.screen == null)
            {
                mc.player.chat("/recipes");
            }
            else if (KeyBindingHandler.KEY_SB_PETS.isDown())
            {
                mc.player.chat("/pets");
            }
            else if (KeyBindingHandler.KEY_SB_WARDROBE.isDown())
            {
                mc.player.chat("/wardrobe");
            }
            else if (KeyBindingHandler.KEY_SB_HOTM.isDown())
            {
                mc.player.chat("/hotm");
            }
        }

        if (KeyBindingHandler.KEY_SB_SETTINGS.isDown())
        {
            mc.setScreen(new SkyBlockSettingsScreen());
        }
        else if (KeyBindingHandler.KEY_SB_API_VIEWER.isDown())
        {
            if (StringUtil.isNullOrEmpty(PlatformConfig.getApiKey()))
            {
                ClientUtils.printClientMessage("Couldn't open API Viewer, Empty API Key in the Config!", ChatFormatting.RED);
                ClientUtils.printClientMessage(TextComponentUtils.formatted("Make sure you're in the Hypixel!", ChatFormatting.YELLOW).append(TextComponentUtils.formatted(" Click Here to create an API key", ChatFormatting.GOLD).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/api new")))));
                return;
            }
            if (!PlatformConfig.getApiKey().matches(SkyBlockEventHandler.UUID_PATTERN_STRING))
            {
                ClientUtils.printClientMessage("Invalid UUID for Hypixel API Key!", ChatFormatting.RED);
                ClientUtils.printClientMessage("Example UUID pattern: " + UUID.randomUUID(), ChatFormatting.YELLOW);
                return;
            }
            if (mc.crosshairPickEntity instanceof RemotePlayer player)
            {
                if (mc.player.connection.getOnlinePlayers().stream().anyMatch(info -> info.getProfile().getName().equals(player.getName().getString())))
                {
                    mc.setScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.PLAYER, player.getDisplayName().getString(), "", ""));
                }
                else
                {
                    mc.setScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.EMPTY));
                }
            }
            else
            {
                mc.setScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.EMPTY));
            }
        }
    }

    private EventResult onWorldJoin(Entity entity, Level level)
    {
        if (entity == Minecraft.getInstance().player)
        {
            this.previousInventory = null;
            SkyBlockEventHandler.dragonHealth = 0;
            ITEM_DROP_CHECK_LIST.clear();

            if (!this.initApiData && Utils.isHypixel())
            {
                var exec = Executors.newSingleThreadScheduledExecutor();
                exec.scheduleAtFixedRate(MainEventHandler::getBazaarData, 0, 10, TimeUnit.SECONDS);
                var exec1 = Executors.newSingleThreadScheduledExecutor();
                exec1.scheduleAtFixedRate(PetStats::scheduleDownloadPetStats, 0, 2, TimeUnit.MINUTES);
                this.initApiData = true;
                return EventResult.interruptTrue();
            }
        }
        return EventResult.pass();
    }

    public void onDisconnectedFromServerEvent()
    {
        SignSelectionList.clearAll();
        SkyBlockEventHandler.dragonHealth = 0;
    }

    public void onItemTooltip(ItemStack itemStack, List<Component> tooltip)
    {
        try
        {
            var mc = Minecraft.getInstance();
            var calendar = Calendar.getInstance();

            if (itemStack.hasTag())
            {
                var extraAttrib = itemStack.getTag().getCompound("ExtraAttributes");
                var insertAt = tooltip.size();
                insertAt--; // rarity

                if (mc.options.advancedItemTooltips)
                {
                    insertAt -= 2; // item name + nbt

                    if (itemStack.isDamaged())
                    {
                        insertAt--; // 1 damage
                    }
                }

                if (SkyBlockcatiaSettings.INSTANCE.showObtainedDate && extraAttrib.contains("timestamp"))
                {
                    var formatter = DateTimeFormatter.ofPattern("[MM/dd/yy h:mm a][M/dd/yy h:mm a][M/d/yy h:mm a]", Locale.ENGLISH);
                    var datetime = LocalDateTime.parse(extraAttrib.getString("timestamp"), formatter);
                    var convertedDatetime = Date.from(datetime.atZone(ZoneId.systemDefault()).toInstant());
                    var formatted = new SimpleDateFormat("d MMMM yyyy h:mm aa", Locale.ROOT).format(convertedDatetime);
                    tooltip.add(insertAt++, TextComponentUtils.formatted("Obtained: " + formatted, ChatFormatting.GRAY));
                }
                if (SkyBlockcatiaSettings.INSTANCE.bazaarOnItemTooltip)
                {
                    for (var entry : MainEventHandler.BAZAAR_DATA.entrySet())
                    {
                        var status = entry.getValue().status();

                        if (extraAttrib.getString("id").equals(entry.getKey()))
                        {
                            if (ClientUtils.isShiftKeyDown())
                            {
                                if (StringUtil.isNullOrEmpty(PlatformConfig.getApiKey()))
                                {
                                    tooltip.add(insertAt++, TextComponentUtils.formatted("Couldn't get bazaar data, Empty API Key in the Config!", ChatFormatting.RED));
                                }
                                else if (!PlatformConfig.getApiKey().matches(SkyBlockEventHandler.UUID_PATTERN_STRING))
                                {
                                    tooltip.add(insertAt++, TextComponentUtils.formatted("Invalid UUID for Hypixel API Key!", ChatFormatting.RED));
                                }
                                else
                                {
                                    var buyStack = 64 * status.buyPrice();
                                    var sellStack = 64 * status.sellPrice();
                                    var buyCurrent = itemStack.getCount() * status.buyPrice();
                                    var sellCurrent = itemStack.getCount() * status.sellPrice();
                                    tooltip.add(insertAt++, TextComponentUtils.formatted("Buy/Sell (Stack): ", ChatFormatting.GRAY).append(ChatFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(buyStack) + ChatFormatting.YELLOW + "/" + ChatFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(sellStack) + " coins"));

                                    if (itemStack.getCount() > 1 && itemStack.getCount() < 64)
                                    {
                                        tooltip.add(insertAt++, TextComponentUtils.formatted("Buy/Sell (Current): ", ChatFormatting.GRAY).append(ChatFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(buyCurrent) + ChatFormatting.YELLOW + "/" + ChatFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(sellCurrent) + " coins"));
                                    }

                                    tooltip.add(insertAt++, TextComponentUtils.formatted("Buy/Sell (One): ", ChatFormatting.GRAY).append(ChatFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(status.buyPrice()) + ChatFormatting.YELLOW + "/" + ChatFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(status.sellPrice()) + " coins"));
                                    tooltip.add(insertAt++, TextComponentUtils.formatted("Last Updated: ", ChatFormatting.GRAY).append(TextComponentUtils.formatted(TimeUtils.getRelativeTime(entry.getValue().lastUpdated()), ChatFormatting.WHITE)));
                                }
                            }
                            else
                            {
                                tooltip.add(insertAt++, TextComponentUtils.formatted("Press <SHIFT> to view Bazaar Buy/Sell", ChatFormatting.GRAY));
                            }
                        }
                    }
                }
            }

            for (var i = 0; i < tooltip.size(); i++)
            {
                var lore = TextComponentUtils.fromJson(tooltip.get(i));

                if (mc.screen instanceof ContainerScreen)
                {
                    var name = mc.screen.getTitle().getString();

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
        catch (Exception ignored)
        {
        }
    }

    /**
     * Credit to codes.biscuit.skyblockaddons.utils.InventoryUtils
     */
    private void getInventoryDifference(Minecraft mc, NonNullList<ItemStack> currentInventory)
    {
        var newInventory = this.copyInventory(currentInventory);
        var previousInventoryMap = Maps.<String, ItemDropDiff>newConcurrentMap();
        var newInventoryMap = Maps.<String, ItemDropDiff>newConcurrentMap();
        SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.removeIf(this::removeUndisplayedToast);

        if (this.previousInventory != null)
        {
            for (var i = 0; i < newInventory.size(); i++)
            {
                var previousItem = this.previousInventory.get(i);
                var newItem = newInventory.get(i);

                if (!previousItem.isEmpty())
                {
                    int amount;

                    if (previousInventoryMap.containsKey(previousItem.getHoverName().getString()))
                    {
                        amount = previousInventoryMap.get(previousItem.getHoverName().getString()).count + previousItem.getCount();
                    }
                    else
                    {
                        amount = previousItem.getCount();
                    }

                    var extraAttributes = previousItem.getOrCreateTagElement("ExtraAttributes");

                    if (extraAttributes != null)
                    {
                        extraAttributes = extraAttributes.copy();
                    }
                    previousInventoryMap.put(previousItem.getHoverName().getString(), new ItemDropDiff(previousItem, amount, extraAttributes));
                }
                if (!newItem.isEmpty())
                {
                    int amount;

                    if (newInventoryMap.containsKey(newItem.getHoverName().getString()))
                    {
                        amount = newInventoryMap.get(newItem.getHoverName().getString()).count + newItem.getCount();
                    }
                    else
                    {
                        amount = newItem.getCount();
                    }

                    var extraAttributes = newItem.getOrCreateTagElement("ExtraAttributes");

                    if (extraAttributes != null)
                    {
                        extraAttributes = extraAttributes.copy();
                    }
                    newInventoryMap.put(newItem.getHoverName().getString(), new ItemDropDiff(newItem, amount, extraAttributes));
                }
            }

            var keySet = Sets.newHashSet(previousInventoryMap.keySet());
            keySet.addAll(newInventoryMap.keySet());

            keySet.forEach(key ->
            {
                var previousAmount = 0;

                if (previousInventoryMap.containsKey(key))
                {
                    previousAmount = previousInventoryMap.get(key).count;
                }

                var newAmount = 0;

                if (newInventoryMap.containsKey(key))
                {
                    newAmount = newInventoryMap.get(key).count;
                }

                var newDiff = newInventoryMap.getOrDefault(key, previousInventoryMap.get(key));
                var diff = newAmount - previousAmount;

                if (diff != 0)
                {
                    var newItem = newDiff.itemStack;

                    if (!newItem.isEmpty())
                    {
                        for (var iterator = SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.iterator(); iterator.hasNext(); )
                        {
                            var drop = iterator.next();
                            var dropName = drop.getName();

                            if (drop.getType() == ToastUtils.DropType.PET_DROP)
                            {
                                if (("[Lvl 1] " + dropName).equals(ChatFormatting.stripFormatting(key)))
                                {
                                    newItem.setCount(diff);

                                    if (mc.getToasts().queued.add(new ItemDropsToast(newItem, drop.getType(), drop.getMagicFind())))
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

                                if (dropName.equals(key.replaceAll("§r$", "")) || !drop.getType().matches(ToastUtils.DropCondition.FORMAT) && dropName.equals(ChatFormatting.stripFormatting(key)))
                                {
                                    newItem.setCount(diff);

                                    if (drop.getToastType() == ToastType.DROP)
                                    {
                                        if (mc.getToasts().queued.add(new ItemDropsToast(newItem, drop.getType(), drop.getMagicFind())))
                                        {
                                            iterator.remove();
                                        }
                                    }
                                    else
                                    {
                                        if (mc.getToasts().queued.add(new GiftToast(newItem, drop.getType(), drop.getType() == ToastUtils.DropType.SANTA_TIER)))
                                        {
                                            iterator.remove();
                                        }
                                    }
                                }
                                else if (drop.getType().matches(ToastUtils.DropCondition.CONTAINS) && key.contains(dropName))
                                {
                                    if (mc.getToasts().queued.add(new ItemDropsToast(newItem, drop.getType(), drop.getMagicFind())))
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
        var copy = Lists.<ItemStack>newArrayListWithCapacity(inventory.size());

        for (var item : inventory)
        {
            copy.add(item.copy());
        }
        return copy;
    }

    private static void addVisitingToast(Minecraft mc, String name)
    {
        CommonUtils.runAsync(() -> mc.getToasts().addToast(new VisitIslandToast(name)));
    }

    private static void replaceText(String lore, Calendar calendar, List<Component> tooltip, int i, String startWith, String newText)
    {
        if (lore.startsWith(startWith))
        {
            lore = lore.substring(lore.indexOf(":") + 2).replaceAll("[^a-zA-Z0-9 ]|^[a-zA-Z ]+", "");
            var timeEstimate = Arrays.stream(lore.split(" ")).map(time -> time.replaceAll("[^0-9]+", "")).toArray(String[]::new);

            var dayF = 0;
            var hourF = 0;
            int minuteF;
            int secondF;

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
            var date1 = new SimpleDateFormat("EEEE h:mm:ss a", Locale.ROOT).format(calendar.getTime());
            var date2 = new SimpleDateFormat("d MMMMM yyyy", Locale.ROOT).format(calendar.getTime());
            var newStrings = Lists.<Component>newArrayList();
            newStrings.add(TextComponentUtils.formatted(newText, ChatFormatting.GRAY));
            newStrings.add(TextComponentUtils.formatted(date1, ChatFormatting.YELLOW));
            newStrings.add(TextComponentUtils.formatted(date2, ChatFormatting.YELLOW));

            if (!ClientUtils.isShiftKeyDown())
            {
                tooltip.add(i + 1, TextComponentUtils.formatted("Press <SHIFT> to view exact time", ChatFormatting.GRAY));
            }
            else
            {
                tooltip.remove(i);

                for (var text : newStrings)
                {
                    tooltip.add(i++, text);
                }
            }
        }
    }

    private static void replaceEstimatedTime(String lore, List<Component> tooltip, int i)
    {
        var mat = ESTIMATED_TIME_PATTERN.matcher(lore);

        if (mat.find())
        {
            lore = mat.group("time");
            var calendar = Calendar.getInstance();
            var timeEstimate = Arrays.stream(lore.split(" ")).map(time -> time.replaceAll("[^0-9]+", "")).toArray(String[]::new);
            var isHour = lore.endsWith("h");

            var dayF = 0;
            var hourF = 0;
            var minuteF = 0;
            var secondF = 0;

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
            var date1 = new SimpleDateFormat("EEEE h:mm:ss a", Locale.ROOT).format(calendar.getTime());

            if (timeEstimate.length == 1)
            {
                date1 = new SimpleDateFormat("EEEE h:00 a", Locale.ROOT).format(calendar.getTime());
            }

            var date2 = new SimpleDateFormat("d MMMMM yyyy", Locale.ROOT).format(calendar.getTime());

            if (!ClientUtils.isShiftKeyDown())
            {
                tooltip.add(i + 1, TextComponentUtils.formatted("Press <SHIFT> to view exact time", ChatFormatting.GRAY));
            }
            else
            {
                tooltip.add(i + 1, TextComponentUtils.formatted(date2 + " " + date1, ChatFormatting.YELLOW));
            }
        }
    }

    private static void replaceBankInterestTime(String lore, Calendar calendar, List<Component> tooltip, int i, String startWith)
    {
        if (lore.startsWith(startWith))
        {
            lore = lore.substring(lore.indexOf(":") + 2).replaceAll("[^0-9]+", " ");
            var timeEstimate = Arrays.stream(lore.split(" ")).map(time -> time.replaceAll("[^0-9]+", "")).toArray(String[]::new);
            var hourF = 0;
            var minuteF = 0;
            var secondF = 0;

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
            var date1 = new SimpleDateFormat("EEEE h:mm:ss a", Locale.ROOT).format(calendar.getTime());

            if (timeEstimate.length == 1)
            {
                date1 = new SimpleDateFormat("EEEE h:00 a", Locale.ROOT).format(calendar.getTime());
            }

            var date2 = new SimpleDateFormat("d MMMMM yyyy", Locale.ROOT).format(calendar.getTime());
            var newStrings = Lists.<Component>newArrayList();
            newStrings.add(TextComponentUtils.formatted("Interest receive at: ", ChatFormatting.GRAY));
            newStrings.add(TextComponentUtils.formatted(date1, ChatFormatting.YELLOW));
            newStrings.add(TextComponentUtils.formatted(date2, ChatFormatting.YELLOW));

            if (!ClientUtils.isShiftKeyDown())
            {
                tooltip.add(i + 1, TextComponentUtils.formatted("Press <SHIFT> to view exact time", ChatFormatting.GRAY));
            }
            else
            {
                tooltip.remove(i);

                for (var text : newStrings)
                {
                    tooltip.add(i++, text);
                }
            }
        }
    }

    private static void replaceAuctionTime(String lore, Calendar calendar, List<Component> tooltip, int i, String startWith)
    {
        var mc = Minecraft.getInstance();

        if (lore.startsWith(startWith))
        {
            var isDay = lore.endsWith("d");
            lore = lore.substring(lore.indexOf(":") + 2).replaceAll("[^0-9]+", " ");
            var timeEstimate = Arrays.stream(lore.split(" ")).map(time -> time.replaceAll("[^0-9]+", "")).toArray(String[]::new);
            var dayF = 0;
            var hourF = 0;
            var minuteF = 0;
            var secondF = 0;
            var newStrings = Lists.<Component>newArrayList();

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
            var date1 = new SimpleDateFormat("EEEE h:mm:ss a", Locale.ROOT).format(calendar.getTime());

            if (timeEstimate.length == 1)
            {
                date1 = new SimpleDateFormat("EEEE h:00 a", Locale.ROOT).format(calendar.getTime());
            }

            var date2 = new SimpleDateFormat("d MMMMM yyyy", Locale.ROOT).format(calendar.getTime());

            if (mc.screen instanceof ContainerScreen)
            {
                var name = mc.screen.getTitle().getString();

                if (name.equals("Auction View"))
                {
                    newStrings.add(TextComponentUtils.formatted("Ends at: " + ChatFormatting.YELLOW + date1 + ", " + date2, ChatFormatting.GRAY));
                }
                else
                {
                    newStrings.add(TextComponentUtils.formatted("Ends at: ", ChatFormatting.GRAY));
                    newStrings.add(TextComponentUtils.formatted(date1, ChatFormatting.YELLOW));
                    newStrings.add(TextComponentUtils.formatted(date2, ChatFormatting.YELLOW));
                }
            }

            if (!ClientUtils.isShiftKeyDown())
            {
                tooltip.add(i + 1, TextComponentUtils.formatted("Press <SHIFT> to view exact time", ChatFormatting.GRAY));
            }
            else
            {
                tooltip.remove(i);

                for (var text : newStrings)
                {
                    tooltip.add(i++, text);
                }
            }
        }
    }

    record ItemDropDiff(ItemStack itemStack, int count, CompoundTag extraAttributes) {}
}