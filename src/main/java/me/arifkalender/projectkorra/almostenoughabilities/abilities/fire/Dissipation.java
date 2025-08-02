package me.arifkalender.projectkorra.almostenoughabilities.abilities.fire;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.attribute.markers.DayNightFactor;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities;
import me.arifkalender.projectkorra.almostenoughabilities.util.UtilizationMethods;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.plugin;
import static me.arifkalender.projectkorra.almostenoughabilities.util.UtilizationMethods.*;


/*
 * Uncontrolled: Yüksek cooldownlı, patlama tarzı büyük animasyonlu bi ateş bloklama skilli, hasar veriyor. Kullanımı, shift + sol klik ile ability açılıyor ve shift basılı tutuluyorsa ve yakalarsa tetikleniyor
 * Controlled: Orta cooldownlı, disk gibi bi ateş bloklama skilli. Kullanımı, shift + sol klik ile ability açılıyo ve x saniye içerisinde ability yakalarsa tetikleniyor.
 *
 * Uncontrolled animasyonu için zeminde etrafa doğru yayılan bir ateş diski oluşabilir. Gövde ve yukarısı için ne yapabilirim bilmiyorum. https://tenor.com/view/ay-fire-power-cartoon-avatar-gif-5696033
 * Controlled animasyonu önden korranın çıkardığı gibi bi disk çıkartabilir. https://64.media.tumblr.com/0c932b055225a141fd2fe223ba9b2d81/b7eedc1c8bc681c4-40/s500x750/e5b1d82e6a2df311a86061461486bac305ae3081.gifv
 *   */
public class Dissipation extends FireAbility implements AddonAbility {

    @Attribute(Attribute.DURATION)
    long duration;
    @DayNightFactor (invert = true)
    @Attribute(Attribute.COOLDOWN)
    long controlledCooldown;
    @DayNightFactor (invert = true)
    @Attribute("UncontrolledCooldown")
    long uncontrolledCooldown;
    @Attribute(Attribute.RADIUS)
    double uncontrolledRadius;
    @Attribute(Attribute.DAMAGE)
    double uncontrolledDamage;
    Location playerFeet;

    public Dissipation(Player player) {
        super(player);
        if (bPlayer.canBend(this) && !CoreAbility.hasAbility(player, CoreAbility.class)) {
            setFields();
            start();
        }
    }

    private void setFields() {
        duration = plugin.getConfig().getLong("Abilities.Fire.Dissipation.FizzleOutTime");
        controlledCooldown = plugin.getConfig().getLong("Abilities.Fire.Dissipation.Controlled.Cooldown");
        uncontrolledCooldown = plugin.getConfig().getLong("Abilities.Fire.Dissipation.Uncontrolled.Cooldown");
        uncontrolledRadius = plugin.getConfig().getDouble("Abilities.Fire.Dissipation.Uncontrolled.Radius");
        uncontrolledDamage = plugin.getConfig().getDouble("Abilities.Fire.Dissipation.Uncontrolled.Damage");
    }

    Particle userParticle;
    boolean remove;
    boolean isBlue = false;

    @Override
    public void progress() {
        if (this.getStartTime() + duration <= System.currentTimeMillis()) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }
        if (!bPlayer.isOnline() || player.isDead() || !bPlayer.canBend(this)) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }

        if (bPlayer.hasElement(Element.BLUE_FIRE) && bPlayer.isElementToggled(Element.BLUE_FIRE)) {
            userParticle = Particle.SOUL_FIRE_FLAME;
        } else {
            userParticle = Particle.FLAME;
        }
        playerFeet=player.getLocation();
        player.getWorld().spawnParticle(userParticle, getLocation().add(player.getEyeLocation().getDirection().multiply(0.5)), 1, 0, 0, 0, 0.05);

        for (CoreAbility ins : UtilizationMethods.getAbilitiesAroundPoint(getLocation(), 2.5)) {
            if (ins.getElement() == Element.FIRE || ins.getElement() == Element.BLUE_FIRE) {
                if (ins.getPlayer() != player) {
                    ins.remove();
                    remove = true;
                    if (ins.getBendingPlayer().hasElement(Element.BLUE_FIRE) && ins.getBendingPlayer().isElementToggled(Element.BLUE_FIRE)) {
                        isBlue = true;
                    } else {
                        isBlue = false;
                    }
                }
            }
        }
        if (remove) {
            if (player.isSneaking()) {
                uncontrolledDissipation();
            } else {
                controlledDissipation();
            }
        }

    }

    private void controlledDissipation() {
        playerFeet.getWorld().playSound(playerFeet, Sound.ENTITY_ARROW_SHOOT, 1, 0);
        for (Location point : getRingXYZ(getLocation(), player.getEyeLocation().getDirection(), 1.5, 33)) {
            //Player particles
            if(bPlayer.hasElement(Element.BLUE_FIRE) && bPlayer.isElementToggled(Element.BLUE_FIRE)){
                point.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, point, 5, 0.2,0.2,0.2,0.05);
            }else{
                point.getWorld().spawnParticle(Particle.FLAME, point, 5, 0.2,0.2,0.2,0.05);
            }

            //Attacker particles
            if (isBlue) {
                point.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, point, 5, 0.2,0.2,0.2,0.05);
            }else{
                point.getWorld().spawnParticle(Particle.FLAME, point, 5, 0.2,0.2,0.2,0.05);
            }
        }
        remove();
        bPlayer.addCooldown(this, controlledCooldown);
    }

    private void uncontrolledDissipation() {
        playerFeet.getWorld().playSound(playerFeet, Sound.ENTITY_ARROW_SHOOT, 1, 0);
        Location left = GeneralMethods.getLeftSide(playerFeet, 1);
        Location right = GeneralMethods.getRightSide(playerFeet, 1);
        left.setY(left.getY()+1);
        right.setY(right.getY()+1);

        left.getWorld().spawnParticle(userParticle, left, (int)(35*uncontrolledRadius), 0,0,0, uncontrolledRadius*0.05);
        right.getWorld().spawnParticle(userParticle, right, (int)(35*uncontrolledRadius), 0,0,0, uncontrolledRadius*0.05);

        new BukkitRunnable(){
            double i;
            @Override
            public void run() {
                i+=0.35;
                playerFeet.getWorld().playSound(playerFeet,Sound.BLOCK_BLASTFURNACE_FIRE_CRACKLE,1.35f,2);
                for(Location point : getRingXZ(playerFeet, i, (int) (10*i))){


                    //Player particles
                    if(bPlayer.hasElement(Element.BLUE_FIRE) && bPlayer.isElementToggled(Element.BLUE_FIRE)){
                        point.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, point, 2, 0.2,0.2,0.2,0.05);
                    }else{
                        point.getWorld().spawnParticle(Particle.FLAME, point, 2, 0.2,0.2,0.2,0.05);
                    }

                    //Attacker particles
                    if (isBlue) {
                        point.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, point, 2, 0.2,0.2,0.2,0.05);
                    }else{
                        point.getWorld().spawnParticle(Particle.FLAME, point, 2, 0.2,0.2,0.2,0.05);
                    }

                    //Damage logic
                    for(Entity entity : GeneralMethods.getEntitiesAroundPoint(point, 1)){
                        if(entity instanceof Damageable && entity != player){
                            DamageHandler.damageEntity(entity, uncontrolledDamage, CoreAbility.getAbility(Dissipation.class));
                        }
                    }
                }
                if(i>=uncontrolledRadius){
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
        remove();
        bPlayer.addCooldown(this);
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
        return controlledCooldown;
    }

    @Override
    public String getName() {
        return "Dissipation";
    }

    @Override
    public Location getLocation() {
        return player.getEyeLocation();
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
        return plugin.getConfig().getString("Strings.Fire.Dissipation.Description");
    }

    @Override
    public String getInstructions() {
        return plugin.getConfig().getString("Strings.Fire.Dissipation.Instructions");
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("Abilities.Dissipation.Enabled");
    }

}
