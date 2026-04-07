package me.diamond.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.diamond.SMP;
import me.diamond.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class ItemOffhandEvent implements Listener {

    private final int MAX_DURATION = 10 * 20;
    private final int STOP_DELAY = 2; // ticks before stopping after no input

    private final Map<Player, Integer> lastPress = new HashMap<>();
    private final Map<Player, Integer> startTick = new HashMap<>();

    @EventHandler
    public void onItemOffHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getOffHandItem();

        if (item == null) return;
        if (item.getData(DataComponentTypes.ITEM_MODEL) == null) return;
        if (!item.getData(DataComponentTypes.ITEM_MODEL).value().equalsIgnoreCase("inferno_ability")) return;

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