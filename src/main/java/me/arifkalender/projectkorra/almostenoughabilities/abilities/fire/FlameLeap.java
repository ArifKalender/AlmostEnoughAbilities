package me.arifkalender.projectkorra.almostenoughabilities.abilities.fire;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.attribute.markers.DayNightFactor;
import com.projectkorra.projectkorra.util.DamageHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.*;

public class FlameLeap extends FireAbility implements AddonAbility {

    @Attribute(Attribute.FIRE_TICK)
    private int fireTicks;
    @Attribute("MaxJumps")
    private int maxJumps;
    @DayNightFactor (invert = true)
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.KNOCKBACK)
    private double xzPower;
    private double yPower;


    public FlameLeap(Player player) {
        super(player);
        if (bPlayer.canBend(this) && !hasAbility(player, FlameLeap.class)) {
            setFields();
            start();
        }
    }

    private void setFields() {
        this.fireTicks = plugin.getConfig().getInt("Abilities.Fire.FlameLeap.FireTicks");
        this.maxJumps = plugin.getConfig().getInt("Abilities.Fire.FlameLeap.MaxJumps");
        this.cooldown = plugin.getConfig().getLong("Abilities.Fire.FlameLeap.Cooldown");
        this.duration = plugin.getConfig().getLong("Abilities.Fire.FlameLeap.FizzleOutTime");
        this.damage = plugin.getConfig().getDouble("Abilities.Fire.FlameLeap.Damage");
        this.xzPower = plugin.getConfig().getDouble("Abilities.Fire.FlameLeap.XZPower");
        this.yPower = plugin.getConfig().getDouble("Abilities.Fire.FlameLeap.YPower");
    }


    List<Entity> damaged = new ArrayList<>();
    @Override
    public void progress() {
        if (this.getStartTime() + duration < System.currentTimeMillis() || maxJumps <= 0) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if (!bPlayer.canBend(this) || !player.isOnline() || player.isDead() || player.getLocation().getBlock().getType() == Material.WATER) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        applyDamage();
        applyParticles();
    }
    private void applyDamage(){
        for(Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), 0.5)){
            if(entity instanceof Damageable){
                if(!damaged.contains(entity) && entity != player){
                    damaged.add(entity);
                    DamageHandler.damageEntity(entity, damage, this);
                    entity.setFireTicks(fireTicks);
                }
            }
        }
    }

    Particle particle;
    private void applyParticles(){
        if(bPlayer.hasElement(Element.BLUE_FIRE) && bPlayer.isElementToggled(Element.BLUE_FIRE)){
            particle = Particle.SOUL_FIRE_FLAME;
        }else{
            particle=Particle.FLAME;
        }

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_AZALEA_STEP, 1, 0);
        Vector particleTargetVector;
        if(player.isSneaking()){
            particleTargetVector = player.getEyeLocation().getDirection().multiply(2);
        }else{
            particleTargetVector = player.getEyeLocation().getDirection().multiply(-2);
        }

        particleTargetVector.setY(0);
        Location particleTarget = player.getLocation().add(particleTargetVector);
        Vector finalTarget = particleTarget.toVector().subtract(player.getLocation().toVector());
        for(int i = 0; i<=5; i++) {
            double xRandom, yRandom, zRandom;
            xRandom = finalTarget.getX()+random.nextDouble(-0.3,0.3);
            yRandom = finalTarget.getY()+random.nextDouble(-0.3,0.3);
            zRandom = finalTarget.getZ()+random.nextDouble(-0.3,0.3);
            player.getWorld().spawnParticle(particle, player.getLocation(), 0, xRandom,yRandom,zRandom,0.15,null,true);
            player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 0,xRandom, yRandom,zRandom,0.15,null,true);
        }

    }

    @Override
    public boolean isSneakAbility() {
        return false;
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
        return "FlameLeap";
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public void load() {
        ProjectKorra.plugin.getServer().getLogger().fine("Loaded " + getName());
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
    public String getInstructions() {
        return plugin.getConfig().getString("Strings.Fire.FlameLeap.Instructions");
    }

    @Override
    public String getDescription() {
        return plugin.getConfig().getString("Strings.Fire.FlameLeap.Description");
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("Abilities.FlameLeap.Enabled");
    }

    public void jumpPlayer() {
        Vector jump = player.getEyeLocation().getDirection().normalize();
        jump.setX(jump.getX() * xzPower);
        jump.setZ(jump.getZ() * xzPower);
        jump.setY(yPower);
        if(player.isSneaking()){
            jump=jump.multiply(-1);
            jump.setY(yPower);
            player.setVelocity(jump);
        }else {
            player.setVelocity(jump);
        }
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.3F, 0);
        spawnBoom(player.getLocation());
        maxJumps--;
        damaged.clear();
        if (maxJumps <= 0) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }
    }
    private void spawnBoom(Location location){
        for(int i = 0;i<=25;i++){
            Location temp = location.clone();
            double xTarget = location.getX()+random.nextDouble(-1.75,1.75);
            double yTarget = location.getY()+random.nextDouble(-1.75,1.75);
            double zTarget = location.getZ()+random.nextDouble(-1.75,1.75);
            temp.setX(xTarget);
            temp.setY(yTarget);
            temp.setZ(zTarget);
            Vector target = temp.toVector().subtract(location.toVector());
            Particle toSpawn = Particle.CLOUD;

            location.getWorld().spawnParticle(toSpawn, location, 0, target.getX(), target.getY(), target.getZ(), 0.1);
        }
    }

}
