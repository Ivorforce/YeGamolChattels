/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvMultiBlockHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import ivorius.ivtoolkit.rendering.grid.Icon;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockLootChest extends Block
{
    public BlockLootChest()
    {
        super(Material.wood);

        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.8f, 1.0f);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntityLootChest tileEntityLootChest = (TileEntityLootChest) world.getTileEntity(pos);
        tileEntityLootChest.dropAllItems();

        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing facing, float par7, float par8, float par9)
    {
        TileEntityLootChest lootChest = (TileEntityLootChest) world.getTileEntity(pos);

        if (!world.isRemote)
        {
            if (!lootChest.opened)
            {
                lootChest.open();
            }
            else if (lootChest.itemAccessible())
            {
                if (lootChest.firstItem() == null)
                {
                    ItemStack currentItem = player.getCurrentEquippedItem();
                    if (currentItem != null)
                    {
                        lootChest.addLoot(currentItem.copy());
                        currentItem.stackSize = 0;
                    }
                    lootChest.close();
                }
                else
                {
                    lootChest.pickUpItem(player);
                }
            }
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        TileEntityLootChest entity = (TileEntityLootChest) worldIn.getTileEntity(pos);
        entity.direction = IvMultiBlockHelper.getRotation(placer);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World var1, IBlockState state)
    {
        return new TileEntityLootChest();
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
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, BlockPos pos)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof TileEntityLootChest)
            return Container.calcRedstoneFromInventory((TileEntityLootChest) tileEntity);

        return super.getComparatorInputOverride(worldIn, pos);
    }
}
