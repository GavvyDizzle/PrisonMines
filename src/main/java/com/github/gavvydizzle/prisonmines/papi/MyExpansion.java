package com.github.gavvydizzle.prisonmines.papi;

import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.mittenmc.serverutils.Numbers;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.contents.MineBlock;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyExpansion extends PlaceholderExpansion {

    private final MineManager mineManager;

    public MyExpansion(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "prisonmines";
    }

    @Override
    public @NotNull String getAuthor() {
        return "GavvyDizzle";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] args = params.split("_");
        if (args.length < 2) {
            return "Argument needed";
        }

        Mine mine = mineManager.getMine(args[0]);
        if (mine == null) {
            return "Invalid mine";
        }

        // a_contents_1_(material|percent)
        if (args[1].equalsIgnoreCase("contents")) {
            int index;
            try {
                index = Integer.parseInt(args[2]) - 1;
            } catch (NumberFormatException e) {
                return "Error parsing number: " + args[2];
            }

            ArrayList<MineBlock> arr = mine.getContents().getSortedBlockList();

            if (index < 0 || index >= arr.size()) {
                return "Invalid index: " + args[2];
            }

            switch (args[3].toLowerCase()) {
                case "material":
                    return arr.get(index).getFormattedName();
                case "percent":
                    return String.valueOf(Numbers.round(mine.getContents().getMineBlockFrequency(arr.get(index)) * 100, 2));
                default:
                    return null;
            }
        }

        switch (args[1].toLowerCase()) {
            case "timeuntilreset": return Numbers.getTimeFormatted(mineManager.getSecondsUntilNextReset(mine), "0s");
            case "name": return mine.getName();
            case "percentremaining": return String.valueOf(Numbers.round(mine.getPercentRemaining(), 2));
            case "percentmined": return String.valueOf(Numbers.round(mine.getPercentMined(), 2));
            case "blocksremaining": return String.valueOf(mine.getNumSolidBlocks());
            case "resetpercentage": return String.valueOf(mine.getResetPercentage());
            case "volume": return String.valueOf(mine.getVolume());
            case "resetlength": return Numbers.getTimeFormatted(mine.getResetLengthSeconds());
        }

        return null;
    }
}