/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.events;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ivorius.yegamolchattels.YGCConfig;
import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraftforge.common.config.Configuration;

/**
 * Created by lukas on 24.05.14.
 */
public class YGCFMLEventHandler
{
    public void register()
    {
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent event)
    {
        if (event instanceof ConfigChangedEvent.OnConfigChangedEvent && event.modID.equals(YeGamolChattels.MODID))
        {
            if (event.configID.equals(Configuration.CATEGORY_GENERAL))
            {
                YGCConfig.loadConfig(event.configID);
            }
        }
    }
}