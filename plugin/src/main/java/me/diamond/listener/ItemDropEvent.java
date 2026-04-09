package me.diamond.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.diamond.SMP;
import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import me.diamond.abilities.Hacker;
import me.diamond.abilities.Sorcerer;
import me.diamond.utils.CooldownManager;
import me.diamond.utils.Utils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemDropEvent implements Listener {
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Key model = event.getItemDrop().getItemStack().getData(DataComponentTypes.ITEM_MODEL);
        Player player = event.getPlayer();

        if (model.value().equalsIgnoreCase("sorcerer_ability")) {
            event.setCancelled(true);
            if (!CooldownManager.isOnCooldown(player, "shadow")) {
                Sorcerer sorcerer = (Sorcerer) AbilityManager.getAbility(player, AbilityType.SORCERER);
                sorcerer.setShadowed(true);
                player.setInvulnerable(true);
                player.setInvisible(true);
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 10, 1);
                player.addPotionEffect(PotionEffectType.SPEED.createEffect(5 * 20, 3));
                CooldownManager.setCooldown(player, "shadow", 15 * 1000);

                // Spawn particles
                BukkitRunnable particles = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) {
                            cancel();
                            return;
                        }
                        Location location = player.getEyeLocation();
                        location.getWorld().spawnParticle(Particle.ASH, location, 100);
                    }
                };
                particles.runTaskTimer(SMP.getPlugin(), 0, 1);

                // Disable shadow mode after duration
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        sorcerer.setShadowed(false);
                        player.setInvulnerable(false);
                        player.setInvisible(false);
                        particles.cancel(); // stop particle task
                    }
                }.runTaskLater(SMP.getPlugin(), 5 * 20); // 5 seconds
            }
            return;
        } else if (model.value().equalsIgnoreCase("hacker_ability")) {
            event.setCancelled(true);
            Hacker hacker = (Hacker) AbilityManager.getAbility(player, AbilityType.HACKER);
            if (hacker.isBlinking()) {
                hacker.setBlinking(false);
                player.hideBossBar(hacker.getBar());
                CooldownManager.setCooldown(player, "blink", 3 * 60 * 1000);
            } else if (!CooldownManager.isOnCooldown(player, "blink")) {
                hacker.setBlinking(true);
                hacker.setBar(Utils.decreasingBossBar(player, Component.text("Blinking"), BossBar.Color.WHITE, 20, () -> hacker.setBlinking(false)));
            }
            return;
        }

        if (model.namespace().equalsIgnoreCase("smp") && model.value().endsWith("_ability")) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You can't drop this!").color(NamedTextColor.RED));
        }
    }
}
