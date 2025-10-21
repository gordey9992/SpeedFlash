package com.example.speedflash.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.example.speedflash.SpeedFlash;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Messages {
    private final SpeedFlash plugin;
    private FileConfiguration messages;
    private File messagesFile;

    public Messages(SpeedFlash plugin) {
        this.plugin = plugin;
    }

    public void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.properties");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.properties", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        
        YamlConfiguration defaultMessages = YamlConfiguration.loadConfiguration(
            new InputStreamReader(plugin.getResource("messages.properties"), StandardCharsets.UTF_8));
        messages.setDefaults(defaultMessages);
        messages.options().copyDefaults(true);
        
        try {
            messages.save(messagesFile);
        } catch (Exception e) {
            plugin.getLogger().warning("Не удалось сохранить messages.properties: " + e.getMessage());
        }
    }

    public void reloadMessages() {
        loadMessages();
    }

    public String getNoPermission() {
        return messages.getString("no-permission", "&cУ вас нет разрешения на использование этой команды!");
    }

    public String getUnknownCommand() {
        return messages.getString("unknown-command", "&cНеизвестная команда. Используйте &f/speedflash help &cдля справки.");
    }

    public String getPlayerOnly() {
        return messages.getString("player-only", "&cЭта команда доступна только игрокам!");
    }

    public String getConfigReloaded() {
        return messages.getString("config-reloaded", "&aКонфигурация успешно перезагружена!");
    }

    public String getEffectApplied() {
        return messages.getString("effect-applied", "&aЭффект применен!");
    }

    public String getEffectRemoved() {
        return messages.getString("effect-removed", "&aЭффект удален!");
    }

    public String getSpeedSet() {
        return messages.getString("speed-set", "&aСкорость установлена на: &e{value}");
    }

    public FileConfiguration getMessagesConfig() {
        return messages;
    }
}
