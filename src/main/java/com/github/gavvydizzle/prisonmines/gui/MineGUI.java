package com.github.gavvydizzle.prisonmines.gui;

import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.gui.anvil.AnvilInputGUI;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.contents.MineBlock;
import com.github.gavvydizzle.prisonmines.mines.contents.WeightChangeResult;
import com.github.gavvydizzle.prisonmines.utils.Sounds;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.ItemStackUtils;
import com.github.mittenmc.serverutils.Numbers;
import com.github.mittenmc.serverutils.gui.pages.ClickableItem;
import com.github.mittenmc.serverutils.gui.pages.DisplayItem;
import com.github.mittenmc.serverutils.gui.pages.PagesMenu;
import com.github.mittenmc.serverutils.item.ItemStackBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

public class MineGUI extends PagesMenu<MineBlock> {

    private static final TextComponent component;
    private static final int helpItemSlot = 0, contentsItemSlot = 1,
            spawnLocItemSlot = 4, maxWeightItemSlot = 7, nameItemSlot = 3, resetTimeItemSlot = 5, resetPercentageItemSlot = 6, resetNowItemSlot = 8;
    private static final ItemStack helpItem, spawnLocItem, maxWeightItem, nameItem, resetTimeItem, resetPercentageItem, resetNowItem;

    static {
        component = new TextComponent(TextComponent.fromLegacyText(Colors.conv("&7(!) Click here for command (!)")));

        spawnLocItem = ItemStackBuilder.of(Material.OAK_DOOR)
                .name("&eChange Spawn Location")
                .lore("&7Click here to change", "&7Is set: &a{val}")
                .build();

        maxWeightItem = ItemStackBuilder.of(Material.ANVIL)
                .name("&eChange Max Weight")
                .lore("&7Click here to change", "&7Current weight: &a{val}")
                .build();

        nameItem = ItemStackBuilder.of(Material.FEATHER)
                .name("&eChange Display Name")
                .lore("&7Click here to change",
                        "&7Current name: {val}")
                .build();

        resetTimeItem = ItemStackBuilder.of(Material.CLOCK)
                .name("&eChange Reset Time")
                .lore("&7Click here to change",
                        "&7Current time: &a{val}")
                .build();

        resetPercentageItem = ItemStackBuilder.of(Material.STONE_PICKAXE)
                .name("&eChange Reset Percentage")
                .lore("&7Click here to change",
                        "&7Current value: &a{val}")
                .build();

        resetNowItem = ItemStackBuilder.of(Material.RED_STAINED_GLASS_PANE)
                .name("&eReset")
                .lore("&7Click here to reset the mine")
                .build();

        helpItem = ItemStackBuilder.of(Material.BOOK)
                .name("&eControls")
                .lore(
                        "&4NUM 9: Remove block",
                        "&aNUM 1: Maximum Value",
                        "&aNUM 2: +{click2}",
                        "&aNUM 3: +{click3}",
                        "&aNUM 4: +{click4}",
                        "&aSHIFT+LEFT: +10",
                        "&aLEFT: +1",
                        "&cRIGHT: -1",
                        "&cSHIFT+RIGHT: -10",
                        "&cNUM 5: -{click4}",
                        "&cNUM 6: -{click3}",
                        "&cNUM 7: -{click2}",
                        "&cNUM 8: Set to 0"
                ).build();
    }

    private final Mine mine;
    private boolean isDirty;
    private final Set<UUID> enteredFromListMenu;

    public MineGUI(Mine mine) {
        super(new PagesMenuBuilder<MineBlock>(mine.getId() + " Contents", 6)
                .slots(IntStream.rangeClosed(9, 44).boxed().toList())
        );

        this.mine = mine;
        isDirty = false;
        enteredFromListMenu = new HashSet<>();

        addStaticItems();
        addDynamicItems();
        reloadItems();
    }

    private void reloadItems() {
        setItems(mine.getContents().getBlockList());
    }

    private void addStaticItems() {
        super.addClickableItem(resetNowItemSlot, new ClickableItem<>(resetNowItem) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                Sounds.generalClickSound.playSound(player);
                mine.resetMine(true, false);
                player.sendMessage(ChatColor.GREEN + "Resetting " + mine.getName());
            }
        });
    }

    private void addDynamicItems() {
        super.addClickableItem(helpItemSlot, new DisplayItem<>(helpItem) {
            @Override
            public @NotNull ItemStack getMenuItem(Player player) {
                ItemStack clone = helpItem.clone();
                ItemStackUtils.replacePlaceholders(clone, Map.of(
                        "{click2}", String.valueOf(mine.getContents().getClick2()),
                        "{click3}", String.valueOf(mine.getContents().getClick3()),
                        "{click4}", String.valueOf(mine.getContents().getClick4())
                ));
                return clone;
            }
        });


        super.addClickableItem(contentsItemSlot, new DisplayItem<>(new ItemStack(Material.AIR)) {
            @Override
            public @NotNull ItemStack getMenuItem(Player player) {
                return mine.getContents().getInfoItem();
            }
        });

        super.addClickableItem(spawnLocItemSlot, new ClickableItem<>(new ItemStack(Material.POTATO)) {
            @Override
            public @NotNull ItemStack getMenuItem(Player player) {
                ItemStack clone = spawnLocItem.clone();
                ItemStackUtils.replacePlaceholders(clone, Map.of("{val}", mine.hasSpawnLocation() ? Colors.conv("&aYes") : Colors.conv("&cNo")));
                return clone;
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                // Remove the player from this list so command prompting does not trigger the list menu to open
                enteredFromListMenu.remove(player.getUniqueId());
                
                TextComponent tc = component.duplicate();
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pmine setSpawnLocation " + mine.getId() + " snap"));
                Sounds.generalClickSound.playSound(player);
            }
        });

        super.addClickableItem(maxWeightItemSlot, new ClickableItem<>(maxWeightItem) {
            @Override
            public @NotNull ItemStack getMenuItem(Player player) {
                ItemStack clone = maxWeightItem.clone();
                ItemStackUtils.replacePlaceholders(clone, Map.of("{val}", String.valueOf(mine.getContents().getMaxWeight())));
                return clone;
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                handleAnvilEditMenuOpen(player, AnvilInputGUI.EditType.MAX_WEIGHT);
                Sounds.generalClickSound.playSound(player);
            }
        });

        super.addClickableItem(nameItemSlot, new ClickableItem<>(nameItem) {
            @Override
            public @NotNull ItemStack getMenuItem(Player player) {
                ItemStack clone = nameItem.clone();
                ItemStackUtils.replacePlaceholders(clone, Map.of("{val}", mine.getName()));
                return clone;
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                handleAnvilEditMenuOpen(player, AnvilInputGUI.EditType.NAME);
                Sounds.generalClickSound.playSound(player);
            }
        });

        super.addClickableItem(resetTimeItemSlot, new ClickableItem<>(resetTimeItem) {
            @Override
            public @NotNull ItemStack getMenuItem(Player player) {
                ItemStack clone = resetTimeItem.clone();
                ItemStackUtils.replacePlaceholders(clone, Map.of("{val}", Numbers.getTimeFormatted(mine.getResetLengthSeconds())));
                return clone;
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                handleAnvilEditMenuOpen(player, AnvilInputGUI.EditType.RESET_TIME);
                Sounds.generalClickSound.playSound(player);
            }
        });

        super.addClickableItem(resetPercentageItemSlot, new ClickableItem<>(resetPercentageItem) {
            @Override
            public @NotNull ItemStack getMenuItem(Player player) {
                ItemStack clone = resetPercentageItem.clone();
                ItemStackUtils.replacePlaceholders(clone, Map.of("{val}", mine.getResetPercentage() == -1 ? "DISABLED (-1)" : mine.getResetPercentage() + "%"));
                return clone;
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                handleAnvilEditMenuOpen(player, AnvilInputGUI.EditType.RESET_PERCENTAGE);
                Sounds.generalClickSound.playSound(player);
            }
        });
    }
    
    private void handleAnvilEditMenuOpen(Player player, AnvilInputGUI.EditType type) {
        enteredFromListMenu.remove(player.getUniqueId());
        PrisonMines.getInstance().getInventoryManager().openAnvilEditMenu(player, type, mine);
    }

    public void openInventory(Player player, boolean openedFromListMenu) {
        if (openedFromListMenu) enteredFromListMenu.add(player.getUniqueId());
        super.openInventory(player);
    }

    @Override
    public void closeInventory(Player player) {
        super.closeInventory(player);

        if (enteredFromListMenu.remove(player.getUniqueId())) {
            InventoryManager inventoryManager = PrisonMines.getInstance().getInventoryManager();
            inventoryManager.openMenuDelayed(player, inventoryManager.getMineListGUI());
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
    public void onItemClick(InventoryClickEvent e, Player player, MineBlock mineBlock) {
        WeightChangeResult weightChangeResult;

        // Removal of mine block
        if (e.getHotbarButton() == 8) {
            if (mine.getContents().removeMineBlock(mineBlock.getMaterial())) {
                isDirty = true;
                reloadItems();
                Sounds.generalClickSound.playSound((Player) e.getWhoClicked());
            }
            return;
        }


        int amount = 0;
        if (e.getHotbarButton() >= 0) {
            amount = switch (e.getHotbarButton()) {
                case 0 -> mine.getContents().getMaxWeight();
                case 1 -> mine.getContents().getClick2();
                case 2 -> mine.getContents().getClick3();
                case 3 -> mine.getContents().getClick4();
                case 4 -> -mine.getContents().getClick4();
                case 5 -> -mine.getContents().getClick3();
                case 6 -> -mine.getContents().getClick2();
                case 7 -> -mine.getContents().getMaxWeight();
                default -> 0;
            };
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
        weightChangeResult = mine.getContents().updateWeight(mineBlock, amount, Math.abs(amount) >= mine.getContents().getMaxWeight());

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
                reloadItems();
                if (amount > 0) {
                    Sounds.increaseChanceSound.playSound((Player) e.getWhoClicked());
                }
                else {
                    Sounds.decreaseChanceSound.playSound((Player) e.getWhoClicked());
                }
        }
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == e.getView().getTopInventory()) {
            super.handleClick(e);
            return;
        }

        e.setCancelled(true);

        // Click to the bottom inventory
        // Add clicked item to the mine
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

        // Everything is good. Add the new block to the mine
        mine.getContents().addMineBlock(new MineBlock(mine, material));
        isDirty = true;
        reloadItems();
        Sounds.generalClickSound.playSound((Player) e.getWhoClicked());
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
