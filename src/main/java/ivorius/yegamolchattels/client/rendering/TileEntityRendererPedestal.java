/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client.rendering;

import ivorius.ivtoolkit.bezier.IvBezierPath3D;
import ivorius.ivtoolkit.bezier.IvBezierPath3DCreator;
import ivorius.ivtoolkit.bezier.IvBezierPath3DRendererText;
import ivorius.ivtoolkit.blocks.IvMultiBlockRenderHelper;
import ivorius.ivtoolkit.rendering.IvRenderHelper;
import ivorius.ivtoolkit.tools.IvStringHelper;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.EnumPedestalEntry;
import ivorius.yegamolchattels.blocks.TileEntityPedestal;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererPedestal extends TileEntitySpecialRenderer
{
    public ResourceLocation[] textures;
    public ResourceLocation[] glowTextures;

    public ModelBase modelStoneItem;

    public IvBezierPath3D runesIronBezierPath;
    public IvBezierPath3D runesGoldBezierPath;
    public IvBezierPath3D runesDiamondBezierPath;

    public IvBezierPath3D chainsNetherBezierPath;

    public IvBezierPath3DRendererText bezierPath3DRendererText;

    public TileEntityRendererPedestal()
    {
        int pedestalEntries = EnumPedestalEntry.getNumberOfEntries();
        this.textures = new ResourceLocation[pedestalEntries];
        this.glowTextures = new ResourceLocation[pedestalEntries];
        for (int i = 0; i < textures.length; i++)
        {
            this.textures[i] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "pedestal" + i + ".png");
        }

        this.glowTextures[4] = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "pedestal4Glow.png");

        modelStoneItem = new ModelPedestalStoneItem();

        runesIronBezierPath = IvBezierPath3DCreator.createSpiraledBezierPath(0.08, 0.3, 4.0, 1.5, 0.2, -0.2, false);
        runesGoldBezierPath = IvBezierPath3DCreator.createSpiraledBezierPath(1.6, 0.3, 1.0, 2.0, 0.2, -0.2, false);
        runesDiamondBezierPath = IvBezierPath3DCreator.createSpiraledBezierPath(2.0, 0.25, 1.0, 4.0, 0.2, -0.2, true);

        bezierPath3DRendererText = new IvBezierPath3DRendererText();
        bezierPath3DRendererText.setFontRenderer(Minecraft.getMinecraft().standardGalacticFontRenderer);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderTileEntityPedestalAt((TileEntityPedestal) tileentity, d, d1, d2, f);
    }

    public void renderTileEntityPedestalAt(TileEntityPedestal tileEntity, double d, double d1, double d2, float f)
    {
        EnumPedestalEntry pedestalEntry = tileEntity.getPedestalEntry();

        if (tileEntity.isParent() && pedestalEntry != null)
        {
            float fractionDone = tileEntity.getFractionItemUp();
            int lightness = tileEntity.getWorldObj().getLightBrightnessForSkyBlocks(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 0);
            int lightnessX = lightness % 65536;
            int lightnessY = lightness / 65536;

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f, 0.5f, 0.05f);
            IvMultiBlockRenderHelper.transformFor(tileEntity, d, d1, d2);
            GL11.glPushMatrix();
            GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);

            Entity emptyEntity = new EntityArrow(tileEntity.getWorldObj());
            emptyEntity.ticksExisted = tileEntity.ticksAlive;
            emptyEntity.dimension = tileEntity.timeItemUp;
            emptyEntity.rotationYaw = tileEntity.ticksAlive + f;

            ModelBase model = pedestalEntry.model;
            ResourceLocation texture = null;
            ResourceLocation glowTexture = null;

            int index = pedestalEntry.getIntIdentifier();
            if (textures.length > index)
                texture = textures[index];

            if (glowTextures.length > index)
                glowTexture = glowTextures[index];

            if (pedestalEntry == EnumPedestalEntry.stonePedestal)
            {
                if (!itemRenderedAsBlock(tileEntity.storedItem))
                    model = modelStoneItem;
            }

            if (texture != null && model != null)
            {
                if (pedestalEntry.blendMode > 0)
                {
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glDisable(GL11.GL_ALPHA_TEST);
                }
                if (pedestalEntry.blendMode == 1)
                    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                if (pedestalEntry.blendMode == 2)
                    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
                if (!RenderManager.instance.options.fancyGraphics)
                    GL11.glEnable(GL11.GL_CULL_FACE);

                bindTexture(texture);
                model.render(emptyEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

                if (glowTexture != null)
                {
                    float alpha = (0.2f + (MathHelper.sin(tileEntity.ticksAlive * 0.05f) + 1.0f) * 0.5f * 0.2f) * fractionDone;

                    if (alpha > 0.0f)
                    {
                        GL11.glEnable(GL11.GL_BLEND);
                        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
                        GL11.glDisable(GL11.GL_ALPHA_TEST);

                        bindTexture(glowTexture);
                        int j = 240;
                        int k = lightnessY;
                        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);

                        //						GL11.glPushMatrix();
                        //						GL11.glScaled(1.01, 1.01, 1.01);
                        model.render(emptyEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                        //						GL11.glPopMatrix();

                        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightnessX / 1.0F, lightnessY / 1.0F);
                    }
                }

                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_BLEND);
                if (!RenderManager.instance.options.fancyGraphics)
                    GL11.glDisable(GL11.GL_CULL_FACE);
            }

            GL11.glPopMatrix();

            EntityItem var3 = null;

            if (tileEntity.storedItem != null)
            {
                var3 = new EntityItem(tileEntity.getWorldObj(), 0.0D, 0.0D, 0.0D, tileEntity.storedItem);
                var3.getEntityItem().stackSize = 1;
                var3.hoverStart = 0.0F;
            }

            if (!RenderManager.instance.options.fancyGraphics)
                GL11.glDisable(GL11.GL_CULL_FACE);
            RenderItem.renderInFrame = true;

            if (pedestalEntry == EnumPedestalEntry.woodPedestal)
            {
                if (var3 != null)
                {
                    GL11.glTranslated(0.0f, -0.43f, -0.2f);
                    GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                    RenderManager.instance.renderEntityWithPosYaw(var3, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                }
            }
            else if (pedestalEntry == EnumPedestalEntry.stonePedestal)
            {
                if (var3 != null)
                {
                    if (model == modelStoneItem)
                    {
                        GL11.glTranslated(0.0f, -0.3f, 0.05f);
                        GL11.glRotatef(-15.0f, 1.0f, 0.0f, 0.0f);
                    }
                    else
                    {
                        GL11.glTranslated(0.0f, -0.2f, 0.0f);
                    }

                    RenderManager.instance.renderEntityWithPosYaw(var3, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                }
            }
            else if (pedestalEntry == EnumPedestalEntry.ironPedestal)
            {
                if (var3 != null)
                {
                    GL11.glPushMatrix();
                    GL11.glTranslated(0.0f, fractionDone * 0.2f + MathHelper.sin((tileEntity.ticksAlive + f) * 0.1f) * 0.022f * fractionDone, 0.0f);
                    RenderManager.instance.renderEntityWithPosYaw(var3, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                    GL11.glPopMatrix();

                    GL11.glTranslated(0.0f, -1.75f, 0.0f);

                    String displayString = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ";
                    if (fractionDone < 1.0f)
                        displayString = IvStringHelper.cheeseString(displayString, 1.0f - fractionDone, 42);

                    bezierPath3DRendererText.setText(displayString);
                    bezierPath3DRendererText.setSpreadToFill(true);
                    bezierPath3DRendererText.setShift((tileEntity.ticksAlive + f) / 2000.0);
                    bezierPath3DRendererText.setInwards(true);
                    bezierPath3DRendererText.setCapBottom(0.0);
                    bezierPath3DRendererText.setCapTop(fractionDone);

                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glTranslatef(0.18f, 0.0f, 0.18f);
                    bezierPath3DRendererText.render(runesIronBezierPath);
                    GL11.glTranslatef(-0.18f * 2.0f, 0.0f, 0.0f);
                    bezierPath3DRendererText.render(runesIronBezierPath);
                    GL11.glTranslatef(0.0f, 0.0f, -0.18f * 2.0f);
                    bezierPath3DRendererText.render(runesIronBezierPath);
                    GL11.glTranslatef(0.18f * 2.0f, 0.0f, 0.0f);
                    bezierPath3DRendererText.render(runesIronBezierPath);
                    GL11.glEnable(GL11.GL_LIGHTING);
                }
            }
            else if (pedestalEntry == EnumPedestalEntry.goldPedestal)
            {
                if (var3 != null)
                {
                    GL11.glPushMatrix();
                    GL11.glTranslated(0.0f, fractionDone * 0.26f + MathHelper.sin((tileEntity.ticksAlive + f) * 0.1f) * 0.022f * fractionDone, 0.0f);

                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glPushMatrix();
                    GL11.glTranslated(0.0f, 0.2f, 0.0f);
                    GL11.glScaled(0.04f, 0.04f, 0.04f);
                    IvRenderHelper.renderLights(tileEntity.ticksAlive + f, 0xddddff, 0.1f * fractionDone, 30);
                    GL11.glPopMatrix();
                    GL11.glEnable(GL11.GL_LIGHTING);

                    RenderManager.instance.renderEntityWithPosYaw(var3, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

                    GL11.glPopMatrix();

                    GL11.glTranslated(0.0f, -1.75f, 0.0f);

                    String displayString = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ";
                    if (fractionDone < 1.0f)
                        displayString = IvStringHelper.cheeseString(displayString, 1.0f - fractionDone, 42);

                    bezierPath3DRendererText.setText(displayString);
                    bezierPath3DRendererText.setSpreadToFill(true);
                    bezierPath3DRendererText.setShift((tileEntity.ticksAlive + f) / 2000.0);
                    bezierPath3DRendererText.setInwards(true);
                    bezierPath3DRendererText.setCapBottom(0.0);
                    bezierPath3DRendererText.setCapTop(fractionDone);

                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glColor3f(1.0f, 1.0f, 1.0f);
                    bezierPath3DRendererText.render(runesGoldBezierPath);
                    GL11.glScaled(-1.0, 1.0, 1.0);
                    bezierPath3DRendererText.render(runesGoldBezierPath);
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                    bezierPath3DRendererText.render(runesGoldBezierPath);
                    GL11.glScaled(-1.0, 1.0, 1.0);
                    bezierPath3DRendererText.render(runesGoldBezierPath);
                    GL11.glEnable(GL11.GL_LIGHTING);
                }
            }
            else if (pedestalEntry == EnumPedestalEntry.diamondPedestal)
            {
                if (var3 != null)
                {
                    GL11.glPushMatrix();
                    GL11.glTranslated(0.0f, fractionDone * 0.32f + MathHelper.sin((tileEntity.ticksAlive + f) * 0.1f) * 0.022f * fractionDone, 0.0f);

                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glPushMatrix();
                    GL11.glTranslated(0.0f, 0.2f, 0.0f);
                    GL11.glScaled(0.07f, 0.07f, 0.07f);
                    IvRenderHelper.renderLights(tileEntity.ticksAlive + f, 0xddddff, 0.2f * fractionDone, 30);
                    IvRenderHelper.renderLights(-(tileEntity.ticksAlive + f), 0xddddff, 0.2f * fractionDone, 30);
                    GL11.glPopMatrix();
                    GL11.glEnable(GL11.GL_LIGHTING);

                    float lAlpha = (0.5f + (MathHelper.sin(tileEntity.ticksAlive * 0.05f) + 1.0f) * 0.5f * 0.2f) * fractionDone;
                    int j = (int) (lAlpha * 240);
                    int k = lightnessY;
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
                    RenderManager.instance.renderEntityWithPosYaw(var3, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightnessX / 1.0F, lightnessY / 1.0F);

                    GL11.glPopMatrix();

                    GL11.glTranslated(0.0f, -1.3f, 0.0f);

                    String displayString = "Lorem ipsum fuck you, why are you even reading this. Come one, a mere mortal is not supposed to decipher something on the likes like the galactic alphabet. There is really no joke here... All that work for nothing. Suits you right!";
                    if (fractionDone < 1.0f)
                        displayString = IvStringHelper.cheeseString(displayString, 1.0f - fractionDone, 42);

                    bezierPath3DRendererText.setText(displayString);
                    bezierPath3DRendererText.setSpreadToFill(true);
                    bezierPath3DRendererText.setShift((tileEntity.ticksAlive + f) / 2000.0);
                    bezierPath3DRendererText.setInwards(true);
                    bezierPath3DRendererText.setCapBottom(0.0);
                    bezierPath3DRendererText.setCapTop(fractionDone);

                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glColor3f(1.0f, 1.0f, 1.0f);
                    bezierPath3DRendererText.render(runesDiamondBezierPath);
                    GL11.glScaled(-1.0, 1.0, 1.0);
                    bezierPath3DRendererText.render(runesDiamondBezierPath);
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                    bezierPath3DRendererText.render(runesDiamondBezierPath);
                    GL11.glScaled(-1.0, 1.0, 1.0);
                    bezierPath3DRendererText.render(runesDiamondBezierPath);
                    GL11.glEnable(GL11.GL_LIGHTING);
                }
            }
//            else if (pedestalEntry == EnumPedestalEntry.netherPedestal)
//            {
//                double itemHeight = fractionDone * 0.32f + MathHelper.sin((tileEntity.ticksAlive + f) * 0.1f) * 0.022f * fractionDone;
//
//                double chainLength = IvMathHelper.clamp(0.0, (fractionDone - 0.6) * 10.0, 1.0);
//
//                GL11.glDisable(GL11.GL_CULL_FACE);
//                String chainString = "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo";
//                for (int i = 0; i < 4; i++)
//                {
//                    float basisHeight = MathHelper.sin((tileEntity.ticksAlive + f) * 0.04f + 3.1415926f * 0.5f * i) * 0.2f;
//                    IvBezierPoint3D[] chainsNetherPoints = new IvBezierPoint3D[] { new IvBezierPoint3D(new double[] { 2.5, 0.5 + basisHeight, 0.0 }, new double[] { -0.5, -0.2, 0.0 }, 0x000000, 0.0, 0.3), new IvBezierPoint3D(new double[] { 0.0, itemHeight + 0.15, 0.0 }, new double[] { -0.5, 0.5, 0.0 }, 0x000000, 0.0, 0.15) };
//                    chainsNetherBezierPath = IvBezierPath3DCreator.createQuickBezierPath(chainsNetherPoints);
//
//                    GL11.glRotatef(90.0f * i + 45.0f, 0.0f, 1.0f, 0.0f);
//                    GL11.glTranslatef(0.05f, 0.0f, 0.0f);
//
//                    GL11.glTranslatef(2.6f, 0.0f, 0.0f);
//                    GL11.glTranslatef(0.0f, 0.0f + basisHeight, 0.0f);
//                    GL11.glScalef(0.4f, 0.4f, 0.4f);
//                    GL11.glColor3f(1.0f, 1.0f, 1.0f);
//                    this.bindTextureByName(texture);
//                    model.render(emptyEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
//                    GL11.glScalef(2.5f, 2.5f, 2.5f);
//                    GL11.glTranslatef(0.0f, -(0.0f + basisHeight), 0.0f);
//                    GL11.glTranslatef(-2.6f, 0.0f, 0.0f);
//
//                    chainsNetherBezierPath.render(chainString, fontRenderer, true, 0.0, false, 0.0f, chainLength);
//                    GL11.glTranslatef(-0.05f, 0.0f, 0.0f);
//                    GL11.glRotatef(-(90.0f * i + 45.0f), 0.0f, 1.0f, 0.0f);
//                }
//                GL11.glEnable(GL11.GL_CULL_FACE);
//
//                if (var3 != null)
//                {
//                    GL11.glPushMatrix();
//                    GL11.glTranslated(0.0f, itemHeight, 0.0f);
//
//                    GL11.glEnable(GL11.GL_BLEND);
//                    GL11.glDisable(GL11.GL_ALPHA_TEST);
//
//                    if (fractionDone <= 0.4f)
//                    {
//                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//                        RenderManager.instance.renderEntityWithPosYaw(var3, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f);
//                    }
//                    else
//                    {
//                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
//                        RenderManager.instance.renderEntityWithPosYaw(var3, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f);
//
//                        GL11.glPushMatrix();
//                        GL11.glScaled(2.0f, 2.0f, 2.0f);
//                        GL11.glTranslated(0.0f, -0.1f, 0.0f);
//                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
//                        GL14.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
//                        RenderManager.instance.renderEntityWithPosYaw(var3, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
//                        GL11.glPopMatrix();
//                        GL14.glBlendEquation(GL14.GL_FUNC_ADD);
//                    }
//
//                    GL11.glDisable(GL11.GL_BLEND);
//                    GL11.glEnable(GL11.GL_ALPHA_TEST);
//                }
//
//                GL11.glPopMatrix();
//            }

            RenderItem.renderInFrame = false;
            if (!RenderManager.instance.options.fancyGraphics)
                GL11.glEnable(GL11.GL_CULL_FACE);

            GL11.glPopMatrix();
        }
    }

    public static boolean itemRenderedAsBlock(ItemStack stack)
    {
        Block block = null;
        if (stack != null)
        {
            block = Block.getBlockFromItem(stack.getItem());
        }

        if (block != null && RenderBlocks.renderItemIn3d(block.getRenderType()))
            return true;

        return false;
    }
}
