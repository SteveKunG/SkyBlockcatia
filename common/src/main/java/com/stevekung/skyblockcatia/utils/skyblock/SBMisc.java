package com.stevekung.skyblockcatia.utils.skyblock;

import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.SupportedPack;

public record SBMisc(@SerializedName("max_fairy_souls") int maxFairySouls, @SerializedName("supported_pack") SupportedPack[] supportedPack) {}