package me.diamond.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.diamond.SMP;
import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import me.diamond.abilities.Hacker;
import me.diamond.utils.CooldownManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class PlayerInteractEvent implements Listener {

    private final int MAX_DURATION = 10 * 20;
    private final int STOP_DELAY = 5; // ticks before stopping after no input

    private final Map<Player, Integer> lastPress = new HashMap<>();
    private final Map<Player, Integer> startTick = new HashMap<>();

    @EventHandler
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        //Blink
        event.setCancelled(checkIfBlinking(player));

        if (item != null) {
            Key model = item.getData(DataComponentTypes.ITEM_MODEL);

            if (model != null) {
                if (event.getAction().isRightClick()) {
                    //Equip abilities
                    if (model.namespace().equalsIgnoreCase("smp") && model.value().endsWith("_equip")) {
                        AbilityType ability = AbilityType.valueOf(model.value().split("_")[0].toUpperCase());
                        if (AbilityManager.getAbilities(player).size() < SMP.MAX_ABILITIES) {
                            AbilityManager.grantAbility(player, ability);
                            player.sendMessage(Component.translatable("Successfully equipped the %s ability!", ability.name().toLowerCase()).color(NamedTextColor.GREEN));
                            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        } else {
                            player.sendMessage(Component.text("You already have the maximum amount of abilities!").color(NamedTextColor.RED));
                            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                        }
                        return;
                    }

                    //Fire Breath
                    if (model.value().equalsIgnoreCase("inferno_ability")) {

                        event.setCancelled(true);

                        int tick = Bukkit.getCurrentTick();

                        // Update last press time
                        lastPress.put(player, tick);

                        // Already running → don't restart
                        if (startTick.containsKey(player)) return;

                        if (CooldownManager.isOnCooldown(player, "fire_breath")) return;

                        startTick.put(player, tick);

                        startFireBreath(player);

                        CooldownManager.setCooldown(player, "fire_breath", 30 * 1000);
                        return;
                    }
                }
            }
        }
    }

    public void onInteract(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (checkIfBlinking(player)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    private boolean checkIfBlinking(Player player) {
        Hacker hacker = (Hacker) AbilityManager.getAbility(player, AbilityType.HACKER);
        boolean isBlinking = (hacker != null && hacker.isBlinking());
        if (isBlinking) {
            player.sendMessage(Component.text("You can't do this while blinking").color(NamedTextColor.RED));
        }
        return isBlinking;
    }

    private void startFireBreath(Player player) {
        Bukkit.getScheduler().runTaskTimer(SMP.getPlugin(), task -> {

            if (!player.isOnline() || player.isDead()) {
                stop(player, task);
                return;
            }

            int currentTick = Bukkit.getCurrentTick();

            // Stop if player stopped pressing F
            if (!lastPress.containsKey(player) ||
                    currentTick - lastPress.get(player) > STOP_DELAY) {
                stop(player, task);
                return;
            }

            // Stop if max duration reached
            if (currentTick - startTick.get(player) > MAX_DURATION) {
                stop(player, task);
                return;
            }

            if (currentTick % 5 == 0) {
                player.getWorld().playSound(
                        player.getLocation(),
                        Sound.BLOCK_FIRE_AMBIENT,
                        0.6f,
                        1.2f
                );
            }

            fireBreath(player);
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1f, 1f);

        }, 0L, 1L);
    }

    private void stop(Player player, org.bukkit.scheduler.BukkitTask task) {
        lastPress.remove(player);
        startTick.remove(player);
        task.cancel();
    }

    private void fireBreath(Player player) {
        Location bodyCenter = player.getLocation().clone().add(0, 0.9, 0); // 0.9 is roughly chest height

        // Get the direction the player is looking
        Vector direction = player.getEyeLocation().getDirection().normalize();

        double maxDistance = 50;
        double step = 0.3;

        for (double i = 0; i < maxDistance; i += step) {
            // Move along the eye direction from body center
            Location point = bodyCenter.clone().add(direction.clone().multiply(i));

            player.getWorld().spawnParticle(Particle.FLAME, point, 2, 0.05, 0.05, 0.05, 0.01);
            player.getWorld().spawnParticle(Particle.SMOKE, point, 1, 0.02, 0.02, 0.02, 0.01);

            if (point.getBlock().getType().isSolid()) {
                Block above = point.getBlock().getRelative(0, 1, 0);
                if (above.getType().isAir()) {
                    above.setType(Material.FIRE);
                }
                break;
            }

            for (Entity entity : point.getWorld().getNearbyEntities(point, 0.3, 0.3, 0.3)) {
                if (entity == player) continue;
                if (!(entity instanceof LivingEntity target)) continue;

                target.setFireTicks(200);
                target.damage(3, player);

                Vector kb = direction.clone().multiply(0.01);
                target.setVelocity(target.getVelocity().add(kb));

                target.getWorld().playSound(
                        target.getLocation(),
                        Sound.ENTITY_BLAZE_HURT,
                        0.8f,
                        1.2f
                );
                return;
            }
        }
    }
}
