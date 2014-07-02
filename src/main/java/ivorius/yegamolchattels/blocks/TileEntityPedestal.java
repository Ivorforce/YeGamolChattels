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
import ivorius.ivtoolkit.network.PacketTileEntityData;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityPedestal extends IvTileEntityMultiBlock implements PartialUpdateHandler
{
    public int pedestalIdentifier;

    public int ticksAlive;

    public ItemStack storedItem;

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
            else if (!itemShouldBeUp && storedItem != null)
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
            if (storedItem != null || stack == null)
                return false;

            if (!worldObj.isRemote)
            {
                storedItem = stack.copy();
                storedItem.stackSize = 1;
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
            if (storedItem == null)
                return false;

            if (!worldObj.isRemote)
            {
                itemShouldBeUp = false;

                if (player != null && getIntegrationTime() == 0)
                {
                    IvEntityHelper.addAsCurrentItem(player, storedItem);
                    storedItem = null;
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
            if (storedItem != null)
            {
                if (!worldObj.isRemote)
                {
                    EntityItem itemEntity = new EntityItem(worldObj, xCoord, yCoord, zCoord, storedItem);
                    worldObj.spawnEntityInWorld(itemEntity);

                    storedItem = null;

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

        if (storedItem != null)
        {
            NBTTagCompound var4 = new NBTTagCompound();
            storedItem.writeToNBT(var4);
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
            storedItem = ItemStack.loadItemStackFromNBT(var4);
        }
        else
        {
            storedItem = null;
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
    public void writeUpdateData(ByteBuf buffer, String context)
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
}
