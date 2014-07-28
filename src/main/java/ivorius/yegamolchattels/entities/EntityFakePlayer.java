/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.entities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;

/**
 * Created by lukas on 28.07.14.
 */
public class EntityFakePlayer extends EntityMob
{
    private String playerUsername;

    @SideOnly(Side.CLIENT)
    private ThreadDownloadImageData downloadImageSkin;
    @SideOnly(Side.CLIENT)
    private ThreadDownloadImageData downloadImageCape;
    @SideOnly(Side.CLIENT)
    private ResourceLocation locationSkin;
    @SideOnly(Side.CLIENT)
    private ResourceLocation locationCape;

    public EntityFakePlayer(World world)
    {
        super(world);

        this.setSize(0.6F, 1.8F);
    }

    public EntityFakePlayer(World par1World, String playerUsername)
    {
        super(par1World);
        this.playerUsername = playerUsername;

        if (worldObj.isRemote)
            setupCustomSkin();
    }

    @SideOnly(Side.CLIENT)
    private void setupCustomSkin()
    {
        String s = getPlayerUsername();

        if (!s.isEmpty())
        {
            this.locationSkin = AbstractClientPlayer.getLocationSkin(s);
            this.locationCape = AbstractClientPlayer.getLocationCape(s);
            this.downloadImageSkin = AbstractClientPlayer.getDownloadImageSkin(this.locationSkin, s);
            this.downloadImageCape = AbstractClientPlayer.getDownloadImageCape(this.locationCape, s);
        }
    }

    @SideOnly(Side.CLIENT)
    public ThreadDownloadImageData getTextureSkin()
    {
        return this.downloadImageSkin;
    }

    @SideOnly(Side.CLIENT)
    public ThreadDownloadImageData getTextureCape()
    {
        return this.downloadImageCape;
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getLocationSkin()
    {
        return this.locationSkin;
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getLocationCape()
    {
        return this.locationCape;
    }

    public String getPlayerUsername()
    {
        return playerUsername;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);

        tagCompound.setString("playerUsername", playerUsername);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound)
    {
        super.readEntityFromNBT(tagCompound);

        playerUsername = tagCompound.getString("playerUsername");

        if (worldObj.isRemote)
            setupCustomSkin();
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();

        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
    }
}
