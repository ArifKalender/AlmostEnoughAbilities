package me.arifkalender.projectkorra.almostenoughabilities.listeners.fire;

import com.projectkorra.projectkorra.BendingPlayer;
import me.arifkalender.projectkorra.almostenoughabilities.abilities.fire.BlazeRush;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;

public class BlazeRushListener implements Listener {

    @EventHandler
    private void onClick(PlayerAnimationEvent event){
        if(event.getPlayer().isSneaking()){
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());
            if(bPlayer.getBoundAbilityName().equalsIgnoreCase("BlazeRush")){
                new BlazeRush(event.getPlayer());
            }
        }
    }
}
