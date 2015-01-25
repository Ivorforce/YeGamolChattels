/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import ivorius.yegamolchattels.blocks.TileEntityMicroBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by lukas on 11.07.14.
 */
public class ItemClubHammer extends ItemTool
{
    public static final float FRAGMENT_DROP_CHANCE = 0.9f;

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
        if (isMicroblockable(world, x, y, z))
        {
            drops.clear();

            Random rand = world.rand;
            int maxMicroblocks = TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_X * TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Y * TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Z;
            int droppedFragments = 0;
            for (int i = 0; i < maxMicroblocks; i++)
            {
                if (rand.nextFloat() < FRAGMENT_DROP_CHANCE)
                    droppedFragments++;
            }

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

    public static boolean isMicroblockable(World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        return block.isOpaqueCube() && world.getTileEntity(x, y, z) == null && block.getBlockHardness(world, x, y, z) >= 0.0f;
    }
}
