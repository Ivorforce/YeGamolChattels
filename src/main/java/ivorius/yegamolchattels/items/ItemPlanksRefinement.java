/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.IvMultiBlockHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemPlanksRefinement extends ItemBlock
{
    public ItemPlanksRefinement(Block block)
    {
        super(block);
        maxStackSize = 16;
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        int rotation = IvMultiBlockHelper.getRotation(par2EntityPlayer);
        List<int[]> positions = IvMultiBlockHelper.getRotatedPositions(rotation, 2, 1, 1);

        IvMultiBlockHelper multiBlockHelper = new IvMultiBlockHelper();
        if (multiBlockHelper.beginPlacing(positions, par3World, par4, par5, par6, par7, par1ItemStack, par2EntityPlayer, this.field_150939_a, 0, rotation))
        {
            for (int[] position : multiBlockHelper)
            {
                multiBlockHelper.placeBlock(position);
            }

            par1ItemStack.stackSize--;
        }

        return true;
    }
}
