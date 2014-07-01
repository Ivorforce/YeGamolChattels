/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;

import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityGrindstone;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererGrindstone extends TileEntitySpecialRenderer
{
    public ModelBase modelBase;
    public ModelBase modelStone;

    public ResourceLocation textureBase;
    public ResourceLocation textureStone;

    public TileEntityRendererGrindstone()
    {
        this.modelBase = new ModelGrindstoneBase();
        this.modelStone = new ModelGrindstoneStone();

        textureBase = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "grindstoneBase.png");
        textureStone = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "grindstoneStone.png");
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderTileEntityGrindstoneAt((TileEntityGrindstone) tileentity, d, d1, d2, f);
    }

    public void renderTileEntityGrindstoneAt(TileEntityGrindstone tileEntity, double d, double d1, double d2, float f)
    {
        int meta = tileEntity.getBlockMetadata();

        Tessellator tessellator = Tessellator.instance;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5f, (float) d2 + 0.5F);
        GL11.glRotatef(-90.0f * (meta >> 1) + 180.0f, 0.0f, 1.0f, 0.0f);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);

        Entity emptyEntity = new EntityArrow(tileEntity.getWorldObj());

        bindTexture(textureBase);
        emptyEntity.rotationYaw = tileEntity.crankRotationVisual + (tileEntity.crankRotationTime > 0 ? f * 0.4f : 0.0f);
        modelBase.render(emptyEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        if (tileEntity.grindstoneHealth > 0)
        {
            float grindstoneRotation = (tileEntity.grindstoneRotationVisual + tileEntity.grindstoneRotationSpeed * f) * 0.0009f;
            emptyEntity.rotationYaw = grindstoneRotation;

            float scale = (float) tileEntity.grindstoneHealth / ((float) TileEntityGrindstone.maxGrindstoneHealth) * 0.8f + 0.2f;
            GL11.glScalef(1.0f, scale, scale);
            GL11.glTranslatef(0.0f, 0.8f / scale - 0.8f, 0.0f);
            bindTexture(textureStone);
            modelStone.render(emptyEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        }

        GL11.glPopMatrix();

        GL11.glPopMatrix();
    }
}
