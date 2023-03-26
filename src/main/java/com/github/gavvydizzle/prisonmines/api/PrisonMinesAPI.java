package com.github.gavvydizzle.prisonmines.api;

import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;

public class PrisonMinesAPI {

    private static PrisonMinesAPI instance;
    private final MineManager mineManager;

    private PrisonMinesAPI(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    /**
     * Accesses the api instance.
     * Might be null if this method is called when {@link com.github.gavvydizzle.prisonmines.PrisonMines}'s startup method is still being executed.
     *
     * @return The instance of this api
     * @since 1.0
     */
    @Nullable
    public static PrisonMinesAPI getInstance() {
        if (instance == null) {
            try {
                instance = new PrisonMinesAPI(PrisonMines.getInstance().getMineManager());
            } catch (Exception e) {
                PrisonMines.getInstance().getLogger().severe("Failed to create the API. You must wait until this plugin is done loading to get an instance.");
                instance = null;
            }
        }
        return instance;
    }

    /**
     * This method does not ensure that the same mine will be returned if the given location contains multiple mines.
     * This should only be used to get a mine if there is no overlap.
     * @param location The location
     * @return The first mine found at this location
     */
    @Nullable
    public Mine getFirstMine(Location location) {
        return mineManager.getFirstMineByBlock(location.getBlock());
    }

    /**
     * This method does not ensure that the same mine will be returned if the given block contains multiple mines.
     * This should only be used to get a mine if there is no overlap.
     * @param block The block
     * @return The first mine found at this location
     */
    @Nullable
    public Mine getFirstMine(Block block) {
        return mineManager.getFirstMineByBlock(block);
    }

    /**
     * Gets a list of mines that contain this location
     * @param location The location
     * @return A Collection of mines
     */
    @NotNull
    public Collection<Mine> getMines(Location location) {
        return mineManager.getMinesByBlock(location);
    }

    /**
     * Gets a list of mines that contain this block
     * @param block The block
     * @return A Collection of mines
     */
    @NotNull
    public Collection<Mine> getMines(Block block) {
        return mineManager.getMinesByBlock(block);
    }

    /**
     * Get a mine by its ID.
     * This is what it is defined as in the PrisonMines/mines folder and in the '/mines panel' menu.
     * @param id The id of the mine (not case-sensitive)
     * @return The mine with this ID or null if none exists
     */
    @Nullable
    public Mine getMineByID(String id) {
        return mineManager.getMine(id);
    }

    /**
     * Removes a block from a mine's number of solid blocks.
     * The block will be removed from all mines that contain this block.
     * @param block The block
     */
    public void blockBreak(Block block) {
        mineManager.removeBlock(block);
    }

    /**
     * Removes blocks from a mine's number of solid blocks.
     * Each block will be removed from all mines that contain these blocks.
     * @param blocks The list of blocks
     */
    public void blockBreak(Collection<Block> blocks) {
        for (Block b : blocks) {
            mineManager.removeBlock(b);
        }
    }

    /**
     * Adds a block to a mine's number of solid blocks.
     * The block will be added to all mines that contain this block.
     * @param block The block
     */
    public void blockPlace(Block block) {
        mineManager.addBlock(block);
    }

    /**
     * Adds blocks to a mine's number of solid blocks.
     * Each block will be added to all mines that contain these blocks.
     * @param blocks The list of blocks
     */
    public void blockPlace(Collection<Block> blocks) {
        for (Block b : blocks) {
            mineManager.addBlock(b);
        }
    }
}
