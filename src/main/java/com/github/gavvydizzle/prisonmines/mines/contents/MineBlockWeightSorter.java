package com.github.gavvydizzle.prisonmines.mines.contents;

import java.util.Comparator;

/**
 * Sorts MineBlocks by weight then by Material name
 */
public class MineBlockWeightSorter implements Comparator<MineBlock> {

    @Override
    public int compare(MineBlock m1, MineBlock m2) {
        if (m1.getWeight() != m2.getWeight()) {
            return Integer.compare(m2.getWeight(), m1.getWeight());
        }
        return m1.getMaterial().toString().compareTo(m2.getMaterial().toString());
    }
}
