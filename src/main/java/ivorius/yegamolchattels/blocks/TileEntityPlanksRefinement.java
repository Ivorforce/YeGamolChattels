package ivorius.yegamolchattels.blocks;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.ivtoolkit.entities.IvEntityHelper;
import ivorius.ivtoolkit.network.IvNetworkHelperServer;
import ivorius.ivtoolkit.network.PacketTileEntityClientEvent;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.ivtoolkit.tools.IvSideClient;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.gui.YGCGuiHandler;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;

/**
 * Created by lukas on 04.05.14.
 */
public class TileEntityPlanksRefinement extends IvTileEntityMultiBlock implements PartialUpdateHandler
{
    private static ArrayList<IPlanksRefinementEntry> planksRefinementEntries = new ArrayList<IPlanksRefinementEntry>();

    public static final int REFINEMENT_SLOTS_X = 16;
    public static final int REFINEMENT_SLOTS_Y = 12;

    public static int MIN_LOW_REFINEMENT_PER_SLOT = 5;
    public static int MIN_HIGH_REFINEMENT_PER_SLOT = 20;
    public static int MISSING_REFINEMENT_ALLOWED = 40;

    public ItemStack containedItem;

    public int[] ticksRefinedPerSlot = new int[REFINEMENT_SLOTS_X * REFINEMENT_SLOTS_Y];

    public static void addRefinement(IPlanksRefinementEntry entry)
    {
        planksRefinementEntries.add(entry);
    }

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

    public boolean tryEquippingItemOnPlayer(EntityPlayer entityLiving)
    {
        if (containedItem != null)
        {
            if (!worldObj.isRemote)
            {
                if (IvEntityHelper.addAsCurrentItem(entityLiving, containedItem))
                {
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
        for (IPlanksRefinementEntry entry : planksRefinementEntries)
        {
            if (entry.matchesSource(containedItem) && entry.matchesTool(tool))
            {
                return true;
            }
        }

        return false;
    }

    public void refineWithItem(ItemStack usedTool, EntityLivingBase entityLivingBase, float x, float y, float speed)
    {
        int speedInfl = MathHelper.floor_float(speed * 1.0f) + ((worldObj.rand.nextFloat() < (speed % 1.0f) * 1.0f) ? 1 : 0);
        usedTool.damageItem(1 + speedInfl, entityLivingBase);

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
                    ticksRefinedPerSlot[slotY * REFINEMENT_SLOTS_X + slotX] += speedInfl;
                }
            }
        }

        if (worldObj.isRemote)
            YeGamolChattels.network.sendToServer(PacketTileEntityClientEvent.packetEntityData(this, "plankRefinement"));

        if (isRefinementComplete())
        {
            completeRefinement();
        }
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

    public void completeRefinement()
    {
        for (int i = 0; i < ticksRefinedPerSlot.length; i++)
        {
            ticksRefinedPerSlot[i] = 0;
        }

        if (!worldObj.isRemote)
        {
            for (IPlanksRefinementEntry entry : planksRefinementEntries)
            {
                if (entry.matchesSource(containedItem))
                {
                    containedItem = entry.getResult();
                    break;
                }
            }

            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public float getRefinement(int slotX, int slotY)
    {
        if (slotX >= 0 && slotX < REFINEMENT_SLOTS_X && slotY >= 0 && slotY < REFINEMENT_SLOTS_Y)
        {
            return ticksRefinedPerSlot[slotY * 16 + slotX] / 20.0f * 0.5f;
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
    public void writeUpdateData(ByteBuf buffer, String context)
    {
        if ("plankRefinement".equals(context))
        {
            for (int tickRefinedPerSlot : ticksRefinedPerSlot)
            {
                buffer.writeInt(tickRefinedPerSlot);
            }
        }
        else if ("refinementGui".equals(context))
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
        if ("plankRefinement".equals(context))
        {
            for (int i = 0; i < ticksRefinedPerSlot.length; i++)
            {
                ticksRefinedPerSlot[i] = buffer.readInt();
            }

            if (isRefinementComplete())
            {
                completeRefinement();
            }
        }
        else if ("refinementGui".equals(context))
        {
            for (int i = 0; i < ticksRefinedPerSlot.length; i++)
            {
                ticksRefinedPerSlot[i] = buffer.readInt();
            }

            IvSideClient.getClientPlayer().openGui(YeGamolChattels.instance, YGCGuiHandler.plankRefinementGuiID, worldObj, xCoord, yCoord, zCoord);
        }
    }
}
