package com.github.gavvydizzle.prisonmines.events;

import com.github.gavvydizzle.prisonmines.mines.Mine;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fires after a mine is done resetting
 */
public class MinePostResetEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Mine mine;
    private final double percentRemaining;

    public MinePostResetEvent(Mine mine, double percentRemaining) {
        this.mine = mine;
        this.percentRemaining = percentRemaining;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Mine getMine() {
        return mine;
    }

    public double getPercentRemaining() {
        return percentRemaining;
    }
}