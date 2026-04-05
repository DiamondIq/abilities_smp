package me.diamond.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PlayerDeathEvent implements Listener {
    @EventHandler
    public void onDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        ItemStack head = ItemStack.of(Material.PLAYER_HEAD);
        head.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                        .consumeSeconds(1.6f)
                        .animation(ItemUseAnimation.EAT)
                        .hasConsumeParticles(true)
                        .addEffect(ConsumeEffect.applyStatusEffects(List.of(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 0)), 1f)) // effect: 5s regeneration I
                        .build());

        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(event.getPlayer());
            meta.setLore(List.of(ChatColor.GRAY + "Eat to gain regeneration"));
            head.setItemMeta(meta);
        }

        // Add the head to dropped loot
        event.getDrops().add(head);
    }
}
