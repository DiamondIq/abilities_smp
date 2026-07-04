package me.diamond.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import me.diamond.abilities.Sorcerer;
import me.diamond.utils.CooldownManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDropEvent implements Listener {
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Key model = event.getItemDrop().getItemStack().getData(DataComponentTypes.ITEM_MODEL);
        Player player = event.getPlayer();

        if (model.value().equalsIgnoreCase("sorcerer_ability")) {
            event.setCancelled(true);
            if (!CooldownManager.isOnCooldown(player, "shadow")) {
                Sorcerer sorcerer = (Sorcerer) AbilityManager.getAbility(player, AbilityType.SORCERER);
                sorcerer.setShadowed();
                CooldownManager.setCooldown(player, "shadow", 15 * 1000);
            }
            return;
        }

        if (model.namespace().equalsIgnoreCase("smp") && model.value().endsWith("_ability")) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You can't drop this!").color(NamedTextColor.RED));
        }
    }
}
