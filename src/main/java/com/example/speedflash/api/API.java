package com.example.speedflash.api;

import com.example.speedflash.SpeedFlash;
import com.example.speedflash.effects.EffectsManager;
import org.bukkit.entity.Player;
import java.util.Set;

public class API {
    private final SpeedFlash plugin;
    private final EffectsManager effectsManager;

    public API(SpeedFlash plugin) {
        this.plugin = plugin;
        this.effectsManager = plugin.getEffectsManager();
    }

    /**
     * Применить эффект к игроку
     */
    public boolean applyEffect(Player player, String effectName) {
        return effectsManager.applyEffect(player, effectName);
    }

    /**
     * Удалить эффект у игрока
     */
    public boolean removeEffect(Player player, String effectName) {
        return effectsManager.removeEffect(player, effectName);
    }

    /**
     * Очистить все эффекты у игрока
     */
    public void clearEffects(Player player) {
        effectsManager.clearEffects(player);
    }

    /**
     * Получить список доступных эффектов
     */
    public Set<String> getAvailableEffects() {
        return effectsManager.getAvailableEffects();
    }

    /**
     * Проверить, имеет ли игрок эффект
     */
    public boolean hasEffect(Player player, String effectName) {
        return effectsManager.getAvailableEffects().contains(effectName);
    }

    /**
     * Установить скорость игрока
     */
    public void setPlayerSpeed(Player player, float speed) {
        player.setWalkSpeed(speed / 10.0f);
    }

    /**
     * Получить текущую скорость игрока
     */
    public float getPlayerSpeed(Player player) {
        return player.getWalkSpeed() * 10.0f;
    }

    /**
     * Сбросить скорость игрока к стандартной
     */
    public void resetPlayerSpeed(Player player) {
        player.setWalkSpeed(0.2f); // Стандартная скорость Minecraft
    }
}
