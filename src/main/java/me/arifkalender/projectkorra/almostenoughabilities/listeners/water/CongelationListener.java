package me.arifkalender.projectkorra.almostenoughabilities.listeners.water;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.arifkalender.projectkorra.almostenoughabilities.abilities.water.Congelation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import static com.projectkorra.projectkorra.ability.CoreAbility.hasAbility;
import static me.arifkalender.projectkorra.almostenoughabilities.abilities.water.Congelation.*;

public class CongelationListener implements Listener {

    @EventHandler
    private void onSneak(PlayerToggleSneakEvent event){
        if(event.isSneaking()){
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());
//            if(hasAbility(event.getPlayer(), Congelation.class)){
//                Congelation congelation = CoreAbility.getAbility(event.getPlayer(), Congelation.class);
//                congelation.setSelectedBlock(getWaterSourceBlock(event.getPlayer(), congelation.getSelectRange(), false));
//            }
            if(bPlayer.getBoundAbilityName().equalsIgnoreCase("Congelation")){
                new Congelation(event.getPlayer());
            }
        }
    }

    @EventHandler
    private void onClick(PlayerAnimationEvent event){
        Player player = event.getPlayer();
        if(hasAbility(player, Congelation.class)){
            Congelation congelation = CoreAbility.getAbility(player, Congelation.class);
            if(!congelation.hasClicked()){
                congelation.triggerClick();
            }
        }
    }
}
