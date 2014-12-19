/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;

import ivorius.ivtoolkit.blocks.IvMultiBlockRenderHelper;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.ivtoolkit.raytracing.IvRaytraceableAxisAlignedBox;
import ivorius.ivtoolkit.raytracing.IvRaytraceableObject;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.BlockItemShelf;
import ivorius.yegamolchattels.blocks.EntityShelfInfo;
import ivorius.yegamolchattels.blocks.TileEntityItemShelfModel0;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class TileEntityRendererItemShelf extends TileEntitySpecialRenderer
{
    public ResourceLocation[][] shelfTextures = new ResourceLocation[BlockItemShelf.shelfTypes][];
    public ModelBase[][] shelfModels = new ModelBase[BlockItemShelf.shelfTypes][];

    public ResourceLocation[] otherDimensionTextures;

    public TileEntityRendererItemShelf()
    {
        shelfModels[0] = new ModelBase[]{new ModelJamiensShelf()};
        shelfTextures[0] = new ResourceLocation[]{new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "jamiensShelf.png")};
        shelfModels[1] = new ModelBase[]{new ModelShelfWall(), new ModelShelfWall1()};
        shelfTextures[1] = new ResourceLocation[]{new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "shelfWall.png"), new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "shelfWall1.png")};
        shelfModels[2] = new ModelBase[]{new ModelWardrobe()};
        shelfTextures[2] = new ResourceLocation[]{new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "wardrobe.png")};

        otherDimensionTextures = new ResourceLocation[5];
        for (int i = 0; i < otherDimensionTextures.length; i++)
            otherDimensionTextures[i] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "narnia" + i + ".png");
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderTileEntityItemShelfAt((TileEntityItemShelfModel0) tileentity, d, d1, d2, f);
    }

    public void renderTileEntityItemShelfAt(TileEntityItemShelfModel0 tileEntity, double d, double d1, double d2, float f)
    {
        if (tileEntity.isParent())
        {
            int shelfType = tileEntity.getShelfType();

            GL11.glPushMatrix();
            IvMultiBlockRenderHelper.transformFor(tileEntity, d, d1, d2);
            GL11.glPushMatrix();
            GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);

            float[] triggerValues = tileEntity.getTriggerValues(f);
            Entity emptyEntity = new EntityShelfInfo(tileEntity.getWorldObj(), triggerValues);
            ModelBase model = shelfModels[shelfType][tileEntity.randomSeed % shelfModels[shelfType].length];
            ResourceLocation texture = shelfTextures[shelfType][tileEntity.randomSeed % shelfTextures[shelfType].length];

            if (shelfType == 0)
                GL11.glTranslatef(-0.5f, -1.0f, 0.0f);
            if (shelfType == 1)
                GL11.glTranslatef(-0.0f, -1.0f, 0.0f);
            if (shelfType == 2)
                GL11.glTranslatef(-0.0f, -0.5f, 0.0f);

            GL11.glDisable(GL11.GL_CULL_FACE);

            this.bindTexture(texture);
            model.render(emptyEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

            GL11.glPopMatrix();

            if (tileEntity.narniaProgress > 0.0f)
            {
                Tessellator tessellator = Tessellator.instance;

                Vector3f rotatedPos = tileEntity.getRotatedVector(new Vector3f((float) d, (float) d1, (float) d2));
                double xDist = rotatedPos.getX() / (Math.abs(rotatedPos.getZ()) + 0.5);

                if (tileEntity.narniaProgress < 1.0f)
                {
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, tileEntity.narniaProgress);
                }

                for (int i = 0; i < otherDimensionTextures.length; i++)
                {
                    bindTexture(otherDimensionTextures[i]);
                    double z = -i * 0.01 - 0.32;
                    double shift = xDist * i * 1.0;

                    tessellator.startDrawingQuads();
                    tessellator.addVertexWithUV(-0.5, 0.7, z, 0.0 + shift, 0.0);
                    tessellator.addVertexWithUV(0.5, 0.7, z, 1.0 + shift, 0.0);
                    tessellator.addVertexWithUV(0.5, -0.3, z, 1.0 + shift, 1.0);
                    tessellator.addVertexWithUV(-0.5, -0.3, z, 0.0 + shift, 1.0);
                    tessellator.draw();
                }

                if (tileEntity.narniaProgress < 1.0f)
                {
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glColor3f(1.0f, 1.0f, 1.0f);
                }
            }

            GL11.glPopMatrix();

//			if (Minecraft.getMinecraft().thePlayer.isSneaking())
//				IvRaytracer.drawStandardOutlinesFromTileEntity(tileEntity.getRaytraceableObjects(f), d, d1, d2, tileEntity);

//            if (!RenderManager.instance.options.fancyGraphics)
//                GL11.glDisable(GL11.GL_CULL_FACE);
            RenderItem.renderInFrame = true;

            List<IvRaytraceableObject> slots = tileEntity.getItemSlotBoxes(f);
            for (IvRaytraceableObject object : slots)
            {
                int slotNumber = tileEntity.getSlotNumber(object);

                if (slotNumber >= 0)
                {
                    ItemStack item = tileEntity.getStackInSlot(slotNumber);

                    if (item != null)
                    {
                        if (tileEntity.getShelfType() == TileEntityItemShelfModel0.shelfWardrobe && slotNumber > 7)
                        {
                            if (triggerValues[0] > 0.0f)
                                drawItemInBox((IvRaytraceableAxisAlignedBox) object, item, tileEntity, d, d1, d2, 3.7f, 90.0f);
                        }
                        else if (tileEntity.getShelfType() == TileEntityItemShelfModel0.shelfWardrobe && slotNumber < 4)
                        {
                            if (triggerValues[1] > 0.0f)
                                drawItemInBox((IvRaytraceableAxisAlignedBox) object, item, tileEntity, d, d1, d2);
                        }
                        else if (tileEntity.getShelfType() == TileEntityItemShelfModel0.shelfWardrobe && slotNumber > 3 && slotNumber < 8)
                        {
                            if (triggerValues[2] > 0.0f)
                                drawItemInBox((IvRaytraceableAxisAlignedBox) object, item, tileEntity, d, d1, d2);
                        }
                        else
                            drawItemInBox((IvRaytraceableAxisAlignedBox) object, item, tileEntity, d, d1, d2);
                    }
                }
            }

            RenderItem.renderInFrame = false;
//            if (!RenderManager.instance.options.fancyGraphics)
//                GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }

    public static void drawItemInBox(IvRaytraceableAxisAlignedBox box, ItemStack item, IvTileEntityMultiBlock tileEntity, double d, double d1, double d2)
    {
        drawItemInBox(box, item, tileEntity, d, d1, d2, 1.0f, 0.0f);
    }

    public static void drawItemInBox(IvRaytraceableAxisAlignedBox box, ItemStack item, IvTileEntityMultiBlock tileEntity, double d, double d1, double d2, float itemScale, float rotY)
    {
        double playerDistSQ = d * d + d1 * d1 + d2 * d2;
        double smallestLength = Math.min(box.getWidth(), Math.min(box.getHeight(), box.getDepth()));

        if (playerDistSQ < smallestLength * smallestLength * 100 * 100)
        {
            EntityItem itemEntity = new EntityItem(tileEntity.getWorldObj(), 0.0D, 0.0D, 0.0D, item);
            itemEntity.hoverStart = 0.0F;

            GL11.glPushMatrix();
            GL11.glTranslatef((float) d - tileEntity.xCoord, (float) d1 - tileEntity.yCoord, (float) d2 - tileEntity.zCoord);
            GL11.glTranslated(box.getX() + box.getWidth() / 2, box.getY() + box.getHeight() / 2, box.getZ() + box.getDepth() / 2);
            GL11.glRotatef(-90.0f * tileEntity.direction + 180.0f + rotY, 0.0f, 1.0f, 0.0f);
            GL11.glScaled(smallestLength * 1.9 * itemScale, smallestLength * 1.9 * itemScale, smallestLength * 1.9 * itemScale);
            GL11.glTranslatef(0.0f, -0.17f, 0.0f);

            RenderManager.instance.renderEntityWithPosYaw(itemEntity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

            GL11.glPopMatrix();
        }
    }
}
