package com.example.speedflash.effects;

public enum EffectTypes {
    SPEED_BOOST("speed_boost", "Ускорение", 1),
    JUMP_BOOST("jump_boost", "Прыжок", 1),
    INVISIBILITY("invisibility", "Невидимость", 2),
    NIGHT_VISION("night_vision", "Ночное зрение", 1),
    WATER_BREATHING("water_breathing", "Подводное дыхание", 1),
    FIRE_RESISTANCE("fire_resistance", "Огнестойкость", 2),
    DAMAGE_PROTECTION("damage_protection", "Защита", 3),
    REGENERATION("regeneration", "Регенерация", 2),
    STRENGTH("strength", "Сила", 2),
    HASTE("haste", "Скорость копания", 1);

    private final String id;
    private final String displayName;
    private final int requiredLevel;

    EffectTypes(String id, String displayName, int requiredLevel) {
        this.id = id;
        this.displayName = displayName;
        this.requiredLevel = requiredLevel;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public static EffectTypes getById(String id) {
        for (EffectTypes type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
