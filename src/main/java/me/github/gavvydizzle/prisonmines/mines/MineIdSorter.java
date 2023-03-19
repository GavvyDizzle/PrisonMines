package me.github.gavvydizzle.prisonmines.mines;

import java.util.Comparator;

public class MineIdSorter implements Comparator<Mine> {

    @Override
    public int compare(Mine mine, Mine t1) {
        return mine.getId().compareTo(t1.getId());
    }
}
