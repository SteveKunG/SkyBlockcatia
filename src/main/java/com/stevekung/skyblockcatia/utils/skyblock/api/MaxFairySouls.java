package com.stevekung.skyblockcatia.utils.skyblock.api;

import com.google.gson.annotations.SerializedName;

public class MaxFairySouls
{
    @SerializedName("max_fairy_souls")
    private int maxFairySouls;

    public int getMaxFairySouls()
    {
        return this.maxFairySouls;
    }
}