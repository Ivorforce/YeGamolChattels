/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;


import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityGrandfatherClock;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererGrandfatherClock extends TileEntitySpecialRenderer
{
    ModelGrandfatherClock clockModel;

    public ResourceLocation texture;

    public TileEntityRendererGrandfatherClock()
    {
        this.clockModel = new ModelGrandfatherClock();

        this.texture = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "grandfatherClockTexture.png");
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderTileEntityStatueAt((TileEntityGrandfatherClock) tileentity, d, d1, d2, f);
    }

    public void renderTileEntityStatueAt(TileEntityGrandfatherClock tileEntity, double d, double d1, double d2, float f)
    {
        int meta = tileEntity.getBlockMetadata();

        if ((meta & 1) == 0)
        {
            Tessellator tessellator = Tessellator.instance;

            bindTexture(texture);

            GL11.glPushMatrix();
            GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5f, (float) d2 + 0.5F);
            GL11.glRotatef(-90.0f * (meta >> 1) + 180.0f, 0.0f, 1.0f, 0.0f);
            GL11.glPushMatrix();
            GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);

            Entity emptyEntity = new EntityArrow(tileEntity.getWorldObj());
            emptyEntity.ticksExisted = (int) tileEntity.pendulumTimeShown;
            emptyEntity.timeUntilPortal = (int) tileEntity.clockTimeShown;

            clockModel.render(emptyEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

            GL11.glPopMatrix();

            GL11.glPopMatrix();
        }
    }
}
