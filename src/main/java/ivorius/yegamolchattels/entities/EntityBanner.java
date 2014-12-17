/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.entities;

import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class EntityBanner extends EntityHanging
{
    int savedSize = -1;

    public float wind;
    public float simWind;

    public EntityBanner(World world)
    {
        super(world);

        setSize(1);
        setColor(0);

        this.setDirection(0);
    }

    public EntityBanner(World world, int x, int y, int z)
    {
        super(world, x, y, z, 0);

        setSize(1);
        setColor(0);

        this.setDirection(0);
    }

    public EntityBanner(World world, int x, int y, int z, int direction, int bannerSize, int bannerColor)
    {
        super(world, x, y, z, direction);

        this.setSize(bannerSize);
        this.setColor(bannerColor);

        this.setDirection(direction);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();

        this.dataWatcher.addObject(25, 0);
        this.dataWatcher.addObject(26, 0);
        this.dataWatcher.addObject(27, 0);

        this.dataWatcher.addObject(28, 0);
        this.dataWatcher.addObject(29, 0);
        this.dataWatcher.addObject(30, 0);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

//        if (!worldObj.isRemote && ticksExisted % 100 == 5)
//        {
//            for (Object player : worldObj.playerEntities)
//            {
//                if (worldObj.playerEntities.size() > 0)
//                {
//                    EntityPlayerMP p = (EntityPlayerMP) player;
//					ModLoader.serverSendPacket(p.playerNetServerHandler, new Packet40EntityMetadata(entityId, dataWatcher, true));
//                }
//            }
//        }

        if (dataWatcher.getWatchableObjectInt(27) != hangingDirection || dataWatcher.getWatchableObjectInt(28) != getXPosition() || dataWatcher.getWatchableObjectInt(29) != getYPosition() || dataWatcher.getWatchableObjectInt(30) != getZPosition())
        {
            if (!worldObj.isRemote)
            {
                dataWatcher.updateObject(27, hangingDirection);
                dataWatcher.updateObject(28, getXPosition());
                dataWatcher.updateObject(29, getYPosition());
                dataWatcher.updateObject(30, getZPosition());
            }
            else
            {
                hangingDirection = dataWatcher.getWatchableObjectInt(27);
                this.setXPosition(dataWatcher.getWatchableObjectInt(28));
                this.setYPosition(dataWatcher.getWatchableObjectInt(29));
                this.setZPosition(dataWatcher.getWatchableObjectInt(30));
                setDirection(hangingDirection);
            }
        }

        if (worldObj.isRemote && getSize() != savedSize)
        {
            savedSize = getSize();
            setDirection(hangingDirection);
        }

        wind = EntityFlag.updateWind(wind, worldObj);
        simWind = EntityFlag.updateSimWind(wind, simWind);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setInteger("BannerColor", getColor());
        nbttagcompound.setInteger("BannerSize", getSize());

        super.writeEntityToNBT(nbttagcompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        setColor(nbttagcompound.getInteger("BannerColor"));
        setSize(nbttagcompound.getInteger("BannerSize"));

        super.readEntityFromNBT(nbttagcompound);
    }

    @Override
    public int getWidthPixels()
    {
        if (getSize() == 0)
            return 16;
        else if (getSize() == 2)
            return 3 * 16;

        return 16;
    }

    @Override
    public int getHeightPixels()
    {
        if (getSize() == 0)
            return 16;
        else if (getSize() == 2)
            return 4 * 16;

        return 16;
    }

    @Override
    public void onBroken(Entity entity)
    {
        if (getSize() == 0)
            worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(YGCItems.bannerSmall, 1, getColor())));

        else if (getSize() == 2)
            worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(YGCItems.bannerLarge, 1, getColor())));

        else
            worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(YGCItems.bannerSmall, 1, getColor())));
    }

    public void dropBanner()
    {
        if (getSize() == 0)
            worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(YGCItems.bannerSmall, 1, getColor())));

        else if (getSize() == 2)
            worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(YGCItems.bannerLarge, 1, getColor())));

        else
            worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(YGCItems.bannerSmall, 1, getColor())));
    }

    public int getSize()
    {
        return dataWatcher.getWatchableObjectInt(25);
    }

    public void setSize(int size)
    {
        dataWatcher.updateObject(25, size);
    }

    public int getColor()
    {
        return dataWatcher.getWatchableObjectInt(26);
    }

    public void setColor(int color)
    {
        dataWatcher.updateObject(26, color);
    }

    @Override
    public boolean onValidSurface()
    {
        if (!this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty())
        {
            return false;
        }
        else
        {
            int var1 = Math.max(1, this.getWidthPixels() / 16);
            int var2 = Math.max(1, this.getHeightPixels() / 16);
            int var3 = this.getXPosition();
            int var4 = this.getYPosition();
            int var5 = this.getZPosition();

            if (this.hangingDirection == 2)
            {
                var3 = MathHelper.floor_double(this.posX - this.getWidthPixels() / 32.0F);
            }

            if (this.hangingDirection == 1)
            {
                var5 = MathHelper.floor_double(this.posZ - this.getWidthPixels() / 32.0F);
            }

            if (this.hangingDirection == 0)
            {
                var3 = MathHelper.floor_double(this.posX - this.getWidthPixels() / 32.0F);
            }

            if (this.hangingDirection == 3)
            {
                var5 = MathHelper.floor_double(this.posZ - this.getWidthPixels() / 32.0F);
            }

            var4 = MathHelper.floor_double(this.posY - this.getHeightPixels() / 32.0F);

            for (int var6 = 0; var6 < var1; ++var6)
            {
                for (int var7 = var2 - 1; var7 < var2; ++var7) //Change to only check upper row
                {
                    Material var8;

                    if (this.hangingDirection != 2 && this.hangingDirection != 0)
                    {
                        var8 = this.worldObj.getBlock(this.getXPosition(), var4 + var7, var5 + var6).getMaterial();
                    }
                    else
                    {
                        var8 = this.worldObj.getBlock(var3 + var6, var4 + var7, this.getZPosition()).getMaterial();
                    }

                    if (!var8.isSolid())
                    {
                        return false;
                    }
                }
            }

            List var9 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);
            Iterator var10 = var9.iterator();
            Entity var11;

            do
            {
                if (!var10.hasNext())
                {
                    return true;
                }

                var11 = (Entity) var10.next();
            }
            while (!(var11 instanceof EntityHanging));

            return false;
        }
    }

    public int getXPosition()
    {
        return this.field_146063_b;
    }

    public int getYPosition()
    {
        return this.field_146064_c;
    }

    public int getZPosition()
    {
        return this.field_146062_d;
    }

    public void setXPosition(int position)
    {
        this.field_146063_b = position;
    }

    public void setYPosition(int position)
    {
        this.field_146064_c = position;
    }

    public void setZPosition(int position)
    {
        this.field_146062_d = position;
    }
}
