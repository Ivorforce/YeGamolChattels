/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvBlockMultiblock;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.tabs.YGCCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockGong extends IvBlockMultiblock
{
    public IIcon[] icons = new IIcon[3];

    public BlockGong(Material material)
    {
        super(material);

        setCreativeTab(YGCCreativeTabs.tabMain);
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
    public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        super.onBlockClicked(par1World, par2, par3, par4, par5EntityPlayer);

        TileEntity tileEntity = par1World.getTileEntity(par2, par3, par4);

        if (tileEntity instanceof TileEntityGong)
            ((TileEntityGong) tileEntity).hitGong(par5EntityPlayer.getHeldItem(), par5EntityPlayer);
    }

    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        TileEntity tileEntity = par1World.getTileEntity(par2, par3, par4);

        if (tileEntity instanceof TileEntityGong)
            ((TileEntityGong) tileEntity).hitGong(par5EntityPlayer.getHeldItem(), par5EntityPlayer);

        return super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
    }

    @Override
    public void parentBlockHarvestItem(World par1World, IvTileEntityMultiBlock tileEntity, int parentX, int parentY, int parentZ, Block block, int blockMeta)
    {
        super.parentBlockHarvestItem(par1World, tileEntity, parentX, parentY, parentZ, block, blockMeta);

        super.dropBlockAsItem(par1World, parentX, parentY, parentZ, new ItemStack(this, 1, blockMeta));
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int i)
    {
        return new TileEntityGong();
    }

    @Override
    public IIcon getIcon(int par1, int par2)
    {
        if (icons.length > par2)
            return icons[par2];

        return icons[0];
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        icons[0] = par1IconRegister.registerIcon(YeGamolChattels.textureBase + "gongSmall");
        icons[1] = par1IconRegister.registerIcon(YeGamolChattels.textureBase + "gongMedium");
        icons[2] = par1IconRegister.registerIcon(YeGamolChattels.textureBase + "gongLarge");
    }
}
