package me.github.gavvydizzle.prisonmines.mines.contents;

public enum WeightChangeResult {
    SUCCESSFUL,
    INVALID_RAISE, // Tried to raise total wright above the max
    INVALID_LOWER, // Tried to lower total weight below 0
    MAX_WEIGHT_REACHED,
    ERROR
}
