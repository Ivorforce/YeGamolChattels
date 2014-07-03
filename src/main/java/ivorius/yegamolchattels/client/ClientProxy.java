/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import ivorius.ivtoolkit.network.*;
import ivorius.yegamolchattels.YGCProxy;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.server.ServerProxy;
import ivorius.yegamolchattels.blocks.*;
import ivorius.yegamolchattels.client.rendering.*;
import ivorius.yegamolchattels.entities.EntityBanner;
import ivorius.yegamolchattels.entities.EntityFlag;
import ivorius.yegamolchattels.entities.EntityGhost;
import net.minecraft.world.chunk.Chunk;
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

        RenderingRegistry.registerBlockHandler(new RenderTreasurePile(YGCBlocks.blockTreasurePileRenderType));
        RenderingRegistry.registerBlockHandler(new RenderTikiTorch(YGCBlocks.blockTikiTorchRenderType));

        RenderingRegistry.registerEntityRenderingHandler(EntityFlag.class, new RenderFlag());
        RenderingRegistry.registerEntityRenderingHandler(EntityBanner.class, new RenderBanner());

        RenderingRegistry.registerEntityRenderingHandler(EntityGhost.class, new RenderGhost());

        MinecraftForge.EVENT_BUS.register(this); // For the rendering events
    }

    @Override
    public void registerPackets()
    {
        YeGamolChattels.network.registerMessage(PacketExtendedEntityPropertiesDataHandler.class, PacketExtendedEntityPropertiesData.class, 0, Side.CLIENT);
        YeGamolChattels.network.registerMessage(PacketEntityDataHandler.class, PacketEntityData.class, 1, Side.CLIENT);
        YeGamolChattels.network.registerMessage(PacketTileEntityDataHandler.class, PacketTileEntityData.class, 3, Side.CLIENT);
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
