package com.example.speedflash.integration;

import com.example.speedflash.SpeedFlash;
import com.example.speedflash.utils.ConfigManager;

public class IntegrationManager {
    private final SpeedFlash plugin;
    private final ConfigManager configManager;
    private TelegramBot telegramBot;

    public IntegrationManager(SpeedFlash plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    public void startIntegrations() {
        // Инициализация Telegram бота
        if (configManager.isTelegramEnabled() && 
            !configManager.getTelegramToken().isEmpty() &&
            !configManager.getTelegramChatId().isEmpty()) {
            
            try {
                telegramBot = new TelegramBot(plugin);
                telegramBot.start();
                plugin.getLogger().info("Telegram бот успешно запущен");
            } catch (Exception e) {
                plugin.getLogger().warning("Не удалось запустить Telegram бота: " + e.getMessage());
            }
        }
        
        // Здесь можно добавить другие интеграции (Discord, VK и т.д.)
    }

    public void sendMessageToTelegram(String message) {
        if (telegramBot != null && configManager.isBroadcastEnabled()) {
            telegramBot.sendMessageToTelegram(message);
        }
    }

    public TelegramBot getTelegramBot() {
        return telegramBot;
    }
}
