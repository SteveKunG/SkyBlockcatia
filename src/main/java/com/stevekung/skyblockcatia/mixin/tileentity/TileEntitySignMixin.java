package com.stevekung.skyblockcatia.mixin.tileentity;

import org.spongepowered.asm.mixin.Mixin;

import com.stevekung.skyblockcatia.utils.IModifiedSign;

import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.IChatComponent;

@Mixin(TileEntitySign.class)
public class TileEntitySignMixin implements IModifiedSign
{
    private int selectionStart = -1;
    private int selectionEnd = -1;
    private boolean caretVisible;

    @Override
    public IChatComponent getText(int line)
    {
        return ((TileEntitySign) (Object) this).signText[line];
    }

    @Override
    public void setText(int line, IChatComponent component)
    {
        ((TileEntitySign) (Object) this).signText[line] = component;
    }

    @Override
    public void setSelectionState(int currentRow, int selectionStart, int selectionEnd, boolean caretVisible)
    {
        ((TileEntitySign) (Object) this).lineBeingEdited = currentRow;
        this.selectionStart = selectionStart;
        this.selectionEnd = selectionEnd;
        this.caretVisible = caretVisible;
    }

    @Override
    public void resetSelectionState()
    {
        ((TileEntitySign) (Object) this).lineBeingEdited = -1;
        this.selectionStart = -1;
        this.selectionEnd = -1;
        this.caretVisible = false;
    }

    @Override
    public boolean getCaretVisible()
    {
        return this.caretVisible;
    }

    @Override
    public int getSelectionStart()
    {
        return this.selectionStart;
    }

    @Override
    public int getSelectionEnd()
    {
        return this.selectionEnd;
    }
}