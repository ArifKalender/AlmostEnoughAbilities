package me.arifkalender.projectkorra.almostenoughabilities.listeners.water;

import me.arifkalender.projectkorra.almostenoughabilities.abilities.water.Congelation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import static com.projectkorra.projectkorra.ability.CoreAbility.hasAbility;
import static me.arifkalender.projectkorra.almostenoughabilities.abilities.water.Congelation.clickOptions;

public class CongelationListener implements Listener {

    @EventHandler
    private void onSneak(PlayerToggleSneakEvent event){

    }

    @EventHandler
    private void onClick(PlayerAnimationEvent event){
        if(hasAbility(event.getPlayer(), Congelation.class)){
            clickOptions().add(event.getPlayer());
        }
    }
}
