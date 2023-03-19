package me.github.gavvydizzle.prisonmines.gui;

import me.github.gavvydizzle.prisonmines.mines.Mine;
import me.github.gavvydizzle.prisonmines.mines.MineManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.UUID;

public class InventoryManager implements Listener {

    private final MineListGUI mineListGUI;
    private final HashMap<UUID, ClickableMenu> playersInInventory;

    public InventoryManager(MineManager mineManager) {
        mineListGUI = new MineListGUI(this, mineManager);
        playersInInventory = new HashMap<>();
    }

    public void reload() {
        mineListGUI.reload();
    }

    /**
     * Saves the menu the player opened so clicks can be passed to it correctly
     * @param player The player
     * @param clickableMenu The menu they opened
     */
    public void onMenuOpen(Player player, ClickableMenu clickableMenu) {
        playersInInventory.put(player.getUniqueId(), clickableMenu);
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

    public void openMineGUI(Player player, Mine mine) {
        onMenuOpen(player, mine.getMineGUI());
        mine.getMineGUI().openInventory(player);
    }

    public MineListGUI getMineListGUI() {
        return mineListGUI;
    }

}
