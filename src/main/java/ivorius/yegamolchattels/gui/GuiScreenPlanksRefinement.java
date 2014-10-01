package ivorius.yegamolchattels.gui;

import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityPlanksRefinement;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Created by lukas on 04.05.14.
 */
public class GuiScreenPlanksRefinement extends GuiScreen
{
    public static ResourceLocation planksRefinementGui = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "planksRefinementGui.png");

    public TileEntityPlanksRefinement tileEntity;
    RenderItem renderItem;

    public int mouseButtonDown = -1;
    public int mouseLastKnownX;
    public int mouseLastKnownY;
    public int mouseLastKnownSpeedX;
    public int mouseLastKnownSpeedY;

    public int ticksMouseDown;

    public static final int PLANK_WIDTH = 16 * 8;
    public static final int PLANK_HEIGHT = 16 * 8;

    public static final int PLANK_SHIFT_X = 0;
    public static final int PLANK_SHIFT_Y = -1;

    public GuiScreenPlanksRefinement(World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityPlanksRefinement)
            this.tileEntity = (TileEntityPlanksRefinement) tileEntity;

        renderItem = new RenderItem();
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (tileEntity == null || tileEntity.isInvalid())
            mc.thePlayer.closeScreen();

        ItemStack tool = mc.thePlayer.getHeldItem();
        if (mouseButtonDown == 0)
        {
            if (tileEntity.isCorrectTool(tool))
            {
                float leftX = TileEntityPlanksRefinement.REFINEMENT_SLOTS_X * PLANK_WIDTH / 16.0f;
                float leftY = TileEntityPlanksRefinement.REFINEMENT_SLOTS_Y * PLANK_HEIGHT / 16.0f;

                float usedX = (mouseLastKnownX - (width / 2 - leftX * 0.5f)) / PLANK_WIDTH;
                float usedY = (mouseLastKnownY - (height / 2 - leftY * 0.5f)) / PLANK_HEIGHT;
                float mouseSpeed = MathHelper.sqrt_float(mouseLastKnownSpeedX * mouseLastKnownSpeedX + mouseLastKnownSpeedY * mouseLastKnownSpeedY);

                tileEntity.refineWithItem(mc.thePlayer, usedX * 16.0f, usedY * 16.0f, mouseSpeed);
            }

            ticksMouseDown++;
        }
        else
            ticksMouseDown = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        drawDefaultBackground();

        this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("guiPlanksRefinementFrame.title"), this.width / 2, 40, 16777215);

        if (tileEntity != null)
        {
            ItemStack stack = tileEntity.containedItem;
            Tessellator tessellator = Tessellator.instance;
            ItemStack heldItem = mc.thePlayer.getHeldItem();

            if (stack != null)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(width / 2, height / 2, 0);
                GL11.glScalef(PLANK_WIDTH / 16.0f, PLANK_HEIGHT / 16.0f, 1.0f);
                GL11.glTranslatef(-8, -8, 0);
                renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, 0, 0);

                RenderHelper.disableStandardItemLighting();
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glShadeModel(GL11.GL_SMOOTH);

                float colorR = stack.getItem() == YGCItems.plank ? 1.0f : 0.2f;
                float colorG = stack.getItem() == YGCItems.plank ? 1.0f : 0.0f;
                float colorB = stack.getItem() == YGCItems.plank ? 1.0f : 0.0f;

                float transX = (16 - TileEntityPlanksRefinement.REFINEMENT_SLOTS_X + PLANK_SHIFT_X) * 0.5f;
                float transY = (16 - TileEntityPlanksRefinement.REFINEMENT_SLOTS_Y + PLANK_SHIFT_Y) * 0.5f;
                for (int slotX = 0; slotX < TileEntityPlanksRefinement.REFINEMENT_SLOTS_X; slotX++)
                    for (int slotY = 0; slotY < TileEntityPlanksRefinement.REFINEMENT_SLOTS_Y; slotY++)
                    {
                        float sX = slotX + transX;
                        float sY = slotY + transY;

                        float eX = (slotX + 1) + transX;
                        float eY = (slotY + 1) + transY;

                        float cX = (sX + eX) * 0.5f;
                        float cY = (sY + eY) * 0.5f;

                        float refFirstV = getCombinedRefinementSafe(slotX, slotY, -1, 1);

                        tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
                        tessellator.setColorRGBA_F(colorR, colorG, colorB, getRefinementSafe(slotX, slotY));
                        tessellator.addVertex(cX, cY, zLevel);

                        tessellator.setColorRGBA_F(colorR, colorG, colorB, refFirstV);
                        tessellator.addVertex(sX, eY, zLevel);

                        tessellator.setColorRGBA_F(colorR, colorG, colorB, getCombinedRefinementSafe(slotX, slotY, 1, 1));
                        tessellator.addVertex(eX, eY, zLevel);

                        tessellator.setColorRGBA_F(colorR, colorG, colorB, getCombinedRefinementSafe(slotX, slotY, 1, -1));
                        tessellator.addVertex(eX, sY, zLevel);

                        tessellator.setColorRGBA_F(colorR, colorG, colorB, getCombinedRefinementSafe(slotX, slotY, -1, -1));
                        tessellator.addVertex(sX, sY, zLevel);

                        tessellator.setColorRGBA_F(colorR, colorG, colorB, refFirstV);
                        tessellator.addVertex(sX, eY, zLevel);

                        tessellator.draw();
                    }

                GL11.glShadeModel(GL11.GL_FLAT);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);

                GL11.glPopMatrix();
            }

            if (heldItem != null)
            {
                float swipeX = mouseButtonDown >= 0 ? MathHelper.sin((ticksMouseDown + partialTicks) * 1.2f) : 0;

                GL11.glPushMatrix();
                GL11.glTranslatef(mouseX + swipeX * 2.0f, mouseY, 0);
                GL11.glScalef(3.0f, 3.0f, 1.0f);
                GL11.glTranslated(-8, -8, 0);
                renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), heldItem, 0, 0);
                GL11.glPopMatrix();
            }
        }
    }

    private float getCombinedRefinementSafe(int x, int y, int dirX, int dirY)
    {
        return getRefinementSafe(x, y) * 0.25f + getRefinementSafe(x + dirX, y) * 0.25f + getRefinementSafe(x, y + dirY) * 0.25f + getRefinementSafe(x + dirX, y + dirY) * 0.25f;
    }

    private float getRefinementSafe(int x, int y)
    {
        x = MathHelper.clamp_int(x, 0, TileEntityPlanksRefinement.REFINEMENT_SLOTS_X - 1);
        y = MathHelper.clamp_int(y, 0, TileEntityPlanksRefinement.REFINEMENT_SLOTS_Y - 1);

        return Math.min(tileEntity.getRefinement(x, y), 1.4f) * 0.15f;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int buttonClicked)
    {
        super.mouseClicked(mouseX, mouseY, buttonClicked);

        if (buttonClicked == 0)
        {
            mouseLastKnownSpeedX = 0;
            mouseLastKnownSpeedY = 0;
            mouseLastKnownX = mouseX;
            mouseLastKnownY = mouseY;
            mouseButtonDown = buttonClicked;
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int buttonClicked)
    {
        super.mouseMovedOrUp(mouseX, mouseY, buttonClicked);

        mouseLastKnownSpeedX = mouseX - mouseLastKnownX;
        mouseLastKnownSpeedY = mouseY - mouseLastKnownY;
        mouseLastKnownX = mouseX;
        mouseLastKnownY = mouseY;

        if (buttonClicked == mouseButtonDown)
        {
            mouseButtonDown = -1;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int buttonClicked, long timeDragged)
    {
        super.mouseClickMove(mouseX, mouseY, buttonClicked, timeDragged);

        if (buttonClicked == mouseButtonDown)
        {
            mouseLastKnownSpeedX = mouseX - mouseLastKnownX;
            mouseLastKnownSpeedY = mouseY - mouseLastKnownY;
            mouseLastKnownX = mouseX;
            mouseLastKnownY = mouseY;
        }
    }
}
