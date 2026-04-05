package me.diamond.listener;

import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import me.diamond.abilities.Inferno;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerHitEvent implements Listener {
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (event.getDamager() instanceof Player player) {
            //Inferno
            Inferno inferno = (Inferno) AbilityManager.getAbility(player, AbilityType.INFERNO);
            if (inferno != null) {
                if (Math.random() < (1.0 / 3.0)) { //1/3 Chance
                    entity.setFireTicks(10 * 20); //10s
                }

                if (inferno.isInfernoActivated() /** && entity instanceof Player **/) {
                    World world = entity.getWorld();
                    int radius = 3;
                    int points = 40;

                    for (int i = 0; i < points; i++) {
                        double angle = 2 * Math.PI * i / points;
                        double x = entity.getX() + radius * Math.cos(angle);
                        double z = entity.getZ() + radius * Math.sin(angle);
                        Location loc = new Location(world, x, entity.getY() + 0.5, z);

                        // Particle effect only (no blocks)
                        world.spawnParticle(Particle.FLAME, loc, 1, 0, 0, 0, 0);
                        world.spawnParticle(Particle.SMOKE, loc, 1, 0, 0, 0, 0);
                    }

                    int cx = entity.getLocation().getBlockX();
                    int cy = entity.getLocation().getBlockY();
                    int cz = entity.getLocation().getBlockZ();

                    for (int x = -radius; x <= radius; x++) {
                        for (int z = -radius; z <= radius; z++) {

                            if (x * x + z * z <= radius * radius) {

                                Block block = world.getBlockAt(cx + x, cy, cz + z);

                                if (block.getType() == Material.AIR) {
                                    block.setType(Material.FIRE);
                                }
                            }
                        }
                    }

                    world.spawnParticle(Particle.LARGE_SMOKE, entity.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.01);
                    world.spawnParticle(Particle.FLAME, entity.getLocation().add(0, 1, 0), 20, 0.5, 1, 0.5, 0.02);

                    world.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
                    world.playSound(player.getLocation(), Sound.ENTITY_GENERIC_BURN, 1f, 0.8f);
                    world.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.8f, 1.2f);

                    entity.setFireTicks(15 * 20); //Set on fire for 15s
                    inferno.setInfernoActivated(false); //Deactivate after hit
                    player.hideBossBar(inferno.getBar());
                }
            }
        }
    }
}
