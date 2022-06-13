package me.muchomore;

import me.muchomore.Commands.GiveCommand;
import me.muchomore.ConfigFiles.SaveNLoad;
import me.muchomore.Listeners.BlockBreakListener;
import me.muchomore.Listeners.BlockPlaceListener;
import me.muchomore.Listeners.ChunkUnloadListener;
import me.muchomore.Listeners.EntityDamageEntityListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class LuckyBlocks extends JavaPlugin {


    static Plugin plugin;
    public HashMap<Location,UUID> luckyBlockLocationUUID;
    public Location luckyBlockLocationLoc;
    public int luckyBlockLocationLocX = 300;
    public int luckyBlockLocationLocY = 100;
    public String one = "8";
    public String two = "11";
    public List<ArmorStand> allArmorStands;
    public HashMap<Location,ItemStack> luckyBlockLocationType;
    public HashMap<UUID,List<Location>> playerLuckyBlockLocations;
    public HashMap<UUID, ArmorStand> luckyBlockArmorStands;
    public HashMap<UUID, ArmorStand> luckyBlockTextStands;
    public HashMapsGettersSetters maps;
    public HashMap<ItemStack,LinkedList<UUID>> prizesMap;
    public HashMap<ItemStack,Integer> chances;
    public HashMap<UUID,List<String>> commandsMap;
    public HashMap<UUID,String> messagesMap;
    public HashMap<UUID,ItemStack> itemsMap;
    public HashMap<UUID,String> prizesNamesMap;
    public HashMap<UUID,Integer> allChancesMap;
    public List<ItemStack> luckyBlocks;

    public UtilisMuchomore utilisMuchomore;





    @Override
    public void onEnable() {
        plugin = this;
        this.utilisMuchomore = new UtilisMuchomore();
        setUpHashMaps();
        loadLuckyBlocks();
        loadSavedMaps();
        getCommand("luckyblocks").setExecutor(new GiveCommand());
        registerEvents(this, new BlockBreakListener(),new BlockPlaceListener(),new ChunkUnloadListener(),new EntityDamageEntityListener());

    }

    @Override
    public void onDisable() {
        saveMaps();
        plugin = null;
    }
    public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    public void loadSavedMaps(){
        SaveNLoad  snl = new SaveNLoad();
        boolean load = snl.loadHashMap();
        if(load){
            System.out.println("[LBmuchomore] Maps loaded!");
        }else{
            System.out.println("[LBmuchomore] Something went wrong while trying to load maps!");
        }
    }
    public void saveMaps(){
        SaveNLoad  snl = new SaveNLoad();
        boolean save = snl.saveHashMap();
        if(save){
            System.out.println("[LBmuchomore] Maps saved!");
        }else{
            System.out.println("[LBmuchomore] Something went wrong, while trying to save hashMaps!");
        }
    }

    private void setUpHashMaps(){
        prizesNamesMap = new HashMap<>();
        allArmorStands = new ArrayList<>();
        luckyBlockLocationUUID = new HashMap<>();
        luckyBlockLocationType = new HashMap<>();
        playerLuckyBlockLocations = new HashMap<>();
        luckyBlockArmorStands = new HashMap<>();
        luckyBlockTextStands = new HashMap<>();
        maps = new HashMapsGettersSetters();


        prizesMap = new HashMap<>();
        chances = new HashMap<>();
        commandsMap = new HashMap<>();
        messagesMap = new HashMap<>();
        itemsMap = new HashMap<>();
        luckyBlocks = new ArrayList<>();
        allChancesMap = new HashMap<>();
    }


    private void loadLuckyBlocks(){
        utilisMuchomore.loadLuckyBlocks();
    }

    public void reloadData(){
        this.utilisMuchomore = new UtilisMuchomore();
        saveMaps();
        setUpHashMaps();
        loadLuckyBlocks();
        loadSavedMaps();
    }

    public int random(int spread){
        Random rand = new Random();
        return rand.nextInt(spread)+1;
    }
    public int getChancesValue(ItemStack luckyBlock){
        for(Map.Entry<ItemStack,Integer>entry:chances.entrySet()){
            if(entry.getKey().isSimilar(luckyBlock)){
                return entry.getValue();
            }
        }
        return 1;

    }
    public UUID rollPrize(ItemStack luckyBlock){
        int rand = random(getChancesValue(luckyBlock));

        LinkedList<UUID> prizes = new LinkedList<>();
        for(Map.Entry<ItemStack,LinkedList<UUID>> entry:prizesMap.entrySet()){
            if(entry.getKey().isSimilar(luckyBlock)){
                prizes = new LinkedList<>(entry.getValue());
            }
        }

        if(prizes.isEmpty())return null;

        int x = 0;
        UUID first = UUID.randomUUID();
        for(UUID id: prizes){
            int chance = allChancesMap.getOrDefault(id,100);
            if(chance>rand){
                return id;
            }
            if(x==0){
                first = id;
            }
            x++;
        }
        return first;
    }
}
