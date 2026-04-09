package me.diamond.abilities;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import lombok.AccessLevel;
import lombok.Getter;
import me.diamond.SMP;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class Ability {
    @Getter
    private final AbilityType type;
    protected final Player player;
    @Getter(AccessLevel.PROTECTED)
    private final Set<PotionEffectType> permanentPotionEffects = Collections.emptySet();

    protected Ability(AbilityType type, Player player) {
        this.type = type;
        this.player = player;
        givePotionEffects();
    }

    public abstract ItemStack[] getSpecialItems();

    public static ItemStack getEquipItem(AbilityType type) {
        ItemStack item = ItemStack.of(Material.PAPER);
        item.setData(DataComponentTypes.CUSTOM_NAME, Component.text(type.name() + " ABILITY").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.BOLD, true));
        item.setData(DataComponentTypes.LORE, ItemLore.lore(List.of(
                Component.text(type.getDescription()).color(NamedTextColor.GRAY),
                Component.text("Right click to equip").color(NamedTextColor.GRAY),
                Component.translatable("You can have a maximum of %s abilities equipped at a time", String.valueOf(SMP.MAX_ABILITIES)).color(NamedTextColor.GRAY)
        )));
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        item.setData(DataComponentTypes.ITEM_MODEL, Key.key("smp", type.name().toLowerCase()));

        return item;
    }

    public void clearUp() {
        player.getInventory().removeItem(getSpecialItems());
        additionalClearUp();
    }

    protected void additionalClearUp() {
    }

    private void givePotionEffects() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || AbilityManager.getAbility(player, type) == null) {
                    cancel();
                    return;
                }

                for (PotionEffectType type : getPermanentPotionEffects()) {
                    PotionEffect effect = player.getPotionEffect(type);
                    //Check if player doesn't have a better effect active
                    if (effect == null || effect.getAmplifier() == 0) {
                        player.addPotionEffect(type.createEffect(20 * 2, 0));
                    }
                }
            }
        }.runTaskTimer(SMP.getPlugin(), 0, 20);
    }
}
