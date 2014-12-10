package ivorius.yegamolchattels;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import ivorius.yegamolchattels.blocks.*;
import ivorius.yegamolchattels.entities.*;
import ivorius.yegamolchattels.items.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Collections;

/**
 * Created by lukas on 10.12.14.
 */
public class YGCRegistryHandler
{
    public static void init()
    {
        YGCBlocks.blockTreasurePileRenderType = RenderingRegistry.getNextAvailableRenderId();
        YGCBlocks.blockTikiTorchRenderType = RenderingRegistry.getNextAvailableRenderId();
        YGCBlocks.blockMicroBlockRenderType = RenderingRegistry.getNextAvailableRenderId();

        // --------------------------------Ghost--------------------------------

        YeGamolChattels.entityGhostGlobalID = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(EntityGhost.class, "ygcGhost", YeGamolChattels.entityGhostGlobalID, 0x888888, 0x554444);
        EntityRegistry.registerModEntity(EntityGhost.class, "ygcGhost", YGCEntityList.ghostID, YeGamolChattels.instance, 80, 3, false);
        BiomeGenBase.mushroomIsland.getSpawnableList(EnumCreatureType.creature).add(new BiomeGenBase.SpawnListEntry(EntityGhost.class, 1, 1, 4));

        // --------------------------------Tiki Torch--------------------------------

        YGCBlocks.tikiTorch = (new BlockTikiTorch()).setHardness(0.0F).setLightLevel(0.9375F).setStepSound(Block.soundTypeWood).setBlockName("tikiTorch").setBlockTextureName("tikiTorch").setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerBlock(YGCBlocks.tikiTorch, ItemTikiTorch.class, "tikiTorch");

        // --------------------------------Banner--------------------------------

        EntityRegistry.registerModEntity(EntityBanner.class, "ygcBanner", YGCEntityList.bannerID, YeGamolChattels.instance, 160, Integer.MAX_VALUE, false);

        YGCItems.bannerSmall = (new ItemBanner(0, "small")).setUnlocalizedName("bannerSmall").setTextureName(YeGamolChattels.textureBase + "banner_small").setCreativeTab(YeGamolChattels.tabMain);
        YGCItems.bannerLarge = (new ItemBanner(2, "large")).setUnlocalizedName("bannerLarge").setTextureName(YeGamolChattels.textureBase + "banner_large").setCreativeTab(YeGamolChattels.tabMain);

        GameRegistry.registerItem(YGCItems.bannerSmall, "bannerSmall", YeGamolChattels.MODID);
        GameRegistry.registerItem(YGCItems.bannerLarge, "bannerLarge", YeGamolChattels.MODID);

        // --------------------------------Statue--------------------------------

        YGCBlocks.statue = (new BlockStatue()).setHardness(2.0F).setStepSound(Block.soundTypeStone).setBlockName("ygcStatue").setBlockTextureName("statue").setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerBlock(YGCBlocks.statue, ItemStatue.class, "statue", 0);

        GameRegistry.registerTileEntity(TileEntityStatue.class, "ygcStatue");

        YGCItems.entityVita = new ItemEntityVita().setUnlocalizedName("ygcEntityVita").setTextureName(YeGamolChattels.textureBase + "entity_vita").setCreativeTab(YeGamolChattels.tabVitas);
        GameRegistry.registerItem(YGCItems.entityVita, "entity_vita", YeGamolChattels.MODID);

        EntityRegistry.registerModEntity(EntityFakePlayer.class, "fakePlayer", YGCEntityList.fakePlayerID, YeGamolChattels.instance, 80, 3, false);

        // --------------------------------Treasure piles--------------------------------

        YGCBlocks.treasurePile = new BlockTreasurePile().setHardness(0.2F).setStepSound(Block.soundTypeMetal).setBlockName("treasurePile").setBlockTextureName(YeGamolChattels.textureBase + "treasurePile").setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerBlock(YGCBlocks.treasurePile, ItemBlock.class, "treasurePile");

        // --------------------------------Flags--------------------------------

        EntityRegistry.registerModEntity(EntityFlag.class, "ygcFlag", YGCEntityList.flagID, YeGamolChattels.instance, 160, Integer.MAX_VALUE, false);

        YGCItems.flagSmall = (new ItemFlag(0, "small")).setUnlocalizedName("flagSmall").setTextureName(YeGamolChattels.textureBase + "flag_small").setCreativeTab(YeGamolChattels.tabMain);
        YGCItems.flagLarge = (new ItemFlag(2, "large")).setUnlocalizedName("flagLarge").setTextureName(YeGamolChattels.textureBase + "flag_large").setCreativeTab(YeGamolChattels.tabMain);

        GameRegistry.registerItem(YGCItems.flagSmall, "flagSmall", YeGamolChattels.MODID);
        GameRegistry.registerItem(YGCItems.flagLarge, "flagLarge", YeGamolChattels.MODID);

        // --------------------------------Old Clock--------------------------------

        YGCBlocks.grandfatherClock = (new BlockGrandfatherClock(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("grandfatherClock").setBlockTextureName(YeGamolChattels.textureBase + "grandfatherClock").setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerBlock(YGCBlocks.grandfatherClock, ItemGrandfatherClock.class, "grandfatherClock");

        GameRegistry.registerTileEntityWithAlternatives(TileEntityGrandfatherClock.class, "ygcGrandfatherClock", "grandfatherClock");

        // --------------------------------Weapon Rack--------------------------------

        YGCBlocks.weaponRack = (new BlockWeaponRack(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("weaponRack").setBlockTextureName("weaponRack").setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerBlock(YGCBlocks.weaponRack, ItemWeaponRack.class, "weaponRack");

        GameRegistry.registerTileEntityWithAlternatives(TileEntityWeaponRack.class, "ygcWeaponRack", "weaponRack");

        // --------------------------------Grindstone--------------------------------

        YGCBlocks.grindstone = (new BlockGrindstone(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("grindstone").setBlockTextureName(YeGamolChattels.textureBase + "grindstoneBase").setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerBlock(YGCBlocks.grindstone, ItemGrindstone.class, "grindstone");

        YGCItems.grindstoneStone = (new ItemGrindstoneStone()).setUnlocalizedName("grindstoneStone").setTextureName(YeGamolChattels.textureBase + "grindstoneStone");
        GameRegistry.registerItem(YGCItems.grindstoneStone, "grindstoneStone", YeGamolChattels.MODID);

        GameRegistry.registerTileEntityWithAlternatives(TileEntityGrindstone.class, "ygcGrindstone", "grindstone");

        // --------------------------------Gongs--------------------------------

        YGCBlocks.gong = (new BlockGong(Material.iron)).setHardness(1.5F).setStepSound(Block.soundTypeMetal).setBlockName("gong");
        GameRegistry.registerBlock(YGCBlocks.gong, ItemGong.class, "gong");

        YGCItems.mallet = (new Item()).setUnlocalizedName("mallet").setCreativeTab(YeGamolChattels.tabMain).setMaxStackSize(1).setTextureName(YeGamolChattels.textureBase + "mallet");
        GameRegistry.registerItem(YGCItems.mallet, "mallet", YeGamolChattels.MODID);

        GameRegistry.registerTileEntityWithAlternatives(TileEntityGong.class, "ygcGong", "gong");

        // --------------------------------Pedestal--------------------------------

        YGCBlocks.pedestal = (new BlockPedestal()).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("pedestal");
        GameRegistry.registerBlock(YGCBlocks.pedestal, ItemPedestal.class, "pedestal");

        GameRegistry.registerTileEntityWithAlternatives(TileEntityPedestal.class, "ygcPedestal", "Pedestal");

        // --------------------------------Item Shelf--------------------------------

        YGCBlocks.itemShelf = new BlockItemShelf(Material.wood).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("ygcItemShelf").setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerBlock(YGCBlocks.itemShelf, ItemItemShelf.class, "ygcItemShelf");

        GameRegistry.registerTileEntityWithAlternatives(TileEntityItemShelfModel0.class, "ygcItemShelf", "Item Shelf");

        // --------------------------------Snow Globe--------------------------------

        YGCBlocks.snowGlobe = new BlockSnowGlobe().setBlockName("ygcSnowGlobe").setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerBlock(YGCBlocks.snowGlobe, ItemBlock.class, "ygcSnowGlobe");

        GameRegistry.registerTileEntityWithAlternatives(TileEntitySnowGlobe.class, "ygcSnowGlobe", "Snow Globe");

        // --------------------------------Carpentry--------------------------------

        YGCItems.plank = new ItemPlank().setUnlocalizedName("plank").setTextureName(YeGamolChattels.textureBase + "plank_").setHasSubtypes(true).setMaxDamage(0).setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerItem(YGCItems.plank, "plank", YeGamolChattels.MODID);

        YGCItems.smoothPlank = new ItemPlank().setUnlocalizedName("smoothPlank").setHasSubtypes(true).setMaxDamage(0).setTextureName(YeGamolChattels.textureBase + "plank_smooth_").setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerItem(YGCItems.smoothPlank, "smooth_plank", YeGamolChattels.MODID);

        YGCItems.refinedPlank = new ItemPlank().setUnlocalizedName("refinedPlank").setHasSubtypes(true).setMaxDamage(0).setTextureName(YeGamolChattels.textureBase + "plank_refined_").setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerItem(YGCItems.refinedPlank, "refined_plank", YeGamolChattels.MODID);

        YGCBlocks.sawBench = new BlockSawBench().setBlockName("ygcSawBench").setBlockTextureName("planks_oak").setHardness(1.5f).setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerBlock(YGCBlocks.sawBench, ItemSawBench.class, "plank_saw");
        GameRegistry.registerTileEntity(TileEntitySawBench.class, "ygcPlankSaw");

        YGCBlocks.tablePress = new BlockTablePress().setBlockName("tablePress").setBlockTextureName("planks_oak").setHardness(1.5f).setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerBlock(YGCBlocks.tablePress, ItemTablePress.class, "table_press");
        GameRegistry.registerTileEntity(TileEntityTablePress.class, "ygcTablePress");

        YGCItems.sandpaper = new Item().setUnlocalizedName("sandpaper").setTextureName(YeGamolChattels.textureBase + "sandpaper").setCreativeTab(YeGamolChattels.tabMain);
        YGCItems.sandpaper.setMaxDamage(2048).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(YGCItems.sandpaper, "sandpaper", YeGamolChattels.MODID);

        YGCItems.linseedOil = new Item().setUnlocalizedName("ygcLinseedOil").setTextureName(YeGamolChattels.textureBase + "linseed_oil").setCreativeTab(YeGamolChattels.tabMain);
        YGCItems.linseedOil.setMaxDamage(2048).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(YGCItems.linseedOil, "linseed_oil", YeGamolChattels.MODID);

        YGCItems.ironSaw = new ItemSaw().setUnlocalizedName("ygcSaw").setTextureName(YeGamolChattels.textureBase + "saw_iron").setCreativeTab(YeGamolChattels.tabMain);
        YGCItems.ironSaw.setMaxDamage(128).setMaxStackSize(1);
        GameRegistry.registerItem(YGCItems.ironSaw, "iron_saw", YeGamolChattels.MODID);

        // --------------------------------Flax--------------------------------

        YGCBlocks.flaxPlant = new BlockFlaxPlant().setBlockName("ygcFlaxPlant").setBlockTextureName(YeGamolChattels.textureBase + "flax");
        GameRegistry.registerBlock(YGCBlocks.flaxPlant, ItemBlock.class, "flax_plant");

        YGCItems.flaxSeeds = new ItemFlaxSeeds(YGCBlocks.flaxPlant, Blocks.farmland).setUnlocalizedName("ygcFlaxSeeds").setTextureName(YeGamolChattels.textureBase + "flax_seeds").setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerItem(YGCItems.flaxSeeds, "flax_seeds", YeGamolChattels.MODID);
        YGCItems.flaxFiber = new Item().setUnlocalizedName("ygcFlaxFiber").setTextureName(YeGamolChattels.textureBase + "flax_fiber").setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerItem(YGCItems.flaxFiber, "flax_fiber", YeGamolChattels.MODID);

        // --------------------------------Microblocks--------------------------------

        YGCBlocks.microBlock = new BlockMicroBlock().setBlockName("ygcMicroBlock").setHardness(1.0F);
        GameRegistry.registerBlock(YGCBlocks.microBlock, ItemMicroBlock.class, "micro_block");
        GameRegistry.registerTileEntity(TileEntityMicroBlock.class, "ygcMicroBlock");

        YGCItems.detailChiselIron = new ItemChisel(0, 1.0f, 0.0f, Item.ToolMaterial.IRON, Collections.emptySet(), true).setUnlocalizedName("ygcChiselIron_point").setTextureName(YeGamolChattels.textureBase + "chisel_iron_point").setCreativeTab(YeGamolChattels.tabMain);
        YGCItems.detailChiselIron.setMaxDamage(256).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(YGCItems.detailChiselIron, "iron_chisel_point", YeGamolChattels.MODID);

        YGCItems.carvingChiselIron = new ItemChisel(1, 0.4f, 0.0f, Item.ToolMaterial.IRON, Collections.emptySet(), false).setUnlocalizedName("ygcChiselIron").setTextureName(YeGamolChattels.textureBase + "chisel_iron").setCreativeTab(YeGamolChattels.tabMain);
        YGCItems.carvingChiselIron.setMaxDamage(256).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(YGCItems.carvingChiselIron, "iron_chisel", YeGamolChattels.MODID);

        YGCItems.clubHammer = (ItemClubHammer) new ItemClubHammer(0.0f, Item.ToolMaterial.IRON, Collections.emptySet()).setUnlocalizedName("ygcClubHammer").setTextureName(YeGamolChattels.textureBase + "club_hammer").setCreativeTab(YeGamolChattels.tabMain);
        YGCItems.clubHammer.setMaxDamage(512).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(YGCItems.clubHammer, "club_hammer", YeGamolChattels.MODID);

        YGCItems.blockFragment = new ItemBlockFragment().setUnlocalizedName("ygcBlockFragment");
        GameRegistry.registerItem(YGCItems.blockFragment, "block_fragment", YeGamolChattels.MODID);

        // --------------------------------Loot Chest--------------------------------

        YGCBlocks.lootChest = new BlockLootChest().setBlockName("ygcLootChest").setHardness(1.5f).setCreativeTab(YeGamolChattels.tabMain);
        GameRegistry.registerBlock(YGCBlocks.lootChest, ItemBlock.class, "loot_chest");
        GameRegistry.registerTileEntity(TileEntityLootChest.class, "ygcLootChest");
    }
}
