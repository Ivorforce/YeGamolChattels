/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.entities;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * Created by lukas on 28.07.14.
 */
public class EntityFakePlayer extends EntityMob
{
    private GameProfile playerProfile;

    @SideOnly(Side.CLIENT)
    private ResourceLocation locationSkin;
    @SideOnly(Side.CLIENT)
    private ResourceLocation locationCape;
    @SideOnly(Side.CLIENT)
    private CallbackHandler callbackHandler;

    public EntityFakePlayer(World world)
    {
        super(world);

        this.setSize(0.6F, 1.8F);
    }

    public EntityFakePlayer(World par1World, GameProfile playerProfile)
    {
        super(par1World);

        this.playerProfile = playerProfile;

        if (worldObj.isRemote)
            setupCustomSkin();
    }

    public String getPlayerUsername()
    {
        return playerProfile != null ? playerProfile.getName() : null;
    }

    public GameProfile getPlayerProfile()
    {
        return playerProfile;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);

        if (this.playerProfile != null)
        {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            NBTUtil.func_152460_a(nbttagcompound1, this.playerProfile);
            tagCompound.setTag("PlayerProfile", nbttagcompound1);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound)
    {
        super.readEntityFromNBT(tagCompound);

        if (tagCompound.hasKey("PlayerProfile", Constants.NBT.TAG_COMPOUND))
        {
            this.playerProfile = NBTUtil.func_152459_a(tagCompound.getCompoundTag("PlayerProfile"));
        }

        if (worldObj != null && worldObj.isRemote)
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

    @SideOnly(Side.CLIENT)
    private void setupCustomSkin()
    {
        String s = getPlayerUsername();

        if (s != null && !s.isEmpty())
        {
            SkinManager skinmanager = Minecraft.getMinecraft().func_152342_ad();
            skinmanager.func_152790_a(playerProfile, callbackHandler, true);
        }
    }

    public static GameProfile createGameProfile(String playerUsername)
    {
        GameProfile gameprofile = MinecraftServer.getServer().func_152358_ax().func_152655_a(playerUsername);

        if (gameprofile != null)
        {
            Property property = (Property) Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object) null);

            if (property == null)
            {
                gameprofile = MinecraftServer.getServer().func_147130_as().fillProfileProperties(gameprofile, true);
            }

            return gameprofile;
        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getLocationSkin()
    {
        return this.locationSkin != null ? this.locationSkin : AbstractClientPlayer.locationStevePng;
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getLocationCape()
    {
        return this.locationCape;
    }

    @SideOnly(Side.CLIENT)
    public static class CallbackHandler implements SkinManager.SkinAvailableCallback
    {
        private EntityFakePlayer player;

        public CallbackHandler(EntityFakePlayer player)
        {
            this.player = player;
        }

        @Override
        public void func_152121_a(MinecraftProfileTexture.Type type, ResourceLocation resourceLocation)
        {
            switch (type)
            {
                case CAPE:
                    this.player.locationCape = resourceLocation;
                case SKIN:
                    this.player.locationSkin = resourceLocation;
            }
        }
    }
}
