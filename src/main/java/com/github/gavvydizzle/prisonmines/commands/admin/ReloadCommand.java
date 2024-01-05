package com.github.gavvydizzle.prisonmines.commands.admin;

import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.commands.AdminCommandManager;
import com.github.gavvydizzle.prisonmines.configs.CommandsConfig;
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

    private final AdminCommandManager adminCommandManager;
    private final ArrayList<String> argsList;

    public ReloadCommand(AdminCommandManager adminCommandManager) {
        this.adminCommandManager = adminCommandManager;
        argsList = new ArrayList<>();
        argsList.add("commands");
        argsList.add("messages");
        argsList.add("mines");

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
                case "commands":
                    reloadCommands();
                    sender.sendMessage(ChatColor.GREEN + "[" + PrisonMines.getInstance().getName() + "] " + "Successfully reloaded commands");
                    break;
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
            reloadCommands();
            reloadMessages();
            reloadMines();
            sender.sendMessage(ChatColor.GREEN + "[" + PrisonMines.getInstance().getName() + "] " + "Successfully reloaded");
            Bukkit.getPluginManager().callEvent(new MinesReloadedEvent());
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], argsList, list);
        }

        return list;
    }

    private void reloadCommands() {
        CommandsConfig.reload();
        adminCommandManager.reload();
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