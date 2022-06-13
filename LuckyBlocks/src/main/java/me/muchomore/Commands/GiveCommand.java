package me.muchomore.Commands;

import me.muchomore.ConfigFiles.ConfigManager;
import me.muchomore.ConfigFiles.ConfigMethods;
import me.muchomore.LuckyBlocks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GiveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))return false;
        Player p = (Player)commandSender;
        String errorMessage = ChatColor.GREEN+"Proper command is: "+ChatColor.YELLOW+"/giveLuckyBlock [Player_Nickname] [amount] [luckyBlockName]";

        LuckyBlocks lb = LuckyBlocks.getPlugin(LuckyBlocks.class);
        if(strings.length==0){
            p.sendMessage(ChatColor.YELLOW+"Use /luckyblocks help");
            return true;
        }
        ConfigManager cfg = new ConfigManager(LuckyBlocks.getPlugin(LuckyBlocks.class),"luckyBlocks.yml");
        String commandSecond = strings[0];
        switch (commandSecond){
            case "give":{
                if(!p.hasPermission("luckyblock.admin.commands")||!p.isOp())return false;
                if(strings.length==4){
                    String nick = strings[1];
                    String luckyBlock = strings[2];
                    String amount = strings[3];


                    if(nick==null||amount==null){
                        p.sendMessage(errorMessage);
                        return false;
                    }
                    Player g = Bukkit.getPlayer(nick);
                    if(g==null||!g.isOnline()){
                        p.sendMessage(ChatColor.RED+"That player is not online or does not exist at all!");
                        return false;
                    }
                    int amountInteger;
                    try{
                        amountInteger =Integer.parseInt(amount);
                    }catch (NumberFormatException e){
                        p.sendMessage(ChatColor.RED+"Wrong number format!");
                        p.sendMessage(errorMessage);
                        return false;
                    }
                    ItemStack itemStack = lb.utilisMuchomore.getLuckyBlock(luckyBlock);

                    if(itemStack==null){
                        p.sendMessage(ChatColor.RED+"There is no luckyblock with given name");
                        return false;
                    }

                    ItemStack[] invContents = g.getInventory().getContents();
                    for(ItemStack it: invContents){
                        if(it==null)continue;
                        if(!it.hasItemMeta())continue;
                        if(it.isSimilar(itemStack)){
                            g.sendMessage(ChatColor.GREEN+"You received "+amountInteger+" lucky blocks!");
                            amountInteger += it.getAmount();
                            it.setAmount(amountInteger);
                            g.getInventory().setContents(invContents);
                            g.updateInventory();
                            return true;
                        }
                    }

                    int empty = g.getInventory().firstEmpty();
                    if(empty>=0){
                        itemStack.setAmount(amountInteger);
                        g.getInventory().setItem(empty,itemStack);
                    }else{
                        g.getWorld().dropItem(g.getLocation(),itemStack);
                    }
                    g.sendMessage(ChatColor.GREEN+"You received "+amountInteger+" lucky blocks!");
                    return true;
                }else{
                    p.sendMessage(ChatColor.YELLOW+"Use /luckyblocks help");
                    return false;
                }
            }
            case "list":{
                if(!p.hasPermission("luckyblock.admin.commands")||!p.isOp())return false;
                if(cfg.getConfig().getConfigurationSection("luckyblocks")==null||cfg.getConfig().getConfigurationSection("luckyblocks").getKeys(false).isEmpty()){
                   p.sendMessage(ChatColor.RED+"Seems like there are no luckyblocks created!");
                     return false;
                }
                p.sendMessage(ChatColor.GREEN+"Luckyblocks list:");
                StringBuilder listLuckyBlocks = new StringBuilder();
                boolean x = true;
                for(String someBlocks :cfg.getConfig().getConfigurationSection("luckyblocks").getKeys(false)){
                    String collored = ChatColor.YELLOW+someBlocks;
                    if(x){
                        listLuckyBlocks.append(collored);
                        x = false;
                    }else{
                        listLuckyBlocks.append(", ").append(collored);
                    }
                }
                p.sendMessage(listLuckyBlocks.toString());
                return true;
            }
            case "help":{
                ConfigMethods cfm = new ConfigMethods(cfg);
                List<String> list = cfm.getStringList("help",new ArrayList<>());
                for(String ss:list){
                    p.sendMessage(lb.utilisMuchomore.stringColorDeserializer(ss));
                }
                return true;
            }
            case "reload":{
                if(!p.hasPermission("luckyblock.admin.commands")||!p.isOp())return false;
                cfg.reloadConfig();
                lb.reloadData();
                p.sendMessage(ChatColor.GREEN+"Config reloaded!");
                return true;
            }
            default:{
                p.sendMessage(ChatColor.YELLOW+"Use /luckyblocks help");
                return true;
            }
        }


    }
}
