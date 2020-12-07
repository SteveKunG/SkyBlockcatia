package com.stevekung.skyblockcatia.utils.skyblock.api;

public interface IBonusTemplate
{
    default int getLevel()
    {
        return 0;
    }

    default double getHealth()
    {
        return 0;
    }

    default double getDefense()
    {
        return 0;
    }

    default double getTrueDefense()
    {
        return 0;
    }

    default double getStrength()
    {
        return 0;
    }

    default double getSpeed()
    {
        return 0;
    }

    default double getCritChance()
    {
        return 0;
    }

    default double getCritDamage()
    {
        return 0;
    }

    default double getAttackSpeed()
    {
        return 0;
    }

    default double getIntelligence()
    {
        return 0;
    }

    default double getSeaCreatureChance()
    {
        return 0;
    }

    default double getMagicFind()
    {
        return 0;
    }

    default double getPetLuck()
    {
        return 0;
    }

    default double getFerocity()
    {
        return 0;
    }

    default double getAbilityDamage()
    {
        return 0;
    }
}