/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.gui;

import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by lukas on 17.02.14.
 */
public class YGCGuiHandler implements IGuiHandler
{
    public static final int plankSawGuiID = 0;
    public static final int plankRefinementGuiID = 1;
    public static final int statueCarvingGuiID = 2;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, BlockPos pos)
    {
        if (ID == statueCarvingGuiID)
            return new ContainerCarveStatue(player.inventory, player, pos);

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, BlockPos pos)
    {
        if (ID == plankSawGuiID)
            return new GuiScreenSawBench(world, pos);
        else if (ID == plankRefinementGuiID)
            return new GuiScreenTablePress(world, pos);
        else if (ID == statueCarvingGuiID)
            return new GuiScreenCarveStatue(player, pos);

        return null;
    }
}
