package me.arifkalender.projectkorra.almostenoughabilities.abilities.earth;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.*;

public class CrustSpikes extends EarthAbility implements AddonAbility {

    private long cooldown;
    private double damage;
    private double range;
    private double spikeMaxLength;
    private double spikeMinLength;
    private float spikeMaxPitch;
    private float spikeMinPitch;
    private double spikeWidthRadius;
    private double spikeFrequency;

    private Location progressing;
    private Location origin;
    private Vector direction;

    public CrustSpikes(Player player) {
        super(player);
        if (bPlayer.canBend(this) && !hasAbility(player, CrustSpikes.class)) {
            setFields();
            if (!this.isEarthbendable(progressing.getBlock())) {
                remove();
                return;
            }
            start();
        }
    }

    private void setFields() {
        this.cooldown = plugin.getConfig().getLong("Abilities.Earth.CrustSpikes.Cooldown");
        this.damage = plugin.getConfig().getDouble("Abilities.Earth.CrustSpikes.Damage");
        this.range = plugin.getConfig().getDouble("Abilities.Earth.CrustSpikes.Range");
        this.spikeMaxLength = plugin.getConfig().getDouble("Abilities.Earth.CrustSpikes.Spikes.MaxLength");
        this.spikeMinLength = plugin.getConfig().getDouble("Abilities.Earth.CrustSpikes.Spikes.MinLength");
        this.spikeMaxPitch = (float) plugin.getConfig().getDouble("Abilities.Earth.CrustSpikes.Spikes.MaxPitch");
        this.spikeMinPitch = (float) plugin.getConfig().getDouble("Abilities.Earth.CrustSpikes.Spikes.MinPitch");
        this.spikeWidthRadius = (float) plugin.getConfig().getDouble("Abilities.Earth.CrustSpikes.Spikes.WidthRadius");
        this.spikeFrequency = (float) plugin.getConfig().getDouble("Abilities.Earth.CrustSpikes.Spikes.Frequency");

        progressing = player.getLocation();
        progressing.setY(progressing.getY() - 0.5);
        origin = progressing.clone();
        direction = player.getEyeLocation().getDirection();
        direction.setY(0);
        direction.normalize();
    }

    private boolean remove = false;
    private boolean tryUp = false;
    @Override
    public void progress() {
        if (!bPlayer.isOnline() || player.isDead()) {
            remove();
            bPlayer.addCooldown(this);
            removeLogic();
            return;
        }
        if (progressing.distance(origin) > range || !bPlayer.canBend(this) || remove) {
            remove();
            bPlayer.addCooldown(this);
            removeLogic();
            return;
        }
        detectBendable();
        progressing.add(direction);
        spawnSpikes(progressing);
    }
    private void removeLogic(){
        if(!toRevert.isEmpty()){

            new BukkitRunnable(){
                @Override
                public void run() {
                    for (TempBlock tempBlock : toRevert) {
                        tempBlock.getLocation().getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, tempBlock.getLocation(), 7, 0.5, 0.5, 0.5, 0.05, tempBlock.getBlockData());
                        tempBlock.getLocation().getWorld().playSound(tempBlock.getLocation(), Sound.BLOCK_TUFF_BREAK, 0.3f, 0.5f);
                        tempBlock.revertBlock();
                    }
                    toRevert.clear();
                }
            }.runTaskLater(plugin, 20*6);

        }
    }

    private void detectBendable(){
        int attempts = 0;
        while(!this.isEarthbendable(progressing.getBlock())){
            progressing.setY(progressing.getY()-1);
            attempts++;
            if(attempts>=2){
                progressing.setY(progressing.getY()+attempts);
                remove=true;
                return;
            }
        }
        if(progressing.clone().add(0,1,0).getBlock().getType() != Material.AIR){
            if(this.isEarthbendable(progressing.clone().add(0,1,0).getBlock())){
                tryUp=true;
            }
        }
        while(tryUp){
            attempts++;
            progressing.setY(progressing.getY()+1);
            if(attempts>=2){
                tryUp=false;
                return;
            }
        }
    }

    private List<Entity> damaged = new ArrayList<>();
    private List<TempBlock> toRevert = new ArrayList<>();
    private void singleSpike(Location location) {
        Location originV2 = location.clone();
        Material type = location.getBlock().getType();
        if (type == Material.AIR) {
            location.setY(location.getY() - 1);
            type = location.getBlock().getType();
        }

        if (this.isEarthbendable(location.getBlock())) {
            Location v2 = location.clone();
            v2.setPitch(random.nextFloat(spikeMinPitch, spikeMaxPitch));
            v2.setYaw(random.nextFloat(-179.9f, 179.9f));
            Vector vector = v2.getDirection();

            double limit = random.nextDouble(spikeMinLength, spikeMaxLength);
            Material finalType = type;
            new BukkitRunnable() {
                int remove=0;
                @Override
                public void run() {
                    if(!isEarthbendable(location.clone().add(0,1,0).getBlock()) && location.clone().add(0,1,0).getBlock().getType()!=Material.AIR){
                        this.cancel();
                    }
                    TempBlock tB = new TempBlock(location.getBlock(), finalType.createBlockData(), CoreAbility.getAbility(CrustSpikes.class));
                    toRevert.add(tB);
                    location.getWorld().playSound(location, Sound.BLOCK_TUFF_BREAK, 1.5f, 0);
                    remove+=50;
                    location.add(vector);

                    for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 1)) {
                        if (!damaged.contains(entity) && entity != player && entity instanceof Damageable) {
                            DamageHandler.damageEntity(entity, player, damage, CoreAbility.getAbility(CrustSpikes.class));
                            entity.setVelocity(vector.multiply(0.6));
                            damaged.add(entity);
                        }
                    }

                    if (originV2.distance(location) > limit) {
                        this.cancel();
                    }
                    if(remove/50 >= 20*5){
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 1);
        }
    }

    private void spawnSpikes(Location location) {
        for (int i = 0; i <= spikeWidthRadius; i++) {
            if (this.isEarthbendable(GeneralMethods.getLeftSide(location, i).getBlock())) {
                if (random.nextDouble(0, 100) <= spikeFrequency) {
                    singleSpike(GeneralMethods.getLeftSide(location, i));
                }
            }
            if (this.isEarthbendable(GeneralMethods.getRightSide(location, i).getBlock())) {
                if (random.nextDouble(0, 100) <= spikeFrequency) {
                    singleSpike(GeneralMethods.getRightSide(location, i));
                }
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
        return "CrustSpikes";
    }

    @Override
    public Location getLocation() {
        return progressing;
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
        return plugin.getConfig().getString("Strings.Earth.CrustSpikes.Description");
    }

    @Override
    public String getInstructions() {
        return plugin.getConfig().getString("Strings.Earth.CrustSpikes.Instructions");
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("Abilities.Earth.CrustSpikes.Enabled");
    }
}
