/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;


import ivorius.ivtoolkit.blocks.IvMultiBlockRenderHelper;
import ivorius.ivtoolkit.rendering.textures.IvTexturePatternColorizer;
import ivorius.ivtoolkit.rendering.textures.ModifiedTexture;
import ivorius.ivtoolkit.rendering.textures.PreBufferedTexture;
import ivorius.yegamolchattels.YGCConfig;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityStatue;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
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

            if (tileEntityStatue.getStatueTexture() != null)
                renderEngineOverride.textureOverride = tileEntityStatue.getStatueTexture();
            else
            {
                ResourceLocation texture = createTexture(tileEntityStatue);

                if (texture != null)
                {
                    tileEntityStatue.setStatueTexture(texture);
                    StatueTextureHandler.retainTexture(texture);
                    renderEngineOverride.textureOverride = texture;
                }
                else
                {
                    renderEngineOverride.textureOverride = statueFallbackTexture;
                }
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

    public ResourceLocation createTexture(TileEntityStatue tileEntityStatue)
    {
        if (YGCConfig.fetchDynamicStatueTextures)
        {
            Entity entity = tileEntityStatue.getStatueEntity();

            TileEntityStatue.BlockFragment fragment = tileEntityStatue.getStatueBlock();
            BufferedImage patternImage = StatueTextureHandler.getTexture(fragment.getBlock(), fragment.getMetadata());

            if (patternImage != null)
            {
                ResourceLocation entityResourceLocation = YGCConfig.doStatueTextureMerge ? StatueTextureHandler.getTexture(entity) : null;
                if (entityResourceLocation != null)
                {
                    ResourceLocation textureColorized = new ResourceLocation(entityResourceLocation + "|YGC_COL_" + fragment.getBlock() + "_" + fragment.getMetadata());

                    if (Minecraft.getMinecraft().getTextureManager().getTexture(textureColorized) == null)
                    {
                        ModifiedTexture modifiedTexture = new ModifiedTexture(entityResourceLocation, new IvTexturePatternColorizer(patternImage, YeGamolChattels.logger), YeGamolChattels.logger);
                        Minecraft.getMinecraft().getTextureManager().loadTexture(textureColorized, modifiedTexture);
                    }

                    return textureColorized;
                }
                else
                {
                    ResourceLocation patternResource = new ResourceLocation(YeGamolChattels.MODID, "Block|" + Block.blockRegistry.getNameForObject(fragment.getBlock()) + "_" + fragment.getMetadata());

                    if (Minecraft.getMinecraft().getTextureManager().getTexture(patternResource) == null)
                        Minecraft.getMinecraft().getTextureManager().loadTexture(patternResource, new PreBufferedTexture(patternImage));

                    return patternResource;
                }
            }
        }

        return null;
    }
}
