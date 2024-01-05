package com.github.gavvydizzle.prisonmines.gui;

import com.github.gavvydizzle.prisonmines.gui.anvil.AnvilInputGUI;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.UUID;

public class InventoryManager implements Listener {

    private final MineListGUI mineListGUI;
    private final AnvilInputGUI anvilInputGUI;
    private final HashMap<UUID, ClickableMenu> playersInInventory;

    public InventoryManager(MineManager mineManager) {
        mineListGUI = new MineListGUI(this, mineManager);
        anvilInputGUI = new AnvilInputGUI(this);
        playersInInventory = new HashMap<>();
    }

    public void reload() {
        mineListGUI.reload();
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent e) {
       ClickableMenu clickableMenu = playersInInventory.remove(e.getPlayer().getUniqueId());
        if (clickableMenu != null) {
            clickableMenu.closeInventory((Player) e.getPlayer());
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        if (playersInInventory.containsKey(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);
            // Pass along the click event without verifying the inventory the click was done in
            playersInInventory.get(e.getWhoClicked().getUniqueId()).handleClick(e);
        }
    }

    /**
     * Closes all menus belonging to this plugin
     */
    public void closeAllMenus() {
        for (UUID uuid : playersInInventory.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) player.closeInventory();
        }
        playersInInventory.clear();
    }

    /**
     * Removes all players from the mine's GUI.
     * @param mine The mine
     */
    public void closeMineMenu(Mine mine) {
        MineGUI mineGUI = mine.getMineGUI();
        HashMap<UUID, ClickableMenu> map = new HashMap<>(playersInInventory);
        for (UUID uuid : map.keySet()) {
            if (playersInInventory.get(uuid) == mineGUI) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) player.closeInventory();
            }
        }
    }

    /**
     * Saves the menu the player opened so clicks can be passed to it correctly
     * @param player The player
     * @param clickableMenu The menu they opened
     */
    public void onMenuOpen(Player player, ClickableMenu clickableMenu) {
        playersInInventory.put(player.getUniqueId(), clickableMenu);
    }

    /**
     * Opens the given menu and adds the player to the list of players with opened menus
     * @param player The player
     * @param clickableMenu The menu to open
     */
    public void openMenu(Player player, ClickableMenu clickableMenu) {
        clickableMenu.openInventory(player);
        playersInInventory.put(player.getUniqueId(), clickableMenu);
    }

    /**
     * Opens the given menu and adds the player to the list of players with opened menus
     * @param player The player
     * @param mineGUI The mine menu to open
     */
    public void openMineMenuFromAdminPanel(Player player, MineGUI mineGUI) {
        mineGUI.openInventory(player, true);
        playersInInventory.put(player.getUniqueId(), mineGUI);
    }

    /**
     * Opens the anvil editor
     * @param player The player
     * @param editType The type they are editing
     * @param mine The mine
     */
    public void openAnvilEditMenu(Player player, AnvilInputGUI.EditType editType, Mine mine) {
        anvilInputGUI.openInventory(player, editType, mine);
        playersInInventory.put(player.getUniqueId(), anvilInputGUI);
    }

    public MineListGUI getMineListGUI() {
        return mineListGUI;
    }
}
