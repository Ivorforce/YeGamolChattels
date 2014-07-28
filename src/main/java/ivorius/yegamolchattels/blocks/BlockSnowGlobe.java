/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSnowGlobe extends BlockContainer
{
    public BlockSnowGlobe()
    {
        super(Material.rock);
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public int getRenderBlockPass()
    {
        return 1;
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
    public TileEntity createNewTileEntity(World var1, int i)
    {
        return new TileEntitySnowGlobe();
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(YeGamolChattels.textureBase + "realityGlobe");
    }

    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5Player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        TileEntity tileEntity = par1World.getTileEntity(par2, par3, par4);

        if (tileEntity instanceof TileEntitySnowGlobe)
        {
            TileEntitySnowGlobe tileEntitySnowGlobe = (TileEntitySnowGlobe) tileEntity;
            return tileEntitySnowGlobe.useItem(par5Player.getHeldItem());
        }

        return super.onBlockActivated(par1World, par2, par3, par4, par5Player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
    }
}
