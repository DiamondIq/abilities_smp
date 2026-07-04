package me.diamond.listener;

import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import me.diamond.abilities.Hacker;
import me.diamond.utils.CooldownManager;
import me.diamond.utils.Utils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerShiftEvent implements Listener {

    private static final long TIME_WINDOW_MS = 400;
    private final Map<UUID, SneakHistory> sneakRegistry = new HashMap<>();

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        // Only count when they push the key down
        if (!event.isSneaking()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        SneakHistory history = sneakRegistry.computeIfAbsent(uuid, _ -> new SneakHistory());

        long lastTap = history.lastTapTime;
        long secondToLastTap = history.secondToLastTapTime;

        history.secondToLastTapTime = lastTap;
        history.lastTapTime = now;

        // Check if a true triple-tap happened
        if (lastTap > 0 && secondToLastTap > 0) {
            if ((now - lastTap <= TIME_WINDOW_MS) && (lastTap - secondToLastTap <= TIME_WINDOW_MS)) {

                // Process the activation based on the item they are holding
                handleAbilityActivation(player);

                history.clear(); // Clear history so it resets cleanly
            }
        }
    }

    private void handleAbilityActivation(Player player) {
        //Dolphins grace
        if (AbilityManager.getAbility(player, AbilityType.AQUAMAN) != null && player.isUnderWater()) {
            if (!CooldownManager.isOnCooldown(player, "dolphins_grace")) {
                player.addPotionEffect(PotionEffectType.DOLPHINS_GRACE.createEffect(10 * 20, 0)); //Activate for 10s
                CooldownManager.setCooldown(player, "dolphins_grace", 2 * 60 * 1000); // 2 Mins cooldown
            }
        } else if (AbilityManager.getAbility(player, AbilityType.HACKER) != null) {
            Hacker hacker = (Hacker) AbilityManager.getAbility(player, AbilityType.HACKER);
            if (hacker == null) return;

            if (hacker.isBlinking()) {
                hacker.setBlinking(false);
                player.hideBossBar(hacker.getBar());
                CooldownManager.setCooldown(player, "blink", 3 * 60 * 1000);
            } else if (!CooldownManager.isOnCooldown(player, "blink")) {
                hacker.setBlinking(true);
                hacker.setBar(Utils.decreasingBossBar(player, Component.text("Blinking"), BossBar.Color.WHITE, 20, () -> hacker.setBlinking(false)));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        sneakRegistry.remove(event.getPlayer().getUniqueId());
    }

    private static class SneakHistory {
        long lastTapTime = 0;
        long secondToLastTapTime = 0;

        void clear() {
            this.lastTapTime = 0;
            this.secondToLastTapTime = 0;
        }
    }
}