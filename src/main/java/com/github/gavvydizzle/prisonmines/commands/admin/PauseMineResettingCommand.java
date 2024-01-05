package com.github.gavvydizzle.prisonmines.commands.admin;

import com.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PauseMineResettingCommand extends SubCommand {

    private final MineManager mineManager;

    public PauseMineResettingCommand(AdminCommandManager adminCommandManager, MineManager mineManager) {
        this.mineManager = mineManager;

        setName("pauseResetting");
        setDescription("Toggle the mine's ability to reset");
        setSyntax("/" + adminCommandManager.getCommandDisplayName() + " pauseResetting <id>");
        setColoredSyntax(ChatColor.YELLOW + getSyntax());
        setPermission(adminCommandManager.getPermissionPrefix() + getName().toLowerCase());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        Mine mine = mineManager.getMine(args[1]);
        if (mine == null) {
            sender.sendMessage(ChatColor.RED + "No mine exists with the id: " + args[1]);
            return;
        }

        mine.toggleIsResettingPaused();
        if (mine.isResettingPaused()) {
            sender.sendMessage(ChatColor.YELLOW + "This mine can no longer reset. The timer will count down, but it will pause at 0 seconds.");
        }
        else {
            sender.sendMessage(ChatColor.GREEN + "This mine can now reset");
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], mineManager.getMineIDs(), list);
        }

        return list;
    }
}