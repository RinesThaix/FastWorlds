package sexy.kostya.fastworlds.nms;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;

/**
 * Created by k.shandurenko on 24/06/2019
 */
public class XWorld extends WorldServer {

    public static XWorld create(World original, WorldCreator creator) {
        return create(((CraftWorld) original).getHandle(), true, false, EnumGamemode.SURVIVAL, creator);
    }

    public static XWorld create(WorldServer original, boolean generateStructures, boolean hardcore, EnumGamemode gamemode, WorldCreator creator) {
        WorldSettings worldSettings = new WorldSettings(
                original.getSeed(),
                gamemode,
                generateStructures,
                hardcore,
                WorldType.getType(creator.type().getName())
        );
        worldSettings.setGeneratorSettings(creator.generatorSettings());
        WorldData data = new WorldData(worldSettings, creator.name());
        data.checkName(creator.name());
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        MinecraftServer server = craftServer.getServer();
        XWorld world =  new XWorld(
                server,
                new XDataManager(original),
                data,
                10 + server.worlds.size(),
                server.methodProfiler,
                World.Environment.NORMAL,
                creator.generator()
        );
        world.b();
        world.original = original;
        world.scoreboard = craftServer.getScoreboardManager().getMainScoreboard().getHandle();
        world.tracker = new EntityTracker(world);
        world.addIWorldAccess(new WorldManager(server, world));
        world.worldData.setDifficulty(EnumDifficulty.EASY);
        world.setSpawnFlags(true, true);
        server.worlds.add(world);
        craftServer.getPluginManager().callEvent(new WorldInitEvent(world.getWorld()));
        craftServer.getPluginManager().callEvent(new WorldLoadEvent(world.getWorld()));
        return world;
    }

    private WorldServer original;

    private XWorld(MinecraftServer server, IDataManager idatamanager, WorldData worlddata, int i, MethodProfiler methodprofiler, World.Environment env, ChunkGenerator gen) {
        super(server, idatamanager, worlddata, i, methodprofiler, env, gen);
    }

    public WorldServer getOriginalWorld() {
        return this.original;
    }

}
