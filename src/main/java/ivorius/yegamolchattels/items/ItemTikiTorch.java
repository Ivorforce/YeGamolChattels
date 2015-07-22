/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.items;

import ivorius.yegamolchattels.blocks.YGCBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import ivorius.ivtoolkit.rendering.grid.Icon;
import net.minecraft.world.World;

public class ItemTikiTorch extends ItemBlock
{
    public ItemTikiTorch(Block block)
    {
        super(block);
        maxStackSize = 16;
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, int blockSide, float par8, float par9, float par10)
    {
        Block prevBlock = par3World.getBlock(pos);
        int prevMeta = par3World.getBlockMetadata(pos);

        if (prevBlock == Blocks.snow_layer && prevMeta < 1)
        {
            blockSide = 1;
        }
        else if (prevBlock != Blocks.vine && prevBlock != Blocks.tallgrass && prevBlock != Blocks.deadbush && !prevBlock.isReplaceable(par3World, pos))
        {
            if (blockSide == 0)
                --y;
            if (blockSide == 1)
                ++y;
            if (blockSide == 2)
                --z;
            if (blockSide == 3)
                ++z;
            if (blockSide == 4)
                --x;
            if (blockSide == 5)
                ++x;
        }

        Block block = YGCBlocks.tikiTorch;

        if (!block.canPlaceBlockAt(par3World, pos))
        {
            return false;
        }

        par3World.setBlock(pos, block, 1, 3);
        par3World.setBlock(x, y + 1, z, block, 0, 3);

        par3World.notifyBlocksOfNeighborChange(pos, block);
        par3World.notifyBlocksOfNeighborChange(x, y + 1, z, block);
        par1ItemStack.stackSize--;
        return true;
    }

    @Override
    public Icon getIconFromDamage(int par1)
    {
        return itemIcon;
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        itemIcon = par1IconRegister.registerIcon(field_150939_a.getItemIconName());
    }
}
