package me.github.gavvydizzle.prisonmines;

import me.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import me.github.gavvydizzle.prisonmines.gui.InventoryManager;
import me.github.gavvydizzle.prisonmines.mines.MineManager;
import me.github.gavvydizzle.prisonmines.papi.MyExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PrisonMines extends JavaPlugin {

    private static PrisonMines instance;
    private MineManager mineManager;
    private InventoryManager inventoryManager;
    private AdminCommandManager adminCommandManager;

    @Override
    public void onEnable() {
        instance = this;

        mineManager = new MineManager(instance);
        getServer().getPluginManager().registerEvents(mineManager, this);

        inventoryManager = new InventoryManager(mineManager);
        getServer().getPluginManager().registerEvents(inventoryManager, this);

        try {
            adminCommandManager = new AdminCommandManager(Objects.requireNonNull(getCommand("pmine")), mineManager, inventoryManager);
        } catch (NullPointerException e) {
            getLogger().severe("The admin command name was changed in the plugin.yml file. Please make it \"islandAdmin\" and restart the server. You can change the aliases but NOT the command name.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            new MyExpansion(mineManager).register();
        }
        catch (Exception e) {
            Bukkit.getLogger().warning("Without PlaceholderAPI you are unable to use placeholders!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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

    public AdminCommandManager getAdminCommandManager() {
        return adminCommandManager;
    }
}
