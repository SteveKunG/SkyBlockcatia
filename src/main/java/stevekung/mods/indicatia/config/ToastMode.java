package stevekung.mods.indicatia.config;

public enum ToastMode
{
    CHAT, TOAST, CHAT_AND_TOAST, DISABLED;

    private static final ToastMode[] values = values();

    public static String getById(int mode)
    {
        return values[mode].toString().toLowerCase();
    }
}