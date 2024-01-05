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

public class CreateMineCommand extends SubCommand {

    private final MineManager mineManager;

    public CreateMineCommand(AdminCommandManager adminCommandManager, MineManager mineManager) {
        this.mineManager = mineManager;

        setName("create");
        setDescription("Create a new mine (optional: clone block list)");
        setSyntax("/" + adminCommandManager.getCommandDisplayName() + " create <id> [copyID]");
        setColoredSyntax(ChatColor.YELLOW + getSyntax());
        setPermission(adminCommandManager.getPermissionPrefix() + getName().toLowerCase());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        if (args.length < 2) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        Mine copyMine = null;
        if (args.length >= 3) {
            copyMine = mineManager.getMine(args[2]);
            if (copyMine == null) {
                sender.sendMessage(ChatColor.RED + "Unable to clone mine. Invalid id: " + args[2]);
                return;
            }
        }

        // Messages are handled through this method
        Mine mine = mineManager.createNewMine((Player) sender, args[1]);

        // If copying mine contents
        if (args.length >= 3) {
            mine.copyContentsFrom(copyMine);
            sender.sendMessage(ChatColor.GREEN + "Successfully cloned mine contents from " + copyMine.getName());
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 3) {
            StringUtil.copyPartialMatches(args[2], mineManager.getMineIDs(), list);
        }

        return list;
    }
}
