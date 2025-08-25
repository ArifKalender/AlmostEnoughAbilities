package me.arifkalender.projectkorra.almostenoughabilities.abilities.air;

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
import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.version;
import static me.arifkalender.projectkorra.almostenoughabilities.util.UtilizationMethods.isIgnored;

public class WindScythe extends AirAbility implements AddonAbility {

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.WIDTH)
    private double width;

    private Vector direction;
    private Location location, origin;
    private Location rightPoint, leftPoint, temp;
    public WindScythe(Player player) {
        super(player);
        if(bPlayer.canBend(this) && !hasAbility(player, WindScythe.class)){
            setFields();
            start();
        }
    }
    private void setFields(){
        cooldown=plugin.getConfig().getLong("Abilities.Air.WindScythe.Cooldown");
        range=plugin.getConfig().getDouble("Abilities.Air.WindScythe.Range");
        width =plugin.getConfig().getDouble("Abilities.Air.WindScythe.Width");

        location=player.getLocation().add(0,0.1,0);
        origin=location.clone();
        bPlayer.addCooldown(this);
        direction = new Vector(location.getDirection().getX(), 0, location.getDirection().getZ());
        direction = direction.normalize();
        rightPoint = GeneralMethods.getRightSide(location.clone().add(direction.clone().multiply(range)), width);
        leftPoint = GeneralMethods.getLeftSide(location.clone().add(direction.clone().multiply(range)), width);
        temp=rightPoint.clone();
    }

    @Override
    public void progress() {
        if(!bPlayer.canBendIgnoreCooldowns(this)){
            remove();
            return;
        }
        temp = GeneralMethods.getLeftSide(temp, 0.3);
        if(temp.distance(leftPoint)<=0.6){
            remove();
            return;
        }

        sweepAnimation();
    }

    private void sweepAnimation(){
        location=origin.clone();
        for(double i = 0; i<=range; i+=0.45) {
            location.add(temp.toVector().subtract(location.toVector()).normalize().multiply(0.45));
            if(!location.getBlock().isPassable()){
                break;
            }
            playAirbendingParticles(location,1,0.05,0.05,0.05);
            replaceCrop(location.getBlock());
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
        return location;
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
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("Abilities.Air.WindScythe.Enabled");
    }

        @Override
    public String getInstructions() {
        return plugin.getConfig().getString("Strings.Air.WindScythe.Instructions");
    }

    @Override
    public String getDescription() {
        return plugin.getConfig().getString("Strings.Air.WindScythe.Description");
    }
}
