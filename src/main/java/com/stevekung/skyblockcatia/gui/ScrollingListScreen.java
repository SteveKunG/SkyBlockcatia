package com.stevekung.skyblockcatia.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.gui.GuiUtils;

public abstract class ScrollingListScreen implements IGuiEventListener
{
    protected final Minecraft mc;
    private final int width;
    private final int height;
    private final int top;
    private final int bottom;
    private final int right;
    private final int left;
    private final int slotHeight;
    private float scrollDistance;
    protected int headerHeight;
    private boolean scrolling;
    protected final World world;
    protected final FontRenderer font;
    protected final int border = 4;
    private final int barWidth = 6;
    private final int scrollBarLeft;
    private final int scrollBarRight;
    private final int viewHeight;

    public ScrollingListScreen(Screen parent, int width, int height, int top, int bottom, int left, int slotHeight)
    {
        this.mc = parent.getMinecraft();
        this.width = width;
        this.height = height;
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = width + this.left;
        this.slotHeight = slotHeight;
        this.world = this.mc.world;
        this.font = this.mc.fontRenderer;
        this.scrollBarRight = this.left + this.width;
        this.scrollBarLeft = this.scrollBarRight - this.barWidth;
        this.viewHeight = this.bottom - this.top;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
    {
        if (scroll != 0)
        {
            this.scrollDistance += -scroll * 10;
            this.applyScrollLimits();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        this.scrolling = button == 0 && mouseX >= this.scrollBarLeft && mouseX < this.scrollBarRight;

        if (this.scrolling)
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        boolean ret = this.scrolling;
        this.scrolling = false;
        return ret;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (this.scrolling)
        {
            int maxScroll = this.height - this.getBarHeight();
            double moved = deltaY / maxScroll;
            this.scrollDistance += this.getContentHeight() * moved;
            this.applyScrollLimits();
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return mouseX >= this.left && mouseX <= this.left + this.width && mouseY >= this.top && mouseY <= this.bottom;
    }

    @SuppressWarnings("deprecation")
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder worldr = tess.getBuffer();
        double scale = this.mc.getMainWindow().getGuiScaleFactor();
        RenderSystem.enableScissor((int)(this.left * scale), (int)(this.mc.getMainWindow().getHeight() - this.bottom * scale), (int)(this.width * scale), (int)(this.viewHeight * scale));

        if (this.mc.world != null)
        {
            GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, this.left, this.top, this.right, this.bottom, 0xC0101010, 0xD0101010);
        }
        else
        {
            RenderSystem.disableLighting();
            RenderSystem.disableFog();
            this.mc.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            final float texScale = 32.0F;
            worldr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldr.pos(this.left,  this.bottom, 0.0D).tex(this.left  / texScale, (this.bottom + (int)this.scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            worldr.pos(this.right, this.bottom, 0.0D).tex(this.right / texScale, (this.bottom + (int)this.scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            worldr.pos(this.right, this.top,    0.0D).tex(this.right / texScale, (this.top    + (int)this.scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            worldr.pos(this.left,  this.top,    0.0D).tex(this.left  / texScale, (this.top    + (int)this.scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            tess.draw();
        }

        int baseY = this.top + this.border - (int)this.scrollDistance;

        for (int index = 0; index < this.getSize(); ++index)
        {
            int top = baseY + index * this.slotHeight + this.headerHeight;
            int slotBuffer = this.slotHeight - this.border;

            if (top <= this.bottom && top + slotBuffer >= this.top)
            {
                this.drawPanel(matrixStack, index, this.left, this.right, top);
            }
        }

        RenderSystem.disableDepthTest();
        int extraHeight = this.getContentHeight() + this.border - this.viewHeight;

        if (extraHeight > 0)
        {
            int height = this.viewHeight * this.viewHeight / this.getContentHeight();

            if (height < 32)
            {
                height = 32;
            }
            if (height > this.viewHeight - this.border*2)
            {
                height = this.viewHeight - this.border*2;
            }

            int barTop = (int)this.scrollDistance * (this.viewHeight - height) / extraHeight + this.top;

            if (barTop < this.top)
            {
                barTop = this.top;
            }

            RenderSystem.disableTexture();
            worldr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldr.pos(this.scrollBarLeft,  this.bottom, 0.0D).tex(0.0F, 1.0F).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            worldr.pos(this.scrollBarRight, this.bottom, 0.0D).tex(1.0F, 1.0F).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            worldr.pos(this.scrollBarRight, this.top,    0.0D).tex(1.0F, 0.0F).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            worldr.pos(this.scrollBarLeft,  this.top,    0.0D).tex(0.0F, 0.0F).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            tess.draw();
            worldr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldr.pos(this.scrollBarLeft,  barTop + height, 0.0D).tex(0.0F, 1.0F).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            worldr.pos(this.scrollBarRight, barTop + height, 0.0D).tex(1.0F, 1.0F).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            worldr.pos(this.scrollBarRight, barTop,          0.0D).tex(1.0F, 0.0F).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            worldr.pos(this.scrollBarLeft,  barTop,          0.0D).tex(0.0F, 0.0F).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            tess.draw();
            worldr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldr.pos(this.scrollBarLeft,      barTop + height - 1, 0.0D).tex(0.0F, 1.0F).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            worldr.pos(this.scrollBarRight - 1, barTop + height - 1, 0.0D).tex(1.0F, 1.0F).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            worldr.pos(this.scrollBarRight - 1, barTop,              0.0D).tex(1.0F, 0.0F).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            worldr.pos(this.scrollBarLeft,      barTop,              0.0D).tex(0.0F, 0.0F).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            tess.draw();
        }
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
        RenderSystem.disableScissor();
    }

    protected int getSize()
    {
        return 1;
    }

    protected abstract void drawPanel(MatrixStack matrixStack, int index, int left, int right, int top);

    private int getContentHeight()
    {
        return this.getSize() * this.slotHeight + this.headerHeight;
    }

    private void applyScrollLimits()
    {
        int listHeight = this.getContentHeight() - (this.viewHeight - this.border);

        if (listHeight < 0)
        {
            listHeight /= 2;
        }

        if (this.scrollDistance < 0.0F)
        {
            this.scrollDistance = 0.0F;
        }
        if (this.scrollDistance > listHeight)
        {
            this.scrollDistance = listHeight;
        }
    }

    private int getBarHeight()
    {
        int barHeight = this.height * this.height / this.getContentHeight();

        if (barHeight < 32)
        {
            barHeight = 32;
        }
        if (barHeight > this.height - this.border * 2)
        {
            barHeight = this.height - this.border * 2;
        }
        return barHeight;
    }
}