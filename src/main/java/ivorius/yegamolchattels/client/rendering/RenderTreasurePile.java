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
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();

        float minUInset = icon.getMinU() + 0.375f * (icon.getMaxU() - icon.getMinU());
        float maxUInset = icon.getMinU() + 0.5f * (icon.getMaxU() - icon.getMinU());
        float minVInset = icon.getMinV() + 0.375f * (icon.getMaxV() - icon.getMinV());
        float maxVInset = icon.getMinV() + 0.5f * (icon.getMaxV() - icon.getMinV());

        Tessellator tessellator = Tessellator.instance;
        double sideInset = 0.0625D;
        double yMax = y + 1.0;

        tessellator.addVertexWithUV(x - 0.5, y, z - 0.5, minU, minV);
        tessellator.addVertexWithUV(x + 0.5, y, z - 0.5, maxU, minV);
        tessellator.addVertexWithUV(x + 0.5, y, z + 0.5, maxU, maxV);
        tessellator.addVertexWithUV(x - 0.5, y, z + 0.5, minU, maxV);

        tessellator.addVertexWithUV(x - (west ? 0.5 : sideInset), yMax, z - (south ? 0.5 : sideInset), (west ? minU : minUInset), (south ? minV : minVInset));
        tessellator.addVertexWithUV(x - (west ? 0.5 : sideInset), yMax, z + (north ? 0.5 : sideInset), (west ? minU : minUInset), (north ? maxV : maxVInset));
        tessellator.addVertexWithUV(x + (east ? 0.5 : sideInset), yMax, z + (north ? 0.5 : sideInset), (east ? maxU : maxUInset), (north ? maxV : maxVInset));
        tessellator.addVertexWithUV(x + (east ? 0.5 : sideInset), yMax, z - (south ? 0.5 : sideInset), (east ? maxU : maxUInset), (south ? minV : minVInset));

        tessellator.addVertexWithUV(x - 0.5, y, z - 0.5, minU, minV);
        tessellator.addVertexWithUV(x - (west ? 0.5 : sideInset), yMax, z - (south ? 0.5 : sideInset), (west ? minU : minUInset), maxV);
        tessellator.addVertexWithUV(x + (east ? 0.5 : sideInset), yMax, z - (south ? 0.5 : sideInset), (east ? maxU : maxUInset), maxV);
        tessellator.addVertexWithUV(x + 0.5, y, z - 0.5, maxU, minV);

        tessellator.addVertexWithUV(x - 0.5, y, z + 0.5, minU, minV);
        tessellator.addVertexWithUV(x + 0.5, y, z + 0.5, maxU, minV);
        tessellator.addVertexWithUV(x + (east ? 0.5 : sideInset), yMax, z + (north ? 0.5 : sideInset), (east ? maxU : maxUInset), maxV);
        tessellator.addVertexWithUV(x - (west ? 0.5 : sideInset), yMax, z + (north ? 0.5 : sideInset), (west ? minU : minUInset), maxV);

        tessellator.addVertexWithUV(x - 0.5, y, z - 0.5, minU, minV);
        tessellator.addVertexWithUV(x - 0.5, y, z + 0.5, maxU, minV);
        tessellator.addVertexWithUV(x - (west ? 0.5 : sideInset), yMax, z + (north ? 0.5 : sideInset), (north ? maxU : maxUInset), maxV);
        tessellator.addVertexWithUV(x - (west ? 0.5 : sideInset), yMax, z - (south ? 0.5 : sideInset), (south ? minU : minUInset), maxV);

        tessellator.addVertexWithUV(x + 0.5, y, z - 0.5, minU, minV);
        tessellator.addVertexWithUV(x + (east ? 0.5 : sideInset), yMax, z - (south ? 0.5 : sideInset), (south ? minU : minUInset), maxV);
        tessellator.addVertexWithUV(x + (east ? 0.5 : sideInset), yMax, z + (north ? 0.5 : sideInset), (north ? maxU : maxUInset), maxV);
        tessellator.addVertexWithUV(x + 0.5, y, z + 0.5, maxU, minV);
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
