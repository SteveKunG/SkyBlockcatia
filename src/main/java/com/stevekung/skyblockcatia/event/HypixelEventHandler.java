package com.stevekung.skyblockcatia.event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.stevekung.skyblockcatia.config.ConfigManagerIN;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.config.ToastMode;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.gui.SignSelectionList;
import com.stevekung.skyblockcatia.gui.api.GuiSkyBlockAPIViewer;
import com.stevekung.skyblockcatia.gui.toasts.*;
import com.stevekung.skyblockcatia.gui.toasts.ToastUtils.ToastType;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class HypixelEventHandler
{
    private static final Pattern LETTERS_NUMBERS = Pattern.compile("[^a-z A-Z:0-9/']");
    private static final Pattern VISIT_ISLAND_PATTERN = Pattern.compile("(?:\\[SkyBlock\\]|\\[SkyBlock\\] (?:\\[VIP?\\u002B{0,1}\\]|\\[MVP?\\u002B{0,2}\\]|\\[YOUTUBE\\])) (?<name>\\w+) is visiting Your Island!");
    public static final String UUID_PATTERN_STRING = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    private static final Pattern UUID_PATTERN = Pattern.compile("Your new API key is (?<uuid>" + HypixelEventHandler.UUID_PATTERN_STRING + ")");
    private static final String RANKED_PATTERN = "(?:(?:\\w)|(?:\\[VIP?\\u002B{0,1}\\]|\\[MVP?\\u002B{0,2}\\]|\\[YOUTUBE\\]) \\w)+";
    private static final Pattern PET_CARE_PATTERN = Pattern.compile("\\u00a7r\\u00a7aI'm currently taking care of your \\u00a7r(?<pet>\\u00a7[0-9a-fk-or][\\w ]+)\\u00a7r\\u00a7a! You can pick it up in (?:(?<day>[\\d]+) day(?:s){0,1} ){0,1}(?:(?<hour>[\\d]+) hour(?:s){0,1} ){0,1}(?:(?<minute>[\\d]+) minute(?:s){0,1} ){0,1}(?:(?<second>[\\d]+) second(?:s){0,1}).\\u00a7r");
    private static final Pattern DRAGON_DOWN_PATTERN = Pattern.compile("\\u00A7r +\\u00A7r\\u00A76\\u00A7l(?<dragon>SUPERIOR|STRONG|YOUNG|OLD|PROTECTOR|UNSTABLE|WISE) DRAGON DOWN!\\u00a7r");
    private static final Pattern DRAGON_SPAWNED_PATTERN = Pattern.compile("\\u00A75\\u262C \\u00A7r\\u00A7d\\u00A7lThe \\u00A7r\\u00A75\\u00A7c\\u00A7l(?<dragon>Superior|Strong|Young|Unstable|Wise|Old|Protector) Dragon\\u00A7r\\u00A7d\\u00A7l has spawned!\\u00A7r");

    // Item Drop Stuff
    private static final String ITEM_PATTERN = "[\\w\\'\\u25C6\\[\\] -]+";
    private static final String DROP_PATTERN = "(?<item>(?:\\u00a7r\\u00a7[0-9a-fk-or]){0,1}(?:\\u00a7[0-9a-fk-or]){0,1}" + ITEM_PATTERN + "(?:[\\(][^\\)]" + ITEM_PATTERN + "[\\)]){0,1})";
    private static final Pattern RARE_DROP_PATTERN = Pattern.compile("\\u00a7r\\u00a76\\u00a7lRARE DROP! " + DROP_PATTERN + "(?:\\b\\u00a7r\\b){0,1} ?(?:\\u00a7r\\u00a7b\\(\\+(?<mf>[0-9]+)% Magic Find!\\)\\u00a7r){0,1}");
    private static final Pattern RARE_DROP_2_SPACE_PATTERN = Pattern.compile("\\u00a7r\\u00a7b\\u00a7lRARE DROP! \\u00a7r\\u00a77\\(" + DROP_PATTERN + "\\u00a7r\\u00a77\\)(?:\\b\\u00a7r\\b){0,1} ?(?:\\u00a7r\\u00a7b\\(\\+(?<mf>[0-9]+)% Magic Find!\\)\\u00a7r){0,1}");
    private static final Pattern RARE_DROP_WITH_BRACKET_PATTERN = Pattern.compile("(?<type>\\u00a7r\\u00a79\\u00a7lVERY RARE|\\u00a7r\\u00a75\\u00a7lVERY RARE|\\u00a7r\\u00a7d\\u00a7lCRAZY RARE) DROP!  \\u00a7r\\u00a77\\(" + DROP_PATTERN + "\\u00a7r\\u00a77\\)(?:\\b\\u00a7r\\b){0,1} ?(?:\\u00a7r\\u00a7b\\(\\+(?<mf>[0-9]+)% Magic Find!\\)\\u00a7r){0,1}");
    private static final Pattern BOSS_DROP_PATTERN = Pattern.compile("(?:(?:" + GameProfileUtils.getUsername() + ")|(?:\\[VIP?\\u002B{0,1}\\]|\\[MVP?\\u002B{0,2}\\]|\\[YOUTUBE\\]) " + GameProfileUtils.getUsername() + ") has obtained " + DROP_PATTERN + "!");

    // Mythos Events
    private static final Pattern RARE_DROP_MYTHOS_PATTERN = Pattern.compile("RARE DROP! You dug out a " + DROP_PATTERN + "!");
    private static final Pattern COINS_MYTHOS_PATTERN = Pattern.compile("Wow! You dug out (?<coin>[0-9,]+) coins!");

    // Bank Interest/Allowance
    private static final Pattern BANK_INTEREST_PATTERN = Pattern.compile("Since you've been away you earned (?<coin>[0-9,]+) coins as interest in your personal bank account!");
    private static final Pattern ALLOWANCE_PATTERN = Pattern.compile("ALLOWANCE! You earned (?<coin>[0-9,]+) coins!");

    // Dungeons
    private static final Pattern DUNGEON_QUALITY_DROP_PATTERN = Pattern.compile("You found a Top Quality Item! " + DROP_PATTERN);
    private static final Pattern DUNGEON_REWARD_PATTERN = Pattern.compile(" +RARE REWARD! " + DROP_PATTERN);

    // Fish catch stuff
    private static final Pattern FISH_CATCH_PATTERN = Pattern.compile("(?<type>GOOD|GREAT) CATCH! You found a " + DROP_PATTERN + ".");
    private static final Pattern COINS_CATCH_PATTERN = Pattern.compile("(?<type>GOOD|GREAT) CATCH! You found (?<coin>[0-9,]+) Coins.");

    // Winter island stuff
    private static final Pattern COINS_GIFT_PATTERN = Pattern.compile("(?<type>COMMON|SWEET|RARE)! \\u002B(?<coin>[0-9,]+) coins gift with " + RANKED_PATTERN + "!");
    private static final Pattern SKILL_EXP_GIFT_PATTERN = Pattern.compile("(?<type>COMMON|SWEET|RARE)! \\u002B(?<exp>[0-9,]+) (?<skill>Farming|Mining|Combat|Foraging|Fishing|Enchanting|Alchemy)+ XP gift with " + RANKED_PATTERN + "!");
    private static final Pattern ITEM_DROP_GIFT_PATTERN = Pattern.compile("(?<type>COMMON|SWEET|RARE)! " + DROP_PATTERN + " gift with " + RANKED_PATTERN + "!");
    private static final Pattern SANTA_TIER_PATTERN = Pattern.compile("SANTA TIER! " + DROP_PATTERN + " gift with " + RANKED_PATTERN + "!");

    // Pet
    private static final Pattern PET_LEVEL_UP_PATTERN = Pattern.compile("\\u00a7r\\u00a7aYour (?<name>\\u00a7r\\u00a7[0-9a-fk-or][\\w ]+) \\u00a7r\\u00a7alevelled up to level \\u00a7r\\u00a79(?<level>\\d+)\\u00a7r\\u00a7a!\\u00a7r");
    private static final Pattern PET_DROP_PATTERN = Pattern.compile("PET DROP! " + DROP_PATTERN + " ?(?:\\(\\+(?<mf>[0-9]+)% Magic Find!\\)){0,1}");

    private static final List<String> LEFT_PARTY_MESSAGE = new ArrayList<>(Arrays.asList("You are not in a party and have been moved to the ALL channel!", "has disbanded the party!", "The party was disbanded because all invites have expired and all members have left."));
    private static final Map<String, String> RENAMED_DROP = ImmutableMap.<String, String>builder().put("\u25C6 Ice Rune", "\u25C6 Ice Rune I").build();
    public static boolean isSkyBlock = false;
    public static boolean foundSkyBlockPack;
    public static String skyBlockPackResolution = "16";
    public static SkyBlockLocation SKY_BLOCK_LOCATION = SkyBlockLocation.YOUR_ISLAND;
    public static String SKYBLOCK_AMPM = "";
    public static float dragonHealth;
    private static final List<ToastUtils.ItemDropCheck> ITEM_DROP_CHECK_LIST = new ArrayList<>();
    private List<ItemStack> previousInventory;
    private SkyBlockBossBar.DragonType dragonType;
    private final Minecraft mc;
    private boolean initVersionCheck;

    public HypixelEventHandler()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (this.mc.thePlayer != null)
        {
            if (event.phase == TickEvent.Phase.START)
            {
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
                        String scoreText = this.keepLettersAndNumbersOnly(EnumChatFormatting.getTextWithoutFormattingCodes(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score1.getPlayerName())));

                        if (scoreText.startsWith("Dragon HP: "))
                        {
                            try
                            {
                                HypixelEventHandler.dragonHealth = Float.valueOf(scoreText.replaceAll("[^\\d]", ""));

                                if (this.dragonType != null)
                                {
                                    SkyBlockBossBar.healthScale = HypixelEventHandler.dragonHealth / this.dragonType.getMaxHealth();
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
                        String scoreText = this.keepLettersAndNumbersOnly(EnumChatFormatting.getTextWithoutFormattingCodes(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score1.getPlayerName())));

                        if (scoreText.endsWith("am"))
                        {
                            HypixelEventHandler.SKYBLOCK_AMPM = " AM";
                        }
                        else if (scoreText.endsWith("pm"))
                        {
                            HypixelEventHandler.SKYBLOCK_AMPM = " PM";
                        }

                        for (SkyBlockLocation location : CachedEnum.locationValues)
                        {
                            if (scoreText.endsWith(location.getLocation()))
                            {
                                HypixelEventHandler.SKY_BLOCK_LOCATION = location;
                                found = true;
                                break;
                            }
                        }
                    }

                    if (scoreObj != null)
                    {
                        HypixelEventHandler.isSkyBlock = EnumChatFormatting.getTextWithoutFormattingCodes(scoreObj.getDisplayName()).contains("SKYBLOCK");
                    }
                    else
                    {
                        HypixelEventHandler.isSkyBlock = false;
                    }

                    if (!found)
                    {
                        HypixelEventHandler.SKY_BLOCK_LOCATION = SkyBlockLocation.NONE;
                    }
                    SkyBlockBossBar.renderBossBar = foundDrag;
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouseClick(MouseEvent event)
    {
        if (InfoUtils.INSTANCE.isHypixel())
        {
            if (event.button == 1 && event.buttonstate)
            {
                if (this.mc.pointedEntity != null && this.mc.pointedEntity instanceof EntityOtherPlayerMP)
                {
                    EntityOtherPlayerMP player = (EntityOtherPlayerMP)this.mc.pointedEntity;

                    if (!this.mc.thePlayer.isSneaking() && this.mc.thePlayer.getHeldItem() == null && ExtendedConfig.instance.rightClickToAddParty)
                    {
                        if (this.mc.thePlayer.sendQueue.getPlayerInfoMap().stream().anyMatch(info -> info.getGameProfile().getName().equals(player.getName())))
                        {
                            this.mc.thePlayer.sendChatMessage("/p " + player.getName());
                            event.setCanceled(true);
                        }
                    }
                    if (this.mc.thePlayer.isSneaking() && ExtendedConfig.instance.sneakToTradeOtherPlayerIsland)
                    {
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
                            String scoreText = this.keepLettersAndNumbersOnly(EnumChatFormatting.getTextWithoutFormattingCodes(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score1.getPlayerName())));

                            if (scoreText.endsWith("'s Island"))
                            {
                                this.mc.thePlayer.sendChatMessage("/trade " + player.getName());
                            }
                        }
                    }
                }
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
            Matcher visitIslandMatcher = HypixelEventHandler.VISIT_ISLAND_PATTERN.matcher(message);
            Matcher uuidMatcher = HypixelEventHandler.UUID_PATTERN.matcher(message);
            Matcher petCareMatcher = HypixelEventHandler.PET_CARE_PATTERN.matcher(formattedMessage);
            Matcher dragonDownMatcher = HypixelEventHandler.DRAGON_DOWN_PATTERN.matcher(formattedMessage);
            Matcher dragonSpawnedMatcher = HypixelEventHandler.DRAGON_SPAWNED_PATTERN.matcher(formattedMessage);

            // Item Drop matcher
            Matcher rareDropPattern = HypixelEventHandler.RARE_DROP_PATTERN.matcher(formattedMessage);
            Matcher bossDropPattern = HypixelEventHandler.BOSS_DROP_PATTERN.matcher(message);

            // Mythos Events matcher
            Matcher rareDropMythosPattern = HypixelEventHandler.RARE_DROP_MYTHOS_PATTERN.matcher(message);
            Matcher coinsMythosPattern = HypixelEventHandler.COINS_MYTHOS_PATTERN.matcher(message);

            // Bank Interest/Allowance
            Matcher bankInterestPattern = HypixelEventHandler.BANK_INTEREST_PATTERN.matcher(message);
            Matcher allowancePattern = HypixelEventHandler.ALLOWANCE_PATTERN.matcher(message);

            // Dungeons matcher
            Matcher dungeonQualityDropPattern = HypixelEventHandler.DUNGEON_QUALITY_DROP_PATTERN.matcher(message);
            Matcher dungeonRewardPattern = HypixelEventHandler.DUNGEON_REWARD_PATTERN.matcher(message);

            // Fish catch matcher
            Matcher fishCatchPattern = HypixelEventHandler.FISH_CATCH_PATTERN.matcher(message);
            Matcher coinsCatchPattern = HypixelEventHandler.COINS_CATCH_PATTERN.matcher(message);

            // Slayer Drop matcher
            Matcher rareDropBracketPattern = HypixelEventHandler.RARE_DROP_WITH_BRACKET_PATTERN.matcher(formattedMessage);
            Matcher rareDrop2SpaceBracketPattern = HypixelEventHandler.RARE_DROP_2_SPACE_PATTERN.matcher(formattedMessage);

            // Gift matcher
            Matcher coinsGiftPattern = HypixelEventHandler.COINS_GIFT_PATTERN.matcher(message);
            Matcher skillExpGiftPattern = HypixelEventHandler.SKILL_EXP_GIFT_PATTERN.matcher(message);
            Matcher itemDropGiftPattern = HypixelEventHandler.ITEM_DROP_GIFT_PATTERN.matcher(message);
            Matcher santaTierPattern = HypixelEventHandler.SANTA_TIER_PATTERN.matcher(message);

            // Pet
            Matcher petLevelUpPattern = HypixelEventHandler.PET_LEVEL_UP_PATTERN.matcher(formattedMessage);
            Matcher petDropPattern = HypixelEventHandler.PET_DROP_PATTERN.matcher(message);

            if (event.type == 0 || this.mc.isSingleplayer() && SkyBlockcatiaMod.isDevelopment)
            {
                if (message.contains("Illegal characters in chat") || message.contains("A kick occurred in your connection"))
                {
                    cancelMessage = true;
                }
                else if (message.contains("You were spawned in Limbo."))
                {
                    event.message = JsonUtils.create(message).setChatStyle(JsonUtils.green());
                }

                if (visitIslandMatcher.matches())
                {
                    String name = visitIslandMatcher.group("name");

                    if (ToastMode.getById(ExtendedConfig.instance.visitIslandToastMode).equalsIgnoreCase("toast") || ToastMode.getById(ExtendedConfig.instance.visitIslandToastMode).equalsIgnoreCase("chat_and_toast"))
                    {
                        HypixelEventHandler.addVisitingToast(name);
                        LoggerIN.logToast(formattedMessage);
                    }
                    cancelMessage = ToastMode.getById(ExtendedConfig.instance.visitIslandToastMode).equalsIgnoreCase("toast") || ToastMode.getById(ExtendedConfig.instance.visitIslandToastMode).equalsIgnoreCase("disabled");
                }
                else if (uuidMatcher.matches())
                {
                    SkyBlockAPIUtils.setApiKeyFromServer(uuidMatcher.group("uuid"));
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

                if (HypixelEventHandler.LEFT_PARTY_MESSAGE.stream().anyMatch(pmess -> message.equals(pmess)))
                {
                    ExtendedConfig.instance.chatMode = 0;
                    ExtendedConfig.instance.save();
                }
                if (ExtendedConfig.instance.leavePartyWhenLastEyePlaced && message.contains(" Brace yourselves! (8/8)"))
                {
                    this.mc.thePlayer.sendChatMessage("/p leave");
                }
                if (ExtendedConfig.instance.automaticOpenMaddox)
                {
                    for (IChatComponent component : event.message.getSiblings())
                    {
                        if (message.contains("[OPEN MENU]") && component.getChatStyle().getChatClickEvent() != null)
                        {
                            this.mc.thePlayer.sendChatMessage(component.getChatStyle().getChatClickEvent().getValue());
                        }
                    }
                }

                if (HypixelEventHandler.isSkyBlock || SkyBlockcatiaMod.isDevelopment)
                {
                    if (ExtendedConfig.instance.currentServerDay && message.startsWith("Sending to server"))
                    {
                        InfoUtils.INSTANCE.schedule(() ->
                        {
                            long day = this.mc.theWorld.getWorldTime() / 24000L;
                            EnumChatFormatting dayColor = day >= 29 ? EnumChatFormatting.RED : EnumChatFormatting.GREEN;

                            if (HypixelEventHandler.isSkyBlock)
                            {
                                ClientUtils.printClientMessage(JsonUtils.create("Current server day: ").setChatStyle(JsonUtils.yellow().setBold(true)).appendSibling(JsonUtils.create(String.valueOf(day)).setChatStyle(JsonUtils.style().setBold(false).setColor(dayColor))));
                            }
                        }, 2500);
                    }
                    if (dragonDownMatcher.matches())
                    {
                        this.clearBossData();

                        if (HypixelEventHandler.isSkyBlock && ExtendedConfig.instance.showHitboxWhenDragonSpawned)
                        {
                            this.mc.getRenderManager().setDebugBoundingBox(false);
                        }
                    }
                    if (dragonSpawnedMatcher.matches())
                    {
                        String dragon = dragonSpawnedMatcher.group("dragon");
                        SkyBlockBossBar.DragonType type = SkyBlockBossBar.DragonType.valueOf(dragon.toUpperCase(Locale.ROOT));
                        SkyBlockBossBar.renderBossBar = true;
                        SkyBlockBossBar.bossName = EnumChatFormatting.RED + type.getName();
                        HUDRenderEventHandler.foundDragon = true;
                        this.dragonType = type;

                        if (HypixelEventHandler.isSkyBlock && ExtendedConfig.instance.showHitboxWhenDragonSpawned)
                        {
                            this.mc.getRenderManager().setDebugBoundingBox(true);
                        }
                    }

                    if (bankInterestPattern.matches())
                    {
                        String coin = bankInterestPattern.group("coin");
                        CoinType coinType = CoinType.TYPE_3;
                        ItemStack coinSkull = RenderUtils.getSkullItemStack(coinType.getId(), coinType.getValue());
                        NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), ToastUtils.DropType.BANK_INTEREST, Integer.valueOf(coin.replace(",", "")), coinSkull, "Coins");
                        LoggerIN.logToast(formattedMessage);
                        cancelMessage = true;
                    }
                    else if (allowancePattern.matches())
                    {
                        String coin = allowancePattern.group("coin");
                        CoinType coinType = CoinType.TYPE_3;
                        ItemStack coinSkull = RenderUtils.getSkullItemStack(coinType.getId(), coinType.getValue());
                        NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), ToastUtils.DropType.ALLOWANCE, Integer.valueOf(coin.replace(",", "")), coinSkull, "Coins");
                        LoggerIN.logToast(formattedMessage);
                        cancelMessage = true;
                    }

                    if (ToastMode.getById(ExtendedConfig.instance.fishCatchToastMode).equalsIgnoreCase("toast") || ToastMode.getById(ExtendedConfig.instance.fishCatchToastMode).equalsIgnoreCase("chat_and_toast"))
                    {
                        if (fishCatchPattern.matches())
                        {
                            String dropType = fishCatchPattern.group("type");
                            String name = fishCatchPattern.group("item");
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, dropType.equals("GOOD") ? ToastUtils.DropType.GOOD_CATCH : ToastUtils.DropType.GREAT_CATCH, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = ToastMode.getById(ExtendedConfig.instance.fishCatchToastMode).equalsIgnoreCase("toast");
                        }
                        else if (coinsCatchPattern.matches())
                        {
                            String type = coinsCatchPattern.group("type");
                            String coin = coinsCatchPattern.group("coin");
                            CoinType coinType = type.equals("GOOD") ? CoinType.TYPE_1 : CoinType.TYPE_2;
                            ItemStack coinSkull = RenderUtils.getSkullItemStack(coinType.getId(), coinType.getValue());
                            NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), type.equals("GOOD") ? ToastUtils.DropType.GOOD_CATCH_COINS : ToastUtils.DropType.GREAT_CATCH_COINS, Integer.valueOf(coin.replace(",", "")), coinSkull, "Coins");
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = ToastMode.getById(ExtendedConfig.instance.fishCatchToastMode).equalsIgnoreCase("toast");
                        }
                    }

                    if (ToastMode.getById(ExtendedConfig.instance.giftToastMode).equalsIgnoreCase("toast") || ToastMode.getById(ExtendedConfig.instance.giftToastMode).equalsIgnoreCase("chat_and_toast"))
                    {
                        if (coinsGiftPattern.matches())
                        {
                            String type = coinsGiftPattern.group("type");
                            String coin = coinsGiftPattern.group("coin");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            ItemStack coinSkull = RenderUtils.getSkullItemStack(CoinType.TYPE_1.getId(), CoinType.TYPE_1.getValue());
                            NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), rarity, Integer.valueOf(coin.replace(",", "")), coinSkull, "Coins");
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = ToastMode.getById(ExtendedConfig.instance.giftToastMode).equalsIgnoreCase("toast");
                        }
                        else if (skillExpGiftPattern.matches())
                        {
                            String type = skillExpGiftPattern.group("type");
                            String exp = skillExpGiftPattern.group("exp");
                            String skill = skillExpGiftPattern.group("skill");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), rarity, Integer.valueOf(exp.replace(",", "")), null, skill);
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = ToastMode.getById(ExtendedConfig.instance.giftToastMode).equalsIgnoreCase("toast");
                        }
                        else if (itemDropGiftPattern.matches())
                        {
                            String type = itemDropGiftPattern.group("type");
                            String name = itemDropGiftPattern.group("item");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, rarity, ToastUtils.ToastType.GIFT));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = ToastMode.getById(ExtendedConfig.instance.giftToastMode).equalsIgnoreCase("toast");
                        }
                        else if (santaTierPattern.matches())
                        {
                            String name = santaTierPattern.group("item");
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.SANTA_TIER, ToastUtils.ToastType.GIFT));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = ToastMode.getById(ExtendedConfig.instance.giftToastMode).equalsIgnoreCase("toast");
                        }
                    }

                    if (ToastMode.getById(ExtendedConfig.instance.rareDropToastMode).equalsIgnoreCase("toast") || ToastMode.getById(ExtendedConfig.instance.rareDropToastMode).equalsIgnoreCase("chat_and_toast"))
                    {
                        boolean isToast = ToastMode.getById(ExtendedConfig.instance.rareDropToastMode).equalsIgnoreCase("toast");

                        if (message.contains("You destroyed an Ender Crystal!"))
                        {
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck("Crystal Fragment", ToastUtils.DropType.DRAGON_CRYSTAL_FRAGMENT, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }

                        if (rareDropPattern.matches())
                        {
                            String name = rareDropPattern.group("item");
                            String magicFind = rareDropPattern.group("mf");
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, ToastUtils.DropType.RARE_DROP, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (rareDropMythosPattern.matches())
                        {
                            String name = rareDropMythosPattern.group("item");
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.RARE_DROP, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (coinsMythosPattern.matches())
                        {
                            String coin = coinsMythosPattern.group("coin");
                            CoinType coinType = CoinType.TYPE_1;
                            ItemStack coinSkull = RenderUtils.getSkullItemStack(coinType.getId(), coinType.getValue());
                            NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), ToastUtils.DropType.MYTHOS_COINS, Integer.valueOf(coin.replace(",", "")), coinSkull, "Coins");
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (bossDropPattern.matches())
                        {
                            String name = bossDropPattern.group("item");
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.BOSS_DROP, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (dungeonQualityDropPattern.matches())
                        {
                            String name = dungeonQualityDropPattern.group("item");
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.DUNGEON_QUALITY_DROP, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (dungeonRewardPattern.matches())
                        {
                            String name = dungeonRewardPattern.group("item");
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, ToastUtils.DropType.DUNGEON_REWARD_DROP, ToastType.DROP));
                        }
                        else if (rareDropBracketPattern.matches())
                        {
                            String type = rareDropBracketPattern.group("type");
                            String name = rareDropBracketPattern.group("item");
                            String magicFind = rareDropBracketPattern.group(3);
                            ToastUtils.DropType dropType = type.startsWith("\u00a7r\u00a79\u00a7lVERY RARE") ? ToastUtils.DropType.SLAYER_VERY_RARE_DROP_BLUE : type.startsWith("\u00a7r\u00a75\u00a7lVERY RARE") ? ToastUtils.DropType.SLAYER_VERY_RARE_DROP_PURPLE : ToastUtils.DropType.SLAYER_CRAZY_RARE_DROP;
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, dropType, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (rareDrop2SpaceBracketPattern.matches())
                        {
                            String name = rareDrop2SpaceBracketPattern.group("item");
                            String magicFind = rareDrop2SpaceBracketPattern.group(2);
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, ToastUtils.DropType.SLAYER_RARE_DROP, ToastType.DROP));
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                    }

                    if (ToastMode.getById(ExtendedConfig.instance.petToastMode).equalsIgnoreCase("toast") || ToastMode.getById(ExtendedConfig.instance.petToastMode).equalsIgnoreCase("chat_and_toast"))
                    {
                        boolean isToast = ToastMode.getById(ExtendedConfig.instance.petToastMode).equalsIgnoreCase("toast");

                        if (petLevelUpPattern.matches())
                        {
                            String name = petLevelUpPattern.group("name");
                            String level = petLevelUpPattern.group("level");
                            ItemStack itemStack = SkyBlockPets.Type.valueOf(EnumChatFormatting.getTextWithoutFormattingCodes(name).replace(" ", "_").toUpperCase(Locale.ROOT)).getPetItem();
                            itemStack.setStackDisplayName(name);
                            NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), ToastUtils.DropType.PET_LEVEL_UP, Integer.valueOf(level), itemStack, "Pet", true);
                            LoggerIN.logToast(formattedMessage);
                            cancelMessage = isToast;
                        }
                        else if (petDropPattern.matches())
                        {
                            String name = petDropPattern.group("item");
                            String magicFind = petDropPattern.group("mf");
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(name, magicFind, ToastUtils.DropType.PET_DROP, ToastType.DROP));
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
        if (HypixelEventHandler.isSkyBlock)
        {
            if (KeyBindingHandler.KEY_SB_ENDER_CHEST.isKeyDown())
            {
                this.mc.thePlayer.sendChatMessage("/enderchest");
            }
            else if (KeyBindingHandler.KEY_SB_CRAFTED_MINIONS.isKeyDown())
            {
                this.mc.thePlayer.sendChatMessage("/craftedgenerators");
            }
            else if (KeyBindingHandler.KEY_SB_CRAFTING_TABLE.isKeyDown())
            {
                this.mc.thePlayer.sendChatMessage("/craft");
            }
            else if (KeyBindingHandler.KEY_SB_MENU.isKeyDown())
            {
                this.mc.thePlayer.sendChatMessage("/sbmenu");
            }
            else if (KeyBindingHandler.KEY_SB_VIEW_RECIPE.isKeyDown() && this.mc.currentScreen == null)
            {
                this.mc.thePlayer.sendChatMessage("/recipes");
            }
        }

        if (KeyBindingHandler.KEY_SB_API_VIEWER.isKeyDown())
        {
            if (StringUtils.isNullOrEmpty(ConfigManagerIN.hypixelApiKey))
            {
                ClientUtils.printClientMessage("Couldn't open API Viewer, Empty text in the Config!", JsonUtils.red());
                ClientUtils.printClientMessage(JsonUtils.create("Make sure you're in the Hypixel!").setChatStyle(JsonUtils.yellow()).appendSibling(JsonUtils.create(" Click Here to create an API key").setChatStyle(JsonUtils.gold().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/api new")))));
                return;
            }
            if (!ConfigManagerIN.hypixelApiKey.matches(HypixelEventHandler.UUID_PATTERN_STRING))
            {
                ClientUtils.printClientMessage("Invalid UUID for Hypixel API Key!", JsonUtils.red());
                ClientUtils.printClientMessage("Example UUID pattern: " + UUID.randomUUID(), JsonUtils.yellow());
                return;
            }
            if (this.mc.pointedEntity != null && this.mc.pointedEntity instanceof EntityOtherPlayerMP)
            {
                EntityOtherPlayerMP player = (EntityOtherPlayerMP)this.mc.pointedEntity;

                if (this.mc.thePlayer.sendQueue.getPlayerInfoMap().stream().anyMatch(info -> info.getGameProfile().getName().equals(player.getName())))
                {
                    this.mc.displayGuiScreen(new GuiSkyBlockAPIViewer(GuiSkyBlockAPIViewer.GuiState.PLAYER, player.getDisplayNameString(), "", ""));
                }
                else
                {
                    this.mc.displayGuiScreen(new GuiSkyBlockAPIViewer(GuiSkyBlockAPIViewer.GuiState.EMPTY));
                }
            }
            else
            {
                this.mc.displayGuiScreen(new GuiSkyBlockAPIViewer(GuiSkyBlockAPIViewer.GuiState.EMPTY));
            }
        }
    }

    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent event)
    {
        String name = event.name;

        if (this.mc.theWorld != null)
        {
            if (name.equals("records.13") && HypixelEventHandler.SKY_BLOCK_LOCATION == SkyBlockLocation.BLAZING_FORTRESS)
            {
                this.mc.ingameGUI.displayTitle(JsonUtils.create("Preparing spawn...").setChatStyle(JsonUtils.red()).getFormattedText(), JsonUtils.create("").setChatStyle(JsonUtils.red()).getFormattedText(), 0, 1200, 20);
                this.mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("random.orb"), 0.75F, 1.0F, (float)this.mc.thePlayer.posX + 0.5F, (float)this.mc.thePlayer.posY + 0.5F, (float)this.mc.thePlayer.posZ + 0.5F));
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
        if (!HypixelEventHandler.isSkyBlock)
        {
            return;
        }

        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        try
        {
            if (event.itemStack.hasTagCompound())
            {
                NBTTagCompound extraAttrib = event.itemStack.getTagCompound().getCompoundTag("ExtraAttributes");
                int toAdd = this.mc.gameSettings.advancedItemTooltips ? 3 : 1;
                ModDecimalFormat format = new ModDecimalFormat("#,###.#");

                if (ExtendedConfig.instance.showObtainedDate && extraAttrib.hasKey("timestamp"))
                {
                    DateFormat parseFormat = new SimpleDateFormat("MM/dd/yy HH:mm a");
                    Date date = parseFormat.parse(extraAttrib.getString("timestamp"));
                    String formatted = new SimpleDateFormat("d MMMM yyyy").format(date);
                    event.toolTip.add(event.toolTip.size() - toAdd, EnumChatFormatting.GRAY + "Obtained: " + EnumChatFormatting.RESET + formatted);
                }
                if (ExtendedConfig.instance.bazaarOnTooltips)
                {
                    for (Map.Entry<String, BazaarData> entry : MainEventHandler.BAZAAR_DATA.entrySet())
                    {
                        BazaarData.Product product = entry.getValue().getProduct();

                        if (extraAttrib.getString("id").equals(entry.getKey()))
                        {
                            if (ClientUtils.isShiftKeyDown())
                            {
                                if (StringUtils.isNullOrEmpty(ConfigManagerIN.hypixelApiKey))
                                {
                                    event.toolTip.add(event.toolTip.size() - toAdd, EnumChatFormatting.RED + "Couldn't get bazaar data, Empty text in the Config!");
                                }
                                else if (!ConfigManagerIN.hypixelApiKey.matches(HypixelEventHandler.UUID_PATTERN_STRING))
                                {
                                    event.toolTip.add(event.toolTip.size() - toAdd, EnumChatFormatting.RED + "Invalid UUID for Hypixel API Key!");
                                }
                                else
                                {
                                    double buyStack = 64 * product.getBuyPrice();
                                    double sellStack = 64 * product.getSellPrice();
                                    double buyCurrent = event.itemStack.stackSize * product.getBuyPrice();
                                    double sellCurrent = event.itemStack.stackSize * product.getSellPrice();
                                    event.toolTip.add(event.toolTip.size() - toAdd, "Buy/Sell (Stack): " + EnumChatFormatting.GOLD + format.format(buyStack) + EnumChatFormatting.YELLOW + "/" + EnumChatFormatting.GOLD + format.format(sellStack) + " coins");

                                    if (event.itemStack.stackSize > 1 && event.itemStack.stackSize < 64)
                                    {
                                        event.toolTip.add(event.toolTip.size() - toAdd, "Buy/Sell (Current): " + EnumChatFormatting.GOLD + format.format(buyCurrent) + EnumChatFormatting.YELLOW + "/" + EnumChatFormatting.GOLD + format.format(sellCurrent) + " coins");
                                    }

                                    event.toolTip.add(event.toolTip.size() - toAdd, "Buy/Sell (One): " + EnumChatFormatting.GOLD + format.format(product.getBuyPrice()) + EnumChatFormatting.YELLOW + "/" + EnumChatFormatting.GOLD + format.format(product.getSellPrice()) + " coins");
                                    event.toolTip.add(event.toolTip.size() - toAdd, "Last Updated: " + EnumChatFormatting.WHITE + CommonUtils.getRelativeTime(entry.getValue().getLastUpdated()));
                                }
                            }
                            else
                            {
                                event.toolTip.add(event.toolTip.size() - toAdd, "Press <SHIFT> to view Bazaar Buy/Sell");
                            }
                        }
                    }
                }
            }
            for (String tooltip : event.toolTip)
            {
                String lore = EnumChatFormatting.getTextWithoutFormattingCodes(tooltip);

                if (this.mc.currentScreen != null && this.mc.currentScreen instanceof GuiChest)
                {
                    GuiChest chest = (GuiChest)this.mc.currentScreen;
                    String name = chest.lowerChestInventory.getDisplayName().getUnformattedText();

                    if (name.equals("Community Shop"))
                    {
                        HypixelEventHandler.replaceEstimateTime(lore, calendar, event.toolTip, dates, "Starts in: ", "Starts at: ");
                    }
                    else
                    {
                        HypixelEventHandler.replaceEstimateTime(lore, calendar, event.toolTip, dates, "Starts in: ", "Event starts at: ");
                    }
                }

                HypixelEventHandler.replaceEstimateTime(lore, calendar, event.toolTip, dates, "Starting in: ", "Event starts at: ");

                HypixelEventHandler.replaceBankInterestTime(lore, calendar, event.toolTip, dates, "Interest in: ");
                HypixelEventHandler.replaceBankInterestTime(lore, calendar, event.toolTip, dates, "Until interest: ");

                HypixelEventHandler.replaceAuctionTime(lore, calendar, event.toolTip, dates, "Ends in: ");

                HypixelEventHandler.replaceEstimateTime(lore, calendar, event.toolTip, dates, "Time left: ", "Ends at: ");
            }
        }
        catch (Exception e) {}
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        ItemStack itemStack = event.entityPlayer.getCurrentEquippedItem();

        if (HypixelEventHandler.isSkyBlock && itemStack != null && itemStack.hasTagCompound() && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
        {
            NBTTagCompound extraAttrib = itemStack.getTagCompound().getCompoundTag("ExtraAttributes");

            if (extraAttrib.getString("id").equals("SNOW_BLASTER") || extraAttrib.getString("id").equals("SNOW_CANNON"))
            {
                event.setCanceled(true);
            }
        }
    }

    private String keepLettersAndNumbersOnly(String text)
    {
        return LETTERS_NUMBERS.matcher(text).replaceAll("");
    }

    /**
     * Credit to codes.biscuit.skyblockaddons.utils.InventoryUtils
     */
    private void getInventoryDifference(ItemStack[] currentInventory)
    {
        List<ItemStack> newInventory = this.copyInventory(currentInventory);
        Map<String, ItemDropDiff> previousInventoryMap = new HashMap<>();
        Map<String, ItemDropDiff> newInventoryMap = new HashMap<>();
        HypixelEventHandler.ITEM_DROP_CHECK_LIST.removeIf(drop -> this.removeUndisplayedToast(drop));

        if (this.previousInventory != null)
        {
            for (int i = 0; i < newInventory.size(); i++)
            {
                ItemStack previousItem = this.previousInventory.get(i);
                ItemStack newItem = newInventory.get(i);

                if (previousItem != null)
                {
                    int amount = previousInventoryMap.getOrDefault(previousItem.getDisplayName(), new ItemDropDiff(previousItem, 0)).count + previousItem.stackSize;
                    previousInventoryMap.put(previousItem.getDisplayName(), new ItemDropDiff(previousItem, amount));
                }
                if (newItem != null)
                {
                    int amount = newInventoryMap.getOrDefault(newItem.getDisplayName(), new ItemDropDiff(newItem, 0)).count + newItem.stackSize;
                    newInventoryMap.put(newItem.getDisplayName(), new ItemDropDiff(newItem, amount));
                }
            }

            Set<String> keySet = new HashSet<>(previousInventoryMap.keySet());
            keySet.addAll(newInventoryMap.keySet());

            keySet.forEach(key ->
            {
                ItemDropDiff previousDiff = previousInventoryMap.getOrDefault(key, new ItemDropDiff(null, 0));
                ItemDropDiff newDiff = newInventoryMap.getOrDefault(key, new ItemDropDiff(null, 0));
                int diff = newDiff.count - previousDiff.count;

                if (diff != 0)
                {
                    ItemStack newItem = newDiff.itemStack;

                    if (newItem != null)
                    {
                        for (Iterator<ToastUtils.ItemDropCheck> iterator = HypixelEventHandler.ITEM_DROP_CHECK_LIST.iterator(); iterator.hasNext();)
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
                                else if (drop.getType().matches(ToastUtils.DropCondition.CONTAINS) && key.contains(dropName))
                                {
                                    if (HUDRenderEventHandler.INSTANCE.getToastGui().add(new ItemDropsToast(newItem, drop.getType(), drop.getMagicFind())))
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
            LoggerIN.logToast("You got " + drop.getName() + " but it doesn't show on toast!");
            return true;
        }
        return false;
    }

    private void clearBossData()
    {
        SkyBlockBossBar.renderBossBar = false;
        SkyBlockBossBar.bossName = null;
        SkyBlockBossBar.healthScale = 0;
        HypixelEventHandler.dragonHealth = 0;
        this.dragonType = null;
        HUDRenderEventHandler.foundDragon = false;
    }

    public static ItemStack getSkillItemStack(String exp, String skill)
    {
        ItemStack itemStack;

        switch (skill)
        {
        default:
        case "Farming":
            itemStack = new ItemStack(Items.diamond_hoe);
            break;
        case "Mining":
            itemStack = new ItemStack(Items.diamond_pickaxe);
            break;
        case "Combat":
            itemStack = new ItemStack(Items.diamond_sword);
            break;
        case "Foraging":
            itemStack = new ItemStack(Items.diamond_axe);
            break;
        case "Fishing":
            itemStack = new ItemStack(Items.fishing_rod);
            break;
        case "Enchanting":
            itemStack = new ItemStack(Blocks.enchanting_table);
            break;
        case "Alchemy":
            itemStack = new ItemStack(Items.brewing_stand);
            break;
        }
        itemStack.setStackDisplayName(ColorUtils.stringToRGB("255,255,85").toColoredFont() + exp + " " + skill + " XP");
        return itemStack;
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

    private static void replaceEstimateTime(String lore, Calendar calendar, List<String> tooltip, List<String> dates, String replacedText, String newText)
    {
        if (lore.startsWith(replacedText))
        {
            lore = lore.substring(lore.indexOf(":") + 2).replaceAll("[^a-zA-Z0-9 ]|^[a-zA-Z ]+", "");
            String[] timeEstimate = Arrays.stream(lore.split(" ")).map(time -> time.replaceAll("[^0-9]+", "")).toArray(size -> new String[size]);
            int dayF = 0;
            int hourF = 0;
            int minuteF = 0;
            int secondF = 0;

            if (timeEstimate.length == 2)
            {
                minuteF = Integer.valueOf(timeEstimate[0]);
                secondF = Integer.valueOf(timeEstimate[1]);
            }
            else if (timeEstimate.length == 3)
            {
                hourF = Integer.valueOf(timeEstimate[0]);
                minuteF = Integer.valueOf(timeEstimate[1]);
                secondF = Integer.valueOf(timeEstimate[2]);
            }
            else
            {
                dayF = Integer.valueOf(timeEstimate[0]);
                hourF = Integer.valueOf(timeEstimate[1]);
                minuteF = Integer.valueOf(timeEstimate[2]);
                secondF = Integer.valueOf(timeEstimate[3]);
            }

            calendar.add(Calendar.DATE, dayF);
            calendar.add(Calendar.HOUR, hourF);
            calendar.add(Calendar.MINUTE, minuteF);
            calendar.add(Calendar.SECOND, secondF);
            String date1 = new SimpleDateFormat("EEEE h:mm:ss a", Locale.ROOT).format(calendar.getTime());
            String date2 = new SimpleDateFormat("d MMMMM yyyy", Locale.ROOT).format(calendar.getTime());
            dates.add(newText);
            dates.add(EnumChatFormatting.YELLOW + date1);
            dates.add(EnumChatFormatting.YELLOW + date2);

            int indexToRemove = 0;

            for (int i = 0; i < tooltip.size(); i++)
            {
                if (tooltip.get(i).contains(replacedText))
                {
                    indexToRemove = i;
                }
            }
            if (!GuiScreen.isShiftKeyDown())
            {
                tooltip.add(indexToRemove + 1, "Press <SHIFT> to view exact time");
            }
            else
            {
                tooltip.remove(indexToRemove);
                tooltip.addAll(indexToRemove, dates);
            }
        }
    }

    private static void replaceBankInterestTime(String lore, Calendar calendar, List<String> tooltip, List<String> dates, String replacedText)
    {
        if (lore.startsWith(replacedText))
        {
            lore = lore.substring(lore.indexOf(":") + 2).replaceAll("[^0-9]+", " ");
            String[] timeEstimate = Arrays.stream(lore.split(" ")).map(time -> time.replaceAll("[^0-9]+", "")).toArray(size -> new String[size]);
            int hourF = 0;
            int minuteF = 0;
            int secondF = 0;

            if (timeEstimate.length == 1)
            {
                hourF = Integer.valueOf(timeEstimate[0]);
            }
            else if (timeEstimate.length == 2)
            {
                minuteF = Integer.valueOf(timeEstimate[0]);
                secondF = Integer.valueOf(timeEstimate[1]);
            }
            else
            {
                hourF = Integer.valueOf(timeEstimate[0]);
                minuteF = Integer.valueOf(timeEstimate[1]);
                secondF = Integer.valueOf(timeEstimate[2]);
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
            dates.add("Interest receive at: ");
            dates.add(EnumChatFormatting.YELLOW + date1);
            dates.add(EnumChatFormatting.YELLOW + date2);

            int indexToRemove = 0;

            for (int i = 0; i < tooltip.size(); i++)
            {
                if (tooltip.get(i).contains(replacedText))
                {
                    indexToRemove = i;
                    break;
                }
            }
            if (!GuiScreen.isShiftKeyDown())
            {
                tooltip.add(indexToRemove + 1, "Press <SHIFT> to view exact time");
            }
            else
            {
                tooltip.remove(indexToRemove);
                tooltip.addAll(indexToRemove, dates);
            }
        }
    }

    private static void replaceAuctionTime(String lore, Calendar calendar, List<String> tooltip, List<String> dates, String replacedText)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (lore.startsWith(replacedText))
        {
            boolean isDay = lore.endsWith("d");
            lore = lore.substring(lore.indexOf(":") + 2).replaceAll("[^0-9]+", " ");
            String[] timeEstimate = Arrays.stream(lore.split(" ")).map(time -> time.replaceAll("[^0-9]+", "")).toArray(size -> new String[size]);
            int dayF = 0;
            int hourF = 0;
            int minuteF = 0;
            int secondF = 0;

            if (timeEstimate.length == 1)
            {
                if (isDay)
                {
                    dayF = Integer.valueOf(timeEstimate[0]);
                }
                else
                {
                    hourF = Integer.valueOf(timeEstimate[0]);
                }
            }
            else if (timeEstimate.length == 2)
            {
                minuteF = Integer.valueOf(timeEstimate[0]);
                secondF = Integer.valueOf(timeEstimate[1]);
            }
            else
            {
                hourF = Integer.valueOf(timeEstimate[0]);
                minuteF = Integer.valueOf(timeEstimate[1]);
                secondF = Integer.valueOf(timeEstimate[2]);
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

            if (mc.currentScreen != null && mc.currentScreen instanceof GuiChest)
            {
                GuiChest chest = (GuiChest)mc.currentScreen;
                String name = chest.lowerChestInventory.getDisplayName().getUnformattedText();

                if (name.equals("Auction View"))
                {
                    dates.add("Ends at: " + EnumChatFormatting.YELLOW + date1 + ", " + date2);
                }
                else
                {
                    dates.add("Ends at: ");
                    dates.add(EnumChatFormatting.YELLOW + date1);
                    dates.add(EnumChatFormatting.YELLOW + date2);
                }
            }

            int indexToRemove = 0;

            for (int i = 0; i < tooltip.size(); i++)
            {
                if (tooltip.get(i).contains(replacedText))
                {
                    indexToRemove = i;
                    break;
                }
            }
            if (!GuiScreen.isShiftKeyDown())
            {
                tooltip.add(indexToRemove + 1, "Press <SHIFT> to view exact time");
            }
            else
            {
                tooltip.remove(indexToRemove);
                tooltip.addAll(indexToRemove, dates);
            }
        }
    }

    private enum CoinType
    {
        TYPE_1("2070f6cb-f5db-367a-acd0-64d39a7e5d1b", "538071721cc5b4cd406ce431a13f86083a8973e1064d2f8897869930ee6e5237"),
        TYPE_2("8ce61ae1-7cb4-3bdd-b1be-448c6fabb355", "dfa087eb76e7687a81e4ef81a7e6772649990f6167ceb0f750a4c5deb6c4fbad"),
        TYPE_3("9dd5008a-08a1-3f4a-b8af-2499bdb8ff3b", "e36e94f6c34a35465fce4a90f2e25976389eb9709a12273574ff70fd4daa6852");

        private final String id;
        private final String value;

        private CoinType(String id, String value)
        {
            this.id = id;
            this.value = value;
        }

        public String getId()
        {
            return this.id;
        }

        public String getValue()
        {
            return this.value;
        }
    }

    class ItemDropDiff
    {
        final ItemStack itemStack;
        final int count;

        public ItemDropDiff(ItemStack itemStack, int count)
        {
            this.itemStack = itemStack;
            this.count = count;
        }
    }
}