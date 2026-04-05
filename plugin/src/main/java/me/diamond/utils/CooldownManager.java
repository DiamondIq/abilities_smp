package me.diamond.utils;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    // Stores: player UUID -> (cooldown key -> expiry time in millis)
    private static final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    /** Set a cooldown for a player and key (ability/item) */
    public static void setCooldown(Player player, String key, long durationMillis) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(key, System.currentTimeMillis() + durationMillis);
    }

    /** Check if a player has an active cooldown */
    public static boolean isOnCooldown(Player player, String key) {
        if (!cooldowns.containsKey(player.getUniqueId())) return false;

        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        Long expiry = playerCooldowns.get(key);
        if (expiry == null) return false;

        if (System.currentTimeMillis() >= expiry) {
            playerCooldowns.remove(key); // expired → remove
            return false;
        }

        player.sendMessage(Component.text("\uE000").font(Key.key("smp", "custom")).append(Component.text(" This ability is on cooldown for " + (expiry - System.currentTimeMillis())/1000 + "s").font(Key.key("minecraft", "default")).color(NamedTextColor.RED)));
        return true;
    }

    /** Get remaining cooldown in milliseconds */
    public static long getRemaining(Player player, String key) {
        if (!cooldowns.containsKey(player.getUniqueId())) return 0;

        Long expiry = cooldowns.get(player.getUniqueId()).get(key);
        if (expiry == null) return 0;

        long remaining = expiry - System.currentTimeMillis();
        if (remaining <= 0) {
            cooldowns.get(player.getUniqueId()).remove(key);
            return 0;
        }
        return remaining;
    }

    /** Remove a cooldown manually */
    public static void removeCooldown(Player player, String key) {
        if (cooldowns.containsKey(player.getUniqueId())) {
            cooldowns.get(player.getUniqueId()).remove(key);
        }
    }

    /** Optional: clear all cooldowns for a player */
    public static void clearAll(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}