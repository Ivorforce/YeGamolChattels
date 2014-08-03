/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.client.rendering;

import ivorius.ivtoolkit.blocks.IvRotatableBlockRenderHelper;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityLootChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererLootChest extends TileEntitySpecialRenderer
{
    private ModelLootChest chestModel;
    private ResourceLocation chestTexture;

    public TileEntityRendererLootChest()
    {
        chestModel = new ModelLootChest();
        chestTexture = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "lootChest.png");
    }

    @Override
    public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float var8)
    {
        TileEntityLootChest lootChest = (TileEntityLootChest) entity;

        GL11.glPushMatrix();
        IvRotatableBlockRenderHelper.transformFor(lootChest, x, y, z);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 1.0f, 0.0f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);

        if (lootChest != null)
        {
            this.chestModel.top.rotateAngleX = (-lootChest.chestFrame * (float) Math.PI);
            this.chestModel.lock.rotateAngleX = (-lootChest.chestFrame * (float) Math.PI);
        }

        bindTexture(chestTexture);
        this.chestModel.render(lootChest);
        GL11.glPopMatrix();

        ItemStack item = null;
        if (lootChest != null && !lootChest.loot.isEmpty())
            item = lootChest.loot.get(0);

        if (item != null && lootChest.chestFrame > 0)
        {
            GL11.glTranslatef(0.0f, lootChest.chestFrame * 0.8f - 0.3f, 0.0f);
            TileEntityRendererWeaponRack.renderItem(lootChest.getWorldObj(), item);
        }

        GL11.glPopMatrix();
    }
}
