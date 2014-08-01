/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.client.rendering;

import cpw.mods.fml.relauncher.ReflectionHelper;
import ivorius.ivtoolkit.logic.ReferenceCounter;
import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.YGCEntityRendererAccessor;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by lukas on 28.07.14.
 */
public class EntityBlockTextureMerger
{
    private static int[] cachedBlockTextureMap;

    public static BufferedImage getTexture(Block block, int metadata)
    {
        IIcon icon = block.getIcon(ForgeDirection.NORTH.ordinal(), metadata);
        if (icon instanceof TextureAtlasSprite)
        {
            TextureAtlasSprite sprite = (TextureAtlasSprite) icon;

            if (sprite.getFrameCount() > 0)
            {
                int[][] textureData = sprite.getFrameTextureData(0);
                int[] textureRGB = textureData[0];
                BufferedImage image = new BufferedImage(sprite.getIconWidth(), sprite.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                image.setRGB(0, 0, sprite.getIconWidth(), sprite.getIconHeight(), textureRGB, 0, 1);
                return image;
            }
            else
            {
                return getStitchedTextureSubImage(icon);
            }
        }

        return null;
    }

    public static BufferedImage getStitchedTextureSubImage(IIcon icon)
    {
        int textureWidth = MathHelper.floor_float(1.0f / (icon.getMaxU() - icon.getMinU()) + 0.5f) * icon.getIconWidth();
        int textureHeight = MathHelper.floor_float(1.0f / (icon.getMaxV() - icon.getMinV()) + 0.5f) * icon.getIconHeight();

        int minX = MathHelper.floor_float(icon.getMinU() * textureWidth + 0.5f);
        int minY = MathHelper.floor_float(icon.getMinV() * textureHeight + 0.5f);

        int[] subImage = getStitchedTextureSubImage(minX, minY, icon.getIconWidth(), icon.getIconHeight(), textureWidth, textureHeight);
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, icon.getIconWidth(), icon.getIconHeight(), subImage, 0, icon.getIconWidth());
        return image;
    }

    public static int[] getStitchedTextureSubImage(int x, int y, int width, int height, int textureWidth, int textureHeight)
    {
        int[] stitched = getStitchedTexture(textureWidth, textureHeight);

        int[] returnTex = new int[width * height];
        for (int curY = 0; curY < height; curY++)
        {
            for (int curX = 0; curX < width; curX++)
            {
                returnTex[curX + curY * width] = stitched[x + curX + (y + curY) * textureWidth];
            }
        }

        return returnTex;
    }

    public static int[] getStitchedTexture(int textureWidth, int textureHeight)
    {
        if (cachedBlockTextureMap == null)
        {
            TextureMap blocksTexture = (TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture);
            int blocksTextureID = blocksTexture.getGlTextureId();

            cachedBlockTextureMap = getStitchedTexture(textureWidth, textureHeight, blocksTextureID);
        }

        return cachedBlockTextureMap;
    }

    public static int[] getStitchedTexture(int textureWidth, int textureHeight, int textureID)
    {
        byte[] pixels = new byte[textureWidth * textureHeight * 4];
        ByteBuffer buffer = ByteBuffer.allocateDirect(pixels.length).order(ByteOrder.nativeOrder());

//                glPixelStorei( GL_UNPACK_ROW_LENGTH, blocksTexture. );
        glBindTexture(GL_TEXTURE_2D, textureID);
        glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        buffer.get(pixels);

        int[] texture = new int[pixels.length / 4];
        for (int i = 0; i < texture.length; i++)
        {
            texture[i] = ((pixels[i * 4 + 3] & 0xff) << 24) | ((pixels[i * 4] & 0xff) << 16) | ((pixels[i * 4 + 1] & 0xff) << 8) | (pixels[i * 4 + 2] & 0xff);
        }
        return texture;
    }

    public static void clearCachedStitchedTexture()
    {
        cachedBlockTextureMap = null;
    }

    public static void saveCachedTexture(BufferedImage bufferedImage, String name)
    {
        try
        {
            String filename = "Rendered_" + Math.random();
            ImageIO.write(bufferedImage, "png", new File(Minecraft.getMinecraft().mcDataDir, filename + ".png"));
            FileUtils.writeStringToFile(new File(Minecraft.getMinecraft().mcDataDir, filename + ".txt"), name);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static ResourceLocation getTexture(Entity entity)
    {
        Render renderObject = RenderManager.instance.getEntityRenderObject(entity);
        return renderObject != null ? YGCEntityRendererAccessor.getResourceLocation(renderObject, entity) : new ResourceLocation(YeGamolChattels.MODID, "textures/blocks/carpet.png");
    }
}
