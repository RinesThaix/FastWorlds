package sexy.kostya.fastworlds;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import sexy.kostya.fastworlds.nms.XWorld;

public final class FastWorlds extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new Listener() {

            private XWorld world;

            @EventHandler
            public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
                String msg = e.getMessage();
                Player p = e.getPlayer();
                World w = p.getWorld();
                switch (msg.toLowerCase()) {
                    case "create":
                        e.setCancelled(true);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(FastWorlds.this, () -> {
                            this.world = XWorld.create(w, new WorldCreator("test_world"));
                        });
                        break;
                    case "switch":
                        e.setCancelled(true);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(FastWorlds.this, () -> {
                            Location tp = p.getLocation().clone();
                            tp.setWorld(w == this.world.getWorld() ? this.world.getOriginalWorld().getWorld() : this.world.getWorld());
                            p.teleport(tp);
                        });
                        break;
                    case "mem":
                        e.setCancelled(true);
                        Runtime runtime = Runtime.getRuntime();
                        long free = runtime.freeMemory() >> 20;
                        long max = runtime.maxMemory() >> 20;
                        p.sendMessage(String.format("%dMb / %dMb memory", max - free, max));
                        break;
                }
            }
        }, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
