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
        return cast(world, this.original.getChunkAt(x, z));
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

    private Chunk cast(World newWorld, Chunk chunk) {
        ChunkSnapshot snapshot = new ChunkSnapshot();
        ChunkSection[] sections = chunk.getSections();
        for (int bx = 0; bx < 16; ++bx) {
            for (int bz = 0; bz < 16; ++bz) {
                for (int by = 0; by < 256; ++by) {
                    int sectionID = by >> 4;
                    ChunkSection section = sections[sectionID];
                    if (section == null) {
                        continue;
                    }
                    snapshot.a(bx, by, bz, section.getType(bx, by & 15, bz));
                }
            }
        }
        chunk = new net.minecraft.server.v1_12_R1.Chunk(
                newWorld,
                snapshot,
                chunk.locX, chunk.locZ
        );
        byte[] biomeData = chunk.getBiomeIndex();
        this.biomes = newWorld.getWorldChunkManager().getBiomes(this.biomes, chunk.locX * 4 - 2, chunk.locZ * 4 - 2, 10, 10);

        for (int i = 0; i < Math.min(this.biomes.length, biomeData.length); ++i) {
            biomeData[i] = (byte) BiomeBase.a(this.biomes[i]);
        }

        chunk.initLighting();
        return chunk;
    }

}
