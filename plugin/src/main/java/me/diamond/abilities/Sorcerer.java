package me.diamond.abilities;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import lombok.Getter;
import lombok.Setter;
import me.diamond.utils.Utils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@Setter
public class Sorcerer extends Ability {
    private boolean isShadowed;

    public Sorcerer(Player player) {
        super(AbilityType.SORCERER, player);
    }

    @Override
    public ItemStack[] getSpecialItems() {
        ItemStack item = new ItemStack(Material.STICK);
        item.setData(DataComponentTypes.ITEM_MODEL, Key.key("smp", "sorcerer_ability"));
        item.setData(DataComponentTypes.CUSTOM_NAME, Utils.gradientText("SORCERER", 0x2e2e2e, 0xa1a1a1).decoration(TextDecoration.BOLD, true));
        item.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(List.of(
                Component.text("[")
                        .color(NamedTextColor.GOLD)
                        .append(Component.translatable("Press %s",
                                Component.keybind("key.drop")
                                        .color(NamedTextColor.GRAY)
                                        .append(Component.text("]").color(NamedTextColor.GOLD))
                        ))
                        .append(Component.text(" Vanish into shadows for 5s.").color(NamedTextColor.GRAY)),
                Component.text("This grants invisibility, invulnerability and speed 4.").color(NamedTextColor.GRAY),
                Component.text("\uE000").font(Key.key("smp", "custom")).color(NamedTextColor.WHITE)
                        .append(Component.text(" This ability has a 2min cooldown.").font(Key.key("minecraft", "default")).color(NamedTextColor.GRAY))
        )).build());

        ItemStack item2 = new ItemStack(Material.SPLASH_POTION);
        item2.setData(DataComponentTypes.ITEM_MODEL, Key.key("smp", "sorcerer_pot_ability"));
        item2.setData(DataComponentTypes.CUSTOM_NAME, Utils.gradientText("POTION OF MYSTERY", 0x52eb34, 0x52eb34).decoration(TextDecoration.BOLD, true));
        item2.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        item2.setData(DataComponentTypes.LORE, ItemLore.lore(List.of(
                Component.text("Mystery")
                        .decoration(TextDecoration.OBFUSCATED, true)
                        .color(NamedTextColor.RED)
                        .append(Component.text(" (00:20)")
                                .decoration(TextDecoration.OBFUSCATED, false)
                                .color(NamedTextColor.RED)),
                Component.text("\uE000")
                        .font(Key.key("smp", "custom"))
                        .color(NamedTextColor.WHITE)
                        .append(Component.text(" This ability has a 1min cooldown.")
                                .font(Key.key("minecraft", "default"))
                                .color(NamedTextColor.GRAY))
        )));

        return new ItemStack[]{item, item2};
    }
}
