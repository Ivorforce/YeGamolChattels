package ivorius.yegamolchattels.blocks;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by lukas on 11.05.14.
 */
public class PlankSawEntry implements PlankSawRegistry.Entry
{
    public ItemStack source;
    public ItemStack destination;

    public PlankSawEntry(ItemStack source, ItemStack destination)
    {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public boolean matchesSource(ItemStack source)
    {
        return source.getItem() == this.source.getItem()
                && ItemStack.areItemStackTagsEqual(source, this.source)
                && (this.source.getItemDamage() == OreDictionary.WILDCARD_VALUE || source.getItemDamage() == this.source.getItemDamage());
    }

    @Override
    public ItemStack getResult(ItemStack source)
    {
        if (destination != null)
            return destination.copy();
        else
            return null;
    }
}
