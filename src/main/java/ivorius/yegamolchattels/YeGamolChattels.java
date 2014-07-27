/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import ivorius.ivtoolkit.math.IvBytePacker;
import ivorius.ivtoolkit.network.*;
import ivorius.yegamolchattels.achievements.YGCAchievementList;
import ivorius.yegamolchattels.blocks.*;
import ivorius.yegamolchattels.entities.EntityBanner;
import ivorius.yegamolchattels.entities.EntityFlag;
import ivorius.yegamolchattels.entities.EntityGhost;
import ivorius.yegamolchattels.entities.YGCEntityList;
import ivorius.yegamolchattels.events.YGCFMLEventHandler;
import ivorius.yegamolchattels.events.YGCForgeEventHandler;
import ivorius.yegamolchattels.gui.YGCGuiHandler;
import ivorius.yegamolchattels.items.*;
import ivorius.yegamolchattels.worldgen.WorldGenFlax;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

@Mod(modid = YeGamolChattels.MODID, version = YeGamolChattels.VERSION, name = YeGamolChattels.NAME, guiFactory = "ivorius.yegamolchattels.gui.YGCConfigGuiFactory")
public class YeGamolChattels
{
    public static final String MODID = "yegamolchattels";
    public static final String VERSION = "1.0.1";
    public static final String NAME = "Ye Gamol Chattels";

    @Instance(value = MODID)
    public static YeGamolChattels instance;

    @SidedProxy(clientSide = "ivorius.yegamolchattels.client.ClientProxy", serverSide = "ivorius.yegamolchattels.server.ServerProxy")
    public static YGCProxy proxy;

    public static String filePathTexturesFull = "yegamolchattels:textures/mod/";
    public static String filePathTextures = "textures/mod/";
    public static String filePathOther = "other/";
    public static String textureBase = "yegamolchattels:";
    public static String soundBase = "yegamolchattels:";
    public static String otherBase = "yegamolchattels:";

    public static Logger logger;
    public static Configuration config;

    public static YGCGuiHandler guiHandler;

    public static SimpleNetworkWrapper network;

    public static YGCFMLEventHandler fmlEventHandler;
    public static YGCForgeEventHandler forgeEventHandler;

    public static CreativeTabs tabMain = new CreativeTabs("yegamolchattels")
    {
        @Override
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(YGCBlocks.snowGlobe);
        }
    };

    public static int entityGhostGlobalID;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        YGCConfig.loadConfig(null);
        config.save();

        guiHandler = new YGCGuiHandler();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);

        fmlEventHandler = new YGCFMLEventHandler();
        fmlEventHandler.register();
        forgeEventHandler = new YGCForgeEventHandler();
        forgeEventHandler.register();

        YGCBlocks.blockTreasurePileRenderType = RenderingRegistry.getNextAvailableRenderId();
        YGCBlocks.blockTikiTorchRenderType = RenderingRegistry.getNextAvailableRenderId();
        YGCBlocks.blockMicroBlockRenderType = RenderingRegistry.getNextAvailableRenderId();

        // --------------------------------Ghost--------------------------------

        entityGhostGlobalID = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(EntityGhost.class, "ygcGhost", entityGhostGlobalID, 0x888888, 0x554444);
        EntityRegistry.registerModEntity(EntityGhost.class, "ygcGhost", YGCEntityList.ghostID, this, 80, 3, false);
        BiomeGenBase.mushroomIsland.getSpawnableList(EnumCreatureType.creature).add(new BiomeGenBase.SpawnListEntry(EntityGhost.class, 1, 1, 4));

        // --------------------------------Tiki Torch--------------------------------

        YGCBlocks.tikiTorch = (new BlockTikiTorch()).setHardness(0.0F).setLightLevel(0.9375F).setStepSound(Block.soundTypeWood).setBlockName("tikiTorch").setBlockTextureName("tikiTorch").setCreativeTab(tabMain);
        GameRegistry.registerBlock(YGCBlocks.tikiTorch, ItemTikiTorch.class, "tikiTorch");

        // --------------------------------Banner--------------------------------

        EntityRegistry.registerModEntity(EntityBanner.class, "ygcBanner", YGCEntityList.bannerID, this, 160, Integer.MAX_VALUE, false);

        YGCItems.bannerSmall = (new ItemBanner(0, "small")).setUnlocalizedName("bannerSmall").setTextureName(textureBase + "bannerSmall").setCreativeTab(tabMain);
        YGCItems.bannerLarge = (new ItemBanner(2, "large")).setUnlocalizedName("bannerLarge").setTextureName(textureBase + "bannerLarge").setCreativeTab(tabMain);

        GameRegistry.registerItem(YGCItems.bannerSmall, "bannerSmall", MODID);
        GameRegistry.registerItem(YGCItems.bannerLarge, "bannerLarge", MODID);

        // --------------------------------Statue--------------------------------

        YGCBlocks.statue = (new BlockStatue(Material.rock, 0)).setHardness(2.0F).setStepSound(Block.soundTypeStone).setBlockName("ygcStatue").setBlockTextureName("statue").setCreativeTab(tabMain);
        GameRegistry.registerBlock(YGCBlocks.statue, ItemStatue.class, "statue", 0);

        GameRegistry.registerTileEntity(TileEntityStatue.class, "ygcStatue");

        YGCItems.pointChiselIron = new ItemStatueChisel().setUnlocalizedName("ygcChiselIron_point").setTextureName(textureBase + "pointChisel").setCreativeTab(tabMain);
        GameRegistry.registerItem(YGCItems.pointChiselIron, "iron_chisel_point", MODID);

        // --------------------------------Treasure piles--------------------------------

        YGCBlocks.treasurePile = new BlockTreasurePile().setHardness(0.2F).setStepSound(Block.soundTypeMetal).setBlockName("treasurePile").setBlockTextureName(textureBase + "treasurePile").setCreativeTab(tabMain);
        GameRegistry.registerBlock(YGCBlocks.treasurePile, ItemBlock.class, "treasurePile");

        // --------------------------------Flags--------------------------------

        EntityRegistry.registerModEntity(EntityFlag.class, "ygcFlag", YGCEntityList.flagID, this, 160, Integer.MAX_VALUE, false);

        YGCItems.flagSmall = (new ItemFlag(0, "small")).setUnlocalizedName("flagSmall").setTextureName(textureBase + "flagSmall").setCreativeTab(tabMain);
        YGCItems.flagLarge = (new ItemFlag(2, "large")).setUnlocalizedName("flagLarge").setTextureName(textureBase + "flagLarge").setCreativeTab(tabMain);

        GameRegistry.registerItem(YGCItems.flagSmall, "flagSmall", MODID);
        GameRegistry.registerItem(YGCItems.flagLarge, "flagLarge", MODID);

        // --------------------------------Old Clock--------------------------------

        YGCBlocks.grandfatherClock = (new BlockGrandfatherClock(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("grandfatherClock").setBlockTextureName(textureBase + "grandfatherClock").setCreativeTab(tabMain);
        GameRegistry.registerBlock(YGCBlocks.grandfatherClock, ItemGrandfatherClock.class, "grandfatherClock");

        GameRegistry.registerTileEntityWithAlternatives(TileEntityGrandfatherClock.class, "ygcGrandfatherClock", "grandfatherClock");

        // --------------------------------Weapon Rack--------------------------------

        YGCBlocks.weaponRack = (new BlockWeaponRack(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("weaponRack").setBlockTextureName("weaponRack").setCreativeTab(tabMain);
        GameRegistry.registerBlock(YGCBlocks.weaponRack, ItemWeaponRack.class, "weaponRack");

        GameRegistry.registerTileEntityWithAlternatives(TileEntityWeaponRack.class, "ygcWeaponRack", "weaponRack");

        // --------------------------------Grindstone--------------------------------

        YGCBlocks.grindstone = (new BlockGrindstone(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("grindstone").setBlockTextureName(textureBase + "grindstoneBase").setCreativeTab(tabMain);
        GameRegistry.registerBlock(YGCBlocks.grindstone, ItemGrindstone.class, "grindstone");

        YGCItems.grindstoneStone = (new ItemGrindstoneStone()).setUnlocalizedName("grindstoneStone").setTextureName(textureBase + "grindstoneStone");
        GameRegistry.registerItem(YGCItems.grindstoneStone, "grindstoneStone", MODID);

        GameRegistry.registerTileEntityWithAlternatives(TileEntityGrindstone.class, "ygcGrindstone", "grindstone");

        // --------------------------------Gongs--------------------------------

        YGCBlocks.gong = (new BlockGong(Material.iron)).setHardness(1.5F).setStepSound(Block.soundTypeMetal).setBlockName("gong");
        GameRegistry.registerBlock(YGCBlocks.gong, ItemGong.class, "gong");

        YGCItems.mallet = (new Item()).setUnlocalizedName("mallet").setCreativeTab(tabMain).setMaxStackSize(1).setTextureName(textureBase + "mallet");
        GameRegistry.registerItem(YGCItems.mallet, "mallet", MODID);

        GameRegistry.registerTileEntityWithAlternatives(TileEntityGong.class, "ygcGong", "gong");

        // --------------------------------Pedestal--------------------------------

        YGCBlocks.pedestal = (new BlockPedestal(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("pedestal");
        GameRegistry.registerBlock(YGCBlocks.pedestal, ItemPedestal.class, "pedestal");

        GameRegistry.registerTileEntityWithAlternatives(TileEntityPedestal.class, "ygcPedestal", "Pedestal");

        // --------------------------------Item Shelf--------------------------------

        YGCBlocks.itemShelf = new BlockItemShelf(Material.wood).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("ygcItemShelf").setCreativeTab(tabMain);
        GameRegistry.registerBlock(YGCBlocks.itemShelf, ItemItemShelf.class, "ygcItemShelf");

        GameRegistry.registerTileEntityWithAlternatives(TileEntityItemShelfModel0.class, "ygcItemShelf", "Item Shelf");

        // --------------------------------Snow Globe--------------------------------

        YGCBlocks.snowGlobe = new BlockSnowGlobe().setBlockName("ygcSnowGlobe").setCreativeTab(tabMain);
        GameRegistry.registerBlock(YGCBlocks.snowGlobe, ItemBlock.class, "ygcSnowGlobe");

        GameRegistry.registerTileEntityWithAlternatives(TileEntitySnowGlobe.class, "ygcSnowGlobe", "Snow Globe");

        // --------------------------------Carpentry--------------------------------

        YGCItems.plank = new ItemPlank().setUnlocalizedName("plank").setTextureName(textureBase + "plank").setHasSubtypes(true).setMaxDamage(0).setCreativeTab(tabMain);
        GameRegistry.registerItem(YGCItems.plank, "plank", MODID);

        YGCItems.smoothPlank = new ItemPlank().setUnlocalizedName("smoothPlank").setHasSubtypes(true).setMaxDamage(0).setTextureName(textureBase + "smoothPlank").setCreativeTab(tabMain);
        GameRegistry.registerItem(YGCItems.smoothPlank, "smooth_plank", MODID);

        YGCItems.refinedPlank = new ItemPlank().setUnlocalizedName("refinedPlank").setHasSubtypes(true).setMaxDamage(0).setTextureName(textureBase + "refinedPlank").setCreativeTab(tabMain);
        GameRegistry.registerItem(YGCItems.refinedPlank, "refined_plank", MODID);

        YGCBlocks.plankSaw = new BlockPlankSaw().setBlockName("plankSaw").setCreativeTab(tabMain);
        GameRegistry.registerBlock(YGCBlocks.plankSaw, ItemPlankSaw.class, "plank_saw");
        GameRegistry.registerTileEntity(TileEntityPlankSaw.class, "ygcPlankSaw");

        YGCBlocks.planksRefinement = new BlockPlanksRefinement().setBlockName("planksRefinement").setCreativeTab(tabMain);
        GameRegistry.registerBlock(YGCBlocks.planksRefinement, ItemPlanksRefinement.class, "planks_refinement_frame");
        GameRegistry.registerTileEntity(TileEntityPlanksRefinement.class, "ygcPlanksRefinement");

        YGCItems.sandpaper = new Item().setUnlocalizedName("sandpaper").setTextureName(textureBase + "sandpaper").setCreativeTab(tabMain);
        YGCItems.sandpaper.setMaxDamage(800).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(YGCItems.sandpaper, "sandpaper", MODID);

        YGCItems.linseedOil = new Item().setUnlocalizedName("ygcLinseedOil").setTextureName(textureBase + "linseedOil").setCreativeTab(tabMain);
        YGCItems.linseedOil.setMaxDamage(800).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(YGCItems.linseedOil, "linseed_oil", MODID);

        // --------------------------------Flax--------------------------------

        YGCBlocks.flaxPlant = new BlockFlaxPlant().setBlockName("ygcFlaxPlant").setBlockTextureName(textureBase + "flax");
        GameRegistry.registerBlock(YGCBlocks.flaxPlant, ItemBlock.class, "flax_plant");

        YGCItems.flaxSeeds = new ItemFlaxSeeds(YGCBlocks.flaxPlant, Blocks.farmland).setUnlocalizedName("ygcFlaxSeeds").setTextureName(textureBase + "flaxSeeds").setCreativeTab(tabMain);
        GameRegistry.registerItem(YGCItems.flaxSeeds, "flax_seeds", MODID);
        YGCItems.flaxFiber = new Item().setUnlocalizedName("ygcFlaxFiber").setTextureName(textureBase + "flaxFiber").setCreativeTab(tabMain);
        GameRegistry.registerItem(YGCItems.flaxFiber, "flax_fiber", MODID);

        // --------------------------------Microblocks--------------------------------

        YGCBlocks.microBlock = new BlockMicroBlock().setBlockName("ygcMicroBlock").setHardness(1.0F);
        GameRegistry.registerBlock(YGCBlocks.microBlock, ItemMicroBlock.class, "ygcMicroBlock");
        GameRegistry.registerTileEntity(TileEntityMicroBlock.class, "ygcMicroBlock");

        YGCItems.detailChiselIron = new ItemChisel(0, 1.0f, 0.0f, Item.ToolMaterial.IRON, new HashSet()).setUnlocalizedName("ygcChiselIron_detail").setTextureName(textureBase + "chiselIron_detail").setCreativeTab(tabMain);
        YGCItems.detailChiselIron.setMaxDamage(200).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(YGCItems.detailChiselIron, "iron_chisel_detail", MODID);

        YGCItems.carvingChiselIron = new ItemChisel(1, 0.4f, 0.0f, Item.ToolMaterial.IRON, new HashSet()).setUnlocalizedName("ygcChiselIron").setTextureName(textureBase + "chiselIron").setCreativeTab(tabMain);
        YGCItems.carvingChiselIron.setMaxDamage(200).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(YGCItems.carvingChiselIron, "iron_chisel", MODID);

        YGCItems.clubHammer = (ItemClubHammer) new ItemClubHammer(0.0f, Item.ToolMaterial.IRON, new HashSet()).setUnlocalizedName("ygcClubHammer").setTextureName(textureBase + "clubHammer").setCreativeTab(tabMain);
        YGCItems.clubHammer.setMaxDamage(200).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(YGCItems.clubHammer, "club_hammer", MODID);

        YGCItems.blockFragment = new ItemBlockFragment().setUnlocalizedName("ygcBlockFragment");
        GameRegistry.registerItem(YGCItems.blockFragment, "block_fragment", MODID);

        // --------------------------------Loot Chest--------------------------------

        YGCBlocks.lootChest = new BlockLootChest().setBlockName("ygcLootChest").setHardness(1.5f).setCreativeTab(tabMain);
        GameRegistry.registerBlock(YGCBlocks.lootChest, ItemBlock.class, "ygcLootChest");
        GameRegistry.registerTileEntity(TileEntityLootChest.class, "ygcLootChest");
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        YeGamolChattels.network.registerMessage(PacketExtendedEntityPropertiesDataHandler.class, PacketExtendedEntityPropertiesData.class, 0, Side.CLIENT);
        YeGamolChattels.network.registerMessage(PacketEntityDataHandler.class, PacketEntityData.class, 1, Side.CLIENT);
        YeGamolChattels.network.registerMessage(PacketTileEntityDataHandler.class, PacketTileEntityData.class, 3, Side.CLIENT);
        YeGamolChattels.network.registerMessage(PacketGuiActionHandler.class, PacketGuiAction.class, 4, Side.SERVER);
        YeGamolChattels.network.registerMessage(PacketTileEntityClientEventHandler.class, PacketTileEntityClientEvent.class, 5, Side.SERVER);

        proxy.registerRenderers();

        addCrafting();
        YGCAchievementList.init();

        GameRegistry.registerWorldGenerator(new WorldGenFlax(), 100);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }

    private void addCrafting()
    {
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(YGCBlocks.tikiTorch), 1), "C", "#", "#", 'C', Items.coal, '#', Items.stick);
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(YGCBlocks.tikiTorch), 1), "C", "#", "#", 'C', new ItemStack(Items.coal, 1, 1), '#', Items.stick);

        GameRegistry.addRecipe(new ItemStack(YGCItems.bannerSmall, 2, 15), "#S#", " # ", '#', Blocks.wool, 'S', Items.stick);
        GameRegistry.addRecipe(new ItemStack(YGCItems.bannerLarge, 1, 15), "#S#", "###", " # ", '#', Blocks.wool, 'S', Items.stick);

        for (int i = 0; i < 16; i++)
        {
            if (i < 15)
                GameRegistry.addShapelessRecipe(new ItemStack(YGCItems.bannerSmall, 1, i), new ItemStack(YGCItems.bannerSmall, 1, 15), new ItemStack(Items.dye, 1, i));
            if (i < 15)
                GameRegistry.addShapelessRecipe(new ItemStack(YGCItems.bannerLarge, 1, i), new ItemStack(YGCItems.bannerLarge, 1, 15), new ItemStack(Items.dye, 1, i));
        }

        BlockStatue.initStatueCrafting();
        for (int r = 0; r < BlockStatue.statueCrafting.length / 2; r++)
        {
            String entityName = (String) BlockStatue.statueCrafting[r * 2 + 1];
            Object craftObject = BlockStatue.statueCrafting[r * 2];

            ItemStack result = ItemStatue.createStatueItemStack(Item.getItemFromBlock(YGCBlocks.statue), entityName, new TileEntityStatue.BlockFragment(Blocks.stone, 0));

            GameRegistry.addRecipe(result, "###", "#O#", "###", '#', Blocks.stone, 'O', craftObject);
        }

        GameRegistry.addRecipe(new ItemStack(YGCBlocks.treasurePile, 1), " # ", "###", "###", '#', Items.gold_ingot);

        GameRegistry.addRecipe(new ItemStack(YGCItems.flagSmall, 2, 15), "I#", "IS", '#', Blocks.wool, 'S', Items.string, 'I', Items.stick);
        GameRegistry.addRecipe(new ItemStack(YGCItems.flagLarge, 1, 15), "IS#", "I##", "I  ", '#', Blocks.wool, 'S', Items.string, 'I', Items.stick);

        for (int i = 0; i < 16; i++)
        {
            if (i < 15)
                GameRegistry.addShapelessRecipe(new ItemStack(YGCItems.flagSmall, 1, i), new ItemStack(YGCItems.flagSmall, 1, 15), new ItemStack(Items.dye, 1, i));
            if (i < 15)
                GameRegistry.addShapelessRecipe(new ItemStack(YGCItems.flagLarge, 1, i), new ItemStack(YGCItems.flagLarge, 1, 15), new ItemStack(Items.dye, 1, i));
        }

        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(YGCBlocks.grandfatherClock)), "IOI", "#R#", "SGS", '#', wildcard(YGCItems.refinedPlank), 'O', Items.clock, 'S', Blocks.cobblestone, 'I', Items.stick, 'R', Items.redstone, 'G', Items.gold_ingot);

        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(YGCBlocks.weaponRack)), "I I", "III", "#I#", '#', wildcard(YGCItems.refinedPlank), 'I', Items.stick);

        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(YGCBlocks.grindstone)), "#I#", "W W", '#', Items.stick, 'I', Blocks.stone, 'W', Blocks.log);
        GameRegistry.addRecipe(new ItemStack(YGCItems.grindstoneStone), " # ", "# #", " # ", '#', Blocks.sandstone);

        GameRegistry.addRecipe(new ItemStack(YGCBlocks.gong, 1, 0), " S ", " # ", "# #", '#', Items.iron_ingot, 'S', Items.string);
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.gong, 1, 1), " S ", " I ", "# #", '#', Items.iron_ingot, 'S', Items.string, 'I', Blocks.iron_block);
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.gong, 1, 2), " S ", " I ", "I I", '#', Items.iron_ingot, 'S', Items.string, 'I', Blocks.iron_block);
        GameRegistry.addRecipe(new ItemStack(YGCItems.mallet), "#L", "I ", '#', Blocks.wool, 'I', Items.stick, 'L', Items.leather);

        GameRegistry.addRecipe(new ItemStack(YGCBlocks.pedestal, 1, EnumPedestalEntry.woodPedestal.getIntIdentifier()), "#", "P", '#', Blocks.log, 'P', Blocks.planks);
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.pedestal, 1, EnumPedestalEntry.stonePedestal.getIntIdentifier()), "#", "C", "#", '#', Blocks.stone, 'C', Blocks.cobblestone);
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.pedestal, 1, EnumPedestalEntry.ironPedestal.getIntIdentifier()), " # ", " I ", "B#B", '#', Blocks.iron_block, 'I', Items.iron_ingot, 'B', Items.book);
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.pedestal, 1, EnumPedestalEntry.goldPedestal.getIntIdentifier()), " # ", "I#I", "BSB", '#', Blocks.gold_block, 'I', Items.gold_ingot, 'B', Items.book, 'S', Blocks.stone);
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.pedestal, 1, EnumPedestalEntry.diamondPedestal.getIntIdentifier()), "I#I", "III", "BSB", '#', Blocks.diamond_block, 'I', Items.diamond, 'B', Items.book, 'S', Blocks.stone);

        GameRegistry.addRecipe(new ItemStack(YGCBlocks.itemShelf, 1, 0), "###", "# #", '#', wildcard(YGCItems.refinedPlank));
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.itemShelf, 1, 1), "##", '#', wildcard(YGCItems.refinedPlank));
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.itemShelf, 1, 2), "###", "# #", "#I#", '#', wildcard(YGCItems.refinedPlank), 'I', Items.iron_ingot);

        GameRegistry.addRecipe(new ItemStack(YGCBlocks.snowGlobe), " # ", "#W#", "SIS", '#', Blocks.glass, 'W', Blocks.planks, 'S', Blocks.stone, 'I', Items.iron_ingot);

        for (int i = 0; i < 6; i++)
            GameRegistry.addRecipe(new ItemStack(Blocks.planks, 1, i), "##", "##", '#', new ItemStack(YGCItems.plank, 1, i));

        TileEntityPlanksRefinement.addRefinement(new PlanksRefinementEntry(YGCItems.plank, YGCItems.sandpaper, new ItemStack(YGCItems.smoothPlank)));
        TileEntityPlanksRefinement.addRefinement(new PlanksRefinementEntry(YGCItems.smoothPlank, YGCItems.linseedOil, new ItemStack(YGCItems.refinedPlank)));

        GameRegistry.addShapelessRecipe(new ItemStack(YGCItems.linseedOil), Items.glass_bottle, YGCItems.flaxSeeds, YGCItems.flaxSeeds, YGCItems.flaxSeeds, YGCItems.flaxSeeds, YGCItems.flaxSeeds, YGCItems.flaxSeeds, YGCItems.flaxSeeds, YGCItems.flaxSeeds);
        GameRegistry.addRecipe(new ItemStack(Blocks.wool, 1), "##", "##", '#', YGCItems.flaxFiber);

        GameRegistry.addRecipe(new ItemStack(YGCItems.clubHammer), "#I#", " I ", " I ", '#', Items.iron_ingot, 'I', Items.stick);
        GameRegistry.addRecipe(new ItemStack(YGCItems.detailChiselIron), " #", "I ", '#', Items.iron_ingot, 'I', Items.stick);
        GameRegistry.addRecipe(new ItemStack(YGCItems.detailChiselIron), "# ", " I", '#', Items.iron_ingot, 'I', Items.stick);

        GameRegistry.addRecipe(new ItemStack(YGCBlocks.lootChest), "#I#", "#R#", '#', wildcard(YGCItems.refinedPlank), 'I', Items.iron_ingot, 'R', Items.redstone);
    }

    private static ItemStack wildcard(Item item)
    {
        return new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
    }
}