package me.arifkalender.projectkorra.almostenoughabilities.abilities.air;

/*
 * Oyuncuların önünde arc şeklinde bi hava olusturup cropları kırmasına yarar, kökleri korur ve kırmaz
 */

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.plugin;
import static me.arifkalender.projectkorra.almostenoughabilities.util.UtilizationMethods.isIgnored;

public class WindScythe extends AirAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute("WidthIncrement")
    private double widthIncrement;

    private Vector direction;
    private Location location, origin;
    public WindScythe(Player player) {
        super(player);
        if(bPlayer.canBend(this)){
            setFields();
            start();
        }
    }
    private void setFields(){
        cooldown=plugin.getConfig().getLong("Abilities.Air.WindScythe.Cooldown");
        range=plugin.getConfig().getDouble("Abilities.Air.WindScythe.Range");
        damage=plugin.getConfig().getDouble("Abilities.Air.WindScythe.Damage");
        widthIncrement=plugin.getConfig().getDouble("Abilities.Air.WindScythe.WidthIncrement");

        location=player.getLocation().add(0,0.1,0);
        origin=location.clone();
        bPlayer.addCooldown(this);
        direction = new Vector(location.getDirection().getX(), 0, location.getDirection().getZ());
        direction = direction.normalize();
    }

    @Override
    public void progress() {
        if(!bPlayer.canBendIgnoreCooldowns(this) || origin.distance(location)>=range){
            remove();
            return;
        }
        progressLocation();
    }
    private void progressLocation(){
        location.add(direction);
        replaceCrop(location.getBlock());
        Location left = location.clone();
        Location right = location.clone();
        playAirbendingParticles(location, 5, 0.1, 0.1, 0.1);
        for(double i = 0; i <= (location.distance(origin)/2); i++){
            left = GeneralMethods.getLeftSide(left, widthIncrement);
            right = GeneralMethods.getRightSide(right, widthIncrement);
            playAirbendingParticles(left, 5, 0.1, 0.1, 0.1);
            playAirbendingParticles(right, 5, 0.1, 0.1, 0.1);
            replaceCrop(left.getBlock());
            replaceCrop(right.getBlock());
        }
    }

    private void replaceCrop(Block block){
        if(block.getBlockData() instanceof Ageable){
            Ageable ageable = (Ageable) block.getBlockData();
            boolean isFullyGrown = ageable.getAge() == ageable.getMaximumAge();
            if(isFullyGrown && !isIgnored(block.getType())){
                ageable.setAge(0);
                block.setBlockData(ageable);
                dropCrop(block.getType(), location);
            }
        }
    }
    private void dropCrop(Material type, Location location){
        switch (type){
            case WHEAT -> {
                location.getWorld().dropItem(location, new ItemStack(Material.WHEAT_SEEDS));
                location.getWorld().dropItem(location, new ItemStack(Material.WHEAT));
            }
            case BEETROOT_SEEDS -> {
                location.getWorld().dropItem(location, new ItemStack(Material.BEETROOT_SEEDS));
                location.getWorld().dropItem(location, new ItemStack(Material.BEETROOT));
            }
            case CARROTS -> {
                location.getWorld().dropItem(location, new ItemStack(Material.CARROT, 1));
            }
            case POTATOES -> {
                location.getWorld().dropItem(location, new ItemStack(Material.POTATO, 1));
            }
            case NETHER_WART -> {
                location.getWorld().dropItem(location, new ItemStack(Material.NETHER_WART, 1));
            }
            case COCOA -> {
                location.getWorld().dropItem(location, new ItemStack(Material.COCOA, 1));
            }
            case SWEET_BERRY_BUSH -> {
                location.getWorld().dropItem(location, new ItemStack(Material.SWEET_BERRIES, 2));
            }
            default -> {

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
        return "WindScythe";
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
        return "";
    }

    @Override
    public String getVersion() {
        return "";
    }
}
