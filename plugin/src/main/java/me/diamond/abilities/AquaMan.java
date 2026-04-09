package me.diamond.abilities;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class AquaMan extends Ability {

    private final NamespacedKey ATTRIBUTE_MODIFIER_KEY = new NamespacedKey("smp", "aquaman");

    public AquaMan(Player player) {
        super(AbilityType.AQUAMAN, player);

        AttributeInstance attr = player.getAttribute(Attribute.SUBMERGED_MINING_SPEED);

        if (attr != null && attr.getModifier(ATTRIBUTE_MODIFIER_KEY) == null) {
             attr.addModifier(new AttributeModifier(
                    ATTRIBUTE_MODIFIER_KEY,
                    4.0,
                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
            ));
        }
    }

    @Override
    public ItemStack[] getSpecialItems() {
        return new ItemStack[0];
    }

    @Override
    protected void additionalClearUp() {
        player.getAttribute(Attribute.SUBMERGED_MINING_SPEED).removeModifier(ATTRIBUTE_MODIFIER_KEY);
    }

    @Override
    protected Set<PotionEffectType> getPermanentPotionEffects() {
        return Set.of(PotionEffectType.DOLPHINS_GRACE, PotionEffectType.WATER_BREATHING);
    }
}

