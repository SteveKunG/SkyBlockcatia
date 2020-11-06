package club.sk1er.patcher.hooks;

// Compile only
public class FontRendererHook
{
    public boolean renderStringAtPos(String text, boolean shadow)
    {
        return false;
    }
}