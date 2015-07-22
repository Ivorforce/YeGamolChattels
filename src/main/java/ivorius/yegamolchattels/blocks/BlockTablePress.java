package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvBlockMultiblock;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by lukas on 04.05.14.
 */
public class BlockTablePress extends IvBlockMultiblock
{
    public BlockTablePress()
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
    public boolean isFullCube()
    {
        return false;
    }

    @Override
    public void parentBlockHarvestItem(World world, IvTileEntityMultiBlock tileEntity, BlockPos pos, IBlockState state)
    {
        dropBlockAsItem(world, pos, new ItemStack(this));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, EntityPlayer player, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IvTileEntityMultiBlock tileEntity = getValidatedTotalParent(this, world, pos);

        if (tileEntity instanceof TileEntityTablePress)
        {
            TileEntityTablePress planksRefinement = (TileEntityTablePress) tileEntity;

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
        return new TileEntityTablePress();
    }
}
