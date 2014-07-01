/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.entities;

import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class EntityFlag extends Entity
{
    public float wind = 0.0f;
    public float simWind = 0.0f;

    public EntityFlag(World world)
    {
        super(world);
        yOffset = 0.0F;
        setSize(0.5F, 0.5F);
        ignoreFrustumCheck = true;
    }

    @Override
    protected void entityInit()
    {
        this.dataWatcher.addObject(25, new Integer(0));
        this.dataWatcher.addObject(26, new Integer(0));
    }

    public void updateBounds()
    {
        float f1 = getFlagHeight();

        boundingBox.setBounds(posX + 0.4, posY, posZ + 0.4, posX + 0.6, posY + f1 / 16F, posZ + 0.6);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if ((ticksExisted + 90) % 100 == 0)
        {
            if (!worldObj.isRemote)
            {
                if (!fallIfNecessary())
                {
                    setDead();
                    dropFlag();
                }

//				int cache = getColor(); //For entities nonliving, Data watcher does not get transferred at start.
//                setColor(-1); 			//Thus, It needs transfer check every few secs
//				setColor(cache);
//
//				cache = getSize();
//				setSize(-1);
//				setSize(cache);
//
//                for (Object player : worldObj.playerEntities)
//                {
//                    if (worldObj.playerEntities.size() > 0)
//                    {
//                        EntityPlayerMP p = (EntityPlayerMP) player;
//
////						ModLoader.serverSendPacket(p.playerNetServerHandler, new Packet40EntityMetadata(entityId, dataWatcher, true));
//                    }
//                }
            }
            else
            {
                updateBounds();
            }
        }

        wind = updateWind(wind, worldObj);
        simWind = updateSimWind(wind, simWind);
    }

    public static float updateSimWind(float wind, float simWind)
    {
        return simWind + (wind - simWind) * 0.02f;
    }

    public static float getInterpolatedWind(float wind, float simWind, float t)
    {
        return simWind + (wind - simWind) * 0.02f * t;
    }

    public static float updateWind(float wind, World worldObj)
    {
        float globalWind = getGlobalWind(worldObj.getWorldTime());

        wind += (worldObj.rand.nextFloat() * globalWind - worldObj.rand.nextFloat() * (1.0f - globalWind)) * 0.2f;
        if (wind < 0.0f)
            wind = 0.0f;
        if (wind > 1.0f)
            wind = 1.0f;

        return wind;
    }

    public static float getGlobalWind(long time)
    {
        return (MathHelper.sin(time * 0.001f) + 1.0f) * 0.5f;
    }

    public boolean fallIfNecessary()
    {
        Block block = worldObj.getBlock((int) posX, (int) posY - 1, (int) posZ);

        AxisAlignedBB bb = boundingBox.copy();
        if (!(block != Blocks.fence && block != Blocks.nether_brick_fence && block != Blocks.cobblestone_wall))
            bb.minY += 0.5f;

        if (worldObj.getCollidingBoundingBoxes(this, bb).size() > 0)
        {
            return false;
        }

        Material material = worldObj.getBlock((int) posX, (int) posY - 1, (int) posZ).getMaterial();

        if (!material.isSolid() && block != Blocks.fence && block != Blocks.nether_brick_fence && block != Blocks.cobblestone_wall)
        {
            return false;
        }

        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox);
        for (int l1 = 0; l1 < list.size(); l1++)
        {
            if (list.get(l1) instanceof EntityFlag)
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void func_145781_i(int p_145781_1_) // EntityWatcher got updated
    {
        this.worldObj.func_147450_X();
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (!isDead && !worldObj.isRemote)
        {
            setDead();
            setBeenAttacked();
            dropFlag();
        }

        return true;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setInteger("TileX", (int) posX);
        nbttagcompound.setInteger("TileY", (int) posY);
        nbttagcompound.setInteger("TileZ", (int) posZ);
        nbttagcompound.setInteger("FlagSize", getSize());
        nbttagcompound.setInteger("FlagColor", getColor());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        posX = nbttagcompound.getInteger("TileX");
        posY = nbttagcompound.getInteger("TileY");
        posZ = nbttagcompound.getInteger("TileZ");
        setSize(nbttagcompound.getInteger("FlagSize"));
        setColor(nbttagcompound.getInteger("FlagColor"));

        updateBounds();
    }

    @Override
    public void moveEntity(double d, double d1, double d2)
    {
        if (!worldObj.isRemote && d * d + d1 * d1 + d2 * d2 > 0.0D)
        {
            setDead();
            dropFlag();
        }
    }

    @Override
    public void addVelocity(double d, double d1, double d2)
    {
        if (!worldObj.isRemote && d * d + d1 * d1 + d2 * d2 > 0.0D)
        {
            setDead();
            dropFlag();
        }
    }

    public int getFlagHeight()
    {
        if (getSize() == 0)
            return 16 * 2;
        if (getSize() == 2)
            return 16 * 8;

        return 16 * 6;
    }

    public void dropFlag()
    {
        if (getSize() == 0)
            worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(YGCItems.flagSmall, 1, getColor())));
        else if (getSize() == 2)
            worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(YGCItems.flagLarge, 1, getColor())));

        else
            worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(YGCItems.flagSmall, 1, getColor())));
    }

    public int getSize()
    {
        return dataWatcher.getWatchableObjectInt(25);
    }

    public void setSize(int size)
    {
        dataWatcher.updateObject(25, size);

        updateBounds();
    }

    public int getColor()
    {
        return dataWatcher.getWatchableObjectInt(26);
    }

    public void setColor(int color)
    {
        dataWatcher.updateObject(26, color);
    }
}
