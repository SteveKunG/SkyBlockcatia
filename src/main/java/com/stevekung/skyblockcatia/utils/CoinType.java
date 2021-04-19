package com.stevekung.skyblockcatia.utils;

public enum CoinType
{
    TYPE_1("2070f6cb-f5db-367a-acd0-64d39a7e5d1b", "538071721cc5b4cd406ce431a13f86083a8973e1064d2f8897869930ee6e5237"),
    TYPE_2("8ce61ae1-7cb4-3bdd-b1be-448c6fabb355", "dfa087eb76e7687a81e4ef81a7e6772649990f6167ceb0f750a4c5deb6c4fbad"),
    TYPE_3("9dd5008a-08a1-3f4a-b8af-2499bdb8ff3b", "e36e94f6c34a35465fce4a90f2e25976389eb9709a12273574ff70fd4daa6852");

    private final String id;
    private final String value;

    CoinType(String id, String value)
    {
        this.id = id;
        this.value = value;
    }

    public String getId()
    {
        return this.id;
    }

    public String getValue()
    {
        return this.value;
    }
}