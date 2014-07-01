/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import java.util.Random;

public class ModelGhost extends ModelBase
{
    public ModelGhost()
    {
        tentacles = new ModelRenderer[6];
        byte byte0 = -16;

        head = new ModelRenderer(this, 0, 0);
        head.addBox(-4F, -8F, -4F, 8, 8, 8, 0);
        head.setRotationPoint(0.0F, 0.0F, 0.0F);

        body = new ModelRenderer(this, 16, 16);
        body.addBox(-4F, 0.0F, -2F, 8, 12, 4, 0.0F);
        body.setRotationPoint(0.0F, 0.0F, 0.0F);

        rightArm = new ModelRenderer(this, 40, 16);
        rightArm.addBox(-3F, -2F, -2F, 4, 12, 4, 0);
        rightArm.setRotationPoint(-5F, 2.0F, 0.0F);
        leftArm = new ModelRenderer(this, 40, 16);
        leftArm.mirror = true;
        leftArm.addBox(-1F, -2F, -2F, 4, 12, 4, 0);
        leftArm.setRotationPoint(5F, 2.0F, 0.0F);

        Random random = new Random(1660L);
        for (int i = 0; i < tentacles.length; i++)
        {
            tentacles[i] = new ModelRenderer(this, 0, 16);
            float f = ((((i % 3 - (i / 3) % 2 * 0.3F) + 0.15F) / 2.0F) * 2.0F - 1.0F) * 3F;
            float f1 = ((i / 3 / 2.0F) * 1.6F - 0.4F) * 3F;
            int j = random.nextInt(7) + 8;
            tentacles[i].addBox(-0.8F, 0.0F, -0.8F, 2, j, 2);
            tentacles[i].rotationPointX = f;
            tentacles[i].rotationPointZ = f1;
            tentacles[i].rotationPointY = 28 + byte0;
        }

    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
    {
        for (int i = 0; i < tentacles.length; i++)
        {
            tentacles[i].rotateAngleX = 0.2F * MathHelper.sin(f2 * 0.3F + i) + 0.4F;
        }

        float f6 = MathHelper.sin(onGround * 3.141593F);
        float f7 = MathHelper.sin((1.0F - (1.0F - onGround) * (1.0F - onGround)) * 3.141593F);
        rightArm.rotateAngleZ = 0.0F;
        leftArm.rotateAngleZ = 0.0F;
        rightArm.rotateAngleY = -(0.1F - f6 * 0.6F);
        leftArm.rotateAngleY = 0.1F - f6 * 0.6F;
        rightArm.rotateAngleX = -1.570796F;
        leftArm.rotateAngleX = -1.570796F;
        rightArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
        leftArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
        rightArm.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
        leftArm.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
        rightArm.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.05F;
        leftArm.rotateAngleX -= MathHelper.sin(f2 * 0.067F) * 0.05F;
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        setRotationAngles(f, f1, f2, f3, f4, f5);
        body.render(f5);
        head.render(f5);
        rightArm.render(f5);
        leftArm.render(f5);

        for (int i = 0; i < tentacles.length; i++)
        {
            tentacles[i].render(f5);
        }
    }

    ModelRenderer body;
    ModelRenderer head;
    ModelRenderer rightArm;
    ModelRenderer leftArm;
    ModelRenderer tentacles[];
}
