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

    public MinePostResetEvent(Mine mine) {
        this.mine = mine;
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
}