package com.github.gavvydizzle.prisonmines.utils;

import com.github.mittenmc.serverutils.Colors;
import com.github.gavvydizzle.prisonmines.configs.MessagesConfig;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages {

    public static String teleportedToMineSpawn, mineResetCountdown, mineResetCountdownSingular;
    public static String invalidMineId, cannotAccessMine, noSpawnLocation;

    // Placeholders
    // {mine_id}
    // {mine_name}
    // {time} time in seconds

    public static void reloadMessages() {
        FileConfiguration config = MessagesConfig.get();
        config.options().copyDefaults(true);

        config.addDefault("teleportedToMineSpawn", "&aTeleported to {mine_name} &aspawn");
        config.addDefault("mineResetCountdown", "{mine_name} resetting in {time} seconds");
        config.addDefault("mineResetCountdownSingular", "{mine_name} resetting in 1 second");
        config.addDefault("invalidMineId", "&cNo mine exists with the id: {id}");
        config.addDefault("cannotAccessMine", "&eYou don't have permission to access this mine");
        config.addDefault("noSpawnLocation", "&cNo spawn location found for mine {id}");

        MessagesConfig.save();

        teleportedToMineSpawn = Colors.conv(config.getString("teleportedToMineSpawn"));
        mineResetCountdown = Colors.conv(config.getString("mineResetCountdown"));
        mineResetCountdownSingular = Colors.conv(config.getString("mineResetCountdownSingular"));
        invalidMineId = Colors.conv(config.getString("invalidMineId"));
        cannotAccessMine = Colors.conv(config.getString("cannotAccessMine"));
        noSpawnLocation = Colors.conv(config.getString("noSpawnLocation"));
    }
}
