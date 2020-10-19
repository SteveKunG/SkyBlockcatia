package com.stevekung.skyblockcatia.utils.skyblock.api;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class BankHistory
{
    private final float amount;
    private final long timestamp;
    private final Action action;
    @SerializedName("initiator_name")
    private final String name;

    public BankHistory(float amount, long timestamp, Action action, String name)
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
        private final ITextComponent stats;

        public Stats(ITextComponent stats)
        {
            this.stats = stats;
        }

        public ITextComponent getStats()
        {
            return this.stats;
        }
    }

    public enum Action
    {
        WITHDRAW("Withdraw", TextFormatting.RED),
        DEPOSIT("Deposit", TextFormatting.GREEN);

        public final String name;
        public final TextFormatting color;

        private Action(String name, TextFormatting color)
        {
            this.name = name;
            this.color = color;
        }
    }
}
