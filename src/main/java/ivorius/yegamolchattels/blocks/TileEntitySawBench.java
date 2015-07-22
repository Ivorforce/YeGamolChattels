package ivorius.yegamolchattels.blocks;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.ivtoolkit.entities.IvEntityHelper;
import ivorius.ivtoolkit.network.ClientEventHandler;
import ivorius.ivtoolkit.network.IvNetworkHelperClient;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.ivtoolkit.tools.IvSideClient;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.gui.YGCGuiHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector2f;

/**
 * Created by lukas on 04.05.14.
 */
public class TileEntitySawBench extends IvTileEntityMultiBlock implements PartialUpdateHandler, ClientEventHandler
{
    public ItemStack containedItem;
    public static final int cutsPerLog = 4;

    public float sawPositionX;
    public float sawPositionY;
    public float woodCutY;
    public float woodCutScore;
    public int cutsLeft;
    public boolean isInWood;

    public boolean tryStoringItem(ItemStack stack, Entity entity)
    {
        if (stack != null && containedItem == null)
        {
            if (PlankSawRegistry.canSawItem(stack))
            {
                if (!worldObj.isRemote)
                {
                    if (stack.stackSize >= 2)
                    {
                        containedItem = stack.copy();
                        containedItem.stackSize = 2;
                        cutsLeft = cutsPerLog;
                        woodCutScore = 0.0f;
                        woodCutY = 0.0f;
                        calculateIsInWood();

                        stack.stackSize -= 2;

                        markDirty();
                        worldObj.markBlockForUpdate(getPos());
                    }
                    else
                    {
                        if (entity instanceof EntityPlayer)
                            ((EntityPlayer) entity).addChatMessage(new ChatComponentTranslation("tile.ygcSawBench.morewood"));
                    }
                }

                return true;
            }
        }

        return false;
    }

    public boolean tryEquippingItemOnPlayer(EntityPlayer entityLiving)
    {
        if (containedItem != null && cutsLeft == cutsPerLog)
        {
            if (!worldObj.isRemote)
            {
                if (IvEntityHelper.addAsCurrentItem(entityLiving, containedItem))
                {
                    containedItem = null;

                    markDirty();
                    worldObj.markBlockForUpdate(getPos());
                }
            }

            return true;
        }

        return false;
    }

//    @Override
//    public void updateEntityParent()
//    {
//        super.updateEntityParent();
//
//        float gravity = 0.02f;
//        float yPlus = isInWood ? Math.min(gravity, woodCutY - sawPositionY) : gravity;
//        moveSawInConstraints(0.0f, yPlus);
//    }

    public void chopOffWood(float score, EntityPlayer player, int usedItemIndex)
    {
        cutsLeft--;
        woodCutScore = 0.0f;
        woodCutY = 0.0f;
        calculateIsInWood();

        if (!worldObj.isRemote)
        {
            ItemStack usedItem = player.inventory.getStackInSlot(usedItemIndex);
            usedItem.damageItem(1, player);
            if (usedItem.stackSize <= 0)
                player.inventory.setInventorySlotContents(usedItemIndex, null);
            player.inventory.markDirty();

            float finalScore = score * score * 0.7f + score * 0.3f;
            int planks = MathHelper.floor_float(finalScore * 4.0f + 0.5f);

            if (planks > 0)
            {
                ItemStack planksStack = PlankSawRegistry.getSawResult(planks, containedItem);
                player.inventory.addItemStackToInventory(planksStack);
            }

            if (cutsLeft <= 0)
                containedItem = null;

            markDirty();
            worldObj.markBlockForUpdate(getPos());
        }
    }

    public float moveSaw(EntityPlayer player, float x, float y, int usedItemIndex)
    {
        if (containedItem != null && isInWood && y != 0.0f)
        {
            float safeY = Math.min(y, woodCutY - sawPositionY);
            float safeX = x / y * safeY;
            moveSawInConstraints(safeX, safeY);

            float leftX = x - safeX;
            float leftY = y - safeY;

            Vector2f movInWood = possibleMovement(leftX, leftY);

            float plusScore = 0.0f;
            if (movInWood.y > 0.0f)
            {
                float vecLength = MathHelper.sqrt_float(movInWood.x * movInWood.x + movInWood.y * movInWood.y);

                float nX = movInWood.x / vecLength;
                float sideMov = Math.abs(nX);
                float sideMovInf = sideMov * sideMov;
                sideMovInf = sideMovInf * sideMovInf * sideMovInf;

                moveSawInConstraints(movInWood.x * 0.5f, movInWood.y * sideMovInf);

                plusScore = movInWood.y * sideMovInf * sideMovInf * sideMovInf;
                woodCutScore += plusScore;

                if (sawPositionY > woodCutY && isInWood)
                    woodCutY = sawPositionY;
            }

            if (worldObj.isRemote)
                IvNetworkHelperClient.sendTileEntityEventPacket(this, "sawMove", YeGamolChattels.network);

            if (woodCutY >= 1.0f)
            {
                chopOffWood(woodCutScore, player, usedItemIndex);

                if (worldObj.isRemote)
                    IvNetworkHelperClient.sendTileEntityEventPacket(this, "woodChop", YeGamolChattels.network, usedItemIndex);
            }

            return plusScore;
        }
        else
        {
            moveSawInConstraints(x, y);

            if (containedItem !=  null && !isInWood)
                calculateIsInWood();

            if (worldObj.isRemote)
                IvNetworkHelperClient.sendTileEntityEventPacket(this, "sawMove", YeGamolChattels.network);
        }

        return 0.0f;
    }

    public Vector2f possibleMovement(float x, float y)
    {
        x = MathHelper.clamp_float(sawPositionX + x, -0.3f, 0.3f);
        y = MathHelper.clamp_float(sawPositionY + y, -0.2f, 1.2f);
        return new Vector2f(x - sawPositionX, y - sawPositionY);
    }

    public Vector2f moveSawInConstraints(float x, float y)
    {
        Vector2f possibleMovement = possibleMovement(x, y);

        sawPositionX = sawPositionX + possibleMovement.x;
        sawPositionY = sawPositionY + possibleMovement.y;

        return possibleMovement;
    }

    public void calculateIsInWood()
    {
        isInWood = sawPositionY <= 0.0f;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.readFromNBT(par1nbtTagCompound);

        containedItem = ItemStack.loadItemStackFromNBT(par1nbtTagCompound.getCompoundTag("containedItem"));

        sawPositionX = par1nbtTagCompound.getFloat("sawPositionX");
        sawPositionY = par1nbtTagCompound.getFloat("sawPositionY");
        woodCutY = par1nbtTagCompound.getFloat("woodCutY");
        woodCutScore = par1nbtTagCompound.getFloat("woodCutScore");
        cutsLeft = par1nbtTagCompound.getInteger("cutsLeft");
        isInWood = par1nbtTagCompound.getBoolean("isInWood");
    }

    @Override
    public void writeToNBT(NBTTagCompound par1nbtTagCompound)
    {
        super.writeToNBT(par1nbtTagCompound);

        if (containedItem != null)
        {
            NBTTagCompound itemNBT = new NBTTagCompound();
            containedItem.writeToNBT(itemNBT);
            par1nbtTagCompound.setTag("containedItem", itemNBT);
        }

        par1nbtTagCompound.setFloat("sawPositionX", sawPositionX);
        par1nbtTagCompound.setFloat("sawPositionY", sawPositionY);
        par1nbtTagCompound.setFloat("woodCutY", woodCutY);
        par1nbtTagCompound.setFloat("woodCutScore", woodCutScore);
        par1nbtTagCompound.setInteger("cutsLeft", cutsLeft);
        par1nbtTagCompound.setBoolean("isInWood", isInWood);
    }

    @Override
    public void writeUpdateData(ByteBuf buffer, String context, Object... params)
    {
        if ("sawOpenGui".equals(context))
        {
            buffer.writeFloat(sawPositionX);
            buffer.writeFloat(sawPositionY);
            buffer.writeFloat(woodCutY);
            buffer.writeFloat(woodCutScore);
            buffer.writeBoolean(isInWood);
        }
    }

    @Override
    public void readUpdateData(ByteBuf buffer, String context)
    {
        if ("sawOpenGui".equals(context))
        {
            sawPositionX = buffer.readFloat();
            sawPositionY = buffer.readFloat();
            woodCutY = buffer.readFloat();
            woodCutScore = buffer.readFloat();
            isInWood = buffer.readBoolean();

            IvSideClient.getClientPlayer().openGui(YeGamolChattels.instance, YGCGuiHandler.plankSawGuiID, worldObj, getPos());
        }
    }

    @Override
    public void assembleClientEvent(ByteBuf buffer, String context, Object... params)
    {
        if ("sawMove".equals(context))
        {
            buffer.writeFloat(sawPositionX);
            buffer.writeFloat(sawPositionY);
            buffer.writeFloat(woodCutY);
            buffer.writeFloat(woodCutScore);
            buffer.writeBoolean(isInWood);
        }
        else if ("woodChop".equals(context))
        {
            buffer.writeInt((Integer) params[0]);
        }
    }

    @Override
    public void onClientEvent(ByteBuf buffer, String context, EntityPlayerMP player)
    {
        if ("sawMove".equals(context))
        {
            sawPositionX = buffer.readFloat();
            sawPositionY = buffer.readFloat();
            woodCutY = buffer.readFloat();
            woodCutScore = buffer.readFloat();
            isInWood = buffer.readBoolean();
        }
        else if ("woodChop".equals(context))
        {
            int itemIndex = buffer.readInt();

            if (woodCutY >= 1.0f && isInWood && containedItem != null)
            {
                chopOffWood(woodCutScore, player, itemIndex);
            }
        }
    }
}
