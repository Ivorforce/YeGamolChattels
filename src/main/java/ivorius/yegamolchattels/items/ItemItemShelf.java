/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.IvMultiBlockHelper;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.blocks.BlockItemShelf;
import ivorius.yegamolchattels.blocks.TileEntityItemShelf;
import ivorius.yegamolchattels.blocks.TileEntityItemShelfModel0;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import ivorius.ivtoolkit.rendering.grid.Icon;
import net.minecraft.world.World;

import java.util.List;

public class ItemItemShelf extends ItemBlock
{
    public Icon[] icons = new Icon[BlockItemShelf.shelfTypes];

    public ItemItemShelf(Block block)
    {
        super(block);
        maxStackSize = 1;
        setHasSubtypes(true);
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        int shelfType = par1ItemStack.getItemDamage();

        int i1 = IvMultiBlockHelper.getRotation(par2EntityPlayer);
        List<int[]> positions = IvMultiBlockHelper.getRotatedPositions(TileEntityItemShelfModel0.getPositionsForType(shelfType), i1);

        IvMultiBlockHelper multiBlockHelper = new IvMultiBlockHelper();
        if (multiBlockHelper.beginPlacing(positions, par3World, par4, par5, par6, par7, par1ItemStack, par2EntityPlayer, this.field_150939_a, shelfType, i1))
        {
            for (int[] position : multiBlockHelper)
            {
                IvTileEntityMultiBlock tileEntity = multiBlockHelper.placeBlock(position);

                if (tileEntity != null && tileEntity instanceof TileEntityItemShelf)
                {

                }
            }

            par1ItemStack.stackSize--;
        }

        return true;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        int type = par1ItemStack.getItemDamage();

        return super.getUnlocalizedName(par1ItemStack) + ".type" + type;
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int size = 0; size < BlockItemShelf.shelfTypes; size++)
        {
            par3List.add(new ItemStack(this, 1, size));
        }
    }

    @Override
    public Icon getIconFromDamage(int par1)
    {
        return field_150939_a.getIcon(0, par1);
    }
}
