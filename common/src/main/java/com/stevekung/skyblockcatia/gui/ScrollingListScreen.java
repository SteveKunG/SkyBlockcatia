package com.stevekung.skyblockcatia.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.Level;

public abstract class ScrollingListScreen implements GuiEventListener
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
    protected final Level world;
    protected final Font font;
    protected final int border = 4;
    private final int scrollBarLeft;
    private final int scrollBarRight;
    private final int viewHeight;

    public ScrollingListScreen(int width, int height, int top, int bottom, int left, int slotHeight)
    {
        this.mc = Minecraft.getInstance();
        this.width = width;
        this.height = height;
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = width + this.left;
        this.slotHeight = slotHeight;
        this.world = this.mc.level;
        this.font = this.mc.font;
        this.scrollBarRight = this.left + this.width;
        int barWidth = 6;
        this.scrollBarLeft = this.scrollBarRight - barWidth;
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
        return this.scrolling;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        var ret = this.scrolling;
        this.scrolling = false;
        return ret;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (this.scrolling)
        {
            var maxScroll = this.height - this.getBarHeight();
            var moved = deltaY / maxScroll;
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

    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        var tess = Tesselator.getInstance();
        var buffer = tess.getBuilder();
        var scale = this.mc.getWindow().getGuiScale();
        RenderSystem.enableScissor((int) (this.left * scale), (int) (this.mc.getWindow().getScreenHeight() - this.bottom * scale), (int) (this.width * scale), (int) (this.viewHeight * scale));

        if (this.mc.level != null)
        {
            drawGradientRect(poseStack.last().pose(), 0, this.left, this.top, this.right, this.bottom, 0xC0101010, 0xD0101010);
        }
        else
        {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
            var texScale = 32.0F;
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            buffer.vertex(this.left, this.bottom, 0.0D).uv(this.left / texScale, (this.bottom + (int) this.scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            buffer.vertex(this.right, this.bottom, 0.0D).uv(this.right / texScale, (this.bottom + (int) this.scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            buffer.vertex(this.right, this.top, 0.0D).uv(this.right / texScale, (this.top + (int) this.scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            buffer.vertex(this.left, this.top, 0.0D).uv(this.left / texScale, (this.top + (int) this.scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            tess.end();
        }

        var baseY = this.top + this.border - (int) this.scrollDistance;

        for (var index = 0; index < this.getSize(); ++index)
        {
            var top = baseY + index * this.slotHeight + this.headerHeight;
            var slotBuffer = this.slotHeight - this.border;

            if (top <= this.bottom && top + slotBuffer >= this.top)
            {
                this.drawPanel(poseStack, index, this.left, this.right, top);
            }
        }

        RenderSystem.disableDepthTest();
        var extraHeight = this.getContentHeight() + this.border - this.viewHeight;

        if (extraHeight > 0)
        {
            var height = this.viewHeight * this.viewHeight / this.getContentHeight();

            if (height < 32)
            {
                height = 32;
            }
            if (height > this.viewHeight - this.border * 2)
            {
                height = this.viewHeight - this.border * 2;
            }

            var barTop = (int) this.scrollDistance * (this.viewHeight - height) / extraHeight + this.top;

            if (barTop < this.top)
            {
                barTop = this.top;
            }

            RenderSystem.disableTexture();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            buffer.vertex(this.scrollBarLeft, this.bottom, 0.0D).uv(0.0F, 1.0F).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            buffer.vertex(this.scrollBarRight, this.bottom, 0.0D).uv(1.0F, 1.0F).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            buffer.vertex(this.scrollBarRight, this.top, 0.0D).uv(1.0F, 0.0F).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            buffer.vertex(this.scrollBarLeft, this.top, 0.0D).uv(0.0F, 0.0F).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            tess.end();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            buffer.vertex(this.scrollBarLeft, barTop + height, 0.0D).uv(0.0F, 1.0F).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            buffer.vertex(this.scrollBarRight, barTop + height, 0.0D).uv(1.0F, 1.0F).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            buffer.vertex(this.scrollBarRight, barTop, 0.0D).uv(1.0F, 0.0F).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            buffer.vertex(this.scrollBarLeft, barTop, 0.0D).uv(0.0F, 0.0F).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            tess.end();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            buffer.vertex(this.scrollBarLeft, barTop + height - 1, 0.0D).uv(0.0F, 1.0F).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            buffer.vertex(this.scrollBarRight - 1, barTop + height - 1, 0.0D).uv(1.0F, 1.0F).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            buffer.vertex(this.scrollBarRight - 1, barTop, 0.0D).uv(1.0F, 0.0F).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            buffer.vertex(this.scrollBarLeft, barTop, 0.0D).uv(0.0F, 0.0F).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            tess.end();
        }
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.disableScissor();
    }

    protected int getSize()
    {
        return 1;
    }

    protected abstract void drawPanel(PoseStack poseStack, int index, int left, int right, int top);

    private int getContentHeight()
    {
        return this.getSize() * this.slotHeight + this.headerHeight;
    }

    private void applyScrollLimits()
    {
        var listHeight = this.getContentHeight() - (this.viewHeight - this.border);

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
        var barHeight = this.height * this.height / this.getContentHeight();

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

    private static void drawGradientRect(Matrix4f mat, int zLevel, int left, int top, int right, int bottom, int startColor, int endColor)
    {
        var startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        var startRed = (float) (startColor >> 16 & 255) / 255.0F;
        var startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        var startBlue = (float) (startColor & 255) / 255.0F;
        var endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        var endRed = (float) (endColor >> 16 & 255) / 255.0F;
        var endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        var endBlue = (float) (endColor & 255) / 255.0F;
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        var tessellator = Tesselator.getInstance();
        var buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, (float) right, (float) top, (float) zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat, (float) left, (float) top, (float) zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat, (float) left, (float) bottom, (float) zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        buffer.vertex(mat, (float) right, (float) bottom, (float) zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        tessellator.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
}