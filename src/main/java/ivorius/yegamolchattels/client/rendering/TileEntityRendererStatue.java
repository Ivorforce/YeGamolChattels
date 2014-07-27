/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;


import ivorius.ivtoolkit.blocks.IvMultiBlockRenderHelper;
import ivorius.ivtoolkit.rendering.textures.IvTexturePatternColorizer;
import ivorius.ivtoolkit.rendering.textures.ModifiedTexture;
import ivorius.ivtoolkit.rendering.textures.PreBufferedTexture;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityStatue;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.YGCEntityRendererAccessor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.io.FileUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.GL11.*;

public class TileEntityRendererStatue extends TileEntitySpecialRenderer
{
    public RenderEngineOverride renderEngineOverride;
    public ResourceLocation statueFallbackTexture;

    private static int[] cachedBlockTextureMap;

    public TileEntityRendererStatue()
    {
        renderEngineOverride = new RenderEngineOverride();
        statueFallbackTexture = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "statueFallbackTexture.png");
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        TileEntityStatue statueEntity = (TileEntityStatue) tileentity;

        if (statueEntity.getStatueEntity() != null)
        {
            renderTileEntityStatueAt(statueEntity, d, d1, d2, f);
        }
    }

    public void renderTileEntityStatueAt(TileEntityStatue tileEntityStatue, double d, double d1, double d2, float f)
    {
        if (tileEntityStatue.isParent())
        {
            GL11.glPushMatrix();
            GL11.glTranslated(0.0, -tileEntityStatue.centerCoordsSize[1], 0.0);
            IvMultiBlockRenderHelper.transformFor(tileEntityStatue, d, d1, d2);

            Entity entity = tileEntityStatue.getStatueEntity();
            entity.setWorld(tileEntityStatue.getWorldObj());

            int statusBarLength = BossStatus.statusBarTime; //Don't render boss health

            TileEntityStatue.BlockFragment fragment = tileEntityStatue.getStatueBlock();
            BufferedImage patternImage = getTexture(fragment.getBlock(), fragment.getMetadata());

            if (patternImage != null)
            {
                ResourceLocation entityResourceLocation = getTexture(entity);
                if (entityResourceLocation != null)
                {
                    ResourceLocation textureColorized = new ResourceLocation(entityResourceLocation + "|YGC_COL_" + fragment.getBlock() + "_" + fragment.getMetadata());

                    if (Minecraft.getMinecraft().getTextureManager().getTexture(textureColorized) == null)
                    {
                        ModifiedTexture modifiedTexture = new ModifiedTexture(entityResourceLocation, new IvTexturePatternColorizer(patternImage, YeGamolChattels.logger), YeGamolChattels.logger);
                        Minecraft.getMinecraft().getTextureManager().loadTexture(textureColorized, modifiedTexture);
                    }

                    renderEngineOverride.textureOverride = textureColorized;
                }
                else
                {
                    ResourceLocation patternResource = new ResourceLocation(YeGamolChattels.MODID, "Block|" + Block.blockRegistry.getNameForObject(fragment.getBlock()) + "_" + fragment.getMetadata());

                    if (Minecraft.getMinecraft().getTextureManager().getTexture(patternResource) == null)
                        Minecraft.getMinecraft().getTextureManager().loadTexture(patternResource, new PreBufferedTexture(patternImage));

                    renderEngineOverride.textureOverride = patternResource;
                }
            }
            else
            {
                renderEngineOverride.textureOverride = statueFallbackTexture;
            }

            renderEngineOverride.renderEngine = RenderManager.instance.renderEngine;
            RenderManager.instance.renderEngine = renderEngineOverride;

            try
            {
                RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0f, 0.0F);
            }
            catch (Exception ex)
            {
                YeGamolChattels.logger.warn("Exception on rendering statue!", ex);
            }

            RenderManager.instance.renderEngine = renderEngineOverride.renderEngine;

            BossStatus.statusBarTime = statusBarLength;

            GL11.glPopMatrix();
        }
    }

    private static BufferedImage getTexture(Block block, int metadata)
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

    private static BufferedImage getStitchedTextureSubImage(IIcon icon)
    {
        int textureWidth = MathHelper.floor_float(1.0f / (icon.getMaxU() - icon.getMinU()) + 0.5f) * icon.getIconWidth();
        int textureHeight = MathHelper.floor_float(1.0f / (icon.getMaxV() - icon.getMinV()) + 0.5f) * icon.getIconHeight();

        int minX = MathHelper.floor_float(icon.getMinU() * textureWidth);
        int minY = MathHelper.floor_float(icon.getMinV() * textureHeight);

        int[] subImage = getStitchedTextureSubImage(minX, minY, icon.getIconWidth(), icon.getIconHeight(), textureWidth, textureHeight);
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, icon.getIconWidth(), icon.getIconHeight(), subImage, 0, 1);
        return image;
    }

    private static int[] getStitchedTextureSubImage(int x, int y, int width, int height, int textureWidth, int textureHeight)
    {
        int[] stitched = getStitchedTexture(textureWidth, textureHeight);

        int[] returnTex = new int[width * height];
        for (int curX = 0; curX < width; curX++)
            for (int curY = 0; curY < height; curY++)
            {
                returnTex[curX + curY * width] = stitched[x + curX + (y + curY) * textureWidth];
            }

        return returnTex;
    }

    private static int[] getStitchedTexture(int textureWidth, int textureHeight)
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
            ImageIO.write(bufferedImage, "jpg", new File(Minecraft.getMinecraft().mcDataDir, filename + ".jpg"));
            FileUtils.writeStringToFile(new File(Minecraft.getMinecraft().mcDataDir, filename + ".txt"), name);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static ResourceLocation getTexture(Entity entity)
    {
        Render renderObject = RenderManager.instance.getEntityRenderObject(entity);
        return renderObject != null ? YGCEntityRendererAccessor.getResourceLocation(renderObject, entity) : new ResourceLocation(YeGamolChattels.MODID, "textures/blocks/carpet.png");
    }
}
