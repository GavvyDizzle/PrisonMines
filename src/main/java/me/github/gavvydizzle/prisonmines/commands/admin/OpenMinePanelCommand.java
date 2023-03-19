package me.github.gavvydizzle.prisonmines.commands.admin;

import com.github.mittenmc.serverutils.PermissionCommand;
import com.github.mittenmc.serverutils.SubCommand;
import me.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import me.github.gavvydizzle.prisonmines.gui.InventoryManager;
import me.github.gavvydizzle.prisonmines.mines.Mine;
import me.github.gavvydizzle.prisonmines.mines.MineManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class OpenMinePanelCommand extends SubCommand implements PermissionCommand {

    private final AdminCommandManager adminCommandManager;
    private final MineManager mineManager;
    private final InventoryManager inventoryManager;

    public OpenMinePanelCommand(AdminCommandManager adminCommandManager, MineManager mineManager, InventoryManager inventoryManager) {
        this.adminCommandManager = adminCommandManager;
        this.mineManager = mineManager;
        this.inventoryManager = inventoryManager;
    }

    @Override
    public String getPermission() {
        return "prisonmines.mineadmin." + getName().toLowerCase();
    }

    @Override
    public String getName() {
        return "panel";
    }

    @Override
    public String getDescription() {
        return "Open a mine menu";
    }

    @Override
    public String getSyntax() {
        return "/" + adminCommandManager.getCommandDisplayName() + " panel <id>";
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

        inventoryManager.openMineGUI((Player) sender, mine);
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