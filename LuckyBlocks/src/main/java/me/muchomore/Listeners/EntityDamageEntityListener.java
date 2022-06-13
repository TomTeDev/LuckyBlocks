package me.muchomore.Listeners;

import me.muchomore.LuckyBlocks;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public class EntityDamageEntityListener implements Listener {
    final LuckyBlocks lb = LuckyBlocks.getPlugin(LuckyBlocks.class);
/*    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e){

        Entity ent = e.getEntity();
        if(!(ent instanceof ArmorStand))return;
        ArmorStand ar = (ArmorStand) ent;
        if(lb.allArmorStands.contains(ar)){
            e.setCancelled(true);
            return;
        }


    }*/

    @EventHandler
    public void onPlayerManipulate(PlayerArmorStandManipulateEvent e) {
        ArmorStand ar = e.getRightClicked();
        if(lb.allArmorStands.contains(ar)){
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerBreak(EntityDamageEvent e) {
        if (e.getEntity() instanceof ArmorStand) {

            if(lb.allArmorStands.contains((ArmorStand) e.getEntity())){
                e.setCancelled(true);
                return;
            }
        }
    }
}
