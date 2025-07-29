package me.arifkalender.projectkorra.almostenoughabilities;

import com.projectkorra.projectkorra.event.BendingReloadEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.plugin;
import static me.arifkalender.projectkorra.almostenoughabilities.AlmostEnoughAbilities.version;

public class ReloadEvent implements Listener {

    @EventHandler
    private void onReload(BendingReloadEvent event){
        CommandSender sender = event.getSender();
        sender.sendMessage(version + " was reloaded!");
        plugin.reloadConfig();
    }

}
