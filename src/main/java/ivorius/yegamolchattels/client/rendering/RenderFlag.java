/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;

import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.entities.EntityFlag;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderFlag extends Render
{
    ResourceLocation poleTexture;
    ResourceLocation clothTexture;

    public RenderFlag()
    {
        poleTexture = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "flagPole.png");
        clothTexture = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "flagCloth.png");
    }

    public void renderFlag(EntityFlag entityflag, double d, double d1, double d2, float f, float f1)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d + 0.5F, (float) d1, (float) d2 + 0.5F);
        GL11.glRotatef(f, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        doRenderFlag(entityflag, 180, f1);
        GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glPopMatrix();
    }

    private void doRenderFlag(EntityFlag entity, float rY, float t)
    {
        Tessellator tessellator = Tessellator.instance;

        float ticks = entity.ticksExisted + t;
        float wind = (EntityFlag.getInterpolatedWind(entity.wind, entity.simWind, t)) * 0.96f + 0.04f;

        float sizeY = entity.getFlagHeight() / 16.0F;

        double tX = ((entity.getColor() % 4) + 0.001) / 4.0;
        double tX1 = ((entity.getColor() % 4) + 0.999) / 4.0;
        double tY = ((entity.getColor() >> 2) + 0.001) / 4.0;
        double tY1 = ((entity.getColor() >> 2) + 0.999) / 4.0;

        double d9 = -0.5D;
        double d10 = 0.5D;
        double d11 = -0.5D;
        double d12 = 0.5D;
        float poleWidth = 0.0625f;

        bindTexture(poleTexture);
        for (int y = 0; y < sizeY; y++)
        {
            setLight(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY) + y, MathHelper.floor_double(entity.posZ));

            float texY0 = 1.0f - y / sizeY;
            float texY1 = 1.0f - (y + 1) / sizeY;

            tessellator.startDrawingQuads();

            tessellator.setNormal(-1, 0, 0);
            tessellator.addVertexWithUV(-poleWidth, y + 1, d11, 0.0f, texY1);
            tessellator.addVertexWithUV(-poleWidth, y, d11, 0.0f, texY0);
            tessellator.addVertexWithUV(-poleWidth, y, d12, 1.0f, texY0);
            tessellator.addVertexWithUV(-poleWidth, y + 1, d12, 1.0f, texY1);

            tessellator.setNormal(1, 0, 0);
            tessellator.addVertexWithUV(poleWidth, y + 1, d12, 0.0f, texY1);
            tessellator.addVertexWithUV(poleWidth, y, d12, 0.0f, texY0);
            tessellator.addVertexWithUV(poleWidth, y, d11, 1.0f, texY0);
            tessellator.addVertexWithUV(poleWidth, y + 1, d11, 1.0f, texY1);

            tessellator.setNormal(0, 0, 1);
            tessellator.addVertexWithUV(d9, y + 1, poleWidth, 0.0f, texY1);
            tessellator.addVertexWithUV(d9, y, poleWidth, 0.0f, texY0);
            tessellator.addVertexWithUV(d10, y, poleWidth, 1.0f, texY0);
            tessellator.addVertexWithUV(d10, y + 1, poleWidth, 1.0f, texY1);

            tessellator.setNormal(0, 0, -1);
            tessellator.addVertexWithUV(d10, y + 1, -poleWidth, 0.0f, texY1);
            tessellator.addVertexWithUV(d10, y, -poleWidth, 0.0f, texY0);
            tessellator.addVertexWithUV(d9, y, -poleWidth, 1.0f, texY0);
            tessellator.addVertexWithUV(d9, y + 1, -poleWidth, 1.0f, texY1);

            tessellator.draw();
        }

        GL11.glPushMatrix();

        GL11.glRotatef(rY, 0, 1, 0);

        tessellator.startDrawingQuads();

        int d = renderManager.options.fancyGraphics ? 32 : 8;

        float thickness = renderManager.options.fancyGraphics ? (0.015f + (sizeY - 1) * 0.004f) : 0.0f;

        float xMul = MathHelper.sin(wind * 3.1415926f * 0.5f);
        float yMul = MathHelper.cos(wind * 3.1415926f * 0.5f);

        GL11.glTranslatef(poleWidth, sizeY * 0.45f, 0.0f);
        GL11.glScaled(0.7f, 0.7f, 1.0f);

        double folding = (1.0 - wind * 4.0);
        if (folding < 0.0)
            folding = 0.0;

        bindTexture(clothTexture);
        setLight(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY + sizeY), MathHelper.floor_double(entity.posZ));
        for (int xSeg = 0; xSeg < d; xSeg++)
        {
            double ratio = (double) (xSeg + 0.001) / (double) d;
            double ratio1 = ((double) xSeg + 0.999) / (double) d;

            double x = sizeY * ratio;
            double x1 = sizeY * ratio1;

            double z = MathHelper.sin((float) ((ratio * 2.5 - ticks * 0.06) * Math.PI)) * sizeY * ratio * 0.05 * wind;
            z += MathHelper.sin((float) (ratio * 25.5)) * sizeY * ratio * 0.06 * folding; // Folding
            double z1 = MathHelper.sin((float) ((ratio1 * 2.5 - ticks * 0.06) * Math.PI)) * sizeY * ratio1 * 0.05 * wind;
            z1 += MathHelper.sin((float) (ratio1 * 25.5)) * sizeY * ratio1 * 0.06 * folding; // Folding
            double texX0 = tX + (tX1 - tX) * ratio;
            double texX1 = tX + (tX1 - tX) * ratio1;

            for (int ySeg = 0; ySeg < d; ySeg++)
            {
                double ratioY = ySeg / (double) d;
                double ratioY1 = (ySeg + 1.0) / d;

                double y = ratioY * sizeY - x * yMul;
                double y1 = ratioY * sizeY - x1 * yMul;

                double texY0 = tY + (tY1 - tY) * ratioY;
                double texY1 = tY + (tY1 - tY) * ratioY1;

                RenderFlag.renderSegment(x * xMul, y, y1, z, z1, 1.0f / d * sizeY * xMul, 1.0f / d * sizeY, thickness, texX0, texY0, texX1, texY1);
            }

            z = z1;
        }

        tessellator.draw();

        GL11.glPopMatrix();
    }

    private void setLight(int x, int y, int z)
    {
        int var7 = this.renderManager.worldObj.getLightBrightnessForSkyBlocks(x, y, z, 0);
        int var8 = var7 % 65536;
        int var9 = var7 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var8, var9);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    public static void renderSegment(double x, double y, double y1, double z, double z1, double sizeX, double sizeY, double sizeZ, double texX, double texY, double texX1, double texY1)
    {
        Tessellator tessellator = Tessellator.instance;

        double xMin = x;
        double xMax = x + sizeX;

        int zShift = -1;
        tessellator.setNormal(0, 0, zShift);
        tessellator.addVertexWithUV(xMax, y1, z1 + zShift * sizeZ, texX1, texY);
        tessellator.addVertexWithUV(xMin, y, z + zShift * sizeZ, texX, texY);
        tessellator.addVertexWithUV(xMin, y + sizeY, z + zShift * sizeZ, texX, texY1);
        tessellator.addVertexWithUV(xMax, y1 + sizeY, z1 + zShift * sizeZ, texX1, texY1);

        zShift = 1;
        tessellator.setNormal(0, 0, zShift);
        tessellator.addVertexWithUV(xMin, y + sizeY, z + zShift * sizeZ, texX, texY1);
        tessellator.addVertexWithUV(xMin, y, z + zShift * sizeZ, texX, texY);
        tessellator.addVertexWithUV(xMax, y1, z1 + zShift * sizeZ, texX1, texY);
        tessellator.addVertexWithUV(xMax, y1 + sizeY, z1 + zShift * sizeZ, texX1, texY1);

        if (sizeZ > 0.0f)
        {
            tessellator.setNormal(0, 1, 0);
            tessellator.addVertexWithUV(xMax, y1 + sizeY, z1 - sizeZ, texX1, texY);
            tessellator.addVertexWithUV(xMin, y + sizeY, z - sizeZ, texX, texY);
            tessellator.addVertexWithUV(xMin, y + sizeY, z + sizeZ, texX, texY1);
            tessellator.addVertexWithUV(xMax, y1 + sizeY, z1 + sizeZ, texX1, texY1);

            tessellator.setNormal(0, -1, 0);
            tessellator.addVertexWithUV(xMin, y, z + sizeZ, texX, texY1);
            tessellator.addVertexWithUV(xMin, y, z - sizeZ, texX, texY);
            tessellator.addVertexWithUV(xMax, y1, z1 - sizeZ, texX1, texY);
            tessellator.addVertexWithUV(xMax, y1, z1 + sizeZ, texX1, texY1);

            tessellator.setNormal(-1, 0, 0);
            tessellator.addVertexWithUV(xMin, y + sizeY, z + sizeZ, texX1, texY1);
            tessellator.addVertexWithUV(xMin, y + sizeY, z - sizeZ, texX, texY1);
            tessellator.addVertexWithUV(xMin, y, z - sizeZ, texX, texY);
            tessellator.addVertexWithUV(xMin, y, z + sizeZ, texX1, texY);

            tessellator.setNormal(1, 0, 0);
            tessellator.addVertexWithUV(xMax, y1, z1 - sizeZ, texX, texY);
            tessellator.addVertexWithUV(xMax, y1 + sizeY, z1 - sizeZ, texX, texY1);
            tessellator.addVertexWithUV(xMax, y1 + sizeY, z1 + sizeZ, texX1, texY1);
            tessellator.addVertexWithUV(xMax, y1, z1 + sizeZ, texX1, texY);
        }
    }

    @Override
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1)
    {
        renderFlag((EntityFlag) entity, d, d1, d2, f, f1);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return poleTexture;
    }
}
