package com.example.speedflash.utils;

import org.bukkit.configuration.file.FileConfiguration;
import com.example.speedflash.SpeedFlash;

public class ConfigManager {
    private final SpeedFlash plugin;
    private FileConfiguration config;

    public ConfigManager(SpeedFlash plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean isEffectEnabled(String effectName) {
        return config.getBoolean("effects." + effectName + ".enabled", true);
    }

    public int getEffectDuration(String effectName) {
        return config.getInt("effects." + effectName + ".duration", 60);
    }

    public double getSpeedMultiplier() {
        return config.getDouble("speed.multiplier", 1.0);
    }

    public boolean isDatabaseEnabled() {
        return config.getBoolean("database.enabled", false);
    }

    public String getDatabaseType() {
        return config.getString("database.type", "SQLite");
    }

    public String getDatabaseHost() {
        return config.getString("database.host", "localhost");
    }

    public int getDatabasePort() {
        return config.getInt("database.port", 3306);
    }

    public String getDatabaseName() {
        return config.getString("database.name", "speedflash");
    }

    public String getDatabaseUsername() {
        return config.getString("database.username", "root");
    }

    public String getDatabasePassword() {
        return config.getString("database.password", "");
    }

    public boolean isTelegramEnabled() {
        return config.getBoolean("telegram.enabled", false);
    }

    public String getTelegramToken() {
        return config.getString("telegram.token", "");
    }

    public String getTelegramChatId() {
        return config.getString("telegram.chat_id", "");
    }

    public String getTelegramUsername() {
        return config.getString("telegram.username", "");
    }

    public boolean isJoinNotificationsEnabled() {
        return config.getBoolean("telegram.join-notifications", true);
    }

    public boolean isBroadcastEnabled() {
        return config.getBoolean("telegram.broadcast-messages", true);
    }
}
