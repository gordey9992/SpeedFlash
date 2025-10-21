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
        this.startTime = System.currentTimeMillis();
        
        // Инициализация веб-сервера
        if (getConfig().getBoolean("web.enabled", true)) {
            this.webServer = new WebServer(this);
            webServer.start();
        }
    }

    @Override
    public void onDisable() {
        if (webServer != null) {
            webServer.stop();
        }
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public void onEnable() {
        instance = this;
        
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
        
        // Регистрация команд
        getCommand("speedflash").setExecutor(new SpeedFlashCommand(this));
        getCommand("speedflashgui").setExecutor(new SpeedFlashGUICommand(this));
        getCommand("speedflashperms").setExecutor(new SpeedFlashPermissionsCommand(this));
        
        // Регистрация слушателей
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        
        getLogger().info("§b" + getName() + " §aуспешно запущен!");
        getLogger().info("§aВерсия: " + getDescription().getVersion());
        getLogger().info("§aАвтор: " + getDescription().getAuthors());
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        getLogger().info("§b" + getName() + " §cотключен");
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

case "lang":
    if (!sender.hasPermission(Permissions.ADMIN)) {
        sender.sendMessage(messages.getNoPermission());
        return true;
    }
    handleLanguageCommand(sender, args);
    break;

private void handleLanguageCommand(CommandSender sender, String[] args) {
    if (args.length < 2) {
        sender.sendMessage("§6🌍 Доступные языки:");
        sender.sendMessage("§e/speedflash lang it_IT §7- Italiano 🇮🇹");
        sender.sendMessage("§e/speedflash lang ru_RU §7- Русский 🇷🇺");
        sender.sendMessage("§e/speedflash lang en_US §7- English 🇺🇸");
        return;
    }
    
    String lang = args[1];
    if (plugin.getLocalization().setLanguage(lang)) {
        sender.sendMessage("§a🌍 Язык изменен на: " + lang);
    } else {
        sender.sendMessage("§c❌ Язык не поддерживается: " + lang);
    }
}
