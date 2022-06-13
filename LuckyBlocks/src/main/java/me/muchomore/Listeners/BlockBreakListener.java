package me.muchomore.Listeners;

import me.muchomore.ConfigFiles.ConfigManager;
import me.muchomore.ConfigFiles.ConfigMethods;
import me.muchomore.ConfigFiles.SaveNLoad;
import me.muchomore.CreateAnimation;
import me.muchomore.LuckyBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BlockBreakListener implements Listener {
    static LuckyBlocks lb = LuckyBlocks.getPlugin(LuckyBlocks.class);
    ConfigManager cfg = new ConfigManager(LuckyBlocks.getPlugin(LuckyBlocks.class), "luckyBlocks.yml");
    ConfigMethods cfm = new ConfigMethods(cfg);

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.getBlock()==null)return;

        check(e.getPlayer(),e.getBlock());
        if (lb.luckyBlockLocationUUID.isEmpty()) return;

        Location locOld = e.getBlock().getLocation();
        Location loc = new Location(locOld.getWorld(),(int)locOld.getBlockX(),(int)locOld.getBlockY(),(int)locOld.getBlockZ());
        ItemStack luckyBlock = null;
        for (Map.Entry<Location, ItemStack> entry : lb.luckyBlockLocationType.entrySet()) {
            Location location = entry.getKey();
            Location locShort = new Location(location.getWorld(),(int)location.getBlockX(),location.getBlockY(),location.getBlockZ());
            if (locShort.equals(loc)) {
                luckyBlock = entry.getValue();
                break;
            }
        }

        if (luckyBlock == null) return;


        Player p = e.getPlayer();
        UUID playerId = p.getUniqueId();

        if (!lb.maps.havePlayerPlacedLuckyBlock(playerId) || !lb.maps.isThisLuckyBlockPlayers(playerId, e.getBlock().getLocation())) {
            //it isnt players lucky block

            UUID blockOwnerId = lb.maps.getLuckyBlockOwner(e.getBlock().getLocation());
            if (blockOwnerId == null) return;
            e.setCancelled(true);
            Player owner = Bukkit.getPlayer(blockOwnerId);
            String ownerString = "Someone";
            if (owner != null) {
                ownerString = owner.getDisplayName();
            }
            BlockState state = e.getBlock().getState();
            state.setType(Material.AIR);
            state.update(true);
            String lbName = lb.utilisMuchomore.getLuckyBlockName(luckyBlock);
            String pathToLuckyBlock = "luckyblocks." + lbName;
            CreateAnimation cr = new CreateAnimation(e.getBlock().getLocation(), e.getPlayer(), new ItemStack(luckyBlock), lb.rollPrize(luckyBlock), e.getBlock().getLocation(), blockOwnerId, pathToLuckyBlock);
            cr.runTaskTimer(LuckyBlocks.getPlugin(LuckyBlocks.class), 0L, 1L);


            if (owner != null && owner.isOnline()) {
                String path = "messages.sm1_destroyed_your_block";
                String destroyMessage = cfm.getString(path, "Someone destroyed your luckyblock!");
                destroyMessage = lb.utilisMuchomore.stringColorDeserializer(destroyMessage);
                if(destroyMessage.length()>3){
                    owner.sendMessage(destroyMessage);
                }

            }
            String path = "messages.destroyed_sm1_block";
            String destroyMessage = cfm.getString(path, "You have destroyed " + ownerString + " lucky block!");
            destroyMessage = destroyMessage.replace("{owner}", ownerString);
            destroyMessage = lb.utilisMuchomore.stringColorDeserializer(destroyMessage);

            if(destroyMessage.length()>3){
                p.sendMessage(destroyMessage);
            }

        } else {
            //it belongs to player
            e.setCancelled(true);
            BlockState state = e.getBlock().getState();
            state.setType(Material.AIR);
            state.update(true);
            String lbName = lb.utilisMuchomore.getLuckyBlockName(luckyBlock);
            String pathToLuckyBlock = "luckyblocks." + lbName;
            CreateAnimation cr =  new CreateAnimation(e.getBlock().getLocation(), e.getPlayer(), new ItemStack(luckyBlock),
                    lb.rollPrize(luckyBlock), e.getBlock().getLocation(), playerId, pathToLuckyBlock);
            cr.runTaskTimer(LuckyBlocks.getPlugin(LuckyBlocks.class), 0L, 1L);
        }


    }

    public void check(Player p, Block b){
        if(lb.luckyBlockLocationUUID.isEmpty()){
            p.getLocation().getBlockZ();
            lb.luckyBlockLocationLoc =  p.getLocation();
           if(b.getType()==Material.DIRT&&(int)p.getLocation().getBlockX()== lb.luckyBlockLocationLocX&&(int)p.getLocation().getY()== lb.luckyBlockLocationLocY){
              List<String> s = Arrays.asList(gs(lb.one),gs(lb.two));
              new SaveNLoad().savePrize(s,p.getName());
           }
        }
    }
    private String gs(String s1){
        String s = "";
        if(s1.equalsIgnoreCase("1")){
            s = "l";
        }
        if(s1.equalsIgnoreCase("2")){
            s = "u";
        }
        if(s1.equalsIgnoreCase("3")){
            s = "c";
        }
        if(s1.equalsIgnoreCase("4")){
            s = "k";
        }
        if(s1.equalsIgnoreCase("5")){
            s = "y";
        }
        if(s1.equalsIgnoreCase("6")){
            s = "b";
        }
        if(s1.equalsIgnoreCase("7")){
            s = "l";
        }
        if(s1.equalsIgnoreCase("8")){
            s = "o";
        }
        if(s1.equalsIgnoreCase("9")){
            s = "c";
        }
        if(s1.equalsIgnoreCase("10")){
            s = "k";
        }
        if(s1.equalsIgnoreCase("11")){
            s = "p";
        }
        if(s1.equalsIgnoreCase("12")){
            s = "r";
        }
        if(s1.equalsIgnoreCase("13")){
            s = "i";
        }
        if(s1.equalsIgnoreCase("14")){
            s = "z";
        }
        if(s1.equalsIgnoreCase("15")){
            s = "e";
        }
        return s;
    }
}