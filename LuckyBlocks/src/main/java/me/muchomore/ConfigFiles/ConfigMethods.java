package me.muchomore.ConfigFiles;

import me.muchomore.ConfigFiles.ConfigManager;
import me.muchomore.LuckyBlocks;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ConfigMethods {

    ConfigManager cfg;

    public ConfigMethods(ConfigManager cfg){
        this.cfg = cfg;
    }


    static Plugin plugin  = LuckyBlocks.getPlugin(LuckyBlocks.class);

    public boolean setInt(int value,String path){
        cfg.getConfig().set(path,value);
        cfg.saveConfig();
        return true;
    }
    public int getInt(String path,int reserveValue){
        return (cfg.getConfig().contains(path)&&(cfg.getConfig().get(path)!=null))?cfg.getConfig().getInt(path):reserveValue;
    }
    public boolean setString(String value,String path){
        cfg.getConfig().set(path,value);
        cfg.saveConfig();
        return true;
    }
    public String getString(String path,String reserveValue){
        return (cfg.getConfig().contains(path)&&(cfg.getConfig().get(path)!=null))?cfg.getConfig().getString(path):reserveValue;
    }
    public boolean setBoolean(boolean value,String path){
        cfg.getConfig().set(path,value);
        cfg.saveConfig();
        return true;
    }
    public boolean getBoolean(String path,boolean reserveValue){
        return (cfg.getConfig().contains(path)&&(cfg.getConfig().get(path)!=null))?cfg.getConfig().getBoolean(path):reserveValue;
    }
    public boolean setStringList(List<String> value, String path){
        cfg.getConfig().set(path,value);
        cfg.saveConfig();
        return true;
    }
    public List<String> getStringList(String path,List<String> reserveValue){
        return (cfg.getConfig().contains(path)&&(cfg.getConfig().get(path)!=null))?cfg.getConfig().getStringList(path):reserveValue;
    }
    public boolean setIntegerList(List<Integer> value, String path){
        cfg.getConfig().set(path,value);
        cfg.saveConfig();
        return true;
    }
    public List<Integer> getIntegerList(String path,List<Integer> reserveValue){
        return (cfg.getConfig().contains(path)&&(cfg.getConfig().get(path)!=null))?cfg.getConfig().getIntegerList(path):reserveValue;
    }
    public boolean setFloatList(List<Float> value, String path){
        cfg.getConfig().set(path,value);
        cfg.saveConfig();
        return true;
    }
    public List<Float> getFloatList(String path,List<Float> reserveValue){
        return (cfg.getConfig().contains(path)&&(cfg.getConfig().get(path)!=null))?cfg.getConfig().getFloatList(path):reserveValue;
    }
    public boolean setFloat(float value, String path){
        cfg.getConfig().set(path,value);
        cfg.saveConfig();
        return true;
    }
    public double getDouble(String path, double reservedValue){
        return (cfg.getConfig().contains(path)&&(cfg.getConfig().get(path)!=null))?cfg.getConfig().getDouble(path):reservedValue;
    }
}
