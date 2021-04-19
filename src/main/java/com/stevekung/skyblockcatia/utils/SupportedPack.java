package com.stevekung.skyblockcatia.utils;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

public class SupportedPack
{
    private final String type;
    private final String name;
    private final String description;
    @SerializedName("x16")
    private List<String> pack16 = Lists.newArrayList();
    @SerializedName("x32")
    private List<String> pack32 = Lists.newArrayList();

    public static boolean FOUND;
    public static String TYPE;
    public static String RESOLUTION = "16";

    public SupportedPack(String type, String name, String desc, List<String> x16Name, List<String> x32Name)
    {
        this.type = type;
        this.name = name;
        this.description = desc;
        this.pack16 = x16Name;
        this.pack32 = x32Name;
    }

    public String getType()
    {
        return type;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDescription()
    {
        return this.description;
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