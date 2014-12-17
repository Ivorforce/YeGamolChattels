/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.items;

import ivorius.yegamolchattels.blocks.YGCBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemGrandfatherClock extends ItemBlock
{
    public ItemGrandfatherClock(Block block)
    {
        super(block);
        maxStackSize = 16;
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int blockSide, float par8, float par9, float par10)
    {
        Block prevBlock = par3World.getBlock(x, y, z);

        if (prevBlock == Blocks.snow_layer)
        {
            blockSide = 1;
        }
        else if (prevBlock != Blocks.vine && prevBlock != Blocks.tallgrass && prevBlock != Blocks.deadbush && !prevBlock.isReplaceable(par3World, x, y, z))
        {
            if (blockSide == 0)
                --y;
            if (blockSide == 1)
                ++y;
            if (blockSide == 2)
                --z;
            if (blockSide == 3)
                ++z;
            if (blockSide == 4)
                --x;
            if (blockSide == 5)
                ++x;
        }

        if (!field_150939_a.canPlaceBlockAt(par3World, x, y, z))
        {
            return false;
        }

        int rotation = MathHelper.floor_double((par2EntityPlayer.rotationYaw * 4F) / 360F + 0.5D) & 3;
        int rotationMeta = rotation << 1;

        par3World.setBlock(x, y, z, field_150939_a, rotationMeta, 3);
        par3World.setBlock(x, y + 1, z, field_150939_a, 1 | rotationMeta, 3);

        par1ItemStack.stackSize--;

        return true;
    }
}
