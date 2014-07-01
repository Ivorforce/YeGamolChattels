package ivorius.yegamolchattels.blocks;

import net.minecraft.item.ItemStack;

/**
 * Created by lukas on 11.05.14.
 */
public interface IPlanksRefinementEntry
{
    public boolean matchesSource(ItemStack source);

    public boolean matchesTool(ItemStack tool);

    public ItemStack getResult();
}
