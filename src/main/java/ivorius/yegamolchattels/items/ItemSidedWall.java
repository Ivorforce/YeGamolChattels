/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemSidedWall extends ItemBlock
{
    public ItemSidedWall(Block block)
    {
        super(block);
        maxStackSize = 16;
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        Block var11 = par3World.getBlock(par4, par5, par6);

        if (var11 == Blocks.snow && (par3World.getBlockMetadata(par4, par5, par6) & 7) < 1)
        {
            par7 = 1;
        }
        else if (var11 != Blocks.vine && var11 != Blocks.tallgrass && var11 != Blocks.deadbush)
        {
            if (par7 == 0)
                --par5;

            if (par7 == 1)
                ++par5;

            if (par7 == 2)
                --par6;

            if (par7 == 3)
                ++par6;

            if (par7 == 4)
                --par4;

            if (par7 == 5)
                ++par4;
        }

        Block block = field_150939_a;

        if (!block.canPlaceBlockAt(par3World, par4, par5, par6))
        {
            return false;
        }

        int i1 = MathHelper.floor_double((par2EntityPlayer.rotationYaw * 4F) / 360F + 0.5D) & 3;

        if (i1 == 0)
            i1 = 0;
        else if (i1 == 1)
            i1 = 3;
        else if (i1 == 2)
            i1 = 1;
        else if (i1 == 3)
            i1 = 2;

        int type = par1ItemStack.getItemDamage();

        par3World.setBlock(par4, par5, par6, block, type | (i1 << 2), 3);

        par1ItemStack.stackSize--;

        return true;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return super.getUnlocalizedName(par1ItemStack) + ".meta" + par1ItemStack.getItemDamage();
    }
}
