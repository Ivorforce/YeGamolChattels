/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.math.IvMathHelper;
import ivorius.ivtoolkit.network.IvNetworkHelperServer;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.ivtoolkit.raytracing.IvRaytraceableObject;
import ivorius.ivtoolkit.raytracing.IvRaytracedIntersection;
import ivorius.yegamolchattels.YeGamolChattels;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class TileEntityItemShelfModel0 extends TileEntityItemShelf implements PartialUpdateHandler
{
    public static final int shelfJamien = 0;
    public static final int shelfWall = 1;
    public static final int shelfWardrobe = 2;

//    public static final String[] jamiensBook = new String[]{"Untitled", "Jamien", "Test Content.", "Moar pages", "Dis gon be good"};

    public boolean narniaActivating;
    public float narniaProgress;

    public TileEntityItemShelfModel0()
    {

    }

    public TileEntityItemShelfModel0(World world)
    {
        randomSeed = world.rand.nextInt(Integer.MAX_VALUE);
    }

    @Override
    public void updateEntityParent()
    {
        super.updateEntityParent();

//        if (YeGamolChattels.easterEggsAllowed && getShelfType() == shelfJamien && ticksAlive % 80 == 0)
//        {
//            for (int i = 0; i < this.getItemSlots(); i++)
//            {
//                if (this.storedItems[i] != null && this.storedItems[i].getItem() == Items.writable_book)
//                {
//                    tryAutocompletingBook(this.storedItems[i], jamiensBook, false);
//                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
//                }
//            }
//        }

        narniaProgress = IvMathHelper.nearValue(narniaProgress, narniaActivating ? 1.0f : 0.0f, 0.0f, 0.01f);

        if (worldObj.isRemote && getTriggerValue(0, 0.0f) > 0.5f)
        {
            int portalParticles = IvMathHelper.randomLinearNumber(worldObj.rand, narniaProgress * 1.5f);

            double[] center = getActiveCenterCoords();
            for (int i = 0; i < portalParticles; i++)
            {
                Vector3f frontDir = getRotatedVector(new Vector3f(0.0f, 0.0f, 1.0f));
                Vector3f sideDir = getRotatedVector(new Vector3f(1.0f, 0.0f, 0.0f));

                double xP = center[0] - 0.45 + sideDir.getX() * worldObj.rand.nextDouble() * 0.9;
                double yP = center[1] - 0.25 + worldObj.rand.nextDouble();
                double zP = center[2] - 0.45 + sideDir.getZ() * worldObj.rand.nextDouble() * 0.9;

                worldObj.spawnParticle("portal", xP, yP, zP, -frontDir.getX(), frontDir.getY(), -frontDir.getZ());
            }
        }
    }

    @Override
    public int getItemSlots()
    {
        int shelfType = this.getShelfType();

        if (shelfType == shelfJamien)
            return 5 * 2;
        if (shelfType == shelfWall)
            return 4;
        if (shelfType == shelfWardrobe)
            return 12;

        return 0;
    }

    @Override
    public List<IvRaytraceableObject> getRaytraceableObjects(float t)
    {
        ArrayList<IvRaytraceableObject> raytraceables = new ArrayList<IvRaytraceableObject>();
        raytraceables.addAll(getItemSlotBoxes(t));

        int shelfType = this.getShelfType();
        if (shelfType == shelfJamien)
        {
            raytraceables.add(getRotatedBox("WallBack", -1.0, -0.5, 0.4, 2.0, 1.0, 0.1));
            raytraceables.add(getRotatedBox("WallLeft", -0.85, -0.5, -0.3, 0.1, 1.0, 0.6));
            raytraceables.add(getRotatedBox("WallRight", 0.75, -0.5, -0.3, 0.1, 1.0, 0.6));
            raytraceables.add(getRotatedBox("WallTop", -1.0, 0.4, -0.3, 2.0, 0.1, 0.6));
            raytraceables.add(getRotatedBox("WallBottom", -1.0, -0.25, -0.3, 2.0, 0.1, 0.6));
        }
        else if (shelfType == shelfWall)
        {
            raytraceables.add(getRotatedBox("WallBottom", -0.5, -0.35, -0.1, 1.0, 0.1, 0.6));
        }
        else if (shelfType == shelfWardrobe)
        {
            raytraceables.add(getRotatedBox("WallBack", -0.5, -1.0, 0.4, 1.0, 2.0, 0.1));
            raytraceables.add(getRotatedBox("WallPortal", -0.5, -1.0, 0.39, 1.0, 2.0, 0.01));
            raytraceables.add(getRotatedBox("WallLeft", -0.5, -1.0, -0.1, 0.1, 2.0, 0.6));
            raytraceables.add(getRotatedBox("WallRight", 0.4, -1.0, -0.1, 0.1, 2.0, 0.6));
            raytraceables.add(getRotatedBox("WallTop", -0.5, 0.9, -0.1, 1.0, 0.1, 0.6));
            raytraceables.add(getRotatedBox("WallBottom", -0.5, -0.875, -0.125, 1.0, 0.125, 0.6));
            raytraceables.add(getRotatedBox("WallBottomDoors", -0.5, -0.375, -0.125, 1.0, 0.125, 0.6));

            raytraceables.add(getInterpolatedRotatedBox("Trigger-0", -0.5, -0.25, -0.125, 0.5, 1.125, 0.1, -0.5, -0.25, -0.5, 0.2, 1.125, 0.4, getTriggerValue(0, t)));
            raytraceables.add(getInterpolatedRotatedBox("Trigger-0", 0.0, -0.25, -0.125, 0.5, 1.125, 0.1, 0.3, -0.25, -0.5, 0.2, 1.125, 0.4, getTriggerValue(0, t)));

            for (int i = 0; i < 2; i++)
            {
                float triggerVal = getTriggerValue(1 + i, t);
                double sHeight = (i == 0) ? -0.5625 : -0.75;
                raytraceables.add(getInterpolatedRotatedBox("Trigger-" + (i + 1), -0.375, sHeight, -0.125, 0.75, 0.1875, 0.01, -0.375, sHeight, -0.5, 0.75, 0.1875, 0.01, triggerVal));

                raytraceables.add(getInterpolatedRotatedBox("DrawerBottom-" + (i + 1), -0.375, sHeight, -0.125, 0.75, 0.01, 0.5, -0.375, sHeight, -0.5, 0.75, 0.01, 0.5, triggerVal));
                raytraceables.add(getInterpolatedRotatedBox("DrawerLeft-" + (i + 1), -0.375, sHeight, -0.125, 0.01, 0.1875, 0.5, -0.375, sHeight, -0.5, 0.01, 0.1875, 0.5, triggerVal));
                raytraceables.add(getInterpolatedRotatedBox("DrawerRight-" + (i + 1), 0.374, sHeight, -0.125, 0.01, 0.1875, 0.5, 0.374, sHeight, -0.5, 0.01, 0.1875, 0.5, triggerVal));
            }
        }

        return raytraceables;
    }

    @Override
    public List<IvRaytraceableObject> getItemSlotBoxes(float t)
    {
        ArrayList<IvRaytraceableObject> raytraceables = new ArrayList<IvRaytraceableObject>();

        int shelfType = this.getShelfType();
        if (shelfType == shelfJamien)
        {
            int itemsPL = 5;
            float width = 1.4f;
            float paddingSides = 0.0f;

            for (int l = 0; l < 2; l++)
                for (int i = 0; i < itemsPL; i++)
                    raytraceables.add(getRotatedBox("Slot" + (i + l * itemsPL), -width / 2 + width / itemsPL * i + paddingSides, l == 0 ? -0.13f : 0.17f, -0.23f, width / itemsPL - paddingSides * 2, 0.18f, 0.25f));
        }
        else if (shelfType == shelfWall)
        {
            int itemsPL = 4;
            float width = 0.9f;
            float paddingSides = 0.0f;

            for (int i = 0; i < itemsPL; i++)
                raytraceables.add(getRotatedBox("Slot" + i, -width / 2 + width / itemsPL * i + paddingSides, -0.25f, 0.0f, width / itemsPL - paddingSides * 2, 0.2f, 0.2f));
        }
        else if (shelfType == shelfWardrobe)
        {
            for (int h = 0; h < 2; h++)
            {
                double shelfH = h == 0 ? -0.5625 : -0.75;
                float frac = getTriggerValue(h + 1, t);

                for (int i = 0; i < 2; i++)
                    for (int n = 0; n < 2; n++)
                        raytraceables.add(getInterpolatedRotatedBox("Slot" + (h * 4 + i * 2 + n), -0.22 + i * 0.24, shelfH, -0.125 + 0.01 + 0.2 * n, 0.2, 0.18, 0.2, -0.22 + i * 0.24, shelfH, -0.5 + 0.01 + 0.2 * n, 0.2, 0.18, 0.2, frac));

                raytraceables.add(getRotatedBox("Slot8", -0.36, 0.2, -0.1, 0.15, 0.6, 0.45));
                raytraceables.add(getRotatedBox("Slot9", -0.17, 0.2, -0.1, 0.15, 0.6, 0.45));
                raytraceables.add(getRotatedBox("Slot10", 0.02, 0.2, -0.1, 0.15, 0.6, 0.45));
                raytraceables.add(getRotatedBox("Slot11", 0.21, 0.2, -0.1, 0.15, 0.6, 0.45));
            }
        }

        return raytraceables;
    }

    @Override
    public boolean tryStoringItemInSlot(int slot, ItemStack stack)
    {
        if (stack != null && storedItems[slot] == null && slot < this.getItemSlots())
        {
            boolean canStore = true;

            if (this.getShelfType() == shelfWardrobe && (slot == 8 || slot == 9 || slot == 10 || slot == 11))
            {
                if (!(stack.getItem() instanceof ItemArmor && ((ItemArmor) stack.getItem()).armorType == 1))
                    canStore = false;
            }

            if (canStore)
            {
                if (!worldObj.isRemote)
                {
                    storeItemInSlot(stack, slot);

                    double[] center = getActiveCenterCoords();
                    worldObj.playSoundEffect(center[0], center[1], center[2], "dig.wood", 0.5f, 1.0f + worldObj.rand.nextFloat());
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public AxisAlignedBB getSpecialSelectedBB()
    {
        if (getShelfType() == shelfWall)
            return getRotatedBB(-0.5, -0.375, -0.125, 1.0, 0.4, 0.625);

        return null;
    }

    @Override
    public AxisAlignedBB getSpecialCollisionBB()
    {
        if (getShelfType() == shelfWall)
            return getRotatedBB(-0.5, -0.375, -0.125, 1.0, 0.125, 0.625);

        return null;
    }

    @Override
    public AxisAlignedBB getSpecialBlockBB()
    {
        if (getShelfType() == shelfWall)
            return getRotatedBB(-0.5, -0.375, -0.125, 1.0, 0.4, 0.625);

        return null;
    }

    public static List<int[]> getPositionsForType(int shelfType)
    {
        ArrayList<int[]> positions = new ArrayList<int[]>();

        if (shelfType == shelfJamien)
        {
            positions.add(new int[]{0, 0, 0});
            positions.add(new int[]{1, 0, 0});
        }
        else if (shelfType == shelfWall)
        {
            positions.add(new int[]{0, 0, 0});
        }
        else if (shelfType == shelfWardrobe)
        {
            positions.add(new int[]{0, 0, 0});
            positions.add(new int[]{0, 1, 0});
        }
        else
            positions.add(new int[]{0, 0, 0});

        return positions;
    }

    public static boolean tryAutocompletingBook(ItemStack stack, String[] book, boolean sign)
    {
        if (stack != null && stack.getItem() == Items.writable_book)
        {
            if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("pages"))
                stack.setTagInfo("pages", new NBTTagList());

            NBTTagCompound var4 = stack.getTagCompound();
            NBTTagList pages = var4.getTagList("pages", Constants.NBT.TAG_STRING);

            if (pages.tagCount() <= book.length - 2)
            {
                for (int n = 0; n < pages.tagCount(); n++)
                {
                    String data = pages.getStringTagAt(n);
                    if (book[n + 2].startsWith(data))
                    {
                        if (book[n + 2].length() > data.length())
                        {
                            data = data + book[n + 2].charAt(data.length());
                            pages.removeTag(n);
                            pages.appendTag(new NBTTagString(data));

                            return true;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }

                if (pages.tagCount() < book.length - 2)
                {
                    pages.appendTag(new NBTTagString("" + pages.tagCount() + 1));
                    return true;
                }
                else
                {
                    if (sign)
                    {
                        stack.setTagInfo("author", new NBTTagString(book[1]));
                        stack.setTagInfo("title", new NBTTagString(book[0]));
                        stack.func_150996_a(Items.written_book); // Set item
                    }

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void activateTrigger(int trigger)
    {
        this.shelfTriggers[trigger] = !this.shelfTriggers[trigger];
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

        if (this.shelfTriggers[trigger])
            worldObj.playSoundEffect(xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, "random.chestopen", 0.3f, 1.6f);
        else
            worldObj.playSoundEffect(xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, "random.chestclosed", 0.3f, 1.6f);
    }

    @Override
    public boolean handleRightClickOnIntersection(EntityPlayer player, ItemStack stack, int side, IvRaytracedIntersection intersection)
    {
        if (getShelfType() == shelfWardrobe)
        {
            if (YeGamolChattels.easterEggsAllowed && "WallPortal".equals(intersection.getUserInfo()))
            {
                if (stack != null && stack.getItem() == Items.ender_pearl)
                {
                    if (!worldObj.isRemote)
                    {
                        player.inventory.mainInventory[player.inventory.currentItem].stackSize--;
                        if (player.inventory.mainInventory[player.inventory.currentItem].stackSize <= 0)
                            player.inventory.mainInventory[player.inventory.currentItem] = null;

                        narniaActivating = !narniaActivating;
                        IvNetworkHelperServer.sendTileEntityUpdatePacket(this, "narniaProgress", YeGamolChattels.network);
                    }

                    return true;
                }
                else if (narniaProgress > 0.8f)
                {
                    if (!worldObj.isRemote)
                        player.addChatMessage(new ChatComponentText("You don't fit through!"));

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.readFromNBT(par1nbtTagCompound);

        narniaActivating = par1nbtTagCompound.getBoolean("narniaActivating");
        narniaProgress = par1nbtTagCompound.getFloat("narniaProgress");
    }

    @Override
    public void writeToNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.writeToNBT(par1nbtTagCompound);

        par1nbtTagCompound.setBoolean("narniaActivating", narniaActivating);
        par1nbtTagCompound.setFloat("narniaProgress", narniaProgress);
    }

    @Override
    public void writeUpdateData(ByteBuf buffer, String context)
    {
        if ("narniaProgress".equals(context))
        {
            buffer.writeBoolean(narniaActivating);
            buffer.writeFloat(narniaProgress);
        }
    }

    @Override
    public void readUpdateData(ByteBuf buffer, String context)
    {
        if ("narniaProgress".equals(context))
        {
            narniaActivating = buffer.readBoolean();
            narniaProgress = buffer.readFloat();
        }
    }
}
