package com.stevekung.skyblockcatia.utils;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

public class SupportedPack
{
    @SerializedName("x16")
    private List<String> pack16 = Lists.newArrayList();

    @SerializedName("x32")
    private List<String> pack32 = Lists.newArrayList();

    public SupportedPack(List<String> pack16, List<String> pack32)
    {
        this.pack16 = pack16;
        this.pack32 = pack32;
    }

    public List<String> getPack16()
    {
        return this.pack16;
    }

    public List<String> getPack32()
    {
        return this.pack32;
    }
}