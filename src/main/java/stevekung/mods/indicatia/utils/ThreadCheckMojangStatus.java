package stevekung.mods.indicatia.utils;

import net.minecraft.client.Minecraft;

public class ThreadCheckMojangStatus extends Thread
{
    public ThreadCheckMojangStatus()
    {
        super("Mojang Status Check Thread");
    }

    @Override
    public void run()
    {
        for (MojangStatusChecker checker : MojangStatusChecker.values)
        {
            MojangServerStatus status = checker.getServiceStatus();
            Minecraft.getMinecraft().thePlayer.addChatMessage(JsonUtils.create(checker.getName() + ": ").appendSibling(JsonUtils.create(status.getColor() + status.getStatus())));
        }
    }
}