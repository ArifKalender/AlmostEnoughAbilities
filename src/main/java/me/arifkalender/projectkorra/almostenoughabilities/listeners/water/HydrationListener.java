package me.arifkalender.projectkorra.almostenoughabilities.listeners.water;

import com.projectkorra.projectkorra.BendingPlayer;
import me.arifkalender.projectkorra.almostenoughabilities.abilities.water.Hydration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class HydrationListener implements Listener {

    @EventHandler
    private void onSneak(PlayerToggleSneakEvent event){
        if(event.isSneaking()){
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());
            if(bPlayer.getBoundAbilityName().equalsIgnoreCase("Hydration")){
                new Hydration(event.getPlayer());
            }
        }
    }

}
