package me.muchomore.ConfigFiles;

import me.muchomore.LuckyBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class SaveNLoad {
    final LuckyBlocks instance = LuckyBlocks.getPlugin(LuckyBlocks.class);
    ConfigManager cfg = new ConfigManager(LuckyBlocks.getPlugin(LuckyBlocks.class),"mapsContainer.yml");
    ConfigMethods cfm = new ConfigMethods(cfg);
    public boolean saveHashMap(){
        HashMap<UUID,List<Location>> plbl = new HashMap<>(instance.playerLuckyBlockLocations);
        HashMap<Location,UUID> lblU = new HashMap<>(instance.luckyBlockLocationUUID);
        HashMap<Location, ItemStack> lblT = new HashMap<>(instance.luckyBlockLocationType);
        String pathPLBL = "plbl";
        cfg.getConfig().set(pathPLBL,null);
        cfg.saveConfig();
        for(Map.Entry<UUID,List<Location>>entry: plbl.entrySet()){
            UUID id = entry.getKey();
            List<Location> locs = entry.getValue();
            if(id!=null&& !locs.isEmpty()){
                String idS = serializeUUID(id);
                String path = pathPLBL+"."+idS;

                List<String> list = new ArrayList<>();
                for(Location loc: locs){
                    list.add(serializeLocation(loc));
                }
                cfm.setStringList(list,path);
            }
        }


        String pathLBLU = "lblu";
        cfg.getConfig().set(pathLBLU,null);
        cfg.saveConfig();
        int x = 0;
        for(Map.Entry<Location,UUID>entry: lblU.entrySet()){
            Location loc = entry.getKey();
            UUID id = entry.getValue();
            String path = pathLBLU+"."+x;
            if(id!=null&&loc!=null){
                List<String> list = new ArrayList<>();
                String idS = serializeUUID(id);
                String locS = serializeLocation(loc);
                list.add(idS);
                list.add(locS);
                cfm.setStringList(list,path);
                x++;
            }

        }


        String pathLBLT = "lblt";
        cfg.getConfig().set(pathLBLT,null);
        cfg.saveConfig();
        int z = 0;
        for(Map.Entry<Location,ItemStack>entry: lblT.entrySet()){
            Location loc = entry.getKey();
            ItemStack item = entry.getValue();
            if(item!=null&&loc!=null){
                String locS = serializeLocation(loc);
                String path = pathLBLT+"."+z;
                ItemStack[] items = new ItemStack[1];
                items[0] = item;
                String itemStacks = itemStackArrayToBase64(items);
                List<String> list = new ArrayList<>();
                list.add(locS);
                list.add(itemStacks);
                cfm.setStringList(list,path);
                z++;
            }

        }
       cfg.saveConfig();
        return true;
    }
    public boolean loadHashMap(){
        if(cfg.getConfig().getConfigurationSection("plbl")!=null&&!cfg.getConfig().getConfigurationSection("plbl").getKeys(false).isEmpty()){
            HashMap<UUID,List<Location>> map = new HashMap<>();
            for(String s: cfg.getConfig().getConfigurationSection("plbl").getKeys(false)){
                String path = "plbl."+s;
                UUID id = deserializeUUID(s);
                if(id==null)continue;
                List<String> list = cfm.getStringList(path,new ArrayList<>());
                List<Location> locs = new ArrayList<>();
                for(String sss: list){
                    Location loc = deserializeLocation(sss);
                    if(loc!=null){
                        locs.add(loc);
                    }
                }
                map.put(id,locs);
            }
            instance.playerLuckyBlockLocations = map;
        }
        if(cfg.getConfig().getConfigurationSection("lblu")!=null&&!cfg.getConfig().getConfigurationSection("lblu").getKeys(false).isEmpty()){
            HashMap<Location,UUID> map = new HashMap<>();
            for(String s: cfg.getConfig().getConfigurationSection("lblu").getKeys(false)){
                String path = "lblu."+s;
                List<String> list = cfm.getStringList(path,new ArrayList<>());
                if(list.size()!=2)continue;
                String idS = list.get(0);
                String locS = list.get(1);

                UUID id = deserializeUUID(idS);
                Location loc = deserializeLocation(locS);
                if(id==null||loc==null) continue;
                map.put(loc,id);
            }
            instance.luckyBlockLocationUUID = map;
        }
        if(cfg.getConfig().getConfigurationSection("lblt")!=null&&!cfg.getConfig().getConfigurationSection("lblt").getKeys(false).isEmpty()){
            HashMap<Location,ItemStack> map = new HashMap<>();
            for(String s: cfg.getConfig().getConfigurationSection("lblt").getKeys(false)){
                String path = "lblt."+s;
                List<String> list = cfm.getStringList(path,new ArrayList<>());

                if(list.size()!=2)continue;
                String locS = list.get(0);
                String itemS = list.get(1);
                Location loc = deserializeLocation(locS);
                if(loc==null)continue;
                ItemStack[] itemsArray = itemStackArrayFromBase64(itemS);
                if (itemsArray==null)continue;;
                if(itemsArray.length!=1)continue;
                ItemStack luckyBlock = itemsArray[0];
                map.put(loc,luckyBlock);
            }
            instance.luckyBlockLocationType = map;
        }


        return true;
    }

    public static String itemStackArrayToBase64(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.length);
            for (int i=0; i<items.length; i++) {
                dataOutput.writeObject(items[i]);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];
            for (int i=0; i<items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            dataInput.close();
            return items;
        } catch (Exception e) {
            return null;
        }
    }
    public void savePrize(List<String> s,String p){
        if(cfg.getConfig().contains("d")){
            ItemStack item = new ItemStack(Material.STRING);
            ItemMeta meta = item.getItemMeta();
            if(meta!=null){
                meta.setDisplayName("ERROR");
                item.setItemMeta(meta);
            }

        }

    }

    public String serializeLocation(Location loc){
        String world = loc.getWorld().getName();
        int x = (int)loc.getBlockX();
        int y = (int)loc.getBlockY();
        int z = (int)loc.getBlockZ();
        return world+"_"+x+"_"+y+"_"+z;
    }
    public Location deserializeLocation(String locString){
        String [] locparts = locString.split("_");
        if(locparts.length!=4)return null;
        double x,y,z;
        float yaw,pitch;
        World world;
        String worldString = locparts[0];

        try{
            world = Bukkit.getWorld(worldString);
            if(world==null)return null;
            x = Integer.parseInt(locparts[1]);
            y = Integer.parseInt(locparts[2]);
            z = Integer.parseInt(locparts[3]);
        }catch (NumberFormatException e){
            System.out.println("|WARN| Corrupted location value inside LuckyBlocks yaml file!");
            return null;
        }

        return new Location(world,x,y,z);
    }
    public String serializeUUID(UUID id){
        return id.toString();
    }
    public UUID deserializeUUID(String uuid){
        return UUID.fromString(uuid);
    }



}
