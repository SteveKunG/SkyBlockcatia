package com.stevekung.skyblockcatia.utils;

import net.minecraft.util.IChatComponent;

public interface IModifiedSign
{
    public IChatComponent getText(int line);
    public void setText(int line, IChatComponent component);
    public void setSelectionState(int currentRow, int selectionStart, int selectionEnd, boolean caretVisible);
    public void resetSelectionState();
    public boolean getCaretVisible();
    public int getSelectionStart();
    public int getSelectionEnd();
}