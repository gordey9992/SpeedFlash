package com.example.speedflash.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Utils {
    
    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    public static void sendActionBar(Player player, String message) {
        player.sendActionBar(color(message));
    }
    
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(color(title), color(subtitle), fadeIn, stay, fadeOut);
    }
    
    public static boolean hasPotionEffect(Player player, PotionEffectType type) {
        return player.getActivePotionEffects().stream()
                .anyMatch(effect -> effect.getType().equals(type));
    }
    
    public static void removePotionEffect(Player player, PotionEffectType type) {
        player.removePotionEffect(type);
    }
    
    public static void applyPotionEffect(Player player, PotionEffectType type, int duration, int amplifier) {
        player.addPotionEffect(new PotionEffect(type, duration, amplifier, true, false));
    }
    
    public static double calculateSpeed(double baseSpeed, double multiplier, int level) {
        return baseSpeed * multiplier * (1 + (level * 0.1));
    }
    
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
