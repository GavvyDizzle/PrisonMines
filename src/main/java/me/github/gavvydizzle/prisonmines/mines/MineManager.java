package me.github.gavvydizzle.prisonmines.mines;

import com.github.mittenmc.serverutils.Numbers;
import com.github.mittenmc.serverutils.RepeatingTask;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import me.github.gavvydizzle.prisonmines.PrisonMines;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class MineManager implements Listener {

    private static final int MAX_VOLUME = 2000000;

    private int tick;
    private final PrisonMines instance;
    private final File mineFolder;
    private final HashMap<String, Mine> mines;

    private boolean resetWhenMineFull;

    public MineManager(PrisonMines instance) {
        this.instance = instance;
        mineFolder = new File(instance.getDataFolder(), "mines");
        mines = new HashMap<>();

        reload();
        startResetClock();
    }

    private void loadMines() {
        mines.clear();

        mineFolder.mkdir();
        for (final File file : Objects.requireNonNull(mineFolder.listFiles())) {
            if (!file.isDirectory()) {
                if (file.getName().endsWith(".yml")) {
                    try {
                        Mine mine = new Mine(file);
                        if (!mine.failedToLoad()) mines.put(mine.getId(), mine);
                    } catch (Exception e) {
                        instance.getLogger().severe("Failed to load mine: " + file.getName() + "!");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Reloads the main config.yml and reloads all mines with the loadMines() method
     */
    public void reload() {
        FileConfiguration config = instance.getConfig();
        config.options().copyDefaults(true);
        config.addDefault("resetWhenMineFull", false);
        instance.saveConfig();

        resetWhenMineFull = config.getBoolean("resetWhenMineFull");

        loadMines();
    }

    /**
     * Starts a clock that runs every 20 server ticks (once a second)
     * This clock checks for an initiates mine resets
     */
    private void startResetClock() {
        new RepeatingTask(instance, 0, 20) {
            @Override
            public void run() {
                tick++;
                for (Mine mine : mines.values()) {
                    if (mine.getNextResetTick() <= tick) {

                        if (!resetWhenMineFull && mine.getNumSolidBlocks() == mine.getVolume()) {
                            //don't reset but restart the timer
                            mine.updateNextResetTick();
                        }
                        else {
                            mine.resetMine();
                            mine.updateNextResetTick();
                        }
                    }
                }
            }
        };
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onBlockBreak(BlockBreakEvent e) {
        for (Mine mine : getMinesByBlock(e.getBlock())) {
            mine.blockRemovedFromMine();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onBlockPlace(BlockPlaceEvent e) {
        for (Mine mine : getMinesByBlock(e.getBlock())) {
            mine.blockPlacedInMine();
        }
    }

    public Collection<Mine> getMinesByBlock(Block block) {
        return getMinesByBlock(block.getLocation());
    }

    public Collection<Mine> getMinesByBlock(Location location) {
        ArrayList<Mine> arr = new ArrayList<>();
        for (Mine mine : mines.values()) {
            if (mine.isInMine(location)) arr.add(mine);
        }
        return arr;
    }

    private boolean isVolumeTooLarge(long mineVolume) {
        return mineVolume > MAX_VOLUME;
    }

    /**
     * Creates a new mine by using the locations of the player's WorldEdit positions.
     * If the id is in use, the mine will not be created.
     * If the player does not have two selected points, the mine will not be created.
     * @param player The player
     * @param id The new mine's id
     * @return The newly created mine
     */
    public Mine createNewMine(Player player, String id) {
        id = id.toLowerCase();
        if (mines.containsKey(id)) {
            player.sendMessage(ChatColor.RED + "This mine id is already in use");
            return null;
        }

        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
        World selectionWorld = localSession.getSelectionWorld();

        Region region;
        try {
            if (selectionWorld == null) throw new IncompleteRegionException();
            region = localSession.getSelection(selectionWorld);
        } catch (IncompleteRegionException ex) {
            player.sendMessage(ChatColor.RED + "You must have two points selected with WorldEdit");
            return null;
        }

        if (isVolumeTooLarge(region.getVolume())) {
            player.sendMessage(ChatColor.RED + "Volume of " + Numbers.withSuffix(region.getVolume()) + " is too large. The maximum is " + Numbers.withSuffix(MAX_VOLUME));
            return null;
        }

        Mine mine = new Mine(id, region.getMinimumPoint(), region.getMinimumPoint(), BukkitAdapter.adapt(selectionWorld));
        mines.put(id, mine);
        instance.getInventoryManager().getMineListGUI().addMine(mine);
        player.sendMessage(ChatColor.GREEN + "Successfully created new mine: " + id);
        return mine;
    }

    /**
     * Deletes an existing mine
     * If the mine does not exist, nothing will happen
     * @param player The player
     * @param id The mine's id to remove
     */
    public void deleteMine(Player player, String id) {
        id = id.toLowerCase();
        if (!mines.containsKey(id)) {
            player.sendMessage(ChatColor.RED + "No mine exists for this id");
            return;
        }

        Mine removed = mines.remove(id);
        instance.getInventoryManager().getMineListGUI().removeMine(removed);
        player.sendMessage(ChatColor.YELLOW + "Successfully deleted mine: " + id);
    }

    /**
     * Updates the mine's region to the player's current WorldGuard selection
     * @param player The player
     * @param mine The mine
     */
    public void resizeMine(Player player, Mine mine) {
        if (mine == null) return;

        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
        World selectionWorld = localSession.getSelectionWorld();

        Region region;
        try {
            if (selectionWorld == null) throw new IncompleteRegionException();
            region = localSession.getSelection(selectionWorld);
        } catch (IncompleteRegionException ex) {
            player.sendMessage(ChatColor.RED + "You must have two points selected with WorldEdit");
            return;
        }

        if (isVolumeTooLarge(region.getVolume())) {
            player.sendMessage(ChatColor.RED + "Volume of " + Numbers.withSuffix(region.getVolume()) + " is too large. The maximum is " + Numbers.withSuffix(MAX_VOLUME));
            return;
        }

        mine.updateRegion(region.getMinimumPoint(), region.getMaximumPoint(), BukkitAdapter.adapt(selectionWorld));
        player.sendMessage(ChatColor.GREEN + "Successfully resized mine. Corners are: " + region.getMinimumPoint() + " and " + region.getMaximumPoint());
    }


    public int getSecondsUntilNextReset(Mine mine) {
        return mine.getNextResetTick() - tick;
    }

    @Nullable
    public Mine getMine(String id) {
        return mines.get(id.toLowerCase());
    }

    public Collection<Mine> getMines() {
        return mines.values();
    }

    public Set<String> getMineIDs() {
        return mines.keySet();
    }

}
