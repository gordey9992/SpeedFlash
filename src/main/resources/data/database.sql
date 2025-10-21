-- SQL скрипты для базы данных SpeedFlash

-- Таблица игроков
CREATE TABLE IF NOT EXISTS speedflash_players (
    uuid VARCHAR(36) PRIMARY KEY,
    username VARCHAR(16) NOT NULL,
    speed_level INT DEFAULT 1,
    effects TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица эффектов
CREATE TABLE IF NOT EXISTS speedflash_effects (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_uuid VARCHAR(36),
    effect_name VARCHAR(64) NOT NULL,
    duration INT DEFAULT 0,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_uuid) REFERENCES speedflash_players(uuid)
);

-- Индексы для оптимизации
CREATE INDEX IF NOT EXISTS idx_player_uuid ON speedflash_players(uuid);
CREATE INDEX IF NOT EXISTS idx_username ON speedflash_players(username);
CREATE INDEX IF NOT EXISTS idx_effects_player ON speedflash_effects(player_uuid);
