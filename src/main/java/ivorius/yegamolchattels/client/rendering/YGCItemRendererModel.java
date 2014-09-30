package ivorius.yegamolchattels.client.rendering;

import ivorius.ivtoolkit.items.IvItemRendererModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created by lukas on 30.09.14.
 */
public class YGCItemRendererModel implements IItemRenderer
{
    public IvItemRendererModel.ItemModelRenderer model;
    public ResourceLocation texture;
    public float modelSize;
    public float[] translation;
    public float[] rotation;

    public YGCItemRendererModel(IvItemRendererModel.ItemModelRenderer model, ResourceLocation texture, float modelSize, float[] translation, float[] rotation)
    {
        this.model = model;
        this.texture = texture;
        this.modelSize = modelSize;
        this.translation = translation;
        this.rotation = rotation;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        GL11.glPushMatrix();

        if (type == ItemRenderType.ENTITY)
        {
            GL11.glTranslated(0.0, 1.0, 0.0);
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            GL11.glTranslated(0.0, 0.3, 0.0);
        }
        else
        {
            GL11.glTranslated(0.5, 1.0, 0.5);
        }

        GL11.glTranslatef(translation[0], translation[1] + 1.0f, translation[2]);

        if (type != IItemRenderer.ItemRenderType.ENTITY)
        {
            float modelScale = 1.0f / modelSize;
            GL11.glScalef(modelScale, modelScale, modelScale);
        }

        GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(rotation[0], 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(rotation[1], 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(rotation[2], 0.0f, 0.0f, 1.0f);

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        model.render(item);

        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
    }
}

