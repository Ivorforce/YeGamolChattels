package ivorius.yegamolchattels.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * Created by lukas on 28.09.14.
 */
public interface MicroblockSelector
{
    boolean showMicroblockSelection(EntityLivingBase renderEntity, ItemStack stack);

    float microblockSelectionSize(ItemStack stack);
}
