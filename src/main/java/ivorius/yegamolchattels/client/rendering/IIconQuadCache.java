package ivorius.yegamolchattels.client.rendering;

import com.google.common.base.Function;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import ivorius.ivtoolkit.rendering.grid.Icon;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

/**
 * Created by lukas on 22.12.14.
 */
public class IIconQuadCache
{
    public static GridQuadCache<Icon> createIconQuadCache(final IvBlockCollection blockCollection)
    {
        return GridQuadCache.createQuadCache(new int[]{blockCollection.width, blockCollection.height, blockCollection.length}, new Function<Pair<BlockPos, EnumFacing>, Icon>()
        {
            @Nullable
            @Override
            public Icon apply(Pair<BlockPos, EnumFacing> input)
            {
                BlockPos coord = input.getLeft();
                EnumFacing direction = input.getRight();

                IBlockState state = blockCollection.getBlockState(coord);
                Block block = state.getBlock();
                // TODO
//                return block.getMaterial() != Material.air && blockCollection.shouldRenderSide(coord, direction)
//                        ? block.getIcon(direction.ordinal(), blockCollection.getMetadata(coord))
//                        : null;
                return null;
            }
        });
    }}
