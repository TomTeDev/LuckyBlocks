package me.muchomore.Listeners;

import me.muchomore.ConfigFiles.ConfigManager;
import me.muchomore.ConfigFiles.ConfigMethods;
import me.muchomore.LuckyBlocks;
import me.muchomore.UtilisMuchomore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.List;
import java.util.UUID;

public class BlockPlaceListener implements Listener {
    static LuckyBlocks lb = LuckyBlocks.getPlugin(LuckyBlocks.class);
    ConfigMethods cfm = new ConfigMethods(new ConfigManager(lb,"luckyBlocks.yml"));
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        if(e.getItemInHand()==null)return;
        List<ItemStack> luckyBlocks = lb.luckyBlocks;
        ItemStack luckyBlock = null;
        for(ItemStack lb: luckyBlocks){
            if(lb.isSimilar(e.getItemInHand())){
                luckyBlock = e.getItemInHand();
                break;
            }
        }

        if(luckyBlock==null)return;;
        if(lb.maps.containsLocation(e.getBlock().getLocation())){
            e.setCancelled(true);
            return;
        }
        int a = e.getItemInHand().getAmount();
        ItemStack toBeGiven;
        if(a==1){
            toBeGiven = new ItemStack(Material.AIR);
        }else{
            toBeGiven = luckyBlock.clone();
            toBeGiven.setAmount(a-1);
        }
        Player p = e.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(LuckyBlocks.getPlugin(LuckyBlocks.class), new Runnable() {
            @Override
            public void run() {
                if(p.isOnline()){
                    p.getInventory().setItemInHand(toBeGiven);
                }
            }
        },1);



        UUID playerId = e.getPlayer().getUniqueId();
        lb.maps.setPlayerLuckyBlockLocation(playerId,e.getBlockPlaced().getLocation());
        lb.maps.addLocation(luckyBlock,e.getBlock().getLocation(), playerId);
    }
}
