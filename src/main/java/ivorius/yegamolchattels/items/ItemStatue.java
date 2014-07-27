/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.IvMultiBlockHelper;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.blocks.BlockStatue;
import ivorius.yegamolchattels.blocks.TileEntityStatue;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagString;
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
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int blockSide, float hitX, float hitY, float hitZ)
    {
        if (!par3World.isRemote) // Some entities start with random sizes
        {
            Entity statueEntity = createStatueEntity(par1ItemStack, par3World);
            if (statueEntity == null)
                statueEntity = new EntityPig(par3World);

            int rotation = IvMultiBlockHelper.getRotation(par2EntityPlayer);
            List<int[]> positions = getStatuePositions(statueEntity, rotation);

            IvMultiBlockHelper multiBlockHelper = new IvMultiBlockHelper();
            if (multiBlockHelper.beginPlacing(positions, par3World, x, y, z, blockSide, par1ItemStack, par2EntityPlayer, this.field_150939_a, 0, rotation))
            {
                for (int[] position : multiBlockHelper)
                {
                    IvTileEntityMultiBlock tileEntity = multiBlockHelper.placeBlock(position);

                    if (tileEntity instanceof TileEntityStatue && tileEntity.isParent())
                    {
                        TileEntityStatue statue = (TileEntityStatue) tileEntity;
                        statue.setStatueEntity(statueEntity, true);
                        statue.setStatueBlock(getStatueBlockFragment(par1ItemStack));
                    }
                }

                par1ItemStack.stackSize--;
            }
        }

        return true;
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

        TileEntityStatue.BlockFragment blockFragment = getStatueBlockFragment(par1ItemStack);
        String localizedBlockName = new ItemStack(blockFragment.getBlock(), 1, blockFragment.getMetadata()).getDisplayName();

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

    public static NBTTagCompound getStatueEntityTag(ItemStack stack)
    {
        return stack.hasTagCompound() ? stack.getTagCompound().getCompoundTag("statueEntity") : new NBTTagCompound();
    }

    public static String getStatueEntityID(ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("statueEntity"))
            return getStatueEntityTag(stack).getString("id");

        return stack.hasTagCompound() ? stack.getTagCompound().getString("statueEntityID") : "";
    }

    public static Entity createStatueEntity(ItemStack stack, World world)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("statueEntity"))
            return EntityList.createEntityFromNBT(getStatueEntityTag(stack), world);
        else
            return EntityList.createEntityByName(getStatueEntityID(stack), world);
    }

    public static void setStatueEntityByID(ItemStack stack, String entityName)
    {
        stack.setTagInfo("statueEntityID", new NBTTagString(entityName));
    }

    public static void setStatueEntity(ItemStack stack, Entity entity)
    {
        NBTTagCompound compound = new NBTTagCompound();
        entity.writeToNBTOptional(compound);
        stack.setTagInfo("statueEntity", compound);
    }

    public static ItemStack createStatueItemStack(Item item, String entityName, TileEntityStatue.BlockFragment blockFragment)
    {
        ItemStack stack = new ItemStack(item);
        setStatueEntityByID(stack, entityName);
        setStatueBlockFragment(stack, blockFragment);
        return stack;
    }

    public static TileEntityStatue.BlockFragment getStatueBlockFragment(ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("statueBlock"))
            return new TileEntityStatue.BlockFragment((Block) Block.blockRegistry.getObject(stack.getTagCompound().getString("statueBlock")), stack.getTagCompound().getInteger("statueMetadata"));

        return new TileEntityStatue.BlockFragment(Blocks.stone, 0);
    }

    public static void setStatueBlockFragment(ItemStack stack, TileEntityStatue.BlockFragment fragment)
    {
        stack.setTagInfo("statueBlock", new NBTTagString(Block.blockRegistry.getNameForObject(fragment.getBlock())));
        stack.setTagInfo("statueMetadata", new NBTTagInt(fragment.getMetadata()));
    }
}
