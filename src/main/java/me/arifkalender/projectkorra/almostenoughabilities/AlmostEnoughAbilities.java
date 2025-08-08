package me.arifkalender.projectkorra.almostenoughabilities;

import com.projectkorra.projectkorra.ability.CoreAbility;
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

import java.util.Random;

public final class AlmostEnoughAbilities extends JavaPlugin {

    /* *･ﾟ✧ AlmostEnoughAbilities 1.0 */
    /* #F454C9 -> #545eb6 */
    public static final String version="§x§F§4§5§4§C§9*§x§E§E§5§4§C§8･§x§E§9§5§5§C§8ﾟ§x§E§3§5§5§C§7✧ §x§D§8§5§6§C§6A§x§D§3§5§6§C§5l§x§C§D§5§6§C§4m§x§C§8§5§7§C§4o§x§C§2§5§7§C§3s§x§B§D§5§7§C§2t§x§B§7§5§8§C§2E§x§B§2§5§8§C§1n§x§A§C§5§8§C§0o§x§A§7§5§9§C§0u§x§A§1§5§9§B§Fg§x§9§C§5§A§B§Fh§x§9§6§5§A§B§EA§x§9§1§5§A§B§Db§x§8§B§5§B§B§Di§x§8§6§5§B§B§Cl§x§8§0§5§B§B§Bi§x§7§B§5§C§B§Bt§x§7§5§5§C§B§Ai§x§7§0§5§C§B§9e§x§6§A§5§D§B§9s §x§5§F§5§D§B§71§x§5§A§5§E§B§7.§x§5§4§5§E§B§60";
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
        CoreAbility.registerPluginAbilities(this, "me.arifkalender.projectkorra.almostenoughabilities.abilities");
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
    }
    private void setFields(){
        UtilizationMethods.fillIgnoredCrops();
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(version + "§c was disabled!");
    }

}
