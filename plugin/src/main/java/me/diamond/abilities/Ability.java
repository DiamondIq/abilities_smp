package me.diamond.abilities;

import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@RequiredArgsConstructor
public abstract class Ability {
    @Getter
    private final AbilityType type;
    protected final Player player;

    public abstract ItemStack[] getSpecialItems();

    public static ItemStack getEquipItem(AbilityType type, String description) {
        ItemStack item = ItemStack.of(Material.PAPER);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY + type.name() + " Ability");
        meta.setLore(List.of(description, "Right Click to equip. You can have a maximum of 3 abilities equipped at a time"));
        item.setItemMeta(meta);

        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        item.setData(DataComponentTypes.ITEM_MODEL, Key.key("smp", type.name().toLowerCase()));

        return item;
    }

    public void clearUp() {
        player.getInventory().removeItem(getSpecialItems());
        additionalClearUp();
    }

    protected void additionalClearUp() {}
}
