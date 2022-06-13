package me.muchomore;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.muchomore.ConfigFiles.ConfigManager;
import me.muchomore.ConfigFiles.ConfigMethods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class UtilisMuchomore {
    private HashMapsGettersSetters maps;

    final ConfigMethods cfm;
    final ConfigManager cfg;
    static LuckyBlocks lb = LuckyBlocks.getPlugin(LuckyBlocks.class);
    public UtilisMuchomore(){
        this.maps = lb.maps;
        this.cfm = new ConfigMethods(new ConfigManager(lb,"luckyBlocks.yml"));
        this.cfg = new ConfigManager(lb,"luckyBlocks.yml");
    }
    public boolean givePrize(Player whom,UUID itemId){
        if(whom!=null&&whom.isOnline()){
            List<String> commands = lb.commandsMap.getOrDefault(itemId,new ArrayList<>());
            for(String s:commands){
                s = s.replace("{player}",whom.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),s);
            }
            String message = lb.messagesMap.getOrDefault(itemId,null);
            if(message !=null&&message.length()>0){
                whom.sendMessage(message);
            }

        }
        return true;
    }
    public boolean removeArmorStand(Player whom){
        UUID playerId = whom.getUniqueId();
        ArmorStand aStand = maps.getArmorStand(playerId);

        if(aStand==null)return true;
      //  aStand.remove();
        maps.removeArmorStand(playerId);
        return true;
    }
    public boolean removeTexttand(Player whom){
        UUID playerId = whom.getUniqueId();
        ArmorStand aStand = maps.getTextStand(playerId);

        if(aStand==null)return true;
        //aStand.remove();
        maps.removeTextStand(playerId);
        return true;
    }
    public boolean deleteLocation(Location loc){
        maps.removeLocation(loc);
        return true;
    }
    public String getName(ItemStack item){
        if(item.hasItemMeta()&&item.getItemMeta().hasDisplayName()){
            return item.getItemMeta().getDisplayName();
        }else{
            return item.getType().name();
        }
    }
    public String stringColorDeserializer(String msg){
        if(msg==null)return null;
        return ChatColor.translateAlternateColorCodes('&',msg);
    }

    public List<String> validateListStringCommand(String path){
       return cfm.getStringList(path,new ArrayList<>());
    }
    public List<String> validateListString(String path){
       List<String> list =  cfm.getStringList(path,new ArrayList<>());
       List<String> coloredList = new ArrayList<>();
       for(String s: list){
           String colored = stringColorDeserializer(s);
           coloredList.add(colored);
       }
      return coloredList;
    }
    public String validateTexture(String path,String value){
      return cfm.getString(path,value);
    }
    public String validateDisplayName(String path){
        String s = cfm.getString(path,null);
        if(s==null)return null;
        return s;
    }
    public int validateChance(String path){
        int x = 0;
        try{
            x = cfm.getInt(path,100);
        }catch (Exception e){
            System.out.println(ChatColor.DARK_RED+"Missing NUMBER value inside luckyBlock.yml config near path: "+path);
            x=100;
        }
        return x;
    }
    public ItemStack validateMaterial(String matPath,String dataPath,String texturePath){

        if(matPath==null)return new ItemStack(Material.DIAMOND_BLOCK);
        String mat = cfm.getString(matPath,null);

        if(mat==null)return new ItemStack(Material.DIAMOND_BLOCK);
        Material m;
        try {

         m = Material.valueOf(mat);
        }catch (Exception e){

            return new ItemStack(Material.DIAMOND_BLOCK);
        }

        if(dataPath==null&&texturePath==null)return new ItemStack(m);
        String data = cfm.getString(dataPath,null);
        String texture = cfm.getString(texturePath,null);
        if(data==null&&texture==null)return new ItemStack(m);
        ItemStack item;
        if(data!=null&&texture==null){
            int dataInt = cfm.getInt(dataPath,0);
            try {

                item = new ItemStack(m, 1, (short)dataInt);
                return item;
            }catch (Exception e){

                return new ItemStack(Material.DIAMOND_BLOCK);
            }
        }
        if(m==Material.SKULL_ITEM){

            if(texture==null)return new ItemStack(m);
            item = getHead(texture);
            return item;
        }
        return new ItemStack(Material.DIAMOND_BLOCK);
    }

    public void loadLuckyBlocks(){
        String path = "luckyblocks";
        if(  cfg.getConfig().getConfigurationSection(path)==null||cfg.getConfig().getConfigurationSection(path).getKeys(false).isEmpty()){
            System.out.println("There are lucky blocks inside luckyBlocks.yml config!");
            cfg.getConfig().createSection(path);
        }
            List<ItemStack> luckyBlocks = new ArrayList<>();
            HashMap<ItemStack,LinkedList<UUID>> prizesMap = new HashMap<>();

            HashMap<ItemStack,Integer> chances = new HashMap<>();
            HashMap<UUID,List<String>> commandsMap = new HashMap<>();
            HashMap<UUID,String> messagesMap = new HashMap<>();
            HashMap<UUID,ItemStack> itemsMap = new HashMap<>();

        for(String s: cfg.getConfig().getConfigurationSection(path).getKeys(false)){
            //Loading LuckyBlock
            String newPath = path+"."+s+".item";
            String luckyBlockMaterial = newPath+".material";
            String luckyBlockData = newPath+".data";
            String luckyBlockTexture = newPath+".texture";
            String luckyBlockDisplayName = newPath+".name";
            String luckyBlockLore = newPath+".lore";

            ItemStack item = validateMaterial(luckyBlockMaterial,luckyBlockData,luckyBlockTexture);

            String displayName = stringColorDeserializer(validateDisplayName(luckyBlockDisplayName));
            List<String> lore = validateListString(luckyBlockLore);


            if(displayName==null&&lore.isEmpty()){
                luckyBlocks.add(item);
                return;
            }
            ItemMeta meta = item.getItemMeta();
            if(displayName!=null){

                meta.setDisplayName(displayName);
            }
            if(!lore.isEmpty()){
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
            luckyBlocks.add(item);

            //Loading Items(prizes) attached to that luckyBlock;

            int chance = 0;
            String pathToItems = path+"."+s+".items";
            if(  cfg.getConfig().getConfigurationSection(path)==null||cfg.getConfig().getConfigurationSection(path).getKeys(false).isEmpty()){
                System.out.println("There are no awards attached to "+luckyBlockDisplayName+" lucky block!");
                prizesMap.put(item,new LinkedList<UUID>());
                continue;
            }
            LinkedList<UUID> linkedListId = new LinkedList<>();
            for(String ss: cfg.getConfig().getConfigurationSection(pathToItems).getKeys(false)){
                String newPathItems = pathToItems+"."+ss;
                String itemsChance = newPathItems+".chance";
                String material = newPathItems+".material";
                String name = newPathItems+".name";
                String data = newPathItems+".data";
                String texturee = newPathItems+".texture";
                String message = newPathItems+".message";
                String commands = newPathItems+".commands";

                UUID prizeId = UUID.randomUUID();

                int chanceValue = validateChance(itemsChance);
                chance = chanceValue+chance;
                ItemStack itemPrize = validateMaterial(material,data,texturee);
                String before = validateDisplayName(name);
                String display_name_Value = stringColorDeserializer(before);
                String messageValue = stringColorDeserializer(validateTexture(message,""));
                List<String> commandsValue = validateListStringCommand(commands);
               // String dataValueString = validateTexture(data,null);



                if(display_name_Value!=null){
                    ItemMeta metaPrize = itemPrize.getItemMeta();
                    metaPrize.setDisplayName(display_name_Value);
                    itemPrize.setItemMeta(metaPrize);

                    lb.prizesNamesMap.put(prizeId,before);
                }
                commandsMap.put(prizeId,commandsValue);
                messagesMap.put(prizeId,messageValue);
                itemsMap.put(prizeId,itemPrize);
                linkedListId.add(prizeId);
                lb.allChancesMap.put(prizeId,chance);



            }

            chances.put(item,chance);
            prizesMap.put(item,linkedListId);
        }
        lb.prizesMap = null;
        lb.chances = null;
        lb.commandsMap = null;
        lb.messagesMap = null;
        lb.itemsMap = null;
        lb.luckyBlocks = null;

        lb.prizesMap = new HashMap<>(prizesMap);
        lb.chances = new HashMap<>(chances);
        lb.commandsMap = new HashMap<>(commandsMap);
        lb.messagesMap = new HashMap<>(messagesMap);
        lb.itemsMap = new HashMap<>(itemsMap);
        lb.luckyBlocks = new ArrayList<>(luckyBlocks);
    }

    public String getLuckyBlockName(ItemStack luckyBlock){
        if(cfg.getConfig().getConfigurationSection("luckyblocks")==null)return null;
        if(cfg.getConfig().getConfigurationSection("luckyblocks").getKeys(false).isEmpty())return null;
        for(String s: cfg.getConfig().getConfigurationSection("luckyblocks").getKeys(false)){
                String newPath = "luckyblocks."+ s+".item";
                if(!cfg.getConfig().contains(newPath))return s;
                String luckyBlockMaterial = newPath+".material";
                String luckyBlockDat = newPath+".data";
                String luckyBlockTexture = newPath+".texture";
                String luckyBlockDisplayName = newPath+".name";
                String luckyBlockLore = newPath+".lore";

                ItemStack item = validateMaterial(luckyBlockMaterial,luckyBlockDat,luckyBlockTexture);

                String displayName = stringColorDeserializer(validateDisplayName(luckyBlockDisplayName));
                List<String> lore = validateListString(luckyBlockLore);

                if(displayName==null&&lore.isEmpty()){

                }else{
                    ItemMeta meta = item.getItemMeta();
                    if(displayName!=null){
                        meta.setDisplayName(displayName);
                    }
                    if(!lore.isEmpty()){
                        meta.setLore(lore);
                    }
                    item.setItemMeta(meta);
                }

                if(item.isSimilar(luckyBlock)){
                    return s;
                }

        }
        return "";


    }

    public ItemStack getLuckyBlock(String luckyBlock){
        if(cfg.getConfig().getConfigurationSection("luckyblocks")==null)return null;
        if(cfg.getConfig().getConfigurationSection("luckyblocks").getKeys(false).isEmpty())return null;
        for(String s: cfg.getConfig().getConfigurationSection("luckyblocks").getKeys(false)){
            if(s.equalsIgnoreCase(luckyBlock)){
                String newPath = "luckyblocks."+ s+".item";
                if(!cfg.getConfig().contains(newPath))return null;
                String luckyBlockMaterial = newPath+".material";
                String luckyData = newPath+".data";
                String luckyBlockTexture = newPath+".texture";
                String luckyBlockDisplayName = newPath+".name";
                String luckyBlockLore = newPath+".lore";

                ItemStack item = validateMaterial(luckyBlockMaterial,luckyData,luckyBlockTexture);

                String displayName = stringColorDeserializer(validateDisplayName(luckyBlockDisplayName));
                List<String> lore = validateListString(luckyBlockLore);

                if(displayName==null&&lore.isEmpty()){
                    return item;
                }
                ItemMeta meta = item.getItemMeta();
                if(displayName!=null){
                    meta.setDisplayName(displayName);
                }
                if(!lore.isEmpty()){
                    meta.setLore(lore);
                }
                item.setItemMeta(meta);
                return item;
            }
        }
        return null;


    }


    private static ItemStack getHead(String texture) {

        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        if (texture.isEmpty())
            return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", texture));

        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);

        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

    public void disablePlugin(){
        LuckyBlocks.getPlugin(LuckyBlocks.class).getPluginLoader().disablePlugin(LuckyBlocks.getPlugin(LuckyBlocks.class));
    }
}
