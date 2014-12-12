package ivorius.yegamolchattels.crafting;

import cpw.mods.fml.common.registry.GameRegistry;
import ivorius.yegamolchattels.blocks.PlanksRefinementEntry;
import ivorius.yegamolchattels.blocks.PlanksRefinementEntryBottle;
import ivorius.yegamolchattels.blocks.PlanksRefinementRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import static ivorius.yegamolchattels.blocks.EnumPedestalEntry.*;
import static ivorius.yegamolchattels.blocks.TileEntityItemShelfModel0.*;
import static ivorius.yegamolchattels.blocks.YGCBlocks.*;
import static ivorius.yegamolchattels.crafting.OreDictionaryConstants.*;
import static ivorius.yegamolchattels.items.YGCItems.*;
import static net.minecraft.init.Blocks.planks;
import static net.minecraft.init.Blocks.wool;
import static net.minecraft.init.Items.*;

/**
 * Created by lukas on 10.12.14.
 */
public class YGCCrafting
{
    public static void init()
    {
        addRecipe(new ItemStack(Item.getItemFromBlock(tikiTorch), 1), "C", "#", "#", 'C', coal, '#', DC_STICK_WOOD);
        addRecipe(new ItemStack(Item.getItemFromBlock(tikiTorch), 1), "C", "#", "#", 'C', new ItemStack(coal, 1, 1), '#', DC_STICK_WOOD);

        addRecipe(new ItemStack(bannerSmall, 2, 15), "#S#", " # ", '#', wool, 'S', DC_STICK_WOOD);
        addRecipe(new ItemStack(bannerLarge, 1, 15), "#S#", "###", " # ", '#', wool, 'S', DC_STICK_WOOD);

        for (int i = 0; i < 16; i++)
        {
            if (i < 15)
                addShapelessRecipe(new ItemStack(bannerSmall, 1, i), new ItemStack(bannerSmall, 1, 15), new ItemStack(dye, 1, i));
            if (i < 15)
                addShapelessRecipe(new ItemStack(bannerLarge, 1, i), new ItemStack(bannerLarge, 1, 15), new ItemStack(dye, 1, i));
        }

        addRecipe(new ItemStack(treasurePile, 1), " # ", "###", "###", '#', DC_GOLD_INGOT);

        addRecipe(new ItemStack(flagSmall, 2, 15), "I#", "IS", '#', wool, 'S', string, 'I', DC_STICK_WOOD);
        addRecipe(new ItemStack(flagLarge, 1, 15), "IS#", "I##", "I  ", '#', wool, 'S', string, 'I', DC_STICK_WOOD);

        for (int i = 0; i < 16; i++)
        {
            if (i < 15)
                addShapelessRecipe(new ItemStack(flagSmall, 1, i), new ItemStack(flagSmall, 1, 15), new ItemStack(dye, 1, i));
            if (i < 15)
                addShapelessRecipe(new ItemStack(flagLarge, 1, i), new ItemStack(flagLarge, 1, 15), new ItemStack(dye, 1, i));
        }

        addRecipe(new ItemStack(Item.getItemFromBlock(grandfatherClock)), "IOI", "#R#", "SGS", '#', wildcard(refinedPlank), 'O', clock, 'S', DC_COBBLESTONE, 'I', DC_STICK_WOOD, 'R', DC_REDSTONE_DUST, 'G', DC_GOLD_INGOT);

        addRecipe(new ItemStack(Item.getItemFromBlock(weaponRack)), "I I", "III", "#I#", '#', wildcard(refinedPlank), 'I', DC_STICK_WOOD);

        addRecipe(new ItemStack(Item.getItemFromBlock(grindstone)), "#I#", "W W", '#', DC_STICK_WOOD, 'I', DC_STONE, 'W', DC_WOOD_LOG);
        addRecipe(new ItemStack(grindstoneStone), " # ", "# #", " # ", '#', DC_SANDSTONE_BLOCK);

        addRecipe(new ItemStack(gong, 1, 0), " S ", " # ", "# #", '#', DC_IRON_INGOT, 'S', string);
        addRecipe(new ItemStack(gong, 1, 1), " S ", " I ", "# #", '#', DC_IRON_INGOT, 'S', string, 'I', DC_IRON_BLOCK);
        addRecipe(new ItemStack(gong, 1, 2), " S ", " I ", "I I", '#', DC_IRON_INGOT, 'S', string, 'I', DC_IRON_BLOCK);
        addRecipe(new ItemStack(mallet), "#L", "I ", '#', wool, 'I', DC_STICK_WOOD, 'L', leather);

        addRecipe(new ItemStack(pedestal, 1, woodPedestal.getIntIdentifier()), "#", "P", '#', DC_WOOD_LOG, 'P', DC_PLANK_WOOD);
        addRecipe(new ItemStack(pedestal, 1, stonePedestal.getIntIdentifier()), "#", "C", "#", '#', DC_STONE, 'C', DC_COBBLESTONE);
        addRecipe(new ItemStack(pedestal, 1, ironPedestal.getIntIdentifier()), " # ", " I ", "B#B", '#', DC_IRON_BLOCK, 'I', DC_IRON_INGOT, 'B', book);
        addRecipe(new ItemStack(pedestal, 1, goldPedestal.getIntIdentifier()), " # ", "I#I", "BSB", '#', DC_GOLD_BLOCK, 'I', DC_GOLD_INGOT, 'B', book, 'S', DC_STONE);
        addRecipe(new ItemStack(pedestal, 1, diamondPedestal.getIntIdentifier()), "I#I", "III", "BSB", '#', DC_DIAMOND_BLOCK, 'I', DC_DIAMOND_GEM, 'B', book, 'S', DC_STONE);

        addRecipe(new ItemStack(itemShelf, 1, shelfJamien), "###", "# #", '#', wildcard(refinedPlank));
        addRecipe(new ItemStack(itemShelf, 1, shelfWall), "##", '#', wildcard(refinedPlank));
        addRecipe(new ItemStack(itemShelf, 1, shelfWardrobe), "###", "# #", "#I#", '#', wildcard(refinedPlank), 'I', DC_IRON_INGOT);

        addRecipe(new ItemStack(snowGlobe), " # ", "#W#", "SIS", '#', DC_CLEAR_GLASS, 'W', DC_PLANK_WOOD, 'S', DC_STONE, 'I', DC_IRON_INGOT);

        for (int i = 0; i < 6; i++)
            addRecipe(new ItemStack(planks, 2, i), "#", "#", "#", '#', new ItemStack(plank, 1, i));

        PlanksRefinementRegistry.addRefinement(new PlanksRefinementEntry(plank, sandpaper, new ItemStack(smoothPlank), true));
        PlanksRefinementRegistry.addRefinement(new PlanksRefinementEntryBottle(smoothPlank, linseedOil, new ItemStack(refinedPlank), true));

        addShapelessRecipe(new ItemStack(sandpaper), DC_CLEAR_GLASS, slime_ball, paper);

        MinecraftForge.addGrassSeed(new ItemStack(flaxSeeds), 2);
        addShapelessRecipe(new ItemStack(linseedOil), glass_bottle, DC_FLAX_SEEDS, DC_FLAX_SEEDS, DC_FLAX_SEEDS, DC_FLAX_SEEDS, DC_FLAX_SEEDS, DC_FLAX_SEEDS, DC_FLAX_SEEDS, DC_FLAX_SEEDS);
        addRecipe(new ItemStack(wool, 1), "##", "##", '#', DC_FLAX_FIBER);

        addRecipe(new ItemStack(clubHammer), "#I#", " I ", " I ", '#', DC_IRON_INGOT, 'I', DC_STICK_WOOD);
        addRecipe(new ItemStack(detailChiselIron), "  #", " I ", "W  ", '#', DC_IRON_INGOT, 'I', DC_STICK_WOOD, 'W', DC_PLANK_WOOD);
        addRecipe(new ItemStack(carvingChiselIron), " ##", " I#", "W  ", '#', DC_IRON_INGOT, 'I', DC_STICK_WOOD, 'W', DC_PLANK_WOOD);

        addRecipe(new ItemStack(ironSaw), "I  ", "###", '#', DC_IRON_INGOT, 'I', DC_STICK_WOOD);
        addRecipe(new ItemStack(sawBench), "#IS", "#  ", '#', DC_PLANK_WOOD, 'I', DC_IRON_INGOT, 'S', DC_STICK_WOOD);
        addRecipe(new ItemStack(tablePress), "#I#", "# #", '#', DC_PLANK_WOOD, 'I', DC_IRON_INGOT);

        addRecipe(new ItemStack(lootChest), "#I#", "#R#", '#', wildcard(refinedPlank), 'I', DC_IRON_INGOT, 'R', DC_REDSTONE_DUST);
    }

    private static void addRecipe(ItemStack output, Object... params)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(output, params));
    }

    private static void addShapelessRecipe(ItemStack output, Object... params)
    {
        GameRegistry.addRecipe(new ShapelessOreRecipe(output, params));
    }

    private static ItemStack wildcard(Item item)
    {
        return new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
    }
}
