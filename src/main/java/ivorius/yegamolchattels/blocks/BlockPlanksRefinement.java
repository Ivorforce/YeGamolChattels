package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvBlockMultiblock;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by lukas on 04.05.14.
 */
public class BlockPlanksRefinement extends IvBlockMultiblock
{
    public BlockPlanksRefinement()
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

        if (tileEntity instanceof TileEntityPlanksRefinement)
        {
            TileEntityPlanksRefinement planksRefinement = (TileEntityPlanksRefinement) tileEntity;

            if (planksRefinement.tryStoringItem(player.getHeldItem(), player))
                return true;
            else if (planksRefinement.tryUsingItem(player.getHeldItem(), player))
                return true;
            else if (planksRefinement.tryEquippingItemOnPlayer(player))
                return true;

            return false;
        }

        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
        return new TileEntityPlanksRefinement();
    }
}
