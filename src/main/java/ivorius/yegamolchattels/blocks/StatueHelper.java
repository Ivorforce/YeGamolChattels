package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvMultiBlockHelper;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.items.ItemStatue;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 28.09.14.
 */
public class StatueHelper
{
    public static boolean canCarveStatue(Entity statueEntity, World world, BlockPos pos)
    {
        if (isValidStatueBlock(world, pos))
        {
            Statue.BlockFragment blockFragment = new Statue.BlockFragment(world.getBlock(pos), world.getBlockMetadata(pos));

            List<int[]> positions = ItemStatue.getStatuePositions(statueEntity, 0);
            List<int[]> validPositions = getValidPositions(positions, world, blockFragment, pos);

            if (validPositions != null)
                return true;
        }

        return false;
    }

    public static TileEntityStatue carveStatue(ItemStack stack, Statue statue, World world, BlockPos pos, EntityLivingBase entityLivingBase)
    {
        if (isValidStatueBlock(world, pos))
        {
            Statue.BlockFragment blockFragment = new Statue.BlockFragment(world.getBlock(pos), world.getBlockMetadata(pos));
            int rotation = 0;

            List<int[]> positions = ItemStatue.getStatuePositions(statue.getEntity(), rotation);
            List<int[]> validPositions = getValidPositions(positions, world, blockFragment, pos);

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

    public static boolean isValidStatueBlock(World world, BlockPos pos)
    {
        Statue.BlockFragment blockFragment = new Statue.BlockFragment(world.getBlock(pos), world.getBlockMetadata(pos));
        return blockFragment.getBlock().getBlockHardness(world, pos) >= 0.0f && isValidStatueBlock(blockFragment);
    }

    private static boolean isValidStatueBlock(Statue.BlockFragment fragment)
    {
        Block block = fragment.getBlock();
        return !(block.hasTileEntity(fragment.getMetadata())) && (block.isOpaqueCube() || block == Blocks.glass || block == Blocks.stained_glass);
    }

    public static List<int[]> getValidPositions(List<int[]> positions, World world, Statue.BlockFragment blockFragment, BlockPos pos)
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
