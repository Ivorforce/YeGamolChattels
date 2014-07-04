/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.IvTileEntityHelper;
import ivorius.ivtoolkit.math.IvMathHelper;
import ivorius.ivtoolkit.network.IvNetworkHelperServer;
import ivorius.ivtoolkit.network.PacketTileEntityData;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.client.rendering.TileEntityRendererSnowGlobe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntitySnowGlobe extends TileEntity implements PartialUpdateHandler
{
    public int glCallListIndex = -1;
    public boolean needsVisualUpdate = true;
    private int timeUntilVisualUpdate = 100;

    public boolean isRealityGlobe = false;
    public float realityGlobeRatio;

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (worldObj.isRemote)
        {
            if (timeUntilVisualUpdate <= 0 && !displaysDefaultHouse())
            {
                timeUntilVisualUpdate = 20 * 5;

                needsVisualUpdate = true;
            }
            else if (!displaysDefaultHouse())
            {
                timeUntilVisualUpdate--;
            }

            boolean didDisplayDefaultHouse = displaysDefaultHouse();

            progressInRGState();

            if (displaysDefaultHouse() != didDisplayDefaultHouse)
                needsVisualUpdate = true;
        }
        else
        {
            progressInRGState();
        }
    }

    private void progressInRGState()
    {
        realityGlobeRatio = IvMathHelper.nearValue(realityGlobeRatio, isRealityGlobe ? 1.0f : 0.0f, 0.0f, 0.005f);
    }

    public boolean useItem(ItemStack stack)
    {
        if (stack != null && stack.getItem() == Items.ender_eye)
        {
            if (!worldObj.isRemote)
            {
                stack.stackSize--;
                isRealityGlobe = !isRealityGlobe;

                IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "snowGlobeData", YeGamolChattels.network);
                markDirty();
            }

            return true;
        }

        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setBoolean("isRealityGlobe", isRealityGlobe);
        nbt.setFloat("realityGlobePercent", realityGlobeRatio);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        isRealityGlobe = nbt.getBoolean("isRealityGlobe");
        realityGlobeRatio = nbt.getFloat("realityGlobePercent");
    }

    public boolean displaysDefaultHouse()
    {
        return realityGlobeRatio < 0.5;
    }

    public float getObfuscationAlpha()
    {
        return (0.5f - Math.abs(realityGlobeRatio - 0.5f)) * 2.0f;
    }

    public float getRumbling()
    {
        return (0.3f - Math.abs(realityGlobeRatio - 0.5f)) * 0.1f;
    }

    public void destructCallList()
    {
        TileEntityRendererSnowGlobe.destructCallList(this);
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();

        destructCallList();
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return IvTileEntityHelper.getStandardDescriptionPacket(this);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return super.getRenderBoundingBox().expand(0.0, 0.1, 0.0);
    }

    @Override
    public void writeUpdateData(ByteBuf buffer, String context)
    {
        if ("snowGlobeData".equals(context))
        {
            buffer.writeBoolean(isRealityGlobe);
            buffer.writeFloat(realityGlobeRatio);
        }
    }

    @Override
    public void readUpdateData(ByteBuf buffer, String context)
    {
        if ("snowGlobeData".equals(context))
        {
            boolean didDisplayDefaultHouse = displaysDefaultHouse();

            isRealityGlobe = buffer.readBoolean();
            realityGlobeRatio = buffer.readFloat();

            if (displaysDefaultHouse() != didDisplayDefaultHouse)
                needsVisualUpdate = true;
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1 || pass == 0;
    }
}
