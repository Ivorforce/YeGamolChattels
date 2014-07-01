/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

public class BlockGrandfatherClock extends BlockContainer
{
    public BlockGrandfatherClock(Material par2Material)
    {
        super(par2Material);
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3)
    {
        if ((par1 & 1) != 0)
        {
            return Item.getItemFromBlock(Blocks.air);
        }

        return super.getItemDropped(par1, par2Random, par3);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int i)
    {
        return new TileEntityGrandfatherClock();
    }

    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5)
    {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);

        dropClockIfCantStay(par1World, par2, par3, par4);
    }

    public void dropClockIfCantStay(World world, int x, int y, int z)
    {
        int type = world.getBlockMetadata(x, y, z) & 1;

        if (type == 0 && world.getBlock(x, y + 1, z) != this)
        {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlock(x, y, z, Blocks.air, 0, 3);
        }
        if (type == 1 && world.getBlock(x, y - 1, z) != this)
        {
            world.setBlock(x, y, z, Blocks.air, 0, 3);
        }
    }
}
