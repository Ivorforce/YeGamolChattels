/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

/**
 * Created by lukas on 09.02.14.
 */
public class RenderEngineOverride extends TextureManager
{
    public TextureManager renderEngine;
    public ResourceLocation textureOverride;

    public RenderEngineOverride()
    {
        super(null);
    }

    public void bindTexture(ResourceLocation par1ResourceLocation)
    {
        if (textureOverride == null)
            renderEngine.bindTexture(par1ResourceLocation);
        else if (par1ResourceLocation.getResourcePath().indexOf("armor") >= 0)
            renderEngine.bindTexture(par1ResourceLocation);
        else if (par1ResourceLocation.getResourcePath().indexOf("spider_eyes") >= 0)
            renderEngine.bindTexture(par1ResourceLocation);
        else if (par1ResourceLocation.getResourcePath().indexOf("enderman_eyes") >= 0)
            renderEngine.bindTexture(par1ResourceLocation);
        else if (par1ResourceLocation.getResourcePath().indexOf("dragon_eyes") >= 0)
            renderEngine.bindTexture(par1ResourceLocation);
        else if (par1ResourceLocation.equals(TextureMap.locationBlocksTexture))
            renderEngine.bindTexture(par1ResourceLocation);
        else
        {
            renderEngine.bindTexture(textureOverride);
        }
    }

    public ResourceLocation getResourceLocation(int par1)
    {
        return renderEngine.getResourceLocation(par1);
    }

    public boolean loadTextureMap(ResourceLocation par1ResourceLocation, TextureMap par2TextureMap)
    {
        return renderEngine.loadTextureMap(par1ResourceLocation, par2TextureMap);
    }

    public boolean loadTickableTexture(ResourceLocation par1ResourceLocation, ITickableTextureObject par2TickableTextureObject)
    {
        return renderEngine.loadTickableTexture(par1ResourceLocation, par2TickableTextureObject);
    }

    public boolean loadTexture(ResourceLocation par1ResourceLocation, final ITextureObject par2TextureObject)
    {
        return renderEngine.loadTexture(par1ResourceLocation, par2TextureObject);
    }

    public ITextureObject getTexture(ResourceLocation par1ResourceLocation)
    {
        return renderEngine.getTexture(par1ResourceLocation);
    }

    public ResourceLocation getDynamicTextureLocation(String par1Str, DynamicTexture par2DynamicTexture)
    {
        return renderEngine.getDynamicTextureLocation(par1Str, par2DynamicTexture);
    }

    public void tick()
    {
        renderEngine.tick();
    }

    public void deleteTexture(ResourceLocation p_147645_1_)
    {
        renderEngine.deleteTexture(p_147645_1_);
    }

    public void onResourceManagerReload(IResourceManager par1ResourceManager)
    {
        renderEngine.onResourceManagerReload(par1ResourceManager);
    }
}
