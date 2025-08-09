package me.arifkalender.projectkorra.almostenoughabilities.util;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.plugin;

public class TempBlockDisplay {
    private BlockDisplay display;
    public TempBlockDisplay(Location location, BlockData blockData, int durationInTicks){
        new TempBlockDisplay(location, blockData, durationInTicks, 0.997f);

    }
    public TempBlockDisplay(Location location, BlockData blockData, int durationInTicks, float size){
        display = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
        display.setBlock(blockData);
        Transformation transformation = display.getTransformation();
        transformation.getScale().set(size);
        display.setTransformation(transformation);

        new BukkitRunnable(){
            @Override
            public void run() {
                display.remove();
            }
        }.runTaskLater(plugin, durationInTicks);
    }



    public BlockDisplay getBlockDisplay() {
        return display;
    }

    public void setDisplay(BlockDisplay display) {
        this.display = display;
    }



}

