package stevekung.mods.indicatia.mixin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

@Mixin(value = GuiScreen.class, priority = 900)
public abstract class GuiScreenMixin extends Gui
{
    private static final List<String> IGNORE_TOOLTIPS = new ArrayList<>(Arrays.asList(" "));

    @Shadow
    private Minecraft mc;

    @Shadow
    protected abstract void keyTyped(char typedChar, int keyCode) throws IOException;

    @Inject(method = "renderToolTip(Lnet/minecraft/item/ItemStack;II)V", cancellable = true, at = @At("HEAD"))
    private void renderToolTip(ItemStack itemStack, int x, int y, CallbackInfo info)
    {
        if (this.ignoreNullItem(itemStack, IGNORE_TOOLTIPS))
        {
            info.cancel();
        }
    }

    @Overwrite
    public void handleKeyboardInput() throws IOException
    {
        char c0 = Keyboard.getEventCharacter();

        if (Keyboard.getEventKey() == 0 && c0 >= ' ' || Keyboard.getEventKeyState())
        {
            this.keyTyped(c0, Keyboard.getEventKey());
        }
        this.mc.dispatchKeypresses();
    }

    private boolean ignoreNullItem(ItemStack itemStack, List<String> ignores)
    {
        String displayName = EnumChatFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName());
        return ignores.stream().anyMatch(name -> displayName.equals(name));
    }
}