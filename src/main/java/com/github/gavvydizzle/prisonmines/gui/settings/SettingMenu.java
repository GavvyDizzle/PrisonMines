package com.github.gavvydizzle.prisonmines.gui.settings;

import com.github.gavvydizzle.prisonmines.utils.Sounds;
import com.github.mittenmc.serverutils.ColoredItems;
import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.gui.ClickableMenu;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class SettingMenu implements ClickableMenu {

    protected static ItemStack backButton, infoItem;

    static {
        backButton = ColoredItems.RED.getGlass("&cBack to Mine Panel");
        infoItem = new ItemStack(Material.PAPER);
    }

    protected final Inventory inventory;
    protected final Mine mine;
    protected boolean isDirty;

    public SettingMenu(Mine mine, String inventoryName, int inventorySize) {
        this.mine = mine;
        inventory = Bukkit.createInventory(null, inventorySize, inventoryName);
        isDirty = false;
    }

    /**
     * Handles when the back button is clicked in this menu
     * @param player The player who clicked
     */
    public void onBackButtonClick(Player player) {
        Sounds.generalClickSound.playSound(player);
        player.closeInventory();
        Bukkit.getScheduler().scheduleSyncDelayedTask(PrisonMines.getInstance(),
                () -> PrisonMines.getInstance().getInventoryManager().openMenu(player, mine.getMineGUI()));
    }

    /**
     * Pushes changes to the mine's config file if isDirty is true
     */
    public abstract void attemptSaveOnClose();

}
