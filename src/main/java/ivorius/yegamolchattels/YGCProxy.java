package ivorius.yegamolchattels;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by lukas on 01.07.14.
 */
public interface YGCProxy
{
    void registerRenderers();

    EntityPlayer getClientPlayer();
}
