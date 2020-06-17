package com.stevekung.skyblockcatia.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SupportedPack
{
    @SerializedName("x16")
    private List<String> pack16 = new ArrayList<>();

    @SerializedName("x32")
    private List<String> pack32 = new ArrayList<>();

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