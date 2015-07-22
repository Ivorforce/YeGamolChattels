/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWeaponRack extends Block
{
    public static final PropertyBool ON_WALL = PropertyBool.create("on_wall");

    public BlockWeaponRack(Material material)
    {
        super(material);
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean isFullCube()
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof TileEntityWeaponRack)
        {
            TileEntityWeaponRack tileEntityWeaponRack = (TileEntityWeaponRack) tileEntity;

            ItemStack heldItem = playerIn.getHeldItem();
            if (tileEntityWeaponRack.tryApplyingEffect(heldItem, playerIn))
            {
                return true;
            }
            else if (heldItem != null && tileEntityWeaponRack.tryStoringItem(heldItem, playerIn))
            {
                return true;
            }
            else
            {
                return tileEntityWeaponRack.pickUpItem(playerIn);
            }
        }

        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);

            if (tileEntity instanceof TileEntityWeaponRack)
                ((TileEntityWeaponRack) tileEntity).dropAllWeapons();
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1iBlockAccess, BlockPos pos)
    {
        TileEntity tileEntity = par1iBlockAccess.getTileEntity(pos);

        if (tileEntity instanceof TileEntityWeaponRack)
        {
            TileEntityWeaponRack tileEntityRack = (TileEntityWeaponRack) tileEntity;
            updateRackBounds(tileEntityRack.getFacing(), tileEntityRack.getWeaponRackType());
        }
        else
            super.setBlockBoundsBasedOnState(par1iBlockAccess, pos);
    }

    public void updateRackBounds(EnumFacing facing, int type)
    {
        if (type == TileEntityWeaponRack.weaponRackTypeWall)
        {
            float width = 0.28F;

            switch (facing)
            {
                case SOUTH:
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - width, 1.0F, 1.0F, 1.0F);
                    break;
                case WEST:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, width, 1.0F, 1.0F);
                    break;
                case NORTH:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, width);
                    break;
                case EAST:
                    this.setBlockBounds(1.0F - width, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
            }
        }
        else
        {
            float width = 0.8F;

            if (facing == EnumFacing.SOUTH || facing == EnumFacing.NORTH)
                this.setBlockBounds(0.0F, 0.0F, (1.0f - width) * 0.5f, 1.0F, 1.0F, (1.0f + width) * 0.5f);
            else
                this.setBlockBounds((1.0f - width) * 0.5f, 0.0F, 0.0f, (1.0f + width) * 0.5f, 1.0F, 1.0f);
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityWeaponRack();
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, BlockPos pos)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof TileEntityWeaponRack)
            return Container.calcRedstoneFromInventory((TileEntityWeaponRack) tileEntity);

        return super.getComparatorInputOverride(worldIn, pos);
    }
}
