package com.stevekung.skyblockcatia.utils.skyblock.api;

import com.google.gson.annotations.SerializedName;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

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
        private final Component stats;

        public Stats(Component stats)
        {
            this.stats = stats;
        }

        public Component getStats()
        {
            return this.stats;
        }
    }

    public enum Action
    {
        WITHDRAW("Withdraw", ChatFormatting.RED),
        DEPOSIT("Deposit", ChatFormatting.GREEN);

        public final String name;
        public final ChatFormatting color;

        Action(String name, ChatFormatting color)
        {
            this.name = name;
            this.color = color;
        }
    }
}
