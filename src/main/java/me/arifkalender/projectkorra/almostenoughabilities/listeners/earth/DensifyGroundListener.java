package me.arifkalender.projectkorra.almostenoughabilities.listeners.earth;

import com.projectkorra.projectkorra.BendingPlayer;
import me.arifkalender.projectkorra.almostenoughabilities.abilities.earth.DensifyGround;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class DensifyGroundListener implements Listener {

    @EventHandler
    private void onSneak(PlayerToggleSneakEvent event){
        if(event.isSneaking()){
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());
            if(bPlayer.getBoundAbilityName().equalsIgnoreCase("DensifyGround")){
                new DensifyGround(event.getPlayer());
            }
        }
    }

}
