/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.blocks;

import ivorius.yegamolchattels.items.YGCItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import ivorius.ivtoolkit.rendering.grid.Icon;
import net.minecraft.util.EnumFacing;

/**
 * Created by lukas on 10.07.14.
 */
public class BlockFlaxPlant extends BlockCrops
{
    private Icon[] icons;

    @Override
    public Icon getIcon(EnumFacing facing, int metadata)
    {
        if (metadata < 0 || metadata > 7)
            metadata = 7;

        return icons[metadata];
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
    public void registerBlockIcons(IconRegister p_149651_1_)
    {
        this.icons = new Icon[8];

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
