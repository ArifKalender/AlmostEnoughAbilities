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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.plugin;

public class BlazeRush extends FireAbility implements AddonAbility {

    @DayNightFactor(invert = true)
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
    int points = 16;
    int i = 0;
    private static Set<Player> SPINNING = new HashSet<>();
    Particle particle;

    public BlazeRush(Player player) {
        super(player);
        if (bPlayer.canBend(this) && !hasAbility(player, BlazeRush.class)) {
            setFields();
            SPINNING.add(player);
            setRiptide(player, true);
            start();
        }
    }

    private void setFields() {
        cooldown = plugin.getConfig().getLong("Abilities.Fire.BlazeRush.Cooldown");
        duration = plugin.getConfig().getLong("Abilities.Fire.BlazeRush.Duration");
        fireTicks = plugin.getConfig().getInt("Abilities.Fire.BlazeRush.FireTicks");
        speed = plugin.getConfig().getDouble("Abilities.Fire.BlazeRush.Speed");
        damage = plugin.getConfig().getDouble("Abilities.Fire.BlazeRush.Damage");
        controllable = plugin.getConfig().getBoolean("Abilities.Fire.BlazeRush.Controllable");
        direction = player.getEyeLocation().getDirection().normalize().multiply(speed);

        if (bPlayer.hasElement(Element.BLUE_FIRE) && bPlayer.isElementToggled(Element.BLUE_FIRE)) {
            particle = Particle.SOUL_FIRE_FLAME;
        } else {
            particle = Particle.FLAME;
        }
    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this) || this.getStartTime() + duration <= System.currentTimeMillis()) {
            remove();
            bPlayer.addCooldown(this);
            SPINNING.remove(player);
            setRiptide(player, false);
            return;
        }
        if (!player.isOnline() || player.isDead()) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if (controllable) direction = player.getEyeLocation().getDirection().normalize().multiply(speed);
        player.setVelocity(direction);
//        if (bPlayer.hasElement(Element.BLUE_FIRE) && bPlayer.isElementToggled(Element.BLUE_FIRE)) {
//            player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation(), 18, 1.5, 1.5, 1.5, 0.05f);
//        } else {
//            player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 18, 1.5, 1.5, 1.5, 0.05f);
//        }

        formSpiral(0.5);
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), 2)) {
            if (entity instanceof Damageable && entity != player) {
                entity.setFireTicks(fireTicks);
                DamageHandler.damageEntity(entity, damage, this);
            }
        }
    }
    private double angle = 0;
    private final double angularSpeed = Math.PI * 1.5; // radians per second, bump for faster
    private long lastTime = System.currentTimeMillis();
    private final double spiralExpansionPerRevolution = 0; // >0 makes radius grow each loop
    private void formSpiral(double baseRadius) {
        Location center = player.getLocation();

        Vector dir = direction.clone();
        if (dir.lengthSquared() < 1e-6) dir = center.getDirection(); // fallback
        dir.normalize();

        Vector up = new Vector(0, 1, 0);
        Vector right = dir.clone().crossProduct(up);
        if (right.lengthSquared() < 1e-6) right = up.clone().crossProduct(dir);
        right.normalize();
        Vector forward = right.clone().crossProduct(dir).normalize();

        long now = System.currentTimeMillis();
        double deltaSec = (now - lastTime) / 1000.0;
        lastTime = now;

        angle = (angle + angularSpeed * deltaSec) % (2 * Math.PI);
        double currentRadius = baseRadius + spiralExpansionPerRevolution * (angle / (2 * Math.PI));
        double x = Math.cos(angle) * currentRadius;
        double y = Math.sin(angle) * currentRadius;
        Vector offset = right.clone().multiply(x).add(forward.clone().multiply(y));

        center.getWorld().spawnParticle(particle, center.clone().add(offset), 3, 0.1, 0.1, 0.1, 0.05);
    }



    public static void setRiptide(Player player, boolean active) {
        if (active) {
            List<EntityData<?>> data = List.of(
                    new EntityData<>(8, EntityDataTypes.BYTE, (byte) 0x04)
            );
            WrapperPlayServerEntityMetadata setData = new WrapperPlayServerEntityMetadata(player.getEntityId(), data);

            for (Player p : Bukkit.getOnlinePlayers()) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(p, setData);
            }
        } else {
            // Send a new metadata packet to clear the flag
            List<EntityData<?>> data = List.of(
                    new EntityData<>(8, EntityDataTypes.BYTE, (byte) 0x00)
            );
            WrapperPlayServerEntityMetadata unsetData = new WrapperPlayServerEntityMetadata(player.getEntityId(), data);

            for (Player p : Bukkit.getOnlinePlayers()) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(p, unsetData);
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
        return plugin.getConfig().getBoolean("Abilities.Fire.BlazeRush.Enabled");
    }

    public static Set<Player> getSpinningPlayers() {
        return SPINNING;
    }
}
