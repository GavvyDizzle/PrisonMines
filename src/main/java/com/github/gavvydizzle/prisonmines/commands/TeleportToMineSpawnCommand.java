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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeleportToMineSpawnCommand implements TabExecutor {

    private final MineManager mineManager;
    private final HashMap<UUID, String> lastVisitedMine;

    public TeleportToMineSpawnCommand(MineManager mineManager) {
        this.mineManager = mineManager;
        lastVisitedMine = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (args.length == 0) {
            if (lastVisitedMine.containsKey(player.getUniqueId())) {
                Mine mine = mineManager.getMine(lastVisitedMine.get(player.getUniqueId()));
                if (mine == null) {
                    sender.sendMessage(ChatColor.YELLOW + "The last mine you visited no longer exists");
                    return true;
                }
                else {
                    if (!mine.hasAccessPermission(player)) {
                        sender.sendMessage(Messages.cannotAccessMine);
                        return true;
                    }

                    if (!mine.teleportToSpawn((Player) sender)) {
                        sender.sendMessage(Messages.noSpawnLocation.replace("{id}", mine.getId()));
                    }
                }
            }
            else {
                sender.sendMessage(ChatColor.YELLOW + "/mine <id>");
            }
            return true;
        }

        Mine mine = mineManager.getMine(args[0]);
        if (mine == null) {
            sender.sendMessage(Messages.invalidMineId.replace("{id}", args[0]));
            return true;
        }

        if (!mine.hasAccessPermission(player)) {
            sender.sendMessage(Messages.cannotAccessMine);
            return true;
        }

        if (!mine.teleportToSpawn((Player) sender)) {
            sender.sendMessage(Messages.noSpawnLocation.replace("{id}", mine.getId()));
        }
        else {
            lastVisitedMine.put(player.getUniqueId(), mine.getId());
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