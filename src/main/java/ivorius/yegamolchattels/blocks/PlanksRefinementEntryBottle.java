package ivorius.yegamolchattels.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by lukas on 01.10.14.
 */
public class PlanksRefinementEntryBottle extends PlanksRefinementEntry
{
    public PlanksRefinementEntryBottle(Item source, Item tool, ItemStack destination)
    {
        super(source, tool, destination);
    }

    public PlanksRefinementEntryBottle(Item source, Item tool, ItemStack destination, boolean copyMetadata)
    {
        super(source, tool, destination, copyMetadata);
    }

    @Override
    public void onToolBreak(ItemStack tool, EntityPlayer player)
    {
        super.onToolBreak(tool, player);

        player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
    }
}
