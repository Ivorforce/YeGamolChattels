package ivorius.yegamolchattels.items;

import net.minecraft.block.BlockWood;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * Created by lukas on 05.05.14.
 */
public class ItemPlank extends Item
{
    public IIcon[] icons;

    public ItemPlank()
    {
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        int dmg = par1ItemStack.getItemDamage();
        String addition = dmg < BlockWood.field_150096_a.length ? ("." + BlockWood.field_150096_a[dmg]) : "";

        return super.getUnlocalizedName() + addition;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        icons = new IIcon[BlockWood.field_150096_a.length];
        for (int i = 0; i < icons.length; i++)
        {
            icons[i] = iconRegister.registerIcon(getIconString() + BlockWood.field_150096_a[i]);
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage)
    {
        return icons[damage % icons.length];
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List list)
    {
        for (int i = 0; i < 6; i++)
            list.add(new ItemStack(item, 1, i));
    }
}
