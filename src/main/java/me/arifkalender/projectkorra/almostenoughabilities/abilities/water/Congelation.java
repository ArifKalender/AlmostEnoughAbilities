package me.arifkalender.projectkorra.almostenoughabilities.abilities.water;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.IceAbility;
import com.projectkorra.projectkorra.ability.SubAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.attribute.markers.DayNightFactor;
import me.arifkalender.projectkorra.almostenoughabilities.util.UtilizationMethods;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
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
    @Attribute(Attribute.HEIGHT)
    private int height;
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

    public Congelation(Player player) {
        super(player);
        if (bPlayer.canBend(this) || !hasAbility(player, Congelation.class)) {
            setFields();
            selected = WaterAbility.getWaterSourceBlock(player, selectRange, false);
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
                    start();
                }else{
                    playWaterSelectParticles(location);
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
        this.height = plugin.getConfig().getInt("Abilities.Water.Congelation.Height");
        this.damage = plugin.getConfig().getDouble("Abilities.Water.Congelation.Damage");
        this.speed = plugin.getConfig().getDouble("Abilities.Water.Congelation.Speed");
        this.selectRange = plugin.getConfig().getDouble("Abilities.Water.Congelation.SelectRange");
        this.range = plugin.getConfig().getDouble("Abilities.Water.Congelation.Range");
        this.location=selected.getLocation().add(-0.5, -0.5, -0.5);
    }

    @Override
    public void progress() {
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
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "Congelation";
    }

    @Override
    public Location getLocation() {
        return null;
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
