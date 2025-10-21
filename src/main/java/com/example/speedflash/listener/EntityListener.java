package com.example.speedflash.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import com.example.speedflash.SpeedFlash;
import com.example.speedflash.effects.EffectsManager;

public class EntityListener implements Listener {
    private final SpeedFlash plugin;
    private final EffectsManager effectsManager;

    public EntityListener(SpeedFlash plugin) {
        this.plugin = plugin;
        this.effectsManager = plugin.getEffectsManager();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        // Защита от урона при определенных эффектах
        if (effectsManager.hasDamageProtection(player)) {
            event.setCancelled(true);
            effectsManager.sendActionBar(player, "§aЗащита скорости активирована!");
        }
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        // Обработка конфликтов эффектов
        if (effectsManager.hasEffectConflict(player, event.getNewEffect())) {
            event.setCancelled(true);
            player.sendMessage("§cЭтот эффект конфликтует с вашими текущими эффектами скорости!");
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        // Отключение голода при эффекте скорости
        if (effectsManager.hasSpeedEffect(player) && effectsManager.shouldPreventHunger(player)) {
            event.setCancelled(true);
            player.setFoodLevel(20);
        }
    }
}
