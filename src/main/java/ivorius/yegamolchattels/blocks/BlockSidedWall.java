/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class BlockSidedWall extends Block
{
    public String[] wallIconPaths;
    public IIcon[] wallIcons = new IIcon[4];

    public BlockSidedWall(Material par2Material, String[] wallIcons)
    {
        super(par2Material);

        this.wallIconPaths = wallIcons;
    }

    @Override
    public IIcon getIcon(int par1, int par2)
    {
        if ((par1 - 2) == (par2 >> 2))
            return super.getIcon(par1, par2);

        return wallIcons[par2 & 3];
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        super.registerBlockIcons(par1IconRegister);

        for (int i = 0; i < wallIconPaths.length; i++)
            wallIcons[i] = par1IconRegister.registerIcon(YeGamolChattels.textureBase + wallIconPaths[i]);
    }

    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < wallIconPaths.length; i++)
            par3List.add(new ItemStack(this, 1, i));
    }
}
