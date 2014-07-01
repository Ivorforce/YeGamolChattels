/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;


import ivorius.ivtoolkit.blocks.IvMultiBlockRenderHelper;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityStatue;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererStatue extends TileEntitySpecialRenderer
{
    public ResourceLocation[] textureOverrides;
    public RenderEngineOverride renderEngineOverride;

    public TileEntityRendererStatue()
    {
        this.textureOverrides = new ResourceLocation[3];
        this.textureOverrides[0] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "statuePlanksEntity.png");
        this.textureOverrides[1] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "statueStoneEntity.png");
        this.textureOverrides[2] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "statueGoldEntity.png");

        renderEngineOverride = new RenderEngineOverride();
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

    public void renderTileEntityStatueAt(TileEntityStatue tileentitystatue, double d, double d1, double d2, float f)
    {
        if (tileentitystatue.isParent())
        {
            GL11.glPushMatrix();
            GL11.glTranslated(0.0, -tileentitystatue.centerCoordsSize[1], 0.0);
            IvMultiBlockRenderHelper.transformFor(tileentitystatue, d, d1, d2);

            Entity entity = tileentitystatue.getStatueEntity();
            entity.setWorld(tileentitystatue.getWorldObj());

            int statusBarLength = BossStatus.statusBarTime; //Don't render boss health

            renderEngineOverride.textureOverride = textureOverrides[tileentitystatue.getTextureType()];
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
}
