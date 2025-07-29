package me.arifkalender.projectkorra.almostenoughabilities.listeners.fire;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.arifkalender.projectkorra.almostenoughabilities.abilities.fire.FlameLeap;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.plugin;

public class FlameLeapListener implements Listener {

    @EventHandler
    private void onClick(PlayerAnimationEvent event){
        if(event.getPlayer().getGameMode() == GameMode.SPECTATOR){
            return;
        }
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());
        Ability ability = bPlayer.getBoundAbility();
        if(ability != null && ability.getName() != null){
            if(ability.getName().equalsIgnoreCase("FlameLeap")){
                if(bPlayer.isOnCooldown(CoreAbility.getAbility("FlameLeap"))){
                   return;
                }
                if(!CoreAbility.hasAbility(event.getPlayer(), FlameLeap.class)){
                    FlameLeap flameLeap = new FlameLeap(event.getPlayer());
                    flameLeap.jumpPlayer();
                }else{
                    FlameLeap flameLeap = CoreAbility.getAbility(event.getPlayer(), FlameLeap.class);
                    flameLeap.jumpPlayer();

                }
            }
        }
    }
    @EventHandler
    private void onDamage(EntityDamageEvent event){
        if(event.getCause() != EntityDamageEvent.DamageCause.FALL){
            return;
        }
        if(!(event.getEntity() instanceof Player)){
            return;
        }
        if(CoreAbility.hasAbility((Player) event.getEntity(), FlameLeap.class)){
            if (plugin.getConfig().getBoolean("Abilities.FlameLeap.NegateFallDamage")){
               event.setCancelled(true);
            }
        }
    }

}
