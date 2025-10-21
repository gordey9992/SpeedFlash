package com.example.speedflash.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import com.example.speedflash.SpeedFlash;
import com.example.speedflash.utils.Messages;
import com.example.speedflash.utils.Permissions;

public class SpeedFlashGUICommand implements CommandExecutor {
    private final SpeedFlash plugin;
    private final Messages messages;

    public SpeedFlashGUICommand(SpeedFlash plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messages.getPlayerOnly());
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission(Permissions.GUI)) {
            player.sendMessage(messages.getNoPermission());
            return true;
        }
        
        openSpeedGUI(player);
        return true;
    }

    private void openSpeedGUI(Player player) {
        Inventory gui = plugin.getServer().createInventory(null, 27, "§bSpeedFlash - Управление");
        
        // Заглушка для GUI - будет реализовано в будущем
        player.sendMessage("§aGUI SpeedFlash будет реализовано в будущих версиях!");
        player.sendMessage("§7Используйте команды для управления скоростью и эффектами.");
    }
}
