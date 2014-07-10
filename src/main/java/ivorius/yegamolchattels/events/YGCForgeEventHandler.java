/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.events;

import net.minecraftforge.common.MinecraftForge;

/**
 * Created by lukas on 24.05.14.
 */
public class YGCForgeEventHandler
{
    public void register()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
