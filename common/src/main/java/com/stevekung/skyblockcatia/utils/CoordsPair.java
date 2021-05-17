package com.stevekung.skyblockcatia.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.mutable.MutableInt;

// Credit to SkyblockAddons but I can't find source code reference
public class CoordsPair
{
    private final MutableInt x;
    private final MutableInt y;

    public CoordsPair(int x, int y)
    {
        this.x = new MutableInt(x);
        this.y = new MutableInt(y);
    }

    public int getX()
    {
        return this.x.getValue();
    }

    public int getY()
    {
        return this.y.getValue();
    }

    public void setY(int y)
    {
        this.y.setValue(y);
    }

    public void setX(int x)
    {
        this.x.setValue(x);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }
        if (obj.getClass() != this.getClass())
        {
            return false;
        }
        CoordsPair chunkCoords = (CoordsPair) obj;
        return new EqualsBuilder().append(this.getX(), chunkCoords.getX()).append(this.getY(), chunkCoords.getY()).isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(83, 11).append(this.getX()).append(this.getY()).toHashCode();
    }

    @Override
    public String toString()
    {
        return this.getX() + "|" + this.getY();
    }
}