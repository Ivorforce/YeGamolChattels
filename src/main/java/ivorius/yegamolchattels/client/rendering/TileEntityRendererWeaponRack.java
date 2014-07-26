/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;

import ivorius.ivtoolkit.blocks.IvRotatableBlockRenderHelper;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityWeaponRack;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererWeaponRack extends TileEntitySpecialRenderer
{
    public ModelWeaponRack rackModelFloor;
    public float[] weaponSlotPositionsFloor = new float[]{-0.28f, -0.12f, 0.13f, 0.29f};
    public ModelWeaponRackWall rackModelWall;
    public float[] weaponSlotPositionsWall = new float[]{0.4f, -0.55f};

    public ResourceLocation[] baseTextures = new ResourceLocation[3];
    public ResourceLocation[] baseTexturesWall = new ResourceLocation[3];

    public ModelBase[] detailModels = new ModelBase[6];
    public ModelBase[] detailModelsWall = new ModelBase[6];
    public ResourceLocation[] detailTextures = new ResourceLocation[6];
    public ResourceLocation[] detailTexturesWall = new ResourceLocation[6];

    public TileEntityRendererWeaponRack()
    {
        this.rackModelFloor = new ModelWeaponRack();
        this.rackModelWall = new ModelWeaponRackWall();

        baseTextures[0] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTexture.png");
        baseTextures[1] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTextureCarved.png");
        baseTextures[2] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTextureDamaged.png");
        baseTexturesWall[0] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTextureWall.png");
        baseTexturesWall[1] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTextureCarvedWall.png");
        baseTexturesWall[2] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTextureDamagedWall.png");

        detailModels[0] = new ModelWeaponRackDetailMushrooms();
        detailTextures[0] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTextureDetailMushrooms.png");
        detailModelsWall[0] = new ModelWeaponRackDetailMushroomsWall();
        detailTexturesWall[0] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTextureDetailMushroomsWall.png");

        detailModels[1] = new ModelWeaponRackDetailBonemeal();
        detailTextures[1] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTextureDetailBonemeal.png");
        detailModelsWall[1] = new ModelWeaponRackDetailBonemealWall();
        detailTexturesWall[1] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTextureDetailBonemealWall.png");

        detailModels[2] = new ModelWeaponRackDetailLeather();
        detailTextures[2] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTextureDetailLeather.png");
        detailModelsWall[2] = new ModelWeaponRackDetailLeatherWall();
        detailTexturesWall[2] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTextureDetailLeatherWall.png");

        detailModels[3] = new ModelWeaponRackDetailCobweb();
        detailTextures[3] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTextureDetailCobweb.png");
        detailModelsWall[3] = new ModelWeaponRackDetailCobwebWall();
        detailTexturesWall[3] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "weaponRackTextureDetailCobwebWall.png");
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderTileEntityWeaponRackAt((TileEntityWeaponRack) tileentity, d, d1, d2, f);
    }

    public void renderTileEntityWeaponRackAt(TileEntityWeaponRack tileEntity, double d, double d1, double d2, float f)
    {
        int direction = tileEntity.getDirection();
        int type = tileEntity.getWeaponRackType();

        GL11.glPushMatrix();
        IvRotatableBlockRenderHelper.transformFor(tileEntity, d, d1, d2);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 1.0f, 0.0f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);

        int baseTextureIndex = tileEntity.effectsApplied[5] ? 2 : (tileEntity.effectsApplied[4] ? 1 : 0);

        bindTexture((type == TileEntityWeaponRack.weaponRackTypeWall ? baseTexturesWall : baseTextures)[baseTextureIndex]);
        (type == TileEntityWeaponRack.weaponRackTypeFloor ? rackModelFloor : rackModelWall).render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        for (int i = 0; i < tileEntity.effectsApplied.length; i++)
        {
            if (tileEntity.effectsApplied[i] && (type == TileEntityWeaponRack.weaponRackTypeFloor ? detailModels[i] : detailModelsWall[i]) != null)
            {
                bindTexture((type == TileEntityWeaponRack.weaponRackTypeWall ? detailTexturesWall : detailTextures)[i]);
                (type == TileEntityWeaponRack.weaponRackTypeFloor ? detailModels[i] : detailModelsWall[i]).render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
            }
        }

        GL11.glPopMatrix();

        for (int i = 0; i < tileEntity.getStoredWeaponSlots(); i++)
        {
            if (tileEntity.storedWeapons[i] != null)
            {
                float swing = 0.0f;
                if (tileEntity.storedWeaponsSwinging[i] < 1.0f)
                    swing = MathHelper.sin(tileEntity.storedWeaponsSwinging[i] * 5.0f) * (1.0f - tileEntity.storedWeaponsSwinging[i]);

                GL11.glPushMatrix();

                if (type == TileEntityWeaponRack.weaponRackTypeFloor)
                {
                    GL11.glTranslatef(weaponSlotPositionsFloor[i], 0.6F, 0.3F);
                    GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                    GL11.glRotatef(-120.0f + swing * 10.0f, 0.0f, 0.0f, 1.0f);
                    GL11.glTranslatef(0.1f, 0.0f, 0.0f);
                }
                else if (type == TileEntityWeaponRack.weaponRackTypeWall)
                {
                    GL11.glTranslatef(0.0f, weaponSlotPositionsWall[i], -0.3F);
                    GL11.glRotatef(135.0f + (i == 1 ? 180.0f : 0.0f), 0.0f, 0.0f, 1.0f);
                }

                GL11.glScaled(1.8, 1.8, 1.8);

                renderItem(tileEntity.getWorldObj(), tileEntity.storedWeapons[i]);

                GL11.glPopMatrix();
            }
        }

        GL11.glPopMatrix();

//		if (Minecraft.getMinecraft().thePlayer.isSneaking())
//			IvRaytracer.drawStandardOutlinesFromTileEntity(tileEntity.getRaytraceableObjects(), d, d1, d2, tileEntity);
    }

    public static void renderItem(World world, ItemStack stack)
    {
        EntityItem entityItem = new EntityItem(world, 0.0D, 0.0D, 0.0D, stack);
        entityItem.hoverStart = 0.0F;

        if (!RenderManager.instance.options.fancyGraphics)
            GL11.glDisable(GL11.GL_CULL_FACE);

        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(entityItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;

        if (!RenderManager.instance.options.fancyGraphics)
            GL11.glEnable(GL11.GL_CULL_FACE);
    }
}
