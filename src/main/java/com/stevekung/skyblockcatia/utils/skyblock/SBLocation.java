package com.stevekung.skyblockcatia.utils.skyblock;

public enum SBLocation
{
    NONE("None"),
    YOUR_ISLAND("Your Island"),
    THE_END("The End"),
    DRAGON_NEST("Dragon's Nest"),
    ;

    private final String location;

    SBLocation(String location)
    {
        this.location = location;
    }

    public String getLocation()
    {
        return this.location;
    }

    public boolean isTheEnd()
    {
        return this == SBLocation.THE_END || this == SBLocation.DRAGON_NEST;
    }
}