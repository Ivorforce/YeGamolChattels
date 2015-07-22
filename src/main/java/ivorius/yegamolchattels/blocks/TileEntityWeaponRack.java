/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.IvTileEntityRotatable;
import ivorius.ivtoolkit.entities.IvEntityHelper;
import ivorius.ivtoolkit.network.IvNetworkHelperServer;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.ivtoolkit.raytracing.IvRaytraceableObject;
import ivorius.ivtoolkit.raytracing.IvRaytracedIntersection;
import ivorius.ivtoolkit.raytracing.IvRaytracerMC;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.achievements.YGCAchievementList;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class TileEntityWeaponRack extends IvTileEntityRotatable implements IInventory, PartialUpdateHandler
{
    public static final int weaponRackTypeFloor = 0;
    public static final int weaponRackTypeWall = 1;

    public ItemStack[] storedWeapons = new ItemStack[4];
    public float[] storedWeaponsSwinging = new float[4];

    public boolean[] effectsApplied = new boolean[6];

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        for (int i = 0; i < storedWeaponsSwinging.length; i++)
        {
            if (storedWeaponsSwinging[i] < 1.0f)
                storedWeaponsSwinging[i] += 0.1f;
        }
    }

    public int getStoredWeaponSlots()
    {
        int type = getBlockMetadata() & 1;

        return type == 0 ? 4 : 2;
    }

    public int getWeaponRackType()
    {
        return getBlockMetadata() & 1;
    }

    public boolean tryStoringItem(ItemStack stack, Entity entity)
    {
        if (stack != null)
        {
            int slot = getHoveredSlot(entity);

            if (slot >= 0 && isItemValidForSlot(slot, stack) && getStackInSlot(slot) == null)
            {
                if (!worldObj.isRemote)
                {
                    setInventorySlotContents(slot, stack.copy());
                    storedWeaponsSwinging[slot] = 0.0f;

                    stack.stackSize--;
                }

                return true;
            }
        }

        return false;
    }

    public boolean tryApplyingEffect(ItemStack stack, Entity entity)
    {
        if (stack != null)
        {
            int slot = getEffectToBeApplied(stack);

            if (slot >= 0 && !effectsApplied[slot])
            {
                if (!worldObj.isRemote)
                {
                    effectsApplied[slot] = true;
                    stack.stackSize--;

                    if (entity instanceof EntityPlayer)
                        ((EntityPlayer) entity).triggerAchievement(YGCAchievementList.weaponRackVariant);

                    IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "weaponRackData", YeGamolChattels.network);
                    markDirty();
                }

                return true;
            }
        }

        return false;
    }

    public int getEffectToBeApplied(ItemStack stack)
    {
        Item item = stack.getItem();

        if (item instanceof ItemBlock)
        {
            ItemBlock itemBlock = (ItemBlock) item;

            if (itemBlock.field_150939_a == Blocks.brown_mushroom || itemBlock.field_150939_a == Blocks.red_mushroom)
                return 0;

            if (itemBlock.field_150939_a == Blocks.web)
                return 3;
        }

        if (item == Items.dye && stack.getItemDamage() == 15)
            return 1;

        if (item == Items.leather)
            return 2;

        if (item == Items.flint)
            return 4;

        if (item == Items.brick)
            return 5;

        return -1;
    }

    public int getHoveredSlot(Entity entity)
    {
        List<IvRaytraceableObject> raytraceables = getRaytraceableObjects();
        IvRaytracedIntersection intersection = IvRaytracerMC.getFirstIntersection(raytraceables, entity);

        if (intersection != null)
        {
            String info = (String) intersection.getUserInfo();
            if (info.startsWith("Slot"))
                return Integer.valueOf(info.substring(4));
        }

        return -1;
    }

    public List<IvRaytraceableObject> getRaytraceableObjects()
    {
        List<IvRaytraceableObject> raytraceables = new ArrayList<>();

        if (getWeaponRackType() == weaponRackTypeFloor)
        {
            raytraceables.add(getRotatedBox("Slot0", -0.45, -0.3, -0.3, 0.225, 0.7, 0.6));
            raytraceables.add(getRotatedBox("Slot1", -0.225, -0.3, -0.3, 0.225, 0.7, 0.6));
            raytraceables.add(getRotatedBox("Slot2", 0.0, -0.3, -0.3, 0.225, 0.7, 0.6));
            raytraceables.add(getRotatedBox("Slot3", 0.225, -0.3, -0.3, 0.225, 0.7, 0.6));
        }
        else
        {
            raytraceables.add(getRotatedBox("Slot0", -0.5, 0.0, 0.1, 1.0, 0.5, 0.4));
            raytraceables.add(getRotatedBox("Slot1", -0.5, -0.5, 0.1, 1.0, 0.5, 0.4));
        }

        return raytraceables;
    }

    public boolean tryDroppingWeapon(int slot)
    {
        if (storedWeapons[slot] != null)
        {
            if (!worldObj.isRemote)
            {
                float shiftDist = 0.7F;
                double xShift = worldObj.rand.nextFloat() * shiftDist + (1.0F - shiftDist) * 0.5D;
                double yShift = worldObj.rand.nextFloat() * 0.2D + 1.1D;
                double zShift = worldObj.rand.nextFloat() * shiftDist + (1.0F - shiftDist) * 0.5D;
                EntityItem var14 = new EntityItem(worldObj, xCoord + xShift, yCoord + yShift, zCoord + zShift, storedWeapons[slot]);
                var14.delayBeforeCanPickup = 10;
                worldObj.spawnEntityInWorld(var14);

                storedWeapons[slot] = null;

                IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "weaponRackData", YeGamolChattels.network);
                markDirty();
            }

            return true;
        }

        return false;
    }

    public boolean pickUpItem(EntityPlayer player)
    {
        int slot = getHoveredSlot(player);

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

    public void dropAllWeapons()
    {
        for (int i = 0; i < storedWeapons.length; i++)
            tryDroppingWeapon(i);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.readFromNBT(par1nbtTagCompound);

        readWeaponRackDataFromNBT(par1nbtTagCompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.writeToNBT(par1nbtTagCompound);

        writeWeaponRackDataToNBT(par1nbtTagCompound);
    }

    public void readWeaponRackDataFromNBT(NBTTagCompound compound)
    {
        NBTTagList items = compound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        this.storedWeapons = new ItemStack[storedWeapons.length];

        for (int i = 0; i < items.tagCount(); ++i)
        {
            NBTTagCompound var4 = items.getCompoundTagAt(i);
            int var5 = var4.getByte("Slot") & 255;

            if (var5 >= 0 && var5 < this.storedWeapons.length)
            {
                this.storedWeapons[var5] = ItemStack.loadItemStackFromNBT(var4);
                this.storedWeaponsSwinging[var5] = var4.getFloat("Swinging");
            }
        }

        NBTTagList effects = compound.getTagList("Effects", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < effects.tagCount(); ++i)
        {
            NBTTagCompound var4 = effects.getCompoundTagAt(i);

            int var5 = var4.getByte("Slot") & 255;
            this.effectsApplied[var5] = var4.getBoolean("Applied");
        }
    }

    public void writeWeaponRackDataToNBT(NBTTagCompound compound)
    {
        NBTTagList items = new NBTTagList();

        for (int i = 0; i < storedWeapons.length; i++)
        {
            if (this.storedWeapons[i] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) i);
                var4.setFloat("Swinging", (byte) storedWeaponsSwinging[i]);
                this.storedWeapons[i].writeToNBT(var4);
                items.appendTag(var4);
            }
        }

        compound.setTag("Items", items);

        NBTTagList effects = new NBTTagList();

        for (int i = 0; i < effectsApplied.length; i++)
        {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setByte("Slot", (byte) i);
            var4.setBoolean("Applied", effectsApplied[i]);
            effects.appendTag(var4);
        }

        compound.setTag("Effects", effects);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.fromBounds(xCoord - 0.5, yCoord - 0.5, zCoord - 0.5, xCoord + 1.5, yCoord + 2.0, zCoord + 1.5);
    }

    @Override
    public void writeUpdateData(ByteBuf buffer, String context, Object... params)
    {
        if ("weaponRackData".equals(context))
        {
            NBTTagCompound compound = new NBTTagCompound();
            writeWeaponRackDataToNBT(compound);
            ByteBufUtils.writeTag(buffer, compound);
        }
    }

    @Override
    public void readUpdateData(ByteBuf buffer, String context)
    {
        if ("weaponRackData".equals(context))
        {
            readWeaponRackDataFromNBT(ByteBufUtils.readTag(buffer));
        }
    }

    @Override
    public int getSizeInventory()
    {
        return storedWeapons.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return storedWeapons[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (this.storedWeapons[slot] != null)
        {
            ItemStack itemstack;

            if (this.storedWeapons[slot].stackSize <= amount)
            {
                itemstack = this.storedWeapons[slot];
                this.storedWeapons[slot] = null;
                this.markDirty();
                worldObj.markBlockForUpdate(getPos());
                return itemstack;
            }
            else
            {
                itemstack = this.storedWeapons[slot].splitStack(amount);

                if (this.storedWeapons[slot].stackSize == 0)
                {
                    this.storedWeapons[slot] = null;
                }

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
        if (this.storedWeapons[slot] != null)
        {
            ItemStack itemstack = this.storedWeapons[slot];
            this.storedWeapons[slot] = null;
            return itemstack;
        }
        else
            return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        this.storedWeapons[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
            stack.stackSize = this.getInventoryStackLimit();

        this.markDirty();
        worldObj.markBlockForUpdate(getPos());
    }

    @Override
    public String getInventoryName()
    {
        return "container.weaponRack";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
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
        Item item = stack.getItem();
        return item instanceof ItemTool || item instanceof ItemSword || (getWeaponRackType() == weaponRackTypeFloor && item instanceof ItemBow) || item == YGCItems.mallet;
    }
}
