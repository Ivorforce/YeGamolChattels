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

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by lukas on 26.07.14.
 */
public class IvTexturePatternColorizer implements IvTextureCreatorMC.LoadingImageEffect
{
    private ResourceLocation resourceLocation;
    private BufferedImage bufferedImage;

    private Logger logger;

    public IvTexturePatternColorizer(ResourceLocation resourceLocation, Logger logger)
    {
        this.resourceLocation = resourceLocation;
        this.logger = logger;
    }

    public IvTexturePatternColorizer(BufferedImage bufferedImage, Logger logger)
    {
        this.bufferedImage = bufferedImage;
        this.logger = logger;
    }

    @Override
    public void load(IResourceManager resourceManager) throws IOException
    {
        if (resourceLocation != null)
            bufferedImage = IvTextureCreatorMC.getImage(resourceManager, resourceLocation, logger);
    }

    @Override
    public int getPixel(int pixel, int x, int y, int sourceDataType, int destDataType)
    {
        float[] colors = IvColorHelper.getARGB(pixel, sourceDataType);

        float[] patternColors = IvColorHelper.getARGB(bufferedImage.getRGB(x % bufferedImage.getWidth(), y % bufferedImage.getHeight()), bufferedImage.getType());
        int[] patternARGBInts = IvColorHelper.getARBGInts(patternColors);

        float[] hsb = Color.RGBtoHSB(patternARGBInts[1], patternARGBInts[2], patternARGBInts[3], null);
        hsb[2] = (0.2126f * colors[1] + 0.7152f * colors[2] + 0.0722f * colors[3]) * 0.5f + hsb[2] * 0.5f;
        float[] patternColor = IvColorHelper.getARBGFloats(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));

        colors[0] = 1.0f; // TODO Figure out why alpha is seemingly random
        colors[1] = colors[1] * 0.1f + patternColor[1] * 0.9f;
        colors[2] = colors[2] * 0.1f + patternColor[2] * 0.9f;
        colors[3] = colors[3] * 0.1f + patternColor[3] * 0.9f;

        return IvColorHelper.getData(colors, destDataType);
    }
}
