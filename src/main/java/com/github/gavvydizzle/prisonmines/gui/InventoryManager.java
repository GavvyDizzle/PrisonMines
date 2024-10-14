package com.github.gavvydizzle.prisonmines.gui;

import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.gui.anvil.AnvilInputGUI;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.mittenmc.serverutils.gui.ClickableMenu;
import com.github.mittenmc.serverutils.gui.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryManager extends MenuManager {

    private final PrisonMines instance;
    private final MineListGUI mineListGUI;
    private final AnvilInputGUI anvilInputGUI;

    public InventoryManager(PrisonMines instance, MineManager mineManager) {
        super(instance);

        this.instance = instance;
        mineListGUI = new MineListGUI(this, mineManager);
        anvilInputGUI = new AnvilInputGUI(this);
    }

    public void reload() {
        mineListGUI.reloadItems();
    }

    /**
     * Removes all players from the mine's GUI.
     * @param mine The mine
     */
    public void closeMineMenu(Mine mine) {
        MineGUI mineGUI = mine.getMineGUI();

        for (Map.Entry<UUID, ClickableMenu> entry : new HashMap<>(super.getViewers()).entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && entry.getValue() == mineGUI) {
                player.closeInventory();
            }
        }
    }

    /**
     * Opens the given menu and adds the player to the list of players with opened menus
     * @param player The player
     * @param mineGUI The mine menu to open
     */
    public void openMineMenuFromAdminPanel(Player player, MineGUI mineGUI) {
        mineGUI.openInventory(player, true);
        super.saveOpenedMenu(player, mineGUI);
    }

    /**
     * Opens the anvil editor
     * @param player The player
     * @param editType The type they are editing
     * @param mine The mine
     */
    public void openAnvilEditMenu(Player player, AnvilInputGUI.EditType editType, Mine mine) {
        anvilInputGUI.openInventory(player, editType, mine);
        super.saveOpenedMenu(player, anvilInputGUI);
    }

    public MineListGUI getMineListGUI() {
        return mineListGUI;
    }
}
