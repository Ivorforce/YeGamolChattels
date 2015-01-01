package ivorius.yegamolchattels.client.rendering;

import gnu.trove.TIntCollection;
import gnu.trove.list.array.TIntArrayList;
import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.*;

import static net.minecraftforge.common.util.ForgeDirection.*;

/**
 * Created by lukas on 22.12.14.
 */
public class IIconQuadCache implements Iterable<IIconQuadCache.CachedQuadLevel>
{
    private final List<CachedQuadLevel> cachedQuadLevels = new ArrayList<>();

    public static int[] getCacheAxes(ForgeDirection direction, int... axes)
    {
        switch (direction)
        {
            case DOWN:
            case UP:
                return new int[]{axes[1], axes[0], axes[2]};
            case WEST:
            case EAST:
                return new int[]{axes[0], axes[2], axes[1]};
            case NORTH:
            case SOUTH:
                return new int[]{axes[2], axes[1], axes[0]};
        }

        throw new IllegalArgumentException();
    }

    public static int[] getNormalAxes(ForgeDirection direction, int... axes)
    {
        return getCacheAxes(direction, axes);
    }

    public static float[] getCacheAxes(ForgeDirection direction, float... axes)
    {
        switch (direction)
        {
            case DOWN:
            case UP:
                return new float[]{axes[1], axes[0], axes[2]};
            case WEST:
            case EAST:
                return new float[]{axes[0], axes[2], axes[1]};
            case NORTH:
            case SOUTH:
                return new float[]{axes[2], axes[1], axes[0]};
        }

        throw new IllegalArgumentException();
    }

    public static float[] getNormalAxes(ForgeDirection direction, float... axes)
    {
        return getCacheAxes(direction, axes);
    }

    public static IIconQuadCache createIconQuadCache(IvBlockCollection blockCollection)
    {
        return createIconQuadCacheGreedy(blockCollection);
    }

    private static IIconQuadCache createIconQuadCacheGreedy(IvBlockCollection blockCollection)
    {
        Map<IIconQuadContext, CoordGrid> partialCache = new HashMap<>();

        for (BlockCoord coord : blockCollection)
        {
            if (blockCollection.getBlock(coord).getMaterial() != Material.air)
            {
                addToCache(partialCache, blockCollection, UP, coord);
                addToCache(partialCache, blockCollection, DOWN, coord);
                addToCache(partialCache, blockCollection, NORTH, coord);
                addToCache(partialCache, blockCollection, EAST, coord);
                addToCache(partialCache, blockCollection, SOUTH, coord);
                addToCache(partialCache, blockCollection, WEST, coord);
            }
        }

        Set<Map.Entry<IIconQuadContext, CoordGrid>> quads = partialCache.entrySet();
        IIconQuadCache cache = new IIconQuadCache();

        for (Map.Entry<IIconQuadContext, CoordGrid> entry : quads)
        {
            IIconQuadContext context = entry.getKey();

            int[] sAxes = getCacheAxes(context.direction, blockCollection.width, blockCollection.height, blockCollection.length);

            QuadCollection mesh = entry.getValue().computeMesh(0, 0, sAxes[1], sAxes[2]);
            FloatBuffer cachedQuadCoords = BufferUtils.createFloatBuffer(mesh.quadCount() * 4);

            float pxAxis = 1.0f / sAxes[1];
            float pzAxis = 1.0f / sAxes[2];

            for (int i = 0; i < mesh.quadCount(); i++)
            {
                cachedQuadCoords.put(mesh.x1(i) * pxAxis)
                        .put(mesh.y1(i) * pzAxis)
                        .put((mesh.x2(i) + 1) * pxAxis)
                        .put((mesh.y2(i) + 1) * pzAxis);
            }
            cachedQuadCoords.position(0);

            float zLevel;
            zLevel = (context.direction.offsetX + context.direction.offsetY + context.direction.offsetZ > 0
                    ? context.layer + 1 : context.layer) * (1.0f / sAxes[0]);

            cache.cachedQuadLevels.add(new CachedQuadLevel(zLevel, context.direction, context.icon, cachedQuadCoords));
        }

        return cache;
    }

    private static void addToCache(Map<IIconQuadContext, CoordGrid> cache, IvBlockCollection collection, ForgeDirection direction, BlockCoord coord)
    {
        if (collection.shouldRenderSide(coord, direction))
        {
            byte metadata = collection.getMetadata(coord);
            IIcon icon = collection.getBlock(coord).getIcon(direction.ordinal(), metadata);

            int[] sAxes = getCacheAxes(direction, coord.x, coord.y, coord.z);
            addToCache(cache, new IIconQuadContext(sAxes[0], direction, icon), sAxes[1], sAxes[2]);
        }
    }

    private static void addToCache(Map<IIconQuadContext, CoordGrid> cache, IIconQuadContext context, int x, int y)
    {
        CoordGrid quad = cache.get(context);
        if (quad == null)
        {
            quad = new CoordGrid();
            cache.put(context, quad);
        }

        quad.addCoord(x, y);
    }

    @Override
    public Iterator<CachedQuadLevel> iterator()
    {
        return cachedQuadLevels.iterator();
    }

    public static class IIconQuadContext
    {
        public final int layer;
        public final ForgeDirection direction;
        public final IIcon icon;

        public IIconQuadContext(int layer, ForgeDirection direction, IIcon icon)
        {
            this.layer = layer;
            this.direction = direction;
            this.icon = icon;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IIconQuadContext that = (IIconQuadContext) o;

            if (layer != that.layer) return false;
            if (direction != that.direction) return false;
            if (!icon.equals(that.icon)) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = layer;
            result = 31 * result + direction.hashCode();
            result = 31 * result + icon.hashCode();
            return result;
        }
    }

    public static class CoordGrid extends TIntArrayList
    {
        public CoordGrid()
        {
        }

        public CoordGrid(int capacity)
        {
            super(capacity);
        }

        public CoordGrid(int capacity, int no_entry_value)
        {
            super(capacity, no_entry_value);
        }

        public CoordGrid(TIntCollection collection)
        {
            super(collection);
        }

        public CoordGrid(int[] values)
        {
            super(values);
        }

        public CoordGrid(int[] values, int no_entry_value, boolean wrap)
        {
            super(values, no_entry_value, wrap);
        }

        public void addCoord(int x, int y)
        {
            add(x);
            add(y);
        }

        public int coordCount()
        {
            return size() / 2;
        }

        public int x(int index)
        {
            return get(index * 2);
        }

        public int y(int index)
        {
            return get(index * 2 + 1);
        }

        public QuadCollection computeMesh(int minX, int minY, int maxX, int maxY)
        {
            boolean[][] mask = new boolean[maxX - minX][maxY - minY];
            QuadCollection collection = new QuadCollection();

            for (int c = 0; c < coordCount(); c++)
                mask[x(c)][y(c)] = true;

            for (int x = minX; x < maxX; x++)
                for (int y = minY; y < maxY; y++)
                {
                    if (mask[x][y])
                    {
                        // Expand X
                        int lX = x, hX = x, lY = y, hY = y;
                        while (lX > minX && mask[lX - 1][y])
                            lX --;
                        while (hX < maxX - 1 && mask[hX + 1][y])
                            hX ++;

                        // Expand Y
                        while (lY > minY && isFree(mask, lX, hX, lY - 1))
                            lY --;
                        while (hY < maxY - 1 && isFree(mask, lX, hX, hY + 1))
                            hY ++;

                        // Fill mask
                        for (int tX = lX; tX <= hX; tX++)
                            for (int tY = lY; tY <= hY; tY++)
                                mask[tX][tY] = false;

                        collection.addQuad(lX, lY, hX, hY);
                    }
                }

            return collection;
        }

        private static boolean isFree(boolean[][] mask, int lX, int hX, int y)
        {
            for (int tX = lX; tX <= hX; tX++)
                if (!mask[tX][y])
                    return false;

            return true;
        }
    }

    public static class QuadCollection extends TIntArrayList
    {
        public QuadCollection()
        {
        }

        public QuadCollection(int capacity)
        {
            super(capacity);
        }

        public QuadCollection(int capacity, int no_entry_value)
        {
            super(capacity, no_entry_value);
        }

        public QuadCollection(TIntCollection collection)
        {
            super(collection);
        }

        public QuadCollection(int[] values)
        {
            super(values);
        }

        public QuadCollection(int[] values, int no_entry_value, boolean wrap)
        {
            super(values, no_entry_value, wrap);
        }

        public void addQuad(int x1, int y1, int x2, int y2)
        {
            add(x1);
            add(y1);
            add(x2);
            add(y2);
        }

        public int x1(int index)
        {
            return get(index * 4);
        }

        public int x2(int index)
        {
            return get(index * 4 + 2);
        }

        public int y1(int index)
        {
            return get(index * 4 + 1);
        }

        public int y2(int index)
        {
            return get(index * 4 + 3);
        }

        public int quadCount()
        {
            return size() / 4;
        }
    }

    public static class CachedQuadLevel
    {
        public final float zLevel;
        public final ForgeDirection direction;
        public final IIcon icon;

        public final FloatBuffer quads;

        public CachedQuadLevel(float zLevel, ForgeDirection direction, IIcon icon, FloatBuffer quads)
        {
            this.zLevel = zLevel;
            this.direction = direction;
            this.icon = icon;
            this.quads = quads;
        }
    }
}
