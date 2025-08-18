package me.arifkalender.projectkorra.almostenoughabilities.abilities.air;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.*;

public class AirPocket extends AirAbility implements AddonAbility {


    @Attribute(Attribute.COOLDOWN)
    long cooldown;
    @Attribute(Attribute.DURATION)
    long duration;
    @Attribute("Power")
    double power;
    @Attribute(Attribute.RADIUS)
    double radius;
    @Attribute(Attribute.RANGE)
    double range;
    Location particleLocation;
    int particleDensity;

    public AirPocket(Player player) {
        super(player);
        setFields();
        if(bPlayer.canBend(this) && ! hasAbility(player, AirPocket.class)){
            start();
        }
    }

    private void setFields(){
        this.cooldown = plugin.getConfig().getLong("Abilities.Air.AirPocket.Cooldown");
        this.duration = plugin.getConfig().getLong("Abilities.Air.AirPocket.Duration");
        this.power = plugin.getConfig().getLong("Abilities.Air.AirPocket.Power");
        this.range = plugin.getConfig().getLong("Abilities.Air.AirPocket.Range");
        this.radius = plugin.getConfig().getLong("Abilities.Air.AirPocket.Radius");
        this.particleDensity = plugin.getConfig().getInt("Abilities.Air.AirPocket.ParticleDensity");
    }




    @Override
    public void progress() {
        if(!bPlayer.canBend(this) || !player.isSneaking() || this.getStartTime() + duration <= System.currentTimeMillis()){
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if(!player.isOnline() || player.isDead()){
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        particleLocation = GeneralMethods.getTargetedLocation(player, range);
        playAnimation();
        pullEntities();
    }

    Vector randomVector;
    Location temp;
    private void playAnimation(){
        for(int i = 0; i <= particleDensity; i++){

            particleLocation.getWorld().playSound(particleLocation, Sound.ENTITY_HORSE_BREATHE, 1, 0);
            randomVector = new Vector(random.nextDouble(-1.5,1.5),random.nextDouble(-1.5,1.5),random.nextDouble(-1.5,1.5));
            temp = particleLocation.clone().add(randomVector);
            double x = particleLocation.clone().toVector().subtract(temp.toVector()).getX();
            double y = particleLocation.clone().toVector().subtract(temp.toVector()).getY();
            double z = particleLocation.clone().toVector().subtract(temp.toVector()).getZ();
            temp.getWorld().spawnParticle(Particle.CLOUD, temp, 0, x, y, z, 0.125);

        }
    }
    private void pullEntities(){
        for(Entity entity : GeneralMethods.getEntitiesAroundPoint(particleLocation, radius)){
            if(entity == player) return;
            Vector velocity = particleLocation.toVector().subtract(entity.getLocation().toVector()).normalize().multiply(power);
            entity.setVelocity(velocity);
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
        return "AirPocket";
    }

    @Override
    public Location getLocation() {
        return particleLocation;
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
        return plugin.getConfig().getString("Strings.Air.AirPocket.Description");
    }

    @Override
    public String getInstructions() {
        return plugin.getConfig().getString("Strings.Air.AirPocket.Instructions");
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("Abilities.Air.AirPocket.Enabled");
    }
}
