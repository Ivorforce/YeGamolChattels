/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Created by lukas on 27.07.14.
 */
public class YGCEntityRendererAccessor
{
    public static ResourceLocation getResourceLocation(Render render, Entity entity)
    {
        return render.getEntityTexture(entity);
    }
}
