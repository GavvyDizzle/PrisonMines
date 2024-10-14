package com.github.gavvydizzle.prisonmines.commands.admin;

import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.mittenmc.serverutils.Numbers;
import com.github.mittenmc.serverutils.SubCommand;
import com.github.mittenmc.serverutils.command.WildcardCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ResetMineCommand extends SubCommand implements WildcardCommand {

    private final MineManager mineManager;

    public ResetMineCommand(AdminCommandManager adminCommandManager, MineManager mineManager) {
        this.mineManager = mineManager;

        setName("reset");
        setDescription("Reset one or all mines");
        setSyntax("/" + adminCommandManager.getCommandDisplayName() + " reset <id|all> [multiplier]");
        setColoredSyntax(ChatColor.YELLOW + getSyntax());
        setPermission(adminCommandManager.getPermissionPrefix() + getName().toLowerCase());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        if (args[1].equalsIgnoreCase("all")) {
            if (args.length >= 3) {
                double multiplier;
                try {
                    multiplier = Double.parseDouble(args[2]);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Invalid multiplier: " + args[2]);
                    return;
                }
                if (!Numbers.isWithinRange(multiplier, 0, 1)) {
                    sender.sendMessage(ChatColor.RED + "Invalid multiplier: " + args[2] + ". It must be between 0 and 1");
                    return;
                }

                mineManager.resetAllMines(multiplier, false);
                if (PrisonMines.getInstance().getFoliaLib().isFolia()) {
                    sender.sendMessage(ChatColor.GREEN + "Resetting all mines and randomized their timers");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Resetting all mines and randomized their timers. Each reset is delayed by " + MineManager.RESET_ALL_TICK_INTERVAL + " ticks");
                }
            }
            else {
                mineManager.resetAllMines(false);if (PrisonMines.getInstance().getFoliaLib().isFolia()) {
                    sender.sendMessage(ChatColor.GREEN + "Resetting all mines");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Resetting all mines. Each reset is delayed by " + MineManager.RESET_ALL_TICK_INTERVAL + " ticks");
                }
            }
            return;
        }

        Mine mine = mineManager.getMine(args[1]);
        if (mine == null) {
            sender.sendMessage(ChatColor.RED + "No mine exists with the id: " + args[1]);
            return;
        }

        mine.resetMine(true, false);
        sender.sendMessage(ChatColor.GREEN + "Resetting " + mine.getName());
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], mineManager.getMineIDs(), list);
        }

        return list;
    }

    @Override
    public Collection<String> getWildcardValues(int index, String[] args) {
        if (index == 1) {
            return List.of("all"); // This command already supports this with the "all" parameter
        }
        return Collections.emptyList();
    }
}