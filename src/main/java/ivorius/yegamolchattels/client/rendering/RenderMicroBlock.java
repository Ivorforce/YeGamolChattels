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
        renderMicroblocks(world, new BlockCoord(x, y, z), blockCollection, (BlockMicroBlock) block, renderer, brightness);

        return true;
    }

    public static void renderMicroblocks(IBlockAccess world, BlockCoord pos, IvBlockCollection blockCollection, BlockMicroBlock origBlock, RenderBlocks renderer, int innerBrightness)
    {
        Tessellator tessellator = Tessellator.instance;
        int x = pos.x;
        int y = pos.y;
        int z = pos.z;

        float oneX = 1.0f / (float) blockCollection.width;
        float oneY = 1.0f / (float) blockCollection.height;
        float oneZ = 1.0f / (float) blockCollection.length;

        tessellator.setBrightness(innerBrightness);
        tessellator.setColorOpaque_F(1.0f, 1.0f, 1.0f);

        renderer.field_152631_f = true; // Fixes random block texture rotations for small textures... Used in renderBlockFence
        for (BlockCoord coord : blockCollection)
        {
            Block block = blockCollection.getBlock(coord);

            if (block.getMaterial() != Material.air)
            {
                int meta = blockCollection.getMetadata(coord);

                origBlock.setBlockBounds(coord.x * oneX, coord.y * oneY, coord.z * oneZ, (coord.x + 1) * oneX, (coord.y + 1) * oneY, (coord.z + 1) * oneZ);
                renderer.setRenderBoundsFromBlock(origBlock);

                origBlock.renderSideCache[ForgeDirection.NORTH.ordinal()] = blockCollection.shouldRenderSide(coord, ForgeDirection.NORTH);
                origBlock.renderSideCache[ForgeDirection.SOUTH.ordinal()] = blockCollection.shouldRenderSide(coord, ForgeDirection.SOUTH);
                origBlock.renderSideCache[ForgeDirection.EAST.ordinal()] = blockCollection.shouldRenderSide(coord, ForgeDirection.EAST);
                origBlock.renderSideCache[ForgeDirection.WEST.ordinal()] = blockCollection.shouldRenderSide(coord, ForgeDirection.WEST);
                origBlock.renderSideCache[ForgeDirection.UP.ordinal()] = blockCollection.shouldRenderSide(coord, ForgeDirection.UP);
                origBlock.renderSideCache[ForgeDirection.DOWN.ordinal()] = blockCollection.shouldRenderSide(coord, ForgeDirection.DOWN);

                origBlock.renderBlockCache = block;
                origBlock.renderBlockMetaCache = meta;

                renderer.renderStandardBlock(origBlock, x, y, z);

//                if (blockCollection.shouldRenderSide(coord, ForgeDirection.NORTH))
//                    renderer.renderFaceZNeg(origBlock, x, y, z, block.getIcon(ForgeDirection.NORTH.ordinal(), meta));
//                if (blockCollection.shouldRenderSide(coord, ForgeDirection.EAST))
//                    renderer.renderFaceXPos(origBlock, x, y, z, block.getIcon(ForgeDirection.EAST.ordinal(), meta));
//                if (blockCollection.shouldRenderSide(coord, ForgeDirection.SOUTH))
//                    renderer.renderFaceZPos(origBlock, x, y, z, block.getIcon(ForgeDirection.SOUTH.ordinal(), meta));
//                if (blockCollection.shouldRenderSide(coord, ForgeDirection.WEST))
//                    renderer.renderFaceXNeg(origBlock, x, y, z, block.getIcon(ForgeDirection.WEST.ordinal(), meta));
//                if (blockCollection.shouldRenderSide(coord, ForgeDirection.UP))
//                    renderer.renderFaceYPos(origBlock, x, y, z, block.getIcon(ForgeDirection.UP.ordinal(), meta));
//                if (blockCollection.shouldRenderSide(coord, ForgeDirection.DOWN))
//                    renderer.renderFaceYNeg(origBlock, x, y, z, block.getIcon(ForgeDirection.DOWN.ordinal(), meta));
            }
        }
        renderer.field_152631_f = false;

        origBlock.setBlockBoundsBasedOnState(world, x, y, z);
        origBlock.renderBlockCache = null;
        origBlock.renderBlockMetaCache = 0;
        Arrays.fill(origBlock.renderSideCache, false);
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
