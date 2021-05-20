package com.stevekung.skyblockcatia.utils.skyblock.api;

public class GameStatus
{
    private final Session session;

    public GameStatus(Session session)
    {
        this.session = session;
    }

    public Session getSession()
    {
        return this.session;
    }

    public class Session
    {
        private final boolean online;
        private final String gameType;
        private final String mode;

        public Session(boolean online, String gameType, String mode)
        {
            this.online = online;
            this.gameType = gameType;
            this.mode = mode;
        }

        public boolean isOnline()
        {
            return this.online;
        }

        public String getGameType()
        {
            return this.gameType;
        }

        public String getMode()
        {
            return this.mode;
        }
    }
}