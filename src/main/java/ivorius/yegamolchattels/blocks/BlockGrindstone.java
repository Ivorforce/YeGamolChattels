/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockGrindstone extends Block
{
    public BlockGrindstone(Material material)
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
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        TileEntity tileEntity = par1World.getTileEntity(par2, par3, par4);

        if (tileEntity instanceof TileEntityGrindstone)
        {
            TileEntityGrindstone tileEntityGrindstone = (TileEntityGrindstone) tileEntity;

            if (par5EntityPlayer.getHeldItem() != null)
            {
                if (!tileEntityGrindstone.tryApplyingItem(par5EntityPlayer.getHeldItem(), par5EntityPlayer))
                    tileEntityGrindstone.tryRepairingItem(par5EntityPlayer.getHeldItem(), par5EntityPlayer);
            }
            else
                tileEntityGrindstone.increaseGrindstoneRotation();

            return true;
        }

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
        return new TileEntityGrindstone();
    }
}
