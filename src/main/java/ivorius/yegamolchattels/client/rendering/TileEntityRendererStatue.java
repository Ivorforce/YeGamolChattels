/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;


import ivorius.ivtoolkit.blocks.IvMultiBlockRenderHelper;
import ivorius.ivtoolkit.logic.IvObjects;
import ivorius.ivtoolkit.rendering.textures.IvTexturePatternColorizer;
import ivorius.ivtoolkit.rendering.textures.ModifiedTexture;
import ivorius.ivtoolkit.rendering.textures.PreBufferedTexture;
import ivorius.yegamolchattels.YGCConfig;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.Statue;
import ivorius.yegamolchattels.blocks.TileEntityStatue;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;

public class TileEntityRendererStatue extends TileEntitySpecialRenderer
{
    public RenderEngineOverride renderEngineOverride;
    public ResourceLocation statueFallbackTexture;

    public TileEntityRendererStatue()
    {
        renderEngineOverride = new RenderEngineOverride();
        statueFallbackTexture = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "statueFallbackTexture.png");
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderTileEntityStatueAt((TileEntityStatue) tileentity, d, d1, d2, f);
    }

    public void renderTileEntityStatueAt(TileEntityStatue tileEntityStatue, double x, double y, double z, float partialTicks)
    {
        if (tileEntityStatue.isParent())
        {
            int renderPass = MinecraftForgeClient.getRenderPass();

            GL11.glPushMatrix();
            GL11.glTranslated(0.0, -tileEntityStatue.centerCoordsSize[1], 0.0);
            IvMultiBlockRenderHelper.transformFor(tileEntityStatue, x, y, z);

            Entity entity = tileEntityStatue.getStatue().getEntity();
            entity.setWorld(tileEntityStatue.getWorldObj());

            int statusBarLength = BossStatus.statusBarTime; //Don't render boss health

            ResourceLocation entityResourceLocation = YGCConfig.doStatueTextureMerge ? EntityBlockTextureMerger.getTexture(entity) : null;

            if (!IvObjects.equals(entityResourceLocation, tileEntityStatue.getUsedEntityTexture()))
            {
                tileEntityStatue.releaseTexture();
                tileEntityStatue.setUsedEntityTexture(null);
            }

            if (tileEntityStatue.getStatueTexture() != null)
                renderEngineOverride.textureOverride = tileEntityStatue.getStatueTexture();
            else
            {
                ResourceLocation colorizedTexture = createTexture(tileEntityStatue, entityResourceLocation);

                tileEntityStatue.setUsedEntityTexture(entityResourceLocation);

                if (colorizedTexture != null)
                {
                    tileEntityStatue.setStatueTexture(colorizedTexture);
                    TextureAllocationHandler.retainTexture(colorizedTexture);
                    renderEngineOverride.textureOverride = colorizedTexture;
                }
                else
                {
                    renderEngineOverride.textureOverride = statueFallbackTexture;
                }
            }

            renderEngineOverride.renderEngine = RenderManager.instance.renderEngine;
            RenderManager.instance.renderEngine = renderEngineOverride;

            if (renderPass > 0) // Somehow 'false' is default
            {
                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            try
            {
                tileEntityStatue.getStatue().updateEntityRotations();
                GL11.glRotatef(180.0f - entity.rotationYaw, 0.0f, 1.0f, 0.0f);
                RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0f, 0.0F);
            }
            catch (Exception ex)
            {
                YeGamolChattels.logger.warn("Exception on rendering statue!", ex);
            }

            if (renderPass > 0)
            {
                GL11.glDepthMask(false);
                GL11.glDisable(GL11.GL_BLEND);
            }

            RenderManager.instance.renderEngine = renderEngineOverride.renderEngine;

            BossStatus.statusBarTime = statusBarLength;

            GL11.glPopMatrix();
        }
    }

    public ResourceLocation createTexture(TileEntityStatue tileEntityStatue, ResourceLocation entityResourceLocation)
    {
        if (YGCConfig.fetchDynamicStatueTextures)
        {
            Statue.BlockFragment fragment = tileEntityStatue.getStatue().getMaterial();
            BufferedImage patternImage = EntityBlockTextureMerger.getTexture(fragment.getBlock(), fragment.getMetadata());

            if (patternImage != null)
            {
                TextureManager texManager = Minecraft.getMinecraft().getTextureManager();

                if (entityResourceLocation != null)
                {
                    ITextureObject entityResourceTexture = texManager.getTexture(entityResourceLocation);
                    if (!isTextureFinal(entityResourceTexture))
                    {
                        return null; // Wait
                    }
                    else
                    {
                        ResourceLocation textureColorized = new ResourceLocation(YeGamolChattels.MODID, "colorized/" + entityResourceLocation + "_" + Block.blockRegistry.getNameForObject(fragment.getBlock()) + "_" + fragment.getMetadata());

                        if (texManager.getTexture(textureColorized) == null)
                        {
                            ModifiedTexture modifiedTexture = new ModifiedTexture(entityResourceLocation, new IvTexturePatternColorizer(patternImage, YeGamolChattels.logger), YeGamolChattels.logger);

                            if (texManager.loadTexture(textureColorized, modifiedTexture))
                                return textureColorized;
                            else
                                return null; // Merging failed
                        }
                        else
                            return textureColorized;
                    }
                }
                else
                {
                    ResourceLocation patternResource = new ResourceLocation(YeGamolChattels.MODID, "blockextract/" + Block.blockRegistry.getNameForObject(fragment.getBlock()) + "_" + fragment.getMetadata());

                    if (texManager.getTexture(patternResource) == null)
                        texManager.loadTexture(patternResource, new PreBufferedTexture(patternImage));

                    return patternResource;
                }
            }
        }

        return null;
    }

    public static boolean isTextureFinal(ITextureObject resourceLocation)
    {
        return true;
    }
}
