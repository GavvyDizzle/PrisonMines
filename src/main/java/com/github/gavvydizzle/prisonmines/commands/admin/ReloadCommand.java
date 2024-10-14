package com.github.gavvydizzle.prisonmines.commands.admin;

import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import com.github.gavvydizzle.prisonmines.configs.MessagesConfig;
import com.github.gavvydizzle.prisonmines.events.MinesReloadedEvent;
import com.github.gavvydizzle.prisonmines.utils.Messages;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends SubCommand {

    private final List<String> argsList = List.of("messages", "mines");

    public ReloadCommand(AdminCommandManager adminCommandManager) {
        setName("reload");
        setDescription("Reloads this plugin or a specified portion");
        setSyntax("/" + adminCommandManager.getCommandDisplayName() + " reload [arg]");
        setColoredSyntax(ChatColor.YELLOW + getSyntax());
        setPermission(adminCommandManager.getPermissionPrefix() + getName().toLowerCase());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            switch (args[1].toLowerCase()) {
                case "messages":
                    reloadMessages();
                    sender.sendMessage(ChatColor.GREEN + "[" + PrisonMines.getInstance().getName() + "] " + "Successfully reloaded all messages");
                    break;
                case "mines":
                    reloadMines();
                    sender.sendMessage(ChatColor.GREEN + "[" + PrisonMines.getInstance().getName() + "] " + "Successfully reloaded all mines");
                    Bukkit.getPluginManager().callEvent(new MinesReloadedEvent());
                    break;
            }
        }
        else {
            reloadMessages();
            reloadMines();
            sender.sendMessage(ChatColor.GREEN + "[" + PrisonMines.getInstance().getName() + "] " + "Successfully reloaded");
            Bukkit.getPluginManager().callEvent(new MinesReloadedEvent());
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], argsList, list);
        }

        return list;
    }

    private void reloadMessages() {
        MessagesConfig.reload();
        Messages.reloadMessages();
    }

    private void reloadMines() {
        PrisonMines.getInstance().getInventoryManager().closeAllMenus();
        PrisonMines.getInstance().getMineManager().reload(false);
        PrisonMines.getInstance().getInventoryManager().reload();
    }
}