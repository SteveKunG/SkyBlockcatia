package com.stevekung.skyblockcatia.utils.skyblock.api;

public class ViewerData
{
    private boolean hasKills = true;
    private boolean hasDeaths = true;
    private boolean hasOthers = true;
    private boolean hasSkills = true;
    private boolean hasSlayers = true;
    private boolean hasCollections = true;
    private boolean hasMinions = true;

    public void setHasKills(boolean hasKills)
    {
        this.hasKills = hasKills;
    }

    public void setHasDeaths(boolean hasDeaths)
    {
        this.hasDeaths = hasDeaths;
    }

    public void setHasOthers(boolean hasOthers)
    {
        this.hasOthers = hasOthers;
    }

    public void setHasSkills(boolean hasSkills)
    {
        this.hasSkills = hasSkills;
    }

    public void setHasSlayers(boolean hasSlayers)
    {
        this.hasSlayers = hasSlayers;
    }

    public void setHasCollections(boolean hasCollections)
    {
        this.hasCollections = hasCollections;
    }

    public void setHasMinions(boolean hasMinions)
    {
        this.hasMinions = hasMinions;
    }

    public boolean isHasKills()
    {
        return this.hasKills;
    }

    public boolean isHasDeaths()
    {
        return this.hasDeaths;
    }

    public boolean isHasOthers()
    {
        return this.hasOthers;
    }

    public boolean isHasSkills()
    {
        return this.hasSkills;
    }

    public boolean isHasSlayers()
    {
        return this.hasSlayers;
    }

    public boolean isHasCollections()
    {
        return this.hasCollections;
    }

    public boolean isHasMinions()
    {
        return this.hasMinions;
    }
}