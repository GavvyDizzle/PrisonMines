package com.github.gavvydizzle.prisonmines.gui.anvil;

import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.anvilediting.AnvilStringType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AnvilColoredStringType extends AnvilStringType {

    public AnvilColoredStringType(String inventoryName, ItemStack infoItem, ItemStack resultItem) {
        super(inventoryName, infoItem, resultItem);
    }

    public AnvilColoredStringType(String inventoryName, ItemStack backButton, ItemStack infoItem, ItemStack resultItem) {
        super(inventoryName, backButton, infoItem, resultItem);
    }

    @Override
    public String convert(Object value) {
        return Colors.conv(super.convert(value));
    }
}
