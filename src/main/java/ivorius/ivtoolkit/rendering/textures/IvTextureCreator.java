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

import net.minecraft.util.MathHelper;

import java.awt.image.BufferedImage;

/**
 * Created by lukas on 26.07.14.
 */
public class IvTextureCreator
{
    public static BufferedImage applyEffect(BufferedImage texture, ImageEffect effect)
    {
        BufferedImage modified = new BufferedImage(texture.getWidth(), texture.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < texture.getWidth(); x++)
            for (int y = 0; y < texture.getHeight(); y++)
            {
                modified.setRGB(x, y, effect.getPixel(texture.getRGB(x, y), x, y, texture.getType(), modified.getType()));
            }

        return modified;
    }

    public static interface ImageEffect
    {
        int getPixel(int pixel, int x, int y, int sourceDataType, int destDataType);
    }
}
