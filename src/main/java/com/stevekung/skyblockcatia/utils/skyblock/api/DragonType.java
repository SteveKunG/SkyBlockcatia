package com.stevekung.skyblockcatia.utils.skyblock.api;

public enum DragonType
{
    OLD_DRAGON(15000000, "old"),
    PROTECTOR_DRAGON(9000000, "protector"),
    STRONG_DRAGON(9000000, "strong"),
    SUPERIOR_DRAGON(12000000, "superior"),
    UNSTABLE_DRAGON(9000000, "unstable"),
    WISE_DRAGON(9000000, "wise"),
    YOUNG_DRAGON(5000000, "young"),
    HOLY_DRAGON(5000000, "holy");

    public static final DragonType[] VALUES = DragonType.values();
    private final float maxHealth;
    private final String shortName;

    private DragonType(float maxHealth, String shortName)
    {
        this.maxHealth = maxHealth;
        this.shortName = shortName;
    }

    public float getMaxHealth()
    {
        return this.maxHealth;
    }

    public String getShortName()
    {
        return this.shortName;
    }

    public boolean isWhiteEye()
    {
        return this == DragonType.PROTECTOR_DRAGON || this == DragonType.OLD_DRAGON;
    }

    public static DragonType getDragonTypeById(String id)
    {
        if (id.startsWith("SUPERIOR_DRAGON"))
        {
            return DragonType.SUPERIOR_DRAGON;
        }
        else if (id.startsWith("WISE_DRAGON"))
        {
            return DragonType.WISE_DRAGON;
        }
        else if (id.startsWith("YOUNG_DRAGON"))
        {
            return DragonType.YOUNG_DRAGON;
        }
        else if (id.startsWith("OLD_DRAGON"))
        {
            return DragonType.OLD_DRAGON;
        }
        else if (id.startsWith("PROTECTOR_DRAGON"))
        {
            return DragonType.PROTECTOR_DRAGON;
        }
        else if (id.startsWith("UNSTABLE_DRAGON"))
        {
            return DragonType.UNSTABLE_DRAGON;
        }
        else if (id.startsWith("STRONG_DRAGON"))
        {
            return DragonType.STRONG_DRAGON;
        }
        else if (id.startsWith("HOLY_DRAGON"))
        {
            return DragonType.HOLY_DRAGON;
        }
        return null;
    }
}