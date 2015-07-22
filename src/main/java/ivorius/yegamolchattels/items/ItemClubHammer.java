/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import ivorius.yegamolchattels.blocks.TileEntityMicroBlock;
import ivorius.yegamolchattels.blocks.YGCBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

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

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        Block block = world.getBlock(pos);

        if (block == YGCBlocks.microBlock)
            block.rotateBlock(world, pos, EnumFacing.UP);

        return super.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
    }

    public void modifyDrops(World world, IBlockState state, ItemStack stack, BlockPos pos, List<ItemStack> drops)
    {
        if (isMicroblockable(state))
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
                ItemBlockFragment.setFragment(drop, new IBlockState(block, (byte) world.getBlockMetadata(pos)));
                droppedFragments -= stackDrop;
                drops.add(drop);
            }
        }
    }

    public static boolean isMicroblockable(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isOpaqueCube() && world.getTileEntity(pos) == null && state.getBlock().getBlockHardness(world, pos) >= 0.0f;
    }

    public static boolean isMicroblockable(IBlockState state)
    {
        return state.getBlock().isOpaqueCube() && !state.getBlock().hasTileEntity(state);
    }
}
