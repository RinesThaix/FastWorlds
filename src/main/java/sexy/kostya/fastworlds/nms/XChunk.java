package sexy.kostya.fastworlds.nms;

import net.minecraft.server.v1_12_R1.*;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by k.shandurenko on 25/06/2019
 */
public class XChunk extends Chunk {

    private boolean couldBeUnloaded = true;

    XChunk(World world, Chunk original) {
        super(world, original.locX, original.locZ);

        cloneSettings(original);

        super.mustSave = false;
        d(original.isDone());

        cloneBlocks(original);
        cloneEntities(original);
        cloneTileEntities(original);
    }

    private void cloneSettings(Chunk original) {
        System.arraycopy(original.heightMap, 0, this.heightMap, 0, 256);
        cloneSettingArray(original, "g");
        cloneSettingArray(original, "h");
        cloneSettingArray(original, "i");
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    private void cloneSettingArray(Chunk original, String arrayName) {
        try {
            Field field = Chunk.class.getDeclaredField(arrayName);
            field.setAccessible(true);
            System.arraycopy(
                    field.get(original),
                    0,
                    field.get(this),
                    0,
                    256
            );
            field.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cloneBlocks(Chunk original) {
        boolean flag = world.worldProvider.m();

        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                for(int y = 0; y < 256; ++y) {
                    int sectionID = y >> 4;
                    ChunkSection originalSection = original.getSections()[sectionID];
                    if (originalSection == null) {
                        continue;
                    }
                    IBlockData blockData = originalSection.getType(x, y & 15, z);
                    if (blockData == null || blockData.getMaterial() == Material.AIR) {
                        continue;
                    }
                    if (getSections()[sectionID] == null) {
                        getSections()[sectionID] = new ChunkSection(sectionID << 4, flag);
                    }
                    ChunkSection mySection = getSections()[sectionID];
                    mySection.setType(x, y & 15, z, blockData);
                }
            }
        }
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
