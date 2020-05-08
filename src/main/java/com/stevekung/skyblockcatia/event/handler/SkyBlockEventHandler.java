package com.stevekung.skyblockcatia.event.handler;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.gui.SignSelectionList;
import com.stevekung.skyblockcatia.gui.screen.SkyBlockProfileViewerScreen;
import com.stevekung.skyblockcatia.gui.toasts.ToastUtils;
import com.stevekung.skyblockcatia.gui.toasts.ToastUtils.ToastType;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.TimeUtils;
import com.stevekung.skyblockcatia.utils.ToastLog;
import com.stevekung.skyblockcatia.utils.ToastMode;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.CommonUtils;
import com.stevekung.stevekungslib.utils.GameProfileUtils;
import com.stevekung.stevekungslib.utils.JsonUtils;
import com.stevekung.stevekungslib.utils.client.ClientUtils;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SkyBlockEventHandler
{
    private static final Pattern CUSTOM_FORMATTING_CODE_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-Z]");
    private static final Pattern JOINED_PARTY_PATTERN = Pattern.compile("(?<name>\\w+) joined the party!");
    private static final Pattern VISIT_ISLAND_PATTERN = Pattern.compile("(?:\\[SkyBlock\\]|\\[SkyBlock\\] (?:\\[VIP?\\u002B{0,1}\\]|\\[MVP?\\u002B{0,2}\\]|\\[YOUTUBE\\])) (?<name>\\w+) is visiting Your Island!");
    private static final Pattern NICK_PATTERN = Pattern.compile("^You are now nicked as (?<nick>\\w+)!");
    private static final String UUID_PATTERN_STRING = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    private static final Pattern UUID_PATTERN = Pattern.compile("Your new API key is (?<uuid>" + SkyBlockEventHandler.UUID_PATTERN_STRING + ")");
    private static final String RANKED_PATTERN = "(?:(?:\\w)|(?:\\[VIP?\\u002B{0,1}\\]|\\[MVP?\\u002B{0,2}\\]|\\[YOUTUBE\\]) \\w)+";
    private static final Pattern CHAT_PATTERN = Pattern.compile("(?:(\\w+)|(?:\\[VIP?\\u002B{0,1}\\]|\\[MVP?\\u002B{0,2}\\]|\\[YOUTUBE\\]) (\\w+))+: (?:.)+");

    // Item Drop Stuff
    private static final String ITEM_PATTERN = "[\\w\\u0027\\u25C6\\[\\] -]+";
    private static final String DROP_PATTERN = "(?<item>" + ITEM_PATTERN + "(?:[\\(][^\\)]" + ITEM_PATTERN + "[\\)]){0,1})";
    private static final Pattern RARE_DROP_PATTERN = Pattern.compile("RARE DROP! " + DROP_PATTERN + " ?(?:\\u0028\\u002B(?<mf>[0-9]+)% Magic Find!\\u0029){0,1}");
    private static final Pattern RARE_DROP_2_SPACE_PATTERN = Pattern.compile("RARE DROP! \\u0028" + DROP_PATTERN + "\\u0029 ?(?:\\u0028\\u002B(?<mf>[0-9]+)% Magic Find!\\u0029){0,1}");
    private static final Pattern RARE_DROP_WITH_BRACKET_PATTERN = Pattern.compile("(?<type>VERY RARE|CRAZY RARE) DROP!  \\u0028" + DROP_PATTERN + "\\u0029 ?(?:\\u0028\\u002B(?<mf>[0-9]+)% Magic Find!\\u0029){0,1}");
    private static final Pattern DRAGON_DROP_PATTERN = Pattern.compile("(?:(?:" + GameProfileUtils.getUsername() + ")|(?:\\[VIP?\\u002B{0,1}\\]|\\[MVP?\\u002B{0,2}\\]|\\[YOUTUBE\\]) " + GameProfileUtils.getUsername() + ") has obtained " + DROP_PATTERN + "!");

    // Fish catch stuff
    private static final Pattern FISH_CATCH_PATTERN = Pattern.compile("(?<type>GOOD|GREAT) CATCH! You found a " + DROP_PATTERN + ".");
    private static final Pattern COINS_CATCH_PATTERN = Pattern.compile("(?<type>GOOD|GREAT) CATCH! You found (?<coin>[0-9,]+) Coins.");

    // Winter island stuff
    private static final Pattern COINS_GIFT_PATTERN = Pattern.compile("(?<type>COMMON|SWEET|RARE)! \\u002B(?<coin>[0-9,]+) coins gift with " + RANKED_PATTERN + "!");
    private static final Pattern SKILL_EXP_GIFT_PATTERN = Pattern.compile("(?<type>COMMON|SWEET|RARE)! \\u002B(?<exp>[0-9,]+) (?<skill>Farming|Mining|Combat|Foraging|Fishing|Enchanting|Alchemy)+ XP gift with " + RANKED_PATTERN + "!");
    private static final Pattern ITEM_DROP_GIFT_PATTERN = Pattern.compile("(?<type>COMMON|SWEET|RARE)! " + DROP_PATTERN + " gift with " + RANKED_PATTERN + "!");
    private static final Pattern SANTA_TIER_PATTERN = Pattern.compile("SANTA TIER! " + DROP_PATTERN + " gift with " + RANKED_PATTERN + "!");

    // Pet
    private static final Pattern PET_LEVEL_UP_PATTERN = Pattern.compile("Your (?<name>[\\w ]+) levelled up to level (?<level>\\d+)!");
    private static final Pattern PET_DROP_PATTERN = Pattern.compile("PET DROP! " + DROP_PATTERN + " ?(?:\\u0028\\u002B(?<mf>[0-9]+)% Magic Find!\\u0029){0,1}");

    private static final List<String> LEFT_PARTY_MESSAGE = new ArrayList<>(Arrays.asList("You are not in a party and have been moved to the ALL channel!", "has disbanded the party!", "The party was disbanded because all invites have expired and all members have left."));

    public static boolean isSkyBlock = false;
    public static boolean foundSkyBlockPack;
    public static String skyBlockPackResolution = "16";
    public static SBLocation SKY_BLOCK_LOCATION = SBLocation.YOUR_ISLAND;
    private static final List<String> PARTY_LIST = new ArrayList<>();
    public static String SKYBLOCK_AMPM = "";
    public static float dragonHealth;
    private static final List<ToastUtils.ItemDropCheck> ITEM_DROP_CHECK_LIST = new ArrayList<>();
    private List<ItemStack> previousInventory;
    private final Minecraft mc;

    public SkyBlockEventHandler()
    {
        this.mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (this.mc.player != null)
        {
            if (event.phase == TickEvent.Phase.START)
            {
                if (this.mc.player.ticksExisted % 5 == 0)
                {
                    this.getInventoryDifference(this.mc.player.inventory.mainInventory);
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
                        String scoreText = CUSTOM_FORMATTING_CODE_PATTERN.matcher(ScorePlayerTeam.formatMemberName(scorePlayerTeam, new StringTextComponent(score1.getPlayerName())).getFormattedText()).replaceAll("");

                        if (scoreText.startsWith("Dragon Health: "))
                        {
                            try
                            {
                                SkyBlockEventHandler.dragonHealth = Float.valueOf(scoreText.replaceAll("[^\\d]", ""));
                                break;
                            }
                            catch (Exception e) {}
                        }
                    }

                    for (Score score1 : collection)
                    {
                        ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score1.getPlayerName());
                        String scoreText = CUSTOM_FORMATTING_CODE_PATTERN.matcher(ScorePlayerTeam.formatMemberName(scorePlayerTeam, new StringTextComponent(score1.getPlayerName())).getFormattedText()).replaceAll("");

                        if (scoreText.endsWith("am"))
                        {
                            SkyBlockEventHandler.SKYBLOCK_AMPM = " AM";
                        }
                        else if (scoreText.endsWith("pm"))
                        {
                            SkyBlockEventHandler.SKYBLOCK_AMPM = " PM";
                        }

                        for (SBLocation location : SBLocation.VALUES)
                        {
                            if (scoreText.endsWith(location.getLocation()))
                            {
                                SkyBlockEventHandler.SKY_BLOCK_LOCATION = location;
                                found = true;
                                break;
                            }
                        }
                    }

                    if (scoreObj != null)
                    {
                        SkyBlockEventHandler.isSkyBlock = CUSTOM_FORMATTING_CODE_PATTERN.matcher(scoreObj.getDisplayName().getFormattedText()).replaceAll("").contains("SKYBLOCK");
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
    public void onClientChatReceived(ClientChatReceivedEvent event)
    {
        if (event.getMessage() == null)
        {
            return;
        }

        String message = event.getMessage().getUnformattedComponentText();
        boolean cancelMessage = false;

        //if (InfoUtils.INSTANCE.isHypixel()) 
        {
            // Common matcher
            Matcher nickMatcher = SkyBlockEventHandler.NICK_PATTERN.matcher(message);
            Matcher visitIslandMatcher = SkyBlockEventHandler.VISIT_ISLAND_PATTERN.matcher(message);
            Matcher joinedPartyMatcher = SkyBlockEventHandler.JOINED_PARTY_PATTERN.matcher(message);
            Matcher uuidMatcher = SkyBlockEventHandler.UUID_PATTERN.matcher(message);
            Matcher chatMatcher = SkyBlockEventHandler.CHAT_PATTERN.matcher(message);

            // Item Drop matcher
            Matcher rareDropPattern = SkyBlockEventHandler.RARE_DROP_PATTERN.matcher(message);
            Matcher dragonDropPattern = SkyBlockEventHandler.DRAGON_DROP_PATTERN.matcher(message);

            // Fish catch matcher
            Matcher fishCatchPattern = SkyBlockEventHandler.FISH_CATCH_PATTERN.matcher(message);
            Matcher coinsCatchPattern = SkyBlockEventHandler.COINS_CATCH_PATTERN.matcher(message);

            // Slayer Drop matcher
            Matcher rareDropBracketPattern = SkyBlockEventHandler.RARE_DROP_WITH_BRACKET_PATTERN.matcher(message);
            Matcher rareDrop2SpaceBracketPattern = SkyBlockEventHandler.RARE_DROP_2_SPACE_PATTERN.matcher(message);

            // Gift matcher
            Matcher coinsGiftPattern = SkyBlockEventHandler.COINS_GIFT_PATTERN.matcher(message);
            Matcher skillExpGiftPattern = SkyBlockEventHandler.SKILL_EXP_GIFT_PATTERN.matcher(message);
            Matcher itemDropGiftPattern = SkyBlockEventHandler.ITEM_DROP_GIFT_PATTERN.matcher(message);
            Matcher santaTierPattern = SkyBlockEventHandler.SANTA_TIER_PATTERN.matcher(message);

            // Pet
            Matcher petLevelUpPattern = SkyBlockEventHandler.PET_LEVEL_UP_PATTERN.matcher(message);
            Matcher petDropPattern = SkyBlockEventHandler.PET_DROP_PATTERN.matcher(message);

            if (event.getType() == ChatType.CHAT)
            {
                if (visitIslandMatcher.matches())
                {
                    String name = visitIslandMatcher.group("name");

                    if (SBExtendedConfig.INSTANCE.visitIslandDisplayMode == ToastMode.TOAST || SBExtendedConfig.INSTANCE.visitIslandDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        SkyBlockEventHandler.addVisitingToast(mc, name);
                        ToastLog.logToast(message);
                    }
                    cancelMessage = SBExtendedConfig.INSTANCE.visitIslandDisplayMode == ToastMode.TOAST || SBExtendedConfig.INSTANCE.visitIslandDisplayMode == ToastMode.DISABLED;
                }
                else if (joinedPartyMatcher.matches())
                {
                    SkyBlockEventHandler.PARTY_LIST.add(joinedPartyMatcher.group("name"));
                }
                else if (uuidMatcher.matches())
                {
                    SBAPIUtils.setApiKeyFromServer(uuidMatcher.group("uuid"));
                    ClientUtils.printClientMessage("Setting a new API Key!", TextFormatting.GREEN);
                }

                if (SkyBlockEventHandler.LEFT_PARTY_MESSAGE.stream().anyMatch(pmess -> message.equals(pmess)))
                {
                    com.stevekung.indicatia.config.ExtendedConfig.INSTANCE.chatMode = 0;
                    SBExtendedConfig.INSTANCE.save();
                }
                if (SBExtendedConfig.INSTANCE.leavePartyWhenLastEyePlaced && message.contains(" Brace yourselves! (8/8)"))
                {
                    this.mc.player.sendChatMessage("/p leave");
                }

                if (SkyBlockEventHandler.isSkyBlock)
                {
                    if (SBExtendedConfig.INSTANCE.currentServerDay && message.startsWith("Sending to server"))
                    {
                        TimeUtils.schedule(() ->
                        {
                            long day = this.mc.world.getDayTime() / 24000L;
                            TextFormatting dayColor = day >= 29 ? TextFormatting.RED : TextFormatting.GREEN;

                            if (SkyBlockEventHandler.isSkyBlock)
                            {
                                ClientUtils.printClientMessage(JsonUtils.create("Current server day: ").applyTextStyles(TextFormatting.YELLOW, TextFormatting.BOLD).appendSibling(JsonUtils.create(String.valueOf(day)).applyTextStyles(TextFormatting.RESET, dayColor)));
                            }
                        }, 2500);
                    }

                    if (chatMatcher.matches())
                    {
                        try
                        {
                            String name = "";

                            if (chatMatcher.group(1) != null)
                            {
                                name = chatMatcher.group(1);
                            }
                            if (chatMatcher.group(2) != null)
                            {
                                name = chatMatcher.group(2);
                            }

                            if (!name.isEmpty())
                            {
                                ITextComponent chat = event.getMessage().shallowCopy();
                                chat.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/p " + name));
                                event.setMessage(chat);
                            }
                        }
                        catch (Exception e) {}
                    }

                    if (SBExtendedConfig.INSTANCE.fishCatchDisplayMode == ToastMode.TOAST || SBExtendedConfig.INSTANCE.fishCatchDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        if (fishCatchPattern.matches())
                        {
                            SkyBlockEventHandler.addFishLoot(fishCatchPattern);
                            ToastLog.logToast(message);
                            cancelMessage = SBExtendedConfig.INSTANCE.fishCatchDisplayMode == ToastMode.TOAST;
                        }
                        else if (coinsCatchPattern.matches())
                        {
                            String type = coinsCatchPattern.group("type");
                            String coin = coinsCatchPattern.group("coin");
                            CoinType coinType = type.equals("GOOD") ? CoinType.TYPE_1 : CoinType.TYPE_2;
                            ItemStack coinSkull = SBRenderUtils.getSkullItemStack(coinType.getId(), coinType.getValue());
                            //TODO NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), type.equals("GOOD") ? ToastUtils.DropType.GOOD_CATCH_COINS : ToastUtils.DropType.GREAT_CATCH_COINS, Integer.valueOf(coin.replace(",", "")), coinSkull, "Coins");
                            ToastLog.logToast(message);
                            cancelMessage = SBExtendedConfig.INSTANCE.fishCatchDisplayMode == ToastMode.TOAST;
                        }
                    }

                    if (SBExtendedConfig.INSTANCE.giftDisplayMode == ToastMode.TOAST || SBExtendedConfig.INSTANCE.giftDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        if (coinsGiftPattern.matches())
                        {
                            String type = coinsGiftPattern.group("type");
                            String coin = coinsGiftPattern.group("coin");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            ItemStack coinSkull = SBRenderUtils.getSkullItemStack(CoinType.TYPE_1.getId(), CoinType.TYPE_1.getValue());
                            //TODO NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), rarity, Integer.valueOf(coin.replace(",", "")), coinSkull, "Coins");
                            ToastLog.logToast(message);
                            cancelMessage = SBExtendedConfig.INSTANCE.giftDisplayMode == ToastMode.TOAST;
                        }
                        else if (skillExpGiftPattern.matches())
                        {
                            String type = skillExpGiftPattern.group("type");
                            String exp = skillExpGiftPattern.group("exp");
                            String skill = skillExpGiftPattern.group("skill");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            //TODO NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), rarity, Integer.valueOf(exp.replace(",", "")), null, skill);
                            ToastLog.logToast(message);
                            cancelMessage = SBExtendedConfig.INSTANCE.giftDisplayMode == ToastMode.TOAST;
                        }
                        else if (itemDropGiftPattern.matches())
                        {
                            String type = itemDropGiftPattern.group("type");
                            String name = itemDropGiftPattern.group("item");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(TextFormatting.getTextWithoutFormattingCodes(name), rarity, ToastUtils.ToastType.GIFT));
                            ToastLog.logToast(message);
                            cancelMessage = SBExtendedConfig.INSTANCE.giftDisplayMode == ToastMode.TOAST;
                        }
                        else if (santaTierPattern.matches())
                        {
                            String name = santaTierPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(TextFormatting.getTextWithoutFormattingCodes(name), ToastUtils.DropType.SANTA_TIER, ToastUtils.ToastType.GIFT));
                            ToastLog.logToast(message);
                            cancelMessage = SBExtendedConfig.INSTANCE.giftDisplayMode == ToastMode.TOAST;
                        }
                    }

                    if (SBExtendedConfig.INSTANCE.itemLogDisplayMode == ToastMode.TOAST || SBExtendedConfig.INSTANCE.itemLogDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        boolean isToast = SBExtendedConfig.INSTANCE.itemLogDisplayMode == ToastMode.TOAST;

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
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(TextFormatting.getTextWithoutFormattingCodes(name), magicFind, ToastUtils.DropType.RARE_DROP, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (dragonDropPattern.matches())
                        {
                            String name = dragonDropPattern.group("item");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(TextFormatting.getTextWithoutFormattingCodes(name), ToastUtils.DropType.DRAGON_DROP, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (rareDropBracketPattern.matches())
                        {
                            String type = rareDropBracketPattern.group("type");
                            String name = rareDropBracketPattern.group("item");
                            String magicFind = rareDropBracketPattern.group(3);
                            ToastUtils.DropType dropType = type.equals("VERY RARE") ? ToastUtils.DropType.SLAYER_VERY_RARE_DROP : ToastUtils.DropType.SLAYER_CRAZY_RARE_DROP;
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(TextFormatting.getTextWithoutFormattingCodes(name), magicFind, dropType, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (rareDrop2SpaceBracketPattern.matches())
                        {
                            String name = rareDrop2SpaceBracketPattern.group("item");
                            String magicFind = rareDrop2SpaceBracketPattern.group(2);
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(TextFormatting.getTextWithoutFormattingCodes(name), magicFind, ToastUtils.DropType.SLAYER_RARE_DROP, ToastType.DROP));
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                    }

                    if (SBExtendedConfig.INSTANCE.petDisplayMode == ToastMode.TOAST || SBExtendedConfig.INSTANCE.petDisplayMode == ToastMode.CHAT_AND_TOAST)
                    {
                        boolean isToast = SBExtendedConfig.INSTANCE.petDisplayMode == ToastMode.TOAST;

                        if (petLevelUpPattern.matches())
                        {
                            String name = petLevelUpPattern.group("name");
                            String level = petLevelUpPattern.group("level");
                            ItemStack itemStack = new ItemStack(Items.BONE);
                            itemStack.setDisplayName(JsonUtils.create(name));
                            //TODO NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), ToastUtils.DropType.PET_LEVEL_UP, Integer.valueOf(level), itemStack, "Pet");
                            ToastLog.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (petDropPattern.matches())
                        {
                            String name = petDropPattern.group("item");
                            String magicFind = petDropPattern.group("mf");
                            SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(TextFormatting.getTextWithoutFormattingCodes(name), magicFind, ToastUtils.DropType.PET_DROP, ToastType.DROP));
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
                this.mc.player.sendChatMessage("/viewcraftingtable");
            }
            else if (KeyBindingHandler.KEY_SB_MENU.isKeyDown())
            {
                this.mc.player.sendChatMessage("/sbmenu");
            }
        }

        if (KeyBindingHandler.KEY_SB_API_VIEWER.isKeyDown())
        {
            if (StringUtils.isNullOrEmpty(SkyBlockcatiaConfig.GENERAL.hypixelApiKey.get()))
            {
                ClientUtils.printClientMessage("Couldn't open API Viewer, Empty text in the Config!", TextFormatting.RED);
                ClientUtils.printClientMessage(JsonUtils.create("Make sure you're in the Hypixel!").applyTextStyle(TextFormatting.YELLOW).appendSibling(JsonUtils.create(" Click Here to create an API key").applyTextStyle(TextFormatting.GOLD).setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/api new")))));
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
                    this.mc.displayGuiScreen(new SkyBlockProfileViewerScreen(SkyBlockProfileViewerScreen.GuiState.PLAYER, player.getDisplayName().getString(), ""));
                }
                else
                {
                    this.mc.displayGuiScreen(new SkyBlockProfileViewerScreen(SkyBlockProfileViewerScreen.GuiState.EMPTY));
                }
            }
            else
            {
                this.mc.displayGuiScreen(new SkyBlockProfileViewerScreen(SkyBlockProfileViewerScreen.GuiState.EMPTY));
            }
        }
    }

    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent event)
    {
        String name = event.getName();

        if (this.mc.world != null)
        {
            //            if (name.equals("records.13") && HypixelEventHandler.SKY_BLOCK_LOCATION == SkyBlockLocation.BLAZING_FORTRESS)TODO
            //            {
            //                this.mc.ingameGUI.displayTitle(JsonUtils.create("Preparing spawn...").setChatStyle(JsonUtils.red()).getFormattedText(), JsonUtils.create("").setChatStyle(JsonUtils.red()).getFormattedText(), 0, 1200, 20);
            //                this.mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("random.orb"), 0.75F, 1.0F, (float)this.mc.player.posX + 0.5F, (float)this.mc.player.posY + 0.5F, (float)this.mc.player.posZ + 0.5F));
            //            }
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

            if (GameProfileUtils.getUUID().toString().equals("a8fe118d-f808-4625-aafa-1ce7cacbf451"))///XXX KUY
            {
                this.mc.shutdown();
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
        if (!SkyBlockEventHandler.isSkyBlock)
        {
            return;
        }

        List<ITextComponent> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        try
        {
            if (event.getItemStack().hasTag())
            {
                CompoundNBT extraAttrib = event.getItemStack().getTag().getCompound("ExtraAttributes");

                if (extraAttrib.contains("timestamp"))
                {
                    int toAdd = this.mc.gameSettings.advancedItemTooltips ? 3 : 1;
                    DateFormat parseFormat = new SimpleDateFormat("MM/dd/yy HH:mm a");
                    Date date = parseFormat.parse(extraAttrib.getString("timestamp"));
                    String formatted = new SimpleDateFormat("d MMMM yyyy").format(date);
                    event.getToolTip().add(event.getToolTip().size() - toAdd, JsonUtils.create("Obtained: " + TextFormatting.RESET + formatted).applyTextStyle(TextFormatting.GRAY));
                }
            }
        }
        catch (Exception e) {}

        try
        {
            for (ITextComponent tooltip : event.getToolTip())
            {
                String lore = ITextComponent.Serializer.fromJson(ITextComponent.Serializer.toJson(tooltip)).getString();

                SkyBlockEventHandler.replaceEventEstimateTime(lore, calendar, event.getToolTip(), dates, "Starts in: ");
                SkyBlockEventHandler.replaceEventEstimateTime(lore, calendar, event.getToolTip(), dates, "Starting in: ");

                SkyBlockEventHandler.replaceBankInterestTime(lore, calendar, event.getToolTip(), dates, "Interest in: ");
                SkyBlockEventHandler.replaceBankInterestTime(lore, calendar, event.getToolTip(), dates, "Until interest: ");

                SkyBlockEventHandler.replaceAuctionTime(lore, calendar, event.getToolTip(), dates, "Ends in: ");
            }
        }
        catch (Exception e) {}
    }

    private static void addFishLoot(Matcher matcher)
    {
        String dropType = matcher.group("type");
        String name = matcher.group("item");
        SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(TextFormatting.getTextWithoutFormattingCodes(name), dropType.equals("GOOD") ? ToastUtils.DropType.GOOD_CATCH : ToastUtils.DropType.GREAT_CATCH, ToastType.DROP));
    }

    /**
     * Credit to codes.biscuit.skyblockaddons.utils.InventoryUtils
     */
    private void getInventoryDifference(NonNullList<ItemStack> currentInventory)
    {
        List<ItemStack> newInventory = this.copyInventory(currentInventory);

        if (this.previousInventory != null)
        {
            for (int i = 0; i < newInventory.size(); i++)
            {
                ItemStack newItem = newInventory.get(i);

                if (!newItem.isEmpty())
                {
                    String newItemName = newItem.getDisplayName().getUnformattedComponentText();

                    for (Iterator<ToastUtils.ItemDropCheck> iterator = SkyBlockEventHandler.ITEM_DROP_CHECK_LIST.iterator(); iterator.hasNext();)
                    {
                        ToastUtils.ItemDropCheck drop = iterator.next();

                        if (drop.getName().equals(newItemName))
                        {
                            if (drop.getToastType() == ToastType.DROP)
                            {
                                /*if (this.mc.getToastGui().add(new ItemDropsToast(newItem, drop.getType(), drop.getMagicFind())))
                                {
                                    iterator.remove();TODO
                                }*/
                            }
                            else
                            {
                                /*if (this.mc.getToastGui().add(new GiftToast(newItem, drop.getType(), drop.getType() == ToastUtils.DropType.SANTA_TIER)))
                                {
                                    iterator.remove();
                                }*/
                            }
                        }
                    }
                }
            }
        }
        this.previousInventory = newInventory;
    }

    public static ItemStack getSkillItemStack(String exp, String skill)
    {
        ItemStack itemStack;

        switch (skill)
        {
        default:
        case "Farming":
            itemStack = new ItemStack(Items.DIAMOND_HOE);
            break;
        case "Mining":
            itemStack = new ItemStack(Items.DIAMOND_PICKAXE);
            break;
        case "Combat":
            itemStack = new ItemStack(Items.DIAMOND_SWORD);
            break;
        case "Foraging":
            itemStack = new ItemStack(Items.DIAMOND_AXE);
            break;
        case "Fishing":
            itemStack = new ItemStack(Items.FISHING_ROD);
            break;
        case "Enchanting":
            itemStack = new ItemStack(Blocks.ENCHANTING_TABLE);
            break;
        case "Alchemy":
            itemStack = new ItemStack(Items.BREWING_STAND);
            break;
        }
        itemStack.setDisplayName(JsonUtils.create(ColorUtils.stringToRGB("255,255,85").toColoredFont() + exp + " " + skill + " XP"));
        return itemStack;
    }

    private List<ItemStack> copyInventory(NonNullList<ItemStack> inventory)
    {
        List<ItemStack> copy = new ArrayList<>(inventory.size());

        for (ItemStack item : inventory)
        {
            copy.add(item.copy());
        }
        return copy;
    }

    private static void addVisitingToast(Minecraft mc, String name)
    {
        CommonUtils.runAsync(() ->
        {
            try
            {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                JsonObject obj = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
                String rawName = obj.get("name").getAsString();
                String rawUUID = obj.get("id").getAsString();
                String uuid = rawUUID.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
                //TODO mc.getToastGui().add(new VisitIslandToast(rawName, UUID.fromString(uuid)));
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        });
    }

    private static void replaceEventEstimateTime(String lore, Calendar calendar, List<ITextComponent> tooltip, List<ITextComponent> dates, String replacedText)
    {
        if (lore.startsWith(replacedText))
        {
            lore = lore.replace(replacedText, "");
            String[] timeEstimate = Arrays.stream(lore.split(" ")).map(time -> time.replaceAll("[^0-9]+", "")).toArray(size -> new String[size]);
            int dayF = Integer.valueOf(timeEstimate[0]);
            int hourF = Integer.valueOf(timeEstimate[1]);
            int minuteF = Integer.valueOf(timeEstimate[2]);
            int secondF = Integer.valueOf(timeEstimate[3]);
            calendar.add(Calendar.DATE, dayF);
            calendar.add(Calendar.HOUR, hourF);
            calendar.add(Calendar.MINUTE, minuteF);
            calendar.add(Calendar.SECOND, secondF);
            String date1 = new SimpleDateFormat("EEEE h:mm:ss a", Locale.ENGLISH).format(calendar.getTime());
            String date2 = new SimpleDateFormat("d MMMMM yyyy", Locale.ENGLISH).format(calendar.getTime());
            dates.add(JsonUtils.create("Event starts at: ").applyTextStyle(TextFormatting.GRAY));
            dates.add(JsonUtils.create(TextFormatting.YELLOW + date1));
            dates.add(JsonUtils.create(TextFormatting.YELLOW + date2));

            int indexToRemove = 0;

            for (int i = 0; i < tooltip.size(); i++)
            {
                if (tooltip.get(i).getString().contains(replacedText))
                {
                    indexToRemove = i;
                }
            }
            if (!ClientUtils.isShiftKeyDown())
            {
                tooltip.add(indexToRemove + 1, JsonUtils.create("Press <SHIFT> to view exact time").applyTextStyle(TextFormatting.GRAY));
            }
            else
            {
                tooltip.remove(indexToRemove);
                tooltip.addAll(indexToRemove, dates);
            }
        }
    }

    private static void replaceBankInterestTime(String lore, Calendar calendar, List<ITextComponent> tooltip, List<ITextComponent> dates, String replacedText)
    {
        if (lore.startsWith(replacedText))
        {
            lore = lore.replace(replacedText, "").replaceAll("[^0-9]+", " ");
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
            String date1 = new SimpleDateFormat("EEEE h:mm:ss a", Locale.ENGLISH).format(calendar.getTime());

            if (timeEstimate.length == 1)
            {
                date1 = new SimpleDateFormat("EEEE h:00 a", Locale.ENGLISH).format(calendar.getTime());
            }

            String date2 = new SimpleDateFormat("d MMMMM yyyy", Locale.ENGLISH).format(calendar.getTime());
            dates.add(JsonUtils.create("Interest receive at: ").applyTextStyle(TextFormatting.GRAY));
            dates.add(JsonUtils.create(TextFormatting.YELLOW + date1));
            dates.add(JsonUtils.create(TextFormatting.YELLOW + date2));

            int indexToRemove = 0;

            for (int i = 0; i < tooltip.size(); i++)
            {
                if (tooltip.get(i).getString().contains(replacedText))
                {
                    indexToRemove = i;
                    break;
                }
            }
            if (!ClientUtils.isShiftKeyDown())
            {
                tooltip.add(indexToRemove + 1, JsonUtils.create("Press <SHIFT> to view exact time").applyTextStyle(TextFormatting.GRAY));
            }
            else
            {
                tooltip.remove(indexToRemove);
                tooltip.addAll(indexToRemove, dates);
            }
        }
    }

    private static void replaceAuctionTime(String lore, Calendar calendar, List<ITextComponent> tooltip, List<ITextComponent> dates, String replacedText)
    {
        Minecraft mc = Minecraft.getInstance();

        if (lore.startsWith(replacedText))
        {
            boolean isDay = lore.endsWith("d");
            lore = lore.replace(replacedText, "").replaceAll("[^0-9]+", " ");
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
            String date1 = new SimpleDateFormat("EEEE h:mm:ss a", Locale.ENGLISH).format(calendar.getTime());

            if (timeEstimate.length == 1)
            {
                date1 = new SimpleDateFormat("EEEE h:00 a", Locale.ENGLISH).format(calendar.getTime());
            }

            String date2 = new SimpleDateFormat("d MMMMM yyyy", Locale.ENGLISH).format(calendar.getTime());

            if (mc.currentScreen != null && mc.currentScreen instanceof ChestScreen)
            {
                ChestScreen chest = (ChestScreen)mc.currentScreen;
                String name = chest.getTitle().getUnformattedComponentText();

                if (name.equals("Auction View"))
                {
                    dates.add(JsonUtils.create("Ends at: " + TextFormatting.YELLOW + date1 + ", " + date2).applyTextStyle(TextFormatting.GRAY));
                }
                else
                {
                    dates.add(JsonUtils.create("Ends at: ").applyTextStyle(TextFormatting.GRAY));
                    dates.add(JsonUtils.create(TextFormatting.YELLOW + date1));
                    dates.add(JsonUtils.create(TextFormatting.YELLOW + date2));
                }
            }

            int indexToRemove = 0;

            for (int i = 0; i < tooltip.size(); i++)
            {
                if (tooltip.get(i).getString().contains(replacedText))
                {
                    indexToRemove = i;
                    break;
                }
            }
            if (!ClientUtils.isShiftKeyDown())
            {
                tooltip.add(indexToRemove + 1, JsonUtils.create("Press <SHIFT> to view exact time").applyTextStyle(TextFormatting.GRAY));
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
        TYPE_1("2070f6cb-f5db-367a-acd0-64d39a7e5d1b", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTM4MDcxNzIxY2M1YjRjZDQwNmNlNDMxYTEzZjg2MDgzYTg5NzNlMTA2NGQyZjg4OTc4Njk5MzBlZTZlNTIzNyJ9fX0="),
        TYPE_2("8ce61ae1-7cb4-3bdd-b1be-448c6fabb355", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZhMDg3ZWI3NmU3Njg3YTgxZTRlZjgxYTdlNjc3MjY0OTk5MGY2MTY3Y2ViMGY3NTBhNGM1ZGViNmM0ZmJhZCJ9fX0=");

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
}