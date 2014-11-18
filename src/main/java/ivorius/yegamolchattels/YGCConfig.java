/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels;

import net.minecraftforge.common.config.Configuration;

import static ivorius.yegamolchattels.YeGamolChattels.*;

/**
 * Created by lukas on 10.07.14.
 */
public class YGCConfig
{
    public static final String CATEGORY_BALANCING = "balancing";
    public static final String CATEGORY_VISUAL = "visual";

    public static boolean areDangerousStatuesAllowed;
    public static boolean areLifeStatuesAllowed;
    public static boolean easterEggsAllowed;

    public static boolean fetchDynamicStatueTextures;
    public static boolean doStatueTextureMerge;

    public static boolean genFlax;

    public static void loadConfig(String configID)
    {
        if (configID == null || configID.equals(Configuration.CATEGORY_GENERAL))
        {
        }

        if (configID == null || configID.equals(CATEGORY_BALANCING))
        {
            areDangerousStatuesAllowed = config.get(CATEGORY_BALANCING, "areDangerousStatuesAllowed", false, "Are dangerous statues allowed to come to life? (e.g. Ender Dragon)").getBoolean();
            areLifeStatuesAllowed = config.get(CATEGORY_BALANCING, "areLifeStatuesAllowed", true, "Are statues allowed to come to life with redstone input?").getBoolean();
            easterEggsAllowed = config.get(CATEGORY_BALANCING, "easterEggsAllowed", true).getBoolean();

            genFlax = config.get(CATEGORY_BALANCING, "generateFlax", true).getBoolean();
        }

        proxy.loadConfig(configID);
    }
}
