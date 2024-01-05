package com.github.gavvydizzle.prisonmines.commands.admin;

import com.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import com.github.gavvydizzle.prisonmines.gui.InventoryManager;
import com.github.gavvydizzle.prisonmines.mines.Mine;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class OpenMinePanelCommand extends SubCommand {

    private final MineManager mineManager;
    private final InventoryManager inventoryManager;

    public OpenMinePanelCommand(AdminCommandManager adminCommandManager, MineManager mineManager, InventoryManager inventoryManager) {
        this.mineManager = mineManager;
        this.inventoryManager = inventoryManager;

        setName("panel");
        setDescription("Open the mine menu");
        setSyntax("/" + adminCommandManager.getCommandDisplayName() + " panel [id]");
        setColoredSyntax(ChatColor.YELLOW + getSyntax());
        setPermission(adminCommandManager.getPermissionPrefix() + getName().toLowerCase());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        if (args.length == 1) {
            inventoryManager.openMenu((Player) sender, inventoryManager.getMineListGUI());
            return;
        }

        Mine mine = mineManager.getMine(args[1]);
        if (mine == null) {
            sender.sendMessage(ChatColor.RED + "No mine exists with the id: " + args[1]);
            return;
        }

        inventoryManager.openMenu((Player) sender, mine.getMineGUI());
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