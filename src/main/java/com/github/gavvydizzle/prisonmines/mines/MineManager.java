package com.github.gavvydizzle.prisonmines.mines;

import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.events.MinePostResetEvent;
import com.github.gavvydizzle.prisonmines.utils.Messages;
import com.github.mittenmc.lib.folialib.impl.PlatformScheduler;
import com.github.mittenmc.serverutils.Numbers;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

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

        reload(true);
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
                        mines.put(mine.getId(), mine);
                    } catch (Exception e) {
                        instance.getLogger().log(Level.SEVERE, "Failed to load mine: " + file.getName() + "!", e);
                    }
                }
            }
        }
    }

    /**
     * Reloads the main config.yml and reloads all mines with the loadMines() method
     */
    public void reload(boolean firstLoad) {
        FileConfiguration config = instance.getConfig();
        config.options().copyDefaults(true);
        config.addDefault("resetOnServerStart.enabled", false);
        config.addDefault("resetOnServerStart.randomizeTime.enabled", false);
        config.addDefault("resetOnServerStart.randomizeTime.min", 0.1);
        config.addDefault("resetMessageSeconds", Arrays.asList(10, 5, 1));
        config.addDefault("removeDroppedItemsOnMineReset", true);
        config.addDefault("disableMineCommand", false);
        config.addDefault("resetWhenMineFull", false);
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
        if (firstLoad && config.getBoolean("resetOnServerStart.enabled")) {
            if (config.getBoolean("resetOnServerStart.randomizeTime.enabled")) {
                resetAllMines(config.getInt("resetOnServerStart.randomizeTime.min"), true);
            }
            else {
                resetAllMines(true);
            }
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
        instance.getFoliaLib().getScheduler().runTimer(new BukkitRunnable() {
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
                            mine.resetMine(true, false);
                        }
                    }
                }
            }
        }, 0, 20);
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
     * @param serverStart If this reset was triggered by the server start setting
     */
    public void resetAllMines(boolean serverStart) {
        if (instance.getFoliaLib().isFolia()) {
            for (Mine mine : mines.values()) {
                mine.resetMine(true, serverStart);
            }
        } else {
            final ArrayList<Mine> arr = new ArrayList<>(mines.values());
            final int[] i = {0};

            instance.getFoliaLib().getScheduler().runTimer((t) -> {
                if (i[0] == arr.size()) {
                    t.cancel();
                    return;
                }

                // Checks if the mine is still loaded
                // This mine will be skipped in the event that it was deleted
                if (mines.containsKey(arr.get(i[0]).getId())) {
                    arr.get(i[0]++).resetMine(true, serverStart);
                }
            }, 0, RESET_ALL_TICK_INTERVAL);
        }
    }

    /**
     * Resets all mines with a short delay between resets.
     * The reset time of all mine will be randomly chosen within the range [multiplier*time, time]
     * @param multiplier The amount to multiply the time by to get the minimum bound
     * @param serverStart If this reset was triggered by the server start setting
     */
    public void resetAllMines(double multiplier, boolean serverStart) {
        if (instance.getFoliaLib().isFolia()) {
            for (Mine mine : mines.values()) {
                mine.resetMine(true, multiplier, serverStart);
            }
        } else {
            final ArrayList<Mine> arr = new ArrayList<>(mines.values());
            final int[] i = {0};

            instance.getFoliaLib().getScheduler().runTimer((t) -> {
                if (i[0] == arr.size()) {
                    t.cancel();
                    return;
                }

                // Checks if the mine is still loaded
                // This mine will be skipped in the event that it was deleted
                if (mines.containsKey(arr.get(i[0]).getId())) {
                    arr.get(i[0]++).resetMine(true, multiplier, serverStart);
                }
            }, 0, RESET_ALL_TICK_INTERVAL);
        }
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
            BoundingBox boundingBox = new BoundingBox(region.getMinimumPoint().x() - 1, region.getMinimumPoint().y(), region.getMinimumPoint().z() - 1,
                    region.getMaximumPoint().x() + 1, region.getMaximumPoint().y() + 1.5, region.getMaximumPoint().z() + 1);

            if (instance.getFoliaLib().isFolia()) {
                // The folia approach follows the following steps:
                // 1. Calculate every chunk containing the deletion bounding box
                // 2. Run a delete task for every chunk

                org.bukkit.World world = e.getMine().getWorld();

                // Get chunk coordinates for the bounding box
                int minChunkX = (int) Math.floor(boundingBox.getMinX() / 16.0);
                int maxChunkX = (int) Math.floor(boundingBox.getMaxX() / 16.0);
                int minChunkZ = (int) Math.floor(boundingBox.getMinZ() / 16.0);
                int maxChunkZ = (int) Math.floor(boundingBox.getMaxZ() / 16.0);

                // Loop through the chunks that the bounding box overlaps
                for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
                    for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                        world.getChunkAtAsyncUrgently(chunkX, chunkZ)
                                .thenAccept(chunk -> removeItemsFromChunk(instance.getFoliaLib().getScheduler(), chunk, boundingBox));
                    }
                }
            } else {
                for (Entity entity : e.getMine().getWorld().getNearbyEntities(boundingBox).stream().filter(entity -> entity.getType().equals(EntityType.ITEM)).toList()) {
                    entity.remove();
                }
            }
        }
    }

    /**
     * Removed all ItemStack entities present in this chunk which are within the bounding box
     * @param scheduler The scheduler to use
     * @param chunk The chunk which must be loaded
     * @param boundingBox The bounding box
     */
    private void removeItemsFromChunk(PlatformScheduler scheduler, Chunk chunk, BoundingBox boundingBox) {
        scheduler.runAtLocation(chunk.getBlock(0,0,0).getLocation(), (t) -> {
            Arrays.stream(chunk.getEntities())
                    .filter(entity -> entity.getType().equals(EntityType.ITEM))
                    .filter(entity -> boundingBox.contains(entity.getLocation().toVector()))
                    .forEach(Entity::remove);
        });
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
        instance.getInventoryManager().getMineListGUI().addItem(mine);
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
        instance.getInventoryManager().getMineListGUI().removeItem(removed);
        instance.getInventoryManager().closeMineMenu(removed);

        removed.delete();
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
