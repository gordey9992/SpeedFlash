package com.example.speedflash.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import com.example.speedflash.SpeedFlash;
import com.example.speedflash.effects.EffectsManager;

public class ChatListener implements Listener {
    private final SpeedFlash plugin;
    private final EffectsManager effectsManager;

    public ChatListener(SpeedFlash plugin) {
        this.plugin = plugin;
        this.effectsManager = plugin.getEffectsManager();
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        // Применение эффектов к сообщениям в чате
        if (effectsManager.hasChatEffect(player)) {
            String formattedMessage = effectsManager.applyChatEffect(player, message);
            event.setMessage(formattedMessage);
        }
        
        // Обработка команд через чат
        if (message.startsWith("#speed ")) {
            if (player.hasPermission("speedflash.speed.chat")) {
                event.setCancelled(true);
                handleChatSpeedCommand(player, message.substring(7));
            }
        }
    }

    private void handleChatSpeedCommand(Player player, String speedValue) {
        try {
            float speed = Float.parseFloat(speedValue);
            if (speed >= 0.1f && speed <= 10.0f) {
                player.setWalkSpeed(speed / 10.0f);
                player.sendMessage("§aСкорость установлена на: §e" + speed);
            } else {
                player.sendMessage("§cСкорость должна быть между 0.1 и 10.0");
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cНеверное значение скорости!");
        }
    }
}
