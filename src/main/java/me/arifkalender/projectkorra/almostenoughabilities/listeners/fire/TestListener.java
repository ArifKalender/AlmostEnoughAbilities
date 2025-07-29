package me.arifkalender.projectkorra.almostenoughabilities.listeners.fire;

import me.arifkalender.projectkorra.almostenoughabilities.util.UtilizationMethods;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.plugin;

public class TestListener implements Listener {

    //@EventHandler
    public void onSneak(PlayerToggleSneakEvent event){
        new BukkitRunnable(){
            String msg = "";
            @Override
            public void run() {
                msg="";
                UtilizationMethods.getAbilitiesAroundPoint(event.getPlayer().getLocation(),4).forEach(coreAbility -> {
                    msg = msg +"["+ coreAbility.getName() + " Distance: " + (int) coreAbility.getLocation().distance(event.getPlayer().getLocation()) + "] ";
                });
                event.getPlayer().sendMessage(msg);
                if(!event.getPlayer().isSneaking()){
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin,0,1);
    }

}
