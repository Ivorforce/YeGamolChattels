/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package net.minecraft.block.material;

import net.minecraft.block.material.Material;

/**
 * Created by lukas on 11.09.14.
 */
public class YGCBlockAccessor
{
    public static Material setImmovableMobility(Material material)
    {
        return material.setImmovableMobility();
    }
}
