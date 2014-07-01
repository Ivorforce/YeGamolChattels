/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;

import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntitySnowGlobe;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class TileEntityRendererSnowGlobe extends TileEntitySpecialRenderer
{
    public ModelBase modelRealityGlobe;
    public ResourceLocation realityGlobeTexture;
    public ResourceLocation realityGlobeTextureClear;

    private RenderBlocks blockRenderer;

    public static IvBlockCollection defaultGlobe;

    public static int sizeX = 10;
    public static int sizeY = 9;
    public static int sizeZ = 10;

    public TileEntityRendererSnowGlobe()
    {
        this.modelRealityGlobe = new ModelRealityGlobe();
        realityGlobeTexture = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "realityGlobeGlass.png");
        realityGlobeTextureClear = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "realityGlobeClear.png");

        //TODO
//        IvBlockMapper mapper = new IvBlockMapper();
//        mapper.readFromResource(new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathOther + "defaultGlobe.nbt"));
//
//        defaultGlobe = mapper.readCollectionFromMapping(sizeX * 2 + 1, sizeY * 2 + 1);
    }

    @Override
    public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8)
    {
        TileEntitySnowGlobe realityGlobe = ((TileEntitySnowGlobe) var1);

        double playerDistSQ = var2 * var2 + var4 * var4 + var6 * var6;
        Tessellator tessellator = Tessellator.instance;

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        GL11.glPushMatrix();
        GL11.glTranslated(var2 + 0.5, var4 + 0.5, var6 + 0.5);

        float rumbling = realityGlobe.getRumbling();
        float rumble = rumbling > 0.0f ? (-var1.getWorldObj().rand.nextFloat() * rumbling) : 0.0f;

        if (playerDistSQ < 4 * 4)
        {
            if (realityGlobe.glCallListIndex < 0)
                constructCallList(realityGlobe, this.blockRenderer);
            else if (realityGlobe.needsVisualUpdate)
            {
                updateCallList(realityGlobe, blockRenderer);
                realityGlobe.needsVisualUpdate = false;
            }

            double sizePX = 1.0 / (double) (sizeX * 2 + 1);
            double sizePY = 1.0 / (double) (sizeX * 2 + 1);
            double sizePZ = 1.0 / (double) (sizeX * 2 + 1);

            if (realityGlobe.glCallListIndex >= 0)
            {
                GL11.glPushMatrix();
                setupWorldTransform(rumble);

                Tessellator var10 = Tessellator.instance;
                this.bindTexture(TextureMap.locationBlocksTexture);
                RenderHelper.disableStandardItemLighting();
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_CULL_FACE);

                if (Minecraft.isAmbientOcclusionEnabled())
                {
                    GL11.glShadeModel(GL11.GL_SMOOTH);
                }
                else
                {
                    GL11.glShadeModel(GL11.GL_FLAT);
                }

                if (realityGlobe.glCallListIndex >= 0)
                {
                    GL11.glCallList(realityGlobe.glCallListIndex);

                    if (!realityGlobe.displaysDefaultHouse())
                    {
                        setupWorldTransform(rumble);
                        GL11.glCallList(realityGlobe.glCallListIndex);
                    }
                }

                RenderHelper.enableStandardItemLighting();

                GL11.glPopMatrix();
            }

            if (!realityGlobe.displaysDefaultHouse())
            {
                if (playerDistSQ < 4 * 4)
                {
                    AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double) var1.xCoord - sizeX, (double) var1.yCoord - sizeY, (double) var1.zCoord - sizeZ, (double) var1.xCoord + 1 + sizeX, (double) var1.yCoord + 1 + sizeY, (double) var1.zCoord + 1 + sizeZ);
                    List entities = var1.getWorldObj().getEntitiesWithinAABBExcludingEntity(null, bb);

                    if (entities.size() > 0)
                    {
                        GL11.glPushMatrix();
                        setupWorldTransform(rumble);
                        GL11.glTranslated(-var1.xCoord - 0.5, -var1.yCoord - 0.5, -var1.zCoord - 0.5);

                        for (Object obj : entities)
                        {
                            Entity entity = (Entity) obj;

                            AxisAlignedBB entityBB = entity.boundingBox;
                            if (entityBB.minX > bb.minX && entityBB.maxX < bb.maxX && entityBB.minY > bb.minY && entityBB.maxY < bb.maxY && entityBB.minZ > bb.minZ && entityBB.maxZ < bb.maxZ)
                            {
                                double rX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) var8;
                                double rY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) var8;
                                double rZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) var8;
                                float rYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * var8;

                                RenderManager.instance.renderEntityWithPosYaw(entity, rX, rY, rZ, rYaw, var8);
                            }
                        }

                        GL11.glPopMatrix();
                    }
                }
            }
        }

        float globeAlpha = Math.max(Math.max((MathHelper.sqrt_float((float) playerDistSQ) - 3.0f), 0.0f) + 0.2f, realityGlobe.getObfuscationAlpha());

        for (int i = ((playerDistSQ < 4 * 4 && !realityGlobe.displaysDefaultHouse()) ? 1 : 0); i >= 0; i--)
        {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glPushMatrix();
            for (int n = 0; n < i; n++)
                setupWorldTransform(rumble);

            GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
            GL11.glTranslated(0.0, -1.0, 0.0);
            bindTexture(realityGlobeTextureClear);
            modelRealityGlobe.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

//            if (globeAlpha > 0.0)
            {
                GL11.glPushMatrix();
                bindTexture(realityGlobeTexture);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, globeAlpha);
                modelRealityGlobe.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();
    }

    @Override
    public void func_147496_a(World par1World)
    {
        blockRenderer = new RenderBlocks(par1World); // onWorldChange
    }

    public void setupWorldTransform(float rumble)
    {
        double sizePX = 1.0 / (double) (sizeX * 2 + 1);
        double sizePY = 1.0 / (double) (sizeX * 2 + 1);
        double sizePZ = 1.0 / (double) (sizeX * 2 + 1);

        GL11.glTranslated(0.0, 0.175 + rumble, 0.0);
        GL11.glScaled(0.85, 0.85, 0.85);
        GL11.glScaled(sizePX, sizePY, sizePZ);
    }

    public static void updateCallList(TileEntitySnowGlobe tileEntity, RenderBlocks blockRenderer)
    {
        drawInCallList(tileEntity, blockRenderer);
    }

    public static void constructCallList(TileEntitySnowGlobe tileEntity, RenderBlocks blockRenderer)
    {
        tileEntity.glCallListIndex = GLAllocation.generateDisplayLists(1);

        drawInCallList(tileEntity, blockRenderer);
    }

    public static void drawInCallList(TileEntitySnowGlobe tileEntity, RenderBlocks blockRenderer)
    {
        Tessellator var10 = Tessellator.instance;

        GL11.glNewList(tileEntity.glCallListIndex, GL11.GL_COMPILE);

        var10.startDrawingQuads();

        if (!tileEntity.displaysDefaultHouse())
        {
            var10.setTranslation((double) (-(float) tileEntity.xCoord - 0.5), (double) (-(float) tileEntity.yCoord - 0.5), (double) (-(float) tileEntity.zCoord - 0.5));
            var10.setColorOpaque_F(1.0f, 1.0f, 1.0f);

            for (int i = 0; i < 2; i++)
            {
                for (int x = -sizeX; x <= sizeX; x++)
                    for (int y = -sizeY; y <= sizeY; y++)
                        for (int z = -sizeZ; z <= sizeZ; z++)
                        {
                            int blockX = x + tileEntity.xCoord;
                            int blockY = y + tileEntity.yCoord;
                            int blockZ = z + tileEntity.zCoord;

                            Block block = tileEntity.getWorldObj().getBlock(blockX, blockY, blockZ);

                            if (block != null)
                            {
                                if (block.getRenderBlockPass() == i)
                                {
                                    if (x == -sizeX || x == sizeX || y == -sizeY || y == sizeY || z == -sizeZ || z == sizeZ)
                                    {
                                        blockRenderer.renderBlockAllFaces(block, blockX, blockY, blockZ);
                                    }
                                    else
                                        blockRenderer.renderBlockByRenderType(block, blockX, blockY, blockZ);
                                }
                            }
                        }
            }

            var10.setTranslation(0.0D, 0.0D, 0.0D);
        }
        else
        {
//            var10.setTranslation((double) (-(float) tileEntity.xCoord - 0.5), (double) (-(float) tileEntity.yCoord - 0.5), (double) (-(float) tileEntity.zCoord - 0.5));
//            var10.setColorOpaque_F(1.0f, 1.0f, 1.0f);
//
//            blockRenderer.brightnessBottomLeft = 255;
//            blockRenderer.brightnessBottomRight = 255;
//            blockRenderer.brightnessTopLeft = 255;
//            blockRenderer.brightnessTopRight = 255;
//
//            for (int i = 0; i < 2; i++)
//            {
//                IvBlockMapper mapper = new IvBlockMapper();
//
//                for (int x = -sizeX; x <= sizeX; x++)
//                    for (int y = -sizeY; y <= sizeY; y++)
//                        for (int z = -sizeZ; z <= sizeZ; z++)
//                        {
//                            int blockX = x + tileEntity.xCoord;
//                            int blockY = y + tileEntity.yCoord;
//                            int blockZ = z + tileEntity.zCoord;
//
//                            int internalX = x + sizeX;
//                            int internalY = y + sizeY;
//                            int internalZ = z + sizeZ;
//
//                            Block block = defaultGlobe.getBlock(internalX, internalY, internalZ);
//                            int meta = defaultGlobe.getMeta(internalX, internalY, internalZ);
//                            int blColor = block != Blocks.grass ? block.getRenderColor(meta) : 0xffffff;
//
//                            float red = (float)(blColor >> 16 & 255) / 255.0F;
//                            float green = (float)(blColor >> 8 & 255) / 255.0F;
//                            float blue = (float)(blColor & 255) / 255.0F;
////                            blockRenderer.enableAO = true;
////                            blockRenderer.colorRedTopLeft = red;
////                            blockRenderer.colorGreenTopLeft = green;
////                            blockRenderer.colorBlueTopLeft = blue;
////                            blockRenderer.colorRedBottomLeft = red;
////                            blockRenderer.colorGreenBottomLeft = green;
////                            blockRenderer.colorBlueBottomLeft = blue;
////                            blockRenderer.colorRedBottomRight = red;
////                            blockRenderer.colorGreenBottomRight = green;
////                            blockRenderer.colorBlueBottomRight = blue;
////                            blockRenderer.colorRedTopRight = red;
////                            blockRenderer.colorGreenTopRight = green;
////                            blockRenderer.colorBlueTopRight = blue;
//
////                            if (i == 0)
////                                mapper.addBlock((x != 0 || y != 0 || z != 0) ? tileEntity.getWorldObj().getBlock(blockX, blockY, blockZ) : Blocks.air, (byte)tileEntity.getWorldObj().getBlockMetadata(blockX, blockY, blockZ));
//
//                            if (block != null)
//                            {
//                                if (block.getRenderBlockPass() == i && block.getRenderType() >= 0)
//                                {
//                                    block.setBlockBoundsForItemRender();
//                                    blockRenderer.setRenderBoundsFromBlock(block);
//
//                                    var10.setColorOpaque_F(red, green, blue);
//                                    if (defaultGlobe.renderSide(internalX, internalY, internalZ, 0))
//                                        blockRenderer.renderFaceYNeg(block, blockX, blockY, blockZ, blockRenderer.getBlockIconFromSideAndMetadata(block, 0, meta));
//                                    if (defaultGlobe.renderSide(internalX, internalY, internalZ, 1))
//                                        blockRenderer.renderFaceYPos(block, blockX, blockY, blockZ, blockRenderer.getBlockIconFromSideAndMetadata(block, 1, meta));
//                                    if (defaultGlobe.renderSide(internalX, internalY, internalZ, 2))
//                                        blockRenderer.renderFaceZNeg(block, blockX, blockY, blockZ, blockRenderer.getBlockIconFromSideAndMetadata(block, 2, meta));
//                                    if (defaultGlobe.renderSide(internalX, internalY, internalZ, 3))
//                                        blockRenderer.renderFaceZPos(block, blockX, blockY, blockZ, blockRenderer.getBlockIconFromSideAndMetadata(block, 3, meta));
//                                    if (defaultGlobe.renderSide(internalX, internalY, internalZ, 4))
//                                        blockRenderer.renderFaceXNeg(block, blockX, blockY, blockZ, blockRenderer.getBlockIconFromSideAndMetadata(block, 4, meta));
//                                    if (defaultGlobe.renderSide(internalX, internalY, internalZ, 5))
//                                        blockRenderer.renderFaceXPos(block, blockX, blockY, blockZ, blockRenderer.getBlockIconFromSideAndMetadata(block, 5, meta));
//                                }
//                            }
//
//                            blockRenderer.enableAO = false;
//                        }
//
////                if (i == 0)
////                    mapper.writeToFile("defaultGlobe.nbt");
//            }
//
//            var10.setTranslation(0.0D, 0.0D, 0.0D);
        }

        var10.draw();

        GL11.glEndList();
    }

    public static void destructCallList(TileEntitySnowGlobe tileEntity)
    {
        if (tileEntity.glCallListIndex >= 0)
        {
            GLAllocation.deleteDisplayLists(tileEntity.glCallListIndex);
            tileEntity.glCallListIndex = -1;
        }
    }

//    public static void writeVertexStateToTagCompound(NBTTagCompound tagCompound)
//    {
//        TesselatorVertexState state = Tessellator.instance.getVertexState(0.0f, 0.0f, 0.0f);
//
//        tagCompound.setBoolean("hasBrightness", state.getHasBrightness());
//        tagCompound.setBoolean("hasColor", state.getHasColor());
//        tagCompound.setBoolean("hasNormals", state.getHasNormals());
//        tagCompound.setBoolean("hasTexture", state.getHasTexture());
//        tagCompound.setInteger("rawBufferIndex", state.getRawBufferIndex());
//        tagCompound.setInteger("vertexCount", state.getVertexCount());
//        tagCompound.setIntArray("rawBuffer", state.getRawBuffer());
//    }
//
//    public static TesselatorVertexState readVertexStateFromTagCompound(NBTTagCompound tagCompound)
//    {
//        boolean hasBrightness = tagCompound.getBoolean("hasBrightness");
//        boolean hasColor = tagCompound.getBoolean("hasColor");
//        boolean hasNormals = tagCompound.getBoolean("hasNormals");
//        boolean hasTexture = tagCompound.getBoolean("hasTexture");
//        int rawBufferIndex = tagCompound.getInteger("rawBufferIndex");
//        int vertexCount = tagCompound.getInteger("vertexCount");
//        int[] rawBuffer = tagCompound.getIntArray("rawBuffer");
//
//        TesselatorVertexState state = new TesselatorVertexState(rawBuffer, rawBufferIndex, vertexCount, hasTexture, hasBrightness, hasNormals, hasColor);
//        return state;
//    }
//
//    public static TesselatorVertexState createVertexStateFromByteArray(byte[] array)
//    {
//        NBTTagCompound cmp = null;
//
//        try
//        {
//            cmp = CompressedStreamTools.decompress(array);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//        return cmp != null ? readVertexStateFromTagCompound(cmp) : null;
//    }
//
//    public static byte[] getByteArrayFromVertexState()
//    {
//        NBTTagCompound cmp = new NBTTagCompound();
//        writeVertexStateToTagCompound(cmp);
//
//        try
//        {
//            return CompressedStreamTools.compress(cmp);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    public static void writeVertexStateToFile(File file)
//    {
//        NBTTagCompound cmp = new NBTTagCompound();
//        writeVertexStateToTagCompound(cmp);
//        try
//        {
//            CompressedStreamTools.safeWrite(cmp, file);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
}
