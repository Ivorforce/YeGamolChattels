/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.yegamolchattels.blocks.TileEntityMicroBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by lukas on 11.07.14.
 */
public class ItemBlockFragment extends Item
{
    public static void setFragment(ItemStack stack, ItemChisel.BlockData blockData)
    {
        stack.setTagInfo("blockFragment", new NBTTagString(Block.blockRegistry.getNameForObject(blockData.block)));
        stack.setTagInfo("blockFragmentMeta", new NBTTagByte(blockData.meta));
    }

    public static ItemChisel.BlockData getFragment(ItemStack stack)
    {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("blockFragment"))
            return null;

        return new ItemChisel.BlockData((Block) Block.blockRegistry.getObject(stack.getTagCompound().getString("blockFragment")), stack.getTagCompound().getByte("blockFragmentMeta"));
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return addBlock(x, y, z, player, itemStack);
    }

    public static boolean addBlock(int x, int y, int z, EntityPlayer player, ItemStack usedStack)
    {
        if (addBlock(player, x, y, z, getFragment(usedStack)))
        {
            usedStack.stackSize--;
            player.inventory.markDirty();

            return true;
        }

        return false;
    }

    public static boolean addBlock(Entity entity, int hoverX, int hoverY, int hoverZ, ItemChisel.BlockData blockFragment)
    {
        World world = entity.worldObj;
        ItemChisel.MicroBlockFragment hoveredFragment = ItemChisel.getHoveredFragment(entity, hoverX, hoverY, hoverZ);

        if (hoveredFragment != null)
        {
            hoveredFragment = hoveredFragment.getOpposite();
            BlockCoord fragmentCoord = hoveredFragment.getCoord();

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
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);

        ItemChisel.BlockData data = getFragment(par1ItemStack);
        if (data != null)
            par3List.add(ItemMicroBlock.getLocalizedName(data));
    }

    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {

    }
}
