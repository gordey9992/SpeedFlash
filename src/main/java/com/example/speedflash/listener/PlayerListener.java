package com.example.speedflash.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import com.example.speedflash.SpeedFlash;
import com.example.speedflash.effects.EffectsManager;
import com.example.speedflash.utils.ConfigManager;

public class PlayerListener implements Listener {
    private final SpeedFlash plugin;
    private final EffectsManager effectsManager;
    private final ConfigManager configManager;

    public PlayerListener(SpeedFlash plugin) {
        this.plugin = plugin;
        this.effectsManager = plugin.getEffectsManager();
        this.configManager = plugin.getConfigManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Применение сохраненных эффектов при входе
        effectsManager.applySavedEffects(player);
        
        // Уведомление в Telegram
        if (configManager.isTelegramEnabled() && configManager.isJoinNotificationsEnabled()) {
            String message = "🟢 Игрок " + player.getName() + " присоединился к серверу!";
            plugin.getIntegrationManager().sendMessageToTelegram(message);
        }
        
        // Сообщение о плагине для админов
        if (player.hasPermission("speedflash.admin")) {
            player.sendMessage("§bSpeedFlash §fактивирован. Версия: §a" + plugin.getDescription().getVersion());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Сохранение эффектов при выходе
        effectsManager.savePlayerEffects(player);
        
        // Очистка временных эффектов
        effectsManager.clearTemporaryEffects(player);
        
        // Уведомление в Telegram
        if (configManager.isTelegramEnabled() && configManager.isJoinNotificationsEnabled()) {
            String message = "🔴 Игрок " + player.getName() + " покинул сервер!";
            plugin.getIntegrationManager().sendMessageToTelegram(message);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Применение эффектов при движении
        effectsManager.handleMovementEffects(player, event);
    }

    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        
        // Обработка эффектов спринта
        effectsManager.handleSprintEffects(player, event.isSprinting());
    }
}
