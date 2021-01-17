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
    private double attackSpeed;
    private double intelligence;
    private double seaCreatureChance;
    private double magicFind;
    private double petLuck;
    private double ferocity;
    private double abilityDamage;
    private double miningSpeed;
    private double miningFortune;
    private double farmingFortune;
    private double foragingFortune;

    public BonusStatTemplate(double health, double defense, double trueDefense, double effectiveHealth, double strength, double speed, double critChance, double critDamage, double attackSpeed, double intelligence, double seaCreatureChance, double magicFind, double petLuck, double ferocity, double abilityDamage, double miningSpeed, double miningFortune, double farmingFortune, double foragingFortune)
    {
        this.health = health;
        this.defense = defense;
        this.trueDefense = trueDefense;
        this.effectiveHealth = effectiveHealth;
        this.strength = strength;
        this.speed = speed;
        this.critChance = critChance;
        this.critDamage = critDamage;
        this.attackSpeed = attackSpeed;
        this.intelligence = intelligence;
        this.seaCreatureChance = seaCreatureChance;
        this.magicFind = magicFind;
        this.petLuck = petLuck;
        this.ferocity = ferocity;
        this.abilityDamage = abilityDamage;
        this.miningSpeed = miningSpeed;
        this.miningFortune = miningFortune;
        this.farmingFortune = farmingFortune;
        this.foragingFortune = foragingFortune;
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
        this.attackSpeed += toAdd.attackSpeed;
        this.intelligence += toAdd.intelligence;
        this.seaCreatureChance += toAdd.seaCreatureChance;
        this.magicFind += toAdd.magicFind;
        this.petLuck += toAdd.petLuck;
        this.ferocity += toAdd.ferocity;
        this.abilityDamage += toAdd.abilityDamage;
        this.miningSpeed += toAdd.miningSpeed;
        this.miningFortune += toAdd.miningFortune;
        this.farmingFortune += toAdd.farmingFortune;
        this.foragingFortune += toAdd.foragingFortune;
        return new BonusStatTemplate(this.health, this.defense, this.trueDefense, this.effectiveHealth, this.strength, this.speed, this.critChance, this.critDamage, this.attackSpeed, this.intelligence, this.seaCreatureChance, this.magicFind, this.petLuck, this.ferocity, this.abilityDamage, miningSpeed, miningFortune, farmingFortune, foragingFortune);
    }

    public static BonusStatTemplate getDefault()
    {
        return new BonusStatTemplate(100, 0, 0, 0, 0, 100, 30, 50, 0, 100, 20, 10, 0, 0, 0, 0, 0, 0, 0);
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

    public double getAttackSpeed()
    {
        return this.attackSpeed;
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

    public double getFerocity()
    {
        return this.ferocity;
    }

    public double getAbilityDamage()
    {
        return this.abilityDamage;
    }

    public double getMiningSpeed()
    {
        return miningSpeed;
    }

    public double getMiningFortune()
    {
        return miningFortune;
    }

    public double getFarmingFortune()
    {
        return farmingFortune;
    }

    public double getForagingFortune()
    {
        return foragingFortune;
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

    public void setAttackSpeed(double attackSpeed)
    {
        this.attackSpeed = attackSpeed;
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

    public void setFerocity(double ferocity)
    {
        this.ferocity = ferocity;
    }

    public void setAbilityDamage(double abilityDamage)
    {
        this.abilityDamage = abilityDamage;
    }

    public void setMiningSpeed(double miningSpeed)
    {
        this.miningSpeed = miningSpeed;
    }

    public void setMiningFortune(double miningFortune)
    {
        this.miningFortune = miningFortune;
    }

    public void setFarmingFortune(double farmingFortune)
    {
        this.farmingFortune = farmingFortune;
    }

    public void setForagingFortune(double foragingFortune)
    {
        this.foragingFortune = foragingFortune;
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

    public BonusStatTemplate addAttackSpeed(double attackSpeed)
    {
        this.attackSpeed += attackSpeed;
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

    public BonusStatTemplate addFerocity(double ferocity)
    {
        this.ferocity += ferocity;
        return this;
    }

    public BonusStatTemplate addAbilityDamage(double abilityDamage)
    {
        this.abilityDamage += abilityDamage;
        return this;
    }

    public BonusStatTemplate addMiningSpeed(double miningSpeed)
    {
        this.miningSpeed += miningSpeed;
        return this;
    }

    public BonusStatTemplate addMiningFortune(double miningFortune)
    {
        this.miningFortune += miningFortune;
        return this;
    }

    public BonusStatTemplate addFarmingFortune(double farmingFortune)
    {
        this.farmingFortune += farmingFortune;
        return this;
    }

    public BonusStatTemplate addForagingFortune(double foragingFortune)
    {
        this.foragingFortune += foragingFortune;
        return this;
    }
}