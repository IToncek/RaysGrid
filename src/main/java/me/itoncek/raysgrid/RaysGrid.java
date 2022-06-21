package me.itoncek.raysgrid;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public final class RaysGrid extends JavaPlugin {

    public static List<Chunk> list = new ArrayList<>();

    public static List<Chunk> ClearList = new ArrayList<>();

    public static boolean lock = false;

    BukkitRunnable runnable = new BukkitRunnable() {
        @Override
        public void run() {
            if((ClearList.size() > 0) && !(list.contains(ClearList.get(0)))) {
                Chunk chunk = ClearList.get(0);
                for(Player p : Bukkit.getOnlinePlayers()) {
                    p.sendActionBar(Component.text("Modifying terrain, please wait... Current queue: " + ClearList.size()));
                }
                for(int x = 0; x <= 15; x++) {
                    for(int y = chunk.getWorld().getMinHeight(); y <= chunk.getWorld().getMaxHeight(); y++) {
                        for(int z = 0; z <= 15; z++) {
                            Block block = chunk.getBlock(x, y, z);
                                if(!isEqual(block.getType())) {
                                    chunk.getBlock(x, y, z).setType(Material.AIR, false);
                                }
                        }
                    }
                }
                ClearList.remove(0);
                list.add(chunk);
                Bukkit.getLogger().info("converted " + chunk.getX() + ";" + chunk.getZ());
            }
        }
    };

    public static boolean isEqual(Material mat) {
        return mat == Material.CHEST || mat == Material.END_GATEWAY || mat == Material.SPAWNER || mat == Material.END_PORTAL_FRAME || mat == Material.NETHER_PORTAL || mat == Material.END_PORTAL;
    }

    @Override
    public void onEnable() {
        for (World world : Bukkit.getWorlds()) {
            world.setSpawnLocation(4, world.getHighestBlockYAt(4, 4), 4);
        }
        try {
            File f = new File("./plugins/RaysGrid/chunks.tmp");
            if(f.exists()) {
                Scanner sc = new Scanner(f);
                sc.forEachRemaining(s -> {
                    String[] s1 = s.split("\\r?,");
                    list.add(Objects.requireNonNull(getServer().getWorld(s1[2])).getChunkAt(Integer.parseInt(s1[0]), Integer.parseInt(s1[1])));
                    //Bukkit.getLogger().info("loaded " + Integer.parseInt(s1[0]) + ";" + Integer.parseInt(s1[1]));
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bukkit.getServer().getPluginManager().registerEvents(new WorldModifier(), this);
        runnable.runTaskTimer(this, 100L, 0L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!lock) {
                    try {
                        lock = true;
                        File f = new File("./plugins/RaysGrid/chunks.tmp");
                        f.createNewFile();
                        FileWriter fw = new FileWriter(f);
                        for (Chunk chunk : list) {
                            fw.write(chunk.getX() + "," + chunk.getZ() + "," + chunk.getWorld().getName() + "\n");
                        }
                        fw.flush();
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        lock = false;
                    } finally {
                        lock = false;
                    }
                }

                Bukkit.getServer().getWorlds().forEach(World::save);
            }
        }.runTaskTimer(this, 60*5*20L, 60*5*20L);
    }

    @Override
    public void onDisable() {
        runnable.cancel();
        if(!lock) {
            try {
                lock = true;
                File f = new File("./plugins/RaysGrid/chunks.tmp");
                f.createNewFile();
                FileWriter fw = new FileWriter(f);
                for (Chunk chunk : list) {
                    fw.write(chunk.getX() + "," + chunk.getZ() + "," + chunk.getWorld().getName() + "\n");
                }
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
                lock = false;
            } finally {
                lock = false;
            }
        }
    }
}
