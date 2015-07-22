/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.IvMultiBlockHelper;
import ivorius.yegamolchattels.blocks.BlockWeaponRack;
import ivorius.yegamolchattels.blocks.TileEntityWeaponRack;
import ivorius.yegamolchattels.blocks.YGCBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemWeaponRack extends ItemBlock
{
    public ItemWeaponRack(Block block)
    {
        super(block);
        maxStackSize = 16;
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumFacing side, float par8, float par9, float par10)
    {
        IBlockState prevState = par3World.getBlockState(pos);

        if (block == Blocks.snow_layer && ((Integer)prevState.getValue(BlockSnow.LAYERS)).intValue() < 1)
        {
            side = EnumFacing.UP;
        }
        else if (!block.isReplaceable(par3World, pos))
        {
            pos = pos.offset(side);
        }
        else
        {
            side = EnumFacing.DOWN; // When replacing a block, this should be on ground
        }

        if (side == EnumFacing.UP)
        {
            return false;
        }

        Block block = YGCBlocks.weaponRack;
        boolean onWall = side != EnumFacing.DOWN;

        if (!block.canPlaceBlockAt(par3World, pos))
        {
            return false;
        }

        EnumFacing facing = !onWall ? par2EntityPlayer.getHorizontalFacing() : side;

        par3World.setBlockState(pos, block.getDefaultState().withProperty(BlockWeaponRack.ON_WALL, onWall), 3);

        TileEntity tileEntity = par3World.getTileEntity(pos);
        if (tileEntity instanceof TileEntityWeaponRack)
            ((TileEntityWeaponRack) tileEntity).facing = facing;

        par1ItemStack.stackSize--;

        return true;
    }
}
