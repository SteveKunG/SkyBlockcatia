package com.stevekung.skyblockcatia.mixin;

import java.util.Deque;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;

@Mixin(ToastComponent.class)
public interface InvokerToastComponent
{
    @Accessor("queued")
    Deque<Toast> getQueued();
}