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
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;

public class TileEntityLootChest extends IvTileEntityRotatable implements PartialUpdateHandler
{
    public static final float LOCK_MAX = 0.5F;
    public static final float LOCK_MIN = 0.0005f;
    public static final float LOCK_FALL_MAX = 0.31F;
    public static final float FINISH_MARGIN = 0.005F; //The margin within what the animations can finish on, so they don't continue forever
    public static final float CHEST_MAX = 0.5F;

    public ArrayList<ItemStack> loot = new ArrayList<>();

    public boolean opened = false;
    public boolean closed = true;
    public float chestFrame = 0F;
    public float lockFrame = 0F;
    public float lockFall = LOCK_MIN;
    public float lockAccel = 1F;

    public void addLoot(ItemStack item)
    {
        this.loot.add(item);

        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public ItemStack pickUpLoot()
    {
        ItemStack stack = loot.remove(0);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return stack;
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
            //Rotate lock
            if (this.lockFrame > 0F + FINISH_MARGIN)
            {
                this.lockFrame -= (0F + this.lockFrame) * 0.1F;
            }
            //Open chest
            else if (this.chestFrame > 0F + FINISH_MARGIN)
            {
                this.lockFrame = 0F;
                if (this.lockFall > LOCK_MIN)
                    this.lockFall -= this.lockFall * 0.2F;
                this.chestFrame -= this.chestFrame * 0.1F;
            }
            else if (this.chestFrame <= 0F + FINISH_MARGIN)
            {
                this.chestFrame = 0F;
                this.lockFall = LOCK_MIN;
            }
        }
        else if (this.opened)
        {
            //Rotate lock
            if (this.lockFrame < LOCK_MAX - FINISH_MARGIN)
            {
                this.lockFrame += (LOCK_MAX - this.lockFrame) * 0.1;
            }
            //Open chest
            else if (this.chestFrame < CHEST_MAX - FINISH_MARGIN)
            {
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
            NBTTagCompound tagCompound = new NBTTagCompound();
            loot.writeToNBT(tagCompound);
            tagList.appendTag(tagCompound);
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
        this.loot.clear();
        NBTTagList tagList = nbt.getTagList("items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            ItemStack item = ItemStack.loadItemStackFromNBT(tag);
            if (item != null)
                this.loot.add(item);
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
    public void writeUpdateData(ByteBuf buffer, String context)
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
        while (!loot.isEmpty())
        {
            worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, loot.remove(0)));
        }
    }
}