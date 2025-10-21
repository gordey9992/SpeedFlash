package com.example.speedflash;

import org.bukkit.plugin.java.JavaPlugin;
import com.example.speedflash.utils.ConfigManager;
import com.example.speedflash.utils.Messages;
import com.example.speedflash.effects.EffectsManager;
import com.example.speedflash.integration.DatabaseManager;
import com.example.speedflash.integration.IntegrationManager;
import com.example.speedflash.commands.SpeedFlashCommand;
import com.example.speedflash.commands.SpeedFlashGUICommand;
import com.example.speedflash.commands.SpeedFlashPermissionsCommand;
import com.example.speedflash.listener.PlayerListener;
import com.example.speedflash.listener.ChatListener;
import com.example.speedflash.listener.EntityListener;

public class SpeedFlash extends JavaPlugin {
    private static SpeedFlash instance;
    private ConfigManager configManager;
    private Messages messages;
    private EffectsManager effectsManager;
    private DatabaseManager databaseManager;
    private IntegrationManager integrationManager;
    private WebServer webServer;
    private long startTime;

    @Override
    public void onEnable() {
        instance = this;
        this.startTime = System.currentTimeMillis();
        
        // Красивое приветствие
        printWelcomeMessage();
        
        // Инициализация менеджеров
        this.configManager = new ConfigManager(this);
        this.messages = new Messages(this);
        this.effectsManager = new EffectsManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.integrationManager = new IntegrationManager(this);
        
        // Загрузка конфигураций
        configManager.loadConfig();
        messages.loadMessages();
        effectsManager.loadEffects();
        
        // Подключение к базе данных
        if (configManager.isDatabaseEnabled()) {
            databaseManager.connect();
        }
        
        // Запуск интеграций
        integrationManager.startIntegrations();
        
        // Инициализация веб-сервера
        if (getConfig().getBoolean("web.enabled", true)) {
            this.webServer = new WebServer(this);
            webServer.start();
        }
        
        // Регистрация команд
        getCommand("speedflash").setExecutor(new SpeedFlashCommand(this));
        getCommand("speedflashgui").setExecutor(new SpeedFlashGUICommand(this));
        getCommand("speedflashperms").setExecutor(new SpeedFlashPermissionsCommand(this));
        
        // Регистрация слушателей
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        
        // Финальное сообщение
        printSuccessMessage();
    }

    @Override
    public void onDisable() {
        printGoodbyeMessage();
        
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        if (webServer != null) {
            webServer.stop();
        }
    }

    private void printWelcomeMessage() {
        getLogger().info("§8╔══════════════════════════════════════════════════════════════╗");
        getLogger().info("§8║                                                              ║");
        getLogger().info("§8║  §b███████╗██████╗ ███████╗███████╗██████╗ §f███████╗██╗      █████╗ ███████╗██╗  ██╗§8║");
        getLogger().info("§8║  §b██╔════╝██╔══██╗██╔════╝██╔════╝██╔══██╗§f██╔════╝██║     ██╔══██╗██╔════╝██║  ██║§8║");
        getLogger().info("§8║  §b███████╗██████╔╝█████╗  █████╗  ██║  ██║§f███████╗██║     ███████║███████╗███████║§8║");
        getLogger().info("§8║  §b╚════██║██╔═══╝ ██╔══╝  ██╔══╝  ██║  ██║§f╚════██║██║     ██╔══██║╚════██║██╔══██║§8║");
        getLogger().info("§8║  §b███████║██║     ███████╗███████╗██████╔╝§f███████║███████╗██║  ██║███████║██║  ██║§8║");
        getLogger().info("§8║  §b╚══════╝╚═╝     ╚══════╝╚══════╝╚═════╝ §f╚══════╝╚══════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝§8║");
        getLogger().info("§8║                                                              ║");
        getLogger().info("§8║  §6⚡ §eСкорость переопределена §6⚡                         §8║");
        getLogger().info("§8║  §7Версия: §f" + getDescription().getVersion() + " §7| §7Автор: §f" + getDescription().getAuthors() + "         §8║");
        getLogger().info("§8║                                                              ║");
        getLogger().info("§8╚══════════════════════════════════════════════════════════════╝");
        
        // Анимированная загрузка
        getLogger().info("§8[§a==========§8] §fИнициализация плагина...");
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        getLogger().info("§8[§a====================§8] §fЗагрузка конфигурации...");
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        getLogger().info("§8[§a============================§8] §fРегистрация команд...");
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        getLogger().info("§8[§a====================================§8] §fЗапуск систем...");
        try { Thread.sleep(200); } catch (InterruptedException e) {}
    }

    private void printSuccessMessage() {
        getLogger().info("§8╔══════════════════════════════════════════════════════════════╗");
        getLogger().info("§8║                                                              ║");
        getLogger().info("§8║  §a✅ §2ПЛАГИН УСПЕШНО АКТИВИРОВАН §a✅                      §8║");
        getLogger().info("§8║                                                              ║");
        getLogger().info("§8║  §b🎯 §fСистема скорости       §a[§2✔§a] §fГотово           §8║");
        getLogger().info("§8║  §b✨ §fЭффекты игроков        §a[§2✔§a] §fГотово           §8║");
        getLogger().info("§8║  §b🌍 §fМультиязычность       §a[§2✔§a] §f3 языка           §8║");
        getLogger().info("§8║  §b🛡️ §fСистема прав          §a[§2✔§a] §fЗагружено        §8║");
        getLogger().info("§8║  §b📊 §fБаза данных           §a[" + (configManager.isDatabaseEnabled() ? "§2✔" : "§7-") + "§a] §f" + 
                         (configManager.isDatabaseEnabled() ? "Подключено" : "Отключено") + "    §8║");
        getLogger().info("§8║  §b🌐 §fВеб-сервер            §a[" + (getConfig().getBoolean("web.enabled", true) ? "§2✔" : "§7-") + "§a] §f" + 
                         (getConfig().getBoolean("web.enabled", true) ? "Запущен" : "Отключен") + "       §8║");
        getLogger().info("§8║                                                              ║");
        getLogger().info("§8║  §6⚡ §eSpeedFlash готов к работе! §6⚡                      §8║");
        getLogger().info("§8║  §7Используйте §f/speedflash help §7для начала              §8║");
        getLogger().info("§8║                                                              ║");
        getLogger().info("§8╚══════════════════════════════════════════════════════════════╝");
        
        // Специальные эффекты для разных режимов
        if (configManager.isDatabaseEnabled() && getConfig().getBoolean("web.enabled", true)) {
            getLogger().info("§6🎮 §eРежим: §bПОЛНЫЙ ФУНКЦИОНАЛ §e- База данных и веб-интерфейс активны!");
        } else if (configManager.isDatabaseEnabled()) {
            getLogger().info("§6🎮 §eРежим: §aСТАНДАРТНЫЙ §e- База данных активна");
        } else {
            getLogger().info("§6🎮 §eРежим: §eБАЗОВЫЙ §e- Основные функции доступны");
        }
    }

    private void printGoodbyeMessage() {
        getLogger().info("§8╔══════════════════════════════════════════════════════════════╗");
        getLogger().info("§8║                                                              ║");
        getLogger().info("§8║  §c🛑 §4ПЛАГИН ОТКЛЮЧАЕТСЯ §c🛑                              §8║");
        getLogger().info("§8║                                                              ║");
        getLogger().info("§8║  §6⚡ §cSpeedFlash §7сохраняет данные...                     §8║");
        getLogger().info("§8║  §7Время работы: §f" + formatUptime() + "                    §8║");
        getLogger().info("§8║  §7До новых встреч! §f🎮                                   §8║");
        getLogger().info("§8║                                                              ║");
        getLogger().info("§8╚══════════════════════════════════════════════════════════════╝");
    }

    private String formatUptime() {
        long uptime = System.currentTimeMillis() - startTime;
        long seconds = uptime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return hours + "ч " + (minutes % 60) + "м " + (seconds % 60) + "с";
        } else if (minutes > 0) {
            return minutes + "м " + (seconds % 60) + "с";
        } else {
            return seconds + "с";
        }
    }

    public static SpeedFlash getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Messages getMessages() {
        return messages;
    }

    public EffectsManager getEffectsManager() {
        return effectsManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public IntegrationManager getIntegrationManager() {
        return integrationManager;
    }

    public WebServer getWebServer() {
        return webServer;
    }

    public long getStartTime() {
        return startTime;
    }
}
