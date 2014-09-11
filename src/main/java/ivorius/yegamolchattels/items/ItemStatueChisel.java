/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.IvMultiBlockHelper;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.Statue;
import ivorius.yegamolchattels.blocks.TileEntityStatue;
import ivorius.yegamolchattels.blocks.YGCBlocks;
import ivorius.yegamolchattels.gui.YGCGuiHandler;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 27.07.14.
 */
public class ItemStatueChisel extends Item
{
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int blockSide, float par8, float par9, float par10)
    {
        if (player.inventory.hasItem(YGCItems.clubHammer))
        {
            Statue.BlockFragment blockFragment = new Statue.BlockFragment(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));

            if (isValidStatueBlock(blockFragment))
            {
                if (!world.isRemote) // Some entities start with random sizes
                {
                    player.openGui(YeGamolChattels.instance, YGCGuiHandler.statueCarvingGuiID, world, x, y, z);
                }

                return true;
            }
        }
        else
        {
            if (!world.isRemote)
                player.addChatComponentMessage(new ChatComponentTranslation("item.ygcChisel.noHammer"));
        }

        return false;
    }

    public static boolean canCarveStatue(Entity statueEntity, World world, int x, int y, int z)
    {
        Statue.BlockFragment blockFragment = new Statue.BlockFragment(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));

        if (isValidStatueBlock(blockFragment))
        {
            List<int[]> positions = ItemStatue.getStatuePositions(statueEntity, 0);
            List<int[]> validPositions = getValidPositions(positions, world, blockFragment, x, y, z);

            if (validPositions != null)
                return true;
        }

        return false;
    }

    public static TileEntityStatue carveStatue(ItemStack stack, Statue statue, World world, int x, int y, int z, EntityLivingBase entityLivingBase)
    {
        Statue.BlockFragment blockFragment = new Statue.BlockFragment(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
        int rotation = 0;

        if (isValidStatueBlock(blockFragment))
        {
            List<int[]> positions = ItemStatue.getStatuePositions(statue.getEntity(), rotation);
            List<int[]> validPositions = getValidPositions(positions, world, blockFragment, x, y, z);

            if (validPositions != null)
            {
                IvMultiBlockHelper multiBlockHelper = new IvMultiBlockHelper();
                if (multiBlockHelper.beginPlacing(validPositions, world, YGCBlocks.statue, 0, rotation))
                {
                    TileEntityStatue parent = null;

                    for (int[] position : multiBlockHelper)
                    {
                        IvTileEntityMultiBlock tileEntity = multiBlockHelper.placeBlock(position);

                        if (tileEntity instanceof TileEntityStatue && tileEntity.isParent())
                        {
                            parent = (TileEntityStatue) tileEntity;
                            TileEntityStatue tileEntityStatue = (TileEntityStatue) tileEntity;
                            statue.setMaterial(blockFragment);
                            tileEntityStatue.setStatue(statue);
                            tileEntityStatue.setStatueRotationYaw((entityLivingBase.rotationYaw + 180.0f) % 360.0f);
                        }
                    }

                    stack.damageItem(1, entityLivingBase);
                    return parent;
                }
            }
        }

        return null;
    }

    public static boolean isValidStatueBlock(Statue.BlockFragment fragment)
    {
        return !(fragment.getBlock() instanceof ITileEntityProvider) && fragment.getBlock().isOpaqueCube();
    }

    public static List<int[]> getValidPositions(List<int[]> positions, World world, Statue.BlockFragment blockFragment, int x, int y, int z)
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
