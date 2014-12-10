package ivorius.yegamolchattels.crafting;

import cpw.mods.fml.common.registry.GameRegistry;
import ivorius.yegamolchattels.blocks.*;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by lukas on 10.12.14.
 */
public class YGCCrafting
{
    public static void init()
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

        GameRegistry.addRecipe(new ItemStack(YGCBlocks.itemShelf, 1, TileEntityItemShelfModel0.shelfJamien), "###", "# #", '#', wildcard(YGCItems.refinedPlank));
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.itemShelf, 1, TileEntityItemShelfModel0.shelfWall), "##", '#', wildcard(YGCItems.refinedPlank));
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.itemShelf, 1, TileEntityItemShelfModel0.shelfWardrobe), "###", "# #", "#I#", '#', wildcard(YGCItems.refinedPlank), 'I', Items.iron_ingot);

        GameRegistry.addRecipe(new ItemStack(YGCBlocks.snowGlobe), " # ", "#W#", "SIS", '#', Blocks.glass, 'W', Blocks.planks, 'S', Blocks.stone, 'I', Items.iron_ingot);

        for (int i = 0; i < 6; i++)
            GameRegistry.addRecipe(new ItemStack(Blocks.planks, 2, i), "#", "#", "#", '#', new ItemStack(YGCItems.plank, 1, i));

        PlanksRefinementRegistry.addRefinement(new PlanksRefinementEntry(YGCItems.plank, YGCItems.sandpaper, new ItemStack(YGCItems.smoothPlank), true));
        PlanksRefinementRegistry.addRefinement(new PlanksRefinementEntryBottle(YGCItems.smoothPlank, YGCItems.linseedOil, new ItemStack(YGCItems.refinedPlank), true));

        GameRegistry.addShapelessRecipe(new ItemStack(YGCItems.sandpaper), Blocks.glass, Items.slime_ball, Items.paper);

        MinecraftForge.addGrassSeed(new ItemStack(YGCItems.flaxSeeds), 2);
        GameRegistry.addShapelessRecipe(new ItemStack(YGCItems.linseedOil), Items.glass_bottle, YGCItems.flaxSeeds, YGCItems.flaxSeeds, YGCItems.flaxSeeds, YGCItems.flaxSeeds, YGCItems.flaxSeeds, YGCItems.flaxSeeds, YGCItems.flaxSeeds, YGCItems.flaxSeeds);
        GameRegistry.addRecipe(new ItemStack(Blocks.wool, 1), "##", "##", '#', YGCItems.flaxFiber);

        GameRegistry.addRecipe(new ItemStack(YGCItems.clubHammer), "#I#", " I ", " I ", '#', Items.iron_ingot, 'I', Items.stick);
        GameRegistry.addRecipe(new ItemStack(YGCItems.detailChiselIron), "  #", " I ", "W  ", '#', Items.iron_ingot, 'I', Items.stick, 'W', Blocks.planks);
        GameRegistry.addRecipe(new ItemStack(YGCItems.carvingChiselIron), " ##", " I#", "W  ", '#', Items.iron_ingot, 'I', Items.stick, 'W', Blocks.planks);

        GameRegistry.addRecipe(new ItemStack(YGCItems.ironSaw), "I  ", "###", '#', Items.iron_ingot, 'I', Items.stick);
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.sawBench), "#IS", "#  ", '#', Blocks.planks, 'I', Items.iron_ingot, 'S', Items.stick);
        GameRegistry.addRecipe(new ItemStack(YGCBlocks.tablePress), "#I#", "# #", '#', Blocks.planks, 'I', Items.iron_ingot);

        GameRegistry.addRecipe(new ItemStack(YGCBlocks.lootChest), "#I#", "#R#", '#', wildcard(YGCItems.refinedPlank), 'I', Items.iron_ingot, 'R', Items.redstone);
    }

    private static ItemStack wildcard(Item item)
    {
        return new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
    }
}
