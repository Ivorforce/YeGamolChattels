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

    public boolean copyMetadata;

    public PlanksRefinementEntry(Item source, Item tool, ItemStack destination)
    {
        this(source, tool, destination, false);
    }

    public PlanksRefinementEntry(Item source, Item tool, ItemStack destination, boolean copyMetadata)
    {
        this.source = source;
        this.tool = tool;
        this.destination = destination;
        this.copyMetadata = copyMetadata;
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
    public ItemStack getResult(ItemStack source)
    {
        if (destination != null)
        {
            ItemStack stack = destination.copy();
            if (copyMetadata)
                stack.setItemDamage(source.getItemDamage());
            return stack;
        }
        else
            return null;
    }
}
