package com.github.gavvydizzle.prisonmines.gui.anvil;

import com.github.mittenmc.serverutils.Numbers;
import com.github.mittenmc.serverutils.anvilediting.AnvilIntegerType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AnvilTimeType extends AnvilIntegerType {

    public AnvilTimeType(String inventoryName, ItemStack infoItem, ItemStack resultItem) {
        super(inventoryName, infoItem, resultItem);
    }

    public AnvilTimeType(String inventoryName, ItemStack backButton, ItemStack infoItem, ItemStack resultItem) {
        super(inventoryName, backButton, infoItem, resultItem);
    }

    @Override
    public Object convert(String value) {
        if (value == null || value.isBlank()) return -1;
        return Numbers.parseSeconds(value);
    }

    @Override
    public String convert(Object value) {
        if (!(value instanceof Integer val)) return "";
        return Numbers.getTimeFormatted(val, "INVALID");
    }

    @Override
    public boolean isInvalidInput(@Nullable String value) {
        if (value == null || value.isBlank()) return true;
        return Numbers.parseSeconds(value) == -1;
    }
}
