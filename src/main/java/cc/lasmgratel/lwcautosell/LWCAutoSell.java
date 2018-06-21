package cc.lasmgratel.lwcautosell;

import com.earth2me.essentials.Essentials;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class LWCAutoSell extends JavaPlugin implements Listener {
    private Config config;
    private static Economy econ;

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Essentials") || !Bukkit.getPluginManager().isPluginEnabled("LWC") || !Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            getLogger().log(Level.WARNING, "Essentials, Vault or LWC not enabled! disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        setupEconomy();

        Essentials essentials = getPlugin(Essentials.class);

        reloadConfig();

        getCommand("autosell").setExecutor(new CommandAutosell(this));
        Bukkit.getScheduler().runTaskTimer(this, new AutoSellTask(this, essentials), 0, 2000);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
    }

    @Override
    public void saveConfig() {
        getConfig().set("gg", config);
        super.saveConfig();
    }

    public Config getInternalConfig() {
        return config;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        getConfig().addDefault("gg", new Config());
        config = (Config) getConfig().get("gg");
    }

    public static Economy getEcon() {
        return econ;
    }

    @EventHandler
    public void onHolderBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof InventoryHolder) {
            config.getLocations().remove(event.getBlock().getLocation());
            getConfig().set("gg", config);
            reloadConfig();
        }
    }
}
