/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.ivtoolkit.network.ITileEntityUpdateData;
import ivorius.ivtoolkit.tools.IvDateHelper;
import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityStatue extends IvTileEntityMultiBlock implements ITileEntityUpdateData
{
    public static Class[] equippableMobs = new Class[]{EntityZombie.class, EntitySkeleton.class};
    public static Class[] dangerousMobs = new Class[]{EntityGiantZombie.class, EntityDragon.class, EntityWither.class};

    private Entity statueEntity;

    public Entity getStatueEntity()
    {
        return statueEntity;
    }

    public void setStatueEntity(Entity statueEntity, boolean safe)
    {
        if (safe && statueEntity == null)
        {
            YeGamolChattels.logger.error("Could not read entity for statue! Using pig instead. (isRemote=" + worldObj.isRemote + ")");
            statueEntity = new EntityPig(getWorldObj());
        }

        this.statueEntity = statueEntity;
    }

    public void setStatueEntityWithNotify(Entity statueEntity, boolean safe)
    {
        setStatueEntity(statueEntity, safe);

        YeGamolChattels.chTileEntityData.sendUpdatePacketSafe(this, "statueData");
        markDirty();
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (YeGamolChattels.easterEggsAllowed && worldObj.isRemote && statueEntity instanceof EntityLiving && IvDateHelper.isHalloween())
        {
            EntityLiving livingStatue = (EntityLiving) statueEntity;

            double[] center = getActiveCenterCoords();
            EntityLivingBase player = Minecraft.getMinecraft().thePlayer;
            statueEntity.setPosition(center[0], center[1], center[2]);

            if (livingStatue instanceof EntityVillager)
                livingStatue.rotationYawHead = livingStatue.rotationYawHead + 180.0f; // Don't ask

            livingStatue.getLookHelper().setLookPositionWithEntity(player, 2.0f, 2.0f);
            livingStatue.getLookHelper().onUpdateLook();

            if (livingStatue instanceof EntityVillager)
                livingStatue.rotationYawHead = livingStatue.rotationYawHead + 180.0f; // Don't ask

            livingStatue.prevRotationYawHead = livingStatue.rotationYawHead;
            livingStatue.prevRotationPitch = livingStatue.rotationPitch;
        }
    }

    public boolean letStatueComeAlive()
    {
        if (statueEntity != null && YeGamolChattels.areLifeStatuesAllowed)
        {
            boolean dangerous = false;
            for (Class c : dangerousMobs)
                if (statueEntity.getClass() == c)
                    dangerous = true;
            if (YeGamolChattels.areDangerousStatuesAllowed || !dangerous)
            {
                if (!worldObj.isRemote)
                {
                    int r = getBlockMetadata();

                    double[] center = getActiveCenterCoords();
                    statueEntity.setLocationAndAngles(center[0], center[1] - centerCoordsSize[1], center[2], (-r + 2) * 90, 0);
                    statueEntity.prevRotationYaw = statueEntity.rotationYaw;
                    statueEntity.prevRotationPitch = statueEntity.rotationPitch;

                    // Rotation doesn't get transmitted ;_;
//                    if (statueEntity instanceof EntityLivingBase)
//                    {
//                        ((EntityLivingBase) statueEntity).rotationYawHead = statueEntity.rotationYaw;
//                        ((EntityLivingBase) statueEntity).prevRotationYawHead = statueEntity.rotationYaw;
//                        ((EntityLivingBase) statueEntity).renderYawOffset = statueEntity.rotationYaw;
//                        ((EntityLivingBase) statueEntity).prevRenderYawOffset = statueEntity.rotationYaw;
//                    }

                    int indefiniteTime = 9999999;
                    if (getBlockType() == YGCBlocks.statueStone)
                    {
                        ((EntityLiving) statueEntity).addPotionEffect(new PotionEffect(Potion.resistance.id, indefiniteTime, 0, true));
                        ((EntityLiving) statueEntity).addPotionEffect(new PotionEffect(Potion.fireResistance.id, indefiniteTime, 0, true));
                    }
                    if (getBlockType() == YGCBlocks.statueGold)
                    {
                        ((EntityLiving) statueEntity).addPotionEffect(new PotionEffect(Potion.fireResistance.id, indefiniteTime, 0, true));
                    }

                    worldObj.spawnEntityInWorld(statueEntity);

                    YeGamolChattels.chTileEntityData.sendUpdatePacketSafe(this, "statueData");
                    markDirty();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);

        writeStatueDataToNBT(par1NBTTagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        readStatueDataFromNBT(par1NBTTagCompound);
    }

    public void writeStatueDataToNBT(NBTTagCompound compound)
    {
        if (statueEntity != null)
        {
            NBTTagCompound statueCompound = new NBTTagCompound();
            statueEntity.writeToNBTOptional(statueCompound);
            compound.setTag("statueEntity", statueCompound);
        }
    }

    public void readStatueDataFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("statueEntity"))
            setStatueEntity(EntityList.createEntityFromNBT(compound.getCompoundTag("statueEntity"), worldObj), true);
    }

    public boolean isEntityEquippable()
    {
        boolean isEqquipable = false;

        if (statueEntity != null)
        {
            for (Class eq : equippableMobs)
                if (eq.isAssignableFrom(statueEntity.getClass()))
                    isEqquipable = true;
        }

        return isEqquipable;
    }

    public boolean tryEquipping(ItemStack item)
    {
        if (statueEntity != null)
        {
            if (isEntityEquippable())
            {
                int slot = item == null ? -1 : EntityLiving.getArmorPosition(item);

                if (slot >= 0 && statueEntity.getLastActiveItems()[slot] == null)
                {
                    if (!worldObj.isRemote)
                    {
                        statueEntity.setCurrentItemOrArmor(slot, item.copy());
                        item.stackSize = 0;

                        YeGamolChattels.chTileEntityData.sendUpdatePacketSafe(this, "statueData");
                        markDirty();
                    }

                    return true;
                }
            }

//            if (statueEntity instanceof EntityHorse)
//            {
//                EntityHorse horse = (EntityHorse) statueEntity;
//
//                if (item.getItem() == Items.saddle)
//                {
//                    horse.horseChest
//                }
//                else if (horse.func_110259_cr() /* canHaveArmor */ && EntityHorse.func_146085_a(item.getItem()) /* isArmor */)
//                {
//
//                }
//            }
        }

        return false;
    }

    public void dropEquipment()
    {
        if (statueEntity != null)
        {
            if (!worldObj.isRemote)
            {
                for (int i = 0; i < statueEntity.getLastActiveItems().length; i++)
                {
                    if (statueEntity.getLastActiveItems()[i] != null)
                    {
                        float var7 = 0.7F;
                        double var8 = worldObj.rand.nextFloat() * var7 + (1.0F - var7) * 0.5D;
                        double var10 = worldObj.rand.nextFloat() * var7 + (1.0F - var7) * 0.2D + 0.6D;
                        double var12 = worldObj.rand.nextFloat() * var7 + (1.0F - var7) * 0.5D;
                        EntityItem var14 = new EntityItem(worldObj, xCoord + var8, yCoord + var10, zCoord + var12, statueEntity.getLastActiveItems()[i]);
                        var14.delayBeforeCanPickup = 10;
                        worldObj.spawnEntityInWorld(var14);

                        statueEntity.getLastActiveItems()[i] = null;
                    }
                }

                YeGamolChattels.chTileEntityData.sendUpdatePacketSafe(this, "statueData");
                markDirty();
            }
        }
    }

    @Override
    public double getMaxRenderDistanceSquared()
    {
        if (statueEntity != null)
        {
            double var3 = statueEntity.boundingBox.getAverageEdgeLength();
            var3 *= 64.0D * statueEntity.renderDistanceWeight;

            return var3 * var3;
        }
        return super.getMaxRenderDistanceSquared();
    }

    public int getTextureType()
    {
        if (getBlockType() == YGCBlocks.statueStone)
            return 1;
        if (getBlockType() == YGCBlocks.statuePlanks)
            return 0;
        if (getBlockType() == YGCBlocks.statueGold)
            return 2;

        return 0;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        if (statueEntity != null)
        {
            if (statueEntity.ignoreFrustumCheck)
                return INFINITE_EXTENT_AABB;

            return getBoxAroundCenter(statueEntity.width / 2, statueEntity.height / 2, statueEntity.width / 2);
        }

        else return super.getRenderBoundingBox();
    }

    @Override
    public void writeUpdateData(ByteBuf buffer, String context)
    {
        if ("statueData".equals(context))
        {
            NBTTagCompound compound = new NBTTagCompound();
            writeStatueDataToNBT(compound);
            ByteBufUtils.writeTag(buffer, compound);
        }
    }

    @Override
    public void readUpdateData(ByteBuf buffer, String context)
    {
        if ("statueData".equals(context))
        {
            readStatueDataFromNBT(ByteBufUtils.readTag(buffer));
        }
    }
}
