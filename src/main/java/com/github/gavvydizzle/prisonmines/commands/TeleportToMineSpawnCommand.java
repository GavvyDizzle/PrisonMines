package com.github.gavvydizzle.prisonmines.commands;


import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.gavvydizzle.prisonmines.utils.Messages;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TeleportToMineSpawnCommand implements TabExecutor {

    private final MineManager mineManager;

    public TeleportToMineSpawnCommand(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "/mine <id>");
        }

        Mine mine = mineManager.getMine(args[0]);
        if (mine == null) {
            sender.sendMessage(Messages.invalidMineId.replace("{id}", args[0]));
            return true;
        }

        Player player = (Player) sender;
        if (!mine.hasAccessPermission(player)) {
            sender.sendMessage(Messages.cannotAccessMine);
        }

        if (!mine.teleportToSpawn((Player) sender)) {
            sender.sendMessage(Messages.noSpawnLocation.replace("{id}", mine.getId()));
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], mineManager.getMineIDs(), list);
        }

        return list;
    }
}