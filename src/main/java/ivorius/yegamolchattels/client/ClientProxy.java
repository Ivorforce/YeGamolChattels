/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ivorius.ivtoolkit.items.IvItemRendererModel;
import ivorius.ivtoolkit.rendering.RenderFakePlayer;
import ivorius.yegamolchattels.YGCProxy;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.*;
import ivorius.yegamolchattels.client.rendering.*;
import ivorius.yegamolchattels.entities.EntityBanner;
import ivorius.yegamolchattels.entities.EntityFakePlayer;
import ivorius.yegamolchattels.entities.EntityFlag;
import ivorius.yegamolchattels.entities.EntityGhost;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

public class ClientProxy implements YGCProxy
{
    @Override
    public void registerRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStatue.class, new TileEntityRendererStatue());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrandfatherClock.class, new TileEntityRendererGrandfatherClock());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeaponRack.class, new TileEntityRendererWeaponRack());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrindstone.class, new TileEntityRendererGrindstone());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGong.class, new TileEntityRendererGong());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPedestal.class, new TileEntityRendererPedestal());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemShelf.class, new TileEntityRendererItemShelf());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySnowGlobe.class, new TileEntityRendererSnowGlobe());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlankSaw.class, new TileEntityRendererPlankSaw());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlanksRefinement.class, new TileEntityRendererPlanksRefinement());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootChest.class, new TileEntityRendererLootChest());

        RenderingRegistry.registerBlockHandler(new RenderTreasurePile(YGCBlocks.blockTreasurePileRenderType));
        RenderingRegistry.registerBlockHandler(new RenderTikiTorch(YGCBlocks.blockTikiTorchRenderType));
        RenderingRegistry.registerBlockHandler(new RenderMicroBlock(YGCBlocks.blockMicroBlockRenderType));

        RenderingRegistry.registerEntityRenderingHandler(EntityFlag.class, new RenderFlag());
        RenderingRegistry.registerEntityRenderingHandler(EntityBanner.class, new RenderBanner());

        RenderingRegistry.registerEntityRenderingHandler(EntityGhost.class, new RenderGhost());
        RenderingRegistry.registerEntityRenderingHandler(EntityFakePlayer.class, new RenderFakePlayer());

        MinecraftForgeClient.registerItemRenderer(YGCItems.blockFragment, new RenderBlockFragment());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(YGCBlocks.lootChest), new IvItemRendererModel(new ModelLootChest(), new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "lootChest.png"), 1.0f, new float[]{0.0f, -0.1f, 0.0f}, new float[]{0.0f, 180.0f, 0.0f}));

        MinecraftForge.EVENT_BUS.register(this); // For the rendering events
    }

    @Override
    public EntityPlayer getClientPlayer()
    {
        return Minecraft.getMinecraft().thePlayer;
    }

    @SubscribeEvent
    public void unloadCunk(ChunkEvent.Unload event)
    {
        Chunk chunk = event.getChunk();

        for (Object tileEntity : chunk.chunkTileEntityMap.values())
        {
            if (tileEntity instanceof TileEntitySnowGlobe)
            {
                ((TileEntitySnowGlobe) tileEntity).destructCallList();
            }
        }
    }

    @SubscribeEvent
    public void unloadWorld(WorldEvent.Unload event)
    {
        for (Object tileEntity : event.world.loadedTileEntityList)
        {
            if (tileEntity instanceof TileEntitySnowGlobe)
            {
                ((TileEntitySnowGlobe) tileEntity).destructCallList();
            }
        }
    }
}
