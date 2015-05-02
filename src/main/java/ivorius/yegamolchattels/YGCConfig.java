/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels;

import ivorius.yegamolchattels.blocks.PlankSawEntry;
import ivorius.yegamolchattels.blocks.PlankSawRegistry;
import ivorius.yegamolchattels.blocks.PlanksRefinementEntry;
import ivorius.yegamolchattels.blocks.PlanksRefinementRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Property;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static ivorius.yegamolchattels.YeGamolChattels.*;

/**
 * Created by lukas on 10.07.14.
 */
public class YGCConfig
{
    public static final String CATEGORY_BALANCING = "balancing";
    public static final String CATEGORY_VISUAL = "visual";

    private static final Set<String> lifeStatuesBlacklist = new HashSet<>();
    public static boolean areLifeStatuesAllowed;
    private static final Set<String> equippableStatues = new HashSet<>();
    public static boolean easterEggsAllowed;

    public static boolean fetchDynamicStatueTextures;
    public static boolean doStatueTextureMerge;

    public static boolean genFlax;

    public static double entityVitaDropChance;

    private static final Set<String> itemShelfBlacklist = new HashSet<>();

    public static final Set<PlankSawRegistry.Entry> customPlankSawing = new HashSet<>();
    public static final Set<PlanksRefinementRegistry.Entry> customPlankRefinement = new HashSet<>();

    public static void loadConfig(String configID)
    {
//        if (configID == null || configID.equals(Configuration.CATEGORY_GENERAL))
//        {
//        }
//
        if (configID == null || configID.equals(CATEGORY_BALANCING))
        {
            getStringSet(lifeStatuesBlacklist, config.get(CATEGORY_BALANCING, "lifeStatuesBlacklist", new String[]{"EnderDragon", "Giant", "WitherBoss"}, "Entity IDs that are not allowed to come alive from statues."));
            getStringSet(equippableStatues, config.get(CATEGORY_BALANCING, "equippableStatues", new String[]{"Zombie", "Skeleton", "PigZombie"}, "Entity IDs that will be treated as equippable mobs as statues"));

            areLifeStatuesAllowed = config.get(CATEGORY_BALANCING, "areLifeStatuesAllowed", true, "Are statues allowed to come to life with redstone input?").getBoolean();
            easterEggsAllowed = config.get(CATEGORY_BALANCING, "easterEggsAllowed", true).getBoolean();

            genFlax = config.get(CATEGORY_BALANCING, "generateFlax", true).getBoolean();

            entityVitaDropChance = config.get(CATEGORY_BALANCING, "entityVitaDropChance", 0.01, "Drop chance of entity vitas when killing a mob. <Temporary config option: Will be removed when the new system is added>").getDouble();

            itemShelfBlacklist.clear();
            for (String s : config.get(CATEGORY_BALANCING, "itemShelfBlacklist", new String[0], "List of item IDs that are not allowed to be placed in item shelves.").getStringList())
                itemShelfBlacklist.add(s.contains(":") ? s : "minecraft:" + s);

            customPlankSawing.clear();
            for (String s : config.get(CATEGORY_BALANCING, "customPlankSawing", new String[0], "List of additional plank saw recipes. Form: SourceJson->DestJson. Example: {id:wood,Damage:2}->{id:yegamolchattels:plank,Damage:2}").getStringList())
            {
                String[] params = s.split("->");
                if (params.length == 2)
                {
                    ItemStack source = tryParseItemStack(params[0]);
                    ItemStack dest = tryParseItemStack(params[1]);

                    if (source != null && dest != null)
                        customPlankSawing.add(new PlankSawEntry(source, dest));
                }
            }

            customPlankRefinement.clear();
            for (String s : config.get(CATEGORY_BALANCING, "itemShelfBlacklist", new String[0], "List of additional plank refinement recipes. Form: SourceJson->ToolJson->DestJson->ToolReturnJson (optional). Example: {id:yegamolchattels:plank,Damage:2}->{id:yegamolchattels:linseed_oil}->{id:yegamolchattels:smoothed_plank,Damage:2}->{id:glass_bottle}").getStringList())
            {
                String[] params = s.split("->");
                if (params.length >= 3)
                {
                    ItemStack source = tryParseItemStack(params[0]);
                    ItemStack tool = tryParseItemStack(params[1]);
                    ItemStack dest = tryParseItemStack(params[2]);
                    ItemStack returnTool = params.length >= 4 ? tryParseItemStack(params[3]) : null;

                    if (source != null && tool != null && dest != null)
                        customPlankRefinement.add(new PlanksRefinementEntry(source, dest, tool.getItem(), returnTool));
                }
            }
        }

        proxy.loadConfig(configID);
    }

    private static ItemStack tryParseItemStack(String json)
    {
        NBTTagCompound compound = tryParseTagCompound(json);
        return compound != null ? ItemStack.loadItemStackFromNBT(compound) : null;
    }

    private static NBTTagCompound tryParseTagCompound(String json)
    {
        NBTBase sourceNBT;

        try
        {
            sourceNBT = JsonToNBT.func_150315_a(json);
        }
        catch (NBTException e)
        {
            return null;
        }

        return sourceNBT instanceof NBTTagCompound ? (NBTTagCompound) sourceNBT : null;
    }

    private static void getStringSet(Set<String> set, Property property)
    {
        set.clear();
        Collections.addAll(set, property.getStringList());
    }

    public static boolean isEntityEquippable(Entity entity)
    {
        return equippableStatues.contains(EntityList.getEntityString(entity));
    }

    public static boolean mayEntityStatueComeAlive(Entity entity)
    {
        return !lifeStatuesBlacklist.contains(EntityList.getEntityString(entity));
    }

    public static boolean mayItemBeStoredInShelf(ItemStack stack)
    {
        return !itemShelfBlacklist.contains(Item.itemRegistry.getNameForObject(stack.getItem()));
    }
}
