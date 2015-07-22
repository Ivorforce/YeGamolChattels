/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import net.minecraft.util.BlockPos;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.tools.MCRegistryDefault;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lukas on 11.07.14.
 */
public class ItemMicroBlock extends ItemBlock
{
    public ItemMicroBlock(Block block)
    {
        super(block);
    }

    public static void setMicroBlock(ItemStack stack, IvBlockCollection collection)
    {
        stack.setTagInfo("microBlock", collection.createTagCompound());
    }

    @Nullable
    public static IvBlockCollection containedMicroBlock(ItemStack stack)
    {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("microBlock"))
            return null;

        return new IvBlockCollection(stack.getTagCompound().getCompoundTag("microBlock"), MCRegistryDefault.INSTANCE);
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);

        IvBlockCollection collection = containedMicroBlock(par1ItemStack);
        if (collection != null)
        {
            Set<IBlockState> blockSet = new HashSet<>();
            for (BlockPos coord : collection)
            {
                Block block = collection.getBlock(coord);
                if (block.getMaterial() != Material.air)
                    blockSet.add(new IBlockState(block, collection.getMetadata(coord)));
            }

            if (blockSet.size() > 0)
            {
                StringBuilder blockNames = new StringBuilder();
                int lineCount = 0;
                int curCount = 0;
                for (IBlockState block : blockSet)
                {
                    if (curCount > 0)
                        blockNames.append(", ");

                    blockNames.append(getLocalizedName(block));
                    curCount++;

                    if (curCount == 3)
                    {
                        curCount = 0;
                        par3List.add(blockNames.toString());
                        blockNames = new StringBuilder();
                        lineCount++;

                        if (lineCount > 3)
                        {
                            par3List.add("[...]");
                            break;
                        }
                    }
                }

                if (curCount > 0)
                    par3List.add(blockNames.toString());
            }
        }
    }

    public static String getLocalizedName(IBlockState blockData)
    {
        ItemStack item = new ItemStack(blockData.block, blockData.meta);
        return item.getItem().getItemStackDisplayName(item);
    }
}
