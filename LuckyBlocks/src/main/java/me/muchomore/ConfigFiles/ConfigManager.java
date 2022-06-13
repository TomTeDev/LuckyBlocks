package me.muchomore.ConfigFiles;

import me.muchomore.LuckyBlocks;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class ConfigManager {
    private Plugin plugin;
    private FileConfiguration fileConfig = null;
    private File file = null;

    String fileName;

    public ConfigManager(Plugin plugin,String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
     /*   File f = new File("plugins/LuckyBlocks/"+fileName);
        if(!f.exists()){
            createCustomConfig(f);
        }*/
        saveDeafaultConfig();
    }
/*    private void createCustomConfig(File f) {
        f.getParentFile().mkdirs();
        try{
            f.createNewFile();
        }catch (Exception e){

        }

    }*/
    public void reloadConfig() {
        if (this.file == null) {
            this.file = new File(this.plugin.getDataFolder(), fileName);
        }
        this.fileConfig = YamlConfiguration.loadConfiguration(this.file);
        InputStream defaultStream = this.plugin.getResource(fileName);
        if (defaultStream != null) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.fileConfig.setDefaults(yamlConfiguration);
        }
    }

    public FileConfiguration getConfig() {
        if (this.fileConfig == null) reloadConfig();
        return this.fileConfig;
    }

    public void saveConfig() {
        if (this.fileConfig == null || this.file == null) return;
        try {
            this.getConfig().save(file);
        } catch (IOException exception) {
            this.plugin.getLogger().log(Level.SEVERE, ChatColor.DARK_RED + "Failed on trying to save "+fileName, exception);
        }
    }

    public void saveDeafaultConfig() {
        if (this.file == null) {
            this.file = new File(this.plugin.getDataFolder(), fileName);
        }
        if (!this.file.exists()) {
            this.plugin.saveResource(fileName, false);
        }
    }
}
