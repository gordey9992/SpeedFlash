package com.example.speedflash.effects;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.configuration.file.FileConfiguration;
import com.example.speedflash.SpeedFlash;
import com.example.speedflash.utils.ConfigManager;
import com.example.speedflash.utils.Utils;
import java.util.*;

public class EffectsManager {
    private final SpeedFlash plugin;
    private final ConfigManager configManager;
    private final Map<String, CustomEffect> effects;
    private final Map<UUID, Set<String>> playerEffects;

    public EffectsManager(SpeedFlash plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.effects = new HashMap<>();
        this.playerEffects = new HashMap<>();
    }

    public void loadEffects() {
        effects.clear();
        
        FileConfiguration config = configManager.getConfig();
        if (config.contains("effects")) {
            for (String effectKey : config.getConfigurationSection("effects").getKeys(false)) {
                String path = "effects." + effectKey;
                boolean enabled = config.getBoolean(path + ".enabled", true);
                int duration = config.getInt(path + ".duration", 60);
                int amplifier = config.getInt(path + ".amplifier", 1);
                
                if (enabled) {
                    effects.put(effectKey, new CustomEffect(effectKey, duration, amplifier));
                }
            }
        }
        
        plugin.getLogger().info("Загружено эффектов: " + effects.size());
    }

    public boolean applyEffect(Player player, String effectName) {
        CustomEffect effect = effects.get(effectName);
        if (effect == null) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        playerEffects.computeIfAbsent(playerId, k -> new HashSet<>()).add(effectName);
        
        effect.apply(player);
        return true;
    }

    public boolean removeEffect(Player player, String effectName) {
        UUID playerId = player.getUniqueId();
        Set<String> effects = playerEffects.get(playerId);
        if (effects != null && effects.remove(effectName)) {
            CustomEffect effect = this.effects.get(effectName);
            if (effect != null) {
                effect.remove(player);
            }
            return true;
        }
        return false;
    }

    public void clearEffects(Player player) {
        UUID playerId = player.getUniqueId();
        Set<String> effects = playerEffects.remove(playerId);
        if (effects != null) {
            for (String effectName : effects) {
                CustomEffect effect = this.effects.get(effectName);
                if (effect != null) {
                    effect.remove(player);
                }
            }
        }
    }

    public void handleMovementEffects(Player player, PlayerMoveEvent event) {
        Set<String> activeEffects = playerEffects.get(player.getUniqueId());
        if (activeEffects != null) {
            for (String effectName : activeEffects) {
                CustomEffect effect = effects.get(effectName);
                if (effect != null) {
                    effect.onMove(player, event);
                }
            }
        }
    }

    public void handleSprintEffects(Player player, boolean isSprinting) {
        if (isSprinting) {
            Set<String> activeEffects = playerEffects.get(player.getUniqueId());
            if (activeEffects != null && activeEffects.contains("speed_boost")) {
                applyEffect(player, "sprint_boost");
            }
        }
    }

    public boolean hasChatEffect(Player player) {
        Set<String> activeEffects = playerEffects.get(player.getUniqueId());
        return activeEffects != null && activeEffects.contains("chat_effect");
    }

    public String applyChatEffect(Player player, String message) {
        return "⚡ " + message + " ⚡";
    }

    public boolean hasDamageProtection(Player player) {
        Set<String> activeEffects = playerEffects.get(player.getUniqueId());
        return activeEffects != null && activeEffects.contains("damage_protection");
    }

    public boolean hasEffectConflict(Player player, org.bukkit.potion.PotionEffect newEffect) {
        return false;
    }

    public boolean hasSpeedEffect(Player player) {
        Set<String> activeEffects = playerEffects.get(player.getUniqueId());
        return activeEffects != null && activeEffects.contains("speed");
    }

    public boolean shouldPreventHunger(Player player) {
        Set<String> activeEffects = playerEffects.get(player.getUniqueId());
        return activeEffects != null && activeEffects.contains("no_hunger");
    }

    public void applySavedEffects(Player player) {
        // Заглушка для будущей реализации
    }

    public void savePlayerEffects(Player player) {
        // Заглушка для будущей реализации
    }

    public void clearTemporaryEffects(Player player) {
        Set<String> activeEffects = playerEffects.get(player.getUniqueId());
        if (activeEffects != null) {
            activeEffects.removeIf(effect -> effects.get(effect).isTemporary());
        }
    }

    public void sendActionBar(Player player, String message) {
        Utils.sendActionBar(player, message);
    }

    public Set<String> getAvailableEffects() {
        return effects.keySet();
    }

    public int getLoadedEffectsCount() {
        return effects.size();
    }
}
