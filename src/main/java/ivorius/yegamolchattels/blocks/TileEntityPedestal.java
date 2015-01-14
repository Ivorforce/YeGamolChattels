/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.ivtoolkit.entities.IvEntityHelper;
import ivorius.ivtoolkit.network.IvNetworkHelperServer;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityPedestal extends IvTileEntityMultiBlock implements IInventory, PartialUpdateHandler
{
    public int pedestalIdentifier;

    public int ticksAlive;

    public final ItemStack[] storedItems = new ItemStack[1];

    public int timeItemUp;
    public boolean itemShouldBeUp = false;

    @Override
    public void updateEntityParent()
    {
        super.updateEntityParent();

        if (isParent())
        {
            if (itemShouldBeUp && timeItemUp < getIntegrationTime())
                timeItemUp++;
            else if (!itemShouldBeUp && timeItemUp > 0)
                timeItemUp--;
            else if (!itemShouldBeUp && storedItems[0] != null)
            {
                dropItem();
            }

            if (timeItemUp > 0)
            {
                float fractionUp = getFractionItemUp();

                float cloudC = 0;
                if (pedestalIdentifier == 3)
                    cloudC = fractionUp * 0.2f;
                if (pedestalIdentifier == 4)
                    cloudC = fractionUp * 0.6f;

                int cloudN = 0;
                while (worldObj.rand.nextFloat() < cloudC && cloudN < 10)
                    cloudN++;

                for (int i = 0; i < cloudN; i++)
                {
                    float xPlus = worldObj.rand.nextFloat() * 1.6f - 0.3f;
                    float yPlus = worldObj.rand.nextFloat() * worldObj.rand.nextFloat() * 0.2f;
                    float zPlus = worldObj.rand.nextFloat() * 1.6f - 0.3f;

                    worldObj.spawnParticle("cloud", xCoord + xPlus, yCoord + yPlus, zCoord + zPlus, 0.0f, worldObj.rand.nextFloat() * 0.01f + 0.01f, 0.0f);
                }
            }

            ticksAlive++;
        }
    }

    public boolean tryStoringItem(ItemStack stack)
    {
        TileEntityPedestal parent = (TileEntityPedestal) getParent();

        if (parent != null)
            return parent.tryStoringItem(stack);
        else if (isParent())
        {
            if (storedItems[0] != null || stack == null)
                return false;

            if (!worldObj.isRemote)
            {
                storedItems[0] = stack.copy();
                itemShouldBeUp = true;

                IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "pedestalData", YeGamolChattels.network);
                markDirty();
            }

            return true;
        }

        return false;
    }

    public boolean startDroppingItem(EntityPlayer player)
    {
        TileEntityPedestal parent = (TileEntityPedestal) getParent();

        if (parent != null)
            return parent.startDroppingItem(player);
        else if (isParent())
        {
            if (storedItems[0] == null)
                return false;

            if (!worldObj.isRemote)
            {
                itemShouldBeUp = false;

                if (player != null && getIntegrationTime() == 0)
                {
                    IvEntityHelper.addAsCurrentItem(player, storedItems[0]);
                    storedItems[0] = null;
                }

                IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "pedestalData", YeGamolChattels.network);
                markDirty();
            }

            return true;
        }

        return false;
    }

    public void dropItem()
    {
        TileEntityPedestal parent = (TileEntityPedestal) getParent();

        if (parent != null)
            parent.dropItem();
        else if (isParent())
        {
            if (storedItems[0] != null)
            {
                if (!worldObj.isRemote)
                {
                    EntityItem itemEntity = new EntityItem(worldObj, xCoord, yCoord, zCoord, storedItems[0]);
                    worldObj.spawnEntityInWorld(itemEntity);

                    storedItems[0] = null;

                    IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "pedestalData", YeGamolChattels.network);
                    markDirty();
                }
            }
        }
    }

    public EnumPedestalEntry getPedestalEntry()
    {
        return EnumPedestalEntry.getEntry(pedestalIdentifier);
    }

    public int getIntegrationTime()
    {
        EnumPedestalEntry entry = getPedestalEntry();
        return entry != null ? entry.integrationTime : 0;
    }

    public float getFractionItemUp()
    {
        float fractionDone;
        if ((float) getIntegrationTime() > 0)
            fractionDone = (float) timeItemUp / (float) getIntegrationTime();
        else
            fractionDone = 1.0f;

        return fractionDone;
    }

    @Override
    public void writeToNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.writeToNBT(par1nbtTagCompound);

        writePedestalDataToNBT(par1nbtTagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.readFromNBT(par1nbtTagCompound);

        readPedestalDataFromNBT(par1nbtTagCompound);
    }

    public void writePedestalDataToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setInteger("pedestalIdentifier", pedestalIdentifier);

        tagCompound.setInteger("timeItemUp", timeItemUp);
        tagCompound.setBoolean("itemShouldBeUp", itemShouldBeUp);

        if (storedItems[0] != null)
        {
            NBTTagCompound var4 = new NBTTagCompound();
            storedItems[0].writeToNBT(var4);
            tagCompound.setTag("storedItem", var4);
        }
    }

    public void readPedestalDataFromNBT(NBTTagCompound tagCompound)
    {
        pedestalIdentifier = tagCompound.getInteger("pedestalIdentifier");

        timeItemUp = tagCompound.getInteger("timeItemUp");
        itemShouldBeUp = tagCompound.getBoolean("itemShouldBeUp");

        if (tagCompound.hasKey("storedItem"))
        {
            NBTTagCompound var4 = tagCompound.getCompoundTag("storedItem");
            storedItems[0] = ItemStack.loadItemStackFromNBT(var4);
        }
        else
        {
            storedItems[0] = null;
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        float[] extent = getPedestalEntry().visualExtent;
        if (extent != null)
        {
            double[] center = getActiveCenterCoords();
            return AxisAlignedBB.getBoundingBox(center[0] - extent[0], center[1] - extent[1], center[2] - extent[2], center[0] + extent[0], center[1] + extent[1], center[2] + extent[2]);
        }

        return super.getRenderBoundingBox();
    }

    @Override
    public void writeUpdateData(ByteBuf buffer, String context, Object... params)
    {
        if ("pedestalData".equals(context))
        {
            NBTTagCompound compound = new NBTTagCompound();
            writePedestalDataToNBT(compound);
            ByteBufUtils.writeTag(buffer, compound);
        }
    }

    @Override
    public void readUpdateData(ByteBuf buffer, String context)
    {
        if ("pedestalData".equals(context))
        {
            readPedestalDataFromNBT(ByteBufUtils.readTag(buffer));
        }
    }

    @Override
    public int getSizeInventory()
    {
        return storedItems.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return storedItems[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (this.storedItems[slot] != null)
        {
            ItemStack itemstack;

            if (this.storedItems[slot].stackSize <= amount)
            {
                itemstack = this.storedItems[slot];
                this.storedItems[slot] = null;
                this.markDirty();
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                return itemstack;
            }
            else
            {
                itemstack = this.storedItems[slot].splitStack(amount);

                if (this.storedItems[slot].stackSize == 0)
                {
                    this.storedItems[slot] = null;
                }

                this.markDirty();
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (this.storedItems[slot] != null)
        {
            ItemStack itemstack = this.storedItems[slot];
            this.storedItems[slot] = null;
            return itemstack;
        }
        else
            return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        this.storedItems[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
            stack.stackSize = this.getInventoryStackLimit();

        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public String getInventoryName()
    {
        return "container.ygcPedestal";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return true;
    }
}
