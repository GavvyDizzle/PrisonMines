package com.github.gavvydizzle.prisonmines.gui;

import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.Numbers;
import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.contents.MineBlock;
import com.github.gavvydizzle.prisonmines.mines.contents.WeightChangeResult;
import com.github.gavvydizzle.prisonmines.utils.Sounds;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class MineGUI implements ClickableMenu {

    private static final TextComponent component;
    private static final ItemStack spawnLocItem, maxWeightItem, nameItem, resetTimeItem, resetPercentageItem, resetNowItem;

    static {
        component = new TextComponent(TextComponent.fromLegacyText(Colors.conv("&7(!) Click here for command (!)")));

        spawnLocItem = new ItemStack(Material.OAK_DOOR);
        ItemMeta meta = spawnLocItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eChange Spawn Location"));
        meta.setLore(Collections.singletonList(Colors.conv("&7Click here to change")));
        spawnLocItem.setItemMeta(meta);

        maxWeightItem = new ItemStack(Material.ANVIL);
        meta = maxWeightItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eChange Max Weight"));
        meta.setLore(Collections.singletonList(Colors.conv("&7Click here to change")));
        maxWeightItem.setItemMeta(meta);

        nameItem = new ItemStack(Material.FEATHER);
        meta = nameItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eChange Display Name"));
        meta.setLore(Collections.singletonList(Colors.conv("&7Click here to change")));
        nameItem.setItemMeta(meta);

        resetTimeItem = new ItemStack(Material.CLOCK);
        meta = resetTimeItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eChange Reset Time"));
        meta.setLore(Collections.singletonList(Colors.conv("&7Click here to change")));
        resetTimeItem.setItemMeta(meta);

        resetPercentageItem = new ItemStack(Material.STONE_PICKAXE);
        meta = resetPercentageItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eChange Reset Percentage"));
        meta.setLore(Collections.singletonList(Colors.conv("&7Click here to change")));
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

    public MineGUI(Mine mine) {
        this.mine = mine;
        inventory = Bukkit.createInventory(null, 54, mine.getId() + " Contents");
        isDirty = false;
        update();

        inventory.setItem(3, nameItem);
        inventory.setItem(4, spawnLocItem);
        inventory.setItem(5, resetTimeItem);
        inventory.setItem(6, resetPercentageItem);
        inventory.setItem(7, maxWeightItem);
        inventory.setItem(8, resetNowItem);
    }

    public void update() {
        updateContents();
        updateClickHelpItem();
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

    @Override
    public void openInventory(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void closeInventory(Player player) {
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

                switch (e.getSlot()) {
                    case 3:
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pmine setName " + mine.getId() + " "));
                        break;
                    case 4:
                        Location l = e.getWhoClicked().getLocation();
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pmine setSpawnLocation " + mine.getId() + " " +
                                Numbers.round(l.getX(), 1) + " " +
                                Numbers.round(l.getY(), 4) + " " +
                                Numbers.round(l.getZ(), 1) + " " +
                                Numbers.round(l.getYaw(), 0) + " " +
                                Numbers.round(l.getPitch(), 0)));
                        break;
                    case 5:
                        player.closeInventory();
                        Bukkit.getScheduler().scheduleSyncDelayedTask(PrisonMines.getInstance(),
                                () -> PrisonMines.getInstance().getInventoryManager().openMenu(player, mine.getResetTimeMenu()));
                        return;
                    case 6:
                        player.closeInventory();
                        Bukkit.getScheduler().scheduleSyncDelayedTask(PrisonMines.getInstance(),
                                () -> PrisonMines.getInstance().getInventoryManager().openMenu(player, mine.getResetPercentageMenu()));
                        return;
                    case 7:
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pmine setMaxWeight " + mine.getId() + " "));
                        break;
                }

                e.getWhoClicked().spigot().sendMessage(tc);
                e.getWhoClicked().closeInventory();
                return;
            }
            else if (e.getSlot() == 8) {
                Sounds.generalClickSound.playSound(player);
                mine.resetMine();
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
    private static boolean isValidMaterial(Material material) {
        return material.isSolid() && !material.name().contains("DOOR");
    }

}