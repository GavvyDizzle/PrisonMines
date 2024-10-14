package com.github.gavvydizzle.prisonmines.gui;

import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.gavvydizzle.prisonmines.utils.Sounds;
import com.github.mittenmc.serverutils.gui.pages.PagesMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MineListGUI extends PagesMenu<Mine> {

    private final InventoryManager inventoryManager;
    private final MineManager mineManager;

    public MineListGUI(InventoryManager inventoryManager, MineManager mineManager) {
        super(new PagesMenuBuilder<>("Mines List", 6));

        this.inventoryManager = inventoryManager;
        this.mineManager = mineManager;

        reloadItems();
    }

    protected void reloadItems() {
        setItems(mineManager.getMines());
    }

    @Override
    public void onItemClick(InventoryClickEvent e, Player player, Mine mine) {
        inventoryManager.openMineMenuFromAdminPanel(player, mine.getMineGUI());
        Sounds.generalClickSound.playSound((Player) e.getWhoClicked());
    }
}
