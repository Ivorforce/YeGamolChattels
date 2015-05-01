package ivorius.yegamolchattels;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import ivorius.yegamolchattels.blocks.*;
import ivorius.yegamolchattels.entities.*;
import ivorius.yegamolchattels.items.*;
import ivorius.yegamolchattels.tabs.YGCCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collections;

import static ivorius.yegamolchattels.blocks.YGCBlocks.*;
import static ivorius.yegamolchattels.crafting.OreDictionaryConstants.*;
import static ivorius.yegamolchattels.items.YGCItems.*;

/**
 * Created by lukas on 10.12.14.
 */
public class YGCRegistryHandler
{
    public static void init()
    {
        YGCCreativeTabs.tabMain = new CreativeTabs("yegamolchattels")
        {
            @Override
            public Item getTabIconItem()
            {
                return Item.getItemFromBlock(YGCBlocks.snowGlobe);
            }
        };

        YGCCreativeTabs.tabVitas = new CreativeTabs("ygc_entityvita")
        {
            @Override
            public Item getTabIconItem()
            {
                return YGCItems.entityVita;
            }
        };

        blockTreasurePileRenderType = RenderingRegistry.getNextAvailableRenderId();
        blockTikiTorchRenderType = RenderingRegistry.getNextAvailableRenderId();
        blockMicroBlockRenderType = RenderingRegistry.getNextAvailableRenderId();

        // --------------------------------Ghost--------------------------------

        YeGamolChattels.entityGhostGlobalID = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(EntityGhost.class, "ygcGhost", YeGamolChattels.entityGhostGlobalID, 0x888888, 0x554444);
        EntityRegistry.registerModEntity(EntityGhost.class, "ygcGhost", YGCEntityList.ghostID, YeGamolChattels.instance, 80, 3, false);
        BiomeGenBase.mushroomIsland.getSpawnableList(EnumCreatureType.creature).add(new BiomeGenBase.SpawnListEntry(EntityGhost.class, 1, 1, 4));

        // --------------------------------Tiki Torch--------------------------------

        tikiTorch = (new BlockTikiTorch()).setHardness(0.0F).setLightLevel(0.9375F).setStepSound(Block.soundTypeWood).setBlockName("tikiTorch").setBlockTextureName("tikiTorch").setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerBlock(tikiTorch, ItemTikiTorch.class, "tikiTorch");

        // --------------------------------Banner--------------------------------

        EntityRegistry.registerModEntity(EntityBanner.class, "ygcBanner", YGCEntityList.bannerID, YeGamolChattels.instance, 160, Integer.MAX_VALUE, false);

        bannerSmall = (new ItemBanner(0, "small")).setUnlocalizedName("bannerSmall").setTextureName(YeGamolChattels.textureBase + "banner_small").setCreativeTab(YGCCreativeTabs.tabMain);
        bannerLarge = (new ItemBanner(2, "large")).setUnlocalizedName("bannerLarge").setTextureName(YeGamolChattels.textureBase + "banner_large").setCreativeTab(YGCCreativeTabs.tabMain);

        GameRegistry.registerItem(bannerSmall, "bannerSmall", YeGamolChattels.MODID);
        GameRegistry.registerItem(bannerLarge, "bannerLarge", YeGamolChattels.MODID);

        // --------------------------------Statue--------------------------------

        statue = (new BlockStatue()).setHardness(2.0F).setStepSound(Block.soundTypeStone).setBlockName("ygcStatue").setBlockTextureName("statue").setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerBlock(statue, ItemStatue.class, "statue", 0);

        GameRegistry.registerTileEntity(TileEntityStatue.class, "ygcStatue");

        entityVita = new ItemEntityVita().setUnlocalizedName("ygcEntityVita").setTextureName(YeGamolChattels.textureBase + "entity_vita").setCreativeTab(YGCCreativeTabs.tabVitas);
        GameRegistry.registerItem(entityVita, "entity_vita", YeGamolChattels.MODID);

        EntityRegistry.registerModEntity(EntityFakePlayer.class, "fakePlayer", YGCEntityList.fakePlayerID, YeGamolChattels.instance, 80, 3, false);

        // --------------------------------Treasure piles--------------------------------

        treasurePile = new BlockTreasurePile().setHardness(0.2F).setStepSound(Block.soundTypeMetal).setBlockName("treasurePile").setBlockTextureName(YeGamolChattels.textureBase + "treasurePile").setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerBlock(treasurePile, ItemBlock.class, "treasurePile");

        // --------------------------------Flags--------------------------------

        EntityRegistry.registerModEntity(EntityFlag.class, "ygcFlag", YGCEntityList.flagID, YeGamolChattels.instance, 160, Integer.MAX_VALUE, false);

        flagSmall = (new ItemFlag(0, "small")).setUnlocalizedName("flagSmall").setTextureName(YeGamolChattels.textureBase + "flag_small").setCreativeTab(YGCCreativeTabs.tabMain);
        flagLarge = (new ItemFlag(2, "large")).setUnlocalizedName("flagLarge").setTextureName(YeGamolChattels.textureBase + "flag_large").setCreativeTab(YGCCreativeTabs.tabMain);

        GameRegistry.registerItem(flagSmall, "flagSmall", YeGamolChattels.MODID);
        GameRegistry.registerItem(flagLarge, "flagLarge", YeGamolChattels.MODID);

        // --------------------------------Old Clock--------------------------------

        grandfatherClock = (new BlockGrandfatherClock(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("grandfatherClock").setBlockTextureName(YeGamolChattels.textureBase + "grandfatherClock").setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerBlock(grandfatherClock, ItemGrandfatherClock.class, "grandfatherClock");

        GameRegistry.registerTileEntityWithAlternatives(TileEntityGrandfatherClock.class, "ygcGrandfatherClock", "grandfatherClock");

        // --------------------------------Weapon Rack--------------------------------

        weaponRack = (new BlockWeaponRack(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("weaponRack").setBlockTextureName("weaponRack").setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerBlock(weaponRack, ItemWeaponRack.class, "weaponRack");

        GameRegistry.registerTileEntityWithAlternatives(TileEntityWeaponRack.class, "ygcWeaponRack", "weaponRack");

        // --------------------------------Grindstone--------------------------------

        grindstone = (new BlockGrindstone(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("grindstone").setBlockTextureName(YeGamolChattels.textureBase + "grindstoneBase").setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerBlock(grindstone, ItemGrindstone.class, "grindstone");

        grindstoneStone = (new ItemGrindstoneStone()).setUnlocalizedName("grindstoneStone").setTextureName(YeGamolChattels.textureBase + "grindstoneStone");
        GameRegistry.registerItem(grindstoneStone, "grindstoneStone", YeGamolChattels.MODID);

        GameRegistry.registerTileEntityWithAlternatives(TileEntityGrindstone.class, "ygcGrindstone", "grindstone");

        // --------------------------------Gongs--------------------------------

        gong = (new BlockGong(Material.iron)).setHardness(1.5F).setStepSound(Block.soundTypeMetal).setBlockName("gong");
        GameRegistry.registerBlock(gong, ItemGong.class, "gong");

        mallet = (new Item()).setUnlocalizedName("mallet").setCreativeTab(YGCCreativeTabs.tabMain).setMaxStackSize(1).setTextureName(YeGamolChattels.textureBase + "mallet");
        GameRegistry.registerItem(mallet, "mallet", YeGamolChattels.MODID);

        GameRegistry.registerTileEntityWithAlternatives(TileEntityGong.class, "ygcGong", "gong");

        // --------------------------------Pedestal--------------------------------

        pedestal = (new BlockPedestal()).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("pedestal");
        GameRegistry.registerBlock(pedestal, ItemPedestal.class, "pedestal");

        GameRegistry.registerTileEntityWithAlternatives(TileEntityPedestal.class, "ygcPedestal", "Pedestal");

        // --------------------------------Item Shelf--------------------------------

        itemShelf = new BlockItemShelf(Material.wood).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("ygcItemShelf").setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerBlock(itemShelf, ItemItemShelf.class, "ygcItemShelf");

        GameRegistry.registerTileEntityWithAlternatives(TileEntityItemShelfModel0.class, "ygcItemShelf", "Item Shelf");

        // --------------------------------Snow Globe--------------------------------

        snowGlobe = new BlockSnowGlobe().setBlockName("ygcSnowGlobe").setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerBlock(snowGlobe, ItemBlock.class, "ygcSnowGlobe");

        GameRegistry.registerTileEntityWithAlternatives(TileEntitySnowGlobe.class, "ygcSnowGlobe", "Snow Globe");

        // --------------------------------Carpentry--------------------------------

        plank = new ItemPlank().setUnlocalizedName("plank").setTextureName(YeGamolChattels.textureBase + "plank_").setHasSubtypes(true).setMaxDamage(0).setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerItem(plank, "plank", YeGamolChattels.MODID);
        OreDictionary.registerOre(DC_SINGLE_PLANK_WOOD, plank);

        smoothPlank = new ItemPlank().setUnlocalizedName("smoothPlank").setHasSubtypes(true).setMaxDamage(0).setTextureName(YeGamolChattels.textureBase + "plank_smooth_").setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerItem(smoothPlank, "smooth_plank", YeGamolChattels.MODID);
        OreDictionary.registerOre(DC_SINGLE_PLANK_WOOD_SMOOTHED, smoothPlank);

        refinedPlank = new ItemPlank().setUnlocalizedName("refinedPlank").setHasSubtypes(true).setMaxDamage(0).setTextureName(YeGamolChattels.textureBase + "plank_refined_").setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerItem(refinedPlank, "refined_plank", YeGamolChattels.MODID);
        OreDictionary.registerOre(DC_SINGLE_PLANK_WOOD_REFINED, refinedPlank);

        sawBench = new BlockSawBench().setBlockName("ygcSawBench").setBlockTextureName("planks_oak").setHardness(1.5f).setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerBlock(sawBench, ItemSawBench.class, "plank_saw");
        GameRegistry.registerTileEntity(TileEntitySawBench.class, "ygcPlankSaw");

        tablePress = new BlockTablePress().setBlockName("tablePress").setBlockTextureName("planks_oak").setHardness(1.5f).setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerBlock(tablePress, ItemTablePress.class, "table_press");
        GameRegistry.registerTileEntity(TileEntityTablePress.class, "ygcTablePress");

        sandpaper = new Item().setUnlocalizedName("sandpaper").setTextureName(YeGamolChattels.textureBase + "sandpaper").setCreativeTab(YGCCreativeTabs.tabMain);
        sandpaper.setMaxDamage(2048).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(sandpaper, "sandpaper", YeGamolChattels.MODID);

        linseedOil = new Item().setUnlocalizedName("ygcLinseedOil").setTextureName(YeGamolChattels.textureBase + "linseed_oil").setCreativeTab(YGCCreativeTabs.tabMain);
        linseedOil.setMaxDamage(2048).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(linseedOil, "linseed_oil", YeGamolChattels.MODID);

        ironSaw = new ItemSaw().setUnlocalizedName("ygcSaw").setTextureName(YeGamolChattels.textureBase + "saw_iron").setCreativeTab(YGCCreativeTabs.tabMain);
        ironSaw.setMaxDamage(128).setMaxStackSize(1);
        GameRegistry.registerItem(ironSaw, "iron_saw", YeGamolChattels.MODID);

        // --------------------------------Flax--------------------------------

        flaxPlant = new BlockFlaxPlant().setBlockName("ygcFlaxPlant").setBlockTextureName(YeGamolChattels.textureBase + "flax");
        GameRegistry.registerBlock(flaxPlant, ItemBlock.class, "flax_plant");
        OreDictionary.registerOre(DC_FLAX_CROP, flaxPlant);

        flaxSeeds = new ItemFlaxSeeds(flaxPlant, Blocks.farmland).setUnlocalizedName("ygcFlaxSeeds").setTextureName(YeGamolChattels.textureBase + "flax_seeds").setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerItem(flaxSeeds, "flax_seeds", YeGamolChattels.MODID);
        OreDictionary.registerOre(DC_FLAX_SEEDS, flaxSeeds);

        flaxFiber = new Item().setUnlocalizedName("ygcFlaxFiber").setTextureName(YeGamolChattels.textureBase + "flax_fiber").setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerItem(flaxFiber, "flax_fiber", YeGamolChattels.MODID);
        OreDictionary.registerOre(DC_FLAX_FIBER, flaxFiber);

        // --------------------------------Microblocks--------------------------------

        microBlock = new BlockMicroBlock().setBlockName("ygcMicroBlock").setHardness(1.0F);
        GameRegistry.registerBlock(microBlock, ItemMicroBlock.class, "micro_block");
        GameRegistry.registerTileEntity(TileEntityMicroBlock.class, "ygcMicroBlock");

        detailChiselIron = new ItemChisel(0, 1.0f, 0.0f, Item.ToolMaterial.IRON, Collections.emptySet(), true).setUnlocalizedName("ygcChiselIron_point").setTextureName(YeGamolChattels.textureBase + "chisel_iron_point").setCreativeTab(YGCCreativeTabs.tabMain);
        detailChiselIron.setMaxDamage(256).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(detailChiselIron, "iron_chisel_point", YeGamolChattels.MODID);

        carvingChiselIron = new ItemChisel(1, 0.9f, 0.0f, Item.ToolMaterial.IRON, Collections.emptySet(), false).setUnlocalizedName("ygcChiselIron").setTextureName(YeGamolChattels.textureBase + "chisel_iron").setCreativeTab(YGCCreativeTabs.tabMain);
        carvingChiselIron.setMaxDamage(256).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(carvingChiselIron, "iron_chisel", YeGamolChattels.MODID);

        clubHammer = (ItemClubHammer) new ItemClubHammer(0.0f, Item.ToolMaterial.IRON, Collections.emptySet()).setUnlocalizedName("ygcClubHammer").setTextureName(YeGamolChattels.textureBase + "club_hammer").setCreativeTab(YGCCreativeTabs.tabMain);
        clubHammer.setMaxDamage(512).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(clubHammer, "club_hammer", YeGamolChattels.MODID);

        blockFragment = new ItemBlockFragment().setUnlocalizedName("ygcBlockFragment");
        GameRegistry.registerItem(blockFragment, "block_fragment", YeGamolChattels.MODID);

        // --------------------------------Loot Chest--------------------------------

        lootChest = new BlockLootChest().setBlockName("ygcLootChest").setHardness(1.5f).setCreativeTab(YGCCreativeTabs.tabMain);
        GameRegistry.registerBlock(lootChest, ItemBlock.class, "loot_chest");
        GameRegistry.registerTileEntity(TileEntityLootChest.class, "ygcLootChest");
    }
}
