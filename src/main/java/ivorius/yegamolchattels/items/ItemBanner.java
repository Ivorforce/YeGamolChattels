/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.items;

import ivorius.yegamolchattels.entities.EntityBanner;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import java.util.List;

public class ItemBanner extends ItemHangingEntity
{
    public int bannerSize;
    public String namePrefix;

    public static final int[] bannerColors = new int[]{0x1e1b1b, 0xb3312c, 0x3b511a, 0x51301a, 0x253192, 0x7b2fbe, 0x287697, 0x888888, 0x434343, 0xd88198, 0x41cd34, 0xdecf2a, 0x6689d3, 0xc354cd, 0xeb8844, 0xf0f0f0};

    public ItemBanner(int bannerSize, String namePrefix)
    {
        super(EntityBanner.class);
        setHasSubtypes(true);
        setMaxDamage(0);

        this.bannerSize = bannerSize;
        this.namePrefix = namePrefix;
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par7 == 0 || par7 == 1)
        {
            return false;
        }
        else
        {
            if (bannerSize == 2)
                par5 -= 2; //Fix for banners

            int var11 = Direction.facingToDirection[par7];
            EntityHanging var12 = this.createHangingEntity(par3World, par4, par5, par6, var11, par1ItemStack.getItemDamage());

            if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
            {
                return false;
            }
            else
            {
                if (var12 != null && var12.onValidSurface())
                {
                    if (!par3World.isRemote)
                    {
                        par3World.spawnEntityInWorld(var12);
                    }

                    --par1ItemStack.stackSize;
                }

                return true;
            }
        }
    }

    private EntityHanging createHangingEntity(World par1World, int par2, int par3, int par4, int par5, int damage)
    {
        return new EntityBanner(par1World, par2, par3, par4, par5, bannerSize, damage);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return super.getUnlocalizedName(itemStack) + ".dye" + itemStack.getItemDamage();
    }

    @Override
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        return ItemBanner.bannerColors[par1ItemStack.getItemDamage()];
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int var4 = 0; var4 < 16; ++var4)
        {
            par3List.add(new ItemStack(par1, 1, var4));
        }
    }
}
