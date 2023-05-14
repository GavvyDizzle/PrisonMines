package com.github.gavvydizzle.prisonmines.commands.admin;

import com.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.mittenmc.serverutils.PermissionCommand;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ResizeMineCommand extends SubCommand implements PermissionCommand {

    private final AdminCommandManager adminCommandManager;
    private final MineManager mineManager;

    public ResizeMineCommand(AdminCommandManager adminCommandManager, MineManager mineManager) {
        this.adminCommandManager = adminCommandManager;
        this.mineManager = mineManager;
    }

    @Override
    public String getPermission() {
        return "prisonmines.mineadmin." + getName().toLowerCase();
    }

    @Override
    public String getName() {
        return "resize";
    }

    @Override
    public String getDescription() {
        return "Resize a mine";
    }

    @Override
    public String getSyntax() {
        return "/" + adminCommandManager.getCommandDisplayName() + " resize <id>";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        if (args.length < 2) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        Mine mine = mineManager.getMine(args[1]);
        if (mine == null) {
            sender.sendMessage(ChatColor.RED + "No mine exists with the id: " + args[1]);
            return;
        }

        // Messages are handled through this method
        mineManager.resizeMine((Player) sender, mine);
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