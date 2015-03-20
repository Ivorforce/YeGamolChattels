package ivorius.yegamolchattels.client.rendering;

import com.google.common.base.Function;
import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

/**
 * Created by lukas on 22.12.14.
 */
public class IIconQuadCache
{
    public static GridQuadCache<IIcon> createIconQuadCache(final IvBlockCollection blockCollection)
    {
        return GridQuadCache.createQuadCache(new int[]{blockCollection.width, blockCollection.height, blockCollection.length}, new Function<Pair<BlockCoord, ForgeDirection>, IIcon>()
        {
            @Nullable
            @Override
            public IIcon apply(Pair<BlockCoord, ForgeDirection> input)
            {
                BlockCoord coord = input.getLeft();
                ForgeDirection direction = input.getRight();

                Block block = blockCollection.getBlock(coord);
                return block.getMaterial() != Material.air && blockCollection.shouldRenderSide(coord, direction)
                        ? block.getIcon(direction.ordinal(), blockCollection.getMetadata(coord))
                        : null;
            }
        });
    }}
