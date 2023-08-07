package com.github.gavvydizzle.prisonmines.mines;

import com.github.gavvydizzle.prisonmines.utils.Messages;
import com.github.mittenmc.serverutils.Numbers;
import com.github.mittenmc.serverutils.RepeatingTask;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.events.MinePostResetEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class MineManager implements Listener {

    private static final int MAX_VOLUME = 2000000;
    public static final int RESET_ALL_TICK_INTERVAL = 10;

    private int tick;
    private final PrisonMines instance;
    private final File mineFolder;
    private final HashMap<String, Mine> mines;

    private final HashSet<Location> taggedBlocks;
    private boolean resetWhenMineFull, removeDroppedItemsOnMineReset;
    private final HashSet<Integer> resetMessageSeconds;

    public MineManager(PrisonMines instance) {
        this.instance = instance;
        mineFolder = new File(instance.getDataFolder(), "mines");
        mines = new HashMap<>();
        taggedBlocks = new HashSet<>();
        resetMessageSeconds = new HashSet<>();

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
        config.addDefault("resetOnServerStart", false);
        config.addDefault("removeDroppedItemsOnMineReset", true);
        config.addDefault("resetMessageSeconds", Arrays.asList(10, 5, 1));
        instance.saveConfig();

        resetWhenMineFull = config.getBoolean("resetWhenMineFull");
        removeDroppedItemsOnMineReset = config.getBoolean("removeDroppedItemsOnMineReset");
        resetMessageSeconds.clear();
        for (int n : config.getIntegerList("resetMessageSeconds")) {
            if (Numbers.isWithinRange(n, 1, 60)) {
                resetMessageSeconds.add(n);
            }
            else {
                instance.getLogger().warning("Reset messages can only exist from 1 to 60 seconds. Remove " + n + " from it to stop receiving this message");
            }
        }

        loadMines();
        if (config.getBoolean("resetOnServerStart")) {
            resetAllMines();
        }
    }

    /**
     * Saves any MineGUIs with unsaved edits
     */
    public void saveOnShutdown() {
        for (Mine mine : mines.values()) {
            mine.getMineGUI().saveIfDirty();
        }
    }

    /**
     * Starts a clock that runs every 20 server ticks (once a second)
     * This clock initiates a mine reset when a mine is checked after its next reset time has been passed.
     * If the mine is paused, then it will not be checked.
     */
    private void startResetClock() {
        new RepeatingTask(instance, 0, 20) {
            @Override
            public void run() {
                tick++;
                for (Mine mine : mines.values()) {
                    if (mine.isResettingPaused()) continue;

                    int secondsRemaining = mine.getNextResetTick() - tick;

                    if (resetMessageSeconds.contains(secondsRemaining)) {
                        sendResetMessage(mine, secondsRemaining);
                    }

                    if (secondsRemaining <= 0) {
                        if (!resetWhenMineFull && mine.getNumSolidBlocks() == mine.getVolume()) {
                            mine.updateNextResetTick(); //don't reset but restart the timer
                        }
                        else {
                            mine.resetMine(true);
                        }
                    }
                }
            }
        };
    }

    private void sendResetMessage(Mine mine, int secondsRemaining) {
        String message = secondsRemaining == 1 ? Messages.mineResetCountdownSingular.replace("{mine_name}", mine.getName()) :
                Messages.mineResetCountdown.replace("{mine_name}", mine.getName()).replace("{time}", String.valueOf(secondsRemaining));

        for (Player player : mine.getPlayersInMine()) {
            player.sendMessage(message);
        }
    }

    /**
     * Resets all mines with a short delay between resets
     */
    public void resetAllMines() {
        new RepeatingTask(instance, 0, RESET_ALL_TICK_INTERVAL) {

            final ArrayList<Mine> arr = new ArrayList<>(mines.values());
            int i = 0;

            @Override
            public void run() {
                if (i >= arr.size()) {
                    cancel();
                    return;
                }

                // Checks if the mine is still loaded
                // This mine will be skipped in the event that it was deleted
                if (mines.containsKey(arr.get(i).getId())) {
                    arr.get(i++).resetMine(true);
                }
            }
        };
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().hasMetadata("player_placed")) {
            e.getBlock().removeMetadata("player_placed", instance);
            taggedBlocks.remove(e.getBlock().getLocation());
            return;
        }

        removeBlock(e.getBlock());
    }

    /**
     * Removes a block from all applicable mines
     * @param block The block
     */
    public void removeBlock(Block block) {
        for (Mine mine : getMinesByBlock(block)) {
            mine.blockRemovedFromMine();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onBlockPlace(BlockPlaceEvent e) {
        if (isInAnyMine(e.getBlock())) {
            e.getBlock().setMetadata("player_placed", new FixedMetadataValue(instance, true));
            taggedBlocks.add(e.getBlock().getLocation());
        }
    }

    /**
     * Adds a block to all applicable mines
     * @param block The block
     */
    public void addBlock(Block block) {
        for (Mine mine : getMinesByBlock(block)) {
            mine.blockPlacedInMine();
        }
    }

    @EventHandler
    private void onMineReset(MinePostResetEvent e) {
        if (removeDroppedItemsOnMineReset) {

            Iterator<Location> iterator = taggedBlocks.iterator();
            while (iterator.hasNext()) {
                Location loc = iterator.next();
                if (e.getMine().isInMine(loc)) {
                    loc.getBlock().removeMetadata("player_placed", instance);
                    iterator.remove();
                }
            }

            Region region = e.getMine().getRegion();
            BoundingBox boundingBox = new BoundingBox(region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ(),
                    region.getMaximumPoint().getX() + 1, region.getMaximumPoint().getY() + 1.5, region.getMaximumPoint().getZ() + 1);
            for (Entity entity : e.getMine().getWorld().getNearbyEntities(boundingBox).stream().filter(entity -> entity.getType().equals(EntityType.DROPPED_ITEM)).collect(Collectors.toList())) {
                entity.remove();
            }
        }
    }

    /**
     * @param block The block
     * @return A list of mines this block is in
     */
    @NotNull
    public Collection<Mine> getMinesByBlock(Block block) {
        return getMinesByBlock(block.getLocation());
    }

    /**
     * @param location The location
     * @return A list of mines this location is in
     */
    @NotNull
    public Collection<Mine> getMinesByBlock(Location location) {
        ArrayList<Mine> arr = new ArrayList<>();
        for (Mine mine : mines.values()) {
            if (mine.isInMine(location)) arr.add(mine);
        }
        return arr;
    }

    /**
     * Determines if this block is in at least one mine.
     * @param block The block
     * @return True if this block is in one or more mines
     */
    public boolean isInAnyMine(Block block) {
        return isInAnyMine(block.getLocation());
    }

    /**
     * Determines if this location is in at least one mine.
     * @param location The location
     * @return True if this location is in one or more mines
     */
    public boolean isInAnyMine(Location location) {
        for (Mine mine : mines.values()) {
            if (mine.isInMine(location)) return true;
        }
        return false;
    }

    /**
     * This method does not ensure that the same mine will be returned if the given block contains multiple mines.
     * This should only be used to get a mine if there is no overlap.
     * @param block The block
     * @return The first mine found at this location
     */
    @Nullable
    public Mine getFirstMineByBlock(Block block) {
        for (Mine mine : mines.values()) {
            if (mine.isInMine(block.getLocation())) return mine;
        }
        return null;
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

        Mine mine = new Mine(id, region.getMinimumPoint(), region.getMaximumPoint(), BukkitAdapter.adapt(selectionWorld));
        mines.put(id, mine);
        instance.getInventoryManager().getMineListGUI().addMine(mine);
        player.sendMessage(ChatColor.GREEN + "Successfully created new mine: " + id);
        return mine;
    }

    /**
     * Deletes an existing mine
     * If the mine does not exist, nothing will happen
     * @param sender The command sender
     * @param id The mine's id to remove
     */
    public void deleteMine(CommandSender sender, String id) {
        id = id.toLowerCase();
        if (!mines.containsKey(id)) {
            sender.sendMessage(ChatColor.RED + "No mine exists for this id");
            return;
        }

        Mine removed = mines.remove(id);
        instance.getInventoryManager().getMineListGUI().removeMine(removed);
        instance.getInventoryManager().closeMineMenu(removed);
        if (!removed.deleteConfigFile()) {
            sender.sendMessage(ChatColor.RED + "Failed to delete the mine file " + removed.getId() + ".yml - You will need to manually delete it or it will load in the future");
        }

        sender.sendMessage(ChatColor.YELLOW + "Successfully deleted mine: " + id);
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

    public int getTick() {
        return tick;
    }

}
