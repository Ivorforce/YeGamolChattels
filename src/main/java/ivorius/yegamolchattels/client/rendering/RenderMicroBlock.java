/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.client.rendering;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.rendering.IvRenderHelper;
import ivorius.yegamolchattels.blocks.BlockMicroBlock;
import ivorius.yegamolchattels.blocks.TileEntityMicroBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

/**
 * Created by lukas on 11.07.14.
 */
public class RenderMicroBlock implements ISimpleBlockRenderingHandler
{
    private int renderID;

    public RenderMicroBlock(int renderID)
    {
        this.renderID = renderID;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
        GL11.glPushMatrix();
        GL11.glScalef(0.4f, 0.4f, 0.4f);
        GL11.glTranslatef(-1f, 0f, 0f);
        IvRenderHelper.renderCubeInvBlock(renderer, Blocks.stone, (byte) 0);
        GL11.glTranslatef(2f, 0f, 0f);
        IvRenderHelper.renderCubeInvBlock(renderer, Blocks.stone, (byte) 0);
        GL11.glTranslatef(-1f, 1f, 0f);
        IvRenderHelper.renderCubeInvBlock(renderer, Blocks.stone, (byte) 0);
        GL11.glTranslatef(0f, -1f, 0f);
        IvRenderHelper.renderCubeInvBlock(renderer, Blocks.stone, (byte) 0);
        GL11.glTranslatef(0f, 0f, 1f);
        IvRenderHelper.renderCubeInvBlock(renderer, Blocks.planks, (byte) 0);
        GL11.glTranslatef(0f, 0f, -2f);
        IvRenderHelper.renderCubeInvBlock(renderer, Blocks.planks, (byte) 0);
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        TileEntityMicroBlock tileEntityMicroBlock = (TileEntityMicroBlock) world.getTileEntity(x, y, z);
        IvBlockCollection blockCollection = tileEntityMicroBlock.getBlockCollection();
        int brightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        renderMicroblocks(world, new BlockCoord(x, y, z), tileEntityMicroBlock.getQuadCache(), (BlockMicroBlock) block, renderer, brightness);

        return true;
    }

    public static void renderMicroblocks(IBlockAccess world, BlockCoord pos, GridQuadCache<IIcon> quadCache, BlockMicroBlock origBlock, RenderBlocks renderer, int innerBrightness)
    {
        Tessellator tessellator = Tessellator.instance;
        int x = pos.x;
        int y = pos.y;
        int z = pos.z;

        tessellator.setBrightness(innerBrightness);
        tessellator.setColorOpaque_F(1.0f, 1.0f, 1.0f);

        renderer.field_152631_f = true; // Fixes random block texture rotations for small textures... Used in renderBlockFence

        for (GridQuadCache.CachedQuadLevel<IIcon> cachedQuadLevel : quadCache)
        {
            origBlock.renderSide = cachedQuadLevel.direction;
            origBlock.renderIcon = cachedQuadLevel.t;

            FloatBuffer quads = cachedQuadLevel.quads;
            while (quads.position() < quads.limit() - 3)
            {
                float minX = quads.get(),
                        minY = quads.get(),
                        maxX = quads.get(),
                        maxY = quads.get();
                float[] minAxes = GridQuadCache.getNormalAxes(cachedQuadLevel.direction, cachedQuadLevel.zLevel, minX, minY);
                float[] maxAxes = GridQuadCache.getNormalAxes(cachedQuadLevel.direction, cachedQuadLevel.zLevel, maxX, maxY);

                origBlock.setBlockBounds(minAxes[0], minAxes[1], minAxes[2], maxAxes[0], maxAxes[1], maxAxes[2]);
                renderer.setRenderBoundsFromBlock(origBlock);
                renderer.renderStandardBlock(origBlock, x, y, z);
            }
            quads.position(0);
        }

        renderer.field_152631_f = false;

        origBlock.setBlockBoundsBasedOnState(world, x, y, z);
        origBlock.renderSide = null;
        origBlock.renderIcon = null;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId()
    {
        return renderID;
    }
}
