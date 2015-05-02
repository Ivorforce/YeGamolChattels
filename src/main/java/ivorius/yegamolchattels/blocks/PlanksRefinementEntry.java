package ivorius.yegamolchattels.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by lukas on 11.05.14.
 */
public class PlanksRefinementEntry implements PlanksRefinementRegistry.Entry
{
    public ItemStack source;
    public ItemStack destination;

    public Item tool;
    public ItemStack returnItem;

    public PlanksRefinementEntry(ItemStack source, ItemStack destination, Item tool, ItemStack returnItem)
    {
        this.source = source;
        this.destination = destination;
        this.tool = tool;
        this.returnItem = returnItem;
    }

    @Override
    public boolean matchesSource(ItemStack source)
    {
        return source.getItem() == this.source.getItem()
                && ItemStack.areItemStackTagsEqual(source, this.source)
                && (this.source.getItemDamage() == OreDictionary.WILDCARD_VALUE || source.getItemDamage() == this.source.getItemDamage());
    }

    @Override
    public boolean matchesTool(ItemStack tool)
    {
        return tool != null && tool.getItem() == this.tool;
    }

    @Override
    public ItemStack getResult(ItemStack source, ItemStack tool)
    {
        if (destination != null)
            return destination.copy();
        else
            return null;
    }

    @Override
    public void onToolBreak(ItemStack tool, EntityPlayer player)
    {
        if (returnItem != null)
            player.inventory.addItemStackToInventory(returnItem.copy());
    }
}
