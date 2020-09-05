package com.stevekung.skyblockcatia.integration.textoverflow;

import java.lang.reflect.Field;

public class TooltipOverflow
{
    public static final TooltipOverflow INSTANCE = new TooltipOverflow();

    public boolean checkCanScroll()
    {
        try
        {
            Class<?> clazz = Class.forName("club.sk1er.mods.overflowscroll.GuiUtilsOverride");
            Field field = clazz.getDeclaredField("allowScrolling");
            return field.getBoolean(clazz);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
