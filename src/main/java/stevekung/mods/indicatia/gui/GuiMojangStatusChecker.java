package stevekung.mods.indicatia.gui;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import stevekung.mods.indicatia.utils.MojangServerStatus;
import stevekung.mods.indicatia.utils.MojangStatusChecker;

public class GuiMojangStatusChecker extends GuiScreen
{
    private static List<String> statusList = new CopyOnWriteArrayList<>();
    private final GuiScreen lastScreen;
    private GuiButton doneButton;
    private GuiButton checkButton;
    private GuiButton refreshButton;

    public GuiMojangStatusChecker(GuiScreen lastScreen)
    {
        this.lastScreen = lastScreen;
    }

    @Override
    public void initGui()
    {
        this.buttonList.add(this.doneButton = new GuiButton(200, this.width / 2 - 100, this.height / 4 + 168, I18n.format("gui.done")));
        this.buttonList.add(this.refreshButton = new GuiButton(201, this.width / 2 + 1, this.height / 4 + 145, 100, 20, "Refresh"));
        this.buttonList.add(this.checkButton = new GuiButton(202, this.width / 2 - 101, this.height / 4 + 145, 100, 20, "Check"));
        this.refreshButton.enabled = false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.doneButton.enabled && keyCode == 1)
        {
            this.mc.displayGuiScreen(null);
            GuiMojangStatusChecker.statusList.clear();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 200)
        {
            this.mc.displayGuiScreen(this.lastScreen);
            GuiMojangStatusChecker.statusList.clear();
        }
        if (button.id == 201)
        {
            GuiMojangStatusChecker.statusList.clear();
            this.checkButton.enabled = true;
            this.refreshButton.enabled = false;
        }
        if (button.id == 202)
        {
            Thread thread = new Thread(() ->
            {
                try
                {
                    for (MojangStatusChecker checker : MojangStatusChecker.values)
                    {
                        MojangServerStatus status = checker.getServiceStatus();
                        GuiMojangStatusChecker.statusList.add(checker.getName() + ": " + status.getColor() + status.getStatus());
                    }
                    this.refreshButton.enabled = true;
                    this.doneButton.enabled = true;
                }
                catch (Exception e) {}
            });

            if (thread.getState() == Thread.State.NEW)
            {
                thread.start();
                this.checkButton.enabled = false;
                this.refreshButton.enabled = false;
                this.doneButton.enabled = false;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Mojang Status Checker", this.width / 2, 15, 16777215);
        int height = 0;

        for (String statusList : GuiMojangStatusChecker.statusList)
        {
            this.drawString(this.fontRendererObj, statusList, this.width / 2 - 120, 35 + height, 16777215);
            height += 12;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}