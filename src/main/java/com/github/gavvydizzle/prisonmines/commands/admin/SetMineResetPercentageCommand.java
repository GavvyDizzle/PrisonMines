package com.github.gavvydizzle.prisonmines.commands.admin;

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

public class SetMineResetPercentageCommand extends SubCommand implements WildcardCommand {

    private final MineManager mineManager;

    public SetMineResetPercentageCommand(AdminCommandManager adminCommandManager, MineManager mineManager) {
        this.mineManager = mineManager;

        setName("setResetPercent");
        setDescription("Change a mine's reset percent");
        setSyntax("/" + adminCommandManager.getCommandDisplayName() + " setResetPercent <id> <percent>");
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

        int percent;
        try {
            percent = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount for percent: " + args[2]);
            return;
        }

        percent = Numbers.constrain(percent, 0, 95);
        mine.setResetPercentage(percent);
        mine.pushResetPercentageUpdate();
        sender.sendMessage(ChatColor.GREEN + "Updated " + mine.getName() + ChatColor.GREEN + " reset time to " + percent + "%");
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
            return mineManager.getMineIDs();
        }
        return Collections.emptyList();
    }
}