package com.github.gavvydizzle.prisonmines.gui;

import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.gui.anvil.AnvilInputGUI;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.contents.MineBlock;
import com.github.gavvydizzle.prisonmines.mines.contents.WeightChangeResult;
import com.github.gavvydizzle.prisonmines.utils.Sounds;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.Numbers;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MineGUI implements ClickableMenu {

    private static final TextComponent component;
    private static final int spawnLocItemSlot = 4, maxWeightItemSlot = 7, nameItemSlot = 3, resetTimeItemSlot = 5, resetPercentageItemSlot = 6, resetNowItemSlot = 8;
    private static final ItemStack spawnLocItem, maxWeightItem, nameItem, resetTimeItem, resetPercentageItem, resetNowItem;

    static {
        component = new TextComponent(TextComponent.fromLegacyText(Colors.conv("&7(!) Click here for command (!)")));

        spawnLocItem = new ItemStack(Material.OAK_DOOR);
        ItemMeta meta = spawnLocItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eChange Spawn Location"));
        meta.setLore(Colors.conv(Arrays.asList(
                "&7Click here to change",
                "&7Is set: &a{val}"
        )));
        spawnLocItem.setItemMeta(meta);

        maxWeightItem = new ItemStack(Material.ANVIL);
        meta = maxWeightItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eChange Max Weight"));
        meta.setLore(Colors.conv(Arrays.asList(
                "&7Click here to change",
                "&7Current weight: &a{val}"
        )));
        maxWeightItem.setItemMeta(meta);

        nameItem = new ItemStack(Material.FEATHER);
        meta = nameItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eChange Display Name"));
        meta.setLore(Colors.conv(Arrays.asList(
                "&7Click here to change",
                "&7Current name: {val}"
        )));
        nameItem.setItemMeta(meta);

        resetTimeItem = new ItemStack(Material.CLOCK);
        meta = resetTimeItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eChange Reset Time"));
        meta.setLore(Colors.conv(Arrays.asList(
                "&7Click here to change",
                "&7Current time: &a{val}"
        )));
        resetTimeItem.setItemMeta(meta);

        resetPercentageItem = new ItemStack(Material.STONE_PICKAXE);
        meta = resetPercentageItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eChange Reset Percentage"));
        meta.setLore(Colors.conv(Arrays.asList(
                "&7Click here to change",
                "&7Current value: &a{val}"
        )));
        meta.removeItemFlags(ItemFlag.values());
        resetPercentageItem.setItemMeta(meta);

        resetNowItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        meta = resetNowItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eReset"));
        meta.setLore(Collections.singletonList(Colors.conv("&7Click here to reset the mine")));
        resetNowItem.setItemMeta(meta);
    }

    private final Inventory inventory;
    private final Mine mine;
    private boolean isDirty;
    private final Set<UUID> enteredFromListMenu;

    public MineGUI(Mine mine) {
        this.mine = mine;
        inventory = Bukkit.createInventory(null, 54, mine.getId() + " Contents");
        isDirty = false;
        enteredFromListMenu = new HashSet<>();
        update();

        updateSettingsItems();
        inventory.setItem(resetNowItemSlot, resetNowItem);
    }

    /**
     * Updates the contents and items with information about the contents
     */
    public void update() {
        updateContents();
        updateClickHelpItem();
        updateMaxWeightItem();
    }

    /**
     * Updates the general info item and individual MineBlock items in this inventory
     */
    public void updateContents() {
        inventory.setItem(1, mine.getContents().getInfoItem());

        int i = 9;
        for (MineBlock mineBlock : mine.getContents().getBlockList()) {
            inventory.setItem(i++, mine.getContents().getInfoItem(mineBlock));
        }
    }

    /**
     * Updates the click helper item
     */
    public void updateClickHelpItem() {
        ItemStack itemStack = new ItemStack(Material.BOOK);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        meta.setDisplayName(Colors.conv("&eControls"));

        ArrayList<String> lore = new ArrayList<>();
        lore.add(Colors.conv("&4NUM 9: Remove block"));
        lore.add("");
        lore.add(Colors.conv("&aNUM 1: Maximum Value"));
        lore.add(Colors.conv("&aNUM 2: +" + mine.getContents().getClick2()));
        lore.add(Colors.conv("&aNUM 3: +" + mine.getContents().getClick3()));
        lore.add(Colors.conv("&aNUM 4: +" + mine.getContents().getClick4()));
        lore.add(Colors.conv("&aSHIFT+LEFT: +10"));
        lore.add(Colors.conv("&aLEFT: +1"));
        lore.add("");
        lore.add(Colors.conv("&cRIGHT: -1"));
        lore.add(Colors.conv("&cSHIFT+RIGHT: -10"));
        lore.add(Colors.conv("&cNUM 5: " + -mine.getContents().getClick4()));
        lore.add(Colors.conv("&cNUM 6: " + -mine.getContents().getClick3()));
        lore.add(Colors.conv("&cNUM 7: " + -mine.getContents().getClick2()));
        lore.add(Colors.conv("&cNUM 8: Set to 0"));
        meta.setLore(lore);

        itemStack.setItemMeta(meta);
        inventory.setItem(0, itemStack);
    }

    public void updateSettingsItems() {
        updateNameItem();
        updateSpawnLocItem();
        updateResetTimeItem();
        updateResetPercentageItem();
    }

    public void updateNameItem() {
        ItemStack itemStack = nameItem.clone();
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        ArrayList<String> lore = new ArrayList<>();
        for (String s : Objects.requireNonNull(meta.getLore())) {
            lore.add(s.replace("{val}", mine.getName()));
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        inventory.setItem(nameItemSlot, itemStack);
    }

    public void updateSpawnLocItem() {
        ItemStack itemStack = spawnLocItem.clone();
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        ArrayList<String> lore = new ArrayList<>();
        for (String s : Objects.requireNonNull(meta.getLore())) {
            lore.add(s.replace("{val}", mine.hasSpawnLocation() ? Colors.conv("&aYes") : Colors.conv("&aNo")));
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        inventory.setItem(spawnLocItemSlot, itemStack);
    }

    public void updateResetTimeItem() {
        ItemStack itemStack = resetTimeItem.clone();
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        ArrayList<String> lore = new ArrayList<>();
        for (String s : Objects.requireNonNull(meta.getLore())) {
            lore.add(s.replace("{val}", Numbers.getTimeFormatted(mine.getResetLengthSeconds())));
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        inventory.setItem(resetTimeItemSlot, itemStack);
    }

    public void updateResetPercentageItem() {
        ItemStack itemStack = resetPercentageItem.clone();
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        ArrayList<String> lore = new ArrayList<>();
        String replacement;
        if (mine.getResetPercentage() == -1) {
            replacement = "DISABLED (-1)";
        }
        else {
            replacement = mine.getResetPercentage() + "%";
        }

        for (String s : Objects.requireNonNull(meta.getLore())) {
            lore.add(s.replace("{val}", replacement));
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        inventory.setItem(resetPercentageItemSlot, itemStack);
    }

    public void updateMaxWeightItem() {
        ItemStack itemStack = maxWeightItem.clone();
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        ArrayList<String> lore = new ArrayList<>();
        for (String s : Objects.requireNonNull(meta.getLore())) {
            lore.add(s.replace("{val}", String.valueOf(mine.getContents().getMaxWeight())));
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        inventory.setItem(maxWeightItemSlot, itemStack);
    }

    @Override
    public void openInventory(Player player) {
        player.openInventory(inventory);
    }

    public void openInventory(Player player, boolean openedFromListMenu) {
        if (openedFromListMenu) enteredFromListMenu.add(player.getUniqueId());
        player.openInventory(inventory);
    }

    @Override
    public void closeInventory(Player player) {
        if (enteredFromListMenu.remove(player.getUniqueId())) {
            InventoryManager inventoryManager = PrisonMines.getInstance().getInventoryManager();
            Bukkit.getScheduler().scheduleSyncDelayedTask(PrisonMines.getInstance(), () ->
                    inventoryManager.openMenu(player, inventoryManager.getMineListGUI()), 0);
        }

        saveIfDirty();
    }

    public void saveIfDirty() {
        if (isDirty) {
            isDirty = false;
            mine.pushContentsUpdate();
        }
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == e.getView().getTopInventory()) {

            Player player = (Player) e.getWhoClicked();

            if (Numbers.isWithinRange(e.getSlot(), 3, 7)) {
                TextComponent tc = component.duplicate();

                Sounds.generalClickSound.playSound(player);

                // Remove the player from this list so command prompting does not trigger the list menu to open
                enteredFromListMenu.remove(player.getUniqueId());

                switch (e.getSlot()) {
                    case 3:
                        PrisonMines.getInstance().getInventoryManager().openAnvilEditMenu(player, AnvilInputGUI.EditType.NAME, mine);
                        return;
                    case 4:
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pmine setSpawnLocation " + mine.getId() + " snap"));
                        break;
                    case 5:
                        PrisonMines.getInstance().getInventoryManager().openAnvilEditMenu(player, AnvilInputGUI.EditType.RESET_TIME, mine);
                        return;
                    case 6:
                        PrisonMines.getInstance().getInventoryManager().openAnvilEditMenu(player, AnvilInputGUI.EditType.RESET_PERCENTAGE, mine);
                        return;
                    case 7:
                        PrisonMines.getInstance().getInventoryManager().openAnvilEditMenu(player, AnvilInputGUI.EditType.MAX_WEIGHT, mine);
                        return;
                }
                e.getWhoClicked().spigot().sendMessage(tc);

                e.getWhoClicked().closeInventory();
                return;
            }
            else if (e.getSlot() == 8) {
                Sounds.generalClickSound.playSound(player);
                mine.resetMine(true, false);
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "Resetting " + mine.getName());
                return;
            }

            int minSlot = 9;
            int maxSlot = minSlot + mine.getContents().getBlockList().size() - 1;
            if (minSlot > maxSlot) return; // No blocks in the mine

            int slot = e.getSlot();
            if (!Numbers.isWithinRange(slot, minSlot, maxSlot)) return;

            WeightChangeResult weightChangeResult;

            ArrayList<MineBlock> arr = mine.getContents().getBlockList();
            int index = slot - minSlot;
            int amount = 0;

            // Removal of mine block
            if (e.getHotbarButton() == 8) {
                if (e.getCurrentItem() == null) return;

                Material material = e.getCurrentItem().getType();
                if (material == Material.AIR) return;

                if (mine.getContents().removeMineBlock(material)) {
                    isDirty = true;
                    inventory.clear(maxSlot);
                    update();
                    Sounds.generalClickSound.playSound((Player) e.getWhoClicked());
                }
            }
            else if (e.getHotbarButton() >= 0) {
                switch (e.getHotbarButton()) {
                    case 0:
                        amount = mine.getContents().getMaxWeight();
                        break;
                    case 1:
                        amount = mine.getContents().getClick2();
                        break;
                    case 2:
                        amount = mine.getContents().getClick3();
                        break;
                    case 3:
                        amount = mine.getContents().getClick4();
                        break;
                    case 4:
                        amount = -mine.getContents().getClick4();
                        break;
                    case 5:
                        amount = -mine.getContents().getClick3();
                        break;
                    case 6:
                        amount = -mine.getContents().getClick2();
                        break;
                    case 7:
                        amount = -mine.getContents().getMaxWeight();
                        break;
                }
            } else {
                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    amount = 10;
                } else if (e.getClick() == ClickType.LEFT) {
                    amount = 1;
                } else if (e.getClick() == ClickType.RIGHT) {
                    amount = -1;
                } else if (e.getClick() == ClickType.SHIFT_RIGHT) {
                    amount = -10;
                }
            }

            if (amount == 0) return;
            weightChangeResult = mine.getContents().updateWeight(arr.get(index), amount, Math.abs(amount) >= mine.getContents().getMaxWeight());

            switch (weightChangeResult) {
                case MAX_WEIGHT_REACHED:
                    player.sendMessage(ChatColor.RED + "Maximum weight reached");
                    break;
                case INVALID_RAISE:
                    player.sendMessage(ChatColor.RED + "Cannot raise weight");
                    break;
                case INVALID_LOWER:
                    player.sendMessage(ChatColor.RED + "Cannot lower weight");
                    break;
                case ERROR:
                    player.sendMessage(ChatColor.RED + "Invalid action");
                    break;
                case SUCCESSFUL:
                    isDirty = true;
                    update();
                    if (amount > 0) {
                        Sounds.increaseChanceSound.playSound((Player) e.getWhoClicked());
                    }
                    else {
                        Sounds.decreaseChanceSound.playSound((Player) e.getWhoClicked());
                    }
            }
        }
        else { // Add clicked item to the mine
            if (e.getCurrentItem() == null) return;

            Material material = e.getCurrentItem().getType();
            if (material == Material.AIR) return;

            if (!isValidMaterial(material)) {
                e.getWhoClicked().sendMessage(ChatColor.RED + "Invalid block");
                Sounds.generalFailSound.playSound((Player) e.getWhoClicked());
                return;
            }

            if (mine.getContents().isMaterialAlreadyUsed(material)) {
                e.getWhoClicked().sendMessage(ChatColor.RED + "Block already in use");
                Sounds.generalFailSound.playSound((Player) e.getWhoClicked());
                return;
            }

            if (mine.getContents().addMineBlock(new MineBlock(material))) {
                isDirty = true;
                update();
                Sounds.generalClickSound.playSound((Player) e.getWhoClicked());
            }
            else {
                e.getWhoClicked().sendMessage(ChatColor.RED + "Maximum blocks reached (45)");
                Sounds.generalFailSound.playSound((Player) e.getWhoClicked());
            }
        }
    }

    /**
     * Determines if this block is a valid block type for the mine
     * @param material The Material
     * @return True if this Material can be used as a block in the mine
     */
    private boolean isValidMaterial(Material material) {
        return material.isBlock();
    }

}
