/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.gui;

import ivorius.ivtoolkit.network.PacketGuiAction;
import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by lukas on 27.07.14.
 */
public class GuiScreenCarveStatue extends GuiContainer
{
    public GuiScreenCarveStatue(EntityPlayer player, int x, int y, int z)
    {
        super(new ContainerCarveStatue(player.inventory, player, x, y, z));
    }

    @Override
    public void initGui()
    {
        super.initGui();

        buttonList.add(new GuiButton(0, width / 2 + 30, height / 2 - 25, 100, 20, I18n.format("gui.carve.confirm")));
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 0)
        {
            YeGamolChattels.network.sendToServer(PacketGuiAction.packetGuiAction("carveStatue"));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {

    }
}
