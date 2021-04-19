package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.skyblockcatia.utils.skyblock.api.ExpProgress;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;

public class SBSlayers
{
    public enum Type
    {
        ZOMBIE("Zombie", ExpProgress.ZOMBIE_SLAYER),
        SPIDER("Spider", ExpProgress.SPIDER_SLAYER),
        WOLF("Wolf", ExpProgress.WOLF_SLAYER);

        private final String name;
        private final ExpProgress[] progress;

        Type(String name, ExpProgress[] progress)
        {
            this.name = name;
            this.progress = progress;
        }

        public String getName()
        {
            return this.name;
        }

        public ExpProgress[] getProgress()
        {
            return this.progress;
        }
    }

    public enum Drops
    {
        TARANTULA_WEB(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Tarantula Web", Items.string),
        REVENANT_FLESH(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Revenant Flesh", Items.rotten_flesh),
        WOLF_TOOTH(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Wolf Tooth", Items.ghast_tear);

        private final String displayName;
        private final Item baseItem;

        Drops(String displayName, Item baseItem)
        {
            this.displayName = displayName;
            this.baseItem = baseItem;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public Item getBaseItem()
        {
            return this.baseItem;
        }
    }
}