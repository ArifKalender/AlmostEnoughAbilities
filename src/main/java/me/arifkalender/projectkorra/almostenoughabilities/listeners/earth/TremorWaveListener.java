package me.arifkalender.projectkorra.almostenoughabilities.listeners.earth;

import com.projectkorra.projectkorra.BendingPlayer;
import me.arifkalender.projectkorra.almostenoughabilities.abilities.earth.TremorWave;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TremorWaveListener implements Listener {

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking()) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());
                new TremorWave(event.getPlayer());
            }
        }
    }

}
