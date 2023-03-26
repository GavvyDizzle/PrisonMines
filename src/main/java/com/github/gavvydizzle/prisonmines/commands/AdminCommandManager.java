package com.github.gavvydizzle.prisonmines.commands;

import com.github.gavvydizzle.prisonmines.commands.admin.*;
import com.github.gavvydizzle.prisonmines.configs.CommandsConfig;
import com.github.gavvydizzle.prisonmines.gui.InventoryManager;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.PermissionCommand;
import com.github.mittenmc.serverutils.SubCommand;
import com.github.gavvydizzle.prisonmines.PrisonMines;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminCommandManager implements TabExecutor {

    private final PluginCommand command;
    private final ArrayList<SubCommand> subcommands = new ArrayList<>();
    private final ArrayList<String> subcommandStrings = new ArrayList<>();
    private String commandDisplayName, helpCommandPadding;

    public AdminCommandManager(PluginCommand command, MineManager mineManager, InventoryManager inventoryManager) {
        this.command = command;
        command.setExecutor(this);

        subcommands.add(new AdminHelpCommand(this));
        subcommands.add(new CopyContentsCommand(this, mineManager));
        subcommands.add(new CreateMineCommand(this, mineManager));
        subcommands.add(new DeleteMineCommand(this, mineManager));
        subcommands.add(new MineSpawnCommand(this, mineManager));
        subcommands.add(new OpenMinePanelCommand(this, mineManager, inventoryManager));
        subcommands.add(new ReloadCommand(this));
        subcommands.add(new ResetMineCommand(this, mineManager));
        subcommands.add(new ResizeMineCommand(this, mineManager));
        subcommands.add(new SetMaxWeightCommand(this, mineManager));
        subcommands.add(new SetMineNameCommand(this, mineManager));
        subcommands.add(new SetMineResetPercentageCommand(this, mineManager));
        subcommands.add(new SetMineResetSecondsCommand(this, mineManager));
        subcommands.add(new SetSpawnLocationCommand(this, mineManager));
        Collections.sort(subcommands);

        for (SubCommand subCommand : subcommands) {
            subcommandStrings.add(subCommand.getName());
        }

        reload();
    }

    public void reload() {
        FileConfiguration config = CommandsConfig.get();
        config.options().copyDefaults(true);
        config.addDefault("commandDisplayName.admin", command.getName());
        config.addDefault("helpCommandPadding.admin", "&6-----(" + PrisonMines.getInstance().getName() + " Admin Commands)-----");

        for (SubCommand subCommand : subcommands) {
            CommandsConfig.setAdminDescriptionDefault(subCommand);
        }
        CommandsConfig.save();

        commandDisplayName = config.getString("commandDisplayName.admin");
        helpCommandPadding = Colors.conv(config.getString("helpCommandPadding.admin"));
    }

    public String getCommandDisplayName() {
        return commandDisplayName;
    }

    public String getHelpCommandPadding() {
        return helpCommandPadding;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < getSubcommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {

                    SubCommand subCommand = subcommands.get(i);

                    if (subCommand instanceof PermissionCommand &&
                            !sender.hasPermission(((PermissionCommand) subCommand).getPermission())) {
                        sender.sendMessage(ChatColor.RED + "Insufficient permission");
                        return true;
                    }

                    subCommand.perform(sender, args);
                    return true;
                }
            }
            sender.sendMessage(ChatColor.RED + "Invalid command");
        }
        sender.sendMessage(ChatColor.YELLOW + "Use '/" + commandDisplayName + " help' to see a list of valid commands");

        return true;
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            ArrayList<String> subcommandsArguments = new ArrayList<>();

            StringUtil.copyPartialMatches(args[0], subcommandStrings, subcommandsArguments);

            return subcommandsArguments;
        }
        else if (args.length >= 2) {
            for (SubCommand subcommand : subcommands) {
                if (args[0].equalsIgnoreCase(subcommand.getName())) {
                    return subcommand.getSubcommandArguments((Player) sender, args);
                }
            }
        }

        return null;
    }

    public PluginCommand getCommand() {
        return command;
    }
}