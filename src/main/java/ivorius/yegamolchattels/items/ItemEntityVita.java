/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lukas on 27.07.14.
 */
public class ItemEntityVita extends Item
{
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (Map.Entry<String, Class> mobID : (Set<Map.Entry<String, Class>>) EntityList.stringToClassMapping.entrySet())
        {
            if (canClassBeValidVita(mobID.getValue()))
                list.add(createVitaItemStack(item, mobID.getKey()));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack par1ItemStack)
    {
        String base = super.getUnlocalizedName(par1ItemStack) + ".base";

        String entityName = getEntityID(par1ItemStack);
        String localizedEntityName = entityName != null && entityName.length() > 0
                ? StatCollector.translateToLocalFormatted("entity." + entityName + ".name")
                : StatCollector.translateToLocalFormatted("tile.ygcStatue.unknown");

        return StatCollector.translateToLocalFormatted(base, localizedEntityName);
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

    public static ItemStack createVitaItemStackAsNewbornEntity(Item item, Entity entity)
    {
        ItemStack stack = new ItemStack(item);
        setEntity(stack, entity);

        Entity savedEntity = createEntity(stack, entity.worldObj);

        if (savedEntity != null)
        {
            savedEntity.isDead = false;

            if (savedEntity instanceof EntityLivingBase)
            {
                EntityLivingBase entityLiving = (EntityLivingBase) savedEntity;
                entityLiving.deathTime = 0;
                entityLiving.attackTime = 0;
                entityLiving.hurtTime = 0;
                entityLiving.extinguish();
                entityLiving.setHealth(entityLiving.getMaxHealth());
            }

            setEntity(stack, savedEntity);

            return stack;
        }

        return null;
    }

    public static boolean canClassBeValidVita(Class<? extends Entity> entityClass)
    {
        return EntityLiving.class.isAssignableFrom(entityClass);
    }
}
