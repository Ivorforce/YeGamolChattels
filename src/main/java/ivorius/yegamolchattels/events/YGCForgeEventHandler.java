/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ivorius.ivtoolkit.rendering.IvRenderHelper;
import ivorius.yegamolchattels.blocks.TileEntityMicroBlock;
import ivorius.yegamolchattels.items.ItemChisel;
import ivorius.yegamolchattels.items.ItemClubHammer;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import org.lwjgl.opengl.GL11;

/**
 * Created by lukas on 24.05.14.
 */
public class YGCForgeEventHandler
{
    public void register()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderWorldLast(RenderWorldLastEvent event)
    {
        EntityLivingBase renderEntity = Minecraft.getMinecraft().renderViewEntity;
        ItemStack heldItem = renderEntity.getHeldItem();
        if (heldItem != null && (heldItem.getItem() == YGCItems.chiselIron || heldItem.getItem() == YGCItems.blockFragment))
        {
            renderSelectedMicroblock(event.partialTicks);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void renderSelectedMicroblock(float partialTicks)
    {
        EntityLivingBase renderEntity = Minecraft.getMinecraft().renderViewEntity;
        MovingObjectPosition hoveredObject = Minecraft.getMinecraft().objectMouseOver;
        ItemChisel.MicroBlockFragment hoveredFragment = ItemChisel.getHoveredFragment(renderEntity, hoveredObject.blockX, hoveredObject.blockY, hoveredObject.blockZ);

        if (hoveredFragment != null)
        {
            double viewerPosX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * (double) partialTicks;
            double viewerPosY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * (double) partialTicks;
            double viewerPosZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * (double) partialTicks;

            GL11.glPushMatrix();
            GL11.glTranslated(hoveredFragment.getCoord().x - viewerPosX, hoveredFragment.getCoord().y - viewerPosY, hoveredFragment.getCoord().z - viewerPosZ);

            GL11.glTranslated((hoveredFragment.getInternalCoord().x + 0.5f) / TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_X, (hoveredFragment.getInternalCoord().y + 0.5f) / TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Y, (hoveredFragment.getInternalCoord().z + 0.5f) / TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Z);
            GL11.glColor3f(0.0f, 0.0f, 0.0f);
            GL11.glLineWidth(1.0f);
            IvRenderHelper.drawCuboid(Tessellator.instance, 0.52f / TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_X, 0.52f / TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Y, 0.52f / TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Z, 1.0f, true);

            GL11.glPopMatrix();
        }
    }

    @SubscribeEvent
    public void blockHarvested(BlockEvent.HarvestDropsEvent event)
    {
        if (event.harvester != null)
        {
            ItemStack equipped = event.harvester.getCurrentEquippedItem();
            if (equipped != null && equipped.getItem() instanceof ItemClubHammer)
            {
                ((ItemClubHammer) equipped.getItem()).modifyDrops(event.world, event.block, equipped, event.x, event.y, event.z, event.drops);
            }
        }
    }
}
