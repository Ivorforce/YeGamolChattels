/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.blocks;

import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

/**
 * Created by lukas on 10.07.14.
 */
public class BlockFlaxPlant extends BlockCrops
{
    private IIcon[] icons;

    @Override
    public IIcon getIcon(int side, int metadata)
    {
        if (metadata < 7)
        {
            if (metadata == 6)
            {
                metadata = 5;
            }

            return this.icons[metadata >> 1];
        }
        else
        {
            return this.icons[3];
        }
    }

    @Override
    protected Item func_149866_i()
    {
        return YGCItems.flaxSeeds;
    }

    @Override
    protected Item func_149865_P()
    {
        return YGCItems.flaxFiber;
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        this.icons = new IIcon[4];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = p_149651_1_.registerIcon(this.getTextureName() + "_stage_" + i);
        }
    }

    @Override
    protected boolean canPlaceBlockOn(Block block)
    {
        return block == Blocks.farmland || block == Blocks.grass || block == Blocks.dirt;
    }
}
