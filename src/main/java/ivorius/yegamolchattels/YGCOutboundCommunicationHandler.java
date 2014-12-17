/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels;

import cpw.mods.fml.common.Loader;
import ivorius.yegamolchattels.blocks.YGCBlocks;
import ivorius.yegamolchattels.items.YGCItems;
import ivorius.yegamolchattels.mods.MineFactoryReloaded;

/**
 * Created by lukas on 24.09.14.
 */
public class YGCOutboundCommunicationHandler
{
    public static void init()
    {
        if (Loader.isModLoaded(MineFactoryReloaded.MOD_ID))
        {
            MineFactoryReloaded.registerPlantableCrop(YGCBlocks.flaxPlant, YGCItems.flaxSeeds, null);

            MineFactoryReloaded.registerHarvestableCrop(YGCBlocks.flaxPlant, 7);

            MineFactoryReloaded.registerFertilizableCrop(YGCBlocks.flaxPlant, 7, null);
        }
    }
}
