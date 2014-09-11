/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.materials;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.YGCBlockAccessor;

/**
 * Created by lukas on 11.09.14.
 */
public class YGCMaterials
{
    public static Material mixed;

    public static void init()
    {
        mixed = new Material(MapColor.stoneColor);
        YGCBlockAccessor.setImmovableMobility(mixed);
    }
}
