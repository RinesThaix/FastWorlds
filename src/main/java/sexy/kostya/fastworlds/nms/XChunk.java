package sexy.kostya.fastworlds.nms;

import net.minecraft.server.v1_12_R1.*;

import java.util.List;

/**
 * Created by k.shandurenko on 25/06/2019
 */
public class XChunk extends Chunk {

    private static ChunkSnapshot getSnapshot(Chunk chunk) {
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
        return snapshot;
    }

    private boolean couldBeUnloaded = true;

    XChunk(World world, Chunk original) {
        super(world, getSnapshot(original), original.locX, original.locZ);
        super.mustSave = false;
        d(original.isDone());
        cloneEntities(original);
        cloneTileEntities(original);
    }

    private void cloneEntities(Chunk original) {
        for (int i = 0; i < original.entitySlices.length; i++) {
            for (Entity entity : original.entitySlices[i]) {
                NBTTagCompound tag = new NBTTagCompound();
                if (entity.d(tag)) {
                    Entity recreated = EntityTypes.a(tag, super.world);
                    if (recreated != null) {
                        this.a(recreated);
                        Entity vehicle = recreated;
                        for (NBTTagCompound dtag = tag; vehicle != null && dtag.hasKey("Riding"); dtag = dtag.getCompound("Riding")) {
                            Entity rider = EntityTypes.a(dtag.getCompound("Riding"), super.world);
                            if (rider != null) {
                                this.a(rider);
                                rider.a(vehicle, true);
                            }
                            vehicle = rider;
                        }
                    }
                }
            }
        }
    }

    private void cloneTileEntities(Chunk original) {
        original.tileEntities.forEach((pos, te) -> {
            NBTTagCompound tag = new NBTTagCompound();
            te.save(tag);
            TileEntity clone = TileEntity.create(super.world, tag);
            if (clone != null) {
                this.a(clone);
            }
        });
        List<NextTickListEntry> list = original.world.a(original, false);
        if (list != null) {
            long time = super.world.getTime();
            list.forEach(e -> super.world.b(e.a, e.a(), (int) (e.b - time), e.c));
        }
    }

    public boolean couldBeUnloaded() {
        return this.couldBeUnloaded;
    }

    public void setCouldBeUnloaded(boolean couldBeUnloaded) {
        this.couldBeUnloaded = couldBeUnloaded;
    }

    @Override
    public void initLighting() {
        // Nothing
    }

    @Override
    public boolean a(boolean flag) { // shall be saved?
        return false;
    }
}
