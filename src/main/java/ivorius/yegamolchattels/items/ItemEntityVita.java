/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import ivorius.yegamolchattels.blocks.TileEntityStatue;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by lukas on 27.07.14.
 */
public class ItemEntityVita extends Item
{
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(createVitaItemStack(item, "ygcGhost"));
        list.add(createVitaItemStack(item, "Creeper"));
        list.add(createVitaItemStack(item, "Skeleton"));
        list.add(createVitaItemStack(item, "Spider"));
        list.add(createVitaItemStack(item, "Giant"));
        list.add(createVitaItemStack(item, "Zombie"));
        list.add(createVitaItemStack(item, "Slime"));
        list.add(createVitaItemStack(item, "Ghast"));
        list.add(createVitaItemStack(item, "PigZombie"));
        list.add(createVitaItemStack(item, "Enderman"));
        list.add(createVitaItemStack(item, "CaveSpider"));
        list.add(createVitaItemStack(item, "Silverfish"));
        list.add(createVitaItemStack(item, "Blaze"));
        list.add(createVitaItemStack(item, "LavaSlime"));
        list.add(createVitaItemStack(item, "EnderDragon"));
        list.add(createVitaItemStack(item, "WitherBoss"));
        list.add(createVitaItemStack(item, "Bat"));
        list.add(createVitaItemStack(item, "Witch"));
        list.add(createVitaItemStack(item, "Pig"));
        list.add(createVitaItemStack(item, "Sheep"));
        list.add(createVitaItemStack(item, "Cow"));
        list.add(createVitaItemStack(item, "Chicken"));
        list.add(createVitaItemStack(item, "Squid"));
        list.add(createVitaItemStack(item, "Wolf"));
        list.add(createVitaItemStack(item, "MushroomCow"));
        list.add(createVitaItemStack(item, "SnowMan"));
        list.add(createVitaItemStack(item, "Ozelot"));
        list.add(createVitaItemStack(item, "VillagerGolem"));
        list.add(createVitaItemStack(item, "EntityHorse"));
        list.add(createVitaItemStack(item, "Villager"));
    }

    @Override
    public String getItemStackDisplayName(ItemStack par1ItemStack)
    {
        String base = super.getUnlocalizedName(par1ItemStack) + ".base";

        String entityName = getEntityID(par1ItemStack);
        String localizedEntityName = entityName != null && entityName.length() > 0 ? I18n.format("entity." + entityName + ".name") : I18n.format("tile.ygcStatue.unknown");

        return I18n.format(base, localizedEntityName);
    }

    @Override
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        return getEntityID(par1ItemStack).hashCode() | 0xff000000;
    }

    public static NBTTagCompound getEntityTag(ItemStack stack)
    {
        return stack.hasTagCompound() ? stack.getTagCompound().getCompoundTag("vitaEntity") : new NBTTagCompound();
    }

    public static String getEntityID(ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("vitaEntity"))
            return getEntityTag(stack).getString("id");

        return stack.hasTagCompound() ? stack.getTagCompound().getString("vitaEntityID") : "";
    }

    public static Entity createEntity(ItemStack stack, World world)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("vitaEntity"))
            return EntityList.createEntityFromNBT(getEntityTag(stack), world);
        else
            return EntityList.createEntityByName(getEntityID(stack), world);
    }

    public static void setEntityByID(ItemStack stack, String entityName)
    {
        stack.setTagInfo("vitaEntityID", new NBTTagString(entityName));
    }

    public static void setEntity(ItemStack stack, Entity entity)
    {
        NBTTagCompound compound = new NBTTagCompound();
        entity.writeToNBTOptional(compound);
        stack.setTagInfo("vitaEntity", compound);
    }

    public static ItemStack createVitaItemStack(Item item, String entityName)
    {
        ItemStack stack = new ItemStack(item);
        setEntityByID(stack, entityName);
        return stack;
    }
}
