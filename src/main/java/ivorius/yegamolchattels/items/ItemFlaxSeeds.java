/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;

/**
 * Created by lukas on 10.07.14.
 */
public class ItemFlaxSeeds extends ItemSeeds
{
    public ItemFlaxSeeds(Block plant, Block soil)
    {
        super(plant, soil);
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return EnumPlantType.Plains;
    }
}
