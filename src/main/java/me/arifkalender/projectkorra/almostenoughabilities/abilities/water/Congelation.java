package me.arifkalender.projectkorra.almostenoughabilities.abilities.water;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.IceAbility;
import com.projectkorra.projectkorra.ability.SubAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.attribute.markers.DayNightFactor;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.plugin;
import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.version;
import static me.arifkalender.projectkorra.almostenoughabilities.util.UtilizationMethods.playWaterSelectParticles;


/*
 * TempFallingBlock veya BlockDisplay
 * */

public class Congelation extends IceAbility implements AddonAbility, SubAbility {

    @DayNightFactor(invert = true)
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @DayNightFactor
    @Attribute(Attribute.DURATION)
    private long duration;
    @DayNightFactor
    @Attribute(Attribute.KNOCKUP)
    private double knockUp;
    @DayNightFactor
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @DayNightFactor
    @Attribute(Attribute.SPEED)
    private double speed;
    @DayNightFactor
    @Attribute(Attribute.SELECT_RANGE)
    private double selectRange;
    @DayNightFactor
    @Attribute(Attribute.RANGE)
    private double range;

    private static Set<Player> clicked = new HashSet<>();
    private Block selected;
    private Location location;
    private Vector direction;

    public Congelation(Player player) {
        super(player);
        if (bPlayer.canBend(this) || !hasAbility(player, Congelation.class)) {
            this.selected = WaterAbility.getWaterSourceBlock(player, selectRange, false);
            setFields();
            selectionLogic();
        }
    }

    public static Set<Player> clickOptions(){
        return clicked;
    }

    private void selectionLogic(){
        new BukkitRunnable(){
            @Override
            public void run() {
                if(clicked.contains(player)){
                    this.cancel();
                    player.sendMessage("79");
                    start();
                }else{
                    playWaterSelectParticles(location);
                    player.sendMessage("83");
                    if(player.isDead() || !player.isOnline() || player.getLocation().distance(location)>selectRange){
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void setFields(){
        this.cooldown = plugin.getConfig().getLong("Abilities.Water.Congelation.Cooldown");
        this.duration = plugin.getConfig().getLong("Abilities.Water.Congelation.Duration");
        this.knockUp = plugin.getConfig().getInt("Abilities.Water.Congelation.KnockUp");
        this.damage = plugin.getConfig().getDouble("Abilities.Water.Congelation.Damage");
        this.speed = plugin.getConfig().getDouble("Abilities.Water.Congelation.Speed");
        this.selectRange = plugin.getConfig().getDouble("Abilities.Water.Congelation.SelectRange");
        this.range = plugin.getConfig().getDouble("Abilities.Water.Congelation.Range");
        this.location=this.selected.getLocation().add(-0.5, -0.5, -0.5);
    }

    @Override
    public void progress() {
        if(this.direction==null){
            this.direction=player.getEyeLocation().getDirection();
        }

        if(player.isDead() || !player.isOnline()){
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if(!bPlayer.canBend(this) || player.getLocation().distance(location)>range){
            remove();
            bPlayer.addCooldown(this);
            return;
        }

        progressLocation();
    }
    private void progressLocation(){
        location.add(direction.normalize().multiply(speed));
        BlockDisplay ice = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
        ice.setBlock(Material.BLUE_ICE.createBlockData());
        new BukkitRunnable(){
            @Override
            public void run() {
                ice.remove();
            }
        }.runTaskLater(plugin, 20*3);
        freezeEntities();
    }
    private void freezeEntities(){
        for(Entity entity : GeneralMethods.getEntitiesAroundPoint(location, speed+0.5)){
            if(entity instanceof LivingEntity){
                new BukkitRunnable(){
                    long startTime = System.currentTimeMillis();
                    @Override
                    public void run() {
                        entity.setVelocity(new Vector(0, knockUp, 0));
                        for(Block block : GeneralMethods.getBlocksAroundPoint(entity.getLocation(), 2.5)){
                            if(block.getY() == entity.getLocation().getY()){
                                new TempBlock(block, Material.ICE.createBlockData(), duration);
                            }
                        }
                        if(startTime + duration < System.currentTimeMillis()){
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 1);
                if(damage > 0) DamageHandler.damageEntity(entity, damage, this);
                this.remove();
                return;
            }
        }
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "Congelation";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "Kugelbltz";
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return plugin.getConfig().getString("Strings.Water.Congelation.Description");
    }

    @Override
    public String getInstructions() {
        return plugin.getConfig().getString("Strings.Water.Congelation.Instructions");
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("Abilities.Water.Congelation.Enabled");
    }
}
