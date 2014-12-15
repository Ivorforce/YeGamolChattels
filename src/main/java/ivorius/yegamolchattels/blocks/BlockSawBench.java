package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvBlockMultiblock;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.ivtoolkit.network.IvNetworkHelperServer;
import ivorius.ivtoolkit.tools.IvAABBs;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.items.ItemSaw;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by lukas on 04.05.14.
 */
public class BlockSawBench extends IvBlockMultiblock
{
    public BlockSawBench()
    {
        super(Material.wood);
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public void parentBlockHarvestItem(World world, IvTileEntityMultiBlock tileEntity, int x, int y, int z, Block block, int metadata)
    {
        dropBlockAsItem(world, x, y, z, new ItemStack(this));
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        IvTileEntityMultiBlock tileEntity = getValidatedTotalParent(this, world, x, y, z);

        if (tileEntity instanceof TileEntitySawBench)
        {
            TileEntitySawBench plankSaw = (TileEntitySawBench) tileEntity;

            if (plankSaw.tryStoringItem(player.getHeldItem(), player))
                return true;
            else if (player.isSneaking() && plankSaw.tryEquippingItemOnPlayer(player))
                return true;
            else if (canUseItemToSaw(player.getHeldItem()))
            {
                if (!world.isRemote)
                {
                    IvNetworkHelperServer.sendTileEntityUpdatePacket(plankSaw, "sawOpenGui", YeGamolChattels.network, player);
                }

                return true;
            }
        }

        return false;
    }

    public static boolean canUseItemToSaw(ItemStack stack)
    {
        return stack != null && stack.getItem() instanceof ItemSaw;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
        return new TileEntitySawBench();
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
    {
        IvTileEntityMultiBlock tileEntity = getValidatedTotalParent(this, blockAccess, x, y, z);

        if (tileEntity instanceof TileEntitySawBench)
            setBlockBounds(IvAABBs.boundsIntersection(tileEntity.getRotatedBB(-1, -1, -0.45, 2, 1.8, 1.45), x, y, z));
        else
            setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    public void setBlockBounds(AxisAlignedBB bb)
    {
        setBlockBounds((float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }
}
