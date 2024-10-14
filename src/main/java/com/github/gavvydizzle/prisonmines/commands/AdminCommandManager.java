package com.github.gavvydizzle.prisonmines.commands;

import com.github.gavvydizzle.prisonmines.commands.admin.*;
import com.github.gavvydizzle.prisonmines.gui.InventoryManager;
import com.github.gavvydizzle.prisonmines.mines.MineManager;
import com.github.mittenmc.serverutils.CommandManager;
import com.github.mittenmc.serverutils.command.HelpCommand;
import org.bukkit.command.PluginCommand;

public class AdminCommandManager extends CommandManager {

    private String helpCommandPadding;

    public AdminCommandManager(PluginCommand command, MineManager mineManager, InventoryManager inventoryManager) {
        super(command);

        registerCommand(new HelpCommand.HelpCommandBuilder(this).build());
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
    }
}