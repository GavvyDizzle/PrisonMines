package com.github.gavvydizzle.prisonmines.commands.admin;

import com.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.gavvydizzle.prisonmines.utils.Messages;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TeleportCenterCommand extends SubCommand {

    private final MineManager mineManager;

    public TeleportCenterCommand(AdminCommandManager adminCommandManager, MineManager mineManager) {
        this.mineManager = mineManager;

        setName("tpCenter");
        setDescription("Teleport to a mine's center point");
        setSyntax("/" + adminCommandManager.getCommandDisplayName() + " tpCenter <id>");
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

        Mine mine = mineManager.getMine(args[1]);
        if (mine == null) {
            sender.sendMessage(Messages.invalidMineId.replace("{id}", args[1]));
            return;
        }

        if (!mine.teleportToCenter((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "Unable to create center point for " + mine.getId());
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