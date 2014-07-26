/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.client.rendering;

import ivorius.ivtoolkit.items.IvItemRendererModel;
import ivorius.yegamolchattels.blocks.TileEntityLootChest;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ModelLootChest extends ModelBase implements IvItemRendererModel.ItemModelRenderer
{
    //fields
    ModelRenderer front;
    ModelRenderer left;
    ModelRenderer back;
    ModelRenderer right;
    ModelRenderer bottom;
    ModelRenderer cobweb;
    ModelRenderer top;
    ModelRenderer padlock1;
    ModelRenderer padlock2;
    ModelRenderer lock;
    ModelRenderer grass;
    ModelRenderer pouch;

    public ModelLootChest()
    {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.front = new ModelRenderer(this, 0, 0);
        this.front.addBox(-7F, 0F, -6F, 14, 10, 2);
        this.front.setRotationPoint(0F, 14F, 0F);
        this.front.setTextureSize(128, 64);
        this.front.mirror = true;
        setRotation(this.front, 0F, 0F, 0F);
        this.left = new ModelRenderer(this, 0, 13);
        this.left.addBox(5F, 0F, -4F, 2, 10, 8);
        this.left.setRotationPoint(0F, 14F, 0F);
        this.left.setTextureSize(128, 64);
        this.left.mirror = true;
        setRotation(this.left, 0F, 0F, 0F);
        this.back = new ModelRenderer(this, 33, 0);
        this.back.addBox(-7F, 0F, 4F, 14, 10, 2);
        this.back.setRotationPoint(0F, 14F, 0F);
        this.back.setTextureSize(128, 64);
        this.back.mirror = true;
        setRotation(this.back, 0F, 0F, 0F);
        this.right = new ModelRenderer(this, 21, 13);
        this.right.addBox(-7F, 0F, -4F, 2, 10, 8);
        this.right.setRotationPoint(0F, 14F, 0F);
        this.right.setTextureSize(128, 64);
        this.right.mirror = true;
        setRotation(this.right, 0F, 0F, 0F);
        this.bottom = new ModelRenderer(this, 0, 32);
        this.bottom.addBox(-5F, 9F, -4F, 10, 1, 8);
        this.bottom.setRotationPoint(0F, 14F, 0F);
        this.bottom.setTextureSize(128, 64);
        this.bottom.mirror = true;
        setRotation(this.bottom, 0F, 0F, 0F);
        this.cobweb = new ModelRenderer(this, 80, 0);
        this.cobweb.addBox(-2F, 9F, -2F, 4, 0, 4);
        this.cobweb.setRotationPoint(0F, 14F, 0F);
        this.cobweb.setTextureSize(128, 64);
        this.cobweb.mirror = true;
        setRotation(this.cobweb, 0.3346075F, -0.1858931F, 0.3346075F);
        this.top = new ModelRenderer(this, 0, 42);
        this.top.addBox(-7.5F, -2F, -12.5F, 15, 3, 13);
        this.top.setRotationPoint(0F, 14F, 6F);
        this.top.setTextureSize(128, 64);
        this.top.mirror = true;
        setRotation(this.top, 0F, 0F, 0F);
        this.padlock1 = new ModelRenderer(this, 80, 5);
        this.padlock1.addBox(-1.2F, 4F, -7F, 3, 2, 1);
        this.padlock1.setRotationPoint(0F, 14F, 0F);
        this.padlock1.setTextureSize(128, 64);
        this.padlock1.mirror = true;
        setRotation(this.padlock1, 0, 0, 0.1F);
        this.padlock2 = new ModelRenderer(this, 89, 5);
        this.padlock2.addBox(-1.2F, 2F, -6.5F, 3, 2, 0);
        this.padlock2.setRotationPoint(0F, 14F, 0F);
        this.padlock2.setTextureSize(128, 64);
        this.padlock2.mirror = true;
        setRotation(this.padlock2, 0, 0, 0.1F);
        this.lock = new ModelRenderer(this, 80, 9);
        this.lock.addBox(-1F, -0.01F, -13F, 2, 3, 2);
        this.lock.setRotationPoint(0F, 14F, 6F);
        this.lock.setTextureSize(128, 64);
        this.lock.mirror = true;
        setRotation(this.lock, 0F, 0F, 0F);
        this.grass = new ModelRenderer(this, 80, 15);
        this.grass.addBox(4F, 5F, -1F, 6, 5, 1);
        this.grass.setRotationPoint(0F, 14F, -8F);
        this.grass.setTextureSize(128, 64);
        this.grass.mirror = true;
        setRotation(this.grass, 0.1115358F, -0.2230717F, 0F);
        this.pouch = new ModelRenderer(this, 80, 22);
        this.pouch.addBox(-7F, 7F, 0F, 2, 5, 4);
        this.pouch.setRotationPoint(0F, 14F, 0F);
        this.pouch.setTextureSize(128, 64);
        this.pouch.mirror = true;
        setRotation(this.pouch, -0.0743572F, 0F, 0.2602503F);
    }

    public void render(TileEntityLootChest entity)
    {
        GL11.glPushMatrix();
        //    super.render(entity, f, f1, f2, f3, f4, f5);
        //    setRotationAngles(f, f1, f2, f3, f4, f5);
        float pixelRatio = 0.0625F;
        this.front.render(pixelRatio);
        this.left.render(pixelRatio);
        this.back.render(pixelRatio);
        this.right.render(pixelRatio);
        this.bottom.render(pixelRatio);
        this.cobweb.render(pixelRatio);
        this.top.render(pixelRatio);

        GL11.glTranslatef(0, entity.lockFall * 0.5F, 0);

        float x = 1.3f;
        float y = -0.4f;

        GL11.glTranslatef(0, x, y);
        GL11.glRotatef(entity.lockFall * 270, 1, 0, 0);
        GL11.glTranslatef(0, -x, -y);

        this.padlock1.render(pixelRatio);

        float z = 0.4F;
        float z1 = -0.075F;
        GL11.glTranslatef(z1, 0, -z);
        GL11.glRotatef(entity.lockFrame * 360, 0, 1, 0);
        GL11.glTranslatef(-z1, 0, z);

        this.padlock2.render(pixelRatio);

        GL11.glTranslatef(z1, 0, -z);
        GL11.glRotatef(-entity.lockFrame * 360, 0, 1, 0);
        GL11.glTranslatef(-z1, 0, z);

        GL11.glTranslatef(0, x, y);
        GL11.glRotatef(-entity.lockFall * 270, 1, 0, 0);
        GL11.glTranslatef(0, -x, -y);

        GL11.glTranslatef(0, -entity.lockFall * 0.5F, 0);

        this.lock.render(pixelRatio);
        this.grass.render(pixelRatio);
        this.pouch.render(pixelRatio);
        GL11.glPopMatrix();
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void render(ItemStack stack)
    {
        float pixelRatio = 0.0625F;
        this.front.render(pixelRatio);
        this.left.render(pixelRatio);
        this.back.render(pixelRatio);
        this.right.render(pixelRatio);
        this.bottom.render(pixelRatio);
        this.cobweb.render(pixelRatio);
        this.top.render(pixelRatio);

        this.padlock1.render(pixelRatio);
        this.padlock2.render(pixelRatio);

        this.lock.render(pixelRatio);
        this.grass.render(pixelRatio);
        this.pouch.render(pixelRatio);
    }
}
