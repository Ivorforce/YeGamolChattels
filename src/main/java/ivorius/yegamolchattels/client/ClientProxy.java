/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.client;

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
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import static ivorius.yegamolchattels.YGCConfig.CATEGORY_VISUAL;

public class ClientProxy implements YGCProxy
{
    public static void registerBlockModel(final Block block, final ResourceLocation resourceLocation)
    {
        registerBlockModel(block.getDefaultState(), resourceLocation);
    }

    public static void registerBlockModel(final IBlockState state, final ResourceLocation resourceLocation)
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(state.getBlock()), state.getBlock().getMetaFromState(state), new ModelResourceLocation(resourceLocation, "inventory"));
    }

    public static void registerItemModel(final Item item, int meta, final ResourceLocation resourceLocation)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(resourceLocation, "inventory"));
        ModelBakery.addVariantName(item, resourceLocation.toString());
    }

    public static void registerBlockModelDefault(final Block block, final String id)
    {
        registerBlockModel(block, new ResourceLocation(YeGamolChattels.MODID, id));
    }

    public static void registerBlockModelDefault(final IBlockState state, final String id)
    {
        registerBlockModel(state, new ResourceLocation(YeGamolChattels.MODID, id));
    }

    public static void registerItemModelDefault(final Item item, int meta, final String id)
    {
        registerItemModel(item, meta, new ResourceLocation(YeGamolChattels.MODID, id));
    }

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
        registerItemModelDefault(YGCItems.bannerLarge, 0, "banner_large");
        registerItemModelDefault(YGCItems.bannerSmall, 0, "banner_small");
        registerItemModelDefault(YGCItems.flagLarge, 0, "flag_large");
        registerItemModelDefault(YGCItems.flagSmall, 0, "flag_small");

        registerItemModelDefault(YGCItems.grindstoneStone, 0, "grindstone_stone");
        registerItemModelDefault(YGCItems.mallet, 0, "mallet");
        registerItemModelDefault(YGCItems.entityVita, 0, "entity_vita");

        registerItemModelDefault(YGCItems.plank, 0, "plank_oak");
        registerItemModelDefault(YGCItems.plank, 1, "plank_spruce");
        registerItemModelDefault(YGCItems.plank, 2, "plank_birch");
        registerItemModelDefault(YGCItems.plank, 3, "plank_jungle");
        registerItemModelDefault(YGCItems.plank, 4, "plank_acacia");
        registerItemModelDefault(YGCItems.plank, 5, "plank_big_oak");
        registerItemModelDefault(YGCItems.smoothPlank, 0, "plank_smooth_oak");
        registerItemModelDefault(YGCItems.smoothPlank, 1, "plank_smooth_spruce");
        registerItemModelDefault(YGCItems.smoothPlank, 2, "plank_smooth_birch");
        registerItemModelDefault(YGCItems.smoothPlank, 3, "plank_smooth_jungle");
        registerItemModelDefault(YGCItems.smoothPlank, 4, "plank_smooth_acacia");
        registerItemModelDefault(YGCItems.smoothPlank, 5, "plank_smooth_big_oak");
        registerItemModelDefault(YGCItems.refinedPlank, 0, "plank_refined_oak");
        registerItemModelDefault(YGCItems.refinedPlank, 1, "plank_refined_spruce");
        registerItemModelDefault(YGCItems.refinedPlank, 2, "plank_refined_birch");
        registerItemModelDefault(YGCItems.refinedPlank, 3, "plank_refined_jungle");
        registerItemModelDefault(YGCItems.refinedPlank, 4, "plank_refined_acacia");
        registerItemModelDefault(YGCItems.refinedPlank, 5, "plank_refined_big_oak");

        registerItemModelDefault(YGCItems.sandpaper, 0, "sandpaper");
        registerItemModelDefault(YGCItems.linseedOil, 0, "linseed_oil");
        registerItemModelDefault(YGCItems.ironSaw, 0, "saw_iron");
        registerItemModelDefault(YGCItems.flaxSeeds, 0, "flax_seeds");
        registerItemModelDefault(YGCItems.flaxFiber, 0, "flax_fiber");
        registerItemModelDefault(YGCItems.detailChiselIron, 0, "chisel_iron_point");
        registerItemModelDefault(YGCItems.carvingChiselIron, 0, "chisel_iron");
        registerItemModelDefault(YGCItems.clubHammer, 0, "club_hammer");

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

        RenderingRegistry.registerEntityRenderingHandler(EntityFlag.class, new RenderFlag());
        RenderingRegistry.registerEntityRenderingHandler(EntityBanner.class, new RenderBanner());

        RenderingRegistry.registerEntityRenderingHandler(EntityGhost.class, new RenderGhost());
        RenderingRegistry.registerEntityRenderingHandler(EntityFakePlayer.class, new RenderFakePlayer());

        // TODO
//        MinecraftForgeClient.registerItemRenderer(YGCItems.blockFragment, new RenderBlockFragment());
//        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(YGCBlocks.lootChest), new YGCItemRendererModel(new ModelLootChest(), new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "lootChest.png"), 1.0f, new float[]{0.0f, -0.1f, 0.0f}, new float[]{0.0f, 180.0f, 0.0f}));
//        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(YGCBlocks.tablePress), new YGCItemRendererModel(new ModelTablePress(), new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "tablePress.png"), 3.0f, new float[]{0.0f, -1.15f, 0.0f}, new float[]{0.0f, 180.0f, 0.0f}));
//        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(YGCBlocks.sawBench), new YGCItemRendererModel(new ModelSawBench(), new ResourceLocation(YeGamolChattels.MODID, YeGamolChattels.filePathTextures + "sawBench.png"), 2.0f, new float[]{0.0f, -0.7f, 0.0f}, new float[]{0.0f, 180.0f, 0.0f}));
    }

    @Override
    public EntityPlayer getClientPlayer()
    {
        return Minecraft.getMinecraft().thePlayer;
    }
}
