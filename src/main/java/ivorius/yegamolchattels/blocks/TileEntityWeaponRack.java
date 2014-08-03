/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import cpw.mods.fml.common.network.ByteBufUtils;
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
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class TileEntityWeaponRack extends IvTileEntityRotatable implements PartialUpdateHandler
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
            Item item = stack.getItem();

            boolean canPlace = item instanceof ItemTool || item instanceof ItemSword || (getWeaponRackType() == weaponRackTypeFloor && item instanceof ItemBow) || item == YGCItems.mallet;
            if (canPlace)
            {
                int slot = getHoveredSlot(entity);

                return slot >= 0 && tryAddingWeapon(stack, slot);

                //    			return tryAddingWeapon(stack);
            }
        }

        return false;
    }

    public boolean tryApplyingEffect(ItemStack stack, Entity entity)
    {
        if (stack != null)
        {
            int slot = getEffectToBeApplied(stack, entity);

            if (slot >= 0 && !effectsApplied[slot])
            {
                if (!worldObj.isRemote)
                {
                    effectsApplied[slot] = true;
                    stack.stackSize--;

                    if (entity instanceof EntityPlayer)
                        ((EntityPlayer) entity).triggerAchievement(YGCAchievementList.weaponRackVariant);
                }

                return true;
            }
        }

        return false;
    }

    public int getEffectToBeApplied(ItemStack stack, Entity entity)
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

    public boolean tryAddingWeapon(ItemStack stack, int slot)
    {
        if (storedWeapons[slot] == null && slot < getStoredWeaponSlots())
        {
            if (!worldObj.isRemote)
            {
                storedWeapons[slot] = stack.copy();
                storedWeapons[slot].stackSize = 1;
                stack.stackSize--;

                storedWeaponsSwinging[slot] = 0.0f;

                IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "weaponRackData", YeGamolChattels.network);
                markDirty();
            }

            return true;
        }

        return false;
    }

    public boolean tryAddingWeapon(ItemStack stack)
    {
        for (int i = 0; i < getStoredWeaponSlots(); i++)
        {
            if (tryAddingWeapon(stack, i))
                return true;
        }

        return false;
    }

    public int getHoveredSlot(Entity entity)
    {
        List<IvRaytraceableObject> raytraceables = getRaytraceableObjects();
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

    public List<IvRaytraceableObject> getRaytraceableObjects()
    {
        ArrayList<IvRaytraceableObject> raytraceables = new ArrayList<IvRaytraceableObject>();

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
                float var7 = 0.7F;
                double var8 = worldObj.rand.nextFloat() * var7 + (1.0F - var7) * 0.5D;
                double var10 = worldObj.rand.nextFloat() * 0.2D + 1.1D;
                double var12 = worldObj.rand.nextFloat() * var7 + (1.0F - var7) * 0.5D;
                EntityItem var14 = new EntityItem(worldObj, xCoord + var8, yCoord + var10, zCoord + var12, storedWeapons[slot]);
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

    public boolean tryEquippingItemOnPlayer(int slot, EntityPlayer entityLiving)
    {
        if (storedWeapons[slot] != null)
        {
            if (!worldObj.isRemote)
            {
                if (IvEntityHelper.addAsCurrentItem(entityLiving, storedWeapons[slot]))
                {
                    storedWeapons[slot] = null;

                    IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "weaponRackData", YeGamolChattels.network);
                    markDirty();
                }
            }

            return true;
        }

        return false;
    }

    public boolean interactWithPlayer(EntityPlayer entity)
    {
        int slot = getHoveredSlot(entity);

        return slot >= 0 && this.tryEquippingItemOnPlayer(slot, entity);
    }

    public void dropAllWeapons()
    {
        for (int i = 0; i < storedWeapons.length; i++)
        {
            tryDroppingWeapon(i);
        }
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
        return AxisAlignedBB.getBoundingBox(xCoord - 0.5, yCoord - 0.5, zCoord - 0.5, xCoord + 1.5, yCoord + 2.0, zCoord + 1.5);
    }

    @Override
    public void writeUpdateData(ByteBuf buffer, String context)
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
}
