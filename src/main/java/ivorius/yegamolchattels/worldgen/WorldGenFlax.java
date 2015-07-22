/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.yegamolchattels.worldgen;

import net.minecraftforge.fml.common.IWorldGenerator;
import ivorius.yegamolchattels.blocks.YGCBlocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

/**
 * Created by lukas on 10.07.14.
 */
public class WorldGenFlax implements IWorldGenerator
{
    private WorldGenFlowers plantGen;

    public WorldGenFlax()
    {
        this.plantGen = new WorldGenFlowers(YGCBlocks.flaxPlant);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        BiomeGenBase biome = world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16);
        BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);

        if (types.length == 1 && types[0] == BiomeDictionary.Type.PLAINS)
        {
            int flaxPlants = 0;
            while (random.nextFloat() < 0.8f && flaxPlants < 10)
                flaxPlants++;

            for (int flower = 0; flower < flaxPlants; ++flower)
            {
                int x = chunkX * 16 + random.nextInt(16) + 8;
                int z = chunkZ * 16 + random.nextInt(16) + 8;
                int y = random.nextInt(world.getHeightValue(x, z) + 32);

                plantGen.func_150550_a(YGCBlocks.flaxPlant, 7 - (random.nextFloat() < 0.1f ? 1 : 0));
                plantGen.generate(world, random, pos);
            }
        }
    }
}
