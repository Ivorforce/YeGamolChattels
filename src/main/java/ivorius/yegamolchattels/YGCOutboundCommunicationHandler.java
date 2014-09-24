/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import ivorius.yegamolchattels.blocks.YGCBlocks;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by lukas on 24.09.14.
 */
public class YGCOutboundCommunicationHandler
{
    public static void init()
    {
        if (Loader.isModLoaded("MineFactoryReloaded"))
        {
            registerMineFactoryCrop(YGCBlocks.flaxPlant, YGCItems.flaxSeeds, null);
        }
    }

    private static void registerMineFactoryCrop(Block crop, Item seed, Integer seedDamage)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("crop", id(crop));
        tag.setString("seed", id(seed));
        if (seedDamage != null) // accepted metadata of the 'seed' item (optional); defaults to OreDictionary.WILDCARD_VALUE
            tag.setInteger("meta", seedDamage);
        FMLInterModComms.sendMessage("MineFactoryReloaded", "registerPlantable_Crop", tag);
    }

    private static String id(Block block)
    {
        return Block.blockRegistry.getNameForObject(block);
    }

    private static String id(Item item)
    {
        return Item.itemRegistry.getNameForObject(item);
    }
}
