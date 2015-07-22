/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

public class BlockGrandfatherClock extends Block
{
    public BlockGrandfatherClock(Material material)
    {
        super(material);
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
    public boolean hasTileEntity(int metadata)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World var1, int i)
    {
        return new TileEntityGrandfatherClock();
    }

    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5)
    {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);

        dropClockIfCantStay(par1World, par2, par3, par4);
    }

    public void dropClockIfCantStay(World world, BlockPos pos)
    {
        int type = world.getBlockMetadata(pos) & 1;

        if (type == 0 && world.getBlock(x, y + 1, z) != this)
        {
            dropBlockAsItem(world, pos, world.getBlockMetadata(pos), 0);
            world.setBlock(pos, Blocks.air, 0, 3);
        }
        if (type == 1 && world.getBlock(x, y - 1, z) != this)
        {
            world.setBlock(pos, Blocks.air, 0, 3);
        }
    }
}
