/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;


import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.Random;

public class BlockTikiTorch extends Block
{
    public IIcon upperTexture;

    public BlockTikiTorch()
    {
        super(Material.circuits);
        setTickRandomly(true);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
        return null;
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
        return YGCBlocks.blockTikiTorchRenderType;
    }

    private boolean canPlaceTorchOn(World par1World, int par2, int par3, int par4)
    {
        if (par1World.isBlockNormalCubeDefault(par2, par3, par4, true))
        {
            return true;
        }

        Block i = par1World.getBlock(par2, par3, par4);

        if (i == Blocks.fence || i == Blocks.nether_brick_fence || i == Blocks.glass || (par1World.getBlock(par2, par3, par4) == YGCBlocks.tikiTorch && par1World.getBlockMetadata(par2, par3, par4) != 0))
        {
            return true;
        }

        if (i != null && (i instanceof BlockStairs))
        {
            int j = par1World.getBlockMetadata(par2, par3, par4);

            if ((4 & j) != 0)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
        if (j >= world.getHeight() - 2) // -1 because index, -1 because it needs 2 height
        {
            return false;
        }
        else
        {
            return canPlaceTorchOn(world, i, j - 1, k) && super.canPlaceBlockAt(world, i, j, k) && super.canPlaceBlockAt(world, i, j + 1, k);
        }
    }

    @Override
    public void updateTick(World world, int i, int j, int k, Random random)
    {
        super.updateTick(world, i, j, k, random);
        if (world.getBlockMetadata(i, j, k) == 0)
        {
            onBlockAdded(world, i, j, k);
        }
    }

    @Override
    public void onBlockAdded(World world, int i, int j, int k)
    {
        dropTorchIfCantStay(world, i, j, k);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        super.onNeighborBlockChange(world, x, y, z, block);

        int i1 = world.getBlockMetadata(x, y, z);

        if (dropTorchIfCantStay(world, x, y, z) && i1 == 0)
        {

        }
        else
        {
            if (world.getBlock(x, y + 1, z) != this)
            {
                world.setBlock(x, y, z, Blocks.air, 0, 3);
            }
        }
    }

    private boolean dropTorchIfCantStay(World world, int i, int j, int k)
    {
        if (!canPlaceTorchOn(world, i, j - 1, k))
        {
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
            world.setBlock(i, j, k, Blocks.air, 0, 3);
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3, Vec3 par6Vec3)
    {
        float f1 = 0.1F;
        int l = par1World.getBlockMetadata(par2, par3, par4);
        setBlockBounds(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, (l == 0 ? 0.6F : 1.0F), 0.5F + f1);

        return super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
    }

    @Override
    public void randomDisplayTick(World world, int i, int j, int k, Random random)
    {
        int l = world.getBlockMetadata(i, j, k);
        double d = i + 0.5F;
        double d1 = j + 0.7F;
        double d2 = k + 0.5F;

        if (l == 0)
        {
            world.spawnParticle("smoke", d, d1, d2, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("flame", d, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        blockIcon = par1IconRegister.registerIcon(YeGamolChattels.textureBase + this.getTextureName());
        upperTexture = par1IconRegister.registerIcon(YeGamolChattels.textureBase + this.getTextureName() + "Upper");
    }

    @Override
    public IIcon getIcon(int par1, int par2)
    {
        if (par2 == 0)
        {
            return upperTexture;
        }
        else
        {
            return super.getIcon(par1, par2);
        }
    }

    @Override
    public String getItemIconName()
    {
        return YeGamolChattels.textureBase + getTextureName();
    }

    @Override
    public int getMobilityFlag()
    {
        return 1;
    }
}
