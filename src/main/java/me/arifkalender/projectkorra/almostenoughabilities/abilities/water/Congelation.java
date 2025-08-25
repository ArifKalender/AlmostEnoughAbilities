package me.arifkalender.projectkorra.almostenoughabilities.abilities.water;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.IceAbility;
import com.projectkorra.projectkorra.ability.SubAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;
import me.arifkalender.projectkorra.almostenoughabilities.util.TempBlockDisplay;
import me.arifkalender.projectkorra.almostenoughabilities.util.UtilizationMethods;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.*;

public class Congelation extends IceAbility implements AddonAbility, SubAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.RANGE)
    private int range;
    @Attribute(Attribute.SELECT_RANGE)
    private int selectRange;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.RADIUS)
    private double radius;
    @Attribute(Attribute.SPEED)
    private float speed;
    @Attribute(Attribute.KNOCKUP)
    private float knockUp;
    @Attribute(Attribute.HEIGHT)
    private int maxHeight;

    private Block selectedBlock;
    private Location location;
    private Vector direction;
    private boolean targetFound = false;

    public Congelation(Player player) {
        super(player);
        if(bPlayer.canBend(this) && !hasAbility(player, Congelation.class)){
            setFields();
            if(selectedBlock != null && this.isWaterbendable(selectedBlock)){
                start();
            }
        }
    }

    private void setFields(){
        cooldown = plugin.getConfig().getLong("Abilities.Water.Congelation.Cooldown");
        duration = plugin.getConfig().getLong("Abilities.Water.Congelation.Duration");
        damage = plugin.getConfig().getDouble("Abilities.Water.Congelation.Damage");
        radius = plugin.getConfig().getDouble("Abilities.Water.Congelation.Radius");
        speed = (float) plugin.getConfig().getDouble("Abilities.Water.Congelation.Speed");
        knockUp = (float) plugin.getConfig().getDouble("Abilities.Water.Congelation.KnockUp");
        range = plugin.getConfig().getInt("Abilities.Water.Congelation.Range");
        maxHeight = plugin.getConfig().getInt("Abilities.Water.Congelation.MaxHeight");
        selectRange = plugin.getConfig().getInt("Abilities.Water.Congelation.SelectRange");

        selectedBlock = getWaterSourceBlock(player, selectRange, false);
        if(selectedBlock != null)
        location = selectedBlock.getLocation().add(0.5, 0.5, 0.5);
    }

    @Override
    public void progress() {
        if(!bPlayer.canBend(this) || location.distance(player.getLocation())>range || !location.clone().add(0,0.6,0).getBlock().isPassable()){
            removeProperly();
            return;
        }
        if(!player.isOnline() || player.isDead()) {
            removeProperly();
            return;
        }

        //Play waterbending particles if not clicked
        if(!hasClicked()){
            UtilizationMethods.playWaterSelectParticles(location);
            return;
        }
        if(!targetFound) progressLocation();
    }

    private void progressLocation(){
        Location previousLocation = location.clone();
        while(location.distance(previousLocation) <= speed/5){
            location.add(direction.normalize().multiply(0.1));
            TempBlockDisplay tBD = new TempBlockDisplay(location.clone().add(random.nextDouble(-0.5,0.5),random.nextDouble(0,0.1),random.nextDouble(-0.5,0.5)), Material.BLUE_ICE.createBlockData(), 40, 0.6f);
            tBD.getBlockDisplay().setTeleportDuration(4);
            new BukkitRunnable(){
                @Override
                public void run() {
                    tBD.getBlockDisplay().teleport(tBD.getBlockDisplay().getLocation().add(0,1,0));
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            tBD.getBlockDisplay().teleport(tBD.getBlockDisplay().getLocation().add(0,-1,0));
                        }
                    }.runTaskLater(plugin,5);
                }
            }.runTaskLater(plugin, 5);


            location.getWorld().playSound(location, Sound.BLOCK_SNOW_BREAK, 1, 0);
            entityDetection(location);
        }
    }

    private void entityDetection(Location detectLocation){
        for(Entity entity : GeneralMethods.getEntitiesAroundPoint(detectLocation, 1)){
            if(entity instanceof LivingEntity && entity != player){
                targetFound = true;
                DamageHandler.damageEntity(entity, damage, this);
                new BukkitRunnable(){
                    long start = System.currentTimeMillis();
                    int startY =(int) entity.getLocation().getY();
                    @Override
                    public void run() {
                        if(player.isSneaking()){
                            if(start + (duration*3) < System.currentTimeMillis()){
                                this.cancel();
                                removeProperly();
                            }
                        }else{
                            if(start + duration < System.currentTimeMillis()){
                                this.cancel();
                                removeProperly();
                            }
                        }


                        //The statement start + duration + 150 < System.currentTimeMillis() is added to make sure no tempblocks spawn near the end of the ability
                        if(Math.abs(player.getLocation().getY() - startY) < maxHeight && ((start + duration) - 150) > System.currentTimeMillis()){
                            punishEntity((LivingEntity) entity);
                        }
                    }
                }.runTaskTimer(plugin, 0, 1);
            }
        }
    }
    List<TempBlock> toRevert = new ArrayList<>();
    private void punishEntity(LivingEntity entity){
        entity.setVelocity(new Vector(0, knockUp, 0));
        for(Block block : GeneralMethods.getBlocksAroundPoint(entity.getLocation(), radius)){
            //To make sure that the block the entity is in DEFINITELY gets frozen I use the statement below
            if(block.getY() == (int)entity.getLocation().getY())
                if(block.isPassable()){
                TempBlock tB = new TempBlock(block, Material.ICE.createBlockData(), this);
                toRevert.add(tB);
            }
        }
    }
    private void removeProperly(){
        remove();
        bPlayer.addCooldown(this);
        toRevert.forEach(tempBlock -> {
            tempBlock.revertBlock();
            tempBlock.getBlock().getWorld().spawnParticle(Particle.SNOWFLAKE, tempBlock.getLocation(), 2, 0.65,0.65,0.65,0.1);
            tempBlock.getBlock().getWorld().playSound(tempBlock.getLocation(), Sound.BLOCK_SNOW_BREAK, 1, 0);
        });;
        toRevert.clear();
        return;
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
        return cooldown;
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

    private boolean click = false;
    public void triggerClick(){
        direction = new Vector(player.getEyeLocation().getDirection().getX(), 0, player.getEyeLocation().getDirection().getZ());
        direction = direction.normalize();
        click = true;
    }
    public boolean hasClicked(){
        return click;
    }

    public Block getSelectedBlock() {
        return selectedBlock;
    }

    public void setSelectedBlock(Block selectedBlock) {
        this.selectedBlock = selectedBlock;
    }

    public int getSelectRange() {
        return selectRange;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("Abilities.Water.Congelation.Enabled");
    }

    @Override
    public String getInstructions() {
        return plugin.getConfig().getString("Strings.Water.Congelation.Instructions");
    }

    @Override
    public String getDescription() {
        return plugin.getConfig().getString("Strings.Water.Congelation.Description");
    }
}