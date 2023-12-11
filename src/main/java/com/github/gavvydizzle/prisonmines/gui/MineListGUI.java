package com.github.gavvydizzle.prisonmines.gui;

import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.MineIdSorter;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.gavvydizzle.prisonmines.utils.Sounds;
import com.github.mittenmc.serverutils.ColoredItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MineListGUI implements ClickableMenu {

    private static final int inventorySize;
    private static final int pageDownSlot;
    private static final int pageInfoSlot;
    private static final int pageUpSlot;
    private static final ItemStack pageInfoItem;
    private static final ItemStack previousPageItem;
    private static final ItemStack nextPageItem;
    private static final ItemStack pageRowFiller;

    static {
        inventorySize = 54;
        pageDownSlot = 48;
        pageInfoSlot = 49;
        pageUpSlot = 50;

        pageInfoItem = new ItemStack(Material.PAPER);

        previousPageItem = new ItemStack(Material.PAPER);
        ItemMeta prevPageMeta = previousPageItem.getItemMeta();
        assert prevPageMeta != null;
        prevPageMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
        previousPageItem.setItemMeta(prevPageMeta);

        nextPageItem = new ItemStack(Material.PAPER);
        ItemMeta nextPageMeta = nextPageItem.getItemMeta();
        assert nextPageMeta != null;
        nextPageMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
        nextPageItem.setItemMeta(nextPageMeta);

        pageRowFiller = ColoredItems.WHITE.getGlass();
    }

    private final MineIdSorter mineIdSorter;
    private final InventoryManager inventoryManager;
    private final MineManager mineManager;
    private final String inventoryName;
    private final ArrayList<Mine> mines;
    private final ArrayList<ItemStack> mineItems;
    private final HashMap<UUID, Integer> playerPages;
    private boolean updateContentsFlag;

    public MineListGUI(InventoryManager inventoryManager, MineManager mineManager) {
        this.mineIdSorter = new MineIdSorter();
        this.inventoryManager = inventoryManager;
        this.mineManager = mineManager;
        this.inventoryName = "Mines List";

        mines = new ArrayList<>();
        mineItems = new ArrayList<>();
        playerPages = new HashMap<>();

        updateContentsFlag = false;

        reload();
    }

    protected void reload() {
        mines.clear();
        addMines(mineManager.getMines());
        updateMineItems();
    }

    @Override
    public void openInventory(Player player) {
        if (updateContentsFlag) {
            updateContentsFlag = false;
            updateMineItems();
        }

        Inventory inventory = Bukkit.createInventory(player, inventorySize, inventoryName);

        for (int slot = 0; slot < getNumItemsOnPage(1); slot++) {
            inventory.setItem(slot, mineItems.get(getIndexByPage(1, slot)));
        }
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, pageRowFiller);
        }
        inventory.setItem(pageDownSlot, previousPageItem);
        inventory.setItem(pageInfoSlot, getPageItem(1));
        inventory.setItem(pageUpSlot, nextPageItem);

        playerPages.put(player.getUniqueId(), 1);
        player.openInventory(inventory);
    }

    @Override
    public void closeInventory(Player player) {
        playerPages.remove(player.getUniqueId());
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != e.getView().getTopInventory()) return;

        if (e.getSlot() == pageUpSlot) {
            if (playerPages.get(e.getWhoClicked().getUniqueId()) < getMaxPage()) {
                playerPages.put(e.getWhoClicked().getUniqueId(), playerPages.get(e.getWhoClicked().getUniqueId()) + 1);
                updatePage((Player) e.getWhoClicked());
            }
        }
        else if (e.getSlot() == pageDownSlot) {
            if (playerPages.get(e.getWhoClicked().getUniqueId()) > 1) {
                playerPages.put(e.getWhoClicked().getUniqueId(), playerPages.get(e.getWhoClicked().getUniqueId()) - 1);
                updatePage((Player) e.getWhoClicked());
            }
        }
        else {
            Mine mine;
            try {
                mine = mines.get(getIndexByPage(playerPages.get(e.getWhoClicked().getUniqueId()), e.getSlot()));
            } catch (Exception ignored) {
                return;
            }

            if (mine == null) return;

            inventoryManager.openMineMenuFromAdminPanel((Player) e.getWhoClicked(), mine.getMineGUI());
            Sounds.generalClickSound.playSound((Player) e.getWhoClicked());
        }
    }

    private void updatePage(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        int page = playerPages.get(player.getUniqueId());

        for (int i = 0; i < 45; i++) {
            inventory.clear(i);
        }

        for (int i = 0; i < getNumItemsOnPage(page); i++) {
            inventory.setItem(i, mineItems.get(getIndexByPage(page, i)));
        }

        inventory.setItem(pageInfoSlot, getPageItem(page));
    }

    private ItemStack getPageItem(int page) {
        ItemStack pageInfo = pageInfoItem.clone();
        ItemMeta meta = pageInfo.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.YELLOW + "Page " + page + "/" + getMaxPage());
        pageInfo.setItemMeta(meta);
        return pageInfo;
    }

    private int getMaxPage() {
        return (mineItems.size() - 1) / 45 + 1;
    }

    private int getNumItemsOnPage(int page) {
        return Math.min(45, mineItems.size() - (page - 1) * 45);
    }

    private int getIndexByPage(int page, int slot) {
        return (page - 1) * 45 + slot;
    }


    /**
     * Reloads the items to show in this menu
     */
    private void updateMineItems() {
        mineItems.clear();
        for (Mine mine : mines) {
            mineItems.add(mine.getGuiItem());
        }
    }

    /**
     * Adds a mine to this GUI
     * @param mine The mine
     */
    public void addMine(Mine mine) {
        mines.add(mine);
        mines.sort(mineIdSorter);
        updateMineItems();
    }

    /**
     * Adds a collection of mines to this GUI
     * @param list The list of mines
     */
    public void addMines(Collection<Mine> list) {
        mines.addAll(list);
        mines.sort(mineIdSorter);
        updateMineItems();
    }

    /**
     * Removes a mine from this GUI
     * @param mine The mine
     */
    public void removeMine(Mine mine) {
        mines.remove(mine);
        updateMineItems();
    }

    public void setUpdateFlag() {
        updateContentsFlag = true;
    }
}
