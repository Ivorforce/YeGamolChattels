/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.client.rendering;

import net.minecraft.client.renderer.GLAllocation;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lukas on 28.07.14.
 */
public class SnowGlobeCallListHandler
{
    private static Set<Integer> callListsToDestroy = new HashSet<>();

    public static void addCallListToDestroy(int callList)
    {
        callListsToDestroy.add(callList);
    }

    public static void destroyAllStoredCallLists()
    {
        for (int i : callListsToDestroy)
        {
            GLAllocation.deleteDisplayLists(i);
        }

        callListsToDestroy.clear();
    }
}
