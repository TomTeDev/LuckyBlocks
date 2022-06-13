package me.muchomore;

import me.muchomore.ConfigFiles.ConfigManager;
import me.muchomore.ConfigFiles.ConfigMethods;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

public class CreateAnimation extends BukkitRunnable {


    Player player;
    Location loc;

    UUID prize;
    UtilisMuchomore utilisMuchomore;
    HashMapsGettersSetters maps;
    ConfigMethods cfm;
    Material particle;
    Location blockLoc;
    UUID blockOwner;
    ItemStack helmet;


    EnumParticle particleType;
    Material particleBlock;
    int particleAmount;
    int ticks = 0;
    int newTicks = 0;
    double var = 0;
    boolean runOnce = true;
    boolean doonce = true;
    static Plugin plugin = LuckyBlocks.getPlugin(LuckyBlocks.class);
    static  LuckyBlocks lb = LuckyBlocks.getPlugin(LuckyBlocks.class);
    public EnumParticle validateParticle(String path,EnumParticle reserve){
        EnumParticle particle;
        try{
            particle = EnumParticle.valueOf(path);
        }catch (Exception e){
            particle = reserve;
        }
        return particle;
    }
    @SuppressWarnings("deprecation")

    public CreateAnimation(Location loc, Player player, ItemStack helmet, UUID prize,Location blockLocation, UUID blockOwnerId,String pathToLuckyBlock) {
        this.utilisMuchomore = new UtilisMuchomore();
        this.maps = new HashMapsGettersSetters();
        this.cfm = new ConfigMethods(new ConfigManager(plugin, "luckyBlocks.yml"));
        this.particleAmount  = cfm.getInt(pathToLuckyBlock+".item.particle_amount",40);
        this.particleBlock = validateMaterial(pathToLuckyBlock+".item.particle_block_type");
        this.particleType = validateParticle(pathToLuckyBlock+".item.particle_type",EnumParticle.BLOCK_CRACK);
        this.blockLoc  = blockLocation;
        this.blockOwner = blockOwnerId;
        this.helmet = helmet;
        UUID playerId = player.getUniqueId();
        maps.addLocation(helmet,loc,playerId);
        loc.add(0.5,0,0.5);
        stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);

        stand.setArms(true);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setSmall(true);
        stand.setHelmet(helmet);
        stand.setNoDamageTicks(175);
        maps.addArmorStand(playerId,stand);
        lb.allArmorStands.add(stand);
        this.loc = loc;
        this.player = player;

        this.prize = prize;


    }
    ArmorStand stand;
    ArmorStand text;

    int tickses = 80;
    int opositeTicks = tickses;
    double addition = 3.6/tickses;
    double t = 0;
    double tTwo = 0;
    @Override
    public void run() {
        if (ticks <= 90) {
            if(ticks <= 60){

                EulerAngle oldrot = stand.getHeadPose();
                EulerAngle newrot = oldrot.add(0, 0.2f, 0);
                stand.setHeadPose(newrot);
                stand.teleport(stand.getLocation().add(0,0.08,0));
                if (ticks % 10 == 0) {
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
                }
                if (ticks % 8 == 0) {

                    var+= Math.PI/2;

                    Location locone = stand.getLocation();
                    //Dividing by "2" makes flames to be closer to each other
                    Location loctwo = locone.clone().add(Math.cos(var)/2,0,Math.sin(var)/2);
                    Location locthree = locone.clone().add(-Math.cos(var)/2,0,Math.sin(var)/2);
                    Location locfour = locone.clone().add(Math.cos(var)/2,0,-Math.sin(var)/2);
                    Location locfive = locone.clone().add(-Math.cos(var)/2,0,-Math.sin(var)/2);




                    playParticlesFlame(loc.getWorld(), loctwo,"FLAME",1,null);
                    playParticlesFlame(loc.getWorld(), locthree,"FLAME",1,null);
                    playParticlesFlame(loc.getWorld(), locfour,"FLAME",1,null);
                    playParticlesFlame(loc.getWorld(), locfive,"FLAME",1,null);
                }
            }else{
                EulerAngle oldrot = stand.getHeadPose();
                EulerAngle newrot = oldrot.add(0, 0.2f, 0);
                stand.setHeadPose(newrot);
            }

            ticks = ticks + 2;
        } else {
            if (runOnce) {
                stand.setHelmet(new ItemStack(Material.AIR));
                loc = stand.getLocation().clone().subtract(0,0.6,0);
                stand.setRightArmPose(new EulerAngle(-(Math.PI / 2), -0.69, 0));
                stand.teleport(stand.getLocation().clone().add(0,1,0));
                stand.setSmall(false);
                //stand.setHelmet(lb.itemsMap.getOrDefault(prize,new ItemStack(Material.DIAMOND_BLOCK)));
                stand.setItemInHand(lb.itemsMap.getOrDefault(prize,new ItemStack(Material.DIAMOND_BLOCK)));
                playParticles(loc.getWorld(), stand.getLocation().clone().add(0,-0.01,0),particleAmount,particleType,particleBlock);
                player.playSound(player.getLocation(), Sound.EXPLODE, 1f, 1f);
                runOnce = false;

                //Text in the up
                text = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
                removeArmorStand(text.getLocation(),100);
                removeArmorStand(stand.getLocation(),100);
                maps.addTextStand(player.getUniqueId(),text);
                String title = lb.prizesNamesMap.getOrDefault(prize,ChatColor.GOLD+"Secret prize");
                title = title.replace("{player}", player.getName()+" ");
                title = title.replace("{amount}", lb.itemsMap.getOrDefault(prize,new ItemStack(Material.DIAMOND_BLOCK)).getAmount() + " x ");
                title = title.replace("{item}", utilisMuchomore.getName(lb.itemsMap.getOrDefault(prize,new ItemStack(Material.DIAMOND_BLOCK))));
                title = utilisMuchomore.stringColorDeserializer(title);
                text.setCustomName(title);
                text.setCustomNameVisible(true);
                text.setGravity(false);
                text.setVisible(false);
                lb.allArmorStands.add(text);
            }

            if (newTicks <= tickses) {
                newTicks++;

                double constant = 2*Math.PI/60;

                tTwo = tTwo+constant;

                //51 0,12 0.34
                stand.teleport(getLocationAroundCircle(loc, 0.4708, tTwo));


            } else {

                utilisMuchomore.removeArmorStand(player);
                utilisMuchomore.removeTexttand(player);
                utilisMuchomore.deleteLocation(loc);

                player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 1f, 1f);
                utilisMuchomore.givePrize(player,prize);
                lb.maps.removeLocation(blockLoc);
                lb.maps.removePlayerLuckyBlockLocation(blockOwner,loc);
                lb.allArmorStands.remove(text);
                lb.allArmorStands.remove(stand);

                cancel();
                return;
            }
        }
    }


    public void removeArmorStand(Location loc,int ticks){
        Bukkit.getScheduler().scheduleSyncDelayedTask(LuckyBlocks.getPlugin(LuckyBlocks.class), new Runnable() {
            @Override
            public void run() {
                for(Entity ent: loc.getWorld().getEntities()){
                    if(!(ent instanceof ArmorStand))continue;
                    Location locEnt = ent.getLocation();
                    if(locEnt.getBlockX()==loc.getBlockX()&&locEnt.getBlockZ()==loc.getBlockZ()){
                        ent.remove();
                    }
                }
            }
        },ticks);
    }
    public Material validateMaterial(String path){
        String mat = cfm.getString(path,"SOUL_sAND");
        Material material;
        try{
            material = Material.valueOf(mat);
        }catch (Exception e){
            material = Material.DIAMOND_BLOCK;
            System.out.println(ChatColor.DARK_RED+"WRONG material value inside luckyBlock.yml config near path: "+path);
        }
        return material;
    }
    public void playParticlesFlame(World world, Location loc, String particle, int amount,ItemStack helmet) {
        PacketPlayOutWorldParticles packet
                = new PacketPlayOutWorldParticles(EnumParticle.valueOf(particle.toString()),
                false,
                (float) loc.getX(),
                (float) loc.getY(),
                (float) loc.getZ(),
                0,
                0,
                0,
                0,
                amount);

        for (Player player:world.getPlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }

    }



    public void playParticles(World world, Location loc, int amount,EnumParticle particleType,Material particleBlock) {
        PacketPlayOutWorldParticles packet;
        int id = particleBlock.getId();
            packet  = new PacketPlayOutWorldParticles(particleType,
                    false,
                    (float) loc.getX(),
                    (float) loc.getY(),
                    (float) loc.getZ(),
                    0,
                    0,
                    0,
                    1,
                    amount,
                    id
            );





        for (Player player:world.getPlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }

    }
    private Location getLocationAroundCircle(Location center, double radius, double angleInRadian) {

        double x = center.getX() + radius * Math.cos(angleInRadian);
        double z = center.getZ() + radius * Math.sin(angleInRadian);
        double y = center.getY();

        Location loc = new Location(center.getWorld(), x, y, z);
        Vector difference = center.toVector().clone().subtract(loc.toVector());
        loc.setDirection(difference);

        return loc;
    }

}