/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.IvMultiBlockHelper;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.blocks.TileEntityStatue;
import ivorius.yegamolchattels.blocks.YGCBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 27.07.14.
 */
public class ItemStatueChisel extends Item
{
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World world, int x, int y, int z, int blockSide, float par8, float par9, float par10)
    {
        TileEntityStatue.BlockFragment blockFragment = new TileEntityStatue.BlockFragment(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));

        if (isValidStatueBlock(blockFragment))
        {
            if (!world.isRemote) // Some entities start with random sizes
            {
                Entity statueEntity = new EntityPig(world);

                int rotation = IvMultiBlockHelper.getRotation(par2EntityPlayer);
                List<int[]> positions = ItemStatue.getStatuePositions(statueEntity, rotation);

                IvMultiBlockHelper multiBlockHelper = new IvMultiBlockHelper();
                if (multiBlockHelper.beginPlacing(getValidPositions(positions, x, y, z), world, YGCBlocks.statue, 0, rotation))
                {
                    for (int[] position : multiBlockHelper)
                    {
                        IvTileEntityMultiBlock tileEntity = multiBlockHelper.placeBlock(position);

                        if (tileEntity instanceof TileEntityStatue && tileEntity.isParent())
                        {
                            TileEntityStatue statue = (TileEntityStatue) tileEntity;
                            statue.setStatueEntity(statueEntity, true);
                            statue.setStatueBlock(blockFragment);
                        }
                    }

                    par1ItemStack.stackSize--;
                }
            }

            return true;
        }

        return false;
    }

    public static boolean isValidStatueBlock(TileEntityStatue.BlockFragment fragment)
    {
        return !(fragment.getBlock() instanceof ITileEntityProvider);
    }

    public static List<int[]> getValidPositions(List<int[]> positions, int x, int y, int z)
    {
        List<int[]> validLocations = new ArrayList<>();

        for (int[] position : positions)
        {
            validLocations.add(new int[]{position[0] + x, position[1] + y, position[2] + z});
        }

        return validLocations;
    }
}
