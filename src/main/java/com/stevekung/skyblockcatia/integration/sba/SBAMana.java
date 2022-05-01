package com.stevekung.skyblockcatia.integration.sba;

import java.lang.reflect.Method;

public class SBAMana
{
    public static final SBAMana INSTANCE = new SBAMana();

    public int getMana()
    {
        try
        {
            Class<?> skyblockAddons = Class.forName("codes.biscuit.skyblockaddons.SkyblockAddons");
            Class<?> attribute = Class.forName("codes.biscuit.skyblockaddons.core.Attribute");
            Object getInstance = skyblockAddons.getDeclaredMethod("getInstance").invoke(skyblockAddons);
            Object getUtils = getInstance.getClass().getDeclaredMethod("getUtils").invoke(getInstance);
            Object getAttributes = getUtils.getClass().getDeclaredMethod("getAttributes").invoke(getUtils);
            Method attributeValues = attribute.getDeclaredMethod("values");
            Object mana = null;

            for (Object obj : (Object[])attributeValues.invoke(null))
            {
                try
                {
                    if (obj.toString().equals("MANA"))
                    {
                        mana = obj;
                        break;
                    }
                }
                catch (Exception e) {}
            }

            Object getManaAttribute = getAttributes.getClass().getDeclaredMethod("get", Object.class).invoke(getAttributes, mana);
            return (int)getManaAttribute.getClass().getDeclaredMethod("getValue").invoke(getManaAttribute);
        }
        catch (Exception e)
        {
            return 0;
        }
    }
}