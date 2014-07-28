/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.server;

import ivorius.yegamolchattels.YGCProxy;
import net.minecraft.entity.player.EntityPlayer;

public class ServerProxy implements YGCProxy
{
    @Override
    public void loadConfig(String categoryID)
    {

    }

    @Override
    public void registerRenderers()
    {

    }

    @Override
    public EntityPlayer getClientPlayer()
    {
        throw new UnsupportedOperationException();
    }
}
