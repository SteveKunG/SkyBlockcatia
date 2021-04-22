package com.stevekung.skyblockcatia.integration.sba;

public class SBAMana
{
    public static final SBAMana INSTANCE = new SBAMana();

    public int getMana()
    {
        return 0;
        //return SkyblockAddons.getInstance().getUtils().getAttributes().get(Attribute.MANA).getValue();TODO
    }
}