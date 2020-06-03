package com.stevekung.skyblockcatia.utils.skyblock.api;

public class BonusStatTemplate
{
    private double health;
    private double defense;
    private double trueDefense;
    private double effectiveHealth;
    private double strength;
    private double speed;
    private double critChance;
    private double critDamage;
    private double intelligence;
    private double seaCreatureChance;
    private double magicFind;
    private double petLuck;

    public BonusStatTemplate(double health, double defense, double trueDefense, double effectiveHealth, double strength, double speed, double critChance, double critDamage, double intelligence, double seaCreatureChance, double magicFind, double petLuck)
    {
        this.health = health;
        this.defense = defense;
        this.trueDefense = trueDefense;
        this.effectiveHealth = effectiveHealth;
        this.strength = strength;
        this.speed = speed;
        this.critChance = critChance;
        this.critDamage = critDamage;
        this.intelligence = intelligence;
        this.seaCreatureChance = seaCreatureChance;
        this.magicFind = magicFind;
        this.petLuck = petLuck;
    }

    public BonusStatTemplate add(BonusStatTemplate toAdd)
    {
        this.health += toAdd.health;
        this.defense += toAdd.defense;
        this.trueDefense += toAdd.trueDefense;
        this.effectiveHealth += toAdd.effectiveHealth;
        this.strength += toAdd.strength;
        this.speed += toAdd.speed;
        this.critChance += toAdd.critChance;
        this.critDamage += toAdd.critDamage;
        this.intelligence += toAdd.intelligence;
        this.seaCreatureChance += toAdd.seaCreatureChance;
        this.magicFind += toAdd.magicFind;
        this.petLuck += toAdd.petLuck;
        return new BonusStatTemplate(this.health, this.defense, this.trueDefense, this.effectiveHealth, this.strength, this.speed, this.critChance, this.critDamage, this.intelligence, this.seaCreatureChance, this.magicFind, this.petLuck);
    }

    public double getHealth()
    {
        return this.health;
    }

    public double getDefense()
    {
        if (this.defense <= 0)
        {
            return 0;
        }
        return this.defense;
    }

    public double getTrueDefense()
    {
        return this.trueDefense;
    }

    public double getEffectiveHealth()
    {
        return this.effectiveHealth;
    }

    public double getStrength()
    {
        return this.strength;
    }

    public double getSpeed()
    {
        return this.speed;
    }

    public double getCritChance()
    {
        if (this.critChance > 100)
        {
            return 100;
        }
        return this.critChance;
    }

    public double getCritDamage()
    {
        return this.critDamage;
    }

    public double getIntelligence()
    {
        return this.intelligence;
    }

    public double getSeaCreatureChance()
    {
        return this.seaCreatureChance;
    }

    public double getMagicFind()
    {
        return this.magicFind;
    }

    public double getPetLuck()
    {
        return this.petLuck;
    }

    public void setHealth(double health)
    {
        this.health = health;
    }

    public void setDefense(double defense)
    {
        this.defense = defense;
    }

    public void setTrueDefense(double trueDefense)
    {
        this.trueDefense = trueDefense;
    }

    public void setEffectiveHealth(double effectiveHealth)
    {
        this.effectiveHealth = effectiveHealth;
    }

    public void setStrength(double strength)
    {
        this.strength = strength;
    }

    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    public void setCritChance(double critChance)
    {
        this.critChance = critChance;
    }

    public void setCritDamage(double critDamage)
    {
        this.critDamage = critDamage;
    }

    public void setIntelligence(double intelligence)
    {
        this.intelligence = intelligence;
    }

    public void setSeaCreatureChance(double seaCreatureChance)
    {
        this.seaCreatureChance = seaCreatureChance;
    }

    public void setMagicFind(double magicFind)
    {
        this.magicFind = magicFind;
    }

    public void setPetLuck(double petLuck)
    {
        this.petLuck = petLuck;
    }

    public BonusStatTemplate addHealth(double health)
    {
        this.health += health;
        return this;
    }

    public BonusStatTemplate addDefense(double defense)
    {
        this.defense += defense;
        return this;
    }

    public BonusStatTemplate addTrueDefense(double trueDefense)
    {
        this.trueDefense += trueDefense;
        return this;
    }

    public BonusStatTemplate addEffectiveHealth(double effectiveHealth)
    {
        this.effectiveHealth += effectiveHealth;
        return this;
    }

    public BonusStatTemplate addStrength(double strength)
    {
        this.strength += strength;
        return this;
    }

    public BonusStatTemplate addSpeed(double speed)
    {
        this.speed += speed;
        return this;
    }

    public BonusStatTemplate addCritChance(double critChance)
    {
        this.critChance += critChance;
        return this;
    }

    public BonusStatTemplate addCritDamage(double critDamage)
    {
        this.critDamage += critDamage;
        return this;
    }

    public BonusStatTemplate addIntelligence(double intelligence)
    {
        this.intelligence += intelligence;
        return this;
    }

    public BonusStatTemplate addSeaCreatureChance(double seaCreatureChance)
    {
        this.seaCreatureChance += seaCreatureChance;
        return this;
    }

    public BonusStatTemplate addMagicFind(double magicFind)
    {
        this.magicFind += magicFind;
        return this;
    }

    public BonusStatTemplate addPetLuck(double petLuck)
    {
        this.petLuck += petLuck;
        return this;
    }
}