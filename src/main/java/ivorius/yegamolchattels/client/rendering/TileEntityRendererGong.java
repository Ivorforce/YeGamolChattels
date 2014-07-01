/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;

import ivorius.ivtoolkit.blocks.IvMultiBlockRenderHelper;
import ivorius.ivtoolkit.rendering.IvRenderHelper;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityGong;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererGong extends TileEntitySpecialRenderer
{
    public ModelBase gongModels[];
    public ResourceLocation[] gongTextures;

    public TileEntityRendererGong()
    {
        gongModels = new ModelBase[3];
        gongModels[0] = new ModelGongSmall();
        gongModels[1] = new ModelGongMedium();
        gongModels[2] = new ModelGongLarge();

        gongTextures = new ResourceLocation[3];
        gongTextures[0] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "gongSmall.png");
        gongTextures[1] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "gongMedium.png");
        gongTextures[2] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "gongLarge.png");
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderTileEntityGongAt((TileEntityGong) tileentity, d, d1, d2, f);
    }

    public void renderTileEntityGongAt(TileEntityGong tileEntity, double d, double d1, double d2, float f)
    {
        if (tileEntity.isParent())
        {
            int gongSize = tileEntity.getBlockMetadata();

            GL11.glPushMatrix();
            IvMultiBlockRenderHelper.transformFor(tileEntity, d, d1, d2);
            GL11.glPushMatrix();
            GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);

            Entity emptyEntity = new EntityArrow(tileEntity.getWorldObj());

            if (tileEntity.vibrationStrength > 0)
            {
                double vibX = (tileEntity.getWorldObj().rand.nextDouble() - 0.5) * 0.00005 * tileEntity.vibrationStrength;
                double vibY = (tileEntity.getWorldObj().rand.nextDouble() - 0.5) * 0.00005 * tileEntity.vibrationStrength;
                double vibZ = (tileEntity.getWorldObj().rand.nextDouble() - 0.5) * 0.00005 * tileEntity.vibrationStrength;
                GL11.glTranslated(vibX, vibY, vibZ);
                emptyEntity.rotationYaw = (float) ((tileEntity.getWorldObj().rand.nextDouble() - 0.5) * 0.0004 * tileEntity.vibrationStrength);
            }

            if (gongSize == 0)
            {
                bindTexture(gongTextures[0]);
                GL11.glTranslated(0.0f, -1.0f, 0.0f);
                gongModels[0].render(emptyEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
            }
            if (gongSize == 1)
            {
                bindTexture(gongTextures[1]);
                GL11.glTranslated(-0.5f, -0.5f, 0.0f);
                gongModels[1].render(emptyEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
            }
            if (gongSize == 2)
            {
                bindTexture(gongTextures[2]);
                gongModels[2].render(emptyEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
            }

            GL11.glPopMatrix();

            if (tileEntity.madnessTimer > 0)
            {
                int r = (int) (MathHelper.sin(tileEntity.madnessTimer * 0.01123f) * 128.0f + 128.0f);
                int g = (int) (MathHelper.sin(tileEntity.madnessTimer * 0.014234f) * 128.0f + 128.0f);
                int b = (int) (MathHelper.sin(tileEntity.madnessTimer * 0.016546f) * 128.0f + 128.0f);
                IvRenderHelper.renderLights(MathHelper.sin(tileEntity.madnessTimer * 0.1f) * 100.0f, (r << 8 + g) << 8 + b, 1.0f, 20);
            }

            GL11.glPopMatrix();

//			if (Minecraft.getMinecraft().thePlayer.isSneaking())
//				IvRaytracer.drawStandardOutlinesFromTileEntity(tileEntity.getGongDistanceRaytraceables(), d, d1, d2, tileEntity);
        }
    }
}
