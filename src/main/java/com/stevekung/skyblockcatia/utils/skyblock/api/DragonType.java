package com.stevekung.skyblockcatia.utils.skyblock.api;

public enum DragonType
{
    OLD_DRAGON(10000000, "old"),
    PROTECTOR_DRAGON(7500000, "protector"),
    STRONG_DRAGON(7500000, "strong"),
    SUPERIOR_DRAGON(10000000, "superior"),
    UNSTABLE_DRAGON(6000000, "unstable"),
    WISE_DRAGON(6000000, "wise"),
    YOUNG_DRAGON(5000000, "young");

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
        DragonType dragonType = null;

        if (id.startsWith("SUPERIOR_DRAGON"))
        {
            dragonType = DragonType.SUPERIOR_DRAGON;
        }
        else if (id.startsWith("WISE_DRAGON"))
        {
            dragonType = DragonType.WISE_DRAGON;
        }
        else if (id.startsWith("YOUNG_DRAGON"))
        {
            dragonType = DragonType.YOUNG_DRAGON;
        }
        else if (id.startsWith("OLD_DRAGON"))
        {
            dragonType = DragonType.OLD_DRAGON;
        }
        else if (id.startsWith("PROTECTOR_DRAGON"))
        {
            dragonType = DragonType.PROTECTOR_DRAGON;
        }
        else if (id.startsWith("UNSTABLE_DRAGON"))
        {
            dragonType = DragonType.UNSTABLE_DRAGON;
        }
        else if (id.startsWith("STRONG_DRAGON"))
        {
            dragonType = DragonType.STRONG_DRAGON;
        }
        return dragonType;
    }
}