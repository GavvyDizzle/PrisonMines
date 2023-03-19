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
import java.util.regex.Pattern;

public class SetMineNameCommand extends SubCommand implements PermissionCommand {

    private final Pattern pattern = Pattern.compile("[\\w-]*");

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
        return "setMineName";
    }

    @Override
    public String getDescription() {
        return "Change a mine's display name";
    }

    @Override
    public String getSyntax() {
        return "/" + adminCommandManager.getCommandDisplayName() + " setMineName <id> <name>";
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

        String name = args[2];
        if (name.trim().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "The name cannot be empty");
            return;
        }
        else if (name.length() > 32) {
            sender.sendMessage(ChatColor.RED + "The name cannot be longer than 32 characters");
            return;
        }
        else if (!pattern.matcher(name).matches()) {
            sender.sendMessage(ChatColor.RED + "The name can only contain letters, numbers, underscores, and dashes (Regex: [a-zA-Z0-9_-]*)");
            return;
        }

        mine.setName(args[2]);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], mineManager.getMineIDs(), list);
        }

        return list;
    }
}