/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 *
 * You are free to:
 *
 * Share — copy and redistribute the material in any medium or format
 * Adapt — remix, transform, and build upon the material
 * The licensor cannot revoke these freedoms as long as you follow the license terms.
 *
 * Under the following terms:
 *
 * Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
 * NonCommercial — You may not use the material for commercial purposes, unless you have a permit by the creator.
 * ShareAlike — If you remix, transform, or build upon the material, you must distribute your contributions under the same license as the original.
 * No additional restrictions — You may not apply legal terms or technological measures that legally restrict others from doing anything the license permits.
 */

package ivorius.ivtoolkit.rendering.textures;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lukas on 26.07.14.
 */
public class IvTextureCreatorMC
{
    public static BufferedImage getImage(IResourceManager resourceManager, ResourceLocation location, Logger logger) throws IOException
    {
        InputStream inputstream = null;

        try
        {
            IResource iresource = resourceManager.getResource(location);
            inputstream = iresource.getInputStream();
            BufferedImage bufferedimage = ImageIO.read(inputstream);
//            boolean flag = false;
//            boolean flag1 = false;
//
//            if (iresource.hasMetadata())
//            {
//                try
//                {
//                    TextureMetadataSection texturemetadatasection = (TextureMetadataSection) iresource.getMetadata("texture");
//
//                    if (texturemetadatasection != null)
//                    {
//                        flag = texturemetadatasection.getTextureBlur();
//                        flag1 = texturemetadatasection.getTextureClamp();
//                    }
//                }
//                catch (RuntimeException runtimeexception)
//                {
//                    logger.warn("Failed reading metadata of: " + location, runtimeexception);
//                }
//            }

            return bufferedimage;
        }
        finally
        {
            if (inputstream != null)
            {
                inputstream.close();
            }
        }
    }

    public static interface LoadingImageEffect extends IvTextureCreator.ImageEffect
    {
        void load(IResourceManager resourceManager) throws IOException;
    }
}
