package com.github.gavvydizzle.prisonmines.commands.admin;

import com.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.mittenmc.serverutils.SubCommand;
import com.github.mittenmc.serverutils.command.WildcardCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SetMaxWeightCommand extends SubCommand implements WildcardCommand {

    private final MineManager mineManager;
    private final ArrayList<String> args2 = new ArrayList<>(Collections.singletonList("scale"));

    public SetMaxWeightCommand(AdminCommandManager adminCommandManager, MineManager mineManager) {
        this.mineManager = mineManager;

        setName("setMaxWeight");
        setDescription("Set a mine's max weight");
        setSyntax("/" + adminCommandManager.getCommandDisplayName() + " setMaxWeight <id> <weight> [scale]");
        setColoredSyntax(ChatColor.YELLOW + getSyntax());
        setPermission(adminCommandManager.getPermissionPrefix() + getName().toLowerCase());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        Mine mine = mineManager.getMine(args[1]);
        if (mine == null) {
            sender.sendMessage(ChatColor.RED + "No mine exists with the id: " + args[1]);
            return;
        }

        int maxWeight;
        try {
            maxWeight = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount for max weight: " + args[2]);
            return;
        }

        boolean scale = args.length >= 4 && args[3].equalsIgnoreCase("scale");
        int oldValue = mine.getContents().getMaxWeight();

        if (scale) {
            if (mine.getContents().setMaxWeight(maxWeight, true)) {
                sender.sendMessage(ChatColor.GREEN + "Updated " + mine.getName() + ChatColor.GREEN + " max weight to " + maxWeight + ChatColor.GRAY + " (was " + oldValue + ")");
                sender.sendMessage(ChatColor.GREEN + "All contents were scaled to have the same distribution");
            }
            else {
                sender.sendMessage(ChatColor.RED + "The contents cannot scale correctly to this new max weight");
            }
        }
        else {
            if (mine.getContents().setMaxWeight(maxWeight, false)) {
                sender.sendMessage(ChatColor.GREEN + "Updated " + mine.getName() + ChatColor.GREEN + " max weight to " + maxWeight + ChatColor.GRAY + " (was " + oldValue + ")");
            }
            else {
                sender.sendMessage(ChatColor.RED + "Unable to update " + mine.getName() + ChatColor.RED + " max weight to " + maxWeight + ". It must be greater than or equal to the total weight which is " + mine.getContents().getTotalWeight());
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], mineManager.getMineIDs(), list);
        }
        else if (args.length == 4) {
            StringUtil.copyPartialMatches(args[3], args2, list);
        }

        return list;
    }

    @Override
    public Collection<String> getWildcardValues(int index, String[] args) {
        if (index == 1) {
            return mineManager.getMineIDs();
        }
        return Collections.emptyList();
    }
}