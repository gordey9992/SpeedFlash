package com.example.speedflash.integration;

import com.example.speedflash.SpeedFlash;
import com.example.speedflash.utils.ConfigManager;
import java.sql.*;

public class DatabaseManager {
    private final SpeedFlash plugin;
    private final ConfigManager configManager;
    private Connection connection;

    public DatabaseManager(SpeedFlash plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    public void connect() {
        try {
            String databaseType = configManager.getDatabaseType();
            
            if ("MySQL".equalsIgnoreCase(databaseType)) {
                String url = "jdbc:mysql://" + configManager.getDatabaseHost() + ":" + 
                           configManager.getDatabasePort() + "/" + configManager.getDatabaseName();
                connection = DriverManager.getConnection(url, 
                    configManager.getDatabaseUsername(), 
                    configManager.getDatabasePassword());
            } else {
                // SQLite по умолчанию
                String url = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/speedflash.db";
                connection = DriverManager.getConnection(url);
            }
            
            plugin.getLogger().info("Успешное подключение к базе данных: " + databaseType);
            createTables();
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка подключения к базе данных: " + e.getMessage());
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                plugin.getLogger().info("Отключение от базы данных");
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка при отключении от базы данных: " + e.getMessage());
            }
        }
    }

    private void createTables() {
        String createPlayersTable = "CREATE TABLE IF NOT EXISTS speedflash_players (" +
            "uuid VARCHAR(36) PRIMARY KEY," +
            "username VARCHAR(16) NOT NULL," +
            "speed_level INT DEFAULT 1," +
            "effects TEXT," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ");";
        
        String createEffectsTable = "CREATE TABLE IF NOT EXISTS speedflash_effects (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "player_uuid VARCHAR(36)," +
            "effect_name VARCHAR(64) NOT NULL," +
            "duration INT DEFAULT 0," +
            "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (player_uuid) REFERENCES speedflash_players(uuid)" +
            ");";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayersTable);
            stmt.execute(createEffectsTable);
            plugin.getLogger().info("Таблицы базы данных созданы/проверены");
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка создания таблиц: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isConnected() {
        return connection != null;
    }
}
