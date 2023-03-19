package me.github.gavvydizzle.prisonmines.mines.contents;

import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.Numbers;
import me.github.gavvydizzle.prisonmines.PrisonMines;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MineContents {

    private static final ItemStack infoItem;

    static {
        infoItem = new ItemStack(Material.PAPER);
        ItemMeta meta = infoItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&eContents Overview"));
        infoItem.setItemMeta(meta);
    }

    private final ArrayList<MineBlock> blockList;
    private int maxWeight, totalWeight;
    private int click2, click3, click4;

    public MineContents() {
        blockList = new ArrayList<>();
        maxWeight = 1000;
        totalWeight = 0;
        calculateClickAmounts();
    }

    /**
     * Copy constructor
     * @param other The MineContents instance to clone
     */
    public MineContents(MineContents other) {
        blockList = new ArrayList<>(other.blockList);
        maxWeight = other.maxWeight;
        totalWeight = other.totalWeight;
        click2 = other.click2;
        click3 = other.click3;
        click4 = other.click4;
    }

    /**
     * Creates a new instance by parsing a list of Strings
     * @param mineBlockStrings The list of strings in the format MATERIAL:#
     * @param maxWeight The max weight for this mine
     */
    public MineContents(Collection<String> mineBlockStrings, int maxWeight) {
        blockList = new ArrayList<>();
        this.maxWeight = maxWeight;

        for (String s : mineBlockStrings) {
            String[] arr = s.split(":");
            if (arr.length != 2) {
                PrisonMines.getInstance().getLogger().warning("Invalid mine contents: " + s);
                continue;
            }

            Material material = Material.getMaterial(arr[0].toUpperCase());
            if (material == null) {
                PrisonMines.getInstance().getLogger().warning("Invalid mine contents material: " + s);
                continue;
            }

            int w;
            try {
                w = Integer.parseInt(arr[1]);
            } catch (NumberFormatException e) {
                PrisonMines.getInstance().getLogger().warning("Invalid mine contents weight: " + s);
                continue;
            }

            blockList.add(new MineBlock(material, w));
        }

        calculateClickAmounts();
        calculateTotalWeight();
        Collections.sort(blockList);
    }

    // Calculates the total weight of all MineBlocks
    // If the total weight is greater than the max, then a detailed error message is printed to the console
    private void calculateTotalWeight() {
        totalWeight = 0;
        for (MineBlock mineBlock : blockList) {
            totalWeight += mineBlock.getWeight();
        }

        if (totalWeight > maxWeight) {
            PrisonMines.getInstance().getLogger().warning("The max weight of this mine was " + maxWeight + ".\n" +
                    "The total weight adds up to " + totalWeight + ".\n" +
                    "The max weight has been changed to " + totalWeight + "to prevent unintended behavior.\n" +
                    "This mine's settings have been manually altered. Below is the current contents of the mine:\n" +
                    blockList);
            maxWeight = totalWeight;
        }
    }

    private void calculateClickAmounts() {
        click2 = maxWeight/2;
        click3 = maxWeight/10;
        click4 = maxWeight/50;
    }

    @Nullable
    private MineBlock getMineBlock(Material material) {
        for (MineBlock mineBlock : blockList) {
            if (material == mineBlock.getMaterial()) return mineBlock;
        }
        return null;
    }

    /**
     * Adds a new MineBlock and sets its weight to 0
     * @param mineBlock The MineBlock
     * @return True if the block was added, false if not
     */
    public boolean addMineBlock(MineBlock mineBlock) {
        if (blockList.size() >= 45) return false; // Don't allow more than 45 unique block types in a mine

        blockList.add(mineBlock);
        mineBlock.setWeight(0);
        Collections.sort(blockList);
        return true;
    }

    public boolean removeMineBlock(Material material) {
        MineBlock mineBlock = getMineBlock(material);
        if (mineBlock == null) return false;

        blockList.remove(mineBlock);
        totalWeight -= mineBlock.getWeight();
        return true;
    }

    /**
     * Updates the weight of this block after ensuring the change is valid.
     * @param material The Material
     * @param amount The amount to change the weight by
     * @param clampValue If true, then the weight will stop at the maximum allowed or zero
     * @return A WeightChangeResult state
     */
    public synchronized WeightChangeResult updateWeight(Material material, int amount, boolean clampValue) {
        return updateWeight(getMineBlock(material), amount, clampValue);
    }

    /**
     * Updates the weight of this block after ensuring the change is valid.
     * @param mineBlock The MineBlock
     * @param amount The amount to change the weight by
     * @param clampValue If true, then the weight will stop at the maximum allowed or zero
     * @return A WeightChangeResult state
     */
    public synchronized WeightChangeResult updateWeight(@Nullable MineBlock mineBlock, int amount, boolean clampValue) {
        if (mineBlock == null) return WeightChangeResult.ERROR;

        if (!clampValue) {
            if (amount > 0 && totalWeight == maxWeight) return WeightChangeResult.MAX_WEIGHT_REACHED;
            else if (amount > 0 && totalWeight + amount > maxWeight) return WeightChangeResult.INVALID_RAISE;
            else if (amount < 0 && totalWeight + amount < 0) return WeightChangeResult.INVALID_LOWER;
            else {
                mineBlock.setWeight(mineBlock.getWeight() + amount);
                totalWeight += amount;
            }
        }
        else {
            if (amount > 0 && totalWeight == maxWeight) return WeightChangeResult.MAX_WEIGHT_REACHED;
            else {
                if (amount > 0 && totalWeight + amount > maxWeight) {
                    mineBlock.setWeight(mineBlock.getWeight() + maxWeight - totalWeight);
                }
                else if (amount < 0 && totalWeight + amount < 0) {
                    mineBlock.setWeight(0);
                }
            }
        }

        calculateTotalWeight();
        Collections.sort(blockList);
        return WeightChangeResult.SUCCESSFUL;
    }

    /**
     * @param material The Material
     * @return The frequency of this material or -1 if it does not exist
     */
    public double getMineBlockFrequency(Material material) {
        return getMineBlockFrequency(getMineBlock(material));
    }

    /**
     * Gets the frequency of this MineBlock.
     * The result can be 0 (it has no weight).
     * A result of -1 means an error occurred.
     * @param mineBlock The MineBlock
     * @return The frequency of this MineBlock or -1 if null
     */
    public double getMineBlockFrequency(@Nullable MineBlock mineBlock) {
        if (mineBlock == null || totalWeight == 0) return -1;
        return mineBlock.getWeight() * 1.0 / totalWeight;
    }

    /**
     * Updates the max weight for this mine
     * @param maxWeight The new max weight
     * @return True if the value was updated, false otherwise
     */
    public boolean setMaxWeight(int maxWeight) {
        if (maxWeight < totalWeight) return false;
        else {
            this.maxWeight = maxWeight;
            calculateClickAmounts();
            return true;
        }
    }

    /**
     * Determines if this material is already a part of this mine
     * @param material The Material
     * @return True if this Material is already in use
     */
    public boolean isMaterialAlreadyUsed(Material material) {
        return getMineBlock(material) != null;
    }

    public ItemStack getInfoItem() {
        ItemStack itemStack = infoItem.clone();
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        ArrayList<String> lore = new ArrayList<>();
        lore.add(Colors.conv("&fMax Weight: " + maxWeight));
        lore.add(Colors.conv("&7Used Weight: " + totalWeight + "/" + maxWeight));
        lore.add("");
        for (MineBlock mineBlock : blockList) {
            lore.add(Colors.conv("&7- &a" + mineBlock.getMaterial().toString() + " &f" + mineBlock.getWeight() + "&7(" + Numbers.round(getMineBlockFrequency(mineBlock)*100, 2) + "%)"));
        }
        meta.setLore(lore);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack getInfoItem(MineBlock mineBlock) {
        ItemStack itemStack = new ItemStack(mineBlock.getMaterial());
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        ArrayList<String> lore = new ArrayList<>();
        lore.add(Colors.conv("&fMax Weight: " + maxWeight));
        lore.add(Colors.conv("&7Used Weight: " + totalWeight + "/" + maxWeight));
        lore.add(Colors.conv("&7My Weight: " + mineBlock.getWeight() + "/" + totalWeight));
        meta.setLore(lore);

        itemStack.setItemMeta(meta);
        return itemStack;
    }


    public ArrayList<MineBlock> getBlockList() {
        return blockList;
    }

    public boolean isBlank() {
        return totalWeight == 0;
    }

    public int getClick2() {
        return click2;
    }

    public int getClick3() {
        return click3;
    }

    public int getClick4() {
        return click4;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    /**
     * @return A list of MineBlocks as Strings for the config file
     */
    public ArrayList<String> getContentsAsStrings() {
        ArrayList<String> arr = new ArrayList<>();
        for (MineBlock mineBlock : blockList) {
            arr.add(mineBlock.toString());
        }
        return arr;
    }
}
