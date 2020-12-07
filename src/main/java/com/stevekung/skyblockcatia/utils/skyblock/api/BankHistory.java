package com.stevekung.skyblockcatia.utils.skyblock.api;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.EnumChatFormatting;

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
        WITHDRAW(EnumChatFormatting.RED + "Withdraw"),
        DEPOSIT(EnumChatFormatting.GREEN + "Deposit");

        public final String name;

        private Action(String name)
        {
            this.name = name;
        }
    }
}
