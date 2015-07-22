/**
 * ************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 * ************************************************************************************************
 */

package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvBlockMultiblock;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.materials.YGCMaterials;
import ivorius.yegamolchattels.tabs.YGCCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import ivorius.ivtoolkit.rendering.grid.Icon;
import net.minecraft.world.World;

public class BlockPedestal extends IvBlockMultiblock
{
    public Icon[] icons;

    public BlockPedestal()
    {
        super(YGCMaterials.mixed);

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
    public boolean isFullCube()
    {
        return false;
    }

    @Override
    public Icon getIcon(int par1, int par2)
    {
        return this.icons.length > par2 ? this.icons[par2] : Blocks.planks.getIcon(0, 0);
    }

    @Override
    public void registerBlockIcons(IconRegister iconRegister)
    {
        this.icons = new Icon[EnumPedestalEntry.getNumberOfEntries()];

        for (int i = 0; i < this.icons.length; i++)
            this.icons[i] = iconRegister.registerIcon(YeGamolChattels.textureBase + "pedestal" + i);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int i)
    {
        return new TileEntityPedestal();
    }

    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        TileEntity tileEntity = par1World.getTileEntity(par2, par3, par4);

        if (tileEntity instanceof TileEntityPedestal)
        {
            if (((TileEntityPedestal) tileEntity).tryStoringItem(player.getHeldItem()))
            {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
            else
            {
                ((TileEntityPedestal) tileEntity).startDroppingItem(player);
            }
        }

        return true;
    }

    @Override
    public void parentBlockDropItemContents(World par1World, IvTileEntityMultiBlock tileEntity, int parentX, int parentY, int parentZ, Block block, int blockMeta)
    {
        ((TileEntityPedestal) tileEntity).dropItem();
    }

    @Override
    public void parentBlockHarvestItem(World par1World, IvTileEntityMultiBlock tileEntity, int parentX, int parentY, int parentZ, Block block, int blockMeta)
    {
        this.dropBlockAsItem(par1World, parentX, parentY, parentZ, new ItemStack(this, 1, ((TileEntityPedestal) tileEntity).pedestalIdentifier));
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, BlockPos pos, int p_149736_5_)
    {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof TileEntityPedestal)
            return Container.calcRedstoneFromInventory((TileEntityPedestal) tileEntity);

        return super.getComparatorInputOverride(world, pos, p_149736_5_);
    }
}
