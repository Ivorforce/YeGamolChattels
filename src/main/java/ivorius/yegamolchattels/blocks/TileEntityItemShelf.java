/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.ivtoolkit.entities.IvEntityHelper;
import ivorius.ivtoolkit.raytracing.IvRaytraceableObject;
import ivorius.ivtoolkit.raytracing.IvRaytracedIntersection;
import ivorius.ivtoolkit.raytracing.IvRaytracerMC;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public abstract class TileEntityItemShelf extends IvTileEntityMultiBlock implements IInventory
{
    public ItemStack[] storedItems = new ItemStack[50];
    public int ticksAlive = 0;

    public int randomSeed = 0;

    public boolean[] shelfTriggers = new boolean[10];
    public float[] shelfTriggerFractions = new float[10];

    public TileEntityItemShelf()
    {

    }

    public TileEntityItemShelf(World world)
    {
        randomSeed = world.rand.nextInt(Integer.MAX_VALUE);
    }

    @Override
    public void updateEntityParent()
    {
        super.updateEntityParent();

        for (int i = 0; i < shelfTriggers.length; i++)
        {
            if (shelfTriggers[i])
                shelfTriggerFractions[i] = Math.min(1.0f, shelfTriggerFractions[i] + 0.25f);
            else
                shelfTriggerFractions[i] = Math.max(0.0f, shelfTriggerFractions[i] - 0.25f);
        }

        ticksAlive++;
    }

    public int getShelfType()
    {
        return this.getBlockMetadata();
    }

    @Override
    public void readFromNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.readFromNBT(par1nbtTagCompound);

        NBTTagList items = par1nbtTagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        this.storedItems = new ItemStack[storedItems.length];

        for (int i = 0; i < items.tagCount(); ++i)
        {
            NBTTagCompound var4 = items.getCompoundTagAt(i);
            int var5 = var4.getByte("Slot") & 255;

            if (var5 >= 0 && var5 < this.storedItems.length)
            {
                this.storedItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

        int triggers = this.shelfTriggers.length;
        this.shelfTriggers = new boolean[triggers];
        this.shelfTriggerFractions = new float[triggers];
        for (int i = 0; i < triggers; i++)
        {
            shelfTriggerFractions[i] = par1nbtTagCompound.getFloat("shelfTriggerVal" + i);
            shelfTriggers[i] = par1nbtTagCompound.getBoolean("shelfTrigger" + i);
        }

        ticksAlive = par1nbtTagCompound.getInteger("ticksAlive");
        randomSeed = par1nbtTagCompound.getInteger("randomSeed");
        if (randomSeed < 0)
            randomSeed = worldObj.rand.nextInt(Integer.MAX_VALUE);
    }

    @Override
    public void writeToNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.writeToNBT(par1nbtTagCompound);

        NBTTagList items = new NBTTagList();

        for (int i = 0; i < storedItems.length; i++)
        {
            if (this.storedItems[i] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) i);
                this.storedItems[i].writeToNBT(var4);
                items.appendTag(var4);
            }
        }

        int triggers = this.shelfTriggers.length;
        for (int i = 0; i < triggers; i++)
        {
            par1nbtTagCompound.setFloat("shelfTriggerVal" + i, shelfTriggerFractions[i]);
            par1nbtTagCompound.setBoolean("shelfTrigger" + i, shelfTriggers[i]);
        }

        par1nbtTagCompound.setTag("Items", items);

        par1nbtTagCompound.setInteger("ticksAlive", ticksAlive);
        par1nbtTagCompound.setInteger("randomSeed", randomSeed);
    }

    public boolean onRightClick(EntityPlayer player, ItemStack stack, EnumFacing facing)
    {
        if (!isParent())
        {
            TileEntityItemShelf parent = (TileEntityItemShelf) getParent();

            return parent != null && parent.onRightClick(player, stack, side);
        }
        else
        {
            List<IvRaytraceableObject> raytraceables = this.getRaytraceableObjects(0.0f);
            IvRaytracedIntersection intersection = IvRaytracerMC.getFirstIntersection(raytraceables, player);

            if (intersection != null)
            {
                if (this.handleRightClickOnIntersection(player, stack, side, intersection))
                {
                    return true;
                }
                else
                {
                    int slotNumber = getSlotNumber(intersection.getHitObject());
                    int triggerNumber = getInfoNumber(intersection.getHitObject(), "Trigger-");

                    if (slotNumber >= 0)
                    {
                        if (stack != null && tryStoringItemInSlot(slotNumber, stack))
                        {
                            player.inventory.mainInventory[player.inventory.currentItem] = null;
                            worldObj.markBlockForUpdate(getPos());
                        }
                        else if (tryEquippingItemOnPlayer(slotNumber, player))
                        {
                            worldObj.markBlockForUpdate(getPos());
                        }

                        return true;
                    }
                    else if (triggerNumber >= 0)
                    {
                        if (triggerNumber < this.shelfTriggers.length)
                        {
                            if (!worldObj.isRemote)
                            {
                                activateTrigger(triggerNumber);
                            }

                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }

    public abstract void activateTrigger(int trigger);

    public abstract boolean handleRightClickOnIntersection(EntityPlayer player, ItemStack stack, EnumFacing facing, IvRaytracedIntersection intersection);

    public void dropAllItems()
    {
        for (int i = 0; i < getItemSlots(); i++)
            this.tryDroppingItem(i);

        worldObj.markBlockForUpdate(getPos());
    }

    public abstract int getItemSlots();

    public int getHoveredSlot(Entity entity)
    {
        List<IvRaytraceableObject> raytraceables = getRaytraceableObjects(0.0f);
        IvRaytracedIntersection intersection = IvRaytracerMC.getFirstIntersection(raytraceables, entity);

        if (intersection != null)
        {
            String info = (String) intersection.getUserInfo();
            if (info.startsWith("Slot"))
            {
                Integer number = Integer.valueOf(info.substring(4));

                return number;
            }
        }

        return -1;
    }

    public abstract List<IvRaytraceableObject> getRaytraceableObjects(float t);

    public abstract List<IvRaytraceableObject> getItemSlotBoxes(float t);

    public boolean tryEquippingItemOnPlayer(int slot, EntityPlayer player)
    {
        if (storedItems[slot] != null)
        {
            if (!worldObj.isRemote && player.inventory.addItemStackToInventory(storedItems[slot]))
            {
                setInventorySlotContents(slot, null);
                player.openContainer.detectAndSendChanges();

                return true;
            }
        }

        return false;
    }

    public abstract boolean tryStoringItemInSlot(int slot, ItemStack stack);

    public int getSlotNumber(IvRaytraceableObject object)
    {
        return getInfoNumber(object, "Slot");
    }

    public int getInfoNumber(IvRaytraceableObject object, String start)
    {
        if (object.userInfo instanceof String)
        {
            String userString = (String) object.userInfo;

            if (userString.startsWith(start))
                return Integer.valueOf(userString.substring(start.length()));
        }

        return -1;
    }

    public void tryDroppingItem(int slot)
    {
        if (storedItems[slot] != null)
        {
            float dropRange = 0.7F;
            double shiftX = worldObj.rand.nextFloat() * dropRange + (1.0F - dropRange) * 0.5D;
            double shiftY = worldObj.rand.nextFloat() * 0.2D + 1.1D;
            double shiftZ = worldObj.rand.nextFloat() * dropRange + (1.0F - dropRange) * 0.5D;
            EntityItem entityItem = new EntityItem(worldObj, xCoord + shiftX, yCoord + shiftY, zCoord + shiftZ, storedItems[slot]);
            entityItem.delayBeforeCanPickup = 10;
            worldObj.spawnEntityInWorld(entityItem);

            storedItems[slot] = null;
            worldObj.markBlockForUpdate(getPos());
        }
    }

    public float getTriggerValue(int slot, float t)
    {
        if (shelfTriggers[slot])
            return Math.min(1.0f, shelfTriggerFractions[slot] + 0.25f * t);
        else
            return Math.max(0.0f, shelfTriggerFractions[slot] - 0.25f * t);
    }

    public float[] getTriggerValues(float t)
    {
        float[] values = new float[this.shelfTriggerFractions.length];
        for (int i = 0; i < values.length; i++)
            values[i] = getTriggerValue(i, t);

        return values;
    }

    public abstract AxisAlignedBB getSpecialSelectedBB();

    public abstract AxisAlignedBB getSpecialCollisionBB();

    public abstract AxisAlignedBB getSpecialBlockBB();

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
                worldObj.markBlockForUpdate(getPos());
                return itemstack;
            }
            else
            {
                itemstack = this.storedItems[slot].splitStack(amount);

                if (this.storedItems[slot].stackSize == 0)
                    this.storedItems[slot] = null;

                this.markDirty();
                worldObj.markBlockForUpdate(getPos());
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
        worldObj.markBlockForUpdate(getPos());
    }

    @Override
    public String getInventoryName()
    {
        return "container.ygcItemShelf";
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
