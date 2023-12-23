package com.github.gavvydizzle.prisonmines;

import com.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import com.github.gavvydizzle.prisonmines.commands.TeleportToMineSpawnCommand;
import com.github.gavvydizzle.prisonmines.events.MinesLoadedEvent;
import com.github.gavvydizzle.prisonmines.gui.InventoryManager;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.gavvydizzle.prisonmines.papi.MyExpansion;
import com.github.gavvydizzle.prisonmines.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Handler;

public final class PrisonMines extends JavaPlugin {

    private static PrisonMines instance;
    private MineManager mineManager;
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        instance = this;

        FileConfiguration config = instance.getConfig();
        config.options().copyDefaults(true);
        config.addDefault("disableMineCommand", false);

        mineManager = new MineManager(instance);
        getServer().getPluginManager().registerEvents(mineManager, this);

        inventoryManager = new InventoryManager(mineManager);
        getServer().getPluginManager().registerEvents(inventoryManager, this);

        try {
            new AdminCommandManager(Objects.requireNonNull(getCommand("pmine")), mineManager, inventoryManager);
        } catch (NullPointerException e) {
            getLogger().severe("The admin command name was changed in the plugin.yml file. Please make it \"islandAdmin\" and restart the server. You can change the aliases but NOT the command name.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!config.getBoolean("disableMineCommand")) {
            try {
                Objects.requireNonNull(getCommand("mine")).setExecutor(new TeleportToMineSpawnCommand(mineManager));
            } catch (NullPointerException e) {
                getLogger().severe("The mine command name was changed in the plugin.yml file. Please make it \"mine\" and restart the server. You can change the aliases but NOT the command name.");
            }
        }

        try {
            new MyExpansion(mineManager).register();
        }
        catch (Exception e) {
            Bukkit.getLogger().warning("Without PlaceholderAPI you are unable to use placeholders!");
        }

        Messages.reloadMessages();

        Bukkit.getPluginManager().callEvent(new MinesLoadedEvent());
    }

    @Override
    public void onDisable() {
        if (mineManager != null) {
            mineManager.saveOnShutdown();
        }
    }


    public static PrisonMines getInstance() {
        return instance;
    }

    public MineManager getMineManager() {
        return mineManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}
