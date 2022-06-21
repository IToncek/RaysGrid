package me.itoncek.raysgrid;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static me.itoncek.raysgrid.RaysGrid.ClearList;
import static me.itoncek.raysgrid.RaysGrid.list;

public class WorldModifier implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChunkLoad(PlayerChunkLoadEvent event) {
        if(!list.contains(event.getChunk())) {
            if(!ClearList.contains(event.getChunk())) {
                ClearList.add(event.getChunk());
            }
        }
    }
}
