package me.arifkalender.projectkorra.almostenoughabilities.listeners.earth;

import com.projectkorra.projectkorra.BendingPlayer;
import me.arifkalender.projectkorra.almostenoughabilities.abilities.earth.DensityAlteration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class DensityAlterationListener implements Listener {

    @EventHandler
    private void onSneak(PlayerToggleSneakEvent event){
        if(event.isSneaking()){
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());
            if(bPlayer.getBoundAbilityName().equalsIgnoreCase("DensityAlteration")){
                new DensityAlteration(event.getPlayer());
            }
        }
    }

}
