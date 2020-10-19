package com.stevekung.skyblockcatia.utils.skyblock;

import com.google.gson.annotations.SerializedName;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;

public class SBBankHistory
{
    private final float amount;
    private final long timestamp;
    private final Action action;
    @SerializedName("initiator_name")
    private final String name;

    public SBBankHistory(float amount, long timestamp, Action action, String name)
    {
        this.amount = amount;
        this.timestamp = timestamp;
        this.action = action;
        this.name = name;
    }

    public float getAmount()
    {
        return this.amount;
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }

    public Action getAction()
    {
        return this.action;
    }

    public String getName()
    {
        return this.name;
    }

    public static class Stats
    {
        private final String stats;

        public Stats(String stats)
        {
            this.stats = stats;
        }

        public String getStats()
        {
            return this.stats;
        }
    }

    public enum Action
    {
        WITHDRAW(TextComponentUtils.formatted("Withdraw", TextFormatting.RED)),
        DEPOSIT(TextComponentUtils.formatted("Deposit", TextFormatting.GREEN));

        public final IFormattableTextComponent component;

        private Action(IFormattableTextComponent component)
        {
            this.component = component;
        }
    }
}
