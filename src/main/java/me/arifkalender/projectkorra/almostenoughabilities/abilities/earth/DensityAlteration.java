package me.arifkalender.projectkorra.almostenoughabilities.abilities.earth;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.TempFallingBlock;
import me.arifkalender.projectkorra.almostenoughabilities.util.UtilizationMethods;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.*;

public class DensityAlteration extends EarthAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeDuration;
    @Attribute(Attribute.DURATION)
    private long duration;
    private double dynamicRadius;
    @Attribute(Attribute.RADIUS)
    private double initialRadius;
    private Location centre;

    public DensityAlteration(Player player) {
        super(player);
        if (bPlayer.canBend(this) && !hasAbility(player, DensityAlteration.class)) {
            setFields();
            startCharge();
        }
    }

    private void setFields() {
        this.cooldown = plugin.getConfig().getLong("Abilities.Earth.DensityAlteration.Cooldown");
        this.chargeDuration = plugin.getConfig().getLong("Abilities.Earth.DensityAlteration.ChargeDuration");
        this.duration = plugin.getConfig().getLong("Abilities.Earth.DensityAlteration.Duration");
        this.initialRadius = plugin.getConfig().getDouble("Abilities.Earth.DensityAlteration.Radius");
        dynamicRadius = initialRadius;
        this.centre = player.getLocation();

        ring = UtilizationMethods.getRingXZ(centre.clone().add(0, -0.5, 0), dynamicRadius, (int) (dynamicRadius * (Math.PI * 2)));
    }

    private void startCharge() {
        long startTime = System.currentTimeMillis();
        new BukkitRunnable() {
            boolean canStart = false;

            @Override
            public void run() {
                if(!bPlayer.getBoundAbilityName().equalsIgnoreCase(getName())){
                    remove();
                    this.cancel();
                    return;
                }

                //0.5 sn geçmişse demek alttaki
                if (startTime + chargeDuration < System.currentTimeMillis()) {
                    canStart = true;
                }
                if (canStart) {
                    if (!player.isSneaking()) {
                        setFields();
                        start();
                        this.cancel();
                    } else {
                        player.getWorld().spawnParticle(Particle.SMOKE, player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.5)), 1, 0, 0, 0, 0.05);
                    }
                } else {
                    if (!player.isSneaking()) {
                        remove();
                        this.cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }


    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreBinds(this) || this.getStartTime() + duration < System.currentTimeMillis()) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if (!player.isOnline() || player.isDead()) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        pullEntities();
        playAnimation();
    }

    private void pullEntities() {
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(centre, initialRadius)) {
            if (entity != player && entity.getType() != EntityType.FALLING_BLOCK) {

                if (this.isEarthbendable(entity.getLocation().add(0, -1, 0).getBlock())) {
                    Vector target = centre.toVector().subtract(entity.getLocation().toVector());
                    entity.setVelocity(target.normalize());

                }
            }
        }
    }

    List<Location> ring = new ArrayList<>();

    private void playAnimation() {
        dynamicRadius -= initialRadius / 20;
        if (dynamicRadius <= 0.7) {
            dynamicRadius = initialRadius;
        }
        ring = UtilizationMethods.getRingXZ(centre.clone().add(0, -0.5, 0), dynamicRadius, (int) (dynamicRadius * (Math.PI * 2)));

        for (Location point : ring) {
            if (random.nextInt(0, 100) < 15) {
                if (isEarthbendable(point.getBlock())) {
                    if (point.clone().add(0, 1, 0).getBlock().getType() != Material.AIR) {
                        new TempFallingBlock(point.clone().add(0, 1.75, 0), point.getBlock().getBlockData(), new Vector(0, 0.1, 0), CoreAbility.getAbility(DensityAlteration.class));
                        point.getWorld().playSound(point, Sound.BLOCK_TUFF_BREAK, 0.3f, 0.5f);
                    }
                    if (point.getBlock().getType() == Material.AIR) {
                        new TempFallingBlock(point.clone().add(0, -0.25, 0), point.getBlock().getBlockData(), new Vector(0, 0.1, 0), CoreAbility.getAbility(DensityAlteration.class));
                        point.getWorld().playSound(point, Sound.BLOCK_TUFF_BREAK, 0.3f, 0.5f);
                    }
                    new TempFallingBlock(point.clone().add(0, 0.75, 0), point.getBlock().getBlockData(), new Vector(0, 0.1, 0), CoreAbility.getAbility(DensityAlteration.class));
                    point.getWorld().playSound(point, Sound.BLOCK_TUFF_BREAK, 0.3f, 0.5f);
                    point.getWorld().playSound(point, Sound.UI_STONECUTTER_TAKE_RESULT, 0.3f, 0.5f);
                    point.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, point.clone().add(0, 1, 0), 4, 0.3, 0.5, 0.5, 0.05, point.getBlock().getBlockData());
                }
            }
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
        return "DensityAlteration";
    }

    @Override
    public Location getLocation() {
        return centre;
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
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("Abilities.Earth.DensityAlteration.Enabled");
    }

    @Override
    public String getInstructions() {
        return plugin.getConfig().getString("Strings.Earth.DensityAlteration.Description");
    }

    @Override
    public String getDescription() {
        return plugin.getConfig().getString("Strings.Earth.DensityAlteration.Instructions");
    }
}
