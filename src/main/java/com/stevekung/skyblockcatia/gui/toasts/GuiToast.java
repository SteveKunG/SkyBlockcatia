package com.stevekung.skyblockcatia.gui.toasts;

import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.stevekung.skyblockcatia.config.ConfigManagerIN;
import com.stevekung.skyblockcatia.utils.ColorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.MathHelper;

public class GuiToast extends Gui
{
    protected final Minecraft mc;
    private final GuiToast.ToastInstance<?>[] visible = new GuiToast.ToastInstance[5];
    private final Deque<IToast> toastsQueue = new ArrayDeque<>();

    public GuiToast(Minecraft mc)
    {
        this.mc = mc;
    }

    public void drawToast(ScaledResolution resolution)
    {
        if (!this.mc.gameSettings.hideGUI && !this.mc.gameSettings.showDebugInfo && !(ConfigManagerIN.enableShortcutGameButton && Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatOpen()))
        {
            RenderHelper.disableStandardItemLighting();

            for (int i = 0; i < this.visible.length; ++i)
            {
                GuiToast.ToastInstance<?> toastinstance = this.visible[i];

                if (toastinstance != null && toastinstance.render(resolution.getScaledWidth(), i))
                {
                    this.visible[i] = null;
                }
                if (this.visible[i] == null && !this.toastsQueue.isEmpty())
                {
                    this.visible[i] = new GuiToast.ToastInstance(this.toastsQueue.removeFirst());
                }
            }
        }
    }

    public static void drawLongItemName(GuiToast toastGui, long delta, long firstDrawTime, FloatBuffer buffer, String itemName, long minDraw, long maxDraw, long timeUntilGone, long textSpeed, boolean shadow)
    {
        int x = 30;
        int textWidth = toastGui.mc.fontRendererObj.getStringWidth(itemName);
        int maxSize = textWidth - 135;
        long timeElapsed = delta - firstDrawTime - minDraw;
        long timeElapsed2 = maxDraw - delta - timeUntilGone;
        int maxTextLength = 125;

        if (textWidth > maxSize && textWidth > maxTextLength)
        {
            if (timeElapsed > 0)
            {
                x = Math.max((int) (-textWidth * timeElapsed / textSpeed + x), -maxSize + 16);
            }

            int backward = Math.max(Math.min((int) -(textWidth * timeElapsed2 / textSpeed), 30), -maxSize + 16);

            if (timeElapsed > timeElapsed2)
            {
                x = backward;
            }
        }

        ScaledResolution res = new ScaledResolution(toastGui.mc);
        double height = res.getScaledHeight();
        double scale = res.getScaleFactor();
        float[] trans = new float[16];

        buffer.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buffer);
        buffer.get(trans);
        float xpos = trans[12];

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) ((xpos + 29) * scale), (int) ((height - 196) * scale), (int) (126 * scale), (int) (195 * scale));

        if (shadow)
        {
            toastGui.mc.fontRendererObj.drawStringWithShadow(itemName, x, 18, ColorUtils.rgbToDecimal(255, 255, 255));
        }
        else
        {
            toastGui.mc.fontRendererObj.drawString(itemName, x, 18, ColorUtils.rgbToDecimal(255, 255, 255));
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Nullable
    public <T extends IToast> T getToast(Class<? extends T> clazz, Object obj)
    {
        for (GuiToast.ToastInstance<?> ins : this.visible)
        {
            if (ins != null && clazz.isAssignableFrom(ins.getToast().getClass()) && ins.getToast().getType().equals(obj))
            {
                if (ins.getToast().equal(ins.getToast()))
                {
                    return (T)ins.getToast();
                }
            }
        }
        for (IToast toast : this.toastsQueue)
        {
            if (clazz.isAssignableFrom(toast.getClass()) && toast.getType().equals(obj))
            {
                if (toast.equal(toast))
                {
                    return (T)toast;
                }
            }
        }
        return null;
    }

    public void clear()
    {
        Arrays.fill(this.visible, null);
        this.toastsQueue.clear();
    }

    public boolean add(IToast toast)
    {
        return this.toastsQueue.add(toast);
    }

    class ToastInstance<T extends IToast>
    {
        private final T toast;
        private long animationTime;
        private long visibleTime;
        private IToast.Visibility visibility;

        private ToastInstance(T toast)
        {
            this.animationTime = -1L;
            this.visibleTime = -1L;
            this.visibility = IToast.Visibility.SHOW;
            this.toast = toast;
        }

        public T getToast()
        {
            return this.toast;
        }

        private float getVisibility(long delta)
        {
            float f = MathHelper.clamp_float((delta - this.animationTime) / 600.0F, 0.0F, 1.0F);
            f = f * f;
            return this.visibility == IToast.Visibility.HIDE ? 1.0F - f : f;
        }

        public boolean render(int x, int z)
        {
            long i = Minecraft.getSystemTime();

            if (this.animationTime == -1L)
            {
                this.animationTime = i;
                this.visibility.playSound(GuiToast.this.mc.getSoundHandler());
            }
            if (this.visibility == IToast.Visibility.SHOW && i - this.animationTime <= 600L)
            {
                this.visibleTime = i;
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.translate(x - 160.0F * this.getVisibility(i), z * 32, 500 + z);
            IToast.Visibility itoast$visibility = this.toast.draw(GuiToast.this, i - this.visibleTime);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();

            if (itoast$visibility != this.visibility)
            {
                this.animationTime = i - (int)((1.0F - this.getVisibility(i)) * 600.0F);
                this.visibility = itoast$visibility;
                this.visibility.playSound(GuiToast.this.mc.getSoundHandler());
            }
            return this.visibility == IToast.Visibility.HIDE && i - this.animationTime > 600L;
        }
    }
}