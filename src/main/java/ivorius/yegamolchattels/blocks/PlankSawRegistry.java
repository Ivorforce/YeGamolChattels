package ivorius.yegamolchattels.blocks;

import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 02.05.15.
 */
public class PlankSawRegistry
{
    private static final List<Entry> entries = new ArrayList<>();

    public static void addSawing(Entry entry)
    {
        entries.add(entry);
    }

    public static List<Entry> allEntries()
    {
        return Collections.unmodifiableList(entries);
    }

    public static Entry entry(ItemStack source)
    {
        for (PlankSawRegistry.Entry entry : entries)
        {
            if (entry.matchesSource(source))
                return entry;
        }

        return null;
    }

    public static boolean canSawItem(ItemStack ingredient)
    {
        return entry(ingredient) != null;
    }

    public static ItemStack getSawResult(int stackSize, ItemStack ingredient)
    {
        Entry entry = entry(ingredient);

        if (entry != null)
        {
            ItemStack result = entry.getResult(ingredient);
            result.stackSize = stackSize;
            return result;
        }
        else
            return null;
    }

    public interface Entry
    {
        boolean matchesSource(ItemStack source);

        ItemStack getResult(ItemStack source);
    }
}
