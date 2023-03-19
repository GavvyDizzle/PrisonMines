package me.github.gavvydizzle.prisonmines.commands.admin;

import com.github.mittenmc.serverutils.PermissionCommand;
import com.github.mittenmc.serverutils.SubCommand;
import me.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import me.github.gavvydizzle.prisonmines.gui.InventoryManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class MineListCommand extends SubCommand implements PermissionCommand {

    private final AdminCommandManager adminCommandManager;
    private final InventoryManager inventoryManager;

    public MineListCommand(AdminCommandManager adminCommandManager, InventoryManager inventoryManager) {
        this.adminCommandManager = adminCommandManager;
        this.inventoryManager = inventoryManager;
    }

    @Override
    public String getPermission() {
        return "prisonmines.mineadmin." + getName().toLowerCase();
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "View all mines";
    }

    @Override
    public String getSyntax() {
        return "/" + adminCommandManager.getCommandDisplayName() + " list";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        inventoryManager.getMineListGUI().openInventory((Player) sender);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return Collections.emptyList();
    }
}