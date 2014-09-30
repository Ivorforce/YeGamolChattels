package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvBlockMultiblock;
import ivorius.ivtoolkit.blocks.IvMultiBlockHelper;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.ivtoolkit.network.IvNetworkHelperServer;
import ivorius.yegamolchattels.YGCMultiBlockHelper;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.items.ItemSaw;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by lukas on 04.05.14.
 */
public class BlockPlankSaw extends IvBlockMultiblock
{
    public BlockPlankSaw()
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        IvTileEntityMultiBlock tileEntity = getValidatedTotalParent(this, world, x, y, z);

        if (tileEntity instanceof TileEntityPlankSaw)
        {
            TileEntityPlankSaw plankSaw = (TileEntityPlankSaw) tileEntity;

            if (plankSaw.tryStoringItem(player.getHeldItem(), player))
                return true;
            else if (player.isSneaking() && plankSaw.tryEquippingItemOnPlayer(player))
                return true;
            else if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemSaw)
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

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
        return new TileEntityPlankSaw();
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
    {
        IvTileEntityMultiBlock tileEntity = getValidatedTotalParent(this, blockAccess, x, y, z);

        if (tileEntity instanceof TileEntityPlankSaw)
            setBlockBounds(YGCMultiBlockHelper.boundsIntersection(tileEntity.getRotatedBB(-1, -1, -0.45, 2, 1.8, 1.45), x, y, z));
        else
            setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    public void setBlockBounds(AxisAlignedBB bb)
    {
        setBlockBounds((float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        return Blocks.planks.getIcon(side, meta);
    }
}
