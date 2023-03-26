package com.github.gavvydizzle.prisonmines.commands.admin;

import com.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.mittenmc.serverutils.Numbers;
import com.github.mittenmc.serverutils.PermissionCommand;
import com.github.mittenmc.serverutils.SubCommand;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetSpawnLocationCommand extends SubCommand implements PermissionCommand {

    private final AdminCommandManager adminCommandManager;
    private final MineManager mineManager;

    public SetSpawnLocationCommand(AdminCommandManager adminCommandManager, MineManager mineManager) {
        this.adminCommandManager = adminCommandManager;
        this.mineManager = mineManager;
    }

    @Override
    public String getPermission() {
        return "prisonmines.mineadmin." + getName().toLowerCase();
    }

    @Override
    public String getName() {
        return "setSpawnLocation";
    }

    @Override
    public String getDescription() {
        return "Change a mine's spawn location";
    }

    @Override
    public String getSyntax() {
        return "/" + adminCommandManager.getCommandDisplayName() + " setSpawnLocation <id> [x] [y] [z] [pitch] [yaw]";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        if (args.length != 2 && args.length != 7) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        Mine mine = mineManager.getMine(args[1]);
        if (mine == null) {
            sender.sendMessage(ChatColor.RED + "No mine exists with the id: " + args[1]);
            return;
        }

        if (args.length == 2) {
            if (mine.updateSpawnLocation(((Player) sender).getLocation())) {
                sender.sendMessage(ChatColor.GREEN + "Updates " + mine.getName() + " spawn location to your current location");
            }
            else {
                sender.sendMessage(ChatColor.RED + "The spawn location must be in the same world as the mine");
            }
        }
        else {
            try {
                double x = Double.parseDouble(args[2]);
                double y = Double.parseDouble(args[3]);
                double z = Double.parseDouble(args[4]);
                float yaw = Float.parseFloat(args[5]);
                float pitch = Float.parseFloat(args[6]);
                if (mine.updateSpawnLocation(new Location(((Player) sender).getWorld(), x, y, z, yaw, pitch))) {
                    sender.sendMessage(ChatColor.GREEN + "Set the cells spawn location to the location you provided");
                }
                else {
                    sender.sendMessage(ChatColor.RED + "The spawn location must be in the same world as the mine");
                }
            }
            catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Make sure your location parameters are numbers");
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        switch (args.length) {
            case 2:
                StringUtil.copyPartialMatches(args[1], mineManager.getMineIDs(), list);
                break;
            case 3:
                StringUtil.copyPartialMatches(args[2], Collections.singletonList("" + Numbers.round(player.getLocation().getX(), 1)), list);
                break;
            case 4:
                StringUtil.copyPartialMatches(args[3], Collections.singletonList("" + Numbers.round(player.getLocation().getY(), 4)), list);
                break;
            case 5:
                StringUtil.copyPartialMatches(args[4], Collections.singletonList("" + Numbers.round(player.getLocation().getZ(), 1)), list);
                break;
            case 6:
                StringUtil.copyPartialMatches(args[5], Collections.singletonList("" + Numbers.round(player.getLocation().getYaw(), 0)), list);
                break;
            case 7:
                StringUtil.copyPartialMatches(args[6], Collections.singletonList("" + Numbers.round(player.getLocation().getPitch(), 0)), list);
                break;
        }

        return list;
    }
}