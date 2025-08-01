package me.arifkalender.projectkorra.almostenoughabilities.abilities.fire;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.attribute.markers.DayNightFactor;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.plugin;

public class BlazeRush extends FireAbility implements AddonAbility {

    @DayNightFactor (invert = true)
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.FIRE_TICK)
    private int fireTicks;
    private boolean controllable;

    Vector direction;
    public BlazeRush(Player player) {
        super(player);
        if(bPlayer.canBend(this) && ! hasAbility(player, BlazeRush.class)){
            setFields();
            start();
        }
    }

    private void setFields(){
        cooldown = plugin.getConfig().getLong("Abilities.Fire.BlazeRush.Cooldown");
        duration = plugin.getConfig().getLong("Abilities.Fire.BlazeRush.Duration");
        fireTicks = plugin.getConfig().getInt("Abilities.Fire.BlazeRush.FireTicks");
        speed = plugin.getConfig().getDouble("Abilities.Fire.BlazeRush.Speed");
        damage = plugin.getConfig().getDouble("Abilities.Fire.BlazeRush.Damage");
        controllable = plugin.getConfig().getBoolean("Abilities.Fire.BlazeRush.Controllable");
        direction = player.getEyeLocation().getDirection().normalize().multiply(speed);
    }

    @Override
    public void progress() {
        if(!bPlayer.canBend(this) || this.getStartTime() + duration <= System.currentTimeMillis()){
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if(!player.isOnline() || player.isDead()){
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if(controllable) direction = player.getEyeLocation().getDirection().normalize().multiply(speed);
        player.setVelocity(direction);
        //Trident animasyonu
        //Partiküller
        for(Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), 2)){
            if(entity instanceof Damageable && entity != player){
                entity.setFireTicks(fireTicks);
                DamageHandler.damageEntity(entity, damage, this);
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
        return cooldown;
    }

    @Override
    public String getName() {
        return "BlazeRush";
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
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
        return AlmostEnoughAbilities.version;
    }

    @Override
    public String getDescription() {
        return plugin.getConfig().getString("Strings.Fire.BlazeRush.Description");
    }

    @Override
    public String getInstructions() {
        return plugin.getConfig().getString("Strings.Fire.BlazeRush.Instructions");
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("Abilities.Fire.BlazeRush.Enabled");
    }
}
