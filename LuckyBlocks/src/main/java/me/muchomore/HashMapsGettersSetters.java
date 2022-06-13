package me.muchomore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HashMapsGettersSetters {

    final LuckyBlocks instance = LuckyBlocks.getPlugin(LuckyBlocks.class);
    public boolean addArmorStand(UUID playerId, ArmorStand armorStand){
        instance.luckyBlockArmorStands.put(playerId,armorStand);
        return true;
    }
    public ArmorStand getArmorStand(UUID playerId){
        return instance.luckyBlockArmorStands.getOrDefault(playerId,null);
    }
    public boolean removeArmorStand(UUID playerId){
        instance.luckyBlockArmorStands.remove(playerId);
        return true;
    }

    public boolean addTextStand(UUID playerId, ArmorStand armorStand){
        instance.luckyBlockTextStands.put(playerId,armorStand);
        return true;
    }
    public ArmorStand getTextStand(UUID playerId){
        return instance.luckyBlockTextStands.getOrDefault(playerId,null);
    }
    public boolean removeTextStand(UUID playerId){
        instance.luckyBlockTextStands.remove(playerId);
        return true;
    }

    public boolean setPlayerLuckyBlockLocation(UUID playerId, Location loc){
        List<Location> list = instance.playerLuckyBlockLocations.getOrDefault(playerId,new ArrayList<>());
        list.add(loc);
        instance.playerLuckyBlockLocations.put(playerId,list);
        return true;
    }
    public boolean havePlayerPlacedLuckyBlock(UUID playerId){
        return instance.playerLuckyBlockLocations.containsKey(playerId);
    }
    public boolean removePlayerLuckyBlockLocation(UUID playerId,Location loc){
        List<Location> list = instance.playerLuckyBlockLocations.getOrDefault(playerId,new ArrayList<>());
        loc = loc.add(0,-1,0);
        for(Location l: list){
            int xl = (int)l.getBlockX();
            int yl = (int)l.getBlockY();
            int zl = (int)l.getBlockZ();
            if(xl == (int)loc.getBlockX()&& yl ==(int) loc.getBlockY() && zl == (int)loc.getBlockZ()){
                list.remove(l);
                break;
            }
        }
        instance.playerLuckyBlockLocations.put(playerId,list);
        return true;
    }
    public List<Location> getPlayerLuckyBlockLocation(UUID playerId){
        return instance.playerLuckyBlockLocations.getOrDefault(playerId,new ArrayList<>());
    }

    public boolean addLocation(ItemStack luckyBlock,Location loc,UUID playerID){
        instance.luckyBlockLocationType.put(loc,luckyBlock);
        instance.luckyBlockLocationUUID.put(loc,playerID);
        return true;
    }
    public boolean containsLocation(Location loc){
       return instance.luckyBlockLocationType.containsKey(loc) || instance.luckyBlockLocationUUID.containsKey(loc);

    }


    public UUID getLuckyBlockOwner(Location loc){
        return instance.luckyBlockLocationUUID.getOrDefault(loc,null);
    }
    public boolean isThisALuckyBlock(Location loc){
        for(Location l : instance.luckyBlockLocationType.keySet()){
            if(l.equals(loc))return true;
        }
        return false;
    }
    public boolean removeLocation(Location loc){
        instance.luckyBlockLocationType.remove(loc);
        instance.luckyBlockLocationUUID.remove(loc);
        return true;
    }
    public boolean isThisLuckyBlockPlayers(UUID playerId, Location blockLoc){
        if(!havePlayerPlacedLuckyBlock(playerId))return false;
        List<Location> locations = getPlayerLuckyBlockLocation(playerId);
        return locations.contains(blockLoc);
    }
}
