/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityGhost extends EntityMob
{
    public EntityGhost(World world)
    {
        super(world);

        setAIMoveSpeed(0.01F);

        noClip = true;

        findNewDestination();
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();

        this.dataWatcher.addObject(15, new Float(0.0f));
        this.dataWatcher.addObject(16, new Float(0.0f));
        this.dataWatcher.addObject(17, new Float(0.0f));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.01F);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5.0f);
    }

    @Override
    public float getAIMoveSpeed()
    {
        return 0.01f;
    }

    @Override
    public void onLivingUpdate()
    {
        if (worldObj.isDaytime() && !worldObj.isRemote)
        {
            float f = getBrightness(1.0F);

            if (f > 0.5F && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && rand.nextFloat() * 30F < (f - 0.4F) * 2.0F)
            {
                setFire(8);
            }
        }

        float moveSpeed = getAIMoveSpeed();

        if (entityToAttack != null)
        {
            double distX = (entityToAttack.posX - posX);
            double distY = (entityToAttack.posY - posY);
            double distZ = (entityToAttack.posZ - posZ);

            double all = MathHelper.sqrt_double(distX * distX + distY * distY + distZ * distZ);

            motionX += distX / all * moveSpeed;
            motionY += distY / all * moveSpeed;
            motionZ += distZ / all * moveSpeed;
        }
        else
        {
            double distX = (getCurrentDestX() - posX);
            double distY = (getCurrentDestY() - posY);
            double distZ = (getCurrentDestZ() - posZ);

            double all = MathHelper.sqrt_double(distX * distX + distY * distY + distZ * distZ);

            motionX += distX / all * moveSpeed;
            motionY += distY / all * moveSpeed;
            motionZ += distZ / all * moveSpeed;

            if (all < 5.0 || all > 300.0)
            {
                findNewDestination();
            }
        }

        double max = 1.0F;
        if (motionX > max)
            motionX = max;
        if (motionX < -max)
            motionX = -max;
        if (motionY > max)
            motionY = max;
        if (motionY < -max)
            motionY = -max;
        if (motionZ > max)
            motionZ = max;
        if (motionZ < -max)
            motionZ = -max;

        motionX *= 0.9;
        motionY *= 0.9;
        motionZ *= 0.9;

        super.onLivingUpdate();

        faceForward();
    }

    public void findNewDestination()
    {
        setCurrentDestX((float) posX + (rand.nextFloat() - 0.5F) * 50.0F);
        setCurrentDestY(((((float) posY + (rand.nextFloat() - 0.5F) * 50.0F) - 70F) * 0.9F) + 70F);
        setCurrentDestZ((float) posZ + (rand.nextFloat() - 0.5F) * 50.0F);
    }

    public void faceForward()
    {
        double d0 = getCurrentDestX() - posX;
        double d2 = getCurrentDestY() - posY;
        double d1 = getCurrentDestZ() - posZ;

        double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float f2 = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float) (-(Math.atan2(d1, d3) * 180.0D / Math.PI));
        this.rotationPitch = this.updateRotation(this.rotationPitch, f3, 10.0f);
        this.rotationYaw = this.updateRotation(this.rotationYaw, f2, 20.0f);
    }

    private float updateRotation(float par1, float par2, float par3)
    {
        float f3 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (f3 > par3)
        {
            f3 = par3;
        }

        if (f3 < -par3)
        {
            f3 = -par3;
        }

        return par1 + f3;
    }

    @Override
    protected String getHurtSound()
    {
        return "mob.creeper";
    }

    @Override
    protected String getDeathSound()
    {
        return "mob.creeperdeath";
    }

    @Override
    protected Item getDropItem()
    {
        return Item.getItemFromBlock(Blocks.wool);
    }

    @Override
    protected void fall(float f)
    {
    }

    @Override
    public void knockBack(Entity entity, float i, double d, double d1)
    {
        motionX /= 2D;
        motionY /= 2D;
        motionZ /= 2D;

        double distX = (entity.posX - posX);
        double distY = ((entity.posY - 1) - posY);
        double distZ = (entity.posZ - posZ);

        double all = distX * distX + distY * distY + distZ * distZ;

        motionX -= distX / all;
        motionY -= distY / all;
        motionZ -= distZ / all;
    }

    @Override
    public void moveEntityWithHeading(float f, float f1)
    {
        moveEntity(motionX, motionY, motionZ);
    }

    @Override
    public boolean isOnLadder()
    {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i)
    {
        if (damagesource == DamageSource.inWall)
            return false;

        return super.attackEntityFrom(damagesource, i);
    }

    public float getCurrentDestX()
    {
        return this.getDataWatcher().getWatchableObjectFloat(15);
    }

    public float getCurrentDestY()
    {
        return this.getDataWatcher().getWatchableObjectFloat(16);
    }

    public float getCurrentDestZ()
    {
        return this.getDataWatcher().getWatchableObjectFloat(17);
    }

    public void setCurrentDestX(float destX)
    {
        this.getDataWatcher().updateObject(15, new Float(destX));
    }

    public void setCurrentDestY(float destY)
    {
        this.getDataWatcher().updateObject(16, new Float(destY));
    }

    public void setCurrentDestZ(float destZ)
    {
        this.getDataWatcher().updateObject(17, new Float(destZ));
    }
}
