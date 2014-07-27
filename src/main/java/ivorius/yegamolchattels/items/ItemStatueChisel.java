/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.IvMultiBlockHelper;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.blocks.TileEntityStatue;
import ivorius.yegamolchattels.blocks.YGCBlocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
                Entity statueEntity = new EntitySpider(world);

                int rotation = IvMultiBlockHelper.getRotation(par2EntityPlayer);
                List<int[]> positions = ItemStatue.getStatuePositions(statueEntity, rotation);
                List<int[]> validPositions = getValidPositions(positions, world, blockFragment, x, y, z);

                if (validPositions != null)
                {
                    IvMultiBlockHelper multiBlockHelper = new IvMultiBlockHelper();
                    if (multiBlockHelper.beginPlacing(validPositions, world, YGCBlocks.statue, 0, rotation))
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
            }

            return true;
        }

        return false;
    }

    public static boolean isValidStatueBlock(TileEntityStatue.BlockFragment fragment)
    {
        return !(fragment.getBlock() instanceof ITileEntityProvider);
    }

    public static List<int[]> getValidPositions(List<int[]> positions, World world, TileEntityStatue.BlockFragment blockFragment, int x, int y, int z)
    {
        List<int[]> validLocations = new ArrayList<>();

        for (int[] origin : positions)
        {
            for (int[] position : positions)
            {
                int posX = position[0] + x - origin[0];
                int posY = position[1] + y - origin[1];
                int posZ = position[2] + z - origin[2];

                if (world.getBlock(posX, posY, posZ) != blockFragment.getBlock() || world.getBlockMetadata(posX, posY, posZ) != blockFragment.getMetadata())
                    break;

                validLocations.add(new int[]{posX, posY, posZ});
            }

            if (validLocations.size() == positions.size())
                return validLocations;

            validLocations.clear();
        }

        return null;
    }
}
