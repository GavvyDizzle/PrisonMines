package me.github.gavvydizzle.prisonmines.papi;

import com.github.mittenmc.serverutils.Numbers;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.github.gavvydizzle.prisonmines.mines.Mine;
import me.github.gavvydizzle.prisonmines.mines.MineManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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

        switch (args[1].toLowerCase()) {
            case "timeuntilreset": return Numbers.getTimeFormatted(mineManager.getSecondsUntilNextReset(mine));
            case "name": return mine.getName();
            case "percentremaining": return "" + Numbers.round(mine.getPercentRemaining(), 2);
            case "percentmined": return "" + Numbers.round(mine.getPercentMined(), 2);
            case "blocksremaining": return  "" + mine.getNumSolidBlocks();
            case "volume": return "" + mine.getVolume();
            case "resetlength": return "" + mine.getResetLengthSeconds();
        }

        return null;
    }
}