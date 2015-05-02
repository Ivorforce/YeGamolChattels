package ivorius.yegamolchattels.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 01.10.14.
 */
public class PlanksRefinementRegistry
{
    private static List<Entry> planksRefinementEntries = new ArrayList<>();

    public static void addRefinement(Entry entry)
    {
        planksRefinementEntries.add(entry);
    }

    public static List<Entry> allEntries()
    {
        return Collections.unmodifiableList(planksRefinementEntries);
    }

    public static Entry entry(ItemStack source, ItemStack tool)
    {
        for (PlanksRefinementRegistry.Entry entry : PlanksRefinementRegistry.planksRefinementEntries)
        {
            if (entry.matchesSource(source) && entry.matchesTool(tool))
            {
                return entry;
            }
        }

        return null;
    }

    public interface Entry
    {
        boolean matchesSource(ItemStack source);

        boolean matchesTool(ItemStack tool);

        ItemStack getResult(ItemStack source, ItemStack tool);

        void onToolBreak(ItemStack tool, EntityPlayer player);
    }
}
