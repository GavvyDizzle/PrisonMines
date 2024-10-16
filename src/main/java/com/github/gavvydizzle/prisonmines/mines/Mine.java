package com.github.gavvydizzle.prisonmines.mines;

import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.events.MinePostResetEvent;
import com.github.gavvydizzle.prisonmines.gui.MineGUI;
import com.github.gavvydizzle.prisonmines.mines.contents.MineBlock;
import com.github.gavvydizzle.prisonmines.mines.contents.MineContents;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.Numbers;
import com.github.mittenmc.serverutils.file.FileEntity;
import com.github.mittenmc.serverutils.gui.pages.ItemGenerator;
import com.github.mittenmc.serverutils.item.ItemStackBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class Mine extends FileEntity implements Comparable<Mine>, ItemGenerator {

    private String accessPermission;

    // Configurable properties
    private String id;
    private String name;
    private BlockVector3 min;
    private BlockVector3 max;
    private Location spawnLocation;
    private World world;
    private MineContents contents;
    private int resetLengthSeconds, resetPercentage;

    // Dynamic properties
    private MineGUI mineGUI;
    private int volume;
    private int numSolidBlocks;
    private boolean isResettingPaused;
    private boolean blockResetOnServerStart;

    // Internal stuff
    private CuboidRegion region;
    private int nextResetTick;
    private boolean resetTimeChanged; // If the reset time has been edited because to the reset percentage has been reached

    /**
     * Creates a new mine from two Locations
     * This will create a new file and save default values to it in the mines folder
     * @param id The id
     * @param min The min point
     * @param max The max point
     * @param world The world
     */
    public Mine(String id, BlockVector3 min, BlockVector3 max, @NotNull World world) {
        super(new File(PrisonMines.getInstance().getDataFolder(), "mines/" + id.toLowerCase() + ".yml"));

        this.id = id.toLowerCase();
        this.name = id;

        this.min = min;
        this.max = max;

        this.world = world;
        contents = new MineContents(this);
        mineGUI = new MineGUI(this);

        super.reload();
    }

    /**
     * Loads the mine from a config file
     * @param file The file to load from
     */
    public Mine(File file) {
        super(file);

        super.reload();
    }

    @Override
    public void reloadData(FileConfiguration config) {
        config.addDefault("id", (id != null ? id : "todo"));
        config.addDefault("name", (name != null ? name : "todo"));
        config.addDefault("world", (world != null ? world.getName() : "world"));
        config.addDefault("blockResetOnServerStart", false);
        config.addDefault("resetLengthSeconds", 600);
        config.addDefault("resetPercentage", 0);
        config.addDefault("loc.min.x", (min != null ? min.x() : 0));
        config.addDefault("loc.min.y", (min != null ? min.y() : 0));
        config.addDefault("loc.min.z", (min != null ? min.z() : 0));
        config.addDefault("loc.max.x", (max != null ? max.x() : 0));
        config.addDefault("loc.max.y", (max != null ? max.y() : 0));
        config.addDefault("loc.max.z", (max != null ? max.z() : 0));
        config.addDefault("contents.maxWeight", 1000);
        config.addDefault("contents.list", new ArrayList<>());

        if (id == null) id = config.getString("id", UUID.randomUUID().toString()).toLowerCase();
        accessPermission = "prisonmines.mine." + id;
        name = config.getString("name") != null ? Colors.conv(config.getString("name")) : id; // Set to the id if the name is not set

        String w = config.getString("world");
        world = Bukkit.getWorld(Objects.requireNonNull(w));
        if (world == null) {
            PrisonMines.getInstance().getLogger().warning("The world '" + w + "' is null for mine " + id + "! This mine is disabled");
        }

        blockResetOnServerStart = config.getBoolean("blockResetOnServerStart");
        resetLengthSeconds = config.getInt("resetLengthSeconds");
        resetPercentage = config.getInt("resetPercentage");
        min = BlockVector3.at(config.getInt("loc.min.x"), config.getInt("loc.min.y"), config.getInt("loc.min.z"));
        max = BlockVector3.at(config.getInt("loc.max.x"), config.getInt("loc.max.y"), config.getInt("loc.max.z"));
        spawnLocation = config.getLocation("loc.spawn");

        region = new CuboidRegion(min, max);
        region.setWorld(BukkitAdapter.adapt(world));

        contents = new MineContents(this, config.getStringList("contents.list"), config.getInt("contents.maxWeight"));
        mineGUI = new MineGUI(this);

        updateVolume();
        numSolidBlocks = volume;
    }

    private void updateVolume() {
        volume = (int) region.getVolume();
    }

    /**
     * The permission is of the format "prisonmines.mine.id"
     * @param player The player
     * @return True if the player does not have permission to access this mine
     */
    public boolean doesNotHaveAccessPermission(Player player) {
        return !player.hasPermission(accessPermission);
    }

    /**
     * Resets this mine by replacing all blocks in the mine.
     * Calling this method schedules the next reset as well.
     * If a paused mine resets then its timer resumes.
     * @param resumeFromPause If a paused timer should be resumed
     * @param serverStart If this reset was triggered by the server start setting
     */
    public void resetMine(boolean resumeFromPause, boolean serverStart) {
        if (serverStart && blockResetOnServerStart) return;

        updateNextResetTick();
        resetTimeChanged = false;
        if (resumeFromPause) isResettingPaused = false;

        if (contents.isBlank()) return;

        double percentRemaining = getPercentRemaining();

        com.sk89q.worldedit.world.World w = BukkitAdapter.adapt(world);
        CuboidRegion selection = new CuboidRegion(w, min, max);

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(w)) {
            RandomPattern pat = new RandomPattern();

            // Make the various WorldEdit block states by using the BukkitAdapter from the spigot block data
            for (MineBlock mineBlock : contents.getBlockList()) {
                if (mineBlock.getWeight() > 0) {
                    pat.add(BukkitAdapter.adapt(mineBlock.getMaterial().createBlockData()), mineBlock.getWeight());
                }
            }

            // Pass in the region and pattern
            editSession.setBlocks(selection, pat);

            numSolidBlocks = volume;
            PrisonMines.getInstance().getFoliaLib().getScheduler().runLater((t) -> Bukkit.getPluginManager().callEvent(new MinePostResetEvent(this, percentRemaining)), 1L);
            teleportContainedPlayersToSpawn();

        } catch (MaxChangedBlocksException ex) {
            PrisonMines.getInstance().getLogger().log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    /**
     * Resets this mine by replacing all blocks in the mine.
     * Calling this method schedules the next reset as well.
     * If a paused mine resets then its timer resumes.
     * @param resumeFromPause If a paused timer should be resumed
     * @param multiplier The multiplier used to determine the minimum bound of the random time remaining
     * @param serverStart If this reset was triggered by the server start setting
     */
    public void resetMine(boolean resumeFromPause, double multiplier, boolean serverStart) {
        if (serverStart && blockResetOnServerStart) return;

        updateNextResetTick((int) Numbers.randomNumber(resetLengthSeconds * multiplier, resetLengthSeconds));
        resetTimeChanged = false;
        if (resumeFromPause) isResettingPaused = false;

        if (contents.isBlank()) return;

        double percentRemaining = getPercentRemaining();

        com.sk89q.worldedit.world.World w = BukkitAdapter.adapt(world);
        CuboidRegion selection = new CuboidRegion(w, min, max);

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(w)) {
            RandomPattern pat = new RandomPattern();

            // Make the various WorldEdit block states by using the BukkitAdapter from the spigot block data
            for (MineBlock mineBlock : contents.getBlockList()) {
                if (mineBlock.getWeight() > 0) {
                    pat.add(BukkitAdapter.adapt(mineBlock.getMaterial().createBlockData()), mineBlock.getWeight());
                }
            }

            // Pass in the region and pattern
            editSession.setBlocks(selection, pat);

            numSolidBlocks = volume;
            PrisonMines.getInstance().getFoliaLib().getScheduler().runLater((t) -> Bukkit.getPluginManager().callEvent(new MinePostResetEvent(this, percentRemaining)), 1L);
            teleportContainedPlayersToSpawn();

        } catch (MaxChangedBlocksException ex) {
            PrisonMines.getInstance().getLogger().log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    /**
     * Sets all blocks in the mine to air
     */
    public void clearMine() {
        com.sk89q.worldedit.world.World w = BukkitAdapter.adapt(world);

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(w)) {
            RandomPattern pat = new RandomPattern();
            pat.add(BukkitAdapter.adapt(Material.AIR.createBlockData()), 1);

            // Create analogous WorldEdit region and set it to air
            CuboidRegion cr = new CuboidRegion(region.getMinimumPoint(), region.getMaximumPoint());
            editSession.replaceBlocks(cr, Masks.alwaysTrue(), pat);

        } catch (MaxChangedBlocksException ex) {
            PrisonMines.getInstance().getLogger().log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    /**
     * Calling this method generates all locations every time
     * This method should be called as little as possible
     * @return A list of every location in this mine
     */
    public ArrayList<Location> getAllLocations() {
        ArrayList<Location> locations = new ArrayList<>(volume);
        for (int x = min.x(); x <= max.x(); x++) {
            for (int y = min.y(); y <= max.y(); y++) {
                for (int z = min.z(); z <= max.z(); z++) {
                    locations.add(new Location(world, x, y, z));
                }
            }
        }
        return locations;
    }

    /**
     * Removes one block from this mine's counter
     */
    public void blockPlacedInMine() {
        numSolidBlocks++;
        if (numSolidBlocks > volume) numSolidBlocks = volume;
    }

    /**
     * Adds one block to this mine's counter
     */
    public void blockRemovedFromMine() {
        numSolidBlocks--;
        if (numSolidBlocks < 0) numSolidBlocks = 0;
        attemptPercentageReset();
    }

    /**
     * If the reset percentage has been reached, the mine will be set to reset in 11 seconds from now
     * It is set to 11 seconds so a 10-second reset warning message can be sent
     */
    private void attemptPercentageReset() {
        if (resetTimeChanged || resetPercentage == -1) return;

        if (numSolidBlocks <= volume * resetPercentage / 100) {
            resetTimeChanged = true;
            int n = PrisonMines.getInstance().getMineManager().getTick() + 11;
            if (n < nextResetTick) {
                nextResetTick = n;
            }
        }
    }

    /**
     * Determines if the location is within the mine
     * @param location The location to check for
     * @return If the location is within the mine
     */
    public boolean isInMine(@NotNull Location location) {
        return Objects.requireNonNull(location.getWorld()).getUID().equals(world.getUID()) &&
                region.contains(BlockVector3.at(location.x(), location.y(), location.z()));
    }

    /**
     * Teleports the player to this mine's spawn location
     * @param player The player to teleport
     * @return True if the player was teleported and false if the spawn point is not set
     */
    public boolean teleportToSpawn(Player player) {
        if (spawnLocation == null) return false;

        PrisonMines.getInstance().getFoliaLib().getScheduler().teleportAsync(player, spawnLocation);
        return true;
    }

    /**
     * Teleports the player to this mine's center location
     * @param player The player to teleport
     * @return True if the player was teleported and false if the location is invalid
     */
    public boolean teleportToCenter(Player player) {
        if (world == null) return false;

        PrisonMines.getInstance().getFoliaLib().getScheduler().teleportAsync(player, getCenterSurfaceLocation());
        return true;
    }

    /**
     * Teleports all players to the mine spawn on reset.
     * If the spawn point is null, players will be teleported to the top of the mine.
     */
    public void teleportContainedPlayersToSpawn() {
        for (Player player : getPlayersInMine()) {
            if (player.getGameMode() == GameMode.SPECTATOR) continue;

            // If the spawn location is not set, teleport the player to the top of the mine by increasing their y value
            if (!teleportToSpawn(player)) {
                Location loc = player.getLocation();
                loc.setY(max.y() + 1);
                PrisonMines.getInstance().getFoliaLib().getScheduler().teleportAsync(player, loc);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isInMine(player.getLocation()) && player.getGameMode() != GameMode.SPECTATOR) {

                // If the spawn location is not set, teleport the player to the top of the mine by increasing their y value
                if (!teleportToSpawn(player)) {
                    Location loc = player.getLocation();
                    loc.setY(max.y() + 1);
                    PrisonMines.getInstance().getFoliaLib().getScheduler().teleportAsync(player, loc);
                }
            }
        }
    }

    /**
     * @return A list of players in this mine
     */
    public Collection<Player> getPlayersInMine() {
        Collection<Player> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isInMine(player.getLocation())) players.add(player);
        }
        return players;
    }

    /**
     * Updates the spawn location of this mine and pushes changes to the mine's config file
     * @param location The new location
     * @return If the location was updated
     */
    public boolean updateSpawnLocation(@NotNull Location location) {
        if (!world.getUID().equals(Objects.requireNonNull(location.getWorld()).getUID())) {
            return false;
        }

        spawnLocation = location;
        mineGUI.refresh();

        super.getConfig().set("loc.spawn", spawnLocation);
        saveConfigAsync();

        return true;
    }

    /**
     * Updates the corners of this mine and pushes changes to the mine's config file.
     * If the world has changed, then the spawn location will be set to null.
     * @param min The minimum point
     * @param max The maximum point
     * @param world The world
     */
    protected void updateRegion(BlockVector3 min, BlockVector3 max, World world) {
        if (!this.world.getUID().equals(world.getUID())) {
            spawnLocation = null;
            PrisonMines.getInstance().getLogger().warning("The world for mine " + id + " has changed. The spawn location has been deleted");
        }

        this.world = world;
        this.min = min;
        this.max = max;
        region.setWorld(BukkitAdapter.adapt(world));
        region.setPos1(min);
        region.setPos2(max);
        updateVolume();

        super.getConfig().set("world", world.getName());
        super.getConfig().set("loc.min.x", min.x());
        super.getConfig().set("loc.min.y", min.y());
        super.getConfig().set("loc.min.z", min.z());
        super.getConfig().set("loc.max.x", max.x());
        super.getConfig().set("loc.max.y", max.y());
        super.getConfig().set("loc.max.z", max.z());

        saveConfigAsync();
    }

    /**
     * Updates the max weight in this mine's config file
     */
    public void updateMaxWeight() {
        super.getConfig().set("contents.maxWeight", contents.getMaxWeight());
        saveConfigAsync();
    }

    /**
     * Updates the contents section of this mine's config file
     */
    public void pushContentsUpdate() {
        super.getConfig().set("contents.list", contents.getContentsAsStrings());
        super.getConfig().set("contents.maxWeight", contents.getMaxWeight());
        saveConfigAsync();
    }

    /**
     * Updates the resetLengthSeconds value in the config file
     */
    public void pushResetTimeUpdate() {
        super.getConfig().set("resetLengthSeconds", resetLengthSeconds);
        saveConfigAsync();
    }

    /**
     * Updates the resetPercentage value in the config file
     */
    public void pushResetPercentageUpdate() {
        super.getConfig().set("resetPercentage", resetPercentage);
        saveConfigAsync();
    }

    /**
     * Copies the contents of the mine and pushes changes to the mine's config file
     * @param mine The mine to copy from
     */
    public void copyContentsFrom(Mine mine) {
        contents = new MineContents(mine.contents, this);
        mineGUI.refresh();

        super.getConfig().set("contents.list", contents.getContentsAsStrings());
        super.getConfig().set("contents.maxWeight", contents.getMaxWeight());
        saveConfigAsync();
    }

    //***** GETTERS & SETTERS *****//

    public String getId() {
        return id;
    }

    /**
     * @return A formatted name for this mine
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the name of the mine and pushes changes to the mine's config file
     * @param newName The new name (supports color codes)
     */
    public void setName(String newName) {
        name = Colors.conv(newName);
        mineGUI.refresh();

        super.getConfig().set("name", newName);
        saveConfigAsync();
    }

    /**
     * Updates the reset length of the mine
     * @param seconds The amount of time in seconds. Constrained to 60 <= x <= 86400
     */
    public void setResetLengthSeconds(int seconds) {
        seconds = Numbers.constrain(seconds, 60, 86400);
        resetLengthSeconds = seconds;
        mineGUI.refresh();
    }

    /**
     * Updates the percentage the mine will reset at.
     * If set to -1, the mine will never reset due to blocks broken
     * @param percent The percent. Constrained to -1 <= x <= 95
     */
    public void setResetPercentage(int percent) {
        percent = Numbers.constrain(percent, -1, 95);
        resetPercentage = percent;
        mineGUI.refresh();
    }

    /**
     * Gets the minimum location of the mine
     * @return A new Location object
     */
    public Location getMinLocation() {
        return new Location(world, min.x(), min.y(), min.z());
    }

    /**
     * Gets the maximum location of the mine
     * @return A new Location object
     */
    public Location getMaxLocation() {
        return new Location(world, max.x(), max.y(), max.z());
    }

    /**
     * Gets the center location of the mine on the top surface.
     * The y value of this location will be one higher than the max point's y value
     * @return A new Location object
     */
    public Location getCenterSurfaceLocation() {
        // Add 0.5 to x and z to account for the max point not being the border of the mine
        Vector3 center = region.getCenter();
        return new Location(world, center.x() + 0.5, max.y() + 1, center.z() + 0.5);
    }

    public boolean hasSpawnLocation() {
        return spawnLocation != null;
    }

    public int getResetLengthSeconds() {
        return resetLengthSeconds;
    }

    public int getResetPercentage() {
        return resetPercentage;
    }

    public int getNextResetTick() {
        return nextResetTick;
    }

    public void updateNextResetTick() {
        nextResetTick = PrisonMines.getInstance().getMineManager().getTick() + resetLengthSeconds;
    }

    /**
     * Updates the reset time of this mine and keeps the value within an acceptable range
     * @param resetLengthSeconds The time until the next reset in seconds
     */
    public void updateNextResetTick(int resetLengthSeconds) {
        resetLengthSeconds = Numbers.constrain(resetLengthSeconds, 11, this.resetLengthSeconds);
        nextResetTick = PrisonMines.getInstance().getMineManager().getTick() + resetLengthSeconds;
    }

    public int getVolume() {
        return volume;
    }

    public int getNumSolidBlocks() {
        return numSolidBlocks;
    }

    public boolean isResettingPaused() {
        return isResettingPaused;
    }

    public void toggleIsResettingPaused() {
        isResettingPaused = !isResettingPaused;
    }

    public double getPercentMined() {
        return 100 - getPercentRemaining();
    }

    public double getPercentRemaining() {
        return numSolidBlocks * 100.0 / volume;
    }

    public MineContents getContents() {
        return contents;
    }

    public MineGUI getMineGUI() {
        return mineGUI;
    }

    public CuboidRegion getRegion() {
        return region;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public @NotNull ItemStack getMenuItem(Player player) {
        return ItemStackBuilder.of(contents.getMostCommonBlock(Material.BEDROCK))
                .name("&e" + id)
                .lore(
                    "&7Name: " + name,
                        "&7Spawn Location set: " + (hasSpawnLocation() ? "&aYes" : "&cNo"),
                        "&7Reset Time: &a" + Numbers.getTimeFormatted(resetLengthSeconds),
                        "&7Reset Percentage: " + (resetPercentage == -1 ? "&cDISABLED (-1)" : "&a" + resetPercentage + "%"),
                        ""
                )
                .lore(contents.getInfoItem().getLore())
                .build();
    }

    @Override
    public @Nullable ItemStack getPlayerItem(Player player) {
        return null;
    }

    @Override
    public int compareTo(@NotNull Mine o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Mine o)) return false;
        return id.equals(o.id);
    }
}
