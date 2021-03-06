/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

// Date: 30-3-2013 8:59:42
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX

package ivorius.yegamolchattels.client.rendering;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelWeaponRackDetailCobwebWall extends ModelBase
{
    //fields
    ModelRenderer cobweb1;
    ModelRenderer cobweb2;

    public ModelWeaponRackDetailCobwebWall()
    {
        textureWidth = 32;
        textureHeight = 32;

        cobweb1 = new ModelRenderer(this, 0, 0);
        cobweb1.addBox(0F, 0F, 0F, 7, 7, 0);
        cobweb1.setRotationPoint(-3F, 8F, 7F);
        cobweb1.setTextureSize(32, 32);
        cobweb1.mirror = true;
        setRotation(cobweb1, -0.1115358F, 0F, 0F);
        cobweb2 = new ModelRenderer(this, 0, 8);
        cobweb2.addBox(0F, 0F, 0F, 6, 5, 0);
        cobweb2.setRotationPoint(-7F, 15F, 5F);
        cobweb2.setTextureSize(32, 32);
        cobweb2.mirror = true;
        setRotation(cobweb2, 0.1858931F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        cobweb1.render(f5);
        cobweb2.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
