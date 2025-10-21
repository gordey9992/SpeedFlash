package com.example.speedflash.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.example.speedflash.SpeedFlash;
import com.example.speedflash.utils.Messages;
import com.example.speedflash.utils.Permissions;

public class SpeedFlashPermissionsCommand implements CommandExecutor {
    private final SpeedFlash plugin;
    private final Messages messages;

    public SpeedFlashPermissionsCommand(SpeedFlash plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permissions.ADMIN)) {
            sender.sendMessage(messages.getNoPermission());
            return true;
        }
        
        if (args.length == 0) {
            showPermissionsInfo(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "list":
                listPermissions(sender);
                break;
            case "check":
                if (args.length < 2) {
                    sender.sendMessage("§cИспользование: /speedflashperms check <игрок>");
                    return true;
                }
                checkPlayerPermissions(sender, args[1]);
                break;
            default:
                sender.sendMessage("§cНеизвестная подкоманда. Используйте: list, check");
                break;
        }
        
        return true;
    }

    private void showPermissionsInfo(CommandSender sender) {
        sender.sendMessage("§b=== SpeedFlash Permissions ===");
        sender.sendMessage("§f/speedflashperms list §7- Список всех прав");
        sender.sendMessage("§f/speedflashperms check <игрок> §7- Проверить права игрока");
    }

    private void listPermissions(CommandSender sender) {
        sender.sendMessage("§b=== Права SpeedFlash ===");
        sender.sendMessage("§aspeedflash.use §7- Основные команды");
        sender.sendMessage("§aspeedflash.reload §7- Перезагрузка конфигурации");
        sender.sendMessage("§aspeedflash.speed §7- Изменение скорости");
        sender.sendMessage("§aspeedflash.effect §7- Применение эффектов");
        sender.sendMessage("§aspeedflash.gui §7- Доступ к GUI");
        sender.sendMessage("§aspeedflash.admin §7- Полный доступ");
    }

    private void checkPlayerPermissions(CommandSender sender, String playerName) {
        Player target = plugin.getServer().getPlayer(playerName);
        if (target == null) {
            sender.sendMessage("§cИгрок не найден или не в сети!");
            return;
        }
        
        sender.sendMessage("§b=== Права игрока " + target.getName() + " ===");
        checkAndDisplayPermission(sender, target, Permissions.USE, "Основные команды");
        checkAndDisplayPermission(sender, target, Permissions.RELOAD, "Перезагрузка");
        checkAndDisplayPermission(sender, target, Permissions.SPEED, "Скорость");
        checkAndDisplayPermission(sender, target, Permissions.EFFECT, "Эффекты");
        checkAndDisplayPermission(sender, target, Permissions.GUI, "GUI");
        checkAndDisplayPermission(sender, target, Permissions.ADMIN, "Админ");
    }

    private void checkAndDisplayPermission(CommandSender sender, Player target, String permission, String description) {
        boolean hasPerm = target.hasPermission(permission);
        String status = hasPerm ? "§a✓" : "§c✗";
        sender.sendMessage(status + " §f" + description + " §7(" + permission + ")");
    }
}
