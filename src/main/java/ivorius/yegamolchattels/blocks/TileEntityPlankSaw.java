package ivorius.yegamolchattels.blocks;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.ivtoolkit.entities.IvEntityHelper;
import ivorius.ivtoolkit.network.PacketTileEntityClientEvent;
import ivorius.ivtoolkit.network.PartialUpdateHandler;
import ivorius.ivtoolkit.tools.IvSideClient;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.gui.YGCGuiHandler;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

/**
 * Created by lukas on 04.05.14.
 */
public class TileEntityPlankSaw extends IvTileEntityMultiBlock implements PartialUpdateHandler
{
    public ItemStack containedItem;
    public static final int cutsPerLog = 6;

    public int sawingPlayerID;
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
            Item item = stack.getItem();

            if (item == Item.getItemFromBlock(Blocks.log) || item == Item.getItemFromBlock(Blocks.log2))
            {
                if (!worldObj.isRemote)
                {
                    containedItem = stack.copy();
                    containedItem.stackSize = 1;
                    cutsLeft = cutsPerLog;
                    woodCutScore = 0.0f;
                    woodCutY = 0.0f;
                    calculateIsInWood();

                    stack.stackSize--;

                    markDirty();
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void updateEntityParent()
    {
        super.updateEntityParent();

//        float gravity = 0.02f;
//        float yPlus = isInWood ? Math.min(gravity, woodCutY - sawPositionY) : gravity;
//        moveSawInConstraints(0.0f, yPlus);
    }

    public void chopOffWood(float score, EntityPlayer player)
    {
        cutsLeft--;
        woodCutScore = 0.0f;
        woodCutY = 0.0f;
        calculateIsInWood();

        if (!worldObj.isRemote)
        {
            float finalScore = score * score * 0.7f + score * 0.3f;
            int planks = MathHelper.floor_float(finalScore * 6.0f + 0.5f);

            if (planks > 0)
            {
                int damage = 0;
                if (containedItem.getItem() == Item.getItemFromBlock(Blocks.log))
                    damage = containedItem.getItemDamage();
                else if (containedItem.getItem() == Item.getItemFromBlock(Blocks.log2))
                    damage = containedItem.getItemDamage() + 4;

                ItemStack planksStack = new ItemStack(YGCItems.plank, planks, damage);
                player.inventory.addItemStackToInventory(planksStack);
            }

            if (cutsLeft <= 0)
            {
                containedItem = null;
            }

            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public float moveSaw(EntityPlayer player, float x, float y)
    {
        sawingPlayerID = player.getEntityId();

        if (containedItem != null)
        {
            float plusScore = 0.0f;
            float yPlus = isInWood ? Math.min(y, woodCutY - sawPositionY) : y;

            moveSawInConstraints(isInWood ? x * 0.5f : x, yPlus);

            float yPlusLeft = y - yPlus;
            if (yPlusLeft > 0.0f && isInWood)
            {
                float vecLength = MathHelper.sqrt_float(x * x + y * y);

                if (vecLength > 0.0f)
                {
                    float nX = x / vecLength;
                    float sideMov = Math.abs(nX);
                    float sideMovInf = sideMov * sideMov;
                    sideMovInf = sideMovInf * sideMovInf * sideMovInf;

                    moveSawInConstraints(0.0f, yPlusLeft * sideMovInf);

                    plusScore = yPlusLeft * sideMovInf * sideMovInf * sideMovInf;
                    woodCutScore += plusScore;
                }

                if (sawPositionY > woodCutY && isInWood)
                    woodCutY = sawPositionY;
            }

            if (!isInWood)
                calculateIsInWood();

            if (worldObj.isRemote)
                YeGamolChattels.network.sendToServer(PacketTileEntityClientEvent.packetEntityData(this, "sawMove"));

            if (woodCutY >= 1.0f)
                chopOffWood(woodCutScore, player);

            return plusScore;
        }
        else
        {
            moveSawInConstraints(x, y);

            if (worldObj.isRemote)
                YeGamolChattels.network.sendToServer(PacketTileEntityClientEvent.packetEntityData(this, "sawMove"));
        }

        return 0.0f;
    }

    public void moveSawInConstraints(float x, float y)
    {
        sawPositionX = MathHelper.clamp_float(sawPositionX + x, -0.3f, 0.3f);
        sawPositionY = MathHelper.clamp_float(sawPositionY + y, -0.2f, 1.2f);
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
    public void writeUpdateData(ByteBuf buffer, String context)
    {
        if ("sawMove".equals(context))
        {
            buffer.writeInt(sawingPlayerID);
            buffer.writeFloat(sawPositionX);
            buffer.writeFloat(sawPositionY);
            buffer.writeFloat(woodCutY);
            buffer.writeFloat(woodCutScore);
            buffer.writeBoolean(isInWood);
        }
        else if ("sawOpenGui".equals(context))
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
        if ("sawMove".equals(context))
        {
            int playerID = buffer.readInt();
            sawPositionX = buffer.readFloat();
            sawPositionY = buffer.readFloat();
            woodCutY = buffer.readFloat();
            woodCutScore = buffer.readFloat();
            isInWood = buffer.readBoolean();

            Entity entity = worldObj.getEntityByID(playerID);
            if (entity instanceof EntityPlayer)
            {
                if (woodCutY >= 1.0f && containedItem != null)
                    chopOffWood(woodCutScore, (EntityPlayer) entity);
            }
        }
        else if ("sawOpenGui".equals(context))
        {
            sawPositionX = buffer.readFloat();
            sawPositionY = buffer.readFloat();
            woodCutY = buffer.readFloat();
            woodCutScore = buffer.readFloat();
            isInWood = buffer.readBoolean();

            IvSideClient.getClientPlayer().openGui(YeGamolChattels.instance, YGCGuiHandler.plankSawGuiID, worldObj, xCoord, yCoord, zCoord);
        }
    }
}
