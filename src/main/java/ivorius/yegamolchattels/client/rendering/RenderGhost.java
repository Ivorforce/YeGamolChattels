/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;

import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderGhost extends RenderLiving
{
    public ResourceLocation texture;

    public RenderGhost()
    {
        super(new ModelGhost(), 0.5F);

        texture = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "ghostTexture.png");
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return texture;
    }
}
