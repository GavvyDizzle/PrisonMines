package com.github.gavvydizzle.prisonmines.commands;

import com.github.gavvydizzle.prisonmines.PrisonMines;
import com.github.gavvydizzle.prisonmines.commands.admin.*;
import com.github.gavvydizzle.prisonmines.configs.CommandsConfig;
import com.github.gavvydizzle.prisonmines.gui.InventoryManager;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.CommandManager;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;

public class AdminCommandManager extends CommandManager {

    private String helpCommandPadding;

    public AdminCommandManager(PluginCommand command, MineManager mineManager, InventoryManager inventoryManager) {
        super(command);

        registerCommand(new AdminHelpCommand(this));
        registerCommand(new ClearMineCommand(this, mineManager));
        registerCommand(new CopyContentsCommand(this, mineManager));
        registerCommand(new CreateMineCommand(this, mineManager));
        registerCommand(new DeleteMineCommand(this, mineManager));
        registerCommand(new MineSpawnCommand(this, mineManager));
        registerCommand(new OpenMinePanelCommand(this, mineManager, inventoryManager));
        registerCommand(new PauseMineResettingCommand(this, mineManager));
        registerCommand(new ReloadCommand(this));
        registerCommand(new ResetMineCommand(this, mineManager));
        registerCommand(new ResizeMineCommand(this, mineManager));
        registerCommand(new SetMaxWeightCommand(this, mineManager));
        registerCommand(new SetMineNameCommand(this, mineManager));
        registerCommand(new SetMineResetPercentageCommand(this, mineManager));
        registerCommand(new SetMineResetSecondsCommand(this, mineManager));
        registerCommand(new SetSpawnLocationCommand(this, mineManager));
        registerCommand(new TeleportCenterCommand(this, mineManager));
        sortCommands();

        reload();
    }

    public void reload() {
        FileConfiguration config = CommandsConfig.get();
        config.options().copyDefaults(true);
        config.addDefault("commandDisplayName.admin", getCommandDisplayName());
        config.addDefault("helpCommandPadding.admin", "&6-----(" + PrisonMines.getInstance().getName() + " Admin Commands)-----");

        for (SubCommand subCommand : getSubcommands()) {
            CommandsConfig.setAdminDescriptionDefault(subCommand);
        }
        CommandsConfig.save();

        setCommandDisplayName(config.getString("commandDisplayName.admin"));
        helpCommandPadding = Colors.conv(config.getString("helpCommandPadding.admin"));
    }

    public String getHelpCommandPadding() {
        return helpCommandPadding;
    }
}