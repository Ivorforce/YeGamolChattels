/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by lukas on 17.02.14.
 */
public class YGCGuiHandler implements IGuiHandler
{
    public static final int plankSawGuiID = 0;
    public static final int plankRefinementGuiID = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == plankSawGuiID)
            return new GuiScreenPlankSaw(world, x, y, z);
        else if (ID == plankRefinementGuiID)
            return new GuiScreenPlanksRefinement(world, x, y, z);

        return null;
    }
}
