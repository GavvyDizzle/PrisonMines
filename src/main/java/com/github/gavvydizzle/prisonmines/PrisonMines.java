package com.github.gavvydizzle.prisonmines;

import com.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import com.github.gavvydizzle.prisonmines.commands.TeleportToMineSpawnCommand;
import com.github.gavvydizzle.prisonmines.events.MinesLoadedEvent;
import com.github.gavvydizzle.prisonmines.gui.InventoryManager;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.gavvydizzle.prisonmines.papi.MyExpansion;
import com.github.gavvydizzle.prisonmines.utils.Messages;
import com.github.mittenmc.serverutils.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public final class PrisonMines extends CorePlugin {

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

        inventoryManager = new InventoryManager(this, mineManager);

        new AdminCommandManager(getCommand("pmine"), mineManager, inventoryManager);
        Objects.requireNonNull(getCommand("mine")).setExecutor(new TeleportToMineSpawnCommand(mineManager));

        try {
            new MyExpansion(mineManager).register();
        }
        catch (Exception e) {
            getLogger().warning("Without PlaceholderAPI you are unable to use placeholders!");
        }

        Messages.reloadMessages();

        Bukkit.getPluginManager().callEvent(new MinesLoadedEvent());
    }

    @Override
    public void onDisable() {
        if (inventoryManager != null) {
            inventoryManager.closeAllMenus();
        }

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
