package com.stevekung.skyblockcatia.utils.skyblock.api;

public class BonusStatTemplate
{
    private int health;
    private int defense;
    private int trueDefense;
    private int effectiveHealth;
    private int strength;
    private int speed;
    private int critChance;
    private int critDamage;
    private int intelligence;
    private int seaCreatureChance;
    private int magicFind;
    private int petLuck;

    public BonusStatTemplate(int health, int defense, int trueDefense, int effectiveHealth, int strength, int speed, int critChance, int critDamage, int intelligence, int seaCreatureChance, int magicFind, int petLuck)
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

    public int getHealth()
    {
        return this.health;
    }

    public int getDefense()
    {
        if (this.defense <= 0)
        {
            return 0;
        }
        return this.defense;
    }

    public int getTrueDefense()
    {
        return this.trueDefense;
    }

    public int getEffectiveHealth()
    {
        return this.effectiveHealth;
    }

    public int getStrength()
    {
        return this.strength;
    }

    public int getSpeed()
    {
        return this.speed;
    }

    public int getCritChance()
    {
        if (this.critChance > 100)
        {
            return 100;
        }
        return this.critChance;
    }

    public int getCritDamage()
    {
        return this.critDamage;
    }

    public int getIntelligence()
    {
        return this.intelligence;
    }

    public int getSeaCreatureChance()
    {
        return this.seaCreatureChance;
    }

    public int getMagicFind()
    {
        return this.magicFind;
    }

    public int getPetLuck()
    {
        return this.petLuck;
    }

    public void setHealth(int health)
    {
        this.health = health;
    }

    public void setDefense(int defense)
    {
        this.defense = defense;
    }

    public void setTrueDefense(int trueDefense)
    {
        this.trueDefense = trueDefense;
    }

    public void setEffectiveHealth(int effectiveHealth)
    {
        this.effectiveHealth = effectiveHealth;
    }

    public void setStrength(int strength)
    {
        this.strength = strength;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    public void setCritChance(int critChance)
    {
        this.critChance = critChance;
    }

    public void setCritDamage(int critDamage)
    {
        this.critDamage = critDamage;
    }

    public void setIntelligence(int intelligence)
    {
        this.intelligence = intelligence;
    }

    public void setSeaCreatureChance(int seaCreatureChance)
    {
        this.seaCreatureChance = seaCreatureChance;
    }

    public void setMagicFind(int magicFind)
    {
        this.magicFind = magicFind;
    }

    public void setPetLuck(int petLuck)
    {
        this.petLuck = petLuck;
    }

    public BonusStatTemplate addHealth(int health)
    {
        this.health += health;
        return this;
    }

    public BonusStatTemplate addDefense(int defense)
    {
        this.defense += defense;
        return this;
    }

    public BonusStatTemplate addTrueDefense(int trueDefense)
    {
        this.trueDefense += trueDefense;
        return this;
    }

    public BonusStatTemplate addEffectiveHealth(int effectiveHealth)
    {
        this.effectiveHealth += effectiveHealth;
        return this;
    }

    public BonusStatTemplate addStrength(int strength)
    {
        this.strength += strength;
        return this;
    }

    public BonusStatTemplate addSpeed(int speed)
    {
        this.speed += speed;
        return this;
    }

    public BonusStatTemplate addCritChance(int critChance)
    {
        this.critChance += critChance;
        return this;
    }

    public BonusStatTemplate addCritDamage(int critDamage)
    {
        this.critDamage += critDamage;
        return this;
    }

    public BonusStatTemplate addIntelligence(int intelligence)
    {
        this.intelligence += intelligence;
        return this;
    }

    public BonusStatTemplate addSeaCreatureChance(int seaCreatureChance)
    {
        this.seaCreatureChance += seaCreatureChance;
        return this;
    }

    public BonusStatTemplate addMagicFind(int magicFind)
    {
        this.magicFind += magicFind;
        return this;
    }

    public BonusStatTemplate addPetLuck(int petLuck)
    {
        this.petLuck += petLuck;
        return this;
    }
}