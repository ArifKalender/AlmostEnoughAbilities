package me.arifkalender.projectkorra.almostenoughabilities.abilities.fire;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.attribute.markers.DayNightFactor;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Method;
import java.util.List;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.plugin;

public class BlazeRush extends EarthAbility implements AddonAbility {

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
            setRiptide(player);
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
            player.setFlying(false);
            return;
        }
        if(!player.isOnline() || player.isDead()){
            remove();
            bPlayer.addCooldown(this);
            player.setFlying(false);
            return;
        }
        if(controllable) direction = player.getEyeLocation().getDirection().normalize().multiply(speed);
        player.setVelocity(direction);
        if(bPlayer.hasElement(Element.BLUE_FIRE) && bPlayer.isElementToggled(Element.BLUE_FIRE)){
            player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation(), 18, 1.5,1.5,1.5, 0.05f);
        }else{
            player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 18, 1.5,1.5,1.5, 0.05f);
        }

        for(Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), 2)){
            if(entity instanceof Damageable && entity != player){
                entity.setFireTicks(fireTicks);
                DamageHandler.damageEntity(entity, damage, this);
            }
        }
    }
    public void setRiptide(Player player) {
        List<EntityData<?>> data = List.of(
                new EntityData<>(8, EntityDataTypes.BYTE, (byte) 0x04)
        );

        WrapperPlayServerEntityMetadata setData = new WrapperPlayServerEntityMetadata(player.getEntityId(), data);

        for (Player p : Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(p, setData);
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
        return true;
        //return plugin.getConfig().getBoolean("Abilities.Fire.BlazeRush.Enabled");
    }
}
