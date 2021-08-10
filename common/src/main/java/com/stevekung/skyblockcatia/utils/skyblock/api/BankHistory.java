package com.stevekung.skyblockcatia.utils.skyblock.api;

import com.google.gson.annotations.SerializedName;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public record BankHistory(float amount, long timestamp, com.stevekung.skyblockcatia.utils.skyblock.api.BankHistory.Action action, @SerializedName("initiator_name") String name)
{
    public record Stats(Component stats) {}

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
