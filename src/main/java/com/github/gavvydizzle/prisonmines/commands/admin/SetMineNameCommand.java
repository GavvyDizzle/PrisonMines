package com.github.gavvydizzle.prisonmines.commands.admin;

import com.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.PermissionCommand;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SetMineNameCommand extends SubCommand implements PermissionCommand {

    private final AdminCommandManager adminCommandManager;
    private final MineManager mineManager;

    public SetMineNameCommand(AdminCommandManager adminCommandManager, MineManager mineManager) {
        this.adminCommandManager = adminCommandManager;
        this.mineManager = mineManager;
    }

    @Override
    public String getPermission() {
        return "prisonmines.mineadmin." + getName().toLowerCase();
    }

    @Override
    public String getName() {
        return "setName";
    }

    @Override
    public String getDescription() {
        return "Change a mine's display name";
    }

    @Override
    public String getSyntax() {
        return "/" + adminCommandManager.getCommandDisplayName() + " setName <id> <name>";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        if (args.length < 3) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        Mine mine = mineManager.getMine(args[1]);
        if (mine == null) {
            sender.sendMessage(ChatColor.RED + "No mine exists with the id: " + args[1]);
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            stringBuilder.append(args[i]).append(" ");
        }
        String name = stringBuilder.toString().trim();
        String coloredName = Colors.conv(name);
        String uncoloredName = Colors.strip(coloredName);

        if (uncoloredName.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "The name cannot be empty");
            return;
        }
        else if (uncoloredName.length() > 16) {
            sender.sendMessage(ChatColor.RED + "The name cannot be longer than 16 characters");
            return;
        }

        mine.setName(name);
        sender.sendMessage(ChatColor.GREEN + "Set mine " + mine.getId() + " name to: " + coloredName);
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