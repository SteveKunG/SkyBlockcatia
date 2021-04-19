package com.stevekung.skyblockcatia.utils.skyblock;

import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.SupportedPack;

public class SBMisc
{
    @SerializedName("max_fairy_souls")
    private final int maxFairySouls;
    @SerializedName("supported_pack")
    private final SupportedPack[] supportedPack;

    public SBMisc(int maxFairySouls, SupportedPack[] supportedPack)
    {
        this.maxFairySouls = maxFairySouls;
        this.supportedPack = supportedPack;
    }

    public int getMaxFairySouls()
    {
        return this.maxFairySouls;
    }

    public SupportedPack[] getSupportedPack()
    {
        return this.supportedPack;
    }
}