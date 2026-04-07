package me.diamond.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.diamond.SMP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.SequencedMap;
import java.util.concurrent.ThreadLocalRandom;

public class PotionThrowEvent implements Listener {

    private final int BASE_DURATION = 30 * 20;

    private final List<PotionEffectType> EFFECT_TYPES = List.of(
            PotionEffectType.MINING_FATIGUE,
            PotionEffectType.WEAKNESS,
            PotionEffectType.BLINDNESS,
            PotionEffectType.SLOWNESS,
            PotionEffectType.GLOWING,
            PotionEffectType.POISON,
            PotionEffectType.INSTANT_DAMAGE,
            PotionEffectType.WITHER
    );

    @EventHandler
    public void onPotionThrow(PotionSplashEvent event) {
        ItemStack potion = event.getPotion().getItem();

        if (potion.getData(DataComponentTypes.ITEM_MODEL) == null) return;
        if (!potion.getData(DataComponentTypes.ITEM_MODEL).value().equalsIgnoreCase("sorcerer_pot_ability")) return;

        // Pick random effect type
        PotionEffectType type = EFFECT_TYPES.get(ThreadLocalRandom.current().nextInt(EFFECT_TYPES.size()));

        for (LivingEntity entity : event.getAffectedEntities()) {

            int duration = (int) (BASE_DURATION * event.getIntensity(entity));
            if (type == PotionEffectType.INSTANT_DAMAGE) duration = 1;

            int amplifier = switch (type.getKey().getKey()) {
                case "mining_fatigue" -> ThreadLocalRandom.current().nextInt(4);
                case "weakness" -> ThreadLocalRandom.current().nextInt(2);
                case "slowness" -> ThreadLocalRandom.current().nextInt(3);
                default -> 0;
            };

            PotionEffect effect = new PotionEffect(
                    type,
                    duration,
                    amplifier,
                    false,
                    true,
                    true
            );

            entity.addPotionEffect(effect);
        }


        event.setCancelled(true);
    }

    @EventHandler
    public void onPotionThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof ThrownPotion potion)) return;
        if (!(potion.getShooter() instanceof Player player)) return;

        ItemStack item = potion.getItem();

        if (item.getData(DataComponentTypes.ITEM_MODEL) == null) return;
        if (!item.getData(DataComponentTypes.ITEM_MODEL).value().equalsIgnoreCase("sorcerer_pot_ability")) return;

        // Give it back next tick
        Bukkit.getScheduler().runTask(SMP.getPlugin(), () -> {
            player.getInventory().addItem(item);
        });
    }
}