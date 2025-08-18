package me.arifkalender.projectkorra.almostenoughabilities;

import com.projectkorra.projectkorra.ability.CoreAbility;
import me.arifkalender.projectkorra.almostenoughabilities.listeners.air.AirPocketListener;
import me.arifkalender.projectkorra.almostenoughabilities.listeners.air.WindScytheListener;
import me.arifkalender.projectkorra.almostenoughabilities.listeners.earth.CrustSpikesListener;
import me.arifkalender.projectkorra.almostenoughabilities.listeners.earth.DensityAlterationListener;
import me.arifkalender.projectkorra.almostenoughabilities.listeners.earth.TremorWaveListener;
import me.arifkalender.projectkorra.almostenoughabilities.listeners.fire.BlazeRushListener;
import me.arifkalender.projectkorra.almostenoughabilities.listeners.fire.DissipationListener;
import me.arifkalender.projectkorra.almostenoughabilities.listeners.fire.FlameLeapListener;
import me.arifkalender.projectkorra.almostenoughabilities.listeners.water.CongelationListener;
import me.arifkalender.projectkorra.almostenoughabilities.listeners.water.HydrationListener;
import me.arifkalender.projectkorra.almostenoughabilities.util.UtilizationMethods;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public final class AlmostEnoughAbilities extends JavaPlugin {

    /* *･ﾟ✧ AlmostEnoughAbilities 1.0 */
    /* #F454C9 -> #545eb6 */
    public static final String version="§x§F§4§5§4§C§9*§x§E§F§5§4§C§8･§x§E§A§5§5§C§8ﾟ§x§E§5§5§5§C§7✧ §x§D§A§5§6§C§6A§x§D§5§5§6§C§5l§x§D§0§5§6§C§5m§x§C§B§5§7§C§4o§x§C§6§5§7§C§3s§x§C§0§5§7§C§3t§x§B§B§5§8§C§2E§x§B§6§5§8§C§2n§x§B§1§5§8§C§1o§x§A§C§5§9§C§0u§x§A§7§5§9§C§0g§x§A§1§5§9§B§Fh§x§9§C§5§9§B§FA§x§9§7§5§A§B§Eb§x§9§2§5§A§B§Di§x§8§D§5§A§B§Dl§x§8§8§5§B§B§Ci§x§8§2§5§B§B§Ct§x§7§D§5§B§B§Bi§x§7§8§5§C§B§Ae§x§7§3§5§C§B§As §x§6§9§5§D§B§80§x§6§3§5§D§B§8.§x§5§E§5§D§B§71§x§5§9§5§E§B§7.§x§5§4§5§E§B§63";
    public static Plugin plugin;
    public static Random random = new Random();
    @Override
    public void onEnable() {
        if (!this.getDataFolder().exists()) {
            saveDefaultConfig();
            this.getConfig().options().copyDefaults(true);
        }
        plugin=this;
        registerListeners();
        setFields();
        new BukkitRunnable(){
            @Override
            public void run() {
                CoreAbility.registerPluginAbilities((JavaPlugin) plugin, "me.arifkalender.projectkorra.almostenoughabilities.abilities");
            }
        }.runTaskLater(plugin, 10);


        getServer().getConsoleSender().sendMessage(version + "§a was enabled!");
    }
    private void registerListeners(){
        plugin.getServer().getPluginManager().registerEvents(new ReloadEvent(), this);
        plugin.getServer().getPluginManager().registerEvents(new FlameLeapListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new DissipationListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new BlazeRushListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new CrustSpikesListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new DensityAlterationListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new TremorWaveListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new CongelationListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new WindScytheListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new HydrationListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new AirPocketListener(), this);
    }
    private void setFields(){
        UtilizationMethods.fillIgnoredCrops();
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(version + "§c was disabled!");
    }

}
