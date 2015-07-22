/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;


import net.minecraftforge.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import ivorius.ivtoolkit.rendering.grid.Icon;
import net.minecraft.world.IBlockAccess;

public class RenderTikiTorch implements ISimpleBlockRenderingHandler
{
    public int renderID;

    public RenderTikiTorch(int renderID)
    {
        this.renderID = renderID;
    }

    public static void renderTikiTorch(RenderBlocks renderblocks, IBlockAccess blockAccess, Block block, float i, float j, float k)
    {
        int meta = blockAccess.getBlockMetadata((int) i, (int) j, (int) k);

        Tessellator tessellator = Tessellator.instance;

        tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, (int) i, (int) j, (int) k));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        Icon var11 = block.getIcon(0, meta);

        if (renderblocks.hasOverrideBlockTexture())
        {
            var11 = renderblocks.overrideBlockTexture;
        }

        double var12 = var11.getMinU();
        double var14 = var11.getMinV();
        double var16 = var11.getMaxU();
        double var18 = var11.getMaxV();

        i += 0.5;
        k += 0.5;

        double d5 = var11.getMinU() + 0.02734375D;
        double d6 = var11.getMinV() + 0.0234375D;
        double d7 = var11.getMinU() + 0.03515625D;
        double d8 = var11.getMinV() + 0.03125D;

        double f = var11.getMinU();
        double f1 = var11.getMaxU();
        double f2 = var11.getMinV();
        double f3 = var11.getMaxV();

        double d9 = i - 0.5D;
        double d10 = i + 0.5D;
        double d11 = k - 0.5D;
        double d12 = k + 0.5D;
        double d13 = 0.0625D;
        double d14 = 0.625D;

        if (meta == 0)
        {
            tessellator.addVertexWithUV(i - d13, j + d14, k - d13, d5, d6);
            tessellator.addVertexWithUV(i - d13, j + d14, k + d13, d5, d8);
            tessellator.addVertexWithUV(i + d13, j + d14, k + d13, d7, d8);
            tessellator.addVertexWithUV(i + d13, j + d14, k - d13, d7, d6);
        }

        tessellator.addVertexWithUV(i - d13, j + 1.0D, d11, f, f2);
        tessellator.addVertexWithUV((i - d13), j, d11, f, f3);
        tessellator.addVertexWithUV((i - d13), j, d12, f1, f3);
        tessellator.addVertexWithUV(i - d13, j + 1.0D, d12, f1, f2);

        tessellator.addVertexWithUV(i + d13, j + 1.0D, d12, f, f2);
        tessellator.addVertexWithUV(i + d13, j, d12, f, f3);
        tessellator.addVertexWithUV(i + d13, j, d11, f1, f3);
        tessellator.addVertexWithUV(i + d13, j + 1.0D, d11, f1, f2);

        tessellator.addVertexWithUV(d9, j + 1.0D, k + d13, f, f2);
        tessellator.addVertexWithUV(d9, j, k + d13, f, f3);
        tessellator.addVertexWithUV(d10, j, k + d13, f1, f3);
        tessellator.addVertexWithUV(d10, j + 1.0D, k + d13, f1, f2);

        tessellator.addVertexWithUV(d10, j + 1.0D, k - d13, f, f2);
        tessellator.addVertexWithUV(d10, j, (k - d13), f, f3);
        tessellator.addVertexWithUV(d9, j, (k - d13), f1, f3);
        tessellator.addVertexWithUV(d9, j + 1.0D, k - d13, f1, f2);
    }

    @Override
    public void renderInventoryBlock(IBlockState state, int modelId, RenderBlocks renderer)
    {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, BlockPos pos, Block block, int modelId, RenderBlocks renderer)
    {
        renderTikiTorch(renderer, world, block, pos);
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
