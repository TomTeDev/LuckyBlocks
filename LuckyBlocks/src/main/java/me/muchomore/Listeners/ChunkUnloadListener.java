package me.muchomore.Listeners;

import me.muchomore.LuckyBlocks;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.List;

public class ChunkUnloadListener implements Listener {
    @EventHandler
    public void onUnload(ChunkUnloadEvent e){
        LuckyBlocks lb = LuckyBlocks.getPlugin(LuckyBlocks.class);
        Entity[]ent =  e.getChunk().getEntities();
        List<Entity> entityList = new ArrayList<>();
        for(Entity entita: ent){
            if(entita instanceof ArmorStand){
                entityList.add(entita);
            }
        }
        for(Entity entityy: entityList){
            if(lb.allArmorStands.contains(entityy)){
                entityy.remove();
                lb.allArmorStands.remove(entityy);
            }
        }
    }
}
