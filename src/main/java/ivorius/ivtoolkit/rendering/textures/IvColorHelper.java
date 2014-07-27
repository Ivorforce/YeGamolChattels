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
public class IvColorHelper
{
    public static int[] getARBGInts(int argb)
    {
        return new int[]{argb >>> 24, (argb >>> 16) & 255, (argb >>> 8) & 255, argb & 255};
    }

    public static int[] getARBGInts(float[] argb)
    {
        return new int[]{MathHelper.floor_float(argb[0] * 255.0f + 0.5f), MathHelper.floor_float(argb[1] * 255.0f + 0.5f), MathHelper.floor_float(argb[2] * 255.0f + 0.5f), MathHelper.floor_float(argb[3] * 255.0f + 0.5f)};
    }

    public static float[] getARBGFloats(int[] argb)
    {
        return new float[]{(float) argb[0] / 255.0f, (float) argb[1] / 255.0f, (float) argb[2] / 255.0f, (float) argb[3] / 255.0f};
    }

    public static float[] getARBGFloats(int argb)
    {
        int alpha = argb >>> 24;
        int red = (argb >>> 16) & 255;
        int green = (argb >>> 8) & 255;
        int blue = argb & 255;

        return new float[]{(float) alpha / 255.0f, (float) red / 255.0f, (float) green / 255.0f, (float) blue / 255.0f};
    }

    public static int getARBGInt(float[] argb)
    {
        int alpha = MathHelper.clamp_int(MathHelper.floor_float(argb[0] * 255.0f + 0.5f), 0, 255);
        int red = MathHelper.clamp_int(MathHelper.floor_float(argb[1] * 255.0f + 0.5f), 0, 255);
        int green = MathHelper.clamp_int(MathHelper.floor_float(argb[2] * 255.0f + 0.5f), 0, 255);
        int blue = MathHelper.clamp_int(MathHelper.floor_float(argb[3] * 255.0f + 0.5f), 0, 255);

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static float[] getARGB(int data, int bufferedImageType)
    {
        switch (bufferedImageType)
        {
            case BufferedImage.TYPE_4BYTE_ABGR:
                return new float[]{getFloatVal(data, 0), getFloatVal(data, 24), getFloatVal(data, 16), getFloatVal(data, 8)};
            case BufferedImage.TYPE_INT_ARGB:
                return new float[]{getFloatVal(data, 24), getFloatVal(data, 16), getFloatVal(data, 8), getFloatVal(data, 0)};
        }

        throw new IllegalArgumentException("Unrecognized buffered image type: " + bufferedImageType);
    }

    public static int getData(float[] argb, int bufferedImageType)
    {
        switch (bufferedImageType)
        {
            case BufferedImage.TYPE_4BYTE_ABGR:
                return getByteVal(argb[0], 0) | getByteVal(argb[3], 8) | getByteVal(argb[2], 16) | getByteVal(argb[1], 24);
            case BufferedImage.TYPE_INT_ARGB:
                return getByteVal(argb[0], 24) | getByteVal(argb[1], 16) | getByteVal(argb[2], 8) | getByteVal(argb[3], 0);
        }

        throw new IllegalArgumentException("Unrecognized buffered image type: " + bufferedImageType);
    }

    public static float getFloatVal(int value, int shift)
    {
        return ((value >>> shift) & 255) / 255.0f;
    }

    public static int getByteVal(float value, int shift)
    {
        return MathHelper.floor_float(MathHelper.clamp_float(value, 0.0f, 1.0f) * 255.0f + 0.5f) << shift;
    }
}
