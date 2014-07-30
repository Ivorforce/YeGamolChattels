/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by lukas on 29.07.14.
 */
public class Statue
{
    private Entity entity;
    private BlockFragment material;

    private float yawHead;
    private float pitchHead;
    private float swing;
    private float stance;

    public Statue(Entity entity, BlockFragment material, float yawHead, float pitchHead, float swing, float stance)
    {
        this.entity = entity;
        this.material = material;
        this.yawHead = yawHead;
        this.pitchHead = pitchHead;
        this.swing = swing;
        this.stance = stance;
    }

    public Statue(NBTTagCompound compound, World worldObj)
    {
        if (compound.hasKey("entity"))
            entity = EntityList.createEntityFromNBT(compound.getCompoundTag("entity"), worldObj);
        else
            entity = new EntityPig(worldObj);

        yawHead = compound.getFloat("yawHead");
        pitchHead = compound.getFloat("pitchHead");
        swing = compound.getFloat("swing");
        stance = compound.getFloat("stance");

        if (compound.hasKey("block"))
        {
            material = getMaterial(compound);
        }
        else
            material = new BlockFragment(Blocks.stone, 0);
    }

    public static void setRotations(EntityLivingBase entityLivingBase, float yawHead, float pitchHead, float swing, float stance)
    {
        entityLivingBase.swingProgress = swing;
        entityLivingBase.prevSwingProgress = swing;

        entityLivingBase.limbSwing = stance;
        entityLivingBase.limbSwingAmount = 0.0f;
        entityLivingBase.prevLimbSwingAmount = 1.0f;

//        entityLivingBase.renderYawOffset = yawHead;
//        entityLivingBase.rotationYaw = yawHead;
        entityLivingBase.rotationYawHead = yawHead;
        entityLivingBase.prevRotationYawHead = yawHead;
        entityLivingBase.rotationPitch = pitchHead;
        entityLivingBase.prevRotationPitch = pitchHead;
    }

    public static String getEntityID(NBTTagCompound compound)
    {
        return compound.getCompoundTag("entity").getString("id");
    }

    public static BlockFragment getMaterial(NBTTagCompound compound)
    {
        return new BlockFragment(Block.getBlockFromName(compound.getString("block")), compound.getInteger("blockMetadata"));
    }

    public Entity getEntity()
    {
        return entity;
    }

    public void setEntity(Entity entity)
    {
        this.entity = entity;
    }

    public BlockFragment getMaterial()
    {
        return material;
    }

    public void setMaterial(BlockFragment material)
    {
        this.material = material;
    }

    public float getYawHead()
    {
        return yawHead;
    }

    public void setYawHead(float yawHead)
    {
        this.yawHead = yawHead;
    }

    public float getPitchHead()
    {
        return pitchHead;
    }

    public void setPitchHead(float pitchHead)
    {
        this.pitchHead = pitchHead;
    }

    public float getSwing()
    {
        return swing;
    }

    public void setSwing(float swing)
    {
        this.swing = swing;
    }

    public float getStance()
    {
        return stance;
    }

    public void setStance(float stance)
    {
        this.stance = stance;
    }

    public void updateEntityRotations()
    {
        if (entity instanceof EntityLivingBase)
        {
            setRotations((EntityLivingBase) entity, yawHead, pitchHead, swing, stance);
        }
    }

    public NBTTagCompound createTagCompound()
    {
        NBTTagCompound compound = new NBTTagCompound();

        if (entity != null)
        {
            NBTTagCompound statueCompound = new NBTTagCompound();
            entity.writeToNBTOptional(statueCompound);
            compound.setTag("entity", statueCompound);
        }

        compound.setFloat("yawHead", yawHead);
        compound.setFloat("pitchHead", pitchHead);
        compound.setFloat("swing", swing);
        compound.setFloat("stance", stance);

        if (material != null)
        {
            compound.setString("block", Block.blockRegistry.getNameForObject(material.getBlock()));
            compound.setInteger("blockMetadata", material.getMetadata());
        }

        return compound;
    }

    public static class BlockFragment
    {
        private Block block;
        private int metadata;

        public BlockFragment(Block block, int metadata)
        {
            this.block = block;
            this.metadata = metadata;
        }

        public Block getBlock()
        {
            return block;
        }

        public int getMetadata()
        {
            return metadata;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BlockFragment that = (BlockFragment) o;

            if (metadata != that.metadata) return false;
            if (!block.equals(that.block)) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = block.hashCode();
            result = 31 * result + metadata;
            return result;
        }
    }
}
