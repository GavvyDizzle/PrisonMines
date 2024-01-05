package com.github.gavvydizzle.prisonmines.commands.admin;

import com.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CopyContentsCommand extends SubCommand {

    private final MineManager mineManager;

    public CopyContentsCommand(AdminCommandManager adminCommandManager, MineManager mineManager) {
        this.mineManager = mineManager;

        setName("cloneContents");
        setDescription("Clone the block list from one mine to another");
        setSyntax("/" + adminCommandManager.getCommandDisplayName() + " cloneContents <from> <to>");
        setColoredSyntax(ChatColor.YELLOW + getSyntax());
        setPermission(adminCommandManager.getPermissionPrefix() + getName().toLowerCase());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        if (args.length < 3) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        if (args[1].equalsIgnoreCase(args[2])) {
            sender.sendMessage(ChatColor.RED + "To and From args must differ");
            return;
        }

        Mine from = mineManager.getMine(args[1]);
        if (from == null) {
            sender.sendMessage(ChatColor.RED + "No mine exists with the id: " + args[1]);
            return;
        }

        Mine to = mineManager.getMine(args[2]);
        if (to == null) {
            sender.sendMessage(ChatColor.RED + "No mine exists with the id: " + args[2]);
            return;
        }

        to.copyContentsFrom(from);
        sender.sendMessage(ChatColor.GREEN + "Successfully cloned mine contents from " + from.getName() + " to " + to.getName());
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], mineManager.getMineIDs(), list);
        }
        else if (args.length == 3) {
            StringUtil.copyPartialMatches(args[2], mineManager.getMineIDs(), list);
        }

        return list;
    }
}