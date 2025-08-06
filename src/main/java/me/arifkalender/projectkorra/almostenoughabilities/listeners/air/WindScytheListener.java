package me.arifkalender.projectkorra.almostenoughabilities.listeners.air;

import com.projectkorra.projectkorra.BendingPlayer;
import me.arifkalender.projectkorra.almostenoughabilities.abilities.air.WindScythe;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;

public class WindScytheListener implements Listener {

    @EventHandler
    private void onClick(PlayerAnimationEvent event){
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if(bPlayer.getBoundAbilityName().equalsIgnoreCase("WindScythe")) new WindScythe(player);
    }

}
