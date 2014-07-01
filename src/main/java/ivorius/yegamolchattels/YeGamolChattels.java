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
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import ivorius.ivtoolkit.network.ChannelHandlerExtendedEntityPropertiesData;
import ivorius.ivtoolkit.network.ChannelHandlerTileEntityData;
import ivorius.yegamolchattels.blocks.*;
import ivorius.yegamolchattels.entities.EntityBanner;
import ivorius.yegamolchattels.entities.EntityFlag;
import ivorius.yegamolchattels.entities.EntityGhost;
import ivorius.yegamolchattels.entities.YGCEntityList;
import ivorius.yegamolchattels.gui.YGCGuiHandler;
import ivorius.yegamolchattels.items.*;
import ivorius.yegamolchattels.server.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

@Mod(modid = YeGamolChattels.MODID, version = YeGamolChattels.VERSION, name = YeGamolChattels.NAME)
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
    public static YGCGuiHandler guiHandler;

    public static ChannelHandlerTileEntityData chTileEntityData;
    public static ChannelHandlerExtendedEntityPropertiesData chEEPData;

    public static int entityGhostGlobalID;

    public static boolean areDangerousStatuesAllowed;
    public static boolean areLifeStatuesAllowed;

    public static boolean easterEggsAllowed;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        Configuration config = new Configuration(event.getSuggestedConfigurationFile());

        config.load();

        areDangerousStatuesAllowed = config.get("General", "areDangerousStatuesAllowed", true).getBoolean(true);
        areLifeStatuesAllowed = config.get("General", "areLifeStatuesAllowed", true).getBoolean(true);
        easterEggsAllowed = config.get("General", "easterEggsAllowed", true).getBoolean(true);

        config.save();

        chEEPData = new ChannelHandlerExtendedEntityPropertiesData("YGC|EntityData");
        NetworkRegistry.INSTANCE.newChannel(chEEPData.packetChannel, chEEPData);

        chTileEntityData = new ChannelHandlerTileEntityData("YGC|TEData");
        NetworkRegistry.INSTANCE.newChannel(chTileEntityData.packetChannel, chTileEntityData);

        guiHandler = new YGCGuiHandler();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);

        YGCBlocks.blockTreasurePileRenderType = RenderingRegistry.getNextAvailableRenderId();
        YGCBlocks.blockTikiTorchRenderType = RenderingRegistry.getNextAvailableRenderId();

        // --------------------------------Ghost--------------------------------

        entityGhostGlobalID = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(EntityGhost.class, "ygcGhost", entityGhostGlobalID, 0x888888, 0x554444);
        EntityRegistry.registerModEntity(EntityGhost.class, "ygcGhost", YGCEntityList.ghostID, this, 80, 3, false);
        BiomeGenBase.mushroomIsland.getSpawnableList(EnumCreatureType.creature).add(new BiomeGenBase.SpawnListEntry(EntityGhost.class, 1, 1, 4));

        // --------------------------------Tiki Torch--------------------------------

        YGCBlocks.tikiTorch = (new BlockTikiTorch()).setHardness(0.0F).setLightLevel(0.9375F).setStepSound(Block.soundTypeWood).setBlockName("tikiTorch").setBlockTextureName("tikiTorch").setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(YGCBlocks.tikiTorch, ItemTikiTorch.class, "tikiTorch");

        // --------------------------------Banner--------------------------------

        EntityRegistry.registerModEntity(EntityBanner.class, "ygcBanner", YGCEntityList.bannerID, this, 160, Integer.MAX_VALUE, false);

        YGCItems.bannerSmall = (new ItemBanner(0, "small")).setUnlocalizedName("bannerSmall").setTextureName(textureBase + "bannerSmall").setCreativeTab(CreativeTabs.tabDecorations);
        YGCItems.bannerLarge = (new ItemBanner(2, "large")).setUnlocalizedName("bannerLarge").setTextureName(textureBase + "bannerLarge").setCreativeTab(CreativeTabs.tabDecorations);

        GameRegistry.registerItem(YGCItems.bannerSmall, "bannerSmall", MODID);
        GameRegistry.registerItem(YGCItems.bannerLarge, "bannerLarge", MODID);

        // --------------------------------Statue--------------------------------

        YGCBlocks.statueStone = (new BlockStatue(Material.rock, 0)).setHardness(2.0F).setStepSound(Block.soundTypeStone).setBlockName("statueStone").setBlockTextureName("statueStone").setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(YGCBlocks.statueStone, ItemStatue.class, "statueStone", MODID, 0);

        YGCBlocks.statuePlanks = (new BlockStatue(Material.wood, 1)).setHardness(2.0F).setStepSound(Block.soundTypeWood).setBlockName("statuePlanks").setBlockTextureName("statuePlanks").setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(YGCBlocks.statuePlanks, ItemStatue.class, "statuePlanks", MODID, 1);

        YGCBlocks.statueGold = (new BlockStatue(Material.iron, 2)).setHardness(2.0F).setStepSound(Block.soundTypeMetal).setBlockName("statueGold").setBlockTextureName("statueGold").setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(YGCBlocks.statueGold, ItemStatue.class, "statueGold", MODID, 2);

        GameRegistry.registerTileEntity(TileEntityStatue.class, "ygcStatue");

        // --------------------------------Treasure piles--------------------------------

        YGCBlocks.treasurePile = new BlockTreasurePile().setHardness(0.2F).setStepSound(Block.soundTypeMetal).setBlockName("treasurePile").setBlockTextureName(textureBase + "treasurePile").setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(YGCBlocks.treasurePile, ItemBlock.class, "treasurePile", MODID);

        // --------------------------------Flags--------------------------------

        EntityRegistry.registerModEntity(EntityFlag.class, "ygcFlag", YGCEntityList.flagID, this, 160, Integer.MAX_VALUE, false);

        YGCItems.flagSmall = (new ItemFlag(0, "small")).setUnlocalizedName("flagSmall").setTextureName(textureBase + "flagSmall").setCreativeTab(CreativeTabs.tabDecorations);
        YGCItems.flagLarge = (new ItemFlag(2, "large")).setUnlocalizedName("flagLarge").setTextureName(textureBase + "flagLarge").setCreativeTab(CreativeTabs.tabDecorations);

        GameRegistry.registerItem(YGCItems.flagSmall, "flagSmall", MODID);
        GameRegistry.registerItem(YGCItems.flagLarge, "flagLarge", MODID);

        // --------------------------------Old Clock--------------------------------

        YGCBlocks.grandfatherClock = (new BlockGrandfatherClock(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("grandfatherClock").setBlockTextureName(textureBase + "grandfatherClock").setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(YGCBlocks.grandfatherClock, ItemGrandfatherClock.class, "grandfatherClock", MODID);

        GameRegistry.registerTileEntityWithAlternatives(TileEntityGrandfatherClock.class, "ygcGrandfatherClock", "grandfatherClock");

        // --------------------------------Weapon Rack--------------------------------

        YGCBlocks.weaponRack = (new BlockWeaponRack(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("weaponRack").setBlockTextureName("weaponRack").setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(YGCBlocks.weaponRack, ItemWeaponRack.class, "weaponRack", MODID);

        GameRegistry.registerTileEntityWithAlternatives(TileEntityWeaponRack.class, "ygcWeaponRack", "weaponRack");

        // --------------------------------Grindstone--------------------------------

        YGCBlocks.grindstone = (new BlockGrindstone(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("grindstone").setBlockTextureName(textureBase + "grindstoneBase").setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(YGCBlocks.grindstone, ItemGrindstone.class, "grindstone", MODID);

        YGCItems.grindstoneStone = (new ItemGrindstoneStone()).setUnlocalizedName("grindstoneStone").setTextureName(textureBase + "grindstoneStone");
        GameRegistry.registerItem(YGCItems.grindstoneStone, "grindstoneStone", MODID);

        GameRegistry.registerTileEntityWithAlternatives(TileEntityGrindstone.class, "ygcGrindstone", "grindstone");

        // --------------------------------Gongs--------------------------------

        YGCBlocks.gong = (new BlockGong(Material.iron)).setHardness(1.5F).setStepSound(Block.soundTypeMetal).setBlockName("gong");
        GameRegistry.registerBlock(YGCBlocks.gong, ItemGong.class, "gong", MODID);

        YGCItems.mallet = (new Item()).setUnlocalizedName("mallet").setCreativeTab(CreativeTabs.tabDecorations).setMaxStackSize(1).setTextureName(textureBase + "mallet");
        GameRegistry.registerItem(YGCItems.mallet, "mallet", MODID);

        GameRegistry.registerTileEntityWithAlternatives(TileEntityGong.class, "ygcGong", "gong");

        // --------------------------------Pedestal--------------------------------

        YGCBlocks.pedestal = (new BlockPedestal(Material.wood)).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("pedestal");
        GameRegistry.registerBlock(YGCBlocks.pedestal, ItemPedestal.class, "pedestal", MODID);

        GameRegistry.registerTileEntityWithAlternatives(TileEntityPedestal.class, "ygcPedestal", "Pedestal");

        // --------------------------------Item Shelf--------------------------------

        YGCBlocks.itemShelf = new BlockItemShelf(Material.wood).setHardness(1.5F).setStepSound(Block.soundTypeWood).setBlockName("ygcItemShelf").setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(YGCBlocks.itemShelf, ItemItemShelf.class, "ygcItemShelf", MODID);

        GameRegistry.registerTileEntityWithAlternatives(TileEntityItemShelfModel0.class, "ygcItemShelf", "Item Shelf");

        // --------------------------------Snow Globe--------------------------------

        YGCBlocks.snowGlobe = new BlockSnowGlobe().setBlockName("ygcSnowGlobe").setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(YGCBlocks.snowGlobe, ItemBlock.class, "ygcSnowGlobe", MODID);

        GameRegistry.registerTileEntityWithAlternatives(TileEntitySnowGlobe.class, "ygcSnowGlobe", "Snow Globe");

        // --------------------------------Carpentry--------------------------------

        YGCItems.plank = new ItemPlank().setUnlocalizedName("plank").setCreativeTab(CreativeTabs.tabMisc).setTextureName(textureBase + "plank");
        GameRegistry.registerItem(YGCItems.plank, "plank", MODID);

        YGCItems.smoothPlank = new ItemPlank().setUnlocalizedName("smoothPlank").setCreativeTab(CreativeTabs.tabMisc).setTextureName(textureBase + "smoothPlank");
        GameRegistry.registerItem(YGCItems.smoothPlank, "smoothPlank", MODID);

        YGCItems.refinedPlank = new ItemPlank().setUnlocalizedName("refinedPlank").setCreativeTab(CreativeTabs.tabMisc).setTextureName(textureBase + "refinedPlank");
        GameRegistry.registerItem(YGCItems.refinedPlank, "refinedPlank", MODID);

        YGCBlocks.plankSaw = new BlockPlankSaw().setBlockName("plankSaw").setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(YGCBlocks.plankSaw, ItemPlankSaw.class, "plankSaw", MODID);

        YGCBlocks.planksRefinement = new BlockPlanksRefinement().setBlockName("planksRefinement").setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(YGCBlocks.planksRefinement, ItemPlanksRefinement.class, "planksRefinement", MODID);

        GameRegistry.registerTileEntity(TileEntityPlankSaw.class, "ygcPlankSaw");
        GameRegistry.registerTileEntity(TileEntityPlanksRefinement.class, "ygcPlanksRefinement");

        YGCItems.sandpaper = new Item().setUnlocalizedName("sandpaper").setTextureName(textureBase + "sandpaper").setCreativeTab(CreativeTabs.tabMisc);
        YGCItems.sandpaper.setMaxDamage(800).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(YGCItems.sandpaper, "sandpaper", MODID);

        YGCItems.oil = new Item().setUnlocalizedName("ygcOil").setTextureName(textureBase + "oil").setCreativeTab(CreativeTabs.tabMisc);
        YGCItems.oil.setMaxDamage(800).setMaxStackSize(1).setNoRepair();
        GameRegistry.registerItem(YGCItems.oil, "oil", MODID);
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        proxy.registerRenderers();

        addCrafting();
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

            ItemStack resultWood = ItemStatue.createStatueItemStack(Item.getItemFromBlock(YGCBlocks.statuePlanks), entityName);
            ItemStack resultStone = ItemStatue.createStatueItemStack(Item.getItemFromBlock(YGCBlocks.statueStone), entityName);
            ItemStack resultGold = ItemStatue.createStatueItemStack(Item.getItemFromBlock(YGCBlocks.statueGold), entityName);

            GameRegistry.addRecipe(resultStone, "###", "#O#", "###", '#', Blocks.stone, 'O', craftObject);
            GameRegistry.addRecipe(resultWood, "###", "#O#", "###", '#', Blocks.planks, 'O', craftObject);
            GameRegistry.addRecipe(resultGold, "###", "#O#", "###", '#', Items.gold_ingot, 'O', craftObject);
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

        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(YGCBlocks.grandfatherClock)), "IOI", "#R#", "SGS", '#', Blocks.planks, 'O', Items.clock, 'S', Blocks.cobblestone, 'I', Items.stick, 'R', Items.redstone, 'G', Items.gold_ingot);

        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(YGCBlocks.weaponRack)), "I I", "III", "#I#", '#', Blocks.planks, 'I', Items.stick);

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

        GameRegistry.addRecipe(new ItemStack(YGCBlocks.itemShelf, 1, 0), "#P#", "#-#", '#', Blocks.log, 'P', Blocks.planks, '-', Items.stick);
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.itemShelf, 1, 1), "#-#", '#', Blocks.planks, '-', Items.stick);
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.itemShelf, 1, 2), "###", "#S#", "#I#", '#', Blocks.log, 'I', Items.iron_ingot, 'S', Items.stick);

        GameRegistry.addRecipe(new ItemStack(YGCBlocks.snowGlobe), "###", "WWW", "SIS", '#', Blocks.glass, 'W', Blocks.planks, 'S', Blocks.stone, 'I', Items.iron_ingot);

        for (int i = 0; i < 6; i++)
            GameRegistry.addRecipe(new ItemStack(Blocks.planks, 1, i), "##", "##", '#', new ItemStack(YGCItems.plank, 1, i));

        TileEntityPlanksRefinement.addRefinement(new PlanksRefinementEntry(YGCItems.plank, YGCItems.sandpaper, new ItemStack(YGCItems.smoothPlank)));
        TileEntityPlanksRefinement.addRefinement(new PlanksRefinementEntry(YGCItems.smoothPlank, YGCItems.oil, new ItemStack(YGCItems.refinedPlank)));
    }
}