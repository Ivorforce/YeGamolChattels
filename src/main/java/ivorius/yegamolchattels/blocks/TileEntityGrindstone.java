/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.IvTileEntityHelper;
import ivorius.ivtoolkit.network.IvNetworkHelperServer;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

public class TileEntityGrindstone extends TileEntity implements PartialUpdateHandler
{
    public static final int maxGrindstoneHealth = 30;

    public int grindstoneHealth;
    public int grindstoneRotationSpeed;
    public int crankRotationTime;
    public int timeSharpening;

    public int ticksTillSound;
    public int grindstoneRotationVisual;
    public float crankRotationVisual;

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (this.grindstoneRotationSpeed > 0)
            this.grindstoneRotationSpeed--;

        if (grindstoneHealth == 0)
            grindstoneRotationSpeed = 0;

        grindstoneRotationVisual = (grindstoneRotationVisual + grindstoneRotationSpeed) % 40000;

        if (crankRotationTime > 0)
        {
            crankRotationTime--;
            crankRotationVisual += 0.4f;
        }

        if (!getWorldObj().isRemote && grindstoneRotationSpeed > 150 && ticksTillSound == 0)
        {
            worldObj.playSoundEffect(xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, "minecart.inside", 0.25f, 0.5f);
            ticksTillSound = 100;
        }

        if (ticksTillSound > 0)
            ticksTillSound--;

        if (timeSharpening > 0)
        {
            timeSharpening--;

            Random rand = getWorldObj().rand;
            if (!getWorldObj().isRemote && rand.nextFloat() < 0.01f)
            {
                ItemStack sandStack = new ItemStack(Blocks.sand);
                EntityItem sandItem = new EntityItem(getWorldObj(), xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, sandStack);
                getWorldObj().spawnEntityInWorld(sandItem);
            }

            getWorldObj().spawnParticle("blockcrack_" + Block.getIdFromBlock(Blocks.sand) + "_0", xCoord + 0.5f, yCoord + 0.7f, zCoord + 0.5f, rand.nextFloat() * 0.2f - 0.1f, 0.1f, rand.nextFloat() * 0.2f - 0.1f);
        }
    }

    public boolean tryApplyingItem(ItemStack stack, EntityLivingBase entity)
    {
        if (stack != null && stack.getItem() == YGCItems.grindstoneStone)
        {
            if (grindstoneHealth == 0)
            {
                if (!worldObj.isRemote)
                {
                    this.grindstoneHealth = maxGrindstoneHealth;
                    stack.stackSize--;

                    IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "grindstoneData", YeGamolChattels.network);
                    markDirty();
                }

                return true;
            }
        }

        return false;
    }

    public boolean tryRepairingItem(ItemStack stack, EntityLivingBase entity)
    {
        if (stack != null)
        {
            Item item = stack.getItem();

            if (grindstoneRotationSpeed > 60 && timeSharpening == 0)
            {
                boolean isRepairable = false;

                if (item instanceof ItemSword)
                {
                    String toolMaterialName = ((ItemSword) item).getToolMaterialName();

                    if (toolMaterialName.equals("STONE") || toolMaterialName.equals("GOLD") || toolMaterialName.equals("IRON"))
                        isRepairable = true;
                }
                if (item instanceof ItemTool)
                {
                    String toolMaterialName = ((ItemTool) item).getToolMaterialName();

                    if (toolMaterialName.equals("STONE") || toolMaterialName.equals("GOLD") || toolMaterialName.equals("IRON"))
                        isRepairable = true;
                }

                if (isRepairable)
                {
                    int currentItemRepairs = getCurrentRepairs(stack);

                    float itemSurvivalChance = currentItemRepairs > 10 ? (1.0f / (1.0f + currentItemRepairs * 0.02f)) : 1;
                    if (!getWorldObj().isRemote && getWorldObj().rand.nextFloat() > itemSurvivalChance)
                    {
                        stack.stackSize--;
                        if (entity != null)
                            entity.renderBrokenItemStack(stack);
                    }
                    else
                    {
                        currentItemRepairs++;
                        setCurrentRepairs(stack, currentItemRepairs);
                    }

                    stack.setItemDamage(Math.max(stack.getItemDamage() - 10, 0));

                    grindstoneHealth -= 1;

                    float chanceToSurvive = grindstoneHealth * 0.2f;

                    if (!getWorldObj().isRemote && getWorldObj().rand.nextFloat() > chanceToSurvive)
                    {
                        for (int i = 0; i < 4; i++)
                        {
                            ItemStack sandStack = new ItemStack(Blocks.sand);
                            EntityItem sandItem = new EntityItem(getWorldObj(), xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, sandStack);
                            getWorldObj().spawnEntityInWorld(sandItem);
                        }

                        grindstoneHealth = 0;
                    }
                    else
                    {
                        timeSharpening = 30;
                    }

                    IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "grindstoneData", YeGamolChattels.network);
                    markDirty();

                    return true;
                }
            }
        }

        return false;
    }

    public void increaseGrindstoneRotation()
    {
        if (!worldObj.isRemote)
        {
            if (grindstoneHealth > 0)
            {
                grindstoneRotationSpeed += 100;

                if (grindstoneRotationSpeed > 1000)
                    grindstoneRotationSpeed = 1000;
            }

            crankRotationTime += 10;

            if (crankRotationTime > 30)
                crankRotationTime = 30;

            IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "grindstoneData", YeGamolChattels.network);
            markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.readFromNBT(par1nbtTagCompound);

        grindstoneHealth = par1nbtTagCompound.getInteger("grindstoneHealth");
        grindstoneRotationSpeed = par1nbtTagCompound.getInteger("rotationSpeed");
        crankRotationTime = par1nbtTagCompound.getInteger("crankRotationTime");
        timeSharpening = par1nbtTagCompound.getInteger("timeSharpening");
    }

    @Override
    public void writeToNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.writeToNBT(par1nbtTagCompound);

        par1nbtTagCompound.setInteger("grindstoneHealth", grindstoneHealth);
        par1nbtTagCompound.setInteger("rotationSpeed", grindstoneRotationSpeed);
        par1nbtTagCompound.setInteger("crankRotationTime", crankRotationTime);
        par1nbtTagCompound.setInteger("timeSharpening", timeSharpening);
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
    public void writeUpdateData(ByteBuf buffer, String context)
    {
        if ("grindstoneData".equals(context))
        {
            buffer.writeInt(grindstoneHealth);
            buffer.writeInt(grindstoneRotationSpeed);
            buffer.writeInt(crankRotationTime);
            buffer.writeInt(timeSharpening);
        }
    }

    @Override
    public void readUpdateData(ByteBuf buffer, String context)
    {
        if ("grindstoneData".equals(context))
        {
            grindstoneHealth = buffer.readInt();
            grindstoneRotationSpeed = buffer.readInt();
            crankRotationTime = buffer.readInt();
            timeSharpening = buffer.readInt();
        }
    }

    public static int getCurrentRepairs(ItemStack stack)
    {
        NBTTagCompound itemTagCompound = stack.getTagCompound();
        if (itemTagCompound != null && itemTagCompound.hasKey("grindstoneRepairs"))
            return itemTagCompound.getInteger("grindstoneRepairs");

        return 0;
    }

    public static void setCurrentRepairs(ItemStack stack, int repairs)
    {
        stack.setTagInfo("grindstoneRepairs", new NBTTagInt(repairs));
    }
}
