package ivorius.yegamolchattels.gui;

import ivorius.ivtoolkit.rendering.IvParticleHelper;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityPlankSaw;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lukas on 04.05.14.
 */
public class GuiScreenPlankSaw extends GuiScreen
{
    public static ResourceLocation sawGui = new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "sawGui.png");

    public TileEntityPlankSaw tileEntityPlankSaw;
    RenderItem renderItem;

    public int mouseLastKnownX;
    public int mouseLastKnownY;

    private List<EntityFX> particles = new ArrayList<>();

    public GuiScreenPlankSaw(World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityPlankSaw)
            tileEntityPlankSaw = (TileEntityPlankSaw) tileEntity;

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

        if (tileEntityPlankSaw == null || tileEntityPlankSaw.isInvalid())
            mc.thePlayer.closeScreen();

        Iterator<EntityFX> iterator = particles.iterator();
        while (iterator.hasNext())
        {
            if (iterator.next().isDead)
                iterator.remove();
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float partialTicks)
    {
        super.drawScreen(par1, par2, partialTicks);

        drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("guiPlankSaw.title"), this.width / 2, 40, 16777215);

        if (tileEntityPlankSaw != null)
        {
            ItemStack stack = tileEntityPlankSaw.containedItem;

            if (stack != null && stack.getItem() instanceof ItemBlock)
            {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                ItemBlock itemBlock = (ItemBlock) stack.getItem();

                drawBlock(tileEntityPlankSaw.cutsLeft - 1, TileEntityPlankSaw.cutsPerLog, 0.0f, itemBlock.field_150939_a, stack.getItemDamage()); // The last layer is bark
                drawParticles(partialTicks);

                if (tileEntityPlankSaw.isInWood)
                    drawSaw();

                drawBlock(tileEntityPlankSaw.cutsLeft, TileEntityPlankSaw.cutsPerLog, tileEntityPlankSaw.woodCutY, itemBlock.field_150939_a, stack.getItemDamage());

                if (!tileEntityPlankSaw.isInWood)
                    drawSaw();
            }
            else
            {
                drawParticles(partialTicks);
                drawSaw();
            }
        }
    }

    private void drawParticles(float partialTicks)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(width / 2, 80 + 50 + 10, 0.0f);
        GL11.glScalef(100.0f, -100.0f, 100.0f);
        double prevInterX = EntityFX.interpPosX;
        double prevInterY = EntityFX.interpPosY;
        double prevInterZ = EntityFX.interpPosZ;
        EntityFX.interpPosX = tileEntityPlankSaw.xCoord + 0.5;
        EntityFX.interpPosY = tileEntityPlankSaw.yCoord + 0.5;
        EntityFX.interpPosZ = tileEntityPlankSaw.zCoord + 0.5;

        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator.instance.startDrawingQuads();
        for (EntityFX entityFX : particles)
            entityFX.renderParticle(Tessellator.instance, partialTicks, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f);
        Tessellator.instance.draw();

        EntityFX.interpPosX = prevInterX;
        EntityFX.interpPosY = prevInterY;
        EntityFX.interpPosZ = prevInterZ;
        GL11.glPopMatrix();
    }

    private void drawSaw()
    {
        int sawX = MathHelper.floor_float(tileEntityPlankSaw.sawPositionX * 100.0f);
        int sawY = MathHelper.floor_float(tileEntityPlankSaw.sawPositionY * 100.0f);

        this.mc.getTextureManager().bindTexture(sawGui);
        drawTexturedModalRect(width / 2 - 101 + sawX, 80 - 33 + sawY, 0, 0, 202, 36);
    }

    private void drawBlock(int layer, int maxLayers, float woodCutY, Block block, int meta)
    {
        boolean isTop = layer == 0;
        boolean isBottom = layer == maxLayers;

        IIcon blockTexture = block.getIcon((isTop || isBottom) ? 2 : 1, meta);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        int shiftY = MathHelper.floor_float(woodCutY * 20.0f);
        int shiftX = shiftY / 3;

        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        drawTexturedTrapezoidFromIcon(width / 2, 80 + shiftY, blockTexture, 100, 100 + shiftX, 100 - shiftY, isTop ? 0.0f : (isBottom ? 1.0f : 0.3f), isTop ? 1.0f : (isBottom ? 0.0f : 0.6f));
//        drawTexturedModelRectFromIcon(width / 2 - 50, 80, blockTexture, 100, 100);
    }

    public void drawTexturedTrapezoidFromIcon(int xCenter, int y, IIcon par3Icon, int widthBottom, int widthTop, int height, float textureY1, float textureY2)
    {
        int widthBottom2 = widthBottom / 2;
        int widthTop2 = widthTop / 2;

        float minV = par3Icon.getMinV() * (1.0f - textureY1) + par3Icon.getMaxV() * textureY1;
        float maxV = par3Icon.getMinV() * (1.0f - textureY2) + par3Icon.getMaxV() * textureY2;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (xCenter - widthBottom2), (double) (y + height), (double) this.zLevel, (double) par3Icon.getMinU(), (double) maxV);
        tessellator.addVertexWithUV((double) (xCenter + (widthBottom - widthBottom2)), (double) (y + height), (double) this.zLevel, (double) par3Icon.getMaxU(), (double) maxV);
        tessellator.addVertexWithUV((double) (xCenter + (widthTop - widthTop2)), (double) (y + 0), (double) this.zLevel, (double) par3Icon.getMaxU(), (double) minV);
        tessellator.addVertexWithUV((double) (xCenter - widthTop2), (double) (y + 0), (double) this.zLevel, (double) par3Icon.getMinU(), (double) minV);
        tessellator.draw();
    }

    public static void drawRect(int x1, int y1, int par2, int par3, int par4)
    {
        int j1;

        if (x1 < par2)
        {
            j1 = x1;
            x1 = par2;
            par2 = j1;
        }

        if (y1 < par3)
        {
            j1 = y1;
            y1 = par3;
            par3 = j1;
        }

        float f3 = (float) (par4 >> 24 & 255) / 255.0F;
        float f = (float) (par4 >> 16 & 255) / 255.0F;
        float f1 = (float) (par4 >> 8 & 255) / 255.0F;
        float f2 = (float) (par4 & 255) / 255.0F;
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(f, f1, f2, f3);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double) x1, (double) par3, 0.0D);
        tessellator.addVertex((double) par2, (double) par3, 0.0D);
        tessellator.addVertex((double) par2, (double) y1, 0.0D);
        tessellator.addVertex((double) x1, (double) y1, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    protected void mouseClicked(int x, int y, int buttonClicked)
    {
        super.mouseClicked(x, y, buttonClicked);

        if (buttonClicked == 0)
        {
            mouseLastKnownX = x;
            mouseLastKnownY = y;
        }
    }

    @Override
    protected void mouseClickMove(int x, int y, int buttonClicked, long timeDragged)
    {
        super.mouseClickMove(x, y, buttonClicked, timeDragged);

        if (buttonClicked == 0)
        {
            EntityPlayer player = mc.thePlayer;
            int movX = x - mouseLastKnownX;
            int movY = y - mouseLastKnownY;

            if (movX != 0 || movY != 0)
            {
                ItemStack containedItem = tileEntityPlankSaw.containedItem;

                float addScore = tileEntityPlankSaw.moveSaw(player, movX * 0.01f, movY * 0.01f);

                if (containedItem != null && containedItem.getItem() instanceof ItemBlock)
                {
                    Block containedBlock = ((ItemBlock) containedItem.getItem()).field_150939_a;
                    int containedMetadata = containedItem.getItemDamage();

                    int particles = MathHelper.ceiling_float_int(addScore * 200.0f);
                    for (int i = 0; i < particles; i++)
                    {
                        double fxX = tileEntityPlankSaw.xCoord + tileEntityPlankSaw.getWorldObj().rand.nextFloat();
                        double fxY = tileEntityPlankSaw.yCoord + (1.05f - tileEntityPlankSaw.sawPositionY);
                        double fxZ = tileEntityPlankSaw.zCoord + 0.5;
                        EntityFX particle = new EntityDiggingFX(tileEntityPlankSaw.getWorldObj(), fxX, fxY, fxZ, movX * 0.02f, -0.1, 0.0, containedBlock, containedMetadata, 2);
                        particle.multipleParticleScaleBy(0.5f);
                        IvParticleHelper.spawnParticle(particle);
                        this.particles.add(particle);
                    }
                }
            }

            mouseLastKnownX = x;
            mouseLastKnownY = y;
        }
    }
}
