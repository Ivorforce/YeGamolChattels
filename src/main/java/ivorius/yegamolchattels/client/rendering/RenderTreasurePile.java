/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class RenderTreasurePile implements ISimpleBlockRenderingHandler
{
    public int renderID;

    public RenderTreasurePile(int renderID)
    {
        this.renderID = renderID;
    }

    public void renderTreasurePile(IIcon icon, float x, float y, float z, boolean north, boolean east, boolean south, boolean west)
    {
        float f1 = icon.getMinU();
        float f2 = icon.getMinV();
        float f3 = icon.getMaxU();
        float f4 = icon.getMaxV();

        float f5 = icon.getMinU() + 0.375f * (icon.getMaxU() - icon.getMinU());
        float f6 = icon.getMinU() + 0.5f * (icon.getMaxU() - icon.getMinU());
        float f7 = icon.getMinV() + 0.375f * (icon.getMaxV() - icon.getMinV());
        float f8 = icon.getMinV() + 0.5f * (icon.getMaxV() - icon.getMinV());

        Tessellator tessellator = Tessellator.instance;
        double d13 = 0.0625D;

        tessellator.addVertexWithUV(x - 0.5, y, z - 0.5, f1, f2);
        tessellator.addVertexWithUV(x + 0.5, y, z - 0.5, f3, f2);
        tessellator.addVertexWithUV(x + 0.5, y, z + 0.5, f3, f4);
        tessellator.addVertexWithUV(x - 0.5, y, z + 0.5, f1, f4);

        tessellator.addVertexWithUV(x - (west ? 0.5 : d13), y + 1, z - (south ? 0.5 : d13), (west ? f1 : f5), (south ? f2 : f7));
        tessellator.addVertexWithUV(x - (west ? 0.5 : d13), y + 1, z + (north ? 0.5 : d13), (west ? f1 : f5), (north ? f4 : f8));
        tessellator.addVertexWithUV(x + (east ? 0.5 : d13), y + 1, z + (north ? 0.5 : d13), (east ? f3 : f6), (north ? f4 : f8));
        tessellator.addVertexWithUV(x + (east ? 0.5 : d13), y + 1, z - (south ? 0.5 : d13), (east ? f3 : f6), (south ? f2 : f7));

        tessellator.addVertexWithUV(x - 0.5, y, z - 0.5, f1, f2);
        tessellator.addVertexWithUV(x - (west ? 0.5 : d13), y + 1, z - (south ? 0.5 : d13), (west ? f1 : f5), f4);
        tessellator.addVertexWithUV(x + (east ? 0.5 : d13), y + 1, z - (south ? 0.5 : d13), (east ? f3 : f6), f4);
        tessellator.addVertexWithUV(x + 0.5, y, z - 0.5, f3, f2);

        tessellator.addVertexWithUV(x - 0.5, y, z + 0.5, f1, f2);
        tessellator.addVertexWithUV(x + 0.5, y, z + 0.5, f3, f2);
        tessellator.addVertexWithUV(x + (east ? 0.5 : d13), y + 1, z + (north ? 0.5 : d13), (east ? f3 : f6), f4);
        tessellator.addVertexWithUV(x - (west ? 0.5 : d13), y + 1, z + (north ? 0.5 : d13), (west ? f1 : f5), f4);

        tessellator.addVertexWithUV(x - 0.5, y, z - 0.5, f1, f2);
        tessellator.addVertexWithUV(x - 0.5, y, z + 0.5, f3, f2);
        tessellator.addVertexWithUV(x - (west ? 0.5 : d13), y + 1, z + (north ? 0.5 : d13), (north ? f3 : f6), f4);
        tessellator.addVertexWithUV(x - (west ? 0.5 : d13), y + 1, z - (south ? 0.5 : d13), (south ? f1 : f5), f4);

        tessellator.addVertexWithUV(x + 0.5, y, z - 0.5, f1, f2);
        tessellator.addVertexWithUV(x + (east ? 0.5 : d13), y + 1, z - (south ? 0.5 : d13), (south ? f1 : f5), f4);
        tessellator.addVertexWithUV(x + (east ? 0.5 : d13), y + 1, z + (north ? 0.5 : d13), (north ? f3 : f6), f4);
        tessellator.addVertexWithUV(x + 0.5, y, z + 0.5, f3, f2);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        if (world.getBlock(x, y + 1, z) == block)
        {
            renderer.renderStandardBlock(block, x, y, z);
        }
        else
        {
            int l = world.getBlockMetadata(x, y, z);

            Tessellator tessellator = Tessellator.instance;

            tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

            IIcon icon = block.getIcon(0, l);

            if (renderer.hasOverrideBlockTexture())
                icon = renderer.overrideBlockTexture;

            boolean north = world.getBlock(x, y, z + 1) == block;
            boolean east = world.getBlock(x + 1, y, z) == block;
            boolean south = world.getBlock(x, y, z - 1) == block;
            boolean west = world.getBlock(x - 1, y, z) == block;

            renderTreasurePile(icon, x + 0.5f, y, z + 0.5f, north, east, south, west);
        }

        return true;
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
