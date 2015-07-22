/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.yegamolchattels.blocks.TileEntityMicroBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by lukas on 11.07.14.
 */
public class ItemBlockFragment extends Item implements MicroblockSelector
{
    public static void setFragment(ItemStack stack, IBlockState blockData)
    {
        stack.setTagInfo("blockFragment", new NBTTagString(Block.blockRegistry.getNameForObject(blockData.getBlock()).toString()));
        stack.setTagInfo("blockFragmentMeta", new NBTTagByte((byte) blockData.getBlock().getMetaFromState(blockData)));
    }

    public static IBlockState getFragment(ItemStack stack)
    {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("blockFragment"))
            return Blocks.air.getDefaultState();

        return ((Block) Block.blockRegistry.getObject(new ResourceLocation(stack.getTagCompound().getString("blockFragment")))).getStateFromMeta(stack.getTagCompound().getByte("blockFragmentMeta"));
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return addBlock(pos, player, itemStack);
    }

    public static boolean addBlock(BlockPos pos, EntityPlayer player, ItemStack usedStack)
    {
        if (addBlock(player, pos, getFragment(usedStack)))
        {
            usedStack.stackSize--;
            player.inventory.markDirty();

            return true;
        }

        return false;
    }

    public static boolean addBlock(Entity entity, int hoverX, int hoverY, int hoverZ, IBlockState blockFragment)
    {
        World world = entity.worldObj;
        ItemChisel.MicroBlockFragment hoveredFragment = ItemChisel.getHoveredFragment(entity, hoverX, hoverY, hoverZ);

        if (hoveredFragment != null)
        {
            hoveredFragment = hoveredFragment.getOpposite();
            BlockPos fragmentCoord = hoveredFragment.getCoord();

            TileEntity tileEntity = world.getTileEntity(fragmentCoord.x, fragmentCoord.y, fragmentCoord.z);

            if (!(tileEntity instanceof TileEntityMicroBlock))
            {
                ItemChisel.convertToMicroBlock(world, fragmentCoord);
                tileEntity = world.getTileEntity(fragmentCoord.x, fragmentCoord.y, fragmentCoord.z);
            }

            if (tileEntity instanceof TileEntityMicroBlock)
            {
                TileEntityMicroBlock tileEntityMicroBlock = (TileEntityMicroBlock) tileEntity;
                IvBlockCollection collection = tileEntityMicroBlock.getBlockCollection();

                Block hitInternalBlock = collection.getBlock(hoveredFragment.getInternalCoord());
                if (hitInternalBlock.getMaterial() == Material.air)
                {
                    collection.setBlockAndMetadata(hoveredFragment.getInternalCoord(), blockFragment.block, blockFragment.meta);
                    if (tileEntityMicroBlock.validateBeingMicroblock())
                        tileEntityMicroBlock.markCacheInvalid();

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        super.addInformation(stack, player, list, par4);

        IBlockState data = getFragment(stack);
        if (data != null)
            list.add(ItemMicroBlock.getLocalizedName(data));
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {

    }

    @Override
    public boolean showMicroblockSelection(EntityLivingBase renderEntity, ItemStack stack)
    {
        return true;
    }

    @Override
    public float microblockSelectionSize(ItemStack stack)
    {
        return 0.52f;
    }
}
