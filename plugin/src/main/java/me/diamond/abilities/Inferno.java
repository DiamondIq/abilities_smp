package me.diamond.abilities;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import lombok.Getter;
import lombok.Setter;
import me.diamond.SMP;
import me.diamond.utils.Utils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@Getter
@Setter
public class Inferno extends Ability {
    private boolean infernoActivated = false;
    private BossBar bar;

    public Inferno(Player player) {
        super(AbilityType.INFERNO, player);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(20 * 2, 0));
            }
        }.runTaskTimer(SMP.getPlugin(), 0, 20);
    }

    public void activate() {
        infernoActivated = true;
        bar = Utils.decreasingBossBar(player, Component.text("Inferno Active"), BossBar.Color.RED, 60, () -> setInfernoActivated(false));
    }

    @Override
    public ItemStack[] getSpecialItems() {
        ItemStack item = new ItemStack(Material.BLAZE_POWDER);
        item.setData(DataComponentTypes.ITEM_MODEL, Key.key("smp", "inferno_ability"));
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("inferno_ability"));
        item.setData(DataComponentTypes.CUSTOM_NAME, Utils.gradientText("INFERNO", 0xFF0000, 0xFFFF00));
        item.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(List.of(
                        Component.text("[").color(NamedTextColor.GOLD).append(Component.text("Right click for 5s").color(NamedTextColor.GRAY).append(Component.text("]").color(NamedTextColor.GOLD))).append(Component.text(" If you hit a player in the next 1min, an Inferno will summon around them.")).color(NamedTextColor.GRAY),
                Component.text("\uE000").font(Key.key("smp", "custom")).color(NamedTextColor.WHITE)
                        .append(Component.text(" This ability has a 2min cooldown.").font(Key.key("minecraft", "default")).color(NamedTextColor.GRAY))))
                .build());
        item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds(5)
                .animation(ItemUseAnimation.CROSSBOW)
                .hasConsumeParticles(false)
                .sound(Key.key(""))
                .build());

        return new ItemStack[]{item};
    }
}
