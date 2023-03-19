package me.github.gavvydizzle.prisonmines.mines;

import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.Numbers;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.github.gavvydizzle.prisonmines.PrisonMines;
import me.github.gavvydizzle.prisonmines.events.MineResetEvent;
import me.github.gavvydizzle.prisonmines.gui.MineGUI;
import me.github.gavvydizzle.prisonmines.mines.contents.MineBlock;
import me.github.gavvydizzle.prisonmines.mines.contents.MineContents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Mine {

    private boolean failedToLoad;

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
    private int resetLengthSeconds;

    // Dynamic properties
    private final MineGUI mineGUI;
    private int volume;
    private int numSolidBlocks;

    // Internal stuff
    private int nextResetTick;
    private ItemStack guiItem;

    /**
     * Creates a new mine from two Locations
     * This will create a new file and save default values to it in the mines folder
     * @param id The id
     * @param min The min point
     * @param max The max point
     * @param world The world
     */
    public Mine(String id, BlockVector3 min, BlockVector3 max, World world) {
        this.id = id.toLowerCase();
        this.name = id;

        this.min = min;
        this.max = max;
        updateVolume();

        resetLengthSeconds = 600;
        spawnLocation = null;
        this.world = world;
        contents = new MineContents();
        mineGUI = new MineGUI(this);

        file = new File(PrisonMines.getInstance().getDataFolder(), "mines/" + id + ".yml");
        config = YamlConfiguration.loadConfiguration(file);
        config.options().copyDefaults(true);

        config.addDefault("id", id);
        config.addDefault("name", id);
        assert world != null;
        config.addDefault("world", world.getName());
        config.addDefault("resetLengthSeconds", resetLengthSeconds);
        config.addDefault("loc.min.x", min.getX());
        config.addDefault("loc.min.y", min.getY());
        config.addDefault("loc.min.z", min.getZ());
        config.addDefault("loc.max.x", max.getX());
        config.addDefault("loc.max.y", max.getY());
        config.addDefault("loc.max.z", max.getZ());
        config.addDefault("loc.spawn", null);
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
        config.addDefault("loc.min.x", 0);
        config.addDefault("loc.min.y", 0);
        config.addDefault("loc.min.z", 0);
        config.addDefault("loc.max.x", 0);
        config.addDefault("loc.max.y", 0);
        config.addDefault("loc.max.z", 0);
        config.addDefault("loc.spawn", null);
        config.addDefault("contents.maxWeight", 1000);
        config.addDefault("contents.list", new ArrayList<>());

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
            failedToLoad = true;
        }

        id = Objects.requireNonNull(config.getString("id")).toLowerCase();
        name = config.getString("name") != null ? Colors.conv(config.getString("name")) : id; // Set to the id if the name is not set
        String w = config.getString("world");
        world = Bukkit.getWorld(Objects.requireNonNull(w));
        if (world == null) {
            PrisonMines.getInstance().getLogger().warning("The world (" + w + ") is null for mine " + id + "! This mine is disabled");
        }
        resetLengthSeconds = config.getInt("resetLengthSeconds");
        min = BlockVector3.at(config.getInt("loc.min.x"), config.getInt("loc.min.y"), config.getInt("loc.min.z"));
        max = BlockVector3.at(config.getInt("loc.max.x"), config.getInt("loc.max.y"), config.getInt("loc.max.z"));
        spawnLocation = config.getLocation("loc.spawn");

        contents = new MineContents(config.getStringList("contents.list"), config.getInt("contents.maxWeight"));
        mineGUI = new MineGUI(this);

        updateVolume();

        if (failedToLoad) return;

        failedToLoad = false;
        createGUIItem();
    }

    private void updateVolume() {
        volume = (max.getX() - min.getX()) * (max.getY() - min.getY()) * (max.getZ() - min.getZ());
        numSolidBlocks = volume;
    }

    private void createGUIItem() {
        guiItem = new ItemStack(Material.STONE);
        ItemMeta meta = guiItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&e" + id));
        guiItem.setItemMeta(meta);
    }


    public void resetMine() {
        if (contents.isBlank()) return;

        com.sk89q.worldedit.world.World w = BukkitAdapter.adapt(world);
        CuboidRegion selection = new CuboidRegion(w, min, max);

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(w)) {
            RandomPattern pat = new RandomPattern();

            // Make the various WorldEdit block states by using the BukkitAdapter from the spigot block data
            for (MineBlock mineBlock : contents.getBlockList()) {
                pat.add(BukkitAdapter.adapt(mineBlock.getMaterial().createBlockData()), mineBlock.getWeight());
            }

            // Pass in the region and pattern
            editSession.setBlocks(selection, pat);

            //TODO - Fire mine reset event
            //TODO - Make sure that the world has been updated before calling it
            Bukkit.getScheduler().runTask(PrisonMines.getInstance(), () -> Bukkit.getPluginManager().callEvent(new MineResetEvent(this)));

        } catch (MaxChangedBlocksException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes one block from this mine's counter
     */
    public void blockPlacedInMine() {
        numSolidBlocks++;
        if (numSolidBlocks > volume) numSolidBlocks = volume;
    }

    /**
     * Removes blocks from this mine's counter
     * @param amount The amount
     */
    public void blockPlacedInMine(int amount) {
        numSolidBlocks += amount;
        if (numSolidBlocks > volume) numSolidBlocks = volume;
    }

    /**
     * Adds one block to this mine's counter
     */
    public void blockRemovedFromMine() {
        numSolidBlocks--;
        if (numSolidBlocks < 0) numSolidBlocks = 0;
    }

    /**
     * Adds blocks to this mine's counter
     * @param amount The amount
     */
    public void blockRemovedFromMine(int amount) {
        numSolidBlocks -= amount;
        if (numSolidBlocks < 0) numSolidBlocks = 0;
    }

    /**
     * Determines if the location is within the mine
     * @param location The location to check for
     * @return If the location is within the mine
     */
    public boolean isInMine(Location location) {
        return Objects.requireNonNull(location.getWorld()).getUID().equals(world.getUID()) &&
                location.getBlockX() >= min.getX() &&
                location.getBlockX() <= max.getX() &&
                location.getBlockY() >= min.getY() &&
                location.getBlockY() <= max.getY() &&
                location.getBlockZ() >= min.getZ() &&
                location.getBlockZ() <= max.getZ();
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

    public boolean setSpawnLocation(Location location) {
        if (!Objects.requireNonNull(location.getWorld()).getUID().equals(Objects.requireNonNull(spawnLocation.getWorld()).getUID())) {
            return false;
        }

        spawnLocation = location;
        return true;
    }

    /**
     * Updates the corners of this mine and pushes changes to the mine's config file
     * @param min The minimum point
     * @param max The maximum point
     * @param world The world
     */
    protected void updateRegion(BlockVector3 min, BlockVector3 max, World world) {
        this.world = world;
        this.min = min;
        this.max = max;

        config.set("world", world.getName());
        config.set("loc.min.x", min.getBlockX());
        config.set("loc.min.y", min.getBlockY());
        config.set("loc.min.z", min.getBlockZ());
        config.set("loc.max.x", max.getBlockX());
        config.set("loc.max.y", max.getBlockY());
        config.set("loc.max.z", max.getBlockZ());

        Bukkit.getScheduler().runTaskAsynchronously(PrisonMines.getInstance(), () -> {
            try {
                config.save(file);
            } catch (IOException e) {
                PrisonMines.getInstance().getLogger().severe("Failed to push region update to " + file.getName());
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Updates the contents section of this mine's config file
     */
    public void pushContentsUpdate() {
        config.set("contents.list", contents.getContentsAsStrings());
        config.set("contents.maxWeight", contents.getMaxWeight());

        Bukkit.getScheduler().runTaskAsynchronously(PrisonMines.getInstance(), () -> {
            try {
                config.save(file);
            } catch (IOException e) {
                PrisonMines.getInstance().getLogger().severe("(update) Failed to push contents update to " + file.getName());
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Copies the contents of the mine and pushes changes to the mine's config file
     * @param mine The mine to copy from
     */
    public void copyContents(Mine mine) {
        contents = new MineContents(mine.contents);

        config.set("contents.list", contents.getContentsAsStrings());
        config.set("contents.maxWeight", contents.getMaxWeight());

        Bukkit.getScheduler().runTaskAsynchronously(PrisonMines.getInstance(), () -> {
            try {
                config.save(file);
            } catch (IOException e) {
                PrisonMines.getInstance().getLogger().severe("(copy) Failed to push contents update to " + file.getName());
                throw new RuntimeException(e);
            }
        });
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

        Bukkit.getScheduler().runTaskAsynchronously(PrisonMines.getInstance(), () -> {
            try {
                config.save(file);
            } catch (IOException e) {
                PrisonMines.getInstance().getLogger().severe("Failed to push name update to " + file.getName());
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Updates the reset length of the mine and pushes changes to the mine's config file
     * @param seconds The amount of time in seconds. Constrained to 60 <= x <= 86400
     */
    public void setResetLengthSeconds(int seconds) {
        seconds = Numbers.constrain(seconds, 60, 86400);
        resetLengthSeconds = seconds;
        config.set("resetLengthSeconds", seconds);
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

    public boolean failedToLoad() {
        return failedToLoad;
    }

    public int getResetLengthSeconds() {
        return resetLengthSeconds;
    }

    public int getNextResetTick() {
        return nextResetTick;
    }

    public void setNextResetTick(int tick) {
        nextResetTick = tick;
    }

    public void updateNextResetTick() {
        nextResetTick += resetLengthSeconds;
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

    public ItemStack getGuiItem() {
        return guiItem;
    }
}
