/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.items;

import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemGrindstoneStone extends Item
{
    public ItemGrindstoneStone()
    {
        super();
        this.setCreativeTab(YeGamolChattels.tabMain);

        setMaxStackSize(1);
    }
}
