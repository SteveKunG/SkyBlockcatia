package com.stevekung.skyblockcatia.utils;

public class SkyBlockBossBar
{
    public static boolean renderBossBar;
    public static float healthScale;
    public static String bossName;

    public enum DragonType
    {
        OLD(15000000, "Old Dragon"),
        PROTECTOR(9000000, "Protector Dragon"),
        STRONG(9000000, "Strong Dragon"),
        SUPERIOR(12000000, "Superior Dragon"),
        UNSTABLE(9000000, "Unstable Dragon"),
        WISE(9000000, "Wise Dragon"),
        YOUNG(5000000, "Young Dragon"),
        HOLY(5000000, "Holy Dragon");

        private final float maxHealth;
        private final String name;

        private DragonType(float maxHealth, String name)
        {
            this.maxHealth = maxHealth;
            this.name = name;
        }

        public float getMaxHealth()
        {
            return this.maxHealth;
        }

        public String getName()
        {
            return this.name;
        }
    }
}