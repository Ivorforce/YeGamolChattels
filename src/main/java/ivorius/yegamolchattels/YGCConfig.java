/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels;

import net.minecraftforge.common.config.Configuration;

/**
 * Created by lukas on 10.07.14.
 */
public class YGCConfig
{
    public static boolean areDangerousStatuesAllowed;
    public static boolean areLifeStatuesAllowed;
    public static boolean easterEggsAllowed;

    public static boolean fetchDynamicStatueTextures;
    public static boolean doStatueTextureMerge;

    public static void loadConfig(String configID)
    {
        if (configID == null || configID.equals(Configuration.CATEGORY_GENERAL))
        {
            areDangerousStatuesAllowed = YeGamolChattels.config.get(Configuration.CATEGORY_GENERAL, "areDangerousStatuesAllowed", false, "Are dangerous statues allowed to come to life? (e.g. Ender Dragon)").getBoolean();
            areLifeStatuesAllowed = YeGamolChattels.config.get(Configuration.CATEGORY_GENERAL, "areLifeStatuesAllowed", true, "Are statues allowed to come to life with redstone input?").getBoolean();
            easterEggsAllowed = YeGamolChattels.config.get(Configuration.CATEGORY_GENERAL, "easterEggsAllowed", true).getBoolean();
        }

        YeGamolChattels.proxy.loadConfig(configID);
    }
}
