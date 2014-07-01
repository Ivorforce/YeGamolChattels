/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.Random;

public class BlockTreasurePile extends Block
{

    public BlockTreasurePile()
    {
        super(Material.sand);
    }

    @Override
    public void onBlockAdded(World world, int i, int j, int k)
    {
        world.scheduleBlockUpdate(i, j, k, this, tickRate());
    }

    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, Block l)
    {
        world.scheduleBlockUpdate(i, j, k, this, tickRate());
    }

    @Override
    public void updateTick(World world, int i, int j, int k, Random random)
    {
        tryToFall(world, i, j, k);
    }

    private void tryToFall(World world, int i, int j, int k)
    {
        int l = i;
        int i1 = j;
        int j1 = k;
        if (canFallBelow(world, l, i1 - 1, j1) && i1 >= 0)
        {
            byte byte0 = 32;
            if (fallInstantly || !world.checkChunksExist(i - byte0, j - byte0, k - byte0, i + byte0, j + byte0, k + byte0))
            {
                world.setBlock(i, j, k, Blocks.air, 0, 3);
                for (; canFallBelow(world, i, j - 1, k) && j > 0; j--)
                {
                }
                if (j > 0)
                {
                    world.setBlock(i, j, k, this, 0, 3);
                }
            }
            else
            {
                EntityFallingBlock entityfallingsand = new EntityFallingBlock(world, i + 0.5F, j + 0.5F, k + 0.5F, this, world.getBlockMetadata(i, j, k));
                world.spawnEntityInWorld(entityfallingsand);
            }
        }
    }

    public int tickRate()
    {
        return 3;
    }

    public static boolean canFallBelow(World world, int i, int j, int k)
    {
        Block l = world.getBlock(i, j, k);
        if (l == Blocks.air)
        {
            return true;
        }
        if (l == Blocks.fire)
        {
            return true;
        }

        Material material = l.getMaterial();
        if (material == Material.water)
        {
            return true;
        }
        return material == Material.lava;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return YGCBlocks.blockTreasurePileRenderType;
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Items.gold_ingot;
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 7;
    }

    @Override
    public String getItemIconName()
    {
        return YeGamolChattels.textureBase + "treasurePile";
    }

    public static boolean fallInstantly = false;

}
