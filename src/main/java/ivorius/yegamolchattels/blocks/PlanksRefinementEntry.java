package ivorius.yegamolchattels.blocks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by lukas on 11.05.14.
 */
public class PlanksRefinementEntry implements IPlanksRefinementEntry
{
    public Item source;
    public Item tool;
    public ItemStack destination;

    public PlanksRefinementEntry(Item source, Item tool, ItemStack destination)
    {
        this.source = source;
        this.destination = destination;
        this.tool = tool;
    }

    @Override
    public boolean matchesSource(ItemStack source)
    {
        return source != null && source.getItem() == this.source;
    }

    @Override
    public boolean matchesTool(ItemStack tool)
    {
        return tool != null && tool.getItem() == this.tool;
    }

    @Override
    public ItemStack getResult()
    {
        return destination != null ? destination.copy() : null;
    }
}
