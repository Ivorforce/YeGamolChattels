/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.client.rendering;

import net.minecraftforge.fml.relauncher.ReflectionHelper;
import ivorius.ivtoolkit.logic.ReferenceCounter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * Created by lukas on 01.08.14.
 */
public class TextureAllocationHandler
{
    private static ReferenceCounter<ResourceLocation> textureObjects = new ReferenceCounter<>();

    public static void retainTexture(ResourceLocation object)
    {
        textureObjects.retain(object, 1);
    }

    public static void releaseTexture(ResourceLocation object)
    {
        textureObjects.release(object, 1);
    }

    public static void deallocateAllFreeTextures()
    {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        for (ResourceLocation textureObject : textureObjects.deallocateAllFreeObjects())
        {
            textureManager.deleteTexture(textureObject);
            Map textureMap = ReflectionHelper.getPrivateValue(TextureManager.class, textureManager, "mapTextureObjects", "field_110585_a");
            textureMap.remove(textureObject);
        }
    }
}
