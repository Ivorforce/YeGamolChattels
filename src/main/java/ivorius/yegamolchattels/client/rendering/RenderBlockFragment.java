/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.client.rendering;

import ivorius.ivtoolkit.rendering.IvRenderHelper;
import ivorius.yegamolchattels.items.ItemBlockFragment;
import ivorius.yegamolchattels.items.ItemChisel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * Created by lukas on 13.07.14.
 */
public class RenderBlockFragment implements IItemRenderer
{
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        ItemChisel.BlockData fragment = ItemBlockFragment.getFragment(item);
        RenderBlocks renderBlocks = RenderBlocks.getInstance();

        GL11.glPushMatrix();

        if (type == ItemRenderType.ENTITY)
        {
//            GL11.glTranslated(0.0, 1.0, 0.0);
        }
        else if (type == ItemRenderType.INVENTORY)
        {
//            GL11.glTranslated(0.0, 0.3, 0.0);
        }
        else
        {
            GL11.glTranslated(0.5, 0.5, 0.5);
        }

        GL11.glScalef(0.4f, 0.4f, 0.4f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        IvRenderHelper.renderCubeInvBlock(renderBlocks, fragment.block);
        GL11.glPopMatrix();
    }
}
