/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.achievements;

import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityItemShelfModel0;
import ivorius.yegamolchattels.blocks.YGCBlocks;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

/**
 * Created by lukas on 26.07.14.
 */
public class YGCAchievementList
{
    public static AchievementPage page;

    public static Achievement refinedPlank;
    public static Achievement wardrobeCrafted;
    public static Achievement grandfatherClockCrafted;
    public static Achievement weaponRackCrafted;

    public static Achievement largeFlagCrafted;
    public static Achievement largeBannerCrafted;

    public static Achievement smallGongPlayed;
    public static Achievement mediumGongPlayed;
    public static Achievement largeGongPlayed;

    public static Achievement ghostKilled;

    public static void init()
    {
        refinedPlank = new Achievement("achievement.refinedPlank", "refinedPlank", 0, 0, new ItemStack(YGCItems.refinedPlank), null).registerStat();
        wardrobeCrafted = new Achievement("achievement.wardrobeCrafted", "wardrobeCrafted", 2, 0, new ItemStack(YGCBlocks.itemShelf, 0, TileEntityItemShelfModel0.shelfWardrobe), refinedPlank).registerStat();
        grandfatherClockCrafted = new Achievement("achievement.grandfatherClockCrafted", "grandfatherClockCrafted", 2, 1, new ItemStack(YGCBlocks.grandfatherClock), refinedPlank).registerStat();
        weaponRackCrafted = new Achievement("achievement.weaponRackCrafted", "weaponRackCrafted", 2, -1, new ItemStack(YGCBlocks.weaponRack), refinedPlank).registerStat();

        largeFlagCrafted = new Achievement("achievement.largeFlagCrafted", "largeFlagCrafted", -2, -2, new ItemStack(YGCItems.flagLarge), null).registerStat();
        largeBannerCrafted = new Achievement("achievement.largeBannerCrafted", "largeBannerCrafted", -1, -2, new ItemStack(YGCItems.bannerLarge), null).registerStat();

        smallGongPlayed = new Achievement("achievement.smallGongPlayed", "smallGongPlayed", -3, 1, new ItemStack(YGCBlocks.gong, 1, 0), null).registerStat();
        mediumGongPlayed = new Achievement("achievement.mediumGongPlayed", "mediumGongPlayed", -3, 2, new ItemStack(YGCBlocks.gong, 1, 1), null).registerStat();
        largeGongPlayed = new Achievement("achievement.largeGongPlayed", "largeGongPlayed", -3, 3, new ItemStack(YGCBlocks.gong, 1, 2), null).registerStat();

        ghostKilled = new Achievement("achievement.ghostKilled", "ghostKilled", 2, 3, new ItemStack(Blocks.wool), null).registerStat();

        page = new AchievementPage(YeGamolChattels.NAME, refinedPlank, wardrobeCrafted, grandfatherClockCrafted, weaponRackCrafted, largeFlagCrafted, largeBannerCrafted, smallGongPlayed, mediumGongPlayed, largeGongPlayed, ghostKilled);
        AchievementPage.registerAchievementPage(page);
    }
}
