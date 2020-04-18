package stevekung.mods.indicatia.config;

public class EnumEquipment
{
    public static enum Ordering
    {
        DEFAULT, REVERSE;

        private static final Ordering[] values = values();

        public static String getById(int mode)
        {
            return values[mode].toString().toLowerCase();
        }
    }

    public static enum Direction
    {
        VERTICAL, HORIZONTAL;

        private static final Direction[] values = values();

        public static String getById(int mode)
        {
            return values[mode].toString().toLowerCase();
        }
    }

    public static enum Status
    {
        DAMAGE_AND_MAX_DAMAGE, PERCENT, ONLY_DAMAGE, NONE, COUNT, COUNT_AND_STACK;

        private static final Status[] values = values();

        public static String getById(int mode)
        {
            return values[mode].toString().toLowerCase();
        }
    }

    public static enum Position
    {
        LEFT, RIGHT, HOTBAR;

        private static final Position[] values = values();

        public static String getById(int mode)
        {
            return values[mode].toString().toLowerCase();
        }
    }
}