/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.gui;

import ivorius.ivtoolkit.gui.GuiControlListener;
import ivorius.ivtoolkit.gui.GuiSlider;
import ivorius.ivtoolkit.network.PacketGuiAction;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityStatue;
import ivorius.yegamolchattels.items.ItemEntityVita;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Created by lukas on 27.07.14.
 */
public class GuiScreenCarveStatue extends GuiContainer implements GuiControlListener<GuiSlider>
{
    private static ResourceLocation guiTexture = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "guiCarving.png");

    private GuiSlider sliderSwing;
    private GuiSlider sliderStance;

    private GuiSlider sliderYawHead;
    private GuiSlider sliderPitchHead;

    public GuiScreenCarveStatue(EntityPlayer player, int x, int y, int z)
    {
        super(new ContainerCarveStatue(player.inventory, player, x, y, z));
    }

    @Override
    public void initGui()
    {
        super.initGui();

        buttonList.add(new GuiButton(0, width / 2 - 80, height / 2 - 22, 85, 20, I18n.format("gui.carve.confirm")));
        buttonList.add(sliderSwing = new GuiSlider(50, width / 2 - 80, height / 2 - 80, 85, 20, I18n.format("gui.carve.swing")));
        sliderSwing.addListener(this);
        sliderSwing.setMinValue(0.0f);
        sliderSwing.setMaxValue(5.0f);
        buttonList.add(sliderStance = new GuiSlider(50, width / 2 - 80, height / 2 - 60, 85, 20, I18n.format("gui.carve.stance")));
        sliderStance.addListener(this);
        sliderStance.setMinValue(0.0f);
        sliderStance.setMaxValue(5.0f);
        buttonList.add(sliderYawHead = new GuiSlider(50, width / 2 - 80, height / 2 - 100, 85, 20, I18n.format("gui.carve.head.yaw")));
        sliderYawHead.addListener(this);
        sliderYawHead.setMinValue(-60.0f);
        sliderYawHead.setMaxValue(60.0f);
        buttonList.add(sliderPitchHead = new GuiSlider(50, width / 2 - 80, height / 2 - 120, 85, 20, I18n.format("gui.carve.head.pitch")));
        sliderPitchHead.addListener(this);
        sliderPitchHead.setMinValue(-60.0f);
        sliderPitchHead.setMaxValue(60.0f);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 0)
        {
            YeGamolChattels.network.sendToServer(PacketGuiAction.packetGuiAction("carveStatue", sliderYawHead.getValue(), sliderPitchHead.getValue(), sliderSwing.getValue(), sliderStance.getValue()));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        this.mc.getTextureManager().bindTexture(guiTexture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        ItemStack currentVita = ((ContainerCarveStatue) inventorySlots).statueEntityCarvingInventory.getStackInSlot(0);
        if (currentVita != null)
        {
            Entity entity = ItemEntityVita.createEntity(currentVita, mc.theWorld);

            if (entity != null)
            {
                if (entity instanceof EntityLivingBase)
                    TileEntityStatue.setRotations((EntityLivingBase) entity, sliderYawHead.getValue(), sliderPitchHead.getValue(), sliderSwing.getValue(), sliderStance.getValue());

                float scale = 50f / (entity instanceof EntitySquid ? 2.5f : entity.height);
                renderEntity(width / 2 + 45, height / 2 - 20, scale, entity);
            }
        }
    }

    public static void renderEntity(int x, int y, float scale, Entity entity)
    {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, 50.0F);
        GL11.glScalef(-scale, scale, scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, entity.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Override
    public void valueChanged(GuiSlider gui)
    {

    }
}
