/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.gui;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.network.PacketGuiAction;
import ivorius.ivtoolkit.tools.IvInventoryHelper;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.blocks.TileEntityStatue;
import ivorius.yegamolchattels.entities.EntityFakePlayer;
import ivorius.yegamolchattels.items.ItemEntityVita;
import ivorius.yegamolchattels.items.ItemStatueChisel;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Created by lukas on 27.07.14.
 */
public class ContainerCarveStatue extends Container implements PacketGuiAction.ActionHandler
{
    private EntityPlayer usingPlayer;
    private int statueX;
    private int statueY;
    private int statueZ;

    private Entity currentCraftedEntity;

    public IInventory statueEntityCarvingInventory = new InventoryBasic("StatueCarve", true, 1)
    {
        @Override
        public boolean isItemValidForSlot(int par1, ItemStack itemStack)
        {
            return getEntity(itemStack, ContainerCarveStatue.this.usingPlayer.getEntityWorld()) != null;
        }

        @Override
        public int getInventoryStackLimit()
        {
            return 1;
        }

        public void markDirty()
        {
            super.markDirty();
            ContainerCarveStatue.this.onCraftMatrixChanged(this);
        }
    };

    public ContainerCarveStatue(InventoryPlayer inventoryPlayer, EntityPlayer player, int statueX, int statueY, int statueZ)
    {
        this.usingPlayer = player;
        this.statueX = statueX;
        this.statueY = statueY;
        this.statueZ = statueZ;

        this.addSlotToContainer(new Slot(this.statueEntityCarvingInventory, 0, 8, 69));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 89 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 147));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer var1)
    {
        return true;
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        super.onCraftMatrixChanged(par1IInventory);

        if (par1IInventory == this.statueEntityCarvingInventory)
        {
            this.updateEntityOutput();
        }
    }

    public void updateEntityOutput()
    {
        currentCraftedEntity = getEntity(statueEntityCarvingInventory.getStackInSlot(0), usingPlayer.getEntityWorld());
    }

    public Entity getCurrentCraftedEntity()
    {
        return currentCraftedEntity;
    }

    public static Entity getEntity(ItemStack stack, World world)
    {
        if (stack != null)
        {
            if (stack.getItem() == YGCItems.entityVita)
                return ItemEntityVita.createEntity(stack, world);
            else if (stack.getItem() == Items.skull)
            {
                if (stack.hasTagCompound() && stack.getTagCompound().hasKey("SkullOwner"))
                {
                    return new EntityFakePlayer(world, stack.getTagCompound().getString("SkullOwner"));
                }
            }
        }

        return null;
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        if (!player.getEntityWorld().isRemote)
        {
            for (int i = 0; i < this.statueEntityCarvingInventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.statueEntityCarvingInventory.getStackInSlotOnClosing(0);

                if (itemstack != null)
                {
                    player.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }

    @Override
    public void handleAction(String context, ByteBuf buffer)
    {
        if ("carveStatue".equals(context))
        {
            float yawHead = buffer.readFloat();
            float pitchHead = buffer.readFloat();
            float swing = buffer.readFloat();
            float stance = buffer.readFloat();

            ItemStack statueStack = statueEntityCarvingInventory.getStackInSlot(0);
            if (statueStack != null)
            {
                if (usingPlayer.inventory.hasItem(YGCItems.clubHammer))
                {
                    Entity statueEntity = getEntity(statueStack, usingPlayer.getEntityWorld());
                    TileEntityStatue createdStatue = ItemStatueChisel.carveStatue(usingPlayer.inventory.getCurrentItem(), statueEntity, statueEntity.worldObj, statueX, statueY, statueZ, usingPlayer);

                    if (createdStatue != null)
                    {
                        createdStatue.setStatueYawHead(yawHead);
                        createdStatue.setStatuePitchHead(pitchHead);
                        createdStatue.setStatueSwing(swing);
                        createdStatue.setStatueStance(stance);

                        int clubHammerSlot = IvInventoryHelper.getInventorySlotContainItem(usingPlayer.inventory, YGCItems.clubHammer);
                        usingPlayer.inventory.getStackInSlot(clubHammerSlot).damageItem(1, usingPlayer);
                        statueEntityCarvingInventory.decrStackSize(0, 1);
                    }
                }
                else
                {
                    usingPlayer.addChatComponentMessage(new ChatComponentTranslation("item.ygcChisel.noHammer"));
                }

                usingPlayer.closeScreen();
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 == 0)
            {
                if (!this.mergeItemStack(itemstack1, 1, 37, true))
                {
                    return null;
                }
            }
            else
            {
                if (((Slot)this.inventorySlots.get(0)).getHasStack() || !((Slot)this.inventorySlots.get(0)).isItemValid(itemstack1))
                {
                    return null;
                }

                if (itemstack1.hasTagCompound() && itemstack1.stackSize == 1)
                {
                    ((Slot)this.inventorySlots.get(0)).putStack(itemstack1.copy());
                    itemstack1.stackSize = 0;
                }
                else if (itemstack1.stackSize >= 1)
                {
                    ItemStack oneItemStack = itemstack1.copy();
                    oneItemStack.stackSize = 1;
                    ((Slot)this.inventorySlots.get(0)).putStack(oneItemStack);
                    --itemstack1.stackSize;
                }
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }
}
