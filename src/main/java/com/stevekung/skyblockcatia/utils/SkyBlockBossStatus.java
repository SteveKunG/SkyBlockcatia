package com.stevekung.skyblockcatia.utils;

import com.stevekung.skyblockcatia.event.HypixelEventHandler;

import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.util.EnumChatFormatting;

public class SkyBlockBossStatus
{
    public static boolean renderBossBar;
    public static float healthScale;
    public static String bossName;

    public static void setBossStatus(IBossDisplayData displayData, boolean hasColorModifier)
    {
        if (HypixelEventHandler.isSkyBlock && HypixelEventHandler.SKY_BLOCK_LOCATION.isTheEnd())
        {
            String name = EnumChatFormatting.getTextWithoutFormattingCodes(displayData.getDisplayName().getUnformattedText());
            DragonType type = DragonType.SUPERIOR;

            if (name.equals("Old Dragon"))
            {
                type = DragonType.OLD;
            }
            else if (name.equals("Protector Dragon"))
            {
                type = DragonType.PROTECTOR;
            }
            else if (name.equals("Strong Dragon"))
            {
                type = DragonType.STRONG;
            }
            else if (name.equals("Superior Dragon"))
            {
                type = DragonType.SUPERIOR;
            }
            else if (name.equals("Unstable Dragon"))
            {
                type = DragonType.UNSTABLE;
            }
            else if (name.equals("Wise Dragon"))
            {
                type = DragonType.WISE;
            }
            else if (name.equals("Young Dragon"))
            {
                type = DragonType.YOUNG;
            }
            else if (name.equals("Holy Dragon"))
            {
                type = DragonType.HOLY;
            }
            SkyBlockBossStatus.healthScale = HypixelEventHandler.dragonHealth / type.getMaxHealth();
            SkyBlockBossStatus.bossName = displayData.getDisplayName().getFormattedText();
        }
        else
        {
            BossStatus.setBossStatus(displayData, false);
        }
    }

    enum DragonType
    {
        OLD(15000000),
        PROTECTOR(9000000),
        STRONG(9000000),
        SUPERIOR(12000000),
        UNSTABLE(9000000),
        WISE(9000000),
        YOUNG(5000000),
        HOLY(5000000);

        private final float maxHealth;

        private DragonType(float maxHealth)
        {
            this.maxHealth = maxHealth;
        }

        public float getMaxHealth()
        {
            return this.maxHealth;
        }
    }
}