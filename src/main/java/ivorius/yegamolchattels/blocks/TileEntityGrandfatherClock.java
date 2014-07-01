/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvTileEntityHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityGrandfatherClock extends TileEntity
{
    public int ticksAlive;

    public long clockTimeShown;
    public long clockTimeMotion;
    public long pendulumTimeShown;
    public long pendulumTimeMotion;

    public int delayUntilSound = 0;

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (worldObj.provider.isSurfaceWorld())
        {
            clockTimeShown = worldObj.getWorldTime();
            pendulumTimeShown = ticksAlive * 20;
        }
        else
        {
            clockTimeMotion += worldObj.rand.nextLong() % 301L;
            if (clockTimeMotion > 1500L)
                clockTimeMotion = 1500L;
            if (clockTimeMotion < -1500L)
                clockTimeMotion = -1500L;
            clockTimeShown += clockTimeMotion;

            pendulumTimeMotion += worldObj.rand.nextLong() % 21L;
            if (pendulumTimeMotion > 100L)
                pendulumTimeMotion = 100L;
            if (pendulumTimeMotion < -100L)
                pendulumTimeMotion = -100L;
            pendulumTimeShown += pendulumTimeMotion;
        }

        if (!worldObj.isRemote && (getBlockMetadata() & 1) == 0)
        {
            if (delayUntilSound <= 0)
            {
                if (worldObj.getWorldTime() % (24000 / 6) == 0)
                {
                    worldObj.playSoundEffect(xCoord + 0.5f, yCoord + 1.0f, zCoord + 0.5f, "note.pling", 0.2f, 0.01f);
                    worldObj.playSoundEffect(xCoord + 0.5f, yCoord + 1.0f, zCoord + 0.5f, "random.orb", 1.0f, 0.01f);
                    worldObj.playSoundEffect(xCoord + 0.5f, yCoord + 1.0f, zCoord + 0.5f, "random.break", 0.3f, 0.1f);
                    delayUntilSound = 10;
                }
                else if (ticksAlive % 40 == 0)
                {
                    worldObj.playSoundEffect(xCoord + 0.5f, yCoord + 1.0f, zCoord + 0.5f, "random.click", 0.1f, 0.5f);
                    delayUntilSound = 10;
                }
                else if (ticksAlive % 40 == 20)
                {
                    worldObj.playSoundEffect(xCoord + 0.5f, yCoord + 1.0f, zCoord + 0.5f, "random.click", 0.1f, 0.6f);
                    delayUntilSound = 10;
                }
            }
            else
                delayUntilSound--;
        }

        ticksAlive++;
    }

    @Override
    public void writeToNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.writeToNBT(par1nbtTagCompound);

        par1nbtTagCompound.setInteger("ticksAlive", ticksAlive);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.readFromNBT(par1nbtTagCompound);

        ticksAlive = par1nbtTagCompound.getInteger("ticksAlive");
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return IvTileEntityHelper.getStandardDescriptionPacket(this);
    }
}
