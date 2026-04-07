package me.diamond.abilities;

import me.diamond.SMP;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class AquaMan extends Ability {

    private final NamespacedKey ATTRIBUTE_MODIFIER_KEY = new NamespacedKey("smp", "aquaman");

    public AquaMan(Player player) {
        super(AbilityType.AQUA_MAN, player);

        AttributeInstance attr = player.getAttribute(Attribute.SUBMERGED_MINING_SPEED);

        if (attr != null) {

            // Remove existing modifier first (prevents stacking)
            attr.getModifiers().stream()
                    .filter(mod -> mod.getKey().equals(ATTRIBUTE_MODIFIER_KEY))
                    .forEach(attr::removeModifier);

             attr.addModifier(new AttributeModifier(
                    ATTRIBUTE_MODIFIER_KEY,
                    4.0,
                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
            ));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || AbilityManager.getAbility(player, AbilityType.AQUA_MAN) == null) {
                    cancel();
                    return;
                }
                player.addPotionEffect(PotionEffectType.DOLPHINS_GRACE.createEffect(2 * 20, 0));
                player.addPotionEffect(PotionEffectType.WATER_BREATHING.createEffect(2 * 20, 0));
            }
        }.runTaskTimer(SMP.getPlugin(), 0, 20);

    }

    @Override
    public ItemStack[] getSpecialItems() {
        return new ItemStack[0];
    }

    @Override
    protected void additionalClearUp() {
        player.getAttribute(Attribute.SUBMERGED_MINING_SPEED).removeModifier(ATTRIBUTE_MODIFIER_KEY);
    }
}

