/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.yegamolchattels.items.*;
import ivorius.yegamolchattels.materials.YGCMaterials;
import ivorius.yegamolchattels.utils.IvBlockCollections;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 11.07.14.
 */
public class BlockMicroBlock extends Block
{
    public BlockMicroBlock()
    {
        super(YGCMaterials.mixed);
    }

    public static void dropAllMicroblockFragments(TileEntityMicroBlock tileEntityMicroBlock, float dropChance)
    {
        World world = tileEntityMicroBlock.getWorld();
        BlockPos pos = tileEntityMicroBlock.getPos();
        IvBlockCollection collection = tileEntityMicroBlock.getBlockCollection();

        if (!world.isRemote)
        {
            for (BlockPos coord : collection.area())
            {
                IBlockState state = collection.getBlockState(coord);
                if (state.getBlock().getMaterial() != Material.air && world.rand.nextFloat() < dropChance)
                {
                    ItemStack drop = new ItemStack(YGCItems.blockFragment);
                    ItemBlockFragment.setFragment(drop, state);
                    EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                    world.spawnEntityInWorld(entityItem);
                }
            }
        }

        tileEntityMicroBlock.setShouldDropAsItem(false);
        world.setBlockToAir(pos);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, BlockPos pos)
    {
        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return ((TileEntityMicroBlock) world.getTileEntity(pos)).isSideOpaque(side);
    }

    @Override
    public boolean isFullCube()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            TileEntityMicroBlock microBlock = (TileEntityMicroBlock) worldIn.getTileEntity(pos);

            if (microBlock.shouldDropAsItem())
            {
                ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
                ItemMicroBlock.setMicroBlock(stack, microBlock.getBlockCollection());
                worldIn.spawnEntityInWorld(new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player)
    {
        TileEntityMicroBlock microBlock = (TileEntityMicroBlock) world.getTileEntity(pos);

        ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
        ItemMicroBlock.setMicroBlock(stack, microBlock.getBlockCollection());
        return stack;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        super.onBlockHarvested(worldIn, pos, state, player);

        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && heldItem.getItem() == YGCItems.clubHammer)
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityMicroBlock)
                dropAllMicroblockFragments((TileEntityMicroBlock) tileEntity, ItemClubHammer.FRAGMENT_DROP_CHANCE);
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        if (!worldIn.isRemote)
        {
            IvBlockCollection collection = ItemMicroBlock.containedMicroBlock(stack);

            if (collection != null)
                ((TileEntityMicroBlock) worldIn.getTileEntity(pos)).setBlockCollection(collection);
        }
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end)
    {
        ItemChisel.MicroBlockFragment hitFragment = ItemChisel.getHoveredFragment(world, pos, start, new Vec3(end.xCoord - start.xCoord, end.yCoord - start.yCoord, end.zCoord - start.zCoord));

        return hitFragment != null ? new MovingObjectPosition(pos, hitFragment.getInternalSide().ordinal(), hitFragment.getHitPoint()) : null;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity)
    {
        AxisAlignedBB thisBox = this.getCollisionBoundingBox(worldIn, pos, state);

        if (thisBox != null && mask.intersectsWith(thisBox))
        {
            TileEntityMicroBlock microBlock = (TileEntityMicroBlock) worldIn.getTileEntity(pos);
            IvBlockCollection collection = microBlock.getBlockCollection();

            double oneX = 1.0 / collection.width;
            double oneY = 1.0 / collection.height;
            double oneZ = 1.0 / collection.length;
            for (BlockPos coord : collection.area())
            {
                if (collection.getBlockState(coord).getBlock().getMaterial() != Material.air)
                {
                    AxisAlignedBB bb = AxisAlignedBB.fromBounds(coord.getX() * oneX + pos.getX(), coord.getY() * oneY + pos.getY(), coord.getZ() * oneZ + pos.getZ(),
                            (coord.getX() + 1) * oneX + pos.getX(), (coord.getY() + 1) * oneY + pos.getY(), (coord.getZ() + 1) * oneZ + pos.getZ());
                    if (bb.intersectsWith(mask))
                        list.add(bb);
                }
            }
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    @Override
    public boolean rotateBlock(World worldObj, BlockPos pos, EnumFacing axis)
    {
        TileEntityMicroBlock microBlock = (TileEntityMicroBlock) worldObj.getTileEntity(pos);

        microBlock.setBlockCollection(IvBlockCollections.transform(microBlock.getBlockCollection(), AxisAlignedTransform2D.transform(1, false)));
        microBlock.markCacheInvalid();

        return super.rotateBlock(worldObj, pos, axis);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World var1, IBlockState state)
    {
        return new TileEntityMicroBlock();
    }
}
