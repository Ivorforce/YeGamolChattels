/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.tools.IvInventoryHelper;
import ivorius.yegamolchattels.blocks.TileEntityMicroBlock;
import ivorius.yegamolchattels.blocks.YGCBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Set;

/**
 * Created by lukas on 11.07.14.
 */
public class ItemChisel extends ItemTool
{
    public ItemChisel(float damage, ToolMaterial material, Set damageVSBlocks)
    {
        super(damage, material, damageVSBlocks);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (player.inventory.hasItem(YGCItems.clubHammer))
        {
            int clubHammerSlot = IvInventoryHelper.getInventorySlotContainItem(player.inventory, YGCItems.clubHammer);
            return chiselAway(world, x, y, z, player, itemStack, player.inventory.getStackInSlot(clubHammerSlot));
        }
        else
        {
            player.addChatComponentMessage(new ChatComponentTranslation("item.ygcChisel.noHammer"));
            return false;
        }
    }

    public boolean chiselAway(World world, int x, int y, int z, EntityPlayer player, ItemStack usedStack, ItemStack clubHammer)
    {
        BlockData hitFragmentData = chiselAway(player, x, y, z);

        if (hitFragmentData != null)
        {
            usedStack.damageItem(1, player);
            clubHammer.damageItem(1, player);

            ItemStack fragment = new ItemStack(YGCItems.blockFragment);
            ItemBlockFragment.setFragment(fragment, hitFragmentData);
            player.inventory.addItemStackToInventory(fragment);
            player.inventory.markDirty();

            return true;
        }

        return false;
    }

    public BlockData chiselAway(Entity entity, int hoverX, int hoverY, int hoverZ)
    {
        World world = entity.worldObj;
        MicroBlockFragment hoveredFragment = getHoveredFragment(entity, hoverX, hoverY, hoverZ);

        if (hoveredFragment != null)
        {
            TileEntity tileEntity = world.getTileEntity(hoveredFragment.coord.x, hoveredFragment.coord.y, hoveredFragment.coord.z);

            if (!(tileEntity instanceof TileEntityMicroBlock))
            {
                convertToMicroBlock(world, hoveredFragment.coord);
                tileEntity = world.getTileEntity(hoveredFragment.coord.x, hoveredFragment.coord.y, hoveredFragment.coord.z);
            }

            if (tileEntity instanceof TileEntityMicroBlock)
            {
                TileEntityMicroBlock tileEntityMicroBlock = (TileEntityMicroBlock) tileEntity;
                IvBlockCollection collection = tileEntityMicroBlock.getBlockCollection();

                Block hitInternalBlock = collection.getBlock(hoveredFragment.getInternalCoord()), block;
                if (hitInternalBlock.getMaterial() != Material.air)
                {
                    byte hitInternalMeta = collection.getMetadata(hoveredFragment.getInternalCoord());

                    collection.setBlockAndMetadata(hoveredFragment.getInternalCoord(), Blocks.air, (byte) 0);
                    if (tileEntityMicroBlock.validateBeingMicroblock())
                        tileEntityMicroBlock.markCacheInvalid();

                    return new BlockData(hitInternalBlock, hitInternalMeta);
                }
            }
        }

        return null;
    }

    public static void convertToMicroBlock(World world, BlockCoord coord)
    {
        Block block = coord.getBlock(world);
        if (ItemClubHammer.isMicroblockable(block) || block.getMaterial() == Material.air)
        {
            byte metadata = (byte) coord.getMetadata(world);

            world.setBlock(coord.x, coord.y, coord.z, YGCBlocks.microBlock);
            TileEntity tileEntity = world.getTileEntity(coord.x, coord.y, coord.z);

            TileEntityMicroBlock tileEntityMicroBlock = (TileEntityMicroBlock) tileEntity;
            IvBlockCollection blockCollection = tileEntityMicroBlock.getBlockCollection();

            if (block != Blocks.air) // Default val
            {
                for (BlockCoord internalCoord : blockCollection)
                    blockCollection.setBlockAndMetadata(internalCoord, block, metadata);
            }
        }
    }

    public static MicroBlockFragment getHoveredFragment(Entity entity, int hoverX, int hoverY, int hoverZ)
    {
        float partialTicks = 1.0f;
        double entityX = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
        double entityY = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + 1.62D - (double)entity.yOffset;
        double entityZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
        Vec3 entityPos = Vec3.createVectorHelper(entityX, entityY, entityZ);
        return getHoveredFragment(entity.worldObj, hoverX, hoverY, hoverZ, entityPos, entity.getLookVec());
    }

    public static MicroBlockFragment getHoveredFragment(World world, int hoverX, int hoverY, int hoverZ, Vec3 entityPos, Vec3 entityLook)
    {
        TileEntity tileEntity = world.getTileEntity(hoverX, hoverY, hoverZ);
        Block origBlock = world.getBlock(hoverX, hoverY, hoverZ);

        IvBlockCollection collection = null;
        if (tileEntity instanceof TileEntityMicroBlock)
        {
            TileEntityMicroBlock tileEntityMicroBlock = (TileEntityMicroBlock) tileEntity;
            collection = tileEntityMicroBlock.getBlockCollection();
        }
        else if (ItemClubHammer.isMicroblockable(origBlock))
        {
            collection = new IvBlockCollection(TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_X, TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Y, TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Z);

            for (BlockCoord coord : collection)
                collection.setBlockAndMetadata(coord, origBlock, (byte) world.getBlockMetadata(hoverX, hoverY, hoverZ));
        }

        if (collection != null)
        {
            Vec3 rayStart = getPositionInBlockCollection(collection, new BlockCoord(hoverX, hoverY, hoverZ), entityPos);
            MovingObjectPosition hitPosition = collection.rayTrace(rayStart, entityLook);
            if (hitPosition != null)
            {
                BlockCoord hitCoord = new BlockCoord(hitPosition.blockX, hitPosition.blockY, hitPosition.blockZ);
                Block hitInternalBlock = collection.getBlock(hitCoord);

                if (hitInternalBlock.getMaterial() != Material.air)
                {
                    return new MicroBlockFragment(new BlockCoord(hoverX, hoverY, hoverZ), hitCoord, ForgeDirection.getOrientation(hitPosition.sideHit), hitPosition.hitVec);
                }
            }
        }

        return null;
    }

    public static Vec3 getPositionInBlockCollection(IvBlockCollection blockCollection, BlockCoord referenceCoord, Vec3 pos)
    {
        return Vec3.createVectorHelper((pos.xCoord - referenceCoord.x) * blockCollection.width, (pos.yCoord - referenceCoord.y) * blockCollection.height, (pos.zCoord - referenceCoord.z) * blockCollection.length);
    }

    public static class MicroBlockFragment
    {
        private BlockCoord coord;
        private BlockCoord internalCoord;
        private ForgeDirection internalSide;
        private Vec3 hitPoint;

        public MicroBlockFragment(BlockCoord coord, BlockCoord internalCoord, ForgeDirection internalSide, Vec3 hitPoint)
        {
            this.coord = coord;
            this.internalCoord = internalCoord;
            this.internalSide = internalSide;
            this.hitPoint = hitPoint;
        }

        public BlockCoord getCoord()
        {
            return coord;
        }

        public BlockCoord getInternalCoord()
        {
            return internalCoord;
        }

        public ForgeDirection getInternalSide()
        {
            return internalSide;
        }

        public Vec3 getHitPoint()
        {
            return hitPoint;
        }

        public MicroBlockFragment getOpposite()
        {
            long newX = coord.x * TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_X + internalCoord.x + internalSide.offsetX;
            long newY = coord.y * TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Y + internalCoord.y + internalSide.offsetY;
            long newZ = coord.z * TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Z + internalCoord.z + internalSide.offsetZ;

            BlockCoord newCoord = new BlockCoord((int)(newX / TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_X), (int)(newY / TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Y), (int)(newZ / TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Z));
            BlockCoord newInternalCoord = new BlockCoord((int)(newX % TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_X), (int)(newY % TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Y), (int)(newZ % TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Z));

            return new MicroBlockFragment(newCoord, newInternalCoord, internalSide.getOpposite(), hitPoint);
        }

        @Override
        public String toString()
        {
            return "MicroBlockFragment{" +
                    "coord=" + coord +
                    ", internalCoord=" + internalCoord +
                    ", internalSide=" + internalSide +
                    ", hitPoint=" + hitPoint +
                    '}';
        }
    }

    public static class BlockData
    {
        public Block block;
        public byte meta;

        public BlockData(Block block, byte meta)
        {
            this.block = block;
            this.meta = meta;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BlockData blockData = (BlockData) o;

            return block == blockData.block && meta == blockData.meta;
        }

        @Override
        public int hashCode()
        {
            int result = block.hashCode();
            result = 31 * result + (int) meta;
            return result;
        }

        @Override
        public String toString()
        {
            return "BlockData{" +
                    "block=" + block +
                    ", meta=" + meta +
                    '}';
        }
    }
}
