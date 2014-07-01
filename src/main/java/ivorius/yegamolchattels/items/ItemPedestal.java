/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.IvMultiBlockHelper;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.blocks.EnumPedestalEntry;
import ivorius.yegamolchattels.blocks.TileEntityPedestal;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class ItemPedestal extends ItemBlock
{
    public ItemPedestal(Block block)
    {
        super(block);
        maxStackSize = 16;
        setHasSubtypes(true);
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        int identifier = par1ItemStack.getItemDamage();
        EnumPedestalEntry entry = EnumPedestalEntry.getEntry(identifier);

        int rotation = IvMultiBlockHelper.getRotation(par2EntityPlayer);
        List<int[]> positions = IvMultiBlockHelper.getRotatedPositions(rotation, entry.size[0], entry.size[1], entry.size[2]);

        IvMultiBlockHelper multiBlockHelper = new IvMultiBlockHelper();
        if (multiBlockHelper.beginPlacing(positions, par3World, par4, par5, par6, par7, par1ItemStack, par2EntityPlayer, this.field_150939_a, identifier, rotation))
        {
            for (int[] position : multiBlockHelper)
            {
                IvTileEntityMultiBlock tileEntity = multiBlockHelper.placeBlock(position);

                if (tileEntity instanceof TileEntityPedestal)
                {
                    ((TileEntityPedestal) tileEntity).pedestalIdentifier = par1ItemStack.getItemDamage();
                }
            }

            par1ItemStack.stackSize--;
        }

        return true;
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < EnumPedestalEntry.getNumberOfEntries(); i++)
            par3List.add(new ItemStack(this, 1, i));
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        int damage = par1ItemStack.getItemDamage();

        String material = "";
        if (damage == 0)
            material = ".planks";
        else if (damage == 1)
            material = ".stone";
        else if (damage == 2)
            material = ".iron";
        else if (damage == 3)
            material = ".gold";
        else if (damage == 4)
            material = ".diamond";
        else if (damage == 5)
            material = ".nether";

        return super.getUnlocalizedName(par1ItemStack) + material;
    }

    @Override
    public IIcon getIconFromDamage(int par1)
    {
        return field_150939_a.getIcon(0, par1);
    }
}
