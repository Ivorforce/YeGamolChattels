/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.achievements;

import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityItemShelfModel0;
import ivorius.yegamolchattels.blocks.YGCBlocks;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 26.07.14.
 */
public class YGCAchievementList
{
    public static AchievementPage page;

    public static Achievement refinedPlank;

    public static Achievement wardrobeCrafted;
    public static Achievement wardrobeSecret;

    public static Achievement grandfatherClockCrafted;

    public static Achievement weaponRackCrafted;
    public static Achievement weaponRackVariant;

    public static Achievement largeFlagCrafted;
    public static Achievement largeBannerCrafted;

    public static Achievement smallGongPlayed;
    public static Achievement mediumGongPlayed;
    public static Achievement largeGongPlayed;
    public static Achievement gongSecret;

    public static Achievement ghostKilled;

    public static Achievement zombieStatueReanimated;
    public static Achievement superExpensiveStatue;

    public static void init()
    {
        List<Achievement> achievements = new ArrayList<>();

        refinedPlank = new Achievement("achievement.refinedPlank", "refinedPlank", 0, 0, new ItemStack(YGCItems.refinedPlank), null).registerStat();
        achievements.add(refinedPlank);

        wardrobeCrafted = new Achievement("achievement.wardrobeCrafted", "wardrobeCrafted", 2, 0, new ItemStack(YGCBlocks.itemShelf, 0, TileEntityItemShelfModel0.shelfWardrobe), refinedPlank).registerStat();
        achievements.add(wardrobeCrafted);
        wardrobeSecret = new Achievement("achievement.wardrobeSecret", "wardrobeSecret", 3, 0, new ItemStack(YGCBlocks.itemShelf, 1, TileEntityItemShelfModel0.shelfWardrobe), wardrobeCrafted).registerStat();
        achievements.add(wardrobeSecret);

        grandfatherClockCrafted = new Achievement("achievement.grandfatherClockCrafted", "grandfatherClockCrafted", 2, 1, new ItemStack(YGCBlocks.grandfatherClock), refinedPlank).registerStat();
        achievements.add(grandfatherClockCrafted);

        weaponRackCrafted = new Achievement("achievement.weaponRackCrafted", "weaponRackCrafted", 2, -1, new ItemStack(YGCBlocks.weaponRack), refinedPlank).registerStat();
        achievements.add(weaponRackCrafted);
        weaponRackVariant = new Achievement("achievement.weaponRackVariant", "weaponRackVariant", 3, -1, new ItemStack(YGCBlocks.weaponRack), weaponRackCrafted).registerStat();
        achievements.add(weaponRackVariant);

        largeFlagCrafted = new Achievement("achievement.largeFlagCrafted", "largeFlagCrafted", -2, -2, new ItemStack(YGCItems.flagLarge), null).registerStat();
        achievements.add(largeFlagCrafted);
        largeBannerCrafted = new Achievement("achievement.largeBannerCrafted", "largeBannerCrafted", -1, -2, new ItemStack(YGCItems.bannerLarge), null).registerStat();
        achievements.add(largeBannerCrafted);

        smallGongPlayed = new Achievement("achievement.smallGongPlayed", "smallGongPlayed", -3, 1, new ItemStack(YGCBlocks.gong, 1, 0), null).registerStat();
        achievements.add(smallGongPlayed);
        mediumGongPlayed = new Achievement("achievement.mediumGongPlayed", "mediumGongPlayed", -4, 1, new ItemStack(YGCBlocks.gong, 1, 1), null).registerStat();
        achievements.add(mediumGongPlayed);
        largeGongPlayed = new Achievement("achievement.largeGongPlayed", "largeGongPlayed", -3, 2, new ItemStack(YGCBlocks.gong, 1, 2), null).registerStat();
        achievements.add(largeGongPlayed);
        gongSecret = new Achievement("achievement.gongSecret", "gongSecret", -4, 2, new ItemStack(YGCBlocks.gong, 1), null).registerStat();
        achievements.add(gongSecret);

        ghostKilled = new Achievement("achievement.ghostKilled", "ghostKilled", 0, 3, new ItemStack(Blocks.wool), null).registerStat();
        achievements.add(ghostKilled);

        zombieStatueReanimated = new Achievement("achievement.zombieStatueReanimated", "zombieStatueReanimated", 3, 4, new ItemStack(Items.skull, 1, 2), null).registerStat();
        achievements.add(zombieStatueReanimated);
        superExpensiveStatue = new Achievement("achievement.superExpensiveStatue", "superExpensiveStatue", 3, 3, new ItemStack(Blocks.gold_block), null).registerStat();
        achievements.add(superExpensiveStatue);

        page = new AchievementPage(YeGamolChattels.NAME, achievements.toArray(new Achievement[achievements.size()]));
        AchievementPage.registerAchievementPage(page);
    }
}
