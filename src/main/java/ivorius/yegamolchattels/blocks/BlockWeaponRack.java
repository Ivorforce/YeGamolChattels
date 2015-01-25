/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWeaponRack extends Block
{
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
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        blockIcon = iconRegister.registerIcon(YeGamolChattels.textureBase + getTextureName());
    }

    @Override
    public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        TileEntity tileEntity = par1World.getTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityWeaponRack)
        {
            TileEntityWeaponRack tileEntityWeaponRack = (TileEntityWeaponRack) tileEntity;

            ItemStack heldItem = player.getHeldItem();
            if (tileEntityWeaponRack.tryApplyingEffect(heldItem, player))
            {
                return true;
            }
            else if (heldItem != null && tileEntityWeaponRack.tryStoringItem(heldItem, player))
            {
                return true;
            }
            else
            {
                return tileEntityWeaponRack.pickUpItem(player);
            }
        }

        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        if (!world.isRemote)
        {
            TileEntity tileEntity = world.getTileEntity(x, y, z);

            if (tileEntity instanceof TileEntityWeaponRack)
                ((TileEntityWeaponRack) tileEntity).dropAllWeapons();
        }

        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1iBlockAccess, int x, int y, int z)
    {
        TileEntity tileEntity = par1iBlockAccess.getTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityWeaponRack)
        {
            TileEntityWeaponRack tileEntityRack = (TileEntityWeaponRack) tileEntity;
            updateRackBounds(tileEntityRack.getDirection(), tileEntityRack.getWeaponRackType());
        }
        else
            super.setBlockBoundsBasedOnState(par1iBlockAccess, x, y, z);
    }

    public void updateRackBounds(int direction, int type)
    {
        if (type == TileEntityWeaponRack.weaponRackTypeWall)
        {
            float width = 0.28F;

            if (direction == 0)
                this.setBlockBounds(0.0F, 0.0F, 1.0F - width, 1.0F, 1.0F, 1.0F);

            if (direction == 1)
                this.setBlockBounds(0.0F, 0.0F, 0.0F, width, 1.0F, 1.0F);

            if (direction == 2)
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, width);

            if (direction == 3)
                this.setBlockBounds(1.0F - width, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            float width = 0.8F;

            if (direction == 0 || direction == 2)
                this.setBlockBounds(0.0F, 0.0F, (1.0f - width) * 0.5f, 1.0F, 1.0F, (1.0f + width) * 0.5f);
            else
                this.setBlockBounds((1.0f - width) * 0.5f, 0.0F, 0.0f, (1.0f + width) * 0.5f, 1.0F, 1.0f);
        }
    }

    @Override
    public boolean hasTileEntity(int metadata)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World var1, int i)
    {
        return new TileEntityWeaponRack();
    }
}
