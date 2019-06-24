package sexy.kostya.fastworlds.nms;

import net.minecraft.server.v1_12_R1.*;

import javax.annotation.Nullable;
import java.io.File;
import java.util.UUID;

/**
 * Created by k.shandurenko on 24/06/2019
 */
public class XDataManager implements IDataManager, IPlayerFileData {

    private final static File DIRECTORY = new File("fast_worlds_cache");

    private final UUID uuid = UUID.randomUUID();
    private final DefinedStructureManager definedStructureManager;
    private final WorldServer original;

    XDataManager(WorldServer original) {
        this.original = original;
        this.definedStructureManager = new DefinedStructureManager((new File(DIRECTORY, "structures")).toString(), original.getServer().getServer().dataConverterManager);
    }

    static {
       DIRECTORY.mkdir();
    }

    @Nullable
    @Override
    public WorldData getWorldData() {
        return null;
    }

    @Override
    public void checkSession() {

    }

    @Override
    public IChunkLoader createChunkLoader(WorldProvider worldProvider) {
        return new XChunkLoader(this.original);
    }

    @Override
    public void saveWorldData(WorldData worldData, NBTTagCompound nbtTagCompound) {

    }

    @Override
    public void saveWorldData(WorldData worldData) {

    }

    @Override
    public IPlayerFileData getPlayerFileData() {
        return this;
    }

    @Override
    public void a() {

    }

    @Override
    public File getDirectory() {
        return DIRECTORY;
    }

    @Override
    public File getDataFile(String s) {
        return null;
    }

    @Override
    public DefinedStructureManager h() {
        return this.definedStructureManager;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public void save(EntityHuman entityHuman) {

    }

    @Nullable
    @Override
    public NBTTagCompound load(EntityHuman entityHuman) {
        return null;
    }

    @Override
    public String[] getSeenPlayers() {
        return new String[0];
    }
}
