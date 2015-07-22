package ivorius.yegamolchattels.items;

import net.minecraft.block.BlockPlanks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by lukas on 05.05.14.
 */
public class ItemPlank extends Item
{
    public ItemPlank()
    {
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        int dmg = par1ItemStack.getItemDamage();
        String addition = dmg < BlockPlanks.EnumType.values().length ? ("." + BlockPlanks.EnumType.values()[dmg]) : "";

        return super.getUnlocalizedName() + addition;
    }
}
