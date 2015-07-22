/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.IvMultiBlockHelper;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.blocks.TileEntityGong;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import ivorius.ivtoolkit.rendering.grid.Icon;
import net.minecraft.world.World;

import java.util.List;

public class ItemGong extends ItemBlock
{
    public ItemGong(Block block)
    {
        super(block);
        maxStackSize = 1;
        setHasSubtypes(true);
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        int gongSize = par1ItemStack.getItemDamage();
        int gongType = 0;

        int rotation = IvMultiBlockHelper.getRotation(par2EntityPlayer);
        List<int[]> positions = IvMultiBlockHelper.getRotatedPositions(rotation, gongSize + 1, gongSize + 1, 1);

        IvMultiBlockHelper multiBlockHelper = new IvMultiBlockHelper();
        if (multiBlockHelper.beginPlacing(positions, par3World, par4, par5, par6, par7, par1ItemStack, par2EntityPlayer, this.field_150939_a, gongSize, rotation))
        {
            for (int[] position : multiBlockHelper)
            {
                IvTileEntityMultiBlock tileEntity = multiBlockHelper.placeBlock(position);

                if (tileEntity instanceof TileEntityGong)
                {
                    ((TileEntityGong) tileEntity).gongType = gongType;
                }
            }

            par1ItemStack.stackSize--;
        }

        return true;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        int size = par1ItemStack.getItemDamage();

        return super.getUnlocalizedName(par1ItemStack) + ".size" + size;
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int size = 0; size < 3; size++)
        {
            par3List.add(new ItemStack(this, 1, size));
        }
    }

    @Override
    public Icon getIconFromDamage(int par1)
    {
        return this.field_150939_a.getIcon(0, par1);
    }
}
