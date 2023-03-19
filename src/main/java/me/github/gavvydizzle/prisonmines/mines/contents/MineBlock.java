package me.github.gavvydizzle.prisonmines.mines.contents;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a block that is part of the mine.
 * This class stores a material and a weight
 */
public class MineBlock implements Comparable<MineBlock> {

    private final Material material;
    private int weight;

    public MineBlock(Material material) {
        this.material = material;
        this.weight = 0;
    }

    public MineBlock(Material material, int weight) {
        this.material = material;
        this.weight = weight;
    }

    public Material getMaterial() {
        return material;
    }

    public int getWeight() {
        return weight;
    }

    /**
     * Set the weight of this MineBlock.
     * @param weight The new weight. Must be positive
     */
    public void setWeight(int weight) {
        if (weight < 0) weight = 0;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return material.toString() + ":" + weight;
    }

    @Override
    public int compareTo(@NotNull MineBlock o) {
        if (material != o.getMaterial()) {
            return material.toString().compareTo(o.toString());
        }
        else if (weight != o.weight) {
            return Integer.compare(weight, o.weight);
        }
        else {
            return -1;
        }
    }
}
