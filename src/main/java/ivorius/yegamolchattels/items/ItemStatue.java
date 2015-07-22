/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.IvMultiBlockHelper;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.blocks.Statue;
import ivorius.yegamolchattels.blocks.TileEntityStatue;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class ItemStatue extends ItemBlock
{
    public int statueMaterial;

    public ItemStatue(Block block, Integer material)
    {
        super(block);
        maxStackSize = 1;

        this.statueMaterial = material;
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, int blockSide, float hitX, float hitY, float hitZ)
    {
        Statue statue = createStatue(par1ItemStack, par3World);

        if (statue != null)
        {
            if (!par3World.isRemote) // Some entities start with random sizes
            {
                int rotation = 0;
                List<int[]> positions = getStatuePositions(statue.getEntity(), rotation);

                IvMultiBlockHelper multiBlockHelper = new IvMultiBlockHelper();
                if (multiBlockHelper.beginPlacing(positions, par3World, pos, blockSide, par1ItemStack, par2EntityPlayer, this.field_150939_a, 0, rotation))
                {
                    for (int[] position : multiBlockHelper)
                    {
                        IvTileEntityMultiBlock tileEntity = multiBlockHelper.placeBlock(position);

                        if (tileEntity instanceof TileEntityStatue && tileEntity.isParent())
                        {
                            TileEntityStatue tileEntityStatue = (TileEntityStatue) tileEntity;
                            tileEntityStatue.setStatue(statue);
                            tileEntityStatue.setStatueRotationYaw((par2EntityPlayer.rotationYaw + 180.0f) % 360.0f);
                        }
                    }

                    par1ItemStack.stackSize--;
                }
            }

            return true;
        }

        return false;
    }

    public static List<int[]> getStatuePositions(Entity entity, int rotation)
    {
        int statueWidth = MathHelper.ceiling_float_int(entity.width);
        int statueHeight = MathHelper.ceiling_float_int(entity.height);

        return IvMultiBlockHelper.getRotatedPositions(rotation, statueWidth, statueHeight, statueWidth);
    }

    @Override
    public String getItemStackDisplayName(ItemStack par1ItemStack)
    {
        String base = super.getUnlocalizedName(par1ItemStack) + ".base";

        String entityName = getStatueEntityID(par1ItemStack);
        String localizedEntityName = entityName != null && entityName.length() > 0 ? I18n.format("entity." + entityName + ".name") : I18n.format("tile.ygcStatue.unknown");

        Statue.BlockFragment blockFragment = getStatueBlockFragment(par1ItemStack);
        String localizedBlockName = blockFragment != null ? new ItemStack(blockFragment.getBlock(), 1, blockFragment.getMetadata()).getDisplayName() : I18n.format("tile.ygcStatue.nomaterial");

        return I18n.format(base, localizedEntityName, localizedBlockName);
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
//        for (int var4 = 0; var4 < BlockStatue.statueCrafting.length / 2; ++var4)
//        {
//            String mobName = (String) BlockStatue.statueCrafting[var4 * 2 + 1];
//            par3List.add(createStatueItemStack(this, mobName, new TileEntityStatue.BlockFragment(Blocks.brick_block, 0)));
//            par3List.add(createStatueItemStack(this, mobName, new TileEntityStatue.BlockFragment(Blocks.stone, 0)));
//            par3List.add(createStatueItemStack(this, mobName, new TileEntityStatue.BlockFragment(Blocks.planks, 0)));
//            par3List.add(createStatueItemStack(this, mobName, new TileEntityStatue.BlockFragment(Blocks.planks, 1)));
//            par3List.add(createStatueItemStack(this, mobName, new TileEntityStatue.BlockFragment(Blocks.planks, 2)));
//            par3List.add(createStatueItemStack(this, mobName, new TileEntityStatue.BlockFragment(Blocks.planks, 3)));
//            par3List.add(createStatueItemStack(this, mobName, new TileEntityStatue.BlockFragment(Blocks.lit_pumpkin, 0)));
//            par3List.add(createStatueItemStack(this, mobName, new TileEntityStatue.BlockFragment(Blocks.iron_ore, 0)));
//        }
    }

    public static Statue createStatue(ItemStack stack, World world)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("statue"))
            return new Statue(stack.getTagCompound().getCompoundTag("statue"), world);

        return null;
    }

    public static void setStatue(ItemStack stack, Statue statue)
    {
        stack.setTagInfo("statue", statue.createTagCompound());
    }

    public static Statue.BlockFragment getStatueBlockFragment(ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("statue"))
            return Statue.getMaterial(stack.getTagCompound().getCompoundTag("statue"));

        return null;
    }

    public static String getStatueEntityID(ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("statue"))
            return Statue.getEntityID(stack.getTagCompound().getCompoundTag("statue"));

        return null;
    }
}
