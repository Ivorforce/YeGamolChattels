/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import ivorius.yegamolchattels.client.rendering.*;
import net.minecraft.client.model.ModelBase;

public enum EnumPedestalEntry
{
    woodPedestal(0, new int[]{1, 2, 1}, new ModelPedestalWood(), 0, null),
    stonePedestal(0, new int[]{1, 2, 1}, new ModelPedestalStoneBlock(), 0, null),
    ironPedestal(20, new int[]{1, 2, 1}, new ModelPedestalIron(), 0, null),
    goldPedestal(60, new int[]{1, 2, 1}, new ModelPedestalGold(), 0, new float[]{3.0f, 2.0f, 3.0f}),
    diamondPedestal(150, new int[]{1, 2, 1}, new ModelPedestalDiamond(), 1, new float[]{4.0f, 5.0f, 4.0f}),
//	netherPedestal( 300, new int[] { 1, 2, 1 }, new ModelPedestalDiamond(), 1, false ),
    ;

    public final int integrationTime;
    public final int[] size;
    public final int blendMode;

    public final ModelBase model;

    public final float[] visualExtent;

    private EnumPedestalEntry(int integrationTime, int[] size, ModelBase model, int blendMode, float[] visualExtent)
    {
        this.integrationTime = integrationTime;
        this.size = size;
        this.model = model;
        this.blendMode = blendMode;
        this.visualExtent = visualExtent;
    }

    public static EnumPedestalEntry getEntry(int identifier)
    {
        EnumPedestalEntry[] entries = EnumPedestalEntry.values();

        return (identifier < 0 || identifier > entries.length) ? null : entries[identifier];
    }

    public static int getNumberOfEntries()
    {
        return EnumPedestalEntry.values().length;
    }

    public int getIntIdentifier()
    {
        return ordinal();
    }
}
