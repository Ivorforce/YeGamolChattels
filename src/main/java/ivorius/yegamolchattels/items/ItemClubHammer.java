/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.yegamolchattels.blocks.TileEntityMicroBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

/**
 * Created by lukas on 11.07.14.
 */
public class ItemClubHammer extends ItemTool
{
    public ItemClubHammer(float damage, ToolMaterial material, Set damageVSBlocks)
    {
        super(damage, material, damageVSBlocks);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass)
    {
        return -1;
    }

    @Override
    public float func_150893_a(ItemStack stack, Block block)
    {
        return 1.5f;
    }

    public void modifyDrops(World world, Block block, ItemStack stack, int x, int y, int z, List<ItemStack> drops)
    {
        if (isMicroblockable(block))
        {
            drops.clear();

            int droppedFragments = world.rand.nextInt(8 * 8 * 8);
            while (droppedFragments > 0)
            {
                int stackDrop = Math.min(droppedFragments, 64);
                ItemStack drop = new ItemStack(YGCItems.blockFragment, stackDrop);
                ItemBlockFragment.setFragment(drop, new ItemChisel.BlockData(block, (byte) world.getBlockMetadata(x, y, z)));
                droppedFragments -= stackDrop;
                drops.add(drop);
            }
        }
    }

    public static boolean isMicroblockable(Block block)
    {
        return block.isOpaqueCube() && !(block instanceof ITileEntityProvider);
    }
}
