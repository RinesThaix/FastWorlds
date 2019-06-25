package sexy.kostya.fastworlds;

import net.minecraft.server.v1_12_R1.Chunk;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import sexy.kostya.fastworlds.nms.XChunk;

/**
 * Created by k.shandurenko on 25/06/2019
 */
public class FastWorldsListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent e) {
        Chunk chunk = ((CraftChunk) e.getChunk()).getHandle();
        if (chunk instanceof XChunk) {
            if (!((XChunk) chunk).couldBeUnloaded()) {
                e.setCancelled(true);
                return;
            }
        }
    }

}
