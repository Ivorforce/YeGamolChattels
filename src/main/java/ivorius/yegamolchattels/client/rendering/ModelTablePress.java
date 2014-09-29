package ivorius.yegamolchattels.client.rendering;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelTablePress extends ModelBase
{
    ModelRenderer Top1;
    ModelRenderer Leg1;
    ModelRenderer Leg2;
    ModelRenderer bar1;
    ModelRenderer bar3;
    ModelRenderer Top2;
    ModelRenderer bar4;
    ModelRenderer bar2;
    ModelRenderer Leg3;
    ModelRenderer Leg4;
    ModelRenderer Press1;
    ModelRenderer screw1;
    ModelRenderer screw2;
    ModelRenderer screw3;
    ModelRenderer Press1m4;
    ModelRenderer Press1m5;
    ModelRenderer Press1m6;
    ModelRenderer Press2;
    ModelRenderer screw4;
    ModelRenderer screw5;
    ModelRenderer screw6;
    ModelRenderer Press2m4;
    ModelRenderer Press2m5;
    ModelRenderer Press2m6;

    public ModelTablePress()
    {
        this(0.0f);
    }

    public ModelTablePress(float par1)
    {
        Top1 = new ModelRenderer(this, 0, 91);
        Top1.setTextureSize(256, 128);
        Top1.addBox(-16F, -2F, -16F, 32, 4, 32);
        Top1.setRotationPoint(-16F, -4F, 0F);
        Leg1 = new ModelRenderer(this, 6, 87);
        Leg1.setTextureSize(256, 128);
        Leg1.addBox(-2F, -13F, -2F, 4, 26, 4);
        Leg1.setRotationPoint(-30F, 11F, -14F);
        Leg2 = new ModelRenderer(this, 6, 87);
        Leg2.setTextureSize(256, 128);
        Leg2.addBox(-2F, -13F, -2F, 4, 26, 4);
        Leg2.setRotationPoint(-30F, 11F, 14F);
        bar1 = new ModelRenderer(this, 35, 59);
        bar1.setTextureSize(256, 128);
        bar1.addBox(-2F, -1F, -12F, 4, 2, 24);
        bar1.setRotationPoint(-30F, 18F, 0F);
        bar3 = new ModelRenderer(this, 31, 52);
        bar3.setTextureSize(256, 128);
        bar3.addBox(-14F, -1F, -2F, 28, 2, 4);
        bar3.setRotationPoint(-14F, 18F, 0F);
        Top2 = new ModelRenderer(this, 0, 91);
        Top2.setTextureSize(256, 128);
        Top2.addBox(-16F, -2F, -16F, 32, 4, 32);
        Top2.setRotationPoint(16F, -4F, 0F);
        bar4 = new ModelRenderer(this, 31, 52);
        bar4.setTextureSize(256, 128);
        bar4.addBox(-14F, -1F, -2F, 28, 2, 4);
        bar4.setRotationPoint(14F, 18F, 0F);
        bar2 = new ModelRenderer(this, 35, 59);
        bar2.setTextureSize(256, 128);
        bar2.addBox(-2F, -1F, -12F, 4, 2, 24);
        bar2.setRotationPoint(30F, 18F, 0F);
        Leg3 = new ModelRenderer(this, 6, 87);
        Leg3.setTextureSize(256, 128);
        Leg3.addBox(-2F, -13F, -2F, 4, 26, 4);
        Leg3.setRotationPoint(30F, 11F, -14F);
        Leg4 = new ModelRenderer(this, 6, 87);
        Leg4.setTextureSize(256, 128);
        Leg4.addBox(-2F, -13F, -2F, 4, 26, 4);
        Leg4.setRotationPoint(30F, 11F, 14F);
        Press1 = new ModelRenderer(this, 10, 11);
        Press1.setTextureSize(256, 128);
        Press1.addBox(-1.5F, -4F, -0.5F, 3, 8, 1);
        Press1.setRotationPoint(-20F, -4F, 19F);
        screw1 = new ModelRenderer(this, 28, 28);
        screw1.setTextureSize(256, 128);
        screw1.addBox(-0.51F, -0.5F, -2F, 1, 1, 4);
        screw1.setRotationPoint(-20F, 12.5F, 14F);
        screw2 = new ModelRenderer(this, 31, 11);
        screw2.setTextureSize(256, 128);
        screw2.addBox(-0.5F, -7.5F, -0.5F, 1, 15, 1);
        screw2.setRotationPoint(-20F, 6.5F, 14F);
        screw3 = new ModelRenderer(this, 29, 7);
        screw3.setTextureSize(256, 128);
        screw3.addBox(-1.05F, -0.5F, -1F, 2, 1, 2);
        screw3.setRotationPoint(-20F, -1.5F, 14F);
        Press1m4 = new ModelRenderer(this, 1, 0);
        Press1m4.setTextureSize(256, 128);
        Press1m4.addBox(-1.5F, -1F, -3.5F, 3, 2, 7);
        Press1m4.setRotationPoint(-20F, -9F, 16F);
        Press1m5 = new ModelRenderer(this, 1, 21);
        Press1m5.setTextureSize(256, 128);
        Press1m5.addBox(-1.5F, -1F, -3.5F, 3, 2, 7);
        Press1m5.setRotationPoint(-20F, 1F, 16F);
        Press1m6 = new ModelRenderer(this, 2, 9);
        Press1m6.setTextureSize(256, 128);
        Press1m6.addBox(-1.5F, -5.5F, -0.5F, 3, 11, 1);
        Press1m6.setRotationPoint(-20F, -4F, 20F);
        Press2 = new ModelRenderer(this, 10, 11);
        Press2.setTextureSize(256, 128);
        Press2.addBox(-1.5F, -4F, -0.5F, 3, 8, 1);
        Press2.setRotationPoint(21F, -4F, 19F);
        screw4 = new ModelRenderer(this, 28, 28);
        screw4.setTextureSize(256, 128);
        screw4.addBox(-0.51F, -0.5F, -2F, 1, 1, 4);
        screw4.setRotationPoint(21F, 12.5F, 14F);
        screw5 = new ModelRenderer(this, 31, 11);
        screw5.setTextureSize(256, 128);
        screw5.addBox(-0.5F, -7.5F, -0.5F, 1, 15, 1);
        screw5.setRotationPoint(21F, 6.5F, 14F);
        screw6 = new ModelRenderer(this, 29, 7);
        screw6.setTextureSize(256, 128);
        screw6.addBox(-1.05F, -0.5F, -1F, 2, 1, 2);
        screw6.setRotationPoint(21F, -1.5F, 14F);
        Press2m4 = new ModelRenderer(this, 1, 0);
        Press2m4.setTextureSize(256, 128);
        Press2m4.addBox(-1.5F, -1F, -3.5F, 3, 2, 7);
        Press2m4.setRotationPoint(21F, -9F, 16F);
        Press2m5 = new ModelRenderer(this, 1, 21);
        Press2m5.setTextureSize(256, 128);
        Press2m5.addBox(-1.5F, -1F, -3.5F, 3, 2, 7);
        Press2m5.setRotationPoint(21F, 1F, 16F);
        Press2m6 = new ModelRenderer(this, 2, 9);
        Press2m6.setTextureSize(256, 128);
        Press2m6.addBox(-1.5F, -5.5F, -0.5F, 3, 11, 1);
        Press2m6.setRotationPoint(21F, -4F, 20F);
    }

    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        Top1.rotateAngleX = 0F;
        Top1.rotateAngleY = 0F;
        Top1.rotateAngleZ = 0F;
        Top1.renderWithRotation(par7);

        Leg1.rotateAngleX = 0F;
        Leg1.rotateAngleY = 0F;
        Leg1.rotateAngleZ = 0F;
        Leg1.renderWithRotation(par7);

        Leg2.rotateAngleX = 0F;
        Leg2.rotateAngleY = 0F;
        Leg2.rotateAngleZ = 0F;
        Leg2.renderWithRotation(par7);

        bar1.rotateAngleX = 0F;
        bar1.rotateAngleY = 0F;
        bar1.rotateAngleZ = 0F;
        bar1.renderWithRotation(par7);

        bar3.rotateAngleX = 0F;
        bar3.rotateAngleY = 0F;
        bar3.rotateAngleZ = 0F;
        bar3.renderWithRotation(par7);

        Top2.rotateAngleX = 0F;
        Top2.rotateAngleY = 0F;
        Top2.rotateAngleZ = 0F;
        Top2.renderWithRotation(par7);

        bar4.rotateAngleX = 0F;
        bar4.rotateAngleY = 0F;
        bar4.rotateAngleZ = 0F;
        bar4.renderWithRotation(par7);

        bar2.rotateAngleX = 0F;
        bar2.rotateAngleY = 0F;
        bar2.rotateAngleZ = 0F;
        bar2.renderWithRotation(par7);

        Leg3.rotateAngleX = 0F;
        Leg3.rotateAngleY = 0F;
        Leg3.rotateAngleZ = 0F;
        Leg3.renderWithRotation(par7);

        Leg4.rotateAngleX = 0F;
        Leg4.rotateAngleY = 0F;
        Leg4.rotateAngleZ = 0F;
        Leg4.renderWithRotation(par7);

        Press1.rotateAngleX = 0F;
        Press1.rotateAngleY = 0F;
        Press1.rotateAngleZ = 0F;
        Press1.renderWithRotation(par7);

        screw1.rotateAngleX = 0F;
        screw1.rotateAngleY = 0F;
        screw1.rotateAngleZ = 0F;
        screw1.renderWithRotation(par7);

        screw2.rotateAngleX = 0F;
        screw2.rotateAngleY = 0F;
        screw2.rotateAngleZ = 0F;
        screw2.renderWithRotation(par7);

        screw3.rotateAngleX = 0F;
        screw3.rotateAngleY = 0F;
        screw3.rotateAngleZ = 0F;
        screw3.renderWithRotation(par7);

        Press1m4.rotateAngleX = 0F;
        Press1m4.rotateAngleY = 0F;
        Press1m4.rotateAngleZ = 0F;
        Press1m4.renderWithRotation(par7);

        Press1m5.rotateAngleX = 0F;
        Press1m5.rotateAngleY = 0F;
        Press1m5.rotateAngleZ = 0F;
        Press1m5.renderWithRotation(par7);

        Press1m6.rotateAngleX = 0F;
        Press1m6.rotateAngleY = 0F;
        Press1m6.rotateAngleZ = 0F;
        Press1m6.renderWithRotation(par7);

        Press2.rotateAngleX = 0F;
        Press2.rotateAngleY = 0F;
        Press2.rotateAngleZ = 0F;
        Press2.renderWithRotation(par7);

        screw4.rotateAngleX = 0F;
        screw4.rotateAngleY = 0F;
        screw4.rotateAngleZ = 0F;
        screw4.renderWithRotation(par7);

        screw5.rotateAngleX = 0F;
        screw5.rotateAngleY = 0F;
        screw5.rotateAngleZ = 0F;
        screw5.renderWithRotation(par7);

        screw6.rotateAngleX = 0F;
        screw6.rotateAngleY = 0F;
        screw6.rotateAngleZ = 0F;
        screw6.renderWithRotation(par7);

        Press2m4.rotateAngleX = 0F;
        Press2m4.rotateAngleY = 0F;
        Press2m4.rotateAngleZ = 0F;
        Press2m4.renderWithRotation(par7);

        Press2m5.rotateAngleX = 0F;
        Press2m5.rotateAngleY = 0F;
        Press2m5.rotateAngleZ = 0F;
        Press2m5.renderWithRotation(par7);

        Press2m6.rotateAngleX = 0F;
        Press2m6.rotateAngleY = 0F;
        Press2m6.rotateAngleZ = 0F;
        Press2m6.renderWithRotation(par7);

    }

}
