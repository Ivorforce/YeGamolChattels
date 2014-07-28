/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.events;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ivorius.yegamolchattels.YGCConfig;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.achievements.YGCAchievementList;
import ivorius.yegamolchattels.blocks.TileEntityItemShelfModel0;
import ivorius.yegamolchattels.blocks.YGCBlocks;
import ivorius.yegamolchattels.client.rendering.SnowGlobeCallListHandler;
import ivorius.yegamolchattels.client.rendering.StatueTextureHandler;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.item.ItemStack;

/**
 * Created by lukas on 24.05.14.
 */
public class YGCFMLEventHandler
{
    public void register()
    {
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent tickEvent)
    {
        if (tickEvent instanceof TickEvent.RenderTickEvent)
        {
            doRenderTick((TickEvent.RenderTickEvent) tickEvent);
        }
    }

    @SideOnly(Side.CLIENT)
    private void doRenderTick(TickEvent.RenderTickEvent renderTickEvent)
    {
        if (renderTickEvent.phase == TickEvent.Phase.START)
        {
            StatueTextureHandler.deallocateAllFreeTextures();
            SnowGlobeCallListHandler.destroyAllStoredCallLists();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent event)
    {
        if (event instanceof ConfigChangedEvent.OnConfigChangedEvent && event.modID.equals(YeGamolChattels.MODID))
        {
            YGCConfig.loadConfig(event.configID);
        }
    }

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event)
    {
        if (event.crafting.isItemEqual(new ItemStack(YGCBlocks.itemShelf, 1, TileEntityItemShelfModel0.shelfWardrobe)))
        {
            event.player.triggerAchievement(YGCAchievementList.wardrobeCrafted);
        }
        else if (event.crafting.isItemEqual(new ItemStack(YGCBlocks.grandfatherClock)))
        {
            event.player.triggerAchievement(YGCAchievementList.grandfatherClockCrafted);
        }
        else if (event.crafting.isItemEqual(new ItemStack(YGCBlocks.weaponRack)))
        {
            event.player.triggerAchievement(YGCAchievementList.weaponRackCrafted);
        }
        else if (event.crafting.getItem() == YGCItems.refinedPlank)
        {
            event.player.triggerAchievement(YGCAchievementList.refinedPlank);
        }
        else if (event.crafting.getItem() == YGCItems.flagLarge)
        {
            event.player.triggerAchievement(YGCAchievementList.largeFlagCrafted);
        }
        else if (event.crafting.getItem() == YGCItems.bannerLarge)
        {
            event.player.triggerAchievement(YGCAchievementList.largeBannerCrafted);
        }
    }
}