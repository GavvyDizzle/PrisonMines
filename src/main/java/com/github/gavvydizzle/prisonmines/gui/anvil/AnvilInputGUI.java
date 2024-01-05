package com.github.gavvydizzle.prisonmines.gui.anvil;

import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.gui.ClickableMenu;
import com.github.gavvydizzle.prisonmines.gui.InventoryManager;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.utils.Sounds;
import com.github.mittenmc.serverutils.ColoredItems;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.Numbers;
import com.github.mittenmc.serverutils.anvilediting.AnvilInputType;
import com.github.mittenmc.serverutils.anvilediting.AnvilIntegerType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnvilInputGUI implements ClickableMenu, Listener {

    public enum EditType {
        NAME,
        RESET_TIME,
        RESET_PERCENTAGE,
        MAX_WEIGHT
    }

    private final InventoryManager inventoryManager;
    private final Map<UUID, Mine> mineMap;
    private final Map<UUID, EditType> editTypeMap;

    private final AnvilColoredStringType nameEditor;
    private final AnvilIntegerType resetTimeEditor;
    private final AnvilIntegerType resetPercentageEditor;
    private final AnvilIntegerType maxWeightEditor;

    public AnvilInputGUI(InventoryManager inventoryManager) {
        PrisonMines.getInstance().getServer().getPluginManager().registerEvents(this, PrisonMines.getInstance());

        this.inventoryManager = inventoryManager;
        mineMap = new HashMap<>();
        editTypeMap = new HashMap<>();

        nameEditor = new AnvilColoredStringType("Edit Name", ColoredItems.YELLOW.getGlass(Colors.conv("&eOld Name: {value}")), ColoredItems.LIME.getGlass(Colors.conv("&eNew Name: &r{value}")));
        resetTimeEditor = new AnvilIntegerType("Edit Reset Time", ColoredItems.YELLOW.getGlass(Colors.conv("&eOld Reset Time: {value}s")), ColoredItems.LIME.getGlass(Colors.conv("&eNew Reset Time: {value}s")));
        resetPercentageEditor = new AnvilIntegerType("Edit Reset Percentage", ColoredItems.YELLOW.getGlass(Colors.conv("&eOld Reset Percent: {value}%")), ColoredItems.LIME.getGlass(Colors.conv("&eNew Reset Percent: {value}%")));
        maxWeightEditor = new AnvilIntegerType("Edit Max Weight", ColoredItems.YELLOW.getGlass(Colors.conv("&eOld Max Weight: {value}")), ColoredItems.LIME.getGlass(Colors.conv("&eNew Max Weight: {value}")));
    }

    /**
     * Maps an EditType to its AnvilInputType
     * @param editType The type of edit
     * @return The AnvilInputType for this edit
     */
    private AnvilInputType mapType(EditType editType) {
        switch (editType) {
            case NAME -> {
                return nameEditor;
            }
            case RESET_TIME -> {
                return resetTimeEditor;
            }
            case RESET_PERCENTAGE -> {
                return resetPercentageEditor;
            }
            case MAX_WEIGHT -> {
                return maxWeightEditor;
            }
        }
        return null;
    }

    /**
     * Retrieves the stored value from the mine
     * @param editType The type of edit being made
     * @param mine The mine
     * @return The initial value before any edits are made
     */
    private Object mapDefaultValue(EditType editType, Mine mine) {
        switch (editType) {
            case NAME -> {
                return mine.getName();
            }
            case RESET_TIME -> {
                return mine.getResetLengthSeconds();
            }
            case RESET_PERCENTAGE -> {
                return mine.getResetPercentage();
            }
            case MAX_WEIGHT -> {
                return mine.getContents().getMaxWeight();
            }
        }
        return null;
    }

    /**
     * Pushes an update to the mine
     * @param editType The type of edit made
     * @param mine The mine
     * @param value The value to save
     * @return True if the update went through, false otherwise
     */
    private boolean pushUpdate(EditType editType, Mine mine, String value) {
        AnvilInputType anvilInputType = mapType(editType);
        if (anvilInputType == null) return false;

        if (anvilInputType.isInvalidInput(value)) return false;

        switch (editType) {
            case NAME -> mine.setName((String) nameEditor.convert(value));
            case RESET_TIME -> {
                mine.setResetLengthSeconds((int) resetTimeEditor.convert(value));
                mine.pushResetTimeUpdate();
            }
            case RESET_PERCENTAGE -> {
                mine.setResetPercentage((int) resetPercentageEditor.convert(value));
                mine.pushResetPercentageUpdate();
            }
            case MAX_WEIGHT -> {
                return mine.getContents().setMaxWeight((int) maxWeightEditor.convert(value), false);
            }
        }

        return true;
    }

    /**
     * Retrieves the stored value from the mine and formats it nicely
     * @param editType The type of edit made
     * @param mine The mine
     * @return A nicely formatted output String
     */
    private String getNewValue(EditType editType, Mine mine) {
        switch (editType) {
            case NAME -> {
                return Colors.conv(mine.getName());
            }
            case RESET_TIME -> {
                return Numbers.getTimeFormatted(mine.getResetLengthSeconds());
            }
            case RESET_PERCENTAGE -> {
                return mine.getResetPercentage() + "%";
            }
            case MAX_WEIGHT -> {
                return String.valueOf(mine.getContents().getMaxWeight());
            }
        }

        return "INVALID";
    }

    @Override
    public void openInventory(Player player) {

    }

    public void openInventory(Player player, EditType editType, Mine mine) {
        AnvilInputType anvilInputType = mapType(editType);
        if (anvilInputType == null) return;

        player.openAnvil(null, true);
        inventoryManager.onMenuOpen(player, this);

        Inventory inventory = player.getOpenInventory().getTopInventory();
        player.getOpenInventory().setTitle(anvilInputType.getInventoryName());

        mineMap.put(player.getUniqueId(), mine);
        editTypeMap.put(player.getUniqueId(), editType);

        // Place the items
        Object value = mapDefaultValue(editType, mine);
        inventory.setItem(0, anvilInputType.getBackButton(value));
        inventory.setItem(1, anvilInputType.getInfoItem(value));
    }

    @Override
    public void closeInventory(Player player) {
        editTypeMap.remove(player.getUniqueId());
        if (mineMap.remove(player.getUniqueId()) != null) {
            player.getOpenInventory().getTopInventory().setContents(new ItemStack[] {null, null, null});
        }
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getClickedInventory() != e.getView().getTopInventory()) return;

        if (e.getSlot() == 0) {
            Sounds.generalClickSound.playSound((Player) e.getWhoClicked());
            Bukkit.getScheduler().scheduleSyncDelayedTask(PrisonMines.getInstance(), () -> inventoryManager.openMenu((Player) e.getWhoClicked(), mineMap.get(e.getWhoClicked().getUniqueId()).getMineGUI()));
        }
        else if (e.getSlot() == 2) {
            EditType editType = editTypeMap.get(e.getWhoClicked().getUniqueId());
            if (editType == null) return;

            Mine mine = mineMap.get(e.getWhoClicked().getUniqueId());
            if (mine == null) return;

            AnvilInputType anvilInputType = mapType(editType);
            if (anvilInputType == null) return;

            AnvilInventory anvilInventory = (AnvilInventory) e.getClickedInventory();
            String input = anvilInventory.getRenameText();

            if (anvilInventory.getItem(2) == null || anvilInputType.isInvalidInput(anvilInventory.getRenameText())) {
                e.getWhoClicked().sendMessage(ChatColor.RED + "Invalid input");
            }
            else {
                if (pushUpdate(editType, mine, input)) {
                    e.getWhoClicked().sendMessage(ChatColor.GREEN + "Successfully updated this value to: " + getNewValue(editType, mine));
                }
                else {
                    e.getWhoClicked().sendMessage(ChatColor.RED + "Unsuccessful edit");
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(PrisonMines.getInstance(), () -> inventoryManager.openMenu((Player) e.getWhoClicked(), mineMap.get(e.getWhoClicked().getUniqueId()).getMineGUI()));
            }

            Sounds.generalClickSound.playSound((Player) e.getWhoClicked());
        }
    }

    @EventHandler
    private void onAnvilRename(PrepareAnvilEvent e) {
        EditType editType = editTypeMap.get(e.getView().getPlayer().getUniqueId());
        if (editType == null) return;

        AnvilInputType anvilInputType = mapType(editType);
        if (anvilInputType == null) return;

        AnvilInventory anvilInventory = e.getInventory();
        String input = anvilInventory.getRenameText();

        if (anvilInputType.isInvalidInput(input)) {
            anvilInventory.setItem(2, null);
        }
        else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(PrisonMines.getInstance(), () -> e.getView().getTopInventory().setItem(2, anvilInputType.getResultItem(input)), 0);
        }
    }
}
