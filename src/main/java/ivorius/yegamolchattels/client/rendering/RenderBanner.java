/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;

import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.entities.EntityBanner;
import ivorius.yegamolchattels.entities.EntityFlag;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBanner extends Render
{
    public ResourceLocation textureBannerSmall;
    public ResourceLocation textureBannerLarge;

    public RenderBanner()
    {
        textureBannerSmall = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "bannersSmall.png");
        textureBannerLarge = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "bannersLarge.png");
    }

    public void doRenderBanner(EntityBanner entitybannersmall, double d, double d1, double d2, float f, float f1)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d, (float) d1, (float) d2);
        GL11.glRotatef(f, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        bindEntityTexture(entitybannersmall);
        float f2 = 0.0625F;
        GL11.glScalef(f2, f2, f2);
        int bannerWidth = entitybannersmall.getWidthPixels();
        int bannerHeight = entitybannersmall.getHeightPixels();
        renderBanner(entitybannersmall, bannerWidth, bannerHeight, (entitybannersmall.getColor() % 4) * bannerWidth, (entitybannersmall.getColor() >> 2) * bannerHeight, f1);
        GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glPopMatrix();
    }

    private void renderBanner(EntityBanner entity, int bannerWidth, int bannerHeight, int texShiftX, int texShiftY, float t)
    {
        Tessellator tessellator = Tessellator.instance;

        float ticks = entity.ticksExisted + t;
        float wind = EntityFlag.getInterpolatedWind(entity.wind, entity.simWind, t);

        double xShift = (-bannerWidth) / 2.0F;
        double yShift = (-bannerHeight) / 2.0F;

        double segWidth = 1.0;
        double segHeight = 1.0;

        tessellator.startDrawingQuads();
        for (int voxX = 0; voxX < bannerWidth; voxX++)
        {
            for (int voxY = 0; voxY < bannerHeight; voxY++)
            {
                double thickness = renderManager.options.fancyGraphics ? 0.7f : 0.0f;
                if ((bannerHeight - voxY) < 2 + bannerHeight / 16)
                    thickness *= 2.0f;
                double zShift = -thickness * 0.5f + 0.5f;

                double voxX1 = voxX + 0.999;
                double voxY1 = voxY + 0.999;

                double minX = xShift + voxX * 1.0;
                double minY = yShift + voxY * 1.0;
                double texMinX = ((texShiftX + bannerWidth) - (voxX + 0.001) * 1) / (bannerWidth * 4.0);
                double texMaxX = ((texShiftX + bannerWidth) - voxX1 * 1) / (bannerWidth * 4.0);
                double texMinY = ((texShiftY + bannerHeight) - (voxY + 0.001) * 1) / (bannerHeight * 4.0);
                double texMaxY = ((texShiftY + bannerHeight) - voxY1 * 1) / (bannerHeight * 4.0);

                double waveEffect = (bannerHeight - voxY - 4) * 0.015f * wind;
                double waveEffect1 = (bannerHeight - voxY1 - 4) * 0.015f * wind;

                double z = waveEffect > 0.0 ? (MathHelper.sin((voxY * 0.04f + ticks * 0.06f) * (float) Math.PI) * waveEffect) : 0.0;
                double z1 = waveEffect1 > 0.0 ? (MathHelper.sin(((float) voxY1 * 0.04f + ticks * 0.06f) * (float) Math.PI) * waveEffect1) : 0.0;

                if (!renderManager.options.fancyGraphics)
                {
                    // To prevent clipping into the wall
                    if (z > 0.3)
                        z = 0.3;
                    if (z1 > 0.3)
                        z1 = 0.3;
                }

                setLight(entity, (float) minX + 0.5f, (float) minY + 0.5f);
                renderSegment(minX, minY, zShift + z, zShift + z1, segWidth, segHeight, thickness, texMinX, texMinY, texMaxX, texMaxY);
            }
        }

        tessellator.draw();
    }

    private void setLight(EntityHanging entity, float par2, float par3)
    {
        int i = MathHelper.floor_double(entity.posX);
        int j = MathHelper.floor_double(entity.posY + (double) (par3 / 16.0F));
        int k = MathHelper.floor_double(entity.posZ);

        switch (entity.hangingDirection)
        {
            case 2:
                i = MathHelper.floor_double(entity.posX + (double) (par2 / 16.0F));
                break;
            case 1:
                k = MathHelper.floor_double(entity.posZ - (double) (par2 / 16.0F));
                break;
            case 0:
                i = MathHelper.floor_double(entity.posX - (double) (par2 / 16.0F));
                break;
            case 3:
                k = MathHelper.floor_double(entity.posZ + (double) (par2 / 16.0F));
                break;
        }

        int l = this.renderManager.worldObj.getLightBrightnessForSkyBlocks(i, j, k, 0);

        int i1 = l % 65536;
        int j1 = l / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) i1, (float) j1);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    @Override
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1)
    {
        doRenderBanner((EntityBanner) entity, d, d1, d2, f, f1);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        if (entity instanceof EntityBanner)
        {
            EntityBanner banner = (EntityBanner) entity;

            if (banner.getSize() == 2)
                return textureBannerLarge;
        }

        return textureBannerSmall;
    }

    public static void renderSegment(double x, double y, double z, double z1, double sizeX, double sizeY, double sizeZ, double texX, double texY, double texX1, double texY1)
    {
        Tessellator tessellator = Tessellator.instance;

        int zShift = -1;
        tessellator.setNormal(0, 0, zShift);
        tessellator.addVertexWithUV(x + sizepos + zShift * sizeZ, texX1, texY);
        tessellator.addVertexWithUV(pos + zShift * sizeZ, texX, texY);
        tessellator.addVertexWithUV(x, y + sizeY, z1 + zShift * sizeZ, texX, texY1);
        tessellator.addVertexWithUV(x + sizeX, y + sizeY, z1 + zShift * sizeZ, texX1, texY1);

        zShift = 1;
        tessellator.setNormal(0, 0, zShift);
        tessellator.addVertexWithUV(x, y + sizeY, z1 + zShift * sizeZ, texX, texY1);
        tessellator.addVertexWithUV(pos + zShift * sizeZ, texX, texY);
        tessellator.addVertexWithUV(x + sizepos + zShift * sizeZ, texX1, texY);
        tessellator.addVertexWithUV(x + sizeX, y + sizeY, z1 + zShift * sizeZ, texX1, texY1);

        if (sizeZ > 0.0f)
        {
            tessellator.setNormal(0, 1, 0);
            tessellator.addVertexWithUV(x + sizeX, y + sizeY, z1 - sizeZ, texX1, texY);
            tessellator.addVertexWithUV(x, y + sizeY, z1 - sizeZ, texX, texY);
            tessellator.addVertexWithUV(x, y + sizeY, z1 + sizeZ, texX, texY1);
            tessellator.addVertexWithUV(x + sizeX, y + sizeY, z1 + sizeZ, texX1, texY1);

            tessellator.setNormal(0, -1, 0);
            tessellator.addVertexWithUV(pos + sizeZ, texX, texY1);
            tessellator.addVertexWithUV(pos - sizeZ, texX, texY);
            tessellator.addVertexWithUV(x + sizepos - sizeZ, texX1, texY);
            tessellator.addVertexWithUV(x + sizepos + sizeZ, texX1, texY1);

            tessellator.setNormal(-1, 0, 0);
            tessellator.addVertexWithUV(x, y + sizeY, z1 + sizeZ, texX1, texY1);
            tessellator.addVertexWithUV(x, y + sizeY, z1 - sizeZ, texX, texY1);
            tessellator.addVertexWithUV(pos - sizeZ, texX, texY);
            tessellator.addVertexWithUV(pos + sizeZ, texX1, texY);

            tessellator.setNormal(1, 0, 0);
            tessellator.addVertexWithUV(x + sizepos - sizeZ, texX, texY);
            tessellator.addVertexWithUV(x + sizeX, y + sizeY, z1 - sizeZ, texX, texY1);
            tessellator.addVertexWithUV(x + sizeX, y + sizeY, z1 + sizeZ, texX1, texY1);
            tessellator.addVertexWithUV(x + sizepos + sizeZ, texX1, texY);
        }
    }
}
