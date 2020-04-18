package stevekung.mods.indicatia.event;

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

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiEditSign;
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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import stevekung.mods.indicatia.config.ConfigManagerIN;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.config.ToastMode;
import stevekung.mods.indicatia.gui.SignSelectionList;
import stevekung.mods.indicatia.gui.api.GuiSkyBlockAPIViewer;
import stevekung.mods.indicatia.gui.toasts.*;
import stevekung.mods.indicatia.gui.toasts.ToastUtils.ToastType;
import stevekung.mods.indicatia.handler.KeyBindingHandler;
import stevekung.mods.indicatia.utils.*;

public class HypixelEventHandler
{
    private static final Pattern LETTERS_NUMBERS = Pattern.compile("[^a-z A-Z:0-9/']");
    private static final Pattern JOINED_PARTY_PATTERN = Pattern.compile("(?<name>\\w+) joined the party!");
    private static final Pattern VISIT_ISLAND_PATTERN = Pattern.compile("(?:\\[SkyBlock\\]|\\[SkyBlock\\] (?:\\[VIP?\\u002B{0,1}\\]|\\[MVP?\\u002B{0,2}\\]|\\[YOUTUBE\\])) (?<name>\\w+) is visiting Your Island!");
    private static final Pattern NICK_PATTERN = Pattern.compile("^You are now nicked as (?<nick>\\w+)!");
    private static final String UUID_PATTERN_STRING = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    private static final Pattern UUID_PATTERN = Pattern.compile("Your new API key is (?<uuid>" + HypixelEventHandler.UUID_PATTERN_STRING + ")");
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
    public static SkyBlockLocation SKY_BLOCK_LOCATION = SkyBlockLocation.YOUR_ISLAND;
    private static final List<String> PARTY_LIST = new ArrayList<>();
    public static String SKYBLOCK_AMPM = "";
    public static float dragonHealth;
    private static final List<ToastUtils.ItemDropCheck> ITEM_DROP_CHECK_LIST = new ArrayList<>();
    private List<ItemStack> previousInventory;
    private final Minecraft mc;

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
                HypixelEventHandler.getHypixelNickedPlayer(this.mc);

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

                        if (scoreText.startsWith("Dragon Health: "))
                        {
                            try
                            {
                                HypixelEventHandler.dragonHealth = Float.valueOf(scoreText.replaceAll("[^\\d]", ""));
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
                    SkyBlockBossStatus.renderBossBar = foundDrag;
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

        String message = event.message.getUnformattedText();
        boolean cancelMessage = false;

        if (InfoUtils.INSTANCE.isHypixel())
        {
            // Common matcher
            Matcher nickMatcher = HypixelEventHandler.NICK_PATTERN.matcher(message);
            Matcher visitIslandMatcher = HypixelEventHandler.VISIT_ISLAND_PATTERN.matcher(message);
            Matcher joinedPartyMatcher = HypixelEventHandler.JOINED_PARTY_PATTERN.matcher(message);
            Matcher uuidMatcher = HypixelEventHandler.UUID_PATTERN.matcher(message);
            Matcher chatMatcher = HypixelEventHandler.CHAT_PATTERN.matcher(message);

            // Item Drop matcher
            Matcher rareDropPattern = HypixelEventHandler.RARE_DROP_PATTERN.matcher(message);
            Matcher dragonDropPattern = HypixelEventHandler.DRAGON_DROP_PATTERN.matcher(message);

            // Fish catch matcher
            Matcher fishCatchPattern = HypixelEventHandler.FISH_CATCH_PATTERN.matcher(message);
            Matcher coinsCatchPattern = HypixelEventHandler.COINS_CATCH_PATTERN.matcher(message);

            // Slayer Drop matcher
            Matcher rareDropBracketPattern = HypixelEventHandler.RARE_DROP_WITH_BRACKET_PATTERN.matcher(message);
            Matcher rareDrop2SpaceBracketPattern = HypixelEventHandler.RARE_DROP_2_SPACE_PATTERN.matcher(message);

            // Gift matcher
            Matcher coinsGiftPattern = HypixelEventHandler.COINS_GIFT_PATTERN.matcher(message);
            Matcher skillExpGiftPattern = HypixelEventHandler.SKILL_EXP_GIFT_PATTERN.matcher(message);
            Matcher itemDropGiftPattern = HypixelEventHandler.ITEM_DROP_GIFT_PATTERN.matcher(message);
            Matcher santaTierPattern = HypixelEventHandler.SANTA_TIER_PATTERN.matcher(message);

            // Pet
            Matcher petLevelUpPattern = HypixelEventHandler.PET_LEVEL_UP_PATTERN.matcher(message);
            Matcher petDropPattern = HypixelEventHandler.PET_DROP_PATTERN.matcher(message);

            if (event.type == 0)
            {
                if (message.contains("Illegal characters in chat") || message.contains("A kick occurred in your connection"))
                {
                    cancelMessage = true;
                }
                else if (message.contains("You were spawned in Limbo."))
                {
                    event.message = JsonUtils.create(message).setChatStyle(JsonUtils.green());
                }
                else if (message.contains("Your nick has been reset!"))
                {
                    ExtendedConfig.instance.hypixelNickName = "";
                    ExtendedConfig.instance.save();
                }

                if (visitIslandMatcher.matches())
                {
                    String name = visitIslandMatcher.group("name");

                    if (ToastMode.getById(ExtendedConfig.instance.visitIslandToastMode).equalsIgnoreCase("toast") || ToastMode.getById(ExtendedConfig.instance.visitIslandToastMode).equalsIgnoreCase("chat_and_toast"))
                    {
                        HypixelEventHandler.addVisitingToast(name);
                        LoggerIN.logToast(message);
                    }

                    cancelMessage = ToastMode.getById(ExtendedConfig.instance.visitIslandToastMode).equalsIgnoreCase("toast") || ToastMode.getById(ExtendedConfig.instance.visitIslandToastMode).equalsIgnoreCase("disabled");

                    if (ExtendedConfig.instance.addPartyVisitIsland && !HypixelEventHandler.PARTY_LIST.stream().anyMatch(pname -> name.equals(pname)))
                    {
                        this.mc.thePlayer.sendChatMessage("/p " + name);
                    }
                }
                else if (joinedPartyMatcher.matches())
                {
                    HypixelEventHandler.PARTY_LIST.add(joinedPartyMatcher.group("name"));
                }
                else if (nickMatcher.matches())
                {
                    ExtendedConfig.instance.hypixelNickName = nickMatcher.group("nick");
                    ExtendedConfig.instance.save();
                }
                else if (uuidMatcher.matches())
                {
                    SkyBlockAPIUtils.setApiKeyFromServer(uuidMatcher.group("uuid"));
                    ClientUtils.printClientMessage("Setting a new API Key!", JsonUtils.green());
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

                if (HypixelEventHandler.isSkyBlock)
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
                                IChatComponent chat = event.message.createCopy();
                                chat.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/p " + name));
                                event.message = chat;
                            }
                        }
                        catch (Exception e) {}
                    }

                    if (ToastMode.getById(ExtendedConfig.instance.fishCatchToastMode).equalsIgnoreCase("toast") || ToastMode.getById(ExtendedConfig.instance.fishCatchToastMode).equalsIgnoreCase("chat_and_toast"))
                    {
                        if (fishCatchPattern.matches())
                        {
                            HypixelEventHandler.addFishLoot(fishCatchPattern);
                            LoggerIN.logToast(message);
                            cancelMessage = ToastMode.getById(ExtendedConfig.instance.fishCatchToastMode).equalsIgnoreCase("toast");
                        }
                        else if (coinsCatchPattern.matches())
                        {
                            String type = coinsCatchPattern.group("type");
                            String coin = coinsCatchPattern.group("coin");
                            CoinType coinType = type.equals("GOOD") ? CoinType.TYPE_1 : CoinType.TYPE_2;
                            ItemStack coinSkull = RenderUtils.getSkullItemStack(coinType.getId(), coinType.getValue());
                            NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), type.equals("GOOD") ? ToastUtils.DropType.GOOD_CATCH_COINS : ToastUtils.DropType.GREAT_CATCH_COINS, Integer.valueOf(coin.replace(",", "")), coinSkull, "Coins");
                            LoggerIN.logToast(message);
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
                            LoggerIN.logToast(message);
                            cancelMessage = ToastMode.getById(ExtendedConfig.instance.giftToastMode).equalsIgnoreCase("toast");
                        }
                        else if (skillExpGiftPattern.matches())
                        {
                            String type = skillExpGiftPattern.group("type");
                            String exp = skillExpGiftPattern.group("exp");
                            String skill = skillExpGiftPattern.group("skill");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), rarity, Integer.valueOf(exp.replace(",", "")), null, skill);
                            LoggerIN.logToast(message);
                            cancelMessage = ToastMode.getById(ExtendedConfig.instance.giftToastMode).equalsIgnoreCase("toast");
                        }
                        else if (itemDropGiftPattern.matches())
                        {
                            String type = itemDropGiftPattern.group("type");
                            String name = itemDropGiftPattern.group("item");
                            ToastUtils.DropType rarity = type.equals("RARE") ? ToastUtils.DropType.RARE_GIFT : type.equals("SWEET") ? ToastUtils.DropType.SWEET_GIFT : ToastUtils.DropType.COMMON_GIFT;
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(EnumChatFormatting.getTextWithoutFormattingCodes(name), rarity, ToastUtils.ToastType.GIFT));
                            LoggerIN.logToast(message);
                            cancelMessage = ToastMode.getById(ExtendedConfig.instance.giftToastMode).equalsIgnoreCase("toast");
                        }
                        else if (santaTierPattern.matches())
                        {
                            String name = santaTierPattern.group("item");
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(EnumChatFormatting.getTextWithoutFormattingCodes(name), ToastUtils.DropType.SANTA_TIER, ToastUtils.ToastType.GIFT));
                            LoggerIN.logToast(message);
                            cancelMessage = ToastMode.getById(ExtendedConfig.instance.giftToastMode).equalsIgnoreCase("toast");
                        }
                    }

                    if (ToastMode.getById(ExtendedConfig.instance.itemDropToastMode).equalsIgnoreCase("toast") || ToastMode.getById(ExtendedConfig.instance.itemDropToastMode).equalsIgnoreCase("chat_and_toast"))
                    {
                        boolean isToast = ToastMode.getById(ExtendedConfig.instance.itemDropToastMode).equalsIgnoreCase("toast");

                        if (message.contains("You destroyed an Ender Crystal!"))
                        {
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck("Crystal Fragment", ToastUtils.DropType.DRAGON_CRYSTAL_FRAGMENT, ToastType.DROP));
                            LoggerIN.logToast(message);
                            cancelMessage = isToast;
                        }

                        if (rareDropPattern.matches())
                        {
                            String name = rareDropPattern.group("item");
                            String magicFind = rareDropPattern.group("mf");
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(EnumChatFormatting.getTextWithoutFormattingCodes(name), magicFind, ToastUtils.DropType.RARE_DROP, ToastType.DROP));
                            LoggerIN.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (dragonDropPattern.matches())
                        {
                            String name = dragonDropPattern.group("item");
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(EnumChatFormatting.getTextWithoutFormattingCodes(name), ToastUtils.DropType.DRAGON_DROP, ToastType.DROP));
                            LoggerIN.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (rareDropBracketPattern.matches())
                        {
                            String type = rareDropBracketPattern.group("type");
                            String name = rareDropBracketPattern.group("item");
                            String magicFind = rareDropBracketPattern.group(3);
                            ToastUtils.DropType dropType = type.equals("VERY RARE") ? ToastUtils.DropType.SLAYER_VERY_RARE_DROP : ToastUtils.DropType.SLAYER_CRAZY_RARE_DROP;
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(EnumChatFormatting.getTextWithoutFormattingCodes(name), magicFind, dropType, ToastType.DROP));
                            LoggerIN.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (rareDrop2SpaceBracketPattern.matches())
                        {
                            String name = rareDrop2SpaceBracketPattern.group("item");
                            String magicFind = rareDrop2SpaceBracketPattern.group(2);
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(EnumChatFormatting.getTextWithoutFormattingCodes(name), magicFind, ToastUtils.DropType.SLAYER_RARE_DROP, ToastType.DROP));
                            LoggerIN.logToast(message);
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
                            ItemStack itemStack = new ItemStack(Items.bone);
                            itemStack.setStackDisplayName(name);
                            NumericToast.addValueOrUpdate(HUDRenderEventHandler.INSTANCE.getToastGui(), ToastUtils.DropType.PET_LEVEL_UP, Integer.valueOf(level), itemStack, "Pet");
                            LoggerIN.logToast(message);
                            cancelMessage = isToast;
                        }
                        else if (petDropPattern.matches())
                        {
                            String name = petDropPattern.group("item");
                            String magicFind = petDropPattern.group("mf");
                            HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(EnumChatFormatting.getTextWithoutFormattingCodes(name), magicFind, ToastUtils.DropType.PET_DROP, ToastType.DROP));
                            LoggerIN.logToast(message);
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
                this.mc.thePlayer.sendChatMessage("/viewcraftingtable");
            }
            else if (KeyBindingHandler.KEY_SB_MENU.isKeyDown())
            {
                this.mc.thePlayer.sendChatMessage("/sbmenu");
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
                    this.mc.displayGuiScreen(new GuiSkyBlockAPIViewer(GuiSkyBlockAPIViewer.GuiState.PLAYER, player.getDisplayNameString(), ""));
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
            SkyBlockBossStatus.renderBossBar = false;
            SkyBlockBossStatus.bossName = null;
            SkyBlockBossStatus.healthScale = 0;
            HypixelEventHandler.dragonHealth = 0;
            ITEM_DROP_CHECK_LIST.clear();
        }
    }

    @SubscribeEvent
    public void onDisconnectedFromServerEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        SignSelectionList.clearAll();
        HypixelEventHandler.dragonHealth = 0;
        SkyBlockBossStatus.renderBossBar = false;
        SkyBlockBossStatus.bossName = null;
        SkyBlockBossStatus.healthScale = 0;
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

                if (extraAttrib.hasKey("timestamp"))
                {
                    int toAdd = this.mc.gameSettings.advancedItemTooltips ? 3 : 1;
                    DateFormat parseFormat = new SimpleDateFormat("MM/dd/yy HH:mm a");
                    Date date = parseFormat.parse(extraAttrib.getString("timestamp"));
                    String formatted = new SimpleDateFormat("d MMMM yyyy").format(date);
                    event.toolTip.add(event.toolTip.size() - toAdd, EnumChatFormatting.GRAY + "Obtained: " + EnumChatFormatting.RESET + formatted);
                }
            }
        }
        catch (Exception e) {}

        try
        {
            for (String tooltip : event.toolTip)
            {
                String lore = EnumChatFormatting.getTextWithoutFormattingCodes(tooltip);

                HypixelEventHandler.replaceEventEstimateTime(lore, calendar, event.toolTip, dates, "Starts in: ");
                HypixelEventHandler.replaceEventEstimateTime(lore, calendar, event.toolTip, dates, "Starting in: ");

                HypixelEventHandler.replaceBankInterestTime(lore, calendar, event.toolTip, dates, "Interest in: ");
                HypixelEventHandler.replaceBankInterestTime(lore, calendar, event.toolTip, dates, "Until interest: ");

                HypixelEventHandler.replaceAuctionTime(lore, calendar, event.toolTip, dates, "Ends in: ");
            }
        }
        catch (Exception e) {}
    }

    private static void getHypixelNickedPlayer(Minecraft mc)
    {
        if (InfoUtils.INSTANCE.isHypixel() && mc.currentScreen instanceof GuiEditSign)
        {
            GuiEditSign gui = (GuiEditSign) mc.currentScreen;

            if (gui.tileSign != null)
            {
                if (!(gui.tileSign.signText[2].getUnformattedText().contains("Enter your") && gui.tileSign.signText[3].getUnformattedText().contains("username here")))
                {
                    return;
                }

                ExtendedConfig.instance.hypixelNickName = gui.tileSign.signText[0].getUnformattedText();

                if (mc.thePlayer.ticksExisted % 40 == 0)
                {
                    ExtendedConfig.instance.save();
                }
            }
        }
    }

    private String keepLettersAndNumbersOnly(String text)
    {
        return LETTERS_NUMBERS.matcher(text).replaceAll("");
    }

    private static void addFishLoot(Matcher matcher)
    {
        String dropType = matcher.group("type");
        String name = matcher.group("item");
        HypixelEventHandler.ITEM_DROP_CHECK_LIST.add(new ToastUtils.ItemDropCheck(EnumChatFormatting.getTextWithoutFormattingCodes(name), dropType.equals("GOOD") ? ToastUtils.DropType.GOOD_CATCH : ToastUtils.DropType.GREAT_CATCH, ToastType.DROP));
    }

    /**
     * Credit to codes.biscuit.skyblockaddons.utils.InventoryUtils
     */
    private void getInventoryDifference(ItemStack[] currentInventory)
    {
        List<ItemStack> newInventory = this.copyInventory(currentInventory);

        if (this.previousInventory != null)
        {
            for (int i = 0; i < newInventory.size(); i++)
            {
                ItemStack newItem = newInventory.get(i);

                if (newItem != null)
                {
                    String newItemName = EnumChatFormatting.getTextWithoutFormattingCodes(newItem.getDisplayName());

                    for (Iterator<ToastUtils.ItemDropCheck> iterator = HypixelEventHandler.ITEM_DROP_CHECK_LIST.iterator(); iterator.hasNext();)
                    {
                        ToastUtils.ItemDropCheck drop = iterator.next();

                        if (drop.getName().equals(newItemName))
                        {
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
        CommonUtils.runAsync(() ->
        {
            try
            {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                JsonObject obj = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
                String rawName = obj.get("name").getAsString();
                String rawUUID = obj.get("id").getAsString();
                String uuid = rawUUID.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
                HUDRenderEventHandler.INSTANCE.getToastGui().add(new VisitIslandToast(rawName, UUID.fromString(uuid)));
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        });
    }

    private static void replaceEventEstimateTime(String lore, Calendar calendar, List<String> tooltip, List<String> dates, String replacedText)
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
            dates.add("Event starts at: ");
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