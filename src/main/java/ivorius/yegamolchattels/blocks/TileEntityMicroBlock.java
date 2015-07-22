/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.blocks;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.network.IvNetworkHelperServer;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.ivtoolkit.tools.MCRegistryDefault;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.client.rendering.GridQuadCache;
import ivorius.yegamolchattels.client.rendering.IconQuadCache;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import ivorius.ivtoolkit.rendering.grid.Icon;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by lukas on 11.07.14.
 */
public class TileEntityMicroBlock extends TileEntity implements PartialUpdateHandler
{
    public static final int MICROBLOCKS_PER_BLOCK_X = 8;
    public static final int MICROBLOCKS_PER_BLOCK_Y = 8;
    public static final int MICROBLOCKS_PER_BLOCK_Z = 8;
    public static final int MAX_MICROBLOCK_MAPPINGS = 64;

    private IvBlockCollection blockCollection;

    private boolean[] isSideOpaque = new boolean[6];

    private boolean shouldDropAsItem = true;

    @SideOnly(Side.CLIENT)
    private GridQuadCache<Icon> quadCache;

    public TileEntityMicroBlock()
    {
        this.blockCollection = new IvBlockCollection(MICROBLOCKS_PER_BLOCK_X, MICROBLOCKS_PER_BLOCK_Y, MICROBLOCKS_PER_BLOCK_Z);
    }

    public IvBlockCollection getBlockCollection()
    {
        return blockCollection;
    }

    public void setBlockCollection(IvBlockCollection blockCollection)
    {
        this.blockCollection = blockCollection;
    }

    public boolean isSideOpaque(EnumFacing direction)
    {
        return isSideOpaque[direction.ordinal()];
    }

    public boolean areAllSidesOpaque()
    {
        return isSideOpaque[0] && isSideOpaque[1] && isSideOpaque[2] && isSideOpaque[3] && isSideOpaque[4] && isSideOpaque[5];
    }

    public void markCacheInvalid()
    {
        markCacheInvalid(false);
    }

    private void markCacheInvalid(boolean fromNBT)
    {
        int[] sizeX = new int[]{1, blockCollection.height, blockCollection.length};
        int[] sizeY = new int[]{blockCollection.width, 1, blockCollection.length};
        int[] sizeZ = new int[]{blockCollection.width, blockCollection.height, 1};
        BlockPos zeroCoord = new BlockPos(0, 0, 0);

        isSideOpaque[EnumFacing.DOWN.ordinal()] = areAllOpaque(BlockArea.areaFromSize(zeroCoord, sizeY));
        isSideOpaque[EnumFacing.UP.ordinal()] = areAllOpaque(BlockArea.areaFromSize(new BlockPos(0, blockCollection.height - 1, 0), sizeY));
        isSideOpaque[EnumFacing.NORTH.ordinal()] = areAllOpaque(BlockArea.areaFromSize(zeroCoord, sizeZ));
        isSideOpaque[EnumFacing.EAST.ordinal()] = areAllOpaque(BlockArea.areaFromSize(new BlockPos(blockCollection.width - 1, 0, 0), sizeX));
        isSideOpaque[EnumFacing.SOUTH.ordinal()] = areAllOpaque(BlockArea.areaFromSize(new BlockPos(0, 0, blockCollection.length - 1), sizeZ));
        isSideOpaque[EnumFacing.WEST.ordinal()] = areAllOpaque(BlockArea.areaFromSize(zeroCoord, sizeX));

        if (worldObj != null)
        {
            if (!worldObj.isRemote)
                IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "microBlocks", YeGamolChattels.network);
            else
                markCacheInvalidClient(fromNBT);

            worldObj.markBlockForUpdate(getPos());
        }

        markDirty();
    }

    @SideOnly(Side.CLIENT)
    private void markCacheInvalidClient(boolean fromNBT)
    {
        quadCache = null;
    }

    public boolean validateBeingMicroblock()
    {
        boolean allSame = true;
        Block curBlock = blockCollection.getBlock(new BlockPos(0, 0, 0));
        int curMeta = blockCollection.getMetadata(new BlockPos(0, 0, 0));
        for (BlockPos coord : blockCollection)
        {
            if (blockCollection.getBlock(coord) != curBlock || blockCollection.getMetadata(coord) != curMeta)
            {
                allSame = false;
                break;
            }
        }

        if (allSame)
        {
            shouldDropAsItem = false;
            worldObj.setBlock(getPos(), curBlock, curMeta, 3);
        }
        else if (blockCollection.getBlockMultiplicity() > MAX_MICROBLOCK_MAPPINGS)
        {
            BlockMicroBlock.dropAllMicroblockFragments(this, 1.0f);
        }

        return !allSame;
    }

    private boolean areAllOpaque(BlockArea area)
    {
        for (BlockPos coord : area)
        {
            if (!blockCollection.getBlock(coord).isOpaqueCube())
                return false;
        }

        return true;
    }

    public boolean shouldDropAsItem()
    {
        return shouldDropAsItem;
    }

    public void setShouldDropAsItem(boolean shouldDropAsItem)
    {
        this.shouldDropAsItem = shouldDropAsItem;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        writeSyncToNBT(compound);
    }

    protected void writeSyncToNBT(NBTTagCompound compound)
    {
        compound.setTag("microblocks", blockCollection.createTagCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        readSyncFromNBT(compound);
    }

    protected void readSyncFromNBT(NBTTagCompound compound)
    {
        blockCollection = new IvBlockCollection(compound.getCompoundTag("microblocks"), MCRegistryDefault.INSTANCE);
        markCacheInvalid(true);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        writeSyncToNBT(compound);
        return new S35PacketUpdateTileEntity(getPos(), 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        readSyncFromNBT(pkt.func_148857_g());
    }

    @Override
    public void writeUpdateData(ByteBuf buffer, String context, Object... params)
    {
        if ("microBlocks".equals(context))
        {
            ByteBufUtils.writeTag(buffer, blockCollection.createTagCompound());
        }
    }

    @Override
    public void readUpdateData(ByteBuf buffer, String context)
    {
        if ("microBlocks".equals(context))
        {
            blockCollection = new IvBlockCollection(ByteBufUtils.readTag(buffer), MCRegistryDefault.INSTANCE);
            markCacheInvalid(true);
        }
    }

    @SideOnly(Side.CLIENT)
    public GridQuadCache<Icon> getQuadCache()
    {
        if (quadCache == null)
            quadCache = IconQuadCache.createIconQuadCache(blockCollection);

        return quadCache;
    }
}
