/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.ivtoolkit.tools.MCRegistryDefault;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntitySnowGlobe;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TileEntityRendererSnowGlobe extends TileEntitySpecialRenderer
{
    public ModelBase modelRealityGlobe;
    public ResourceLocation realityGlobeTexture;
    public ResourceLocation realityGlobeTextureClear;

    private RenderBlocks blockRenderer;

    public static IvBlockCollection defaultGlobe;

    public static final int SIZE_X = 10;
    public static final int SIZE_Y = 9;
    public static final int SIZE_Z = 10;

    public TileEntityRendererSnowGlobe()
    {
        this.modelRealityGlobe = new ModelRealityGlobe();
        realityGlobeTexture = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "realityGlobeGlass.png");
        realityGlobeTextureClear = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "realityGlobeClear.png");

        defaultGlobe = blockCollectionFromResourceLocation(new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathOther + "defaultGlobe.nbt"), MCRegistryDefault.INSTANCE);
    }

    public static IvBlockCollection blockCollectionFromResourceLocation(ResourceLocation resourceLocation, MCRegistry registry)
    {
        IResource globeResource = null;
        try
        {
            globeResource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (globeResource != null)
        {
            NBTTagCompound compound = null;
            try
            {
                compound = CompressedStreamTools.read(new DataInputStream(globeResource.getInputStream()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (compound != null)
                return new IvBlockCollection(compound, registry);
        }

        return null;
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks)
    {
        int pass = MinecraftForgeClient.getRenderPass();
        TileEntitySnowGlobe realityGlobe = ((TileEntitySnowGlobe) tileEntity);

        double playerDistSQ = x * x + y * y + z * z;

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

        float rumbling = realityGlobe.getRumbling();
        float rumble = rumbling > 0.0f ? (-tileEntity.getWorldObj().rand.nextFloat() * rumbling) : 0.0f;

        if (playerDistSQ < 4 * 4 && pass == 0)
        {
            if (realityGlobe.getGlCallListIndex() < 0 || realityGlobe.needsVisualUpdate)
            {
                constructCallList(realityGlobe, this.blockRenderer);
                realityGlobe.needsVisualUpdate = false;
            }

            double sizePX = 1.0 / (double) (SIZE_X * 2 + 1);
            double sizePY = 1.0 / (double) (SIZE_X * 2 + 1);
            double sizePZ = 1.0 / (double) (SIZE_X * 2 + 1);

            if (realityGlobe.getGlCallListIndex() >= 0)
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
                    GL11.glShadeModel(GL11.GL_SMOOTH);
                else
                    GL11.glShadeModel(GL11.GL_FLAT);

                if (realityGlobe.getGlCallListIndex() >= 0)
                {
                    GL11.glCallList(realityGlobe.getGlCallListIndex());

                    if (!realityGlobe.displaysDefaultHouse())
                    {
                        setupWorldTransform(rumble);
                        GL11.glCallList(realityGlobe.getGlCallListIndex());
                    }
                }

                RenderHelper.enableStandardItemLighting();

                GL11.glPopMatrix();
            }

            if (!realityGlobe.displaysDefaultHouse())
            {
                if (playerDistSQ < 4 * 4)
                {
                    AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double) tileEntity.xCoord - SIZE_X, (double) tileEntity.yCoord - SIZE_Y, (double) tileEntity.zCoord - SIZE_Z, (double) tileEntity.xCoord + 1 + SIZE_X, (double) tileEntity.yCoord + 1 + SIZE_Y, (double) tileEntity.zCoord + 1 + SIZE_Z);
                    List entities = tileEntity.getWorldObj().getEntitiesWithinAABBExcludingEntity(null, bb);

                    if (entities.size() > 0)
                    {
                        GL11.glPushMatrix();
                        setupWorldTransform(rumble);
                        GL11.glTranslated(-tileEntity.xCoord - 0.5, -tileEntity.yCoord - 0.5, -tileEntity.zCoord - 0.5);

                        for (Object obj : entities)
                        {
                            Entity entity = (Entity) obj;

                            AxisAlignedBB entityBB = entity.boundingBox;
                            if (entityBB.minX > bb.minX && entityBB.maxX < bb.maxX && entityBB.minY > bb.minY && entityBB.maxY < bb.maxY && entityBB.minZ > bb.minZ && entityBB.maxZ < bb.maxZ)
                            {
                                double rX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
                                double rY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
                                double rZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
                                float rYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;

                                RenderManager.instance.renderEntityWithPosYaw(entity, rX, rY, rZ, rYaw, partialTicks);
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
            if (pass == 0)
                modelRealityGlobe.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

//            if (globeAlpha > 0.0)
            {
                GL11.glPushMatrix();
                bindTexture(realityGlobeTexture);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, globeAlpha);
                if (pass == 1)
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
        double sizePX = 1.0 / (double) (SIZE_X * 2 + 1);
        double sizePY = 1.0 / (double) (SIZE_X * 2 + 1);
        double sizePZ = 1.0 / (double) (SIZE_X * 2 + 1);

        GL11.glTranslated(0.0, 0.175 + rumble, 0.0);
        GL11.glScaled(0.85, 0.85, 0.85);
        GL11.glScaled(sizePX, sizePY, sizePZ);
    }

    public static void constructCallList(TileEntitySnowGlobe tileEntity, RenderBlocks blockRenderer)
    {
        tileEntity.setGlCallListIndex(GLAllocation.generateDisplayLists(1));

        drawInCallList(tileEntity, blockRenderer);
    }

    public static void drawInCallList(TileEntitySnowGlobe tileEntity, RenderBlocks blockRenderer)
    {
        Tessellator var10 = Tessellator.instance;

        GL11.glNewList(tileEntity.getGlCallListIndex(), GL11.GL_COMPILE);

        var10.startDrawingQuads();

        if (!tileEntity.displaysDefaultHouse())
        {
            var10.setTranslation(-(float) tileEntity.xCoord - 0.5, -(float) tileEntity.yCoord - 0.5, -(float) tileEntity.zCoord - 0.5);
            var10.setColorOpaque_F(1.0f, 1.0f, 1.0f);

            for (int i = 0; i < 2; i++)
            {
                for (int x = -SIZE_X; x <= SIZE_X; x++)
                    for (int y = -SIZE_Y; y <= SIZE_Y; y++)
                        for (int z = -SIZE_Z; z <= SIZE_Z; z++)
                        {
                            int blockX = x + tileEntity.xCoord;
                            int blockY = y + tileEntity.yCoord;
                            int blockZ = z + tileEntity.zCoord;

                            Block block = tileEntity.getWorldObj().getBlock(blockX, blockY, blockZ);

                            if (block != null)
                            {
                                if (block.getRenderBlockPass() == i)
                                {
                                    if (x == -SIZE_X || x == SIZE_X || y == -SIZE_Y || y == SIZE_Y || z == -SIZE_Z || z == SIZE_Z)
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
            if (defaultGlobe != null)
            {
                BlockCoord lowerCoord = new BlockCoord(-SIZE_X, -SIZE_Y, -SIZE_Z);
                BlockArea area = new BlockArea(lowerCoord, new BlockCoord(SIZE_X, SIZE_Y, SIZE_Z));

                var10.setTranslation(-(float) tileEntity.xCoord - 0.5, -(float) tileEntity.yCoord - 0.5, -(float) tileEntity.zCoord - 0.5);
                var10.setColorOpaque_F(1.0f, 1.0f, 1.0f);

                blockRenderer.brightnessBottomLeft = 255;
                blockRenderer.brightnessBottomRight = 255;
                blockRenderer.brightnessTopLeft = 255;
                blockRenderer.brightnessTopRight = 255;

                for (int i = 0; i < 2; i++)
                {
                    for (BlockCoord internalCoord : area)
                    {
                        BlockCoord worldCoord = internalCoord.add(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                        BlockCoord collectionCoord = internalCoord.subtract(lowerCoord);

                        Block block = defaultGlobe.getBlock(collectionCoord);
                        int meta = defaultGlobe.getMetadata(collectionCoord);
                        int blColor = block != Blocks.grass ? block.getRenderColor(meta) : 0xffffff;

                        float red = (float) (blColor >> 16 & 255) / 255.0F;
                        float green = (float) (blColor >> 8 & 255) / 255.0F;
                        float blue = (float) (blColor & 255) / 255.0F;
//                        blockRenderer.enableAO = true;
//                        blockRenderer.colorRedTopLeft = red;
//                        blockRenderer.colorGreenTopLeft = green;
//                        blockRenderer.colorBlueTopLeft = blue;
//                        blockRenderer.colorRedBottomLeft = red;
//                        blockRenderer.colorGreenBottomLeft = green;
//                        blockRenderer.colorBlueBottomLeft = blue;
//                        blockRenderer.colorRedBottomRight = red;
//                        blockRenderer.colorGreenBottomRight = green;
//                        blockRenderer.colorBlueBottomRight = blue;
//                        blockRenderer.colorRedTopRight = red;
//                        blockRenderer.colorGreenTopRight = green;
//                        blockRenderer.colorBlueTopRight = blue;

                        if (block != null)
                        {
                            if (block.getRenderBlockPass() == i && block.getRenderType() >= 0)
                            {
                                block.setBlockBoundsForItemRender();
                                blockRenderer.setRenderBoundsFromBlock(block);

                                var10.setColorOpaque_F(red, green, blue);
                                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
                                {
                                    if (defaultGlobe.shouldRenderSide(collectionCoord, dir))
                                        blockRenderer.renderFaceYNeg(block, worldCoord.x, worldCoord.y, worldCoord.z, blockRenderer.getBlockIconFromSideAndMetadata(block, dir.ordinal(), meta));
                                }
                            }
                        }

                        blockRenderer.enableAO = false;
                    }
                }

                var10.setTranslation(0.0D, 0.0D, 0.0D);
            }
        }

        var10.draw();

        GL11.glEndList();

//        captureAndWriteToFile("defaultGlobe.nbt", new BlockCoord(tileEntity), tileEntity.getWorldObj());
    }

    public static void captureAndWriteToFile(String fileName, BlockCoord coord, World world)
    {
        BlockCoord lowerCoord = new BlockCoord(-SIZE_X, -SIZE_Y, -SIZE_Z);
        BlockArea area = new BlockArea(lowerCoord, new BlockCoord(SIZE_X, SIZE_Y, SIZE_Z));

        IvBlockCollection blockCollection = new IvBlockCollection(SIZE_X * 2 + 1, SIZE_Y * 2 + 1, SIZE_Z * 2 + 1);
        for (BlockCoord internalCoord : area)
        {
            BlockCoord worldCoord = internalCoord.add(coord);
            BlockCoord collectionCoord = internalCoord.subtract(lowerCoord);
            Block block = internalCoord.x != 0 || internalCoord.y != 0 || internalCoord.z != 0 ? worldCoord.getBlock(world) : Blocks.air;
            blockCollection.setBlockAndMetadata(collectionCoord, block, (byte) worldCoord.getMetadata(world));
        }

        NBTTagCompound compound = blockCollection.createTagCompound();
        try
        {
            CompressedStreamTools.write(compound, new File(Minecraft.getMinecraft().mcDataDir, fileName));
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
