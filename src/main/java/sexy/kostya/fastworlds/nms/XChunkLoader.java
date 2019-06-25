package sexy.kostya.fastworlds.nms;

import net.minecraft.server.v1_12_R1.*;

import javax.annotation.Nullable;

/**
 * Created by k.shandurenko on 24/06/2019
 */
public class XChunkLoader implements IChunkLoader {

    private final WorldServer original;

    private BiomeBase[] biomes;

    XChunkLoader(WorldServer original) {
        this.original = original;
    }

    @Nullable
    @Override
    public Chunk a(World world, int x, int z) {
        boolean couldBeUnloaded = true;
        if (world instanceof XWorld) {
            couldBeUnloaded = ((XWorld) world).isChunksCouldBeUnloaded();
        }
        return cast(world, this.original.getChunkAt(x, z), couldBeUnloaded);
    }

    @Override
    public void saveChunk(World world, Chunk chunk, boolean b) {

    }

    @Override
    public void b() {

    }

    @Override
    public void c() {

    }

    @Override
    public boolean chunkExists(int x, int z) {
        return true;
    }

    private Chunk cast(World newWorld, Chunk chunk, boolean couldBeUnloaded) {
        XChunk xchunk = new XChunk(newWorld, chunk);
        byte[] biomeData = xchunk.getBiomeIndex();
        this.biomes = newWorld.getWorldChunkManager().getBiomes(this.biomes, chunk.locX * 4 - 2, chunk.locZ * 4 - 2, 10, 10);

        for (int i = 0; i < Math.min(this.biomes.length, biomeData.length); ++i) {
            biomeData[i] = (byte) BiomeBase.a(this.biomes[i]);
        }

        return xchunk;
    }

}
