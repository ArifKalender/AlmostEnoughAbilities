package me.arifkalender.projectkorra.almostenoughabilities.abilities.earth;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.util.TempFallingBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.plugin;
import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.version;

public class TremorWave extends EarthAbility implements AddonAbility {

    private long cooldown;
    private long duration;
    private double speed;
    private double damage;
    private double radius;

    private Location location;
    public TremorWave(Player player) {
        super(player);
        if(bPlayer.canBend(this) && !hasAbility(player, TremorWave.class)){
            setFields();
            start();
        }
    }
    private void setFields(){
        this.cooldown = plugin.getConfig().getLong("Abilities.Earth.TremorWave.Cooldown");
        this.duration = plugin.getConfig().getLong("Abilities.Earth.TremorWave.Duration");
        this.speed = plugin.getConfig().getDouble("Abilities.Earth.TremorWave.Speed");
        this.damage = plugin.getConfig().getDouble("Abilities.Earth.TremorWave.Damage");
        this.radius = plugin.getConfig().getDouble("Abilities.Earth.TremorWave.Radius");
        this.location=player.getLocation();
        this.location.setY(player.getLocation().getY()-0.5);
    }

    @Override
    public void progress() {
        if(!bPlayer.canBend(this) || this.duration + this.getStartTime() < System.currentTimeMillis() ||
                !bPlayer.getBoundAbilityName().equalsIgnoreCase(getName())){
            remove();
            player.sendMessage("1");
            bPlayer.addCooldown(this);
            return;
        }
        if(location.getBlock().getType() != Material.AIR && !isEarthbendable(location.getBlock())){
            remove();
            player.sendMessage("3");
            bPlayer.addCooldown(this);
            return;

        }
        if(!player.isOnline() || player.isDead()){
            player.sendMessage("2");
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        this.location=player.getLocation();
        this.location.setY(player.getLocation().getY()-0.5);
        setVelocity();
        prepareDamage(location);
    }
    private void setVelocity(){
        Vector velocity = new Vector(player.getEyeLocation().getDirection().getX(),0,player.getEyeLocation().getDirection().getZ()).normalize();
        velocity = velocity.multiply(speed);
        velocity.setY(-10);
        player.setVelocity(velocity);
    }
    // [14:23:47 INFO]: Kugelbltz lost connection: Internal Exception: io.netty.handler.codec.EncoderException: java.lang.IllegalStateException: zip file closed
    // Muhtemelen alttaki metottan kaynaklanıyor

    private void prepareDamage(Location origin){
        // Left
        new BukkitRunnable(){
            double steps=0;
            Location leftOrigin = origin;
            @Override
            public void run() {
                if (!CoreAbility.hasAbility(player, TremorWave.class)) {
                    this.cancel();
                    return;
                }

                if(leftOrigin.distance(origin) >= radius){
                    this.cancel();
                }
                if(isEarthbendable(leftOrigin.getBlock())){
                    if(!leftOrigin.getBlock().equals(origin.getBlock())){
                        Material type = leftOrigin.getBlock().getType();
                        new TempBlock(leftOrigin.getBlock(), Material.AIR.createBlockData(), 100, CoreAbility.getAbility(TremorWave.class));
                        new TempFallingBlock(leftOrigin, type.createBlockData(), new Vector(0,0.1,0), CoreAbility.getAbility(TremorWave.class));
                        for(Entity entity : GeneralMethods.getEntitiesAroundPoint(leftOrigin, 1)){
                            if(entity instanceof Damageable && entity != player){
                                DamageHandler.damageEntity(entity, player, damage, CoreAbility.getAbility(TremorWave.class));
                            }
                        }
                    }
                }else{
                    if(leftOrigin.getBlock().getType()!=Material.AIR) this.cancel();;
                }
                steps+=0.25;
                leftOrigin = GeneralMethods.getLeftSide(leftOrigin, steps);
            }
        }.runTaskTimer(plugin, 0, 1);
        // Right
        new BukkitRunnable(){
            double steps=0;
            Location rightOrigin = origin;
            @Override
            public void run() {
                if (!CoreAbility.hasAbility(player, TremorWave.class)) {
                    this.cancel();
                    return;
                }

                if(rightOrigin.distance(origin) >= radius){
                    this.cancel();
                }
                if(isEarthbendable(rightOrigin.getBlock())){
                    if(!rightOrigin.getBlock().equals(origin.getBlock())){
                        Material type = rightOrigin.getBlock().getType();
                        new TempBlock(rightOrigin.getBlock(), Material.AIR.createBlockData(), 100, CoreAbility.getAbility(TremorWave.class));
                        new TempFallingBlock(rightOrigin, type.createBlockData(), new Vector(0,0.1,0), CoreAbility.getAbility(TremorWave.class));
                        for(Entity entity : GeneralMethods.getEntitiesAroundPoint(rightOrigin, 1)){
                            if(entity instanceof Damageable && entity != player){
                                DamageHandler.damageEntity(entity, player, damage, CoreAbility.getAbility(TremorWave.class));
                            }
                        }
                    }
                }else{
                    if(rightOrigin.getBlock().getType()!=Material.AIR) this.cancel();;
                }
                steps+=0.25;
                rightOrigin = GeneralMethods.getRightSide(rightOrigin, steps);
            }
        }.runTaskTimer(plugin, 0, 1);
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
        return "TremorWave";
    }

    @Override
    public Location getLocation() {
        return this.location;
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
        return plugin.getConfig().getBoolean("Abilities.Earth.TremorWave.Enabled");
    }

    @Override
    public String getInstructions() {
        return plugin.getConfig().getString("Strings.Earth.TremorWave.Instructions");
    }

    @Override
    public String getDescription() {
        return plugin.getConfig().getString("Strings.Earth.TremorWave.Description");
    }
}
