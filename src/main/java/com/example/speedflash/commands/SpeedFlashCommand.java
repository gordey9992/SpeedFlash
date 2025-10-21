package com.example.speedflash.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.example.speedflash.SpeedFlash;
import com.example.speedflash.utils.Messages;
import com.example.speedflash.utils.Permissions;
import com.example.speedflash.effects.EffectsManager;
import java.util.*;

public class SpeedFlashCommand implements CommandExecutor, TabCompleter {
    private final SpeedFlash plugin;
    private final Messages messages;
    private final EffectsManager effectsManager;

    public SpeedFlashCommand(SpeedFlash plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
        this.effectsManager = plugin.getEffectsManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                sendHelp(sender);
                break;
                
            case "reload":
                if (!sender.hasPermission(Permissions.RELOAD)) {
                    sender.sendMessage(messages.getNoPermission());
                    return true;
                }
                plugin.getConfigManager().reloadConfig();
                plugin.getMessages().reloadMessages();
                effectsManager.loadEffects();
                sender.sendMessage(messages.getConfigReloaded());
                break;
                
            case "speed":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(messages.getPlayerOnly());
                    return true;
                }
                if (!sender.hasPermission(Permissions.SPEED)) {
                    sender.sendMessage(messages.getNoPermission());
                    return true;
                }
                handleSpeedCommand((Player) sender, args);
                break;
                
            case "effect":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(messages.getPlayerOnly());
                    return true;
                }
                if (!sender.hasPermission(Permissions.EFFECT)) {
                    sender.sendMessage(messages.getNoPermission());
                    return true;
                }
                handleEffectCommand((Player) sender, args);
                break;
                
            case "info":
                sender.sendMessage("§b=== " + plugin.getName() + " Info ===");
                sender.sendMessage("§fВерсия: §a" + plugin.getDescription().getVersion());
                sender.sendMessage("§fАвтор: §a" + plugin.getDescription().getAuthors());
                sender.sendMessage("§fЭффектов загружено: §a" + effectsManager.getLoadedEffectsCount());
                break;
                
            default:
                sender.sendMessage(messages.getUnknownCommand());
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        String[] helpMessages = {
            "§b=== " + plugin.getName() + " - Помощь ===",
            "§f/speedflash help §7- Показать это сообщение",
            "§f/speedflash reload §7- Перезагрузить конфигурацию",
            "§f/speedflash speed <значение> §7- Установить скорость",
            "§f/speedflash effect <эффект> §7- Применить эффект",
            "§f/speedflash info §7- Информация о плагине",
            "§f/speedflashgui §7- Открыть GUI",
            "§f/speedflashperms §7- Управление правами"
        };
        
        sender.sendMessage(helpMessages);
    }

    private void handleSpeedCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /speedflash speed <значение>");
            return;
        }
        
        try {
            float speed = Float.parseFloat(args[1]);
            if (speed < 0.1 || speed > 10.0) {
                player.sendMessage("§cСкорость должна быть между 0.1 и 10.0");
                return;
            }
            
            player.setWalkSpeed(speed / 10.0f);
            player.sendMessage("§aСкорость установлена на: §e" + speed);
            
        } catch (NumberFormatException e) {
            player.sendMessage("§cНеверное значение скорости!");
        }
    }

    private void handleEffectCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /speedflash effect <эффект>");
            player.sendMessage("§cДоступные эффекты: " + String.join(", ", effectsManager.getAvailableEffects()));
            return;
        }
        
        String effectName = args[1];
        if (effectsManager.applyEffect(player, effectName)) {
            player.sendMessage("§aЭффект '§e" + effectName + "§a' применен!");
        } else {
            player.sendMessage("§cЭффект '§e" + effectName + "§c' не найден!");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.addAll(Arrays.asList("help", "reload", "speed", "effect", "info"));
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "effect":
                    completions.addAll(effectsManager.getAvailableEffects());
                    break;
                case "speed":
                    completions.addAll(Arrays.asList("1", "2", "3", "5", "10"));
                    break;
            }
        }
        
        return completions;
    }
}
