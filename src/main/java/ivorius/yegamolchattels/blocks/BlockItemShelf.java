/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvBlockMultiblock;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockItemShelf extends IvBlockMultiblock
{
    public static final int shelfTypes = 3;

    public IIcon[] icons;

    public BlockItemShelf(Material par2Material)
    {
        super(par2Material);

        setCreativeTab(YeGamolChattels.tabMain);
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
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        TileEntity tileEntity = par1World.getTileEntity(par2, par3, par4);

        if (tileEntity instanceof TileEntityItemShelf)
        {
            return ((TileEntityItemShelf) tileEntity).onRightClick(par5EntityPlayer, par5EntityPlayer.getCurrentEquippedItem(), par6);
        }

        return false;
    }

    @Override
    public void parentBlockDropItemContents(World par1World, IvTileEntityMultiBlock tileEntity, int parentX, int parentY, int parentZ, Block block, int blockMeta)
    {
        ((TileEntityItemShelf) tileEntity).dropAllItems();
    }

    @Override
    public void parentBlockHarvestItem(World par1World, IvTileEntityMultiBlock tileEntity, int parentX, int parentY, int parentZ, Block block, int blockMeta)
    {
        super.dropBlockAsItem(par1World, parentX, parentY, parentZ, new ItemStack(this, 1, blockMeta));
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int i)
    {
        return new TileEntityItemShelfModel0(var1);
    }

    @Override
    public IIcon getIcon(int par1, int par2)
    {
        if (icons.length > par2)
        {
            return icons[par2];
        }

        return icons[0];
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.icons = new IIcon[shelfTypes];

        for (int i = 0; i < this.icons.length; i++)
        {
            this.icons[i] = par1IconRegister.registerIcon(YeGamolChattels.textureBase + "itemShelf" + i);
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1iBlockAccess, int par2, int par3, int par4)
    {
        TileEntity tileEntity = par1iBlockAccess.getTileEntity(par2, par3, par4);

        if (tileEntity instanceof TileEntityItemShelf)
        {
            AxisAlignedBB bb = ((TileEntityItemShelf) tileEntity).getSpecialBlockBB();

            if (bb != null)
            {
                setBlockBounds((float) bb.minX - par2, (float) bb.minY - par3, (float) bb.minZ - par4, (float) bb.maxX - par2, (float) bb.maxY - par3, (float) bb.maxZ - par4);
            }
            else
            {
                setBlockBounds(0, 0, 0, 1, 1, 1);
            }
        }
        else
        {
            setBlockBounds(0, 0, 0, 1, 1, 1);
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        TileEntity tileEntity = par1World.getTileEntity(par2, par3, par4);

        if (tileEntity instanceof TileEntityItemShelf)
        {
            AxisAlignedBB specialBB = ((TileEntityItemShelf) tileEntity).getSpecialSelectedBB();
            if (specialBB != null)
            {
                return specialBB;
            }
        }

        return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        TileEntity tileEntity = par1World.getTileEntity(par2, par3, par4);

        if (tileEntity instanceof TileEntityItemShelf)
        {
            AxisAlignedBB specialBB = ((TileEntityItemShelf) tileEntity).getSpecialCollisionBB();
            if (specialBB != null)
            {
                return specialBB;
            }
        }

        return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
    }
}
