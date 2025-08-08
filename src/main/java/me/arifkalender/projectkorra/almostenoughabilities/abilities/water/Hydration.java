package me.arifkalender.projectkorra.almostenoughabilities.abilities.water;

/*
 * Cropların üstündeyken shift basarsan ve yakınında su varsa cropları hızlı büyütür
 *
 */

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.PlantAbility;
import com.projectkorra.projectkorra.ability.SubAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.*;

public class Hydration extends PlantAbility implements AddonAbility, SubAbility {

    @Attribute("Power")
    private double power;
    @Attribute("WaterSearchRadius")
    private double waterSearchRadius;
    @Attribute(Attribute.RADIUS)
    private double effectRadius;

    public Hydration(Player player) {
        super(player);
        if(bPlayer.canBend(this) && !hasAbility(player, Hydration.class)){
            player.sendMessage("sa");
            setFields();
            start();
        }
    }

    private void setFields(){
        this.power = plugin.getConfig().getDouble("Abilities.Water.Hydration.Power");
        this.waterSearchRadius = plugin.getConfig().getDouble("Abilities.Water.Hydration.WaterSearchRadius");
        this.effectRadius = plugin.getConfig().getDouble("Abilities.Water.Hydration.EffectRadius");
    }

    boolean canGrow=false;
    List<Block> nearbyBlocks = new ArrayList<>();
    @Override
    public void progress() {
        player.sendMessage("!!!");
        if(!player.isOnline() || player.isDead()){
            remove();
            return;
        }
        if(!bPlayer.canBend(this) || !player.isSneaking()){
            remove();
            return;
        }

        nearbyBlocks = GeneralMethods.getBlocksAroundPoint(player.getLocation(), waterSearchRadius);
        for(Block block : nearbyBlocks){
            if(block.getType() == Material.WATER){
                canGrow=true;
                break;
            }
            canGrow=false;
        }

        if(canGrow){
            player.sendMessage("cangrow");
            for(Block block : GeneralMethods.getBlocksAroundPoint(player.getLocation(), effectRadius)){
                player.sendMessage("blocks");
                if(block.getBlockData() instanceof Ageable){
                    player.sendMessage("ageable");
                    block.getWorld().spawnParticle(Particle.RAIN, block.getLocation().add(0,0.2,0), 2, 0.5, 0.1, 0.5, 0.05);
                    if(random.nextDouble(0,100)<=power){
                        player.sendMessage("random");
                        Ageable ageable = (Ageable) block.getBlockData();
                        if(ageable.getAge() < ageable.getMaximumAge()){
                            player.sendMessage("maxage");
                            block.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, block.getLocation().add(0,0.2,0), 2, 0.5, 0.1, 0.5, 0.05);
                            ageable.setAge(ageable.getAge()+1);
                            block.setBlockData(ageable);
                        }
                    }
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
        return 0;
    }

    @Override
    public String getName() {
        return "Hydration";
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
        return version;
    }

    @Override
    public String getInstructions(){
        return plugin.getConfig().getString("Strings.Water.Hydration.Instructions");
    }

    @Override
    public String getDescription(){
        return plugin.getConfig().getString("Strings.Water.Hydration.Description");
    }

    @Override
    public boolean isEnabled(){
        return plugin.getConfig().getBoolean("Abilities.Water.Hydration.Enabled");
    }
}
