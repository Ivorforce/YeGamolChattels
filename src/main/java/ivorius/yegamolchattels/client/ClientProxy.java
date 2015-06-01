/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import ivorius.yegamolchattels.YGCConfig;
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
import net.minecraftforge.client.MinecraftForgeClient;

import static ivorius.yegamolchattels.YGCConfig.CATEGORY_VISUAL;

public class ClientProxy implements YGCProxy
{
    @Override
    public void loadConfig(String categoryID)
    {
        if (categoryID == null || CATEGORY_VISUAL.equals(categoryID))
        {
            YGCConfig.fetchDynamicStatueTextures = YeGamolChattels.config.get(CATEGORY_VISUAL, "fetchDynamicStatueTextures", true).getBoolean();
            YGCConfig.doStatueTextureMerge = YeGamolChattels.config.get(CATEGORY_VISUAL, "doStatueTextureMerge", true).getBoolean();
        }
    }

    @Override
    public void registerRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStatue.class, new TileEntityRendererStatue());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrandfatherClock.class, new TileEntityRendererGrandfatherClock());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeaponRack.class, new TileEntityRendererWeaponRack());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrindstone.class, new TileEntityRendererGrindstone());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGong.class, new TileEntityRendererGong());

        TileEntityRendererPedestal pedestalRenderer = new TileEntityRendererPedestal();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPedestal.class, pedestalRenderer);
        pedestalRenderer.registerModel(EnumPedestalEntry.woodPedestal, new ModelPedestalWood());
        pedestalRenderer.registerModel(EnumPedestalEntry.stonePedestal, new ModelPedestalStoneBlock());
        pedestalRenderer.registerModel(EnumPedestalEntry.ironPedestal, new ModelPedestalIron());
        pedestalRenderer.registerModel(EnumPedestalEntry.goldPedestal, new ModelPedestalGold());
        pedestalRenderer.registerModel(EnumPedestalEntry.diamondPedestal, new ModelPedestalDiamond());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemShelf.class, new TileEntityRendererItemShelf());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySnowGlobe.class, new TileEntityRendererSnowGlobe());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySawBench.class, new TileEntityRendererSawBench());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTablePress.class, new TileEntityRendererTablePress());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootChest.class, new TileEntityRendererLootChest());

        RenderingRegistry.registerBlockHandler(new RenderTreasurePile(YGCBlocks.blockTreasurePileRenderType));
        RenderingRegistry.registerBlockHandler(new RenderTikiTorch(YGCBlocks.blockTikiTorchRenderType));
        RenderingRegistry.registerBlockHandler(new RenderMicroBlock(YGCBlocks.blockMicroBlockRenderType));

        RenderingRegistry.registerEntityRenderingHandler(EntityFlag.class, new RenderFlag());
        RenderingRegistry.registerEntityRenderingHandler(EntityBanner.class, new RenderBanner());

        RenderingRegistry.registerEntityRenderingHandler(EntityGhost.class, new RenderGhost());
        RenderingRegistry.registerEntityRenderingHandler(EntityFakePlayer.class, new RenderFakePlayer());

        MinecraftForgeClient.registerItemRenderer(YGCItems.blockFragment, new RenderBlockFragment());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(YGCBlocks.lootChest), new YGCItemRendererModel(new ModelLootChest(), new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "lootChest.png"), 1.0f, new float[]{0.0f, -0.1f, 0.0f}, new float[]{0.0f, 180.0f, 0.0f}));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(YGCBlocks.tablePress), new YGCItemRendererModel(new ModelTablePress(), new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "tablePress.png"), 3.0f, new float[]{0.0f, -1.15f, 0.0f}, new float[]{0.0f, 180.0f, 0.0f}));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(YGCBlocks.sawBench), new YGCItemRendererModel(new ModelSawBench(), new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "sawBench.png"), 2.0f, new float[]{0.0f, -0.7f, 0.0f}, new float[]{0.0f, 180.0f, 0.0f}));
    }

    @Override
    public EntityPlayer getClientPlayer()
    {
        return Minecraft.getMinecraft().thePlayer;
    }
}
