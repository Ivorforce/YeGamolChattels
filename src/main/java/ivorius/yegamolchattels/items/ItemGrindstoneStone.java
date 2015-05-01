/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.items;

import ivorius.yegamolchattels.tabs.YGCCreativeTabs;
import net.minecraft.item.Item;

public class ItemGrindstoneStone extends Item
{
    public ItemGrindstoneStone()
    {
        super();
        this.setCreativeTab(YGCCreativeTabs.tabMain);

        setMaxStackSize(1);
    }
}
