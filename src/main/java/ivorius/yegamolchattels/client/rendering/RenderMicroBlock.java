/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.client.rendering;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.yegamolchattels.blocks.BlockMicroBlock;
import ivorius.yegamolchattels.blocks.TileEntityMicroBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import java.nio.FloatBuffer;
import java.util.Arrays;

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

    public static void renderMicroblocks(IBlockAccess world, BlockCoord pos, IIconQuadCache quadCache, BlockMicroBlock origBlock, RenderBlocks renderer, int innerBrightness)
    {
        Tessellator tessellator = Tessellator.instance;
        int x = pos.x;
        int y = pos.y;
        int z = pos.z;

        tessellator.setBrightness(innerBrightness);
        tessellator.setColorOpaque_F(1.0f, 1.0f, 1.0f);

        renderer.field_152631_f = true; // Fixes random block texture rotations for small textures... Used in renderBlockFence

        for (IIconQuadCache.CachedQuadLevel cachedQuadLevel : quadCache)
        {
            origBlock.renderSide = cachedQuadLevel.direction;
            origBlock.renderIcon = cachedQuadLevel.icon;

            FloatBuffer quads = cachedQuadLevel.quads;
            while (quads.position() < quads.limit() - 3)
            {
                float minX = quads.get(),
                        minY = quads.get(),
                        maxX = quads.get(),
                        maxY = quads.get();
                float[] minAxes = IIconQuadCache.getNormalAxes(cachedQuadLevel.direction, cachedQuadLevel.zLevel, minX, minY);
                float[] maxAxes = IIconQuadCache.getNormalAxes(cachedQuadLevel.direction, cachedQuadLevel.zLevel, maxX, maxY);

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
        return false;
    }

    @Override
    public int getRenderId()
    {
        return renderID;
    }
}
