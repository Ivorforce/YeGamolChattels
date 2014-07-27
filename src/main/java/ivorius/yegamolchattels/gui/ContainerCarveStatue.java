/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.gui;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.network.PacketGuiAction;
import ivorius.ivtoolkit.network.PacketGuiActionHandler;
import ivorius.yegamolchattels.items.ItemEntityVita;
import ivorius.yegamolchattels.items.ItemStatueChisel;
import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;

/**
 * Created by lukas on 27.07.14.
 */
public class ContainerCarveStatue extends Container implements PacketGuiAction.ActionHandler
{
    private EntityPlayer usingPlayer;
    private int statueX;
    private int statueY;
    private int statueZ;

    private IInventory statueEntityCarvingInventory = new InventoryBasic("StatueCarve", true, 1)
    {
        @Override
        public boolean isItemValidForSlot(int par1, ItemStack itemStack)
        {
            return itemStack.getItem() == YGCItems.entityVita;
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

        this.addSlotToContainer(new Slot(this.statueEntityCarvingInventory, 0, 134, 40));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
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
            ItemStack statueStack = statueEntityCarvingInventory.getStackInSlot(0);
            if (statueStack != null)
            {
                Entity statueEntity = ItemEntityVita.createEntity(statueStack, usingPlayer.getEntityWorld());

                if (ItemStatueChisel.carveStatue(usingPlayer.inventory.getCurrentItem(), statueEntity, statueEntity.worldObj, statueX, statueY, statueZ, usingPlayer))
                {
                    statueEntityCarvingInventory.decrStackSize(0, 1);
                    usingPlayer.closeScreen();
                }
            }
        }
    }
}
