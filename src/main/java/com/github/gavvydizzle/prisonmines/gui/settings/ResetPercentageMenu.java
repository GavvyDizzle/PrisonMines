package com.github.gavvydizzle.prisonmines.gui.settings;

import com.github.gavvydizzle.prisonmines.utils.Sounds;
import com.github.mittenmc.serverutils.ColoredItems;
import com.github.mittenmc.serverutils.Colors;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ResetPercentageMenu extends SettingMenu {

    public ResetPercentageMenu(Mine mine) {
        super(mine, "Change Reset Percentage", 18);

        inventory.setItem(0, backButton);
        updateInfoItem();

        inventory.setItem(10, ColoredItems.GREEN.getGlass(Colors.conv("&aAdd 20%")));
        inventory.setItem(11, ColoredItems.GREEN.getGlass(Colors.conv("&aAdd 5%")));
        inventory.setItem(12, ColoredItems.GREEN.getGlass(Colors.conv("&aAdd 1%")));

        inventory.setItem(14, ColoredItems.RED.getGlass(Colors.conv("&cDecrease 1%")));
        inventory.setItem(15, ColoredItems.RED.getGlass(Colors.conv("&cDecrease 5%")));
        inventory.setItem(16, ColoredItems.RED.getGlass(Colors.conv("&cDecrease 20%")));
    }

    private void updateInfoItem() {
        ItemStack itemStack = infoItem.clone();
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eCurrent Reset Percentage: " + mine.getResetPercentage() + "%"));
        itemStack.setItemMeta(meta);
        inventory.setItem(4, itemStack);
    }

    @Override
    public void openInventory(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void closeInventory(Player player) {
        attemptSaveOnClose();
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != e.getView().getTopInventory()) return;

        int delta = 0;
        switch (e.getSlot()) {
            case 0:
                onBackButtonClick((Player) e.getWhoClicked());
                return;
            case 10:
                delta = 20;
                break;
            case 11:
                delta = 5;
                break;
            case 12:
                delta = 1;
                break;
            case 14:
                delta = -1;
                break;
            case 15:
                delta = -5;
                break;
            case 16:
                delta = -20;
                break;
        }

        if (delta != 0) {
            int old = mine.getResetPercentage();
            mine.setResetPercentage(old + delta);

            if (old != mine.getResetPercentage()) {
                isDirty = true;
                updateInfoItem();
                if (delta > 0) {
                    Sounds.increaseChanceSound.playSound((Player) e.getWhoClicked());
                } else {
                    Sounds.decreaseChanceSound.playSound((Player) e.getWhoClicked());
                }
            }
            else {
                Sounds.generalFailSound.playSound((Player) e.getWhoClicked());
            }
        }
    }

    @Override
    public void attemptSaveOnClose() {
        if (isDirty) {
            isDirty = false;
            mine.pushResetPercentageUpdate();
        }
    }

}
