package ivorius.yegamolchattels.blocks;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.ivtoolkit.entities.IvEntityHelper;
import ivorius.ivtoolkit.network.*;
import ivorius.ivtoolkit.tools.IvSideClient;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.achievements.YGCAchievementList;
import ivorius.yegamolchattels.gui.YGCGuiHandler;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

/**
 * Created by lukas on 04.05.14.
 */
public class TileEntityTablePress extends IvTileEntityMultiBlock implements PartialUpdateHandler, ClientEventHandler
{
    public static final int REFINEMENT_SLOTS_X = 16;
    public static final int REFINEMENT_SLOTS_Y = 7;

    public static int MIN_LOW_REFINEMENT_PER_SLOT = 3;
    public static int MIN_HIGH_REFINEMENT_PER_SLOT = 6;
    public static int MAX_REFINEMENT_PER_SLOT = 10;
    public static int MISSING_REFINEMENT_ALLOWED = 30;

    public ItemStack containedItem;

    public int[] ticksRefinedPerSlot = new int[REFINEMENT_SLOTS_X * REFINEMENT_SLOTS_Y];

    public boolean tryStoringItem(ItemStack stack, Entity entity)
    {
        if (stack != null && containedItem == null)
        {
            Item item = stack.getItem();

            if (item == YGCItems.plank || item == YGCItems.smoothPlank || item == YGCItems.refinedPlank)
            {
                if (!worldObj.isRemote)
                {
                    containedItem = stack.copy();
                    containedItem.stackSize = 1;

                    stack.stackSize--;

                    markDirty();
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }

                return true;
            }
        }

        return false;
    }

    public boolean tryEquippingItemOnPlayer(EntityPlayer player)
    {
        if (containedItem != null)
        {
            if (!worldObj.isRemote)
            {
                if (IvEntityHelper.addAsCurrentItem(player, containedItem))
                {
                    if (containedItem.getItem() == YGCItems.refinedPlank)
                    {
                        player.triggerAchievement(YGCAchievementList.refinedPlank);
                    }

                    containedItem = null;

                    markDirty();
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
            }

            return true;
        }

        return false;
    }

    public boolean tryUsingItem(ItemStack usedTool, EntityPlayer player)
    {
        if (containedItem == null)
        {
            return false;
        }

        if (isCorrectTool(usedTool))
        {
            if (!worldObj.isRemote)
            {
                IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "refinementGui", YeGamolChattels.network, player);
            }

            return true;
        }

        return false;
    }

    public boolean isCorrectTool(ItemStack tool)
    {
        return getCurrentResult(tool) != null;
    }

    public void refineWithItem(EntityPlayer entityPlayer, int usedItem, float x, float y, float speed)
    {
        speed = speed * 0.3f;
        int speedInfl = MathHelper.floor_float(speed) + ((worldObj.rand.nextFloat() < speed % 1.0f) ? 1 : 0);

        int startSlotX = MathHelper.floor_float(x - 1.5f + 0.5f);
        int endSlotX = MathHelper.floor_float(x + 1.5f + 0.5f);

        int startSlotY = MathHelper.floor_float(y - 1.5f + 0.5f);
        int endSlotY = MathHelper.floor_float(y + 1.5f + 0.5f);

        for (int slotX = startSlotX; slotX < endSlotX; slotX++)
        {
            for (int slotY = startSlotY; slotY < endSlotY; slotY++)
            {
                if (slotX >= 0 && slotX < REFINEMENT_SLOTS_X && slotY >= 0 && slotY < REFINEMENT_SLOTS_Y)
                {
                    int index = slotY * REFINEMENT_SLOTS_X + slotX;
                    ticksRefinedPerSlot[index] += speedInfl;
                    if (ticksRefinedPerSlot[index] > MAX_REFINEMENT_PER_SLOT)
                        ticksRefinedPerSlot[index] = MAX_REFINEMENT_PER_SLOT;
                }
            }
        }

        if (worldObj.isRemote)
            IvNetworkHelperClient.sendTileEntityUpdatePacket(this, "plankRefinement", YeGamolChattels.network, entityPlayer.inventory.currentItem, speedInfl);

        if (isRefinementComplete())
            completeRefinement(entityPlayer.inventory.getStackInSlot(usedItem));
    }

    public boolean isRefinementComplete()
    {
        int ticksMissing = 0;

        for (int ticksRefined : ticksRefinedPerSlot)
        {
            if (ticksRefined < MIN_LOW_REFINEMENT_PER_SLOT)
                return false;
            else if (ticksRefined < MIN_HIGH_REFINEMENT_PER_SLOT)
            {
                ticksMissing += ticksRefined - MIN_HIGH_REFINEMENT_PER_SLOT;

                if (ticksMissing > MISSING_REFINEMENT_ALLOWED)
                    return false;
            }
        }

        return true;
    }

    public void completeRefinement(ItemStack tool)
    {
        for (int i = 0; i < ticksRefinedPerSlot.length; i++)
        {
            ticksRefinedPerSlot[i] = 0;
        }

        if (!worldObj.isRemote)
        {
            PlanksRefinementRegistry.Entry entry = getCurrentResult(tool);
            if (entry != null)
                containedItem = entry.getResult(containedItem, tool);
            else
                YeGamolChattels.logger.error("Unknown refinement result for '" + containedItem + "'");

            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public PlanksRefinementRegistry.Entry getCurrentResult(ItemStack tool)
    {
        return PlanksRefinementRegistry.entry(containedItem, tool);
    }

    public float getRefinement(int slotX, int slotY)
    {
        if (slotX >= 0 && slotX < REFINEMENT_SLOTS_X && slotY >= 0 && slotY < REFINEMENT_SLOTS_Y)
        {
            return ticksRefinedPerSlot[slotY * 16 + slotX] / (float) MIN_HIGH_REFINEMENT_PER_SLOT;
        }

        return -1.0f;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.readFromNBT(par1nbtTagCompound);

        containedItem = ItemStack.loadItemStackFromNBT(par1nbtTagCompound.getCompoundTag("containedItem"));

        this.ticksRefinedPerSlot = par1nbtTagCompound.getIntArray("ticksRefinedPerSlot");
        if (ticksRefinedPerSlot == null || ticksRefinedPerSlot.length != REFINEMENT_SLOTS_X * REFINEMENT_SLOTS_Y)
        {
            this.ticksRefinedPerSlot = new int[16 * 16];
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.writeToNBT(par1nbtTagCompound);

        if (containedItem != null)
        {
            NBTTagCompound itemNBT = new NBTTagCompound();
            containedItem.writeToNBT(itemNBT);
            par1nbtTagCompound.setTag("containedItem", itemNBT);
        }

        par1nbtTagCompound.setIntArray("ticksRefinedPerSlot", ticksRefinedPerSlot);
    }

    @Override
    public void writeUpdateData(ByteBuf buffer, String context, Object... params)
    {
        if ("refinementGui".equals(context))
        {
            for (int tickRefinedPerSlot : ticksRefinedPerSlot)
            {
                buffer.writeInt(tickRefinedPerSlot);
            }
        }
    }

    @Override
    public void readUpdateData(ByteBuf buffer, String context)
    {
        if ("refinementGui".equals(context))
        {
            for (int i = 0; i < ticksRefinedPerSlot.length; i++)
            {
                ticksRefinedPerSlot[i] = buffer.readInt();
            }

            IvSideClient.getClientPlayer().openGui(YeGamolChattels.instance, YGCGuiHandler.plankRefinementGuiID, worldObj, xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void assembleClientEvent(ByteBuf buffer, String context, Object... params)
    {
        if ("plankRefinement".equals(context))
        {
            buffer.writeInt((Integer) params[0]);
            buffer.writeInt((Integer) params[1]);

            for (int tickRefinedPerSlot : ticksRefinedPerSlot)
            {
                buffer.writeInt(tickRefinedPerSlot);
            }
        }
    }

    @Override
    public void onClientEvent(ByteBuf buffer, String context, EntityPlayerMP player)
    {
        if ("plankRefinement".equals(context))
        {
            int usedItem = buffer.readInt();
            ItemStack usedStack = player.inventory.getStackInSlot(usedItem);
            int speedInfl = buffer.readInt();

            if (isCorrectTool(usedStack))
            {
                for (int i = 0; i < ticksRefinedPerSlot.length; i++)
                {
                    ticksRefinedPerSlot[i] = buffer.readInt();
                }

                usedStack.damageItem(1 + speedInfl, player);
                if (usedStack.stackSize <= 0)
                {
                    player.inventory.setInventorySlotContents(usedItem, null);
                    PlanksRefinementRegistry.Entry entry = getCurrentResult(usedStack);
                    entry.onToolBreak(usedStack, player);
                }

                if (isRefinementComplete())
                    completeRefinement(usedStack);
            }
        }
    }
}
