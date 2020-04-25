package com.stevekung.skyblockcatia.utils;

import net.minecraft.util.StringTranslate;

public class LangUtils
{
    private static final StringTranslate INSTANCE = StringTranslate.getInstance();

    public static String translate(String key)
    {
        return LangUtils.INSTANCE.translateKey(key);
    }

    public static String translate(String key, Object... format)
    {
        return LangUtils.INSTANCE.translateKeyFormat(key, format);
    }
}