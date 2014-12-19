/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.blocks;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.IvTileEntityRotatable;
import ivorius.ivtoolkit.network.IvNetworkHelperServer;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;

public class TileEntityLootChest extends IvTileEntityRotatable implements IInventory, PartialUpdateHandler
{
    public static final float LOCK_MAX = 0.5F;
    public static final float LOCK_MIN = 0.0005f;
    public static final float LOCK_FALL_MAX = 0.31F;
    public static final float FINISH_MARGIN = 0.005F; //The margin within what the animations can finish on, so they don't continue forever
    public static final float CHEST_MAX = 0.5F;

    public final ItemStack[] loot = new ItemStack[10];

    public boolean opened = false;
    public boolean closed = true;
    public float chestFrame = 0F;
    public float lockFrame = 0F;
    public float lockFall = LOCK_MIN;
    public float lockAccel = 1F;

    public int firstSlot(boolean empty)
    {
        for (int i = 0; i < loot.length; i++)
        {
            if (loot[i] == null == empty)
                return i;
        }

        return -1;
    }

    public ItemStack firstItem()
    {
        int slot = firstSlot(false);
        if (slot >= 0)
            return getStackInSlot(slot);
        return null;
    }

    public boolean addLoot(ItemStack item)
    {
        int slot = firstSlot(true);
        if (slot >= 0)
        {
            setInventorySlotContents(slot, item.copy());
            return true;
        }

        return false;
    }

    public boolean pickUpItem(EntityPlayer player)
    {
        int slot = firstSlot(false);

        if (slot >= 0)
        {
            ItemStack stack = getStackInSlot(slot);
            if (stack != null)
            {
                if (!worldObj.isRemote && player.inventory.addItemStackToInventory(stack))
                {
                    setInventorySlotContents(slot, null);
                    player.openContainer.detectAndSendChanges();
                }

                return true;
            }
        }

        return false;
    }

    public void open()
    {
        this.closed = false;
        this.opened = true;

        IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "openState", YeGamolChattels.network);
    }

    public void close()
    {
        this.opened = false;
        this.closed = true;

        IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "openState", YeGamolChattels.network);
    }

    public boolean itemAccessible()
    {
        return this.chestFrame > LOCK_MAX * 0.5f && !this.closed;
    }

    @Override
    public void updateEntity()
    {
        if (this.closed)
        {
            //Open chest
            if (this.chestFrame > 0F + FINISH_MARGIN)
            {
                if (chestFrame >= CHEST_MAX - FINISH_MARGIN)
                    this.worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5D, zCoord + 0.5, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.05F);

                if (this.lockFall > LOCK_MIN)
                    this.lockFall -= this.lockFall * 0.2F;
                this.chestFrame -= this.chestFrame * 0.2F;
            }
            else if (this.chestFrame <= 0F + FINISH_MARGIN)
            {
                this.chestFrame = 0F;
                this.lockFall = LOCK_MIN;

                //Rotate lock
                if (this.lockFrame > 0F + FINISH_MARGIN)
                    this.lockFrame -= (0F + this.lockFrame) * 0.1F;
                else
                    this.lockFrame = 0F;
            }
        }
        else if (this.opened)
        {
            //Rotate lock
            if (this.lockFrame < LOCK_MAX - FINISH_MARGIN)
                this.lockFrame += (LOCK_MAX - this.lockFrame) * 0.1;
            //Open chest
            else if (this.chestFrame < CHEST_MAX - FINISH_MARGIN)
            {
                if (chestFrame == 0.0f)
                    this.worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5D, zCoord + 0.5, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.05F);

                this.lockFrame = LOCK_MAX;

                if (this.lockAccel < 2F)
                    this.lockAccel += 0.05F;
                if (this.lockFall < LOCK_FALL_MAX)
                    this.lockFall *= this.lockAccel;
                else
                {
                    this.lockAccel = this.chestFrame / CHEST_MAX;
                    if (this.lockAccel > 1F - 0.05F)
                        this.lockAccel = 1F;
                    this.lockFall *= this.lockAccel;
                }
                this.chestFrame += (0.5F - this.chestFrame) * 0.1F;
                if (this.chestFrame < 0.49F)
                {
                    for (int i = 0; i < 10; i++)
                    {
                        float velocity = 0.001F;
                        float velX = (this.worldObj.rand.nextInt(100) * velocity) - (this.worldObj.rand.nextInt(100) * velocity);
                        float velY = (this.worldObj.rand.nextInt(100) * velocity) - (this.worldObj.rand.nextInt(100) * velocity);
                        float velZ = (this.worldObj.rand.nextInt(100) * velocity) - (this.worldObj.rand.nextInt(100) * velocity);
                        this.worldObj.spawnParticle("smoke", this.xCoord + 0.5F, this.yCoord + 0.5F, this.zCoord + 0.5F, velX, velY, velZ);
                    }
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList tagList = new NBTTagList();
        for (ItemStack loot : this.loot)
        {
            if (loot != null)
            {
                NBTTagCompound tagCompound = new NBTTagCompound();
                loot.writeToNBT(tagCompound);
                tagList.appendTag(tagCompound);
            }
        }
        nbt.setTag("items", tagList);

        nbt.setFloat("lockFrame", this.lockFrame);
        nbt.setFloat("chestFrame", this.chestFrame);
        nbt.setFloat("lockFall", this.lockFall);
        nbt.setFloat("lockAccel", this.lockAccel);
        nbt.setBoolean("opened", this.opened);
        nbt.setBoolean("closed", this.closed);

        super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        Arrays.fill(loot, null);
        NBTTagList tagList = nbt.getTagList("items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            ItemStack item = ItemStack.loadItemStackFromNBT(tag);
            if (item != null)
                addLoot(item);
        }

        this.lockFrame = nbt.getFloat("lockFrame");
        this.chestFrame = nbt.getFloat("chestFrame");
        this.lockFall = nbt.getFloat("lockFall");
        this.lockAccel = nbt.getFloat("lockAccel");
        this.opened = nbt.getBoolean("opened");
        this.closed = nbt.getBoolean("closed");

        super.readFromNBT(nbt);
    }

    @Override
    public void writeUpdateData(ByteBuf buffer, String context, Object... params)
    {
        if ("openState".equals(context))
        {
            buffer.writeBoolean(opened);
            buffer.writeBoolean(closed);
        }
    }

    @Override
    public void readUpdateData(ByteBuf buffer, String context)
    {
        if ("openState".equals(context))
        {
            opened = buffer.readBoolean();
            closed = buffer.readBoolean();
        }
    }

    public void dropAllItems()
    {
        for (int i = 0; i < getSizeInventory(); i++)
        {
            ItemStack stack = getStackInSlotOnClosing(i);
            if (stack != null)
                worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, stack));
        }
    }

    @Override
    public int getSizeInventory()
    {
        return loot.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return loot[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (this.loot[slot] != null)
        {
            ItemStack itemstack;

            if (this.loot[slot].stackSize <= amount)
            {
                itemstack = this.loot[slot];
                this.loot[slot] = null;
                this.markDirty();
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                return itemstack;
            }
            else
            {
                itemstack = this.loot[slot].splitStack(amount);

                if (this.loot[slot].stackSize == 0)
                {
                    this.loot[slot] = null;
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
        if (this.loot[slot] != null)
        {
            ItemStack itemstack = this.loot[slot];
            this.loot[slot] = null;
            return itemstack;
        }
        else
            return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        this.loot[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
            stack.stackSize = this.getInventoryStackLimit();

        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public String getInventoryName()
    {
        return "container.lootChest";
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