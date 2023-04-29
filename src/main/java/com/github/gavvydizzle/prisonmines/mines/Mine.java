package com.github.gavvydizzle.prisonmines.mines;

import com.github.gavvydizzle.prisonmines.gui.MineGUI;
import com.github.gavvydizzle.prisonmines.gui.settings.ResetPercentageMenu;
import com.github.gavvydizzle.prisonmines.gui.settings.ResetTimeMenu;
import com.github.gavvydizzle.prisonmines.mines.contents.MineContents;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.Numbers;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.events.MinePostResetEvent;
import com.github.gavvydizzle.prisonmines.mines.contents.MineBlock;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class Mine {

    private boolean failedToLoad;
    private final String accessPermission;

    // Configurable properties
    private final String id;
    private String name;
    private BlockVector3 min;
    private BlockVector3 max;
    private Location spawnLocation;
    private World world;
    private MineContents contents;
    private final File file;
    private final FileConfiguration config;
    private int resetLengthSeconds, resetPercentage;

    // Dynamic properties
    private final MineGUI mineGUI;
    private final ResetTimeMenu resetTimeMenu;
    private final ResetPercentageMenu resetPercentageMenu;
    private int volume;
    private int numSolidBlocks;

    // Internal stuff
    private final CuboidRegion region;
    private int nextResetTick;
    private ItemStack guiItem;
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
        this.id = id.toLowerCase();
        accessPermission = "prisonmines.mine." + id;
        this.name = id;

        this.min = min;
        this.max = max;
        region = new CuboidRegion(min, max);
        region.setWorld(BukkitAdapter.adapt(world));
        updateVolume();
        numSolidBlocks = volume;

        resetLengthSeconds = 600;
        resetPercentage = 0;
        spawnLocation = null;
        this.world = world;
        contents = new MineContents(this);
        mineGUI = new MineGUI(this);
        resetTimeMenu = new ResetTimeMenu(this);
        resetPercentageMenu = new ResetPercentageMenu(this);

        file = new File(PrisonMines.getInstance().getDataFolder(), "mines/" + id + ".yml");
        config = YamlConfiguration.loadConfiguration(file);
        config.options().copyDefaults(true);

        config.addDefault("id", id);
        config.addDefault("name", id);
        config.addDefault("world", world.getName());
        config.addDefault("resetLengthSeconds", resetLengthSeconds);
        config.addDefault("resetPercentage", resetPercentage);
        config.addDefault("loc.min.x", min.getX());
        config.addDefault("loc.min.y", min.getY());
        config.addDefault("loc.min.z", min.getZ());
        config.addDefault("loc.max.x", max.getX());
        config.addDefault("loc.max.y", max.getY());
        config.addDefault("loc.max.z", max.getZ());
        config.addDefault("contents.maxWeight", 1000);
        config.addDefault("contents.list", new ArrayList<>());

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        failedToLoad = false;
        createGUIItem();
    }

    /**
     * Loads the mine from a config file
     * @param file The file to load from
     */
    public Mine(File file) {
        this.file = file;

        config = YamlConfiguration.loadConfiguration(file);
        config.options().copyDefaults(true);

        config.addDefault("id", "todo");
        config.addDefault("name", "todo");
        config.addDefault("world", "world");
        config.addDefault("resetLengthSeconds", 600);
        config.addDefault("resetPercentage", 0);
        config.addDefault("loc.min.x", 0);
        config.addDefault("loc.min.y", 0);
        config.addDefault("loc.min.z", 0);
        config.addDefault("loc.max.x", 0);
        config.addDefault("loc.max.y", 0);
        config.addDefault("loc.max.z", 0);
        config.addDefault("contents.maxWeight", 1000);
        config.addDefault("contents.list", new ArrayList<>());

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
            failedToLoad = true;
        }

        id = Objects.requireNonNull(config.getString("id")).toLowerCase();
        accessPermission = "prisonmines.mine." + id;
        name = config.getString("name") != null ? Colors.conv(config.getString("name")) : id; // Set to the id if the name is not set
        String w = config.getString("world");
        world = Bukkit.getWorld(Objects.requireNonNull(w));
        if (world == null) {
            PrisonMines.getInstance().getLogger().warning("The world (" + w + ") is null for mine " + id + "! This mine is disabled");
        }
        resetLengthSeconds = config.getInt("resetLengthSeconds");
        resetPercentage = config.getInt("resetPercentage");
        min = BlockVector3.at(config.getInt("loc.min.x"), config.getInt("loc.min.y"), config.getInt("loc.min.z"));
        max = BlockVector3.at(config.getInt("loc.max.x"), config.getInt("loc.max.y"), config.getInt("loc.max.z"));
        spawnLocation = config.getLocation("loc.spawn");

        region = new CuboidRegion(min, max);
        region.setWorld(BukkitAdapter.adapt(world));

        contents = new MineContents(this, config.getStringList("contents.list"), config.getInt("contents.maxWeight"));
        mineGUI = new MineGUI(this);
        resetTimeMenu = new ResetTimeMenu(this);
        resetPercentageMenu = new ResetPercentageMenu(this);

        updateVolume();
        numSolidBlocks = volume;

        if (failedToLoad) return;

        failedToLoad = false;
        createGUIItem();
    }

    private void updateVolume() {
        volume = (int) region.getVolume();
    }

    /**
     * The permission is of the format "prisonmines.mine.id"
     * @param player The player
     * @return True if the player has permission to access this mine
     */
    public boolean hasAccessPermission(Player player) {
        return player.hasPermission(accessPermission);
    }

    private void createGUIItem() {
        guiItem = new ItemStack(Material.STONE);
        ItemMeta meta = guiItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&e" + id));
        guiItem.setItemMeta(meta);
    }


    /**
     * Resets this mine by replacing all blocks in the mine.
     * Calling this method schedules the next reset as well.
     */
    public void resetMine() {
        updateNextResetTick();
        resetTimeChanged = false;

        if (contents.isBlank()) return;

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
            Bukkit.getScheduler().runTask(PrisonMines.getInstance(), () -> Bukkit.getPluginManager().callEvent(new MinePostResetEvent(this)));
            teleportContainedPlayersToSpawn();

        } catch (MaxChangedBlocksException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Calling this method generates all locations every time
     * This method should be called as little as possible
     * @return A list of every location in this mine
     */
    public ArrayList<Location> getAllLocations() {
        ArrayList<Location> locations = new ArrayList<>(volume);
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
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
        if (resetTimeChanged) return;

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
                region.contains(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    /**
     * Teleports the player to this mine's spawn location
     * @param player The player to teleport
     * @return True if the player was teleported and false if the spawn point is not set
     */
    public boolean teleportToSpawn(Player player) {
        if (spawnLocation == null) return false;
        return player.teleport(spawnLocation);
    }

    /**
     * Teleports the player to this mine's center location
     * @param player The player to teleport
     * @return True if the player was teleported and false if the location is invalid
     */
    public boolean teleportToCenter(Player player) {
        if (world == null) return false;
        return player.teleport(getCenterLocation());
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
                loc.setY(max.getY() + 1);
                player.teleport(loc);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isInMine(player.getLocation()) && player.getGameMode() != GameMode.SPECTATOR) {

                // If the spawn location is not set, teleport the player to the top of the mine by increasing their y value
                if (!teleportToSpawn(player)) {
                    Location loc = player.getLocation();
                    loc.setY(max.getY() + 1);
                    player.teleport(loc);
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

    protected boolean deleteConfigFile() {
        return file.delete();
    }

    private void updateConfigFileAsync(String errorMessage) {
        Bukkit.getScheduler().runTaskAsynchronously(PrisonMines.getInstance(), () -> {
            try {
                config.save(file);
            } catch (IOException e) {
                PrisonMines.getInstance().getLogger().severe(errorMessage);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Updates the spawn location of this mine and pushes changes to the mine;s config file
     * @param location The new location
     * @return If the location was updated
     */
    public boolean updateSpawnLocation(@NotNull Location location) {
        if (!world.getUID().equals(Objects.requireNonNull(location.getWorld()).getUID())) {
            return false;
        }

        spawnLocation = location;

        config.set("loc.spawn", spawnLocation);
        updateConfigFileAsync("Failed to push spawn location update to " + file.getName());

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

        config.set("world", world.getName());
        config.set("loc.min.x", min.getBlockX());
        config.set("loc.min.y", min.getBlockY());
        config.set("loc.min.z", min.getBlockZ());
        config.set("loc.max.x", max.getBlockX());
        config.set("loc.max.y", max.getBlockY());
        config.set("loc.max.z", max.getBlockZ());

        updateConfigFileAsync("Failed to push region update to " + file.getName());
    }

    /**
     * Updates the max weight in this mine's config file
     */
    public void updateMaxWeight() {
        config.set("contents.maxWeight", contents.getMaxWeight());
        updateConfigFileAsync("Failed to push max weight update to " + file.getName());
    }

    /**
     * Updates the contents section of this mine's config file
     */
    public void pushContentsUpdate() {
        config.set("contents.list", contents.getContentsAsStrings());
        config.set("contents.maxWeight", contents.getMaxWeight());
        updateConfigFileAsync("(update) Failed to push contents update to " + file.getName());
    }

    /**
     * Updates the resetLengthSeconds value in the config file
     */
    public void pushResetTimeUpdate() {
        config.set("resetLengthSeconds", resetLengthSeconds);
        updateConfigFileAsync("Failed to push reset length update to " + file.getName());
    }

    /**
     * Updates the resetPercentage value in the config file
     */
    public void pushResetPercentageUpdate() {
        config.set("resetPercentage", resetPercentage);
        updateConfigFileAsync("Failed to push reset percentage update to " + file.getName());
    }

    /**
     * Copies the contents of the mine and pushes changes to the mine's config file
     * @param mine The mine to copy from
     */
    public void copyContentsFrom(Mine mine) {
        contents = new MineContents(mine.contents, this);
        mineGUI.update();

        config.set("contents.list", contents.getContentsAsStrings());
        config.set("contents.maxWeight", contents.getMaxWeight());
        updateConfigFileAsync("(copy) Failed to push contents update to " + file.getName());
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

        config.set("name", newName);
        updateConfigFileAsync("Failed to push name update to " + file.getName());
    }

    /**
     * Updates the reset length of the mine
     * @param seconds The amount of time in seconds. Constrained to 60 <= x <= 86400
     */
    public void setResetLengthSeconds(int seconds) {
        seconds = Numbers.constrain(seconds, 60, 86400);
        resetLengthSeconds = seconds;
    }

    /**
     * Updates the percentage the mine will reset at
     * @param percent The percent. Constrained to 0 <= x <= 95
     */
    public void setResetPercentage(int percent) {
        percent = Numbers.constrain(percent, 0, 95);
        resetPercentage = percent;
    }

    /**
     * Gets the minimum location of the mine
     * @return A new Location object
     */
    public Location getMinLocation() {
        return new Location(world, min.getX(), min.getY(), min.getZ());
    }

    /**
     * Gets the maximum location of the mine
     * @return A new Location object
     */
    public Location getMaxLocation() {
        return new Location(world, max.getX(), max.getY(), max.getZ());
    }

    /**
     * Gets the center location of the mine
     * The y value of this location will be one higher than the max point's y value
     * @return A new Location object
     */
    public Location getCenterLocation() {
        // Add 0.5 to x and z to account for the max point not being the border of the mine
        Vector3 center = region.getCenter();
        return new Location(world, center.getX() + 0.5, max.getY() + 1, center.getZ() + 0.5);
    }

    public boolean failedToLoad() {
        return failedToLoad;
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

    public int getVolume() {
        return volume;
    }

    public int getNumSolidBlocks() {
        return numSolidBlocks;
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

    public ResetTimeMenu getResetTimeMenu() {
        return resetTimeMenu;
    }

    public ResetPercentageMenu getResetPercentageMenu() {
        return resetPercentageMenu;
    }

    public ItemStack getGuiItem() {
        return guiItem;
    }

    public CuboidRegion getRegion() {
        return region;
    }

    public World getWorld() {
        return world;
    }
}
