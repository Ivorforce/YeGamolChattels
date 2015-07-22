/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.items;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.tools.IvInventoryHelper;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.StatueHelper;
import ivorius.yegamolchattels.blocks.TileEntityMicroBlock;
import ivorius.yegamolchattels.blocks.YGCBlocks;
import ivorius.yegamolchattels.gui.YGCGuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by lukas on 11.07.14.
 */
public class ItemChisel extends ItemTool implements MicroblockSelector
{
    public boolean canCarveStatues;
    private int carvingDistance;
    private float fragmentPickupChance;

    public ItemChisel(int carvingDistance, float fragmentPickupChance, float damage, ToolMaterial material, Set damageVSBlocks, boolean canCarveStatues)
    {
        super(damage, material, damageVSBlocks);
        this.carvingDistance = carvingDistance;
        this.fragmentPickupChance = fragmentPickupChance;
        this.canCarveStatues = canCarveStatues;
    }

    public static boolean chiselAway(BlockPos pos, EntityPlayer player, ItemStack usedStack, ItemStack clubHammer, int range, float fragmentPickupChance)
    {
        List<IBlockState> hitFragmentDatas = chiselAway(player, pos, range);

        if (hitFragmentDatas != null && hitFragmentDatas.size() > 0)
        {
            for (IBlockState data : hitFragmentDatas)
            {
                if (itemRand.nextFloat() < fragmentPickupChance)
                {
                    usedStack.damageItem(1, player);
                    clubHammer.damageItem(1, player);

                    ItemStack fragment = new ItemStack(YGCItems.blockFragment);
                    ItemBlockFragment.setFragment(fragment, data);
                    player.inventory.addItemStackToInventory(fragment);
                    player.inventory.markDirty();
                }
            }

            return true;
        }

        return false;
    }

    public static List<IBlockState> chiselAway(Entity entity, BlockPos hover, int range)
    {
        World world = entity.worldObj;
        MicroBlockFragment hoveredFragment = getHoveredFragment(entity, hover);

        if (hoveredFragment != null)
        {
            TileEntity tileEntity = world.getTileEntity(hoveredFragment.coord);

            if (!(tileEntity instanceof TileEntityMicroBlock))
            {
                convertToMicroBlock(world, hoveredFragment.coord);
                tileEntity = world.getTileEntity(hoveredFragment.coord);
            }

            if (tileEntity instanceof TileEntityMicroBlock)
            {
                TileEntityMicroBlock tileEntityMicroBlock = (TileEntityMicroBlock) tileEntity;
                IvBlockCollection collection = tileEntityMicroBlock.getBlockCollection();

                List<IBlockState> returnList = new ArrayList<>();
                for (BlockPos carveCoord : new BlockArea(hoveredFragment.getInternalCoord().subtract(new Vec3i(range, range, range)), hoveredFragment.getInternalCoord().add(range, range, range)))
                {
                    IBlockState hitInternalBlock = collection.getBlockState(carveCoord);
                    if (hitInternalBlock.getBlock().getMaterial() != Material.air)
                    {
                        collection.setBlockState(carveCoord, Blocks.air.getDefaultState());
                        returnList.add(hitInternalBlock);
                    }
                }

                if (tileEntityMicroBlock.validateBeingMicroblock())
                    tileEntityMicroBlock.markCacheInvalid();

                return returnList;
            }
        }

        return null;
    }

    public static void convertToMicroBlock(World world, BlockPos coord)
    {
        IBlockState state = world.getBlockState(coord);
        if (ItemClubHammer.isMicroblockable(world, coord) || state.getBlock().getMaterial() == Material.air)
        {
            world.setBlockState(coord, YGCBlocks.microBlock.getDefaultState());
            TileEntity tileEntity = world.getTileEntity(coord);

            TileEntityMicroBlock tileEntityMicroBlock = (TileEntityMicroBlock) tileEntity;
            IvBlockCollection blockCollection = tileEntityMicroBlock.getBlockCollection();

            if (state.getBlock() != Blocks.air) // Default val
            {
                for (BlockPos internalCoord : blockCollection.area())
                    blockCollection.setBlockState(internalCoord, state);
            }
        }
    }

    public static MicroBlockFragment getHoveredFragment(Entity entity, BlockPos hover)
    {
        float partialTicks = 1.0f;
        double entityX = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
        double entityY = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + entity.getEyeHeight();
        double entityZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;

        if (!entity.worldObj.isRemote && entity instanceof EntityPlayer && entity.isSneaking())
            entityY -= 0.1f; // TODO Find a way not to hardcode this

        Vec3 entityPos = new Vec3(entityX, entityY, entityZ);
        return getHoveredFragment(entity.worldObj, hover, entityPos, entity.getLookVec());
    }

    public static MicroBlockFragment getHoveredFragment(World world, BlockPos hover, Vec3 entityPos, Vec3 entityLook)
    {
        TileEntity tileEntity = world.getTileEntity(hover);
        IBlockState origBlock = world.getBlockState(hover);

        IvBlockCollection collection = null;
        if (tileEntity instanceof TileEntityMicroBlock)
        {
            TileEntityMicroBlock tileEntityMicroBlock = (TileEntityMicroBlock) tileEntity;
            collection = tileEntityMicroBlock.getBlockCollection();
        }
        else if (ItemClubHammer.isMicroblockable(world, hover))
        {
            collection = new IvBlockCollection(TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_X, TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Y, TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Z);

            for (BlockPos coord : collection.area())
                collection.setBlockState(coord, origBlock);
        }

        if (collection != null)
        {
            Vec3 rayStart = getPositionInBlockCollection(collection, hover, entityPos);
            MovingObjectPosition hitPosition = collection.rayTrace(rayStart, entityLook);
            if (hitPosition != null)
            {
                BlockPos hitCoord = hitPosition.getBlockPos();
                IBlockState hitInternalBlock = collection.getBlockState(hitCoord);

                if (hitInternalBlock.getBlock().getMaterial() != Material.air)
                    return new MicroBlockFragment(hover, hitCoord, hitPosition.sideHit, hitPosition.hitVec);
            }
        }

        return null;
    }

    public static Vec3 getPositionInBlockCollection(IvBlockCollection blockCollection, BlockPos referenceCoord, Vec3 pos)
    {
        return new Vec3((pos.xCoord - referenceCoord.getX()) * blockCollection.width, (pos.yCoord - referenceCoord.getY()) * blockCollection.height, (pos.zCoord - referenceCoord.getZ()) * blockCollection.length);
    }

    public int getCarvingDistance()
    {
        return carvingDistance;
    }

    public float getFragmentPickupChance()
    {
        return fragmentPickupChance;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.inventory.hasItem(YGCItems.clubHammer))
        {
            if (showMicroblockSelection(player, itemStack))
            {
                int clubHammerSlot = IvInventoryHelper.getInventorySlotContainItem(player.inventory, YGCItems.clubHammer);
                return chiselAway(pos, player, itemStack, player.inventory.getStackInSlot(clubHammerSlot), carvingDistance, fragmentPickupChance);
            }
            else
            {
                if (StatueHelper.isValidStatueBlock(world, pos))
                {
                    if (!world.isRemote) // Some entities start with random sizes
                    {
                        player.openGui(YeGamolChattels.instance, YGCGuiHandler.statueCarvingGuiID, world, pos.getX(), pos.getY(), pos.getZ());
                    }

                    return true;
                }
            }
        }
        else
        {
            if (!world.isRemote)
                player.addChatComponentMessage(new ChatComponentTranslation("item.ygcChisel.noHammer"));
        }

        return false;
    }

    @Override
    public boolean showMicroblockSelection(EntityLivingBase renderEntity, ItemStack stack)
    {
        return !(canCarveStatues && renderEntity.isSneaking());
    }

    @Override
    public float microblockSelectionSize(ItemStack stack)
    {
        return 0.52f + carvingDistance;
    }

    public static class MicroBlockFragment
    {
        private BlockPos coord;
        private BlockPos internalCoord;
        private EnumFacing internalSide;
        private Vec3 hitPoint;

        public MicroBlockFragment(BlockPos coord, BlockPos internalCoord, EnumFacing internalSide, Vec3 hitPoint)
        {
            this.coord = coord;
            this.internalCoord = internalCoord;
            this.internalSide = internalSide;
            this.hitPoint = hitPoint;
        }

        public BlockPos getCoord()
        {
            return coord;
        }

        public BlockPos getInternalCoord()
        {
            return internalCoord;
        }

        public EnumFacing getInternalSide()
        {
            return internalSide;
        }

        public Vec3 getHitPoint()
        {
            return hitPoint;
        }

        public MicroBlockFragment getOpposite()
        {
            int blockX = coord.getX();
            int blockY = coord.getY();
            int blockZ = coord.getZ();
            int internalX = internalCoord.getX() + internalSide.getFrontOffsetX();
            int internalY = internalCoord.getY() + internalSide.getFrontOffsetY();
            int internalZ = internalCoord.getZ() + internalSide.getFrontOffsetZ();

            if (internalX < 0)
            {
                internalX = TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_X - 1;
                blockX--;
            }
            else if (internalX >= TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_X)
            {
                internalX = 0;
                blockX++;
            }

            if (internalY < 0)
            {
                internalY = TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Y - 1;
                blockY--;
            }
            else if (internalY >= TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Y)
            {
                internalY = 0;
                blockY++;
            }

            if (internalZ < 0)
            {
                internalZ = TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Z - 1;
                blockZ--;
            }
            else if (internalZ >= TileEntityMicroBlock.MICROBLOCKS_PER_BLOCK_Z)
            {
                internalZ = 0;
                blockZ++;
            }

            return new MicroBlockFragment(new BlockPos(blockX, blockY, blockZ), new BlockPos(internalX, internalY, internalZ), internalSide.getOpposite(), hitPoint);
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
}
