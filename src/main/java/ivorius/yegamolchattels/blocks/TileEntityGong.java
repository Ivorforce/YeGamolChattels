/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.ivtoolkit.network.IvNetworkHelperServer;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.ivtoolkit.raytracing.IvRaytraceableObject;
import ivorius.ivtoolkit.raytracing.IvRaytracedIntersection;
import ivorius.ivtoolkit.raytracing.IvRaytracerMC;
import ivorius.yegamolchattels.YGCConfig;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.achievements.YGCAchievementList;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class TileEntityGong extends IvTileEntityMultiBlock implements PartialUpdateHandler
{
    public int vibrationStrength;
    public int gongType;
    public int madnessTimer = -1;

    @Override
    public void updateEntityParent()
    {
        super.updateEntityParent();

        if (vibrationStrength > 0)
            vibrationStrength--;

        if (madnessTimer > 0)
        {
            madnessTimer--;
            vibrationStrength = 200;

            if (!worldObj.isRemote)
            {
                IChatComponent sendComponent = null;

                if (madnessTimer == 260)
                    sendComponent = new ChatComponentText("Attempting rescue using GONG-" + EnumChatFormatting.OBFUSCATED + "Engine-12930" + EnumChatFormatting.RESET + "! ");
                if (madnessTimer == 230)
                    sendComponent = new ChatComponentText("GONG " + EnumChatFormatting.OBFUSCATED + "RESCUE STEP 1" + EnumChatFormatting.RESET + "! ");
                if (madnessTimer == 200)
                    sendComponent = new ChatComponentText("GONG " + EnumChatFormatting.OBFUSCATED + "THE CAKE IS A LIE" + EnumChatFormatting.RESET + "! ");
                if (madnessTimer == 180)
                    sendComponent = new ChatComponentText("Re" + EnumChatFormatting.OBFUSCATED + "sc" + EnumChatFormatting.RESET + "ue failed! Ivorius i" + EnumChatFormatting.OBFUSCATED + "s incred" + EnumChatFormatting.RESET + "ibly sorry!");
                if (madnessTimer == 144)
                    sendComponent = new ChatComponentText("Initia" + EnumChatFormatting.OBFUSCATED + "ting se" + EnumChatFormatting.RESET + "lf-destruct! Executing in 7.4" + EnumChatFormatting.OBFUSCATED + "32 se" + EnumChatFormatting.RESET + "conds... " + EnumChatFormatting.OBFUSCATED + "GongGongGong" + EnumChatFormatting.RESET + "");
                if (madnessTimer == 60)
                    sendComponent = new ChatComponentText("3...! " + EnumChatFormatting.OBFUSCATED + "GongGongGong" + EnumChatFormatting.RESET + "");
                if (madnessTimer == 40)
                    sendComponent = new ChatComponentText("2." + EnumChatFormatting.OBFUSCATED + ".." + EnumChatFormatting.RESET + "! ");
                if (madnessTimer == 20)
                    sendComponent = new ChatComponentText("" + EnumChatFormatting.OBFUSCATED + "1.." + EnumChatFormatting.RESET + ". Expl" + EnumChatFormatting.OBFUSCATED + "osion imm" + EnumChatFormatting.RESET + "inent! ");

                if (sendComponent != null)
                    MinecraftServer.getServer().getConfigurationManager().sendChatMsg(sendComponent);

                if (worldObj.rand.nextInt(madnessTimer / 10 + 5) == 0)
                {
                    hitGong(new ItemStack(YGCItems.mallet), worldObj.rand.nextFloat() * this.getBlockMetadata());
                }
            }
            else
            {
                for (int i = 0; i < (20 - madnessTimer / 10); i++)
                {
                    float offsetX = worldObj.rand.nextFloat() * 10.0f - 5.0f;
                    float offsetY = worldObj.rand.nextFloat() * 10.0f - 5.0f;
                    float offsetZ = worldObj.rand.nextFloat() * 10.0f - 5.0f;

                    int gongSize = getBlockMetadata();

                    double[] center = getActiveCenterCoords();
                    double startX = center[0] + worldObj.rand.nextFloat() * gongSize - gongSize * 0.5f;
                    double startY = center[1] + worldObj.rand.nextFloat() * gongSize - gongSize * 0.5f - 0.5f; //Don't ask me
                    double startZ = center[2] + worldObj.rand.nextFloat() * gongSize - gongSize * 0.5f;

                    getWorldObj().spawnParticle("portal", startX + offsetX, startY + offsetY, startZ + offsetZ, -offsetX, -offsetY, -offsetZ);
                }
            }
        }
        else if (madnessTimer == 0 && !worldObj.isRemote)
        {
            worldObj.createExplosion(null, xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, 2.0f, true);
            madnessTimer = -1;
        }
    }

    public boolean hitGong(ItemStack stack, Entity entity)
    {
        List<IvRaytraceableObject> raytraceables = getGongDistanceRaytraceables();
        IvRaytracedIntersection intersection = IvRaytracerMC.getFirstIntersection(raytraceables, entity);

        if (intersection != null)
        {
            Double info = (Double) intersection.getUserInfo();

            if (entity instanceof EntityPlayer)
            {
                if (isValidHitItem(stack))
                {
                    switch (getBlockMetadata())
                    {
                        case 0:
                            ((EntityPlayer) entity).triggerAchievement(YGCAchievementList.smallGongPlayed);
                            break;
                        case 1:
                            ((EntityPlayer) entity).triggerAchievement(YGCAchievementList.mediumGongPlayed);
                            break;
                        case 2:
                            ((EntityPlayer) entity).triggerAchievement(YGCAchievementList.largeGongPlayed);
                            break;
                    }
                }
                else if (stack != null && stack.getItem() == Items.ender_pearl)
                {
                    ((EntityPlayer) entity).triggerAchievement(YGCAchievementList.gongSecret);
                }
            }

            return hitGong(stack, info.floatValue());
        }

        return false;
    }

    public boolean hitGong(ItemStack stack, float distanceToCenter)
    {
        if (!isParent())
        {
            TileEntityGong parent = (TileEntityGong) getParent();

            if (parent != null)
                parent.hitGong(stack, distanceToCenter);
        }
        else
        {
            int gongSize = getBlockMetadata();

            String sound = YeGamolChattels.soundBase + "gong";
            if (this.madnessTimer > 0)
                distanceToCenter = worldObj.rand.nextFloat() * 10.0f - 1.0f;
            float pitch = 0.8f + distanceToCenter * 0.4f;

            boolean failedHit = !isValidHitItem(stack);

            if (failedHit)
            {
                sound = YeGamolChattels.soundBase + "gongWrong";
                pitch = 1.0f + distanceToCenter * 0.4f - gongSize * 0.4f;
            }
            else
            {
                if (gongSize == 0)
                    sound = YeGamolChattels.soundBase + "gongSmall";
                if (gongSize == 2)
                    sound = YeGamolChattels.soundBase + "gongLarge";
            }

            worldObj.playSoundEffect(xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, sound, 2.0f + gongSize * 4.0f, pitch);

            if (!failedHit)
                vibrationStrength = 100;
            else
                vibrationStrength = 50;

            if (stack != null && stack.getItem() == Items.ender_pearl && YGCConfig.easterEggsAllowed)
            {
                madnessTimer = 320;

                if (!worldObj.isRemote)
                    MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("Oh no! Something has gong wrong! " + EnumChatFormatting.OBFUSCATED + "GongGongGong" + EnumChatFormatting.RESET + ""));
            }

            IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "vibrationData", YeGamolChattels.network);
            markDirty();
        }

        return true;
    }

    public List<IvRaytraceableObject> getGongDistanceRaytraceables()
    {
        ArrayList<IvRaytraceableObject> raytraceables = new ArrayList<IvRaytraceableObject>();

        double gSize = getBlockMetadata() + 1;
        int steps = MathHelper.ceiling_double_int(20 * gSize);

        for (int i = 0; i < steps; i++)
        {
            double ratio = (double) (i + 1) / (double) steps;
            double width = (1.0 - ratio) * 0.2 + 0.2;
            double stepSizeH = ratio * gSize * 0.5;

            raytraceables.add(getRotatedBox((double) i / (double) steps * gSize * 0.5, -stepSizeH, -stepSizeH, -width * 0.5, stepSizeH * 2, stepSizeH * 2, width));
        }

        return raytraceables;
    }

    public boolean isValidHitItem(ItemStack stack)
    {
        return stack != null && stack.getItem() == YGCItems.mallet;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.readFromNBT(par1nbtTagCompound);

        vibrationStrength = par1nbtTagCompound.getInteger("vibrationStrength");
        gongType = par1nbtTagCompound.getInteger("gongType");

        madnessTimer = par1nbtTagCompound.getInteger("madnessTimer");
    }

    @Override
    public void writeToNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.writeToNBT(par1nbtTagCompound);

        par1nbtTagCompound.setInteger("vibrationStrength", vibrationStrength);
        par1nbtTagCompound.setInteger("gongType", gongType);

        par1nbtTagCompound.setInteger("madnessTimer", madnessTimer);
    }

    @Override
    public void writeUpdateData(ByteBuf buffer, String context)
    {
        if ("vibrationData".equals(context))
        {
            buffer.writeInt(vibrationStrength);
            buffer.writeInt(madnessTimer);
        }
    }

    @Override
    public void readUpdateData(ByteBuf buffer, String context)
    {
        if ("vibrationData".equals(context))
        {
            vibrationStrength = buffer.readInt();
            madnessTimer = buffer.readInt();
        }
    }
}
