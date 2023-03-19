package me.github.gavvydizzle.prisonmines.commands.admin;

import com.github.mittenmc.serverutils.PermissionCommand;
import com.github.mittenmc.serverutils.SubCommand;
import me.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import me.github.gavvydizzle.prisonmines.mines.Mine;
import me.github.gavvydizzle.prisonmines.mines.MineManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CopyContentsCommand extends SubCommand implements PermissionCommand {

    private final AdminCommandManager adminCommandManager;
    private final MineManager mineManager;

    public CopyContentsCommand(AdminCommandManager adminCommandManager, MineManager mineManager) {
        this.adminCommandManager = adminCommandManager;
        this.mineManager = mineManager;
    }

    @Override
    public String getPermission() {
        return "prisonmines.mineadmin." + getName().toLowerCase();
    }

    @Override
    public String getName() {
        return "cloneContents";
    }

    @Override
    public String getDescription() {
        return "Clone the block list from one mine to another";
    }

    @Override
    public String getSyntax() {
        return "/" + adminCommandManager.getCommandDisplayName() + " cloneContents <from> <to>";
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

        to.copyContents(from);
        sender.sendMessage(ChatColor.GREEN + "Successfully cloned mine contents from " + from.getName() + " to " + to.getName());
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
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