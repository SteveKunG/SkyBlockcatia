package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.skyblockcatia.utils.skyblock.api.ExpProgress;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

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
        TARANTULA_WEB(TextComponentUtils.formatted("Tarantula Web", TextFormatting.GREEN), Items.STRING),
        REVENANT_FLESH(TextComponentUtils.formatted("Revenant Flesh", TextFormatting.GREEN), Items.ROTTEN_FLESH),
        WOLF_TOOTH(TextComponentUtils.formatted("Wolf Tooth", TextFormatting.GREEN), Items.GHAST_TEAR);

        private final ITextComponent displayName;
        private final IItemProvider baseItem;

        Drops(ITextComponent displayName, IItemProvider baseItem)
        {
            this.displayName = displayName;
            this.baseItem = baseItem;
        }

        public ITextComponent getDisplayName()
        {
            return this.displayName;
        }

        public IItemProvider getBaseItem()
        {
            return this.baseItem;
        }
    }
}