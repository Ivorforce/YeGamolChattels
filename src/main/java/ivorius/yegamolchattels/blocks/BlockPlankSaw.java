package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvBlockMultiblock;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.gui.YGCGuiHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
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
            else
            {
                if (world.isRemote)
                    player.openGui(YeGamolChattels.instance, YGCGuiHandler.plankSawGuiID, world, plankSaw.xCoord, plankSaw.yCoord, plankSaw.zCoord);

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
}
