package me.arifkalender.projectkorra.almostenoughabilities.listeners.earth;

import com.projectkorra.projectkorra.BendingPlayer;
import me.arifkalender.projectkorra.almostenoughabilities.abilities.earth.CrustSpikes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;

public class CrustSpikesListener implements Listener {

    @EventHandler
    private void onClick(PlayerAnimationEvent event){
        Player player = event.getPlayer();
        if (player.isSneaking()){
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
            if(bPlayer.getBoundAbilityName().equalsIgnoreCase("CrustSpikes")){
                new CrustSpikes(player);
            }
        }
    }

}
