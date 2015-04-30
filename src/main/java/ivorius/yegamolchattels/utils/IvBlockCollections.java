package ivorius.yegamolchattels.utils;

import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;

/**
 * Created by lukas on 30.04.15.
 */
public class IvBlockCollections
{
    public static IvBlockCollection transform(IvBlockCollection collection, AxisAlignedTransform2D transform)
    {
        int[] size = new int[]{collection.width, collection.length, collection.height};
        int[] newSize = transform.getRotation() % 2 == 1 ? new int[]{size[2], size[1], size[0]} : size.clone();

        IvBlockCollection copy = new IvBlockCollection(newSize[0], newSize[1], newSize[2]);

        for (BlockCoord coord : copy)
        {
            BlockCoord transformed = transform.apply(coord, size);
            copy.setBlockAndMetadata(transformed, collection.getBlock(coord), collection.getMetadata(coord));
        }

        return copy;
    }
}
